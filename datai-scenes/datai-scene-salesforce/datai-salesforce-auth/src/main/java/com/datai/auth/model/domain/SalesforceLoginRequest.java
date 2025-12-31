package com.datai.auth.model.domain;

import java.io.Serializable;

/**
 * Salesforce登录请求实体
 * 
 * @author datai
 * @date 2025-12-14
 */
public class SalesforceLoginRequest implements Serializable {
    private static final long serialVersionUID = 1L;
    
    /**
     * 登录类型，如oauth2、salesforce_cli、legacy_credential等
     */
    private String loginType;
    
    /**
     * 用户名
     */
    private String username;
    
    /**
     * 密码
     */
    private String password;
    
    /**
     * 安全令牌
     */
    private String securityToken;
    
    /**
     * OAuth客户端ID
     */
    private String clientId;
    
    /**
     * OAuth客户端密钥
     */
    private String clientSecret;
    
    /**
     * OAuth授权类型
     */
    private String grantType;
    
    /**
     * Salesforce CLI组织别名
     */
    private String orgAlias;
    
    /**
     * 私有密钥路径
     */
    private String privateKeyPath;
    
    /**
     * 私有密钥密码
     */
    private String privateKeyPassword;
    
    /**
     * OAuth授权码
     */
    private String code;
    
    /**
     * OAuth state参数，用于防止CSRF攻击
     */
    private String state;
    
    /**
     * Session ID，用于Session ID登录方式
     */
    private String sessionId;

    /**
     * 登录URL，如https://login.salesforce.com
     */
    private String loginUrl;

    // getter和setter方法
    public String getLoginUrl() {
        return loginUrl;
    }

    // getter和setter方法
    public String setLoginUrl() {
        return loginUrl;
    }

    // getter和setter方法
    public String getLoginType() {
        return loginType;
    }
    
    public void setLoginType(String loginType) {
        this.loginType = loginType;
    }
    
    public String getUsername() {
        return username;
    }
    
    public void setUsername(String username) {
        this.username = username;
    }
    
    public String getPassword() {
        return password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    
    public String getSecurityToken() {
        return securityToken;
    }
    
    public void setSecurityToken(String securityToken) {
        this.securityToken = securityToken;
    }
    
    public String getClientId() {
        return clientId;
    }
    
    public void setClientId(String clientId) {
        this.clientId = clientId;
    }
    
    public String getClientSecret() {
        return clientSecret;
    }
    
    public void setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
    }
    
    public String getGrantType() {
        return grantType;
    }
    
    public void setGrantType(String grantType) {
        this.grantType = grantType;
    }
    
    public String getOrgAlias() {
        return orgAlias;
    }
    
    public void setOrgAlias(String orgAlias) {
        this.orgAlias = orgAlias;
    }
    
    public String getPrivateKeyPath() {
        return privateKeyPath;
    }
    
    public void setPrivateKeyPath(String privateKeyPath) {
        this.privateKeyPath = privateKeyPath;
    }
    
    public String getPrivateKeyPassword() {
        return privateKeyPassword;
    }
    
    public void setPrivateKeyPassword(String privateKeyPassword) {
        this.privateKeyPassword = privateKeyPassword;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getState() {
        return state;
    }
    
    public void setState(String state) {
        this.state = state;
    }
    
    public String getSessionId() {
        return sessionId;
    }
    
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
}