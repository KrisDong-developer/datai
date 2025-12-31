package com.datai.auth.model.domain;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 登录历史信息对象 datai_sf_login_history
 * 
 * @author datai
 * @date 2026-01-01
 */
@Schema(description = "登录历史信息对象")
public class DataiSfLoginHistory extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 主键ID */
    @Schema(title = "主键ID")
    private Long id;

    /** 登录类型 */
    @Schema(title = "登录类型")
    @Excel(name = "登录类型")
    private String loginType;

    /** 用户名 */
    @Schema(title = "用户名")
    @Excel(name = "用户名")
    private String username;

    /** 加密密码 */
    @Schema(title = "加密密码")
    @Excel(name = "加密密码")
    private String passwordEncrypted;

    /** 加密安全令牌 */
    @Schema(title = "加密安全令牌")
    @Excel(name = "加密安全令牌")
    private String securityTokenEncrypted;

    /** OAuth客户端ID */
    @Schema(title = "OAuth客户端ID")
    @Excel(name = "OAuth客户端ID")
    private String clientId;

    /** 加密客户端密钥 */
    @Schema(title = "加密客户端密钥")
    @Excel(name = "加密客户端密钥")
    private String clientSecretEncrypted;

    /** OAuth授权类型 */
    @Schema(title = "OAuth授权类型")
    @Excel(name = "OAuth授权类型")
    private String grantType;

    /** 组织别名 */
    @Schema(title = "组织别名")
    @Excel(name = "组织别名")
    private String orgAlias;

    /** 私有密钥路径 */
    @Schema(title = "私有密钥路径")
    @Excel(name = "私有密钥路径")
    private String privateKeyPath;

    /** OAuth授权码 */
    @Schema(title = "OAuth授权码")
    @Excel(name = "OAuth授权码")
    private String code;

    /** OAuth state参数 */
    @Schema(title = "OAuth state参数")
    @Excel(name = "OAuth state参数")
    private String state;

    /** 会话ID */
    @Schema(title = "会话ID")
    @Excel(name = "会话ID")
    private String sessionId;

    /** 环境编码 */
    @Schema(title = "环境编码")
    @Excel(name = "环境编码")
    private String environmentCode;

    /** 环境ID */
    @Schema(title = "环境ID")
    @Excel(name = "环境ID")
    private Long environmentId;

    /** 实例URL */
    @Schema(title = "实例URL")
    @Excel(name = "实例URL")
    private String instanceUrl;

    /** 组织ID */
    @Schema(title = "组织ID")
    @Excel(name = "组织ID")
    private String organizationId;

    /** 请求IP */
    @Schema(title = "请求IP")
    @Excel(name = "请求IP")
    private String requestIp;

    /** 请求端口 */
    @Schema(title = "请求端口")
    @Excel(name = "请求端口")
    private Integer requestPort;

    /** 用户代理 */
    @Schema(title = "用户代理")
    @Excel(name = "用户代理")
    private String userAgent;

    /** 设备类型 */
    @Schema(title = "设备类型")
    @Excel(name = "设备类型")
    private String deviceType;

    /** 浏览器类型 */
    @Schema(title = "浏览器类型")
    @Excel(name = "浏览器类型")
    private String browserType;

    /** 操作系统 */
    @Schema(title = "操作系统")
    @Excel(name = "操作系统")
    private String osType;

    /** 登录状态 */
    @Schema(title = "登录状态")
    @Excel(name = "登录状态")
    private String loginStatus;

    /** 错误代码 */
    @Schema(title = "错误代码")
    @Excel(name = "错误代码")
    private String errorCode;

    /** 错误信息 */
    @Schema(title = "错误信息")
    @Excel(name = "错误信息")
    private String errorMessage;

    /** 结果会话ID */
    @Schema(title = "结果会话ID")
    @Excel(name = "结果会话ID")
    private String sessionIdResult;

    /** 加密刷新令牌 */
    @Schema(title = "加密刷新令牌")
    @Excel(name = "加密刷新令牌")
    private String refreshTokenEncrypted;

    /** 令牌类型 */
    @Schema(title = "令牌类型")
    @Excel(name = "令牌类型")
    private String tokenType;

    /** 过期时间(秒) */
    @Schema(title = "过期时间(秒)")
    @Excel(name = "过期时间(秒)")
    private Integer expiresIn;

    /** 请求时间 */
    @Schema(title = "请求时间")
    @Excel(name = "请求时间")
    private LocalDateTime requestTime;

    /** 响应时间 */
    @Schema(title = "响应时间")
    @Excel(name = "响应时间")
    private LocalDateTime responseTime;

    /** 处理耗时(毫秒) */
    @Schema(title = "处理耗时(毫秒)")
    @Excel(name = "处理耗时(毫秒)")
    private Long durationMs;

    /** 操作人 */
    @Schema(title = "操作人")
    @Excel(name = "操作人")
    private String operator;

    /** 部门ID */
    @Schema(title = "部门ID")
    private Long deptId;
    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }


    public void setLoginType(String loginType) 
    {
        this.loginType = loginType;
    }

    public String getLoginType() 
    {
        return loginType;
    }


    public void setUsername(String username) 
    {
        this.username = username;
    }

    public String getUsername() 
    {
        return username;
    }


    public void setPasswordEncrypted(String passwordEncrypted) 
    {
        this.passwordEncrypted = passwordEncrypted;
    }

    public String getPasswordEncrypted() 
    {
        return passwordEncrypted;
    }


    public void setSecurityTokenEncrypted(String securityTokenEncrypted) 
    {
        this.securityTokenEncrypted = securityTokenEncrypted;
    }

    public String getSecurityTokenEncrypted() 
    {
        return securityTokenEncrypted;
    }


    public void setClientId(String clientId) 
    {
        this.clientId = clientId;
    }

    public String getClientId() 
    {
        return clientId;
    }


    public void setClientSecretEncrypted(String clientSecretEncrypted) 
    {
        this.clientSecretEncrypted = clientSecretEncrypted;
    }

    public String getClientSecretEncrypted() 
    {
        return clientSecretEncrypted;
    }


    public void setGrantType(String grantType) 
    {
        this.grantType = grantType;
    }

    public String getGrantType() 
    {
        return grantType;
    }


    public void setOrgAlias(String orgAlias) 
    {
        this.orgAlias = orgAlias;
    }

    public String getOrgAlias() 
    {
        return orgAlias;
    }


    public void setPrivateKeyPath(String privateKeyPath) 
    {
        this.privateKeyPath = privateKeyPath;
    }

    public String getPrivateKeyPath() 
    {
        return privateKeyPath;
    }


    public void setCode(String code) 
    {
        this.code = code;
    }

    public String getCode() 
    {
        return code;
    }


    public void setState(String state) 
    {
        this.state = state;
    }

    public String getState() 
    {
        return state;
    }


    public void setSessionId(String sessionId) 
    {
        this.sessionId = sessionId;
    }

    public String getSessionId() 
    {
        return sessionId;
    }


    public void setEnvironmentCode(String environmentCode) 
    {
        this.environmentCode = environmentCode;
    }

    public String getEnvironmentCode() 
    {
        return environmentCode;
    }


    public void setEnvironmentId(Long environmentId) 
    {
        this.environmentId = environmentId;
    }

    public Long getEnvironmentId() 
    {
        return environmentId;
    }


    public void setInstanceUrl(String instanceUrl) 
    {
        this.instanceUrl = instanceUrl;
    }

    public String getInstanceUrl() 
    {
        return instanceUrl;
    }


    public void setOrganizationId(String organizationId) 
    {
        this.organizationId = organizationId;
    }

    public String getOrganizationId() 
    {
        return organizationId;
    }


    public void setRequestIp(String requestIp) 
    {
        this.requestIp = requestIp;
    }

    public String getRequestIp() 
    {
        return requestIp;
    }


    public void setRequestPort(Integer requestPort) 
    {
        this.requestPort = requestPort;
    }

    public Integer getRequestPort() 
    {
        return requestPort;
    }


    public void setUserAgent(String userAgent) 
    {
        this.userAgent = userAgent;
    }

    public String getUserAgent() 
    {
        return userAgent;
    }


    public void setDeviceType(String deviceType) 
    {
        this.deviceType = deviceType;
    }

    public String getDeviceType() 
    {
        return deviceType;
    }


    public void setBrowserType(String browserType) 
    {
        this.browserType = browserType;
    }

    public String getBrowserType() 
    {
        return browserType;
    }


    public void setOsType(String osType) 
    {
        this.osType = osType;
    }

    public String getOsType() 
    {
        return osType;
    }


    public void setLoginStatus(String loginStatus) 
    {
        this.loginStatus = loginStatus;
    }

    public String getLoginStatus() 
    {
        return loginStatus;
    }


    public void setErrorCode(String errorCode) 
    {
        this.errorCode = errorCode;
    }

    public String getErrorCode() 
    {
        return errorCode;
    }


    public void setErrorMessage(String errorMessage) 
    {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() 
    {
        return errorMessage;
    }


    public void setSessionIdResult(String sessionIdResult) 
    {
        this.sessionIdResult = sessionIdResult;
    }

    public String getSessionIdResult() 
    {
        return sessionIdResult;
    }


    public void setRefreshTokenEncrypted(String refreshTokenEncrypted) 
    {
        this.refreshTokenEncrypted = refreshTokenEncrypted;
    }

    public String getRefreshTokenEncrypted() 
    {
        return refreshTokenEncrypted;
    }


    public void setTokenType(String tokenType) 
    {
        this.tokenType = tokenType;
    }

    public String getTokenType() 
    {
        return tokenType;
    }


    public void setExpiresIn(Integer expiresIn) 
    {
        this.expiresIn = expiresIn;
    }

    public Integer getExpiresIn() 
    {
        return expiresIn;
    }


    public void setRequestTime(LocalDateTime requestTime) 
    {
        this.requestTime = requestTime;
    }

    public LocalDateTime getRequestTime() 
    {
        return requestTime;
    }


    public void setResponseTime(LocalDateTime responseTime) 
    {
        this.responseTime = responseTime;
    }

    public LocalDateTime getResponseTime() 
    {
        return responseTime;
    }


    public void setDurationMs(Long durationMs) 
    {
        this.durationMs = durationMs;
    }

    public Long getDurationMs() 
    {
        return durationMs;
    }


    public void setOperator(String operator) 
    {
        this.operator = operator;
    }

    public String getOperator() 
    {
        return operator;
    }


    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("loginType", getLoginType())
            .append("username", getUsername())
            .append("passwordEncrypted", getPasswordEncrypted())
            .append("securityTokenEncrypted", getSecurityTokenEncrypted())
            .append("clientId", getClientId())
            .append("clientSecretEncrypted", getClientSecretEncrypted())
            .append("grantType", getGrantType())
            .append("orgAlias", getOrgAlias())
            .append("privateKeyPath", getPrivateKeyPath())
            .append("code", getCode())
            .append("state", getState())
            .append("sessionId", getSessionId())
            .append("environmentCode", getEnvironmentCode())
            .append("environmentId", getEnvironmentId())
            .append("instanceUrl", getInstanceUrl())
            .append("organizationId", getOrganizationId())
            .append("requestIp", getRequestIp())
            .append("requestPort", getRequestPort())
            .append("userAgent", getUserAgent())
            .append("deviceType", getDeviceType())
            .append("browserType", getBrowserType())
            .append("osType", getOsType())
            .append("loginStatus", getLoginStatus())
            .append("errorCode", getErrorCode())
            .append("errorMessage", getErrorMessage())
            .append("sessionIdResult", getSessionIdResult())
            .append("refreshTokenEncrypted", getRefreshTokenEncrypted())
            .append("tokenType", getTokenType())
            .append("expiresIn", getExpiresIn())
            .append("requestTime", getRequestTime())
            .append("responseTime", getResponseTime())
            .append("durationMs", getDurationMs())
            .append("operator", getOperator())
            .append("deptId", getDeptId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
