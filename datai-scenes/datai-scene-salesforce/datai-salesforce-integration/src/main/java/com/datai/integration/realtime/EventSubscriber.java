package com.datai.integration.realtime;

/**
 * 事件订阅器接口
 * 用于订阅Salesforce Event Bus事件
 */
public interface EventSubscriber {
    
    /**
     * 启动事件订阅
     */
    void startSubscription();
    
    /**
     * 停止事件订阅
     */
    void stopSubscription();
    
    /**
     * 检查是否已订阅
     * @return 是否已订阅
     */
    boolean isSubscribed();
}
