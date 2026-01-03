package com.datai.auth.model.domain;

import java.io.Serializable;

/**
 * Salesforce登录结果实体 (优化增强版)
 * * @author datai
 * @date 2025-12-14
 */
public class SalesforceLoginResult implements Serializable {
    private static final long serialVersionUID = 1L;

    /** 登录状态 */
    private boolean success;
    private String errorCode;
    private String errorMessage;

    /** 核心授权信息 */
    private String sessionId;   // 对应 SOAP 的 sessionId
    private String refreshToken;  // 仅在 OAuth 流程中存在
    private String tokenType = "Bearer";
    private long expiresIn;       // 过期时间(秒)
    private long loginTimestamp; // 记录登录时间点
    private long expirationTimestamp; // 记录过期时间点（登录时间 + 过期时长）

    /** 环境信息 */
    private String instanceUrl;       // API 访问基础地址
    private String metadataServerUrl; // 元数据 API 专用地址
    private boolean sandbox;          // 是否为沙盒环境
    private boolean passwordExpired;  // 密码是否已过期

    /** 用户与组织标识 */
    private String userId;
    private String organizationId;

    /** 扩展用户信息 (由 userInfo 节点提取) */
    private String userFullName;
    private String userEmail;
    private String organizationName;
    private String language;
    private String timeZone;

    public SalesforceLoginResult() {
    }

    // --- 业务辅助方法 ---

    /**
     * 判断 sessionId 是否有效（基于本地时间估算）
     * 提前 5 分钟判断为过期，以预留网络传输时间
     */
    public boolean isSessionExpired() {
        if (expiresIn <= 0) return false;
        if (expirationTimestamp <= 0) return false;
        long bufferMillis = 300 * 1000;
        return System.currentTimeMillis() > (expirationTimestamp - bufferMillis);
    }

    // --- 增强的 Getter 和 Setter ---

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public String getSessionId() { return sessionId; }
    public void setSessionId(String sessionId) { this.sessionId = sessionId; }

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }

    public String getInstanceUrl() { return instanceUrl; }
    public void setInstanceUrl(String instanceUrl) { this.instanceUrl = instanceUrl; }

    public String getMetadataServerUrl() { return metadataServerUrl; }
    public void setMetadataServerUrl(String metadataServerUrl) { this.metadataServerUrl = metadataServerUrl; }

    public String getOrganizationId() { return organizationId; }
    public void setOrganizationId(String organizationId) { this.organizationId = organizationId; }

    public String getUserId() { return userId; }
    public void setUserId(String userId) { this.userId = userId; }

    public String getTokenType() { return tokenType; }
    public void setTokenType(String tokenType) { this.tokenType = tokenType; }

    public long getExpiresIn() { return expiresIn; }
    public void setExpiresIn(long expiresIn) { this.expiresIn = expiresIn; }

    public String getErrorMessage() { return errorMessage; }
    public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }

    public String getErrorCode() { return errorCode; }
    public void setErrorCode(String errorCode) { this.errorCode = errorCode; }

    public boolean isSandbox() { return sandbox; }
    public void setSandbox(boolean sandbox) { this.sandbox = sandbox; }

    public boolean isPasswordExpired() { return passwordExpired; }
    public void setPasswordExpired(boolean passwordExpired) { this.passwordExpired = passwordExpired; }

    public String getUserFullName() { return userFullName; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getOrganizationName() { return organizationName; }
    public void setOrganizationName(String organizationName) { this.organizationName = organizationName; }

    public String getLanguage() { return language; }
    public void setLanguage(String language) { this.language = language; }

    public String getTimeZone() { return timeZone; }
    public void setTimeZone(String timeZone) { this.timeZone = timeZone; }

    public long getLoginTimestamp() { return loginTimestamp; }
    public void setLoginTimestamp(long loginTimestamp) { this.loginTimestamp = loginTimestamp; }

    public long getExpirationTimestamp() { return expirationTimestamp; }
    public void setExpirationTimestamp(long expirationTimestamp) { this.expirationTimestamp = expirationTimestamp; }

    @Override
    public String toString() {
        return "SalesforceLoginResult{" +
                "success=" + success +
                ", userId='" + userId + '\'' +
                ", instanceUrl='" + instanceUrl + '\'' +
                ", sandbox=" + sandbox +
                ", passwordExpired=" + passwordExpired +
                '}';
    }
}