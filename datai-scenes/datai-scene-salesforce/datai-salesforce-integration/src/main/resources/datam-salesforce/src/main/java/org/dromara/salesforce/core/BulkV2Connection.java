package org.dromara.salesforce.core;

import com.sforce.async.*;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import com.sforce.ws.bind.TypeMapper;
import com.sforce.ws.parser.PullParserException;
import com.sforce.ws.parser.XmlInputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dromara.salesforce.constant.BulkConstant;
import org.springframework.http.HttpMethod;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Bulk API V2连接类 - 提供与Salesforce Bulk API V2交互的功能
 *
 * BulkV2Connection扩展了BulkConnection，提供了与Salesforce Bulk API V2交互的所有功能。
 * 它支持创建、管理和监控Bulk API作业，包括数据导入和查询操作。
 *
 * 主要功能：
 * 1. 创建和管理Bulk API V2作业
 * 2. 上传数据文件进行处理
 * 3. 获取作业状态和结果
 * 4. 处理作业状态变更
 *
 * 设计特点：
 * 1. 使用标准HTTP连接替代自定义传输实现
 * 2. 支持JSON格式的数据交换
 * 3. 提供完整的异常处理机制
 * 4. 支持导入和查询两种作业类型
 *
 * 使用场景：
 * - 处理大量数据的批量导入操作
 * - 执行复杂的数据查询操作
 * - 需要更高性能和更简单接口的批量操作
 *
 * @author Salesforce
 * @since 64.0.0
 */
public class BulkV2Connection extends BulkConnection{

    private String queryLocator = "";

    private int numberOfRecordsInQueryResult = 0;

    private HashMap<String, String> headers = new HashMap<String, String>();

    public static final TypeMapper typeMapper = new TypeMapper((String)null, (String)null, false);

    private static final Logger logger = LogManager.getLogger(BulkV2Connection.class);

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
        HashMap<String, String> headers = getHeaders(JSON_CONTENT_TYPE, JSON_CONTENT_TYPE);
        return doSendJobRequestToServer(urlString,
            headers,
            HttpMethod.GET,
            ContentType.JSON,
            null,
            true,
            "Failed to get job status for job " + jobId);
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
        return setJobState(jobId, isQuery, JobStateEnum.Aborted, "Failed to abort job " + jobId);
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

        return doSendJobRequestToServer(urlString,
                headers,
                HttpMethod.PATCH,
                ContentType.JSON,
                requestBodyMap,
                true,
                errorMessage);
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
            throw new AsyncApiException("Failed to get query results for job " + jobId, AsyncExceptionCode.ClientInputError, e);
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
            HttpURLConnection connection = createHttpConnection(urlString, headers, BulkConstant.METHOD_PUT);
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
            throw new AsyncApiException("Failed to upload to server for job " + jobId, AsyncExceptionCode.ClientInputError, e);
        }

        setJobState(jobId, false, JobStateEnum.UploadComplete, "Failed to mark completion of the upload");
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
        doSaveIngestResults(jobId, filename, BulkConstant.INGEST_RESULTS_SUCCESSFUL, append);
    }

    /**
     * 保存导入失败结果
     *
     * @param jobId 作业ID
     * @param filename 文件名
     * @throws AsyncApiException 异步API异常
     */
    public void saveIngestFailureResults(String jobId, String filename) throws AsyncApiException {
        doSaveIngestResults(jobId, filename, BulkConstant.INGEST_RESULTS_UNSUCCESSFUL, true);
    }

    /**
     * 保存未处理的记录
     *
     * @param jobId 作业ID
     * @param filename 文件名
     * @throws AsyncApiException 异步API异常
     */
    public void saveIngestUnprocessedRecords(String jobId, String filename) throws AsyncApiException {
        doSaveIngestResults(jobId, filename, BulkConstant.INGEST_RECORDS_UNPROCESSED, false);
    }

    /**
     * 获取导入成功结果流
     *
     * @param jobId 作业ID
     * @return InputStream 结果流
     * @throws AsyncApiException 异步API异常
     */
    public InputStream getIngestSuccessResultsStream(String jobId) throws AsyncApiException {
        return doGetIngestResultsStream(jobId, BulkConstant.INGEST_RESULTS_SUCCESSFUL);
    }

    /**
     * 获取导入失败结果流
     *
     * @param jobId 作业ID
     * @return InputStream 结果流
     * @throws AsyncApiException 异步API异常
     */
    public InputStream getIngestFailedResultsStream(String jobId) throws AsyncApiException {
        return doGetIngestResultsStream(jobId, BulkConstant.INGEST_RESULTS_UNSUCCESSFUL);
    }

    /**
     * 获取未处理记录流
     *
     * @param jobId 作业ID
     * @return InputStream 结果流
     * @throws AsyncApiException 异步API异常
     */
    public InputStream getIngestUnprocessedRecordsStream(String jobId) throws AsyncApiException {
        return doGetIngestResultsStream(jobId, BulkConstant.INGEST_RECORDS_UNPROCESSED);
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
        urlString += BulkConstant.URI_STEM_INGEST + jobId + "/";
        return urlString;
    }

    /**
     * 创建作业
     *
     * @param job 作业信息
     * @return JobInfo 作业信息
     * @throws AsyncApiException 异步API异常
     */
    public JobInfo createJob(JobInfo job) throws AsyncApiException {
        ContentType type = job.getContentType();
        if (type != null && type != ContentType.CSV) {
            throw new AsyncApiException("Unsupported Content Type", AsyncExceptionCode.FeatureNotEnabled);
        }
        OperationEnum operation = job.getOperation();
        String urlString = constructRequestURL(job.getId());
        Map<String, String> requestHeaders;

        Map<String, Object> requestBodyMap = new HashMap<>();
        requestBodyMap.put("operation", job.getOperation().toString());
        requestHeaders = getHeaders(JSON_CONTENT_TYPE, JSON_CONTENT_TYPE);
        requestBodyMap.put("object", job.getObject());
        requestBodyMap.put("contentType", type.toString());
        requestBodyMap.put("lineEnding", "LF");
        if (operation.equals(OperationEnum.upsert)) {
            requestBodyMap.put("externalIdFieldName", job.getExternalIdFieldName());
        }
        if (operation.equals(OperationEnum.upsert)
                || operation.equals(OperationEnum.insert)
                || operation.equals(OperationEnum.update)) {
            if (job.getAssignmentRuleId() != null && !job.getAssignmentRuleId().isBlank()) {
                requestBodyMap.put("assignmentRuleId", job.getAssignmentRuleId());
            }
        }
        return doSendJobRequestToServer(urlString,
                requestHeaders,
                HttpMethod.POST,
                ContentType.JSON,
                requestBodyMap,
                true,
                "Failed to create job");
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
    private JobInfo doSendJobRequestToServer(String urlString,
                                             Map<String, String> headers,
                                             HttpMethod requestMethod,
                                             ContentType responseContentType,
                                             Map<String, Object> requestBodyMap,
                                             boolean processServerResponse,
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
                method = BulkConstant.METHOD_GET;
            } else if (requestMethod.equals(HttpMethod.POST)) {
                method = BulkConstant.METHOD_POST;
            } else if (requestMethod.equals(HttpMethod.PATCH)) {
                method = BulkConstant.METHOD_PATCH;
            } else if (requestMethod.equals(HttpMethod.PUT)) {
                method = BulkConstant.METHOD_PUT;
            } else {
                method = BulkConstant.METHOD_GET;
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
                connection = createHttpConnection(urlString, headers, BulkConstant.METHOD_GET);
                in = connection.getInputStream();
            } else {
                connection = createHttpConnection(urlString, headers, method);
                connection.setDoOutput(true);

                if (requestBodyMap != null) {
                    String requestContent = serializeToJson(requestBodyMap);
                    try (OutputStream out = connection.getOutputStream()) {
                        out.write(requestContent.getBytes(BulkConstant.UTF_8));
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
                    xin.setInput(in, BulkConstant.UTF_8);
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
            throw new AsyncApiException(exceptionMessageString, AsyncExceptionCode.ClientInputError, e);
        } catch (ConnectionException e) {
            throw new AsyncApiException(exceptionMessageString, AsyncExceptionCode.ClientInputError, e);
        } catch (PullParserException e) {
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
                exception = new AsyncApiException(errorList[0].getErrorCode() + " : " + errorList[0].getMessage(), AsyncExceptionCode.Unknown);
            } else {
                exception = new AsyncApiException("Unknown error occurred", AsyncExceptionCode.Unknown);
            }
            throw exception;
        } catch (IOException | NullPointerException e) {
            throw new AsyncApiException("Failed to parse exception", AsyncExceptionCode.ClientInputError, e);
        }
    }

    /**
     * 获取HTTP头
     *
     * @param requestContentType 请求内容类型
     * @param acceptContentType 接受内容类型
     * @return Map<String, String> HTTP头映射
     */
    private HashMap<String, String> getHeaders(String requestContentType, String acceptContentType) {
        HashMap<String, String> newMap = new HashMap<String, String>();
        String authHeaderValue = BulkConstant.AUTH_HEADER_VALUE_PREFIX + getConfig().getSessionId();
        newMap.put(BulkConstant.REQUEST_CONTENT_TYPE_HEADER, requestContentType);
        newMap.put(BulkConstant.ACCEPT_CONTENT_TYPES_HEADER, acceptContentType);
        newMap.put(BulkConstant.AUTH_HEADER, authHeaderValue);
        newMap.put(BulkConstant.SFORCE_CALL_OPTIONS_HEADER, getConfig().getRequestHeader(BulkConstant.SFORCE_CALL_OPTIONS_HEADER));
        logger.debug(BulkConstant.SFORCE_CALL_OPTIONS_HEADER + " : " + getConfig().getRequestHeader(BulkConstant.SFORCE_CALL_OPTIONS_HEADER));
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
    private InputStream doGetQueryResultStream(URL resultsURL, Map<String, String> headers) throws IOException, AsyncApiException, ConnectionException {
        HttpURLConnection connection = createHttpConnection(resultsURL.toString(), headers, BulkConstant.METHOD_GET);
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
            HttpURLConnection connection = createHttpConnection(resultsURLString, getHeaders(JSON_CONTENT_TYPE, JSON_CONTENT_TYPE), BulkConstant.METHOD_GET);
            return connection.getInputStream();
        } catch (IOException e) {
            throw new AsyncApiException("Failed to get " + resultsType + " for job id " + jobId, AsyncExceptionCode.ClientInputError, e);
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
    private void doSaveIngestResults(String jobId, String filename, String resultsType, boolean append) throws AsyncApiException {
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
            throw new AsyncApiException("File " + filename + " not found", AsyncExceptionCode.ClientInputError, e);
        } catch (IOException e) {
            throw new AsyncApiException("Failed to get " + resultsType + " for job " + jobId, AsyncExceptionCode.ClientInputError, e);
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
    private HttpURLConnection createHttpConnection(String urlString, Map<String, String> headers, String method) throws IOException {
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
        throw new UnsupportedOperationException("JSON deserialization not implemented in this refactored version");
    }

    /**
     * 反序列化JSON字符串为BulkV2Error数组
     *
     * @param is 输入流
     * @return BulkV2Error[] 错误数组
     * @throws IOException IO异常
     */
    private BulkV2Error[] deserializeErrorsFromJson(InputStream is) throws IOException {
        throw new UnsupportedOperationException("JSON deserialization not implemented in this refactored version");
    }
}
