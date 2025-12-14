package com.datai.auth.domain;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 令牌绑定对象 datai_sf_token_binding
 * 
 * @author datai
 * @date 2025-12-14
 */
@Schema(description = "令牌绑定对象")
public class DataiSfTokenBinding extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 绑定ID */
    @Schema(title = "绑定ID")
    private Long bindingId;

    /** 租户编号 */
    @Schema(title = "租户编号")
    @Excel(name = "租户编号")
    private String tenantId;

    /** 令牌ID */
    @Schema(title = "令牌ID")
    @Excel(name = "令牌ID")
    private Long tokenId;

    /** 绑定类型 */
    @Schema(title = "绑定类型")
    @Excel(name = "绑定类型")
    private String bindingType;

    /** 设备ID */
    @Schema(title = "设备ID")
    @Excel(name = "设备ID")
    private String deviceId;

    /** 绑定IP */
    @Schema(title = "绑定IP")
    @Excel(name = "绑定IP")
    private String bindingIp;

    /** 绑定状态 */
    @Schema(title = "绑定状态")
    @Excel(name = "绑定状态")
    private String status;

    /** 绑定时间 */
    @Schema(title = "绑定时间")
    @Excel(name = "绑定时间")
    private LocalDate bindingTime;

    /** 过期时间 */
    @Schema(title = "过期时间")
    @Excel(name = "过期时间")
    private LocalDate expireTime;
    public void setBindingId(Long bindingId) 
    {
        this.bindingId = bindingId;
    }

    public Long getBindingId() 
    {
        return bindingId;
    }


    public void setTenantId(String tenantId) 
    {
        this.tenantId = tenantId;
    }

    public String getTenantId() 
    {
        return tenantId;
    }


    public void setTokenId(Long tokenId) 
    {
        this.tokenId = tokenId;
    }

    public Long getTokenId() 
    {
        return tokenId;
    }


    public void setBindingType(String bindingType) 
    {
        this.bindingType = bindingType;
    }

    public String getBindingType() 
    {
        return bindingType;
    }


    public void setDeviceId(String deviceId) 
    {
        this.deviceId = deviceId;
    }

    public String getDeviceId() 
    {
        return deviceId;
    }


    public void setBindingIp(String bindingIp) 
    {
        this.bindingIp = bindingIp;
    }

    public String getBindingIp() 
    {
        return bindingIp;
    }


    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }


    public void setBindingTime(LocalDate bindingTime) 
    {
        this.bindingTime = bindingTime;
    }

    public LocalDate getBindingTime() 
    {
        return bindingTime;
    }


    public void setExpireTime(LocalDate expireTime) 
    {
        this.expireTime = expireTime;
    }

    public LocalDate getExpireTime() 
    {
        return expireTime;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("bindingId", getBindingId())
            .append("tenantId", getTenantId())
            .append("tokenId", getTokenId())
            .append("bindingType", getBindingType())
            .append("deviceId", getDeviceId())
            .append("bindingIp", getBindingIp())
            .append("status", getStatus())
            .append("bindingTime", getBindingTime())
            .append("expireTime", getExpireTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
