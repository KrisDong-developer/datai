package com.datai.integration.factory.impl;

import com.datai.integration.core.IPartnerV1Connection;
import com.datai.integration.core.PartnerV1Connection;
import com.datai.integration.factory.AbstractConnectionFactory;
import com.datai.integration.proxy.ConnectionProxy;
import com.sforce.ws.ConnectorConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * SOAP连接工厂类 - 管理全局唯一的SOAP连接实例
 *
 * @author Salesforce
 */
@Slf4j
@Component
public class SOAPConnectionFactory extends AbstractConnectionFactory<IPartnerV1Connection> {

    @Autowired
    private ConnectionProxy connectionProxy;

    @Override
    protected IPartnerV1Connection createConnection() {
        try {
            ConnectorConfig config = new ConnectorConfig();
            config.setSessionId(getSessionId());
            config.setServiceEndpoint(getInstanceUrl() + "/services/Soap/u/59.0");
            
            PartnerV1Connection connection = new PartnerV1Connection(config);
            
            return connectionProxy.createProxy(connection, "SOAP");
        } catch (Exception e) {
            log.error("创建SOAP连接失败", e);
            throw new RuntimeException("创建SOAP连接失败: " + e.getMessage(), e);
        }
    }
}
