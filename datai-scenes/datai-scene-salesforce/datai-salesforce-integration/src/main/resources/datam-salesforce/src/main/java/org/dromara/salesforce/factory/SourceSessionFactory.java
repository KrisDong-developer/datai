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
import org.dromara.salesforce.core.SourceSessionClient;

import java.util.Calendar;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 源会话客户端工厂类
 *
 * @author Kris
 */
@Slf4j
public class SourceSessionFactory {

    private static final Map<String, SourceSessionClient> CLIENT_CACHE = new ConcurrentHashMap<>();
    private static final ReentrantLock LOCK = new ReentrantLock();

    /**
     * 获取源会话客户端实例
     * 如果缓存中没有有效的客户端，则根据SalesforceSingletonConfig中的配置信息进行登录
     *
     * @return SourceSessionClient 源会话客户端实例
     * @throws RuntimeException 当登录失败或配置不完整时抛出异常
     */
    public static SourceSessionClient instance() {
        String configKey = CacheNames.SOURCE_SESSION_CONFIG;

        SourceSessionClient client = CLIENT_CACHE.get(configKey);
        if (client == null || !client.isSessionValid()) {
            LOCK.lock();
            try {
                client = CLIENT_CACHE.get(configKey);
                if (client == null || !client.isSessionValid()) {
                    // 尝试根据配置登录
                    client = loginFromConfig();
                    CLIENT_CACHE.put(configKey, client);
                    log.info("创建并缓存源会话客户端实例");
                }
            } finally {
                LOCK.unlock();
            }
        }
        return client;
    }

    /**
     * 根据SalesforceSingletonConfig中的配置信息登录到源ORG
     *
     * @return SourceSessionClient 已登录的源会话客户端
     * @throws RuntimeException 当登录失败或配置不完整时抛出异常
     */
    private static SourceSessionClient loginFromConfig() {
        log.info("Attempting to login to source org using config values");

        // 从配置中获取源ORG登录信息
        SalesforceSingletonConfig config = SalesforceSingletonConfig.getInstance();
        String username = config.getSfdcUsername();
        String password = config.getSfdcPassword();
        String serverUrl = config.getSfdcEndpointProduction();
        String apiVersion = config.getSalesforceApiVersion();

        if (StringUtils.isEmpty(username) || StringUtils.isEmpty(password) || StringUtils.isEmpty(serverUrl)) {
            throw new RuntimeException("源ORG登录配置不完整");
        }

        try {
            // 创建连接配置
            ConnectorConfig connectorConfig = new ConnectorConfig();
            connectorConfig.setAuthEndpoint(serverUrl + "/services/Soap/u/" + apiVersion);
            connectorConfig.setServiceEndpoint(serverUrl + "/services/Soap/u/" + apiVersion);

            // 设置代理配置（如果存在）
            String proxyHost = config.getSourceProxyHost();
            String proxyPort = config.getSourceProxyPort();
            if (StringUtils.isNotEmpty(proxyHost) && StringUtils.isNotEmpty(proxyPort)) {
                connectorConfig.setProxy(proxyHost, Integer.parseInt(proxyPort));
                String proxyUsername = config.getSourceProxyUsername();
                String proxyPassword = config.getSourceProxyPassword();
                if (StringUtils.isNotEmpty(proxyUsername) && StringUtils.isNotEmpty(proxyPassword)) {
                    connectorConfig.setProxyUsername(proxyUsername);
                    connectorConfig.setProxyPassword(proxyPassword);
                }
            }

            // 执行登录操作
            PartnerConnection connection = new PartnerConnection(connectorConfig);
            LoginResult loginResult = connection.login(username, password);

            // 初始化源会话客户端
            SourceSessionClient sessionClient = new SourceSessionClient();
            sessionClient.setSessionId(loginResult.getSessionId());
            sessionClient.setServerUrl(loginResult.getServerUrl());
            sessionClient.setUserInfo(loginResult.getUserInfo());
            sessionClient.setLastActivityTime(Calendar.getInstance().getTimeInMillis());

            log.info("Successfully logged in to source org with session ID: {}", loginResult.getSessionId());
            return sessionClient;
        } catch (ConnectionException e) {
            log.error("Failed to login to source org", e);
            throw new RuntimeException("源ORG登录失败: " + e.getMessage(), e);
        } catch (Exception e) {
            log.error("Unexpected error during source org login", e);
            throw new RuntimeException("源ORG登录过程中发生未知错误: " + e.getMessage(), e);
        }
    }

    /**
     * 清除源会话信息
     * 从缓存中移除源会话客户端实例
     */
    public static void clearSession() {
        String configKey = CacheNames.SOURCE_SESSION_CONFIG;
        SourceSessionClient client = CLIENT_CACHE.remove(configKey);
        if (client != null) {
            log.info("源会话信息已清除");
        } else {
            log.info("没有找到源会话信息");
        }
    }
}
