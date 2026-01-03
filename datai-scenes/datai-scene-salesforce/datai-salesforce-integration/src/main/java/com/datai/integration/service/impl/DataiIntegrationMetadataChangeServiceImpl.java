package com.datai.integration.service.impl;

import java.time.LocalDateTime;
import java.util.*;

import com.datai.common.utils.DateUtils;
import com.datai.common.utils.SecurityUtils;
import com.datai.integration.factory.impl.SOAPConnectionFactory;
import com.datai.integration.mapper.CustomMapper;
import com.sforce.soap.partner.*;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.datai.integration.mapper.DataiIntegrationMetadataChangeMapper;
import com.datai.integration.model.domain.DataiIntegrationMetadataChange;
import com.datai.integration.model.domain.DataiIntegrationObject;
import com.datai.integration.model.domain.DataiIntegrationField;
import com.datai.integration.service.IDataiIntegrationMetadataChangeService;
import com.datai.integration.service.IDataiIntegrationObjectService;
import com.datai.integration.service.IDataiIntegrationFieldService;
import com.datai.integration.service.IDataiIntegrationPicklistService;
import com.datai.integration.service.IDataiIntegrationFilterLookupService;
import com.datai.common.core.domain.model.LoginUser;

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
    private SOAPConnectionFactory soapConnectionFactory;

    @Autowired
    private CustomMapper customMapper;

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
        Map<String, Object> statistics = new HashMap<>();

        try {
            Map<String, Object> result = dataiIntegrationMetadataChangeMapper.selectChangeStatistics(params);
            
            int totalCount = result.get("totalCount") != null ? ((Number) result.get("totalCount")).intValue() : 0;
            int syncedCount = result.get("syncedCount") != null ? ((Number) result.get("syncedCount")).intValue() : 0;
            int unsyncedCount = result.get("unsyncedCount") != null ? ((Number) result.get("unsyncedCount")).intValue() : 0;
            
            double syncRate = totalCount > 0 ? (double) syncedCount / totalCount * 100 : 0;
            
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
            
        } catch (Exception e) {
            statistics.put("success", false);
            statistics.put("message", "获取变更统计信息失败: " + e.getMessage());
        }
        
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
        List<Map<String, Object>> syncResults = new ArrayList<>();
        int successCount = 0;
        int failCount = 0;

        for (Long id : ids) {
            Map<String, Object> syncResult = syncToLocalDatabase(id);
            
            Map<String, Object> syncResultItem = new HashMap<>();
            syncResultItem.put("id", id);
            syncResultItem.put("success", syncResult.get("success"));
            syncResultItem.put("message", syncResult.get("message"));
            syncResults.add(syncResultItem);

            if ((Boolean) syncResult.get("success")) {
                successCount++;
            } else {
                failCount++;
            }
        }

        Map<String, Object> data = new HashMap<>();
        data.put("totalCount", ids.length);
        data.put("successCount", successCount);
        data.put("failCount", failCount);
        data.put("details", syncResults);
        
        result.put("success", failCount == 0);
        result.put("message", String.format("批量同步完成: 成功 %d 条, 失败 %d 条", successCount, failCount));
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
                    
                    PartnerConnection connection = retryOperation(() -> soapConnectionFactory.getConnection(), 3, 1000);
                    log.info("成功获取源ORG连接");
                    
                    DescribeSObjectResult objDetail = connection.describeSObject(objectApi.trim());
                    
                    DataiIntegrationObject object = buildObjectMetadata(objDetail);
                    
                    if (object == null) {
                        log.error("构建对象 {} 的元数据失败", objectApi);
                        result.put("success", false);
                        result.put("message", "构建对象元数据失败");
                        return;
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
                            createOrUpdateDatabaseTable(objectApi, metadataChange.getObjectLabel(), result);
                        } else {
                            log.info("对象 {} 是普通对象，数据量少于五百万，创建正常表", objectApi);
                            createOrUpdateDatabaseTable(objectApi, metadataChange.getObjectLabel(), result);
                        }
                        
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

    private void createOrUpdateDatabaseTable(String objectApi, String objectLabel, Map<String, Object> result) {
        PartnerConnection connection = null;
        try {
            connection = soapConnectionFactory.getConnection();
            DescribeSObjectResult objDetail = connection.describeSObject(objectApi);
            
            if (objDetail == null) {
                result.put("success", false);
                result.put("message", "无法获取对象元数据");
                return;
            }

            List<Map<String, Object>> fieldMaps = new ArrayList<>();
            List<Map<String, Object>> indexMaps = new ArrayList<>();
            
            for (com.sforce.soap.partner.Field field : objDetail.getFields()) {
                Map<String, Object> fieldMap = new HashMap<>();
                fieldMap.put("fieldName", field.getName());
                fieldMap.put("fieldType", convertSalesforceTypeToMySQL(field.getType() != null ? field.getType().toString() : null));
                fieldMap.put("fieldLength", field.getLength());
                fieldMap.put("fieldPrecision", field.getPrecision());
                fieldMap.put("fieldScale", field.getScale());
                fieldMap.put("isNullable", field.isNillable());
                fieldMap.put("isUnique", field.isUnique());
                fieldMap.put("isPrimaryKey", field.isIdLookup());
                fieldMaps.add(fieldMap);
                
                if (field.isIdLookup()) {
                    Map<String, Object> indexMap = new HashMap<>();
                    indexMap.put("indexName", "idx_" + field.getName());
                    indexMap.put("indexField", field.getName());
                    indexMap.put("indexType", "PRIMARY");
                    indexMaps.add(indexMap);
                }
            }
            
            customMapper.createTable(objectApi, objectLabel, fieldMaps, indexMaps);
            log.info("成功创建或更新表: {}", objectApi);
            
        } catch (ConnectionException e) {
            result.put("success", false);
            result.put("message", "获取Salesforce连接失败: " + e.getMessage());
            log.error("获取Salesforce连接失败: {}", e.getMessage(), e);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "创建或更新表失败: " + e.getMessage());
            log.error("创建或更新表失败: {}", e.getMessage(), e);
        }
    }

//    private void updateDatabaseTable(String objectApi, String objectLabel, Map<String, Object> result) {
//        PartnerConnection connection = null;
//        try {
//            connection = soapConnectionFactory.getConnection();
//            DescribeSObjectResult objDetail = connection.describeSObject(objectApi);
//
//            if (objDetail == null) {
//                result.put("success", false);
//                result.put("message", "无法获取对象元数据");
//                return;
//            }
//
//            List<Map<String, Object>> fieldMaps = new ArrayList<>();
//
//            for (com.sforce.soap.partner.Field field : objDetail.getFields()) {
//                Map<String, Object> fieldMap = new HashMap<>();
//                fieldMap.put("fieldName", field.getName());
//                fieldMap.put("fieldType", convertSalesforceTypeToMySQL(field.getType() != null ? field.getType().toString() : null));
//                fieldMap.put("fieldLength", field.getLength());
//                fieldMap.put("fieldPrecision", field.getPrecision());
//                fieldMap.put("fieldScale", field.getScale());
//                fieldMap.put("isNullable", field.isNillable());
//                fieldMap.put("isUnique", field.isUnique());
//                fieldMap.put("isPrimaryKey", field.isIdLookup());
//                fieldMaps.add(fieldMap);
//            }
//
//            customMapper.createTable(objectApi, objectLabel, fieldMaps, new ArrayList<>());
//            log.info("成功更新表结构: {}", objectApi);
//
//        } catch (ConnectionException e) {
//            result.put("success", false);
//            result.put("message", "获取Salesforce连接失败: " + e.getMessage());
//            log.error("获取Salesforce连接失败: {}", e.getMessage(), e);
//        } catch (Exception e) {
//            result.put("success", false);
//            result.put("message", "更新表结构失败: " + e.getMessage());
//            log.error("更新表结构失败: {}", e.getMessage(), e);
//        }
//    }

    private void dropDatabaseTable(String objectApi, Map<String, Object> result) {
        try {
            String dropTableSql = "DROP TABLE IF EXISTS " + objectApi;
            customMapper.executeUpdate(dropTableSql);
            log.info("成功删除表: {}", objectApi);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "删除表失败: " + e.getMessage());
            log.error("删除表失败: {}", e.getMessage(), e);
        }
    }

    private void addDatabaseColumn(String objectApi, String fieldApi, Map<String, Object> result) {
        PartnerConnection connection = null;
        try {
            connection = soapConnectionFactory.getConnection();
            DescribeSObjectResult objDetail = connection.describeSObject(objectApi);
            
            if (objDetail == null) {
                result.put("success", false);
                result.put("message", "无法获取对象元数据");
                return;
            }

            for (com.sforce.soap.partner.Field field : objDetail.getFields()) {
                if (fieldApi.equals(field.getName())) {
                    String mysqlType = convertSalesforceTypeToMySQL(field.getType() != null ? field.getType().toString() : null);
                    
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
        PartnerConnection connection = null;
        try {
            connection = soapConnectionFactory.getConnection();
            DescribeSObjectResult objDetail = connection.describeSObject(objectApi);
            
            if (objDetail == null) {
                result.put("success", false);
                result.put("message", "无法获取对象元数据");
                return;
            }

            for (com.sforce.soap.partner.Field field : objDetail.getFields()) {
                if (fieldApi.equals(field.getName())) {
                    String mysqlType = convertSalesforceTypeToMySQL(field.getType() != null ? field.getType().toString() : null);
                    
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

    private String convertSalesforceTypeToMySQL(String typeStr) {
        if (typeStr == null || typeStr.isEmpty()) {
            return "VARCHAR(255)";
        }
        
        switch (typeStr) {
            case "id":
                return "VARCHAR(18)";
            case "string":
            case "email":
            case "url":
            case "phone":
                return "VARCHAR(255)";
            case "textarea":
                return "TEXT";
            case "boolean":
                return "TINYINT(1)";
            case "int":
                return "INT";
            case "double":
                return "DOUBLE";
            case "currency":
                return "DECIMAL(18,4)";
            case "date":
                return "DATE";
            case "datetime":
                return "DATETIME";
            case "time":
                return "TIME";
            case "percent":
                return "DECIMAL(10,2)";
            case "reference":
                return "VARCHAR(18)";
            case "picklist":
            case "multipicklist":
                return "VARCHAR(255)";
            case "combobox":
                return "VARCHAR(255)";
            case "base64":
                return "LONGBLOB";
            case "anyType":
                return "VARCHAR(255)";
            case "address":
                return "TEXT";
            case "location":
                return "VARCHAR(255)";
            case "encryptedstring":
                return "VARCHAR(255)";
            default:
                return "VARCHAR(255)";
        }
    }

    @Override
    public Map<String, Object> pullAllMetadataChanges() {
        // 初始化返回结果
        Map<String, Object> result = new HashMap<>();
        
        try {
            log.info("开始执行全对象元数据变更拉取");
            
            // 建立与Salesforce的连接，使用重试机制确保连接成功
            PartnerConnection connection = retryOperation(() -> soapConnectionFactory.getConnection(), 3, 1000);
            log.info("成功获取Salesforce SOAP连接");

            // 获取Salesforce中所有对象的全局描述信息
            DescribeGlobalResult globalDescribe = connection.describeGlobal();
            DescribeGlobalSObjectResult[] sObjects = globalDescribe.getSobjects();

            // 检查是否获取到任何对象
            if (sObjects == null || sObjects.length == 0) {
                log.warn("未获取到任何Salesforce对象");
                result.put("success", true);
                result.put("message", "未获取到任何Salesforce对象");
                result.put("objectChangeCount", 0);
                result.put("fieldChangeCount", 0);
                return result;
            }

            log.info("从Salesforce获取到 {} 个对象", sObjects.length);

            // 用于存储已同步的对象API名称，以便后续检测删除的对象
            Set<String> syncedObjectApis = new HashSet<>();
            int objectChangeCount = 0; // 记录对象变更数量
            int fieldChangeCount = 0;  // 记录字段变更数量

            // 遍历所有Salesforce对象
            for (DescribeGlobalSObjectResult sObject : sObjects) {
                try {
                    String objectApi = sObject.getName();
                    
                    // 检查对象是否需要同步（满足查询、创建、更新或删除任一条件）
                    if (shouldSyncObject(sObject)) {
                        // 获取对象的详细描述信息
                        DescribeSObjectResult objDetail = connection.describeSObject(objectApi);
                        
                        // 查询数据库中是否已存在该对象
                        DataiIntegrationObject queryObject = new DataiIntegrationObject();
                        queryObject.setApi(objectApi);
                        List<DataiIntegrationObject> existingObjects = dataiIntegrationObjectService.selectDataiIntegrationObjectList(queryObject);
                        
                        // 构建当前对象的元数据信息
                        DataiIntegrationObject newObject = buildObjectMetadata(objDetail);
                        boolean hasFieldChange = false; // 标记该对象是否有字段变更
                        
                        // 判断对象是否为新增
                        if (existingObjects.isEmpty()) {
                            // 新增对象到数据库
                            dataiIntegrationObjectService.insertDataiIntegrationObject(newObject);
                            // 记录对象新增变更
                            recordObjectChange(newObject, null, "INSERT");
                            objectChangeCount++;
                            log.info("新增对象并记录变更: {}", objectApi);
                            // 重新查询以获取新插入对象的ID
                            existingObjects = dataiIntegrationObjectService.selectDataiIntegrationObjectList(queryObject);
                            if (!existingObjects.isEmpty()) {
                                newObject.setId(existingObjects.get(0).getId());
                            }
                            // 将对象API添加到已同步集合中
                            syncedObjectApis.add(objectApi);
                            // 对象新增后直接跳过字段比较
                            continue;
                        }
                        
                        // 对象已存在，比较新旧对象的差异
                        DataiIntegrationObject existingObject = existingObjects.get(0);
                        newObject.setId(existingObject.getId());
                        List<String> changedFields = compareObjects(existingObject, newObject);
                        if (!changedFields.isEmpty()) {
                            // 记录对象更新变更
                            recordObjectChange(newObject, existingObject, "UPDATE");
                            objectChangeCount++;
                            log.debug("记录对象更新: {} - 变更: {}", objectApi, String.join(", ", changedFields));
                        }
                        
                        // 将对象API添加到已同步集合中
                        syncedObjectApis.add(objectApi);
                        
                        // 查询数据库中该对象的现有字段
                        DataiIntegrationField queryField = new DataiIntegrationField();
                        queryField.setApi(objectApi);
                        List<DataiIntegrationField> existingFields = dataiIntegrationFieldService.selectDataiIntegrationFieldList(queryField);
                        
                        // 将现有字段转换为Map，便于快速查找
                        Map<String, DataiIntegrationField> existingFieldMap = new HashMap<>();
                        for (DataiIntegrationField existingField : existingFields) {
                            existingFieldMap.put(existingField.getField(), existingField);
                        }
                        
                        // 遍历Salesforce中的所有字段
                        for (Field field : objDetail.getFields()) {
                            // 构建当前字段的元数据信息
                            DataiIntegrationField newField = buildFieldMetadata(objectApi, field);
                            // 检查字段是否已存在
                            DataiIntegrationField existingField = existingFieldMap.get(field.getName());
                            
                            if (existingField == null) {
                                // 字段为新增
                                recordFieldChange(objectApi, newObject.getLabel(), newField.getField(), 
                                                newField.getLabel(), null, "INSERT", "新增字段", newObject.getIsCustom());
                                fieldChangeCount++;
                                hasFieldChange = true;
                                log.debug("记录字段新增: {}.{}", objectApi, field.getName());
                            } else {
                                // 字段已存在，比较新旧字段的差异
                                List<String> changedFieldProps = compareFields(existingField, newField);
                                if (!changedFieldProps.isEmpty()) {
                                    // 记录字段更新变更
                                    recordFieldChange(objectApi, newObject.getLabel(), newField.getField(), 
                                                    newField.getLabel(), "字段属性变更: " + String.join(", ", changedFieldProps), 
                                                    "UPDATE", "字段属性更新", newObject.getIsCustom());
                                    fieldChangeCount++;
                                    hasFieldChange = true;
                                    log.debug("记录字段更新: {}.{} - 变更: {}", objectApi, field.getName(), 
                                             String.join(", ", changedFieldProps));
                                }
                            }
                        }
                        
                        // 检查是否有已删除的字段（在数据库中存在但在Salesforce中不存在）
                        for (DataiIntegrationField existingField : existingFields) {
                            boolean fieldExists = false;
                            for (Field field : objDetail.getFields()) {
                                if (existingField.getField().equals(field.getName())) {
                                    fieldExists = true;
                                    break;
                                }
                            }
                            if (!fieldExists) {
                                // 记录字段删除变更
                                recordFieldChange(objectApi, newObject.getLabel(), existingField.getField(), 
                                                existingField.getLabel(), "字段已从Salesforce中删除", 
                                                "DELETE", "字段删除", newObject.getIsCustom());
                                fieldChangeCount++;
                                hasFieldChange = true;
                                log.warn("记录字段删除: {}.{}", objectApi, existingField.getField());
                            }
                        }
                        
                        // 如果该对象有字段变更，禁用其增量更新状态
                        if (hasFieldChange && newObject.getId() != null) {
                            DataiIntegrationObject updateObject = new DataiIntegrationObject();
                            updateObject.setId(newObject.getId());
                            updateObject.setIsIncremental(false); // 禁用增量更新
                            dataiIntegrationObjectService.updateDataiIntegrationObject(updateObject);
                            log.info("检测到字段变更，已禁用对象 {} 的增量更新状态", objectApi);
                        }
                    }
                } catch (Exception e) {
                    // 记录处理单个对象时的错误，但继续处理其他对象
                    log.error("处理对象 {} 时出错: {}", sObject.getName(), e.getMessage(), e);
                }
            }
            
            // 检查并记录已从Salesforce中删除的对象
            checkDeletedObjectsForMetadata(syncedObjectApis);

            log.info("全对象元数据变更拉取完成，对象变更: {} 个，字段变更: {} 个", objectChangeCount, fieldChangeCount);
            
            // 设置返回结果
            result.put("success", true);
            result.put("objectChangeCount", objectChangeCount);
            result.put("fieldChangeCount", fieldChangeCount);
            result.put("message", String.format("全对象元数据变更拉取完成，对象变更: %d 个，字段变更: %d 个", objectChangeCount, fieldChangeCount));
            
            return result;
        } catch (Exception e) {
            // 记录整个拉取过程中的异常
            log.error("全对象元数据变更拉取时发生异常", e);
            result.put("success", false);
            result.put("message", "全对象元数据变更拉取失败: " + e.getMessage());
            return result;
        }
    }

    /**
     * 判断对象是否需要同步
     * 只有满足以下任一条件的对象才需要同步：
     * - 可查询 (isQueryable)
     * - 可创建 (isCreateable)
     * - 可更新 (isUpdateable)
     * - 可删除 (isDeletable)
     * 
     * @param sObject Salesforce对象描述信息
     * @return 如果对象需要同步返回true，否则返回false
     */
    private boolean shouldSyncObject(DescribeGlobalSObjectResult sObject) {
        return sObject.isQueryable() || sObject.isCreateable() || sObject.isUpdateable() || sObject.isDeletable();
    }

    /**
     * 根据Salesforce对象描述信息构建对象元数据实体
     * 将Salesforce的DescribeSObjectResult转换为DataiIntegrationObject实体
     * 
     * @param objDetail Salesforce对象的详细描述信息
     * @return 构建的DataiIntegrationObject实体，如果构建失败返回null
     */
    /**
     * 根据Salesforce字段描述信息构建字段元数据实体
     * 将Salesforce的Field对象转换为DataiIntegrationField实体
     * 
     * @param objectApi 所属对象的API名称
     * @param field Salesforce字段描述信息
     * @return 构建的DataiIntegrationField实体
     */
    private DataiIntegrationField buildFieldMetadata(String objectApi, Field field) {
        // 创建字段元数据实体
        DataiIntegrationField fieldEntity = new DataiIntegrationField();
        // 设置所属对象API名称
        fieldEntity.setApi(objectApi);
        // 设置字段名称
        fieldEntity.setField(field.getName());
        // 设置字段标签
        fieldEntity.setLabel(field.getLabel());
        // 设置是否可创建
        fieldEntity.setIsCreateable(field.isCreateable());
        // 设置是否可为空
        fieldEntity.setIsNillable(field.isNillable());
        // 设置是否可更新
        fieldEntity.setIsUpdateable(field.isUpdateable());
        // 设置是否在创建时有默认值
        fieldEntity.setIsDefaultedOnCreate(field.isDefaultedOnCreate());
        // 设置是否唯一
        fieldEntity.setIsUnique(field.isUnique());
        // 设置是否可过滤
        fieldEntity.setIsFilterable(field.isFilterable());
        // 设置是否可排序
        fieldEntity.setIsSortable(field.isSortable());
        // 设置是否可聚合
        fieldEntity.setIsAggregatable(field.isAggregatable());
        // 设置是否可分组
        fieldEntity.setIsGroupable(field.isGroupable());
        // 设置是否为多态外键
        fieldEntity.setIsPolymorphicForeignKey(field.isPolymorphicForeignKey());
        // 设置多态外键类型字段
        fieldEntity.setPolymorphicForeignField(field.getName() + "_type");
        // 设置是否为外部ID
        fieldEntity.setIsExternalId(field.isExternalId());
        // 设置是否为自定义字段
        fieldEntity.setIsCustom(field.isCustom());
        // 设置是否为计算字段
        fieldEntity.setIsCalculated(field.isCalculated());
        // 设置是否为自动编号字段
        fieldEntity.setIsAutoNumber(field.isAutoNumber());
        // 设置是否区分大小写
        fieldEntity.setIsCaseSensitive(field.isCaseSensitive());
        // 设置是否为加密字段
        fieldEntity.setIsEncrypted(field.isEncrypted());
        // 设置是否为HTML格式化字段
        fieldEntity.setIsHtmlFormatted(field.isHtmlFormatted());
        // 设置是否为ID查找字段
        fieldEntity.setIsIdLookup(field.isIdLookup());
        // 设置是否为权限字段
        fieldEntity.setIsPermissionable(field.isPermissionable());
        // 设置是否为受限选择列表
        fieldEntity.setIsRestrictedPicklist(field.isRestrictedPicklist());
        // 设置是否为受限删除
        fieldEntity.setIsRestrictedDelete(field.isRestrictedDelete());
        // 设置写入是否需要主记录读取权限
        fieldEntity.setIsWriteRequiresMasterRead(field.isWriteRequiresMasterRead());
        // 设置字段数据类型
        fieldEntity.setFieldDataType(field.getType() != null ? field.getType().toString() : null);
        // 设置字段长度
        fieldEntity.setFieldLength(field.getLength());
        // 设置字段精度
        fieldEntity.setFieldPrecision(field.getPrecision());
        // 设置字段小数位数
        fieldEntity.setFieldScale(field.getScale());
        // 设置字段字节长度
        fieldEntity.setFieldByteLength(field.getByteLength());
        // 设置默认值公式
        fieldEntity.setDefaultValue(field.getDefaultValueFormula());
        // 设置计算公式
        fieldEntity.setCalculatedFormula(field.getCalculatedFormula());
        // 设置内联帮助文本
        fieldEntity.setInlineHelpText(field.getInlineHelpText());
        // 设置关系名称
        fieldEntity.setRelationshipName(field.getRelationshipName());
        // 设置关系顺序
        fieldEntity.setRelationshipOrder(field.getRelationshipOrder());
        // 设置引用目标字段
        fieldEntity.setReferenceTargetField(field.getReferenceTo() != null && field.getReferenceTo().length > 0 ? field.getReferenceTo()[0] : null);
        
        // 如果字段有引用目标，则设置引用目标
        if (field.getReferenceTo() != null && field.getReferenceTo().length > 0) {
            // 设置引用目标（多个引用目标用逗号分隔）
            fieldEntity.setReferenceTo(String.join(",", field.getReferenceTo()));
            // 设置第一个引用目标字段
            fieldEntity.setReferenceTargetField(field.getReferenceTo()[0]);
        }
        
        return fieldEntity;
    }

    /**
     * 比较两个对象元数据的差异
     * 检查新旧对象元数据之间的不同属性，并返回有变化的属性列表
     * 
     * @param oldObject 旧的对象元数据
     * @param newObject 新的对象元数据
     * @return 有变化的属性名称列表
     */
    private List<String> compareObjects(DataiIntegrationObject oldObject, DataiIntegrationObject newObject) {
        // 初始化变更字段列表
        List<String> changedFields = new ArrayList<>();
        
        // 比较标签名称
        if (!Objects.equals(oldObject.getLabel(), newObject.getLabel())) {
            changedFields.add("label");
        }
        // 比较复数标签名称
        if (!Objects.equals(oldObject.getLabelPlural(), newObject.getLabelPlural())) {
            changedFields.add("labelPlural");
        }
        // 比较键前缀
        if (!Objects.equals(oldObject.getKeyPrefix(), newObject.getKeyPrefix())) {
            changedFields.add("keyPrefix");
        }
        // 比较是否可查询状态
        if (!Objects.equals(oldObject.getIsQueryable(), newObject.getIsQueryable())) {
            changedFields.add("isQueryable");
        }
        // 比较是否可创建状态
        if (!Objects.equals(oldObject.getIsCreateable(), newObject.getIsCreateable())) {
            changedFields.add("isCreateable");
        }
        // 比较是否可更新状态
        if (!Objects.equals(oldObject.getIsUpdateable(), newObject.getIsUpdateable())) {
            changedFields.add("isUpdateable");
        }
        // 比较是否可删除状态
        if (!Objects.equals(oldObject.getIsDeletable(), newObject.getIsDeletable())) {
            changedFields.add("isDeletable");
        }
        // 比较是否可复制状态
        if (!Objects.equals(oldObject.getIsReplicateable(), newObject.getIsReplicateable())) {
            changedFields.add("isReplicateable");
        }
        // 比较是否可检索状态
        if (!Objects.equals(oldObject.getIsRetrieveable(), newObject.getIsRetrieveable())) {
            changedFields.add("isRetrieveable");
        }
        // 比较是否可搜索状态
        if (!Objects.equals(oldObject.getIsSearchable(), newObject.getIsSearchable())) {
            changedFields.add("isSearchable");
        }
        
        return changedFields;
    }

    /**
     * 比较两个字段元数据的差异
     * 检查新旧字段元数据之间的不同属性，并返回有变化的属性列表
     * 
     * @param oldField 旧的字段元数据
     * @param newField 新的字段元数据
     * @return 有变化的属性名称列表
     */
    private List<String> compareFields(DataiIntegrationField oldField, DataiIntegrationField newField) {
        // 初始化变更字段列表
        List<String> changedFields = new ArrayList<>();
        
        // 比较标签名称
        if (!Objects.equals(oldField.getLabel(), newField.getLabel())) {
            changedFields.add("label");
        }
        // 比较是否可创建状态
        if (!Objects.equals(oldField.getIsCreateable(), newField.getIsCreateable())) {
            changedFields.add("isCreateable");
        }
        // 比较是否可为空状态
        if (!Objects.equals(oldField.getIsNillable(), newField.getIsNillable())) {
            changedFields.add("isNillable");
        }
        // 比较是否可更新状态
        if (!Objects.equals(oldField.getIsUpdateable(), newField.getIsUpdateable())) {
            changedFields.add("isUpdateable");
        }
        // 比较是否在创建时有默认值状态
        if (!Objects.equals(oldField.getIsDefaultedOnCreate(), newField.getIsDefaultedOnCreate())) {
            changedFields.add("isDefaultedOnCreate");
        }
        // 比较是否唯一状态
        if (!Objects.equals(oldField.getIsUnique(), newField.getIsUnique())) {
            changedFields.add("isUnique");
        }
        // 比较是否可过滤状态
        if (!Objects.equals(oldField.getIsFilterable(), newField.getIsFilterable())) {
            changedFields.add("isFilterable");
        }
        // 比较是否可排序状态
        if (!Objects.equals(oldField.getIsSortable(), newField.getIsSortable())) {
            changedFields.add("isSortable");
        }
        // 比较是否可聚合状态
        if (!Objects.equals(oldField.getIsAggregatable(), newField.getIsAggregatable())) {
            changedFields.add("isAggregatable");
        }
        // 比较是否可分组状态
        if (!Objects.equals(oldField.getIsGroupable(), newField.getIsGroupable())) {
            changedFields.add("isGroupable");
        }
        // 比较是否为多态外键状态
        if (!Objects.equals(oldField.getIsPolymorphicForeignKey(), newField.getIsPolymorphicForeignKey())) {
            changedFields.add("isPolymorphicForeignKey");
        }
        // 比较是否为外部ID状态
        if (!Objects.equals(oldField.getIsExternalId(), newField.getIsExternalId())) {
            changedFields.add("isExternalId");
        }
        // 比较是否为自定义字段状态
        if (!Objects.equals(oldField.getIsCustom(), newField.getIsCustom())) {
            changedFields.add("isCustom");
        }
        // 比较是否为计算字段状态
        if (!Objects.equals(oldField.getIsCalculated(), newField.getIsCalculated())) {
            changedFields.add("isCalculated");
        }
        // 比较是否为自动编号字段状态
        if (!Objects.equals(oldField.getIsAutoNumber(), newField.getIsAutoNumber())) {
            changedFields.add("isAutoNumber");
        }
        // 比较是否区分大小写状态
        if (!Objects.equals(oldField.getIsCaseSensitive(), newField.getIsCaseSensitive())) {
            changedFields.add("isCaseSensitive");
        }
        // 比较是否为加密字段状态
        if (!Objects.equals(oldField.getIsEncrypted(), newField.getIsEncrypted())) {
            changedFields.add("isEncrypted");
        }
        // 比较是否为HTML格式化字段状态
        if (!Objects.equals(oldField.getIsHtmlFormatted(), newField.getIsHtmlFormatted())) {
            changedFields.add("isHtmlFormatted");
        }
        // 比较是否为ID查找字段状态
        if (!Objects.equals(oldField.getIsIdLookup(), newField.getIsIdLookup())) {
            changedFields.add("isIdLookup");
        }
        // 比较是否为权限字段状态
        if (!Objects.equals(oldField.getIsPermissionable(), newField.getIsPermissionable())) {
            changedFields.add("isPermissionable");
        }
        // 比较是否为受限选择列表状态
        if (!Objects.equals(oldField.getIsRestrictedPicklist(), newField.getIsRestrictedPicklist())) {
            changedFields.add("isRestrictedPicklist");
        }
        // 比较是否为受限删除状态
        if (!Objects.equals(oldField.getIsRestrictedDelete(), newField.getIsRestrictedDelete())) {
            changedFields.add("isRestrictedDelete");
        }
        // 比较写入是否需要主记录读取权限状态
        if (!Objects.equals(oldField.getIsWriteRequiresMasterRead(), newField.getIsWriteRequiresMasterRead())) {
            changedFields.add("isWriteRequiresMasterRead");
        }
        // 比较字段数据类型
        if (!Objects.equals(oldField.getFieldDataType(), newField.getFieldDataType())) {
            changedFields.add("fieldDataType");
        }
        // 比较字段长度
        if (!Objects.equals(oldField.getFieldLength(), newField.getFieldLength())) {
            changedFields.add("fieldLength");
        }
        // 比较字段精度
        if (!Objects.equals(oldField.getFieldPrecision(), newField.getFieldPrecision())) {
            changedFields.add("fieldPrecision");
        }
        // 比较字段小数位数
        if (!Objects.equals(oldField.getFieldScale(), newField.getFieldScale())) {
            changedFields.add("fieldScale");
        }
        // 比较字段字节长度
        if (!Objects.equals(oldField.getFieldByteLength(), newField.getFieldByteLength())) {
            changedFields.add("fieldByteLength");
        }
        // 比较默认值公式
        if (!Objects.equals(oldField.getDefaultValue(), newField.getDefaultValue())) {
            changedFields.add("defaultValue");
        }
        // 比较计算公式
        if (!Objects.equals(oldField.getCalculatedFormula(), newField.getCalculatedFormula())) {
            changedFields.add("calculatedFormula");
        }
        // 比较内联帮助文本
        if (!Objects.equals(oldField.getInlineHelpText(), newField.getInlineHelpText())) {
            changedFields.add("inlineHelpText");
        }
        // 比较关系名称
        if (!Objects.equals(oldField.getRelationshipName(), newField.getRelationshipName())) {
            changedFields.add("relationshipName");
        }
        // 比较引用目标
        if (!Objects.equals(oldField.getReferenceTo(), newField.getReferenceTo())) {
            changedFields.add("referenceTo");
        }
        
        return changedFields;
    }

    /**
     * 记录对象元数据变更
     * 将对象级别的变更（新增、修改、删除）记录到元数据变更表中
     * 
     * @param newObject 新的对象元数据
     * @param oldObject 旧的对象元数据（新增操作时为null）
     * @param operationType 操作类型（INSERT、UPDATE、DELETE）
     */
    private void recordObjectChange(DataiIntegrationObject newObject, DataiIntegrationObject oldObject, String operationType) {
        try {
            String changeReason;
            if (oldObject != null) {
                List<String> changedFields = compareObjects(oldObject, newObject);
                if (!changedFields.isEmpty()) {
                    changeReason = "对象属性变更: " + String.join(", ", changedFields);
                } else {
                    changeReason = "对象属性更新";
                }
            } else {
                changeReason = "新增对象";
            }

            int similarCount = dataiIntegrationMetadataChangeMapper.countSimilarChanges(
                "OBJECT", operationType, newObject.getApi(), null, changeReason);
                
            if (similarCount > 0) {
                log.debug("发现相似的未同步对象变更记录，跳过重复记录: {} - {}", newObject.getApi(), operationType);
                return;
            }

            DataiIntegrationMetadataChange metadataChange = new DataiIntegrationMetadataChange();
            metadataChange.setChangeType("OBJECT");
            metadataChange.setOperationType(operationType);
            metadataChange.setObjectApi(newObject.getApi());
            metadataChange.setObjectLabel(newObject.getLabel());
            metadataChange.setChangeTime(LocalDateTime.now());
            metadataChange.setSyncStatus(false);
            metadataChange.setIsCustom(newObject.getIsCustom());
            metadataChange.setChangeReason(changeReason);
            metadataChange.setChangeUser("SYSTEM");

            insertDataiIntegrationMetadataChange(metadataChange);
            log.debug("记录对象变更成功: {} - {}", newObject.getApi(), operationType);
        } catch (Exception e) {
            log.error("记录对象变更失败: {} - {}", newObject.getApi(), e.getMessage(), e);
        }
    }

    /**
     * 记录字段元数据变更
     * 将字段级别的变更（新增、修改、删除）记录到元数据变更表中
     * 
     * @param objectApi 所属对象的API名称
     * @param objectLabel 所属对象的标签名称
     * @param fieldApi 字段API名称
     * @param fieldLabel 字段标签名称
     * @param changeReason 变更原因（可为null，使用defaultReason）
     * @param operationType 操作类型（INSERT、UPDATE、DELETE）
     * @param defaultReason 默认变更原因
     * @param isCustom 是否为自定义对象
     */
    private void recordFieldChange(String objectApi, String objectLabel, String fieldApi, String fieldLabel, 
                                   String changeReason, String operationType, String defaultReason, Boolean isCustom) {
        try {
            String finalChangeReason = changeReason != null ? changeReason : defaultReason;

            int similarCount = dataiIntegrationMetadataChangeMapper.countSimilarChanges(
                "FIELD", operationType, objectApi, fieldApi, finalChangeReason);
                
            if (similarCount > 0) {
                log.debug("发现相似的未同步字段变更记录，跳过重复记录: {}.{} - {}", objectApi, fieldApi, operationType);
                return;
            }

            DataiIntegrationMetadataChange metadataChange = new DataiIntegrationMetadataChange();
            metadataChange.setChangeType("FIELD");
            metadataChange.setOperationType(operationType);
            metadataChange.setObjectApi(objectApi);
            metadataChange.setObjectLabel(objectLabel);
            metadataChange.setFieldApi(fieldApi);
            metadataChange.setFieldLabel(fieldLabel);
            metadataChange.setChangeTime(LocalDateTime.now());
            metadataChange.setSyncStatus(false);
            metadataChange.setIsCustom(isCustom);
            metadataChange.setChangeReason(finalChangeReason);
            metadataChange.setChangeUser("SYSTEM");

            insertDataiIntegrationMetadataChange(metadataChange);
            log.debug("记录字段变更成功: {}.{} - {}", objectApi, fieldApi, operationType);
        } catch (Exception e) {
            log.error("记录字段变更失败: {}.{} - {}", objectApi, fieldApi, e.getMessage(), e);
        }
    }

    /**
     * 检查并记录已从Salesforce中删除的对象
     * 通过比较数据库中存储的对象与从Salesforce获取的对象列表，
     * 识别出已从Salesforce中删除的对象并记录相应的删除变更
     * 
     * @param syncedObjectApis 从Salesforce获取到的已同步对象API名称集合
     */
    private void checkDeletedObjectsForMetadata(Set<String> syncedObjectApis) {
        try {
            DataiIntegrationObject queryObject = new DataiIntegrationObject();
            List<DataiIntegrationObject> allObjects = dataiIntegrationObjectService.selectDataiIntegrationObjectList(queryObject);
            
            for (DataiIntegrationObject object : allObjects) {
                if (!syncedObjectApis.contains(object.getApi())) {
                    String changeReason = "对象已从Salesforce中删除";

                    int similarCount = dataiIntegrationMetadataChangeMapper.countSimilarChanges(
                        "OBJECT", "DELETE", object.getApi(), null, changeReason);
                        
                    if (similarCount > 0) {
                        log.debug("发现相似的未同步对象删除记录，跳过重复记录: {}", object.getApi());
                        continue;
                    }

                    DataiIntegrationMetadataChange metadataChange = new DataiIntegrationMetadataChange();
                    metadataChange.setChangeType("OBJECT");
                    metadataChange.setOperationType("DELETE");
                    metadataChange.setObjectApi(object.getApi());
                    metadataChange.setObjectLabel(object.getLabel());
                    metadataChange.setChangeTime(LocalDateTime.now());
                    metadataChange.setSyncStatus(false);
                    metadataChange.setIsCustom(object.getIsCustom());
                    metadataChange.setChangeReason(changeReason);
                    metadataChange.setChangeUser("SYSTEM");
                    
                    insertDataiIntegrationMetadataChange(metadataChange);
                    log.warn("检测到对象已删除: {}", object.getApi());
                }
            }
        } catch (Exception e) {
            log.error("检查已删除对象时出错: {}", e.getMessage(), e);
        }
    }

    /**
     * 重试操作执行
     * 对指定的操作执行重试机制，在操作失败时进行重试，直到达到最大重试次数
     * 
     * @param <T> 操作返回值类型
     * @param operation 需要执行的操作（Supplier函数式接口）
     * @param maxRetries 最大重试次数
     * @param delayMs 每次重试前的延迟时间（毫秒）
     * @return 操作执行结果
     */
    private <T> T retryOperation(java.util.function.Supplier<T> operation, int maxRetries, long delayMs) {
        // 初始化重试计数器
        int retries = 0;
        // 无限循环直到操作成功或达到最大重试次数
        while (true) {
            try {
                // 执行操作并返回结果
                return operation.get();
            } catch (Exception e) {
                // 增加重试计数
                retries++;
                if (retries >= maxRetries) {
                    // 如果达到最大重试次数，记录错误并抛出异常
                    log.error("操作失败，已达到最大重试次数: {}", maxRetries, e);
                    throw e;
                }
                try {
                    // 记录重试日志并延迟指定时间
                    log.warn("操作失败，将在 {} 毫秒后重试，当前重试次数: {}", delayMs, retries);
                    Thread.sleep(delayMs);
                } catch (InterruptedException ie) {
                    // 如果线程在延迟期间被中断，恢复中断状态并抛出运行时异常
                    Thread.currentThread().interrupt();
                    log.error("重试延迟时线程被中断", ie);
                    throw new RuntimeException(ie);
                }
            }
        }
    }

    /**
     * 构建对象元数据
     *
     * @param objDetail 对象详情
     * @return 构建的对象元数据
     */
    private DataiIntegrationObject buildObjectMetadata(DescribeSObjectResult objDetail) {
        try {
            DataiIntegrationObject object = new DataiIntegrationObject();
            // 基本信息
            object.setApi(objDetail.getName());
            object.setLabel(objDetail.getLabel());
            object.setLabelPlural(objDetail.getLabelPlural());
            object.setKeyPrefix(objDetail.getKeyPrefix());

            // 对象属性
            object.setIsCustom(objDetail.isCustom());
            object.setIsCustomSetting(objDetail.isCustomSetting());
            
            // 对象权限
            object.setIsQueryable(objDetail.isQueryable());
            object.setIsCreateable(objDetail.isCreateable());
            object.setIsUpdateable(objDetail.isUpdateable());
            object.setIsDeletable(objDetail.isDeletable());
            object.setIsReplicateable(objDetail.isReplicateable());
            object.setIsRetrieveable(objDetail.isRetrieveable());
            object.setIsSearchable(objDetail.isSearchable());
            
            // 同步设置
            object.setIsWork(true);
            object.setIsIncremental(true);
            
            // 同步时间
            object.setLastSyncDate(LocalDateTime.now());
            
            return object;
        } catch (Exception e) {
            log.error("构建对象 {} 元数据时出错: {}", objDetail.getName(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * 保存对象字段信息
     *
     * @param objDetail 对象详情
     */
    private void saveObjectFieldsToDataiIntegrationField(DescribeSObjectResult objDetail) {
        String objectApi = objDetail.getName();

        List<DataiIntegrationField> fields = new ArrayList<>();
        List<com.datai.integration.model.domain.DataiIntegrationPicklist> picklists = new ArrayList<>();
        List<com.datai.integration.model.domain.DataiIntegrationFilterLookup> filterLookups = new ArrayList<>();

        for (Field field : objDetail.getFields()) {
            try {
                DataiIntegrationField fieldEntity = new DataiIntegrationField();
                fieldEntity.setApi(objectApi);
                fieldEntity.setField(field.getName());
                fieldEntity.setLabel(field.getLabel());
                fieldEntity.setIsCreateable(field.isCreateable());
                fieldEntity.setIsNillable(field.isNillable());
                fieldEntity.setIsUpdateable(field.isUpdateable());
                fieldEntity.setIsDefaultedOnCreate(field.isDefaultedOnCreate());
                fieldEntity.setIsUnique(field.isUnique());
                fieldEntity.setIsFilterable(field.isFilterable());
                fieldEntity.setIsSortable(field.isSortable());
                fieldEntity.setIsAggregatable(field.isAggregatable());
                fieldEntity.setIsGroupable(field.isGroupable());
                fieldEntity.setIsPolymorphicForeignKey(field.isPolymorphicForeignKey());
                fieldEntity.setPolymorphicForeignField(field.getName() + "_type");
                fieldEntity.setIsExternalId(field.isExternalId());
                fieldEntity.setIsCustom(field.isCustom());
                fieldEntity.setIsCalculated(field.isCalculated());
                fieldEntity.setIsAutoNumber(field.isAutoNumber());
                fieldEntity.setIsCaseSensitive(field.isCaseSensitive());
                fieldEntity.setIsEncrypted(field.isEncrypted());
                fieldEntity.setIsHtmlFormatted(field.isHtmlFormatted());
                fieldEntity.setIsIdLookup(field.isIdLookup());
                fieldEntity.setIsPermissionable(field.isPermissionable());
                fieldEntity.setIsRestrictedPicklist(field.isRestrictedPicklist());
                fieldEntity.setIsRestrictedDelete(field.isRestrictedDelete());
                fieldEntity.setIsWriteRequiresMasterRead(field.isWriteRequiresMasterRead());
                fieldEntity.setFieldDataType(field.getType() != null ? field.getType().toString() : null);
                fieldEntity.setFieldLength(field.getLength());
                fieldEntity.setFieldPrecision(field.getPrecision());
                fieldEntity.setFieldScale(field.getScale());
                fieldEntity.setFieldByteLength(field.getByteLength());
                fieldEntity.setDefaultValue(field.getDefaultValueFormula());
                fieldEntity.setCalculatedFormula(field.getCalculatedFormula());
                fieldEntity.setInlineHelpText(field.getInlineHelpText());
                fieldEntity.setRelationshipName(field.getRelationshipName());
                fieldEntity.setRelationshipOrder(field.getRelationshipOrder());
                fieldEntity.setReferenceTargetField(field.getReferenceTo() != null && field.getReferenceTo().length > 0 ? field.getReferenceTo()[0] : null);
                
                if (field.getReferenceTo() != null && field.getReferenceTo().length > 0) {
                    fieldEntity.setReferenceTo(String.join(",", field.getReferenceTo()));
                    fieldEntity.setReferenceTargetField(field.getReferenceTo()[0]);
                }
                
                if (field.getType() != null) {
                    String fieldType = field.getType().toString().toLowerCase();
                    if ("picklist".equals(fieldType) || "multipicklist".equals(fieldType)) {
                        PicklistEntry[] picklistValues = field.getPicklistValues();
                        if (picklistValues != null && picklistValues.length > 0) {
                            for (PicklistEntry picklistValue : picklistValues) {
                                com.datai.integration.model.domain.DataiIntegrationPicklist picklist = new com.datai.integration.model.domain.DataiIntegrationPicklist();
                                picklist.setApi(objectApi);
                                picklist.setField(field.getName());
                                picklist.setPicklistLabel(picklistValue.getLabel());
                                picklist.setPicklistValue(picklistValue.getValue());
                                picklist.setIsActive(picklistValue.isActive());
                                picklist.setIsDefault(picklistValue.isDefaultValue());
                                picklists.add(picklist);
                            }
                        }
                    }
                }
                
                if (field.getType() != null && "reference".equals(field.getType().toString().toLowerCase())) {
                    FilteredLookupInfo filteredLookupInfo = field.getFilteredLookupInfo();

                    if (filteredLookupInfo != null) {
                        com.datai.integration.model.domain.DataiIntegrationFilterLookup filterLookup = new com.datai.integration.model.domain.DataiIntegrationFilterLookup();
                        filterLookup.setApi(objectApi);
                        filterLookup.setField(field.getName());

                        String[] controllingFields = filteredLookupInfo.getControllingFields();
                        if (controllingFields != null) {
                            filterLookup.setControllingField(String.join(",", controllingFields));
                        } else {
                            filterLookup.setControllingField(null);
                        }

                        filterLookup.setDependent(filteredLookupInfo.getDependent());
                        filterLookups.add(filterLookup);
                    }
                }
                fields.add(fieldEntity);

            } catch (Exception e) {
                log.error("保存对象 {} 的字段 {} 信息时出错: {}", objectApi, field.getName(), e.getMessage());
            }
        }

        syncFieldsAndRecordChanges(objectApi, fields, picklists, filterLookups);
    }

    private void syncFieldsAndRecordChanges(String objectApi, List<DataiIntegrationField> newFields, 
                                            List<com.datai.integration.model.domain.DataiIntegrationPicklist> picklists, 
                                            List<com.datai.integration.model.domain.DataiIntegrationFilterLookup> filterLookups) {
        try {
            DataiIntegrationField queryField = new DataiIntegrationField();
            queryField.setApi(objectApi);
            List<DataiIntegrationField> existingFields = dataiIntegrationFieldService.selectDataiIntegrationFieldList(queryField);
            
            Map<String, DataiIntegrationField> existingFieldMap = new HashMap<>();
            for (DataiIntegrationField existingField : existingFields) {
                existingFieldMap.put(existingField.getField(), existingField);
            }
            
            Map<String, DataiIntegrationField> newFieldMap = new HashMap<>();
            for (DataiIntegrationField newField : newFields) {
                newFieldMap.put(newField.getField(), newField);
            }
            
            DataiIntegrationObject queryObject = new DataiIntegrationObject();
            queryObject.setApi(objectApi);
            List<DataiIntegrationObject> objects = dataiIntegrationObjectService.selectDataiIntegrationObjectList(queryObject);
            String objectLabel = objects.isEmpty() ? objectApi : objects.get(0).getLabel();
            Boolean isCustom = objects.isEmpty() ? false : objects.get(0).getIsCustom();
            
            for (DataiIntegrationField newField : newFields) {
                DataiIntegrationField existingField = existingFieldMap.get(newField.getField());
                
                if (existingField == null) {
                    dataiIntegrationFieldService.insertDataiIntegrationField(newField);
                    recordFieldChange(objectApi, objectLabel, newField.getField(), newField.getLabel(), 
                                     null, "INSERT", "新增字段", isCustom);
                    log.debug("新增字段: {}.{}", objectApi, newField.getField());
                } else {
                    List<String> changedFields = compareFields(existingField, newField);
                    if (!changedFields.isEmpty()) {
                        newField.setId(existingField.getId());
                        dataiIntegrationFieldService.updateDataiIntegrationField(newField);
                        recordFieldChange(objectApi, objectLabel, newField.getField(), newField.getLabel(), 
                                         "字段属性变更: " + String.join(", ", changedFields), 
                                         "UPDATE", "字段属性更新", isCustom);
                        log.debug("更新字段: {}.{} - 变更: {}", objectApi, newField.getField(), 
                                 String.join(", ", changedFields));
                    }
                }
            }
            
            if (!picklists.isEmpty()) {
                for (com.datai.integration.model.domain.DataiIntegrationPicklist picklist : picklists) {
                    dataiIntegrationPicklistService.insertDataiIntegrationPicklist(picklist);
                }
            }
            if (!filterLookups.isEmpty()) {
                for (com.datai.integration.model.domain.DataiIntegrationFilterLookup filterLookup : filterLookups) {
                    dataiIntegrationFilterLookupService.insertDataiIntegrationFilterLookup(filterLookup);
                }
            }
            
        } catch (Exception e) {
            log.error("同步对象 {} 的字段时出错: {}", objectApi, e.getMessage(), e);
        }
    }


    /**
     * 检查对象是否为大数据量对象
     *
     * @param connection Salesforce连接
     * @param objectApi 对象API名称
     * @return 对象数据量
     */
    private int isLargeObject(PartnerConnection connection, String objectApi) {
        try {
            QueryResult queryResult = connection.queryAll("SELECT COUNT() FROM " + objectApi);
            
            if (queryResult != null && queryResult.getRecords() != null && queryResult.getRecords().length > 0) {
                SObject record = queryResult.getRecords()[0];
                Object countValue = record.getField("expr0");
                
                if (countValue != null) {
                    String countStr = countValue.toString();
                    long countLong = Long.parseLong(countStr);
                    return Math.min(Math.toIntExact(countLong), Integer.MAX_VALUE);
                }
            }
        } catch (NumberFormatException e) {
            log.error("将对象 {} 的数据量转换为数字时出错: {}", objectApi, e.getMessage());
        } catch (ArithmeticException e) {
            log.error("对象 {} 的数据量超过int最大值，返回int最大值: {}", objectApi, e.getMessage());
            return Integer.MAX_VALUE;
        } catch (Exception e) {
            log.error("检查对象 {} 的数据量时出错: {}", objectApi, e.getMessage());
        }
        return 0;
    }

    /**
     * 更新对象的字段信息
     *
     * @param objectApi 对象API名称
     * @param objectNum 对象数据量
     */
    private void updateDataiIntegrationObjectFields(String objectApi, int objectNum) {
        if (objectApi == null || objectApi.trim().isEmpty()) {
            log.warn("对象API为空，无法更新字段信息");
            return;
        }
        
        try {
            String dateField = dataiIntegrationFieldService.getDateField(objectApi);
            String blobField = dataiIntegrationFieldService.getBlobField(objectApi);
            
            boolean hasDeletedField = dataiIntegrationFieldService.isDeletedFieldExists(objectApi);
            
            boolean isPartitioned = objectNum > LARGE_OBJECT_THRESHOLD;

            log.info("对象 {} 字段信息，数据量: {}, 是否分区: {}, 日期字段: {}, 二进制字段: {}, 是否有删除字段: {}", 
                     objectApi, objectNum, isPartitioned, dateField, blobField, hasDeletedField);
            
            DataiIntegrationObject queryObject = new DataiIntegrationObject();
            queryObject.setApi(objectApi);
            List<DataiIntegrationObject> objects = dataiIntegrationObjectService.selectDataiIntegrationObjectList(queryObject);
            
            if (!objects.isEmpty()) {
                DataiIntegrationObject object = objects.get(0);
                object.setLastSyncDate(LocalDateTime.now());
                
                int updateResult = dataiIntegrationObjectService.updateDataiIntegrationObject(object);
                
                if (updateResult > 0) {
                    log.debug("成功更新对象 {} 的最后同步时间", objectApi);
                } else {
                    log.warn("更新对象 {} 的最后同步时间失败", objectApi);
                }
            }
        } catch (Exception e) {
            log.error("处理对象 {} 的字段信息时出错: {}", objectApi, e.getMessage(), e);
        }
    }
}
