package com.datai.auth.strategy.impl;

import cn.hutool.json.JSONObject;
import com.datai.auth.domain.SalesforceLoginResult;
import com.datai.auth.domain.SalesforceLoginRequest;
import com.datai.auth.strategy.LoginStrategy;
import com.datai.common.utils.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * OAuth 2.0登录策略实现
 * 支持OAuth 2.0授权码流程、密码流程和客户端凭证流程
 * 
 * @author datai
 * @date 2025-12-14
 */
@Component
public class OAuth2LoginStrategy implements LoginStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(OAuth2LoginStrategy.class);
    
    // 令牌撤销URL格式
    private static final String REVOKE_TOKEN_URL_FORMAT = "%s/services/oauth2/revoke";
    
    @Override
    public SalesforceLoginResult login(SalesforceLoginRequest request) {
        logger.info("执行OAuth 2.0登录，登录类型：{}，授权类型：{}", request.getLoginType(), request.getGrantType());
        
        // 这里实现具体的OAuth 2.0登录逻辑
        // 根据grantType不同，执行不同的授权流程
        SalesforceLoginResult result = new SalesforceLoginResult();
        
        try {
            // 1. 获取Salesforce配置
            Map<String, String> config = getSalesforceConfig();
            
            // 2. 根据授权类型执行不同的登录逻辑
            switch (request.getGrantType()) {
                case "password":
                    // 实现密码流程
                    result = executePasswordGrant(request, config);
                    break;
                case "client_credentials":
                    // 实现客户端凭证流程
                    result = executeClientCredentialsGrant(request, config);
                    break;
                case "authorization_code":
                    // 实现授权码流程
                    result = executeAuthorizationCodeGrant(request, config);
                    break;
                default:
                    throw new IllegalArgumentException("Unsupported grant type: " + request.getGrantType());
            }
            
            result.setSuccess(true);
        } catch (Exception e) {
            logger.error("OAuth 2.0登录失败: {}", e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
            result.setErrorCode("OAUTH2_LOGIN_FAILED");
        }
        
        return result;
    }
    
    @Override
    public SalesforceLoginResult refreshToken(String refreshToken, String loginType) {
        logger.info("执行OAuth 2.0刷新令牌操作");
        
        SalesforceLoginResult result = new SalesforceLoginResult();
        
        try {
            // 1. 验证必要参数
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                throw new IllegalArgumentException("Refresh token is required");
            }
            
            // 2. 获取Salesforce配置
            Map<String, String> config = getSalesforceConfig();
            
            // 3. 构建请求参数
            Map<String, String> params = new HashMap<>();
            params.put("grant_type", "refresh_token");
            params.put("refresh_token", refreshToken);
            params.put("client_id", config.get("clientId"));
            params.put("client_secret", config.get("clientSecret"));
            
            // 4. 发送请求
            String response = sendPostRequest(config.get("loginUrl"), params);
            
            // 5. 解析响应
            JSONObject jsonResponse = new JSONObject(response);
            
            // 6. 设置结果
            result.setAccessToken(jsonResponse.getStr("access_token"));
            result.setTokenType(jsonResponse.getStr("token_type"));
            result.setExpiresIn(jsonResponse.getLong("expires_in"));
            result.setInstanceUrl(jsonResponse.getStr("instance_url"));
            
            if (jsonResponse.containsKey("id")) {
                result.setUserId(extractUserIdFromIdUrl(jsonResponse.getStr("id")));
            }
            
            if (jsonResponse.containsKey("organization_id")) {
                result.setOrganizationId(jsonResponse.getStr("organization_id"));
            }
            
            result.setSuccess(true);
            logger.info("OAuth 2.0刷新令牌成功");
        } catch (Exception e) {
            logger.error("OAuth 2.0刷新令牌失败: {}", e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
            result.setErrorCode("OAUTH2_REFRESH_FAILED");
        }
        
        return result;
    }
    
    @Override
    public boolean logout(String accessToken, String loginType) {
        logger.info("执行OAuth 2.0登出操作");
        
        try {
            // 1. 获取Salesforce配置
            Map<String, String> config = getSalesforceConfig();
            
            // 2. 构建撤销令牌URL
            String loginUrl = config.get("loginUrl");
            String instanceUrl = loginUrl.substring(0, loginUrl.indexOf("/services"));
            String revokeUrl = String.format(REVOKE_TOKEN_URL_FORMAT, instanceUrl);
            
            // 3. 构建请求参数
            Map<String, String> params = new HashMap<>();
            params.put("token", accessToken);
            
            // 4. 发送请求
            sendPostRequest(revokeUrl, params);
            
            logger.info("OAuth 2.0登出成功");
            return true;
        } catch (Exception e) {
            logger.error("OAuth 2.0登出失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public String getLoginType() {
        return "oauth2";
    }
    
    /**
     * 获取Salesforce配置信息
     * 
     * @return 配置信息Map
     * @throws RuntimeException 配置获取失败时抛出
     */
    private Map<String, String> getSalesforceConfig() {
        Cache cache = CacheUtils.getCache("salesforce_config_cache");
        if (cache == null) {
            throw new RuntimeException("Salesforce config cache not found");
        }
        
        String apiVersion = CacheUtils.get("salesforce_config_cache", "salesforce.api.version", String.class);
        String environmentType = CacheUtils.get("salesforce_config_cache", "salesforce.environment.type", String.class);
        String loginUrl = getLoginUrl(environmentType);
        String clientId = CacheUtils.get("salesforce_config_cache", "salesforce.oauth.client.id", String.class);
        String clientSecret = CacheUtils.get("salesforce_config_cache", "salesforce.oauth.client.secret", String.class);
        String redirectUri = CacheUtils.get("salesforce_config_cache", "salesforce.oauth.redirect.uri", String.class);
        
        // 验证必要配置
        if (clientId == null || clientId.trim().isEmpty()) {
            throw new RuntimeException("Salesforce OAuth client ID is not configured");
        }
        if (clientSecret == null || clientSecret.trim().isEmpty()) {
            throw new RuntimeException("Salesforce OAuth client secret is not configured");
        }
        
        Map<String, String> config = new HashMap<>();
        config.put("apiVersion", apiVersion != null ? apiVersion : "65.0");
        config.put("loginUrl", loginUrl);
        config.put("clientId", clientId);
        config.put("clientSecret", clientSecret);
        config.put("redirectUri", redirectUri);
        
        return config;
    }
    
    /**
     * 获取Salesforce登录URL
     * 
     * @param environmentType 环境类型
     * @return 登录URL
     */
    private String getLoginUrl(String environmentType) {
        switch (environmentType) {
            case "sandbox":
                return "https://test.salesforce.com/services/oauth2/token";
            case "custom":
                Cache cache = CacheUtils.getCache("salesforce_config_cache");
                if (cache == null) {
                    throw new RuntimeException("Salesforce config cache not found");
                }
                String customEndpoint = CacheUtils.get("salesforce_config_cache", "salesforce.api.endpoint.custom", String.class);
                if (customEndpoint == null || customEndpoint.trim().isEmpty()) {
                    throw new IllegalArgumentException("Custom endpoint is required for custom environment type");
                }
                return customEndpoint + "/services/oauth2/token";
            default:
                return "https://login.salesforce.com/services/oauth2/token";
        }
    }
    
    /**
     * 执行密码流程登录
     * 
     * @param request 登录请求
     * @param config 配置信息
     * @return 登录结果
     * @throws Exception 执行失败时抛出
     */
    private SalesforceLoginResult executePasswordGrant(SalesforceLoginRequest request, Map<String, String> config) throws Exception {
        logger.debug("执行OAuth 2.0密码流程登录");
        
        // 1. 构建请求参数
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "password");
        params.put("client_id", config.get("clientId"));
        params.put("client_secret", config.get("clientSecret"));
        params.put("username", request.getUsername());
        
        // 组合密码和安全令牌
        String passwordWithToken = request.getPassword() + (request.getSecurityToken() != null ? request.getSecurityToken() : "");
        params.put("password", passwordWithToken);
        
        // 2. 发送请求
        String response = sendPostRequest(config.get("loginUrl"), params);
        
        // 3. 解析响应
        JSONObject jsonResponse = new JSONObject(response);
        
        // 4. 创建结果
        SalesforceLoginResult result = new SalesforceLoginResult();
        result.setAccessToken(jsonResponse.getStr("access_token"));
        if (jsonResponse.containsKey("refresh_token")) {
            result.setRefreshToken(jsonResponse.getStr("refresh_token"));
        }
        result.setTokenType(jsonResponse.getStr("token_type"));
        result.setExpiresIn(jsonResponse.getLong("expires_in"));
        result.setInstanceUrl(jsonResponse.getStr("instance_url"));
        
        if (jsonResponse.containsKey("id")) {
            result.setUserId(extractUserIdFromIdUrl(jsonResponse.getStr("id")));
        }
        
        if (jsonResponse.containsKey("organization_id")) {
            result.setOrganizationId(jsonResponse.getStr("organization_id"));
        }
        
        return result;
    }
    
    /**
     * 执行客户端凭证流程登录
     * 
     * @param request 登录请求
     * @param config 配置信息
     * @return 登录结果
     * @throws Exception 执行失败时抛出
     */
    private SalesforceLoginResult executeClientCredentialsGrant(SalesforceLoginRequest request, Map<String, String> config) throws Exception {
        logger.debug("执行OAuth 2.0客户端凭证流程登录");
        
        // 1. 构建请求参数
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "client_credentials");
        params.put("client_id", config.get("clientId"));
        params.put("client_secret", config.get("clientSecret"));
        
        // 2. 发送请求
        String response = sendPostRequest(config.get("loginUrl"), params);
        
        // 3. 解析响应
        JSONObject jsonResponse = new JSONObject(response);
        
        // 4. 创建结果
        SalesforceLoginResult result = new SalesforceLoginResult();
        result.setAccessToken(jsonResponse.getStr("access_token"));
        result.setTokenType(jsonResponse.getStr("token_type"));
        result.setExpiresIn(jsonResponse.getLong("expires_in"));
        result.setInstanceUrl(jsonResponse.getStr("instance_url"));
        
        if (jsonResponse.containsKey("organization_id")) {
            result.setOrganizationId(jsonResponse.getStr("organization_id"));
        }
        
        return result;
    }
    
    /**
     * 执行授权码流程登录
     * 
     * @param request 登录请求
     * @param config 配置信息
     * @return 登录结果
     * @throws Exception 执行失败时抛出
     */
    private SalesforceLoginResult executeAuthorizationCodeGrant(SalesforceLoginRequest request, Map<String, String> config) throws Exception {
        logger.debug("执行OAuth 2.0授权码流程登录");
        
        // 1. 构建请求参数
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "authorization_code");
        params.put("client_id", config.get("clientId"));
        params.put("client_secret", config.get("clientSecret"));
        params.put("redirect_uri", config.get("redirectUri"));
        params.put("code", extractAuthorizationCode(request));
        
        // 2. 发送请求
        String response = sendPostRequest(config.get("loginUrl"), params);
        
        // 3. 解析响应
        JSONObject jsonResponse = new JSONObject(response);
        
        // 4. 创建结果
        SalesforceLoginResult result = new SalesforceLoginResult();
        result.setAccessToken(jsonResponse.getStr("access_token"));
        if (jsonResponse.containsKey("refresh_token")) {
            result.setRefreshToken(jsonResponse.getStr("refresh_token"));
        }
        result.setTokenType(jsonResponse.getStr("token_type"));
        result.setExpiresIn(jsonResponse.getLong("expires_in"));
        result.setInstanceUrl(jsonResponse.getStr("instance_url"));
        
        if (jsonResponse.containsKey("id")) {
            result.setUserId(extractUserIdFromIdUrl(jsonResponse.getStr("id")));
        }
        
        if (jsonResponse.containsKey("organization_id")) {
            result.setOrganizationId(jsonResponse.getStr("organization_id"));
        }
        
        return result;
    }
    
    /**
     * 发送POST请求
     * 
     * @param url 请求URL
     * @param params 请求参数
     * @return 响应内容
     * @throws Exception 发送请求失败时抛出
     */
    private String sendPostRequest(String url, Map<String, String> params) throws Exception {
        if (url == null || url.trim().isEmpty()) {
            throw new IllegalArgumentException("URL is required");
        }
        
        logger.debug("发送POST请求到URL: {}", url);
        logger.debug("请求参数: {}", params);
        
        // 构建请求体
        StringBuilder requestBody = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            if (requestBody.length() > 0) {
                requestBody.append("&");
            }
            requestBody.append(entry.getKey()).append("=")
                    .append(java.net.URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8));
        }
        
        URL requestUrl = new URL(url);
        HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
        
        // 设置请求属性
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        
        // 发送请求
        try (OutputStream os = connection.getOutputStream()) {
            byte[] input = requestBody.toString().getBytes(StandardCharsets.UTF_8);
            os.write(input, 0, input.length);
        }
        
        // 获取响应
        int responseCode = connection.getResponseCode();
        logger.debug("响应状态码: {}", responseCode);
        
        try (BufferedReader br = new BufferedReader(
                new InputStreamReader(responseCode == 200 ? connection.getInputStream() : connection.getErrorStream(), StandardCharsets.UTF_8))) {
            StringBuilder response = new StringBuilder();
            String responseLine;
            while ((responseLine = br.readLine()) != null) {
                response.append(responseLine.trim());
            }
            
            String responseStr = response.toString();
            logger.debug("响应内容: {}", responseStr);
            
            if (responseCode != 200) {
                // 解析错误响应
                JSONObject errorResponse = new JSONObject(responseStr);
                String errorDescription = errorResponse.containsKey("error_description") ?
                        errorResponse.getStr("error_description") :
                        (errorResponse.containsKey("error") ? errorResponse.getStr("error") : "Unknown error");
                throw new RuntimeException(errorDescription);
            }
            
            return responseStr;
        } finally {
            connection.disconnect();
        }
    }
    
    /**
     * 从ID URL中提取用户ID
     * 
     * @param idUrl ID URL
     * @return 用户ID
     */
    private String extractUserIdFromIdUrl(String idUrl) {
        // ID URL格式: https://login.salesforce.com/id/00Dxx00000000123/005xx0000000456
        if (idUrl != null && idUrl.contains("/") && idUrl.lastIndexOf('/') > 0) {
            return idUrl.substring(idUrl.lastIndexOf('/') + 1);
        }
        return null;
    }
    
    /**
     * 从请求中提取授权码
     * 
     * @param request 登录请求
     * @return 授权码
     * @throws IllegalArgumentException 无法提取授权码时抛出
     */
    private String extractAuthorizationCode(SalesforceLoginRequest request) {
        String code = request.getCode();
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Authorization code is required for authorization_code grant type");
        }
        return code;
    }
}