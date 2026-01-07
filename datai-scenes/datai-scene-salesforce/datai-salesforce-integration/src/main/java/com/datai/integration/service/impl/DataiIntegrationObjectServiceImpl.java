package com.datai.integration.service.impl;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

import com.datai.common.core.domain.model.LoginUser;
import com.datai.common.utils.DateUtils;
import com.datai.common.utils.SecurityUtils;
import com.datai.integration.factory.impl.SOAPConnectionFactory;
import com.datai.integration.mapper.CustomMapper;
import com.datai.integration.mapper.DataiIntegrationObjectMapper;
import com.datai.integration.model.domain.DataiIntegrationBatch;
import com.datai.integration.model.domain.DataiIntegrationObject;
import com.datai.integration.model.param.DataiSyncParam;
import com.datai.integration.service.IDataiIntegrationBatchService;
import com.datai.integration.service.IDataiIntegrationFieldService;
import com.datai.integration.service.IDataiIntegrationObjectService;
import com.datai.salesforce.common.utils.SoqlBuilder;
import com.datai.setting.future.SalesforceExecutor;
import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import org.apache.commons.lang3.StringUtils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


/**
 * 对象同步控制Service业务层处理
 *
 * @author datai
 * @date 2025-12-24
 */
@Service
@Slf4j
public class DataiIntegrationObjectServiceImpl implements IDataiIntegrationObjectService {
    @Autowired
    private DataiIntegrationObjectMapper dataiIntegrationObjectMapper;

    @Autowired
    private CustomMapper customMapper;

    @Autowired
    private SOAPConnectionFactory soapConnectionFactory;

    @Autowired
    private IDataiIntegrationFieldService dataiIntegrationFieldService;

    @Autowired
    private IDataiIntegrationBatchService dataiIntegrationBatchService;

    @Autowired
    private SalesforceExecutor salesforceExecutor;

    /**
     * 查询对象同步控制
     *
     * @param id 对象同步控制主键
     * @return 对象同步控制
     */
    @Override
    public DataiIntegrationObject selectDataiIntegrationObjectById(Integer id)
    {
        return dataiIntegrationObjectMapper.selectDataiIntegrationObjectById(id);
    }

    /**
     * 查询对象同步控制列表
     *
     * @param dataiIntegrationObject 对象同步控制
     * @return 对象同步控制
     */
    @Override
    public List<DataiIntegrationObject> selectDataiIntegrationObjectList(DataiIntegrationObject dataiIntegrationObject)
    {
        return dataiIntegrationObjectMapper.selectDataiIntegrationObjectList(dataiIntegrationObject);
    }

    /**
     * 新增对象同步控制
     *
     * @param dataiIntegrationObject 对象同步控制
     * @return 结果
     */
    @Override
    public int insertDataiIntegrationObject(DataiIntegrationObject dataiIntegrationObject)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

        dataiIntegrationObject.setCreateTime(DateUtils.getNowDate());
        dataiIntegrationObject.setUpdateTime(DateUtils.getNowDate());
        dataiIntegrationObject.setCreateBy(username);
        dataiIntegrationObject.setUpdateBy(username);
        return dataiIntegrationObjectMapper.insertDataiIntegrationObject(dataiIntegrationObject);
    }

    /**
     * 修改对象同步控制
     *
     * @param dataiIntegrationObject 对象同步控制
     * @return 结果
     */
    @Override
    public int updateDataiIntegrationObject(DataiIntegrationObject dataiIntegrationObject)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

        dataiIntegrationObject.setUpdateTime(DateUtils.getNowDate());
        dataiIntegrationObject.setUpdateBy(username);
        return dataiIntegrationObjectMapper.updateDataiIntegrationObject(dataiIntegrationObject);
    }

    /**
     * 批量删除对象同步控制
     *
     * @param ids 需要删除的对象同步控制主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationObjectByIds(Integer[] ids)
    {
        return dataiIntegrationObjectMapper.deleteDataiIntegrationObjectByIds(ids);
    }

    /**
     * 删除对象同步控制信息
     *
     * @param id 对象同步控制主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationObjectById(Integer id)
    {
        return dataiIntegrationObjectMapper.deleteDataiIntegrationObjectById(id);
    }

    @Override
    public Map<String, Object> getSyncStatistics(Integer id)
    {
        Map<String, Object> statistics = new HashMap<>();

        try {
            if (id == null) {
                statistics.put("success", false);
                statistics.put("message", "对象ID不能为空");
                return statistics;
            }

            DataiIntegrationObject object = dataiIntegrationObjectMapper.selectDataiIntegrationObjectById(id);
            if (object == null) {
                statistics.put("success", false);
                statistics.put("message", "对象不存在");
                return statistics;
            }

            if (object.getApi() == null) {
                statistics.put("success", false);
                statistics.put("message", "对象API名称不能为空");
                return statistics;
            }

            Map<String, Object> syncStats = dataiIntegrationObjectMapper.selectObjectSyncStatistics(object.getApi());
            
            if (syncStats == null) {
                syncStats = new HashMap<>();
            }
            
            int totalSyncCount = syncStats.get("totalSyncCount") != null ? ((Number) syncStats.get("totalSyncCount")).intValue() : 0;
            int successSyncCount = syncStats.get("successSyncCount") != null ? ((Number) syncStats.get("successSyncCount")).intValue() : 0;
            int failedSyncCount = syncStats.get("failedSyncCount") != null ? ((Number) syncStats.get("failedSyncCount")).intValue() : 0;
            
            double successRate = totalSyncCount > 0 ? (double) successSyncCount / totalSyncCount * 100 : 0;
            
            statistics.put("success", true);
            statistics.put("message", "获取对象同步统计信息成功");
            Map<String, Object> data = new HashMap<>();
            data.put("objectId", object.getId());
            data.put("objectApi", object.getApi());
            data.put("objectLabel", object.getLabel());
            data.put("totalSyncCount", totalSyncCount);
            data.put("successSyncCount", successSyncCount);
            data.put("failedSyncCount", failedSyncCount);
            data.put("successRate", successRate);
            data.put("totalSyncRecords", syncStats.get("totalSyncRecords"));
            data.put("avgSyncTime", syncStats.get("avgSyncTime"));
            data.put("firstSyncTime", syncStats.get("firstSyncTime"));
            data.put("lastSyncTime", syncStats.get("lastSyncTime"));
            data.put("firstSyncStartTime", syncStats.get("firstSyncStartTime"));
            data.put("lastSyncEndTime", syncStats.get("lastSyncEndTime"));
            data.put("fullSyncCount", syncStats.get("fullSyncCount"));
            data.put("incrementalSyncCount", syncStats.get("incrementalSyncCount"));
            data.put("lastBatchDate", object.getLastBatchDate());
            data.put("lastSyncDate", object.getLastSyncDate());
            data.put("syncStatus", object.getSyncStatus());
            data.put("errorMessage", object.getErrorMessage());
            data.put("totalRows", object.getTotalRows());
            statistics.put("data", data);
            
        } catch (Exception e) {
            statistics.put("success", false);
            statistics.put("message", "获取对象同步统计信息失败: " + e.getMessage());
            log.error("获取对象同步统计信息失败: {}", e.getMessage(), e);
        }
        
        return statistics;
    }

    @Override
    public List<Map<String, Object>> getObjectDependencies(Integer id)
    {
        try {
            if (id == null) {
                throw new RuntimeException("对象ID不能为空");
            }

            DataiIntegrationObject object = dataiIntegrationObjectMapper.selectDataiIntegrationObjectById(id);
            if (object == null) {
                throw new RuntimeException("对象不存在");
            }

            if (object.getApi() == null) {
                throw new RuntimeException("对象API名称不能为空");
            }

            List<Map<String, Object>> dependencies = dataiIntegrationObjectMapper.selectObjectDependencies(object.getApi());
            return dependencies != null ? dependencies : new ArrayList<>();
            
        } catch (RuntimeException e) {
            throw e;
        } catch (Exception e) {
            log.error("获取对象依赖关系失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取对象依赖关系失败: " + e.getMessage());
        }
    }

    @Override
    public Map<String, Object> createObjectStructure(Integer id)
    {
        Map<String, Object> result = new HashMap<>();
        
        try {
            DataiIntegrationObject object = dataiIntegrationObjectMapper.selectDataiIntegrationObjectById(id);
            if (object == null) {
                result.put("success", false);
                result.put("message", "对象不存在");
                return result;
            }

            String objectApi = object.getApi();
            String objectLabel = object.getLabel();
            
            log.info("开始创建对象表结构: {}, 标签: {}", objectApi, objectLabel);

            PartnerConnection connection = null;
            connection = soapConnectionFactory.getConnection();
            if (connection == null) {
                result.put("success", false);
                result.put("message", "无法获取Salesforce连接");
                return result;
            }

            DescribeSObjectResult objDetail = connection.describeSObject(objectApi);

            if (objDetail == null) {
                result.put("success", false);
                result.put("message", "无法获取对象元数据");
                return result;
            }

            List<Map<String, Object>> fieldMaps = new ArrayList<>();
            List<Map<String, Object>> indexMaps = new ArrayList<>();

            for (Field field : objDetail.getFields()) {
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
            log.info("成功创建表结构: {}", objectApi);

            result.put("success", true);
            result.put("message", "表结构创建成功");
            result.put("tableName", objectApi);
            result.put("tableLabel", objectLabel);
            result.put("fieldCount", fieldMaps.size());
            
        } catch (ConnectionException e) {
            result.put("success", false);
            result.put("message", "获取Salesforce连接失败: " + e.getMessage());
            log.error("获取Salesforce连接失败: {}", e.getMessage(), e);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "创建表结构失败: " + e.getMessage());
            log.error("创建表结构失败: {}", e.getMessage(), e);
        }
        
        return result;
    }

    /**
     * 将Salesforce数据类型转换为MySQL数据类型
     * 
     * 该方法负责将Salesforce对象字段的数据类型映射为MySQL数据库对应的数据类型，
     * 用于自动创建本地数据表时确定字段的SQL数据类型定义。
     * 
     * 类型映射规则：
     * - id → VARCHAR(18)：Salesforce的ID字段为18位字符串
     * - string/email/url/phone → VARCHAR(255)：文本类型字段
     * - textarea → TEXT：长文本字段
     * - boolean → TINYINT(1)：布尔值，MySQL中用0和1表示
     * - int → INT：整数字段
     * - double → DOUBLE：双精度浮点数
     * - currency → DECIMAL(18,4)：货币类型，保留4位小数
     * - date → DATE：日期类型
     * - datetime → DATETIME：日期时间类型
     * - time → TIME：时间类型
     * - percent → DECIMAL(10,2)：百分比，保留2位小数
     * - reference → VARCHAR(18)：引用字段，存储其他对象的ID
     * - picklist/multipicklist → VARCHAR(255)：下拉列表字段
     * - combobox → VARCHAR(255)：组合框字段
     * - base64 → LONGBLOB：二进制大对象，用于存储附件等
     * - address → TEXT：地址字段
     * - location → VARCHAR(255)：位置字段
     * - 其他未知类型 → VARCHAR(255)：默认类型
     * 
     * 使用场景：
     * - 自动创建数据表时确定字段类型
     * - 动态生成DDL语句
     * - 确保数据类型兼容性
     * 
     * @param typeStr Salesforce字段类型字符串，如"string"、"int"等
     * @return 对应的MySQL数据类型字符串，如"VARCHAR(255)"、"INT"等
     */
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
                log.warn("未知的Salesforce类型: {}, 使用默认类型VARCHAR(255)", typeStr);
                return "VARCHAR(255)";
        }
    }

    @Override
    public Map<String, Object> updateWorkStatus(Integer id, Boolean isWork)
    {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (id == null) {
                log.error("对象ID不能为空");
                result.put("success", false);
                result.put("message", "对象ID不能为空");
                return result;
            }
            
            if (isWork == null) {
                log.error("启用同步状态不能为空");
                result.put("success", false);
                result.put("message", "启用同步状态不能为空");
                return result;
            }
            
            log.info("开始变更对象启用同步状态，对象ID: {}, isWork: {}", id, isWork);
            
            DataiIntegrationObject object = dataiIntegrationObjectMapper.selectDataiIntegrationObjectById(id);
            if (object == null) {
                log.error("对象不存在，对象ID: {}", id);
                result.put("success", false);
                result.put("message", "对象不存在");
                return result;
            }
            
            Boolean oldStatus = object.getIsWork();
            object.setIsWork(isWork);
            
            int updateResult = updateDataiIntegrationObject(object);
            
            if (updateResult > 0) {
                log.info("成功变更对象 {} 的启用同步状态，从 {} 变更为 {}", object.getApi(), oldStatus, isWork);
                result.put("success", true);
                result.put("message", "变更启用同步状态成功");
                result.put("objectId", object.getId());
                result.put("objectApi", object.getApi());
                result.put("objectLabel", object.getLabel());
                result.put("oldStatus", oldStatus);
                result.put("newStatus", isWork);
            } else {
                log.error("变更对象 {} 的启用同步状态失败", object.getApi());
                result.put("success", false);
                result.put("message", "变更启用同步状态失败");
            }
            
        } catch (Exception e) {
            log.error("变更对象 {} 启用同步状态时发生异常", id, e);
            result.put("success", false);
            result.put("message", "变更启用同步状态失败: " + e.getMessage());
        }
        
        return result;
    }

    @Override
    public Map<String, Object> updateIncrementalStatus(Integer id, Boolean isIncremental)
    {
        Map<String, Object> result = new HashMap<>();
        
        try {
            if (id == null) {
                log.error("对象ID不能为空");
                result.put("success", false);
                result.put("message", "对象ID不能为空");
                return result;
            }
            
            if (isIncremental == null) {
                log.error("增量更新状态不能为空");
                result.put("success", false);
                result.put("message", "增量更新状态不能为空");
                return result;
            }
            
            log.info("开始变更对象增量更新状态，对象ID: {}, isIncremental: {}", id, isIncremental);
            
            DataiIntegrationObject object = dataiIntegrationObjectMapper.selectDataiIntegrationObjectById(id);
            if (object == null) {
                log.error("对象不存在，对象ID: {}", id);
                result.put("success", false);
                result.put("message", "对象不存在");
                return result;
            }
            
            Boolean oldStatus = object.getIsIncremental();
            object.setIsIncremental(isIncremental);
            
            int updateResult = updateDataiIntegrationObject(object);
            
            if (updateResult > 0) {
                log.info("成功变更对象 {} 的增量更新状态，从 {} 变更为 {}", object.getApi(), oldStatus, isIncremental);
                result.put("success", true);
                result.put("message", "变更增量更新状态成功");
                result.put("objectId", object.getId());
                result.put("objectApi", object.getApi());
                result.put("objectLabel", object.getLabel());
                result.put("oldStatus", oldStatus);
                result.put("newStatus", isIncremental);
            } else {
                log.error("变更对象 {} 的增量更新状态失败", object.getApi());
                result.put("success", false);
                result.put("message", "变更增量更新状态失败");
            }
            
        } catch (Exception e) {
            log.error("变更对象 {} 增量更新状态时发生异常", id, e);
            result.put("success", false);
            result.put("message", "变更增量更新状态失败: " + e.getMessage());
        }
        
        return result;
    }

    @Override
    public Map<String, Object> getObjectStatistics()
    {
        Map<String, Object> statistics = new HashMap<>();

        try {
            log.info("开始获取对象整体统计信息");

            List<DataiIntegrationObject> allObjects = dataiIntegrationObjectMapper.selectDataiIntegrationObjectList(new DataiIntegrationObject());

            int totalObjects = allObjects.size();
            int enabledObjects = 0;
            int disabledObjects = 0;
            int incrementalObjects = 0;
            int fullSyncObjects = 0;
            int customObjects = 0;
            int standardObjects = 0;
            int successSyncObjects = 0;
            int failedSyncObjects = 0;
            int totalRows = 0;
            LocalDateTime latestSyncDate = null;
            LocalDateTime latestFullSyncDate = null;

            for (DataiIntegrationObject object : allObjects) {
                if (Boolean.TRUE.equals(object.getIsWork())) {
                    enabledObjects++;
                } else {
                    disabledObjects++;
                }

                if (Boolean.TRUE.equals(object.getIsIncremental())) {
                    incrementalObjects++;
                } else {
                    fullSyncObjects++;
                }

                if (Boolean.TRUE.equals(object.getIsCustom())) {
                    customObjects++;
                } else {
                    standardObjects++;
                }

                if (Boolean.TRUE.equals(object.getSyncStatus())) {
                    successSyncObjects++;
                } else if (Boolean.FALSE.equals(object.getSyncStatus())) {
                    failedSyncObjects++;
                }

                if (object.getTotalRows() != null) {
                    totalRows += object.getTotalRows();
                }

                if (object.getLastSyncDate() != null) {
                    if (latestSyncDate == null || object.getLastSyncDate().isAfter(latestSyncDate)) {
                        latestSyncDate = object.getLastSyncDate();
                    }
                }

                if (object.getLastBatchDate() != null) {
                    if (latestFullSyncDate == null || object.getLastBatchDate().isAfter(latestFullSyncDate)) {
                        latestFullSyncDate = object.getLastBatchDate();
                    }
                }
            }

            double enabledRate = totalObjects > 0 ? (double) enabledObjects / totalObjects * 100 : 0;
            double incrementalRate = totalObjects > 0 ? (double) incrementalObjects / totalObjects * 100 : 0;
            double customRate = totalObjects > 0 ? (double) customObjects / totalObjects * 100 : 0;
            double successRate = enabledObjects > 0 ? (double) successSyncObjects / enabledObjects * 100 : 0;

            statistics.put("success", true);
            statistics.put("message", "获取对象统计信息成功");

            Map<String, Object> data = new HashMap<>();
            data.put("totalObjects", totalObjects);
            data.put("enabledObjects", enabledObjects);
            data.put("disabledObjects", disabledObjects);
            data.put("enabledRate", enabledRate);
            data.put("incrementalObjects", incrementalObjects);
            data.put("fullSyncObjects", fullSyncObjects);
            data.put("incrementalRate", incrementalRate);
            data.put("customObjects", customObjects);
            data.put("standardObjects", standardObjects);
            data.put("customRate", customRate);
            data.put("successSyncObjects", successSyncObjects);
            data.put("failedSyncObjects", failedSyncObjects);
            data.put("successRate", successRate);
            data.put("totalRows", totalRows);
            data.put("latestSyncDate", latestSyncDate);
            data.put("latestFullSyncDate", latestFullSyncDate);
            statistics.put("data", data);

            log.info("成功获取对象整体统计信息，总对象数: {}, 启用同步: {}, 增量更新: {}", totalObjects, enabledObjects, incrementalObjects);

        } catch (Exception e) {
            log.error("获取对象整体统计信息失败", e);
            statistics.put("success", false);
            statistics.put("message", "获取对象统计信息失败: " + e.getMessage());
        }

        return statistics;
    }

    /**
     * 同步单个Salesforce对象的数据到本地数据库
     * 
     * @param id 对象ID，用于标识需要同步的Salesforce对象
     * @return Map<String, Object> 同步结果，包含success、message、objectId、objectApi、totalCount、duration、syncType、lastBatchDate等字段
     * @throws ConnectionException 当获取Salesforce连接失败时抛出
     * @throws RuntimeException 当同步过程中发生其他异常时抛出
     */
    @Override
    public Map<String, Object> syncSingleObjectData(Integer id)
    {
        Map<String, Object> result = new HashMap<>();
        long startTime = System.currentTimeMillis();
        DataiIntegrationObject object = null;
        
        try {
            if (id == null) {
                log.error("对象ID不能为空");
                result.put("success", false);
                result.put("message", "对象ID不能为空");
                return result;
            }
            
            log.info("开始同步单对象数据，对象ID: {}", id);

            object = dataiIntegrationObjectMapper.selectDataiIntegrationObjectById(id);
            if (object == null) {
                log.error("对象不存在，对象ID: {}", id);
                result.put("success", false);
                result.put("message", "对象不存在");
                return result;
            }
            
            if (object.getApi() == null || object.getApi().trim().isEmpty()) {
                log.error("对象API为空，对象ID: {}", id);
                result.put("success", false);
                result.put("message", "对象API不能为空");
                return result;
            }
            
            if (!object.getIsWork()) {
                log.warn("对象未启用同步，对象ID: {}, 对象API: {}", id, object.getApi());
                result.put("success", false);
                result.put("message", "对象未启用同步");
                return result;
            }
            
            String objectApi = object.getApi().trim();
            
            log.info("准备同步对象数据，对象API: {}", objectApi);
            
            LocalDateTime lastSyncDate = object.getLastSyncDate();
            Map<String, Object> syncResult;
            
            if (lastSyncDate == null) {
                log.info("对象 {} 的最近同步时间为空，执行全量数据拉取", objectApi);
                syncResult = syncFullData(object, startTime);
            } else {
                log.info("对象 {} 的最近同步时间不为空（{}），执行增量数据拉取", objectApi, lastSyncDate);
                syncResult = syncIncrementalData(object, startTime);
            }
            
            result.putAll(syncResult);
            
        } catch (Exception e) {
            log.error("同步单对象数据失败，对象ID: {}", id, e);
            if (object != null) {
                object.setErrorMessage("同步单对象数据失败: " + e.getMessage());
                object.setSyncStatus(false);
                object.setUpdateTime(DateUtils.getNowDate());
                updateDataiIntegrationObject(object);
            }
            result.put("success", false);
            result.put("message", "同步单对象数据失败: " + e.getMessage());
        }
        
        return result;
    }

    @Override
    public Map<String, Object> syncMultipleObjectData(Integer[] ids) {
        Map<String, Object> result = new HashMap<>();
        long startTime = System.currentTimeMillis();
        
        try {
            if (ids == null || ids.length == 0) {
                log.error("对象ID数组不能为空");
                result.put("success", false);
                result.put("message", "对象ID数组不能为空");
                return result;
            }
            
            log.info("开始同步多个对象数据，对象数量: {}", ids.length);
            
            List<Map<String, Object>> syncResults = new ArrayList<>();
            int successCount = 0;
            int failureCount = 0;
            int totalSyncRecords = 0;
            List<Map<String, Object>> failedObjects = new ArrayList<>();
            
            for (int i = 0; i < ids.length; i++) {
                Integer id = ids[i];
                log.info("开始同步第 {}/{} 个对象，对象ID: {}", i + 1, ids.length, id);
                
                try {
                    Map<String, Object> singleResult = syncSingleObjectData(id);
                    syncResults.add(singleResult);
                    
                    if ((Boolean) singleResult.get("success")) {
                        successCount++;
                        Integer totalCount = (Integer) singleResult.get("totalCount");
                        if (totalCount != null) {
                            totalSyncRecords += totalCount;
                        }
                        log.info("对象 {} 同步成功，同步记录数: {}", id, totalCount);
                    } else {
                        failureCount++;
                        Map<String, Object> failedObject = new HashMap<>();
                        failedObject.put("objectId", id);
                        failedObject.put("message", singleResult.get("message"));
                        failedObjects.add(failedObject);
                        log.error("对象 {} 同步失败: {}", id, singleResult.get("message"));
                    }
                    
                } catch (Exception e) {
                    failureCount++;
                    Map<String, Object> failedObject = new HashMap<>();
                    failedObject.put("objectId", id);
                    failedObject.put("message", "同步异常: " + e.getMessage());
                    failedObjects.add(failedObject);
                    log.error("同步对象 {} 时发生异常", id, e);
                }
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            
            result.put("success", true);
            result.put("message", "多对象数据同步完成");
            result.put("totalObjects", ids.length);
            result.put("successCount", successCount);
            result.put("failureCount", failureCount);
            result.put("totalSyncRecords", totalSyncRecords);
            result.put("duration", duration);
            result.put("syncResults", syncResults);
            result.put("failedObjects", failedObjects);
            
            log.info("多对象数据同步完成，总对象数: {}, 成功: {}, 失败: {}, 总同步记录数: {}, 耗时: {}ms", 
                    ids.length, successCount, failureCount, totalSyncRecords, duration);
            
        } catch (Exception e) {
            log.error("同步多个对象数据时发生异常", e);
            result.put("success", false);
            result.put("message", "同步多个对象数据时发生异常: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 全量数据拉取
     * 查询所有批次并使用多线程并行拉取
     * 
     * @param object 对象信息
     * @param startTime 开始时间
     * @return 同步结果
     */
    private Map<String, Object> syncFullData(DataiIntegrationObject object, long startTime) {
        Map<String, Object> result = new HashMap<>();
        String objectApi = object.getApi().trim();
        
        try {
            DataiIntegrationBatch queryBatch = new DataiIntegrationBatch();
            queryBatch.setApi(objectApi);
            queryBatch.setSyncType("FULL");
            List<DataiIntegrationBatch> batches = dataiIntegrationBatchService.selectDataiIntegrationBatchList(queryBatch);
            
            if (batches.isEmpty()) {
                log.warn("对象 {} 没有找到全量同步批次，无法执行全量数据拉取", objectApi);
                result.put("success", false);
                result.put("message", "没有找到全量同步批次");
                return result;
            }
            
            log.info("对象 {} 共找到 {} 个全量同步批次，准备使用多线程并行拉取", objectApi, batches.size());
            
            List<java.util.concurrent.Future<?>> futures = new ArrayList<>();
            List<Map<String, Object>> batchResults = new CopyOnWriteArrayList<>();
            int successBatchCount = 0;
            int failedBatchCount = 0;
            
            for (int i = 0; i < batches.size(); i++) {
                DataiIntegrationBatch batch = batches.get(i);
                final int batchIndex = i;
                
                java.util.concurrent.Future<?> future = salesforceExecutor.execute(() -> {
                    try {
                        log.info("开始拉取批次 {} (批次ID: {}, 批次索引: {}/{})", 
                                batch.getSyncStartDate(), batch.getId(), batchIndex + 1, batches.size());
                        
                        Map<String, Object> batchResult = dataiIntegrationBatchService.syncBatchData(batch.getId());
                        batchResults.add(batchResult);
                        
                        if ((Boolean) batchResult.get("success")) {
                            log.info("批次 {} 数据拉取成功，同步数据量: {}", 
                                    batch.getSyncStartDate(), batchResult.get("syncNum"));
                        } else {
                            log.error("批次 {} 数据拉取失败: {}", 
                                    batch.getSyncStartDate(), batchResult.get("message"));
                        }

                    } catch (Exception e) {
                        log.error("拉取批次 {} 时发生异常", batch.getSyncStartDate(), e);
                        Map<String, Object> errorResult = new HashMap<>();
                        errorResult.put("success", false);
                        errorResult.put("message", e.getMessage());
                        batchResults.add(errorResult);
                    }
                }, 0, i);
                
                futures.add(future);
            }
            
            for (java.util.concurrent.Future<?> future : futures) {
                try {
                    future.get();
                } catch (Exception e) {
                    log.error("获取批次同步结果时发生异常", e);
                    failedBatchCount++;
                }
            }
            
            for (Map<String, Object> batchResult : batchResults) {
                if ((Boolean) batchResult.get("success")) {
                    successBatchCount++;
                } else {
                    failedBatchCount++;
                }
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            LocalDateTime now = LocalDateTime.now();
            
            object.setLastSyncDate(now);
            
            DataiIntegrationBatch queryAllBatches = new DataiIntegrationBatch();
            queryAllBatches.setApi(objectApi);
            List<DataiIntegrationBatch> allBatches = dataiIntegrationBatchService.selectDataiIntegrationBatchList(queryAllBatches);
            
            int totalDbNum = allBatches.stream()
                .filter(batch -> batch.getDbNum() != null)
                .mapToInt(DataiIntegrationBatch::getDbNum)
                .sum();
            
            object.setTotalRows(totalDbNum);
            object.setSyncStatus(failedBatchCount == 0);
            object.setUpdateTime(DateUtils.getNowDate());
            updateDataiIntegrationObject(object);
            
            result.put("success", true);
            result.put("message", "全量数据拉取完成");
            result.put("objectId", object.getId());
            result.put("objectApi", objectApi);
            result.put("totalCount", totalDbNum);
            result.put("duration", duration);
            result.put("syncType", "full");
            result.put("lastBatchDate", object.getLastBatchDate());
            result.put("totalBatchCount", batches.size());
            result.put("successBatchCount", successBatchCount);
            result.put("failedBatchCount", failedBatchCount);
            
            log.info("对象 {} 全量数据拉取完成，共处理 {} 个批次，成功 {} 个，失败 {} 个，总记录数: {}，耗时: {}ms", 
                    objectApi, batches.size(), successBatchCount, failedBatchCount, totalDbNum, duration);
            
        } catch (Exception e) {
            log.error("全量数据拉取失败，对象API: {}", objectApi, e);
            result.put("success", false);
            result.put("message", "全量数据拉取失败: " + e.getMessage());
        }
        
        return result;
    }

    /**
     * 增量数据拉取
     * 创建增量批次并使用多线程并行拉取
     * 
     * @param object 对象信息
     * @param startTime 开始时间
     * @return 同步结果
     */
    private Map<String, Object> syncIncrementalData(DataiIntegrationObject object, long startTime) {
        Map<String, Object> result = new HashMap<>();
        String objectApi = object.getApi().trim();
        LocalDateTime lastBatchDate = object.getLastBatchDate();
        
        try {
            if (!Boolean.TRUE.equals(object.getIsIncremental())) {
                log.warn("对象 {} 未启用增量更新，不执行增量数据拉取", objectApi);
                result.put("success", false);
                result.put("message", "对象未启用增量更新");
                return result;
            }
            
            PartnerConnection connection = soapConnectionFactory.getConnection();
            if (connection == null) {
                log.error("无法获取Salesforce连接");
                result.put("success", false);
                result.put("message", "无法获取Salesforce连接");
                return result;
            }
            String batchField = object.getBatchField();
            
            if (batchField == null || batchField.trim().isEmpty()) {
                log.warn("对象 {} 没有设置批次字段，使用默认日期字段", objectApi);
                batchField = dataiIntegrationFieldService.getDateField(objectApi);
            }
            
            if (batchField == null || batchField.trim().isEmpty()) {
                log.error("对象 {} 无法确定批次字段，无法创建增量批次", objectApi);
                result.put("success", false);
                result.put("message", "无法确定批次字段");
                return result;
            }
            
            DataiIntegrationBatch incrementalBatch = new DataiIntegrationBatch();
            incrementalBatch.setApi(objectApi);
            incrementalBatch.setLabel(object.getLabel());
            incrementalBatch.setBatchField(dataiIntegrationFieldService.getUpdateField(objectApi));
            incrementalBatch.setSyncType("INCREMENTAL");
            incrementalBatch.setSyncStatus(false);
            incrementalBatch.setSyncStartDate(lastBatchDate);
            incrementalBatch.setSyncEndDate(LocalDateTime.now());
            incrementalBatch.setCreateTime(DateUtils.getNowDate());
            incrementalBatch.setUpdateTime(DateUtils.getNowDate());
            
            dataiIntegrationBatchService.insertDataiIntegrationBatch(incrementalBatch);
            Integer batchId = incrementalBatch.getId();
            log.info("为对象 {} 创建增量批次，批次ID: {}", objectApi, batchId);
            
            object.setLastBatchDate(incrementalBatch.getSyncEndDate());

            Map<String, Object> batchResult = dataiIntegrationBatchService.syncBatchData(batchId);

            Integer totalDbNum = customMapper.countBySQL(objectApi, null);
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            LocalDateTime now = LocalDateTime.now();
            
            if ((Boolean) batchResult.get("success")) {
                object.setLastSyncDate(now);
                object.setTotalRows(totalDbNum);
                object.setSyncStatus(true);
                object.setUpdateTime(DateUtils.getNowDate());
                updateDataiIntegrationObject(object);
                
                result.put("success", true);
                result.put("message", "增量数据拉取成功");
                result.put("objectId", object.getId());
                result.put("objectApi", objectApi);
                result.put("totalCount", batchResult.get(""));
                result.put("duration", duration);
                result.put("syncType", "incremental");
                result.put("lastBatchDate", object.getLastBatchDate());
                result.put("lastSyncDate", object.getLastSyncDate());
                
                log.info("对象 {} 增量数据拉取成功，总数据量: {}，耗时: {}ms", objectApi, totalDbNum, duration);
            } else {
                object.setSyncStatus(false);
                object.setUpdateTime(DateUtils.getNowDate());
                updateDataiIntegrationObject(object);
                
                result.put("success", false);
                result.put("message", "增量数据拉取失败: " + batchResult.get("message"));
                
                log.error("对象 {} 增量数据拉取失败: {}", objectApi, batchResult.get("message"));
            }
            
        } catch (Exception e) {
            log.error("增量数据拉取失败，对象API: {}", objectApi, e);
            result.put("success", false);
            result.put("message", "增量数据拉取失败: " + e.getMessage());
        }
        
        return result;
    }

    private List<String> getSalesforceObjectFields(PartnerConnection connection, String objectApi) throws ConnectionException
    {
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
     * 构建动态SOQL查询语句
     * 
     * @param param Salesforce查询参数对象
     * @param lastCreatedDate 最后创建日期，用于增量查询
     * @param maxId 最大ID值，用于分页查询
     * @param isFirstQuery 是否为首次查询
     * @return 构建好的SOQL查询语句
     */
    private String buildDynamicQuery(DataiSyncParam param, Date lastCreatedDate, String maxId, boolean isFirstQuery)
    {
        SoqlBuilder builder = new SoqlBuilder();
        
        String[] selectFields = param.getSelect().split(",");
        builder.select(selectFields)
               .from(param.getApi());
        
        if (param.getIsDeleted() != null && param.getIsDeleted()) {
            builder.where("IsDeleted = true");
        }
        
        if (isFirstQuery && param.getBeginModifyDate() != null) {
            String dateField = param.getBatchField();
            if (StringUtils.isNotEmpty(dateField)) {
                builder.whereGe(dateField, param.getBeginModifyDate());
            }
        }
        
        if (!isFirstQuery && lastCreatedDate != null) {
            String dateField = param.getBatchField();
            if (StringUtils.isNotEmpty(dateField)) {
                builder.whereGe(dateField, lastCreatedDate);
            }
        }
        
        if (StringUtils.isNotEmpty(maxId)) {
            builder.whereGt("Id", maxId);
        }
        
        builder.orderBy(param.getBatchField(), SoqlBuilder.SortOrder.ASC)
               .orderBy("Id", SoqlBuilder.SortOrder.ASC);
        
        if (param.getLimit() != null && param.getLimit() > 0) {
            builder.limit(param.getLimit());
        }
        
        return builder.build();
    }


    /**
     * 检查指定表是否为分区表
     * 
     * @param tableName 需要检查的表名，对应Salesforce对象的API名称
     * @return 如果表是分区表返回true，否则返回false
     */
    private boolean checkIfTablePartitioned(String tableName)
    {
        try {
            DataiIntegrationObject object = dataiIntegrationObjectMapper.selectDataiIntegrationObjectByApi(tableName);
            if (object != null && object.getIsPartitioned() != null) {
                return object.getIsPartitioned();
            }
            return false;
        } catch (Exception e) {
            log.warn("检查对象 {} 分区状态失败: {}，默认按非分区表处理", tableName, e.getMessage());
            return false;
        }
    }
    
    /**
     * 批量保存数据到非分区表
     * 
     * @param tableName 目标表名，对应Salesforce对象的API名称
     * @param dataList 需要保存的数据列表，每条记录是一个Map，键为字段名，值为字段值
     */
    private void saveDataToTable(String tableName, List<Map<String, Object>> dataList)
    {
        // 检查数据列表是否为空，为空则直接返回
        if (dataList == null || dataList.isEmpty()) {
            return;
        }
        
        try {
            // 创建字段名列表，用于存储表的所有列名
            List<String> keys = new ArrayList<>();
            // 创建字段值列表，用于存储所有记录的字段值
            List<Collection<Object>> values = new ArrayList<>();
            
            // 遍历所有数据记录
            for (Map<String, Object> dataMap : dataList) {
                // 如果是第一条记录，提取所有字段名作为列名
                if (keys.isEmpty()) {
                    keys.addAll(dataMap.keySet());
                }
                // 添加当前记录的字段值到值列表
                values.add(dataMap.values());
            }
            
            // 调用CustomMapper执行批量插入操作
            customMapper.saveBatch(tableName, keys, values);
            log.info("成功保存 {} 条记录到表 {}", dataList.size(), tableName);
        } catch (Exception e) {
            // 记录错误日志并重新抛出异常，由调用方处理
            log.error("批量保存数据到表 {} 失败: {}", tableName, e.getMessage(), e);
            throw e;
        }
    }
    
    /**
     * 批量保存数据到分区表的指定分区
     * 
     * @param tableName 目标表名，对应Salesforce对象的API名称
     * @param partitionName 分区名称，标识数据应该保存到哪个分区（如p2025）
     * @param dataList 需要保存的数据列表，每条记录是一个Map，键为字段名，值为字段值
     */
    private void saveDataToPartition(String tableName, String partitionName, List<Map<String, Object>> dataList)
    {
        // 检查数据列表是否为空，为空则直接返回
        if (dataList == null || dataList.isEmpty()) {
            return;
        }
        
        try {
            // 创建字段名列表，用于存储表的所有列名
            List<String> keys = new ArrayList<>();
            // 创建字段值列表，用于存储所有记录的字段值
            List<Collection<Object>> values = new ArrayList<>();
            
            // 遍历所有数据记录
            for (Map<String, Object> dataMap : dataList) {
                // 如果是第一条记录，提取所有字段名作为列名
                if (keys.isEmpty()) {
                    keys.addAll(dataMap.keySet());
                }
                // 添加当前记录的字段值到值列表
                values.add(dataMap.values());
            }
            
            // 调用CustomMapper执行批量插入操作到指定分区
            customMapper.saveBatchToPartition(tableName, partitionName, keys, values);
            log.info("成功保存 {} 条记录到分区 {}", dataList.size(), partitionName);
        } catch (Exception e) {
            // 记录错误日志并重新抛出异常，由调用方处理
            log.error("批量保存数据到分区 {} 失败: {}", partitionName, e.getMessage(), e);
            throw e;
        }
    }
}
