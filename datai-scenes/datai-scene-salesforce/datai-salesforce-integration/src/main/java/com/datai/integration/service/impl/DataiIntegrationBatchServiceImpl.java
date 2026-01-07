package com.datai.integration.service.impl;

import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Supplier;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.datai.common.utils.DateUtils;
import com.datai.common.utils.SecurityUtils;
import com.datai.integration.model.param.DataiSyncParam;
import com.datai.salesforce.common.utils.SoqlBuilder;
import com.datai.integration.util.ConvertUtil;
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

            boolean retryResult = syncObjectDataByBatch(batch.getApi(), id);

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

            log.info("准备同步对象 {} 的批次 {} 数据", objectApi, id);

            boolean syncResult = syncObjectDataByBatch(objectApi, id);

            batch = selectDataiIntegrationBatchById(id);
            
            if (syncResult) {
                log.info("批次 {} 数据同步成功", id);
                result.put("success", true);
                result.put("message", "批次数据同步成功");
                result.put("batchId", id);
                result.put("api", objectApi);
                result.put("label", batch.getLabel());
                result.put("syncNum", batch.getDbNum());
            } else {
                log.error("批次 {} 数据同步失败", id);
                result.put("success", false);
                result.put("message", "批次数据同步失败");
                result.put("batchId", id);
                result.put("api", objectApi);
                result.put("syncNum", batch.getDbNum() != null ? batch.getDbNum() : 0);
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
    private boolean syncObjectDataByBatch(String objectApi, Integer batchId) {
        log.info("准备同步Salesforce对象的指定批次数据，对象API: {}, 批次ID: {}", objectApi, batchId);
        
        if (objectApi == null || objectApi.trim().isEmpty() || batchId == null ) {
            log.error("对象API或批次ID为空，无法同步指定批次数据");
            return false;
        }
        
        DataiIntegrationBatch batch = null;
        DataiIntegrationBatchHistory batchHistory = new DataiIntegrationBatchHistory();
        long startTime = System.currentTimeMillis();
        LocalDateTime syncStartTime = LocalDateTime.now();
        
        try {
            batch = selectDataiIntegrationBatchById(batchId);
            if (batch == null) {
                log.error("批次不存在，批次ID: {}", batchId);
                return false;
            }
            
            PartnerConnection connection = retryOperation(() -> soapConnectionFactory.getConnection(), 3, 1000);

            List<String> fieldList = getSalesforceObjectFields(connection, objectApi);
            log.info("获取到对象 {} 的字段列表: {}", objectApi, fieldList);

            DataiSyncParam param = new DataiSyncParam();
            param.setApi(objectApi);
            param.setSelect(String.join(",", fieldList));
            param.setBatchField(batch.getBatchField());

            if (integrationFieldService.isDeletedFieldExists(objectApi)) {
                param.setIsDeleted(true);
            }

            if (batch.getSyncStartDate() != null) {
                param.setBeginDate(java.sql.Timestamp.valueOf(batch.getSyncStartDate()));
            }
            if (batch.getSyncEndDate() != null) {
                param.setEndDate(java.sql.Timestamp.valueOf(batch.getSyncEndDate()));
            }

            int sfTotalCount = querySalesforceDataCount(connection, param);
            log.info("对象 {} 批次 {} Salesforce中共有 {} 条记录需要同步", objectApi, batchId, sfTotalCount);
            
            int dbProcessedCount = executeQueryAndProcessData(connection, param, fieldList);
            log.info("对象 {} 批次 {} 数据同步完成，共处理 {} 条记录", objectApi, batchId, dbProcessedCount);
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            LocalDateTime syncEndTime = LocalDateTime.now();
            
            batchHistory.setApi(objectApi);
            batchHistory.setLabel(batch.getLabel());
            batchHistory.setBatchId(batchId);
            batchHistory.setBatchField(batch.getBatchField());
            batchHistory.setSyncNum(dbProcessedCount);
            batchHistory.setSyncType(batch.getSyncType());
            batchHistory.setSyncStatus(true);
            batchHistory.setStartTime(syncStartTime);
            batchHistory.setEndTime(syncEndTime);
            batchHistory.setCost(duration);
            batchHistory.setSyncStartTime(syncStartTime);
            batchHistory.setSyncEndTime(syncEndTime);
            batchHistoryService.insertDataiIntegrationBatchHistory(batchHistory);
            log.info("批次 {} 历史记录已保存，同步数据量: {}, 耗时: {}ms", batchId, dbProcessedCount, duration);
            
            batch.setSfNum(sfTotalCount);
            batch.setDbNum(dbProcessedCount);
            batch.setSyncStatus(true);
            if (batch.getFirstSyncTime() == null) {
                batch.setFirstSyncTime(syncStartTime);
            }
            batch.setLastSyncTime(syncEndTime);
            batch.setUpdateTime(DateUtils.getNowDate());
            updateDataiIntegrationBatch(batch);
            log.info("批次 {} 信息已更新，SF数据量: {}, 本地数据量: {}, 同步状态: {}, 首次同步时间: {}, 最后同步时间: {}", 
                    batchId, sfTotalCount, dbProcessedCount, true, batch.getFirstSyncTime(), syncEndTime);
            
            insertSyncLog(objectApi, batch.getLabel(), batch.getSyncType(), batchId, true, null, duration);
            
            return true;
        } catch (Exception e) {
            log.error("同步Salesforce对象指定批次数据失败，对象API: {}, 批次ID: {}", objectApi, batchId, e);
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            LocalDateTime syncEndTime = LocalDateTime.now();
            
            batchHistory.setApi(objectApi);
            batchHistory.setLabel(batch != null ? batch.getLabel() : "");
            batchHistory.setBatchId(batchId);
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
            
            if (batch != null) {
                batch.setSyncStatus(false);
                batch.setLastSyncTime(syncEndTime);
                batch.setUpdateTime(DateUtils.getNowDate());
                updateDataiIntegrationBatch(batch);
                log.info("批次 {} 信息已更新，同步状态: {}, 结束时间: {}", batchId, false, syncEndTime);
            }
            
            insertSyncLog(objectApi, batch != null ? batch.getLabel() : "", batch != null ? batch.getSyncType() : "", batchId, false, e.getMessage(), duration);
            
            return false;
        }
    }

    private void insertSyncLog(String objectApi, String label, String syncType, Integer batchId, boolean success, String errorMessage, long duration) {
        try {
            com.datai.integration.model.domain.DataiIntegrationSyncLog syncLog = new com.datai.integration.model.domain.DataiIntegrationSyncLog();
            syncLog.setObjectApi(objectApi);
            syncLog.setBatchId(batchId);
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
     * 查询Salesforce数据量
     *
     * @param connection Salesforce连接
     * @param param 查询参数
     * @return 符合条件的总记录数
     */
    private int querySalesforceDataCount(PartnerConnection connection, DataiSyncParam param) {
        int totalCount = 0;
        
        try {
            String countQuery = buildCountQuery(param);
            log.info("查询数据量SQL: {}", countQuery);
            
            QueryResult result = connection.queryAll(countQuery);
            
            if (result != null && result.getRecords() != null && result.getRecords().length > 0) {
                SObject record = result.getRecords()[0];
                Object countValue = record.getField("expr0");
                if (countValue != null) {
                    totalCount = Integer.parseInt(countValue.toString());
                }
            }
            
            log.info("对象 {} 符合条件的总记录数: {}", param.getApi(), totalCount);
        } catch (Exception e) {
            log.error("查询数据量时发生异常，对象API: {}", param.getApi(), e);
        }
        
        return totalCount;
    }

    /**
     * 构建COUNT查询语句
     *
     * @param param 查询参数
     * @return SOQL COUNT查询语句
     */
    private String buildCountQuery(DataiSyncParam param) {
        SoqlBuilder builder = new SoqlBuilder();
        builder.from(param.getApi());
        
        if (param.getIsDeleted() != null && param.getIsDeleted()) {
            builder.where("IsDeleted = false");
        }
        
        if (param.getBeginDate() != null && param.getEndDate() != null) {
            builder.whereGe(param.getBatchField(), param.getBeginDate())
                   .whereLe(param.getBatchField(), param.getEndDate());
        } else if (param.getBeginDate() != null) {
            builder.whereGe(param.getBatchField(), param.getBeginDate());
        } else if (param.getEndDate() != null) {
            builder.whereLe(param.getBatchField(), param.getEndDate());
        }
        
        if (param.getBeginCreateDate() != null && param.getEndCreateDate() != null) {
            builder.whereGe(param.getBatchField(), param.getBeginCreateDate())
                   .whereLe(param.getBatchField(), param.getEndCreateDate());
        } else if (param.getBeginCreateDate() != null) {
            builder.whereGe(param.getBatchField(), param.getBeginCreateDate());
        } else if (param.getEndCreateDate() != null) {
            builder.whereLe(param.getBatchField(), param.getEndCreateDate());
        }
        
        if (param.getBeginModifyDate() != null && param.getEndModifyDate() != null) {
            builder.whereGe(param.getBatchField(), param.getBeginModifyDate())
                   .whereLe(param.getBatchField(), param.getEndModifyDate());
        } else if (param.getBeginModifyDate() != null) {
            builder.whereGe(param.getBatchField(), param.getBeginModifyDate());
        } else if (param.getEndModifyDate() != null) {
            builder.whereLe(param.getBatchField(), param.getEndModifyDate());
        }
        
        return builder.buildCountQuery();
    }

    /**
     * 执行查询并处理结果
     *
     * @param connection SOAP连接
     * @param param      查询参数
     * @param fieldList  字段列表
     * @return 处理的数据条数
     */
    private int executeQueryAndProcessData(PartnerConnection connection, DataiSyncParam param, List<String> fieldList) {
        int totalCount = 0;
        JSONArray objects = null;
        String maxId = null;
        Date lastDate = null;

        try {
            DescribeSObjectResult describeSObject = connection.describeSObject(param.getApi());
            Field[] dsrFields = describeSObject.getFields();

            while (true) {
                DataiSyncParam queryParam = new DataiSyncParam();
                queryParam.setApi(param.getApi());
                queryParam.setSelect(param.getSelect());
                queryParam.setBatchField(param.getBatchField());
                queryParam.setIdField(param.getIdField());
                queryParam.setIsDeleted(param.getIsDeleted());
                
                queryParam.setBeginDate(param.getBeginDate());
                queryParam.setEndDate(param.getEndDate());
                queryParam.setLastDate(lastDate);
                queryParam.setMaxId(maxId);
                
                String query = buildDynamicQuery(queryParam);
                log.info("执行查询SQL: {}", query);
                QueryResult result = connection.queryAll(query);

                if (result.getRecords() != null && result.getRecords().length > 0) {
                    objects = ConvertUtil.toJsonArray(result.getRecords(), dsrFields);
                    Date maxDate = objects.getJSONObject(objects.size() - 1).getDate(param.getBatchField());
                    Optional<String> maxIdOptional = objects.stream()
                            .map(t -> (JSONObject) t)
                            .filter(t -> maxDate.equals(t.getDate(param.getBatchField())))
                            .map(t -> t.getString("Id"))
                            .max(String::compareTo);
                    maxId = maxIdOptional.orElse(null);
                    lastDate = maxDate;

                    totalCount += processQueryResult(param.getApi(), result.getRecords(), objects);
                    log.info("已处理 {} 条记录", totalCount);
                }

                if (result.isDone() || result.getRecords() == null || result.getRecords().length == 0) {
                    break;
                }
            }
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
    private String buildDynamicQuery(DataiSyncParam param) {
        SoqlBuilder builder = new SoqlBuilder();
        
        String[] selectFields = param.getSelect().split(",");
        builder.select(selectFields)
               .from(param.getApi());
        
        if (param.getIsDeleted() != null && param.getIsDeleted()) {
            builder.where("IsDeleted = false");
        }
        
        if (param.getBeginDate() != null && param.getEndDate() != null) {
            builder.whereGe(param.getBatchField(), param.getBeginDate())
                   .whereLe(param.getBatchField(), param.getEndDate());
        } else if (param.getBeginDate() != null) {
            builder.whereGe(param.getBatchField(), param.getBeginDate());
        } else if (param.getEndDate() != null) {
            builder.whereLe(param.getBatchField(), param.getEndDate());
        }
        
        if (param.getBeginCreateDate() != null && param.getEndCreateDate() != null) {
            builder.whereGe(param.getBatchField(), param.getBeginCreateDate())
                   .whereLe(param.getBatchField(), param.getEndCreateDate());
        } else if (param.getBeginCreateDate() != null) {
            builder.whereGe(param.getBatchField(), param.getBeginCreateDate());
        } else if (param.getEndCreateDate() != null) {
            builder.whereLe(param.getBatchField(), param.getEndCreateDate());
        }

        if (param.getLastDate() != null) {
            builder.whereLe(param.getBatchField(), param.getLastDate());
        }
        
        if (param.getBeginModifyDate() != null && param.getEndModifyDate() != null) {
            builder.whereGe(param.getBatchField(), param.getBeginModifyDate())
                   .whereLe(param.getBatchField(), param.getEndModifyDate());
        } else if (param.getBeginModifyDate() != null) {
            builder.whereGe(param.getBatchField(), param.getBeginModifyDate());
        } else if (param.getEndModifyDate() != null) {
            builder.whereLe(param.getBatchField(), param.getEndModifyDate());
        }
        
        if (StringUtils.isNotEmpty(param.getMaxId())) {
            builder.whereGt(param.getIdField(), param.getMaxId());
        }
        
        if (param.getLimit() != null && param.getLimit() > 0) {
            builder.limit(param.getLimit());
        }

        
        return builder.build();
    }

    /**
     * 处理查询结果
     *
     * @param api       API名称

     * @return 处理的数据条数
     */
    private int processQueryResult(String api, SObject[] records, JSONArray objects) {
        int count = 0;
        
        log.info("处理API {} 的查询结果，共 {} 条记录", api, records.length);

        boolean isPartitioned = checkIfTablePartitioned(api);
        String batchField = null;
        if (isPartitioned) {
            batchField = integrationFieldService.getDateField(api);
        }

        if (objects == null || objects.isEmpty()) {
            log.info("API {} 没有需要处理的数据", api);
            return 0;
        }

        try {
            List<String> ids = Arrays.stream(records).map(SObject::getId).collect(Collectors.toList());
            List<String> existsIds = customMapper.getIds(api, ids);
            
            String partitionName = null;
            if (isPartitioned && batchField != null) {
                partitionName = buildPartitionName(batchField, objects);
            }

            List<Map<String, Object>> insertMaps = new ArrayList<>();
            List<Map<String, Object>> updateMaps = new ArrayList<>();
            List<String> updateIds = new ArrayList<>();

            for (int i = 0; i < objects.size(); i++) {
                JSONObject jsonObject = objects.getJSONObject(i);
                String id = jsonObject.getString("Id");
                
                Map<String, Object> recordMap = new HashMap<>();
                for (String key : jsonObject.keySet()) {
                    recordMap.put(key, jsonObject.get(key));
                }

                if (existsIds.contains(id)) {
                    updateMaps.add(recordMap);
                    updateIds.add(id);
                } else {
                    insertMaps.add(recordMap);
                }
            }

            if (!insertMaps.isEmpty()) {
                List<String> keys = new ArrayList<>(insertMaps.get(0).keySet());
                List<Collection<Object>> values = new ArrayList<>();
                
                for (Map<String, Object> recordMap : insertMaps) {
                    List<Object> rowValues = new ArrayList<>();
                    for (String key : keys) {
                        rowValues.add(recordMap.get(key));
                    }
                    values.add(rowValues);
                }

                if (isPartitioned && partitionName != null) {
                    customMapper.saveBatchToPartition(api, partitionName, keys, values);
                    log.info("成功插入 {} 条记录到分区表 {} 的分区 {}", values.size(), api, partitionName);
                } else {
                    customMapper.saveBatch(api, keys, values);
                    log.info("成功插入 {} 条记录到表 {}", values.size(), api);
                }
                count += values.size();
            }

            if (!updateMaps.isEmpty()) {
                for (int i = 0; i < updateMaps.size(); i++) {
                    Map<String, Object> recordMap = updateMaps.get(i);
                    String id = updateIds.get(i);
                    
                    List<Map<String, Object>> maps = new ArrayList<>();
                    for (Map.Entry<String, Object> entry : recordMap.entrySet()) {
                        Map<String, Object> paramMap = new HashMap<>();
                        paramMap.put("key", entry.getKey());
                        paramMap.put("value", entry.getValue());
                        maps.add(paramMap);
                    }

                    if (isPartitioned && partitionName != null) {
                        customMapper.updateByIdFromPartition(api, partitionName, maps, id);
                    } else {
                        customMapper.updateById(api, maps, id);
                    }
                }
                log.info("成功更新 {} 条记录到表 {}", updateMaps.size(), api);
                count += updateMaps.size();
            }
            
        } catch (Exception e) {
            log.error("处理API {} 的查询结果时发生异常", api, e);
            throw new RuntimeException("处理查询结果失败: " + e.getMessage(), e);
        }

        return count;
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
     * 根据批次字段和数据构建分区名
     *
     * @param batchField 批次字段名
     * @param objects 数据对象数组
     * @return 分区名（如p2025）
     */
    private String buildPartitionName(String batchField, JSONArray objects) {
        String partitionName = "p_default";
        
        if (StringUtils.isNotEmpty(batchField) && !objects.isEmpty()) {
            JSONObject firstRecord = objects.getJSONObject(0);
            Object dateValue = firstRecord.get(batchField);
            
            if (dateValue instanceof Date) {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime((Date) dateValue);
                int year = calendar.get(Calendar.YEAR);
                partitionName = "p" + year;
            }
        }
        
        return partitionName;
    }


}
