package com.datai.setting.event;

import com.datai.setting.model.domain.DataiConfiguration;
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
    
    /** 旧值 */
    private String oldValue;
    
    /** 新值 */
    private String newValue;
    
    /** ORG类型 */
    private String orgType;
    
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
    
    /**
     * 构造函数，包含旧值和新值
     * 
     * @param source 事件源
     * @param config 配置对象
     * @param operationType 操作类型
     * @param oldValue 旧值
     * @param newValue 新值
     */
    public ConfigChangeEvent(Object source, DataiConfiguration config, String operationType, 
                            String oldValue, String newValue) {
        super(source);
        this.config = config;
        this.operationType = operationType;
        this.oldValue = oldValue;
        this.newValue = newValue;
    }
    
    /**
     * 构造函数，包含旧值、新值和ORG类型
     * 
     * @param source 事件源
     * @param config 配置对象
     * @param operationType 操作类型
     * @param oldValue 旧值
     * @param newValue 新值
     * @param orgType ORG类型
     */
    public ConfigChangeEvent(Object source, DataiConfiguration config, String operationType, 
                            String oldValue, String newValue, String orgType) {
        super(source);
        this.config = config;
        this.operationType = operationType;
        this.oldValue = oldValue;
        this.newValue = newValue;
        this.orgType = orgType;
    }
    
    public DataiConfiguration getConfig() {
        return config;
    }
    
    public String getOperationType() {
        return operationType;
    }
    
    public String getOldValue() {
        return oldValue;
    }
    
    public String getNewValue() {
        return newValue;
    }
    
    public String getOrgType() {
        return orgType;
    }
    
    @Override
    public String toString() {
        return String.format("ConfigChangeEvent [configKey=%s, operationType=%s, oldValue=%s, newValue=%s, orgType=%s]", 
            config.getConfigKey(), operationType, oldValue, newValue, orgType);
    }
}