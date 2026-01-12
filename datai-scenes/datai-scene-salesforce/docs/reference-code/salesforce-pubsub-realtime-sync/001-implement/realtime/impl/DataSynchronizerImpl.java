package com.datai.integration.realtime.impl;

import com.datai.integration.mapper.CustomMapper;
import com.datai.integration.model.domain.DataiIntegrationObject;
import com.datai.integration.realtime.DataSynchronizer;
import com.datai.integration.service.IDataiIntegrationObjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * 数据同步器实现类
 * 用于将Salesforce Change Events变更数据同步至本地数据库
 */
@Component
@Slf4j
public class DataSynchronizerImpl implements DataSynchronizer {
    
    @Autowired
    private CustomMapper customMapper;
    
    @Autowired
    private IDataiIntegrationObjectService dataiIntegrationObjectService;
    
    @Override
    public void synchronizeData(String objectType, String recordId, String changeType, Map<String, Object> changeData, Date changeDate) {
        log.info("开始同步数据变更: 对象={}, 记录ID={}, 变更类型={}", objectType, recordId, changeType);
        
        try {
            // 检查对象是否开启实时同步
            if (!isObjectRealtimeSyncEnabled(objectType)) {
                log.info("对象 {} 未开启实时同步，跳过数据同步", objectType);
                return;
            }
            
            // 执行upsert操作
            upsertData(objectType, recordId, changeData);
            
            // 更新对象的最后同步时间
            updateLastSyncTime(objectType, changeDate);
            
            log.info("数据变更同步成功: 对象={}, 记录ID={}", objectType, recordId);
        } catch (Exception e) {
            log.error("同步数据变更时发生异常: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 检查对象是否开启实时同步
     * @param objectType 对象类型
     * @return 是否开启实时同步
     */
    private boolean isObjectRealtimeSyncEnabled(String objectType) {
        log.info("检查对象是否开启实时同步: {}", objectType);
        
        try {
            // 查询对象信息
            DataiIntegrationObject queryObject = new DataiIntegrationObject();
            queryObject.setApi(objectType);
            List<DataiIntegrationObject> objects = dataiIntegrationObjectService.selectDataiIntegrationObjectList(queryObject);
            
            if (objects != null && !objects.isEmpty()) {
                DataiIntegrationObject object = objects.get(0);
                boolean isRealtimeSync = Boolean.TRUE.equals(object.getIsRealtimeSync());
                log.info("对象 {} 的实时同步状态: {}", objectType, isRealtimeSync);
                return isRealtimeSync;
            }
            
            log.warn("对象 {} 不存在，默认不开启实时同步", objectType);
            return false;
        } catch (Exception e) {
            log.error("检查对象实时同步状态时发生异常: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 执行upsert操作
     * @param objectType 对象类型
     * @param recordId 记录ID
     * @param changeData 变更数据
     */
    private void upsertData(String objectType, String recordId, Map<String, Object> changeData) {
        log.info("执行upsert操作: 对象={}, 记录ID={}", objectType, recordId);
        
        try {
            // 检查记录是否存在
            boolean exists = checkRecordExists(objectType, recordId);
            
            if (exists) {
                // 更新记录
                updateRecord(objectType, recordId, changeData);
                log.info("更新记录成功: 对象={}, 记录ID={}", objectType, recordId);
            } else {
                // 插入记录
                insertRecord(objectType, changeData);
                log.info("插入记录成功: 对象={}, 记录ID={}", objectType, recordId);
            }
        } catch (Exception e) {
            log.error("执行upsert操作时发生异常: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 检查记录是否存在
     * @param objectType 对象类型
     * @param recordId 记录ID
     * @return 记录是否存在
     */
    private boolean checkRecordExists(String objectType, String recordId) {
        log.info("检查记录是否存在: 对象={}, 记录ID={}", objectType, recordId);
        
        try {
            // 参考DataiIntegrationBatchServiceImpl中的实现方式
            java.util.List<String> ids = java.util.Collections.singletonList(recordId);
            java.util.List<String> existsIds = customMapper.getIds(objectType, ids);
            boolean exists = existsIds.contains(recordId);
            log.info("记录存在状态: 对象={}, 记录ID={}, 存在={}", objectType, recordId, exists);
            return exists;
        } catch (Exception e) {
            log.error("检查记录是否存在时发生异常: {}", e.getMessage(), e);
            return false;
        }
    }
    
    /**
     * 插入记录
     * @param objectType 对象类型
     * @param changeData 变更数据
     */
    private void insertRecord(String objectType, Map<String, Object> changeData) {
        log.info("插入记录: 对象={}", objectType);
        
        try {
            // 参考DataiIntegrationBatchServiceImpl中的实现方式
            java.util.List<java.util.Map<String, Object>> insertMaps = new java.util.ArrayList<>();
            insertMaps.add(changeData);
            
            // 构建字段名列表
            java.util.List<String> fields = new java.util.ArrayList<>(changeData.keySet());
            
            // 构建字段值列表
            java.util.List<java.util.Collection<Object>> valuesList = new java.util.ArrayList<>();
            java.util.List<Object> values = new java.util.ArrayList<>(changeData.values());
            valuesList.add(values);
            
            // 执行插入操作
            customMapper.saveBatch(objectType, fields, valuesList);
            log.info("插入记录成功: 对象={}, 字段数={}", objectType, fields.size());
        } catch (Exception e) {
            log.error("插入记录时发生异常: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 更新记录
     * @param objectType 对象类型
     * @param recordId 记录ID
     * @param changeData 变更数据
     */
    private void updateRecord(String objectType, String recordId, Map<String, Object> changeData) {
        log.info("更新记录: 对象={}, 记录ID={}", objectType, recordId);
        
        try {
            // 参考DataiIntegrationBatchServiceImpl中的实现方式
            java.util.List<java.util.Map<String, Object>> maps = new java.util.ArrayList<>();
            for (java.util.Map.Entry<String, Object> entry : changeData.entrySet()) {
                java.util.Map<String, Object> paramMap = new java.util.HashMap<>();
                paramMap.put("key", entry.getKey());
                paramMap.put("value", entry.getValue());
                maps.add(paramMap);
            }
            
            // 执行更新操作
            customMapper.updateById(objectType, maps, recordId);
            log.info("更新记录成功: 对象={}, 记录ID={}, 更新字段数={}", objectType, recordId, changeData.size());
        } catch (Exception e) {
            log.error("更新记录时发生异常: {}", e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 更新对象的最后同步时间
     * @param objectType 对象类型
     * @param changeDate 变更时间
     */
    private void updateLastSyncTime(String objectType, Date changeDate) {
        log.info("更新对象的最后同步时间: 对象={}, 时间={}", objectType, changeDate);
        
        try {
            // 参考DataiIntegrationBatchServiceImpl中的实现方式
            // 查询对象信息
            DataiIntegrationObject queryObject = new DataiIntegrationObject();
            queryObject.setApi(objectType);
            java.util.List<DataiIntegrationObject> objects = dataiIntegrationObjectService.selectDataiIntegrationObjectList(queryObject);
            
            if (objects != null && !objects.isEmpty()) {
                DataiIntegrationObject object = objects.get(0);
                // 修复类型错误：将Timestamp转换为LocalDateTime
                object.setLastSyncDate(new java.sql.Timestamp(changeDate.getTime()).toLocalDateTime());
                object.setUpdateTime(new java.util.Date());
                dataiIntegrationObjectService.updateDataiIntegrationObject(object);
                log.info("更新对象最后同步时间成功: 对象={}", objectType);
            }
        } catch (Exception e) {
            log.error("更新对象最后同步时间时发生异常: {}", e.getMessage(), e);
        }
    }
}
