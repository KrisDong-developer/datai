package com.datai.integration.service.impl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.function.Supplier;
import java.util.concurrent.TimeUnit;
import com.datai.common.utils.DateUtils;
import com.datai.common.utils.SecurityUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.integration.mapper.DataiIntegrationBatchMapper;
import com.datai.integration.model.domain.DataiIntegrationBatch;
import com.datai.integration.service.IDataiIntegrationBatchService;
import com.datai.common.core.domain.model.LoginUser;
import com.datai.integration.model.domain.DataiIntegrationBatchHistory;
import com.datai.integration.service.IDataiIntegrationBatchHistoryService;
import com.datai.integration.service.IDataiIntegrationFieldService;
import com.datai.integration.service.IDataiIntegrationSyncLogService;
import com.datai.integration.mapper.CustomMapper;
import com.datai.integration.factory.impl.SOAPConnectionFactory;
import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import lombok.extern.slf4j.Slf4j;


/**
 * 数据批次Service业务层处理
 *
 * @author datai
 * @date 2025-12-24
 */
@Service
@Slf4j
public class DataiIntegrationBatchServiceImpl implements IDataiIntegrationBatchService {
    @Autowired
    private DataiIntegrationBatchMapper dataiIntegrationBatchMapper;

    @Autowired
    private IDataiIntegrationBatchHistoryService batchHistoryService;

    @Autowired
    private IDataiIntegrationFieldService integrationFieldService;

    @Autowired
    private CustomMapper customMapper;

    @Autowired
    private SOAPConnectionFactory soapConnectionFactory;

    @Autowired
    private IDataiIntegrationSyncLogService dataiIntegrationSyncLogService;

    /**
     * 查询数据批次
     *
     * @param id 数据批次主键
     * @return 数据批次
     */
    @Override
    public DataiIntegrationBatch selectDataiIntegrationBatchById(Integer id)
    {
        return dataiIntegrationBatchMapper.selectDataiIntegrationBatchById(id);
    }

    /**
     * 查询数据批次列表
     *
     * @param dataiIntegrationBatch 数据批次
     * @return 数据批次
     */
    @Override
    public List<DataiIntegrationBatch> selectDataiIntegrationBatchList(DataiIntegrationBatch dataiIntegrationBatch)
    {
        return dataiIntegrationBatchMapper.selectDataiIntegrationBatchList(dataiIntegrationBatch);
    }

    /**
     * 新增数据批次
     *
     * @param dataiIntegrationBatch 数据批次
     * @return 结果
     */
    @Override
    public int insertDataiIntegrationBatch(DataiIntegrationBatch dataiIntegrationBatch)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationBatch.setCreateTime(DateUtils.getNowDate());
                dataiIntegrationBatch.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationBatch.setCreateBy(username);
                dataiIntegrationBatch.setUpdateBy(username);
            return dataiIntegrationBatchMapper.insertDataiIntegrationBatch(dataiIntegrationBatch);
    }

    /**
     * 修改数据批次
     *
     * @param dataiIntegrationBatch 数据批次
     * @return 结果
     */
    @Override
    public int updateDataiIntegrationBatch(DataiIntegrationBatch dataiIntegrationBatch)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationBatch.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationBatch.setUpdateBy(username);
        return dataiIntegrationBatchMapper.updateDataiIntegrationBatch(dataiIntegrationBatch);
    }

    /**
     * 批量删除数据批次
     *
     * @param ids 需要删除的数据批次主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationBatchByIds(Integer[] ids)
    {
        return dataiIntegrationBatchMapper.deleteDataiIntegrationBatchByIds(ids);
    }

    /**
     * 删除数据批次信息
     *
     * @param id 数据批次主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationBatchById(Integer id)
    {
        return dataiIntegrationBatchMapper.deleteDataiIntegrationBatchById(id);
    }

    /**
     * 重试失败的批次
     *
     * @param id 批次ID
     * @return 结果
     */
    @Override
    public boolean retryFailed(Integer id) {
        try {
            log.info("开始重试失败的批次，批次ID: {}", id);

            DataiIntegrationBatch batch = selectDataiIntegrationBatchById(id);
            if (batch == null) {
                log.error("批次不存在，批次ID: {}", id);
                return false;
            }

            DataiIntegrationBatchHistory queryHistory = new DataiIntegrationBatchHistory();
            queryHistory.setBatchId(id);
            queryHistory.setSyncStatus(false);
            List<DataiIntegrationBatchHistory> failedHistories = batchHistoryService.selectDataiIntegrationBatchHistoryList(queryHistory);

            if (failedHistories.isEmpty()) {
                log.warn("批次没有失败的同步记录，批次ID: {}", id);
                return true;
            }

            log.info("批次 {} 共有 {} 条失败的同步记录，准备重试", id, failedHistories.size());

            boolean retryResult = syncObjectDataByBatch(batch.getApi(), id.toString());

            if (retryResult) {
                log.info("批次 {} 重试成功", id);
            } else {
                log.error("批次 {} 重试失败", id);
            }

            return retryResult;
        } catch (Exception e) {
            log.error("重试批次 {} 时发生异常", id, e);
            return false;
        }
    }

    /**
     * 获取批次同步统计信息
     *
     * @param id 批次ID
     * @return 统计信息
     */
    @Override
    public Map<String, Object> getSyncStatistics(Integer id) {
        Map<String, Object> statistics = new HashMap<>();

        try {
            DataiIntegrationBatch batch = selectDataiIntegrationBatchById(id);
            if (batch == null) {
                log.error("批次不存在，批次ID: {}", id);
                statistics.put("success", false);
                statistics.put("message", "批次不存在");
                return statistics;
            }

            DataiIntegrationBatchHistory queryHistory = new DataiIntegrationBatchHistory();
            queryHistory.setBatchId(id);
            List<DataiIntegrationBatchHistory> histories = batchHistoryService.selectDataiIntegrationBatchHistoryList(queryHistory);

            int totalCount = histories.size();
            int successCount = 0;
            int failedCount = 0;
            int totalSyncNum = 0;
            long totalCost = 0L;
            long minCost = Long.MAX_VALUE;
            long maxCost = 0L;

            for (DataiIntegrationBatchHistory history : histories) {
                if (history.getSyncStatus()) {
                    successCount++;
                } else {
                    failedCount++;
                }

                if (history.getSyncNum() != null) {
                    totalSyncNum += history.getSyncNum();
                }

                if (history.getCost() != null) {
                    totalCost += history.getCost();
                    minCost = Math.min(minCost, history.getCost());
                    maxCost = Math.max(maxCost, history.getCost());
                }
            }

            long avgCost = totalCount > 0 ? totalCost / totalCount : 0;
            if (minCost == Long.MAX_VALUE) {
                minCost = 0L;
            }

            statistics.put("success", true);
            statistics.put("message", "获取统计信息成功");
            int finalSuccessCount = successCount;
            int finalFailedCount = failedCount;
            int finalTotalSyncNum = totalSyncNum;
            long finalTotalCost = totalCost;
            long finalMinCost = minCost;
            long finalMaxCost = maxCost;
            statistics.put("data", new HashMap<String, Object>() {{
                put("batchId", id);
                put("api", batch.getApi());
                put("label", batch.getLabel());
                put("syncType", batch.getSyncType());
                put("sfNum", batch.getSfNum());
                put("dbNum", batch.getDbNum());
                put("totalCount", totalCount);
                put("successCount", finalSuccessCount);
                put("failedCount", finalFailedCount);
                put("successRate", totalCount > 0 ? (double) finalSuccessCount / totalCount * 100 : 0);
                put("totalSyncNum", finalTotalSyncNum);
                put("totalCost", finalTotalCost);
                put("avgCost", avgCost);
                put("minCost", finalMinCost);
                put("maxCost", finalMaxCost);
                put("firstSyncTime", batch.getFirstSyncTime());
                put("lastSyncTime", batch.getLastSyncTime());
                put("syncStatus", batch.getSyncStatus());
            }});

            log.info("获取批次 {} 统计信息成功", id);
        } catch (Exception e) {
            log.error("获取批次 {} 统计信息时发生异常", id, e);
            statistics.put("success", false);
            statistics.put("message", "获取统计信息失败: " + e.getMessage());
        }

        return statistics;
    }

    @Override
    public Map<String, Object> syncBatchData(Integer id) {
        Map<String, Object> result = new HashMap<>();

        try {
            log.info("开始同步批次数据，批次ID: {}", id);

            DataiIntegrationBatch batch = selectDataiIntegrationBatchById(id);
            if (batch == null) {
                log.error("批次不存在，批次ID: {}", id);
                result.put("success", false);
                result.put("message", "批次不存在");
                return result;
            }

            String objectApi = batch.getApi();
            String batchId = id.toString();

            log.info("准备同步对象 {} 的批次 {} 数据", objectApi, batchId);

            boolean syncResult = syncObjectDataByBatch(objectApi, batchId);

            if (syncResult) {
                log.info("批次 {} 数据同步成功", id);
                result.put("success", true);
                result.put("message", "批次数据同步成功");
                result.put("batchId", id);
                result.put("api", objectApi);
                result.put("label", batch.getLabel());
            } else {
                log.error("批次 {} 数据同步失败", id);
                result.put("success", false);
                result.put("message", "批次数据同步失败");
                result.put("batchId", id);
                result.put("api", objectApi);
            }

        } catch (Exception e) {
            log.error("同步批次 {} 数据时发生异常", id, e);
            result.put("success", false);
            result.put("message", "同步批次数据失败: " + e.getMessage());
        }

        return result;
    }

    /**
     * 同步Salesforce对象的指定批次数据
     *
     * @param objectApi Salesforce对象API
     * @param batchId   批次ID
     * @return 同步结果
     */
    private boolean syncObjectDataByBatch(String objectApi, String batchId) {
        log.info("准备同步Salesforce对象的指定批次数据，对象API: {}, 批次ID: {}", objectApi, batchId);
        
        if (objectApi == null || objectApi.trim().isEmpty() || batchId == null || batchId.trim().isEmpty()) {
            log.error("对象API或批次ID为空，无法同步指定批次数据");
            return false;
        }
        
        DataiIntegrationBatch batch = null;
        DataiIntegrationBatchHistory batchHistory = new DataiIntegrationBatchHistory();
        long startTime = System.currentTimeMillis();
        LocalDateTime syncStartTime = LocalDateTime.now();
        
        try {
            batch = selectDataiIntegrationBatchById(Integer.parseInt(batchId));
            if (batch == null) {
                log.error("批次不存在，批次ID: {}", batchId);
                return false;
            }
            
            PartnerConnection connection = retryOperation(() -> soapConnectionFactory.getConnection(), 3, 1000);
            log.info("成功获取Salesforce SOAP连接");

            List<String> fieldList = getSalesforceObjectFields(connection, objectApi);
            log.info("获取到对象 {} 的字段列表: {}", objectApi, fieldList);

            SalesforceParam param = new SalesforceParam();
            param.setApi(objectApi);
            param.setSelect(String.join(",", fieldList));

            if (integrationFieldService.isDeletedFieldExists(objectApi)) {
                param.setIsDeleted(true);
            }

            int totalCount = executeQueryAndProcessData(connection, param, fieldList);
            log.info("对象 {} 批次 {} 数据同步完成，共处理 {} 条记录", objectApi, batchId, totalCount);
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            LocalDateTime syncEndTime = LocalDateTime.now();
            
            batchHistory.setApi(objectApi);
            batchHistory.setLabel(batch.getLabel());
            batchHistory.setBatchId(Integer.parseInt(batchId));
            batchHistory.setBatchField(batch.getBatchField());
            batchHistory.setSyncNum(totalCount);
            batchHistory.setSyncType(batch.getSyncType());
            batchHistory.setSyncStatus(true);
            batchHistory.setStartTime(syncStartTime);
            batchHistory.setEndTime(syncEndTime);
            batchHistory.setCost(duration);
            batchHistory.setSyncStartTime(syncStartTime);
            batchHistory.setSyncEndTime(syncEndTime);
            batchHistoryService.insertDataiIntegrationBatchHistory(batchHistory);
            log.info("批次 {} 历史记录已保存，同步数据量: {}, 耗时: {}ms", batchId, totalCount, duration);

            insertSyncLog(objectApi, batch.getLabel(), batch.getSyncType(), true, null, duration);
            
            return true;
        } catch (Exception e) {
            log.error("同步Salesforce对象指定批次数据失败，对象API: {}, 批次ID: {}", objectApi, batchId, e);
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            LocalDateTime syncEndTime = LocalDateTime.now();
            
            batchHistory.setApi(objectApi);
            batchHistory.setLabel(batch != null ? batch.getLabel() : "");
            batchHistory.setBatchId(Integer.parseInt(batchId));
            batchHistory.setBatchField(batch != null ? batch.getBatchField() : "");
            batchHistory.setSyncNum(0);
            batchHistory.setSyncType(batch != null ? batch.getSyncType() : "");
            batchHistory.setSyncStatus(false);
            batchHistory.setStartTime(syncStartTime);
            batchHistory.setEndTime(syncEndTime);
            batchHistory.setCost(duration);
            batchHistory.setSyncStartTime(syncStartTime);
            batchHistory.setSyncEndTime(syncEndTime);
            batchHistoryService.insertDataiIntegrationBatchHistory(batchHistory);
            log.info("批次 {} 失败历史记录已保存，耗时: {}ms", batchId, duration);
            
            insertSyncLog(objectApi, batch != null ? batch.getLabel() : "", batch != null ? batch.getSyncType() : "", false, e.getMessage(), duration);
            
            return false;
        }
    }

    private void insertSyncLog(String objectApi, String label, String syncType, boolean success, String errorMessage, long duration) {
        try {
            com.datai.integration.model.domain.DataiIntegrationSyncLog syncLog = new com.datai.integration.model.domain.DataiIntegrationSyncLog();
            syncLog.setObjectApi(objectApi);
            syncLog.setOperationType("FULL".equals(syncType) ? "全量同步" : "增量同步");
            syncLog.setOperationStatus(success ? "成功" : "失败");
            syncLog.setErrorMessage(errorMessage);
            syncLog.setExecutionTime(new java.math.BigDecimal(duration / 1000.0));
            syncLog.setCreateTime(DateUtils.getNowDate());
            
            try {
                LoginUser loginUser = SecurityUtils.getLoginUser();
                if (loginUser != null && loginUser.getDeptId() != null) {
                    syncLog.setDeptId(loginUser.getDeptId());
                }
            } catch (Exception ex) {
                log.warn("获取部门ID失败: {}", ex.getMessage());
            }
            
            dataiIntegrationSyncLogService.insertDataiIntegrationSyncLog(syncLog);
            log.info("同步日志已记录，对象API: {}, 操作类型: {}, 状态: {}", objectApi, syncLog.getOperationType(), syncLog.getOperationStatus());
        } catch (Exception e) {
            log.error("插入同步日志失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 重试操作
     *
     * @param operation 操作
     * @param maxRetries 最大重试次数
     * @param delayMs 延迟毫秒数
     * @param <T> 返回类型
     * @return 操作结果
     */
    private <T> T retryOperation(Supplier<T> operation, int maxRetries, long delayMs) {
        int retries = 0;
        while (true) {
            try {
                return operation.get();
            } catch (Exception e) {
                retries++;
                if (retries >= maxRetries) {
                    log.error("操作失败，已达到最大重试次数: {}", maxRetries, e);
                    throw e;
                }
                try {
                    log.warn("操作失败，将在 {} 毫秒后重试，当前重试次数: {}", delayMs, retries);
                    Thread.sleep(delayMs);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    log.error("重试延迟时线程被中断", ie);
                    throw new RuntimeException(ie);
                }
            }
        }
    }

    /**
     * 获取Salesforce对象字段列表
     *
     * @param connection SOAP连接
     * @param objectApi  对象API名称
     * @return 字段列表
     * @throws ConnectionException 连接异常
     */
    private List<String> getSalesforceObjectFields(PartnerConnection connection, String objectApi) throws ConnectionException {
        List<String> fields = new ArrayList<>();

        DescribeSObjectResult describeResult = connection.describeSObject(objectApi);
        Field[] objectFields = describeResult.getFields();

        for (Field field : objectFields) {
            if (field.getType() != null && "base64".equalsIgnoreCase(field.getType().toString())) {
                continue;
            }
            fields.add(field.getName());
        }

        if (!fields.contains("Id")) {
            fields.add("Id");
        }
        if (!fields.contains("Name")) {
            fields.add("Name");
        }

        return fields;
    }

    /**
     * 执行查询并处理结果
     *
     * @param connection SOAP连接
     * @param param      查询参数
     * @param fieldList  字段列表
     * @return 处理的数据条数
     */
    private int executeQueryAndProcessData(PartnerConnection connection, SalesforceParam param, List<String> fieldList) {
        int totalCount = 0;
        try {
            String query = buildDynamicQuery(param);
            log.info("执行查询SQL: {}", query);

            QueryResult result = connection.queryAll(query);

            while (true) {
                if (result.getRecords() != null && result.getRecords().length > 0) {
                    totalCount += processQueryResult(param.getApi(), result, fieldList);
                    log.info("已处理 {} 条记录", totalCount);
                }

                if (result.isDone()) {
                    break;
                }

                result = connection.queryMore(result.getQueryLocator());

                TimeUnit.MILLISECONDS.sleep(100);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("查询数据时线程被中断，对象API: {}", param.getApi(), e);
        } catch (Exception e) {
            log.error("查询处理数据时发生异常，对象API: {}", param.getApi(), e);
        }
        return totalCount;
    }

    /**
     * 构建动态查询语句
     *
     * @param param 查询参数
     * @return SOQL查询语句
     */
    private String buildDynamicQuery(SalesforceParam param) {
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT ").append(param.getSelect())
                   .append(" FROM ").append(param.getApi());
        
        if (param.getIsDeleted() != null && param.getIsDeleted()) {
            queryBuilder.append(" WHERE IsDeleted = true");
        }
        
        return queryBuilder.toString();
    }

    /**
     * 处理查询结果
     *
     * @param api       API名称
     * @param result    查询结果
     * @param fieldList 字段列表
     * @return 处理的数据条数
     */
    private int processQueryResult(String api, QueryResult result, List<String> fieldList) {
        int count = 0;
        
        if (result == null || result.getRecords() == null) {
            log.info("处理API {} 的查询结果，共 0 条记录", api);
            return count;
        }
        
        SObject[] records = result.getRecords();
        log.info("处理API {} 的查询结果，共 {} 条记录", api, records.length);

        boolean isPartitioned = checkIfTablePartitioned(api);
        String batchField = null;
        if (isPartitioned) {
            batchField = integrationFieldService.getDateField(api);
        }

        Map<String, List<Map<String, Object>>> partitionedData = new HashMap<>();
        List<Map<String, Object>> normalData = new ArrayList<>();

        for (SObject record : records) {
            if (record == null) {
                continue;
            }
            
            try {
                Map<String, Object> recordMap = convertSObjectToMap(record, fieldList);

                if (isPartitioned) {
                    String partitionName = "p_default";
                    
                    if (StringUtils.isNotEmpty(batchField) && recordMap.containsKey(batchField.toLowerCase())) {
                        Object dateValue = recordMap.get(batchField.toLowerCase());
                        if (dateValue instanceof Date) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime((Date) dateValue);
                            int year = calendar.get(Calendar.YEAR);
                            partitionName = "p" + year;
                        }
                    }
                    
                    partitionedData.computeIfAbsent(partitionName, k -> new ArrayList<>()).add(recordMap);
                } else {
                    normalData.add(recordMap);
                }

                count++;

            } catch (Exception e) {
                String recordId = record.getId() != null ? record.getId() : "未知";
                log.error("处理记录时发生异常，记录ID: {}", recordId, e);
            }
        }

        try {
            if (isPartitioned) {
                for (Map.Entry<String, List<Map<String, Object>>> entry : partitionedData.entrySet()) {
                    String partitionName = entry.getKey();
                    List<Map<String, Object>> dataList = entry.getValue();
                    if (!dataList.isEmpty()) {
                        if (!dataList.isEmpty()) {
                            Collection<String> keys = dataList.get(0).keySet();
                            Collection<Collection<Object>> values = new ArrayList<>();
                            for (Map<String, Object> recordMap : dataList) {
                                Collection<Object> recordValues = new ArrayList<>();
                                for (String key : keys) {
                                    recordValues.add(recordMap.get(key));
                                }
                                values.add(recordValues);
                            }
                            customMapper.saveBatchToPartition(api.toLowerCase(), partitionName, keys, values);
                            log.info("批量插入分区 {} 数据 {} 条", partitionName, dataList.size());
                        }
                    }
                }
            } else {
                if (!normalData.isEmpty()) {
                    Collection<String> keys = normalData.get(0).keySet();
                    Collection<Collection<Object>> values = new ArrayList<>();
                    for (Map<String, Object> recordMap : normalData) {
                        Collection<Object> recordValues = new ArrayList<>();
                        for (String key : keys) {
                            recordValues.add(recordMap.get(key));
                        }
                        values.add(recordValues);
                    }
                    customMapper.saveBatch(api.toLowerCase(), keys, values);
                    log.info("批量插入普通表数据 {} 条", normalData.size());
                }
            }
        } catch (Exception e) {
            log.error("批量处理数据时发生异常，API: {}", api, e);
            fallbackToSingleRecordProcessing(api, records, fieldList, isPartitioned, batchField);
        }

        return count;
    }

    /**
     * 批量处理失败时的回退方案，使用单条记录处理
     *
     * @param api       API名称
     * @param records   记录列表
     * @param fieldList 字段列表
     * @param isPartitioned 是否为分区表
     * @param batchField 批次字段
     */
    private void fallbackToSingleRecordProcessing(String api, SObject[] records, List<String> fieldList, boolean isPartitioned, String batchField) {
        log.warn("批量处理数据失败，将回退到单条记录处理，API: {}", api);
        
        if (records == null || records.length == 0) {
            log.info("回退处理API {} 的记录，共 0 条记录", api);
            return;
        }
        
        for (SObject record : records) {
            if (record == null) {
                continue;
            }
            
            try {
                Map<String, Object> recordMap = convertSObjectToMap(record, fieldList);
                
                if (isPartitioned) {
                    String partitionName = "p_default";
                    
                    if (StringUtils.isNotEmpty(batchField) && recordMap.containsKey(batchField.toLowerCase())) {
                        Object dateValue = recordMap.get(batchField.toLowerCase());
                        if (dateValue instanceof Date) {
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime((Date) dateValue);
                            int year = calendar.get(Calendar.YEAR);
                            partitionName = "p" + year;
                        }
                    }
                    
                    customMapper.upsertToPartition(api.toLowerCase(), partitionName, recordMap);
                } else {
                    customMapper.upsert(api.toLowerCase(), recordMap);
                }
            } catch (Exception e) {
                String recordId = record.getId() != null ? record.getId() : "未知";
                log.error("单条处理记录时发生异常，记录ID: {}", recordId, e);
            }
        }
    }

    /**
     * 检查表是否已分区
     *
     * @param tableName 表名
     * @return 是否已分区
     */
    private boolean checkIfTablePartitioned(String tableName) {
        Boolean isPartitioned = customMapper.isPartitioned(tableName);
        return (isPartitioned != null) ? isPartitioned : false;
    }

    /**
     * 将SObject转换为Map
     *
     * @param record SObject记录
     * @param fieldList 字段列表
     * @return 转换后的Map
     */
    private Map<String, Object> convertSObjectToMap(SObject record, List<String> fieldList) {
        Map<String, Object> recordMap = new HashMap<>();

        if (record.getId() != null) {
            recordMap.put("Id", record.getId());
        }

        for (String field : fieldList) {
            if ("Id".equalsIgnoreCase(field)) {
                continue;
            }

            try {
                Object value = record.getField(field);
                if (value != null) {
                    if (value instanceof java.util.Calendar) {
                        recordMap.put(field.toLowerCase(), ((Calendar) value).getTime());
                    } else {
                        recordMap.put(field.toLowerCase(), value);
                    }
                } else {
                    recordMap.put(field.toLowerCase(), null);
                }
            } catch (Exception e) {
                log.warn("获取字段 {} 的值时发生异常", field, e);
            }
        }

        return recordMap;
    }

    /**
     * Salesforce查询参数内部类
     */
    private static class SalesforceParam {
        private String api;
        private String select;
        private Boolean isDeleted;

        public String getApi() {
            return api;
        }

        public void setApi(String api) {
            this.api = api;
        }

        public String getSelect() {
            return select;
        }

        public void setSelect(String select) {
            this.select = select;
        }

        public Boolean getIsDeleted() {
            return isDeleted;
        }

        public void setIsDeleted(Boolean isDeleted) {
            this.isDeleted = isDeleted;
        }
    }
}
