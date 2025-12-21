package org.dromara.salesforce.service.Impl;

import com.alibaba.fastjson.JSON;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.client.model.ExecuteResult;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sforce.soap.partner.*;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.dromara.salesforce.config.SalesforceSingletonConfig;
import org.dromara.salesforce.constant.SalesforceConstant;
import org.dromara.salesforce.domain.bo.*;
import org.dromara.salesforce.domain.param.SalesforceIncrementParam;
import org.dromara.salesforce.domain.param.SalesforceParam;
import org.dromara.salesforce.domain.param.SalesforceStockParam;
import org.dromara.salesforce.factory.SOAPConnectionFactory;
import org.dromara.salesforce.mapper.CustomMapper;
import org.dromara.salesforce.service.*;
import org.dromara.salesforce.util.ConvertUtil;
import org.dromara.salesforce.util.DataUtil;
import org.dromara.salesforce.util.SqlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Salesforce对象同步服务实现类
 * <p>
 * 该服务用于同步Salesforce所有对象的元数据到本地数据库
 * 1. 获取源ORG的connection
 * 2. 获取EntityDefinition的所有字段
 * 3. 拉取EntityDefinition的所有数据
 * </p>
 *
 * @author lingma
 * @since 1.0.0
 */
@Slf4j
@Service
public class SobjectSyncServiceImpl implements SobjectSyncService {

    @Autowired
    private CustomMapper customMapper;

    @Autowired
    private IMigrationFieldService migrationFieldService;

    @Autowired
    private IMigrationObjectService migrationObjectService;

    @Autowired
    private IMigrationPicklistService migrationPicklistService;

    @Autowired
    private IMigrationFilterLookupService migrationFilterLookupService;

    @Autowired
    private IMigrationConfigService migrationConfigService;

    @Autowired
    private IMigrationBatchService migrationBatchService;



    @Override
    public ExecuteResult syncObjects(JobArgs jobArgs) {
        log.info("开始执行Salesforce对象同步任务");
        String value = String.valueOf(jobArgs.getJobParams());

        try {
            // 获取源ORG连接
            PartnerConnection connection = SOAPConnectionFactory.sourceInstance();
            log.info("成功获取源ORG连接");

            // 检查是否有指定API参数
            Set<String> apiList = null;
            if (StringUtils.isNotBlank(value)) {
                try {
                    // 尝试解析为SalesforceParam对象
                    SalesforceParam param = JSON.parseObject(value, SalesforceParam.class);

                    if (param != null && StringUtils.isNotBlank(param.getApi())) {
                        // 按逗号拆分API列表
                        String ids = param.getApi();
                        apiList = DataUtil.toApiSet(ids);
                    }
                } catch (Exception e) {
                    log.warn("解析job参数为SalesforceParam对象失败: {}", e.getMessage());
                }
            }

            if (apiList != null && !apiList.isEmpty()) {
                // 如果指定了API列表，则只同步这些API对象
                log.info("根据参数同步指定的Salesforce对象: {}", apiList);
                for (String api : apiList) {
                    if (StringUtils.isNotBlank(api)) {
                        try {
                            DescribeSObjectResult objDetail = connection.describeSObject(api.trim());
                            syncAllObjects(connection, new DescribeGlobalSObjectResult[]{convertToGlobalSObject(objDetail)});
                        } catch (ConnectionException e) {
                            log.error("获取对象 {} 的元数据失败: {}", api, e.getMessage());
                        }
                    }
                }
            } else {
                // 获取所有对象列表
                DescribeGlobalResult globalResult = connection.describeGlobal();
                DescribeGlobalSObjectResult[] sObjects = globalResult.getSobjects();
                log.info("获取到{}个Salesforce对象", sObjects.length);

                // 同步所有对象的元数据
                syncAllObjects(connection, sObjects);
            }

            log.info("Salesforce对象同步任务执行完成");
            return ExecuteResult.success("Salesforce对象同步成功");
        } catch (Exception e) {
            log.error("Salesforce对象同步任务执行失败", e);
            return ExecuteResult.failure("Salesforce对象同步失败: " + e.getMessage());
        }
    }

    @Override
    public int countSfNumByDateField(PartnerConnection connect, SalesforceParam param) {
        try {
            String field = migrationFieldService.getDateField(param.getApi());
            param.setDateField(field);
            Map<String, Object> map = Maps.newHashMap();
            map.put("param", param);
            String sql = SqlUtil.showSql("org.dromara.salesforce.mapper.SalesforceMapper.count", map);
            log.info("count sql: {}", sql);
            QueryResult queryResult = connect.queryAll(sql);
            SObject record = queryResult.getRecords()[0];

            return Integer.parseInt(record.getField("num").toString());

        } catch (ConnectionException e) {
            log.error("执行Salesforce查询存量总数时发生连接异常", e);
        } catch (Exception e) {
            log.error("执行Salesforce查询存量总数时发生未知异常", e);
        }
        return 0;
    }

    @Override
    public int countSfStockNum(PartnerConnection connect, SalesforceStockParam param) {
        try {
            Map<String, Object> map = Maps.newHashMap();
            map.put("param", param);
            String sql = SqlUtil.showSql("org.dromara.salesforce.mapper.SalesforceMapper.stockCount", map);
            log.info("count sql: {}", sql);
            QueryResult queryResult = connect.queryAll(sql);
            SObject record = queryResult.getRecords()[0];

            return Integer.parseInt(record.getField("num").toString());

        } catch (ConnectionException e) {
            log.error("执行Salesforce查询存量总数时发生连接异常", e);
        } catch (Exception e) {
            log.error("执行Salesforce查询存量总数时发生未知异常", e);
        }
        return 0;
    }

    @Override
    public int countSfIncrementNum(PartnerConnection connect, SalesforceIncrementParam param) {
        try {
            String updateField = migrationFieldService.getUpdateField(param.getApi());
            param.setDateField(updateField);
            Map<String, Object> map = Maps.newHashMap();
            map.put("param", param);
            String sql = SqlUtil.showSql("org.dromara.salesforce.mapper.SalesforceMapper.incrementCount", map);
            log.info("count sql: {}", sql);
            QueryResult queryResult = connect.queryAll(sql);
            SObject record = queryResult.getRecords()[0];

            return Integer.parseInt(record.getField("num").toString());

        } catch (ConnectionException e) {
            log.error("执行Salesforce查询增量总数时发生连接异常", e);
        } catch (Exception e) {
            log.error("执行Salesforce查询增量总数时发生未知异常", e);
        }
        return 0;
    }

    /**
     * 同步所有对象的表结构
     *
     * @param connection 连接
     * @param sObjects 对象列表
     * @throws ConnectionException 连接异常
     */
    private void syncAllObjects(PartnerConnection connection, DescribeGlobalSObjectResult[] sObjects) throws ConnectionException {

        for (DescribeGlobalSObjectResult sObj : sObjects) {
            try {
                // 获取对象详细信息
                DescribeSObjectResult objDetail = connection.describeSObject(sObj.getName());

                // 保存对象元数据到MigrationObject表
                MigrationObjectBo objectBo = buildObjectMetadata(objDetail);

                // 检查buildObjectMetadata是否成功
                if (objectBo == null) {
                    log.error("构建对象 {} 的元数据失败", sObj.getName());
                    continue;
                }

                // 保存对象字段信息到MigrationField表
                List<MigrationFieldBo> migrationFieldBos = saveObjectFieldsToMigrationField(objDetail);

                // 检查对象数据量
                int objectNum = isLargeObject(connection, sObj.getName());

                // 更新MigrationObject表中的日期和二进制字段信息
                updateMigrationObjectFields(sObj.getName(), objectNum);

                // 统一分区表判断阈值为100万
                if (objectNum > 1000000) {
                    log.info("对象 {} 是大数据量对象，数据量大于一百万，创建分区表", sObj.getName());
                    createPartitionTable(objectBo, migrationFieldBos);
                } else {
                    log.info("对象 {} 是普通对象，数据量少于一百万，创建正常表", sObj.getName());
                    createTable(objectBo, migrationFieldBos);
                }

            } catch (Exception e) {
                log.error("处理对象 {} 时出错: {}", sObj.getName(), e.getMessage(), e);
            }
        }

    }




    /**
     * 更新MigrationObject表中的字段信息
     *
     * @param objectApi 对象API名称
     */
    private void updateMigrationObjectFields(String objectApi, int objectNum) {
        try {
            String dateField = migrationFieldService.getDateField(objectApi);
            String blodField = migrationFieldService.getBlodField(objectApi);

            MigrationObjectBo objectBo = new MigrationObjectBo();
            objectBo.setApi(objectApi);
            objectBo.setBatchField(dateField);
            objectBo.setBlobField(blodField);
            objectBo.setTotalRows(objectNum);
            // 统一分区判断阈值为100万
            objectBo.setIsPartitioned(objectNum > 1000000);
            migrationObjectService.updateByApiName(objectBo);
        } catch (Exception e) {
            log.error("更新对象 {} 的字段信息时出错: {}", objectApi, e.getMessage(), e);
        }
    }

    /**
     * 保存对象字段信息到MigrationField表
     *
     * @param objDetail 对象详情
     */
    private List<MigrationFieldBo> saveObjectFieldsToMigrationField(DescribeSObjectResult objDetail) {
        String objectApi = objDetail.getName();

        List<MigrationFieldBo> migrationFields = Lists.newArrayList();

        List<MigrationPicklistBo> migrationPicklists = Lists.newArrayList();

        List<MigrationFilterLookupBo> migrationFilterLookups = Lists.newArrayList();

        for (Field field : objDetail.getFields()) {
            try {
                MigrationFieldBo fieldBo = new MigrationFieldBo();
                fieldBo.setApi(objectApi);
                fieldBo.setField(field.getName());
                fieldBo.setLabel(field.getLabel());
                fieldBo.setIsCreateable(field.isCreateable());
                fieldBo.setIsNillable(field.isNillable());
                fieldBo.setIsUpdateable(field.isUpdateable());
                fieldBo.setIsDefaultedOnCreate(field.isDefaultedOnCreate());
                fieldBo.setIsUnique(field.isUnique());
                fieldBo.setIsFilterable(field.isFilterable());
                fieldBo.setIsSortable(field.isSortable());
                fieldBo.setIsAggregatable(field.isAggregatable());
                fieldBo.setIsGroupable(field.isGroupable());
                fieldBo.setIsPolymorphicForeignKey(field.isPolymorphicForeignKey());
                fieldBo.setPolymorphicForeignField(String.join("_",field.getName(),"type") );
                fieldBo.setIsExternalId(field.isExternalId());
                fieldBo.setIsCustom(field.isCustom());
                fieldBo.setIsCalculated(field.isCalculated());
                fieldBo.setIsAutoNumber(field.isAutoNumber());
                fieldBo.setIsCaseSensitive(field.isCaseSensitive());
                fieldBo.setIsEncrypted(field.isEncrypted());
                fieldBo.setIsHtmlFormatted(field.isHtmlFormatted());
                fieldBo.setIsIdLookup(field.isIdLookup());
                fieldBo.setIsPermissionable(field.isPermissionable());
                fieldBo.setIsRestrictedPicklist(field.isRestrictedPicklist());
                fieldBo.setIsRestrictedDelete(field.isRestrictedDelete());
                fieldBo.setIsWriteRequiresMasterRead(field.isWriteRequiresMasterRead());
                fieldBo.setFieldDataType(field.getType() != null ? field.getType().toString() : null);
                fieldBo.setFieldLength(field.getLength());
                fieldBo.setFieldPrecision(field.getPrecision());
                fieldBo.setFieldScale(field.getScale());
                fieldBo.setFieldByteLength(field.getByteLength());
                fieldBo.setDefaultValue(field.getDefaultValueFormula());
                fieldBo.setCalculatedFormula(field.getCalculatedFormula());
                fieldBo.setInlineHelpText(field.getInlineHelpText());
                fieldBo.setRelationshipName(field.getRelationshipName());
                fieldBo.setRelationshipOrder(field.getRelationshipOrder());
                fieldBo.setReferenceTargetField(field.getReferenceTo() != null && field.getReferenceTo().length > 0 ? field.getReferenceTo()[0] : null);
                if ("picklist".equals(field.getType().toString())){
                    PicklistEntry[] picklistValues = field.getPicklistValues();
                    if (ArrayUtils.isNotEmpty(picklistValues)) {
                        for (PicklistEntry picklistValue : picklistValues) {
                            MigrationPicklistBo picklistBo = new MigrationPicklistBo();
                            picklistBo.setApi(objectApi);
                            picklistBo.setField(field.getName());
                            picklistBo.setLabel(picklistValue.getLabel());
                            picklistBo.setValue(picklistValue.getValue());
                            picklistBo.setActive(picklistValue.isActive());
                            picklistBo.setDefaultValue(picklistValue.isDefaultValue());
                            migrationPicklists.add(picklistBo);
                        }
                    }
                }
                if ("reference".equals(field.getType().toString())) {
                    FilteredLookupInfo filteredLookupInfo = field.getFilteredLookupInfo();

                    if (filteredLookupInfo != null) {
                        MigrationFilterLookupBo filterLookupBo = new MigrationFilterLookupBo();
                        filterLookupBo.setApi(objectApi);
                        filterLookupBo.setField(field.getName());

                        // 获取控制字段
                        String[] controllingFields = filteredLookupInfo.getControllingFields();
                        if (controllingFields != null) {
                            filterLookupBo.setControllingField(String.join(",", controllingFields));
                        } else {
                            filterLookupBo.setControllingField(null);
                        }

                        filterLookupBo.setDependent(filteredLookupInfo.getDependent());

                        migrationFilterLookups.add(filterLookupBo);
                    }
                }
                migrationFields.add(fieldBo);

            } catch (Exception e) {
                log.error("保存对象 {} 的字段 {} 信息时出错: {}", objectApi, field.getName(), e.getMessage());
            }
        }

        migrationFieldService.insertBatch(migrationFields);
        migrationPicklistService.insertBatch(migrationPicklists);
        migrationFilterLookupService.insertBatch(migrationFilterLookups);

        return migrationFields;
    }

    /**
     * 构建对象元数据
     *
     * @param objDetail 对象详情
     */
    private MigrationObjectBo buildObjectMetadata(DescribeSObjectResult objDetail) {
        try {
            MigrationObjectBo objectBo = new MigrationObjectBo();
            objectBo.setApi(objDetail.getName());
            objectBo.setLabel(objDetail.getLabel());
            objectBo.setKeyPrefix(objDetail.getKeyPrefix());
            objectBo.setIsCustom(objDetail.isCustom());
            objectBo.setIsEditable(objDetail.isCreateable());
            objectBo.setIsWork(true); // 默认启用
            objectBo.setIsUpdate(true); // 默认允许更新
            objectBo.setIsCustomsetting(objDetail.isCustomSetting());
            objectBo.setLastSyncDate(new Date());
            // 保存到数据库
            migrationObjectService.insertByBo(objectBo);
            log.info("保存对象 {} 元数据到MigrationObject表", objDetail.getName());
            return objectBo;
        } catch (Exception e) {
            log.error("保存对象 {} 元数据时出错: {}", objDetail.getName(), e.getMessage());
            return null;
        }
    }


    /**
     * 添加字段定义
     *
     * @param fields 字段列表
     * @param name   字段名
     * @param type   字段类型
     * @param comment 字段注释
     */
    private void addFieldDefinition(List<Map<String, Object>> fields, String name, String type, String comment) {
        Map<String, Object> field = new HashMap<>();
        field.put("name", name);
        field.put("type", type);
        field.put("comment", comment);
        fields.add(field);
    }


    /**
     * 创建分区表结构
     * <p>
     * 根据MigrationObject中的表基础信息和MigrationField中的字段信息创建分区表结构。
     * 通过CustomMapper的createTable方法实现表的创建。
     * </p>
     */
    private void createPartitionTable(MigrationObjectBo objectBo, List<MigrationFieldBo> migrationFieldBos) {
        try {
            // 检查表是否存在
            if (StringUtils.isNotEmpty(customMapper.checkTable(objectBo.getApi()))) {
                log.info("表{}已存在，跳过创建表", objectBo.getApi());
                return;
            }

            // 构建表字段定义
            List<Map<String, Object>> fieldDefinitions = new ArrayList<>();
            // 字段列表
            List<String> fields = Lists.newArrayList();

            // 添加其他字段
            for (MigrationFieldBo field : migrationFieldBos) {
                Map<String, Object> fieldDef = new HashMap<>();
                fieldDef.put("name", field.getField());
                fieldDef.put("type", ConvertUtil.convertTypeSFToLocal(field));
                fieldDef.put("comment", field.getLabel() != null ? field.getLabel().replaceAll("'", "\\\\'") : "");
                fieldDefinitions.add(fieldDef);

                fields.add(field.getField());

                // 如果字段类型是base64，新增文件路径、是否下载、是否上传三个字段
                if ("base64".equalsIgnoreCase(field.getFieldDataType())) {
                    Map<String, Object> filePathField = new HashMap<>();
                    filePathField.put("name", "file_path");
                    filePathField.put("type", "text");
                    filePathField.put("comment", (field.getLabel() != null ? field.getLabel() : field.getField()) + "文件路径");
                    fieldDefinitions.add(filePathField);

                    Map<String, Object> isDownloadField = new HashMap<>();
                    isDownloadField.put("name", "is_download");
                    isDownloadField.put("type", "tinyint(1) DEFAULT 0");
                    isDownloadField.put("comment", (field.getLabel() != null ? field.getLabel() : field.getField()) + "是否下载");
                    fieldDefinitions.add(isDownloadField);

                    Map<String, Object> isUploadField = new HashMap<>();
                    isUploadField.put("name", "is_upload");
                    isUploadField.put("type", "tinyint(1) DEFAULT 0");
                    isUploadField.put("comment", (field.getLabel() != null ? field.getLabel() : field.getField()) + "是否上传");
                    fieldDefinitions.add(isUploadField);
                }

                // 处理多态外键字段
                if (StringUtils.isNotBlank(field.getPolymorphicForeignField())) {
                    Map<String, Object> polymorphicField = new HashMap<>();
                    polymorphicField.put("name", field.getPolymorphicForeignField());
                    polymorphicField.put("type", "varchar(255)");
                    polymorphicField.put("comment", (field.getLabel() != null ? field.getLabel() : field.getField()) + "关联对象");
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
            Map<String, Object> isUpdateField = new HashMap<>();
            isUpdateField.put("name", "is_update");
            isUpdateField.put("type", "tinyint(1) DEFAULT 0");
            isUpdateField.put("comment", "是否更新");
            fieldDefinitions.add(isUpdateField);

            // 添加fail_reason字段
            Map<String, Object> failReasonField = new HashMap<>();
            failReasonField.put("name", "fail_reason");
            failReasonField.put("type", "text");
            failReasonField.put("comment", "失败原因");
            fieldDefinitions.add(failReasonField);

            // 构建索引定义
            List<Map<String, Object>> indexDefinitions = new ArrayList<>();
            for (String tableIndex : SalesforceConstant.TABLE_INDEX) {
                if (fields.contains(tableIndex)) {
                    Map<String, Object> indexMap = Maps.newHashMap();
                    indexMap.put("field", tableIndex);
                    indexMap.put("name", String.format("IDX_%s_%s", objectBo.getApi(), tableIndex));
                    indexDefinitions.add(indexMap);
                }
            }

            // 构建分区结构，按object上的时间字段构建分区
            List<Map<String, Object>> partitions = new ArrayList<>();

            // 添加默认分区，按照年份进行分区
            Calendar calendar = Calendar.getInstance();
            int currentYear = calendar.get(Calendar.YEAR);

            // 创建分区
            SalesforceSingletonConfig instance = SalesforceSingletonConfig.getInstance();
            String sourceOrgStartDate = instance.getSourceOrgStartDate();

            // 如果配置了开始时间，则从开始时间所在年份开始创建分区
            int startYear;
            if (StringUtils.isNotEmpty(sourceOrgStartDate)) {
                try {
                    Date startDate = java.sql.Date.valueOf(sourceOrgStartDate);
                    Calendar startCalendar = Calendar.getInstance();
                    startCalendar.setTime(startDate);
                    startYear = startCalendar.get(Calendar.YEAR);
                } catch (Exception e) {
                    log.warn("解析SOURCE_ORG_START_DATE配置失败，使用默认年份: {}", e.getMessage());
                    startYear = currentYear - SalesforceConstant.DEFAULT_PARTITION_YEARS;
                }
            } else {
                startYear = currentYear - SalesforceConstant.DEFAULT_PARTITION_YEARS;
            }

            // 创建从开始年份到当前年份的分区
            for (int year = startYear; year <= currentYear; year++) {
                Map<String, Object> partition = new HashMap<>();
                partition.put("name", "p" + year);
                partition.put("value", year + 1); // RANGE分区的值是小于该值的范围
                partitions.add(partition);
            }

            // 添加未来一年的分区
            Map<String, Object> futurePartition = new HashMap<>();
            futurePartition.put("name", "p" + (currentYear + 1));
            futurePartition.put("value", (currentYear + 2));
            partitions.add(futurePartition);

            // 创建RANGE分区表，按年份分区
            customMapper.createRangePartitionTable(
                objectBo.getApi(),
                objectBo.getLabel(),
                fieldDefinitions,
                indexDefinitions,
                objectBo.getBatchField(),
                partitions
            );

            log.info("成功创建分区表结构: {}", objectBo.getApi());
        } catch (Exception e) {
            log.error("创建对象 {} 的分区表结构时出错: {}", objectBo.getApi(), e.getMessage(), e);
        }
    }

    /**
     * 创建普通表结构（非分区表）
     * <p>
     * 根据MigrationObject中的表基础信息和MigrationField中的字段信息创建普通表结构。
     * 通过CustomMapper的createTable方法实现表的创建。
     * </p>
     *
     * @param objectBo          迁移对象信息
     * @param migrationFieldBos 迁移字段信息列表
     */
    private void createTable(MigrationObjectBo objectBo, List<MigrationFieldBo> migrationFieldBos) {
        try {
            // 检查表是否存在
            if (StringUtils.isNotEmpty(customMapper.checkTable(objectBo.getApi()))) {
                log.info("表{}已存在，跳过创建表", objectBo.getApi());
                return; // 表已存在，直接返回
            }

            // 构建表字段定义
            List<Map<String, Object>> fieldDefinitions = new ArrayList<>();
            // 字段列表
            List<String> fields = Lists.newArrayList();

            // 添加其他字段
            for (MigrationFieldBo field : migrationFieldBos) {
                Map<String, Object> fieldDef = new HashMap<>();
                fieldDef.put("name", field.getField());
                fieldDef.put("type", ConvertUtil.convertTypeSFToLocal(field));
                fieldDef.put("comment", field.getLabel() != null ? field.getLabel().replaceAll("'", "\\\\'") : "");
                fieldDefinitions.add(fieldDef);

                fields.add(field.getField());

                // 如果字段类型是base64，新增文件路径、是否下载、是否上传三个字段
                if ("base64".equalsIgnoreCase(field.getFieldDataType())) {
                    Map<String, Object> filePathField = new HashMap<>();
                    filePathField.put("name", "file_path");
                    filePathField.put("type", "text");
                    filePathField.put("comment", (field.getLabel() != null ? field.getLabel() : field.getField()) + "文件路径");
                    fieldDefinitions.add(filePathField);

                    Map<String, Object> isDownloadField = new HashMap<>();
                    isDownloadField.put("name", "is_download");
                    isDownloadField.put("type", "tinyint(1) DEFAULT 0");
                    isDownloadField.put("comment", (field.getLabel() != null ? field.getLabel() : field.getField()) + "是否下载");
                    fieldDefinitions.add(isDownloadField);

                    Map<String, Object> isUploadField = new HashMap<>();
                    isUploadField.put("name", "is_upload");
                    isUploadField.put("type", "tinyint(1) DEFAULT 0");
                    isUploadField.put("comment", (field.getLabel() != null ? field.getLabel() : field.getField()) + "是否上传");
                    fieldDefinitions.add(isUploadField);
                }

                // 处理多态外键字段
                if (StringUtils.isNotBlank(field.getPolymorphicForeignField())) {
                    Map<String, Object> polymorphicField = new HashMap<>();
                    polymorphicField.put("name", field.getPolymorphicForeignField());
                    polymorphicField.put("type", "varchar(255)");
                    polymorphicField.put("comment", (field.getLabel() != null ? field.getLabel() : field.getField()) + "关联对象");
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
            Map<String, Object> isUpdateField = new HashMap<>();
            isUpdateField.put("name", "is_update");
            isUpdateField.put("type", "tinyint(1) DEFAULT 0");
            isUpdateField.put("comment", "是否更新");
            fieldDefinitions.add(isUpdateField);

            // 添加fail_reason字段
            Map<String, Object> failReasonField = new HashMap<>();
            failReasonField.put("name", "fail_reason");
            failReasonField.put("type", "text");
            failReasonField.put("comment", "失败原因");
            fieldDefinitions.add(failReasonField);

            // 构建索引定义
            List<Map<String, Object>> indexDefinitions = new ArrayList<>();
            for (String tableIndex : SalesforceConstant.TABLE_INDEX) {
                if (fields.contains(tableIndex)) {
                    Map<String, Object> indexMap = Maps.newHashMap();
                    indexMap.put("field", tableIndex);
                    indexMap.put("name", String.format("IDX_%s_%s", objectBo.getApi(), tableIndex));
                    indexDefinitions.add(indexMap);
                }
            }

            // 创建表
            customMapper.createTable(objectBo.getApi(), objectBo.getLabel(), fieldDefinitions, indexDefinitions);

            log.info("成功创建表结构: {}", objectBo.getApi());
        } catch (Exception e) {
            log.error("创建对象 {} 的表结构时出错: {}", objectBo.getApi(), e.getMessage(), e);
        }
    }


    /**
     * 检查Salesforce对象的数据量是否超过一百万
     *
     * @param connection Salesforce连接
     * @param objectApi  对象API名称
     * @return 如果数据量超过一百万返回true，否则返回false
     */
    private int isLargeObject(PartnerConnection connection, String objectApi) {
        try {
            SalesforceSingletonConfig config = SalesforceSingletonConfig.getInstance();
            Date organizationCreatedDate;

            // 如果config中有配置的开始时间，则使用配置的值
            if (config.getSourceOrgStartDate() != null) {
                organizationCreatedDate = java.sql.Date.valueOf(config.getSourceOrgStartDate());
            } else {
                // 查询组织创建时间作为开始时间
                QueryResult queryResult = connection.queryAll("SELECT Id,CreatedDate FROM Organization ORDER BY CreatedDate ASC LIMIT 1 ");
                SObject record = queryResult.getRecords()[0];
                Date createdDate = (Date) record.getField("CreatedDate");
                // 将日期取整为本月的第一天的00:00
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(createdDate);
                calendar.set(Calendar.DAY_OF_MONTH, 1);
                calendar.set(Calendar.HOUR_OF_DAY, 0);
                calendar.set(Calendar.MINUTE, 0);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                organizationCreatedDate = calendar.getTime();
                // 更新配置
                migrationConfigService.updateConfigValueByKey("SOURCE_ORG_START_DATE", organizationCreatedDate.toString());
                config.refreshConfig();
            }

            // 构造查询参数
            SalesforceParam salesforceParam = new SalesforceParam();
            salesforceParam.setBeginDate(organizationCreatedDate);
            salesforceParam.setEndDate(new Date());
            salesforceParam.setApi(objectApi);
            salesforceParam.setDateField(migrationFieldService.getDateField(objectApi));

            // 查询对象数据总量
            int totalNum = countSfNumByDateField(connection, salesforceParam);

            // 判断数据量是否超过一百万
            return totalNum;
        } catch (Exception e) {
            log.error("检查对象 {} 是否为大对象时出错: {}", objectApi, e.getMessage());
            return 0; // 出错时默认不是大对象
        }
    }

    /**
     * 将DescribeSObjectResult转换为DescribeGlobalSObjectResult
     *
     * @param objDetail DescribeSObjectResult对象
     * @return DescribeGlobalSObjectResult对象
     */
    private DescribeGlobalSObjectResult convertToGlobalSObject(DescribeSObjectResult objDetail) {
        DescribeGlobalSObjectResult globalSObject = new DescribeGlobalSObjectResult();
        globalSObject.setName(objDetail.getName());
        globalSObject.setLabel(objDetail.getLabel());
        globalSObject.setKeyPrefix(objDetail.getKeyPrefix());
        globalSObject.setLabelPlural(objDetail.getLabelPlural());
        globalSObject.setCustom(objDetail.isCustom());
        globalSObject.setCreateable(objDetail.isCreateable());
        globalSObject.setUpdateable(objDetail.isUpdateable());
        globalSObject.setDeletable(objDetail.isDeletable());
        globalSObject.setQueryable(objDetail.isQueryable());
        globalSObject.setReplicateable(objDetail.isReplicateable());
        globalSObject.setRetrieveable(objDetail.isRetrieveable());
        globalSObject.setSearchable(objDetail.isSearchable());
        globalSObject.setUndeletable(objDetail.isUndeletable());
        globalSObject.setTriggerable(objDetail.isTriggerable());
        globalSObject.setMergeable(objDetail.isMergeable());
        globalSObject.setFeedEnabled(objDetail.isFeedEnabled());
        globalSObject.setDeprecatedAndHidden(objDetail.isDeprecatedAndHidden());
        return globalSObject;
    }
}
