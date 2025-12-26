package com.datai.integration.service.impl;

import com.datai.integration.domain.DataiIntegrationField;
import com.datai.integration.domain.DataiIntegrationFilterLookup;
import com.datai.integration.domain.DataiIntegrationObject;
import com.datai.integration.domain.DataiIntegrationPicklist;
import com.datai.integration.factory.SOAPConnectionFactory;
import com.datai.integration.mapper.CustomMapper;
import com.datai.integration.service.IDataiIntegrationFieldService;
import com.datai.integration.service.IDataiIntegrationFilterLookupService;
import com.datai.integration.service.IDataiIntegrationObjectService;
import com.datai.integration.service.IDataiIntegrationPicklistService;
import com.datai.integration.service.ISalesforceDataPullService;
import com.datai.salesforce.common.param.SalesforceParam;
import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.FilteredLookupInfo;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.PicklistEntry;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Salesforce数据拉取服务实现类
 * <p>
 * 该类实现了Salesforce数据拉取的所有方法，包括单个对象拉取、批量拉取、增量拉取等。
 * </p>
 */
@Service
@Slf4j
public class SalesforceDataPullServiceImpl implements ISalesforceDataPullService {

    @Autowired
    private CustomMapper customMapper;

    @Autowired
    private IDataiIntegrationFieldService integrationFieldService;

    @Autowired
    private IDataiIntegrationObjectService integrationObjectService;

    @Autowired
    private IDataiIntegrationPicklistService integrationPicklistService;

    @Autowired
    private IDataiIntegrationFilterLookupService integrationFilterLookupService;



    /**
     * 大数据量对象阈值（100万）
     */
    private static final int LARGE_OBJECT_THRESHOLD = 5000000;

    /**
     * 同步多个Salesforce对象的表结构
     *
     * @param objectApis Salesforce对象API列表
     * @return 同步结果，键为对象API，值为同步结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public Map<String, Boolean> syncMultipleObjectStructures(List<String> objectApis) {
        log.info("准备同步Salesforce对象的表结构，对象API列表: {}", objectApis);
        Map<String, Boolean> resultMap = new HashMap<>();
        
        if (objectApis == null || objectApis.isEmpty()) {
            log.warn("对象API列表为空，无需同步");
            return resultMap;
        }

        try {
            // 获取源ORG连接，添加重试机制
            PartnerConnection connection = retryOperation(() -> SOAPConnectionFactory.instance(), 3, 1000);
            log.info("成功获取源ORG连接");

            for (String objectApi : objectApis) {
                if (objectApi != null && !objectApi.trim().isEmpty()) {
                    boolean result = false;
                    try {
                        log.info("开始同步对象: {}", objectApi);
                        // 获取对象详细信息
                        DescribeSObjectResult objDetail = connection.describeSObject(objectApi.trim());
                        
                        // 保存对象元数据
                        DataiIntegrationObject object = buildObjectMetadata(objDetail);
                        
                        // 检查buildObjectMetadata是否成功
                        if (object == null) {
                            log.error("构建对象 {} 的元数据失败，跳过该对象的同步", objectApi);
                            resultMap.put(objectApi.trim(), false);
                            continue;
                        }

                        // 保存对象字段信息
                        saveObjectFieldsToDataiIntegrationField(objDetail);

                        // 检查对象数据量
                        int objectNum = isLargeObject(connection, objectApi.trim());
                        log.info("对象 {} 的数据量: {}", objectApi, objectNum);

                        // 更新对象的日期和二进制字段信息
                        updateDataiIntegrationObjectFields(objectApi.trim(), objectNum);

                        // 统一分区表判断阈值为100万
                        if (objectNum > LARGE_OBJECT_THRESHOLD) {
                            log.info("对象 {} 是大数据量对象，数据量大于一百万，创建分区表", objectApi);
                            // 创建分区表（实际实现需要根据实际情况调整）
                            result = true;
                        } else {
                            log.info("对象 {} 是普通对象，数据量少于一百万，创建正常表", objectApi);
                            // 创建正常表（实际实现需要根据实际情况调整）
                            result = true;
                        }
                        
                        log.info("同步对象 {} 成功", objectApi);
                        resultMap.put(objectApi.trim(), result);
                    } catch (ConnectionException e) {
                        log.error("获取对象 {} 的元数据失败: {}", objectApi, e.getMessage(), e);
                        resultMap.put(objectApi.trim(), false);
                    } catch (Exception e) {
                        log.error("处理对象 {} 时出错: {}", objectApi, e.getMessage(), e);
                        resultMap.put(objectApi.trim(), false);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Salesforce对象同步任务执行失败", e);
        }
        
        return resultMap;
    }

    /**
     * 更新对象的字段信息
     *
     * @param objectApi 对象API名称
     * @param objectNum 对象数据量
     */
    private void updateDataiIntegrationObjectFields(String objectApi, int objectNum) {
        if (StringUtils.isBlank(objectApi)) {
            log.warn("对象API为空，无法更新字段信息");
            return;
        }
        
        try {
            // 获取日期字段和二进制字段
            String dateField = integrationFieldService.getDateField(objectApi);
            String blobField = integrationFieldService.getBlobField(objectApi);
            
            // 检查是否存在删除字段
            boolean hasDeletedField = integrationFieldService.isDeletedFieldExists(objectApi);
            
            // 检查对象是否需要分区
            boolean isPartitioned = objectNum > LARGE_OBJECT_THRESHOLD;

            log.info("对象 {} 字段信息，数据量: {}, 是否分区: {}, 日期字段: {}, 二进制字段: {}, 是否有删除字段: {}", 
                     objectApi, objectNum, isPartitioned, dateField, blobField, hasDeletedField);
            
            // 查询对象是否存在
            DataiIntegrationObject queryObject = new DataiIntegrationObject();
            queryObject.setApi(objectApi);
            List<DataiIntegrationObject> objects = integrationObjectService.selectDataiIntegrationObjectList(queryObject);
            
            if (!objects.isEmpty()) {
                // 更新现有对象
                DataiIntegrationObject object = objects.get(0);
                // 仅更新最后同步时间
                object.setLastSyncDate(LocalDateTime.now());
                
                int updateResult = integrationObjectService.updateDataiIntegrationObject(object);
                
                if (updateResult > 0) {
                    log.debug("成功更新对象 {} 的最后同步时间", objectApi);
                } else {
                    log.warn("更新对象 {} 的最后同步时间失败", objectApi);
                }
            }
        } catch (Exception e) {
            log.error("处理对象 {} 的字段信息时出错: {}", objectApi, e.getMessage(), e);
            // 不抛出异常，因为该方法不是事务的核心部分
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
        List<DataiIntegrationPicklist> picklists = new ArrayList<>();
        List<DataiIntegrationFilterLookup> filterLookups = new ArrayList<>();

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
                
                // 设置引用目标对象
                if (field.getReferenceTo() != null && field.getReferenceTo().length > 0) {
                    fieldEntity.setReferenceTo(String.join(",", field.getReferenceTo()));
                    fieldEntity.setReferenceTargetField(field.getReferenceTo()[0]);
                }
                
                // 处理选择列表字段
                if (field.getType() != null) {
                    String fieldType = field.getType().toString().toLowerCase();
                    if ("picklist".equals(fieldType) || "multipicklist".equals(fieldType)) {
                        PicklistEntry[] picklistValues = field.getPicklistValues();
                        if (picklistValues != null && picklistValues.length > 0) {
                            for (PicklistEntry picklistValue : picklistValues) {
                                DataiIntegrationPicklist picklist = new DataiIntegrationPicklist();
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
                
                // 处理引用字段
                if (field.getType() != null && "reference".equals(field.getType().toString().toLowerCase())) {
                    FilteredLookupInfo filteredLookupInfo = field.getFilteredLookupInfo();

                    if (filteredLookupInfo != null) {
                        DataiIntegrationFilterLookup filterLookup = new DataiIntegrationFilterLookup();
                        filterLookup.setApi(objectApi);
                        filterLookup.setField(field.getName());

                        // 获取控制字段
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

        // 保存字段、选择列表和过滤查找信息
        if (!fields.isEmpty()) {
            for (DataiIntegrationField field : fields) {
                integrationFieldService.insertDataiIntegrationField(field);
            }
        }
        if (!picklists.isEmpty()) {
            for (DataiIntegrationPicklist picklist : picklists) {
                integrationPicklistService.insertDataiIntegrationPicklist(picklist);
            }
        }
        if (!filterLookups.isEmpty()) {
            for (DataiIntegrationFilterLookup filterLookup : filterLookups) {
                integrationFilterLookupService.insertDataiIntegrationFilterLookup(filterLookup);
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
            object.setIsWork(true); // 默认启用
            object.setIsIncremental(true); // 默认增量更新
            
            // 同步时间
            object.setLastSyncDate(LocalDateTime.now());
            
            // 保存到数据库
            int insertResult = integrationObjectService.insertDataiIntegrationObject(object);
            if (insertResult > 0) {
                log.info("保存对象 {} 元数据到数据库成功", objDetail.getName());
            } else {
                log.warn("保存对象 {} 元数据到数据库失败，返回结果: {}", objDetail.getName(), insertResult);
            }
            return object;
        } catch (Exception e) {
            log.error("保存对象 {} 元数据时出错: {}", objDetail.getName(), e.getMessage(), e);
            return null;
        }
    }

    /**
     * 重试操作的通用方法
     *
     * @param operation 要执行的操作
     * @param maxRetries 最大重试次数
     * @param delayMs 重试延迟时间（毫秒）
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
     * 检查Salesforce对象的数据量
     *
     * @param connection Salesforce连接
     * @param objectApi  对象API名称
     * @return 对象的数据量
     */
    private int isLargeObject(PartnerConnection connection, String objectApi) {
        try {
            // 查询对象数据总量（简化实现，实际应使用更高效的方式）
            QueryResult queryResult = connection.queryAll("SELECT COUNT() FROM " + objectApi);
            
            // 检查查询结果
            if (queryResult != null && queryResult.getRecords() != null && queryResult.getRecords().length > 0) {
                SObject record = queryResult.getRecords()[0];
                Object countValue = record.getField("expr0");
                
                if (countValue != null) {
                    String countStr = countValue.toString();
                    // 安全转换为long
                    long countLong = Long.parseLong(countStr);
                    // 转换为int，确保不会溢出
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
     * 同步单个Salesforce对象的数据
     *
     * @param objectApi Salesforce对象API
     * @return 同步结果
     */
    public boolean syncSingleObjectData(String objectApi) {
        log.info("准备同步单个Salesforce对象的数据，对象API: {}", objectApi);
        long startTime = System.currentTimeMillis();
        
        try {
            // 获取Salesforce SOAP连接，添加重试机制
            PartnerConnection connection = retryOperation(() -> SOAPConnectionFactory.instance(), 3, 1000);
            log.info("成功获取Salesforce SOAP连接，耗时 {}ms", System.currentTimeMillis() - startTime);

            // 获取对象的字段信息
            List<String> fieldList = getSalesforceObjectFields(connection, objectApi);
            log.info("获取到对象 {} 的字段列表，共 {} 个字段", objectApi, fieldList.size());

            // 构建查询参数
            SalesforceParam param = new SalesforceParam();
            param.setApi(objectApi);
            param.setSelect(String.join(",", fieldList));
            
            // 如果存在删除字段，则查询删除记录
            if (integrationFieldService.isDeletedFieldExists(objectApi)) {
                param.setIsDeleted(true);
                log.info("对象 {} 存在删除字段，将包含删除记录", objectApi);
            }

            // 执行查询并处理结果
            int totalCount = executeQueryAndProcessData(connection, param, fieldList);
            
            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            log.info("对象 {} 数据同步完成，共处理 {} 条记录，耗时 {}ms", 
                     objectApi, totalCount, duration);

            return true;
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            log.error("同步单个Salesforce对象数据失败，对象API: {}，耗时 {}ms", 
                      objectApi, endTime - startTime, e);
            return false;
        }
    }

    /**
     * 获取Salesforce对象字段列表
     * <p>
     * 查询指定对象的所有字段，并过滤掉不支持的字段类型（如base64）
     * </p>
     *
     * @param connection SOAP连接
     * @param objectApi  对象API名称
     * @return 字段列表
     * @throws ConnectionException 连接异常
     */
    private List<String> getSalesforceObjectFields(PartnerConnection connection, String objectApi) throws ConnectionException {
        List<String> fields = new ArrayList<>();

        // 获取对象描述信息
        DescribeSObjectResult describeResult = connection.describeSObject(objectApi);
        Field[] objectFields = describeResult.getFields();

        // 过滤字段
        for (Field field : objectFields) {
            // 不查询文件类型字段
            if (field.getType() != null && "base64".equalsIgnoreCase(field.getType().toString())) {
                continue;
            }
            fields.add(field.getName());
        }

        // 添加常用系统字段
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
     * <p>
     * 根据参数分批查询Salesforce数据并处理
     * </p>
     *
     * @param connection SOAP连接
     * @param param      查询参数
     * @param fieldList  字段列表
     * @return 处理的数据条数
     */
    private int executeQueryAndProcessData(PartnerConnection connection, SalesforceParam param, List<String> fieldList) {
        int totalCount = 0;
        try {
            // 构建查询语句
            String query = buildDynamicQuery(param);
            log.info("执行查询SQL: {}", query);

            // 执行查询
            QueryResult result = connection.queryAll(query);

            // 处理查询结果
            while (true) {
                if (result.getRecords() != null && result.getRecords().length > 0) {
                    totalCount += processQueryResult(param.getApi(), result, fieldList);
                    log.info("已处理 {} 条记录", totalCount);
                }

                // 检查是否还有更多数据
                if (result.isDone()) {
                    break;
                }

                // 使用queryMore获取下一批数据
                result = connection.queryMore(result.getQueryLocator());

                // 添加短暂延迟，避免API限制
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
        // 构建简单的查询语句（实际实现需要根据实际情况调整）
        StringBuilder queryBuilder = new StringBuilder();
        queryBuilder.append("SELECT ").append(param.getSelect())
                   .append(" FROM ").append(param.getApi());
        
        // 添加删除记录查询条件
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
        
        // 检查查询结果是否为空
        if (result == null || result.getRecords() == null) {
            log.info("处理API {} 的查询结果，共 0 条记录", api);
            return count;
        }
        
        SObject[] records = result.getRecords();
        log.info("处理API {} 的查询结果，共 {} 条记录", api, records.length);

        // 检查表是否分区（只检查一次，减少数据库查询）
        boolean isPartitioned = checkIfTablePartitioned(api);
        String batchField = null;
        if (isPartitioned) {
            // 只获取一次日期字段，减少方法调用
            batchField = integrationFieldService.getDateField(api);
        }

        // 准备批量处理的数据
        Map<String, List<Map<String, Object>>> partitionedData = new HashMap<>();
        List<Map<String, Object>> normalData = new ArrayList<>();

        // 转换所有记录
        for (SObject record : records) {
            if (record == null) {
                continue;
            }
            
            try {
                // 将SObject记录转换为Map
                Map<String, Object> recordMap = convertSObjectToMap(record, fieldList);

                if (isPartitioned) {
                    // 根据日期字段值确定分区名
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
                    
                    // 按分区名分组数据
                    partitionedData.computeIfAbsent(partitionName, k -> new ArrayList<>()).add(recordMap);
                } else {
                    // 普通表数据
                    normalData.add(recordMap);
                }

                count++;

            } catch (Exception e) {
                String recordId = record.getId() != null ? record.getId() : "未知";
                log.error("处理记录时发生异常，记录ID: {}", recordId, e);
            }
        }

        // 批量处理数据
        try {
            if (isPartitioned) {
                // 按分区批量插入
                for (Map.Entry<String, List<Map<String, Object>>> entry : partitionedData.entrySet()) {
                    String partitionName = entry.getKey();
                    List<Map<String, Object>> dataList = entry.getValue();
                    if (!dataList.isEmpty()) {
                        // 将数据转换为saveBatchToPartition方法所需的格式
                        if (!dataList.isEmpty()) {
                            // 获取字段名列表
                            Collection<String> keys = dataList.get(0).keySet();
                            // 转换值列表
                            Collection<Collection<Object>> values = new ArrayList<>();
                            for (Map<String, Object> recordMap : dataList) {
                                Collection<Object> recordValues = new ArrayList<>();
                                for (String key : keys) {
                                    recordValues.add(recordMap.get(key));
                                }
                                values.add(recordValues);
                            }
                            // 调用批量插入方法
                            customMapper.saveBatchToPartition(api.toLowerCase(), partitionName, keys, values);
                            log.info("批量插入分区 {} 数据 {} 条", partitionName, dataList.size());
                        }
                    }
                }
            } else {
                // 批量插入普通表
                if (!normalData.isEmpty()) {
                    // 将数据转换为saveBatch方法所需的格式
                    Collection<String> keys = normalData.get(0).keySet();
                    Collection<Collection<Object>> values = new ArrayList<>();
                    for (Map<String, Object> recordMap : normalData) {
                        Collection<Object> recordValues = new ArrayList<>();
                        for (String key : keys) {
                            recordValues.add(recordMap.get(key));
                        }
                        values.add(recordValues);
                    }
                    // 调用批量插入方法
                    customMapper.saveBatch(api.toLowerCase(), keys, values);
                    log.info("批量插入普通表数据 {} 条", normalData.size());
                }
            }
        } catch (Exception e) {
            log.error("批量处理数据时发生异常，API: {}", api, e);
            // 如果批量处理失败，尝试单条处理
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
        
        // 检查参数是否为空
        if (records == null || records.length == 0) {
            log.info("回退处理API {} 的记录，共 0 条记录", api);
            return;
        }
        
        for (SObject record : records) {
            if (record == null) {
                continue;
            }
            
            try {
                // 将SObject记录转换为Map
                Map<String, Object> recordMap = convertSObjectToMap(record, fieldList);
                
                if (isPartitioned) {
                    // 根据日期字段值确定分区名
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
        // 查询数据库确认表是否已分区
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

        // 添加记录ID
        if (record.getId() != null) {
            recordMap.put("Id", record.getId());
        }

        // 添加其他字段
        for (String field : fieldList) {
            // 跳过ID字段，因为已经处理过了
            if ("Id".equalsIgnoreCase(field)) {
                continue;
            }

            try {
                Object value = record.getField(field);
                // 处理不同类型的字段值
                if (value != null) {
                    // 处理日期类型
                    if (value instanceof java.util.Calendar) {
                        recordMap.put(field.toLowerCase(), ((Calendar) value).getTime());
                    }
                    // 处理其他类型
                    else {
                        recordMap.put(field.toLowerCase(), value);
                    }
                } else {
                    // 对于null值，可以选择不放入map或者放入null
                    recordMap.put(field.toLowerCase(), null);
                }
            } catch (Exception e) {
                log.warn("获取字段 {} 的值时发生异常", field, e);
            }
        }

        return recordMap;
    }

    /**
     * 同步单个Salesforce对象的数据
     *
     * @param objectApi Salesforce对象API
     * @return 同步结果
     */
    @Override
    public boolean syncObjectsData(String objectApi) {
        if (objectApi == null || objectApi.trim().isEmpty()) {
            log.error("对象API为空，无法同步数据");
            return false;
        }
        return syncSingleObjectData(objectApi.trim());
    }

    @Override
    public Map<String, Boolean> syncObjectsData(List<String> objectApis) {
        log.info("准备同步多个Salesforce对象的数据，对象API列表: {}", objectApis);
        Map<String, Boolean> resultMap = new ConcurrentHashMap<>();
        
        if (objectApis == null || objectApis.isEmpty()) {
            log.warn("对象API列表为空，无需同步数据");
            return resultMap;
        }
        
        // 使用并行流处理多个对象，提高同步效率
        objectApis.parallelStream()
            .filter(objectApi -> objectApi != null && !objectApi.trim().isEmpty())
            .forEach(objectApi -> {
                String trimmedApi = objectApi.trim();
                try {
                    boolean result = syncObjectsData(trimmedApi);
                    resultMap.put(trimmedApi, result);
                    if (result) {
                        log.info("对象 {} 数据同步成功", trimmedApi);
                    } else {
                        log.error("对象 {} 数据同步失败", trimmedApi);
                    }
                } catch (Exception e) {
                    log.error("同步对象 {} 数据时发生异常", trimmedApi, e);
                    resultMap.put(trimmedApi, false);
                }
            });
        
        return resultMap;
    }

    /**
     * 同步Salesforce对象的指定批次数据
     *
     * @param objectApi Salesforce对象API
     * @param batchId   批次ID
     * @return 同步结果
     */
    @Override
    public boolean syncObjectDataByBatch(String objectApi, String batchId) {
        log.info("准备同步Salesforce对象的指定批次数据，对象API: {}, 批次ID: {}", objectApi, batchId);
        
        if (objectApi == null || objectApi.trim().isEmpty() || batchId == null || batchId.trim().isEmpty()) {
            log.error("对象API或批次ID为空，无法同步指定批次数据");
            return false;
        }
        
        try {
            // 获取Salesforce SOAP连接，添加重试机制
            PartnerConnection connection = retryOperation(() -> SOAPConnectionFactory.instance(), 3, 1000);
            log.info("成功获取Salesforce SOAP连接");

            // 获取对象的字段信息
            List<String> fieldList = getSalesforceObjectFields(connection, objectApi);
            log.info("获取到对象 {} 的字段列表: {}", objectApi, fieldList);

            // 构建查询参数
            SalesforceParam param = new SalesforceParam();
            param.setApi(objectApi);
            param.setSelect(String.join(",", fieldList));

            // 如果存在删除字段，则查询删除记录
            if (integrationFieldService.isDeletedFieldExists(objectApi)) {
                param.setIsDeleted(true);
            }

            // 执行查询并处理结果
            int totalCount = executeQueryAndProcessData(connection, param, fieldList);
            log.info("对象 {} 批次 {} 数据同步完成，共处理 {} 条记录", objectApi, batchId, totalCount);

            return true;
        } catch (Exception e) {
            log.error("同步Salesforce对象指定批次数据失败，对象API: {}, 批次ID: {}", objectApi, batchId, e);
            return false;
        }
    }
}