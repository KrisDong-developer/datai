package org.dromara.salesforce.service.Impl;

import com.sforce.async.AsyncApiException;
import com.sforce.async.ContentType;
import com.sforce.async.JobInfo;
import com.sforce.async.JobStateEnum;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import org.dromara.salesforce.factory.SourceSessionFactory;
import org.dromara.salesforce.service.BulkV2ConnectionService;
import org.dromara.salesforce.factory.TargetSessionFactory;
import org.dromara.salesforce.core.SourceSessionClient;
import org.dromara.salesforce.core.TargetSessionClient;
import org.dromara.salesforce.core.BulkV2Connection;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * Bulk V2连接服务实现类 - 实现Bulk API V2操作的具体逻辑
 *
 * BulkV2ConnectionServiceImpl是BulkV2ConnectionService接口的实现类。
 * 它负责处理Bulk API V2操作的具体逻辑，包括作业创建、状态查询、结果获取等。
 *
 * 主要功能：
 * 1. 实现Bulk API V2的所有操作方法
 * 2. 处理与Salesforce Bulk API V2的通信
 * 3. 管理源ORG和目标ORG的连接
 *
 * 设计特点：
 * - 使用Spring注解声明为服务组件
 * - 实现BulkV2ConnectionService接口定义的所有方法
 * - 提供统一的异常处理机制
 * - 支持源和目标两个ORG的连接（用于数据迁移）
 *
 * 使用场景：
 * - 处理大量数据的批量导入/导出操作
 * - 需要更高性能和更简单接口的批量操作
 * - 数据在两个ORG之间迁移的场景
 *
 * @author Salesforce
 * @since 64.0.0
 */
@Service
public class BulkV2ConnectionServiceImpl implements BulkV2ConnectionService {

    // ==================== 向后兼容方法 - 默认使用源ORG ====================

    @Override
    public JobInfo createJob(JobInfo job) throws ConnectionException, AsyncApiException {
        return createJobInSource(job);
    }

    @Override
    public JobInfo getJobStatus(String jobId) throws ConnectionException, AsyncApiException {
        return getJobStatusInSource(jobId);
    }

    @Override
    public JobInfo updateJobState(String jobId, JobStateEnum state) throws ConnectionException, AsyncApiException {
        return updateJobStateInSource(jobId, state);
    }

    @Override
    public void uploadJobData(String jobId, InputStream data) throws AsyncApiException {
        uploadJobDataInSource(jobId, data);
    }

    @Override
    public InputStream getSuccessResults(String jobId, ContentType contentType) throws AsyncApiException {
        return getSuccessResultsInSource(jobId, contentType);
    }

    @Override
    public InputStream getUnprocessedRecords(String jobId, ContentType contentType) throws AsyncApiException {
        return getUnprocessedRecordsInSource(jobId, contentType);
    }

    @Override
    public InputStream getFailedResults(String jobId, ContentType contentType) throws AsyncApiException {
        return getFailedResultsInSource(jobId, contentType);
    }

    // ==================== 源ORG专用方法 ====================

    @Override
    public JobInfo createJobInSource(JobInfo job) throws ConnectionException, AsyncApiException {
        SourceSessionClient sessionClient = SourceSessionFactory.instance();
        BulkV2Connection bulkConnection = createBulkConnection(sessionClient);
        return bulkConnection.createJob(job);
    }

    @Override
    public JobInfo getJobStatusInSource(String jobId) throws ConnectionException, AsyncApiException {
        SourceSessionClient sessionClient = SourceSessionFactory.instance();
        BulkV2Connection bulkConnection = createBulkConnection(sessionClient);
        return bulkConnection.getJobStatus(jobId);
    }

    @Override
    public JobInfo updateJobStateInSource(String jobId, JobStateEnum state) throws ConnectionException, AsyncApiException {
        SourceSessionClient sessionClient = SourceSessionFactory.instance();
        BulkV2Connection bulkConnection = createBulkConnection(sessionClient);
        // 根据BulkV2Connection类的实现，使用setJobState方法
        return bulkConnection.setJobState(jobId, false, state, "Failed to update job state for job " + jobId);
    }

    @Override
    public void uploadJobDataInSource(String jobId, InputStream data) throws AsyncApiException {
        SourceSessionClient sessionClient = SourceSessionFactory.instance();
        BulkV2Connection bulkConnection = createBulkConnection(sessionClient);
        // 根据BulkV2Connection类的实现，使用startIngest方法
        bulkConnection.startIngest(jobId, data);
    }

    @Override
    public InputStream getSuccessResultsInSource(String jobId, ContentType contentType) throws AsyncApiException {
        SourceSessionClient sessionClient = SourceSessionFactory.instance();
        BulkV2Connection bulkConnection = createBulkConnection(sessionClient);
        // 根据BulkV2Connection类的实现，使用getIngestSuccessResultsStream方法
        return bulkConnection.getIngestSuccessResultsStream(jobId);
    }

    @Override
    public InputStream getUnprocessedRecordsInSource(String jobId, ContentType contentType) throws AsyncApiException {
        SourceSessionClient sessionClient = SourceSessionFactory.instance();
        BulkV2Connection bulkConnection = createBulkConnection(sessionClient);
        // 根据BulkV2Connection类的实现，使用getIngestUnprocessedRecordsStream方法
        return bulkConnection.getIngestUnprocessedRecordsStream(jobId);
    }

    @Override
    public InputStream getFailedResultsInSource(String jobId, ContentType contentType) throws AsyncApiException {
        SourceSessionClient sessionClient = SourceSessionFactory.instance();
        BulkV2Connection bulkConnection = createBulkConnection(sessionClient);
        // 根据BulkV2Connection类的实现，使用getIngestFailedResultsStream方法
        return bulkConnection.getIngestFailedResultsStream(jobId);
    }

    // ==================== 目标ORG专用方法 ====================

    @Override
    public JobInfo createJobInTarget(JobInfo job) throws ConnectionException, AsyncApiException {
        TargetSessionClient sessionClient = TargetSessionFactory.instance();
        BulkV2Connection bulkConnection = createBulkConnection(sessionClient);
        return bulkConnection.createJob(job);
    }

    @Override
    public JobInfo getJobStatusInTarget(String jobId) throws ConnectionException, AsyncApiException {
        TargetSessionClient sessionClient = TargetSessionFactory.instance();
        BulkV2Connection bulkConnection = createBulkConnection(sessionClient);
        return bulkConnection.getJobStatus(jobId);
    }

    @Override
    public JobInfo updateJobStateInTarget(String jobId, JobStateEnum state) throws ConnectionException, AsyncApiException {
        TargetSessionClient sessionClient = TargetSessionFactory.instance();
        BulkV2Connection bulkConnection = createBulkConnection(sessionClient);
        // 根据BulkV2Connection类的实现，使用setJobState方法
        return bulkConnection.setJobState(jobId, false, state, "Failed to update job state for job " + jobId);
    }

    @Override
    public void uploadJobDataInTarget(String jobId, InputStream data) throws AsyncApiException {
        TargetSessionClient sessionClient = TargetSessionFactory.instance();
        BulkV2Connection bulkConnection = createBulkConnection(sessionClient);
        // 根据BulkV2Connection类的实现，使用startIngest方法
        bulkConnection.startIngest(jobId, data);
    }

    @Override
    public InputStream getSuccessResultsInTarget(String jobId, ContentType contentType) throws AsyncApiException {
        TargetSessionClient sessionClient = TargetSessionFactory.instance();
        BulkV2Connection bulkConnection = createBulkConnection(sessionClient);
        // 根据BulkV2Connection类的实现，使用getIngestSuccessResultsStream方法
        return bulkConnection.getIngestSuccessResultsStream(jobId);
    }

    @Override
    public InputStream getUnprocessedRecordsInTarget(String jobId, ContentType contentType) throws AsyncApiException {
        TargetSessionClient sessionClient = TargetSessionFactory.instance();
        BulkV2Connection bulkConnection = createBulkConnection(sessionClient);
        // 根据BulkV2Connection类的实现，使用getIngestUnprocessedRecordsStream方法
        return bulkConnection.getIngestUnprocessedRecordsStream(jobId);
    }

    @Override
    public InputStream getFailedResultsInTarget(String jobId, ContentType contentType) throws AsyncApiException {
        TargetSessionClient sessionClient = TargetSessionFactory.instance();
        BulkV2Connection bulkConnection = createBulkConnection(sessionClient);
        // 根据BulkV2Connection类的实现，使用getIngestFailedResultsStream方法
        return bulkConnection.getIngestFailedResultsStream(jobId);
    }

    /**
     * 创建BulkV2Connection实例
     *
     * @param sessionClient 会话客户端
     * @return BulkV2Connection Bulk连接实例
     * @throws AsyncApiException 异步API异常
     */
    private BulkV2Connection createBulkConnection(Object sessionClient) throws AsyncApiException {
        ConnectorConfig config = new ConnectorConfig();

        if (sessionClient instanceof SourceSessionClient) {
            SourceSessionClient sourceSession = (SourceSessionClient) sessionClient;
            config.setSessionId(sourceSession.getSessionId());
            config.setServiceEndpoint(sourceSession.getServer() + "/services/data/v59.0/jobs/");
        } else if (sessionClient instanceof TargetSessionClient) {
            TargetSessionClient targetSession = (TargetSessionClient) sessionClient;
            config.setSessionId(targetSession.getSessionId());
            config.setServiceEndpoint(targetSession.getServer() + "/services/data/v59.0/jobs/");
        }

        return new BulkV2Connection(config);
    }
}
