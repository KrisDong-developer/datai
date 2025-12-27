package com.datai.integration.factory.impl;

import com.datai.integration.factory.AbstractConnectionFactory;
import com.sforce.ws.ConnectorConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * REST连接工厂类 - 管理全局唯一的REST连接配置实例
 *
 * @author Salesforce
 */
@Slf4j
@Component
public class RESTConnectionFactory extends AbstractConnectionFactory<ConnectorConfig> {

    @Override
    protected ConnectorConfig createConnection() {
        ConnectorConfig config = new ConnectorConfig();
        config.setSessionId(getSessionId());
        config.setRestEndpoint(getInstanceUrl() + "/services/data/v59.0/");
        return config;
    }
}
