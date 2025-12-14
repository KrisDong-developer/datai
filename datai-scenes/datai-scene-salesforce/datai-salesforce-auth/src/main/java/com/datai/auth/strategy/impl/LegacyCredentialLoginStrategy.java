package com.datai.auth.strategy.impl;

import com.datai.auth.domain.SalesforceLoginResult;
import com.datai.auth.domain.SalesforceLoginRequest;
import com.datai.auth.strategy.LoginStrategy;
import com.datai.common.utils.CacheUtils;
import org.apache.xmlbeans.impl.soap.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;
import org.w3c.dom.traversal.NodeIterator;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Iterator;
import java.util.Map;

/**
 * 传统账密凭证登录策略实现
 * 支持使用用户名、密码和安全令牌进行登录
 * 支持SOAP登录方式
 * 
 * @author datai
 * @date 2025-12-14
 */
@Component
public class LegacyCredentialLoginStrategy implements LoginStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(LegacyCredentialLoginStrategy.class);

    @Override
    public SalesforceLoginResult login(SalesforceLoginRequest request) {
        logger.info("执行传统账密凭证登录，登录类型：{}", request.getLoginType());
        
        SalesforceLoginResult result = new SalesforceLoginResult();
        
        try {
            // 1. 验证请求参数
            validateRequest(request);
            
            // 2. 获取Salesforce配置
            Map<String, String> config = getSalesforceConfig();
            
            // 3. 构建SOAP请求
            String soapRequest = buildSoapLoginRequest(request, config);
            
            // 4. 发送SOAP请求
            String soapResponse = sendSoapRequest(soapRequest, config.get("endpointUrl"));
            
            // 5. 解析SOAP响应
            result = parseSoapResponse(soapResponse);
            result.setSuccess(true);
            
            logger.info("传统账密凭证登录成功，用户名: {}", request.getUsername());
        } catch (Exception e) {
            logger.error("传统账密凭证登录失败: {}", e.getMessage(), e);
            result.setSuccess(false);
            result.setErrorMessage(e.getMessage());
            result.setErrorCode("LEGACY_LOGIN_FAILED");
        }
        
        return result;
    }
    
    @Override
    public SalesforceLoginResult refreshToken(String refreshToken, String loginType) {
        logger.info("执行传统账密凭证刷新令牌操作");
        
        // 传统账密登录方式不支持刷新令牌，需要重新登录
        SalesforceLoginResult result = new SalesforceLoginResult();
        result.setSuccess(false);
        result.setErrorMessage("Legacy credential login does not support token refresh");
        result.setErrorCode("REFRESH_NOT_SUPPORTED");
        
        return result;
    }
    
    @Override
    public boolean logout(String accessToken, String loginType) {
        logger.info("执行传统账密凭证登出操作");
        
        // 传统账密登录方式的登出操作主要是清理本地状态和调用Salesforce登出API
        try {
            // 1. 获取Salesforce配置
            Map<String, String> config = getSalesforceConfig();
            
            // 2. 调用Salesforce登出API
            boolean logoutSuccess = executeSoapLogout(accessToken, config);
            
            // 3. 清理本地缓存
            cleanupLocalCache(accessToken);
            
            if (logoutSuccess) {
                logger.info("传统账密凭证登出成功");
            } else {
                logger.warn("传统账密凭证登出失败");
            }
            
            return logoutSuccess;
        } catch (Exception e) {
            logger.error("传统账密凭证登出失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public String getLoginType() {
        return "legacy_credential";
    }
    
    /**
     * 验证登录请求参数
     * 
     * @param request 登录请求
     * @throws IllegalArgumentException 参数验证失败时抛出
     */
    private void validateRequest(SalesforceLoginRequest request) {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new IllegalArgumentException("Username is required");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new IllegalArgumentException("Password is required");
        }
        if (request.getSecurityToken() == null || request.getSecurityToken().trim().isEmpty()) {
            throw new IllegalArgumentException("Security token is required");
        }
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
        String endpointUrl = getEndpointUrl(environmentType);
        String namespace = CacheUtils.get("salesforce_config_cache", "salesforce.api.namespace", String.class);
        String bindingName = CacheUtils.get("salesforce_config_cache", "salesforce.api.binding", String.class);
        String portType = CacheUtils.get("salesforce_config_cache", "salesforce.api.port_type", String.class);
        String serviceName = CacheUtils.get("salesforce_config_cache", "salesforce.api.service_name", String.class);
        
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
                "namespace", namespace,
                "bindingName", bindingName,
                "portType", portType,
                "serviceName", serviceName
        );
    }
    
    /**
     * 获取Salesforce API端点URL
     * 
     * @param environmentType 环境类型
     * @return 端点URL
     */
    private String getEndpointUrl(String environmentType) {
        Cache cache = CacheUtils.getCache("salesforce_config_cache");
        if (cache == null) {
            throw new RuntimeException("Salesforce config cache not found");
        }
        
        switch (environmentType) {
            case "sandbox":
                return CacheUtils.get("salesforce_config_cache", "salesforce.api.endpoint.sandbox", String.class);
            case "custom":
                return CacheUtils.get("salesforce_config_cache", "salesforce.api.endpoint.custom", String.class);
            default:
                return CacheUtils.get("salesforce_config_cache", "salesforce.api.endpoint.production", String.class);
        }
    }

    /**
     * 构建SOAP登录请求
     *
     * @param request 登录请求
     * @param config 配置信息
     * @return SOAP请求字符串
     */
    private String buildSoapLoginRequest(SalesforceLoginRequest request, Map<String, String> config) {
        // 组合密码和安全令牌
        String combinedPassword = request.getPassword() + request.getSecurityToken();

        // 格式化SOAP请求模板
        return "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
                "<env:Envelope xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
                "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "    xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "  <env:Body>\n" +
                "    <n1:login xmlns:n1=\"urn:partner.soap.sforce.com\">\n" +
                "      <n1:username>" + escapeXml(request.getUsername()) + "</n1:username>\n" +
                "      <n1:password>" + escapeXml(combinedPassword) + "</n1:password>\n" +
                "    </n1:login>\n" +
                "  </env:Body>\n" +
                "</env:Envelope>";
    }

    /**
     * 发送SOAP请求
     * 
     * @param soapRequest SOAP请求字符串
     * @param endpointUrl 端点URL
     * @return SOAP响应字符串
     * @throws Exception 发送请求失败时抛出
     */
    private String sendSoapRequest(String soapRequest, String endpointUrl) throws Exception {
        if (endpointUrl == null || endpointUrl.trim().isEmpty()) {
            throw new IllegalArgumentException("Endpoint URL is required");
        }
        
        logger.debug("发送SOAP请求到URL: {}", endpointUrl);
        logger.debug("SOAP请求内容: {}", soapRequest);
        
        URL url = new URL(endpointUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        
        // 设置请求属性
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
        connection.setRequestProperty("SOAPAction", "login");
        connection.setDoOutput(true);
        connection.setDoInput(true);
        
        // 发送请求
        try (var outputStream = connection.getOutputStream()) {
            byte[] requestBytes = soapRequest.getBytes(StandardCharsets.UTF_8);
            outputStream.write(requestBytes);
            outputStream.flush();
        }
        
        // 获取响应
        int responseCode = connection.getResponseCode();
        logger.debug("SOAP响应状态码: {}", responseCode);
        
        try (var inputStream = responseCode == HttpURLConnection.HTTP_OK ? 
                connection.getInputStream() : connection.getErrorStream()) {
            if (inputStream == null) {
                throw new IOException("No response from server");
            }
            
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            
            String response = result.toString(StandardCharsets.UTF_8);
            logger.debug("SOAP响应内容: {}", response);
            return response;
        } finally {
            connection.disconnect();
        }
    }
    
    /**
     * 解析SOAP响应
     * 
     * @param soapResponse SOAP响应字符串
     * @return 登录结果
     * @throws Exception 解析失败时抛出
     */
    private SalesforceLoginResult parseSoapResponse(String soapResponse) throws Exception {
        SalesforceLoginResult result = new SalesforceLoginResult();
        
        // 创建SOAP消息工厂
        MessageFactory messageFactory = MessageFactory.newInstance();
        
        // 创建SOAP消息
        try (InputStream inputStream = new ByteArrayInputStream(soapResponse.getBytes(StandardCharsets.UTF_8))) {
            SOAPMessage soapMessage = messageFactory.createMessage(null, inputStream);
            
            // 获取SOAPBody
            SOAPBody soapBody = soapMessage.getSOAPBody();
            
            // 检查是否有SOAP错误
            SOAPFault soapFault = soapBody.getFault();
            if (soapFault != null) {
                throw new Exception(soapFault.getFaultString());
            }
            
            // 解析登录响应
            SOAPElement loginResponse = (SOAPElement) soapBody.getFirstChild();
            if (loginResponse != null) {
                SOAPElement resultElement = (SOAPElement) loginResponse.getFirstChild();
                if (resultElement != null) {
                    // 遍历结果元素的子元素
                    Iterator childElements = resultElement.getChildElements();
                    for (SOAPElement childElement = (SOAPElement) childElements.next();
                         childElement != null; 
                         childElement = (SOAPElement) childElements.next()) {
                        String elementName = childElement.getLocalName();
                        String elementValue = childElement.getTextContent();
                        
                        switch (elementName) {
                            case "sessionId":
                                result.setAccessToken(elementValue);
                                break;
                            case "serverUrl":
                                // 从serverUrl中提取instanceUrl
                                int instanceUrlEndIndex = elementValue.indexOf("/services");
                                if (instanceUrlEndIndex > 0) {
                                    result.setInstanceUrl(elementValue.substring(0, instanceUrlEndIndex));
                                }
                                break;
                            case "userId":
                                result.setUserId(elementValue);
                                break;
                            case "organizationId":
                                result.setOrganizationId(elementValue);
                                break;
                            default:
                                // 忽略其他元素
                                break;
                        }
                    }
                }
            }
        }
        
        // 设置默认值
        if (result.getTokenType() == null) {
            result.setTokenType("Bearer");
        }
        if (result.getExpiresIn() == 0) {
            // 从配置中获取会话超时时间，默认7200秒
            Cache cache = CacheUtils.getCache("salesforce_config_cache");
            if (cache != null) {
                String sessionTimeout = CacheUtils.get("salesforce_config_cache", "salesforce.session.timeout", String.class);
                if (sessionTimeout != null) {
                    try {
                        result.setExpiresIn(Long.parseLong(sessionTimeout));
                    } catch (NumberFormatException e) {
                        logger.warn("无效的会话超时时间配置: {}, 使用默认值7200秒", sessionTimeout);
                        result.setExpiresIn(7200);
                    }
                } else {
                    result.setExpiresIn(7200);
                }
            } else {
                result.setExpiresIn(7200);
            }
        }
        
        return result;
    }
    
    /**
     * 执行SOAP登出操作
     * 
     * @param accessToken 访问令牌
     * @param config 配置信息
     * @return 登出是否成功
     * @throws Exception 登出失败时抛出
     */
    private boolean executeSoapLogout(String accessToken, Map<String, String> config) throws Exception {
        // 构建登出SOAP请求
        String soapLogoutRequest = buildSoapLogoutRequest(accessToken);
        
        // 发送登出请求
        String soapResponse = sendSoapRequest(soapLogoutRequest, config.get("endpointUrl"));
        
        // 解析登出响应
        return parseSoapLogoutResponse(soapResponse);
    }
    
    /**
     * 构建SOAP登出请求
     * 
     * @param sessionId 访问令牌
     * @return SOAP登出请求字符串
     */
    private String buildSoapLogoutRequest(String sessionId) {
        return "<?xml version=\"1.0\" encoding=\"utf-8\" ?>\n" +
                "<env:Envelope xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"\n" +
                "    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n" +
                "    xmlns:env=\"http://schemas.xmlsoap.org/soap/envelope/\">\n" +
                "  <env:Header>\n" +
                "    <n1:SessionHeader xmlns:n1=\"urn:partner.soap.sforce.com\">\n" +
                "      <n1:sessionId>" + escapeXml(sessionId) + "</n1:sessionId>\n" +
                "    </n1:SessionHeader>\n" +
                "  </env:Header>\n" +
                "  <env:Body>\n" +
                "    <n1:logout xmlns:n1=\"urn:partner.soap.sforce.com\" />\n" +
                "  </env:Body>\n" +
                "</env:Envelope>";
    }

    /**
     * 转义XML特殊字符
     *
     * @param input 输入字符串
     * @return 转义后的字符串
     */
    private String escapeXml(String input) {
        if (input == null) {
            return null;
        }
        return input.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&apos;");
    }
    /**
     * 解析SOAP登出响应
     * 
     * @param soapResponse SOAP响应字符串
     * @return 登出是否成功
     * @throws Exception 解析失败时抛出
     */
    private boolean parseSoapLogoutResponse(String soapResponse) throws Exception {
        MessageFactory messageFactory = MessageFactory.newInstance();
        
        try (InputStream inputStream = new ByteArrayInputStream(soapResponse.getBytes(StandardCharsets.UTF_8))) {
            SOAPMessage soapMessage = messageFactory.createMessage(null, inputStream);
            
            // 获取SOAPBody
            SOAPBody soapBody = soapMessage.getSOAPBody();
            
            // 检查是否有SOAP错误
            SOAPFault soapFault = soapBody.getFault();
            if (soapFault != null) {
                logger.error("SOAP登出失败: {}", soapFault.getFaultString());
                return false;
            }
            
            // 登出成功
            return true;
        }
    }
    
    /**
     * 清理本地缓存
     * 
     * @param accessToken 访问令牌
     */
    private void cleanupLocalCache(String accessToken) {
        // 清理与该令牌相关的缓存
        try {
            // 这里可以添加具体的缓存清理逻辑
            logger.debug("清理本地缓存，访问令牌: {}", accessToken.substring(0, Math.min(10, accessToken.length())) + "...");
        } catch (Exception e) {
            logger.error("清理本地缓存失败: {}", e.getMessage(), e);
        }
    }
}