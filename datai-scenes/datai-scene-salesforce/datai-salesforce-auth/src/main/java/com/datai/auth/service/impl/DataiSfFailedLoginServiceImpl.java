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
 * @date 2025-12-24
 */
@Service
public class DataiSfFailedLoginServiceImpl implements IDataiSfFailedLoginService {
    @Autowired
    private DataiSfFailedLoginMapper dataiSfFailedLoginMapper;

    /**
     * 查询失败登录
     *
     * @param id 失败登录主键
     * @return 失败登录
     */
    @Override
    public DataiSfFailedLogin selectDataiSfFailedLoginById(Long id)
    {
        return dataiSfFailedLoginMapper.selectDataiSfFailedLoginById(id);
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

                dataiSfFailedLogin.setUpdateTime(DateUtils.getNowDate());
                dataiSfFailedLogin.setUpdateBy(username);
        return dataiSfFailedLoginMapper.updateDataiSfFailedLogin(dataiSfFailedLogin);
    }

    /**
     * 批量删除失败登录
     *
     * @param ids 需要删除的失败登录主键
     * @return 结果
     */
    @Override
    public int deleteDataiSfFailedLoginByIds(Long[] ids)
    {
        return dataiSfFailedLoginMapper.deleteDataiSfFailedLoginByIds(ids);
    }

    /**
     * 删除失败登录信息
     *
     * @param id 失败登录主键
     * @return 结果
     */
    @Override
    public int deleteDataiSfFailedLoginById(Long id)
    {
        return dataiSfFailedLoginMapper.deleteDataiSfFailedLoginById(id);
    }
}
