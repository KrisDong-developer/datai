package com.datai.setting.service.impl;

import java.util.List;

import com.datai.common.core.domain.model.LoginUser;
import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.setting.mapper.DataiConfigVersionMapper;
import com.datai.setting.domain.DataiConfigVersion;
import com.datai.setting.service.IDataiConfigVersionService;


/**
 * 配置版本Service业务层处理
 *
 * @author datai
 * @date 2025-12-14
 */
@Service
public class DataiConfigVersionServiceImpl implements IDataiConfigVersionService {
    @Autowired
    private DataiConfigVersionMapper dataiConfigVersionMapper;

    /**
     * 查询配置版本
     *
     * @param versionId 配置版本主键
     * @return 配置版本
     */
    @Override
    public DataiConfigVersion selectDataiConfigVersionByVersionId(Long versionId)
    {
        return dataiConfigVersionMapper.selectDataiConfigVersionByVersionId(versionId);
    }

    /**
     * 查询配置版本列表
     *
     * @param dataiConfigVersion 配置版本
     * @return 配置版本
     */
    @Override
    public List<DataiConfigVersion> selectDataiConfigVersionList(DataiConfigVersion dataiConfigVersion)
    {
        return dataiConfigVersionMapper.selectDataiConfigVersionList(dataiConfigVersion);
    }

    /**
     * 新增配置版本
     *
     * @param dataiConfigVersion 配置版本
     * @return 结果
     */
    @Override
    public int insertDataiConfigVersion(DataiConfigVersion dataiConfigVersion)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiConfigVersion.setCreateTime(DateUtils.getNowDate());
                dataiConfigVersion.setUpdateTime(DateUtils.getNowDate());
                dataiConfigVersion.setCreateBy(username);
                dataiConfigVersion.setUpdateBy(username);
            return dataiConfigVersionMapper.insertDataiConfigVersion(dataiConfigVersion);
    }

    /**
     * 修改配置版本
     *
     * @param dataiConfigVersion 配置版本
     * @return 结果
     */
    @Override
    public int updateDataiConfigVersion(DataiConfigVersion dataiConfigVersion)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiConfigVersion.setUpdateTime(DateUtils.getNowDate());
                dataiConfigVersion.setUpdateBy(username);
        return dataiConfigVersionMapper.updateDataiConfigVersion(dataiConfigVersion);
    }

    /**
     * 批量删除配置版本
     *
     * @param versionIds 需要删除的配置版本主键
     * @return 结果
     */
    @Override
    public int deleteDataiConfigVersionByVersionIds(Long[] versionIds)
    {
        return dataiConfigVersionMapper.deleteDataiConfigVersionByVersionIds(versionIds);
    }

    /**
     * 删除配置版本信息
     *
     * @param versionId 配置版本主键
     * @return 结果
     */
    @Override
    public int deleteDataiConfigVersionByVersionId(Long versionId)
    {
        return dataiConfigVersionMapper.deleteDataiConfigVersionByVersionId(versionId);
    }
}
