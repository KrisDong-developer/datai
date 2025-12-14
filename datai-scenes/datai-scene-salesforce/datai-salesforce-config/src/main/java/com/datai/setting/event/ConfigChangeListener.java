package com.datai.setting.event;

import com.datai.setting.domain.DataiConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 配置变更事件监听器
 * 用于处理配置更新后的逻辑，实现组件重新初始化机制
 */
@Component
public class ConfigChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(ConfigChangeListener.class);
    
    /**
     * 监听配置变更事件
     * 
     * @param event 配置变更事件
     */
    @EventListener
    public void handleConfigChangeEvent(ConfigChangeEvent event) {
        long startTime = System.currentTimeMillis();
        logger.info("开始处理配置变更事件: {}", event);
        
        try {
            DataiConfiguration config = event.getConfig();
            String operationType = event.getOperationType();
            
            // 根据配置键和操作类型执行不同的处理逻辑
            handleSpecificConfigChange(config, operationType);
            
            // 通用处理逻辑
            handleGenericConfigChange(config, operationType);
            
            long endTime = System.currentTimeMillis();
            logger.info("配置变更事件处理完成，耗时: {}ms", (endTime - startTime));
        } catch (Exception e) {
            logger.error("处理配置变更事件失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 处理特定配置变更
     * 
     * @param config 配置对象
     * @param operationType 操作类型
     */
    private void handleSpecificConfigChange(DataiConfiguration config, String operationType) {
        String configKey = config.getConfigKey();
        String configValue = config.getConfigValue();
        
        // 根据不同的配置键执行不同的处理逻辑
        switch (configKey) {
            case "salesforce.api.version":
                handleApiVersionChange(configValue, operationType);
                break;
            case "salesforce.environment.type":
                handleEnvironmentTypeChange(configValue, operationType);
                break;
            case "salesforce.auth.type":
                handleAuthTypeChange(configValue, operationType);
                break;
            // 可以添加更多特定配置的处理逻辑
            default:
                logger.debug("没有特定处理逻辑的配置: {}", configKey);
                break;
        }
    }
    
    /**
     * 处理API版本变更
     * 
     * @param apiVersion API版本
     * @param operationType 操作类型
     */
    private void handleApiVersionChange(String apiVersion, String operationType) {
        logger.info("Salesforce API版本变更: {}, 操作类型: {}", apiVersion, operationType);
        // 这里可以实现API版本变更后的处理逻辑
        // 例如：重新初始化API客户端、更新WSDL文件等
    }
    
    /**
     * 处理环境类型变更
     * 
     * @param environmentType 环境类型
     * @param operationType 操作类型
     */
    private void handleEnvironmentTypeChange(String environmentType, String operationType) {
        logger.info("Salesforce环境类型变更: {}, 操作类型: {}", environmentType, operationType);
        // 这里可以实现环境类型变更后的处理逻辑
        // 例如：切换服务端点、更新认证配置等
    }
    
    /**
     * 处理认证类型变更
     * 
     * @param authType 认证类型
     * @param operationType 操作类型
     */
    private void handleAuthTypeChange(String authType, String operationType) {
        logger.info("Salesforce认证类型变更: {}, 操作类型: {}", authType, operationType);
        // 这里可以实现认证类型变更后的处理逻辑
        // 例如：重新初始化认证客户端、更新认证配置等
    }
    
    /**
     * 处理通用配置变更
     * 
     * @param config 配置对象
     * @param operationType 操作类型
     */
    private void handleGenericConfigChange(DataiConfiguration config, String operationType) {
        logger.debug("通用配置变更处理: {}, 操作类型: {}", config.getConfigKey(), operationType);
        
        // 这里可以实现通用的配置变更处理逻辑
        // 例如：更新缓存、通知其他服务等
    }
}