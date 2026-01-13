package com.datai.integration.realtime;

import java.util.Map;

/**
 * 实时同步服务接口
 * 用于管理实时同步服务的生命周期
 */
public interface RealtimeSyncService {

    /**
     * 启动实时同步服务
     */
    void start();

    /**
     * 停止实时同步服务
     */
    void stop();

    /**
     * 重启实时同步服务
     */
    void restart();

    /**
     * 刷新对象注册表
     */
    void refreshObjectRegistry();

    /**
     * 检查实时同步服务是否已启动
     * @return 是否已启动
     */
    boolean isStarted();

    /**
     * 获取实时同步统计信息
     * @return 统计信息Map，包含服务状态、对象统计、同步性能等数据
     */
    Map<String, Object> getStatistics();
}