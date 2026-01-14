package com.datai.integration.factory.impl;

import com.datai.common.utils.CacheUtils;
import com.datai.common.utils.StringUtils;
import com.datai.integration.core.SessionManager;
import com.datai.setting.config.SalesforceConfigCacheManager;
import com.salesforce.multicloudj.pubsub.client.SubscriptionClient;
import com.salesforce.multicloudj.sts.model.CredentialsOverrider;
import com.salesforce.multicloudj.sts.model.CredentialsType;
import com.salesforce.multicloudj.sts.model.StsCredentials;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * Pub/Sub API 连接工厂
 * 负责管理与 Salesforce Pub/Sub API 的连接
 */
@Slf4j
@Component
public class PubSubConnectionFactory {

    private static final String SUBSCRIPTION_NAME_PREFIX = "datai-sf-sub-";
    private static final String SESSION_NAME = "datai-sf-pubsub-session";
    private static final String TOPIC_SEPARATOR = "/";

    private static final String ERROR_TOPIC_EMPTY = "订阅主题不能为空";
    private static final String ERROR_NO_ACCESS_TOKEN = "无法获取有效的访问令牌";
    private static final String ERROR_NO_INSTANCE_URL = "无法获取实例 URL";
    private static final String ERROR_ENDPOINT_RESOLUTION = "解析端点 URI 失败";
    private static final String ERROR_CLIENT_CREATION = "Salesforce Pub/Sub Client 实例化异常";
    private static final String ERROR_TOPIC_INVALID = "订阅主题格式无效";

    @Autowired
    private SessionManager sessionManager;

    @Autowired
    private SalesforceConfigCacheManager salesforceConfigCacheManager;

    private final ConcurrentMap<String, SubscriptionClient> subscriptionClients = new ConcurrentHashMap<>();

    @Value("${salesforce.pubsub.provider.id:salesforce}")
    private String providerId;

    @Value("${salesforce.pubsub.endpoint:}")
    private String pubSubEndpoint;

    @Value("${salesforce.pubsub.region:us-east-1}")
    private String region;

    /**
     * 获取或创建订阅客户端
     * @param topic 订阅主题
     * @param orgType ORG 类型（source 或 target）
     * @return 订阅客户端
     */
    public synchronized SubscriptionClient getSubscriptionClient(String topic, String orgType) {
        String cacheKey = buildCacheKey(topic, orgType);
        return subscriptionClients.computeIfAbsent(cacheKey, key -> createSubscriptionClient(topic, orgType));
    }

    /**
     * 获取或创建订阅客户端（默认使用源 ORG）
     * @param topic 订阅主题
     * @return 订阅客户端
     */
    public synchronized SubscriptionClient getSubscriptionClient(String topic) {
        return getSubscriptionClient(topic, "source");
    }

    /**
     * 创建订阅客户端
     * @param topic 订阅主题
     * @param orgType ORG 类型（source 或 target）
     * @return 订阅客户端
     */
    private SubscriptionClient createSubscriptionClient(String topic, String orgType) {
        log.info("开始构建 Salesforce Pub/Sub 客户端 [Topic: {}, OrgType: {}]", topic, orgType);

        try {
            validateTopic(topic);
            
            String accessToken = sessionManager.getCurrentSession(orgType);
            String instanceUrl = sessionManager.getInstanceUrl(orgType);
            
            validateCredentials(accessToken, instanceUrl);

            log.debug("获取到访问令牌和实例 URL [InstanceUrl: {}, OrgType: {}]", instanceUrl, orgType);

            URI endpointUri = resolveEndpoint(instanceUrl);
            log.debug("解析端点 URI: {}", endpointUri);

            StsCredentials stsCredentials = createStsCredentials(accessToken);
            log.debug("创建 StsCredentials 成功");

            CredentialsOverrider credentialsOverrider = createCredentialsOverrider(stsCredentials);
            log.debug("创建 CredentialsOverrider 成功");

            String subscriptionName = buildSubscriptionName(topic, orgType);
            log.debug("订阅名称: {}", subscriptionName);

            SubscriptionClient client = buildSubscriptionClient(subscriptionName, endpointUri, credentialsOverrider);

            log.info("成功构建 Salesforce Pub/Sub 客户端 [Topic: {}, OrgType: {}, SubscriptionName: {}]", topic, orgType, subscriptionName);
            return client;

        } catch (IllegalArgumentException | IllegalStateException e) {
            log.error("创建 Pub/Sub 订阅客户端失败 [Topic: {}, OrgType: {}]: {}", topic, orgType, e.getMessage());
            throw e;
        } catch (URISyntaxException e) {
            log.error("解析端点 URI 失败 [Topic: {}, OrgType: {}, InstanceUrl: {}]: {}", topic, orgType, sessionManager.getInstanceUrl(orgType), e.getMessage());
            throw new RuntimeException(ERROR_ENDPOINT_RESOLUTION, e);
        } catch (Exception e) {
            log.error("创建 Pub/Sub 订阅客户端失败 [Topic: {}, OrgType: {}]: {}", topic, orgType, e.getMessage(), e);
            throw new RuntimeException(ERROR_CLIENT_CREATION, e);
        }
    }

    private void validateTopic(String topic) {
        if (topic == null || topic.trim().isEmpty()) {
            log.error(ERROR_TOPIC_EMPTY);
            throw new IllegalArgumentException(ERROR_TOPIC_EMPTY);
        }
        
        String trimmedTopic = topic.trim();
        if (trimmedTopic.length() > 255) {
            log.error(ERROR_TOPIC_INVALID);
            throw new IllegalArgumentException(ERROR_TOPIC_INVALID);
        }
        
        if (!trimmedTopic.matches("^[a-zA-Z0-9/_\\-]+$")) {
            log.error(ERROR_TOPIC_INVALID);
            throw new IllegalArgumentException(ERROR_TOPIC_INVALID);
        }
    }

    private void validateCredentials(String accessToken, String instanceUrl) {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            log.error(ERROR_NO_ACCESS_TOKEN);
            throw new IllegalStateException(ERROR_NO_ACCESS_TOKEN);
        }

        if (instanceUrl == null || instanceUrl.trim().isEmpty()) {
            log.error(ERROR_NO_INSTANCE_URL);
            throw new IllegalStateException(ERROR_NO_INSTANCE_URL);
        }
    }

    private StsCredentials createStsCredentials(String accessToken) {
        if (accessToken == null || accessToken.trim().isEmpty()) {
            log.error(ERROR_NO_ACCESS_TOKEN);
            throw new IllegalStateException(ERROR_NO_ACCESS_TOKEN);
        }
        String cacheKey = salesforceConfigCacheManager.getEnvironmentCacheKey();
        String consumerKey = CacheUtils.get(cacheKey, "salesforce.pubsub.consumer.key", String.class);
        String consumerSecret = CacheUtils.get(cacheKey, "salesforce.pubsub.consumer.secret", String.class);
        return new StsCredentials(consumerKey, consumerSecret, accessToken.trim());
    }

    private CredentialsOverrider createCredentialsOverrider(StsCredentials stsCredentials) {
        if (stsCredentials == null) {
            log.error("StsCredentials 不能为空");
            throw new IllegalStateException("StsCredentials 不能为空");
        }
        return new CredentialsOverrider.Builder(CredentialsType.SESSION)
                .withSessionCredentials(stsCredentials)
                .withSessionName(SESSION_NAME)
                .build();
    }

    private String buildSubscriptionName(String topic, String orgType) {
        return SUBSCRIPTION_NAME_PREFIX + orgType.toUpperCase() + "_" + topic.replace(TOPIC_SEPARATOR, "_");
    }

    private String buildCacheKey(String topic, String orgType) {
        return orgType + ":" + topic;
    }

    private SubscriptionClient buildSubscriptionClient(String subscriptionName, URI endpointUri, CredentialsOverrider credentialsOverrider) {
        return SubscriptionClient.builder(providerId)
                .withSubscriptionName(subscriptionName)
                .withRegion(region)
                .withEndpoint(endpointUri)
                .withCredentialsOverrider(credentialsOverrider)
                .build();
    }

    /**
     * 健壮的端点解析逻辑
     */
    private URI resolveEndpoint(String instanceUrl) throws URISyntaxException {
        if (StringUtils.hasText(pubSubEndpoint)) {
            log.info("使用配置的 Pub/Sub 端点: {}", pubSubEndpoint);
            return new URI(pubSubEndpoint);
        }

        log.debug("从实例 URL 自动推导 Pub/Sub 端点: {}", instanceUrl);
        URI uri = new URI(instanceUrl);
        
        String scheme = uri.getScheme();
        String host = uri.getHost();
        
        if (scheme == null || scheme.isEmpty()) {
            log.error("实例 URL 缺少协议方案: {}", instanceUrl);
            throw new URISyntaxException(instanceUrl, "缺少协议方案");
        }
        
        if (host == null || host.isEmpty()) {
            log.error("实例 URL 缺少主机名: {}", instanceUrl);
            throw new URISyntaxException(instanceUrl, "缺少主机名");
        }
        
        String baseUri = scheme + "://" + host;
        log.debug("推导的 Pub/Sub 端点: {}", baseUri);
        return new URI(baseUri);
    }
    /**
     * 关闭所有订阅客户端
     */
    public void closeAllClients() {
        subscriptionClients.forEach((cacheKey, client) -> {
            try {
                client.close();
                log.info("关闭 Salesforce Pub/Sub API 订阅客户端，缓存键: {}", cacheKey);
            } catch (Exception e) {
                log.error("关闭 Salesforce Pub/Sub API 订阅客户端时发生异常 [缓存键: {}]: {}", cacheKey, e.getMessage(), e);
            }
        });
        subscriptionClients.clear();
    }

    /**
     * 关闭指定主题的订阅客户端（默认使用源 ORG）
     * @param topic 订阅主题
     */
    public void closeClient(String topic) {
        closeClient(topic, "source");
    }

    /**
     * 关闭指定主题和 ORG 类型的订阅客户端
     * @param topic 订阅主题
     * @param orgType ORG 类型（source 或 target）
     */
    public void closeClient(String topic, String orgType) {
        String cacheKey = buildCacheKey(topic, orgType);
        SubscriptionClient client = subscriptionClients.remove(cacheKey);
        if (client != null) {
            try {
                client.close();
                log.info("关闭 Salesforce Pub/Sub API 订阅客户端，主题: {}, ORG 类型: {}", topic, orgType);
            } catch (Exception e) {
                log.error("关闭 Salesforce Pub/Sub API 订阅客户端时发生异常 [主题: {}, ORG 类型: {}]: {}", topic, orgType, e.getMessage(), e);
            }
        }
    }
}