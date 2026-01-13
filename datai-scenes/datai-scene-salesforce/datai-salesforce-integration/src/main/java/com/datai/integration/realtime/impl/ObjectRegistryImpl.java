package com.datai.integration.realtime.impl;

import com.datai.integration.model.domain.DataiIntegrationObject;
import com.datai.integration.realtime.ObjectRegistry;
import com.datai.integration.service.IDataiIntegrationObjectService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Component
public class ObjectRegistryImpl implements ObjectRegistry {

    @Autowired
    @Lazy
    private IDataiIntegrationObjectService integrationObjectService;

    private final ConcurrentMap<String, DataiIntegrationObject> objectRegistry = new ConcurrentHashMap<>();

    @Override
    public void refreshRegistry() {
        log.info("刷新对象注册表");

        try {
            Set<DataiIntegrationObject> realtimeSyncObjects = integrationObjectService.getRealtimeSyncObjects();

            objectRegistry.clear();

            for (DataiIntegrationObject object : realtimeSyncObjects) {
                objectRegistry.put(object.getApi(), object);
                log.debug("注册实时同步对象: {}", object.getApi());
            }

            log.info("对象注册表刷新完成，共注册 {} 个实时同步对象", objectRegistry.size());
        } catch (Exception e) {
            log.error("刷新对象注册表时发生异常: {}", e.getMessage(), e);
        }
    }

    @Override
    public boolean isObjectRegistered(String objectApi) {
        return objectRegistry.containsKey(objectApi);
    }

    @Override
    public DataiIntegrationObject getObject(String objectApi) {
        return objectRegistry.get(objectApi);
    }

    @Override
    public Set<String> getRegisteredObjects() {
        return objectRegistry.keySet();
    }

    @Override
    public int getRegisteredObjectCount() {
        return objectRegistry.size();
    }

    @Override
    public void registerObject(DataiIntegrationObject object) {
        if (object == null || object.getApi() == null) {
            log.warn("尝试注册空对象或对象API为空，跳过注册");
            return;
        }

        if (!object.getIsRealtimeSync()) {
            log.warn("尝试注册未启用实时同步的对象: {}, 跳过注册", object.getApi());
            return;
        }

        objectRegistry.put(object.getApi(), object);
        log.info("注册实时同步对象: {}", object.getApi());
    }

    @Override
    public void unregisterObject(String objectApi) {
        if (objectApi == null) {
            log.warn("尝试注销空对象API，跳过注销");
            return;
        }

        DataiIntegrationObject removedObject = objectRegistry.remove(objectApi);
        if (removedObject != null) {
            log.info("注销实时同步对象: {}", objectApi);
        } else {
            log.warn("尝试注销未注册的对象: {}", objectApi);
        }
    }

    @Override
    public List<DataiIntegrationObject> getRealtimeSyncObjects() {
        return new ArrayList<>(objectRegistry.values());
    }
}