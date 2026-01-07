package com.datai.integration.core;

import com.sforce.async.AsyncApiException;
import com.sforce.async.ContentType;
import com.sforce.async.JobInfo;
import com.sforce.async.JobStateEnum;

import java.io.InputStream;

/**
 * Bulk API V2 连接接口
 * 提供 Salesforce Bulk API V2 的核心功能，包括作业管理、数据导入和查询操作
 */
public interface IBulkV2Connection {

    JobInfo createJob(JobInfo job) throws AsyncApiException;

    JobInfo createQueryJob(String soql) throws AsyncApiException;

    JobInfo getJobStatus(String jobId) throws AsyncApiException;

    JobInfo getJobStatus(String jobId, ContentType contentType) throws AsyncApiException;

    JobInfo closeJob(String jobId) throws AsyncApiException;

    JobInfo abortJob(String jobId) throws AsyncApiException;

    JobInfo abortJob(String jobId, boolean isQuery) throws AsyncApiException;

    JobInfo setJobState(String jobId, boolean isQuery, JobStateEnum state, String errorMessage) throws AsyncApiException;

    JobInfo getExtractJobStatus(String jobId) throws AsyncApiException;

    JobInfo getIngestJobStatus(String jobId) throws AsyncApiException;

    JobInfo awaitJobCompletion(String jobId, long intervalMillis, long timeoutMillis) throws AsyncApiException;

    InputStream getQueryResultStream(String jobId, String locator) throws AsyncApiException;

    JobInfo startIngest(String jobId, InputStream bulkUploadStream) throws AsyncApiException;

    void saveIngestSuccessResults(String jobId, String filename, boolean append) throws AsyncApiException;

    void saveIngestFailureResults(String jobId, String filename) throws AsyncApiException;

    void saveIngestUnprocessedRecords(String jobId, String filename) throws AsyncApiException;

    InputStream getIngestSuccessResultsStream(String jobId) throws AsyncApiException;

    InputStream getIngestFailedResultsStream(String jobId) throws AsyncApiException;

    InputStream getIngestUnprocessedRecordsStream(String jobId) throws AsyncApiException;

    String getQueryLocator();

    int getNumberOfRecordsInQueryResult();
}
