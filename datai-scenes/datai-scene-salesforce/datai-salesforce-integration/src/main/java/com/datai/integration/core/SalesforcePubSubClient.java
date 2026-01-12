package com.datai.integration.core;

import com.salesforce.multicloudj.PubSubClient;
import com.salesforce.multicloudj.PubSubClientFactory;
import com.salesforce.multicloudj.Subscription;
import com.salesforce.multicloudj.SubscriptionListener;
import com.salesforce.multicloudj.protobuf.EventBatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Salesforce Pub/Sub API 客户端
 * 用于管理与 Salesforce Pub/Sub API 的连接和订阅
 */
@Slf4j
@Component
public class SalesforcePubSubClient {

    private PubSubClient pubSubClient;
    private Subscription subscription;
    private final AtomicBoolean connected = new AtomicBoolean(false);
    private ScheduledExecutorService executorService;
    private String accessToken;
    private String instanceUrl;

    @Value("${salesforce.pubsub.endpoint:https://api.salesforce.com/eventbus/v1}")
    private String pubSubEndpoint;

    @Value("${salesforce.pubsub.replayId:-1}")
    private long replayId;

    @Value("${salesforce.pubsub.timeout:30}")
    private int timeout;

    @Value("${salesforce.pubsub.reconnect.attempts:5}")
    private int reconnectAttempts;

    @Value("${salesforce.pubsub.reconnect.delay:5}")
    private int reconnectDelay;

    /**
     * 初始化 Pub/Sub API 客户端
     * @param accessToken Salesforce 访问令牌
     * @param instanceUrl Salesforce 实例 URL
     * @return 是否初始化成功
     */
    public boolean initialize(String accessToken, String instanceUrl) {
        try {
            log.info("开始初始化 Salesforce Pub/Sub API 客户端");
            this.accessToken = accessToken;
            this.instanceUrl = instanceUrl;

            // 创建 PubSubClientFactory
            PubSubClientFactory factory = PubSubClientFactory.builder()
                    .withAuthProvider(() -> accessToken)
                    .withEndpoint(pubSubEndpoint)
                    .withInstanceUrl(instanceUrl)
                    .build();

            // 创建 PubSubClient
            pubSubClient = factory.createClient();

            // 连接到 Salesforce Event Bus
            boolean connectionResult = connectWithRetry(reconnectAttempts);

            if (connectionResult) {
                // 启动连接监控
                startConnectionMonitor();
            }

            return connectionResult;
        } catch (Exception e) {
            log.error("初始化 Salesforce Pub/Sub API 客户端失败: {}", e.getMessage(), e);
            return false;
        }
    }

    /**
     * 带重试的连接方法
     * @param maxAttempts 最大重试次数
     * @return 是否连接成功
     */
    private boolean connectWithRetry(int maxAttempts) {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            log.info("连接到 Salesforce Event Bus (尝试 {} / {})", attempt, maxAttempts);

            CountDownLatch latch = new CountDownLatch(1);
            AtomicBoolean connectionSuccess = new AtomicBoolean(false);

            try {
                pubSubClient.connect(connection -> {
                    if (connection.isSuccess()) {
                        log.info("成功连接到 Salesforce Event Bus");
                        connected.set(true);
                        connectionSuccess.set(true);
                    } else {
                        log.error("连接到 Salesforce Event Bus 失败: {}", connection.getError());
                        connected.set(false);
                        connectionSuccess.set(false);
                    }
                    latch.countDown();
                });

                // 等待连接完成
                if (!latch.await(timeout, TimeUnit.SECONDS)) {
                    log.error("连接到 Salesforce Event Bus 超时");
                    if (attempt < maxAttempts) {
                        log.info("{}秒后重试连接", reconnectDelay);
                        TimeUnit.SECONDS.sleep(reconnectDelay);
                    }
                    continue;
                }

                if (connectionSuccess.get()) {
                    return true;
                } else if (attempt < maxAttempts) {
                    log.info("{}秒后重试连接", reconnectDelay);
                    TimeUnit.SECONDS.sleep(reconnectDelay);
                }
            } catch (Exception e) {
                log.error("连接到 Salesforce Event Bus 时发生异常: {}", e.getMessage(), e);
                if (attempt < maxAttempts) {
                    try {
                        log.info("{}秒后重试连接", reconnectDelay);
                        TimeUnit.SECONDS.sleep(reconnectDelay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        return false;
    }

    /**
     * 启动连接监控
     */
    private void startConnectionMonitor() {
        executorService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "pubsub-connection-monitor");
            t.setDaemon(true);
            return t;
        });

        // 每30秒检查一次连接状态
        executorService.scheduleAtFixedRate(() -> {
            try {
                if (!connected.get() && pubSubClient != null) {
                    log.warn("检测到 Pub/Sub API 连接已断开，尝试重连");
                    connectWithRetry(3);
                }
            } catch (Exception e) {
                log.error("连接监控时发生异常: {}", e.getMessage(), e);
            }
        }, 30, 30, TimeUnit.SECONDS);
    }

    /**
     * 订阅事件通道
     * @param topic 事件通道名称
     * @param listener 事件监听器
     * @return 是否订阅成功
     */
    public boolean subscribe(String topic, SubscriptionListener listener) {
        try {
            if (!connected.get() || pubSubClient == null) {
                log.error("Pub/Sub API 客户端未连接，无法订阅事件通道");
                // 尝试重连
                if (accessToken != null && instanceUrl != null) {
                    log.info("尝试重连后再订阅");
                    if (connectWithRetry(3)) {
                        log.info("重连成功，继续订阅");
                    } else {
                        return false;
                    }
                } else {
                    return false;
                }
            }

            log.info("开始订阅事件通道: {}", topic);

            // 创建订阅
            subscription = pubSubClient.subscribe(topic, replayId, listener);

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
                subscription = null;
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
            // 停止连接监控
            if (executorService != null) {
                executorService.shutdown();
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            }

            // 取消订阅
            unsubscribe();

            // 断开连接
            if (pubSubClient != null) {
                log.info("开始断开与 Salesforce Event Bus 的连接");
                pubSubClient.disconnect();
                connected.set(false);
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
        return connected.get();
    }

    /**
     * 获取 PubSubClient
     * @return PubSubClient
     */
    public PubSubClient getPubSubClient() {
        return pubSubClient;
    }

    /**
     * 获取当前订阅
     * @return Subscription
     */
    public Subscription getSubscription() {
        return subscription;
    }

    /**
     * 健康检查
     * @return 连接是否健康
     */
    public boolean healthCheck() {
        return connected.get() && pubSubClient != null;
    }

    /**
     * 刷新访问令牌
     * @param newAccessToken 新的访问令牌
     * @return 是否刷新成功
     */
    public boolean refreshToken(String newAccessToken) {
        try {
            this.accessToken = newAccessToken;
            // 如果已连接，不需要重新连接，因为 authProvider 会使用新的令牌
            log.info("成功刷新 Pub/Sub API 访问令牌");
            return true;
        } catch (Exception e) {
            log.error("刷新访问令牌失败: {}", e.getMessage(), e);
            return false;
        }
    }
}
