package org.dromara.salesforce.factory;

import com.sforce.soap.partner.GetUserInfoResult;
import com.sforce.soap.partner.LoginResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import com.sforce.ws.ConnectorConfig;
import lombok.extern.slf4j.Slf4j;
import org.dromara.common.core.constant.CacheNames;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.salesforce.config.SalesforceSingletonConfig;
import org.dromara.salesforce.core.TargetSessionClient;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 目标会话客户端工厂类
 *
 * @author Kris
 */
@Slf4j
public class TargetSessionFactory {

    private static final Map<String, TargetSessionClient> CLIENT_CACHE = new ConcurrentHashMap<>();
    private static final ReentrantLock LOCK = new ReentrantLock();

    /**
     * 获取目标会话客户端实例
     * 如果缓存中没有有效的客户端，则根据SalesforceSingletonConfig中的配置信息进行登录
     *
     * @return TargetSessionClient 目标会话客户端实例
     * @throws RuntimeException 当登录失败或配置不完整时抛出异常
     */
    public static TargetSessionClient instance() {
        String configKey = CacheNames.TARGET_SESSION_CONFIG;

        TargetSessionClient client = CLIENT_CACHE.get(configKey);
        if (client == null || !client.isSessionValid()) {
            LOCK.lock();
            try {
                client = CLIENT_CACHE.get(configKey);
                if (client == null || !client.isSessionValid()) {
                    // 尝试根据配置登录
                    client = loginFromConfig();
                    CLIENT_CACHE.put(configKey, client);
                    log.info("创建并缓存目标会话客户端实例");
                }
            } finally {
                LOCK.unlock();
            }
        }
        return client;
    }

    /**
     * 根据SalesforceSingletonConfig中的配置信息登录到目标ORG
     *
     * @return TargetSessionClient 已登录的目标会话客户端
     * @throws RuntimeException 当登录失败或配置不完整时抛出异常
     */
    private static TargetSessionClient loginFromConfig() {
        log.info("Attempting to login to target org using config values");

        // 从配置中获取目标ORG登录信息
        SalesforceSingletonConfig config = SalesforceSingletonConfig.getInstance();
        String username = config.getTargetSfdcUsername();
        String password = config.getTargetSfdcPassword();
        String serverUrl = config.getTargetSfdcEndpointProduction();

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(serverUrl)) {
            throw new RuntimeException("目标ORG登录配置不完整");
        }

        try {
            // 创建连接配置
            ConnectorConfig connectorConfig = new ConnectorConfig();
            connectorConfig.setAuthEndpoint(serverUrl + "/services/Soap/u/59.0");
            connectorConfig.setServiceEndpoint(serverUrl + "/services/Soap/u/59.0");

            // 设置代理配置（如果存在）
            String proxyHost = config.getTargetSfdcProxyHost();
            String proxyPort = config.getTargetSfdcProxyPort();
            if (StringUtils.isNotEmpty(proxyHost) && StringUtils.isNotEmpty(proxyPort)) {
                connectorConfig.setProxy(proxyHost, Integer.parseInt(proxyPort));
                String proxyUsername = config.getTargetSfdcProxyUsername();
                String proxyPassword = config.getTargetSfdcProxyPassword();
                if (StringUtils.isNotEmpty(proxyUsername) && StringUtils.isNotEmpty(proxyPassword)) {
                    connectorConfig.setProxyUsername(proxyUsername);
                    connectorConfig.setProxyPassword(proxyPassword);
                }
            }

            // 执行登录操作
            PartnerConnection connection = new PartnerConnection(connectorConfig);
            LoginResult loginResult = connection.login(username, password);

            // 初始化目标会话客户端
            TargetSessionClient sessionClient = new TargetSessionClient();
            sessionClient.setSessionId(loginResult.getSessionId());
            sessionClient.setServerUrl(loginResult.getServerUrl());
            sessionClient.setUserInfo(loginResult.getUserInfo());
            sessionClient.setLastActivityTime(Calendar.getInstance().getTimeInMillis());

            log.info("Successfully logged in to target org with session ID: {}", loginResult.getSessionId());
            return sessionClient;
        } catch (ConnectionException e) {
            log.error("Failed to login to target org", e);
            throw new RuntimeException("目标ORG登录失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during target org login", e);
            throw new RuntimeException("目标ORG登录过程中发生未知错误: " + e.getMessage(), e);
        }
    }
    
    /**
     * 清除目标会话信息
     * 从缓存中移除目标会话客户端实例
     */
    public static void clearSession() {
        String configKey = CacheNames.TARGET_SESSION_CONFIG;
        TargetSessionClient client = CLIENT_CACHE.remove(configKey);
        if (client != null) {
            log.info("目标会话信息已清除");
        } else {
            log.info("没有找到目标会话信息");
        }
    }
}