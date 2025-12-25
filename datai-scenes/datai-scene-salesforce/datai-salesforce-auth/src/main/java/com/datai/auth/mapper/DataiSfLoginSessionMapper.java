package com.datai.auth.mapper;

import java.util.List;
import com.datai.auth.domain.DataiSfLoginSession;

/**
 * 登录会话Mapper接口
 * 
 * @author datai
 * @date 2025-12-24
 */
public interface DataiSfLoginSessionMapper 
{
    /**
     * 查询登录会话
     * 
     * @param id 登录会话主键
     * @return 登录会话
     */
    public DataiSfLoginSession selectDataiSfLoginSessionById(Long id);

    /**
     * 查询登录会话列表
     * 
     * @param dataiSfLoginSession 登录会话
     * @return 登录会话集合
     */
    public List<DataiSfLoginSession> selectDataiSfLoginSessionList(DataiSfLoginSession dataiSfLoginSession);

    /**
     * 新增登录会话
     * 
     * @param dataiSfLoginSession 登录会话
     * @return 结果
     */
    public int insertDataiSfLoginSession(DataiSfLoginSession dataiSfLoginSession);

    /**
     * 修改登录会话
     * 
     * @param dataiSfLoginSession 登录会话
     * @return 结果
     */
    public int updateDataiSfLoginSession(DataiSfLoginSession dataiSfLoginSession);

    /**
     * 删除登录会话
     * 
     * @param id 登录会话主键
     * @return 结果
     */
    public int deleteDataiSfLoginSessionById(Long id);

    /**
     * 批量删除登录会话
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataiSfLoginSessionByIds(Long[] ids);
}
