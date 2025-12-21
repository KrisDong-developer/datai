package org.dromara.salesforce.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * 应用配置类 - 管理Salesforce数据加载器的配置参数
 * 
 * AppConfig用于管理应用程序的各种配置参数，包括HTTP传输设置、API调用选项等。
 * 它提供了一种集中管理配置的方式，便于在应用程序的不同部分访问和使用配置参数。
 * 
 * 主要功能：
 * 1. 管理应用程序配置参数
 * 2. 提供配置参数的访问接口
 * 3. 支持布尔型配置参数的获取
 * 
 * 设计特点：
 * - 使用Spring Boot的@ConfigurationProperties注解进行配置绑定
 * - 通过单例模式确保全局唯一实例
 * - 支持灵活的配置参数管理
 * 
 * 使用场景：
 * - 配置HTTP传输选项
 * - 管理API调用行为
 * - 控制应用程序功能开关
 * 
 * @author Salesforce
 * @since 64.0.0
 */
@Component
@ConfigurationProperties(prefix = "salesforce.app")
public class AppConfig {
    
    /**
     * 使用传统的HTTP GET方法属性键
     */
    public static final String PROP_USE_LEGACY_HTTP_GET = "use.legacy.http.get";
    
    /**
     * 配置参数映射表
     */
    private Map<String, Object> properties = new HashMap<>();
    
    /**
     * 当前配置实例
     */
    private static AppConfig currentConfig;
    
    /**
     * 构造函数
     */
    public AppConfig() {
        currentConfig = this;
        // 默认配置
        properties.put(PROP_USE_LEGACY_HTTP_GET, false);
    }
    
    /**
     * 获取当前配置实例
     * 
     * @return AppConfig 当前配置实例
     */
    public static AppConfig getCurrentConfig() {
        return currentConfig;
    }
    
    /**
     * 获取布尔型配置参数值
     * 
     * @param key 配置参数键
     * @return boolean 配置参数值
     */
    public boolean getBoolean(String key) {
        Object value = properties.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return false;
    }
    
    /**
     * 设置配置参数值
     * 
     * @param key 配置参数键
     * @param value 配置参数值
     */
    public void setProperty(String key, Object value) {
        properties.put(key, value);
    }
    
    /**
     * 获取配置参数值
     * 
     * @param key 配置参数键
     * @return Object 配置参数值
     */
    public Object getProperty(String key) {
        return properties.get(key);
    }
    
    // Getter和Setter方法
    public Map<String, Object> getProperties() {
        return properties;
    }
    
    public void setProperties(Map<String, Object> properties) {
        this.properties = properties;
    }
}