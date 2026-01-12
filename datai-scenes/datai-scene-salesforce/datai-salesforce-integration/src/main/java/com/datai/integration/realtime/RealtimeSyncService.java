package com.datai.integration.realtime;

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
}