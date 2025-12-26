package com.datai.integration.mapper;

import java.util.List;
import com.datai.integration.domain.DataiIntegrationSyncLog;

/**
 * 数据同步日志Mapper接口
 * 
 * @author datai
 * @date 2025-12-26
 */
public interface DataiIntegrationSyncLogMapper 
{
    /**
     * 查询数据同步日志
     * 
     * @param id 数据同步日志主键
     * @return 数据同步日志
     */
    public DataiIntegrationSyncLog selectDataiIntegrationSyncLogById(Long id);

    /**
     * 查询数据同步日志列表
     * 
     * @param dataiIntegrationSyncLog 数据同步日志
     * @return 数据同步日志集合
     */
    public List<DataiIntegrationSyncLog> selectDataiIntegrationSyncLogList(DataiIntegrationSyncLog dataiIntegrationSyncLog);

    /**
     * 新增数据同步日志
     * 
     * @param dataiIntegrationSyncLog 数据同步日志
     * @return 结果
     */
    public int insertDataiIntegrationSyncLog(DataiIntegrationSyncLog dataiIntegrationSyncLog);

    /**
     * 修改数据同步日志
     * 
     * @param dataiIntegrationSyncLog 数据同步日志
     * @return 结果
     */
    public int updateDataiIntegrationSyncLog(DataiIntegrationSyncLog dataiIntegrationSyncLog);

    /**
     * 删除数据同步日志
     * 
     * @param id 数据同步日志主键
     * @return 结果
     */
    public int deleteDataiIntegrationSyncLogById(Long id);

    /**
     * 批量删除数据同步日志
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataiIntegrationSyncLogByIds(Long[] ids);
}
