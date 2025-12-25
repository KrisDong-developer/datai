package com.datai.integration.factory;

import com.datai.integration.constant.SalesforceConstants;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectorConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * SOAP连接工厂类 - 管理全局唯一的SOAP连接实例
 *
 * @author Salesforce
 */
@Slf4j
public class SOAPConnectionFactory {

    private static final Map<String, PartnerConnection> CONNECTION_CACHE = new ConcurrentHashMap<>();
    private static final ReentrantLock LOCK = new ReentrantLock();

    /**
     * 获取SOAP连接实例
     * 如果缓存中没有有效的连接，则根据当前会话创建新的连接
     *
     * @return PartnerConnection SOAP连接实例
     */
    public static PartnerConnection instance() {
        String configKey = SalesforceConstants.SESSION_CONFIG_SOURCE;

        PartnerConnection connection = CONNECTION_CACHE.get(configKey);
        if (connection == null) {
            LOCK.lock();
            try {
                connection = CONNECTION_CACHE.get(configKey);
                if (connection == null) {
                    connection = createSOAPConnection();
                    CONNECTION_CACHE.put(configKey, connection);
                    log.info("创建并缓存SOAP连接实例");
                }
            } finally {
                LOCK.unlock();
            }
        }
        return connection;
    }

    /**
     * 创建SOAP连接
     * 通过调用auth模块的/status接口获取会话信息
     *
     * @return PartnerConnection SOAP连接实例
     */
    private static PartnerConnection createSOAPConnection() {
        try {
            ConnectorConfig config = new ConnectorConfig();
            config.setSessionId(com.datai.integration.util.SessionUtil.getAccessToken());
            config.setServiceEndpoint(com.datai.integration.util.SessionUtil.getInstanceUrl() + "/services/Soap/u/59.0");
            return new PartnerConnection(config);
        } catch (Exception e) {
            log.error("Failed to create SOAP connection", e);
            throw new RuntimeException("创建SOAP连接失败: " + e.getMessage(), e);
        }
    }

    /**
     * 清除SOAP连接
     * 从缓存中移除SOAP连接实例
     */
    public static void clearConnection() {
        String configKey = SalesforceConstants.SESSION_CONFIG_SOURCE;
        PartnerConnection connection = CONNECTION_CACHE.remove(configKey);
        if (connection != null) {
            log.info("SOAP连接已清除");
        } else {
            log.info("没有找到SOAP连接");
        }
    }
}