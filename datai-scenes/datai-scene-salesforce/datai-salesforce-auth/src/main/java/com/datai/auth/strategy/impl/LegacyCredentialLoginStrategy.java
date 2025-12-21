package com.datai.auth.strategy.impl;

import com.datai.auth.domain.SalesforceLoginResult;
import com.datai.auth.domain.SalesforceLoginRequest;
import com.datai.auth.strategy.LoginStrategy;
import com.datai.common.utils.CacheUtils;
import com.datai.salesforce.common.constant.SalesforceConfigConstants;
import com.datai.salesforce.common.exception.SalesforceLegacyCredentialLoginException;
import jakarta.xml.soap.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Optional;

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
            if (e instanceof SalesforceLegacyCredentialLoginException) {
                result.setErrorMessage(e.getMessage());
                result.setErrorCode(((SalesforceLegacyCredentialLoginException) e).getErrorCode());
            } else {
                result.setErrorMessage(e.getMessage());
                result.setErrorCode("LEGACY_LOGIN_FAILED");
            }
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
     * @throws SalesforceLegacyCredentialLoginException 参数验证失败时抛出
     */
    private void validateRequest(SalesforceLoginRequest request) throws SalesforceLegacyCredentialLoginException {
        if (request.getUsername() == null || request.getUsername().trim().isEmpty()) {
            throw new SalesforceLegacyCredentialLoginException("MISSING_USERNAME", "Username is required");
        }
        if (request.getPassword() == null || request.getPassword().trim().isEmpty()) {
            throw new SalesforceLegacyCredentialLoginException("MISSING_PASSWORD", "Password is required");
        }
    }
    
    /**
     * 获取Salesforce配置信息
     * 
     * @return 配置信息Map
     * @throws SalesforceLegacyCredentialLoginException 配置获取失败时抛出
     */
    private Map<String, String> getSalesforceConfig() throws SalesforceLegacyCredentialLoginException {
        Cache cache = CacheUtils.getCache(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY);
        if (cache == null) {
            throw new SalesforceLegacyCredentialLoginException("CONFIG_NOT_FOUND", "Salesforce config cache not found");
        }
        
        String apiVersion = CacheUtils.get(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY, "salesforce.api.version", String.class);
        String environmentType = CacheUtils.get(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY, "salesforce.environment.type", String.class);
        String endpointUrl = getEndpointUrl(environmentType);
        String namespace = CacheUtils.get(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY, "salesforce.api.namespace", String.class);
        String bindingName = CacheUtils.get(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY, "salesforce.api.binding", String.class);
        String portType = CacheUtils.get(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY, "salesforce.api.port_type", String.class);
        String serviceName = CacheUtils.get(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY, "salesforce.api.service_name", String.class);
        
        // 验证必要配置
        if (apiVersion == null) {
            apiVersion = "65.0"; // 默认值
        }
        if (namespace == null) {
            namespace = "http://soap.sforce.com/2006/08/apex"; // 默认值
        }
        
        // 使用HashMap替代Map.of()以允许null值
        Map<String, String> configMap = new HashMap<>();
        configMap.put("apiVersion", apiVersion);
        configMap.put("environmentType", environmentType);
        configMap.put("endpointUrl", endpointUrl);
        configMap.put("namespace", namespace);
        configMap.put("bindingName", bindingName);
        configMap.put("portType", portType);
        configMap.put("serviceName", serviceName);
        
        return configMap;
    }
    
    /**
     * 获取Salesforce API端点URL
     * 
     * @param environmentType 环境类型
     * @return 端点URL
     * @throws SalesforceLegacyCredentialLoginException 配置获取失败时抛出
     */
    private String getEndpointUrl(String environmentType) throws SalesforceLegacyCredentialLoginException {
        Cache cache = CacheUtils.getCache(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY);
        if (cache == null) {
            throw new SalesforceLegacyCredentialLoginException("CONFIG_NOT_FOUND", "Salesforce config cache not found");
        }
        
        if (environmentType == null) {
            return CacheUtils.get(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY, "salesforce.api.endpoint.production", String.class);
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
     * 构建SOAP登录请求 (基于 Apache Commons Text 优化版)
     *
     * @param request 登录请求对象
     * @param config  额外的配置信息 (如命名空间版本)
     * @return 安全的 SOAP 请求字符串
     */
    private String buildSoapLoginRequest(SalesforceLoginRequest request, Map<String, String> config) {
        // 1. 组合密码和安全令牌 (Salesforce 要求：Password + SecurityToken)
        String rawPassword = StringUtils.defaultString(request.getPassword())
                + StringUtils.defaultString(request.getSecurityToken());

        // 2. 对输入参数进行 XML 转义，防止非法字符破坏 XML 结构
        // escapeXml11 可以处理大部分 XML 实体及高位字符
        String safeUsername = StringEscapeUtils.escapeXml11(request.getUsername());
        String safePassword = StringEscapeUtils.escapeXml11(rawPassword);

        // 3. 使用 Java 文本块定义模板 (Java 15+)
        // 如果是低版本 Java，请使用 StringBuilder
        String soapTemplate = """
        <?xml version="1.0" encoding="utf-8" ?>
        <env:Envelope xmlns:xsd="http://www.w3.org/2001/XMLSchema"
                      xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
                      xmlns:env="http://schemas.xmlsoap.org/soap/envelope/">
          <env:Body>
            <n1:login xmlns:n1="urn:partner.soap.sforce.com">
              <n1:username>%s</n1:username>
              <n1:password>%s</n1:password>
            </n1:login>
          </env:Body>
        </env:Envelope>
        """;

        // 4. 填充并返回
        return String.format(soapTemplate, safeUsername, safePassword);
    }

    private String sendSoapRequest(String soapRequest, String endpointUrl) throws Exception {
        if (StringUtils.isBlank(endpointUrl)) {
            throw new IllegalArgumentException("Endpoint URL is required");
        }

        logger.debug("Sending SOAP request to: {}", endpointUrl);

        URL url = new URL(endpointUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();

        try {
            // 1. 设置关键 Header
            connection.setRequestMethod("POST");
            // 核心修复：Salesforce Partner API 要求 SOAPAction 为空字符串双引号
            connection.setRequestProperty("SOAPAction", "\"\"");
            connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            connection.setRequestProperty("Accept", "text/xml");

            // 2. 增加超时控制 (防止网络抖动挂死应用)
            connection.setConnectTimeout(10000); // 10s
            connection.setReadTimeout(30000);    // 30s

            connection.setDoOutput(true);
            connection.setDoInput(true);

            // 3. 发送请求
            try (OutputStream os = connection.getOutputStream()) {
                os.write(soapRequest.getBytes(StandardCharsets.UTF_8));
                os.flush();
            }

            int responseCode = connection.getResponseCode();

            // 4. 读取响应 (统一处理正常和错误流)
            boolean isSuccess = (responseCode >= 200 && responseCode < 300);
            try (InputStream is = isSuccess ? connection.getInputStream() : connection.getErrorStream()) {
                if (is == null) {
                    throw new SalesforceLegacyCredentialLoginException("HTTP_" + responseCode, "Server returned no content.");
                }

                String response = new String(is.readAllBytes(), StandardCharsets.UTF_8);

                if (!isSuccess) {
                    // --- 核心优化点：提取结构化错误信息 ---
                    SalesforceLoginResult errorDetail = parseErrorResponse(response);

                    logger.error("Salesforce SOAP Login Failed. Status: {}, Code: {}, Message: {}",
                            responseCode, errorDetail.getErrorCode(), errorDetail.getErrorMessage());

                    // 抛出带有“易读消息”的自定义异常
                    throw new SalesforceLegacyCredentialLoginException(
                            errorDetail.getErrorCode(),
                            errorDetail.getErrorMessage()
                    );
                }

                return response;
            }
        } finally {
            connection.disconnect();
        }
    }

    /**
     * 从 SOAP 错误响应中解析具体的异常信息
     */
    private SalesforceLoginResult parseErrorResponse(String errorXml) {
        SalesforceLoginResult errorResult = new SalesforceLoginResult();
        errorResult.setSuccess(false);
        errorResult.setErrorCode("UNKNOWN_SOAP_ERROR");
        errorResult.setErrorMessage(errorXml); // 默认放全文，防止解析失败

        try {
            MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.DYNAMIC_SOAP_PROTOCOL);
            try (InputStream is = new ByteArrayInputStream(errorXml.getBytes(StandardCharsets.UTF_8))) {
                SOAPMessage soapMessage = messageFactory.createMessage(new MimeHeaders(), is);
                SOAPBody body = soapMessage.getSOAPBody();

                if (body.hasFault()) {
                    SOAPFault fault = body.getFault();
                    // 1. 尝试获取 exceptionCode (通常在 detail 节点下)
                    String detailCode = extractDetailValue(fault, "exceptionCode");
                    // 2. 尝试获取 exceptionMessage
                    String detailMsg = extractDetailValue(fault, "exceptionMessage");

                    errorResult.setErrorCode(detailCode != null ? detailCode : fault.getFaultCode());
                    errorResult.setErrorMessage(detailMsg != null ? detailMsg : fault.getFaultString());
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to parse SOAP Fault detail, falling back to raw response.");
        }
        return errorResult;
    }

    /**
     * 辅助方法：从 Fault Detail 中提取指定标签的值
     */
    private String extractDetailValue(SOAPFault fault, String tagName) {
        Detail detail = fault.getDetail();
        if (detail == null) return null;

        Iterator<?> it = detail.getChildElements();
        while (it.hasNext()) {
            Object obj = it.next();
            if (obj instanceof SOAPElement) {
                SOAPElement el = (SOAPElement) obj;
                // 递归查找一层，因为 Salesforce 的 Fault 结构通常是 Detail -> UnexpectedErrorFault -> exceptionCode
                Iterator<?> subIt = el.getChildElements();
                while (subIt.hasNext()) {
                    Object subObj = subIt.next();
                    if (subObj instanceof SOAPElement) {
                        SOAPElement subEl = (SOAPElement) subObj;
                        if (tagName.equals(subEl.getLocalName())) {
                            return subEl.getTextContent();
                        }
                    }
                }
            }
        }
        return null;
    }

    /**
     * 解析SOAP响应 (优化版)
     *
     * @param soapResponse SOAP响应字符串
     * @return 登录结果
     * @throws Exception 解析失败或登录失败时抛出
     */
    private SalesforceLoginResult parseSoapResponse(String soapResponse) throws Exception {
        SalesforceLoginResult result = new SalesforceLoginResult();

        // 1. 创建并配置 MimeHeaders (核心修复点)
        MimeHeaders headers = new MimeHeaders();
        // 必须告诉 SAAJ 实现类内容的类型，否则它不知道该用哪个 Message 对象去解析
        headers.addHeader("Content-Type", "text/xml; charset=utf-8");

        // 使用标准工厂
        MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.DYNAMIC_SOAP_PROTOCOL);

        try (InputStream inputStream = new ByteArrayInputStream(soapResponse.getBytes(StandardCharsets.UTF_8))) {
            // 2. 传入配置好的 headers
            SOAPMessage soapMessage = messageFactory.createMessage(headers, inputStream);
            SOAPBody soapBody = soapMessage.getSOAPBody();

            // 3. 检查 SOAP Fault (处理凭据错误、锁定等)
            if (soapBody.hasFault()) {
                // 这里可以调用之前建议的错误解析逻辑，或者简单的解析
                SOAPFault fault = soapBody.getFault();
                String faultCode = fault.getFaultCode();
                String faultString = fault.getFaultString();

                logger.error("Salesforce SOAP Login Fault: {} - {}", faultCode, faultString);
                throw new SalesforceLegacyCredentialLoginException(faultCode, faultString);
            }

            // 2. 定位 <result> 节点
            // Salesforce 的响应通常是 <loginResponse><result>...</result></loginResponse>
            SOAPElement resultElement = findElementRecursive(soapBody, "result");

            if (resultElement == null) {
                logger.error("未在响应中找到 <result> 节点，原始响应: {}", soapResponse);
                throw new Exception("Invalid Salesforce SOAP response structure.");
            }

            // 3. 循环解析字段
            Iterator<Node> fields = resultElement.getChildElements();
            while (fields.hasNext()) {
                Node node = fields.next();
                if (!(node instanceof SOAPElement)) continue;

                SOAPElement field = (SOAPElement) node;
                String name = field.getLocalName();
                String value = field.getTextContent();

                if (StringUtils.isBlank(value) && !"userInfo".equalsIgnoreCase(name)) continue;

                switch (name) {
                    case "sessionId":
                        result.setAccessToken(value);
                        break;
                    case "serverUrl":
                        extractInstanceUrl(result, value);
                        break;
                    case "metadataServerUrl":
                        result.setMetadataServerUrl(value);
                        break;
                    case "userId":
                        result.setUserId(value);
                        break;
                    case "organizationId":
                        result.setOrganizationId(value);
                        break;
                    case "passwordExpired":
                        result.setPasswordExpired(Boolean.parseBoolean(value));
                        break;
                    case "sandbox":
                        result.setSandbox(Boolean.parseBoolean(value));
                        break;
                    case "userInfo":
                        parseUserInfo(result, field);
                        break;
                    default:
                        // 记录未预期但可能重要的字段
                        logger.debug("Skip unexpected SOAP field: {}", name);
                        break;
                }
            }
        }

        // 4. 后置处理：设置默认过期时间和 Token 类型
        fillDefaultExpiration(result);
        return result;
    }

    /**
     * 递归/深度查找指定的 SOAP 元素 (解决换行符和嵌套问题)
     */
    private SOAPElement findElementRecursive(SOAPElement parent, String localName) {
        Iterator<Node> it = parent.getChildElements();
        while (it.hasNext()) {
            Node node = it.next();
            if (node instanceof SOAPElement) {
                SOAPElement el = (SOAPElement) node;
                if (localName.equalsIgnoreCase(el.getLocalName())) {
                    return el;
                }
                // 继续向下查找一层 (如 loginResponse -> result)
                SOAPElement found = findElementRecursive(el, localName);
                if (found != null) return found;
            }
        }
        return null;
    }

    /**
     * 解析深层嵌套的 UserInfo 结构
     */
    private void parseUserInfo(SalesforceLoginResult result, SOAPElement userInfoNode) {
        Iterator<Node> it = userInfoNode.getChildElements();
        while (it.hasNext()) {
            Node node = it.next();
            if (node instanceof SOAPElement) {
                SOAPElement sub = (SOAPElement) node;
                String name = sub.getLocalName();
                String value = sub.getTextContent();

                switch (name) {
                    case "userFullName": result.setUserFullName(value); break;
                    case "userEmail": result.setUserEmail(value); break;
                    case "organizationName": result.setOrganizationName(value); break;
                    case "userLanguage": result.setLanguage(value); break;
                    case "userTimeZone": result.setTimeZone(value); break;
                }
            }
        }
    }

    /**
     * 提取 Instance URL
     */
    private void extractInstanceUrl(SalesforceLoginResult result, String serverUrl) {
        // 示例: https://ap24.salesforce.com/services/Soap/u/58.0
        int index = serverUrl.indexOf("/services");
        if (index > 0) {
            result.setInstanceUrl(serverUrl.substring(0, index));
        } else {
            result.setInstanceUrl(serverUrl);
        }
    }

    /**
     * 提取辅助方法：设置过期时间
     */
    private void fillDefaultExpiration(SalesforceLoginResult result) {
        if (result.getTokenType() == null) {
            result.setTokenType("Bearer");
        }

        long expiresIn = 7200L; // 默认值

        if (result.getExpiresIn() == 0) {
            try {
                // 假设 CacheUtils.get 返回的是 Object 或者可能为 null
                // 使用 Optional 避免深层 if-else
                expiresIn = Optional.ofNullable(CacheUtils.getCache(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY))
                        .map(c -> CacheUtils.get(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY, "salesforce.session.timeout", String.class))
                        .filter(StringUtils::isNumeric) // 确保是数字
                        .map(Long::parseLong)
                        .orElse(7200L);
            } catch (Exception e) {
                logger.warn("读取Salesforce会话超时配置失败，使用默认值: 7200", e);
            }
            result.setExpiresIn(expiresIn);
        }
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
     * 解析SOAP登出响应 (优化版)
     * * @param soapResponse SOAP响应字符串
     * @return 登出是否成功
     * @throws Exception 解析错误时抛出异常
     */
    private boolean parseSoapLogoutResponse(String soapResponse) throws Exception {
        if (soapResponse == null || soapResponse.trim().isEmpty()) {
            logger.warn("SOAP登出响应内容为空");
            return false;
        }

        // 1. 使用 MessageFactory.newInstance() 获取标准实现工厂
        // 使用 DYNAMIC_SOAP_PROTOCOL 兼容 SOAP 1.1 和 1.2
        MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.DYNAMIC_SOAP_PROTOCOL);

        try (InputStream inputStream = new ByteArrayInputStream(soapResponse.getBytes(StandardCharsets.UTF_8))) {
            // 2. 指定 MimeHeaders 以避免某些解析器的兼容性问题
            SOAPMessage soapMessage = messageFactory.createMessage(new MimeHeaders(), inputStream);

            // 3. 检查 Body 是否存在
            SOAPBody soapBody = soapMessage.getSOAPBody();
            if (soapBody == null) {
                logger.error("SOAP响应Body缺失");
                return false;
            }

            // 4. 检查 SOAP Fault (显式错误)
            if (soapBody.hasFault()) {
                SOAPFault soapFault = soapBody.getFault();
                logger.error("Salesforce登出失败 [FaultCode: {}, FaultString: {}]",
                        soapFault.getFaultCode(),
                        soapFault.getFaultString());
                return false;
            }

            // 5. 业务逻辑校验
            // Salesforce 的 logout 响应通常包含一个 logoutResponse 元素
            // 如果没有 Fault，基本可以判定为 HTTP 200 OK，即登出成功
            logger.info("Salesforce会话已成功注销");
            return true;

        } catch (SOAPException e) {
            logger.error("解析SOAP响应时发生XML语法错误: {}", e.getMessage());
            throw new Exception("SOAP解析失败", e);
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