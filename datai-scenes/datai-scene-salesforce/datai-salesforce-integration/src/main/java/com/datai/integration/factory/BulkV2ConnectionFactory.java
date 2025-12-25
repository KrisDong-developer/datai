package com.datai.integration.factory;

import com.datai.integration.constant.SalesforceConstants;
import com.datai.integration.core.BulkV2Connection;
import com.sforce.ws.ConnectorConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * BulkV2连接工厂类 - 管理全局唯一的BulkV2连接实例
 *
 * @author Salesforce
 */
@Slf4j
public class BulkV2ConnectionFactory {

    private static final Map<String, BulkV2Connection> CONNECTION_CACHE = new ConcurrentHashMap<>();
    private static final ReentrantLock LOCK = new ReentrantLock();

    /**
     * 获取BulkV2连接实例
     * 如果缓存中没有有效的连接，则根据客户端创建新的连接
     *
     * @return BulkV2Connection BulkV2连接实例
     */
    public static BulkV2Connection instance() {
        String configKey = SalesforceConstants.SESSION_CONFIG_SOURCE;

        BulkV2Connection connection = CONNECTION_CACHE.get(configKey);
        if (connection == null) {
            LOCK.lock();
            try {
                connection = CONNECTION_CACHE.get(configKey);
                if (connection == null) {
                    connection = createBulkV2Connection();
                    CONNECTION_CACHE.put(configKey, connection);
                    log.info("创建并缓存BulkV2连接实例");
                }
            } finally {
                LOCK.unlock();
            }
        }
        return connection;
    }

    /**
     * 创建BulkV2连接
     * 通过调用auth模块的/status接口获取会话信息
     *
     * @return BulkV2Connection BulkV2连接实例
     */
    private static BulkV2Connection createBulkV2Connection() {
        try {
            ConnectorConfig config = new ConnectorConfig();
            config.setSessionId(com.datai.integration.util.SessionUtil.getAccessToken());
            config.setServiceEndpoint(com.datai.integration.util.SessionUtil.getInstanceUrl() + "/services/data/v59.0/jobs/");
            return new BulkV2Connection(config);
        } catch (Exception e) {
            log.error("Failed to create BulkV2 connection", e);
            throw new RuntimeException("创建BulkV2连接失败: " + e.getMessage(), e);
        }
    }

    /**
     * 清除BulkV2连接
     * 从缓存中移除BulkV2连接实例
     */
    public static void clearConnection() {
        String configKey = SalesforceConstants.SESSION_CONFIG_SOURCE;
        BulkV2Connection connection = CONNECTION_CACHE.remove(configKey);
        if (connection != null) {
            log.info("BulkV2连接已清除");
        } else {
            log.info("没有找到BulkV2连接");
        }
    }
}