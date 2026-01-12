package com.datai.integration.realtime.impl;

import com.datai.integration.factory.impl.PubSubConnectionFactory;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.salesforce.eventbus.protobuf.SchemaRequest;
import com.salesforce.eventbus.protobuf.SchemaInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.Schema;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class SchemaCacheService {

    @Autowired
    private PubSubConnectionFactory connectionFactory;

    // 使用 Guava Cache 缓存解析后的 Avro Schema 对象
    private final Cache<String, Schema> schemaCache = CacheBuilder.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(24, TimeUnit.HOURS)
            .build();

    public Schema getSchema(String schemaId) {
        try {
            return schemaCache.get(schemaId, () -> fetchSchemaFromSalesforce(schemaId));
        } catch (Exception e) {
            log.error("获取 Schema 失败: {}", schemaId, e);
            throw new RuntimeException(e);
        }
    }

    private Schema fetchSchemaFromSalesforce(String schemaId) {
        log.info("Schema 缓存未命中，从 Salesforce 获取 Schema: {}", schemaId);

        // 使用 BlockingStub 同步请求 Schema
        SchemaRequest request = SchemaRequest.newBuilder().setSchemaId(schemaId).build();
        SchemaInfo response = connectionFactory.getBlockingStub().getSchema(request);

        String schemaJson = response.getSchemaJson();
        // 解析 JSON 为 Avro Schema 对象
        return new Schema.Parser().parse(schemaJson);
    }
}