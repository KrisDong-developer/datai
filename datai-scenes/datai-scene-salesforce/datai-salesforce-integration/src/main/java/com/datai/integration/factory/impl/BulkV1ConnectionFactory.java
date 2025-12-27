package com.datai.integration.factory.impl;

import com.datai.integration.core.BulkV1Connection;
import com.datai.integration.factory.AbstractConnectionFactory;
import com.sforce.ws.ConnectorConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * BulkV1连接工厂类 - 管理全局唯一的BulkV1连接实例
 *
 * @author Salesforce
 */
@Slf4j
@Component
public class BulkV1ConnectionFactory extends AbstractConnectionFactory<BulkV1Connection> {

    @Override
    protected BulkV1Connection createConnection() {
        try {
            ConnectorConfig config = new ConnectorConfig();
            config.setSessionId(getSessionId());
            config.setServiceEndpoint(getInstanceUrl() + "/services/async/47.0");
            return new BulkV1Connection(config);
        } catch (Exception e) {
            log.error("创建BulkV1连接失败", e);
            throw new RuntimeException("创建BulkV1连接失败: " + e.getMessage(), e);
        }
    }
}
