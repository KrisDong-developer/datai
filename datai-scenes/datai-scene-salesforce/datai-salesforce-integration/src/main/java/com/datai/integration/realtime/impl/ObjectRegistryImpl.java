package com.datai.integration.realtime.impl;

import com.datai.integration.model.domain.DataiIntegrationObject;
import com.datai.integration.realtime.ObjectRegistry;
import com.datai.integration.service.IDataiIntegrationObjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 对象注册表实现
 * 用于管理所有启用实时同步的对象
 */
@Component
@Slf4j
public class ObjectRegistryImpl implements ObjectRegistry {

    @Autowired
    private IDataiIntegrationObjectService objectService;

    private final Map<String, DataiIntegrationObject> objectRegistry = new ConcurrentHashMap<>();

    @Override
    public void registerObject(DataiIntegrationObject object) {
        if (object == null || object.getApi() == null) {
            log.warn("尝试注册空对象或API为空的对象，跳过注册");
            return;
        }

        objectRegistry.put(object.getApi(), object);
        log.info("成功注册启用实时同步的对象: {}", object.getApi());
    }

    @Override
    public void unregisterObject(String objectApi) {
        if (objectApi == null) {
            log.warn("尝试注销API为空的对象，跳过注销");
            return;
        }

        if (objectRegistry.remove(objectApi) != null) {
            log.info("成功注销对象: {}", objectApi);
        } else {
            log.warn("尝试注销不存在的对象: {}", objectApi);
        }
    }

    @Override
    public void refreshRegistry() {
        log.info("开始刷新对象注册表");

        try {
            // 清空现有注册表
            objectRegistry.clear();

            // 获取所有启用实时同步的对象
            List<DataiIntegrationObject> realtimeSyncObjects = objectService.getRealtimeSyncObjects();

            // 注册每个对象
            for (DataiIntegrationObject object : realtimeSyncObjects) {
                registerObject(object);
            }

            log.info("对象注册表刷新完成，共注册 {} 个启用实时同步的对象", realtimeSyncObjects.size());
        } catch (Exception e) {
            log.error("刷新对象注册表时发生异常: {}", e.getMessage(), e);
        }
    }

    @Override
    public List<DataiIntegrationObject> getRealtimeSyncObjects() {
        return new ArrayList<>(objectRegistry.values());
    }

    @Override
    public boolean isObjectRealtimeSyncEnabled(String objectApi) {
        if (objectApi == null) {
            return false;
        }

        DataiIntegrationObject object = objectRegistry.get(objectApi);
        return object != null && Boolean.TRUE.equals(object.getIsRealtimeSync());
    }
}
