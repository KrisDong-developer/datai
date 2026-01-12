package com.datai.integration.factory.impl;

import com.datai.integration.core.SalesforcePubSubClient;
import com.datai.integration.factory.AbstractConnectionFactory;
import com.datai.salesforce.auth.service.ISalesforceAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Salesforce Pub/Sub API 连接工厂
 * 用于创建和管理 PubSubClient 实例
 */
@Slf4j
@Component
public class PubSubConnectionFactory extends AbstractConnectionFactory<SalesforcePubSubClient> {

    @Autowired
    private ISalesforceAuthService salesforceAuthService;

    @Autowired
    private SalesforcePubSubClient pubSubClient;

    // 连接池
    private final ConcurrentMap<String, SalesforcePubSubClient> connectionPool = new ConcurrentHashMap<>();
    private final AtomicInteger connectionCount = new AtomicInteger(0);

    @Value("${salesforce.pubsub.connection.pool.size:5}")
    private int maxPoolSize;

    @Value("${salesforce.pubsub.connection.ttl:3600}")
    private int connectionTtl;

    /**
     * 获取 Salesforce Pub/Sub API 客户端
     * @return SalesforcePubSubClient 实例
     */
    @Override
    public SalesforcePubSubClient getConnection() {
        try {
            // 检查是否已连接
            if (pubSubClient.isConnected()) {
                log.info("Pub/Sub API 客户端已连接，直接返回实例");
                return pubSubClient;
            }

            // 获取 Salesforce 访问令牌和实例 URL
            String accessToken = salesforceAuthService.getAccessToken();
            String instanceUrl = salesforceAuthService.getInstanceUrl();

            if (accessToken == null || instanceUrl == null) {
                log.error("无法获取 Salesforce 访问令牌或实例 URL");
                return null;
            }

            // 初始化 Pub/Sub API 客户端
            boolean initialized = pubSubClient.initialize(accessToken, instanceUrl);
            if (initialized) {
                log.info("成功获取 Pub/Sub API 客户端实例");
                // 添加到连接池
                addToConnectionPool(accessToken, pubSubClient);
                return pubSubClient;
            } else {
                log.error("初始化 Pub/Sub API 客户端失败");
                return null;
            }
        } catch (Exception e) {
            log.error("获取 Pub/Sub API 客户端实例失败: {}", e.getMessage(), e);
            return null;
        }
    }

    /**
     * 从连接池获取客户端
     * @param key 连接键
     * @return SalesforcePubSubClient 实例
     */
    public SalesforcePubSubClient getConnectionFromPool(String key) {
        return connectionPool.get(key);
    }

    /**
     * 添加客户端到连接池
     * @param key 连接键
     * @param client SalesforcePubSubClient 实例
     */
    private void addToConnectionPool(String key, SalesforcePubSubClient client) {
        // 检查连接池大小
        if (connectionCount.get() >= maxPoolSize) {
            // 清理过期连接
            cleanupExpiredConnections();
            // 如果仍然超过最大大小，移除最早的连接
            if (connectionCount.get() >= maxPoolSize) {
                removeOldestConnection();
            }
        }

        connectionPool.put(key, client);
        connectionCount.incrementAndGet();
        log.info("添加连接到连接池，当前连接数: {}", connectionCount.get());
    }

    /**
     * 清理过期连接
     */
    private void cleanupExpiredConnections() {
        // 这里可以实现过期连接清理逻辑
        // 例如，检查连接创建时间，移除超过TTL的连接
        log.info("清理过期连接");
    }

    /**
     * 移除最早的连接
     */
    private void removeOldestConnection() {
        // 这里可以实现移除最早连接的逻辑
        // 例如，根据连接添加时间排序，移除最早的
        if (!connectionPool.isEmpty()) {
            String oldestKey = connectionPool.keySet().iterator().next();
            SalesforcePubSubClient client = connectionPool.remove(oldestKey);
            if (client != null) {
                client.disconnect();
                connectionCount.decrementAndGet();
                log.info("移除最早的连接，当前连接数: {}", connectionCount.get());
            }
        }
    }

    /**
     * 关闭 Salesforce Pub/Sub API 连接
     * @param connection SalesforcePubSubClient 实例
     */
    @Override
    public void closeConnection(SalesforcePubSubClient connection) {
        try {
            if (connection != null) {
                connection.disconnect();
                log.info("成功关闭 Pub/Sub API 连接");
                // 从连接池移除
                removeFromConnectionPool(connection);
            }
        } catch (Exception e) {
            log.error("关闭 Pub/Sub API 连接失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 从连接池移除连接
     * @param client SalesforcePubSubClient 实例
     */
    private void removeFromConnectionPool(SalesforcePubSubClient client) {
        // 遍历连接池，找到并移除对应连接
        for (String key : connectionPool.keySet()) {
            if (connectionPool.get(key) == client) {
                connectionPool.remove(key);
                connectionCount.decrementAndGet();
                log.info("从连接池移除连接，当前连接数: {}", connectionCount.get());
                break;
            }
        }
    }

    /**
     * 清理所有连接
     */
    public void cleanupAllConnections() {
        log.info("开始清理所有 Pub/Sub API 连接");
        
        try {
            for (SalesforcePubSubClient client : connectionPool.values()) {
                if (client != null) {
                    client.disconnect();
                }
            }
            connectionPool.clear();
            connectionCount.set(0);
            log.info("成功清理所有 Pub/Sub API 连接");
        } catch (Exception e) {
            log.error("清理所有 Pub/Sub API 连接失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 获取连接池大小
     * @return 连接池大小
     */
    public int getPoolSize() {
        return connectionCount.get();
    }

    /**
     * 健康检查
     * @return 是否健康
     */
    public boolean healthCheck() {
        try {
            // 检查主客户端连接状态
            if (pubSubClient.isConnected()) {
                return true;
            }
            
            // 检查连接池中的连接
            for (SalesforcePubSubClient client : connectionPool.values()) {
                if (client != null && client.isConnected()) {
                    return true;
                }
            }
            
            return false;
        } catch (Exception e) {
            log.error("健康检查失败: {}", e.getMessage(), e);
            return false;
        }
    }
}
