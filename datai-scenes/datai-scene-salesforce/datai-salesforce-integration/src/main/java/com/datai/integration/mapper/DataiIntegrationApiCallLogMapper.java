package com.datai.integration.mapper;

import java.util.List;
import java.util.Map;
import com.datai.integration.domain.DataiIntegrationApiCallLog;

/**
 * API调用日志Mapper接口
 * 
 * @author datai
 * @date 2025-12-28
 */
public interface DataiIntegrationApiCallLogMapper 
{
    /**
     * 查询API调用日志
     * 
     * @param id API调用日志主键
     * @return API调用日志
     */
    public DataiIntegrationApiCallLog selectDataiIntegrationApiCallLogById(Long id);

    /**
     * 查询API调用日志列表
     * 
     * @param dataiIntegrationApiCallLog API调用日志
     * @return API调用日志集合
     */
    public List<DataiIntegrationApiCallLog> selectDataiIntegrationApiCallLogList(DataiIntegrationApiCallLog dataiIntegrationApiCallLog);

    /**
     * 新增API调用日志
     * 
     * @param dataiIntegrationApiCallLog API调用日志
     * @return 结果
     */
    public int insertDataiIntegrationApiCallLog(DataiIntegrationApiCallLog dataiIntegrationApiCallLog);

    /**
     * 修改API调用日志
     * 
     * @param dataiIntegrationApiCallLog API调用日志
     * @return 结果
     */
    public int updateDataiIntegrationApiCallLog(DataiIntegrationApiCallLog dataiIntegrationApiCallLog);

    /**
     * 删除API调用日志
     * 
     * @param id API调用日志主键
     * @return 结果
     */
    public int deleteDataiIntegrationApiCallLogById(Long id);

    /**
     * 批量删除API调用日志
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataiIntegrationApiCallLogByIds(Long[] ids);

    /**
     * 获取API调用日志统计信息
     * 
     * @param params 查询参数
     * @return 统计信息
     */
    public Map<String, Object> getApiCallLogStatistics(Map<String, Object> params);
}
