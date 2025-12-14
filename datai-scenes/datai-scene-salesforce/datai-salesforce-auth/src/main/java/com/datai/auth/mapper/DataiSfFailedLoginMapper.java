package com.datai.auth.mapper;

import java.util.List;
import com.datai.auth.domain.DataiSfFailedLogin;

/**
 * 失败登录Mapper接口
 * 
 * @author datai
 * @date 2025-12-14
 */
public interface DataiSfFailedLoginMapper 
{
    /**
     * 查询失败登录
     * 
     * @param failedId 失败登录主键
     * @return 失败登录
     */
    public DataiSfFailedLogin selectDataiSfFailedLoginByFailedId(Long failedId);

    /**
     * 查询失败登录列表
     * 
     * @param dataiSfFailedLogin 失败登录
     * @return 失败登录集合
     */
    public List<DataiSfFailedLogin> selectDataiSfFailedLoginList(DataiSfFailedLogin dataiSfFailedLogin);

    /**
     * 新增失败登录
     * 
     * @param dataiSfFailedLogin 失败登录
     * @return 结果
     */
    public int insertDataiSfFailedLogin(DataiSfFailedLogin dataiSfFailedLogin);

    /**
     * 修改失败登录
     * 
     * @param dataiSfFailedLogin 失败登录
     * @return 结果
     */
    public int updateDataiSfFailedLogin(DataiSfFailedLogin dataiSfFailedLogin);

    /**
     * 删除失败登录
     * 
     * @param failedId 失败登录主键
     * @return 结果
     */
    public int deleteDataiSfFailedLoginByFailedId(Long failedId);

    /**
     * 批量删除失败登录
     * 
     * @param failedIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataiSfFailedLoginByFailedIds(Long[] failedIds);
}
