package com.datai.integration.core;

import com.datai.salesforce.common.constant.SalesforceConfigConstants;
import com.datai.auth.model.domain.SalesforceLoginResult;
import com.datai.auth.service.ISalesforceLoginService;
import com.datai.common.utils.CacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * 会话管理类 - 用于获取Salesforce会话信息
 * 支持会话失效时自动重新登录
 *
 * @author datai
 */
@Slf4j
@Component
public class SessionManager {

    @Autowired
    private ISalesforceLoginService salesforceLoginService;

    /**
     * 获取当前Salesforce会话信息
     * 如果会话无效，则自动尝试重新登录
     *
     * @return SalesforceLoginResult 会话信息
     * @throws RuntimeException 如果获取会话信息失败
     */
    public SalesforceLoginResult getCurrentLoginResult() {
        log.info("获取当前Salesforce会话信息");

        try {
            SalesforceLoginResult loginResult = CacheUtils.get(SalesforceConfigConstants.CACHE_NAME, SalesforceConfigConstants.CURRENT_RESULT, SalesforceLoginResult.class);

            if (loginResult != null && loginResult.isSuccess() && !loginResult.isSessionExpired()) {
                log.info("获取会话信息成功，访问令牌前缀: {}", 
                        loginResult.getSessionId() != null ? loginResult.getSessionId().substring(0, Math.min(10, loginResult.getSessionId().length())) : "null");
                return loginResult;
            }

            if (loginResult == null) {
                log.warn("会话信息为null，尝试自动重新登录");
            } else if (!loginResult.isSuccess()) {
                log.warn("登录状态为失败，尝试自动重新登录，错误信息: {}", loginResult.getErrorMessage());
            } else if (loginResult.isSessionExpired()) {
                log.warn("Session已过期，尝试自动重新登录");
            }

            return autoLogin();

        } catch (Exception e) {
            log.error("获取会话信息失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取会话信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 自动重新登录
     *
     * @return SalesforceLoginResult 登录结果
     * @throws RuntimeException 如果自动登录失败
     */
    private SalesforceLoginResult autoLogin() {
        log.info("开始执行自动重新登录");

        try {
            SalesforceLoginResult result = salesforceLoginService.autoLogin(null);

            if (result == null || !result.isSuccess()) {
                String errorMsg = result != null ? result.getErrorMessage() : "登录结果为空";
                log.error("自动重新登录失败: {}", errorMsg);
                throw new RuntimeException("自动重新登录失败: " + errorMsg);
            }

            log.info("自动重新登录成功，Session ID前缀: {}", 
                    result.getSessionId() != null ? result.getSessionId().substring(0, Math.min(10, result.getSessionId().length())) : "null");
            return result;

        } catch (Exception e) {
            log.error("自动重新登录异常: {}", e.getMessage(), e);
            throw new RuntimeException("自动重新登录失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取SessionId
     *
     * @return 访问令牌
     * @throws RuntimeException 如果获取访问SessionId
     */
    public String getCurrentSession() {
        SalesforceLoginResult result = getCurrentLoginResult();
        if (result == null || result.getSessionId() == null) {
            throw new RuntimeException("获取访问令牌失败: 没有有效的会话信息");
        }
        return result.getSessionId();
    }

    /**
     * 获取实例URL
     *
     * @return 实例URL
     * @throws RuntimeException 如果获取实例URL失败
     */
    public String getInstanceUrl() {
        SalesforceLoginResult result = getCurrentLoginResult();
        if (result == null || result.getInstanceUrl() == null) {
            throw new RuntimeException("获取实例URL失败: 没有有效的会话信息");
        }
        return result.getInstanceUrl();
    }

    /**
     * 判断当前会话信息是否有效
     * 检查条件：
     * 1. 会话信息不为null
     * 2. 登录状态为成功
     * 3. Session ID不为空
     * 4. Session未过期
     *
     * @return 如果会话有效返回true，否则返回false
     */
    public boolean isSessionValid() {
        log.info("检查当前Salesforce会话信息是否有效");

        try {
            SalesforceLoginResult loginResult = CacheUtils.get(SalesforceConfigConstants.CACHE_NAME, SalesforceConfigConstants.CURRENT_RESULT, SalesforceLoginResult.class);

            if (loginResult == null) {
                log.warn("会话信息为null，会话无效");
                return false;
            }

            if (!loginResult.isSuccess()) {
                log.warn("登录状态为失败，会话无效");
                return false;
            }

            if (loginResult.getSessionId() == null || loginResult.getSessionId().trim().isEmpty()) {
                log.warn("Session ID为空，会话无效");
                return false;
            }

            if (loginResult.isSessionExpired()) {
                log.warn("Session已过期，会话无效");
                return false;
            }

            log.info("会话信息有效，Session ID前缀: {}", 
                    loginResult.getSessionId() != null ? 
                            loginResult.getSessionId().substring(0, Math.min(10, loginResult.getSessionId().length())) : "null");
            return true;

        } catch (Exception e) {
            log.error("检查会话信息有效性时发生异常: {}", e.getMessage(), e);
            return false;
        }
    }
}