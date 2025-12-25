package com.datai.integration.core;

import com.datai.integration.constant.SalesforceConstants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sforce.async.*;
import com.sforce.ws.ConnectorConfig;
import com.sforce.ws.parser.XmlInputStream;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.client5.http.classic.methods.HttpGet;
import org.apache.hc.client5.http.classic.methods.HttpPost;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.impl.classic.CloseableHttpResponse;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.core5.http.ContentType;
import org.apache.hc.core5.http.io.entity.EntityUtils;
import org.apache.hc.core5.http.io.entity.InputStreamEntity;
import org.apache.hc.core5.http.io.entity.StringEntity;
import org.apache.hc.core5.net.URIBuilder;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Bulk V1 连接类
 * 最终支持增删改查操作
 */
@Slf4j
public class BulkV1Connection extends BulkConnection {

    public BulkV1Connection(ConnectorConfig config) throws AsyncApiException {
        super(config);
        addHeader(SalesforceConstants.SFORCE_CALL_OPTIONS_HEADER, config.getRequestHeader(SalesforceConstants.SFORCE_CALL_OPTIONS_HEADER));
    }

    // ==========================================
    // 1. 新增：CSV 流式上传方法 (Create Batch)
    // ==========================================

    /**
     * 流式上传 CSV 数据到一个已存在的 Job
     *
     * @param jobId      Salesforce Job ID
     * @param csvStream  CSV 数据流（例如来自文件、S3 或其他网络位置）
     * @return BatchInfo 上传成功后的批次信息
     */
    public BatchInfo createBatchFromStream(String jobId, InputStream csvStream) throws AsyncApiException {
        try {
            URI uri = new URIBuilder(getConfig().getRestEndpoint())
                    .appendPath("job")
                    .appendPath(jobId)
                    .appendPath("batch")
                    .build();

            // 每次请求创建独立的 HttpClient (按要求不使用连接池)
            try (CloseableHttpClient httpClient = HttpClients.createDefault()) {
                HttpPost httpPost = new HttpPost(uri);

                // 必须的鉴权和内容类型
                httpPost.setHeader("X-SFDC-Session", getConfig().getSessionId());
                httpPost.setHeader("Content-Type", "text/csv; charset=UTF-8");

                // 使用 InputStreamEntity 实现真正的流式传输
                // -1 表示长度未知，或者你可以传入具体的长度以优化性能
                InputStreamEntity entity = new InputStreamEntity(csvStream, -1, ContentType.create("text/csv", StandardCharsets.UTF_8));
                httpPost.setEntity(entity);

                try (CloseableHttpResponse response = httpClient.execute(httpPost)) {
                    int status = response.getCode();
                    if (status >= 200 && status < 300) {
                        // 解析返回的 BatchInfo (XML 格式)
                        return processBulkV1Get(response.getEntity().getContent(), com.sforce.async.ContentType.XML, BatchInfo.class);
                    } else {
                        String errorMsg = EntityUtils.toString(response.getEntity());
                        throw new IOException("创建批次失败 (Status " + status + "): " + errorMsg);
                    }
                }
            }
        } catch (Exception e) {
            log.error("流式上传 CSV 失败, jobId: {}", jobId, e);
            // 这里可以根据需要回退到 super.createBatchFromStream(...)，但标准 SDK 通常不支持直接传流
            throw new AsyncApiException("CSV上传异常: " + e.getMessage(), AsyncExceptionCode.ClientInputError, e);
        }
    }

    // ==========================================
    // 2. 作业状态控制方法
    // ==========================================

    public JobInfo closeJob(String jobId) throws AsyncApiException {
        return updateJobState(jobId, JobStateEnum.Closed);
    }

    public JobInfo abortJob(String jobId) throws AsyncApiException {
        return updateJobState(jobId, JobStateEnum.Aborted);
    }

    private JobInfo updateJobState(String jobId, JobStateEnum newState) throws AsyncApiException {
        try {
            URI uri = new URIBuilder(getConfig().getRestEndpoint()).appendPath("job").appendPath(jobId).build();
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost post = new HttpPost(uri);
                post.setHeader("X-SFDC-Session", getConfig().getSessionId());
                post.setHeader("Content-Type", "application/xml");

                String xmlBody = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" +
                        "<jobInfo xmlns=\"http://www.force.com/2009/06/asyncapi/dataload\">" +
                        "<state>" + newState.name() + "</state>" +
                        "</jobInfo>";
                post.setEntity(new StringEntity(xmlBody, StandardCharsets.UTF_8));

                try (CloseableHttpResponse response = client.execute(post)) {
                    return processBulkV1Get(response.getEntity().getContent(), com.sforce.async.ContentType.XML, JobInfo.class);
                }
            }
        } catch (Exception e) {
            log.error("变更 Job 状态失败: {}", jobId, e);
            throw new AsyncApiException("状态更新异常", AsyncExceptionCode.ClientInputError, e);
        }
    }

    /**
     * 同步轮询直到 Job 完成
     */
    public JobInfo awaitJobCompletion(String jobId, long intervalMillis, long timeoutMillis) throws AsyncApiException {
        long startTime = System.currentTimeMillis();
        while (true) {
            JobInfo job = getJobStatus(jobId);
            if (job.getState() == JobStateEnum.JobComplete || job.getState() == JobStateEnum.Failed || job.getState() == JobStateEnum.Aborted) {
                return job;
            }
            if (System.currentTimeMillis() - startTime > timeoutMillis) {
                throw new AsyncApiException("等待 Job 超时: " + jobId, AsyncExceptionCode.ClientInputError);
            }
            try { Thread.sleep(intervalMillis); } catch (InterruptedException e) { Thread.currentThread().interrupt(); break; }
        }
        return null;
    }

    // ==========================================
    // 3. 获取信息方法 (GET 逻辑)
    // ==========================================

    public JobInfo getJobStatus(String jobId) throws AsyncApiException {
        try {
            InputStream in = invokeBulkV1GET(new String[]{"job", jobId});
            return processBulkV1Get(in, com.sforce.async.ContentType.XML, JobInfo.class);
        } catch (Exception e) {
            return super.getJobStatus(jobId, com.sforce.async.ContentType.XML);
        }
    }

    public InputStream getBatchResultStream(String jobId, String batchId) throws AsyncApiException {
        try {
            return invokeBulkV1GET(new String[]{"job", jobId, "batch", batchId, "result"});
        } catch (Exception e) {
            return super.getBatchResultStream(jobId, batchId);
        }
    }

    private InputStream invokeBulkV1GET(String[] urlParts) throws AsyncApiException {
        try {
            URIBuilder builder = new URIBuilder(getConfig().getRestEndpoint());
            for (String part : urlParts) builder.appendPath(part);
            URI uri = builder.build();

            CloseableHttpClient httpClient = HttpClients.createDefault();
            HttpGet httpGet = new HttpGet(uri);
            httpGet.setHeader("X-SFDC-Session", getConfig().getSessionId());

            CloseableHttpResponse response = httpClient.execute(httpGet);
            if (response.getCode() >= 200 && response.getCode() < 300) {
                // 返回自定义包装流，确保读取完毕后关闭 httpClient 和 response
                return new FilterInputStream(response.getEntity().getContent()) {
                    @Override
                    public void close() throws IOException {
                        try { super.close(); } finally { response.close(); httpClient.close(); }
                    }
                };
            } else {
                String err = EntityUtils.toString(response.getEntity());
                response.close();
                httpClient.close();
                throw new IOException("API Error " + response.getCode() + ": " + err);
            }
        } catch (Exception e) {
            throw new AsyncApiException("GET 请求失败: " + e.getMessage(), AsyncExceptionCode.ClientInputError, e);
        }
    }

    /**
     * 通用解析器 - 重构版
     */
    private <T> T processBulkV1Get(InputStream is, com.sforce.async.ContentType contentType, Class<T> returnClass) throws AsyncApiException {
        // 自动管理资源关闭
        try (InputStream input = is) {
            if (input == null) {
                return null;
            }

            // 1. 处理 JSON 格式
            if (contentType == com.sforce.async.ContentType.JSON) {
                ObjectMapper mapper = new ObjectMapper();
                return mapper.readValue(input, returnClass);
            }

            // 2. 处理 XML 格式 (Salesforce 默认 WSC 逻辑)
            XmlInputStream xin = new XmlInputStream();
            xin.setInput(input, "UTF-8");

            // 实例化返回对象
            T result = returnClass.getDeclaredConstructor().newInstance();

            // 调用 Salesforce 自动生成的 load 方法进行 XML 映射
            // 注意：typeMapper 假设是类中的成员变量
            Method loadMethod = returnClass.getMethod("load", XmlInputStream.class, typeMapper.getClass());
            loadMethod.invoke(result, xin, typeMapper);

            return result;

        } catch (Exception e) {
            // 统一异常包装
            throw new AsyncApiException("解析响应失败: " + e.getMessage(), AsyncExceptionCode.ClientInputError, e);
        }
    }
    // ==========================================
    // 4. 新增：创建 Job (Create Job)
    // ==========================================

    /**
     * 创建一个新的 Bulk Job
     * @param objectType Salesforce 对象名 (如 Account)
     * @param operation 操作类型 (insert, update, upsert, delete, query)
     * @return JobInfo
     */
    public JobInfo createJob(String objectType, OperationEnum operation) throws AsyncApiException {
        JobInfo job = new JobInfo();
        job.setObject(objectType);
        job.setOperation(operation);
        job.setContentType(com.sforce.async.ContentType.CSV); // 默认 CSV
        return createJob(job);
    }

    // ==========================================
    // 5. 新增：获取批次状态 (Get Batch Status)
    // ==========================================

    /**
     * 获取特定 Batch 的详细状态
     */
    public BatchInfo getBatchInfo(String jobId, String batchId) throws AsyncApiException {
        try {
            InputStream in = invokeBulkV1GET(new String[]{"job", jobId, "batch", batchId});
            return processBulkV1Get(in, com.sforce.async.ContentType.XML, BatchInfo.class);
        } catch (Exception e) {
            return super.getBatchInfo(jobId, batchId);
        }
    }

    /**
     * 获取 Job 下所有批次的状态列表
     */
    public BatchInfoList getBatchInfoList(String jobId) throws AsyncApiException {
        try {
            InputStream in = invokeBulkV1GET(new String[]{"job", jobId, "batch"});
            return processBulkV1Get(in, com.sforce.async.ContentType.XML, BatchInfoList.class);
        } catch (Exception e) {
            return super.getBatchInfoList(jobId);
        }
    }

    // ==========================================
    // 6. 新增：查询结果处理 (Bulk Query Support)
    // ==========================================

    /**
     * 自定义方法：获取 Bulk Query 的结果 ID 数组
     * 避开父类 getQueryResultList 的返回类型限制，直接返回 String[]
     */
    public String[] getQueryResultIds(String jobId, String batchId) throws AsyncApiException {
        // 逻辑：如果是我们自己通过 invokeBulkV1GET 获取的流，手动解析以提高效率
        try (InputStream in = invokeBulkV1GET(new String[]{"job", jobId, "batch", batchId, "result"})) {
            XmlInputStream xin = new XmlInputStream();
            xin.setInput(in, "UTF-8");

            java.util.List<String> results = new java.util.ArrayList<>();

            while (xin.next() != XmlInputStream.END_DOCUMENT) {
                if (xin.getEventType() == XmlInputStream.START_TAG) {
                    // Bulk V1 Query 返回的 XML 节点名为 <result>ID</result>
                    if ("result".equals(xin.getName())) {
                        results.add(xin.nextText());
                    }
                }
            }
            log.info("Job {} Batch {} 共有 {} 个结果文件", jobId, batchId, results.size());
            return results.toArray(new String[0]);
        } catch (Exception e) {
            log.warn("手动解析 QueryResultIds 失败，尝试调用父类并转换类型: {}", e.getMessage());
            // Fallback: 调用父类方法并手动提取 String[]
            QueryResultList qrl = super.getQueryResultList(jobId, batchId);
            return (qrl != null) ? qrl.getResult() : new String[0];
        }
    }

    /**
     * 获取具体的查询结果流 (针对具体的 Result ID)
     */
    public InputStream getQueryResultStream(String jobId, String batchId, String resultId) throws AsyncApiException {
        return invokeBulkV1GET(new String[]{"job", jobId, "batch", batchId, "result", resultId});
    }
}