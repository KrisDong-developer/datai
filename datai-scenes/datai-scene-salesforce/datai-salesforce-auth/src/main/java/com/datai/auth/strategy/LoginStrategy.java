package com.datai.auth.strategy;

import com.datai.auth.model.domain.SalesforceLoginResult;
import com.datai.auth.model.domain.SalesforceLoginRequest;

/**
 * 登录策略接口，定义不同登录方式的通用方法
 * 
 * @author datai
 * @date 2025-12-14
 */
public interface LoginStrategy {
    
    /**
     * 执行登录操作
     * 
     * @param request 登录请求
     * @return 登录结果
     */
    SalesforceLoginResult login(SalesforceLoginRequest request);
    
    /**
     * 刷新Session
     * 
     * @param refreshToken 刷新Session
     * @param loginType 登录类型
     * @return 新的登录结果
     */
    SalesforceLoginResult refreshToken(String refreshToken, String loginType);
    
    /**
     * 执行登出操作
     * 
     * @param sessionId Session ID
     * @param loginType 登录类型
     * @return 登出是否成功
     */
    boolean logout(String sessionId, String loginType);
    
    /**
     * 获取登录类型
     * 
     * @return 登录类型
     */
    String getLoginType();
}