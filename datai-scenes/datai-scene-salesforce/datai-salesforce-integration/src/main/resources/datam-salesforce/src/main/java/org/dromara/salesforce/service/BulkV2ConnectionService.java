package org.dromara.salesforce.service;

import com.sforce.async.AsyncApiException;
import com.sforce.async.ContentType;
import com.sforce.async.JobInfo;
import com.sforce.async.JobStateEnum;
import com.sforce.ws.ConnectionException;

import java.io.InputStream;

/**
 * Bulk V2连接服务接口 - 提供Bulk API V2操作的相关方法
 * 
 * BulkV2ConnectionService是Data Loader中负责处理Bulk API V2操作的服务接口。
 * 它定义了与Bulk V2 API交互所需的所有方法，包括作业创建、状态查询、结果获取等功能。
 * 
 * 主要功能：
 * 1. 创建和管理Bulk V2作业
 * 2. 获取作业状态和结果
 * 3. 提供源ORG和目标ORG的独立操作方法
 * 
 * 设计特点：
 * - 遵循Spring Boot服务接口设计规范
 * - 提供源ORG和目标ORG的独立操作方法
 * - 统一异常处理，抛出ConnectionException和AsyncApiException
 * - 向后兼容，提供默认操作方法
 * 
 * 使用场景：
 * - 处理大量数据的批量导入/导出操作
 * - 需要更高性能和更简单接口的批量操作
 * - 数据在两个ORG之间迁移的场景
 * 
 * @author Salesforce
 * @since 64.0.0
 */
public interface BulkV2ConnectionService {

    // ==================== 向后兼容方法 - 默认使用源ORG ====================
    
    /**
     * 创建作业
     * 
     * @param job 作业信息
     * @return JobInfo 作业信息
     * @throws ConnectionException 连接异常
     * @throws AsyncApiException 异步API异常
     */
    JobInfo createJob(JobInfo job) throws ConnectionException, AsyncApiException;
    
    /**
     * 获取作业状态
     * 
     * @param jobId 作业ID
     * @return JobInfo 作业信息
     * @throws ConnectionException 连接异常
     * @throws AsyncApiException 异步API异常
     */
    JobInfo getJobStatus(String jobId) throws ConnectionException, AsyncApiException;
    
    /**
     * 更新作业状态
     * 
     * @param jobId 作业ID
     * @param state 作业状态
     * @return JobInfo 作业信息
     * @throws ConnectionException 连接异常
     * @throws AsyncApiException 异步API异常
     */
    JobInfo updateJobState(String jobId, JobStateEnum state) throws ConnectionException, AsyncApiException;
    
    /**
     * 上传作业数据
     * 
     * @param jobId 作业ID
     * @param data 数据输入流
     * @throws AsyncApiException 异步API异常
     */
    void uploadJobData(String jobId, InputStream data) throws AsyncApiException;
    
    /**
     * 获取成功结果
     * 
     * @param jobId 作业ID
     * @param contentType 内容类型
     * @return InputStream 结果流
     * @throws AsyncApiException 异步API异常
     */
    InputStream getSuccessResults(String jobId, ContentType contentType) throws AsyncApiException;
    
    /**
     * 获取未处理记录
     * 
     * @param jobId 作业ID
     * @param contentType 内容类型
     * @return InputStream 未处理记录流
     * @throws AsyncApiException 异步API异常
     */
    InputStream getUnprocessedRecords(String jobId, ContentType contentType) throws AsyncApiException;
    
    /**
     * 获取失败结果
     * 
     * @param jobId 作业ID
     * @param contentType 内容类型
     * @return InputStream 失败结果流
     * @throws AsyncApiException 异步API异常
     */
    InputStream getFailedResults(String jobId, ContentType contentType) throws AsyncApiException;

    // ==================== 源ORG专用方法 ====================
    
    /**
     * 在源ORG中创建作业
     * 
     * @param job 作业信息
     * @return JobInfo 作业信息
     * @throws ConnectionException 连接异常
     * @throws AsyncApiException 异步API异常
     */
    JobInfo createJobInSource(JobInfo job) throws ConnectionException, AsyncApiException;
    
    /**
     * 获取源ORG作业状态
     * 
     * @param jobId 作业ID
     * @return JobInfo 作业信息
     * @throws ConnectionException 连接异常
     * @throws AsyncApiException 异步API异常
     */
    JobInfo getJobStatusInSource(String jobId) throws ConnectionException, AsyncApiException;
    
    /**
     * 更新源ORG作业状态
     * 
     * @param jobId 作业ID
     * @param state 作业状态
     * @return JobInfo 作业信息
     * @throws ConnectionException 连接异常
     * @throws AsyncApiException 异步API异常
     */
    JobInfo updateJobStateInSource(String jobId, JobStateEnum state) throws ConnectionException, AsyncApiException;
    
    /**
     * 上传源ORG作业数据
     * 
     * @param jobId 作业ID
     * @param data 数据输入流
     * @throws AsyncApiException 异步API异常
     */
    void uploadJobDataInSource(String jobId, InputStream data) throws AsyncApiException;
    
    /**
     * 获取源ORG成功结果
     * 
     * @param jobId 作业ID
     * @param contentType 内容类型
     * @return InputStream 结果流
     * @throws AsyncApiException 异步API异常
     */
    InputStream getSuccessResultsInSource(String jobId, ContentType contentType) throws AsyncApiException;
    
    /**
     * 获取源ORG未处理记录
     * 
     * @param jobId 作业ID
     * @param contentType 内容类型
     * @return InputStream 未处理记录流
     * @throws AsyncApiException 异步API异常
     */
    InputStream getUnprocessedRecordsInSource(String jobId, ContentType contentType) throws AsyncApiException;
    
    /**
     * 获取源ORG失败结果
     * 
     * @param jobId 作业ID
     * @param contentType 内容类型
     * @return InputStream 失败结果流
     * @throws AsyncApiException 异步API异常
     */
    InputStream getFailedResultsInSource(String jobId, ContentType contentType) throws AsyncApiException;

    // ==================== 目标ORG专用方法 ====================
    
    /**
     * 在目标ORG中创建作业
     * 
     * @param job 作业信息
     * @return JobInfo 作业信息
     * @throws ConnectionException 连接异常
     * @throws AsyncApiException 异步API异常
     */
    JobInfo createJobInTarget(JobInfo job) throws ConnectionException, AsyncApiException;
    
    /**
     * 获取目标ORG作业状态
     * 
     * @param jobId 作业ID
     * @return JobInfo 作业信息
     * @throws ConnectionException 连接异常
     * @throws AsyncApiException 异步API异常
     */
    JobInfo getJobStatusInTarget(String jobId) throws ConnectionException, AsyncApiException;
    
    /**
     * 更新目标ORG作业状态
     * 
     * @param jobId 作业ID
     * @param state 作业状态
     * @return JobInfo 作业信息
     * @throws ConnectionException 连接异常
     * @throws AsyncApiException 异步API异常
     */
    JobInfo updateJobStateInTarget(String jobId, JobStateEnum state) throws ConnectionException, AsyncApiException;
    
    /**
     * 上传目标ORG作业数据
     * 
     * @param jobId 作业ID
     * @param data 数据输入流
     * @throws AsyncApiException 异步API异常
     */
    void uploadJobDataInTarget(String jobId, InputStream data) throws AsyncApiException;
    
    /**
     * 获取目标ORG成功结果
     * 
     * @param jobId 作业ID
     * @param contentType 内容类型
     * @return InputStream 结果流
     * @throws AsyncApiException 异步API异常
     */
    InputStream getSuccessResultsInTarget(String jobId, ContentType contentType) throws AsyncApiException;
    
    /**
     * 获取目标ORG未处理记录
     * 
     * @param jobId 作业ID
     * @param contentType 内容类型
     * @return InputStream 未处理记录流
     * @throws AsyncApiException 异步API异常
     */
    InputStream getUnprocessedRecordsInTarget(String jobId, ContentType contentType) throws AsyncApiException;
    
    /**
     * 获取目标ORG失败结果
     * 
     * @param jobId 作业ID
     * @param contentType 内容类型
     * @return InputStream 失败结果流
     * @throws AsyncApiException 异步API异常
     */
    InputStream getFailedResultsInTarget(String jobId, ContentType contentType) throws AsyncApiException;
}