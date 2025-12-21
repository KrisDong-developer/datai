package com.datai.auth.strategy.impl;

import com.datai.auth.domain.SalesforceLoginResult;
import com.datai.auth.domain.SalesforceLoginRequest;
import com.datai.auth.strategy.LoginStrategy;
import com.datai.common.utils.CacheUtils;
import com.datai.salesforce.common.constant.SalesforceConfigConstants;
import com.datai.salesforce.common.exception.SalesforceSessionIdLoginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Salesforce Session ID登录策略实现
 * 使用已有的Salesforce Session ID进行登录
 * 
 * @author datai
 * @date 2025-12-14
 */
@Component
public class SessionIdLoginStrategy implements LoginStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(SessionIdLoginStrategy.class);
    
    /**
     * 登录类型标识
     */
    private static final String LOGIN_TYPE = "session_id";
    
    /**
     * Salesforce Identity API路径
     */
    private static final String IDENTITY_API_PATH = "/services/oauth2/userinfo";
    
    @Override
    public SalesforceLoginResult login(SalesforceLoginRequest request) {
        logger.info("使用Session ID执行Salesforce登录");
        
        SalesforceLoginResult result = new SalesforceLoginResult();
        
        try {
            // 获取Session ID
            String sessionId = request.getSessionId();
            
            // 验证Session ID是否存在
            if (sessionId == null || sessionId.isEmpty()) {
                throw new SalesforceSessionIdLoginException("SESSION_ID_EMPTY", "Session ID不能为空");
            }
            
            // 获取Salesforce配置
            Map<String, String> config = getSalesforceConfig();
            
            // 构建Identity API URL
            String identityUrl = buildIdentityApiUrl(config);
            
            // 验证Session ID并获取用户信息
            Map<String, String> userInfo = validateSessionId(sessionId, identityUrl);
            
            // 构建登录结果
            result.setSuccess(true);
            result.setAccessToken(sessionId); // Session ID作为访问令牌
            result.setRefreshToken(null); // Session ID登录通常没有刷新令牌
            result.setInstanceUrl(userInfo.get("instance_url"));
            result.setOrganizationId(userInfo.get("organization_id"));
            result.setUserId(userInfo.get("user_id"));
            result.setTokenType("Bearer");
            result.setExpiresIn(getSessionTimeout(config));
            
            logger.info("Session ID登录成功，用户ID: {}", userInfo.get("user_id"));
            
        } catch (Exception e) {
            logger.error("Session ID登录失败: {}", e.getMessage(), e);
            result.setSuccess(false);
            if (e instanceof SalesforceSessionIdLoginException) {
                result.setErrorMessage(e.getMessage());
                result.setErrorCode(((SalesforceSessionIdLoginException) e).getErrorCode());
            } else {
                result.setErrorMessage("Session ID登录失败: " + e.getMessage());
                result.setErrorCode("session_id_LOGIN_FAILED");
            }
        }
        
        return result;
    }
    
    @Override
    public SalesforceLoginResult refreshToken(String refreshToken, String loginType) {
        logger.info("Session ID登录不支持刷新令牌");
        
        SalesforceLoginResult result = new SalesforceLoginResult();
        result.setSuccess(false);
        result.setErrorMessage("Session ID登录不支持刷新令牌");
        result.setErrorCode("REFRESH_TOKEN_NOT_SUPPORTED");
        
        return result;
    }
    
    @Override
    public boolean logout(String accessToken, String loginType) {
        logger.info("执行Session ID登出操作，访问令牌: {}", accessToken.substring(0, 10) + "...");
        
        try {
            // 获取Salesforce配置
            Map<String, String> config = getSalesforceConfig();
            
            // 调用Salesforce登出API
            boolean logoutSuccess = executeLogout(accessToken, config);
            
            logger.info("Session ID登出{}", logoutSuccess ? "成功" : "失败");
            return logoutSuccess;
        } catch (Exception e) {
            logger.error("Session ID登出失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public String getLoginType() {
        return LOGIN_TYPE;
    }
    
    /**
     * 获取Salesforce配置信息
     * 
     * @return 配置信息Map
     * @throws SalesforceSessionIdLoginException 配置获取失败时抛出
     */
    private Map<String, String> getSalesforceConfig() throws SalesforceSessionIdLoginException {
        Cache cache = CacheUtils.getCache(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY);
        if (cache == null) {
            throw new SalesforceSessionIdLoginException("CONFIG_NOT_FOUND", "Salesforce config cache not found");
        }
        
        String apiVersion = CacheUtils.get(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY, "salesforce.api.version", String.class);
        String environmentType = CacheUtils.get(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY, "salesforce.environment.type", String.class);
        String endpointUrl = getEndpointUrl(environmentType);
        String namespace = CacheUtils.get(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY, "salesforce.api.namespace", String.class);
        
        // 验证必要配置
        if (apiVersion == null) {
            apiVersion = "65.0"; // 默认值
        }
        if (namespace == null) {
            namespace = "http://soap.sforce.com/2006/08/apex"; // 默认值
        }
        
        return Map.of(
                "apiVersion", apiVersion,
                "environmentType", environmentType,
                "endpointUrl", endpointUrl,
                "namespace", namespace
        );
    }
    
    /**
     * 获取Salesforce API端点URL
     * 
     * @param environmentType 环境类型
     * @return 端点URL
     * @throws SalesforceSessionIdLoginException 配置获取失败时抛出
     */
    private String getEndpointUrl(String environmentType) throws SalesforceSessionIdLoginException {
        Cache cache = CacheUtils.getCache(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY);
        if (cache == null) {
            throw new SalesforceSessionIdLoginException("CONFIG_NOT_FOUND", "Salesforce config cache not found");
        }
        
        switch (environmentType) {
            case "sandbox":
                return CacheUtils.get(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY, "salesforce.api.endpoint.sandbox", String.class);
            case "custom":
                return CacheUtils.get(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY, "salesforce.api.endpoint.custom", String.class);
            default:
                return CacheUtils.get(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY, "salesforce.api.endpoint.production", String.class);
        }
    }
    
    /**
     * 构建Identity API URL
     * 
     * @param config Salesforce配置
     * @return Identity API URL
     */
    private String buildIdentityApiUrl(Map<String, String> config) {
        String environmentType = config.get("environmentType");
        String baseUrl;
        
        if ("sandbox".equals(environmentType)) {
            baseUrl = "https://test.salesforce.com";
        } else {
            baseUrl = "https://login.salesforce.com";
        }
        
        return baseUrl + IDENTITY_API_PATH;
    }
    
    /**
     * 验证Session ID并获取用户信息
     * 
     * @param sessionId Session ID
     * @param identityUrl Identity API URL
     * @return 用户信息Map
     * @throws Exception 验证失败时抛出
     */
    private Map<String, String> validateSessionId(String sessionId, String identityUrl) throws Exception {
        logger.debug("验证Session ID，调用Identity API: {}", identityUrl);
        
        URL url = new URL(identityUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        // 设置请求属性
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Authorization", "Bearer " + sessionId);
        connection.setRequestProperty("Accept", "application/json");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        
        // 获取响应
        int responseCode = connection.getResponseCode();
        logger.debug("Identity API响应状态码: {}", responseCode);
        
        try (InputStream inputStream = responseCode == HttpURLConnection.HTTP_OK ? 
                connection.getInputStream() : connection.getErrorStream()) {
            if (inputStream == null) {
                throw new IOException("No response from Identity API");
            }
            
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            
            String response = result.toString(StandardCharsets.UTF_8);
            logger.debug("Identity API响应内容: {}", response);
            
            if (responseCode != HttpURLConnection.HTTP_OK) {
                throw new SalesforceSessionIdLoginException("INVALID_SESSION_ID", "Invalid Session ID: " + response);
            }
            
            // 解析JSON响应
            return parseIdentityResponse(response);
        } finally {
            connection.disconnect();
        }
    }
    
    /**
     * 解析Identity API响应
     * 
     * @param response Identity API响应字符串
     * @return 解析后的用户信息Map
     * @throws Exception 解析失败时抛出
     */
    private Map<String, String> parseIdentityResponse(String response) throws Exception {
        // 使用Jackson或其他JSON库解析响应
        // 这里简化处理，实际项目中应使用JSON解析库
        // 例如: ObjectMapper mapper = new ObjectMapper();
        //       Map<String, String> userInfo = mapper.readValue(response, new TypeReference<Map<String, String>>() {});
        
        // 简化实现：从响应中提取关键信息
        String instanceUrl = extractValue(response, "instance_url");
        String organizationId = extractValue(response, "organization_id");
        String userId = extractValue(response, "user_id");
        
        if (instanceUrl == null || organizationId == null || userId == null) {
            throw new Exception("Invalid response from Identity API: missing required fields");
        }
        
        return Map.of(
                "instance_url", instanceUrl,
                "organization_id", organizationId,
                "user_id", userId
        );
    }
    
    /**
     * 从JSON字符串中提取值（简化实现）
     * 
     * @param json JSON字符串
     * @param key 键名
     * @return 值
     */
    private String extractValue(String json, String key) {
        String searchKey = "\"" + key + "\":";
        int startIndex = json.indexOf(searchKey);
        if (startIndex == -1) {
            return null;
        }
        
        startIndex += searchKey.length();
        int endIndex = json.indexOf(",", startIndex);
        if (endIndex == -1) {
            endIndex = json.indexOf("}", startIndex);
        }
        
        if (endIndex == -1) {
            return null;
        }
        
        String value = json.substring(startIndex, endIndex).trim();
        // 移除引号
        if (value.startsWith("\"")) {
            value = value.substring(1);
        }
        if (value.endsWith("\"")) {
            value = value.substring(0, value.length() - 1);
        }
        
        return value;
    }
    
    /**
     * 获取会话超时时间
     * 
     * @param config Salesforce配置
     * @return 会话超时时间（秒）
     */
    private long getSessionTimeout(Map<String, String> config) {
        Cache cache = CacheUtils.getCache(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY);
        if (cache != null) {
            String sessionTimeout = CacheUtils.get(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY, "salesforce.session.timeout", String.class);
            if (sessionTimeout != null) {
                try {
                    return Long.parseLong(sessionTimeout);
                } catch (NumberFormatException e) {
                    logger.warn("无效的会话超时时间配置: {}, 使用默认值7200秒", sessionTimeout);
                }
            }
        }
        return 7200; // 默认2小时
    }
    
    /**
     * 执行登出操作
     * 
     * @param accessToken 访问令牌
     * @param config Salesforce配置
     * @return 登出是否成功
     * @throws Exception 登出失败时抛出
     */
    private boolean executeLogout(String accessToken, Map<String, String> config) throws Exception {
        // 构建登出URL
        String logoutUrl = config.get("endpointUrl").replace("/services/Soap/u/", "/services/oauth2/revoke");
        
        URL url = new URL(logoutUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        // 设置请求属性
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        
        // 构建请求体
        String requestBody = "token=" + accessToken;
        
        // 发送请求
        try (var outputStream = connection.getOutputStream()) {
            byte[] requestBytes = requestBody.getBytes(StandardCharsets.UTF_8);
            outputStream.write(requestBytes);
            outputStream.flush();
        }
        
        // 获取响应
        int responseCode = connection.getResponseCode();
        logger.debug("登出API响应状态码: {}", responseCode);
        
        // 登出成功返回200或204
        return responseCode == HttpURLConnection.HTTP_OK || responseCode == 204;
    }
}