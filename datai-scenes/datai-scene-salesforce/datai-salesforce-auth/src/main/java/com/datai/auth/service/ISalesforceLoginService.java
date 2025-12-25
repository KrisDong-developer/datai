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
     * 根据不同登录类型执行登录
     * 
     * @param request 登录请求
     * @return 登录结果
     */
    SalesforceLoginResult login(SalesforceLoginRequest request);

    /**
     * 刷新访问令牌
     * 
     * @param refreshToken 刷新令牌
     * @param loginType 登录类型
     * @return 新的登录结果
     */
    SalesforceLoginResult refreshToken(String refreshToken, String loginType);

    /**
     * 执行登出操作
     * 
     * @param sessionId 会话ID
     * @param loginType 登录类型
     * @return 登出是否成功
     */
    boolean logout(String sessionId, String loginType);

    /**
     * 获取当前登录状态
     * 
     * @return 当前登录状态
     */
    SalesforceLoginResult getCurrentLoginStatus();

    /**
     * 保存登录状态到缓存
     * 
     * @param result 登录结果
     */
    void saveLoginStatus(SalesforceLoginResult result);

    /**
     * 获取当前登录会话信息
     * 
     * @return 当前登录会话
     */
    DataiSfLoginSession getCurrentLoginSession();
    
    /**
     * 自动登录，使用上一次成功登录的参数
     * 
     * @return 登录结果
     */
    SalesforceLoginResult autoLogin();
}