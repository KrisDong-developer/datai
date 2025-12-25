package com.datai.integration.core;

import com.datai.integration.constant.SalesforceConstants;
import com.sforce.async.*;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.sforce.ws.bind.TypeMapper;
import com.sforce.ws.parser.PullParserException;
import com.sforce.ws.parser.XmlInputStream;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpMethod;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * Bulk API V2连接类 - 提供与Salesforce Bulk API V2交互的功能
 *
 * BulkV2Connection扩展了BulkConnection，提供了与Salesforce Bulk API V2交互的所有功能。
 * 它支持创建、管理和监控Bulk API作业，包括数据导入和查询操作。
 *
 * @author datai
 * @since 1.0.0
 */
@Slf4j
public class BulkV2Connection extends BulkConnection {

    private String queryLocator = "";

    private int numberOfRecordsInQueryResult = 0;

    private final Map<String, String> headers = new HashMap<>();

    public static final TypeMapper typeMapper = new TypeMapper(null, null, false);

    /**
     * 构造函数 - 创建BulkV2Connection实例
     *
     * @param config 连接器配置
     * @throws AsyncApiException 异步API异常
     */
    public BulkV2Connection(ConnectorConfig config) throws AsyncApiException {
        super(config);
    }

    /**
     * 获取作业状态
     *
     * @param jobId 作业ID
     * @return JobInfo 作业信息
     * @throws AsyncApiException 异步API异常
     */
    public JobInfo getJobStatus(String jobId) throws AsyncApiException {
        return getJobStatus(jobId, ContentType.JSON);
    }

    /**
     * 关闭作业
     *
     * @param jobId 作业ID
     * @return JobInfo 作业信息
     * @throws AsyncApiException 异步API异常
     */
    public JobInfo closeJob(String jobId) throws AsyncApiException {
        return getJobStatus(jobId);
    }

    /**
     * 获取作业状态（指定内容类型）
     *
     * @param jobId 作业ID
     * @param contentType 内容类型
     * @return JobInfo 作业信息
     * @throws AsyncApiException 异步API异常
     */
    public JobInfo getJobStatus(String jobId, ContentType contentType) throws AsyncApiException {
        String urlString = constructRequestURL(jobId);
        Map<String, String> headers = getHeaders(JSON_CONTENT_TYPE, JSON_CONTENT_TYPE);
        
        try {
            return doSendJobRequestToServer(urlString, headers, HttpMethod.GET, 
                ContentType.JSON, null, true, "获取作业状态失败: " + jobId);
        } catch (Exception e) {
            log.error("获取作业状态失败，jobId: {}", jobId, e);
            throw new AsyncApiException("获取作业状态失败: " + jobId, AsyncExceptionCode.ClientInputError, e);
        }
    }

    /**
     * 中止作业
     *
     * @param jobId 作业ID
     * @param isQuery 是否为查询作业
     * @return JobInfo 作业信息
     * @throws AsyncApiException 异步API异常
     */
    public JobInfo abortJob(String jobId, boolean isQuery) throws AsyncApiException {
        return setJobState(jobId, isQuery, JobStateEnum.Aborted, "中止作业失败: " + jobId);
    }

    /**
     * 设置作业状态
     *
     * @param jobId 作业ID
     * @param isQuery 是否为查询作业
     * @param state 作业状态
     * @param errorMessage 错误消息
     * @return JobInfo 作业信息
     * @throws AsyncApiException 异步API异常
     */
    public JobInfo setJobState(String jobId, boolean isQuery, JobStateEnum state, String errorMessage) throws AsyncApiException {
        String urlString = constructRequestURL(jobId);
        Map<String, String> headers = getHeaders(JSON_CONTENT_TYPE, JSON_CONTENT_TYPE);
        
        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("state", state.toString());

        try {
            return doSendJobRequestToServer(urlString, headers, HttpMethod.PATCH, 
                ContentType.JSON, requestBodyMap, true, errorMessage);
        } catch (Exception e) {
            log.error("设置作业状态失败，jobId: {}, state: {}", jobId, state, e);
            throw new AsyncApiException(errorMessage, AsyncExceptionCode.ClientInputError, e);
        }
    }

    /**
     * 获取提取作业状态
     *
     * @param jobId 作业ID
     * @return JobInfo 作业信息
     * @throws AsyncApiException 异步API异常
     */
    public JobInfo getExtractJobStatus(String jobId) throws AsyncApiException {
        return getJobStatus(jobId);
    }

    /**
     * 获取查询结果流
     *
     * @param jobId 作业ID
     * @param locator 定位器
     * @return InputStream 查询结果流
     * @throws AsyncApiException 异步API异常
     */
    public InputStream getQueryResultStream(String jobId, String locator) throws AsyncApiException {
        String urlString = constructRequestURL(jobId) + "results/";
        if (locator != null && !locator.isEmpty() && !"null".equalsIgnoreCase(locator)) {
            urlString += "?locator=" + locator;
        }
        
        try {
            return doGetQueryResultStream(new URL(urlString), getHeaders(JSON_CONTENT_TYPE, CSV_CONTENT_TYPE));
        } catch (IOException | ConnectionException e) {
            log.error("获取查询结果流失败，jobId: {}, locator: {}", jobId, locator, e);
            throw new AsyncApiException("获取查询结果失败: " + jobId, AsyncExceptionCode.ClientInputError, e);
        }
    }

    /**
     * 获取查询定位器
     *
     * @return String 查询定位器
     */
    public String getQueryLocator() {
        return this.queryLocator;
    }

    /**
     * 获取查询结果中的记录数
     *
     * @return int 记录数
     */
    public int getNumberOfRecordsInQueryResult() {
        return this.numberOfRecordsInQueryResult;
    }

    /**
     * 开始导入
     * @param jobId 作业ID
     * @param bulkUploadStream 批量上传流
     * @return JobInfo 作业信息
     * @throws AsyncApiException 异步API异常
     */
    public JobInfo startIngest(String jobId, InputStream bulkUploadStream) throws AsyncApiException {
        String urlString = constructRequestURL(jobId) + "batches/";
        Map<String, String> headers = getHeaders(CSV_CONTENT_TYPE, JSON_CONTENT_TYPE);
        
        try {
            HttpURLConnection connection = createHttpConnection(urlString, headers, SalesforceConstants.METHOD_PUT);
            connection.setDoOutput(true);

            try (OutputStream out = connection.getOutputStream()) {
                byte[] buffer = new byte[8192];
                int bytesRead;
                while ((bytesRead = bulkUploadStream.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }

            int responseCode = connection.getResponseCode();
            if (responseCode < 200 || responseCode >= 300) {
                try (InputStream errorStream = connection.getErrorStream()) {
                    parseAndThrowException(errorStream, ContentType.JSON);
                }
            }
        } catch (IOException e) {
            log.error("开始导入失败，jobId: {}", jobId, e);
            throw new AsyncApiException("上传数据失败: " + jobId, AsyncExceptionCode.ClientInputError, e);
        }

        setJobState(jobId, false, JobStateEnum.UploadComplete, "标记上传完成失败: " + jobId);
        return getIngestJobStatus(jobId);
    }

    /**
     * 获取导入作业状态
     *
     * @param jobId 作业ID
     * @return JobInfo 作业信息
     * @throws AsyncApiException 异步API异常
     */
    public JobInfo getIngestJobStatus(String jobId) throws AsyncApiException {
        return getJobStatus(jobId);
    }

    /**
     * 保存导入成功结果
     *
     * @param jobId 作业ID
     * @param filename 文件名
     * @param append 是否追加
     * @throws AsyncApiException 异步API异常
     */
    public void saveIngestSuccessResults(String jobId, String filename, boolean append) throws AsyncApiException {
        doSaveIngestResults(jobId, filename, SalesforceConstants.INGEST_RESULTS_SUCCESSFUL, append);
    }

    /**
     * 保存导入失败结果
     *
     * @param jobId 作业ID
     * @param filename 文件名
     * @throws AsyncApiException 异步API异常
     */
    public void saveIngestFailureResults(String jobId, String filename) throws AsyncApiException {
        doSaveIngestResults(jobId, filename, SalesforceConstants.INGEST_RESULTS_UNSUCCESSFUL, true);
    }

    /**
     * 保存未处理的记录
     *
     * @param jobId 作业ID
     * @param filename 文件名
     * @throws AsyncApiException 异步API异常
     */
    public void saveIngestUnprocessedRecords(String jobId, String filename) throws AsyncApiException {
        doSaveIngestResults(jobId, filename, SalesforceConstants.INGEST_RECORDS_UNPROCESSED, false);
    }

    /**
     * 获取导入成功结果流
     *
     * @param jobId 作业ID
     * @return InputStream 结果流
     * @throws AsyncApiException 异步API异常
     */
    public InputStream getIngestSuccessResultsStream(String jobId) throws AsyncApiException {
        return doGetIngestResultsStream(jobId, SalesforceConstants.INGEST_RESULTS_SUCCESSFUL);
    }

    /**
     * 获取导入失败结果流
     *
     * @param jobId 作业ID
     * @return InputStream 结果流
     * @throws AsyncApiException 异步API异常
     */
    public InputStream getIngestFailedResultsStream(String jobId) throws AsyncApiException {
        return doGetIngestResultsStream(jobId, SalesforceConstants.INGEST_RESULTS_UNSUCCESSFUL);
    }

    /**
     * 获取未处理记录流
     *
     * @param jobId 作业ID
     * @return InputStream 结果流
     * @throws AsyncApiException 异步API异常
     */
    public InputStream getIngestUnprocessedRecordsStream(String jobId) throws AsyncApiException {
        return doGetIngestResultsStream(jobId, SalesforceConstants.INGEST_RECORDS_UNPROCESSED);
    }

    /**
     * 构造请求URL
     *
     * @param jobId 作业ID
     * @return String 请求URL
     */
    private String constructRequestURL(String jobId) {
        String urlString = getConfig().getRestEndpoint();
        if (jobId == null) {
            jobId = "";
        }
        urlString += SalesforceConstants.URI_STEM_INGEST + jobId + "/";
        return urlString;
    }

    /**
     * 创建作业 (重构版)
     *
     * @param job 作业信息
     * @return JobInfo 响应的作业信息
     * @throws AsyncApiException 异步API异常
     */
    public JobInfo createJob(JobInfo job) throws AsyncApiException {
        // 1. 验证内容类型 (提前排除非法状态)
        validateContentType(job.getContentType());

        // 2. 构造请求 URL
        String urlString = constructRequestURL(job.getId());

        // 3. 构建请求头
        Map<String, String> requestHeaders = getHeaders(JSON_CONTENT_TYPE, JSON_CONTENT_TYPE);

        // 4. 构建请求体 (使用 Map 组装，逻辑更清晰)
        Map<String, Object> requestBody = buildCreateJobBody(job);

        try {
            // 5. 发送请求
            return doSendJobRequestToServer(
                    urlString,
                    requestHeaders,
                    HttpMethod.POST,
                    ContentType.JSON,
                    requestBody,
                    true,
                    "创建作业失败"
            );
        } catch (Exception e) {
            log.error("创建作业失败 [Object: {}, Operation: {}]", job.getObject(), job.getOperation(), e);
            // 保持异常链完整
            if (e instanceof AsyncApiException) {
                throw (AsyncApiException) e;
            }
            throw new AsyncApiException("创建作业时发生未知错误", AsyncExceptionCode.ClientInputError, e);
        }
    }

    /**
     * 校验 ContentType
     */
    private void validateContentType(ContentType type) throws AsyncApiException {
        if (type != null && type != ContentType.CSV) {
            throw new AsyncApiException("Bulk API V1 仅支持 CSV 内容类型", AsyncExceptionCode.FeatureNotEnabled);
        }
    }

    /**
     * 封装请求体构建逻辑
     */
    private Map<String, Object> buildCreateJobBody(JobInfo job) {
        Map<String, Object> body = new HashMap<>();
        OperationEnum op = job.getOperation();

        body.put("operation", op.toString());
        body.put("object", job.getObject());
        body.put("contentType", Optional.ofNullable(job.getContentType()).orElse(ContentType.CSV).toString());
        body.put("lineEnding", "LF");

        // Upsert 逻辑
        if (OperationEnum.upsert.equals(op)) {
            body.put("externalIdFieldName", job.getExternalIdFieldName());
        }

        // 分配规则逻辑 (使用判空工具，排除 Blank 字符串)
        boolean supportsAssignmentRule = EnumSet.of(OperationEnum.insert, OperationEnum.update, OperationEnum.upsert).contains(op);
        if (supportsAssignmentRule && job.getAssignmentRuleId() != null && !job.getAssignmentRuleId().trim().isEmpty()) {
            body.put("assignmentRuleId", job.getAssignmentRuleId());
        }

        return body;
    }

    /**
     * 发送作业请求到服务器
     *
     * @param urlString URL字符串
     * @param headers HTTP头
     * @param requestMethod HTTP方法
     * @param responseContentType 响应内容类型
     * @param requestBodyMap 请求体映射
     * @param processServerResponse 是否处理服务器响应
     * @param exceptionMessageString 异常消息字符串
     * @return JobInfo 作业信息
     * @throws AsyncApiException 异步API异常
     */
    private JobInfo doSendJobRequestToServer(String urlString, Map<String, String> headers,
                                             HttpMethod requestMethod, ContentType responseContentType,
                                             Map<String, Object> requestBodyMap, boolean processServerResponse,
                                             String exceptionMessageString) throws AsyncApiException {
        if (headers == null) {
            headers = getHeaders(JSON_CONTENT_TYPE, JSON_CONTENT_TYPE);
        }
        
        try {
            InputStream in = null;
            boolean successfulRequest = true;
            HttpURLConnection connection;
            String method;
            
            if (requestMethod.equals(HttpMethod.GET)) {
                method = SalesforceConstants.METHOD_GET;
            } else if (requestMethod.equals(HttpMethod.POST)) {
                method = SalesforceConstants.METHOD_POST;
            } else if (requestMethod.equals(HttpMethod.PATCH)) {
                method = SalesforceConstants.METHOD_PATCH;
            } else if (requestMethod.equals(HttpMethod.PUT)) {
                method = SalesforceConstants.METHOD_PUT;
            } else {
                method = SalesforceConstants.METHOD_GET;
            }

            if (requestMethod == HttpMethod.GET) {
                if (requestBodyMap != null && !requestBodyMap.isEmpty()) {
                    Set<String> paramNameSet = requestBodyMap.keySet();
                    boolean firstParam = true;
                    for (Object paramName : paramNameSet) {
                        if (firstParam) {
                            urlString += "?" + paramName.toString() + "=" + requestBodyMap.get(paramName);
                            firstParam = false;
                        } else {
                            urlString += "&" + paramName.toString() + "=" + requestBodyMap.get(paramName);
                        }
                    }
                }
                connection = createHttpConnection(urlString, headers, SalesforceConstants.METHOD_GET);
                in = connection.getInputStream();
            } else {
                connection = createHttpConnection(urlString, headers, method);
                connection.setDoOutput(true);

                if (requestBodyMap != null) {
                    String requestContent = serializeToJson(requestBodyMap);
                    try (OutputStream out = connection.getOutputStream()) {
                        out.write(requestContent.getBytes(SalesforceConstants.UTF_8));
                    }
                }

                in = connection.getInputStream();
                successfulRequest = connection.getResponseCode() >= 200 && connection.getResponseCode() < 300;
            }
            
            if (!processServerResponse) {
                return null;
            }
            
            JobInfo result = null;
            if (successfulRequest) {
                if (responseContentType == ContentType.ZIP_XML || responseContentType == ContentType.XML) {
                    XmlInputStream xin = new XmlInputStream();
                    xin.setInput(in, SalesforceConstants.UTF_8);
                    result = new JobInfo();
                    result.load(xin, typeMapper);
                } else {
                    result = deserializeJobInfoFromJson(in);
                }
            } else {
                parseAndThrowException(in, responseContentType);
            }
            
            return result;
        } catch (IOException e) {
            log.error("发送作业请求失败，url: {}", urlString, e);
            throw new AsyncApiException(exceptionMessageString, AsyncExceptionCode.ClientInputError, e);
        } catch (ConnectionException e) {
            log.error("连接异常，url: {}", urlString, e);
            throw new AsyncApiException(exceptionMessageString, AsyncExceptionCode.ClientInputError, e);
        } catch (PullParserException e) {
            log.error("解析异常，url: {}", urlString, e);
            throw new AsyncApiException(exceptionMessageString, AsyncExceptionCode.ClientInputError, e);
        }
    }

    /**
     * 解析并抛出异常
     *
     * @param is 输入流
     * @param type 内容类型
     * @throws AsyncApiException 异步API异常
     */
    private void parseAndThrowException(InputStream is, ContentType type) throws AsyncApiException {
        try {
            AsyncApiException exception;

            BulkV2Error[] errorList = deserializeErrorsFromJson(is);
            if (errorList.length > 0 && errorList[0].getMessage().contains("Aggregate Relationships not supported in Bulk Query")) {
                exception = new AsyncApiException(errorList[0].getMessage(), AsyncExceptionCode.FeatureNotEnabled);
            } else if (errorList.length > 0) {
                exception = new AsyncApiException(errorList[0].getErrorCode() + " : " + errorList[0].getMessage(), 
                                               AsyncExceptionCode.Unknown);
            } else {
                exception = new AsyncApiException("发生未知错误", AsyncExceptionCode.Unknown);
            }
            throw exception;
        } catch (IOException | NullPointerException e) {
            log.error("解析异常失败", e);
            throw new AsyncApiException("解析异常失败", AsyncExceptionCode.ClientInputError, e);
        }
    }

    /**
     * 获取HTTP头
     *
     * @param requestContentType 请求内容类型
     * @param acceptContentType 接受内容类型
     * @return Map<String, String> HTTP头映射
     */
    private Map<String, String> getHeaders(String requestContentType, String acceptContentType) {
        Map<String, String> newMap = new HashMap<>();
        String authHeaderValue = SalesforceConstants.AUTH_HEADER_VALUE_PREFIX + getConfig().getSessionId();
        
        newMap.put(SalesforceConstants.REQUEST_CONTENT_TYPE_HEADER, requestContentType);
        newMap.put(SalesforceConstants.ACCEPT_CONTENT_TYPES_HEADER, acceptContentType);
        newMap.put(SalesforceConstants.AUTH_HEADER, authHeaderValue);
        newMap.put(SalesforceConstants.SFORCE_CALL_OPTIONS_HEADER, getConfig().getRequestHeader(SalesforceConstants.SFORCE_CALL_OPTIONS_HEADER));
        
        log.debug("{} : {}", SalesforceConstants.SFORCE_CALL_OPTIONS_HEADER, 
                 getConfig().getRequestHeader(SalesforceConstants.SFORCE_CALL_OPTIONS_HEADER));
        
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            newMap.put(entry.getKey(), entry.getValue());
        }
        
        return newMap;
    }

    /**
     * 获取查询结果流
     *
     * @param resultsURL 结果URL
     * @param headers HTTP头
     * @return InputStream 结果流
     * @throws IOException IO异常
     * @throws AsyncApiException 异步API异常
     * @throws ConnectionException 连接异常
     */
    private InputStream doGetQueryResultStream(URL resultsURL, Map<String, String> headers) 
            throws IOException, AsyncApiException, ConnectionException {
        HttpURLConnection connection = createHttpConnection(resultsURL.toString(), headers, SalesforceConstants.METHOD_GET);
        InputStream is = connection.getInputStream();

        String locator = connection.getHeaderField("Sforce-Locator");
        if (locator != null) {
            this.queryLocator = locator;
        }

        String numberOfRecords = connection.getHeaderField("Sforce-NumberOfRecords");
        if (numberOfRecords != null) {
            this.numberOfRecordsInQueryResult = Integer.parseInt(numberOfRecords);
        }

        return is;
    }

    /**
     * 获取导入结果流
     *
     * @param jobId 作业ID
     * @param resultsType 结果类型
     * @return InputStream 结果流
     * @throws AsyncApiException 异步API异常
     */
    private InputStream doGetIngestResultsStream(String jobId, String resultsType) throws AsyncApiException {
        String resultsURLString = constructRequestURL(jobId) + resultsType;
        try {
            HttpURLConnection connection = createHttpConnection(resultsURLString, 
                getHeaders(JSON_CONTENT_TYPE, JSON_CONTENT_TYPE), SalesforceConstants.METHOD_GET);
            return connection.getInputStream();
        } catch (IOException e) {
            log.error("获取导入结果流失败，jobId: {}, resultsType: {}", jobId, resultsType, e);
            throw new AsyncApiException("获取" + resultsType + "失败: " + jobId, AsyncExceptionCode.ClientInputError, e);
        }
    }

    /**
     * 保存导入结果
     *
     * @param jobId 作业ID
     * @param filename 文件名
     * @param resultsType 结果类型
     * @param append 是否追加
     * @throws AsyncApiException 异步API异常
     */
    private void doSaveIngestResults(String jobId, String filename, String resultsType, boolean append) 
            throws AsyncApiException {
        try (BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(filename, append));
             BufferedInputStream bis = new BufferedInputStream(doGetIngestResultsStream(jobId, resultsType))) {

            byte[] buffer = new byte[2048];
            boolean firstLineSkipped = !append;
            for (int len; (len = bis.read(buffer)) > 0; ) {
                if (!firstLineSkipped) {
                    String str = new String(buffer, StandardCharsets.UTF_8);
                    if (str.contains("\n")) {
                        String[] parts = str.split("\n");
                        if (parts.length > 1) {
                            StringBuilder sb = new StringBuilder();
                            for (int i = 1; i < parts.length; i++) {
                                if (i > 1) {
                                    sb.append(System.lineSeparator());
                                }
                                sb.append(parts[i]);
                            }
                            buffer = sb.toString().getBytes(StandardCharsets.UTF_8);
                            int counter = 0;
                            while (counter < buffer.length && buffer[counter] != 0) {
                                counter++;
                            }
                            len = counter;
                            firstLineSkipped = true;
                        }
                    }
                }
                if (firstLineSkipped) {
                    bos.write(buffer, 0, len);
                }
            }
            bos.flush();
        } catch (FileNotFoundException e) {
            log.error("文件未找到，filename: {}", filename, e);
            throw new AsyncApiException("文件未找到: " + filename, AsyncExceptionCode.ClientInputError, e);
        } catch (IOException e) {
            log.error("保存导入结果失败，jobId: {}, filename: {}", jobId, filename, e);
            throw new AsyncApiException("获取" + resultsType + "失败: " + jobId, AsyncExceptionCode.ClientInputError, e);
        }
    }

    /**
     * 创建HTTP连接
     *
     * @param urlString URL字符串
     * @param headers HTTP头
     * @param method HTTP方法
     * @return HttpURLConnection HTTP连接
     * @throws IOException IO异常
     */
    private HttpURLConnection createHttpConnection(String urlString, Map<String, String> headers, String method) 
            throws IOException {
        URL url = new URL(urlString);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod(method);

        for (Map.Entry<String, String> entry : headers.entrySet()) {
            connection.setRequestProperty(entry.getKey(), entry.getValue());
        }

        return connection;
    }

    /**
     * 序列化对象为JSON字符串
     *
     * @param object 对象
     * @return String JSON字符串
     */
    private String serializeToJson(Object object) {
        StringBuilder json = new StringBuilder("{");
        if (object instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) object;
            boolean first = true;
            for (Map.Entry<?, ?> entry : map.entrySet()) {
                if (!first) {
                    json.append(",");
                }
                json.append("\"").append(entry.getKey()).append("\":");
                if (entry.getValue() instanceof String) {
                    json.append("\"").append(entry.getValue()).append("\"");
                } else {
                    json.append(entry.getValue());
                }
                first = false;
            }
        }
        json.append("}");
        return json.toString();
    }

    /**
     * 反序列化JSON字符串为JobInfo对象
     *
     * @param is 输入流
     * @return JobInfo 对象实例
     * @throws IOException IO异常
     */
    private JobInfo deserializeJobInfoFromJson(InputStream is) throws IOException {
        // 在实际项目中，这里应该使用Jackson或其他JSON库进行反序列化
        throw new UnsupportedOperationException("JSON反序列化未实现");
    }

    /**
     * 反序列化JSON字符串为BulkV2Error数组
     *
     * @param is 输入流
     * @return BulkV2Error[] 错误数组
     * @throws IOException IO异常
     */
    private BulkV2Error[] deserializeErrorsFromJson(InputStream is) throws IOException {
        // 在实际项目中，这里应该使用Jackson或其他JSON库进行反序列化
        throw new UnsupportedOperationException("JSON反序列化未实现");
    }

    /**
     * BulkV2Error 类 - 表示Bulk API V2错误信息
     */
    private static class BulkV2Error {
        private String errorCode;
        private String message;
        private String[] fields;

        public String getErrorCode() {
            return errorCode;
        }

        public void setErrorCode(String errorCode) {
            this.errorCode = errorCode;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public String[] getFields() {
            return fields;
        }

        public void setFields(String[] fields) {
            this.fields = fields;
        }
    }

    /**
     * 创建 Bulk Query 作业
     * * @param soql SOQL 查询语句
     * @return JobInfo 作业信息
     */
    public JobInfo createQueryJob(String soql) throws AsyncApiException {
        // V2 Query 的 Endpoint 是 /jobs/query
        String urlString = getConfig().getRestEndpoint() + SalesforceConstants.URI_STEM_QUERY;
        Map<String, String> requestHeaders = getHeaders(JSON_CONTENT_TYPE, JSON_CONTENT_TYPE);

        Map<String, Object> body = new HashMap<>();
        body.put("query", soql);
        body.put("operation", "query");
        // 可以指定字段定界符等，这里默认 CSV
        body.put("contentType", ContentType.CSV.toString());
        body.put("columnDelimiter", "COMMA");
        body.put("lineEnding", "LF");

        try {
            return doSendJobRequestToServer(urlString, requestHeaders, HttpMethod.POST,
                    ContentType.JSON, body, true, "创建查询作业失败");
        } catch (Exception e) {
            log.error("创建 Bulk Query 失败: {}", soql, e);
            throw new AsyncApiException("创建查询作业失败", AsyncExceptionCode.ClientInputError, e);
        }
    }

    /**
     * 轮询 Job 状态直到完成或失败
     * * @param jobId 作业ID
     * @param intervalMillis 轮询间隔（毫秒）
     * @param timeoutMillis 超时时间（毫秒）
     * @return 最终的 JobInfo
     */
    public JobInfo awaitJobCompletion(String jobId, long intervalMillis, long timeoutMillis) throws AsyncApiException {
        long start = System.currentTimeMillis();
        while (true) {
            JobInfo status = getJobStatus(jobId);
            JobStateEnum state = status.getState();

            if (state == JobStateEnum.JobComplete || state == JobStateEnum.Failed || state == JobStateEnum.Aborted) {
                return status;
            }

            if (System.currentTimeMillis() - start > timeoutMillis) {
                throw new AsyncApiException("等待 Job 超时: " + jobId, AsyncExceptionCode.ClientInputError);
            }

            try {
                Thread.sleep(intervalMillis);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new AsyncApiException("轮询被中断", AsyncExceptionCode.ClientInputError, e);
            }
        }
    }


}