package com.datai.integration.mapper;

import com.datai.integration.model.domain.DataiIntegrationRealtimeSyncLog;

import java.util.List;
import java.util.Map;

/**
 * 实时同步日志Mapper接口
 * 
 * @author datai
 * @date 2026-01-09
 */
public interface DataiIntegrationRealtimeSyncLogMapper 
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
     * 删除实时同步日志
     * 
     * @param id 实时同步日志主键
     * @return 结果
     */
    public int deleteDataiIntegrationRealtimeSyncLogById(Long id);

    /**
     * 批量删除实时同步日志
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataiIntegrationRealtimeSyncLogByIds(Long[] ids);

    /**
     * 查询综合统计信息
     * 
     * @param params 查询参数
     * @return 统计信息
     */
    Map<String, Object> selectOverallStatistics(Map<String, Object> params);

    /**
     * 按对象统计
     * 
     * @param params 查询参数
     * @return 对象统计列表
     */
    List<Map<String, Object>> selectStatisticsByObject(Map<String, Object> params);

    /**
     * 按操作类型统计
     * 
     * @param params 查询参数
     * @return 操作类型统计列表
     */
    List<Map<String, Object>> selectStatisticsByOperationType(Map<String, Object> params);

    /**
     * 按状态统计
     * 
     * @param params 查询参数
     * @return 状态统计列表
     */
    List<Map<String, Object>> selectStatisticsByStatus(Map<String, Object> params);

    /**
     * 按时间趋势统计
     * 
     * @param params 查询参数
     * @return 时间趋势统计列表
     */
    List<Map<String, Object>> selectStatisticsByTrend(Map<String, Object> params);
}
