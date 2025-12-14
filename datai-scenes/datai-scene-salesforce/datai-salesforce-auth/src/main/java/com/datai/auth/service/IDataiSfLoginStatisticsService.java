package com.datai.auth.service;

import java.util.List;
import com.datai.auth.domain.DataiSfLoginStatistics;

/**
 * 登录统计Service接口
 * 
 * @author datai
 * @date 2025-12-14
 */
public interface IDataiSfLoginStatisticsService 
{
    /**
     * 查询登录统计
     * 
     * @param statId 登录统计主键
     * @return 登录统计
     */
    public DataiSfLoginStatistics selectDataiSfLoginStatisticsByStatId(Long statId);

    /**
     * 查询登录统计列表
     * 
     * @param dataiSfLoginStatistics 登录统计
     * @return 登录统计集合
     */
    public List<DataiSfLoginStatistics> selectDataiSfLoginStatisticsList(DataiSfLoginStatistics dataiSfLoginStatistics);

    /**
     * 新增登录统计
     * 
     * @param dataiSfLoginStatistics 登录统计
     * @return 结果
     */
    public int insertDataiSfLoginStatistics(DataiSfLoginStatistics dataiSfLoginStatistics);

    /**
     * 修改登录统计
     * 
     * @param dataiSfLoginStatistics 登录统计
     * @return 结果
     */
    public int updateDataiSfLoginStatistics(DataiSfLoginStatistics dataiSfLoginStatistics);

    /**
     * 批量删除登录统计
     * 
     * @param statIds 需要删除的登录统计主键集合
     * @return 结果
     */
    public int deleteDataiSfLoginStatisticsByStatIds(Long[] statIds);

    /**
     * 删除登录统计信息
     * 
     * @param statId 登录统计主键
     * @return 结果
     */
    public int deleteDataiSfLoginStatisticsByStatId(Long statId);
}
