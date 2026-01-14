package com.datai.integration.core;

import com.datai.auth.model.domain.DataiSfLoginHistory;
import com.datai.auth.service.IDataiSfLoginHistoryService;
import com.datai.common.core.domain.AjaxResult;
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

    @Autowired
    private IDataiSfLoginHistoryService dataiSfLoginHistoryService;

    /**
     * 获取当前Salesforce会话信息
     * 如果会话无效，则自动尝试重新登录
     *
     * @param orgType ORG类型（source/target）
     * @return SalesforceLoginResult 会话信息
     * @throws RuntimeException 如果获取会话信息失败
     */
    public SalesforceLoginResult getCurrentLoginResult(String orgType) {
        log.info("获取当前Salesforce会话信息，ORG类型: {}", orgType);

        try {
            SalesforceLoginResult loginResult = salesforceLoginService.getCurrentLoginResultByOrgType(orgType);

            if (isLoginResultValid(loginResult)) {
                log.info("获取会话信息成功，ORG类型: {}, 访问令牌前缀: {}", orgType,
                        loginResult.getSessionId() != null ? loginResult.getSessionId().substring(0, Math.min(10, loginResult.getSessionId().length())) : "null");
                return loginResult;
            }

            logSessionStatus(loginResult, orgType);
            return autoLogin(orgType);

        } catch (Exception e) {
            log.error("获取会话信息失败，ORG类型: {}, 错误: {}", orgType, e.getMessage(), e);
            throw new RuntimeException("获取会话信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 自动重新登录
     *
     * @param orgType ORG类型（source/target）
     * @return SalesforceLoginResult 登录结果
     * @throws RuntimeException 如果自动登录失败
     */
    private SalesforceLoginResult autoLogin(String orgType) {
        log.info("开始执行自动重新登录，ORG类型: {}", orgType);

        try {
            DataiSfLoginHistory latestLoginHistory = dataiSfLoginHistoryService.selectLatestSuccessLoginHistoryByOrgType(orgType);

            if (latestLoginHistory == null) {
                throw new RuntimeException("未找到登录历史记录，ORG类型: " + orgType);
            }

            Long historyId = latestLoginHistory.getId();

            SalesforceLoginResult result = salesforceLoginService.autoLogin(historyId);

            if (result == null || !result.isSuccess()) {
                String errorMsg = result != null ? result.getErrorMessage() : "登录结果为空";
                log.error("自动重新登录失败，ORG类型: {}, 错误: {}", orgType, errorMsg);
                throw new RuntimeException("自动重新登录失败: " + errorMsg);
            }

            log.info("自动重新登录成功，ORG类型: {}, Session ID前缀: {}", orgType,
                    result.getSessionId() != null ? result.getSessionId().substring(0, Math.min(10, result.getSessionId().length())) : "null");
            return result;

        } catch (Exception e) {
            log.error("自动重新登录异常，ORG类型: {}, 错误: {}", orgType, e.getMessage(), e);
            throw new RuntimeException("自动重新登录失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取SessionId
     *
     * @param orgType ORG类型（source/target）
     * @return 访问令牌
     * @throws RuntimeException 如果获取访问SessionId
     */
    public String getCurrentSession(String orgType) {
        SalesforceLoginResult result = getCurrentLoginResult(orgType);
        if (result == null || result.getSessionId() == null) {
            throw new RuntimeException("获取访问令牌失败: 没有有效的会话信息，ORG类型: " + orgType);
        }
        return result.getSessionId();
    }

    /**
     * 获取实例URL
     *
     * @param orgType ORG类型（source/target）
     * @return 实例URL
     * @throws RuntimeException 如果获取实例URL失败
     */
    public String getInstanceUrl(String orgType) {
        SalesforceLoginResult result = getCurrentLoginResult(orgType);
        if (result == null || result.getInstanceUrl() == null) {
            throw new RuntimeException("获取实例URL失败: 没有有效的会话信息，ORG类型: " + orgType);
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
     * @param orgType ORG类型（source/target）
     * @return 如果会话有效返回true，否则返回false
     */
    public boolean isSessionValid(String orgType) {
        log.info("检查当前Salesforce会话信息是否有效，ORG类型: {}", orgType);

        try {
            SalesforceLoginResult loginResult = getCurrentLoginResult(orgType);
            return isLoginResultValid(loginResult);

        } catch (Exception e) {
            log.error("检查会话信息有效性时发生异常，ORG类型: {}, 错误: {}", orgType, e.getMessage(), e);
            return false;
        }
    }

    /**
     * 判断登录结果是否有效
     *
     * @param loginResult 登录结果
     * @return 如果有效返回true，否则返回false
     */
    private boolean isLoginResultValid(SalesforceLoginResult loginResult) {
        return loginResult != null 
                && loginResult.isSuccess() 
                && !loginResult.isSessionExpired();
    }

    /**
     * 记录会话状态日志
     *
     * @param loginResult 登录结果
     * @param orgType ORG类型（source/target）
     */
    private void logSessionStatus(SalesforceLoginResult loginResult, String orgType) {
        if (loginResult == null) {
            log.warn("会话信息为null，ORG类型: {}，尝试自动重新登录", orgType);
        } else if (!loginResult.isSuccess()) {
            log.warn("登录状态为失败，ORG类型: {}，尝试自动重新登录，错误信息: {}", orgType, loginResult.getErrorMessage());
        } else if (loginResult.isSessionExpired()) {
            log.warn("Session已过期，ORG类型: {}，尝试自动重新登录", orgType);
        }
    }
}