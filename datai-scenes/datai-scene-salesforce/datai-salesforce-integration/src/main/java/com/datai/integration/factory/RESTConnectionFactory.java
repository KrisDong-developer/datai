package com.datai.integration.factory;

import com.datai.salesforce.common.constant.SalesforceConstants;
import com.sforce.ws.ConnectorConfig;
import lombok.extern.slf4j.Slf4j;

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

    private static final Map<String, ConnectorConfig> CONNECTION_CACHE = new ConcurrentHashMap<>();
    private static final ReentrantLock LOCK = new ReentrantLock();

    /**
     * 获取REST连接配置实例
     * 如果缓存中没有有效的连接配置，则根据当前会话创建新的连接配置
     *
     * @return ConnectorConfig REST连接配置实例
     */
    public static ConnectorConfig instance() {
        String configKey = SalesforceConstants.SESSION_CONFIG_SOURCE;

        ConnectorConfig config = CONNECTION_CACHE.get(configKey);
        if (config == null) {
            LOCK.lock();
            try {
                config = CONNECTION_CACHE.get(configKey);
                if (config == null) {
                    config = createRESTConnection();
                    CONNECTION_CACHE.put(configKey, config);
                    log.info("创建并缓存REST连接配置实例");
                }
            } finally {
                LOCK.unlock();
            }
        }
        return config;
    }

    /**
     * 创建REST连接配置
     * 通过调用auth模块的/status接口获取会话信息
     *
     * @return ConnectorConfig REST连接配置实例
     */
    private static ConnectorConfig createRESTConnection() {
        ConnectorConfig config = new ConnectorConfig();
        config.setSessionId(com.datai.integration.util.SessionUtil.getCurrentSession());
        config.setRestEndpoint(com.datai.integration.util.SessionUtil.getInstanceUrl() + "/services/data/v59.0/");
        return config;
    }

    /**
     * 清除REST连接配置
     * 从缓存中移除REST连接配置实例
     */
    public static void clearConnection() {
        String configKey = SalesforceConstants.SESSION_CONFIG_SOURCE;
        ConnectorConfig config = CONNECTION_CACHE.remove(configKey);
        if (config != null) {
            log.info("REST连接配置已清除");
        } else {
            log.info("没有找到REST连接配置");
        }
    }
}