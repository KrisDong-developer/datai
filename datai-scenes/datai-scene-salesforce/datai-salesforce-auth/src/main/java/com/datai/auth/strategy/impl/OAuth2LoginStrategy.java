package com.datai.auth.strategy.impl;

import com.datai.setting.config.SalesforceConfigCacheManager;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.datai.auth.domain.SalesforceLoginResult;
import com.datai.auth.domain.SalesforceLoginRequest;
import com.datai.auth.strategy.LoginStrategy;
import com.datai.common.utils.CacheUtils;
import com.datai.salesforce.common.constant.SalesforceConfigConstants;
import com.datai.salesforce.common.exception.SalesforceOAuthException;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * OAuth 2.0登录策略实现
 * 支持OAuth 2.0授权码流程、密码流程和客户端凭证流程
 * 
 * @author datai
 * @date 2025-12-14
 */
@Component
public class OAuth2LoginStrategy implements LoginStrategy {
    @Resource
    private SalesforceConfigCacheManager salesforceConfigCacheManager;
    
    private static final Logger logger = LoggerFactory.getLogger(OAuth2LoginStrategy.class);
    
    // Session撤销URL格式
    private static final String REVOKE_TOKEN_URL_FORMAT = "%s/services/oauth2/revoke";
    
    // PKCE相关常量
    private static final String CODE_VERIFIER_KEY = "oauth2_code_verifier";
    
    // 用于存储state参数和code_verifier的临时缓存
    private static final Map<String, String> STATE_CODE_VERIFIER_MAP = new ConcurrentHashMap<>();
    private static final Map<String, Long> STATE_TIMESTAMP_MAP = new ConcurrentHashMap<>();
    
    // State参数过期时间（5分钟）
    private static final long STATE_EXPIRATION_TIME = 5 * 60 * 1000;
    
    // Jackson ObjectMapper实例
    private final ObjectMapper objectMapper = new ObjectMapper();
    
    // 定期清理过期的state参数
//    static {
//        Thread cleanupThread = new Thread(() -> {
//            while (!Thread.currentThread().isInterrupted()) {
//                try {
//                    Thread.sleep(STATE_EXPIRATION_TIME);
//                    cleanupExpiredStates();
//                } catch (InterruptedException e) {
//                    Thread.currentThread().interrupt();
//                    break;
//                }
//            }
//        });
//        cleanupThread.setDaemon(true);
//        cleanupThread.start();
//    }
    
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
                    throw new SalesforceOAuthException("OAUTH2_UNSUPPORTED_GRANT_TYPE", "Unsupported grant type: " + request.getGrantType());
            }
            
            result.setSuccess(true);
        } catch (Exception e) {
            logger.error("OAuth 2.0登录失败: {}", e.getMessage(), e);
            result.setSuccess(false);
            if (e instanceof SalesforceOAuthException) {
                result.setErrorMessage(e.getMessage());
                result.setErrorCode(((SalesforceOAuthException) e).getErrorCode());
            } else {
                result.setErrorMessage(e.getMessage());
                result.setErrorCode("OAUTH2_LOGIN_FAILED");
            }
        }
        
        return result;
    }
    
    @Override
    public SalesforceLoginResult refreshToken(String refreshToken, String loginType) {
        logger.info("执行OAuth 2.0刷新Session操作");
        
        SalesforceLoginResult result = new SalesforceLoginResult();
        
        try {
            // 1. 验证必要参数
            if (refreshToken == null || refreshToken.trim().isEmpty()) {
                throw new SalesforceOAuthException("OAUTH2_MISSING_REFRESH_TOKEN", "Refresh session is required");
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
            JsonNode jsonResponse = objectMapper.readTree(response);
            
            // 6. 设置结果
            result.setSessionId(jsonResponse.get("access_token").asText());
            result.setTokenType(jsonResponse.get("token_type").asText());
            result.setExpiresIn(jsonResponse.get("expires_in").asLong());
            result.setInstanceUrl(jsonResponse.get("instance_url").asText());
            
            if (jsonResponse.has("id")) {
                result.setUserId(extractUserIdFromIdUrl(jsonResponse.get("id").asText()));
            }
            
            if (jsonResponse.has("organization_id")) {
                result.setOrganizationId(jsonResponse.get("organization_id").asText());
            }
            
            // 刷新Session可能不存在于响应中
            if (jsonResponse.has("refresh_token")) {
                result.setRefreshToken(jsonResponse.get("refresh_token").asText());
            }
            
            result.setSuccess(true);
            logger.info("OAuth 2.0刷新Session成功");
        } catch (Exception e) {
            logger.error("OAuth 2.0刷新Session失败: {}", e.getMessage(), e);
            result.setSuccess(false);
            if (e instanceof SalesforceOAuthException) {
                result.setErrorMessage(e.getMessage());
                result.setErrorCode(((SalesforceOAuthException) e).getErrorCode());
            } else {
                result.setErrorMessage(e.getMessage());
                result.setErrorCode("OAUTH2_REFRESH_FAILED");
            }
        }
        
        return result;
    }
    
    @Override
    public boolean logout(String sessionId, String loginType) {
        logger.info("执行OAuth 2.0登出操作");
        
        try {
            // 1. 获取Salesforce配置
            Map<String, String> config = getSalesforceConfig();
            
            // 2. 构建撤销Session URL
            String loginUrl = config.get("loginUrl");
            String instanceUrl = loginUrl.substring(0, loginUrl.indexOf("/services"));
            String revokeUrl = String.format(REVOKE_TOKEN_URL_FORMAT, instanceUrl);
            
            // 3. 构建请求参数
            Map<String, String> params = new HashMap<>();
            params.put("token", sessionId);
            
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
        Cache cache = CacheUtils.getCache(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY);
        if (cache == null) {
            throw new SalesforceOAuthException("OAUTH2_CONFIG_ERROR", "Salesforce config cache not found");
        }
        
        String apiVersion = CacheUtils.get(salesforceConfigCacheManager.getEnvironmentCacheKey(), "salesforce.api.version", String.class);
        String environmentType = CacheUtils.get(salesforceConfigCacheManager.getEnvironmentCacheKey(), "salesforce.environment.type", String.class);
        String loginUrl = getLoginUrl(environmentType);
        String clientId = CacheUtils.get(salesforceConfigCacheManager.getEnvironmentCacheKey(), "salesforce.oauth.client.id", String.class);
        String clientSecret = CacheUtils.get(salesforceConfigCacheManager.getEnvironmentCacheKey(), "salesforce.oauth.client.secret", String.class);
        String redirectUri = CacheUtils.get(salesforceConfigCacheManager.getEnvironmentCacheKey(), "salesforce.oauth.redirect.uri", String.class);
        
        // 验证必要配置
        if (clientId == null || clientId.trim().isEmpty()) {
            throw new SalesforceOAuthException("OAUTH2_MISSING_CLIENT_ID", "Salesforce OAuth client ID is not configured");
        }
        if (clientSecret == null || clientSecret.trim().isEmpty()) {
            throw new SalesforceOAuthException("OAUTH2_MISSING_CLIENT_SECRET", "Salesforce OAuth client secret is not configured");
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
                Cache cache = CacheUtils.getCache(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY);
                if (cache == null) {
                    throw new SalesforceOAuthException("OAUTH2_CONFIG_ERROR", "Salesforce config cache not found");
                }
                String customEndpoint = CacheUtils.get(salesforceConfigCacheManager.getEnvironmentCacheKey(), "salesforce.api.endpoint.custom", String.class);
                if (customEndpoint == null || customEndpoint.trim().isEmpty()) {
                    throw new SalesforceOAuthException("OAUTH2_INVALID_CUSTOM_ENDPOINT", "Custom endpoint is required for custom environment type");
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
        JsonNode jsonResponse = objectMapper.readTree(response);
        
        // 4. 创建结果
        SalesforceLoginResult result = new SalesforceLoginResult();
        result.setSessionId(jsonResponse.get("access_token").asText());
        result.setTokenType(jsonResponse.get("token_type").asText());
        result.setExpiresIn(jsonResponse.get("expires_in").asLong());
        result.setInstanceUrl(jsonResponse.get("instance_url").asText());
        
        if (jsonResponse.has("id")) {
            result.setUserId(extractUserIdFromIdUrl(jsonResponse.get("id").asText()));
        }
        
        if (jsonResponse.has("organization_id")) {
            result.setOrganizationId(jsonResponse.get("organization_id").asText());
        }
        
        // 刷新Session可能不存在于响应中
        if (jsonResponse.has("refresh_token")) {
            result.setRefreshToken(jsonResponse.get("refresh_token").asText());
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
        JsonNode jsonResponse = objectMapper.readTree(response);
        
        // 4. 创建结果
        SalesforceLoginResult result = new SalesforceLoginResult();
        result.setSessionId(jsonResponse.get("access_token").asText());
        result.setTokenType(jsonResponse.get("token_type").asText());
        result.setExpiresIn(jsonResponse.get("expires_in").asLong());
        result.setInstanceUrl(jsonResponse.get("instance_url").asText());
        
        if (jsonResponse.has("organization_id")) {
            result.setOrganizationId(jsonResponse.get("organization_id").asText());
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
        
        // 1. 检查是否提供了授权码
        String code = request.getCode();
        String state = request.getState();
        if (code == null || code.trim().isEmpty()) {
            throw new SalesforceOAuthException("OAUTH2_MISSING_AUTHORIZATION_CODE", "Authorization code is required for authorization_code grant type. Please redirect user to authorization URL first.");
        }
        
        // 2. 验证state参数，防止CSRF攻击
        if (state == null || state.trim().isEmpty()) {
            throw new SalesforceOAuthException("OAUTH2_MISSING_STATE", "State parameter is required for authorization_code grant type to prevent CSRF attacks.");
        }
        
        // 3. 验证state参数是否有效且未过期
        String codeVerifier = STATE_CODE_VERIFIER_MAP.get(state);
        Long timestamp = STATE_TIMESTAMP_MAP.get(state);
        if (codeVerifier == null || timestamp == null) {
            throw new SalesforceOAuthException("OAUTH2_INVALID_STATE", "Invalid or expired state parameter.");
        }
        
        // 4. 检查state是否过期
        if (System.currentTimeMillis() - timestamp > STATE_EXPIRATION_TIME) {
            // 清理过期的state
            STATE_CODE_VERIFIER_MAP.remove(state);
            STATE_TIMESTAMP_MAP.remove(state);
            throw new SalesforceOAuthException("OAUTH2_EXPIRED_STATE", "State parameter has expired.");
        }
        
        // 5. 构建请求参数
        Map<String, String> params = new HashMap<>();
        params.put("grant_type", "authorization_code");
        params.put("client_id", config.get("clientId"));
        params.put("client_secret", config.get("clientSecret"));
        params.put("redirect_uri", config.get("redirectUri"));
        params.put("code", code);
        
        // 6. 如果使用了PKCE，添加code_verifier参数
        if (codeVerifier != null && !codeVerifier.isEmpty()) {
            params.put("code_verifier", codeVerifier);
        }
        
        // 7. 发送请求
        String response = sendPostRequest(config.get("loginUrl"), params);
        
        // 8. 解析响应
        JsonNode jsonResponse = objectMapper.readTree(response);
        
        // 9. 创建结果
        SalesforceLoginResult result = new SalesforceLoginResult();
        result.setSessionId(jsonResponse.get("access_token").asText());
        result.setTokenType(jsonResponse.get("token_type").asText());
        result.setExpiresIn(jsonResponse.get("expires_in").asLong());
        result.setInstanceUrl(jsonResponse.get("instance_url").asText());
        
        if (jsonResponse.has("id")) {
            result.setUserId(extractUserIdFromIdUrl(jsonResponse.get("id").asText()));
        }
        
        if (jsonResponse.has("organization_id")) {
            result.setOrganizationId(jsonResponse.get("organization_id").asText());
        }
        
        // 刷新Session可能不存在于响应中
        if (jsonResponse.has("refresh_token")) {
            result.setRefreshToken(jsonResponse.get("refresh_token").asText());
        }
        
        // 10. 清理已使用的state
        STATE_CODE_VERIFIER_MAP.remove(state);
        STATE_TIMESTAMP_MAP.remove(state);
        
        return result;
    }
    
    /**
     * 生成授权URL，用于OAuth 2.0授权码流程的第一步
     * 
     * @param config 配置信息
     * @param usePkce 是否使用PKCE
     * @return 授权URL
     */
    public String generateAuthorizationUrl(Map<String, String> config, boolean usePkce) {
        try {
            StringBuilder authUrl = new StringBuilder();
            
            // 构建基础授权URL
            String authEndpoint = config.get("loginUrl").replace("/services/oauth2/token", "/services/oauth2/authorize");
            authUrl.append(authEndpoint);
            authUrl.append("?response_type=code");
            authUrl.append("&client_id=").append(URLEncoder.encode(config.get("clientId"), StandardCharsets.UTF_8.name()));
            authUrl.append("&redirect_uri=").append(URLEncoder.encode(config.get("redirectUri"), StandardCharsets.UTF_8.name()));
            
            // 添加随机state参数防止CSRF攻击
            String state = generateStateParameter();
            authUrl.append("&state=").append(state);
            
            // 如果启用PKCE，添加相关参数
            if (usePkce) {
                String codeVerifier = generateCodeVerifier();
                String codeChallenge = generateCodeChallenge(codeVerifier);
                
                // 将state和code_verifier关联保存到缓存中，以便后续使用
                STATE_CODE_VERIFIER_MAP.put(state, codeVerifier);
                STATE_TIMESTAMP_MAP.put(state, System.currentTimeMillis());
                
                authUrl.append("&code_challenge=").append(codeChallenge);
                authUrl.append("&code_challenge_method=S256");
            } else {
                // 即使不使用PKCE，也保存state以便验证
                STATE_CODE_VERIFIER_MAP.put(state, null);
                STATE_TIMESTAMP_MAP.put(state, System.currentTimeMillis());
            }
            
            logger.info("生成OAuth 2.0授权URL，使用PKCE: {}", usePkce);
            return authUrl.toString();
        } catch (Exception e) {
            logger.error("生成授权URL失败: {}", e.getMessage(), e);
            throw new SalesforceOAuthException("OAUTH2_GENERATE_AUTH_URL_FAILED", "Failed to generate authorization URL", e);
        }
    }
    
    /**
     * 生成code_verifier用于PKCE
     * 
     * @return code_verifier
     */
    private String generateCodeVerifier() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] codeVerifier = new byte[32];
        secureRandom.nextBytes(codeVerifier);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(codeVerifier);
    }
    
    /**
     * 根据code_verifier生成code_challenge
     * 
     * @param codeVerifier code_verifier
     * @return code_challenge
     * @throws NoSuchAlgorithmException 如果不支持SHA-256算法
     */
    private String generateCodeChallenge(String codeVerifier) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(codeVerifier.getBytes(StandardCharsets.US_ASCII));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(digest);
        } catch (NoSuchAlgorithmException e) {
            logger.error("生成code_challenge失败: {}", e.getMessage(), e);
            throw new SalesforceOAuthException("OAUTH2_GENERATE_CODE_CHALLENGE_FAILED", "Failed to generate code challenge", e);
        }
    }
    
    /**
     * 生成随机state参数防止CSRF攻击
     * 
     * @return state参数
     */
    private String generateStateParameter() {
        SecureRandom secureRandom = new SecureRandom();
        byte[] stateBytes = new byte[16];
        secureRandom.nextBytes(stateBytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(stateBytes);
    }
    
    /**
     * 清理过期的state参数
     */
    private static void cleanupExpiredStates() {
        long currentTime = System.currentTimeMillis();
        STATE_TIMESTAMP_MAP.entrySet().removeIf(entry -> 
            currentTime - entry.getValue() > STATE_EXPIRATION_TIME);
        
        // 清理对应的code_verifier映射
        STATE_CODE_VERIFIER_MAP.entrySet().removeIf(entry -> 
            !STATE_TIMESTAMP_MAP.containsKey(entry.getKey()));
        
        logger.debug("清理过期的state参数，剩余数量: {}", STATE_TIMESTAMP_MAP.size());
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
            throw new SalesforceOAuthException("OAUTH2_MISSING_URL", "URL is required");
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
                    .append(URLEncoder.encode(entry.getValue(), StandardCharsets.UTF_8.name()));
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
                JsonNode errorResponse = objectMapper.readTree(responseStr);
                String errorDescription = errorResponse.has("error_description") ?
                        errorResponse.get("error_description").asText() :
                        (errorResponse.has("error") ? errorResponse.get("error").asText() : "Unknown error");
                String errorCode = errorResponse.has("error") ? 
                        errorResponse.get("error").asText() : "OAUTH2_REQUEST_FAILED";
                throw new SalesforceOAuthException("OAUTH2_" + errorCode.toUpperCase(), errorDescription);
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
}