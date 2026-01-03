package com.datai.integration.service;

import java.util.List;
import com.datai.integration.model.domain.DataiIntegrationRateLimit;

/**
 * API限流监控Service接口
 * 提供API限流配置的增删改查功能，用于管理API调用频率限制
 * 
 * @author datai
 * @date 2025-12-24
 */
public interface IDataiIntegrationRateLimitService 
{
    /**
     * 根据主键ID查询API限流监控配置
     * 
     * @param id API限流监控主键
     * @return API限流监控配置对象
     */
    public DataiIntegrationRateLimit selectDataiIntegrationRateLimitById(Long id);

    /**
     * 根据条件查询API限流监控配置列表
     * 支持多条件组合查询，如限流类型、API名称等
     * 
     * @param dataiIntegrationRateLimit 查询条件对象
     * @return API限流监控配置集合
     */
    public List<DataiIntegrationRateLimit> selectDataiIntegrationRateLimitList(DataiIntegrationRateLimit dataiIntegrationRateLimit);

    /**
     * 新增API限流监控配置
     * 创建新的限流规则，包括限流类型、最大限制值、重置时间等
     * 
     * @param dataiIntegrationRateLimit API限流监控配置对象
     * @return 影响的行数
     */
    public int insertDataiIntegrationRateLimit(DataiIntegrationRateLimit dataiIntegrationRateLimit);

    /**
     * 修改API限流监控配置
     * 更新现有的限流规则，包括使用量、剩余值、重置时间等
     * 
     * @param dataiIntegrationRateLimit API限流监控配置对象
     * @return 影响的行数
     */
    public int updateDataiIntegrationRateLimit(DataiIntegrationRateLimit dataiIntegrationRateLimit);

    /**
     * 批量删除API限流监控配置
     * 根据主键ID数组批量删除多条限流配置记录
     * 
     * @param ids 需要删除的API限流监控主键集合
     * @return 影响的行数
     */
    public int deleteDataiIntegrationRateLimitByIds(Long[] ids);

    /**
     * 根据主键ID删除API限流监控配置
     * 删除单条限流配置记录
     * 
     * @param id API限流监控主键
     * @return 影响的行数
     */
    public int deleteDataiIntegrationRateLimitById(Long id);
}
