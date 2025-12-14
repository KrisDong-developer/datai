package com.datai.auth.service.impl;

import java.util.List;

import com.datai.common.core.domain.model.LoginUser;
import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.auth.mapper.DataiSfLoginStatisticsMapper;
import com.datai.auth.domain.DataiSfLoginStatistics;
import com.datai.auth.service.IDataiSfLoginStatisticsService;


/**
 * 登录统计Service业务层处理
 *
 * @author datai
 * @date 2025-12-14
 */
@Service
public class DataiSfLoginStatisticsServiceImpl implements IDataiSfLoginStatisticsService {
    @Autowired
    private DataiSfLoginStatisticsMapper dataiSfLoginStatisticsMapper;

    /**
     * 查询登录统计
     *
     * @param statId 登录统计主键
     * @return 登录统计
     */
    @Override
    public DataiSfLoginStatistics selectDataiSfLoginStatisticsByStatId(Long statId)
    {
        return dataiSfLoginStatisticsMapper.selectDataiSfLoginStatisticsByStatId(statId);
    }

    /**
     * 查询登录统计列表
     *
     * @param dataiSfLoginStatistics 登录统计
     * @return 登录统计
     */
    @Override
    public List<DataiSfLoginStatistics> selectDataiSfLoginStatisticsList(DataiSfLoginStatistics dataiSfLoginStatistics)
    {
        return dataiSfLoginStatisticsMapper.selectDataiSfLoginStatisticsList(dataiSfLoginStatistics);
    }

    /**
     * 新增登录统计
     *
     * @param dataiSfLoginStatistics 登录统计
     * @return 结果
     */
    @Override
    public int insertDataiSfLoginStatistics(DataiSfLoginStatistics dataiSfLoginStatistics)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiSfLoginStatistics.setCreateTime(DateUtils.getNowDate());
                dataiSfLoginStatistics.setUpdateTime(DateUtils.getNowDate());
                dataiSfLoginStatistics.setCreateBy(username);
                dataiSfLoginStatistics.setUpdateBy(username);
            return dataiSfLoginStatisticsMapper.insertDataiSfLoginStatistics(dataiSfLoginStatistics);
    }

    /**
     * 修改登录统计
     *
     * @param dataiSfLoginStatistics 登录统计
     * @return 结果
     */
    @Override
    public int updateDataiSfLoginStatistics(DataiSfLoginStatistics dataiSfLoginStatistics)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiSfLoginStatistics.setUpdateTime(DateUtils.getNowDate());
                dataiSfLoginStatistics.setUpdateBy(username);
        return dataiSfLoginStatisticsMapper.updateDataiSfLoginStatistics(dataiSfLoginStatistics);
    }

    /**
     * 批量删除登录统计
     *
     * @param statIds 需要删除的登录统计主键
     * @return 结果
     */
    @Override
    public int deleteDataiSfLoginStatisticsByStatIds(Long[] statIds)
    {
        return dataiSfLoginStatisticsMapper.deleteDataiSfLoginStatisticsByStatIds(statIds);
    }

    /**
     * 删除登录统计信息
     *
     * @param statId 登录统计主键
     * @return 结果
     */
    @Override
    public int deleteDataiSfLoginStatisticsByStatId(Long statId)
    {
        return dataiSfLoginStatisticsMapper.deleteDataiSfLoginStatisticsByStatId(statId);
    }
}
