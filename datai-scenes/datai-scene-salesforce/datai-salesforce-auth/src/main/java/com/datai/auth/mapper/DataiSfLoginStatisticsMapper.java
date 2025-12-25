package com.datai.auth.mapper;

import java.util.List;
import com.datai.auth.domain.DataiSfLoginStatistics;

/**
 * 登录统计Mapper接口
 * 
 * @author datai
 * @date 2025-12-24
 */
public interface DataiSfLoginStatisticsMapper 
{
    /**
     * 查询登录统计
     * 
     * @param id 登录统计主键
     * @return 登录统计
     */
    public DataiSfLoginStatistics selectDataiSfLoginStatisticsById(Long id);

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
     * 删除登录统计
     * 
     * @param id 登录统计主键
     * @return 结果
     */
    public int deleteDataiSfLoginStatisticsById(Long id);

    /**
     * 批量删除登录统计
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataiSfLoginStatisticsByIds(Long[] ids);
}
