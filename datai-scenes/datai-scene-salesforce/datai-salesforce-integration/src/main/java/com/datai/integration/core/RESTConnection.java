package com.datai.integration.core;

import com.datai.salesforce.common.constant.SalesforceConstants;
import com.datai.salesforce.common.constant.SalesforceConnectionConstants;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sforce.async.AsyncApiException;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.StatusCode;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.beanutils.DynaProperty;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * REST连接类 - 提供与Salesforce REST API的交互功能
 *
 * 这个类封装了与Salesforce REST API的交互功能，主要用于Composite REST操作。
 * 设计目标是减少对外部依赖的依赖，使类更加独立和可重用。
 *
 * @author datai
 * @since 1.0.0
 */
@Slf4j
public class RESTConnection implements IRESTConnection {

    private final ConnectorConfig connectorConfig;
    private String apiVersion = SalesforceConnectionConstants.DEFAULT_API_VERSION;
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * 构造函数 - 创建RESTConnection实例
     *
     * @param config 连接器配置
     * @throws AsyncApiException 异步API异常
     */
    public RESTConnection(ConnectorConfig config) throws AsyncApiException {
        this.connectorConfig = config;
    }

    /**
     * 设置API版本
     *
     * @param version API版本号
     */
    public void setApiVersion(String version) {
        this.apiVersion = version;
    }

    /**
     * 执行加载操作 (Composite Collections API)
     * 支持 update, delete, insert (patch/post/delete)
     */
    public SaveResult[] loadAction(String sessionId, ACTION_ENUM action, List<DynaBean> dynabeans) throws ConnectionException {
        if (dynabeans == null || dynabeans.isEmpty()) {
            return new SaveResult[0];
        }

        String actionName = action.name();
        log.debug("开始执行 SObject 批量操作: {}, 记录数: {}", actionName, dynabeans.size());

        try {
            Map<String, Object> batchRecords = getSobjectMapForCompositeREST(dynabeans);
            String jsonPayload = objectMapper.writeValueAsString(batchRecords);

            String endpoint = buildCompositeSobjectsEndpoint();
            String httpMethod = getHttpMethodForAction(action);
            String url = endpoint;

            if (action == ACTION_ENUM.DELETE) {
                List<String> ids = extractIds(dynabeans);
                url = url + "?ids=" + String.join(",", ids) + "&allOrNone=false";
                jsonPayload = null;
            }

            Map<String, String> headers = createRestHeaders(sessionId);
            SimpleHttpResponse response = sendHttpRequest(url, headers, httpMethod, jsonPayload);

            if (!response.isSuccessful()) {
                log.warn("Salesforce REST 请求失败 [Code: {}], 响应: {}", response.getResponseCode(), response.getContentString());
                throw new ConnectionException("REST API 调用失败: " + response.getResponseCode() + " " + response.getContentString());
            }

            return parseSaveResults(response.getContent());

        } catch (IOException e) {
            log.error("REST 操作 IO/JSON 解析错误: {}", actionName, e);
            throw new ConnectionException("无法处理请求数据或响应", e);
        } catch (Exception e) {
            log.error("REST 操作发生非预期错误: {}", actionName, e);
            if (e instanceof ConnectionException) throw (ConnectionException) e;
            throw new ConnectionException("操作失败: " + e.getMessage(), e);
        }
    }

    private String buildCompositeSobjectsEndpoint() {
        String endpoint = connectorConfig.getRestEndpoint();
        if (endpoint == null || endpoint.isEmpty()) {
            throw new IllegalStateException("REST endpoint is not configured");
        }
        
        if (!endpoint.contains("/composite/sobjects")) {
            if (endpoint.endsWith("/")) {
                endpoint = endpoint + "composite/sobjects";
            } else {
                endpoint = endpoint + "/composite/sobjects";
            }
        }
        
        return endpoint;
    }

    private String getHttpMethodForAction(ACTION_ENUM action) {
        switch (action) {
            case DELETE:
                return SalesforceConstants.HTTP_METHOD_DELETE;
            case UPDATE:
                return SalesforceConstants.HTTP_METHOD_PATCH;
            case INSERT:
            default:
                return SalesforceConstants.HTTP_METHOD_POST;
        }
    }

    private List<String> extractIds(List<DynaBean> dynabeans) {
        List<String> ids = new ArrayList<>();
        for (DynaBean bean : dynabeans) {
            Object id = bean.get("Id"); // 或者是 "id"
            if (id != null) {
                ids.add(id.toString());
            }
        }
        return ids;
    }

    /**
     * 提取 Header 构建逻辑
     */
    private Map<String, String> createRestHeaders(String sessionId) {
        Map<String, String> headers = new HashMap<>();
        headers.put(SalesforceConstants.CONTENT_TYPE_HEADER, SalesforceConstants.CONTENT_TYPE_JSON);
        headers.put(SalesforceConstants.ACCEPT_HEADER, SalesforceConstants.CONTENT_TYPE_JSON);
        // 确保 sessionId 包含 Bearer 前缀，如果没有则添加
        String authValue = sessionId;
        if (!sessionId.startsWith("Bearer ") && SalesforceConstants.AUTH_HEADER_PREFIX != null) {
            authValue = SalesforceConstants.AUTH_HEADER_PREFIX + sessionId;
        }
        headers.put(SalesforceConstants.AUTH_HEADER_NAME, authValue);
        return headers;
    }

    /**
     * 将 JSON 响应解析为 SaveResult 数组
     */
    private SaveResult[] parseSaveResults(byte[] content) throws IOException {
        if (content == null || content.length == 0) return new SaveResult[0];

        List<Map<String, Object>> rawResults = objectMapper.readValue(content, new TypeReference<List<Map<String, Object>>>() {});
        List<SaveResult> resultList = new ArrayList<>();

        for (Map<String, Object> node : rawResults) {
            SaveResult saveResult = new SaveResult();
            saveResult.setId((String) node.get("id"));
            saveResult.setSuccess(Boolean.TRUE.equals(node.get("success")));

            // 解析错误信息
            List<Map<String, Object>> errors = (List<Map<String, Object>>) node.get("errors");
            if (errors != null && !errors.isEmpty()) {
                saveResult.setErrors(mapToSoapErrors(errors));
            }

            resultList.add(saveResult);
        }
        return resultList.toArray(new SaveResult[0]);
    }

    /**
     * 将 REST 错误格式转换为 SOAP/Partner Error 对象
     */
    private com.sforce.soap.partner.Error[] mapToSoapErrors(List<Map<String, Object>> errorMaps) {
        return errorMaps.stream().map(m -> {
            com.sforce.soap.partner.Error error = new com.sforce.soap.partner.Error();
            error.setMessage((String) m.get("message"));

            // 状态码转换 - 安全转换防止 Enum 不存在报错
            String statusCodeStr = (String) m.get("statusCode");
            if (statusCodeStr != null) {
                try {
                    error.setStatusCode(StatusCode.valueOf(statusCodeStr));
                } catch (IllegalArgumentException e) {
                    // 如果找不到对应的 Enum，记录日志或设为默认值
                    log.warn("无法映射 StatusCode: {}", statusCodeStr);
                }
            }

            // 字段列表转换
            List<String> fields = (List<String>) m.get("fields");
            if (fields != null) {
                error.setFields(fields.toArray(new String[0]));
            }

            return error;
        }).toArray(com.sforce.soap.partner.Error[]::new);
    }


    /**
     * 获取SObject映射用于Composite REST
     * 此方法将 DynaBean 转换为 Map，并处理 attributes 元数据
     *
     * @param dynaBeans DynaBean列表
     * @return Map<String, Object> SObject映射
     */
    private Map<String, Object> getSobjectMapForCompositeREST(List<DynaBean> dynaBeans) {
        List<Map<String, Object>> sobjectList = new ArrayList<>();

        for (DynaBean bean : dynaBeans) {
            Map<String, Object> sobj = new HashMap<>();

            // 获取所有属性
            DynaProperty[] properties = bean.getDynaClass().getDynaProperties();
            for (DynaProperty property : properties) {
                String name = property.getName();
                Object value = bean.get(name);

                // 忽略值为 null 的字段以避免覆盖 SF 中的数据（Patch 语义）
                // 如果需要清空字段，DynaBean 中应该显式传递 fieldsToNull，这里简化处理
                if (value != null) {
                    // Salesforce REST API 需要 "attributes" 对象包含 "type"
                    if ("attributes".equals(name) && value instanceof Map) {
                        sobj.put("attributes", value);
                    } else if ("type".equals(name)) {
                        // 如果 DynaBean 扁平化存储了 type，手动构建 attributes
                        Map<String, String> attributes = new HashMap<>();
                        attributes.put("type", (String) value);
                        sobj.put("attributes", attributes);
                    } else {
                        sobj.put(name, value);
                    }
                }
            }

            // 安全检查：如果没有 attributes.type，API 会报错。
            // 这里假设 DynaBean 来源已经处理好了 attributes。

            sobjectList.add(sobj);
        }

        Map<String, Object> recordsMap = new HashMap<>();
        recordsMap.put("records", sobjectList);
        // allOrNone: true 表示原子性操作，false 表示允许部分成功
        recordsMap.put("allOrNone", false);

        return recordsMap;
    }

    /**
     * 发送HTTP请求
     *
     * @param urlString URL字符串
     * @param headers HTTP头
     * @param method HTTP方法
     * @param requestBody 请求体
     * @return SimpleHttpResponse HTTP响应
     * @throws IOException IO异常
     */
    private SimpleHttpResponse sendHttpRequest(String urlString, Map<String, String> headers,
                                               String method, String requestBody) throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);
        connection.setConnectTimeout(SalesforceConnectionConstants.DEFAULT_CONNECTION_TIMEOUT);
        connection.setReadTimeout(SalesforceConnectionConstants.DEFAULT_READ_TIMEOUT);

        if (headers != null) {
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }

        if (requestBody != null && !requestBody.isEmpty()) {
            connection.setDoOutput(true);
            try (OutputStream out = connection.getOutputStream()) {
                out.write(requestBody.getBytes(StandardCharsets.UTF_8));
            }
        }

        int responseCode = connection.getResponseCode();
        boolean successful = responseCode >= 200 && responseCode < 300;

        String content;
        try (InputStream inputStream = successful ? connection.getInputStream() : connection.getErrorStream()) {
            if (inputStream != null) {
                content = IOUtils.toString(inputStream, StandardCharsets.UTF_8);
            } else {
                content = "";
            }
        }

        return new SimpleHttpResponse(successful, responseCode, content != null ? content.getBytes(StandardCharsets.UTF_8) : new byte[0]);
    }

    /**
     * 内部简单的 Response 包装类，去除对 Apache HttpClient 的依赖
     */
    @Getter
    public static class SimpleHttpResponse {
        private final boolean successful;
        private final int responseCode;
        private final byte[] content;

        public SimpleHttpResponse(boolean successful, int responseCode, byte[] content) {
            this.successful = successful;
            this.responseCode = responseCode;
            this.content = content;
        }

        // 为了兼容原代码的 get() 方法，假设原意是获取状态码
        public int get() {
            return responseCode;
        }

        public String getContentString() {
            return new String(content, StandardCharsets.UTF_8);
        }
    }
}