package org.dromara.salesforce.service.Impl;

import com.sforce.async.*;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import org.dromara.salesforce.factory.SourceSessionFactory;
import org.dromara.salesforce.service.BulkV1ConnectionService;
import org.dromara.salesforce.factory.TargetSessionFactory;
import org.dromara.salesforce.core.SourceSessionClient;
import org.dromara.salesforce.core.TargetSessionClient;
import org.dromara.salesforce.core.BulkV1Connection;
import org.springframework.stereotype.Service;

import java.io.InputStream;

/**
 * Bulk V1连接服务实现类 - 实现Bulk API V1操作的具体逻辑
 *
 * BulkV1ConnectionServiceImpl是BulkV1ConnectionService接口的实现类。
 * 它负责处理Bulk API V1操作的具体逻辑，包括作业状态查询、批处理信息获取等。
 *
 * 主要功能：
 * 1. 实现Bulk API V1的所有操作方法
 * 2. 处理与Salesforce Bulk API V1的通信
 * 3. 管理源ORG和目标ORG的连接
 *
 * 设计特点：
 * - 使用Spring注解声明为服务组件
 * - 实现BulkV1ConnectionService接口定义的所有方法
 * - 提供统一的异常处理机制
 * - 支持源和目标两个ORG的连接（用于数据迁移）
 *
 * 使用场景：
 * - 处理大量数据的批量导入/导出操作
 * - 需要异步处理的长时间运行操作
 * - 数据在两个ORG之间迁移的场景
 *
 * @author Salesforce
 * @since 64.0.0
 */
@Service
public class BulkV1ConnectionServiceImpl implements BulkV1ConnectionService {

    // ==================== 向后兼容方法 - 默认使用源ORG ====================

    @Override
    public JobInfo getJobStatus(String jobId) throws ConnectionException, AsyncApiException {
        return getJobStatusInSource(jobId);
    }

    @Override
    public BatchInfoList getBatchInfoList(String jobId) throws ConnectionException, AsyncApiException {
        return getBatchInfoListInSource(jobId);
    }

    @Override
    public InputStream getBatchResultStream(String jobId, String batchId) throws AsyncApiException {
        return getBatchResultStreamInSource(jobId, batchId);
    }

    @Override
    public String[] getQueryResultIds(String jobId, String batchId) throws AsyncApiException {
        return getQueryResultIdsInSource(jobId, batchId);
    }

    @Override
    public InputStream getQueryResultStream(String jobId, String batchId, String resultId) throws AsyncApiException {
        return getQueryResultStreamInSource(jobId, batchId, resultId);
    }

    // ==================== 源ORG专用方法 ====================

    @Override
    public JobInfo getJobStatusInSource(String jobId) throws ConnectionException, AsyncApiException {
        SourceSessionClient sessionClient = SourceSessionFactory.instance();
        BulkV1Connection bulkConnection = createBulkConnection(sessionClient);
        return bulkConnection.getJobStatus(jobId);
    }

    @Override
    public BatchInfoList getBatchInfoListInSource(String jobId) throws ConnectionException, AsyncApiException {
        SourceSessionClient sessionClient = SourceSessionFactory.instance();
        BulkV1Connection bulkConnection = createBulkConnection(sessionClient);
        return bulkConnection.getBatchInfoList(jobId);
    }

    @Override
    public InputStream getBatchResultStreamInSource(String jobId, String batchId) throws AsyncApiException {
        SourceSessionClient sessionClient = SourceSessionFactory.instance();
        BulkV1Connection bulkConnection = createBulkConnection(sessionClient);
        return bulkConnection.getBatchResultStream(jobId, batchId);
    }

    @Override
    public String[] getQueryResultIdsInSource(String jobId, String batchId) throws AsyncApiException {
        SourceSessionClient sessionClient = SourceSessionFactory.instance();
        BulkV1Connection bulkConnection = createBulkConnection(sessionClient);
        QueryResultList result = bulkConnection.getQueryResultList(jobId, batchId);
        return result.getResult();
    }

    @Override
    public InputStream getQueryResultStreamInSource(String jobId, String batchId, String resultId) throws AsyncApiException {
        SourceSessionClient sessionClient = SourceSessionFactory.instance();
        BulkV1Connection bulkConnection = createBulkConnection(sessionClient);
        return bulkConnection.getQueryResultStream(jobId, batchId, resultId);
    }

    // ==================== 目标ORG专用方法 ====================

    @Override
    public JobInfo getJobStatusInTarget(String jobId) throws ConnectionException, AsyncApiException {
        TargetSessionClient sessionClient = TargetSessionFactory.instance();
        BulkV1Connection bulkConnection = createBulkConnection(sessionClient);
        return bulkConnection.getJobStatus(jobId);
    }

    @Override
    public BatchInfoList getBatchInfoListInTarget(String jobId) throws ConnectionException, AsyncApiException {
        TargetSessionClient sessionClient = TargetSessionFactory.instance();
        BulkV1Connection bulkConnection = createBulkConnection(sessionClient);
        return bulkConnection.getBatchInfoList(jobId);
    }

    @Override
    public InputStream getBatchResultStreamInTarget(String jobId, String batchId) throws AsyncApiException {
        TargetSessionClient sessionClient = TargetSessionFactory.instance();
        BulkV1Connection bulkConnection = createBulkConnection(sessionClient);
        return bulkConnection.getBatchResultStream(jobId, batchId);
    }

    @Override
    public String[] getQueryResultIdsInTarget(String jobId, String batchId) throws AsyncApiException {
        TargetSessionClient sessionClient = TargetSessionFactory.instance();
        BulkV1Connection bulkConnection = createBulkConnection(sessionClient);
        QueryResultList result = bulkConnection.getQueryResultList(jobId, batchId);
        return result.getResult();
    }

    @Override
    public InputStream getQueryResultStreamInTarget(String jobId, String batchId, String resultId) throws AsyncApiException {
        TargetSessionClient sessionClient = TargetSessionFactory.instance();
        BulkV1Connection bulkConnection = createBulkConnection(sessionClient);
        return bulkConnection.getQueryResultStream(jobId, batchId, resultId);
    }

    /**
     * 创建BulkV1Connection实例
     *
     * @param sessionClient 会话客户端
     * @return BulkV1Connection Bulk连接实例
     * @throws AsyncApiException 异步API异常
     */
    private BulkV1Connection createBulkConnection(Object sessionClient) throws AsyncApiException {
        ConnectorConfig config = new ConnectorConfig();

        if (sessionClient instanceof SourceSessionClient) {
            SourceSessionClient sourceSession = (SourceSessionClient) sessionClient;
            config.setSessionId(sourceSession.getSessionId());
            config.setServiceEndpoint(sourceSession.getServer() + "/services/async/47.0");
        } else if (sessionClient instanceof TargetSessionClient) {
            TargetSessionClient targetSession = (TargetSessionClient) sessionClient;
            config.setSessionId(targetSession.getSessionId());
            config.setServiceEndpoint(targetSession.getServer() + "/services/async/47.0");
        }

        return new BulkV1Connection(config);
    }
}
