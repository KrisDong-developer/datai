package com.datai.integration.factory.impl;

import com.datai.integration.core.PubSubClient;
import com.datai.integration.factory.AbstractConnectionFactory;
import com.datai.salesforce.auth.service.ISalesforceAuthService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Salesforce Pub/Sub API 连接工厂
 * 用于创建和管理 PubSubClient 实例
 */
@Slf4j
@Component
public class PubSubConnectionFactory extends AbstractConnectionFactory<PubSubClient> {

    @Autowired
    private ISalesforceAuthService salesforceAuthService;

    @Autowired
    private PubSubClient pubSubClient;

    /**
     * 获取 Salesforce Pub/Sub API 客户端
     * @return PubSubClient 实例
     */
    @Override
    public PubSubClient getConnection() {
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
     * 关闭 Salesforce Pub/Sub API 连接
     * @param connection PubSubClient 实例
     */
    @Override
    public void closeConnection(PubSubClient connection) {
        try {
            if (connection != null) {
                connection.disconnect();
                log.info("成功关闭 Pub/Sub API 连接");
            }
        } catch (Exception e) {
            log.error("关闭 Pub/Sub API 连接失败: {}", e.getMessage(), e);
        }
    }
}
