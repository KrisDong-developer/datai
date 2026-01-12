package com.datai.integration.realtime.impl;

import com.datai.integration.factory.impl.PubSubConnectionFactory;
import com.datai.integration.realtime.EventSubscriber;
import com.salesforce.multicloudj.pubsub.client.SubscriptionClient;
import com.salesforce.multicloudj.pubsub.driver.Message;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Pub/Sub API 事件订阅器实现
 * 用于订阅 Salesforce Event Bus 事件并处理事件
 */
@Slf4j
@Component
public class PubSubEventSubscriberImpl implements EventSubscriber {

    @Autowired
    private PubSubConnectionFactory connectionFactory;

    @Autowired
    private EventProcessorImpl eventProcessor;

    private SubscriptionClient subscriptionClient;
    private final AtomicBoolean subscribed = new AtomicBoolean(false);
    private final String TOPIC = "/event/ChangeEvents"; // 监听所有变更事件
    private ScheduledExecutorService monitorExecutor;

    @Value("${salesforce.pubsub.retry.delay:30}")
    private int retryDelay;

    @Override
    @PostConstruct
    public void startSubscription() {
        if (subscribed.get()) {
            log.info("Salesforce Pub/Sub API 订阅已经启动，跳过启动操作");
            return;
        }

        log.info("启动 Salesforce Pub/Sub API 订阅: {}", TOPIC);

        try {
            // 获取订阅客户端
            subscriptionClient = connectionFactory.getSubscriptionClient(TOPIC);

            // 启动事件接收线程
            monitorExecutor = Executors.newSingleThreadScheduledExecutor();
            monitorExecutor.execute(this::receiveEvents);

            // 启动监控线程，处理断线重连
            monitorExecutor.scheduleWithFixedDelay(this::checkSubscriptionStatus, 0, retryDelay, TimeUnit.SECONDS);

            subscribed.set(true);
            log.info("Salesforce Pub/Sub API 订阅启动成功");
        } catch (Exception e) {
            log.error("启动 Salesforce Pub/Sub API 订阅时发生异常: {}", e.getMessage(), e);
            subscribed.set(false);
        }
    }

    /**
     * 接收并处理事件
     */
    private void receiveEvents() {
        while (subscribed.get()) {
            try {
                if (subscriptionClient != null) {
                    // 接收事件
                    Message message = subscriptionClient.receive();
                    if (message != null) {
                        log.debug("接收到事件: {}", message.getLoggableID());
                        
                        // 处理事件
                        eventProcessor.processMessage(message);

                        // 确认接收
                        subscriptionClient.sendAck(message.getAckID());
                    }
                }
            } catch (Exception e) {
                log.error("接收和处理事件时发生异常: {}", e.getMessage(), e);
                // 短暂休眠后继续
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    /**
     * 检查订阅状态
     */
    private void checkSubscriptionStatus() {
        if (!subscribed.get()) {
            log.warn("Salesforce Pub/Sub API 订阅已断开，尝试重新连接");
            try {
                // 重新启动订阅
                stopSubscription();
                startSubscription();
            } catch (Exception e) {
                log.error("重新连接 Salesforce Pub/Sub API 订阅时发生异常: {}", e.getMessage(), e);
            }
        }
    }

    @Override
    @PreDestroy
    public void stopSubscription() {
        if (!subscribed.get()) {
            log.info("Salesforce Pub/Sub API 订阅未启动，跳过停止操作");
            return;
        }

        log.info("停止 Salesforce Pub/Sub API 订阅: {}", TOPIC);

        try {
            // 停止监控线程
            if (monitorExecutor != null) {
                monitorExecutor.shutdown();
                if (!monitorExecutor.awaitTermination(5, TimeUnit.SECONDS)) {
                    monitorExecutor.shutdownNow();
                }
            }

            // 关闭订阅客户端
            if (subscriptionClient != null) {
                subscriptionClient.close();
                connectionFactory.closeClient(TOPIC);
            }

            subscribed.set(false);
            subscriptionClient = null;
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
        return TOPIC;
    }
}