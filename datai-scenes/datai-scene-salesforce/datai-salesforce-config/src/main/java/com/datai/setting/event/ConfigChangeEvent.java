package com.datai.setting.event;

import com.datai.setting.domain.DataiConfiguration;
import org.springframework.context.ApplicationEvent;

/**
 * 配置变更事件
 * 用于在配置更新时发布事件，通知相关服务配置已变更
 */
public class ConfigChangeEvent extends ApplicationEvent {
    private static final long serialVersionUID = 1L;
    
    /** 配置对象 */
    private DataiConfiguration config;
    
    /** 操作类型：CREATE、UPDATE、DELETE */
    private String operationType;
    
    /**
     * 构造函数
     * 
     * @param source 事件源
     * @param config 配置对象
     * @param operationType 操作类型
     */
    public ConfigChangeEvent(Object source, DataiConfiguration config, String operationType) {
        super(source);
        this.config = config;
        this.operationType = operationType;
    }
    
    public DataiConfiguration getConfig() {
        return config;
    }
    
    public String getOperationType() {
        return operationType;
    }
    
    @Override
    public String toString() {
        return String.format("ConfigChangeEvent [configKey=%s, operationType=%s]", 
            config.getConfigKey(), operationType);
    }
}