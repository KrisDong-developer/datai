package com.datai.integration.factory.impl;

import com.datai.integration.core.BulkV2Connection;
import com.datai.integration.factory.AbstractConnectionFactory;
import com.sforce.ws.ConnectorConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * BulkV2连接工厂类 - 管理全局唯一的BulkV2连接实例
 *
 * @author Salesforce
 */
@Slf4j
@Component
public class BulkV2ConnectionFactory extends AbstractConnectionFactory<BulkV2Connection> {

    @Override
    protected BulkV2Connection createConnection() {
        try {
            ConnectorConfig config = new ConnectorConfig();
            config.setSessionId(getSessionId());
            config.setServiceEndpoint(getInstanceUrl() + "/services/data/v59.0/jobs/");
            return new BulkV2Connection(config);
        } catch (Exception e) {
            log.error("创建BulkV2连接失败", e);
            throw new RuntimeException("创建BulkV2连接失败: " + e.getMessage(), e);
        }
    }
}
