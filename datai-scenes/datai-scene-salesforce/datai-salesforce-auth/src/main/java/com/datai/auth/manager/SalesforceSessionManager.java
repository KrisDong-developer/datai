package com.datai.auth.manager;

import com.datai.auth.cache.SalesforceAuthCacheManager;
import com.datai.auth.domain.SalesforceLoginResult;
import com.datai.auth.service.ISalesforceLoginService;
import com.datai.common.utils.CacheUtils;
import com.datai.salesforce.common.exception.NotLoggedInException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Salesforce会话管理器
 * 负责管理Salesforce登录会话信息，提供统一的会话获取和管理接口
 *
 * @author datai
 * @date 2025-12-23
 */
@Component
public class SalesforceSessionManager {

    private static final Logger logger = LoggerFactory.getLogger(SalesforceSessionManager.class);

    @Autowired
    private SalesforceAuthCacheManager authCacheManager;

    @Autowired
    private ISalesforceLoginService salesforceLoginService;

    /**
     * 缓存名称
     */
    private static final String CACHE_NAME = "salesforce_auth_cache";

    /**
     * 当前登录会话的缓存键
     */
    private static final String CURRENT_SESSION_KEY = "current_salesforce_session";

    /**
     * 获取当前登录会话信息
     *
     * @return 当前登录会话
     * @throws NotLoggedInException 如果没有活跃的登录会话
     */
    public SalesforceLoginResult getCurrentSession() throws NotLoggedInException {
        // 先从缓存中获取
        SalesforceLoginResult result = CacheUtils.get(CACHE_NAME, CURRENT_SESSION_KEY, SalesforceLoginResult.class);
        
        if (result != null) {
            logger.debug("从缓存获取当前登录会话成功，访问令牌前缀: {}", 
                result.getAccessToken() != null ? result.getAccessToken().substring(0, Math.min(10, result.getAccessToken().length())) : "null");
            
            // 检查令牌是否已过期
            if (!result.isTokenExpired()) {
                return result;
            } else {
                logger.warn("当前登录会话已过期，需要重新获取");
                // 清除过期会话
                clearCurrentSession();
            }
        }
        
        // 缓存中没有或已过期，从登录服务获取
        logger.info("从登录服务获取当前登录会话");
        result = salesforceLoginService.getCurrentLoginStatus();
        
        if (result != null && result.isSuccess()) {
            // 缓存获取到的会话
            CacheUtils.put(CACHE_NAME, CURRENT_SESSION_KEY, result);
            logger.info("缓存当前登录会话成功，访问令牌前缀: {}", 
                result.getAccessToken() != null ? result.getAccessToken().substring(0, Math.min(10, result.getAccessToken().length())) : "null");
            return result;
        } else {
            logger.error("获取当前登录会话失败，错误信息: {}", result != null ? result.getErrorMessage() : "未知错误");
            throw new NotLoggedInException("没有活跃的Salesforce登录会话");
        }
    }

    /**
     * 设置当前登录会话
     *
     * @param result 登录结果
     */
    public void setCurrentSession(SalesforceLoginResult result) {
        if (result != null && result.isSuccess()) {
            CacheUtils.put(CACHE_NAME, CURRENT_SESSION_KEY, result);
            logger.info("设置当前登录会话成功，访问令牌前缀: {}", 
                result.getAccessToken() != null ? result.getAccessToken().substring(0, Math.min(10, result.getAccessToken().length())) : "null");
        } else {
            logger.warn("设置当前登录会话失败，登录结果无效: {}", result);
        }
    }

    /**
     * 清除当前登录会话
     */
    public void clearCurrentSession() {
        CacheUtils.remove(CACHE_NAME, CURRENT_SESSION_KEY);
        logger.info("清除当前登录会话成功");
    }

    /**
     * 获取当前会话的访问令牌
     *
     * @return 访问令牌
     * @throws NotLoggedInException 如果没有活跃的登录会话
     */
    public String getCurrentAccessToken() throws NotLoggedInException {
        SalesforceLoginResult result = getCurrentSession();
        if (result.getAccessToken() == null) {
            throw new NotLoggedInException("当前会话没有访问令牌");
        }
        return result.getAccessToken();
    }

    /**
     * 获取当前会话的实例URL
     *
     * @return 实例URL
     * @throws NotLoggedInException 如果没有活跃的登录会话
     */
    public String getCurrentInstanceUrl() throws NotLoggedInException {
        SalesforceLoginResult result = getCurrentSession();
        if (result.getInstanceUrl() == null) {
            throw new NotLoggedInException("当前会话没有实例URL");
        }
        return result.getInstanceUrl();
    }

    /**
     * 检查是否有活跃的登录会话
     *
     * @return 是否有活跃的登录会话
     */
    public boolean hasActiveSession() {
        try {
            SalesforceLoginResult result = getCurrentSession();
            return result != null && result.isSuccess() && !result.isTokenExpired();
        } catch (NotLoggedInException e) {
            return false;
        }
    }
}
