package com.datai.auth.service.impl;

import java.util.List;
        import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.auth.mapper.DataiSfLoginSessionMapper;
import com.datai.auth.model.domain.DataiSfLoginSession;
import com.datai.auth.service.IDataiSfLoginSessionService;
import com.datai.common.core.domain.model.LoginUser;

/**
 * 登录会话信息Service业务层处理
 *
 * @author datai
 * @date 2025-12-25
 */
@Service
public class DataiSfLoginSessionServiceImpl implements IDataiSfLoginSessionService {
    @Autowired
    private DataiSfLoginSessionMapper dataiSfLoginSessionMapper;

    /**
     * 查询登录会话信息
     *
     * @param id 登录会话信息主键
     * @return 登录会话信息
     */
    @Override
    public DataiSfLoginSession selectDataiSfLoginSessionById(Long id)
    {
        return dataiSfLoginSessionMapper.selectDataiSfLoginSessionById(id);
    }

    @Override
    public DataiSfLoginSession selectDataiSfLoginSessionBySessionId(String sessionId)
    {
        return dataiSfLoginSessionMapper.selectDataiSfLoginSessionBySessionId(sessionId);
    }

    /**
     * 查询登录会话信息列表
     *
     * @param dataiSfLoginSession 登录会话信息
     * @return 登录会话信息
     */
    @Override
    public List<DataiSfLoginSession> selectDataiSfLoginSessionList(DataiSfLoginSession dataiSfLoginSession)
    {
        return dataiSfLoginSessionMapper.selectDataiSfLoginSessionList(dataiSfLoginSession);
    }

    /**
     * 新增登录会话信息
     *
     * @param dataiSfLoginSession 登录会话信息
     * @return 结果
     */
    @Override
    public int insertDataiSfLoginSession(DataiSfLoginSession dataiSfLoginSession)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiSfLoginSession.setCreateTime(DateUtils.getNowDate());
                dataiSfLoginSession.setUpdateTime(DateUtils.getNowDate());
                dataiSfLoginSession.setCreateBy(username);
                dataiSfLoginSession.setUpdateBy(username);
            return dataiSfLoginSessionMapper.insertDataiSfLoginSession(dataiSfLoginSession);
    }

    /**
     * 修改登录会话信息
     *
     * @param dataiSfLoginSession 登录会话信息
     * @return 结果
     */
    @Override
    public int updateDataiSfLoginSession(DataiSfLoginSession dataiSfLoginSession)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiSfLoginSession.setUpdateTime(DateUtils.getNowDate());
                dataiSfLoginSession.setUpdateBy(username);
        return dataiSfLoginSessionMapper.updateDataiSfLoginSession(dataiSfLoginSession);
    }

    /**
     * 批量删除登录会话信息
     *
     * @param ids 需要删除的登录会话信息主键
     * @return 结果
     */
    @Override
    public int deleteDataiSfLoginSessionByIds(Long[] ids)
    {
        return dataiSfLoginSessionMapper.deleteDataiSfLoginSessionByIds(ids);
    }

    /**
     * 删除登录会话信息信息
     *
     * @param id 登录会话信息主键
     * @return 结果
     */
    @Override
    public int deleteDataiSfLoginSessionById(Long id)
    {
        return dataiSfLoginSessionMapper.deleteDataiSfLoginSessionById(id);
    }
}
