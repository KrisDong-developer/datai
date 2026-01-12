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
 * 对象注册表实现类
 * 用于管理所有启用实时同步的对象信息
 */
@Component
@Slf4j
public class ObjectRegistryImpl implements ObjectRegistry {
    
    @Autowired
    private IDataiIntegrationObjectService dataiIntegrationObjectService;
    
    // 存储启用实时同步的对象
    private Map<String, DataiIntegrationObject> realtimeSyncObjects = new ConcurrentHashMap<>();
    
    @Override
    public void registerObject(DataiIntegrationObject object) {
        log.info("注册启用实时同步的对象: {}", object.getApi());
        
        if (Boolean.TRUE.equals(object.getIsRealtimeSync())) {
            realtimeSyncObjects.put(object.getApi(), object);
            log.info("对象 {} 注册成功", object.getApi());
        } else {
            log.info("对象 {} 未开启实时同步，跳过注册", object.getApi());
        }
    }
    
    @Override
    public void unregisterObject(String objectApi) {
        log.info("注销对象: {}", objectApi);
        
        if (realtimeSyncObjects.containsKey(objectApi)) {
            realtimeSyncObjects.remove(objectApi);
            log.info("对象 {} 注销成功", objectApi);
        } else {
            log.info("对象 {} 未注册，跳过注销", objectApi);
        }
    }
    
    @Override
    public List<DataiIntegrationObject> getRealtimeSyncObjects() {
        return new ArrayList<>(realtimeSyncObjects.values());
    }
    
    @Override
    public boolean isObjectRegistered(String objectApi) {
        return realtimeSyncObjects.containsKey(objectApi);
    }
    
    @Override
    public void refreshRegistry() {
        log.info("开始刷新对象注册表");
        
        try {
            // 清空现有注册表
            realtimeSyncObjects.clear();
            
            // 查询所有启用实时同步的对象
            DataiIntegrationObject queryObject = new DataiIntegrationObject();
            queryObject.setIsRealtimeSync(true);
            List<DataiIntegrationObject> objects = dataiIntegrationObjectService.selectDataiIntegrationObjectList(queryObject);
            
            // 注册启用实时同步的对象
            for (DataiIntegrationObject object : objects) {
                registerObject(object);
            }
            
            log.info("对象注册表刷新成功，共注册 {} 个启用实时同步的对象", realtimeSyncObjects.size());
        } catch (Exception e) {
            log.error("刷新对象注册表时发生异常: {}", e.getMessage(), e);
        }
    }
}
