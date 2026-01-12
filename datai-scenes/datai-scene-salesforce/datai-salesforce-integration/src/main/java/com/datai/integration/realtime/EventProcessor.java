package com.datai.integration.realtime;

import com.salesforce.eventbus.protobuf.Event;

import java.util.List;

/**
 * 事件处理器接口
 * 用于解析和处理捕获的变更事件
 */
public interface EventProcessor {
    
    /**
     * 处理单个事件
     * @param event 事件对象
     */
    void processEvent(Event event);
    
    /**
     * 批量处理事件
     * @param events 事件列表
     */
    void processGrpcEvents(List<Event> events);
    
    /**
     * 处理Pub/Sub API事件批次
     * @param batch 事件批次
     */
    void processEventBatch(Object batch);
}
