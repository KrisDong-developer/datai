package com.datai.integration.mapper;

import java.util.List;
import com.datai.integration.model.domain.DataiIntegrationRateLimit;

/**
 * API限流监控Mapper接口
 * 
 * @author datai
 * @date 2025-12-24
 */
public interface DataiIntegrationRateLimitMapper 
{
    /**
     * 查询API限流监控
     * 
     * @param id API限流监控主键
     * @return API限流监控
     */
    public DataiIntegrationRateLimit selectDataiIntegrationRateLimitById(Long id);

    /**
     * 查询API限流监控列表
     * 
     * @param dataiIntegrationRateLimit API限流监控
     * @return API限流监控集合
     */
    public List<DataiIntegrationRateLimit> selectDataiIntegrationRateLimitList(DataiIntegrationRateLimit dataiIntegrationRateLimit);

    /**
     * 新增API限流监控
     * 
     * @param dataiIntegrationRateLimit API限流监控
     * @return 结果
     */
    public int insertDataiIntegrationRateLimit(DataiIntegrationRateLimit dataiIntegrationRateLimit);

    /**
     * 修改API限流监控
     * 
     * @param dataiIntegrationRateLimit API限流监控
     * @return 结果
     */
    public int updateDataiIntegrationRateLimit(DataiIntegrationRateLimit dataiIntegrationRateLimit);

    /**
     * 删除API限流监控
     * 
     * @param id API限流监控主键
     * @return 结果
     */
    public int deleteDataiIntegrationRateLimitById(Long id);

    /**
     * 批量删除API限流监控
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataiIntegrationRateLimitByIds(Long[] ids);
}
