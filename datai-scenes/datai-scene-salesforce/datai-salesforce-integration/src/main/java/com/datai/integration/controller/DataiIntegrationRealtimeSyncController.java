package com.datai.integration.controller;

import com.datai.integration.realtime.RealtimeSyncService;
import com.datai.integration.realtime.impl.ObjectRegistryImpl;
import com.datai.integration.model.domain.DataiIntegrationObject;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;
import java.util.HashMap;

/**
 * 实时同步控制器
 * 用于管理实时同步服务
 */
@RestController
@RequestMapping("/integration/realtime")
@Tag(name = "【实时同步管理】管理")
@Slf4j
public class DataiIntegrationRealtimeSyncController {
    
    @Autowired
    private RealtimeSyncService realtimeSyncService;
    
    @Autowired
    private ObjectRegistryImpl objectRegistry;
    
    /**
     * 获取实时同步服务状态
     */
    @Operation(summary = "获取实时同步服务状态")
    @PreAuthorize("@ss.hasPermi('integration:realtime:status')")
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        log.info("获取实时同步服务状态");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            // 获取启用实时同步的对象列表
            List<DataiIntegrationObject> realtimeSyncObjects = objectRegistry.getRealtimeSyncObjects();
            
            result.put("success", true);
            result.put("message", "获取实时同步服务状态成功");
            result.put("realtimeSyncObjects", realtimeSyncObjects);
            result.put("objectCount", realtimeSyncObjects.size());
            
            log.info("获取实时同步服务状态成功，共 {} 个启用实时同步的对象", realtimeSyncObjects.size());
        } catch (Exception e) {
            log.error("获取实时同步服务状态时发生异常: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "获取实时同步服务状态失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 启动实时同步服务
     */
    @Operation(summary = "启动实时同步服务")
    @PreAuthorize("@ss.hasPermi('integration:realtime:start')")
    @PostMapping("/start")
    public Map<String, Object> start() {
        log.info("启动实时同步服务");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            realtimeSyncService.start();
            result.put("success", true);
            result.put("message", "实时同步服务启动成功");
            log.info("实时同步服务启动成功");
        } catch (Exception e) {
            log.error("启动实时同步服务时发生异常: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "实时同步服务启动失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 停止实时同步服务
     */
    @Operation(summary = "停止实时同步服务")
    @PreAuthorize("@ss.hasPermi('integration:realtime:stop')")
    @PostMapping("/stop")
    public Map<String, Object> stop() {
        log.info("停止实时同步服务");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            realtimeSyncService.stop();
            result.put("success", true);
            result.put("message", "实时同步服务停止成功");
            log.info("实时同步服务停止成功");
        } catch (Exception e) {
            log.error("停止实时同步服务时发生异常: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "实时同步服务停止失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 重启实时同步服务
     */
    @Operation(summary = "重启实时同步服务")
    @PreAuthorize("@ss.hasPermi('integration:realtime:restart')")
    @PostMapping("/restart")
    public Map<String, Object> restart() {
        log.info("重启实时同步服务");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            realtimeSyncService.restart();
            result.put("success", true);
            result.put("message", "实时同步服务重启成功");
            log.info("实时同步服务重启成功");
        } catch (Exception e) {
            log.error("重启实时同步服务时发生异常: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "实时同步服务重启失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 刷新对象注册表
     */
    @Operation(summary = "刷新对象注册表")
    @PreAuthorize("@ss.hasPermi('integration:realtime:refresh')")
    @PostMapping("/refresh")
    public Map<String, Object> refresh() {
        log.info("刷新对象注册表");
        
        Map<String, Object> result = new HashMap<>();
        
        try {
            realtimeSyncService.refreshObjectRegistry();
            
            // 获取刷新后的对象列表
            List<DataiIntegrationObject> realtimeSyncObjects = objectRegistry.getRealtimeSyncObjects();
            
            result.put("success", true);
            result.put("message", "对象注册表刷新成功");
            result.put("realtimeSyncObjects", realtimeSyncObjects);
            result.put("objectCount", realtimeSyncObjects.size());
            
            log.info("对象注册表刷新成功，共 {} 个启用实时同步的对象", realtimeSyncObjects.size());
        } catch (Exception e) {
            log.error("刷新对象注册表时发生异常: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "对象注册表刷新失败: " + e.getMessage());
        }
        
        return result;
    }
}
