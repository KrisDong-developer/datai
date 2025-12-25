package com.datai.auth.mapper;

import java.util.List;
import com.datai.auth.domain.DataiSfFailedLogin;

/**
 * 失败登录Mapper接口
 * 
 * @author datai
 * @date 2025-12-24
 */
public interface DataiSfFailedLoginMapper 
{
    /**
     * 查询失败登录
     * 
     * @param id 失败登录主键
     * @return 失败登录
     */
    public DataiSfFailedLogin selectDataiSfFailedLoginById(Long id);

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
     * @param id 失败登录主键
     * @return 结果
     */
    public int deleteDataiSfFailedLoginById(Long id);

    /**
     * 批量删除失败登录
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataiSfFailedLoginByIds(Long[] ids);
}
