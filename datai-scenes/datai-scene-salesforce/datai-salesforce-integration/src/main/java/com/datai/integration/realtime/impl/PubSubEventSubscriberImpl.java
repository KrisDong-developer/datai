package com.datai.integration.realtime.impl;

import com.datai.integration.factory.impl.PubSubConnectionFactory;
import com.datai.integration.realtime.EventSubscriber;
import com.salesforce.multicloudj.SubscriptionListener;
import com.salesforce.multicloudj.protobuf.EventBatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Pub/Sub API 事件订阅器实现类
 * 用于订阅 Salesforce Event Bus 事件，实时捕获数据变更
 */
@Slf4j
@Component
public class PubSubEventSubscriberImpl implements EventSubscriber {

    @Autowired
    private PubSubConnectionFactory pubSubConnectionFactory;

    @Autowired
    private EventProcessorImpl eventProcessor;

    private final AtomicBoolean subscribed = new AtomicBoolean(false);
    private String subscribedTopic;
    private ScheduledExecutorService executorService;

    @Value("${salesforce.pubsub.subscription.retry.attempts:3}")
    private int subscriptionRetryAttempts;

    @Value("${salesforce.pubsub.subscription.retry.delay:10}")
    private int subscriptionRetryDelay;

    @Override
    public void startSubscription() {
        log.info("开始启动 Salesforce Pub/Sub API 订阅");

        try {
            // 启动订阅（带重试）
            boolean subscriptionResult = startSubscriptionWithRetry(subscriptionRetryAttempts);

            if (subscriptionResult) {
                // 启动订阅监控
                startSubscriptionMonitor();
            }
        } catch (Exception e) {
            log.error("启动 Salesforce Pub/Sub API 订阅时发生异常: {}", e.getMessage(), e);
        }
    }

    /**
     * 带重试的订阅启动方法
     * @param maxAttempts 最大重试次数
     * @return 是否订阅成功
     */
    private boolean startSubscriptionWithRetry(int maxAttempts) {
        for (int attempt = 1; attempt <= maxAttempts; attempt++) {
            log.info("启动 Salesforce Pub/Sub API 订阅 (尝试 {} / {})", attempt, maxAttempts);

            try {
                // 获取 Pub/Sub API 客户端
                var pubSubClient = pubSubConnectionFactory.getConnection();
                if (pubSubClient == null) {
                    log.error("无法获取 Pub/Sub API 客户端");
                    if (attempt < maxAttempts) {
                        log.info("{}秒后重试", subscriptionRetryDelay);
                        TimeUnit.SECONDS.sleep(subscriptionRetryDelay);
                    }
                    continue;
                }

                // 订阅 Change Events 通道
                String topic = "/event/ChangeEvents";
                boolean subscriptionResult = pubSubClient.subscribe(topic, new SubscriptionListener() {
                    @Override
                    public void onSubscribe(String subscriptionId) {
                        log.info("成功订阅事件通道 {}，订阅ID: {}", topic, subscriptionId);
                        subscribed.set(true);
                        subscribedTopic = topic;
                    }

                    @Override
                    public void onError(String error) {
                        log.error("订阅事件通道 {} 发生错误: {}", topic, error);
                        subscribed.set(false);
                        // 错误发生时尝试重新订阅
                        handleSubscriptionError();
                    }

                    @Override
                    public void onComplete() {
                        log.info("订阅事件通道 {} 完成", topic);
                        subscribed.set(false);
                        // 订阅完成时尝试重新订阅
                        handleSubscriptionComplete();
                    }

                    @Override
                    public void onEventBatch(EventBatch eventBatch) {
                        try {
                            log.info("接收到事件批次，包含 {} 个事件", eventBatch.getEventsCount());
                            
                            // 处理事件批次
                            eventProcessor.processEventBatch(eventBatch);
                        } catch (Exception e) {
                            log.error("处理事件批次时发生异常: {}", e.getMessage(), e);
                        }
                    }
                });

                if (subscriptionResult) {
                    log.info("Salesforce Pub/Sub API 订阅启动成功");
                    return true;
                } else {
                    log.error("Salesforce Pub/Sub API 订阅启动失败");
                    if (attempt < maxAttempts) {
                        log.info("{}秒后重试", subscriptionRetryDelay);
                        TimeUnit.SECONDS.sleep(subscriptionRetryDelay);
                    }
                }
            } catch (Exception e) {
                log.error("启动 Salesforce Pub/Sub API 订阅时发生异常: {}", e.getMessage(), e);
                if (attempt < maxAttempts) {
                    try {
                        log.info("{}秒后重试", subscriptionRetryDelay);
                        TimeUnit.SECONDS.sleep(subscriptionRetryDelay);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            }
        }
        return false;
    }

    /**
     * 处理订阅错误
     */
    private void handleSubscriptionError() {
        log.info("处理订阅错误，尝试重新订阅");
        executorService.schedule(() -> {
            if (!subscribed.get()) {
                log.info("重新启动 Salesforce Pub/Sub API 订阅");
                startSubscriptionWithRetry(3);
            }
        }, 5, TimeUnit.SECONDS);
    }

    /**
     * 处理订阅完成
     */
    private void handleSubscriptionComplete() {
        log.info("处理订阅完成，尝试重新订阅");
        executorService.schedule(() -> {
            if (!subscribed.get()) {
                log.info("重新启动 Salesforce Pub/Sub API 订阅");
                startSubscriptionWithRetry(3);
            }
        }, 5, TimeUnit.SECONDS);
    }

    /**
     * 启动订阅监控
     */
    private void startSubscriptionMonitor() {
        executorService = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread t = new Thread(r, "pubsub-subscription-monitor");
            t.setDaemon(true);
            return t;
        });

        // 每60秒检查一次订阅状态
        executorService.scheduleAtFixedRate(() -> {
            try {
                if (!subscribed.get()) {
                    log.warn("检测到 Pub/Sub API 订阅已断开，尝试重新订阅");
                    startSubscriptionWithRetry(3);
                }
            } catch (Exception e) {
                log.error("订阅监控时发生异常: {}", e.getMessage(), e);
            }
        }, 60, 60, TimeUnit.SECONDS);
    }

    @Override
    public void stopSubscription() {
        log.info("开始停止 Salesforce Pub/Sub API 订阅");

        try {
            // 停止订阅监控
            if (executorService != null) {
                executorService.shutdown();
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            }

            // 获取 Pub/Sub API 客户端
            var pubSubClient = pubSubConnectionFactory.getConnection();
            if (pubSubClient != null) {
                // 取消订阅
                pubSubClient.unsubscribe();
                log.info("成功取消订阅事件通道: {}", subscribedTopic);
            }

            subscribed.set(false);
            subscribedTopic = null;
            log.info("Salesforce Pub/Sub API 订阅停止成功");
        } catch (Exception e) {
            log.error("停止 Salesforce Pub/Sub API 订阅时发生异常: {}", e.getMessage(), e);
        }
    }

    @Override
    public boolean isSubscribed() {
        return subscribed.get();
    }

    /**
     * 获取当前订阅的主题
     * @return 订阅主题
     */
    public String getSubscribedTopic() {
        return subscribedTopic;
    }
}
