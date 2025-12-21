package org.dromara.salesforce.factory;

import com.sforce.ws.ConnectorConfig;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.CacheNames;
import org.dromara.salesforce.core.SourceSessionClient;
import org.dromara.salesforce.core.TargetSessionClient;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * REST连接工厂类 - 管理全局唯一的REST连接配置实例
 *
 * @author Salesforce
 */
@Slf4j
public class RESTConnectionFactory {

    private static final Map<String, ConnectorConfig> SOURCE_CONNECTION_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, ConnectorConfig> TARGET_CONNECTION_CACHE = new ConcurrentHashMap<>();
    private static final ReentrantLock SOURCE_LOCK = new ReentrantLock();
    private static final ReentrantLock TARGET_LOCK = new ReentrantLock();

    /**
     * 获取源ORG的REST连接配置实例
     * 如果缓存中没有有效的连接配置，则根据源会话客户端创建新的连接配置
     *
     * @return ConnectorConfig REST连接配置实例
     */
    public static ConnectorConfig sourceInstance() {
        String configKey = CacheNames.SOURCE_SESSION_CONFIG;

        ConnectorConfig config = SOURCE_CONNECTION_CACHE.get(configKey);
        if (config == null) {
            SOURCE_LOCK.lock();
            try {
                config = SOURCE_CONNECTION_CACHE.get(configKey);
                if (config == null) {
                    SourceSessionClient sessionClient = SourceSessionFactory.instance();
                    config = createRESTConnection(sessionClient);
                    SOURCE_CONNECTION_CACHE.put(configKey, config);
                    log.info("创建并缓存源ORG的REST连接配置实例");
                }
            } finally {
                SOURCE_LOCK.unlock();
            }
        }
        return config;
    }

    /**
     * 获取目标ORG的REST连接配置实例
     * 如果缓存中没有有效的连接配置，则根据目标会话客户端创建新的连接配置
     *
     * @return ConnectorConfig REST连接配置实例
     */
    public static ConnectorConfig targetInstance() {
        String configKey = CacheNames.TARGET_SESSION_CONFIG;

        ConnectorConfig config = TARGET_CONNECTION_CACHE.get(configKey);
        if (config == null) {
            TARGET_LOCK.lock();
            try {
                config = TARGET_CONNECTION_CACHE.get(configKey);
                if (config == null) {
                    TargetSessionClient sessionClient = TargetSessionFactory.instance();
                    config = createRESTConnection(sessionClient);
                    TARGET_CONNECTION_CACHE.put(configKey, config);
                    log.info("创建并缓存目标ORG的REST连接配置实例");
                }
            } finally {
                TARGET_LOCK.unlock();
            }
        }
        return config;
    }

    /**
     * 根据源会话客户端创建REST连接配置
     *
     * @param sessionClient 源会话客户端
     * @return ConnectorConfig REST连接配置实例
     */
    private static ConnectorConfig createRESTConnection(SourceSessionClient sessionClient) {
        ConnectorConfig config = new ConnectorConfig();
        config.setSessionId(sessionClient.getSessionId());
        config.setRestEndpoint(sessionClient.getServer() + "/services/data/v59.0/");
        return config;
    }

    /**
     * 根据目标会话客户端创建REST连接配置
     *
     * @param sessionClient 目标会话客户端
     * @return ConnectorConfig REST连接配置实例
     */
    private static ConnectorConfig createRESTConnection(TargetSessionClient sessionClient) {
        ConnectorConfig config = new ConnectorConfig();
        config.setSessionId(sessionClient.getSessionId());
        config.setRestEndpoint(sessionClient.getServer() + "/services/data/v59.0/");
        return config;
    }

    /**
     * 清除源ORG的REST连接配置
     * 从缓存中移除源ORG的REST连接配置实例
     */
    public static void clearSourceConnection() {
        String configKey = CacheNames.SOURCE_SESSION_CONFIG;
        ConnectorConfig config = SOURCE_CONNECTION_CACHE.remove(configKey);
        if (config != null) {
            log.info("源ORG的REST连接配置已清除");
        } else {
            log.info("没有找到源ORG的REST连接配置");
        }
    }

    /**
     * 清除目标ORG的REST连接配置
     * 从缓存中移除目标ORG的REST连接配置实例
     */
    public static void clearTargetConnection() {
        String configKey = CacheNames.TARGET_SESSION_CONFIG;
        ConnectorConfig config = TARGET_CONNECTION_CACHE.remove(configKey);
        if (config != null) {
            log.info("目标ORG的REST连接配置已清除");
        } else {
            log.info("没有找到目标ORG的REST连接配置");
        }
    }
}