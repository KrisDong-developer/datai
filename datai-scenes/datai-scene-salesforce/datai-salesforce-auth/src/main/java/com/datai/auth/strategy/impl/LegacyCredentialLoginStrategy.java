package com.datai.auth.strategy.impl;

import com.datai.auth.model.domain.SalesforceLoginResult;
import com.datai.auth.model.domain.SalesforceLoginRequest;
import com.datai.auth.strategy.LoginStrategy;
import com.datai.common.utils.CacheUtils;
import com.datai.salesforce.common.constant.SalesforceConfigConstants;
import com.datai.salesforce.common.exception.SalesforceLegacyCredentialLoginException;
import jakarta.annotation.Resource;
import jakarta.xml.soap.*;
import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.Cache;
import org.springframework.stereotype.Component;
import com.datai.setting.config.SalesforceConfigCacheManager;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
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

    @Resource
    private SalesforceConfigCacheManager salesforceConfigCacheManager;

    private static final Logger logger = LoggerFactory.getLogger(LegacyCredentialLoginStrategy.class);

    private static final Map<String, String> ERROR_MESSAGE_MAP = new HashMap<>();

    static {
        ERROR_MESSAGE_MAP.put("LOGIN_MUST_USE_SECURITY_TOKEN", "用户名、密码或安全标记无效，或用户被锁定");
        ERROR_MESSAGE_MAP.put("INVALID_LOGIN", "用户名或密码错误");
        ERROR_MESSAGE_MAP.put("INVALID_OPERATION_WITH_EXPIRED_PASSWORD", "密码已过期，请重置密码");
        ERROR_MESSAGE_MAP.put("USER_LOCKED_OUT", "用户已被锁定，请联系管理员");
        ERROR_MESSAGE_MAP.put("PASSWORD_EXPIRED", "密码已过期");
        ERROR_MESSAGE_MAP.put("SERVER_UNAVAILABLE", "服务器暂时不可用，请稍后重试");
        ERROR_MESSAGE_MAP.put("API_DISABLED_FOR_ORG", "组织的API访问已禁用");
        ERROR_MESSAGE_MAP.put("UNKNOWN_EXCEPTION", "登录失败，请检查凭据或联系管理员");
    }

    @Override
    public SalesforceLoginResult login(SalesforceLoginRequest request) {
        logger.info("执行传统账密凭证登录，登录类型：{}", request.getLoginType());

        SalesforceLoginResult result = new SalesforceLoginResult();

        try {
            // 1. 验证请求参数
            validateRequest(request);

            // 2. 获取Salesforce配置
            Map<String, String> config = getSalesforceConfig(request.getLoginUrl());

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
        logger.info("执行传统账密凭证刷新Session操作");

        // 传统账密登录方式不支持刷新Session，需要重新登录
        SalesforceLoginResult result = new SalesforceLoginResult();
        result.setSuccess(false);
        result.setErrorMessage("Legacy credential login does not support session refresh");
        result.setErrorCode("REFRESH_NOT_SUPPORTED");

        return result;
    }

    @Override
    public boolean logout(String sessionId, String loginType) {
        logger.info("执行传统账密凭证登出操作");

        // 传统账密登录方式的登出操作主要是清理本地状态和调用Salesforce登出API
        try {
            // 1. 获取Salesforce配置
            Map<String, String> config = getSalesforceConfig(null);

            // 2. 调用Salesforce登出API
            boolean logoutSuccess = executeSoapLogout(sessionId, config);

            // 3. 清理本地缓存
            cleanupLocalCache(sessionId);

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
     * @param customLoginUrl 自定义登录URL，可为null
     * @return 配置信息Map
     * @throws SalesforceLegacyCredentialLoginException 配置获取失败时抛出
     */
    private Map<String, String> getSalesforceConfig(String customLoginUrl) throws SalesforceLegacyCredentialLoginException {
        // 1. 获取基础环境配置（建议统一从一个配置对象读取，减少重复的 CacheUtils.get 调用）
        String envCacheKey = salesforceConfigCacheManager.getEnvironmentCacheKey();

        // 提取配置，同时提供默认值处理
        String apiVersion = getCacheValue(envCacheKey, "salesforce.api.version", "65.0");
        String environmentType = getCacheValue(envCacheKey, "salesforce.environment.type", "production");
        String namespace = getCacheValue(envCacheKey, "salesforce.api.namespace", "urn:partner.soap.sforce.com"); // 修正：Partner API 通常使用这个命名空间

        // 2. 核心优化：处理 Endpoint URL
        String rawEndpoint = (StringUtils.isNotBlank(customLoginUrl))
                ? customLoginUrl.trim()
                : getEndpointUrl(environmentType);

        // 自动补全 Salesforce SOAP 路径 (核心修复)
        String finalEndpoint = formatSalesforceSoapEndpoint(rawEndpoint, apiVersion);

        logger.info("Final Salesforce SOAP Endpoint: {}", finalEndpoint);

        // 3. 构建配置结果
        Map<String, String> configMap = new HashMap<>();
        configMap.put("apiVersion", apiVersion);
        configMap.put("endpointUrl", finalEndpoint);
        configMap.put("namespace", namespace);
        configMap.put("environmentType", environmentType);
        configMap.put("bindingName", getCacheValue(envCacheKey, "salesforce.api.binding", null));
        configMap.put("portType", getCacheValue(envCacheKey, "salesforce.api.port_type", null));
        configMap.put("serviceName", getCacheValue(envCacheKey, "salesforce.api.service_name", null));

        return configMap;
    }

    /**
     * 辅助方法：确保 URL 包含正确的 SOAP 服务路径
     */
    private String formatSalesforceSoapEndpoint(String url, String version) {
        if (StringUtils.isBlank(url)) return url;

        // 如果只有域名，或者没有包含 services/Soap 路径
        if (!url.contains("/services/Soap/u/")) {
            // 去除结尾的反斜杠
            String baseUrl = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;
            // 自动拼装 Partner API 路径 (/u/ 代表 partner)
            return String.format("%s/services/Soap/u/%s", baseUrl, version);
        }
        return url;
    }

    private String getCacheValue(String key, String item, String defaultValue) {
        String val = CacheUtils.get(key, item, String.class);
        return (val != null) ? val : defaultValue;
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
            return CacheUtils.get(salesforceConfigCacheManager.getEnvironmentCacheKey(), "salesforce.api.endpoint.production", String.class);
        }

        switch (environmentType) {
            case "sandbox":
                return CacheUtils.get(salesforceConfigCacheManager.getEnvironmentCacheKey(), "salesforce.api.endpoint.sandbox", String.class);
            case "custom":
                return CacheUtils.get(salesforceConfigCacheManager.getEnvironmentCacheKey(), "salesforce.api.endpoint.custom", String.class);
            default:
                return CacheUtils.get(salesforceConfigCacheManager.getEnvironmentCacheKey(), "salesforce.api.endpoint.production", String.class);
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
        if (StringUtils.isBlank(endpointUrl) || StringUtils.isBlank(soapRequest)) {
            throw new IllegalArgumentException("Endpoint URL and SOAP request body are required");
        }

        logger.debug("Sending SOAP request to: {}. Payload size: {} bytes", endpointUrl, soapRequest.length());

        HttpURLConnection connection = null;
        try {
            URL url = new URL(endpointUrl);
            connection = (HttpURLConnection) url.openConnection();

            // 配置请求参数
            connection.setRequestMethod("POST");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(30000);
            connection.setDoOutput(true);

            // 设置 Header
            connection.setRequestProperty("Content-Type", "text/xml; charset=utf-8");
            connection.setRequestProperty("SOAPAction", "\"\"");
            connection.setRequestProperty("Accept", "text/xml");
            // 显式开启 Keep-Alive
            connection.setRequestProperty("Connection", "Keep-Alive");

            // 发送数据
            byte[] requestBytes = soapRequest.getBytes(StandardCharsets.UTF_8);
            connection.setFixedLengthStreamingMode(requestBytes.length); // 明确告知长度，提高性能
            try (OutputStream os = connection.getOutputStream()) {
                os.write(requestBytes);
            }

            int responseCode = connection.getResponseCode();

            // 读取响应
            try (InputStream is = (responseCode >= 200 && responseCode < 300)
                    ? connection.getInputStream()
                    : connection.getErrorStream()) {

                if (is == null) {
                    throw new SalesforceLegacyCredentialLoginException("HTTP_" + responseCode, "Empty response body from Salesforce");
                }

                // 使用包装后的读取方法，防止内存溢出或乱码
                String response = readStream(is);

                if (responseCode >= 300) {
                    handleErrorResponse(responseCode, response);
                }

                return response;
            }
        } catch (SocketTimeoutException e) {
            logger.error("Timeout connecting to Salesforce at {}", endpointUrl);
            throw new Exception("Salesforce connection timed out", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    /**
     * 提取辅助方法：流读取
     */
    private String readStream(InputStream is) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }

    /**
     * 提取辅助方法：解析错误逻辑
     */
    private void handleErrorResponse(int statusCode, String responseBody) throws SalesforceLegacyCredentialLoginException {
        SalesforceLoginResult errorDetail = parseErrorResponse(responseBody);
        logger.error("Salesforce SOAP Error. Status: {}, Code: {}, Message: {}",
                statusCode, errorDetail.getErrorCode(), errorDetail.getErrorMessage());

        throw new SalesforceLegacyCredentialLoginException(
                errorDetail.getErrorCode(),
                errorDetail.getErrorMessage()
        );
    }
    /**
     * 从 SOAP 错误响应中解析具体的异常信息
     */
    private SalesforceLoginResult parseErrorResponse(String errorXml) {
        SalesforceLoginResult errorResult = new SalesforceLoginResult();
        errorResult.setSuccess(false);
        errorResult.setErrorCode("UNKNOWN_SOAP_ERROR");
        errorResult.setErrorMessage("登录失败，请检查凭据或联系管理员");

        try {
            MessageFactory messageFactory = MessageFactory.newInstance(SOAPConstants.DYNAMIC_SOAP_PROTOCOL);
            try (InputStream is = new ByteArrayInputStream(errorXml.getBytes(StandardCharsets.UTF_8))) {
                SOAPMessage soapMessage = messageFactory.createMessage(new MimeHeaders(), is);
                SOAPBody body = soapMessage.getSOAPBody();

                if (body.hasFault()) {
                    SOAPFault fault = body.getFault();
                    String detailCode = extractDetailValue(fault, "exceptionCode");
                    String detailMsg = extractDetailValue(fault, "exceptionMessage");

                    String errorCode = detailCode != null ? detailCode : fault.getFaultCode();
                    errorResult.setErrorCode(errorCode);

                    String friendlyMessage = getFriendlyErrorMessage(errorCode);
                    errorResult.setErrorMessage(friendlyMessage);

                    logger.error("Salesforce SOAP Login Failed. Code: {}, Original Message: {}, Friendly Message: {}",
                            errorCode, detailMsg, friendlyMessage);
                }
            }
        } catch (Exception e) {
            logger.warn("Failed to parse SOAP Fault detail, falling back to default error message.");
        }
        return errorResult;
    }

    /**
     * 获取友好的错误消息
     *
     * @param errorCode 错误代码
     * @return 友好的中文错误消息
     */
    private String getFriendlyErrorMessage(String errorCode) {
        if (errorCode == null) {
            return "登录失败，请检查凭据或联系管理员";
        }

        String message = ERROR_MESSAGE_MAP.get(errorCode);
        if (message != null) {
            return message;
        }

        if (errorCode.startsWith("sf:")) {
            String codeWithoutPrefix = errorCode.substring(3);
            message = ERROR_MESSAGE_MAP.get(codeWithoutPrefix);
            if (message != null) {
                return message;
            }
        }

        return "登录失败，请检查凭据或联系管理员";
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
                        result.setSessionId(value);
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
                        .map(c -> CacheUtils.get(salesforceConfigCacheManager.getEnvironmentCacheKey(), "salesforce.session.timeout", String.class))
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
     * @param sessionId Session ID
     * @param config 配置信息
     * @return 登出是否成功
     * @throws Exception 登出失败时抛出
     */
    private boolean executeSoapLogout(String sessionId, Map<String, String> config) throws Exception {
        // 构建登出SOAP请求
        String soapLogoutRequest = buildSoapLogoutRequest(sessionId);

        // 发送登出请求
        String soapResponse = sendSoapRequest(soapLogoutRequest, config.get("endpointUrl"));

        // 解析登出响应
        return parseSoapLogoutResponse(soapResponse);
    }

    /**
     * 构建SOAP登出请求
     *
     * @param sessionId Session ID
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
     * @param sessionId Session ID
     */
    private void cleanupLocalCache(String sessionId) {
        // 清理与该Session相关的缓存
        try {
            // 这里可以添加具体的缓存清理逻辑
            logger.debug("清理本地缓存，Session ID: {}", sessionId.substring(0, Math.min(10, sessionId.length())) + "...");
        } catch (Exception e) {
            logger.error("清理本地缓存失败: {}", e.getMessage(), e);
        }
    }
}