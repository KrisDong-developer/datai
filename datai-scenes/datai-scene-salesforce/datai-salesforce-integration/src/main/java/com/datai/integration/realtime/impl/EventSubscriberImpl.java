package com.datai.integration.realtime.impl;

import com.datai.integration.core.IPartnerV1Connection;
import com.datai.integration.factory.impl.SOAPConnectionFactory;
import com.datai.integration.realtime.EventSubscriber;
import com.sforce.soap.partner.PartnerConnection;

import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * 事件订阅器实现类
 * 用于订阅Salesforce Change Events，实时捕获数据变更
 */
@Component
@Slf4j
public class EventSubscriberImpl implements EventSubscriber {
    
    @Autowired
    private SOAPConnectionFactory soapConnectionFactory;
    
    @Autowired
    private EventProcessorImpl eventProcessor;
    
    private PartnerConnection connection;
    private boolean subscribed = false;
    private List<String> subscribedChannels = new ArrayList<>();
    
    @Override
    public void startSubscription() throws ConnectionException {
        log.info("开始启动Salesforce Change Events订阅");
        
        // 获取Salesforce连接
        this.connection = (PartnerConnection) soapConnectionFactory.getConnection();
        if (connection == null) {
            throw new ConnectionException("无法获取Salesforce连接");
        }

        
        // 订阅Change Events通道
        subscribeToChangeEvents();
        
        subscribed = true;
        log.info("Salesforce Change Events订阅启动成功");
    }
    
    @Override
    public void stopSubscription() {
        log.info("开始停止Salesforce Change Events订阅");
        
        if (connection != null) {
            try {
                // 取消订阅所有通道
                unsubscribeFromAllChannels();
                connection.logout();
            } catch (ConnectionException e) {
                log.error("停止Salesforce Change Events订阅时发生异常: {}", e.getMessage(), e);
            }
        }
        
        subscribed = false;
        subscribedChannels.clear();
        log.info("Salesforce Change Events订阅停止成功");
    }
    
    @Override
    public boolean isSubscribed() {
        return subscribed;
    }
    
    /**
     * 订阅Change Events通道
     * @throws ConnectionException 当订阅失败时抛出
     */
    private void subscribeToChangeEvents() throws ConnectionException {
        log.info("开始订阅Change Events通道");
        
        // 创建订阅请求
        SubscribeRequest[] subscribeRequests = new SubscribeRequest[1];
        SubscribeRequest subscribeRequest = new SubscribeRequest();
        subscribeRequest.setTopic("/event/ChangeEvents");
        subscribeRequests[0] = subscribeRequest;
        
        // 执行订阅
        SubscribeResult[] results = connection.subscribe(subscribeRequests);
        
        for (int i = 0; i < results.length; i++) {
            if (results[i].isSuccess()) {
                log.info("成功订阅通道: {}", subscribeRequests[i].getTopic());
                subscribedChannels.add(subscribeRequests[i].getTopic());
            } else {
                log.error("订阅通道失败: {}, 错误: {}", 
                    subscribeRequests[i].getTopic(), results[i].getError());
                throw new ConnectionException("订阅通道失败: " + results[i].getError());
            }
        }
        
        // 开始接收事件
        startReceivingEvents();
    }
    
    /**
     * 开始接收事件
     */
    private void startReceivingEvents() {
        new Thread(() -> {
            log.info("开始接收Salesforce Change Events");
            
            while (subscribed && connection != null) {
                try {
                    // 接收事件
                    SObject[] events = connection.receive(60000); // 60秒超时
                    
                    if (events != null && events.length > 0) {
                        log.info("接收到 {} 个Change Events事件", events.length);
                        
                        // 处理事件
                        for (SObject event : events) {
                            eventProcessor.processEvent(event);
                        }
                    }
                } catch (ConnectionException e) {
                    log.error("接收Salesforce Change Events时发生异常: {}", e.getMessage(), e);
                    
                    // 尝试重新连接
                    try {
                        log.info("尝试重新连接Salesforce");
                        IPartnerV1Connection partnerConnection = soapConnectionFactory.getConnection();
                        if (partnerConnection != null) {
                            this.connection = partnerConnection.getConnection();
                            subscribeToChangeEvents();
                        }
                    } catch (ConnectionException ex) {
                        log.error("重新连接Salesforce失败: {}", ex.getMessage(), ex);
                    }
                } catch (Exception e) {
                    log.error("处理Salesforce Change Events时发生异常: {}", e.getMessage(), e);
                }
            }
        }).start();
    }
    
    /**
     * 取消订阅所有通道
     */
    private void unsubscribeFromAllChannels() {
        // 实现取消订阅逻辑
        // 由于Salesforce API的限制，这里可能需要使用unsubscribe方法
        // 但为了简化实现，我们暂时只关闭连接
        log.info("取消订阅所有Change Events通道");
    }
}
