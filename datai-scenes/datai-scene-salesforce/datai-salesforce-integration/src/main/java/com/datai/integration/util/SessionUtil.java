package com.datai.integration.util;

import com.datai.salesforce.common.constant.SalesforceConfigConstants;
import com.datai.auth.domain.SalesforceLoginResult;
import com.datai.common.utils.CacheUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

/**
 * 会话工具类 - 用于获取Salesforce会话信息
 * 直接调用auth模块的方法获取当前登录状态
 *
 * @author datai
 */
@Slf4j
@Component
public class SessionUtil {


    /**
     * 获取当前Salesforce会话信息
     *
     * @return SalesforceLoginResult 会话信息
     * @throws RuntimeException 如果获取会话信息失败
     */
    public static SalesforceLoginResult getCurrentLoginResult() {
        log.info("获取当前Salesforce会话信息");

        try {
            // 尝试从缓存获取登录结果
            SalesforceLoginResult loginResult = (SalesforceLoginResult) CacheUtils.get(SalesforceConfigConstants.CACHE_NAME, SalesforceConfigConstants.CURRENT_RESULT);

            if (loginResult != null && loginResult.isSuccess()) {
                log.info("获取会话信息成功，访问令牌前缀: {}", 
                        loginResult.getSessionId() != null ? loginResult.getSessionId() : "null");
                return loginResult;
            } else {
                String errorMsg = loginResult != null ? loginResult.getErrorMessage() : "未知错误";
                log.error("获取会话信息失败: {}", errorMsg);
                throw new RuntimeException("获取会话信息失败: " + errorMsg);
            }
        } catch (Exception e) {
            log.error("获取会话信息失败: {}", e.getMessage(), e);
            throw new RuntimeException("获取会话信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取SessionId
     *
     * @return 访问令牌
     * @throws RuntimeException 如果获取访问SessionId
     */
    public static String getCurrentSession() {
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
    public static String getInstanceUrl() {
        SalesforceLoginResult result = getCurrentLoginResult();
        if (result == null || result.getInstanceUrl() == null) {
            throw new RuntimeException("获取实例URL失败: 没有有效的会话信息");
        }
        return result.getInstanceUrl();
    }
}