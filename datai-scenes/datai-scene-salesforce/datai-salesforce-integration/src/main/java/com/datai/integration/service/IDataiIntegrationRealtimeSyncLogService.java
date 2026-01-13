package com.datai.integration.service;

import com.datai.integration.model.domain.DataiIntegrationRealtimeSyncLog;

import java.util.List;
import java.util.Map;

/**
 * 实时同步日志Service接口
 * 
 * @author datai
 * @date 2026-01-09
 */
public interface IDataiIntegrationRealtimeSyncLogService 
{
    /**
     * 查询实时同步日志
     * 
     * @param id 实时同步日志主键
     * @return 实时同步日志
     */
    public DataiIntegrationRealtimeSyncLog selectDataiIntegrationRealtimeSyncLogById(Long id);

    /**
     * 查询实时同步日志列表
     * 
     * @param dataiIntegrationRealtimeSyncLog 实时同步日志
     * @return 实时同步日志集合
     */
    public List<DataiIntegrationRealtimeSyncLog> selectDataiIntegrationRealtimeSyncLogList(DataiIntegrationRealtimeSyncLog dataiIntegrationRealtimeSyncLog);

    /**
     * 新增实时同步日志
     * 
     * @param dataiIntegrationRealtimeSyncLog 实时同步日志
     * @return 结果
     */
    public int insertDataiIntegrationRealtimeSyncLog(DataiIntegrationRealtimeSyncLog dataiIntegrationRealtimeSyncLog);

    /**
     * 修改实时同步日志
     * 
     * @param dataiIntegrationRealtimeSyncLog 实时同步日志
     * @return 结果
     */
    public int updateDataiIntegrationRealtimeSyncLog(DataiIntegrationRealtimeSyncLog dataiIntegrationRealtimeSyncLog);

    /**
     * 批量删除实时同步日志
     * 
     * @param ids 需要删除的实时同步日志主键集合
     * @return 结果
     */
    public int deleteDataiIntegrationRealtimeSyncLogByIds(Long[] ids);

    /**
     * 删除实时同步日志信息
     * 
     * @param id 实时同步日志主键
     * @return 结果
     */
    public int deleteDataiIntegrationRealtimeSyncLogById(Long id);

    /**
     * 获取实时同步日志统计信息
     * 
     * @param params 查询参数
     * @return 统计信息
     */
    Map<String, Object> getStatistics(Map<String, Object> params);
}
