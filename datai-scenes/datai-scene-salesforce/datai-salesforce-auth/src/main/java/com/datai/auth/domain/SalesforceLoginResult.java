package com.datai.auth.domain;

import java.io.Serializable;

/**
 * Salesforce登录结果实体
 * 
 * @author datai
 * @date 2025-12-14
 */
public class SalesforceLoginResult implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 登录是否成功
     */
    private boolean success;
    
    /**
     * 访问令牌，用于访问Salesforce API
     */
    private String accessToken;
    
    /**
     * 刷新令牌，用于刷新访问令牌
     */
    private String refreshToken;
    
    /**
     * 实例URL，Salesforce实例URL
     */
    private String instanceUrl;
    
    /**
     * 组织ID，Salesforce组织ID
     */
    private String organizationId;
    
    /**
     * 用户ID，Salesforce用户ID
     */
    private String userId;
    
    /**
     * 令牌类型，如Bearer
     */
    private String tokenType;
    
    /**
     * 访问令牌过期时间（秒）
     */
    private long expiresIn;
    
    /**
     * 错误信息，登录失败时的错误描述
     */
    private String errorMessage;
    
    /**
     * 错误代码，登录失败时的错误代码
     */
    private String errorCode;
    
    // getter和setter方法
    public boolean isSuccess() {
        return success;
    }
    
    public void setSuccess(boolean success) {
        this.success = success;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getRefreshToken() {
        return refreshToken;
    }
    
    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }
    
    public String getInstanceUrl() {
        return instanceUrl;
    }
    
    public void setInstanceUrl(String instanceUrl) {
        this.instanceUrl = instanceUrl;
    }
    
    public String getOrganizationId() {
        return organizationId;
    }
    
    public void setOrganizationId(String organizationId) {
        this.organizationId = organizationId;
    }
    
    public String getUserId() {
        return userId;
    }
    
    public void setUserId(String userId) {
        this.userId = userId;
    }
    
    public String getTokenType() {
        return tokenType;
    }
    
    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }
    
    public long getExpiresIn() {
        return expiresIn;
    }
    
    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }
    
    public String getErrorMessage() {
        return errorMessage;
    }
    
    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
    
    public String getErrorCode() {
        return errorCode;
    }
    
    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }
}