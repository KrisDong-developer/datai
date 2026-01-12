package com.datai.integration.realtime;

import com.datai.integration.model.domain.DataiIntegrationObject;
import java.util.List;

/**
 * 对象注册表接口
 * 用于管理所有启用实时同步的对象信息
 */
public interface ObjectRegistry {
    
    /**
     * 注册启用实时同步的对象
     * @param object 对象信息
     */
    void registerObject(DataiIntegrationObject object);
    
    /**
     * 注销对象
     * @param objectApi 对象API
     */
    void unregisterObject(String objectApi);
    
    /**
     * 获取所有启用实时同步的对象
     * @return 启用实时同步的对象列表
     */
    List<DataiIntegrationObject> getRealtimeSyncObjects();
    
    /**
     * 检查对象是否已注册
     * @param objectApi 对象API
     * @return 是否已注册
     */
    boolean isObjectRegistered(String objectApi);
    
    /**
     * 刷新对象注册表
     */
    void refreshRegistry();
}
