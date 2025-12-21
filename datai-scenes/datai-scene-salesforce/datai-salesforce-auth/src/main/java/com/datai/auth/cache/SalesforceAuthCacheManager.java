package com.datai.auth.cache;

import com.datai.auth.domain.SalesforceLoginResult;
import com.datai.common.utils.CacheUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * Salesforce认证缓存管理器
 * 负责管理Salesforce登录相关的缓存操作
 */
@Component
public class SalesforceAuthCacheManager {
    
    private static final Logger logger = LoggerFactory.getLogger(SalesforceAuthCacheManager.class);
    
    // 缓存名称
    private static final String CACHE_NAME = "salesforce_auth_cache";
    
    // 不同数据类型的缓存过期时间（秒）
    private static final long ACCESS_TOKEN_EXPIRE = 7200; // 2小时
    private static final long REFRESH_TOKEN_EXPIRE = 2592000; // 30天
    private static final long LOGIN_RESULT_EXPIRE = 7200; // 2小时
    private static final long SESSION_INFO_EXPIRE = 7200; // 2小时
    
    /**
     * 缓存登录结果
     * 
     * @param accessToken 访问令牌
     * @param result 登录结果
     */
    public void cacheLoginResult(String accessToken, SalesforceLoginResult result) {
        if (accessToken == null || result == null) {
            logger.warn("缓存登录结果失败，参数为空");
            return;
        }
        
        try {
            String key = "login_result:" + accessToken;
            CacheUtils.put(CACHE_NAME, key, result, LOGIN_RESULT_EXPIRE, TimeUnit.SECONDS);
            logger.debug("登录结果缓存成功，访问令牌前缀: {}", accessToken.substring(0, Math.min(10, accessToken.length())));
        } catch (Exception e) {
            logger.error("缓存登录结果失败，访问令牌前缀: {}", accessToken.substring(0, Math.min(10, accessToken.length())), e);
        }
    }
    
    /**
     * 获取缓存的登录结果
     * 
     * @param accessToken 访问令牌
     * @return 登录结果
     */
    public SalesforceLoginResult getLoginResult(String accessToken) {
        if (accessToken == null) {
            return null;
        }
        
        try {
            String key = "login_result:" + accessToken;
            SalesforceLoginResult result = CacheUtils.get(CACHE_NAME, key, SalesforceLoginResult.class);
            if (result != null) {
                logger.debug("获取缓存登录结果成功，访问令牌前缀: {}", accessToken.substring(0, Math.min(10, accessToken.length())));
            }
            return result;
        } catch (Exception e) {
            logger.error("获取缓存登录结果失败，访问令牌前缀: {}", accessToken.substring(0, Math.min(10, accessToken.length())), e);
            return null;
        }
    }
    
    /**
     * 缓存访问令牌相关信息
     * 
     * @param accessToken 访问令牌
     * @param result 登录结果
     */
    public void cacheAccessTokenInfo(String accessToken, SalesforceLoginResult result) {
        if (accessToken == null || result == null) {
            logger.warn("缓存访问令牌信息失败，参数为空");
            return;
        }
        
        try {
            // 缓存用户ID
            if (result.getUserId() != null) {
                String userIdKey = "user_id:" + accessToken;
                CacheUtils.put(CACHE_NAME, userIdKey, result.getUserId(), ACCESS_TOKEN_EXPIRE, TimeUnit.SECONDS);
            }
            
            // 缓存组织ID
            if (result.getOrganizationId() != null) {
                String orgIdKey = "org_id:" + accessToken;
                CacheUtils.put(CACHE_NAME, orgIdKey, result.getOrganizationId(), ACCESS_TOKEN_EXPIRE, TimeUnit.SECONDS);
            }
            
            // 缓存实例URL
            if (result.getInstanceUrl() != null) {
                String instanceUrlKey = "instance_url:" + accessToken;
                CacheUtils.put(CACHE_NAME, instanceUrlKey, result.getInstanceUrl(), ACCESS_TOKEN_EXPIRE, TimeUnit.SECONDS);
            }
            
            logger.debug("访问令牌信息缓存成功，访问令牌前缀: {}", accessToken.substring(0, Math.min(10, accessToken.length())));
        } catch (Exception e) {
            logger.error("缓存访问令牌信息失败，访问令牌前缀: {}", accessToken.substring(0, Math.min(10, accessToken.length())), e);
        }
    }
    
    /**
     * 获取缓存的用户ID
     * 
     * @param accessToken 访问令牌
     * @return 用户ID
     */
    public String getCachedUserId(String accessToken) {
        if (accessToken == null) {
            return null;
        }
        
        try {
            String key = "user_id:" + accessToken;
            String userId = CacheUtils.get(CACHE_NAME, key, String.class);
            if (userId != null) {
                logger.debug("获取缓存用户ID成功，访问令牌前缀: {}", accessToken.substring(0, Math.min(10, accessToken.length())));
            }
            return userId;
        } catch (Exception e) {
            logger.error("获取缓存用户ID失败，访问令牌前缀: {}", accessToken.substring(0, Math.min(10, accessToken.length())), e);
            return null;
        }
    }
    
    /**
     * 获取缓存的组织ID
     * 
     * @param accessToken 访问令牌
     * @return 组织ID
     */
    public String getCachedOrgId(String accessToken) {
        if (accessToken == null) {
            return null;
        }
        
        try {
            String key = "org_id:" + accessToken;
            String orgId = CacheUtils.get(CACHE_NAME, key, String.class);
            if (orgId != null) {
                logger.debug("获取缓存组织ID成功，访问令牌前缀: {}", accessToken.substring(0, Math.min(10, accessToken.length())));
            }
            return orgId;
        } catch (Exception e) {
            logger.error("获取缓存组织ID失败，访问令牌前缀: {}", accessToken.substring(0, Math.min(10, accessToken.length())), e);
            return null;
        }
    }
    
    /**
     * 获取缓存的实例URL
     * 
     * @param accessToken 访问令牌
     * @return 实例URL
     */
    public String getCachedInstanceUrl(String accessToken) {
        if (accessToken == null) {
            return null;
        }
        
        try {
            String key = "instance_url:" + accessToken;
            String instanceUrl = CacheUtils.get(CACHE_NAME, key, String.class);
            if (instanceUrl != null) {
                logger.debug("获取缓存实例URL成功，访问令牌前缀: {}", accessToken.substring(0, Math.min(10, accessToken.length())));
            }
            return instanceUrl;
        } catch (Exception e) {
            logger.error("获取缓存实例URL失败，访问令牌前缀: {}", accessToken.substring(0, Math.min(10, accessToken.length())), e);
            return null;
        }
    }
    
    /**
     * 缓存刷新令牌相关信息
     * 
     * @param refreshToken 刷新令牌
     * @param result 登录结果
     */
    public void cacheRefreshTokenInfo(String refreshToken, SalesforceLoginResult result) {
        if (refreshToken == null || result == null) {
            logger.warn("缓存刷新令牌信息失败，参数为空");
            return;
        }
        
        try {
            // 缓存刷新令牌对应的访问令牌
            if (result.getAccessToken() != null) {
                String accessTokenKey = "access_token_for_refresh:" + refreshToken;
                CacheUtils.put(CACHE_NAME, accessTokenKey, result.getAccessToken(), REFRESH_TOKEN_EXPIRE, TimeUnit.SECONDS);
            }
            
            logger.debug("刷新令牌信息缓存成功，刷新令牌前缀: {}", refreshToken.substring(0, Math.min(10, refreshToken.length())));
        } catch (Exception e) {
            logger.error("缓存刷新令牌信息失败，刷新令牌前缀: {}", refreshToken.substring(0, Math.min(10, refreshToken.length())), e);
        }
    }
    
    /**
     * 获取与刷新令牌关联的访问令牌
     * 
     * @param refreshToken 刷新令牌
     * @return 访问令牌
     */
    public String getAccessTokenForRefreshToken(String refreshToken) {
        if (refreshToken == null) {
            return null;
        }
        
        try {
            String key = "access_token_for_refresh:" + refreshToken;
            String accessToken = CacheUtils.get(CACHE_NAME, key, String.class);
            if (accessToken != null) {
                logger.debug("获取刷新令牌关联的访问令牌成功，刷新令牌前缀: {}", refreshToken.substring(0, Math.min(10, refreshToken.length())));
            }
            return accessToken;
        } catch (Exception e) {
            logger.error("获取刷新令牌关联的访问令牌失败，刷新令牌前缀: {}", refreshToken.substring(0, Math.min(10, refreshToken.length())), e);
            return null;
        }
    }
    
    /**
     * 缓存会话信息
     * 
     * @param sessionId 会话ID
     * @param sessionInfo 会话信息
     */
    public void cacheSessionInfo(String sessionId, Object sessionInfo) {
        if (sessionId == null || sessionInfo == null) {
            logger.warn("缓存会话信息失败，参数为空");
            return;
        }
        
        try {
            String key = "session_info:" + sessionId;
            CacheUtils.put(CACHE_NAME, key, sessionInfo, SESSION_INFO_EXPIRE, TimeUnit.SECONDS);
            logger.debug("会话信息缓存成功，会话ID: {}", sessionId);
        } catch (Exception e) {
            logger.error("缓存会话信息失败，会话ID: {}", sessionId, e);
        }
    }
    
    /**
     * 获取缓存的会话信息
     * 
     * @param sessionId 会话ID
     * @return 会话信息
     */
    public Object getSessionInfo(String sessionId) {
        if (sessionId == null) {
            return null;
        }
        
        try {
            String key = "session_info:" + sessionId;
            Object sessionInfo = CacheUtils.get(CACHE_NAME, key, Object.class);
            if (sessionInfo != null) {
                logger.debug("获取缓存会话信息成功，会话ID: {}", sessionId);
            }
            return sessionInfo;
        } catch (Exception e) {
            logger.error("获取缓存会话信息失败，会话ID: {}", sessionId, e);
            return null;
        }
    }
    
    /**
     * 清除指定访问令牌的缓存
     * 
     * @param accessToken 访问令牌
     */
    public void evictAccessTokenCache(String accessToken) {
        if (accessToken == null) {
            return;
        }
        
        try {
            // 清除登录结果缓存
            CacheUtils.remove(CACHE_NAME, "login_result:" + accessToken);
            
            // 清除访问令牌相关信息缓存
            CacheUtils.remove(CACHE_NAME, "user_id:" + accessToken);
            CacheUtils.remove(CACHE_NAME, "org_id:" + accessToken);
            CacheUtils.remove(CACHE_NAME, "instance_url:" + accessToken);
            
            logger.debug("清除访问令牌相关缓存成功，访问令牌前缀: {}", accessToken.substring(0, Math.min(10, accessToken.length())));
        } catch (Exception e) {
            logger.error("清除访问令牌相关缓存失败，访问令牌前缀: {}", accessToken.substring(0, Math.min(10, accessToken.length())), e);
        }
    }
    
    /**
     * 清除指定刷新令牌的缓存
     * 
     * @param refreshToken 刷新令牌
     */
    public void evictRefreshTokenCache(String refreshToken) {
        if (refreshToken == null) {
            return;
        }
        
        try {
            // 清除刷新令牌相关信息缓存
            CacheUtils.remove(CACHE_NAME, "access_token_for_refresh:" + refreshToken);
            
            logger.debug("清除刷新令牌相关缓存成功，刷新令牌前缀: {}", refreshToken.substring(0, Math.min(10, refreshToken.length())));
        } catch (Exception e) {
            logger.error("清除刷新令牌相关缓存失败，刷新令牌前缀: {}", refreshToken.substring(0, Math.min(10, refreshToken.length())), e);
        }
    }
    
    /**
     * 清除指定会话的缓存
     * 
     * @param sessionId 会话ID
     */
    public void evictSessionCache(String sessionId) {
        if (sessionId == null) {
            return;
        }
        
        try {
            // 清除会话信息缓存
            CacheUtils.remove(CACHE_NAME, "session_info:" + sessionId);
            
            logger.debug("清除会话相关缓存成功，会话ID: {}", sessionId);
        } catch (Exception e) {
            logger.error("清除会话相关缓存失败，会话ID: {}", sessionId, e);
        }
    }
    
    /**
     * 清除所有认证缓存
     */
    public void clearAllAuthCache() {
        try {
            CacheUtils.clear(CACHE_NAME);
            logger.info("清除所有认证缓存成功");
        } catch (Exception e) {
            logger.error("清除所有认证缓存失败", e);
        }
    }
}