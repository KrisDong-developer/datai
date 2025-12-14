package com.datai.setting.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 配置对象 datai_configuration
 * 
 * @author datai
 * @date 2025-12-14
 */
@Schema(description = "配置对象")
public class DataiConfiguration extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 配置ID */
    @Schema(title = "配置ID")
    private Long configId;

    /** 配置键 */
    @Schema(title = "配置键")
    @Excel(name = "配置键")
    private String configKey;

    /** 配置值 */
    @Schema(title = "配置值")
    @Excel(name = "配置值")
    private String configValue;

    /** 环境ID */
    @Schema(title = "环境ID")
    @Excel(name = "环境ID")
    private Long environmentId;

    /** 是否敏感配置 */
    @Schema(title = "是否敏感配置")
    @Excel(name = "是否敏感配置")
    private Long isSensitive;

    /** 是否加密存储 */
    @Schema(title = "是否加密存储")
    @Excel(name = "是否加密存储")
    private Long isEncrypted;

    /** 配置描述 */
    @Schema(title = "配置描述")
    @Excel(name = "配置描述")
    private String description;

    /** 是否激活 */
    @Schema(title = "是否激活")
    @Excel(name = "是否激活")
    private Long isActive;

    /** 租户编号 */
    @Schema(title = "租户编号")
    @Excel(name = "租户编号")
    private String tenantId;
    public void setConfigId(Long configId) 
    {
        this.configId = configId;
    }

    public Long getConfigId() 
    {
        return configId;
    }


    public void setConfigKey(String configKey) 
    {
        this.configKey = configKey;
    }

    public String getConfigKey() 
    {
        return configKey;
    }


    public void setConfigValue(String configValue) 
    {
        this.configValue = configValue;
    }

    public String getConfigValue() 
    {
        return configValue;
    }


    public void setEnvironmentId(Long environmentId) 
    {
        this.environmentId = environmentId;
    }

    public Long getEnvironmentId() 
    {
        return environmentId;
    }


    public void setIsSensitive(Long isSensitive) 
    {
        this.isSensitive = isSensitive;
    }

    public Long getIsSensitive() 
    {
        return isSensitive;
    }


    public void setIsEncrypted(Long isEncrypted) 
    {
        this.isEncrypted = isEncrypted;
    }

    public Long getIsEncrypted() 
    {
        return isEncrypted;
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
            .append("configId", getConfigId())
            .append("configKey", getConfigKey())
            .append("configValue", getConfigValue())
            .append("environmentId", getEnvironmentId())
            .append("isSensitive", getIsSensitive())
            .append("isEncrypted", getIsEncrypted())
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
