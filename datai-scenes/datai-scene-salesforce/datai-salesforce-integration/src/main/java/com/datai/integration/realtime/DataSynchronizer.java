package com.datai.integration.realtime;

import java.util.Map;

/**
 * 数据同步器接口
 * 用于将变更数据同步至本地数据库
 */
public interface DataSynchronizer {
    
    /**
     * 同步数据至本地数据库
     * @param objectApi 对象API
     * @param recordId 记录ID
     * @param operationType 操作类型
     * @param changeData 变更数据
     */
    void synchronizeData(String objectApi, String recordId, String operationType, Map<String, Object> changeData);
    
    /**
     * 批量同步数据
     * @param syncDataList 同步数据列表
     */
    void batchSynchronizeData(java.util.List<Map<String, Object>> syncDataList);
    
    /**
     * 执行upsert操作
     * @param objectApi 对象API
     * @param recordId 记录ID
     * @param changeData 变更数据
     */
    void upsertRecord(String objectApi, String recordId, Map<String, Object> changeData);
}
