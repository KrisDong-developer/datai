package com.datai.integration.service.impl;

import java.text.SimpleDateFormat;
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
import com.datai.integration.service.IDataiIntegrationBatchService;
import com.datai.integration.service.IDataiIntegrationFieldService;
import com.datai.integration.service.IDataiIntegrationObjectService;
import com.datai.salesforce.common.param.SalesforceParam;
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
            data.put("lastFullSyncDate", object.getLastFullSyncDate());
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

                if (object.getLastFullSyncDate() != null) {
                    if (latestFullSyncDate == null || object.getLastFullSyncDate().isAfter(latestFullSyncDate)) {
                        latestFullSyncDate = object.getLastFullSyncDate();
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
     * @return Map<String, Object> 同步结果，包含success、message、objectId、objectApi、totalCount、duration、syncType、lastFullSyncDate等字段
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
            
            LocalDateTime lastFullSyncDate = object.getLastFullSyncDate();
            Map<String, Object> syncResult;
            
            if (lastFullSyncDate == null) {
                log.info("对象 {} 的全量拉取时间为空，执行全量数据拉取", objectApi);
                syncResult = syncFullData(object, startTime);
            } else {
                log.info("对象 {} 的全量拉取时间不为空（{}），执行增量数据拉取", objectApi, lastFullSyncDate);
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
            int totalSyncCount = 0;
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
                    if (batchResult.get("syncNum") != null) {
                        totalSyncCount += (Integer) batchResult.get("syncNum");
                    }
                } else {
                    failedBatchCount++;
                }
            }
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            LocalDateTime now = LocalDateTime.now();
            
            object.setLastFullSyncDate(now);
            object.setLastSyncDate(now);
            object.setTotalRows(totalSyncCount);
            object.setSyncStatus(failedBatchCount == 0);
            object.setUpdateTime(DateUtils.getNowDate());
            updateDataiIntegrationObject(object);
            
            result.put("success", true);
            result.put("message", "全量数据拉取完成");
            result.put("objectId", object.getId());
            result.put("objectApi", objectApi);
            result.put("totalCount", totalSyncCount);
            result.put("duration", duration);
            result.put("syncType", "full");
            result.put("lastFullSyncDate", object.getLastFullSyncDate());
            result.put("totalBatchCount", batches.size());
            result.put("successBatchCount", successBatchCount);
            result.put("failedBatchCount", failedBatchCount);
            
            log.info("对象 {} 全量数据拉取完成，共处理 {} 个批次，成功 {} 个，失败 {} 个，总记录数: {}，耗时: {}ms", 
                    objectApi, batches.size(), successBatchCount, failedBatchCount, totalSyncCount, duration);
            
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
        LocalDateTime lastFullSyncDate = object.getLastFullSyncDate();
        
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
            
            List<String> fieldList = getSalesforceObjectFields(connection, objectApi);
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
            incrementalBatch.setBatchField(batchField);
            incrementalBatch.setSyncType("INCREMENTAL");
            incrementalBatch.setSyncStatus(false);
            incrementalBatch.setSyncStartDate(lastFullSyncDate);
            incrementalBatch.setSyncEndDate(LocalDateTime.now());
            incrementalBatch.setCreateTime(DateUtils.getNowDate());
            incrementalBatch.setUpdateTime(DateUtils.getNowDate());
            
            int batchId = dataiIntegrationBatchService.insertDataiIntegrationBatch(incrementalBatch);
            log.info("为对象 {} 创建增量批次，批次ID: {}", objectApi, batchId);
            
            Map<String, Object> batchResult = dataiIntegrationBatchService.syncBatchData(batchId);
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            LocalDateTime now = LocalDateTime.now();
            
            if ((Boolean) batchResult.get("success")) {
                object.setLastSyncDate(now);
                Integer syncNum = (Integer) batchResult.get("syncNum");
                if (object.getTotalRows() != null) {
                    object.setTotalRows(object.getTotalRows() + syncNum);
                } else {
                    object.setTotalRows(syncNum);
                }
                object.setSyncStatus(true);
                object.setUpdateTime(DateUtils.getNowDate());
                updateDataiIntegrationObject(object);
                
                result.put("success", true);
                result.put("message", "增量数据拉取成功");
                result.put("objectId", object.getId());
                result.put("objectApi", objectApi);
                result.put("totalCount", syncNum);
                result.put("duration", duration);
                result.put("syncType", "incremental");
                result.put("lastFullSyncDate", object.getLastFullSyncDate());
                result.put("lastSyncDate", object.getLastSyncDate());
                
                log.info("对象 {} 增量数据拉取成功，同步数据量: {}，耗时: {}ms", objectApi, syncNum, duration);
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

    private int executeQueryAndProcessData(PartnerConnection connection, SalesforceParam param, List<String> fieldList)
    {
        int totalCount = 0;
        int failCount = 0;
        int batch = 0;
        Date lastCreatedDate = null;
        String maxId = null;
        final int MAX_FAIL_COUNT = 3;
        boolean isFirstQuery = true;
        
        try {
            while (true) {
                try {
                    String query = buildDynamicQuery(param, lastCreatedDate, maxId, isFirstQuery);
                    log.info("执行查询，批次: {}, SQL: {}", batch, query);
                    
                    QueryResult queryResult = connection.query(query);
                    
                    if (queryResult == null || queryResult.getRecords() == null || queryResult.getRecords().length == 0) {
                        log.info("批次 {} 查询完成，无更多数据", batch);
                        break;
                    }
                    
                    int currentBatchCount = processQueryResult(param.getApi(), queryResult, fieldList);
                    totalCount += currentBatchCount;
                    
                    SObject[] records = queryResult.getRecords();
                    lastCreatedDate = getLastCreatedDate(records, param.getDateField());
                    maxId = getMaxId(records, param.getDateField(), lastCreatedDate);
                    
                    log.info("批次 {} 处理完成，本批次记录数: {}, 总记录数: {}, 最后时间: {}, 最大ID: {}", 
                            batch, currentBatchCount, totalCount, lastCreatedDate, maxId);
                    
                    failCount = 0;
                    batch++;
                    isFirstQuery = false;
                    
                }catch (Exception e) {
                    failCount++;
                    log.error("批次 {} 查询失败，失败次数: {}/{}，错误信息: {}", 
                            batch, failCount, MAX_FAIL_COUNT, e.getMessage(), e);
                    
                    if (failCount > MAX_FAIL_COUNT) {
                        log.error("达到最大失败次数 {}，停止查询", MAX_FAIL_COUNT);
                        throw new RuntimeException("查询失败次数过多: " + e.getMessage(), e);
                    }

                }
            }
            
            log.info("查询完成，总批次: {}, 总记录数: {}", batch, totalCount);
            
        } catch (Exception e) {
            log.error("执行查询并处理数据失败: {}", e.getMessage(), e);
        }
        
        return totalCount;
    }
    
    /**
     * 从查询结果中获取最后一条记录的日期字段值
     * 
     * @param records Salesforce查询返回的记录数组
     * @param dateField 日期字段名称，用于提取时间戳
     * @return 最后一条记录的日期字段值，如果获取失败则返回null
     */
    private Date getLastCreatedDate(SObject[] records, String dateField)
    {
        // 检查记录数组是否为空
        if (records == null || records.length == 0) {
            return null;
        }
        
        // 获取最后一条记录
        SObject lastRecord = records[records.length - 1];
        if (lastRecord == null) {
            return null;
        }
        
        try {
            // 提取指定的日期字段值
            Object dateValue = lastRecord.getField(dateField);
            if (dateValue instanceof Calendar) {
                // 将Calendar类型转换为Date类型
                return ((Calendar) dateValue).getTime();
            }
        } catch (Exception e) {
            log.warn("获取最后创建时间失败: {}", e.getMessage());
        }
        
        return null;
    }
    
    /**
     * 从查询结果中获取指定日期对应的最大ID值
     * 
     * @param records Salesforce查询返回的记录数组
     * @param dateField 日期字段名称，用于比较日期值
     * @param lastCreatedDate 日期参考值，只查找该日期的记录
     * @return 指定日期对应的最大ID值，如果没有匹配记录则返回null
     */
    private String getMaxId(SObject[] records, String dateField, Date lastCreatedDate)
    {
        // 检查记录数组和日期值是否为空，避免空指针异常
        if (records == null || records.length == 0 || lastCreatedDate == null) {
            return null;
        }
        
        // 初始化最大ID变量，用于存储遍历过程中找到的最大ID值
        String maxId = null;
        
        // 遍历所有记录，查找日期字段值等于lastCreatedDate的记录
        for (SObject record : records) {
            // 跳过空记录，避免空指针异常
            if (record == null) {
                continue;
            }
            
            try {
                // 提取记录的日期字段值
                Object dateValue = record.getField(dateField);
                if (dateValue instanceof Calendar) {
                    // 将Calendar类型转换为Date类型，便于比较
                    Date recordDate = ((Calendar) dateValue).getTime();
                    // 只处理与参考日期相同的记录
                    if (recordDate.equals(lastCreatedDate)) {
                        // 获取记录的ID
                        String recordId = record.getId();
                        // 更新最大ID：如果当前记录ID大于已知的最大ID，则更新
                        if (recordId != null && (maxId == null || recordId.compareTo(maxId) > 0)) {
                            maxId = recordId;
                        }
                    }
                }
            } catch (Exception e) {
                // 字段获取失败时记录警告日志，继续处理其他记录
                log.warn("获取记录ID失败: {}", e.getMessage());
            }
        }
        
        // 返回指定日期对应的最大ID值，该值将作为下一批次查询的起始ID
        return maxId;
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
    private String buildDynamicQuery(SalesforceParam param, Date lastCreatedDate, String maxId, boolean isFirstQuery)
    {
        // 使用StringBuilder构建查询语句，提高字符串拼接效率
        StringBuilder queryBuilder = new StringBuilder();
        // 构建SELECT和FROM子句
        queryBuilder.append("SELECT ").append(param.getSelect())
                   .append(" FROM ").append(param.getApi());
        
        // 创建条件列表，用于存储所有WHERE条件
        List<String> conditions = new ArrayList<>();
        
        // 如果配置了查询已删除记录，添加IsDeleted条件
        if (param.getIsDeleted() != null && param.getIsDeleted()) {
            conditions.add("IsDeleted = true");
        }
        
        // 首次查询时，如果配置了开始修改日期，添加日期范围条件
        if (isFirstQuery && param.getBeginModifyDate() != null) {
            String dateField = param.getDateField();
            if (StringUtils.isNotEmpty(dateField)) {
                // 创建UTC时区的日期格式化器
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.000Z'");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                // 格式化日期为Salesforce支持的格式
                String dateStr = sdf.format(param.getBeginModifyDate());
                // 添加日期大于等于条件
                conditions.add(dateField + " >= " + dateStr);
            }
        }
        
        // 非首次查询时，使用最后创建日期作为查询条件
        if (!isFirstQuery && lastCreatedDate != null) {
            String dateField = param.getDateField();
            if (StringUtils.isNotEmpty(dateField)) {
                // 创建UTC时区的日期格式化器
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'.000Z'");
                sdf.setTimeZone(TimeZone.getTimeZone("UTC"));
                // 格式化日期为Salesforce支持的格式
                String dateStr = sdf.format(lastCreatedDate);
                // 添加日期大于等于条件
                conditions.add(dateField + " >= " + dateStr);
            }
        }
        
        // 如果配置了最大ID，添加ID大于条件，实现精确分页
        if (StringUtils.isNotEmpty(maxId)) {
            conditions.add("Id > '" + maxId + "'");
        }
        
        // 如果有条件，构建WHERE子句
        if (!conditions.isEmpty()) {
            queryBuilder.append(" WHERE ").append(String.join(" AND ", conditions));
        }
        
        // 添加ORDER BY子句，按日期字段和ID排序，确保数据顺序一致
        queryBuilder.append(" ORDER BY ").append(param.getDateField()).append(", Id");
        
        // 如果配置了查询限制，添加LIMIT子句
        if (param.getLimit() != null && param.getLimit() > 0) {
            queryBuilder.append(" LIMIT ").append(param.getLimit());
        }
        
        // 返回构建好的SOQL查询语句
        return queryBuilder.toString();
    }
    
    /**
     * 处理查询结果并将数据保存到数据库
     * 
     * @param api Salesforce对象的API名称，用于标识目标表
     * @param result Salesforce查询返回的结果集，包含所有需要同步的记录
     * @param fieldList 需要同步的字段列表，用于控制数据转换和存储的字段范围
     * @return 成功处理的记录数量
     */
    private int processQueryResult(String api, QueryResult result, List<String> fieldList)
    {
        // 初始化计数器，用于统计成功处理的记录数量
        int count = 0;
        
        // 检查查询结果是否为空，避免空指针异常
        if (result == null || result.getRecords() == null) {
            log.info("处理API {} 的查询结果，共 0 条记录", api);
            return count;
        }
        
        // 获取查询结果中的所有记录
        SObject[] records = result.getRecords();
        log.info("处理API {} 的查询结果，共 {} 条记录", api, records.length);
        
        // 检查目标表是否为分区表，决定数据保存策略
        boolean isPartitioned = checkIfTablePartitioned(api);
        // 初始化批次字段变量，用于分区表的数据分配
        String batchField = null;
        
        // 如果是分区表，获取批次字段配置
        if (isPartitioned) {
            // 根据API名称查询对象配置
            DataiIntegrationObject object = dataiIntegrationObjectMapper.selectDataiIntegrationObjectByApi(api);
            if (object != null && StringUtils.isNotEmpty(object.getBatchField())) {
                // 使用对象配置的批次字段
                batchField = object.getBatchField();
                log.info("对象 {} 配置了批次字段: {}", api, batchField);
            } else {
                // 如果对象未配置批次字段，使用日期字段作为默认批次字段
                batchField = dataiIntegrationFieldService.getDateField(api);
                log.info("对象 {} 未配置批次字段，使用日期字段: {}", api, batchField);
            }
        }
        
        // 创建分区数据Map，用于存储按分区名分组的数据
        Map<String, List<Map<String, Object>>> partitionedData = new HashMap<>();
        // 创建普通数据列表，用于存储非分区表的数据
        List<Map<String, Object>> normalData = new ArrayList<>();
        
        // 遍历所有记录，进行数据转换和分区分配
        for (SObject record : records) {
            // 跳过空记录，避免空指针异常
            if (record == null) {
                continue;
            }
            
            try {
                // 将SObject对象转换为Map格式，便于后续处理
                Map<String, Object> recordMap = convertSObjectToMap(record, fieldList);
                
                // 如果是分区表且配置了批次字段，进行分区分配
                if (isPartitioned && StringUtils.isNotEmpty(batchField)) {
                    // 初始化分区名称为默认分区
                    String partitionName = "p_default";
                    
                    // 检查记录中是否包含批次字段
                    if (recordMap.containsKey(batchField.toLowerCase())) {
                        // 获取批次字段的值
                        Object batchValue = recordMap.get(batchField.toLowerCase());
                        if (batchValue instanceof Date) {
                            // 如果批次字段值为Date类型，使用年份作为分区标识（如p2025）
                            Calendar calendar = Calendar.getInstance();
                            calendar.setTime((Date) batchValue);
                            int year = calendar.get(Calendar.YEAR);
                            partitionName = "p" + year;
                            log.debug("记录根据批次字段 {} 的值 {} 分配到分区 {}", batchField, batchValue, partitionName);
                        } else if (batchValue != null) {
                            // 如果批次字段值为其他类型，使用哈希值作为分区标识
                            partitionName = "p_" + String.valueOf(batchValue).hashCode();
                            log.debug("记录根据批次字段 {} 的值 {} 分配到分区 {}", batchField, batchValue, partitionName);
                        }
                    }
                    
                    // 将记录添加到对应分区的数据列表中
                    partitionedData.computeIfAbsent(partitionName, k -> new ArrayList<>()).add(recordMap);
                } else {
                    // 非分区表，直接添加到普通数据列表
                    normalData.add(recordMap);
                }
                
                // 增加处理计数
                count++;
                
            } catch (Exception e) {
                // 记录处理异常，但继续处理其他记录
                String recordId = record.getId() != null ? record.getId() : "未知";
                log.error("处理记录时发生异常，记录ID: {}", recordId, e);
            }
        }
        
        try {
            // 如果是分区表且有分区数据，批量保存到各个分区
            if (isPartitioned && !partitionedData.isEmpty()) {
                log.info("开始批量保存分区数据，共 {} 个分区", partitionedData.size());
                // 遍历所有分区，保存数据
                for (Map.Entry<String, List<Map<String, Object>>> entry : partitionedData.entrySet()) {
                    String partitionName = entry.getKey();
                    List<Map<String, Object>> dataList = entry.getValue();
                    // 保存数据到指定分区
                    saveDataToPartition(api, partitionName, dataList);
                    log.info("成功保存 {} 条记录到分区 {}", dataList.size(), partitionName);
                }
            } else if (!normalData.isEmpty()) {
                // 非分区表，直接保存数据到目标表
                saveDataToTable(api, normalData);
                log.info("成功保存 {} 条记录到表 {}", normalData.size(), api);
            }
        } catch (Exception e) {
            // 记录批量保存失败的异常
            log.error("批量保存数据到数据库失败: {}", e.getMessage(), e);
        }
        
        // 返回成功处理的记录数量
        return count;
    }
    
    /**
     * 将Salesforce的SObject对象转换为Map格式
     * 
     * @param record Salesforce的SObject对象，包含从Salesforce查询返回的单条记录数据
     * @param fieldList 需要提取的字段列表，控制转换过程中包含哪些字段
     * @return 包含所有指定字段值的Map对象，字段名统一为小写格式
     */
    private Map<String, Object> convertSObjectToMap(SObject record, List<String> fieldList)
    {
        // 创建Map对象用于存储转换后的记录数据
        Map<String, Object> recordMap = new HashMap<>();
        
        // 添加记录ID字段，Id字段总是被包含在结果中
        if (record.getId() != null) {
            recordMap.put("Id", record.getId());
        }
        
        // 遍历所有需要提取的字段
        for (String field : fieldList) {
            // 跳过Id字段，因为已经单独处理过
            if ("Id".equalsIgnoreCase(field)) {
                continue;
            }
            
            try {
                // 从SObject中获取字段值
                Object value = record.getField(field);
                if (value != null) {
                    // 检查字段值是否为Calendar类型（日期类型）
                    if (value instanceof java.util.Calendar) {
                        // 将Calendar类型转换为Date类型，便于数据库存储
                        recordMap.put(field.toLowerCase(), ((Calendar) value).getTime());
                    } else {
                        // 其他类型直接存储，字段名转换为小写以适应数据库命名规范
                        recordMap.put(field.toLowerCase(), value);
                    }
                } else {
                    // 空值字段也保留在结果中，值为null
                    recordMap.put(field.toLowerCase(), null);
                }
            } catch (Exception e) {
                // 字段获取异常时记录警告日志，但不中断处理流程
                log.warn("获取字段 {} 的值时发生异常", field, e);
            }
        }
        
        // 返回转换后的Map对象
        return recordMap;
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
            // 调用CustomMapper查询数据库元数据，判断表是否为分区表
            return customMapper.isPartitioned(tableName);
        } catch (Exception e) {
            // 查询失败时记录警告日志，默认按非分区表处理
            log.warn("检查表 {} 是否分区失败: {}", tableName, e.getMessage());
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
