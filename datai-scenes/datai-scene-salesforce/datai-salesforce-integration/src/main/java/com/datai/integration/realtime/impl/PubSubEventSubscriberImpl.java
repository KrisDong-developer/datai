package com.datai.integration.realtime.impl;

import com.datai.integration.factory.impl.PubSubConnectionFactory;
import com.datai.integration.realtime.EventSubscriber;
import com.salesforce.eventbus.SubscriptionListener;
import com.salesforce.eventbus.protobuf.EventBatch;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

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

    private boolean subscribed = false;
    private String subscribedTopic;

    @Override
    public void startSubscription() {
        log.info("开始启动 Salesforce Pub/Sub API 订阅");

        try {
            // 获取 Pub/Sub API 客户端
            var pubSubClient = pubSubConnectionFactory.getConnection();
            if (pubSubClient == null) {
                log.error("无法获取 Pub/Sub API 客户端");
                return;
            }

            // 订阅 Change Events 通道
            String topic = "/event/ChangeEvents";
            boolean subscribed = pubSubClient.subscribe(topic, new SubscriptionListener() {
                @Override
                public void onSubscribe(String subscriptionId) {
                    log.info("成功订阅事件通道 {}，订阅ID: {}", topic, subscriptionId);
                }

                @Override
                public void onError(String error) {
                    log.error("订阅事件通道 {} 发生错误: {}", topic, error);
                }

                @Override
                public void onComplete() {
                    log.info("订阅事件通道 {} 完成", topic);
                }

                @Override
                public void onEventBatch(EventBatch eventBatch) {
                    log.info("接收到事件批次，包含 {} 个事件", eventBatch.getEventsCount());
                    
                    // 处理事件批次
                    eventProcessor.processEventBatch(eventBatch);
                }
            });

            if (subscribed) {
                this.subscribed = true;
                this.subscribedTopic = topic;
                log.info("Salesforce Pub/Sub API 订阅启动成功");
            } else {
                log.error("Salesforce Pub/Sub API 订阅启动失败");
            }
        } catch (Exception e) {
            log.error("启动 Salesforce Pub/Sub API 订阅时发生异常: {}", e.getMessage(), e);
        }
    }

    @Override
    public void stopSubscription() {
        log.info("开始停止 Salesforce Pub/Sub API 订阅");

        try {
            // 获取 Pub/Sub API 客户端
            var pubSubClient = pubSubConnectionFactory.getConnection();
            if (pubSubClient != null) {
                // 取消订阅
                pubSubClient.unsubscribe();
                log.info("成功取消订阅事件通道: {}", subscribedTopic);
            }

            subscribed = false;
            subscribedTopic = null;
            log.info("Salesforce Pub/Sub API 订阅停止成功");
        } catch (Exception e) {
            log.error("停止 Salesforce Pub/Sub API 订阅时发生异常: {}", e.getMessage(), e);
        }
    }

    @Override
    public boolean isSubscribed() {
        return subscribed;
    }
}
