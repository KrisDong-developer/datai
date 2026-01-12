package com.datai.integration.realtime.impl;

import com.datai.integration.realtime.DataSynchronizer;
import com.datai.integration.realtime.EventProcessor;
import com.salesforce.multicloudj.pubsub.driver.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class EventProcessorImpl implements EventProcessor {

    @Autowired
    private DataSynchronizer dataSynchronizer;

    @Override
    public void processMessage(Message message) {
        if (message == null) {
            log.warn("尝试处理空消息，跳过处理");
            return;
        }

        try {
            String loggableID = message.getLoggableID();
            log.debug("处理消息: {}", loggableID);

            Map<String, Object> eventData = parseMessage(message);
            
            String objectType = (String) eventData.get("objectType");
            String recordId = (String) eventData.get("recordId");
            String changeType = (String) eventData.get("changeType");
            Map<String, Object> changeData = (Map<String, Object>) eventData.get("changeData");
            Date changeDate = (Date) eventData.get("changeDate");

            if (objectType == null || recordId == null) {
                log.warn("消息解析后缺少关键字段: objectType={}, recordId={}", objectType, recordId);
                return;
            }

            dataSynchronizer.synchronizeData(objectType, recordId, changeType, changeData, changeDate);
        } catch (Exception e) {
            log.error("处理消息时发生异常: {}", e.getMessage(), e);
        }
    }

    @Override
    public void processMessageBatch(Message[] messages) {
        if (messages == null || messages.length == 0) {
            log.warn("尝试处理空消息批次，跳过处理");
            return;
        }

        log.info("开始处理消息批次，共 {} 条消息", messages.length);
        
        List<DataSynchronizer.SyncData> syncDataList = new ArrayList<>();
        int successCount = 0;
        int failureCount = 0;

        for (Message message : messages) {
            try {
                Map<String, Object> eventData = parseMessage(message);
                
                String objectType = (String) eventData.get("objectType");
                String recordId = (String) eventData.get("recordId");
                String changeType = (String) eventData.get("changeType");
                Map<String, Object> changeData = (Map<String, Object>) eventData.get("changeData");
                Date changeDate = (Date) eventData.get("changeDate");

                if (objectType != null && recordId != null) {
                    syncDataList.add(new DataSynchronizer.SyncData(objectType, recordId, changeType, changeData, changeDate));
                }
            } catch (Exception e) {
                failureCount++;
                log.error("解析批次消息时发生异常: {}", e.getMessage(), e);
            }
        }

        if (!syncDataList.isEmpty()) {
            try {
                dataSynchronizer.batchSynchronizeData(syncDataList);
                successCount = syncDataList.size();
            } catch (Exception e) {
                log.error("批量同步数据时发生异常: {}", e.getMessage(), e);
            }
        }

        log.info("消息批次处理完成，成功: {}, 失败: {}, 总计: {}", successCount, failureCount, messages.length);
    }

    private Map<String, Object> parseMessage(Message message) {
        Map<String, Object> eventData = new java.util.HashMap<>();

        try {
            byte[] body = message.getBody();
            if (body != null) {
                String bodyString = new String(body);
                log.debug("消息体: {}", bodyString);
                
                eventData.putAll(extractEventData(bodyString));
            }
        } catch (Exception e) {
            log.error("解析消息时发生异常: {}", e.getMessage(), e);
        }

        return eventData;
    }

    private Map<String, Object> extractEventData(String bodyString) {
        Map<String, Object> eventData = new java.util.HashMap<>();
        
        try {
            eventData.put("objectType", extractObjectApi(bodyString));
            eventData.put("recordId", extractRecordId(bodyString));
            eventData.put("changeType", extractOperationType(bodyString));
            eventData.put("changeData", extractChangeData(bodyString));
            eventData.put("changeDate", new Date());
        } catch (Exception e) {
            log.error("提取事件数据时发生异常: {}", e.getMessage(), e);
        }

        return eventData;
    }

    private String extractObjectApi(String bodyString) {
        return "Account";
    }

    private String extractRecordId(String bodyString) {
        return "001XXXXXXXXXXXXXXX";
    }

    private String extractOperationType(String bodyString) {
        return "UPDATE";
    }

    private Map<String, Object> extractChangeData(String bodyString) {
        Map<String, Object> changeData = new java.util.HashMap<>();
        changeData.put("Name", "Test Account Updated");
        changeData.put("Description", "Updated description");
        return changeData;
    }
}