package com.datai.integration.factory;

import com.datai.integration.core.SessionManager;
import com.datai.salesforce.common.constant.SalesforceConstants;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Salesforce连接工厂抽象基类
 * 提供通用的连接缓存和管理逻辑，使用模板方法模式
 *
 * @param <T> 连接类型
 * @author Salesforce
 */
@Slf4j
public abstract class AbstractConnectionFactory<T> implements ISalesforceConnectionFactory<T> {

    @Autowired
    protected SessionManager sessionManager;

    private final Map<String, T> connectionCache = new ConcurrentHashMap<>();
    private final ReentrantLock lock = new ReentrantLock();

    @Override
    public T getConnection() {
        String configKey = getConfigKey();

        T connection = connectionCache.get(configKey);
        
        if (connection != null && !sessionManager.isSessionValid()) {
            log.warn("检测到Session已过期，清除缓存的{}连接", getConnectionType());
            lock.lock();
            try {
                connectionCache.remove(configKey);
                connection = null;
            } finally {
                lock.unlock();
            }
        }
        
        if (connection == null) {
            lock.lock();
            try {
                connection = connectionCache.get(configKey);
                
                if (connection == null) {
                    connection = createConnection();
                    connectionCache.put(configKey, connection);
                    log.info("创建并缓存{}连接实例", getConnectionType());
                }
            } finally {
                lock.unlock();
            }
        }
        return connection;
    }

    @Override
    public void clearConnection() {
        String configKey = getConfigKey();
        T connection = connectionCache.remove(configKey);
        if (connection != null) {
            log.info("{}连接已清除", getConnectionType());
        } else {
            log.info("没有找到{}连接", getConnectionType());
        }
    }

    @Override
    public String getConnectionType() {
        return this.getClass().getSimpleName().replace("Factory", "");
    }

    protected String getConfigKey() {
        return SalesforceConstants.SESSION_CONFIG_SOURCE;
    }

    protected String getSessionId() {
        return sessionManager.getCurrentSession();
    }

    protected String getInstanceUrl() {
        return sessionManager.getInstanceUrl();
    }

    protected abstract T createConnection();
}
