package com.datai.setting.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 配置环境对象 datai_config_environment
 * 
 * @author datai
 * @date 2025-12-14
 */
@Schema(description = "配置环境对象")
public class DataiConfigEnvironment extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 环境ID */
    @Schema(title = "环境ID")
    private Long environmentId;

    /** 环境名称 */
    @Schema(title = "环境名称")
    @Excel(name = "环境名称")
    private String environmentName;

    /** 环境编码 */
    @Schema(title = "环境编码")
    @Excel(name = "环境编码")
    private String environmentCode;

    /** 环境描述 */
    @Schema(title = "环境描述")
    @Excel(name = "环境描述")
    private String description;

    /** 是否激活 */
    @Schema(title = "是否激活")
    @Excel(name = "是否激活")
    private Long isActive;

    /** 租户编号 */
    @Schema(title = "租户编号")
    @Excel(name = "租户编号")
    private String tenantId;
    public void setEnvironmentId(Long environmentId) 
    {
        this.environmentId = environmentId;
    }

    public Long getEnvironmentId() 
    {
        return environmentId;
    }


    public void setEnvironmentName(String environmentName) 
    {
        this.environmentName = environmentName;
    }

    public String getEnvironmentName() 
    {
        return environmentName;
    }


    public void setEnvironmentCode(String environmentCode) 
    {
        this.environmentCode = environmentCode;
    }

    public String getEnvironmentCode() 
    {
        return environmentCode;
    }


    public void setDescription(String description) 
    {
        this.description = description;
    }

    public String getDescription() 
    {
        return description;
    }


    public void setIsActive(Long isActive) 
    {
        this.isActive = isActive;
    }

    public Long getIsActive() 
    {
        return isActive;
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
            .append("environmentId", getEnvironmentId())
            .append("environmentName", getEnvironmentName())
            .append("environmentCode", getEnvironmentCode())
            .append("description", getDescription())
            .append("isActive", getIsActive())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("tenantId", getTenantId())
            .toString();
    }
}
