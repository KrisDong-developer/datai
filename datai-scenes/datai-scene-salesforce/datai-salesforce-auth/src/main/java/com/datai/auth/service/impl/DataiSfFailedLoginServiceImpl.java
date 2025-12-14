package com.datai.auth.service.impl;

import java.util.List;

import com.datai.common.core.domain.model.LoginUser;
import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.auth.mapper.DataiSfFailedLoginMapper;
import com.datai.auth.domain.DataiSfFailedLogin;
import com.datai.auth.service.IDataiSfFailedLoginService;


/**
 * 失败登录Service业务层处理
 *
 * @author datai
 * @date 2025-12-14
 */
@Service
public class DataiSfFailedLoginServiceImpl implements IDataiSfFailedLoginService {
    @Autowired
    private DataiSfFailedLoginMapper dataiSfFailedLoginMapper;

    /**
     * 查询失败登录
     *
     * @param failedId 失败登录主键
     * @return 失败登录
     */
    @Override
    public DataiSfFailedLogin selectDataiSfFailedLoginByFailedId(Long failedId)
    {
        return dataiSfFailedLoginMapper.selectDataiSfFailedLoginByFailedId(failedId);
    }

    /**
     * 查询失败登录列表
     *
     * @param dataiSfFailedLogin 失败登录
     * @return 失败登录
     */
    @Override
    public List<DataiSfFailedLogin> selectDataiSfFailedLoginList(DataiSfFailedLogin dataiSfFailedLogin)
    {
        return dataiSfFailedLoginMapper.selectDataiSfFailedLoginList(dataiSfFailedLogin);
    }

    /**
     * 新增失败登录
     *
     * @param dataiSfFailedLogin 失败登录
     * @return 结果
     */
    @Override
    public int insertDataiSfFailedLogin(DataiSfFailedLogin dataiSfFailedLogin)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiSfFailedLogin.setCreateTime(DateUtils.getNowDate());
                dataiSfFailedLogin.setUpdateTime(DateUtils.getNowDate());
                dataiSfFailedLogin.setCreateBy(username);
                dataiSfFailedLogin.setUpdateBy(username);
            return dataiSfFailedLoginMapper.insertDataiSfFailedLogin(dataiSfFailedLogin);
    }

    /**
     * 修改失败登录
     *
     * @param dataiSfFailedLogin 失败登录
     * @return 结果
     */
    @Override
    public int updateDataiSfFailedLogin(DataiSfFailedLogin dataiSfFailedLogin)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

        return dataiSfFailedLoginMapper.updateDataiSfFailedLogin(dataiSfFailedLogin);
    }

    /**
     * 批量删除失败登录
     *
     * @param failedIds 需要删除的失败登录主键
     * @return 结果
     */
    @Override
    public int deleteDataiSfFailedLoginByFailedIds(Long[] failedIds)
    {
        return dataiSfFailedLoginMapper.deleteDataiSfFailedLoginByFailedIds(failedIds);
    }

    /**
     * 删除失败登录信息
     *
     * @param failedId 失败登录主键
     * @return 结果
     */
    @Override
    public int deleteDataiSfFailedLoginByFailedId(Long failedId)
    {
        return dataiSfFailedLoginMapper.deleteDataiSfFailedLoginByFailedId(failedId);
    }
}
