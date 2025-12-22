package com.datai.integration.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 对象字段信息对象 datai_integration_field
 * 
 * @author datai
 * @date 2025-12-22
 */
@Schema(description = "对象字段信息对象")
public class DataiIntegrationField extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 主键ID */
    @Schema(title = "主键ID")
    private Long id;

    /** 所属对象API */
    @Schema(title = "所属对象API")
    @Excel(name = "所属对象API")
    private String api;

    /** 字段API */
    @Schema(title = "字段API")
    @Excel(name = "字段API")
    private String field;

    /** 字段标签 */
    @Schema(title = "字段标签")
    @Excel(name = "字段标签")
    private String label;

    /** 是否可创建 */
    @Schema(title = "是否可创建")
    @Excel(name = "是否可创建")
    private Integer isCreateable;

    /** 是否为空 */
    @Schema(title = "是否为空")
    @Excel(name = "是否为空")
    private Integer isNillable;

    /** 是否可更新 */
    @Schema(title = "是否可更新")
    @Excel(name = "是否可更新")
    private Integer isUpdateable;

    /** 是否默认值 */
    @Schema(title = "是否默认值")
    @Excel(name = "是否默认值")
    private Integer isDefaultedOnCreate;

    /** 是否唯一 */
    @Schema(title = "是否唯一")
    @Excel(name = "是否唯一")
    private Integer isUnique;

    /** 是否可过滤 */
    @Schema(title = "是否可过滤")
    @Excel(name = "是否可过滤")
    private Integer isFilterable;

    /** 是否可排序 */
    @Schema(title = "是否可排序")
    @Excel(name = "是否可排序")
    private Integer isSortable;

    /** 是否可聚合 */
    @Schema(title = "是否可聚合")
    @Excel(name = "是否可聚合")
    private Integer isAggregatable;

    /** 是否可分组 */
    @Schema(title = "是否可分组")
    @Excel(name = "是否可分组")
    private Integer isGroupable;

    /** 是否多态外键 */
    @Schema(title = "是否多态外键")
    @Excel(name = "是否多态外键")
    private Integer isPolymorphicForeignKey;

    /** 是否外部ID */
    @Schema(title = "是否外部ID")
    @Excel(name = "是否外部ID")
    private Integer isExternalId;

    /** 是否自定义字段 */
    @Schema(title = "是否自定义字段")
    @Excel(name = "是否自定义字段")
    private Integer isCustom;

    /** 是否计算字段 */
    @Schema(title = "是否计算字段")
    @Excel(name = "是否计算字段")
    private Integer isCalculated;

    /** 是否自动编号字段 */
    @Schema(title = "是否自动编号字段")
    @Excel(name = "是否自动编号字段")
    private Integer isAutoNumber;

    /** 是否区分大小写 */
    @Schema(title = "是否区分大小写")
    @Excel(name = "是否区分大小写")
    private Integer isCaseSensitive;

    /** 是否加密字段 */
    @Schema(title = "是否加密字段")
    @Excel(name = "是否加密字段")
    private Integer isEncrypted;

    /** 是否HTML格式字段 */
    @Schema(title = "是否HTML格式字段")
    @Excel(name = "是否HTML格式字段")
    private Integer isHtmlFormatted;

    /** 是否可通过ID查找 */
    @Schema(title = "是否可通过ID查找")
    @Excel(name = "是否可通过ID查找")
    private Integer isIdLookup;

    /** 是否可设置权限 */
    @Schema(title = "是否可设置权限")
    @Excel(name = "是否可设置权限")
    private Integer isPermissionable;

    /** 是否限制选择列表 */
    @Schema(title = "是否限制选择列表")
    @Excel(name = "是否限制选择列表")
    private Integer isRestrictedPicklist;

    /** 是否限制删除 */
    @Schema(title = "是否限制删除")
    @Excel(name = "是否限制删除")
    private Integer isRestrictedDelete;

    /** 写入时是否需要主读 */
    @Schema(title = "写入时是否需要主读")
    @Excel(name = "写入时是否需要主读")
    private Integer isWriteRequiresMasterRead;

    /** 是否可以通过距离查询 */
    @Schema(title = "是否可以通过距离查询")
    @Excel(name = "是否可以通过距离查询")
    private Integer queryByDistance;

    /** 是否可以预过滤搜索 */
    @Schema(title = "是否可以预过滤搜索")
    @Excel(name = "是否可以预过滤搜索")
    private Integer searchPrefilterable;

    /** 字段数据类型 */
    @Schema(title = "字段数据类型")
    @Excel(name = "字段数据类型")
    private String fieldDataType;

    /** 字段长度 */
    @Schema(title = "字段长度")
    @Excel(name = "字段长度")
    private Long fieldLength;

    /** 数字字段的精度 */
    @Schema(title = "数字字段的精度")
    @Excel(name = "数字字段的精度")
    private Long fieldPrecision;

    /** 数字字段的小数位数 */
    @Schema(title = "数字字段的小数位数")
    @Excel(name = "数字字段的小数位数")
    private Long fieldScale;

    /** 字段的字节长度 */
    @Schema(title = "字段的字节长度")
    @Excel(name = "字段的字节长度")
    private Long fieldByteLength;

    /** SOAP类型 */
    @Schema(title = "SOAP类型")
    @Excel(name = "SOAP类型")
    private String soapType;

    /** 关系名称 */
    @Schema(title = "关系名称")
    @Excel(name = "关系名称")
    private String relationshipName;

    /** 引用目标字段 */
    @Schema(title = "引用目标字段")
    @Excel(name = "引用目标字段")
    private String referenceTargetField;

    /** 引用目标对象的API名称列表（逗号分隔） */
    @Schema(title = "引用目标对象的API名称列表（逗号分隔）")
    @Excel(name = "引用目标对象的API名称列表", readConverterExp = "逗=号分隔")
    private String referenceTo;

    /** 多态映射字段 */
    @Schema(title = "多态映射字段")
    @Excel(name = "多态映射字段")
    private String polymorphicForeignField;

    /** 关系顺序 */
    @Schema(title = "关系顺序")
    @Excel(name = "关系顺序")
    private Long relationshipOrder;

    /** 默认值 */
    @Schema(title = "默认值")
    @Excel(name = "默认值")
    private String defaultValue;

    /** 计算字段的公式 */
    @Schema(title = "计算字段的公式")
    @Excel(name = "计算字段的公式")
    private String calculatedFormula;

    /** 内联帮助文本 */
    @Schema(title = "内联帮助文本")
    @Excel(name = "内联帮助文本")
    private String inlineHelpText;

    /** 掩码 */
    @Schema(title = "掩码")
    @Excel(name = "掩码")
    private String mask;

    /** 掩码类型 */
    @Schema(title = "掩码类型")
    @Excel(name = "掩码类型")
    private String maskType;

    /** 选择列表值 (JSON或其他格式) */
    @Schema(title = "选择列表值 (JSON或其他格式)")
    @Excel(name = "选择列表值 (JSON或其他格式)")
    private String picklistValues;

    /** 租户编号 */
    @Schema(title = "租户编号")
    @Excel(name = "租户编号")
    private String tenantId;
    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }


    public void setApi(String api) 
    {
        this.api = api;
    }

    public String getApi() 
    {
        return api;
    }


    public void setField(String field) 
    {
        this.field = field;
    }

    public String getField() 
    {
        return field;
    }


    public void setLabel(String label) 
    {
        this.label = label;
    }

    public String getLabel() 
    {
        return label;
    }


    public void setIsCreateable(Integer isCreateable) 
    {
        this.isCreateable = isCreateable;
    }

    public Integer getIsCreateable() 
    {
        return isCreateable;
    }


    public void setIsNillable(Integer isNillable) 
    {
        this.isNillable = isNillable;
    }

    public Integer getIsNillable() 
    {
        return isNillable;
    }


    public void setIsUpdateable(Integer isUpdateable) 
    {
        this.isUpdateable = isUpdateable;
    }

    public Integer getIsUpdateable() 
    {
        return isUpdateable;
    }


    public void setIsDefaultedOnCreate(Integer isDefaultedOnCreate) 
    {
        this.isDefaultedOnCreate = isDefaultedOnCreate;
    }

    public Integer getIsDefaultedOnCreate() 
    {
        return isDefaultedOnCreate;
    }


    public void setIsUnique(Integer isUnique) 
    {
        this.isUnique = isUnique;
    }

    public Integer getIsUnique() 
    {
        return isUnique;
    }


    public void setIsFilterable(Integer isFilterable) 
    {
        this.isFilterable = isFilterable;
    }

    public Integer getIsFilterable() 
    {
        return isFilterable;
    }


    public void setIsSortable(Integer isSortable) 
    {
        this.isSortable = isSortable;
    }

    public Integer getIsSortable() 
    {
        return isSortable;
    }


    public void setIsAggregatable(Integer isAggregatable) 
    {
        this.isAggregatable = isAggregatable;
    }

    public Integer getIsAggregatable() 
    {
        return isAggregatable;
    }


    public void setIsGroupable(Integer isGroupable) 
    {
        this.isGroupable = isGroupable;
    }

    public Integer getIsGroupable() 
    {
        return isGroupable;
    }


    public void setIsPolymorphicForeignKey(Integer isPolymorphicForeignKey) 
    {
        this.isPolymorphicForeignKey = isPolymorphicForeignKey;
    }

    public Integer getIsPolymorphicForeignKey() 
    {
        return isPolymorphicForeignKey;
    }


    public void setIsExternalId(Integer isExternalId) 
    {
        this.isExternalId = isExternalId;
    }

    public Integer getIsExternalId() 
    {
        return isExternalId;
    }


    public void setIsCustom(Integer isCustom) 
    {
        this.isCustom = isCustom;
    }

    public Integer getIsCustom() 
    {
        return isCustom;
    }


    public void setIsCalculated(Integer isCalculated) 
    {
        this.isCalculated = isCalculated;
    }

    public Integer getIsCalculated() 
    {
        return isCalculated;
    }


    public void setIsAutoNumber(Integer isAutoNumber) 
    {
        this.isAutoNumber = isAutoNumber;
    }

    public Integer getIsAutoNumber() 
    {
        return isAutoNumber;
    }


    public void setIsCaseSensitive(Integer isCaseSensitive) 
    {
        this.isCaseSensitive = isCaseSensitive;
    }

    public Integer getIsCaseSensitive() 
    {
        return isCaseSensitive;
    }


    public void setIsEncrypted(Integer isEncrypted) 
    {
        this.isEncrypted = isEncrypted;
    }

    public Integer getIsEncrypted() 
    {
        return isEncrypted;
    }


    public void setIsHtmlFormatted(Integer isHtmlFormatted) 
    {
        this.isHtmlFormatted = isHtmlFormatted;
    }

    public Integer getIsHtmlFormatted() 
    {
        return isHtmlFormatted;
    }


    public void setIsIdLookup(Integer isIdLookup) 
    {
        this.isIdLookup = isIdLookup;
    }

    public Integer getIsIdLookup() 
    {
        return isIdLookup;
    }


    public void setIsPermissionable(Integer isPermissionable) 
    {
        this.isPermissionable = isPermissionable;
    }

    public Integer getIsPermissionable() 
    {
        return isPermissionable;
    }


    public void setIsRestrictedPicklist(Integer isRestrictedPicklist) 
    {
        this.isRestrictedPicklist = isRestrictedPicklist;
    }

    public Integer getIsRestrictedPicklist() 
    {
        return isRestrictedPicklist;
    }


    public void setIsRestrictedDelete(Integer isRestrictedDelete) 
    {
        this.isRestrictedDelete = isRestrictedDelete;
    }

    public Integer getIsRestrictedDelete() 
    {
        return isRestrictedDelete;
    }


    public void setIsWriteRequiresMasterRead(Integer isWriteRequiresMasterRead) 
    {
        this.isWriteRequiresMasterRead = isWriteRequiresMasterRead;
    }

    public Integer getIsWriteRequiresMasterRead() 
    {
        return isWriteRequiresMasterRead;
    }


    public void setQueryByDistance(Integer queryByDistance) 
    {
        this.queryByDistance = queryByDistance;
    }

    public Integer getQueryByDistance() 
    {
        return queryByDistance;
    }


    public void setSearchPrefilterable(Integer searchPrefilterable) 
    {
        this.searchPrefilterable = searchPrefilterable;
    }

    public Integer getSearchPrefilterable() 
    {
        return searchPrefilterable;
    }


    public void setFieldDataType(String fieldDataType) 
    {
        this.fieldDataType = fieldDataType;
    }

    public String getFieldDataType() 
    {
        return fieldDataType;
    }


    public void setFieldLength(Long fieldLength) 
    {
        this.fieldLength = fieldLength;
    }

    public Long getFieldLength() 
    {
        return fieldLength;
    }


    public void setFieldPrecision(Long fieldPrecision) 
    {
        this.fieldPrecision = fieldPrecision;
    }

    public Long getFieldPrecision() 
    {
        return fieldPrecision;
    }


    public void setFieldScale(Long fieldScale) 
    {
        this.fieldScale = fieldScale;
    }

    public Long getFieldScale() 
    {
        return fieldScale;
    }


    public void setFieldByteLength(Long fieldByteLength) 
    {
        this.fieldByteLength = fieldByteLength;
    }

    public Long getFieldByteLength() 
    {
        return fieldByteLength;
    }


    public void setSoapType(String soapType) 
    {
        this.soapType = soapType;
    }

    public String getSoapType() 
    {
        return soapType;
    }


    public void setRelationshipName(String relationshipName) 
    {
        this.relationshipName = relationshipName;
    }

    public String getRelationshipName() 
    {
        return relationshipName;
    }


    public void setReferenceTargetField(String referenceTargetField) 
    {
        this.referenceTargetField = referenceTargetField;
    }

    public String getReferenceTargetField() 
    {
        return referenceTargetField;
    }


    public void setReferenceTo(String referenceTo) 
    {
        this.referenceTo = referenceTo;
    }

    public String getReferenceTo() 
    {
        return referenceTo;
    }


    public void setPolymorphicForeignField(String polymorphicForeignField) 
    {
        this.polymorphicForeignField = polymorphicForeignField;
    }

    public String getPolymorphicForeignField() 
    {
        return polymorphicForeignField;
    }


    public void setRelationshipOrder(Long relationshipOrder) 
    {
        this.relationshipOrder = relationshipOrder;
    }

    public Long getRelationshipOrder() 
    {
        return relationshipOrder;
    }


    public void setDefaultValue(String defaultValue) 
    {
        this.defaultValue = defaultValue;
    }

    public String getDefaultValue() 
    {
        return defaultValue;
    }


    public void setCalculatedFormula(String calculatedFormula) 
    {
        this.calculatedFormula = calculatedFormula;
    }

    public String getCalculatedFormula() 
    {
        return calculatedFormula;
    }


    public void setInlineHelpText(String inlineHelpText) 
    {
        this.inlineHelpText = inlineHelpText;
    }

    public String getInlineHelpText() 
    {
        return inlineHelpText;
    }


    public void setMask(String mask) 
    {
        this.mask = mask;
    }

    public String getMask() 
    {
        return mask;
    }


    public void setMaskType(String maskType) 
    {
        this.maskType = maskType;
    }

    public String getMaskType() 
    {
        return maskType;
    }


    public void setPicklistValues(String picklistValues) 
    {
        this.picklistValues = picklistValues;
    }

    public String getPicklistValues() 
    {
        return picklistValues;
    }


    public void setTenantId(String tenantId) 
    {
        this.tenantId = tenantId;
    }

    public String getTenantId() 
    {
        return tenantId;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("api", getApi())
            .append("field", getField())
            .append("label", getLabel())
            .append("isCreateable", getIsCreateable())
            .append("isNillable", getIsNillable())
            .append("isUpdateable", getIsUpdateable())
            .append("isDefaultedOnCreate", getIsDefaultedOnCreate())
            .append("isUnique", getIsUnique())
            .append("isFilterable", getIsFilterable())
            .append("isSortable", getIsSortable())
            .append("isAggregatable", getIsAggregatable())
            .append("isGroupable", getIsGroupable())
            .append("isPolymorphicForeignKey", getIsPolymorphicForeignKey())
            .append("isExternalId", getIsExternalId())
            .append("isCustom", getIsCustom())
            .append("isCalculated", getIsCalculated())
            .append("isAutoNumber", getIsAutoNumber())
            .append("isCaseSensitive", getIsCaseSensitive())
            .append("isEncrypted", getIsEncrypted())
            .append("isHtmlFormatted", getIsHtmlFormatted())
            .append("isIdLookup", getIsIdLookup())
            .append("isPermissionable", getIsPermissionable())
            .append("isRestrictedPicklist", getIsRestrictedPicklist())
            .append("isRestrictedDelete", getIsRestrictedDelete())
            .append("isWriteRequiresMasterRead", getIsWriteRequiresMasterRead())
            .append("queryByDistance", getQueryByDistance())
            .append("searchPrefilterable", getSearchPrefilterable())
            .append("fieldDataType", getFieldDataType())
            .append("fieldLength", getFieldLength())
            .append("fieldPrecision", getFieldPrecision())
            .append("fieldScale", getFieldScale())
            .append("fieldByteLength", getFieldByteLength())
            .append("soapType", getSoapType())
            .append("relationshipName", getRelationshipName())
            .append("referenceTargetField", getReferenceTargetField())
            .append("referenceTo", getReferenceTo())
            .append("polymorphicForeignField", getPolymorphicForeignField())
            .append("relationshipOrder", getRelationshipOrder())
            .append("defaultValue", getDefaultValue())
            .append("calculatedFormula", getCalculatedFormula())
            .append("inlineHelpText", getInlineHelpText())
            .append("mask", getMask())
            .append("maskType", getMaskType())
            .append("picklistValues", getPicklistValues())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("tenantId", getTenantId())
            .toString();
    }
}
