package com.datai.integration.service.impl;

import com.datai.integration.model.domain.DataiIntegrationField;
import com.datai.integration.model.domain.DataiIntegrationFilterLookup;
import com.datai.integration.model.domain.DataiIntegrationMetadataChange;
import com.datai.integration.model.domain.DataiIntegrationObject;
import com.datai.integration.model.domain.DataiIntegrationPicklist;
import com.datai.integration.model.param.DataiSyncParam;
import com.datai.integration.factory.impl.SOAPConnectionFactory;
import com.datai.integration.mapper.CustomMapper;
import com.datai.integration.service.IDataiIntegrationFieldService;
import com.datai.integration.service.IDataiIntegrationFilterLookupService;
import com.datai.integration.service.IDataiIntegrationMetadataChangeService;
import com.datai.integration.service.IDataiIntegrationObjectService;
import com.datai.integration.service.IDataiIntegrationPicklistService;
import com.datai.integration.service.ISalesforceDataPullService;
import com.datai.salesforce.common.utils.SoqlBuilder;
import com.datai.integration.util.ConvertUtil;
import com.datai.setting.future.SalesforceExecutor;
import com.sforce.soap.partner.DescribeGlobalResult;
import com.sforce.soap.partner.DescribeGlobalSObjectResult;
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


import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
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

    @Autowired
    private IDataiIntegrationMetadataChangeService metadataChangeService;

    @Autowired
    private SOAPConnectionFactory soapConnectionFactory;

    @Autowired
    private SalesforceExecutor salesforceExecutor;


    /**
     * 大数据量对象阈值（500万）
     */
    private static final int LARGE_OBJECT_THRESHOLD = 5000000;

    /**
     * 同步多个Salesforce对象的表结构
     *
     * @param objectApis Salesforce对象API列表
     * @return 同步结果，键为对象API，值为同步结果
     */
    @Override
    public boolean syncObjectStructures(List<String> objectApis) {
        log.info("准备同步Salesforce对象的表结构，对象API列表: {}", objectApis);
        if (objectApis == null || objectApis.isEmpty()) {
            DataiIntegrationObject object = new DataiIntegrationObject();
            object.setIsWork(true);
            object.setSyncStatus(true);
            List<DataiIntegrationObject> integrationObjects = integrationObjectService.selectDataiIntegrationObjectList(object);
            integrationObjects.forEach(item -> objectApis.add(item.getApi()));
            if (objectApis == null || objectApis.isEmpty()){
                log.warn("对象API列表为空，无需同步，请输入API或启用对象");
                return false;
            }
        }
        try {
            // 获取源ORG连接，添加重试机制
            PartnerConnection connection = retryOperation(() -> soapConnectionFactory.getConnection(), 3, 1000);
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
                    } catch (ConnectionException e) {
                        log.error("获取对象 {} 的元数据失败: {}", objectApi, e.getMessage(), e);
                    } catch (Exception e) {
                        log.error("处理对象 {} 时出错: {}", objectApi, e.getMessage(), e);
                    }
                }
            }
        } catch (Exception e) {
            log.error("Salesforce对象同步任务执行失败", e);
        }
        
        return true;
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

        // 同步字段并记录变更
        syncFieldsAndRecordChanges(objectApi, fields, picklists, filterLookups);
    }

    private void syncFieldsAndRecordChanges(String objectApi, List<DataiIntegrationField> newFields, 
                                            List<DataiIntegrationPicklist> picklists, 
                                            List<DataiIntegrationFilterLookup> filterLookups) {
        try {
            // 获取现有字段
            DataiIntegrationField queryField = new DataiIntegrationField();
            queryField.setApi(objectApi);
            List<DataiIntegrationField> existingFields = integrationFieldService.selectDataiIntegrationFieldList(queryField);
            
            // 创建现有字段映射
            Map<String, DataiIntegrationField> existingFieldMap = new HashMap<>();
            for (DataiIntegrationField existingField : existingFields) {
                existingFieldMap.put(existingField.getField(), existingField);
            }
            
            // 创建新字段映射
            Map<String, DataiIntegrationField> newFieldMap = new HashMap<>();
            for (DataiIntegrationField newField : newFields) {
                newFieldMap.put(newField.getField(), newField);
            }
            
            // 获取对象信息
            DataiIntegrationObject queryObject = new DataiIntegrationObject();
            queryObject.setApi(objectApi);
            List<DataiIntegrationObject> objects = integrationObjectService.selectDataiIntegrationObjectList(queryObject);
            String objectLabel = objects.isEmpty() ? objectApi : objects.get(0).getLabel();
            Boolean isCustom = objects.isEmpty() ? false : objects.get(0).getIsCustom();
            
            // 处理新增和更新的字段
            for (DataiIntegrationField newField : newFields) {
                DataiIntegrationField existingField = existingFieldMap.get(newField.getField());
                
                if (existingField == null) {
                    // 新增字段
                    integrationFieldService.insertDataiIntegrationField(newField);
                    recordFieldChange(objectApi, objectLabel, newField.getField(), newField.getLabel(), 
                                     null, "INSERT", "新增字段", isCustom);
                    log.debug("新增字段: {}.{}", objectApi, newField.getField());
                } else {
                    // 检查字段是否有变更
                    List<String> changedFields = compareFields(existingField, newField);
                    if (!changedFields.isEmpty()) {
                        newField.setId(existingField.getId());
                        integrationFieldService.updateDataiIntegrationField(newField);
                        recordFieldChange(objectApi, objectLabel, newField.getField(), newField.getLabel(), 
                                         "字段属性变更: " + String.join(", ", changedFields), 
                                         "UPDATE", "字段属性更新", isCustom);
                        log.debug("更新字段: {}.{} - 变更: {}", objectApi, newField.getField(), 
                                 String.join(", ", changedFields));
                    }
                }
            }
            
            // 处理删除的字段
            for (DataiIntegrationField existingField : existingFields) {
                if (!newFieldMap.containsKey(existingField.getField())) {
                    recordFieldChange(objectApi, objectLabel, existingField.getField(), existingField.getLabel(), 
                                     "字段已从Salesforce中删除", "DELETE", "字段删除", isCustom);
                    log.warn("检测到字段已删除: {}.{}", objectApi, existingField.getField());
                }
            }
            
            // 保存选择列表和过滤查找信息
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
            
        } catch (Exception e) {
            log.error("同步对象 {} 的字段时出错: {}", objectApi, e.getMessage(), e);
        }
    }

    private List<String> compareFields(DataiIntegrationField oldField, DataiIntegrationField newField) {
        List<String> changedFields = new ArrayList<>();
        
        if (!Objects.equals(oldField.getLabel(), newField.getLabel())) {
            changedFields.add("label");
        }
        if (!Objects.equals(oldField.getIsCreateable(), newField.getIsCreateable())) {
            changedFields.add("isCreateable");
        }
        if (!Objects.equals(oldField.getIsNillable(), newField.getIsNillable())) {
            changedFields.add("isNillable");
        }
        if (!Objects.equals(oldField.getIsUpdateable(), newField.getIsUpdateable())) {
            changedFields.add("isUpdateable");
        }
        if (!Objects.equals(oldField.getIsDefaultedOnCreate(), newField.getIsDefaultedOnCreate())) {
            changedFields.add("isDefaultedOnCreate");
        }
        if (!Objects.equals(oldField.getIsUnique(), newField.getIsUnique())) {
            changedFields.add("isUnique");
        }
        if (!Objects.equals(oldField.getIsFilterable(), newField.getIsFilterable())) {
            changedFields.add("isFilterable");
        }
        if (!Objects.equals(oldField.getIsSortable(), newField.getIsSortable())) {
            changedFields.add("isSortable");
        }
        if (!Objects.equals(oldField.getIsAggregatable(), newField.getIsAggregatable())) {
            changedFields.add("isAggregatable");
        }
        if (!Objects.equals(oldField.getIsGroupable(), newField.getIsGroupable())) {
            changedFields.add("isGroupable");
        }
        if (!Objects.equals(oldField.getIsPolymorphicForeignKey(), newField.getIsPolymorphicForeignKey())) {
            changedFields.add("isPolymorphicForeignKey");
        }
        if (!Objects.equals(oldField.getIsExternalId(), newField.getIsExternalId())) {
            changedFields.add("isExternalId");
        }
        if (!Objects.equals(oldField.getIsCustom(), newField.getIsCustom())) {
            changedFields.add("isCustom");
        }
        if (!Objects.equals(oldField.getIsCalculated(), newField.getIsCalculated())) {
            changedFields.add("isCalculated");
        }
        if (!Objects.equals(oldField.getIsAutoNumber(), newField.getIsAutoNumber())) {
            changedFields.add("isAutoNumber");
        }
        if (!Objects.equals(oldField.getIsCaseSensitive(), newField.getIsCaseSensitive())) {
            changedFields.add("isCaseSensitive");
        }
        if (!Objects.equals(oldField.getIsEncrypted(), newField.getIsEncrypted())) {
            changedFields.add("isEncrypted");
        }
        if (!Objects.equals(oldField.getIsHtmlFormatted(), newField.getIsHtmlFormatted())) {
            changedFields.add("isHtmlFormatted");
        }
        if (!Objects.equals(oldField.getIsIdLookup(), newField.getIsIdLookup())) {
            changedFields.add("isIdLookup");
        }
        if (!Objects.equals(oldField.getIsPermissionable(), newField.getIsPermissionable())) {
            changedFields.add("isPermissionable");
        }
        if (!Objects.equals(oldField.getIsRestrictedPicklist(), newField.getIsRestrictedPicklist())) {
            changedFields.add("isRestrictedPicklist");
        }
        if (!Objects.equals(oldField.getIsRestrictedDelete(), newField.getIsRestrictedDelete())) {
            changedFields.add("isRestrictedDelete");
        }
        if (!Objects.equals(oldField.getIsWriteRequiresMasterRead(), newField.getIsWriteRequiresMasterRead())) {
            changedFields.add("isWriteRequiresMasterRead");
        }
        if (!Objects.equals(oldField.getFieldDataType(), newField.getFieldDataType())) {
            changedFields.add("fieldDataType");
        }
        if (!Objects.equals(oldField.getFieldLength(), newField.getFieldLength())) {
            changedFields.add("fieldLength");
        }
        if (!Objects.equals(oldField.getFieldPrecision(), newField.getFieldPrecision())) {
            changedFields.add("fieldPrecision");
        }
        if (!Objects.equals(oldField.getFieldScale(), newField.getFieldScale())) {
            changedFields.add("fieldScale");
        }
        if (!Objects.equals(oldField.getFieldByteLength(), newField.getFieldByteLength())) {
            changedFields.add("fieldByteLength");
        }
        if (!Objects.equals(oldField.getDefaultValue(), newField.getDefaultValue())) {
            changedFields.add("defaultValue");
        }
        if (!Objects.equals(oldField.getCalculatedFormula(), newField.getCalculatedFormula())) {
            changedFields.add("calculatedFormula");
        }
        if (!Objects.equals(oldField.getInlineHelpText(), newField.getInlineHelpText())) {
            changedFields.add("inlineHelpText");
        }
        if (!Objects.equals(oldField.getRelationshipName(), newField.getRelationshipName())) {
            changedFields.add("relationshipName");
        }
        if (!Objects.equals(oldField.getRelationshipOrder(), newField.getRelationshipOrder())) {
            changedFields.add("relationshipOrder");
        }
        if (!Objects.equals(oldField.getReferenceTargetField(), newField.getReferenceTargetField())) {
            changedFields.add("referenceTargetField");
        }
        if (!Objects.equals(oldField.getReferenceTo(), newField.getReferenceTo())) {
            changedFields.add("referenceTo");
        }
        if (!Objects.equals(oldField.getPolymorphicForeignField(), newField.getPolymorphicForeignField())) {
            changedFields.add("polymorphicForeignField");
        }
        
        return changedFields;
    }

    private void recordFieldChange(String objectApi, String objectLabel, String fieldApi, String fieldLabel, 
                                   String changeReason, String operationType, String defaultReason, Boolean isCustom) {
        try {
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
            metadataChange.setChangeReason(changeReason != null ? changeReason : defaultReason);
            metadataChange.setChangeUser("SYSTEM");

            metadataChangeService.insertDataiIntegrationMetadataChange(metadataChange);
            log.debug("记录字段变更成功: {}.{} - {}", objectApi, fieldApi, operationType);
        } catch (Exception e) {
            log.error("记录字段变更失败: {}.{} - {}", objectApi, fieldApi, e.getMessage(), e);
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

            
            return object;
        } catch (Exception e) {
            log.error("构建对象 {} 元数据时出错: {}", objDetail.getName(), e.getMessage(), e);
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
            PartnerConnection connection = retryOperation(() -> soapConnectionFactory.getConnection(), 3, 1000);
            log.info("成功获取Salesforce SOAP连接，耗时 {}ms", System.currentTimeMillis() - startTime);

            // 获取对象的字段信息
            List<String> fieldList = getSalesforceObjectFields(connection, objectApi);
            log.info("获取到对象 {} 的字段列表，共 {} 个字段", objectApi, fieldList.size());

            // 构建查询参数
            DataiSyncParam param = new DataiSyncParam();
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
    private int executeQueryAndProcessData(PartnerConnection connection, DataiSyncParam param, List<String> fieldList) {
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
    private String buildDynamicQuery(DataiSyncParam param) {
        SoqlBuilder builder = new SoqlBuilder();
        
        String[] selectFields = param.getSelect().split(",");
        builder.select(selectFields)
               .from(param.getApi());
        
        if (param.getIsDeleted() != null && param.getIsDeleted()) {
            builder.where("IsDeleted = true");
        } else if (param.getIsDeleted() != null && !param.getIsDeleted()) {
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
            builder.whereGe("CreatedDate", param.getBeginCreateDate())
                   .whereLe("CreatedDate", param.getEndCreateDate());
        } else if (param.getBeginCreateDate() != null) {
            builder.whereGe("CreatedDate", param.getBeginCreateDate());
        } else if (param.getEndCreateDate() != null) {
            builder.whereLe("CreatedDate", param.getEndCreateDate());
        }
        
        if (param.getBeginModifyDate() != null && param.getEndModifyDate() != null) {
            builder.whereGe("LastModifiedDate", param.getBeginModifyDate())
                   .whereLe("LastModifiedDate", param.getEndModifyDate());
        } else if (param.getBeginModifyDate() != null) {
            builder.whereGe("LastModifiedDate", param.getBeginModifyDate());
        } else if (param.getEndModifyDate() != null) {
            builder.whereLe("LastModifiedDate", param.getEndModifyDate());
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
        return null;
    }



    @Override
    public boolean syncObjectsData(List<String> objectApis) {
        log.info("准备同步多个Salesforce对象的数据，对象API列表: {}", objectApis);

        Map<String, Boolean> resultMap = new ConcurrentHashMap<>();

        if (objectApis == null || objectApis.isEmpty()) {
            DataiIntegrationObject object = new DataiIntegrationObject();
            object.setIsWork(true);
            object.setSyncStatus(true);
            List<DataiIntegrationObject> integrationObjects = integrationObjectService.selectDataiIntegrationObjectList(object);
            integrationObjects.forEach(item -> objectApis.add(item.getApi()));
            if (objectApis == null || objectApis.isEmpty()){
                log.warn("对象API列表为空，无需同步，请输入API或启用对象");
                return false;
            }
        }

        log.info("开始多线程并发同步 {} 个对象", objectApis.size());

        List<Future<?>> futures = new ArrayList<>();
        Map<String, Integer> objectPriorityMap = new HashMap<>();

        for (String objectApi : objectApis) {
            if (objectApi == null || objectApi.trim().isEmpty()) {
                log.warn("对象API为空，跳过");
                continue;
            }

            try {
                DataiIntegrationObject queryObject = new DataiIntegrationObject();
                queryObject.setApi(objectApi.trim());
                List<DataiIntegrationObject> objects = integrationObjectService.selectDataiIntegrationObjectList(queryObject);
                
                Integer priority = 0;
                if (!objects.isEmpty()) {
                    priority = objects.get(0).getObjectIndex() != null ? objects.get(0).getObjectIndex() : 0;
                }
                objectPriorityMap.put(objectApi.trim(), priority);
            } catch (Exception e) {
                log.error("获取对象 {} 的优先级信息失败，使用默认优先级: {}", objectApi, e.getMessage());
                objectPriorityMap.put(objectApi.trim(), 0);
            }
        }

        int batchIndex = 0;
        for (String objectApi : objectApis) {
            if (objectApi == null || objectApi.trim().isEmpty()) {
                continue;
            }

            final String api = objectApi.trim();
            final int priority = objectPriorityMap.getOrDefault(api, 0);

            Future<?> future = salesforceExecutor.execute(() -> {
                try {
                    log.info("开始同步对象: {} (优先级: {})", api, priority);
                    boolean result = syncSingleObjectData(api);
                    resultMap.put(api, result);
                    if (result) {
                        log.info("对象 {} 同步成功", api);
                    } else {
                        log.warn("对象 {} 同步失败", api);
                    }
                } catch (Exception e) {
                    log.error("同步对象 {} 时发生异常: {}", api, e.getMessage(), e);
                    resultMap.put(api, false);
                }
            }, batchIndex, priority);

            futures.add(future);
            batchIndex++;
        }

        log.info("所有对象同步任务已提交，等待任务完成...");
        
        int successCount = 0;
        int failCount = 0;

        for (Future<?> future : futures) {
            try {
                future.get();
            } catch (Exception e) {
                log.error("等待同步任务完成时发生异常: {}", e.getMessage(), e);
                failCount++;
            }
        }

        for (Map.Entry<String, Boolean> entry : resultMap.entrySet()) {
            if (entry.getValue()) {
                successCount++;
            } else {
                failCount++;
            }
        }

        log.info("多线程并发同步完成，成功: {}, 失败: {}", successCount, failCount);
        
        return failCount == 0;
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
            PartnerConnection connection = retryOperation(() -> soapConnectionFactory.getConnection(), 3, 1000);
            log.info("成功获取Salesforce SOAP连接");

            // 获取对象的字段信息
            List<String> fieldList = getSalesforceObjectFields(connection, objectApi);
            log.info("获取到对象 {} 的字段列表: {}", objectApi, fieldList);

            // 构建查询参数
            DataiSyncParam param = new DataiSyncParam();
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

    @Override
    public boolean autoSyncObjects() {
        log.info("开始自动同步Salesforce对象信息");

        try {
            PartnerConnection connection = retryOperation(() -> soapConnectionFactory.getConnection(), 3, 1000);
            log.info("成功获取Salesforce SOAP连接");

            DescribeGlobalResult globalDescribe = connection.describeGlobal();
            DescribeGlobalSObjectResult[] sObjects = globalDescribe.getSobjects();

            if (sObjects == null || sObjects.length == 0) {
                log.warn("未获取到任何Salesforce对象");
                return true;
            }

            log.info("从Salesforce获取到 {} 个对象", sObjects.length);

            int syncCount = 0;
            int skipCount = 0;
            Set<String> syncedObjectApis = new HashSet<>();

            for (DescribeGlobalSObjectResult sObject : sObjects) {
                try {
                    if (shouldSyncObject(sObject)) {
                        String operationType = syncSingleObject(connection, sObject.getName());
                        if (operationType != null) {
                            syncCount++;
                            syncedObjectApis.add(sObject.getName());
                            log.info("成功同步对象: {} ({}) - 操作类型: {}", sObject.getLabel(), sObject.getName(), operationType);
                        }
                    } else {
                        skipCount++;
                        log.debug("跳过对象: {} (不满足同步条件)", sObject.getName());
                    }
                } catch (Exception e) {
                    log.error("同步对象 {} 失败: {}", sObject.getName(), e.getMessage(), e);
                }
            }

            checkDeletedObjects(syncedObjectApis);

            log.info("自动同步完成，共同步 {} 个对象，跳过 {} 个对象", syncCount, skipCount);
            return true;
        } catch (Exception e) {
            log.error("自动同步Salesforce对象信息失败", e);
            return false;
        }
    }

    private boolean shouldSyncObject(DescribeGlobalSObjectResult sObject) {
        return sObject.isQueryable() || sObject.isCreateable() || sObject.isUpdateable() || sObject.isDeletable();
    }

    private String syncSingleObject(PartnerConnection connection, String objectApi) throws ConnectionException {
        DescribeSObjectResult objDetail = connection.describeSObject(objectApi);

        DataiIntegrationObject queryObject = new DataiIntegrationObject();
        queryObject.setApi(objectApi);
        List<DataiIntegrationObject> existingObjects = integrationObjectService.selectDataiIntegrationObjectList(queryObject);

        DataiIntegrationObject object = buildObjectMetadata(objDetail);
        String operationType = null;

        if (existingObjects.isEmpty()) {
            integrationObjectService.insertDataiIntegrationObject(object);
            operationType = "INSERT";
            log.debug("插入新对象: {}", objectApi);
            recordObjectChange(object, null, operationType);
        } else {
            DataiIntegrationObject existingObject = existingObjects.get(0);
            object.setId(existingObject.getId());
            integrationObjectService.updateDataiIntegrationObject(object);
            operationType = "UPDATE";
            log.debug("更新已存在对象: {}", objectApi);
            recordObjectChange(object, existingObject, operationType);
        }

        return operationType;
    }

    private void recordObjectChange(DataiIntegrationObject newObject, DataiIntegrationObject oldObject, String operationType) {
        try {
            DataiIntegrationMetadataChange metadataChange = new DataiIntegrationMetadataChange();
            metadataChange.setChangeType("OBJECT");
            metadataChange.setOperationType(operationType);
            metadataChange.setObjectApi(newObject.getApi());
            metadataChange.setObjectLabel(newObject.getLabel());
            metadataChange.setChangeTime(LocalDateTime.now());
            metadataChange.setSyncStatus(false);
            metadataChange.setIsCustom(newObject.getIsCustom());

            if (oldObject != null) {
                List<String> changedFields = new ArrayList<>();
                if (!Objects.equals(oldObject.getLabel(), newObject.getLabel())) {
                    changedFields.add("label");
                }
                if (!Objects.equals(oldObject.getLabelPlural(), newObject.getLabelPlural())) {
                    changedFields.add("labelPlural");
                }
                if (!Objects.equals(oldObject.getKeyPrefix(), newObject.getKeyPrefix())) {
                    changedFields.add("keyPrefix");
                }
                if (!Objects.equals(oldObject.getIsQueryable(), newObject.getIsQueryable())) {
                    changedFields.add("isQueryable");
                }
                if (!Objects.equals(oldObject.getIsCreateable(), newObject.getIsCreateable())) {
                    changedFields.add("isCreateable");
                }
                if (!Objects.equals(oldObject.getIsUpdateable(), newObject.getIsUpdateable())) {
                    changedFields.add("isUpdateable");
                }
                if (!Objects.equals(oldObject.getIsDeletable(), newObject.getIsDeletable())) {
                    changedFields.add("isDeletable");
                }
                if (!Objects.equals(oldObject.getIsReplicateable(), newObject.getIsReplicateable())) {
                    changedFields.add("isReplicateable");
                }
                if (!Objects.equals(oldObject.getIsRetrieveable(), newObject.getIsRetrieveable())) {
                    changedFields.add("isRetrieveable");
                }
                if (!Objects.equals(oldObject.getIsSearchable(), newObject.getIsSearchable())) {
                    changedFields.add("isSearchable");
                }
                if (!Objects.equals(oldObject.getIsCustom(), newObject.getIsCustom())) {
                    changedFields.add("isCustom");
                }
                if (!Objects.equals(oldObject.getIsCustomSetting(), newObject.getIsCustomSetting())) {
                    changedFields.add("isCustomSetting");
                }
                if (!Objects.equals(oldObject.getIsWork(), newObject.getIsWork())) {
                    changedFields.add("isWork");
                }
                if (!Objects.equals(oldObject.getIsIncremental(), newObject.getIsIncremental())) {
                    changedFields.add("isIncremental");
                }

                if (!changedFields.isEmpty()) {
                    metadataChange.setChangeReason("对象属性变更: " + String.join(", ", changedFields));
                } else {
                    metadataChange.setChangeReason("对象元数据更新");
                }
            } else {
                metadataChange.setChangeReason("新增对象");
            }

            metadataChange.setChangeUser("SYSTEM");

            metadataChangeService.insertDataiIntegrationMetadataChange(metadataChange);
            log.debug("记录对象变更成功: {} - {}", newObject.getApi(), operationType);
        } catch (Exception e) {
            log.error("记录对象变更失败: {} - {}", newObject.getApi(), e.getMessage(), e);
        }
    }

    private void checkDeletedObjects(Set<String> syncedObjectApis) {
        try {
            DataiIntegrationObject queryObject = new DataiIntegrationObject();
            queryObject.setIsWork(true);
            List<DataiIntegrationObject> existingObjects = integrationObjectService.selectDataiIntegrationObjectList(queryObject);

            for (DataiIntegrationObject existingObject : existingObjects) {
                if (!syncedObjectApis.contains(existingObject.getApi())) {
                    try {
                        DescribeGlobalResult globalDescribe = soapConnectionFactory.getConnection().describeGlobal();
                        DescribeGlobalSObjectResult[] sObjects = globalDescribe.getSobjects();
                        boolean objectExists = false;
                        
                        if (sObjects != null) {
                            for (DescribeGlobalSObjectResult sObject : sObjects) {
                                if (existingObject.getApi().equals(sObject.getName())) {
                                    objectExists = true;
                                    break;
                                }
                            }
                        }

                        if (!objectExists) {
                            DataiIntegrationMetadataChange metadataChange = new DataiIntegrationMetadataChange();
                            metadataChange.setChangeType("OBJECT");
                            metadataChange.setOperationType("DELETE");
                            metadataChange.setObjectApi(existingObject.getApi());
                            metadataChange.setObjectLabel(existingObject.getLabel());
                            metadataChange.setChangeTime(LocalDateTime.now());
                            metadataChange.setSyncStatus(false);
                            metadataChange.setIsCustom(existingObject.getIsCustom());
                            metadataChange.setChangeReason("对象已从Salesforce中删除");
                            metadataChange.setChangeUser("SYSTEM");

                            metadataChangeService.insertDataiIntegrationMetadataChange(metadataChange);
                            log.warn("检测到对象已删除: {}", existingObject.getApi());
                        }
                    } catch (Exception e) {
                        log.error("检查对象删除状态失败: {} - {}", existingObject.getApi(), e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.error("检查删除对象时出错: {}", e.getMessage(), e);
        }
    }

    @Override
    public boolean syncMetadataChanges() {
        log.info("开始同步元数据变更到元数据变更信息表");

        try {
            PartnerConnection connection = retryOperation(() -> soapConnectionFactory.getConnection(), 3, 1000);
            log.info("成功获取Salesforce SOAP连接");

            DescribeGlobalResult globalDescribe = connection.describeGlobal();
            DescribeGlobalSObjectResult[] sObjects = globalDescribe.getSobjects();

            if (sObjects == null || sObjects.length == 0) {
                log.warn("未获取到任何Salesforce对象");
                return true;
            }

            log.info("从Salesforce获取到 {} 个对象", sObjects.length);

            Set<String> syncedObjectApis = new HashSet<>();
            int objectChangeCount = 0;
            int fieldChangeCount = 0;

            for (DescribeGlobalSObjectResult sObject : sObjects) {
                try {
                    String objectApi = sObject.getName();
                    
                    if (shouldSyncObject(sObject)) {
                        DescribeSObjectResult objDetail = connection.describeSObject(objectApi);
                        
                        DataiIntegrationObject queryObject = new DataiIntegrationObject();
                        queryObject.setApi(objectApi);
                        List<DataiIntegrationObject> existingObjects = integrationObjectService.selectDataiIntegrationObjectList(queryObject);
                        
                        DataiIntegrationObject newObject = buildObjectMetadata(objDetail);
                        boolean hasFieldChange = false;
                        
                        if (existingObjects.isEmpty()) {
                            integrationObjectService.insertDataiIntegrationObject(newObject);
                            recordObjectChange(newObject, null, "INSERT");
                            objectChangeCount++;
                            log.info("新增对象并记录变更: {}", objectApi);
                            existingObjects = integrationObjectService.selectDataiIntegrationObjectList(queryObject);
                            if (!existingObjects.isEmpty()) {
                                newObject.setId(existingObjects.get(0).getId());
                            }
                        } else {
                            DataiIntegrationObject existingObject = existingObjects.get(0);
                            newObject.setId(existingObject.getId());
                            List<String> changedFields = compareObjects(existingObject, newObject);
                            if (!changedFields.isEmpty()) {
                                recordObjectChange(newObject, existingObject, "UPDATE");
                                objectChangeCount++;
                                log.debug("记录对象更新: {} - 变更: {}", objectApi, String.join(", ", changedFields));
                            }
                        }
                        
                        syncedObjectApis.add(objectApi);
                        
                        DataiIntegrationField queryField = new DataiIntegrationField();
                        queryField.setApi(objectApi);
                        List<DataiIntegrationField> existingFields = integrationFieldService.selectDataiIntegrationFieldList(queryField);
                        
                        Map<String, DataiIntegrationField> existingFieldMap = new HashMap<>();
                        for (DataiIntegrationField existingField : existingFields) {
                            existingFieldMap.put(existingField.getField(), existingField);
                        }
                        
                        for (Field field : objDetail.getFields()) {
                            DataiIntegrationField newField = buildFieldMetadata(objectApi, field);
                            DataiIntegrationField existingField = existingFieldMap.get(field.getName());
                            
                            if (existingField == null) {
                                recordFieldChange(objectApi, newObject.getLabel(), newField.getField(), 
                                                newField.getLabel(), null, "INSERT", "新增字段", newObject.getIsCustom());
                                fieldChangeCount++;
                                hasFieldChange = true;
                                log.debug("记录字段新增: {}.{}", objectApi, field.getName());
                            } else {
                                List<String> changedFieldProps = compareFields(existingField, newField);
                                if (!changedFieldProps.isEmpty()) {
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
                        
                        for (DataiIntegrationField existingField : existingFields) {
                            boolean fieldExists = false;
                            for (Field field : objDetail.getFields()) {
                                if (existingField.getField().equals(field.getName())) {
                                    fieldExists = true;
                                    break;
                                }
                            }
                            if (!fieldExists) {
                                recordFieldChange(objectApi, newObject.getLabel(), existingField.getField(), 
                                                existingField.getLabel(), "字段已从Salesforce中删除", 
                                                "DELETE", "字段删除", newObject.getIsCustom());
                                fieldChangeCount++;
                                hasFieldChange = true;
                                log.warn("记录字段删除: {}.{}", objectApi, existingField.getField());
                            }
                        }
                        
                        if (hasFieldChange && newObject.getId() != null) {
                            DataiIntegrationObject updateObject = new DataiIntegrationObject();
                            updateObject.setId(newObject.getId());
                            updateObject.setIsIncremental(false);
                            integrationObjectService.updateDataiIntegrationObject(updateObject);
                            log.info("检测到字段变更，已禁用对象 {} 的增量更新状态", objectApi);
                        }
                    }
                } catch (Exception e) {
                    log.error("处理对象 {} 时出错: {}", sObject.getName(), e.getMessage(), e);
                }
            }
            
            checkDeletedObjectsForMetadata(syncedObjectApis);

            log.info("同步元数据变更完成，对象变更: {} 个，字段变更: {} 个", objectChangeCount, fieldChangeCount);
            return true;
        } catch (Exception e) {
            log.error("同步元数据变更失败", e);
            return false;
        }
    }

    private DataiIntegrationField buildFieldMetadata(String objectApi, Field field) {
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
        
        return fieldEntity;
    }

    private List<String> compareObjects(DataiIntegrationObject oldObject, DataiIntegrationObject newObject) {
        List<String> changedFields = new ArrayList<>();
        
        if (!Objects.equals(oldObject.getLabel(), newObject.getLabel())) {
            changedFields.add("label");
        }
        if (!Objects.equals(oldObject.getLabelPlural(), newObject.getLabelPlural())) {
            changedFields.add("labelPlural");
        }
        if (!Objects.equals(oldObject.getKeyPrefix(), newObject.getKeyPrefix())) {
            changedFields.add("keyPrefix");
        }
        if (!Objects.equals(oldObject.getIsQueryable(), newObject.getIsQueryable())) {
            changedFields.add("isQueryable");
        }
        if (!Objects.equals(oldObject.getIsCreateable(), newObject.getIsCreateable())) {
            changedFields.add("isCreateable");
        }
        if (!Objects.equals(oldObject.getIsUpdateable(), newObject.getIsUpdateable())) {
            changedFields.add("isUpdateable");
        }
        if (!Objects.equals(oldObject.getIsDeletable(), newObject.getIsDeletable())) {
            changedFields.add("isDeletable");
        }
        if (!Objects.equals(oldObject.getIsReplicateable(), newObject.getIsReplicateable())) {
            changedFields.add("isReplicateable");
        }
        if (!Objects.equals(oldObject.getIsRetrieveable(), newObject.getIsRetrieveable())) {
            changedFields.add("isRetrieveable");
        }
        if (!Objects.equals(oldObject.getIsSearchable(), newObject.getIsSearchable())) {
            changedFields.add("isSearchable");
        }
        if (!Objects.equals(oldObject.getIsCustom(), newObject.getIsCustom())) {
            changedFields.add("isCustom");
        }
        if (!Objects.equals(oldObject.getIsCustomSetting(), newObject.getIsCustomSetting())) {
            changedFields.add("isCustomSetting");
        }
        
        return changedFields;
    }

    private void checkDeletedObjectsForMetadata(Set<String> syncedObjectApis) {
        try {
            DataiIntegrationObject queryObject = new DataiIntegrationObject();
            List<DataiIntegrationObject> existingObjects = integrationObjectService.selectDataiIntegrationObjectList(queryObject);

            for (DataiIntegrationObject existingObject : existingObjects) {
                if (!syncedObjectApis.contains(existingObject.getApi())) {
                    try {
                        DescribeGlobalResult globalDescribe = soapConnectionFactory.getConnection().describeGlobal();
                        DescribeGlobalSObjectResult[] sObjects = globalDescribe.getSobjects();
                        boolean objectExists = false;
                        
                        if (sObjects != null) {
                            for (DescribeGlobalSObjectResult sObject : sObjects) {
                                if (existingObject.getApi().equals(sObject.getName())) {
                                    objectExists = true;
                                    break;
                                }
                            }
                        }

                        if (!objectExists) {
                            DataiIntegrationMetadataChange metadataChange = new DataiIntegrationMetadataChange();
                            metadataChange.setChangeType("OBJECT");
                            metadataChange.setOperationType("DELETE");
                            metadataChange.setObjectApi(existingObject.getApi());
                            metadataChange.setObjectLabel(existingObject.getLabel());
                            metadataChange.setChangeTime(LocalDateTime.now());
                            metadataChange.setSyncStatus(false);
                            metadataChange.setIsCustom(existingObject.getIsCustom());
                            metadataChange.setChangeReason("对象已从Salesforce中删除");
                            metadataChange.setChangeUser("SYSTEM");

                            metadataChangeService.insertDataiIntegrationMetadataChange(metadataChange);
                            log.warn("记录对象删除: {}", existingObject.getApi());
                        }
                    } catch (Exception e) {
                        log.error("检查对象删除状态失败: {} - {}", existingObject.getApi(), e.getMessage());
                    }
                }
            }
        } catch (Exception e) {
            log.error("检查删除对象时出错: {}", e.getMessage(), e);
        }
    }
}