package com.datai.setting.service.impl;

import java.util.List;
        import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.setting.mapper.DataiConfigEnvironmentMapper;
import com.datai.setting.domain.DataiConfigEnvironment;
import com.datai.setting.service.IDataiConfigEnvironmentService;
import com.datai.common.core.domain.model.LoginUser;


/**
 * 配置环境Service业务层处理
 *
 * @author datai
 * @date 2025-12-24
 */
@Service
public class DataiConfigEnvironmentServiceImpl implements IDataiConfigEnvironmentService {
    @Autowired
    private DataiConfigEnvironmentMapper dataiConfigEnvironmentMapper;

    /**
     * 查询配置环境
     *
     * @param id 配置环境主键
     * @return 配置环境
     */
    @Override
    public DataiConfigEnvironment selectDataiConfigEnvironmentById(Long id)
    {
        return dataiConfigEnvironmentMapper.selectDataiConfigEnvironmentById(id);
    }

    /**
     * 查询配置环境列表
     *
     * @param dataiConfigEnvironment 配置环境
     * @return 配置环境
     */
    @Override
    public List<DataiConfigEnvironment> selectDataiConfigEnvironmentList(DataiConfigEnvironment dataiConfigEnvironment)
    {
        return dataiConfigEnvironmentMapper.selectDataiConfigEnvironmentList(dataiConfigEnvironment);
    }

    /**
     * 新增配置环境
     *
     * @param dataiConfigEnvironment 配置环境
     * @return 结果
     */
    @Override
    public int insertDataiConfigEnvironment(DataiConfigEnvironment dataiConfigEnvironment)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiConfigEnvironment.setCreateTime(DateUtils.getNowDate());
                dataiConfigEnvironment.setUpdateTime(DateUtils.getNowDate());
                dataiConfigEnvironment.setCreateBy(username);
                dataiConfigEnvironment.setUpdateBy(username);
            return dataiConfigEnvironmentMapper.insertDataiConfigEnvironment(dataiConfigEnvironment);
    }

    /**
     * 修改配置环境
     *
     * @param dataiConfigEnvironment 配置环境
     * @return 结果
     */
    @Override
    public int updateDataiConfigEnvironment(DataiConfigEnvironment dataiConfigEnvironment)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiConfigEnvironment.setUpdateTime(DateUtils.getNowDate());
                dataiConfigEnvironment.setUpdateBy(username);
        return dataiConfigEnvironmentMapper.updateDataiConfigEnvironment(dataiConfigEnvironment);
    }

    /**
     * 批量删除配置环境
     *
     * @param ids 需要删除的配置环境主键
     * @return 结果
     */
    @Override
    public int deleteDataiConfigEnvironmentByIds(Long[] ids)
    {
        return dataiConfigEnvironmentMapper.deleteDataiConfigEnvironmentByIds(ids);
    }

    /**
     * 删除配置环境信息
     *
     * @param id 配置环境主键
     * @return 结果
     */
    @Override
    public int deleteDataiConfigEnvironmentById(Long id)
    {
        return dataiConfigEnvironmentMapper.deleteDataiConfigEnvironmentById(id);
    }
}
