package com.datai.integration.factory.impl;

import com.datai.integration.core.BulkV1Connection;
import com.datai.integration.core.IBulkV1Connection;
import com.datai.integration.factory.AbstractConnectionFactory;
import com.datai.integration.proxy.ConnectionProxy;
import com.sforce.ws.ConnectorConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * BulkV1连接工厂类 - 管理全局唯一的BulkV1连接实例
 *
 * @author Salesforce
 */
@Slf4j
@Component
public class BulkV1ConnectionFactory extends AbstractConnectionFactory<IBulkV1Connection> {

    @Autowired
    private ConnectionProxy connectionProxy;

    @Override
    protected IBulkV1Connection createConnection() {
        try {
            ConnectorConfig config = new ConnectorConfig();
            config.setSessionId(getSessionId());
            config.setServiceEndpoint(getInstanceUrl() + "/services/async/47.0");
            BulkV1Connection connection = new BulkV1Connection(config);
            return connectionProxy.createProxy(connection, "BULK_V1");
        } catch (Exception e) {
            log.error("创建BulkV1连接失败", e);
            throw new RuntimeException("创建BulkV1连接失败: " + e.getMessage(), e);
        }
    }
}
