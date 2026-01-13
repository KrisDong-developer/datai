package com.datai.integration.realtime.impl;

import com.datai.integration.realtime.DataSynchronizer;
import com.datai.integration.realtime.EventProcessor;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.salesforce.multicloudj.pubsub.driver.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 事件处理器实现类
 * 负责解析消息并分发给同步器
 */
@Slf4j
@Component
public class EventProcessorImpl implements EventProcessor {

    private static final String WARN_EMPTY_MESSAGE = "尝试处理空消息，跳过处理";
    private static final String WARN_EMPTY_BATCH = "尝试处理空消息批次，跳过处理";
    private static final String WARN_MISSING_FIELDS = "消息解析后缺少关键字段: objectType={}, recordId={}";
    private static final String ERROR_PARSE_MESSAGE = "解析消息时发生异常";
    private static final String ERROR_EXTRACT_EVENT_DATA = "提取事件数据时发生异常";
    private static final String ERROR_PARSE_BATCH_MESSAGE = "解析批次消息时发生异常";
    private static final String ERROR_BATCH_SYNC = "批量同步数据时发生异常";
    private static final String ERROR_PROCESS_MESSAGE = "处理消息时发生异常";
    private static final String ERROR_MESSAGE_BODY_NULL = "消息体为空";

    private static final String METADATA_OBJECT_TYPE = "objectType";
    private static final String METADATA_RECORD_ID = "recordId";
    private static final String METADATA_CHANGE_TYPE = "changeType";

    @Autowired
    private DataSynchronizer dataSynchronizer;

    @Autowired
    private ObjectMapper objectMapper; // 注入Spring自带的JSON解析器

    @Override
    public void processMessage(Message message) {
        if (message == null) {
            log.warn(WARN_EMPTY_MESSAGE);
            return;
        }

        String loggableID = message.getLoggableID();
        log.debug("开始处理消息: {}", loggableID);

        try {
            validateMessage(message);

            // 解析消息并提取数据
            Map<String, Object> eventData = parseMessage(message);

            if (!validateEventData(eventData, loggableID)) {
                return;
            }

            String objectType = (String) eventData.get("objectType");
            String recordId = (String) eventData.get("recordId");
            String changeType = (String) eventData.get("changeType");
            @SuppressWarnings("unchecked")
            Map<String, Object> changeData = (Map<String, Object>) eventData.get("changeData");
            Date changeDate = (Date) eventData.get("changeDate");

            log.debug("准备同步数据: objectType={}, recordId={}, changeType={}", objectType, recordId, changeType);
            dataSynchronizer.synchronizeData(objectType, recordId, changeType, changeData, changeDate);
            log.debug("消息处理完成: {}", loggableID);
        } catch (Exception e) {
            log.error("{}: {}", ERROR_PROCESS_MESSAGE, e.getMessage(), e);
        }
    }

    @Override
    public void processMessageBatch(Message[] messages) {
        if (messages == null || messages.length == 0) {
            log.warn(WARN_EMPTY_BATCH);
            return;
        }

        log.info("开始处理消息批次，共 {} 条消息", messages.length);

        List<DataSynchronizer.SyncData> syncDataList = new ArrayList<>();
        int failureCount = 0;

        for (int i = 0; i < messages.length; i++) {
            Message message = messages[i];
            try {
                validateMessage(message);
                Map<String, Object> eventData = parseMessage(message);

                if (validateEventData(eventData, message.getLoggableID())) {
                    syncDataList.add(new DataSynchronizer.SyncData(
                            (String) eventData.get("objectType"),
                            (String) eventData.get("recordId"),
                            (String) eventData.get("changeType"),
                            (Map<String, Object>) eventData.get("changeData"),
                            (Date) eventData.get("changeDate")
                    ));
                }
            } catch (Exception e) {
                failureCount++;
                log.error("{} [索引: {}]: {}", ERROR_PARSE_BATCH_MESSAGE, i, e.getMessage(), e);
            }
        }

        if (!syncDataList.isEmpty()) {
            try {
                dataSynchronizer.batchSynchronizeData(syncDataList);
            } catch (Exception e) {
                log.error("{}: {}", ERROR_BATCH_SYNC, e.getMessage(), e);
            }
        }

        log.info("消息批次处理完成，成功: {}, 失败: {}, 总计: {}", syncDataList.size(), failureCount, messages.length);
    }

    private void validateMessage(Message message) {
        if (message.getBody() == null) {
            throw new IllegalArgumentException(ERROR_MESSAGE_BODY_NULL);
        }
    }

    private boolean validateEventData(Map<String, Object> eventData, String loggableID) {
        String objectType = (String) eventData.get("objectType");
        String recordId = (String) eventData.get("recordId");

        if (!StringUtils.hasText(objectType) || !StringUtils.hasText(recordId)) {
            log.warn(WARN_MISSING_FIELDS, objectType, recordId);
            return false;
        }
        return true;
    }

    private Map<String, Object> parseMessage(Message message) {
        try {
            byte[] body = message.getBody();
            String bodyString = new String(body, StandardCharsets.UTF_8);

            // 将JSON字符串解析为Map结构
            Map<String, Object> bodyMap = Collections.emptyMap();
            if (StringUtils.hasText(bodyString)) {
                bodyMap = objectMapper.readValue(bodyString, new TypeReference<Map<String, Object>>() {});
            }

            return extractEventData(bodyMap, message.getMetadata());
        } catch (Exception e) {
            log.error("{}: {}", ERROR_PARSE_MESSAGE, e.getMessage(), e);
            return Collections.emptyMap();
        }
    }

    private Map<String, Object> extractEventData(Map<String, Object> bodyMap, Map<String, String> metadata) {
        Map<String, Object> eventData = new HashMap<>();

        try {
            // 1. 提取 ObjectType: 优先从Metadata取，没有则尝试从Body的'entityName'或'objectType'取
            String objectType = (metadata != null) ? metadata.get(METADATA_OBJECT_TYPE) : null;
            if (objectType == null) {
                objectType = (String) bodyMap.getOrDefault("entityName", bodyMap.get("objectType"));
            }
            eventData.put("objectType", objectType);

            // 2. 提取 RecordId: 优先从Metadata取，没有则尝试从Body的'id'或'recordId'取
            String recordId = (metadata != null) ? metadata.get(METADATA_RECORD_ID) : null;
            if (recordId == null) {
                recordId = (String) bodyMap.getOrDefault("id", bodyMap.get("recordId"));
            }
            eventData.put("recordId", recordId);

            // 3. 提取 ChangeType: 默认为 UPDATE
            String changeType = (metadata != null) ? metadata.get(METADATA_CHANGE_TYPE) : null;
            if (changeType == null) {
                changeType = (String) bodyMap.getOrDefault("changeType", "UPDATE");
            }
            eventData.put("changeType", changeType);

            // 4. 提取 ChangeData: 获取实际的字段变更集
            // 如果Body中包含'payload'或'data'节点，则提取该节点，否则认为整个Body就是变更数据
            Object data = bodyMap.getOrDefault("payload", bodyMap.getOrDefault("data", bodyMap));
            eventData.put("changeData", data instanceof Map ? data : bodyMap);

            // 5. 设置变更时间
            eventData.put("changeDate", new Date());

        } catch (Exception e) {
            log.error("{}: {}", ERROR_EXTRACT_EVENT_DATA, e.getMessage(), e);
        }

        return eventData;
    }
}