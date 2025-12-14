package com.datai.auth.domain;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 登录审计日志对象 datai_sf_login_audit
 * 
 * @author datai
 * @date 2025-12-14
 */
@Schema(description = "登录审计日志对象")
public class DataiSfLoginAudit extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 日志ID */
    @Schema(title = "日志ID")
    private Long auditId;

    /** 租户编号 */
    @Schema(title = "租户编号")
    @Excel(name = "租户编号")
    private String tenantId;

    /** 用户名 */
    @Schema(title = "用户名")
    @Excel(name = "用户名")
    private String username;

    /** 操作类型 */
    @Schema(title = "操作类型")
    @Excel(name = "操作类型")
    private String operationType;

    /** 操作结果 */
    @Schema(title = "操作结果")
    @Excel(name = "操作结果")
    private String result;

    /** 操作时间 */
    @Schema(title = "操作时间")
    @Excel(name = "操作时间")
    private LocalDate operationTime;

    /** IP地址 */
    @Schema(title = "IP地址")
    @Excel(name = "IP地址")
    private String ipAddress;

    /** 设备信息 */
    @Schema(title = "设备信息")
    @Excel(name = "设备信息")
    private String deviceInfo;

    /** 浏览器信息 */
    @Schema(title = "浏览器信息")
    @Excel(name = "浏览器信息")
    private String browserInfo;

    /** 登录类型 */
    @Schema(title = "登录类型")
    @Excel(name = "登录类型")
    private String loginType;

    /** 错误信息 */
    @Schema(title = "错误信息")
    @Excel(name = "错误信息")
    private String errorMessage;

    /** 会话ID */
    @Schema(title = "会话ID")
    @Excel(name = "会话ID")
    private Long sessionId;

    /** 令牌ID */
    @Schema(title = "令牌ID")
    @Excel(name = "令牌ID")
    private Long tokenId;

    /** 请求ID */
    @Schema(title = "请求ID")
    @Excel(name = "请求ID")
    private String requestId;
    public void setAuditId(Long auditId) 
    {
        this.auditId = auditId;
    }

    public Long getAuditId() 
    {
        return auditId;
    }


    public void setTenantId(String tenantId) 
    {
        this.tenantId = tenantId;
    }

    public String getTenantId() 
    {
        return tenantId;
    }


    public void setUsername(String username) 
    {
        this.username = username;
    }

    public String getUsername() 
    {
        return username;
    }


    public void setOperationType(String operationType) 
    {
        this.operationType = operationType;
    }

    public String getOperationType() 
    {
        return operationType;
    }


    public void setResult(String result) 
    {
        this.result = result;
    }

    public String getResult() 
    {
        return result;
    }


    public void setOperationTime(LocalDate operationTime) 
    {
        this.operationTime = operationTime;
    }

    public LocalDate getOperationTime() 
    {
        return operationTime;
    }


    public void setIpAddress(String ipAddress) 
    {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() 
    {
        return ipAddress;
    }


    public void setDeviceInfo(String deviceInfo) 
    {
        this.deviceInfo = deviceInfo;
    }

    public String getDeviceInfo() 
    {
        return deviceInfo;
    }


    public void setBrowserInfo(String browserInfo) 
    {
        this.browserInfo = browserInfo;
    }

    public String getBrowserInfo() 
    {
        return browserInfo;
    }


    public void setLoginType(String loginType) 
    {
        this.loginType = loginType;
    }

    public String getLoginType() 
    {
        return loginType;
    }


    public void setErrorMessage(String errorMessage) 
    {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() 
    {
        return errorMessage;
    }


    public void setSessionId(Long sessionId) 
    {
        this.sessionId = sessionId;
    }

    public Long getSessionId() 
    {
        return sessionId;
    }


    public void setTokenId(Long tokenId) 
    {
        this.tokenId = tokenId;
    }

    public Long getTokenId() 
    {
        return tokenId;
    }


    public void setRequestId(String requestId) 
    {
        this.requestId = requestId;
    }

    public String getRequestId() 
    {
        return requestId;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("auditId", getAuditId())
            .append("tenantId", getTenantId())
            .append("username", getUsername())
            .append("operationType", getOperationType())
            .append("result", getResult())
            .append("operationTime", getOperationTime())
            .append("ipAddress", getIpAddress())
            .append("deviceInfo", getDeviceInfo())
            .append("browserInfo", getBrowserInfo())
            .append("loginType", getLoginType())
            .append("errorMessage", getErrorMessage())
            .append("sessionId", getSessionId())
            .append("tokenId", getTokenId())
            .append("requestId", getRequestId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .toString();
    }
}
