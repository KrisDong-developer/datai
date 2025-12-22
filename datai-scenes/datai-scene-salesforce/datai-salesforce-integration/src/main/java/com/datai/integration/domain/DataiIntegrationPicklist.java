package com.datai.integration.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 字段选择列信息对象 datai_integration_picklist
 * 
 * @author datai
 * @date 2025-12-22
 */
@Schema(description = "字段选择列信息对象")
public class DataiIntegrationPicklist extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 主键ID */
    @Schema(title = "主键ID")
    private Long id;

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
    private Long picklistIndex;

    /** 是否激活 */
    @Schema(title = "是否激活")
    @Excel(name = "是否激活")
    private Integer isActive;

    /** 是否默认值 */
    @Schema(title = "是否默认值")
    @Excel(name = "是否默认值")
    private Integer isDefault;

    /** 有效性 */
    @Schema(title = "有效性")
    @Excel(name = "有效性")
    private String validFor;

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


    public void setPicklistIndex(Long picklistIndex) 
    {
        this.picklistIndex = picklistIndex;
    }

    public Long getPicklistIndex() 
    {
        return picklistIndex;
    }


    public void setIsActive(Integer isActive) 
    {
        this.isActive = isActive;
    }

    public Integer getIsActive() 
    {
        return isActive;
    }


    public void setIsDefault(Integer isDefault) 
    {
        this.isDefault = isDefault;
    }

    public Integer getIsDefault() 
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
            .append("picklistValue", getPicklistValue())
            .append("picklistLabel", getPicklistLabel())
            .append("picklistIndex", getPicklistIndex())
            .append("isActive", getIsActive())
            .append("isDefault", getIsDefault())
            .append("validFor", getValidFor())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("tenantId", getTenantId())
            .toString();
    }
}
