package com.datai.integration.realtime;

import com.sforce.soap.partner.sobject.SObject;

/**
 * 事件处理器接口
 * 用于处理捕获到的Salesforce Change Events
 */
public interface EventProcessor {
    
    /**
     * 处理捕获到的事件
     * @param event Salesforce Change Event
     */
    void processEvent(SObject event);
}
