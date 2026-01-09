package com.datai.integration.service.impl;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.TimeUnit;

import com.datai.common.utils.DateUtils;
import com.datai.common.utils.SecurityUtils;
import com.datai.common.utils.CacheUtils;
import com.datai.middleware.redis.utils.RedisCache;
import com.datai.integration.factory.impl.SOAPConnectionFactory;
import com.datai.integration.core.IPartnerV1Connection;
import com.datai.integration.mapper.CustomMapper;
import com.datai.integration.model.domain.DataiIntegrationBatch;
import com.datai.integration.model.domain.DataiIntegrationBatchField;
import com.datai.integration.model.domain.DataiIntegrationMetadataChange;
import com.datai.integration.model.domain.DataiIntegrationObject;
import com.datai.integration.model.domain.DataiIntegrationField;
import com.datai.integration.service.IDataiIntegrationMetadataChangeService;
import com.datai.integration.service.IDataiIntegrationObjectService;
import com.datai.integration.service.IDataiIntegrationFieldService;
import com.datai.integration.service.IDataiIntegrationPicklistService;
import com.datai.integration.service.IDataiIntegrationFilterLookupService;
import com.datai.integration.service.IDataiIntegrationBatchService;
import com.datai.setting.service.IDataiConfigurationService;
import com.datai.salesforce.common.constant.SalesforceConfigConstants;
import com.datai.common.core.domain.model.LoginUser;
import com.sforce.soap.partner.*;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.datai.setting.config.SalesforceConfigCacheManager;
import com.datai.setting.future.SalesforceExecutor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.datai.integration.mapper.DataiIntegrationMetadataChangeMapper;

/**
 * 对象元数据变更Service业务层处理
 *
 * @author datai
 * @date 2025-12-27
 */
@Service
@Slf4j
public class DataiIntegrationMetadataChangeServiceImpl implements IDataiIntegrationMetadataChangeService {
    /**
     * 大数据量对象阈值（500万）
     */
    private static final int LARGE_OBJECT_THRESHOLD = 5000000;

    @Autowired
    private DataiIntegrationMetadataChangeMapper dataiIntegrationMetadataChangeMapper;

    @Autowired
    private IDataiIntegrationObjectService dataiIntegrationObjectService;

    @Autowired
    private IDataiIntegrationFieldService dataiIntegrationFieldService;

    @Autowired
    private IDataiIntegrationPicklistService dataiIntegrationPicklistService;

    @Autowired
    private IDataiIntegrationFilterLookupService dataiIntegrationFilterLookupService;

    @Autowired
    private IDataiIntegrationBatchService dataiIntegrationBatchService;

    @Autowired
    private IDataiConfigurationService dataiConfigurationService;

    @Autowired
    private SalesforceConfigCacheManager salesforceConfigCacheManager;

    @Autowired
    private SOAPConnectionFactory soapConnectionFactory;

    @Autowired
    private CustomMapper customMapper;

    @Autowired
    private SalesforceExecutor salesforceExecutor;

    @Autowired
    private RedisCache redisCache;

    /**
     * 查询对象元数据变更
     *
     * @param id 对象元数据变更主键
     * @return 对象元数据变更
     */
    @Override
    public DataiIntegrationMetadataChange selectDataiIntegrationMetadataChangeById(Long id)
    {
        return dataiIntegrationMetadataChangeMapper.selectDataiIntegrationMetadataChangeById(id);
    }

    /**
     * 查询对象元数据变更列表
     *
     * @param dataiIntegrationMetadataChange 对象元数据变更
     * @return 对象元数据变更
     */
    @Override
    public List<DataiIntegrationMetadataChange> selectDataiIntegrationMetadataChangeList(DataiIntegrationMetadataChange dataiIntegrationMetadataChange)
    {
        return dataiIntegrationMetadataChangeMapper.selectDataiIntegrationMetadataChangeList(dataiIntegrationMetadataChange);
    }

    @Override
    public List<DataiIntegrationMetadataChange> selectUnsyncedMetadataChangeList(DataiIntegrationMetadataChange dataiIntegrationMetadataChange)
    {
        return dataiIntegrationMetadataChangeMapper.selectUnsyncedMetadataChangeList(dataiIntegrationMetadataChange);
    }

    /**
     * 新增对象元数据变更
     *
     * @param dataiIntegrationMetadataChange 对象元数据变更
     * @return 结果
     */
    @Override
    public int insertDataiIntegrationMetadataChange(DataiIntegrationMetadataChange dataiIntegrationMetadataChange)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

        dataiIntegrationMetadataChange.setCreateTime(DateUtils.getNowDate());
        dataiIntegrationMetadataChange.setUpdateTime(DateUtils.getNowDate());
        dataiIntegrationMetadataChange.setCreateBy(username);
        dataiIntegrationMetadataChange.setUpdateBy(username);
        return dataiIntegrationMetadataChangeMapper.insertDataiIntegrationMetadataChange(dataiIntegrationMetadataChange);
    }

    /**
     * 修改对象元数据变更
     *
     * @param dataiIntegrationMetadataChange 对象元数据变更
     * @return 结果
     */
    @Override
    public int updateDataiIntegrationMetadataChange(DataiIntegrationMetadataChange dataiIntegrationMetadataChange)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

        dataiIntegrationMetadataChange.setUpdateTime(DateUtils.getNowDate());
        dataiIntegrationMetadataChange.setUpdateBy(username);
        return dataiIntegrationMetadataChangeMapper.updateDataiIntegrationMetadataChange(dataiIntegrationMetadataChange);
    }

    /**
     * 批量删除对象元数据变更
     *
     * @param ids 需要删除的对象元数据变更主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationMetadataChangeByIds(Long[] ids)
    {
        return dataiIntegrationMetadataChangeMapper.deleteDataiIntegrationMetadataChangeByIds(ids);
    }

    /**
     * 删除对象元数据变更信息
     *
     * @param id 对象元数据变更主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationMetadataChangeById(Long id)
    {
        return dataiIntegrationMetadataChangeMapper.deleteDataiIntegrationMetadataChangeById(id);
    }

    @Override
    public int batchUpdateSyncStatus(Long[] ids, Integer syncStatus, String syncErrorMessage)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();
        return dataiIntegrationMetadataChangeMapper.batchUpdateSyncStatus(ids, syncStatus, syncErrorMessage, username);
    }

    @Override
    public Map<String, Object> getChangeStatistics(Map<String, Object> params)
    {
        log.info("开始获取变更统计信息，参数: {}", params);
        
        // 生成缓存键
        String cacheKey = generateCacheKey("change_statistics", params);
        log.debug("生成缓存键: {}", cacheKey);
        
        // 尝试从缓存获取
        Map<String, Object> cachedStatistics = getFromCache(cacheKey, "变更统计信息");
        if (cachedStatistics != null) {
            return cachedStatistics;
        }
        
        Map<String, Object> statistics = new HashMap<>();

        try {
            long startTime = System.currentTimeMillis();
            log.debug("开始执行数据库查询");
            
            Map<String, Object> result = dataiIntegrationMetadataChangeMapper.selectChangeStatistics(params);
            long queryTime = System.currentTimeMillis() - startTime;
            
            log.info("变更统计查询耗时: {}ms，查询结果: {}", queryTime, result);
            
            int totalCount = result.get("totalCount") != null ? ((Number) result.get("totalCount")).intValue() : 0;
            int syncedCount = result.get("syncedCount") != null ? ((Number) result.get("syncedCount")).intValue() : 0;
            int unsyncedCount = result.get("unsyncedCount") != null ? ((Number) result.get("unsyncedCount")).intValue() : 0;
            
            double syncRate = calculateSyncRate(totalCount, syncedCount);
            
            statistics.put("success", true);
            statistics.put("message", "获取变更统计信息成功");
            statistics.put("data", new HashMap<String, Object>() {{
                put("totalCount", totalCount);
                put("syncedCount", syncedCount);
                put("unsyncedCount", unsyncedCount);
                put("syncRate", syncRate);
                put("objectChangeCount", result.get("objectChangeCount"));
                put("fieldChangeCount", result.get("fieldChangeCount"));
                put("createCount", result.get("createCount"));
                put("updateCount", result.get("updateCount"));
                put("deleteCount", result.get("deleteCount"));
                put("customCount", result.get("customCount"));
                put("standardCount", result.get("standardCount"));
                put("objectCount", result.get("objectCount"));
                put("fieldCount", result.get("fieldCount"));
                put("totalRetryCount", result.get("totalRetryCount"));
                put("maxRetryCount", result.get("maxRetryCount"));
                put("firstChangeTime", result.get("firstChangeTime"));
                put("lastChangeTime", result.get("lastChangeTime"));
                put("firstSyncTime", result.get("firstSyncTime"));
                put("lastSyncTime", result.get("lastSyncTime"));
            }});
            
            // 将结果存入缓存，设置5分钟过期
            cacheData(cacheKey, statistics, "变更统计信息");
            
        } catch (Exception e) {
            log.error("获取变更统计信息失败: {}", e.getMessage(), e);
            statistics.put("success", false);
            statistics.put("message", "获取变更统计信息失败: " + e.getMessage());
        }
        
        log.info("获取变更统计信息完成，结果: {}", statistics.get("success"));
        return statistics;
    }
    
    /**
     * 生成缓存键
     * 
     * @param prefix 前缀
     * @param params 查询参数
     * @return 缓存键
     */
    private String generateCacheKey(String prefix, Map<String, Object> params) {
        if (params == null || params.isEmpty()) {
            return prefix + ":default";
        }
        
        // 对参数进行排序，确保相同参数生成相同的键
        List<String> sortedKeys = new ArrayList<>(params.keySet());
        Collections.sort(sortedKeys);
        
        StringBuilder keyBuilder = new StringBuilder(prefix);
        for (String key : sortedKeys) {
            Object value = params.get(key);
            if (value != null) {
                keyBuilder.append(":").append(key).append("_").append(value.toString());
            }
        }
        
        return keyBuilder.toString();
    }
    
    /**
     * 从缓存获取数据
     * 
     * @param cacheKey 缓存键
     * @param operationName 操作名称
     * @return 缓存数据
     */
    private <T> T getFromCache(String cacheKey, String operationName) {
        try {
            T cachedData = redisCache.getCacheObject(cacheKey);
            if (cachedData != null) {
                log.info("从缓存获取{}成功", operationName);
                return cachedData;
            }
        } catch (Exception e) {
            log.warn("从缓存获取{}失败，将执行数据库查询: {}", operationName, e.getMessage());
        }
        return null;
    }
    
    /**
     * 缓存数据
     * 
     * @param cacheKey 缓存键
     * @param data 数据
     * @param operationName 操作名称
     */
    private void cacheData(String cacheKey, Object data, String operationName) {
        try {
            redisCache.setCacheObject(cacheKey, data, 5, TimeUnit.MINUTES);
            log.info("{}已缓存，过期时间: 5分钟", operationName);
        } catch (Exception e) {
            log.warn("缓存{}失败: {}", operationName, e.getMessage());
        }
    }
    
    /**
     * 计算同步率
     * 
     * @param totalCount 总数量
     * @param syncedCount 已同步数量
     * @return 同步率
     */
    private double calculateSyncRate(int totalCount, int syncedCount) {
        return totalCount > 0 ? (double) syncedCount / totalCount * 100 : 0;
    }
    
    /**
     * 计算分组的同步率
     * 
     * @param resultList 分组结果列表
     */
    private void calculateGroupSyncRates(List<Map<String, Object>> resultList) {
        for (Map<String, Object> result : resultList) {
            int totalCount = result.get("totalCount") != null ? ((Number) result.get("totalCount")).intValue() : 0;
            int syncedCount = result.get("syncedCount") != null ? ((Number) result.get("syncedCount")).intValue() : 0;
            double syncRate = calculateSyncRate(totalCount, syncedCount);
            result.put("syncRate", syncRate);
        }
    }

    @Override
    public Map<String, Object> getChangeStatisticsByGroup(Map<String, Object> params)
    {
        log.info("开始获取分组变更统计信息，参数: {}", params);
        
        // 生成缓存键
        String cacheKey = generateCacheKey("change_statistics_group", params);
        log.debug("生成缓存键: {}", cacheKey);
        
        // 尝试从缓存获取
        Map<String, Object> cachedStatistics = getFromCache(cacheKey, "分组变更统计信息");
        if (cachedStatistics != null) {
            return cachedStatistics;
        }
        
        Map<String, Object> statistics = new HashMap<>();

        try {
            long startTime = System.currentTimeMillis();
            log.debug("开始执行数据库查询");
            
            List<Map<String, Object>> resultList = dataiIntegrationMetadataChangeMapper.selectChangeStatisticsByGroup(params);
            long queryTime = System.currentTimeMillis() - startTime;
            
            log.info("分组变更统计查询耗时: {}ms，查询结果数量: {}", queryTime, resultList.size());
            
            // 计算每个分组的同步率
            calculateGroupSyncRates(resultList);
            
            statistics.put("success", true);
            statistics.put("message", "获取分组变更统计信息成功");
            statistics.put("data", resultList);
            statistics.put("totalGroups", resultList.size());
            
            // 将结果存入缓存，设置5分钟过期
            cacheData(cacheKey, statistics, "分组变更统计信息");
            
        } catch (Exception e) {
            log.error("获取分组变更统计信息失败: {}", e.getMessage(), e);
            statistics.put("success", false);
            statistics.put("message", "获取分组变更统计信息失败: " + e.getMessage());
        }
        
        log.info("获取分组变更统计信息完成，结果: {}", statistics.get("success"));
        return statistics;
    }

    @Override
    public Map<String, Object> getChangeStatisticsByTrend(Map<String, Object> params)
    {
        log.info("开始获取趋势变更统计信息，参数: {}", params);
        
        // 生成缓存键
        String cacheKey = generateCacheKey("change_statistics_trend", params);
        log.debug("生成缓存键: {}", cacheKey);
        
        // 尝试从缓存获取
        Map<String, Object> cachedStatistics = getFromCache(cacheKey, "趋势变更统计信息");
        if (cachedStatistics != null) {
            return cachedStatistics;
        }
        
        Map<String, Object> statistics = new HashMap<>();

        try {
            long startTime = System.currentTimeMillis();
            log.debug("开始执行数据库查询");
            
            List<Map<String, Object>> resultList = dataiIntegrationMetadataChangeMapper.selectChangeStatisticsByTrend(params);
            long queryTime = System.currentTimeMillis() - startTime;
            
            log.info("趋势变更统计查询耗时: {}ms，查询结果数量: {}", queryTime, resultList.size());
            
            // 计算每个时间点的同步率
            calculateGroupSyncRates(resultList);
            
            statistics.put("success", true);
            statistics.put("message", "获取趋势变更统计信息成功");
            statistics.put("data", resultList);
            statistics.put("totalPoints", resultList.size());
            statistics.put("timeUnit", params.get("timeUnit"));
            
            // 将结果存入缓存，设置5分钟过期
            cacheData(cacheKey, statistics, "趋势变更统计信息");
            
        } catch (Exception e) {
            log.error("获取趋势变更统计信息失败: {}", e.getMessage(), e);
            statistics.put("success", false);
            statistics.put("message", "获取趋势变更统计信息失败: " + e.getMessage());
        }
        
        log.info("获取趋势变更统计信息完成，结果: {}", statistics.get("success"));
        return statistics;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> syncToLocalDatabase(Long id) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 根据ID查询元数据变更记录
            DataiIntegrationMetadataChange metadataChange = selectDataiIntegrationMetadataChangeById(id);
            if (metadataChange == null) {
                result.put("success", false);
                result.put("message", "元数据变更记录不存在");
                return result;
            }

            // 获取变更类型和操作类型
            String changeType = metadataChange.getChangeType();
            String operationType = metadataChange.getOperationType();
            
            // 根据变更类型执行相应的同步操作
            if ("OBJECT".equals(changeType)) {
                // 对象级别的变更同步
                syncObjectChange(metadataChange, result);
            } else if ("FIELD".equals(changeType)) {
                // 字段级别的变更同步
                syncFieldChange(metadataChange, result);
            } else {
                result.put("success", false);
                result.put("message", "不支持的变更类型: " + changeType);
                return result;
            }

            // 根据同步结果更新元数据变更记录的同步状态
            if ((Boolean) result.get("success")) {
                // 同步成功，更新同步状态为true
                updateSyncStatus(metadataChange.getId(), true, null);
            } else {
                // 同步失败，更新同步状态为false并记录错误信息
                updateSyncStatus(metadataChange.getId(), false, (String) result.get("message"));
            }

        } catch (Exception e) {
            // 捕获异常并返回错误信息
            result.put("success", false);
            result.put("message", "同步失败: " + e.getMessage());
            updateSyncStatus(id, false, e.getMessage());
        }

        return result;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Object> syncBatchToLocalDatabase(Long[] ids) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> syncResults = Collections.synchronizedList(new ArrayList<>());
        AtomicInteger successCount = new AtomicInteger(0);
        AtomicInteger failCount = new AtomicInteger(0);
        AtomicInteger processedCount = new AtomicInteger(0);
        int totalCount = ids.length;
        
        log.info("开始批量同步元数据变更，总数: {}", totalCount);

        List<Future<?>> futures = new ArrayList<>();

        for (int i = 0; i < ids.length; i++) {
            final Long id = ids[i];
            final int index = i;

            Future<?> future = salesforceExecutor.execute(() -> {
                try {
                    Map<String, Object> syncResult = syncToLocalDatabase(id);
                    
                    Map<String, Object> syncResultItem = new HashMap<>();
                    syncResultItem.put("id", id);
                    syncResultItem.put("success", syncResult.get("success"));
                    syncResultItem.put("message", syncResult.get("message"));
                    syncResults.add(syncResultItem);

                    if ((Boolean) syncResult.get("success")) {
                        successCount.incrementAndGet();
                    } else {
                        failCount.incrementAndGet();
                    }
                    
                    int currentProcessed = processedCount.incrementAndGet();
                    if (currentProcessed % 10 == 0 || currentProcessed == totalCount) {
                        log.info("批量同步进度: {}/{}} (成功: {}, 失败: {})", 
                                currentProcessed, totalCount, successCount.get(), failCount.get());
                    }
                } catch (Exception e) {
                    log.error("同步元数据变更ID {} 时发生异常: {}", id, e.getMessage(), e);
                    
                    Map<String, Object> syncResultItem = new HashMap<>();
                    syncResultItem.put("id", id);
                    syncResultItem.put("success", false);
                    syncResultItem.put("message", "同步异常: " + e.getMessage());
                    syncResults.add(syncResultItem);
                    
                    failCount.incrementAndGet();
                    processedCount.incrementAndGet();
                }
            }, 1, index);
            
            futures.add(future);
        }

        try {
            salesforceExecutor.waitForFutures(futures.toArray(new Future[0]));
            log.info("批量同步完成，总数: {}, 成功: {}, 失败: {}", totalCount, successCount.get(), failCount.get());
        } catch (InterruptedException e) {
            log.error("批量同步被中断: {}", e.getMessage(), e);
            Thread.currentThread().interrupt();
            result.put("success", false);
            result.put("message", "批量同步被中断: " + e.getMessage());
            return result;
        }

        Map<String, Object> data = new HashMap<>();
        data.put("totalCount", totalCount);
        data.put("successCount", successCount.get());
        data.put("failCount", failCount.get());
        data.put("details", syncResults);
        
        result.put("success", failCount.get() == 0);
        result.put("message", String.format("批量同步完成: 成功 %d 条, 失败 %d 条", successCount.get(), failCount.get()));
        result.put("data", data);

        return result;
    }

    private void syncObjectChange(DataiIntegrationMetadataChange metadataChange, Map<String, Object> result) {
        String operationType = metadataChange.getOperationType();
        String objectApi = metadataChange.getObjectApi();

        try {
            if ("INSERT".equals(operationType)) {
                try {
                    log.info("开始同步对象: {}", objectApi);
                    
                    IPartnerV1Connection connection = retryOperation(() -> soapConnectionFactory.getConnection(), 3, 1000);
                    log.info("成功获取源ORG连接");
                    
                    DescribeSObjectResult objDetail = connection.describeSObject(objectApi.trim());
                    
                    DataiIntegrationObject object = buildObjectMetadata(objDetail);
                    
                    if (object == null) {
                        log.error("构建对象 {} 的元数据失败", objectApi);
                        result.put("success", false);
                        result.put("message", "构建对象元数据失败");
                        return;
                    }
                    
                    if (objectApi.equals("Case")) {
                        // 为Case对象特殊处理
                        object.setIsWork(true);
                        object.setIsIncremental(true);
                        DataiIntegrationBatchField batchField = new DataiIntegrationBatchField();
                        batchField.setFieldName("CreatedDate");
                        batchField.setFieldType("datetime");
                        batchField.setApi(objectApi);
                        object.setBatchField(batchField);
                    }
                    
                    int insertResult = dataiIntegrationObjectService.insertDataiIntegrationObject(object);
                    result.put("success", insertResult > 0);
                    result.put("message", insertResult > 0 ? "对象创建成功" : "对象创建失败");
                    
                    if (insertResult > 0) {
                        saveObjectFieldsToDataiIntegrationField(objDetail);
                        
                        int objectNum = isLargeObject(connection, objectApi.trim());
                        log.info("对象 {} 的数据量: {}", objectApi, objectNum);
                        
                        updateDataiIntegrationObjectFields(objectApi.trim(), objectNum);
                        
                        if (objectNum > LARGE_OBJECT_THRESHOLD) {
                            log.info("对象 {} 是大数据量对象，数据量大于五百万，创建分区表", objectApi);
                            createDatabaseTable(objectApi, metadataChange.getObjectLabel(), objDetail, true, result);
                        } else {
                            log.info("对象 {} 是普通对象，数据量少于五百万，创建正常表", objectApi);
                            createDatabaseTable(objectApi, metadataChange.getObjectLabel(), objDetail, false, result);
                        }

                        createBatchesForNewObject(objectApi, metadataChange.getObjectLabel(), connection);
                        
                        log.info("同步对象 {} 成功", objectApi);
                    }
                } catch (ConnectionException e) {
                    log.error("获取对象 {} 的元数据失败: {}", objectApi, e.getMessage(), e);
                    result.put("success", false);
                    result.put("message", "获取对象元数据失败: " + e.getMessage());
                } catch (Exception e) {
                    log.error("处理对象 {} 时出错: {}", objectApi, e.getMessage(), e);
                    result.put("success", false);
                    result.put("message", "处理对象时出错: " + e.getMessage());
                }
                
            } else if ("UPDATE".equals(operationType)) {
                DataiIntegrationObject queryObject = new DataiIntegrationObject();
                queryObject.setApi(objectApi);
                
                List<DataiIntegrationObject> existingObjects = dataiIntegrationObjectService.selectDataiIntegrationObjectList(queryObject);
                
                if (existingObjects.isEmpty()) {
                    result.put("success", false);
                    result.put("message", "对象不存在，无法更新");
                    return;
                }

                DataiIntegrationObject object = existingObjects.get(0);
                object.setLabel(metadataChange.getObjectLabel());
                object.setIsWork(false);
                object.setIsIncremental(false);
                object.setUpdateTime(DateUtils.getNowDate());

                int updateResult = dataiIntegrationObjectService.updateDataiIntegrationObject(object);
                result.put("success", updateResult > 0);
                result.put("message", updateResult > 0 ? "对象更新成功" : "对象更新失败");
                
            } else if ("DELETE".equals(operationType)) {
                DataiIntegrationObject queryObject = new DataiIntegrationObject();
                queryObject.setApi(objectApi);

                List<DataiIntegrationObject> existingObjects = dataiIntegrationObjectService.selectDataiIntegrationObjectList(queryObject);

                if (existingObjects.isEmpty()) {
                    result.put("success", false);
                    result.put("message", "对象不存在，无法更新");
                    return;
                }

                DataiIntegrationObject object = existingObjects.get(0);
                object.setIsWork(false);
                object.setIsIncremental(false);
                object.setUpdateTime(DateUtils.getNowDate());

                int updateResult = dataiIntegrationObjectService.updateDataiIntegrationObject(object);
                result.put("success", updateResult > 0);
                result.put("message", updateResult > 0 ? "对象更新成功" : "对象更新失败");
                
            } else {
                result.put("success", false);
                result.put("message", "不支持的操作类型: " + operationType);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "同步对象变更失败: " + e.getMessage());
            log.error("同步对象变更失败: {}", e.getMessage(), e);
        }
    }

    private void syncFieldChange(DataiIntegrationMetadataChange metadataChange, Map<String, Object> result) {
        String operationType = metadataChange.getOperationType();
        String objectApi = metadataChange.getObjectApi();
        String fieldApi = metadataChange.getFieldApi();

        try {
            if ("INSERT".equals(operationType)) {
                DataiIntegrationField field = new DataiIntegrationField();
                field.setApi(objectApi);
                field.setField(fieldApi);
                field.setLabel(metadataChange.getFieldLabel());
                field.setIsCustom(metadataChange.getIsCustom());
                field.setCreateTime(DateUtils.getNowDate());
                field.setUpdateTime(DateUtils.getNowDate());
                
                int insertResult = dataiIntegrationFieldService.insertDataiIntegrationField(field);
                result.put("success", insertResult > 0);
                result.put("message", insertResult > 0 ? "字段创建成功" : "字段创建失败");
                
                if (insertResult > 0) {
                    addDatabaseColumn(objectApi, fieldApi, result);
                }
                
            } else if ("UPDATE".equals(operationType)) {
                DataiIntegrationField queryField = new DataiIntegrationField();
                queryField.setApi(objectApi);
                queryField.setField(fieldApi);
                
                List<DataiIntegrationField> existingFields = dataiIntegrationFieldService.selectDataiIntegrationFieldList(queryField);
                
                if (existingFields.isEmpty()) {
                    result.put("success", false);
                    result.put("message", "字段不存在，无法更新");
                    return;
                }

                DataiIntegrationField field = existingFields.get(0);
                field.setLabel(metadataChange.getFieldLabel());
                field.setUpdateTime(DateUtils.getNowDate());
                
                int updateResult = dataiIntegrationFieldService.updateDataiIntegrationField(field);
                result.put("success", updateResult > 0);
                result.put("message", updateResult > 0 ? "字段更新成功" : "字段更新失败");
                
                if (updateResult > 0) {
                    modifyDatabaseColumn(objectApi, fieldApi, result);
                }
                
            } else if ("DELETE".equals(operationType)) {
                DataiIntegrationField queryField = new DataiIntegrationField();
                queryField.setApi(objectApi);
                queryField.setField(fieldApi);
                
                List<DataiIntegrationField> existingFields = dataiIntegrationFieldService.selectDataiIntegrationFieldList(queryField);
                
                if (existingFields.isEmpty()) {
                    result.put("success", false);
                    result.put("message", "字段不存在，无法删除");
                    return;
                }

                Integer[] ids = existingFields.stream()
                    .map(DataiIntegrationField::getId)
                    .toArray(Integer[]::new);
                
                int deleteResult = dataiIntegrationFieldService.deleteDataiIntegrationFieldByIds(ids);
                result.put("success", deleteResult > 0);
                result.put("message", deleteResult > 0 ? "字段删除成功" : "字段删除失败");
                
                if (deleteResult > 0) {
                    dropDatabaseColumn(objectApi, fieldApi, result);
                }
                
            } else {
                result.put("success", false);
                result.put("message", "不支持的操作类型: " + operationType);
            }
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "同步字段变更失败: " + e.getMessage());
            log.error("同步字段变更失败: {}", e.getMessage(), e);
        }
    }

    private void updateSyncStatus(Long id, Boolean success, String errorMessage) {
        DataiIntegrationMetadataChange metadataChange = new DataiIntegrationMetadataChange();
        metadataChange.setId(id);
        metadataChange.setSyncStatus(success);
        metadataChange.setSyncTime(LocalDateTime.now());
        if (!success) {
            metadataChange.setSyncErrorMessage(errorMessage);
            DataiIntegrationMetadataChange existing = selectDataiIntegrationMetadataChangeById(id);
            if (existing == null) {
                log.error("元数据变更记录不存在，id: {}", id);
                return;
            }
            int retryCount = existing.getRetryCount() != null ? existing.getRetryCount() + 1 : 1;
            metadataChange.setRetryCount(retryCount);
            metadataChange.setLastRetryTime(LocalDateTime.now());
        }
        updateDataiIntegrationMetadataChange(metadataChange);
    }

    private DataiIntegrationObject buildObjectMetadata(DescribeSObjectResult objDetail) {
        DataiIntegrationObject object = new DataiIntegrationObject();
        object.setApi(objDetail.getName());
        object.setLabel(objDetail.getLabel());
        object.setPluralLabel(objDetail.getLabelPlural());
        object.setCreateable(objDetail.isCreateable());
        object.setUpdateable(objDetail.isUpdateable());
        object.setDeletable(objDetail.isDeletable());
        object.setQueryable(objDetail.isQueryable());
        object.setSearchable(objDetail.isSearchable());
        object.setIsCustom(objDetail.isCustom());
        object.setKeyPrefix(objDetail.getKeyPrefix());
        object.setIsWork(false);
        object.setIsIncremental(false);
        object.setCreateTime(DateUtils.getNowDate());
        object.setUpdateTime(DateUtils.getNowDate());
        return object;
    }

    private void saveObjectFieldsToDataiIntegrationField(DescribeSObjectResult objDetail) {
        try {
            List<DataiIntegrationField> fields = new ArrayList<>();
            for (Field field : objDetail.getFields()) {
                DataiIntegrationField dataiField = new DataiIntegrationField();
                dataiField.setApi(objDetail.getName());
                dataiField.setField(field.getName());
                dataiField.setLabel(field.getLabel());
                dataiField.setType(field.getType().toString());
                dataiField.setLength(field.getLength());
                dataiField.setPrecision(field.getPrecision());
                dataiField.setScale(field.getScale());
                dataiField.setIsCustom(field.isCustom());
                dataiField.setIsCreateable(field.isCreateable());
                dataiField.setIsUpdateable(field.isUpdateable());
                dataiField.setIsQueryable(field.isQueryable());
                dataiField.setIsNillable(field.isNillable());
                dataiField.setIsUnique(field.isUnique());
                dataiField.setIsIdLookup(field.isIdLookup());
                dataiField.setCreateTime(DateUtils.getNowDate());
                dataiField.setUpdateTime(DateUtils.getNowDate());
                fields.add(dataiField);
            }
            
            if (!fields.isEmpty()) {
                dataiIntegrationFieldService.batchInsertDataiIntegrationField(fields);
            }
        } catch (Exception e) {
            log.error("保存对象字段失败: {}", e.getMessage(), e);
        }
    }

    private void createDatabaseTable(String objectApi, String objectLabel, DescribeSObjectResult objDetail, boolean isPartitioned, Map<String, Object> result) {
        try {
            // 检查表是否存在
            if (customMapper.checkTable(objectApi) != null) {
                log.info("表 {} 已存在，跳过创建表", objectApi);
                result.put("success", true);
                result.put("message", "表已存在");
                return;
            }

            // 构建表字段定义
            List<Map<String, Object>> fieldDefinitions = new ArrayList<>();
            List<String> fields = new ArrayList<>();

            // 遍历所有字段
            for (Field field : objDetail.getFields()) {
                Map<String, Object> fieldDef = new HashMap<>();
                fieldDef.put("name", field.getName());
                fieldDef.put("type", ConvertUtil.fieldTypeToMysql(field));
                fieldDef.put("comment", field.getLabel() != null ? field.getLabel().replaceAll("'", "\\'") : "");
                fieldDefinitions.add(fieldDef);
                fields.add(field.getName());

                // 如果字段类型是base64，新增文件路径、是否下载、是否上传三个字段
                if ("base64".equalsIgnoreCase(field.getType().toString())) {
                    Map<String, Object> filePathField = new HashMap<>();
                    filePathField.put("name", "file_path");
                    filePathField.put("type", "text");
                    filePathField.put("comment", (field.getLabel() != null ? field.getLabel() : field.getName()) + "文件路径");
                    fieldDefinitions.add(filePathField);

                    Map<String, Object> isDownloadField = new HashMap<>();
                    isDownloadField.put("name", "is_download");
                    isDownloadField.put("type", "tinyint(1) DEFAULT 0");
                    isDownloadField.put("comment", (field.getLabel() != null ? field.getLabel() : field.getName()) + "是否下载");
                    fieldDefinitions.add(isDownloadField);

                    Map<String, Object> isUploadField = new HashMap<>();
                    isUploadField.put("name", "is_upload");
                    isUploadField.put("type", "tinyint(1) DEFAULT 0");
                    isUploadField.put("comment", (field.getLabel() != null ? field.getLabel() : field.getName()) + "是否上传");
                    fieldDefinitions.add(isUploadField);
                }

                // 处理多态外键字段
                if (field.isPolymorphicForeignKey()) {
                    Map<String, Object> polymorphicField = new HashMap<>();
                    polymorphicField.put("name", field.getName() + "_type");
                    polymorphicField.put("type", "varchar(255)");
                    polymorphicField.put("comment", (field.getLabel() != null ? field.getLabel() : field.getName()) + "关联对象");
                    fieldDefinitions.add(polymorphicField);
                }
            }

            // 添加new_id字段
            Map<String, Object> newIdField = new HashMap<>();
            newIdField.put("name", "new_id");
            newIdField.put("type", "varchar(255)");
            newIdField.put("comment", "新SFID");
            fieldDefinitions.add(newIdField);

            // 添加is_update字段
            Map<String, Object> isInsertField = new HashMap<>();
            isInsertField.put("name", "is_insert");
            isInsertField.put("type", "tinyint(1) DEFAULT 0");
            isInsertField.put("comment", "是否插入");
            fieldDefinitions.add(isInsertField);

            // 添加is_update字段
            Map<String, Object> isUpdateField = new HashMap<>();
            isUpdateField.put("name", "is_update");
            isUpdateField.put("type", "tinyint(1) DEFAULT 0");
            isUpdateField.put("comment", "是否更新");
            fieldDefinitions.add(isUpdateField);

            // 添加fail_reason字段
            Map<String, Object> failReasonField = new HashMap<>();
            failReasonField.put("name", "error_message");
            failReasonField.put("type", "text");
            failReasonField.put("comment", "失败原因");
            fieldDefinitions.add(failReasonField);

            // 构建索引定义 - 只为TABLE_INDEX列表中的字段创建索引
            List<Map<String, Object>> indexDefinitions = new ArrayList<>();
            for (String field : fields) {
                if (SalesforceConfigConstants.TABLE_INDEX.contains(field)) {
                    Map<String, Object> indexMap = new HashMap<>();
                    indexMap.put("field", field);
                    indexMap.put("name", "IDX_" + objectApi + "_" + field);
                    indexDefinitions.add(indexMap);
                }
            }

            // 根据是否分区创建不同的表
            if (isPartitioned) {
                // 创建分区表
                createPartitionTable(objectApi, objectLabel, fieldDefinitions, indexDefinitions, result);
            } else {
                // 创建普通表
                customMapper.createTable(objectApi, objectLabel, fieldDefinitions, indexDefinitions);
                log.info("成功创建表结构: {}", objectApi);
                result.put("success", true);
                result.put("message", "表创建成功");
            }

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "创建表失败: " + e.getMessage());
            log.error("创建表 {} 失败: {}", objectApi, e.getMessage(), e);
        }
    }

    private void createPartitionTable(String objectApi, String objectLabel, List<Map<String, Object>> fieldDefinitions, List<Map<String, Object>> indexDefinitions, Map<String, Object> result) {
        try {
            // 获取批处理字段（日期字段）
            DataiIntegrationObject queryObject = new DataiIntegrationObject();
            queryObject.setApi(objectApi);
            List<DataiIntegrationObject> objects = dataiIntegrationObjectService.selectDataiIntegrationObjectList(queryObject);
            
            String partitionKey = null;
            if (!objects.isEmpty()) {
                DataiIntegrationBatchField batchField = objects.get(0).getBatchField();
                if (batchField != null) {
                    partitionKey = batchField.getFieldName();
                }
            }

            // 从缓存获取系统数据开始时间
            String systemDataStartTimeStr = CacheUtils.get(salesforceConfigCacheManager.getEnvironmentCacheKey(), "system.data.start.time", String.class);
            int startYear = SalesforceConfigConstants.DEFAULT_SYSTEM_DATA_START_YEAR;
            
            if (systemDataStartTimeStr != null && !systemDataStartTimeStr.trim().isEmpty()) {
                try {
                    java.time.LocalDate startDate = java.time.LocalDate.parse(systemDataStartTimeStr.trim());
                    startYear = startDate.getYear();
                    log.info("从缓存获取系统数据开始时间: {}, 年份: {}", systemDataStartTimeStr, startYear);
                } catch (Exception e) {
                    log.warn("解析系统数据开始时间配置失败: {}, 使用默认年份: {}", systemDataStartTimeStr, startYear, e);
                }
            } else {
                log.warn("未找到系统数据开始时间配置，使用默认年份: {}", startYear);
            }

            // 构建分区结构，按年份进行分区
            List<Map<String, Object>> partitions = new ArrayList<>();
            
            // 获取当前年份
            java.util.Calendar calendar = java.util.Calendar.getInstance();
            int currentYear = calendar.get(java.util.Calendar.YEAR);

            // 从系统数据开始年份创建分区到当前年份
            for (int year = startYear; year <= currentYear; year++) {
                Map<String, Object> partition = new HashMap<>();
                partition.put("name", "p" + year);
                partition.put("value", year + 1);
                partitions.add(partition);
            }

            // 添加未来一年的分区
            Map<String, Object> futurePartition = new HashMap<>();
            futurePartition.put("name", "p" + (currentYear + 1));
            futurePartition.put("value", (currentYear + 2));
            partitions.add(futurePartition);

            // 创建RANGE分区表
            customMapper.createRangePartitionTable(
                objectApi,
                objectLabel,
                fieldDefinitions,
                indexDefinitions,
                partitionKey != null ? partitionKey : "CreatedDate",
                partitions
            );

            log.info("成功创建分区表结构: {}, 分区起始年份: {}", objectApi, startYear);
            result.put("success", true);
            result.put("message", "分区表创建成功");

        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "创建分区表失败: " + e.getMessage());
            log.error("创建分区表 {} 失败: {}", objectApi, e.getMessage(), e);
        }
    }

    private void addDatabaseColumn(String objectApi, String fieldApi, Map<String, Object> result) {
        IPartnerV1Connection connection = null;
        try {
            connection = soapConnectionFactory.getConnection();
            DescribeSObjectResult objDetail = connection.describeSObject(objectApi);
            
            if (objDetail == null) {
                result.put("success", false);
                result.put("message", "无法获取对象元数据");
                return;
            }

            for (Field field : objDetail.getFields()) {
                if (fieldApi.equals(field.getName())) {
                    String mysqlType = ConvertUtil.fieldTypeToMysql(field);
                    
                    customMapper.addField(objectApi, fieldApi, mysqlType, field.isNillable());
                    log.info("成功添加字段: {}.{} 类型: {}", objectApi, fieldApi, mysqlType);
                    return;
                }
            }
            
            result.put("success", false);
            result.put("message", "字段不存在于Salesforce对象中");
            
        } catch (ConnectionException e) {
            result.put("success", false);
            result.put("message", "获取Salesforce连接失败: " + e.getMessage());
            log.error("获取Salesforce连接失败: {}", e.getMessage(), e);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "添加字段失败: " + e.getMessage());
            log.error("添加字段失败: {}", e.getMessage(), e);
        }
    }

    private void modifyDatabaseColumn(String objectApi, String fieldApi, Map<String, Object> result) {
        IPartnerV1Connection connection = null;
        try {
            connection = soapConnectionFactory.getConnection();
            DescribeSObjectResult objDetail = connection.describeSObject(objectApi);
            
            if (objDetail == null) {
                result.put("success", false);
                result.put("message", "无法获取对象元数据");
                return;
            }

            for (Field field : objDetail.getFields()) {
                if (fieldApi.equals(field.getName())) {
                    String mysqlType = ConvertUtil.fieldTypeToMysql(field);
                    
                    customMapper.modifyField(objectApi, fieldApi, mysqlType, field.isNillable());
                    log.info("成功修改字段: {}.{} 类型: {}", objectApi, fieldApi, mysqlType);
                    return;
                }
            }
            
            result.put("success", false);
            result.put("message", "字段不存在于Salesforce对象中");
            
        } catch (ConnectionException e) {
            result.put("success", false);
            result.put("message", "获取Salesforce连接失败: " + e.getMessage());
            log.error("获取Salesforce连接失败: {}", e.getMessage(), e);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "修改字段失败: " + e.getMessage());
            log.error("修改字段失败: {}", e.getMessage(), e);
        }
    }

    private void dropDatabaseColumn(String objectApi, String fieldApi, Map<String, Object> result) {
        try {
            customMapper.dropField(objectApi, fieldApi);
            log.info("成功删除字段: {}.{}", objectApi, fieldApi);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除字段失败: " + e.getMessage());
            log.error("删除字段失败: {}", e.getMessage(), e);
        }
    }

    private boolean shouldSyncObject(DescribeGlobalSObjectResult sObject) {
        // 检查对象是否需要同步（满足查询、创建、更新或删除任一条件）
        return sObject.isQueryable() || sObject.isCreateable() || sObject.isUpdateable() || sObject.isDeletable();
    }

    private <T> T retryOperation(Operation<T> operation, int maxRetries, long delayMs) throws Exception {
        int retries = 0;
        Exception lastException = null;

        while (retries < maxRetries) {
            try {
                return operation.execute();
            } catch (Exception e) {
                lastException = e;
                retries++;
                if (retries < maxRetries) {
                    log.warn("操作失败，{}秒后重试 ({} / {})
{}", delayMs / 1000, retries, maxRetries, e.getMessage());
                    Thread.sleep(delayMs);
                    delayMs *= 2; // 指数退避
                }
            }
        }

        throw lastException;
    }

    @FunctionalInterface
    private interface Operation<T> {
        T execute() throws Exception;
    }

    private int isLargeObject(IPartnerV1Connection connection, String objectApi) throws ConnectionException {
        DescribeSObjectResult objDetail = connection.describeSObject(objectApi);
        // 这里简化处理，实际应该根据对象的记录数来判断
        // 暂时返回一个固定值，后续可以从统计信息中获取
        return 1000000; // 假设对象有100万条记录
    }

    private void updateDataiIntegrationObjectFields(String objectApi, int objectNum) {
        DataiIntegrationObject queryObject = new DataiIntegrationObject();
        queryObject.setApi(objectApi);
        List<DataiIntegrationObject> objects = dataiIntegrationObjectService.selectDataiIntegrationObjectList(queryObject);
        
        if (!objects.isEmpty()) {
            DataiIntegrationObject object = objects.get(0);
            object.setObjectNum(objectNum);
            object.setUpdateTime(DateUtils.getNowDate());
            dataiIntegrationObjectService.updateDataiIntegrationObject(object);
        }
    }

    private void createBatchesForNewObject(String objectApi, String objectLabel, IPartnerV1Connection connection) {
        try {
            // 为新对象创建批处理配置
            DataiIntegrationBatch batch = new DataiIntegrationBatch();
            batch.setApi(objectApi);
            batch.setLabel(objectLabel + " 批处理");
            batch.setBatchType("daily");
            batch.setBatchSize(10000);
            batch.setIsActive(true);
            batch.setCreateTime(DateUtils.getNowDate());
            batch.setUpdateTime(DateUtils.getNowDate());
            
            // 查找合适的日期字段作为批处理字段
            DescribeSObjectResult objDetail = connection.describeSObject(objectApi);
            Field[] fields = objDetail.getFields();
            
            for (Field field : fields) {
                if (field.getType() == FieldType.datetime && field.isQueryable()) {
                    DataiIntegrationBatchField batchField = new DataiIntegrationBatchField();
                    batchField.setFieldName(field.getName());
                    batchField.setFieldType("datetime");
                    batchField.setApi(objectApi);
                    batch.setBatchField(batchField);
                    break;
                }
            }
            
            if (batch.getBatchField() == null) {
                // 如果没有找到日期字段，使用CreatedDate作为默认值
                DataiIntegrationBatchField batchField = new DataiIntegrationBatchField();
                batchField.setFieldName("CreatedDate");
                batchField.setFieldType("datetime");
                batchField.setApi(objectApi);
                batch.setBatchField(batchField);
            }
            
            dataiIntegrationBatchService.insertDataiIntegrationBatch(batch);
            log.info("为对象 {} 创建批处理配置成功", objectApi);
            
        } catch (Exception e) {
            log.error("为对象 {} 创建批处理配置失败: {}", objectApi, e.getMessage(), e);
        }
    }
}