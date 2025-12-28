package com.datai.integration.controller;

import com.datai.integration.domain.dto.*;
import com.datai.integration.service.ConnectionMethodCallService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Salesforce API限流控制器
 * <p>
 * 该控制器提供API限流状态查询和管理功能，包括：
 * - 查询限流状态
 * - 重置限流计数器
 * - 获取API使用统计
 * </p>
 *
 * @author datai
 * @since 1.0.0
 */
@Api(tags = "Salesforce API限流管理")
@RestController
@RequestMapping("/api/rate-limit")
@Slf4j
public class SalesforceRateLimitController {

    @Autowired
    private SalesforceRateLimiter rateLimiter;

    @Autowired
    private RateLimiterMonitor rateLimiterMonitor;

    @Autowired
    private ConnectionMethodCallService connectionMethodCallService;

    /**
     * 获取限流状态信息
     *
     * @return 限流状态信息
     */
    @ApiOperation("获取限流状态信息")
    @GetMapping("/status")
    public Map<String, Object> getStatus() {
        Map<String, Object> result = new HashMap<>();
        result.put("success", true);
        result.put("message", "获取限流状态成功");
        result.put("data", buildRateLimitInfo());
        return result;
    }

    /**
     * 获取指定API类型的限流状态
     *
     * @param apiType API类型
     * @return 限流状态信息
     */
    @ApiOperation("获取指定API类型的限流状态")
    @GetMapping("/status/{apiType}")
    public Map<String, Object> getStatusByType(
            @ApiParam(value = "API类型", required = true)
            @PathVariable String apiType) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            SalesforceRateLimiter.ApiType type = SalesforceRateLimiter.ApiType.valueOf(apiType.toUpperCase());
            result.put("success", true);
            result.put("message", "获取限流状态成功");
            result.put("data", buildApiTypeInfo(type));
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("message", "无效的API类型: " + apiType);
            result.put("data", null);
        }
        
        return result;
    }

    /**
     * 获取可用令牌数
     *
     * @param apiType API类型
     * @return 可用令牌数
     */
    @ApiOperation("获取可用令牌数")
    @GetMapping("/tokens/{apiType}")
    public Map<String, Object> getAvailableTokens(
            @ApiParam(value = "API类型", required = true)
            @PathVariable String apiType) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            SalesforceRateLimiter.ApiType type = SalesforceRateLimiter.ApiType.valueOf(apiType.toUpperCase());
            int tokens = rateLimiter.getAvailableTokens(type);
            result.put("success", true);
            result.put("message", "获取可用令牌数成功");
            result.put("data", tokens);
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("message", "无效的API类型: " + apiType);
            result.put("data", null);
        }
        
        return result;
    }

    /**
     * 获取今日已使用的API请求数
     *
     * @param apiType API类型
     * @return 已使用的请求数
     */
    @ApiOperation("获取今日已使用的API请求数")
    @GetMapping("/used/{apiType}")
    public Map<String, Object> getDailyUsedRequests(
            @ApiParam(value = "API类型", required = true)
            @PathVariable String apiType) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            SalesforceRateLimiter.ApiType type = SalesforceRateLimiter.ApiType.valueOf(apiType.toUpperCase());
            int used = rateLimiter.getDailyUsedRequests(type);
            result.put("success", true);
            result.put("message", "获取今日已使用的API请求数成功");
            result.put("data", used);
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("message", "无效的API类型: " + apiType);
            result.put("data", null);
        }
        
        return result;
    }

    /**
     * 获取今日剩余的API请求数
     *
     * @param apiType API类型
     * @return 剩余请求数
     */
    @ApiOperation("获取今日剩余的API请求数")
    @GetMapping("/remaining/{apiType}")
    public Map<String, Object> getDailyRemainingRequests(
            @ApiParam(value = "API类型", required = true)
            @PathVariable String apiType) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            SalesforceRateLimiter.ApiType type = SalesforceRateLimiter.ApiType.valueOf(apiType.toUpperCase());
            int remaining = rateLimiter.getDailyRemainingRequests(type);
            result.put("success", true);
            result.put("message", "获取今日剩余的API请求数成功");
            result.put("data", remaining);
        } catch (IllegalArgumentException e) {
            result.put("success", false);
            result.put("message", "无效的API类型: " + apiType);
            result.put("data", null);
        }
        
        return result;
    }

    /**
     * 重置限流计数器
     *
     * @return 重置结果
     */
    @ApiOperation("重置限流计数器")
    @PostMapping("/reset")
    public Map<String, Object> reset() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            rateLimiter.reset();
            result.put("success", true);
            result.put("message", "限流计数器重置成功");
            result.put("data", buildRateLimitInfo());
            log.info("限流计数器已重置");
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "重置限流计数器失败: " + e.getMessage());
            result.put("data", null);
            log.error("重置限流计数器失败", e);
        }
        
        return result;
    }

    /**
     * 构建限流信息
     *
     * @return 限流信息
     */
    private Map<String, Object> buildRateLimitInfo() {
        Map<String, Object> info = new HashMap<>();
        
        for (SalesforceRateLimiter.ApiType apiType : SalesforceRateLimiter.ApiType.values()) {
            info.put(apiType.getName(), buildApiTypeInfo(apiType));
        }
        
        return info;
    }

    /**
     * 构建API类型信息
     *
     * @param apiType API类型
     * @return API类型信息
     */
    private Map<String, Object> buildApiTypeInfo(SalesforceRateLimiter.ApiType apiType) {
        Map<String, Object> typeInfo = new HashMap<>();
        typeInfo.put("apiType", apiType.getName());
        typeInfo.put("availableTokens", rateLimiter.getAvailableTokens(apiType));
        typeInfo.put("dailyUsed", rateLimiter.getDailyUsedRequests(apiType));
        typeInfo.put("dailyLimit", apiType.getMaxRequestsPerDay());
        typeInfo.put("dailyRemaining", rateLimiter.getDailyRemainingRequests(apiType));
        typeInfo.put("requestsPerSecond", apiType.getRequestsPerSecond());
        
        return typeInfo;
    }

    @ApiOperation("获取实时限流状态")
    @GetMapping("/monitor/realtime/{apiType}")
    public Map<String, Object> getRealTimeStatus(
            @ApiParam(value = "API类型", required = true)
            @PathVariable String apiType) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            RealTimeStatusDTO status = rateLimiterMonitor.getRealTimeStatus(apiType);
            result.put("success", true);
            result.put("message", "获取实时限流状态成功");
            result.put("data", status);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取实时限流状态失败: " + e.getMessage());
            result.put("data", null);
            log.error("获取实时限流状态失败", e);
        }
        
        return result;
    }

    @ApiOperation("获取所有API类型的实时限流状态")
    @GetMapping("/monitor/realtime")
    public Map<String, Object> getAllRealTimeStatus() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, RealTimeStatusDTO> statuses = rateLimiterMonitor.getAllRealTimeStatus();
            result.put("success", true);
            result.put("message", "获取所有实时限流状态成功");
            result.put("data", statuses);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取所有实时限流状态失败: " + e.getMessage());
            result.put("data", null);
            log.error("获取所有实时限流状态失败", e);
        }
        
        return result;
    }

    @ApiOperation("获取限流告警信息")
    @GetMapping("/monitor/alerts/{apiType}")
    public Map<String, Object> getAlerts(
            @ApiParam(value = "API类型", required = true)
            @PathVariable String apiType,
            @ApiParam(value = "是否已解决")
            @RequestParam(required = false) Boolean resolved) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<AlertDTO> alerts = rateLimiterMonitor.getAlerts(apiType, resolved);
            result.put("success", true);
            result.put("message", "获取限流告警信息成功");
            result.put("data", alerts);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取限流告警信息失败: " + e.getMessage());
            result.put("data", null);
            log.error("获取限流告警信息失败", e);
        }
        
        return result;
    }

    @ApiOperation("获取所有限流告警信息")
    @GetMapping("/monitor/alerts")
    public Map<String, Object> getAllAlerts(
            @ApiParam(value = "是否已解决")
            @RequestParam(required = false) Boolean resolved) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            List<AlertDTO> alerts = rateLimiterMonitor.getAllAlerts(resolved);
            result.put("success", true);
            result.put("message", "获取所有限流告警信息成功");
            result.put("data", alerts);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取所有限流告警信息失败: " + e.getMessage());
            result.put("data", null);
            log.error("获取所有限流告警信息失败", e);
        }
        
        return result;
    }

    @ApiOperation("解决告警")
    @PutMapping("/monitor/alerts/{apiType}/{alertId}/resolve")
    public Map<String, Object> resolveAlert(
            @ApiParam(value = "API类型", required = true)
            @PathVariable String apiType,
            @ApiParam(value = "告警ID", required = true)
            @PathVariable Long alertId) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            rateLimiterMonitor.resolveAlert(apiType, alertId);
            result.put("success", true);
            result.put("message", "告警已解决");
            result.put("data", null);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "解决告警失败: " + e.getMessage());
            result.put("data", null);
            log.error("解决告警失败", e);
        }
        
        return result;
    }

    @ApiOperation("配置限流参数")
    @PostMapping("/monitor/config/{apiType}")
    public Map<String, Object> configureLimit(
            @ApiParam(value = "API类型", required = true)
            @PathVariable String apiType,
            @RequestBody LimitConfigDTO config) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            LimitConfigDTO updatedConfig = rateLimiterMonitor.configureLimit(apiType, config);
            result.put("success", true);
            result.put("message", "配置限流参数成功");
            result.put("data", updatedConfig);
            log.info("已更新API类型 {} 的限流配置", apiType);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "配置限流参数失败: " + e.getMessage());
            result.put("data", null);
            log.error("配置限流参数失败", e);
        }
        
        return result;
    }

    @ApiOperation("获取限流配置")
    @GetMapping("/monitor/config/{apiType}")
    public Map<String, Object> getLimitConfig(
            @ApiParam(value = "API类型", required = true)
            @PathVariable String apiType) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            LimitConfigDTO config = rateLimiterMonitor.getLimitConfig(apiType);
            result.put("success", true);
            result.put("message", "获取限流配置成功");
            result.put("data", config);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取限流配置失败: " + e.getMessage());
            result.put("data", null);
            log.error("获取限流配置失败", e);
        }
        
        return result;
    }

    @ApiOperation("获取所有限流配置")
    @GetMapping("/monitor/config")
    public Map<String, Object> getAllLimitConfigs() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, LimitConfigDTO> configs = rateLimiterMonitor.getAllLimitConfigs();
            result.put("success", true);
            result.put("message", "获取所有限流配置成功");
            result.put("data", configs);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取所有限流配置失败: " + e.getMessage());
            result.put("data", null);
            log.error("获取所有限流配置失败", e);
        }
        
        return result;
    }

    @ApiOperation("获取API使用统计")
    @GetMapping("/monitor/statistics/{apiType}")
    public Map<String, Object> getUsageStatistics(
            @ApiParam(value = "API类型", required = true)
            @PathVariable String apiType,
            @ApiParam(value = "开始时间", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @ApiParam(value = "结束时间", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            UsageStatisticsDTO statistics = rateLimiterMonitor.getUsageStatistics(apiType, startTime, endTime);
            result.put("success", true);
            result.put("message", "获取API使用统计成功");
            result.put("data", statistics);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取API使用统计失败: " + e.getMessage());
            result.put("data", null);
            log.error("获取API使用统计失败", e);
        }
        
        return result;
    }

    @ApiOperation("获取API使用趋势")
    @GetMapping("/monitor/trend/{apiType}")
    public Map<String, Object> getTrend(
            @ApiParam(value = "API类型", required = true)
            @PathVariable String apiType,
            @ApiParam(value = "趋势类型", required = true)
            @RequestParam TrendDTO.TrendType trendType,
            @ApiParam(value = "时间间隔", required = true)
            @RequestParam TrendDTO.Interval interval,
            @ApiParam(value = "开始时间", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @ApiParam(value = "结束时间", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            TrendDTO trend = rateLimiterMonitor.getTrend(apiType, trendType, interval, startTime, endTime);
            result.put("success", true);
            result.put("message", "获取API使用趋势成功");
            result.put("data", trend);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取API使用趋势失败: " + e.getMessage());
            result.put("data", null);
            log.error("获取API使用趋势失败", e);
        }
        
        return result;
    }

    @ApiOperation("获取连接方法调用统计")
    @GetMapping("/connection/method-calls")
    public Map<String, Object> getConnectionMethodCalls() {
        Map<String, Object> result = new HashMap<>();
        
        try {
            Map<String, Integer> methodCalls = connectionMethodCallService.getAllMethodCallCounts();
            result.put("success", true);
            result.put("message", "获取连接方法调用统计成功");
            result.put("data", methodCalls);
        } catch (Exception e) {
            result.put("success", false);
            result.put("message", "获取连接方法调用统计失败: " + e.getMessage());
            result.put("data", null);
            log.error("获取连接方法调用统计失败", e);
        }
        
        return result;
    }
}
