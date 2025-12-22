package com.datai.integration.service;

import java.util.List;
import com.datai.integration.domain.DataiIntegrationLog;

/**
 * 数据同步日志Service接口
 * 
 * @author datai
 * @date 2025-12-22
 */
public interface IDataiIntegrationLogService 
{
    /**
     * 查询数据同步日志
     * 
     * @param id 数据同步日志主键
     * @return 数据同步日志
     */
    public DataiIntegrationLog selectDataiIntegrationLogById(Long id);

    /**
     * 查询数据同步日志列表
     * 
     * @param dataiIntegrationLog 数据同步日志
     * @return 数据同步日志集合
     */
    public List<DataiIntegrationLog> selectDataiIntegrationLogList(DataiIntegrationLog dataiIntegrationLog);

    /**
     * 新增数据同步日志
     * 
     * @param dataiIntegrationLog 数据同步日志
     * @return 结果
     */
    public int insertDataiIntegrationLog(DataiIntegrationLog dataiIntegrationLog);

    /**
     * 修改数据同步日志
     * 
     * @param dataiIntegrationLog 数据同步日志
     * @return 结果
     */
    public int updateDataiIntegrationLog(DataiIntegrationLog dataiIntegrationLog);

    /**
     * 批量删除数据同步日志
     * 
     * @param ids 需要删除的数据同步日志主键集合
     * @return 结果
     */
    public int deleteDataiIntegrationLogByIds(Long[] ids);

    /**
     * 删除数据同步日志信息
     * 
     * @param id 数据同步日志主键
     * @return 结果
     */
    public int deleteDataiIntegrationLogById(Long id);
}
