package org.dromara.salesforce.service;

import com.sforce.async.*;
import com.sforce.ws.ConnectionException;
import java.io.InputStream;

/**
 * Bulk V1连接服务接口 - 提供Bulk API V1操作的相关方法
 * 
 * BulkV1ConnectionService是Data Loader中负责处理Bulk API V1操作的服务接口。
 * 它定义了与Bulk V1 API交互所需的所有方法，包括作业状态查询、批处理信息获取等功能。
 * 
 * 主要功能：
 * 1. 获取作业状态
 * 2. 获取批处理信息列表
 * 3. 获取批处理结果流
 * 4. 获取查询结果列表和流
 * 5. 提供源ORG和目标ORG的独立操作方法
 * 
 * 设计特点：
 * - 遵循Spring Boot服务接口设计规范
 * - 提供源ORG和目标ORG的独立操作方法
 * - 统一异常处理
 * - 向后兼容
 * 
 * 使用场景：
 * - 处理大量数据的批量导入/导出操作
 * - 需要异步处理的长时间运行操作
 * - 数据在两个ORG之间迁移的场景
 * 
 * @author Salesforce
 * @since 64.0.0
 */
public interface BulkV1ConnectionService {
    
    // 通用异常接口
    @FunctionalInterface
    interface ThrowingOperation<T, E extends Exception> {
        T execute() throws ConnectionException, AsyncApiException, Exception;
    }

    // ==================== 向后兼容方法 - 默认使用源ORG ====================
    
    JobInfo getJobStatus(String jobId) throws Exception;
    BatchInfoList getBatchInfoList(String jobId) throws Exception;
    InputStream getBatchResultStream(String jobId, String batchId) throws Exception;
    String[] getQueryResultIds(String jobId, String batchId) throws Exception;
    InputStream getQueryResultStream(String jobId, String batchId, String resultId) throws Exception;

    // ==================== 源ORG专用方法 ====================
    
    JobInfo getJobStatusInSource(String jobId) throws Exception;
    BatchInfoList getBatchInfoListInSource(String jobId) throws Exception;
    InputStream getBatchResultStreamInSource(String jobId, String batchId) throws Exception;
    String[] getQueryResultIdsInSource(String jobId, String batchId) throws Exception;
    InputStream getQueryResultStreamInSource(String jobId, String batchId, String resultId) throws Exception;

    // ==================== 目标ORG专用方法 ====================
    
    JobInfo getJobStatusInTarget(String jobId) throws Exception;
    BatchInfoList getBatchInfoListInTarget(String jobId) throws Exception;
    InputStream getBatchResultStreamInTarget(String jobId, String batchId) throws Exception;
    String[] getQueryResultIdsInTarget(String jobId, String batchId) throws Exception;
    InputStream getQueryResultStreamInTarget(String jobId, String batchId, String resultId) throws Exception;
}