package com.datai.integration.realtime.impl;

import com.datai.integration.factory.impl.PubSubConnectionFactory;
import com.datai.integration.realtime.EventProcessor;
import com.datai.integration.realtime.EventSubscriber;
import com.salesforce.multicloudj.pubsub.client.SubscriptionClient;
import com.salesforce.multicloudj.pubsub.driver.Message;
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

    private static final String DEFAULT_TOPIC = "/event/ChangeEvents";
    private static final String MONITOR_THREAD_NAME = "pubsub-monitor";
    private static final String EVENT_RECEIVER_THREAD_NAME = "pubsub-event-receiver";
    
    private static final String ERROR_SUBSCRIPTION_ALREADY_STARTED = "Salesforce Pub/Sub API 订阅已经启动，跳过启动操作";
    private static final String ERROR_SUBSCRIPTION_ALREADY_STOPPED = "Salesforce Pub/Sub API 订阅未启动，跳过停止操作";
    private static final String ERROR_SUBSCRIPTION_DISCONNECTED = "Salesforce Pub/Sub API 订阅已断开，尝试重新连接";
    private static final String ERROR_TOPIC_EMPTY = "订阅主题不能为空";
    private static final String ERROR_EVENT_PROCESSING = "接收和处理事件时发生异常";
    private static final String ERROR_RECONNECT_FAILED = "重新连接 Salesforce Pub/Sub API 订阅时发生异常";
    private static final String ERROR_MONITOR_SHUTDOWN = "监控线程关闭超时，强制关闭";
    
    private static final long MONITOR_SHUTDOWN_TIMEOUT_SECONDS = 5;
    private static final long ERROR_SLEEP_SECONDS = 1;

    @Autowired
    private PubSubConnectionFactory connectionFactory;

    @Autowired
    private EventProcessor eventProcessor;

    private SubscriptionClient subscriptionClient;
    private final AtomicBoolean subscribed = new AtomicBoolean(false);
    private ScheduledExecutorService monitorExecutor;

    @Value("${salesforce.pubsub.topic:" + DEFAULT_TOPIC + "}")
    private String topic;

    @Value("${salesforce.pubsub.retry.delay:30}")
    private int retryDelay;

    @Override
    public void startSubscription() {
        if (subscribed.get()) {
            log.warn(ERROR_SUBSCRIPTION_ALREADY_STARTED);
            return;
        }

        validateTopic();
        log.info("启动 Salesforce Pub/Sub API 订阅: {}", topic);

        try {
            initializeSubscription();
            subscribed.set(true);
            log.info("Salesforce Pub/Sub API 订阅启动成功");
        } catch (Exception e) {
            log.error("启动 Salesforce Pub/Sub API 订阅时发生异常: {}", e.getMessage(), e);
            subscribed.set(false);
        }
    }

    private void validateTopic() {
        if (topic == null || topic.trim().isEmpty()) {
            log.error(ERROR_TOPIC_EMPTY);
            throw new IllegalStateException(ERROR_TOPIC_EMPTY);
        }
    }

    private void initializeSubscription() {
        subscriptionClient = connectionFactory.getSubscriptionClient(topic);
        monitorExecutor = Executors.newSingleThreadScheduledExecutor(r -> {
            Thread thread = new Thread(r, MONITOR_THREAD_NAME);
            thread.setDaemon(true);
            return thread;
        });
        monitorExecutor.execute(this::receiveEvents);
        monitorExecutor.scheduleWithFixedDelay(this::checkSubscriptionStatus, 0, retryDelay, TimeUnit.SECONDS);
    }

    /**
     * 接收并处理事件
     */
    private void receiveEvents() {
        Thread.currentThread().setName(EVENT_RECEIVER_THREAD_NAME);
        
        while (subscribed.get()) {
            try {
                if (subscriptionClient != null) {
                    Message message = subscriptionClient.receive();
                    if (message != null) {
                        processMessage(message);
                    }
                }
            } catch (Exception e) {
                log.error(ERROR_EVENT_PROCESSING + ": {}", e.getMessage(), e);
                sleepOnError();
            }
        }
    }

    private void processMessage(Message message) {
        String loggableId = message.getLoggableID();
        log.debug("接收到事件: {}", loggableId);
        
        try {
            eventProcessor.processMessage(message);
            subscriptionClient.sendAck(message.getAckID());
        } catch (Exception e) {
            log.error("处理事件失败 [LoggableID: {}]: {}", loggableId, e.getMessage(), e);
        }
    }

    private void sleepOnError() {
        try {
            TimeUnit.SECONDS.sleep(ERROR_SLEEP_SECONDS);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
        }
    }

    /**
     * 检查订阅状态
     */
    private void checkSubscriptionStatus() {
        if (!subscribed.get()) {
            log.warn(ERROR_SUBSCRIPTION_DISCONNECTED);
            attemptReconnect();
        }
    }

    private void attemptReconnect() {
        try {
            stopSubscription();
            startSubscription();
        } catch (Exception e) {
            log.error(ERROR_RECONNECT_FAILED + ": {}", e.getMessage(), e);
        }
    }

    @Override
    @PreDestroy
    public void stopSubscription() {
        if (!subscribed.get()) {
            log.warn(ERROR_SUBSCRIPTION_ALREADY_STOPPED);
            return;
        }

        log.info("停止 Salesforce Pub/Sub API 订阅: {}", topic);

        try {
            shutdownMonitorExecutor();
            closeSubscriptionClient();
            subscribed.set(false);
            subscriptionClient = null;
            log.info("Salesforce Pub/Sub API 订阅停止成功");
        } catch (Exception e) {
            log.error("停止 Salesforce Pub/Sub API 订阅时发生异常: {}", e.getMessage(), e);
        }
    }

    private void shutdownMonitorExecutor() throws InterruptedException {
        if (monitorExecutor != null) {
            monitorExecutor.shutdown();
            if (!monitorExecutor.awaitTermination(MONITOR_SHUTDOWN_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                log.warn(ERROR_MONITOR_SHUTDOWN);
                monitorExecutor.shutdownNow();
            }
        }
    }

    private void closeSubscriptionClient() {
        if (subscriptionClient != null) {
            try {
                subscriptionClient.close();
                connectionFactory.closeClient(topic);
            } catch (Exception e) {
                log.error("关闭订阅客户端时发生异常: {}", e.getMessage(), e);
            }
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
        return topic;
    }
}