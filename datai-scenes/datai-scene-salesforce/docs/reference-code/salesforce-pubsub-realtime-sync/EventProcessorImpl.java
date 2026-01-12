package com.datai.integration.realtime.impl;

import com.datai.integration.realtime.EventProcessor;
import com.datai.integration.realtime.DataSynchronizer;
import com.salesforce.eventbus.protobuf.ConsumerEvent;
import com.sforce.soap.partner.sobject.SObject;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.DecoderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.*;

@Component
@Slf4j
public class EventProcessorImpl implements EventProcessor {

    @Autowired
    private DataSynchronizer dataSynchronizer;

    @Autowired
    private SchemaCacheService schemaCacheService;

    /**
     * 处理 gRPC 事件列表
     */
    public void processGrpcEvents(List<ConsumerEvent> events) {
        log.info("接收到 {} 个 Pub/Sub 事件，开始处理...", events.size());

        for (ConsumerEvent event : events) {
            try {
                // 1. 获取 Schema
                String schemaId = event.getEvent().getSchemaId();
                Schema schema = schemaCacheService.getSchema(schemaId);

                // 2. 解析 Payload
                ByteBuffer payloadBuffer = event.getEvent().getPayload().asReadOnlyByteBuffer();
                // Avro 需要 byte[] 数组
                byte[] payloadBytes = new byte[payloadBuffer.remaining()];
                payloadBuffer.get(payloadBytes);

                GenericRecord record = deserializeAvro(payloadBytes, schema);

                // 3. 转换为业务 Map 并提取关键信息
                processAvroRecord(record, event.getReplayId().toByteArray());

            } catch (Exception e) {
                log.error("解析事件失败 (ReplayID: {}): {}", event.getReplayId(), e.getMessage(), e);
            }
        }
    }

    /**
     * Avro 反序列化
     */
    private GenericRecord deserializeAvro(byte[] data, Schema schema) throws IOException {
        GenericDatumReader<GenericRecord> reader = new GenericDatumReader<>(schema);
        BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(data, null);
        return reader.read(null, decoder);
    }

    /**
     * 提取字段并调用同步逻辑
     */
    private void processAvroRecord(GenericRecord record, byte[] replayId) {
        // CDC 事件通常有 ChangeEventHeader
        GenericRecord header = (GenericRecord) record.get("ChangeEventHeader");
        if (header == null) {
            log.warn("非 CDC 事件，跳过处理");
            return;
        }

        // 提取 Header 信息
        String entityName = header.get("entityName").toString();
        String changeType = header.get("changeType").toString();
        Long commitTimestamp = (Long) header.get("commitTimestamp");
        Date changeDate = new Date(commitTimestamp);

        // 提取 RecordId (CDC 中 recordIds 是一个列表)
        String recordId = "Unknown";
        Object recordIdsObj = header.get("recordIds");
        if (recordIdsObj instanceof List) {
            List<?> ids = (List<?>) recordIdsObj;
            if (!ids.isEmpty()) {
                recordId = ids.get(0).toString();
            }
        }

        log.info("处理事件: Object={}, Type={}, ID={}", entityName, changeType, recordId);

        // 提取变更数据 (将 Avro Record 转为 Map)
        Map<String, Object> changeData = new HashMap<>();
        for (Schema.Field field : record.getSchema().getFields()) {
            String fieldName = field.name();
            // 排除 Header 和 Null 值
            Object value = record.get(fieldName);

            if (value != null && !"ChangeEventHeader".equals(fieldName)) {
                // 处理 Avro 特殊类型 (Utf8 -> String)
                if (value instanceof CharSequence) {
                    value = value.toString();
                }
                changeData.put(fieldName, value);
            }
        }

        // 调用原有的同步逻辑
        dataSynchronizer.synchronizeData(entityName, recordId, changeType, changeData, changeDate);
    }

    // 兼容旧代码接口
    @Override
    public void processEvent(SObject event) {
        // ... 旧代码保持不变 ...
    }
}