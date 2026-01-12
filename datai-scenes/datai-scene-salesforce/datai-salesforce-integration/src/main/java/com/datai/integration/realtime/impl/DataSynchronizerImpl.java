package com.datai.integration.realtime.impl;

import com.datai.integration.mapper.CustomMapper;
import com.datai.integration.realtime.DataSynchronizer;
import com.datai.integration.service.IDataiIntegrationRealtimeSyncLogService;
import com.datai.integration.model.domain.DataiIntegrationRealtimeSyncLog;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class DataSynchronizerImpl implements DataSynchronizer {

    @Autowired
    private CustomMapper customMapper;

    @Autowired
    private IDataiIntegrationRealtimeSyncLogService syncLogService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void synchronizeData(String objectType, String recordId, String changeType, Map<String, Object> changeData, Date changeDate) {
        if (objectType == null || recordId == null) {
            log.warn("尝试同步数据时对象API或记录ID为空，跳过同步");
            return;
        }

        try {
            upsertRecord(objectType, recordId, changeData);
            recordSyncLog(objectType, recordId, changeType, changeData, "SUCCESS", null);
        } catch (Exception e) {
            log.error("同步数据时发生异常: objectType={}, recordId={}, error={}", objectType, recordId, e.getMessage(), e);
            recordSyncLog(objectType, recordId, changeType, changeData, "FAILED", e.getMessage());
            throw e;
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void batchSynchronizeData(List<SyncData> syncDataList) {
        if (syncDataList == null || syncDataList.isEmpty()) {
            log.warn("尝试批量同步空数据列表，跳过同步");
            return;
        }

        log.info("开始批量同步 {} 条数据", syncDataList.size());
        int successCount = 0;
        int failureCount = 0;

        for (SyncData syncData : syncDataList) {
            try {
                synchronizeData(syncData.objectType(), syncData.recordId(), syncData.changeType(), syncData.changeData(), syncData.changeDate());
                successCount++;
            } catch (Exception e) {
                failureCount++;
                log.error("批量同步单条数据失败: objectType={}, recordId={}, error={}", syncData.objectType(), syncData.recordId(), e.getMessage());
            }
        }

        log.info("批量同步完成，成功: {}, 失败: {}, 总计: {}", successCount, failureCount, syncDataList.size());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void upsertRecord(String objectType, String recordId, Map<String, Object> data) {
        if (objectType == null || recordId == null) {
            log.warn("尝试执行upsert操作时对象API或记录ID为空，跳过操作");
            return;
        }

        String tableName = "sf_" + objectType.toLowerCase();
        
        Map<String, Object> upsertData = new HashMap<>();
        upsertData.put("Id", recordId);
        if (data != null) {
            upsertData.putAll(data);
        }

        try {
            customMapper.upsert(tableName, upsertData);
            log.debug("成功执行upsert操作: {}@{}", recordId, objectType);
        } catch (Exception e) {
            log.error("执行upsert操作时发生异常: tableName={}, recordId={}, error={}", tableName, recordId, e.getMessage(), e);
            throw new RuntimeException("执行upsert操作失败", e);
        }
    }

    private void recordSyncLog(String objectType, String recordId, String changeType, Map<String, Object> changeData, String syncStatus, String errorMessage) {
        try {
            DataiIntegrationRealtimeSyncLog syncLog = new DataiIntegrationRealtimeSyncLog();
            syncLog.setObjectName(objectType);
            syncLog.setRecordId(recordId);
            syncLog.setOperationType(changeType);
            syncLog.setChangeData(changeData != null ? changeData.toString() : null);
            syncLog.setSyncStatus(syncStatus);
            syncLog.setErrorMessage(errorMessage);
            syncLog.setSalesforceTimestamp(LocalDateTime.now());
            syncLog.setSyncTimestamp(LocalDateTime.now());

            syncLogService.insertDataiIntegrationRealtimeSyncLog(syncLog);
        } catch (Exception e) {
            log.error("记录同步日志时发生异常: {}", e.getMessage(), e);
        }
    }
}