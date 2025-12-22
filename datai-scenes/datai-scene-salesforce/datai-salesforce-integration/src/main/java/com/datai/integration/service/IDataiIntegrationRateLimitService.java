package com.datai.integration.service;

import java.util.List;
import com.datai.integration.domain.DataiIntegrationRateLimit;

/**
 * API限流管理Service接口
 * 
 * @author datai
 * @date 2025-12-22
 */
public interface IDataiIntegrationRateLimitService 
{
    /**
     * 查询API限流管理
     * 
     * @param id API限流管理主键
     * @return API限流管理
     */
    public DataiIntegrationRateLimit selectDataiIntegrationRateLimitById(Long id);

    /**
     * 查询API限流管理列表
     * 
     * @param dataiIntegrationRateLimit API限流管理
     * @return API限流管理集合
     */
    public List<DataiIntegrationRateLimit> selectDataiIntegrationRateLimitList(DataiIntegrationRateLimit dataiIntegrationRateLimit);

    /**
     * 新增API限流管理
     * 
     * @param dataiIntegrationRateLimit API限流管理
     * @return 结果
     */
    public int insertDataiIntegrationRateLimit(DataiIntegrationRateLimit dataiIntegrationRateLimit);

    /**
     * 修改API限流管理
     * 
     * @param dataiIntegrationRateLimit API限流管理
     * @return 结果
     */
    public int updateDataiIntegrationRateLimit(DataiIntegrationRateLimit dataiIntegrationRateLimit);

    /**
     * 批量删除API限流管理
     * 
     * @param ids 需要删除的API限流管理主键集合
     * @return 结果
     */
    public int deleteDataiIntegrationRateLimitByIds(Long[] ids);

    /**
     * 删除API限流管理信息
     * 
     * @param id API限流管理主键
     * @return 结果
     */
    public int deleteDataiIntegrationRateLimitById(Long id);
}
