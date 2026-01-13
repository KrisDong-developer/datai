package com.datai.integration.controller;

import com.datai.integration.realtime.EventSubscriber;
import com.datai.integration.realtime.ObjectRegistry;
import com.datai.integration.realtime.RealtimeSyncService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/integration/realtime/service")
@Tag(name = "【实时服务管理】统一管理")
@Slf4j
public class RealtimeServiceController {

    @Autowired
    private ObjectRegistry objectRegistry;

    @Autowired
    private EventSubscriber eventSubscriber;

    @Autowired
    private RealtimeSyncService realtimeSyncService;

    @Operation(summary = "获取实时服务状态")
    @PreAuthorize("@ss.hasPermi('integration:realtime:status')")
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        log.info("获取实时服务状态");

        Map<String, Object> result = new HashMap<>();

        try {
            Map<String, Object> status = new HashMap<>();
            status.put("objectRegistryInitialized", objectRegistry.getRegisteredObjectCount() > 0);
            status.put("registeredObjectCount", objectRegistry.getRegisteredObjectCount());
            status.put("eventSubscriberStarted", eventSubscriber.isSubscribed());
            status.put("realtimeSyncServiceStarted", realtimeSyncService.isStarted());

            result.put("success", true);
            result.put("message", "获取实时服务状态成功");
            result.put("status", status);

            log.info("获取实时服务状态成功");
        } catch (Exception e) {
            log.error("获取实时服务状态时发生异常: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "获取实时服务状态失败: " + e.getMessage());
        }

        return result;
    }

    @Operation(summary = "初始化对象注册表")
    @PreAuthorize("@ss.hasPermi('integration:realtime:init:registry')")
    @PostMapping("/init/registry")
    public Map<String, Object> initRegistry() {
        log.info("初始化对象注册表");

        Map<String, Object> result = new HashMap<>();

        try {
            objectRegistry.refreshRegistry();

            result.put("success", true);
            result.put("message", "对象注册表初始化成功");
            result.put("registeredObjectCount", objectRegistry.getRegisteredObjectCount());

            log.info("对象注册表初始化成功，共注册 {} 个对象", objectRegistry.getRegisteredObjectCount());
        } catch (Exception e) {
            log.error("初始化对象注册表时发生异常: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "对象注册表初始化失败: " + e.getMessage());
        }

        return result;
    }

    @Operation(summary = "启动事件订阅")
    @PreAuthorize("@ss.hasPermi('integration:realtime:start:subscriber')")
    @PostMapping("/start/subscriber")
    public Map<String, Object> startSubscriber() {
        log.info("启动事件订阅");

        Map<String, Object> result = new HashMap<>();

        try {
            eventSubscriber.startSubscription();

            result.put("success", true);
            result.put("message", "事件订阅启动成功");
            result.put("subscribed", eventSubscriber.isSubscribed());

            log.info("事件订阅启动成功");
        } catch (Exception e) {
            log.error("启动事件订阅时发生异常: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "事件订阅启动失败: " + e.getMessage());
        }

        return result;
    }

    @Operation(summary = "停止事件订阅")
    @PreAuthorize("@ss.hasPermi('integration:realtime:stop:subscriber')")
    @PostMapping("/stop/subscriber")
    public Map<String, Object> stopSubscriber() {
        log.info("停止事件订阅");

        Map<String, Object> result = new HashMap<>();

        try {
            eventSubscriber.stopSubscription();

            result.put("success", true);
            result.put("message", "事件订阅停止成功");
            result.put("subscribed", eventSubscriber.isSubscribed());

            log.info("事件订阅停止成功");
        } catch (Exception e) {
            log.error("停止事件订阅时发生异常: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "事件订阅停止失败: " + e.getMessage());
        }

        return result;
    }

    @Operation(summary = "启动实时同步服务")
    @PreAuthorize("@ss.hasPermi('integration:realtime:start:service')")
    @PostMapping("/start/service")
    public Map<String, Object> startService() {
        log.info("启动实时同步服务");

        Map<String, Object> result = new HashMap<>();

        try {
            realtimeSyncService.start();

            result.put("success", true);
            result.put("message", "实时同步服务启动成功");
            result.put("started", realtimeSyncService.isStarted());

            log.info("实时同步服务启动成功");
        } catch (Exception e) {
            log.error("启动实时同步服务时发生异常: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "实时同步服务启动失败: " + e.getMessage());
        }

        return result;
    }

    @Operation(summary = "停止实时同步服务")
    @PreAuthorize("@ss.hasPermi('integration:realtime:stop:service')")
    @PostMapping("/stop/service")
    public Map<String, Object> stopService() {
        log.info("停止实时同步服务");

        Map<String, Object> result = new HashMap<>();

        try {
            realtimeSyncService.stop();

            result.put("success", true);
            result.put("message", "实时同步服务停止成功");
            result.put("started", realtimeSyncService.isStarted());

            log.info("实时同步服务停止成功");
        } catch (Exception e) {
            log.error("停止实时同步服务时发生异常: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "实时同步服务停止失败: " + e.getMessage());
        }

        return result;
    }

    @Operation(summary = "重启实时同步服务")
    @PreAuthorize("@ss.hasPermi('integration:realtime:restart:service')")
    @PostMapping("/restart/service")
    public Map<String, Object> restartService() {
        log.info("重启实时同步服务");

        Map<String, Object> result = new HashMap<>();

        try {
            realtimeSyncService.restart();

            result.put("success", true);
            result.put("message", "实时同步服务重启成功");
            result.put("started", realtimeSyncService.isStarted());

            log.info("实时同步服务重启成功");
        } catch (Exception e) {
            log.error("重启实时同步服务时发生异常: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "实时同步服务重启失败: " + e.getMessage());
        }

        return result;
    }

    @Operation(summary = "一键启动所有实时服务")
    @PreAuthorize("@ss.hasPermi('integration:realtime:start:all')")
    @PostMapping("/start/all")
    public Map<String, Object> startAll() {
        log.info("一键启动所有实时服务");

        Map<String, Object> result = new HashMap<>();

        try {
            Map<String, Object> details = new HashMap<>();

            objectRegistry.refreshRegistry();
            details.put("objectRegistry", "初始化成功，注册对象数: " + objectRegistry.getRegisteredObjectCount());

            eventSubscriber.startSubscription();
            details.put("eventSubscriber", "启动成功，订阅状态: " + eventSubscriber.isSubscribed());

            realtimeSyncService.start();
            details.put("realtimeSyncService", "启动成功，运行状态: " + realtimeSyncService.isStarted());

            result.put("success", true);
            result.put("message", "所有实时服务启动成功");
            result.put("details", details);

            log.info("所有实时服务启动成功");
        } catch (Exception e) {
            log.error("启动所有实时服务时发生异常: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "启动所有实时服务失败: " + e.getMessage());
        }

        return result;
    }

    @Operation(summary = "一键停止所有实时服务")
    @PreAuthorize("@ss.hasPermi('integration:realtime:stop:all')")
    @PostMapping("/stop/all")
    public Map<String, Object> stopAll() {
        log.info("一键停止所有实时服务");

        Map<String, Object> result = new HashMap<>();

        try {
            Map<String, Object> details = new HashMap<>();

            realtimeSyncService.stop();
            details.put("realtimeSyncService", "停止成功");

            eventSubscriber.stopSubscription();
            details.put("eventSubscriber", "停止成功");

            result.put("success", true);
            result.put("message", "所有实时服务停止成功");
            result.put("details", details);

            log.info("所有实时服务停止成功");
        } catch (Exception e) {
            log.error("停止所有实时服务时发生异常: {}", e.getMessage(), e);
            result.put("success", false);
            result.put("message", "停止所有实时服务失败: " + e.getMessage());
        }

        return result;
    }
}
