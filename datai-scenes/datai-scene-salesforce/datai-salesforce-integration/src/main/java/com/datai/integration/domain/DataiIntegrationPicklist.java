package com.datai.integration.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 字段选择列表信息对象 datai_integration_picklist
 * 
 * @author datai
 * @date 2025-12-24
 */
@Schema(description = "字段选择列表信息对象")
public class DataiIntegrationPicklist extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 主键ID */
    @Schema(title = "主键ID")
    private Integer id;

    /** 部门ID */
    @Schema(title = "部门ID")
    private Long deptId;

    /** 对象API */
    @Schema(title = "对象API")
    @Excel(name = "对象API")
    private String api;

    /** 字段API */
    @Schema(title = "字段API")
    @Excel(name = "字段API")
    private String field;

    /** 选择列表值 */
    @Schema(title = "选择列表值")
    @Excel(name = "选择列表值")
    private String picklistValue;

    /** 选择列表标签 */
    @Schema(title = "选择列表标签")
    @Excel(name = "选择列表标签")
    private String picklistLabel;

    /** 排序 */
    @Schema(title = "排序")
    @Excel(name = "排序")
    private Integer picklistIndex;

    /** 是否激活 */
    @Schema(title = "是否激活")
    @Excel(name = "是否激活")
    private Boolean isActive;

    /** 是否默认值 */
    @Schema(title = "是否默认值")
    @Excel(name = "是否默认值")
    private Boolean isDefault;

    /** 有效性 */
    @Schema(title = "有效性")
    @Excel(name = "有效性")
    private String validFor;
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


    public void setPicklistValue(String picklistValue) 
    {
        this.picklistValue = picklistValue;
    }

    public String getPicklistValue() 
    {
        return picklistValue;
    }


    public void setPicklistLabel(String picklistLabel) 
    {
        this.picklistLabel = picklistLabel;
    }

    public String getPicklistLabel() 
    {
        return picklistLabel;
    }


    public void setPicklistIndex(Integer picklistIndex) 
    {
        this.picklistIndex = picklistIndex;
    }

    public Integer getPicklistIndex() 
    {
        return picklistIndex;
    }


    public void setIsActive(Boolean isActive) 
    {
        this.isActive = isActive;
    }

    public Boolean getIsActive() 
    {
        return isActive;
    }


    public void setIsDefault(Boolean isDefault) 
    {
        this.isDefault = isDefault;
    }

    public Boolean getIsDefault() 
    {
        return isDefault;
    }


    public void setValidFor(String validFor) 
    {
        this.validFor = validFor;
    }

    public String getValidFor() 
    {
        return validFor;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deptId", getDeptId())
            .append("api", getApi())
            .append("field", getField())
            .append("picklistValue", getPicklistValue())
            .append("picklistLabel", getPicklistLabel())
            .append("picklistIndex", getPicklistIndex())
            .append("isActive", getIsActive())
            .append("isDefault", getIsDefault())
            .append("validFor", getValidFor())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
