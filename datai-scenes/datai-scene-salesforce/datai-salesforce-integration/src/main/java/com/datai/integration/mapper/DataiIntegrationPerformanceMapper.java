package com.datai.integration.mapper;

import java.util.List;
import com.datai.integration.domain.DataiIntegrationPerformance;

/**
 * 同步性能监控Mapper接口
 * 
 * @author datai
 * @date 2025-12-22
 */
public interface DataiIntegrationPerformanceMapper 
{
    /**
     * 查询同步性能监控
     * 
     * @param id 同步性能监控主键
     * @return 同步性能监控
     */
    public DataiIntegrationPerformance selectDataiIntegrationPerformanceById(Long id);

    /**
     * 查询同步性能监控列表
     * 
     * @param dataiIntegrationPerformance 同步性能监控
     * @return 同步性能监控集合
     */
    public List<DataiIntegrationPerformance> selectDataiIntegrationPerformanceList(DataiIntegrationPerformance dataiIntegrationPerformance);

    /**
     * 新增同步性能监控
     * 
     * @param dataiIntegrationPerformance 同步性能监控
     * @return 结果
     */
    public int insertDataiIntegrationPerformance(DataiIntegrationPerformance dataiIntegrationPerformance);

    /**
     * 修改同步性能监控
     * 
     * @param dataiIntegrationPerformance 同步性能监控
     * @return 结果
     */
    public int updateDataiIntegrationPerformance(DataiIntegrationPerformance dataiIntegrationPerformance);

    /**
     * 删除同步性能监控
     * 
     * @param id 同步性能监控主键
     * @return 结果
     */
    public int deleteDataiIntegrationPerformanceById(Long id);

    /**
     * 批量删除同步性能监控
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataiIntegrationPerformanceByIds(Long[] ids);
}
