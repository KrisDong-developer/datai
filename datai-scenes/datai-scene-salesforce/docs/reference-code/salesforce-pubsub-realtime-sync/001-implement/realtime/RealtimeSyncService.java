package com.datai.integration.realtime;

import com.datai.integration.realtime.impl.ObjectRegistryImpl;
import com.datai.integration.realtime.impl.PubSubEventSubscriberImpl;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
/**
 * 实时同步服务
 * 用于初始化和启动实时同步核心组件
 */
@Service
@Slf4j
public class RealtimeSyncService {
    
    @Autowired
    private PubSubEventSubscriberImpl eventSubscriber;
    
    @Autowired
    private ObjectRegistryImpl objectRegistry;
    
    private boolean started = false;
    
    /**
     * 启动实时同步服务
     */
    @PostConstruct
    public void start() {
        log.info("开始启动实时同步服务");
        
        try {
            // 刷新对象注册表
            objectRegistry.refreshRegistry();
            
            // 启动事件订阅
            eventSubscriber.startSubscription();
            
            started = true;
            log.info("实时同步服务启动成功");
        } catch (Exception e) {
            log.error("启动实时同步服务时发生异常: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 停止实时同步服务
     */
    @PreDestroy
    public void stop() {
        log.info("开始停止实时同步服务");
        
        try {
            if (started) {
                // 停止事件订阅
                eventSubscriber.stopSubscription();
                log.info("实时同步服务停止成功");
            } else {
                log.info("实时同步服务未启动，跳过停止操作");
            }
        } catch (Exception e) {
            log.error("停止实时同步服务时发生异常: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 重启实时同步服务
     */
    public void restart() {
        log.info("开始重启实时同步服务");
        
        try {
            // 停止服务
            stop();
            
            // 启动服务
            start();
            
            log.info("实时同步服务重启成功");
        } catch (Exception e) {
            log.error("重启实时同步服务时发生异常: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 刷新对象注册表
     */
    public void refreshObjectRegistry() {
        log.info("开始刷新对象注册表");
        
        try {
            objectRegistry.refreshRegistry();
            log.info("对象注册表刷新成功");
        } catch (Exception e) {
            log.error("刷新对象注册表时发生异常: {}", e.getMessage(), e);
        }
    }
}
