package com.datai.integration.factory.impl;

import com.datai.salesforce.common.constant.SalesforceConstants;
import com.datai.integration.core.BulkV2Connection;
import com.datai.integration.core.IBulkV2Connection;
import com.datai.integration.factory.AbstractConnectionFactory;
import com.datai.integration.proxy.ConnectionProxy;
import com.sforce.ws.ConnectorConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BulkV2ConnectionFactory extends AbstractConnectionFactory<IBulkV2Connection> {

    @Autowired
    private ConnectionProxy connectionProxy;

    @Override
    protected IBulkV2Connection createConnection() {
        try {
            ConnectorConfig config = new ConnectorConfig();
            config.setSessionId(getSessionId());
            config.setServiceEndpoint(getInstanceUrl() + "/services/data/v" + 
                SalesforceConstants.REST_API_VERSION.replace("v", "") + "/jobs/");
            BulkV2Connection connection = new BulkV2Connection(config);
            return connectionProxy.createProxy(connection, "BULK_V2");
        } catch (Exception e) {
            log.error("创建BulkV2连接失败", e);
            throw new RuntimeException("创建BulkV2连接失败: " + e.getMessage(), e);
        }
    }
}
