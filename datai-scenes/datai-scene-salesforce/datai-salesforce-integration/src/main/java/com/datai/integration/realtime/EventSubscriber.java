package com.datai.integration.realtime;

import com.sforce.ws.ConnectionException;

/**
 * 事件订阅器接口
 * 用于订阅Salesforce Change Events，实时捕获数据变更
 */
public interface EventSubscriber {
    
    /**
     * 启动事件订阅
     * @throws ConnectionException 当获取Salesforce连接失败时抛出
     */
    void startSubscription() throws ConnectionException;
    
    /**
     * 停止事件订阅
     */
    void stopSubscription();
    
    /**
     * 检查订阅状态
     * @return 是否正在订阅
     */
    boolean isSubscribed();
}
