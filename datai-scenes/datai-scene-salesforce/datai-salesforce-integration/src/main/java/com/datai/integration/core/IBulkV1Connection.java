package com.datai.integration.core;

import com.sforce.async.*;

import java.io.InputStream;

/**
 * Bulk API V1 连接接口
 * 提供 Salesforce Bulk API V1 的核心功能，包括作业管理、批次操作和查询结果处理
 */
public interface IBulkV1Connection {

    JobInfo createJob(String object, String operation) throws AsyncApiException;

    JobInfo createJob(JobInfo job) throws AsyncApiException;

    JobInfo createJob(JobInfo job, com.sforce.async.ContentType contentType) throws AsyncApiException;

    JobInfo createJob(String objectType, OperationEnum operation) throws AsyncApiException;

    BatchInfo createBatchFromStream(String jobId, InputStream csvStream) throws AsyncApiException;

    BatchInfo createBatchFromStream(JobInfo jobInfo, InputStream input) throws AsyncApiException;

    BatchInfo createBatchFromZipStream(JobInfo jobInfo, InputStream zipInput) throws AsyncApiException;

    BatchInfo createBatchFromDir(JobInfo job, InputStream batchContent, java.io.File attachmentDir) throws AsyncApiException;

    BatchInfo createBatchWithFileAttachments(JobInfo jobInfo, InputStream batchContent, java.io.File rootDirectory, String... files) throws AsyncApiException;

    BatchInfo createBatchWithFileAttachments(JobInfo jobInfo, InputStream batchContent, java.util.Map<String, java.io.File> attachedFiles) throws AsyncApiException;

    BatchInfo createBatchWithInputStreamAttachments(JobInfo jobInfo, InputStream batchContent, java.util.Map<String, InputStream> attachments) throws AsyncApiException;

    BatchInfo createBatchFromForeignCsvStream(JobInfo jobInfo, InputStream input, String charSet) throws AsyncApiException;

    void createTransformationSpecFromStream(JobInfo jobInfo, InputStream input) throws AsyncApiException;

    BatchRequest createBatch(JobInfo job) throws AsyncApiException;

    CsvBatchRequest createCsvBatch(JobInfo job) throws AsyncApiException;

    JobInfo getJobStatus(String jobId) throws AsyncApiException;

    JobInfo closeJob(String jobId) throws AsyncApiException;

    JobInfo abortJob(String jobId) throws AsyncApiException;

    JobInfo awaitJobCompletion(String jobId, long intervalMillis, long timeoutMillis) throws AsyncApiException;

    BatchInfoList getBatchInfoList(String jobId) throws AsyncApiException;

    BatchInfoList getBatchInfoList(String jobId, com.sforce.async.ContentType contentType) throws AsyncApiException;

    InputStream getBatchResultStream(String jobId, String batchId) throws AsyncApiException;

    String[] getQueryResultIds(String jobId, String batchId) throws AsyncApiException;

    InputStream getQueryResultStream(String jobId, String batchId, String resultId) throws AsyncApiException;
}
