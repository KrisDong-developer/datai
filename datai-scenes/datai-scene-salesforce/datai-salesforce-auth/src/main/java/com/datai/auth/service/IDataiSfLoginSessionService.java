package com.datai.auth.service;

import java.util.List;
import com.datai.auth.domain.DataiSfLoginSession;

/**
 * 登录会话Service接口
 * 
 * @author datai
 * @date 2025-12-14
 */
public interface IDataiSfLoginSessionService 
{
    /**
     * 查询登录会话
     * 
     * @param sessionId 登录会话主键
     * @return 登录会话
     */
    public DataiSfLoginSession selectDataiSfLoginSessionBySessionId(Long sessionId);

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
     * 批量删除登录会话
     * 
     * @param sessionIds 需要删除的登录会话主键集合
     * @return 结果
     */
    public int deleteDataiSfLoginSessionBySessionIds(Long[] sessionIds);

    /**
     * 删除登录会话信息
     * 
     * @param sessionId 登录会话主键
     * @return 结果
     */
    public int deleteDataiSfLoginSessionBySessionId(Long sessionId);
}
