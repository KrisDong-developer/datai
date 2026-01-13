package com.datai.integration.realtime;

import java.util.Date;
import java.util.List;
import java.util.Map;

public interface DataSynchronizer {
    
    void synchronizeData(String objectType, String recordId, String changeType, Map<String, Object> changeData, Date changeDate);

    void batchSynchronizeData(List<SyncData> syncDataList);

    void upsertRecord(String objectType, String recordId, Map<String, Object> data);

    record SyncData(String objectType, String recordId, String changeType, Map<String, Object> changeData, Date changeDate) {}
}
