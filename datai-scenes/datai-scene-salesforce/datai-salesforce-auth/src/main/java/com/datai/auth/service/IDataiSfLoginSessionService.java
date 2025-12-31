package com.datai.auth.service;

import java.util.List;
import com.datai.auth.model.domain.DataiSfLoginSession;

/**
 * 登录会话信息Service接口
 * 
 * @author datai
 * @date 2025-12-25
 */
public interface IDataiSfLoginSessionService 
{
    /**
     * 查询登录会话信息
     * 
     * @param id 登录会话信息主键
     * @return 登录会话信息
     */
    public DataiSfLoginSession selectDataiSfLoginSessionById(Long id);

    /**
     * 根据Session ID查询登录会话信息
     * 
     * @param sessionId Session ID
     * @return 登录会话信息
     */
    public DataiSfLoginSession selectDataiSfLoginSessionBySessionId(String sessionId);

    /**
     * 查询登录会话信息列表
     * 
     * @param dataiSfLoginSession 登录会话信息
     * @return 登录会话信息集合
     */
    public List<DataiSfLoginSession> selectDataiSfLoginSessionList(DataiSfLoginSession dataiSfLoginSession);

    /**
     * 新增登录会话信息
     * 
     * @param dataiSfLoginSession 登录会话信息
     * @return 结果
     */
    public int insertDataiSfLoginSession(DataiSfLoginSession dataiSfLoginSession);

    /**
     * 修改登录会话信息
     * 
     * @param dataiSfLoginSession 登录会话信息
     * @return 结果
     */
    public int updateDataiSfLoginSession(DataiSfLoginSession dataiSfLoginSession);

    /**
     * 批量删除登录会话信息
     * 
     * @param ids 需要删除的登录会话信息主键集合
     * @return 结果
     */
    public int deleteDataiSfLoginSessionByIds(Long[] ids);

    /**
     * 删除登录会话信息信息
     * 
     * @param id 登录会话信息主键
     * @return 结果
     */
    public int deleteDataiSfLoginSessionById(Long id);
}
