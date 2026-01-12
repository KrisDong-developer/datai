package com.datai.integration.realtime;

import java.util.Date;
import java.util.Map;

/**
 * 数据同步器接口
 * 用于将Salesforce Change Events变更数据同步至本地数据库
 */
public interface DataSynchronizer {
    
    /**
     * 同步数据变更
     * @param objectType 对象类型
     * @param recordId 记录ID
     * @param changeType 变更类型
     * @param changeData 变更数据
     * @param changeDate 变更时间
     */
    void synchronizeData(String objectType, String recordId, String changeType, Map<String, Object> changeData, Date changeDate);
}
