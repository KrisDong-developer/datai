package org.dromara.salesforce.factory;

import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectorConfig;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.CacheNames;
import org.dromara.salesforce.core.SourceSessionClient;
import org.dromara.salesforce.core.TargetSessionClient;

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

    private static final Map<String, PartnerConnection> SOURCE_CONNECTION_CACHE = new ConcurrentHashMap<>();
    private static final Map<String, PartnerConnection> TARGET_CONNECTION_CACHE = new ConcurrentHashMap<>();
    private static final ReentrantLock SOURCE_LOCK = new ReentrantLock();
    private static final ReentrantLock TARGET_LOCK = new ReentrantLock();

    /**
     * 获取源ORG的SOAP连接实例
     * 如果缓存中没有有效的连接，则根据源会话客户端创建新的连接
     *
     * @return PartnerConnection SOAP连接实例
     */
    public static PartnerConnection sourceInstance() {
        String configKey = CacheNames.SOURCE_SESSION_CONFIG;

        PartnerConnection connection = SOURCE_CONNECTION_CACHE.get(configKey);
        if (connection == null) {
            SOURCE_LOCK.lock();
            try {
                connection = SOURCE_CONNECTION_CACHE.get(configKey);
                if (connection == null) {
                    SourceSessionClient sessionClient = SourceSessionFactory.instance();
                    connection = createSOAPConnection(sessionClient);
                    SOURCE_CONNECTION_CACHE.put(configKey, connection);
                    log.info("创建并缓存源ORG的SOAP连接实例");
                }
            } finally {
                SOURCE_LOCK.unlock();
            }
        }
        return connection;
    }

    /**
     * 获取目标ORG的SOAP连接实例
     * 如果缓存中没有有效的连接，则根据目标会话客户端创建新的连接
     *
     * @return PartnerConnection SOAP连接实例
     */
    public static PartnerConnection targetInstance() {
        String configKey = CacheNames.TARGET_SESSION_CONFIG;

        PartnerConnection connection = TARGET_CONNECTION_CACHE.get(configKey);
        if (connection == null) {
            TARGET_LOCK.lock();
            try {
                connection = TARGET_CONNECTION_CACHE.get(configKey);
                if (connection == null) {
                    TargetSessionClient sessionClient = TargetSessionFactory.instance();
                    connection = createSOAPConnection(sessionClient);
                    TARGET_CONNECTION_CACHE.put(configKey, connection);
                    log.info("创建并缓存目标ORG的SOAP连接实例");
                }
            } finally {
                TARGET_LOCK.unlock();
            }
        }
        return connection;
    }

    /**
     * 根据源会话客户端创建SOAP连接
     *
     * @param sessionClient 源会话客户端
     * @return PartnerConnection SOAP连接实例
     */
    private static PartnerConnection createSOAPConnection(SourceSessionClient sessionClient) {
        try {
            ConnectorConfig config = new ConnectorConfig();
            config.setSessionId(sessionClient.getSessionId());
            config.setServiceEndpoint(sessionClient.getServer() + "/services/Soap/u/59.0");
            return new PartnerConnection(config);
        } catch (Exception e) {
            log.error("Failed to create SOAP connection", e);
            throw new RuntimeException("创建源ORG的SOAP连接失败: " + e.getMessage(), e);
        }
    }

    /**
     * 根据目标会话客户端创建SOAP连接
     *
     * @param sessionClient 目标会话客户端
     * @return PartnerConnection SOAP连接实例
     */
    private static PartnerConnection createSOAPConnection(TargetSessionClient sessionClient) {
        try {
            ConnectorConfig config = new ConnectorConfig();
            config.setSessionId(sessionClient.getSessionId());
            config.setServiceEndpoint(sessionClient.getServer() + "/services/Soap/u/59.0");
            return new PartnerConnection(config);
        } catch (Exception e) {
            log.error("Failed to create SOAP connection", e);
            throw new RuntimeException("创建目标ORG的SOAP连接失败: " + e.getMessage(), e);
        }
    }

    /**
     * 清除源ORG的SOAP连接
     * 从缓存中移除源ORG的SOAP连接实例
     */
    public static void clearSourceConnection() {
        String configKey = CacheNames.SOURCE_SESSION_CONFIG;
        PartnerConnection connection = SOURCE_CONNECTION_CACHE.remove(configKey);
        if (connection != null) {
            log.info("源ORG的SOAP连接已清除");
        } else {
            log.info("没有找到源ORG的SOAP连接");
        }
    }

    /**
     * 清除目标ORG的SOAP连接
     * 从缓存中移除目标ORG的SOAP连接实例
     */
    public static void clearTargetConnection() {
        String configKey = CacheNames.TARGET_SESSION_CONFIG;
        PartnerConnection connection = TARGET_CONNECTION_CACHE.remove(configKey);
        if (connection != null) {
            log.info("目标ORG的SOAP连接已清除");
        } else {
            log.info("没有找到目标ORG的SOAP连接");
        }
    }
}