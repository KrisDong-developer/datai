package com.datai.setting.service.impl;

import java.util.List;
        import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.setting.mapper.DataiConfigVersionMapper;
import com.datai.setting.domain.DataiConfigVersion;
import com.datai.setting.service.IDataiConfigVersionService;
import com.datai.common.core.domain.model.LoginUser;


/**
 * 配置版本Service业务层处理
 *
 * @author datai
 * @date 2025-12-24
 */
@Service
public class DataiConfigVersionServiceImpl implements IDataiConfigVersionService {
    @Autowired
    private DataiConfigVersionMapper dataiConfigVersionMapper;

    /**
     * 查询配置版本
     *
     * @param id 配置版本主键
     * @return 配置版本
     */
    @Override
    public DataiConfigVersion selectDataiConfigVersionById(Long id)
    {
        return dataiConfigVersionMapper.selectDataiConfigVersionById(id);
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
     * @param ids 需要删除的配置版本主键
     * @return 结果
     */
    @Override
    public int deleteDataiConfigVersionByIds(Long[] ids)
    {
        return dataiConfigVersionMapper.deleteDataiConfigVersionByIds(ids);
    }

    /**
     * 删除配置版本信息
     *
     * @param id 配置版本主键
     * @return 结果
     */
    @Override
    public int deleteDataiConfigVersionById(Long id)
    {
        return dataiConfigVersionMapper.deleteDataiConfigVersionById(id);
    }
}
