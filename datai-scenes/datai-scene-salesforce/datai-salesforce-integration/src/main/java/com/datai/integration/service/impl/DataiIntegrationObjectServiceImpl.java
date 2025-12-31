package com.datai.integration.service.impl;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.datai.common.core.domain.model.LoginUser;
import com.datai.common.utils.DateUtils;
import com.datai.common.utils.SecurityUtils;
import com.datai.integration.factory.impl.SOAPConnectionFactory;
import com.datai.integration.mapper.CustomMapper;
import com.datai.integration.mapper.DataiIntegrationObjectMapper;
import com.datai.integration.domain.DataiIntegrationObject;
import com.datai.integration.service.IDataiIntegrationObjectService;
import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;

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
}
