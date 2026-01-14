package com.datai.integration.factory.impl;

import com.datai.salesforce.common.constant.SalesforceConstants;
import com.datai.integration.core.IRESTConnection;
import com.datai.integration.core.RESTConnection;
import com.datai.integration.factory.AbstractConnectionFactory;
import com.datai.integration.proxy.ConnectionProxy;
import com.sforce.async.AsyncApiException;
import com.sforce.ws.ConnectorConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class RESTConnectionFactory extends AbstractConnectionFactory<IRESTConnection> {

    @Autowired
    private ConnectionProxy connectionProxy;

    @Override
    protected IRESTConnection createConnection(String orgType) {
        try {
            ConnectorConfig config = new ConnectorConfig();
            config.setSessionId(sessionManager.getCurrentSession(orgType));
            config.setRestEndpoint(sessionManager.getInstanceUrl(orgType) + "/services/data/v" + 
                SalesforceConstants.REST_API_VERSION.replace("v", "") + "/");
            RESTConnection connection = new RESTConnection(config);
            return connectionProxy.createProxy(connection, "REST");
        } catch (AsyncApiException e) {
            log.error("创建REST连接失败，ORG类型: {}", orgType, e);
            throw new RuntimeException("创建REST连接失败: " + e.getMessage(), e);
        }
    }
}
