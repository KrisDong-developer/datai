package com.datai.integration.realtime.impl;

import com.datai.integration.model.domain.DataiIntegrationObject;
import com.datai.integration.realtime.EventSubscriber;
import com.datai.integration.realtime.ObjectRegistry;
import com.datai.integration.realtime.RealtimeSyncService;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;


/**
 * 实时同步服务实现
 * 用于初始化和启动实时同步核心组件
 */
@Service
@Slf4j
public class RealtimeSyncServiceImpl implements RealtimeSyncService {

    @Autowired
    private EventSubscriber eventSubscriber;

    @Autowired
    private ObjectRegistry objectRegistry;

    private boolean started = false;
    private LocalDateTime startTime;

    @Override
    public void start() {
        if (started) {
            log.info("实时同步服务已经启动，跳过启动操作");
            return;
        }

        log.info("开始启动实时同步服务");

        try {
            // 刷新对象注册表
            objectRegistry.refreshRegistry();

            // 启动事件订阅
            eventSubscriber.startSubscription();

            started = true;
            startTime = LocalDateTime.now();
            log.info("实时同步服务启动成功");
        } catch (Exception e) {
            log.error("启动实时同步服务时发生异常: {}", e.getMessage(), e);
            started = false;
            startTime = null;
        }
    }

    @Override
    @PreDestroy
    public void stop() {
        if (!started) {
            log.info("实时同步服务未启动，跳过停止操作");
            return;
        }

        log.info("开始停止实时同步服务");

        try {
            // 停止事件订阅
            eventSubscriber.stopSubscription();
            started = false;
            log.info("实时同步服务停止成功");
        } catch (Exception e) {
            log.error("停止实时同步服务时发生异常: {}", e.getMessage(), e);
        }
    }

    @Override
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

    @Override
    public void refreshObjectRegistry() {
        log.info("开始刷新对象注册表");

        try {
            objectRegistry.refreshRegistry();
            log.info("对象注册表刷新成功");
        } catch (Exception e) {
            log.error("刷新对象注册表时发生异常: {}", e.getMessage(), e);
        }
    }

    @Override
    public boolean isStarted() {
        return started;
    }

    @Override
    public Map<String, Object> getStatistics() {
        Map<String, Object> statistics = new HashMap<>();

        try {
            Map<String, Object> serviceStatus = getServiceStatus();
            Map<String, Object> objectStatistics = getObjectStatistics();
            Map<String, Object> subscriptionStatistics = getSubscriptionStatistics();

            statistics.put("success", true);
            statistics.put("message", "获取实时同步统计信息成功");
            statistics.put("data", new HashMap<String, Object>() {{
                put("serviceStatus", serviceStatus);
                put("objectStatistics", objectStatistics);
                put("subscriptionStatistics", subscriptionStatistics);
            }});

            log.info("获取实时同步统计信息成功");
        } catch (Exception e) {
            log.error("获取实时同步统计信息时发生异常: {}", e.getMessage(), e);
            statistics.put("success", false);
            statistics.put("message", "获取实时同步统计信息失败: " + e.getMessage());
        }

        return statistics;
    }

    private Map<String, Object> getServiceStatus() {
        Map<String, Object> serviceStatus = new HashMap<>();
        serviceStatus.put("isRunning", started);

        if (started && startTime != null) {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            serviceStatus.put("startTime", startTime.format(formatter));

            Duration duration = Duration.between(startTime, LocalDateTime.now());
            long hours = duration.toHours();
            long minutes = duration.toMinutesPart();
            long seconds = duration.toSecondsPart();

            if (hours > 0) {
                serviceStatus.put("runningDuration", String.format("%d小时%d分钟", hours, minutes));
            } else if (minutes > 0) {
                serviceStatus.put("runningDuration", String.format("%d分钟%d秒", minutes, seconds));
            } else {
                serviceStatus.put("runningDuration", String.format("%d秒", seconds));
            }
        } else {
            serviceStatus.put("startTime", null);
            serviceStatus.put("runningDuration", null);
        }

        return serviceStatus;
    }

    private Map<String, Object> getObjectStatistics() {
        Map<String, Object> objectStatistics = new HashMap<>();

        List<DataiIntegrationObject> realtimeSyncObjects = objectRegistry.getRealtimeSyncObjects();

        int totalCount = realtimeSyncObjects.size();
        int standardCount = 0;
        int customCount = 0;

        List<Map<String, Object>> objectDetails = new ArrayList<>();

        for (DataiIntegrationObject object : realtimeSyncObjects) {
            Map<String, Object> objectDetail = new HashMap<>();
            objectDetail.put("objectApi", object.getApi());
            objectDetail.put("objectName", object.getLabel());
            objectDetail.put("isCustom", object.getIsCustom());

            if (Boolean.TRUE.equals(object.getIsCustom())) {
                customCount++;
            } else {
                standardCount++;
            }

            objectDetails.add(objectDetail);
        }

        objectStatistics.put("totalCount", totalCount);
        objectStatistics.put("standardCount", standardCount);
        objectStatistics.put("customCount", customCount);
        objectStatistics.put("objects", objectDetails);

        return objectStatistics;
    }

    private Map<String, Object> getSubscriptionStatistics() {
        Map<String, Object> subscriptionStatistics = new HashMap<>();

        boolean isSubscribed = eventSubscriber.isSubscribed();
        subscriptionStatistics.put("isSubscribed", isSubscribed);

        return subscriptionStatistics;
    }
}