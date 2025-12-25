package com.datai.integration.factory;

import com.datai.integration.constant.SalesforceConstants;
import com.datai.integration.core.BulkV1Connection;
import com.sforce.ws.ConnectorConfig;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * BulkV1连接工厂类 - 管理全局唯一的BulkV1连接实例
 *
 * @author Salesforce
 */
@Slf4j
public class BulkV1ConnectionFactory {

    private static final Map<String, BulkV1Connection> CONNECTION_CACHE = new ConcurrentHashMap<>();
    private static final ReentrantLock LOCK = new ReentrantLock();

    /**
     * 获取BulkV1连接实例
     * 如果缓存中没有有效的连接，则根据当前会话创建新的连接
     *
     * @return BulkV1Connection BulkV1连接实例
     */
    public static BulkV1Connection instance() {
        String configKey = SalesforceConstants.SESSION_CONFIG_SOURCE;

        BulkV1Connection connection = CONNECTION_CACHE.get(configKey);
        if (connection == null) {
            LOCK.lock();
            try {
                connection = CONNECTION_CACHE.get(configKey);
                if (connection == null) {
                    connection = createBulkV1Connection();
                    CONNECTION_CACHE.put(configKey, connection);
                    log.info("创建并缓存BulkV1连接实例");
                }
            } finally {
                LOCK.unlock();
            }
        }
        return connection;
    }

    /**
     * 创建BulkV1连接
     * 通过调用auth模块的/status接口获取会话信息
     *
     * @return BulkV1Connection BulkV1连接实例
     */
    private static BulkV1Connection createBulkV1Connection() {
        try {
            ConnectorConfig config = new ConnectorConfig();
            config.setSessionId(com.datai.integration.util.SessionUtil.getAccessToken());
            config.setServiceEndpoint(com.datai.integration.util.SessionUtil.getInstanceUrl() + "/services/async/47.0");
            return new BulkV1Connection(config);
        } catch (Exception e) {
            log.error("Failed to create BulkV1 connection", e);
            throw new RuntimeException("创建BulkV1连接失败: " + e.getMessage(), e);
        }
    }

    /**
     * 清除BulkV1连接
     * 从缓存中移除BulkV1连接实例
     */
    public static void clearConnection() {
        String configKey = SalesforceConstants.SESSION_CONFIG_SOURCE;
        BulkV1Connection connection = CONNECTION_CACHE.remove(configKey);
        if (connection != null) {
            log.info("BulkV1连接已清除");
        } else {
            log.info("没有找到BulkV1连接");
        }
    }
}