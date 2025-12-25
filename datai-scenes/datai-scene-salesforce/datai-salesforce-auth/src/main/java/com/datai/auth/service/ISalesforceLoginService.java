package com.datai.auth.service;

import com.datai.auth.domain.DataiSfLoginSession;
import com.datai.auth.domain.SalesforceLoginResult;
import com.datai.auth.domain.SalesforceLoginRequest;

/**
 * Salesforce登录服务接口
 * 定义登录相关的核心功能
 * 
 * @author datai
 * @date 2025-12-14
 */
public interface ISalesforceLoginService {

    /**
     * 执行登录操作
     * 
     * @param request 登录请求
     * @return 登录结果
     */
    SalesforceLoginResult login(SalesforceLoginRequest request);

    /**
     * 执行登出操作
     * 
     * @param sessionId 会话ID
     * @param loginType 登录类型
     * @return 登出是否成功
     */
    boolean logout(String sessionId, String loginType);

    /**
     * 获取当前登录信息
     * 
     * @param sessionId 会话ID
     * @return 登录会话信息
     */
    DataiSfLoginSession getCurrentLoginInfo(String sessionId);

    /**
     * 自动登录
     * 
     * @param historyId 登录历史ID
     * @return 登录结果
     */
    SalesforceLoginResult autoLogin(Long historyId);
}