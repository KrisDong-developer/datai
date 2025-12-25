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
 * @date 2025-12-24
 */
@Schema(description = "配置对象")
public class DataiConfiguration extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 配置ID */
    @Schema(title = "配置ID")
    private Long id;

    /** 部门ID */
    @Schema(title = "部门ID")
    private Long deptId;

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
    private Boolean isSensitive;

    /** 是否加密存储 */
    @Schema(title = "是否加密存储")
    @Excel(name = "是否加密存储")
    private Boolean isEncrypted;

    /** 配置描述 */
    @Schema(title = "配置描述")
    @Excel(name = "配置描述")
    private String description;

    /** 是否激活 */
    @Schema(title = "是否激活")
    @Excel(name = "是否激活")
    private Boolean isActive;

    /** 配置版本号 */
    @Schema(title = "配置版本号")
    @Excel(name = "配置版本号")
    private Integer version;
    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
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


    public void setIsSensitive(Boolean isSensitive) 
    {
        this.isSensitive = isSensitive;
    }

    public Boolean getIsSensitive() 
    {
        return isSensitive;
    }


    public void setIsEncrypted(Boolean isEncrypted) 
    {
        this.isEncrypted = isEncrypted;
    }

    public Boolean getIsEncrypted() 
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


    public void setIsActive(Boolean isActive) 
    {
        this.isActive = isActive;
    }

    public Boolean getIsActive() 
    {
        return isActive;
    }

    public void setVersion(Integer version) 
    {
        this.version = version;
    }

    public Integer getVersion() 
    {
        return version;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deptId", getDeptId())
            .append("configKey", getConfigKey())
            .append("configValue", getConfigValue())
            .append("environmentId", getEnvironmentId())
            .append("isSensitive", getIsSensitive())
            .append("isEncrypted", getIsEncrypted())
            .append("description", getDescription())
            .append("isActive", getIsActive())
            .append("version", getVersion())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
