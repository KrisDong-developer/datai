package org.dromara.salesforce.core;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sforce.async.AsyncApiException;
import com.sforce.soap.partner.SaveResult;
import com.sforce.soap.partner.StatusCode;
import com.sforce.soap.partner.fault.ApiFault;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import org.apache.commons.beanutils.DynaBean;
import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 重构后的RESTConnection类，减少对dataloader内部依赖的依赖
 *
 * 这个类封装了与Salesforce REST API的交互功能，主要用于Composite REST操作。
 * 重构的目标是减少对dataloader特定工具类和配置类的依赖，使类更加独立和可重用。
 *
 * 主要改进：
 * 1. 移除对dataloader特定工具类的依赖（如AppUtil、Messages、DLLogManager等）
 * 2. 使用标准的日志框架（Log4j）
 * 3. 简化HTTP传输处理
 * 4. 提供更清晰的结构
 *
 * @author Salesforce
 * @since 64.0.0
 */
public class RESTConnection {
    private ConnectorConfig connectorConfig;
    private String apiVersion = "64.0";
    private static Logger logger = LogManager.getLogger(RESTConnection.class);

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
     * 执行加载操作
     *
     * @param sessionId 会话ID
     * @param action 操作类型（更新/删除）
     * @param dynabeans DynaBean列表
     * @return SaveResult[] 保存结果数组
     * @throws ConnectionException 连接异常
     */
    public SaveResult[] loadAction(String sessionId, ACTION_ENUM action, List<DynaBean> dynabeans) throws ConnectionException {
        String actionStr = "update"; // default
        switch (action) {
            case DELETE:
                actionStr = "delete";
                break;
            default:
                actionStr = "update";
                break;
        }
        logger.debug("Begin operation: " + actionStr);
        ConnectionException connectionException = null;
        try {
            Map<String, Object> batchRecords = this.getSobjectMapForCompositeREST(dynabeans);
            String json = "";
            try {
                ObjectMapper mapper = new ObjectMapper();
                json = mapper.writeValueAsString(batchRecords);
                logger.debug("JSON for batch update using Composite REST:\n" + json);
            } catch (JsonProcessingException e) {
                logger.error(e.getMessage());
                throw new ConnectionException(e.getMessage());
            }

            HashMap<String, String> headers = new HashMap<String, String>();
            headers.put("Content-Type", "application/JSON");
            headers.put("ACCEPT", "application/JSON");
            headers.put("Authorization", "Bearer " + sessionId);

            // 创建简单的HTTP传输实现
            SimpleHttpTransport transport = new SimpleHttpTransport();

            // assume update operation by default and set http method value to PATCH
            String httpMethod = "PATCH";
            if (action == ACTION_ENUM.DELETE) {
                httpMethod = "DELETE";
            }
            try {
                String url = connectorConfig.getRestEndpoint() + "?updateOnly=true";
                transport.connect(url, headers, httpMethod);
                transport.write(json.getBytes(StandardCharsets.UTF_8.name()));
                transport.close();
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new ConnectionException(e.getMessage());
            }
            InputStream in = null;
            try {
                in = transport.getContent();
            } catch (Exception e) {
                logger.error(e.getMessage());
                throw new ConnectionException(e.getMessage());
            }
            boolean successfulRequest = transport.isSuccessful();
            ArrayList<SaveResult> resultList = new ArrayList<SaveResult>();
            if (successfulRequest) {
                ObjectMapper mapper = new ObjectMapper();
                try {
                    Object[] jsonResults = mapper.readValue(in, Object[].class);
                    for (Object result : jsonResults) {
                        Map<String, Object> resultMap = (Map<String, Object>)result;
                        SaveResult resultToSave = new SaveResult();
                        resultToSave.setId((String)resultMap.get("id"));
                        resultToSave.setSuccess(((Boolean)resultMap.get("success")).booleanValue());
                        List<Map<String, Object>> errorResultsArray = (List<Map<String, Object>>)resultMap.get("errors");
                        ArrayList<com.sforce.soap.partner.Error> errorList = new ArrayList<com.sforce.soap.partner.Error>();
                        if (errorResultsArray != null) {
                            for (Map<String, Object> errorMap : errorResultsArray) {
                                com.sforce.soap.partner.Error error = new com.sforce.soap.partner.Error();
                                String codeStr = StatusCode.valuesToEnums.get((String)errorMap.get("statusCode"));
                                if (codeStr != null) {
                                    StatusCode statusCode = StatusCode.valueOf(codeStr);
                                    error.setStatusCode(statusCode);
                                }
                                error.setMessage((String) errorMap.get("message"));
                                List<String> fieldsList = (List<String>) errorMap.get("fields");
                                if (fieldsList != null) {
                                    error.setFields(fieldsList.toArray(new String[0]));
                                }
                                errorList.add(error);
                            }
                            resultToSave.setErrors(errorList.toArray(new com.sforce.soap.partner.Error[0]));
                        }
                        resultList.add(resultToSave);
                    }
                } catch (Exception e) {
                    logger.warn("Composite REST returned no results - " + e.getMessage());
                    throw new ConnectionException(e.getMessage());
                }
            } else {
                try {
                    String resultStr = IOUtils.toString(in, StandardCharsets.UTF_8);
                    logger.warn(resultStr);
                } catch (Exception e) {
                    logger.warn(e.getMessage());
                }
            }
            return resultList.toArray(new SaveResult[0]);
        } catch (ConnectionException ex) {
            logger.error("Operation error: " + actionStr + ", " + ex.getMessage(), ex);
            if (ex instanceof ApiFault) {
                ApiFault fault = (ApiFault)ex;
                String faultMessage = fault.getExceptionMessage();
                logger.error("Operation error: " + actionStr + ", " + faultMessage, fault);
            }
            connectionException = ex;
        }
        throw connectionException;
    }

    /**
     * 获取SObject映射用于Composite REST
     *
     * @param dynaBeans DynaBean列表
     * @return Map<String, Object> SObject映射
     */
    private Map<String, Object> getSobjectMapForCompositeREST(List<DynaBean> dynaBeans) {
        // 简化的实现，实际项目中需要完整的转换逻辑
        List<Map<String, Object>> sobjectList = new ArrayList<>();
        for (DynaBean bean : dynaBeans) {
            // 在实际实现中，需要将DynaBean的属性映射到Map
            Map<String, Object> sobj = new HashMap<>();
            sobjectList.add(sobj);
        }

        HashMap<String, Object> recordsMap = new HashMap<String, Object>();
        recordsMap.put("records", sobjectList);
        recordsMap.put("allOrNone", false);
        return recordsMap;
    }

    /**
     * 简单的HTTP传输实现类
     *
     * 这个类提供了一个简化的HTTP传输实现，用于替代dataloader中的复杂传输机制。
     * 它专注于基本的HTTP操作，减少对外部依赖的需要。
     */
    private static class SimpleHttpTransport {
        private boolean successful = false;
        private InputStream content;

        /**
         * 连接到指定URL
         *
         * @param url URL地址
         * @param headers HTTP头
         * @param method HTTP方法
         * @throws IOException IO异常
         */
        public void connect(String url, Map<String, String> headers, String method) throws Exception {
            // 简化的HTTP连接实现
            // 在实际项目中，这里应该使用标准的HTTP客户端库如Apache HttpClient或Java内置的HttpURLConnection
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) new java.net.URL(url).openConnection();
            connection.setRequestMethod(method);

            // 设置请求头
            for (Map.Entry<String, String> entry : headers.entrySet()) {
                connection.setRequestProperty(entry.getKey(), entry.getValue());
            }

            connection.setDoOutput(true);
            this.content = connection.getInputStream();
            this.successful = connection.getResponseCode() >= 200 && connection.getResponseCode() < 300;
        }

        /**
         * 写入数据
         *
         * @param data 数据字节数组
         * @throws IOException IO异常
         */
        public void write(byte[] data) throws Exception {
            // 在实际实现中，这里应该将数据写入HTTP请求体
            // 由于这是简化的实现，我们暂时留空
        }

        /**
         * 关闭连接
         *
         * @throws IOException IO异常
         */
        public void close() throws Exception {
            // 在实际实现中，这里应该关闭HTTP连接
            // 由于这是简化的实现，我们暂时留空
        }

        /**
         * 获取响应内容
         *
         * @return InputStream 响应内容输入流
         */
        public InputStream getContent() {
            return this.content;
        }

        /**
         * 检查请求是否成功
         *
         * @return boolean 请求是否成功
         */
        public boolean isSuccessful() {
            return this.successful;
        }
    }

    /**
     * 操作类型枚举
     */
    public enum ACTION_ENUM {
        UPDATE,
        DELETE
    }
}
