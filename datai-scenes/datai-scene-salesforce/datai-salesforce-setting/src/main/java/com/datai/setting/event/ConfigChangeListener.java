package com.datai.setting.event;

import com.datai.common.core.domain.model.LoginUser;
import com.datai.common.utils.DateUtils;
import com.datai.common.utils.SecurityUtils;
import com.datai.setting.domain.DataiConfigAuditLog;
import com.datai.setting.domain.DataiConfiguration;
import com.datai.setting.service.IDataiConfigAuditLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.time.LocalDateTime;

/**
 * 配置变更事件监听器
 * 用于处理配置更新后的逻辑，实现组件重新初始化机制
 */
@Component
public class ConfigChangeListener {
    private static final Logger logger = LoggerFactory.getLogger(ConfigChangeListener.class);
    
    @Autowired
    private IDataiConfigAuditLogService auditLogService;
    
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
            
            // 记录配置变更审计日志
            recordConfigAuditLog(config, operationType, event.getOldValue(), event.getNewValue(), "SUCCESS", null);
            
            long endTime = System.currentTimeMillis();
            logger.info("配置变更事件处理完成，耗时: {}ms", (endTime - startTime));
        } catch (Exception e) {
            logger.error("处理配置变更事件失败: {}", e.getMessage(), e);
            // 记录配置变更审计日志（失败）
            recordConfigAuditLog(event.getConfig(), event.getOperationType(), 
                                event.getOldValue(), event.getNewValue(), "FAILED", e.getMessage());
        }
    }
    
    /**
     * 记录配置变更审计日志
     * 
     * @param config 配置对象
     * @param operationType 操作类型
     * @param oldValue 旧值
     * @param newValue 新值
     * @param result 操作结果
     * @param errorMessage 错误信息
     */
    private void recordConfigAuditLog(DataiConfiguration config, String operationType, 
                                     String oldValue, String newValue, String result, String errorMessage) {
        try {
            // 获取当前登录用户
            LoginUser loginUser = SecurityUtils.getLoginUser();
            String username = loginUser != null ? loginUser.getUsername() : "system";
            Long deptId = loginUser != null ? loginUser.getDeptId() : null;
            
            // 获取HTTP请求信息
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            String ipAddress = "unknown";
            String userAgent = "unknown";
            String requestId = "unknown";
            
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                ipAddress = getIpAddress(request);
                userAgent = request.getHeader("User-Agent");
                requestId = request.getHeader("Request-ID");
                if (requestId == null) {
                    requestId = "system-generated-" + System.currentTimeMillis();
                }
            }
            
            // 创建审计日志对象
            DataiConfigAuditLog auditLog = new DataiConfigAuditLog();
            auditLog.setDeptId(deptId);
            auditLog.setOperationType(operationType);
            auditLog.setObjectType("CONFIGURATION");
            auditLog.setObjectId(config.getId());
            auditLog.setOldValue(oldValue);
            auditLog.setNewValue(newValue);
            auditLog.setOperationDesc(String.format("%s配置: %s", operationType, config.getConfigKey()));
            auditLog.setOperator(username);
            auditLog.setOperationTime(LocalDateTime.now());
            auditLog.setIpAddress(ipAddress);
            auditLog.setUserAgent(userAgent);
            auditLog.setRequestId(requestId);
            auditLog.setResult(result);
            auditLog.setErrorMessage(errorMessage);
            
            // 设置创建人和更新人信息
            auditLog.setCreateBy(username);
            auditLog.setUpdateBy(username);
            
            // 保存审计日志
            auditLogService.insertDataiConfigAuditLog(auditLog);
            logger.info("配置变更审计日志已记录: {}", auditLog.getOperationDesc());
        } catch (Exception e) {
            logger.error("记录配置变更审计日志失败: {}", e.getMessage(), e);
        }
    }
    
    /**
     * 获取客户端IP地址
     * 
     * @param request HTTP请求
     * @return IP地址
     */
    private String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        // 处理多个IP地址的情况，取第一个
        if (ip != null && ip.contains(",")) {
            ip = ip.split(",")[0].trim();
        }
        return ip;
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