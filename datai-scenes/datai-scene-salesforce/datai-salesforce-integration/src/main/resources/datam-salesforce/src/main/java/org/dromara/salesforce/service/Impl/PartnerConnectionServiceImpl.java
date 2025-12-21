package org.dromara.salesforce.service.Impl;

import com.sforce.soap.partner.*;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import org.dromara.salesforce.factory.SourceSessionFactory;
import org.dromara.salesforce.service.PartnerConnectionService;
import org.dromara.salesforce.factory.TargetSessionFactory;
import org.dromara.salesforce.core.SourceSessionClient;
import org.dromara.salesforce.core.TargetSessionClient;
import org.springframework.stereotype.Service;

/**
 * Partner连接服务实现类 - 实现Partner API操作的具体逻辑
 *
 * PartnerConnectionServiceImpl是PartnerConnectionService接口的实现类。
 * 它负责处理Partner API操作的具体逻辑，包括CRUD操作、查询操作等。
 *
 * 主要功能：
 * 1. 实现Partner API的所有操作方法
 * 2. 处理与Salesforce SOAP API的通信
 * 3. 管理连接和会话
 *
 * 设计特点：
 * - 使用Spring注解声明为服务组件
 * - 实现PartnerConnectionService接口定义的所有方法
 * - 提供统一的异常处理机制
 * - 直接使用Force.com API，去除对PartnerClient的依赖
 * - 支持源和目标两个ORG的连接（用于数据迁移）
 *
 * 使用场景：
 * - 需要使用SOAP API进行数据操作的场景
 * - 需要复杂查询和操作的场景
 * - 数据在两个ORG之间迁移的场景
 *
 * @author Salesforce
 * @since 64.0.0
 */
@Service
public class PartnerConnectionServiceImpl implements PartnerConnectionService {

    // ==================== 向后兼容方法 - 默认使用源ORG ====================

    @Override
    public SaveResult[] create(SObject[] sObjects) throws ConnectionException {
        return createInSource(sObjects);
    }

    @Override
    public SaveResult[] update(SObject[] sObjects) throws ConnectionException {
        return updateInSource(sObjects);
    }

    @Override
    public DeleteResult[] delete(String[] ids) throws ConnectionException {
        return deleteInSource(ids);
    }

    @Override
    public UpsertResult[] upsert(String externalIDFieldName, SObject[] sObjects) throws ConnectionException {
        return upsertInSource(externalIDFieldName, sObjects);
    }

    @Override
    public UndeleteResult[] undelete(String[] ids) throws ConnectionException {
        return undeleteInSource(ids);
    }

    @Override
    public QueryResult query(String queryString) throws ConnectionException {
        return queryInSource(queryString);
    }

    @Override
    public QueryResult queryAll(String queryString) throws ConnectionException {
        return queryAllInSource(queryString);
    }

    @Override
    public QueryResult queryMore(String queryLocator) throws ConnectionException {
        return queryMoreInSource(queryLocator);
    }

    @Override
    public DescribeSObjectResult describeSObject(String sObjectType) throws ConnectionException {
        return describeSObjectInSource(sObjectType);
    }

    @Override
    public DescribeSObjectResult[] describeSObjects(String[] sObjectTypes) throws ConnectionException {
        return describeSObjectsInSource(sObjectTypes);
    }

    @Override
    public DescribeGlobalResult describeGlobal() throws ConnectionException {
        return describeGlobalInSource();
    }

    @Override
    public DescribeLayoutResult describeLayout(String sObjectType, String[] recordTypeIds) throws ConnectionException {
        return describeLayoutInSource(sObjectType, recordTypeIds);
    }

    @Override
    public DescribeLayoutResult describeLayout(String sObjectType, String recordTypeId, String[] recordTypeIds) throws ConnectionException {
        return describeLayoutInSource(sObjectType, recordTypeId, recordTypeIds);
    }

    // ==================== 源ORG专用方法 ====================

    @Override
    public SaveResult[] createInSource(SObject[] sObjects) throws ConnectionException {
        SourceSessionClient sessionClient = SourceSessionFactory.instance();
        PartnerConnection connection = createPartnerConnection(sessionClient);
        return connection.create(sObjects);
    }

    @Override
    public SaveResult[] updateInSource(SObject[] sObjects) throws ConnectionException {
        SourceSessionClient sessionClient = SourceSessionFactory.instance();
        PartnerConnection connection = createPartnerConnection(sessionClient);
        return connection.update(sObjects);
    }

    @Override
    public DeleteResult[] deleteInSource(String[] ids) throws ConnectionException {
        SourceSessionClient sessionClient = SourceSessionFactory.instance();
        PartnerConnection connection = createPartnerConnection(sessionClient);
        return connection.delete(ids);
    }

    @Override
    public UpsertResult[] upsertInSource(String externalIDFieldName, SObject[] sObjects) throws ConnectionException {
        SourceSessionClient sessionClient = SourceSessionFactory.instance();
        PartnerConnection connection = createPartnerConnection(sessionClient);
        return connection.upsert(externalIDFieldName, sObjects);
    }

    @Override
    public UndeleteResult[] undeleteInSource(String[] ids) throws ConnectionException {
        SourceSessionClient sessionClient = SourceSessionFactory.instance();
        PartnerConnection connection = createPartnerConnection(sessionClient);
        return connection.undelete(ids);
    }

    @Override
    public QueryResult queryInSource(String queryString) throws ConnectionException {
        SourceSessionClient sessionClient = SourceSessionFactory.instance();
        PartnerConnection connection = createPartnerConnection(sessionClient);
        return connection.query(queryString);
    }

    @Override
    public QueryResult queryAllInSource(String queryString) throws ConnectionException {
        SourceSessionClient sessionClient = SourceSessionFactory.instance();
        PartnerConnection connection = createPartnerConnection(sessionClient);
        return connection.queryAll(queryString);
    }

    @Override
    public QueryResult queryMoreInSource(String queryLocator) throws ConnectionException {
        SourceSessionClient sessionClient = SourceSessionFactory.instance();
        PartnerConnection connection = createPartnerConnection(sessionClient);
        return connection.queryMore(queryLocator);
    }

    @Override
    public DescribeSObjectResult describeSObjectInSource(String sObjectType) throws ConnectionException {
        SourceSessionClient sessionClient = SourceSessionFactory.instance();
        PartnerConnection connection = createPartnerConnection(sessionClient);
        return connection.describeSObject(sObjectType);
    }

    @Override
    public DescribeSObjectResult[] describeSObjectsInSource(String[] sObjectTypes) throws ConnectionException {
        SourceSessionClient sessionClient = SourceSessionFactory.instance();
        PartnerConnection connection = createPartnerConnection(sessionClient);
        return connection.describeSObjects(sObjectTypes);
    }

    @Override
    public DescribeGlobalResult describeGlobalInSource() throws ConnectionException {
        SourceSessionClient sessionClient = SourceSessionFactory.instance();
        PartnerConnection connection = createPartnerConnection(sessionClient);
        return connection.describeGlobal();
    }

    @Override
    public DescribeLayoutResult describeLayoutInSource(String sObjectType, String[] recordTypeIds) throws ConnectionException {
        SourceSessionClient sessionClient = SourceSessionFactory.instance();
        PartnerConnection connection = createPartnerConnection(sessionClient);
        return connection.describeLayout(sObjectType, (String)null, recordTypeIds);
    }

    @Override
    public DescribeLayoutResult describeLayoutInSource(String sObjectType, String recordTypeId, String[] recordTypeIds) throws ConnectionException {
        SourceSessionClient sessionClient = SourceSessionFactory.instance();
        PartnerConnection connection = createPartnerConnection(sessionClient);
        return connection.describeLayout(sObjectType, recordTypeId, recordTypeIds);
    }

    // ==================== 目标ORG专用方法 ====================

    @Override
    public SaveResult[] createInTarget(SObject[] sObjects) throws ConnectionException {
        TargetSessionClient sessionClient = TargetSessionFactory.instance();
        PartnerConnection connection = createPartnerConnection(sessionClient);
        return connection.create(sObjects);
    }

    @Override
    public SaveResult[] updateInTarget(SObject[] sObjects) throws ConnectionException {
        TargetSessionClient sessionClient = TargetSessionFactory.instance();
        PartnerConnection connection = createPartnerConnection(sessionClient);
        return connection.update(sObjects);
    }

    @Override
    public DeleteResult[] deleteInTarget(String[] ids) throws ConnectionException {
        TargetSessionClient sessionClient = TargetSessionFactory.instance();
        PartnerConnection connection = createPartnerConnection(sessionClient);
        return connection.delete(ids);
    }

    @Override
    public UpsertResult[] upsertInTarget(String externalIDFieldName, SObject[] sObjects) throws ConnectionException {
        TargetSessionClient sessionClient = TargetSessionFactory.instance();
        PartnerConnection connection = createPartnerConnection(sessionClient);
        return connection.upsert(externalIDFieldName, sObjects);
    }

    @Override
    public UndeleteResult[] undeleteInTarget(String[] ids) throws ConnectionException {
        TargetSessionClient sessionClient = TargetSessionFactory.instance();
        PartnerConnection connection = createPartnerConnection(sessionClient);
        return connection.undelete(ids);
    }

    @Override
    public QueryResult queryInTarget(String queryString) throws ConnectionException {
        TargetSessionClient sessionClient = TargetSessionFactory.instance();
        PartnerConnection connection = createPartnerConnection(sessionClient);
        return connection.query(queryString);
    }

    @Override
    public QueryResult queryAllInTarget(String queryString) throws ConnectionException {
        TargetSessionClient sessionClient = TargetSessionFactory.instance();
        PartnerConnection connection = createPartnerConnection(sessionClient);
        return connection.queryAll(queryString);
    }

    @Override
    public QueryResult queryMoreInTarget(String queryLocator) throws ConnectionException {
        TargetSessionClient sessionClient = TargetSessionFactory.instance();
        PartnerConnection connection = createPartnerConnection(sessionClient);
        return connection.queryMore(queryLocator);
    }

    @Override
    public DescribeSObjectResult describeSObjectInTarget(String sObjectType) throws ConnectionException {
        TargetSessionClient sessionClient = TargetSessionFactory.instance();
        PartnerConnection connection = createPartnerConnection(sessionClient);
        return connection.describeSObject(sObjectType);
    }

    @Override
    public DescribeSObjectResult[] describeSObjectsInTarget(String[] sObjectTypes) throws ConnectionException {
        TargetSessionClient sessionClient = TargetSessionFactory.instance();
        PartnerConnection connection = createPartnerConnection(sessionClient);
        return connection.describeSObjects(sObjectTypes);
    }

    @Override
    public DescribeGlobalResult describeGlobalInTarget() throws ConnectionException {
        TargetSessionClient sessionClient = TargetSessionFactory.instance();
        PartnerConnection connection = createPartnerConnection(sessionClient);
        return connection.describeGlobal();
    }

    @Override
    public DescribeLayoutResult describeLayoutInTarget(String sObjectType, String[] recordTypeIds) throws ConnectionException {
        TargetSessionClient sessionClient = TargetSessionFactory.instance();
        PartnerConnection connection = createPartnerConnection(sessionClient);
        return connection.describeLayout(sObjectType, (String)null, recordTypeIds);
    }

    @Override
    public DescribeLayoutResult describeLayoutInTarget(String sObjectType, String recordTypeId, String[] recordTypeIds) throws ConnectionException {
        TargetSessionClient sessionClient = TargetSessionFactory.instance();
        PartnerConnection connection = createPartnerConnection(sessionClient);
        return connection.describeLayout(sObjectType, recordTypeId, recordTypeIds);
    }

    /**
     * 创建PartnerConnection实例
     *
     * @param sessionClient 会话客户端
     * @return PartnerConnection Partner连接实例
     * @throws ConnectionException 连接异常
     */
    private PartnerConnection createPartnerConnection(Object sessionClient) throws ConnectionException {
        ConnectorConfig config = new ConnectorConfig();

        if (sessionClient instanceof SourceSessionClient) {
            SourceSessionClient sourceSession = (SourceSessionClient) sessionClient;
            config.setSessionId(sourceSession.getSessionId());
            config.setServiceEndpoint(sourceSession.getServer() + "/services/Soap/u/59.0");
        } else if (sessionClient instanceof TargetSessionClient) {
            TargetSessionClient targetSession = (TargetSessionClient) sessionClient;
            config.setSessionId(targetSession.getSessionId());
            config.setServiceEndpoint(targetSession.getServer() + "/services/Soap/u/59.0");
        }

        return new PartnerConnection(config);
    }
}
