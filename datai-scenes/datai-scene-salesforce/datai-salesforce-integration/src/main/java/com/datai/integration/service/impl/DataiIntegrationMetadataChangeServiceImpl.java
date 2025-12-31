package com.datai.integration.service.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.Arrays;

import com.datai.common.utils.DateUtils;
import com.datai.common.utils.SecurityUtils;
import com.datai.integration.factory.impl.SOAPConnectionFactory;
import com.datai.integration.mapper.CustomMapper;
import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.datai.integration.mapper.DataiIntegrationMetadataChangeMapper;
import com.datai.integration.domain.DataiIntegrationMetadataChange;
import com.datai.integration.domain.DataiIntegrationObject;
import com.datai.integration.domain.DataiIntegrationField;
import com.datai.integration.service.IDataiIntegrationMetadataChangeService;
import com.datai.integration.service.IDataiIntegrationObjectService;
import com.datai.integration.service.IDataiIntegrationFieldService;
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
    @Autowired
    private DataiIntegrationMetadataChangeMapper dataiIntegrationMetadataChangeMapper;

    @Autowired
    private IDataiIntegrationObjectService dataiIntegrationObjectService;

    @Autowired
    private IDataiIntegrationFieldService dataiIntegrationFieldService;

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
            DataiIntegrationMetadataChange metadataChange = selectDataiIntegrationMetadataChangeById(id);
            if (metadataChange == null) {
                result.put("success", false);
                result.put("message", "元数据变更记录不存在");
                return result;
            }

            String changeType = metadataChange.getChangeType();
            String operationType = metadataChange.getOperationType();
            
            if ("OBJECT".equals(changeType)) {
                syncObjectChange(metadataChange, result);
            } else if ("FIELD".equals(changeType)) {
                syncFieldChange(metadataChange, result);
            } else {
                result.put("success", false);
                result.put("message", "不支持的变更类型: " + changeType);
                return result;
            }

            if ((Boolean) result.get("success")) {
                updateSyncStatus(metadataChange.getId(), true, null);
            } else {
                updateSyncStatus(metadataChange.getId(), false, (String) result.get("message"));
            }

        } catch (Exception e) {
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
                DataiIntegrationObject object = new DataiIntegrationObject();
                object.setApi(objectApi);
                object.setLabel(metadataChange.getObjectLabel());
                object.setIsCustom(metadataChange.getIsCustom());
                object.setCreateTime(DateUtils.getNowDate());
                object.setUpdateTime(DateUtils.getNowDate());
                
                int insertResult = dataiIntegrationObjectService.insertDataiIntegrationObject(object);
                result.put("success", insertResult > 0);
                result.put("message", insertResult > 0 ? "对象创建成功" : "对象创建失败");
                
                if (insertResult > 0) {
                    createOrUpdateDatabaseTable(objectApi, metadataChange.getObjectLabel(), result);
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
                object.setUpdateTime(DateUtils.getNowDate());
                
                int updateResult = dataiIntegrationObjectService.updateDataiIntegrationObject(object);
                result.put("success", updateResult > 0);
                result.put("message", updateResult > 0 ? "对象更新成功" : "对象更新失败");
                
                if (updateResult > 0) {
                    updateDatabaseTable(objectApi, metadataChange.getObjectLabel(), result);
                }
                
            } else if ("DELETE".equals(operationType)) {
                DataiIntegrationObject queryObject = new DataiIntegrationObject();
                queryObject.setApi(objectApi);
                
                List<DataiIntegrationObject> existingObjects = dataiIntegrationObjectService.selectDataiIntegrationObjectList(queryObject);
                
                if (existingObjects.isEmpty()) {
                    result.put("success", false);
                    result.put("message", "对象不存在，无法删除");
                    return;
                }

                Integer[] ids = existingObjects.stream()
                    .map(DataiIntegrationObject::getId)
                    .toArray(Integer[]::new);
                
                int deleteResult = dataiIntegrationObjectService.deleteDataiIntegrationObjectByIds(ids);
                result.put("success", deleteResult > 0);
                result.put("message", deleteResult > 0 ? "对象删除成功" : "对象删除失败");
                
                if (deleteResult > 0) {
                    dropDatabaseTable(objectApi, result);
                }
                
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

    private void updateDatabaseTable(String objectApi, String objectLabel, Map<String, Object> result) {
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
            }
            
            customMapper.createTable(objectApi, objectLabel, fieldMaps, new ArrayList<>());
            log.info("成功更新表结构: {}", objectApi);
            
        } catch (ConnectionException e) {
            result.put("success", false);
            result.put("message", "获取Salesforce连接失败: " + e.getMessage());
            log.error("获取Salesforce连接失败: {}", e.getMessage(), e);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "更新表结构失败: " + e.getMessage());
            log.error("更新表结构失败: {}", e.getMessage(), e);
        }
    }

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
                    customMapper.createField(objectApi, fieldApi);
                    log.info("成功添加字段: {}.{}", objectApi, fieldApi);
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
                    String alterSql = String.format("ALTER TABLE %s MODIFY COLUMN %s %s", 
                                                   objectApi, fieldApi, mysqlType);
                    customMapper.executeUpdate(alterSql);
                    log.info("成功修改字段: {}.{}", objectApi, fieldApi);
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
            String alterSql = String.format("ALTER TABLE %s DROP COLUMN %s", objectApi, fieldApi);
            customMapper.executeUpdate(alterSql);
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
}
