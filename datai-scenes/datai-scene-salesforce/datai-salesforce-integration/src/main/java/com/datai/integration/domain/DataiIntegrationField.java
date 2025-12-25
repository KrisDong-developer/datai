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
 * @date 2025-12-24
 */
@Schema(description = "对象字段信息对象")
public class DataiIntegrationField extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 主键ID */
    @Schema(title = "主键ID")
    private Integer id;

    /** 部门ID */
    @Schema(title = "部门ID")
    private Long deptId;

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
    private Boolean isCreateable;

    /** 是否为空 */
    @Schema(title = "是否为空")
    @Excel(name = "是否为空")
    private Boolean isNillable;

    /** 是否可更新 */
    @Schema(title = "是否可更新")
    @Excel(name = "是否可更新")
    private Boolean isUpdateable;

    /** 是否默认值 */
    @Schema(title = "是否默认值")
    @Excel(name = "是否默认值")
    private Boolean isDefaultedOnCreate;

    /** 是否唯一 */
    @Schema(title = "是否唯一")
    @Excel(name = "是否唯一")
    private Boolean isUnique;

    /** 是否可过滤 */
    @Schema(title = "是否可过滤")
    @Excel(name = "是否可过滤")
    private Boolean isFilterable;

    /** 是否可排序 */
    @Schema(title = "是否可排序")
    @Excel(name = "是否可排序")
    private Boolean isSortable;

    /** 是否可聚合 */
    @Schema(title = "是否可聚合")
    @Excel(name = "是否可聚合")
    private Boolean isAggregatable;

    /** 是否可分组 */
    @Schema(title = "是否可分组")
    @Excel(name = "是否可分组")
    private Boolean isGroupable;

    /** 是否多态外键 */
    @Schema(title = "是否多态外键")
    @Excel(name = "是否多态外键")
    private Boolean isPolymorphicForeignKey;

    /** 是否外部ID */
    @Schema(title = "是否外部ID")
    @Excel(name = "是否外部ID")
    private Boolean isExternalId;

    /** 是否自定义字段 */
    @Schema(title = "是否自定义字段")
    @Excel(name = "是否自定义字段")
    private Boolean isCustom;

    /** 是否计算字段 */
    @Schema(title = "是否计算字段")
    @Excel(name = "是否计算字段")
    private Boolean isCalculated;

    /** 是否自动编号字段 */
    @Schema(title = "是否自动编号字段")
    @Excel(name = "是否自动编号字段")
    private Boolean isAutoNumber;

    /** 是否区分大小写 */
    @Schema(title = "是否区分大小写")
    @Excel(name = "是否区分大小写")
    private Boolean isCaseSensitive;

    /** 是否加密字段 */
    @Schema(title = "是否加密字段")
    @Excel(name = "是否加密字段")
    private Boolean isEncrypted;

    /** 是否HTML格式字段 */
    @Schema(title = "是否HTML格式字段")
    @Excel(name = "是否HTML格式字段")
    private Boolean isHtmlFormatted;

    /** 是否可通过ID查找 */
    @Schema(title = "是否可通过ID查找")
    @Excel(name = "是否可通过ID查找")
    private Boolean isIdLookup;

    /** 是否可设置权限 */
    @Schema(title = "是否可设置权限")
    @Excel(name = "是否可设置权限")
    private Boolean isPermissionable;

    /** 是否限制选择列表 */
    @Schema(title = "是否限制选择列表")
    @Excel(name = "是否限制选择列表")
    private Boolean isRestrictedPicklist;

    /** 是否限制删除 */
    @Schema(title = "是否限制删除")
    @Excel(name = "是否限制删除")
    private Boolean isRestrictedDelete;

    /** 写入时是否需要主读 */
    @Schema(title = "写入时是否需要主读")
    @Excel(name = "写入时是否需要主读")
    private Boolean isWriteRequiresMasterRead;

    /** 是否可以通过距离查询 */
    @Schema(title = "是否可以通过距离查询")
    @Excel(name = "是否可以通过距离查询")
    private Boolean queryByDistance;

    /** 是否可以预过滤搜索 */
    @Schema(title = "是否可以预过滤搜索")
    @Excel(name = "是否可以预过滤搜索")
    private Boolean searchPrefilterable;

    /** 字段数据类型 */
    @Schema(title = "字段数据类型")
    @Excel(name = "字段数据类型")
    private String fieldDataType;

    /** 字段长度 */
    @Schema(title = "字段长度")
    @Excel(name = "字段长度")
    private Integer fieldLength;

    /** 数字字段的精度 */
    @Schema(title = "数字字段的精度")
    @Excel(name = "数字字段的精度")
    private Integer fieldPrecision;

    /** 数字字段的小数位数 */
    @Schema(title = "数字字段的小数位数")
    @Excel(name = "数字字段的小数位数")
    private Integer fieldScale;

    /** 字段的字节长度 */
    @Schema(title = "字段的字节长度")
    @Excel(name = "字段的字节长度")
    private Integer fieldByteLength;

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
    private Integer relationshipOrder;

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
    public void setId(Integer id) 
    {
        this.id = id;
    }

    public Integer getId() 
    {
        return id;
    }


    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
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


    public void setIsCreateable(Boolean isCreateable) 
    {
        this.isCreateable = isCreateable;
    }

    public Boolean getIsCreateable() 
    {
        return isCreateable;
    }


    public void setIsNillable(Boolean isNillable) 
    {
        this.isNillable = isNillable;
    }

    public Boolean getIsNillable() 
    {
        return isNillable;
    }


    public void setIsUpdateable(Boolean isUpdateable) 
    {
        this.isUpdateable = isUpdateable;
    }

    public Boolean getIsUpdateable() 
    {
        return isUpdateable;
    }


    public void setIsDefaultedOnCreate(Boolean isDefaultedOnCreate) 
    {
        this.isDefaultedOnCreate = isDefaultedOnCreate;
    }

    public Boolean getIsDefaultedOnCreate() 
    {
        return isDefaultedOnCreate;
    }


    public void setIsUnique(Boolean isUnique) 
    {
        this.isUnique = isUnique;
    }

    public Boolean getIsUnique() 
    {
        return isUnique;
    }


    public void setIsFilterable(Boolean isFilterable) 
    {
        this.isFilterable = isFilterable;
    }

    public Boolean getIsFilterable() 
    {
        return isFilterable;
    }


    public void setIsSortable(Boolean isSortable) 
    {
        this.isSortable = isSortable;
    }

    public Boolean getIsSortable() 
    {
        return isSortable;
    }


    public void setIsAggregatable(Boolean isAggregatable) 
    {
        this.isAggregatable = isAggregatable;
    }

    public Boolean getIsAggregatable() 
    {
        return isAggregatable;
    }


    public void setIsGroupable(Boolean isGroupable) 
    {
        this.isGroupable = isGroupable;
    }

    public Boolean getIsGroupable() 
    {
        return isGroupable;
    }


    public void setIsPolymorphicForeignKey(Boolean isPolymorphicForeignKey) 
    {
        this.isPolymorphicForeignKey = isPolymorphicForeignKey;
    }

    public Boolean getIsPolymorphicForeignKey() 
    {
        return isPolymorphicForeignKey;
    }


    public void setIsExternalId(Boolean isExternalId) 
    {
        this.isExternalId = isExternalId;
    }

    public Boolean getIsExternalId() 
    {
        return isExternalId;
    }


    public void setIsCustom(Boolean isCustom) 
    {
        this.isCustom = isCustom;
    }

    public Boolean getIsCustom() 
    {
        return isCustom;
    }


    public void setIsCalculated(Boolean isCalculated) 
    {
        this.isCalculated = isCalculated;
    }

    public Boolean getIsCalculated() 
    {
        return isCalculated;
    }


    public void setIsAutoNumber(Boolean isAutoNumber) 
    {
        this.isAutoNumber = isAutoNumber;
    }

    public Boolean getIsAutoNumber() 
    {
        return isAutoNumber;
    }


    public void setIsCaseSensitive(Boolean isCaseSensitive) 
    {
        this.isCaseSensitive = isCaseSensitive;
    }

    public Boolean getIsCaseSensitive() 
    {
        return isCaseSensitive;
    }


    public void setIsEncrypted(Boolean isEncrypted) 
    {
        this.isEncrypted = isEncrypted;
    }

    public Boolean getIsEncrypted() 
    {
        return isEncrypted;
    }


    public void setIsHtmlFormatted(Boolean isHtmlFormatted) 
    {
        this.isHtmlFormatted = isHtmlFormatted;
    }

    public Boolean getIsHtmlFormatted() 
    {
        return isHtmlFormatted;
    }


    public void setIsIdLookup(Boolean isIdLookup) 
    {
        this.isIdLookup = isIdLookup;
    }

    public Boolean getIsIdLookup() 
    {
        return isIdLookup;
    }


    public void setIsPermissionable(Boolean isPermissionable) 
    {
        this.isPermissionable = isPermissionable;
    }

    public Boolean getIsPermissionable() 
    {
        return isPermissionable;
    }


    public void setIsRestrictedPicklist(Boolean isRestrictedPicklist) 
    {
        this.isRestrictedPicklist = isRestrictedPicklist;
    }

    public Boolean getIsRestrictedPicklist() 
    {
        return isRestrictedPicklist;
    }


    public void setIsRestrictedDelete(Boolean isRestrictedDelete) 
    {
        this.isRestrictedDelete = isRestrictedDelete;
    }

    public Boolean getIsRestrictedDelete() 
    {
        return isRestrictedDelete;
    }


    public void setIsWriteRequiresMasterRead(Boolean isWriteRequiresMasterRead) 
    {
        this.isWriteRequiresMasterRead = isWriteRequiresMasterRead;
    }

    public Boolean getIsWriteRequiresMasterRead() 
    {
        return isWriteRequiresMasterRead;
    }


    public void setQueryByDistance(Boolean queryByDistance) 
    {
        this.queryByDistance = queryByDistance;
    }

    public Boolean getQueryByDistance() 
    {
        return queryByDistance;
    }


    public void setSearchPrefilterable(Boolean searchPrefilterable) 
    {
        this.searchPrefilterable = searchPrefilterable;
    }

    public Boolean getSearchPrefilterable() 
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


    public void setFieldLength(Integer fieldLength) 
    {
        this.fieldLength = fieldLength;
    }

    public Integer getFieldLength() 
    {
        return fieldLength;
    }


    public void setFieldPrecision(Integer fieldPrecision) 
    {
        this.fieldPrecision = fieldPrecision;
    }

    public Integer getFieldPrecision() 
    {
        return fieldPrecision;
    }


    public void setFieldScale(Integer fieldScale) 
    {
        this.fieldScale = fieldScale;
    }

    public Integer getFieldScale() 
    {
        return fieldScale;
    }


    public void setFieldByteLength(Integer fieldByteLength) 
    {
        this.fieldByteLength = fieldByteLength;
    }

    public Integer getFieldByteLength() 
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


    public void setRelationshipOrder(Integer relationshipOrder) 
    {
        this.relationshipOrder = relationshipOrder;
    }

    public Integer getRelationshipOrder() 
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



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deptId", getDeptId())
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
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
