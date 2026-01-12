package com.datai.integration.core;

import com.salesforce.eventbus.EventBusClient;
import com.salesforce.eventbus.EventBusClientFactory;
import com.salesforce.eventbus.Subscription;
import com.salesforce.eventbus.SubscriptionListener;
import com.salesforce.eventbus.protobuf.EventBatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * Salesforce Pub/Sub API 客户端
 * 用于管理与 Salesforce Pub/Sub API 的连接和订阅
 */
@Slf4j
@Component
public class PubSubClient {

    private EventBusClient eventBusClient;
    private Subscription subscription;
    private boolean connected = false;

    @Value("${salesforce.pubsub.endpoint:https://api.salesforce.com/eventbus/v1}")
    private String pubSubEndpoint;

    @Value("${salesforce.pubsub.replayId:-1}")
    private long replayId;

    @Value("${salesforce.pubsub.timeout:30}")
    private int timeout;

    /**
     * 初始化 Pub/Sub API 客户端
     * @param accessToken Salesforce 访问令牌
     * @param instanceUrl Salesforce 实例 URL
     * @return 是否初始化成功
     */
    public boolean initialize(String accessToken, String instanceUrl) {
        try {
            log.info("开始初始化 Salesforce Pub/Sub API 客户端");

            // 创建 EventBusClientFactory
            EventBusClientFactory factory = EventBusClientFactory.builder()
                    .withAuthProvider(() -> accessToken)
                    .withEndpoint(pubSubEndpoint)
                    .withInstanceUrl(instanceUrl)
                    .build();

            // 创建 EventBusClient
            eventBusClient = factory.createClient();

            // 连接到 Salesforce Event Bus
            CountDownLatch latch = new CountDownLatch(1);
            eventBusClient.connect(connection -> {
                if (connection.isSuccess()) {
                    log.info("成功连接到 Salesforce Event Bus");
                    connected = true;
                } else {
                    log.error("连接到 Salesforce Event Bus 失败: {}", connection.getError());
                    connected = false;
                }
                latch.countDown();
            });

            // 等待连接完成
            if (!latch.await(timeout, TimeUnit.SECONDS)) {
                log.error("连接到 Salesforce Event Bus 超时");
                return false;
            }

            return connected;
        } catch (Exception e) {
            log.error("初始化 Salesforce Pub/Sub API 客户端失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 订阅事件通道
     * @param topic 事件通道名称
     * @param listener 事件监听器
     * @return 是否订阅成功
     */
    public boolean subscribe(String topic, SubscriptionListener listener) {
        try {
            if (!connected || eventBusClient == null) {
                log.error("Pub/Sub API 客户端未连接，无法订阅事件通道");
                return false;
            }

            log.info("开始订阅事件通道: {}", topic);

            // 创建订阅
            subscription = eventBusClient.subscribe(topic, replayId, listener);

            log.info("成功订阅事件通道: {}", topic);
            return true;
        } catch (Exception e) {
            log.error("订阅事件通道 {} 失败: {}", topic, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 取消订阅
     */
    public void unsubscribe() {
        try {
            if (subscription != null) {
                log.info("开始取消订阅事件通道");
                subscription.cancel();
                log.info("成功取消订阅事件通道");
            }
        } catch (Exception e) {
            log.error("取消订阅事件通道失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        try {
            if (eventBusClient != null) {
                log.info("开始断开与 Salesforce Event Bus 的连接");
                eventBusClient.disconnect();
                connected = false;
                log.info("成功断开与 Salesforce Event Bus 的连接");
            }
        } catch (Exception e) {
            log.error("断开与 Salesforce Event Bus 的连接失败: {}", e.getMessage(), e);
        }
    }

    /**
     * 检查客户端是否已连接
     * @return 是否已连接
     */
    public boolean isConnected() {
        return connected;
    }

    /**
     * 获取 EventBusClient
     * @return EventBusClient
     */
    public EventBusClient getEventBusClient() {
        return eventBusClient;
    }

    /**
     * 获取当前订阅
     * @return Subscription
     */
    public Subscription getSubscription() {
        return subscription;
    }
}
