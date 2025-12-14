package com.datai.setting.service.impl;

import java.util.List;

import com.datai.common.core.domain.model.LoginUser;
import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.setting.mapper.DataiConfigurationMapper;
import com.datai.setting.domain.DataiConfiguration;
import com.datai.setting.service.IDataiConfigurationService;


/**
 * 配置Service业务层处理
 *
 * @author datai
 * @date 2025-12-14
 */
@Service
public class DataiConfigurationServiceImpl implements IDataiConfigurationService {
    @Autowired
    private DataiConfigurationMapper dataiConfigurationMapper;

    /**
     * 查询配置
     *
     * @param configId 配置主键
     * @return 配置
     */
    @Override
    public DataiConfiguration selectDataiConfigurationByConfigId(Long configId)
    {
        return dataiConfigurationMapper.selectDataiConfigurationByConfigId(configId);
    }

    /**
     * 查询配置列表
     *
     * @param dataiConfiguration 配置
     * @return 配置
     */
    @Override
    public List<DataiConfiguration> selectDataiConfigurationList(DataiConfiguration dataiConfiguration)
    {
        return dataiConfigurationMapper.selectDataiConfigurationList(dataiConfiguration);
    }

    /**
     * 新增配置
     *
     * @param dataiConfiguration 配置
     * @return 结果
     */
    @Override
    public int insertDataiConfiguration(DataiConfiguration dataiConfiguration)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiConfiguration.setCreateTime(DateUtils.getNowDate());
                dataiConfiguration.setUpdateTime(DateUtils.getNowDate());
                dataiConfiguration.setCreateBy(username);
                dataiConfiguration.setUpdateBy(username);
            return dataiConfigurationMapper.insertDataiConfiguration(dataiConfiguration);
    }

    /**
     * 修改配置
     *
     * @param dataiConfiguration 配置
     * @return 结果
     */
    @Override
    public int updateDataiConfiguration(DataiConfiguration dataiConfiguration)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiConfiguration.setUpdateTime(DateUtils.getNowDate());
                dataiConfiguration.setUpdateBy(username);
        return dataiConfigurationMapper.updateDataiConfiguration(dataiConfiguration);
    }

    /**
     * 批量删除配置
     *
     * @param configIds 需要删除的配置主键
     * @return 结果
     */
    @Override
    public int deleteDataiConfigurationByConfigIds(Long[] configIds)
    {
        return dataiConfigurationMapper.deleteDataiConfigurationByConfigIds(configIds);
    }

    /**
     * 删除配置信息
     *
     * @param configId 配置主键
     * @return 结果
     */
    @Override
    public int deleteDataiConfigurationByConfigId(Long configId)
    {
        return dataiConfigurationMapper.deleteDataiConfigurationByConfigId(configId);
    }
}
