package com.datai.auth.service.impl;

import com.datai.auth.domain.SalesforceLoginResult;
import com.datai.auth.domain.SalesforceLoginRequest;
import com.datai.auth.strategy.LoginStrategyFactory;
import com.datai.auth.strategy.LoginStrategy;
import com.datai.auth.domain.DataiSfLoginSession;
import com.datai.auth.service.IDataiSfLoginSessionService;
import com.datai.auth.domain.DataiSfToken;
import com.datai.auth.service.IDataiSfTokenService;
import com.datai.auth.domain.DataiSfLoginAudit;
import com.datai.auth.service.IDataiSfLoginAuditService;
import com.datai.auth.domain.DataiSfFailedLogin;
import com.datai.auth.service.IDataiSfFailedLoginService;
import com.datai.auth.domain.DataiSfLoginStatistics;
import com.datai.auth.service.IDataiSfLoginStatisticsService;
import com.datai.auth.service.ISalesforceLoginService;
import com.datai.auth.cache.SalesforceAuthCacheManager;
import com.datai.common.utils.DateUtils;
import com.datai.common.utils.CacheUtils;
import com.datai.salesforce.common.exception.SalesforceAuthException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Salesforce登录服务实现
 * 实现登录认证的核心逻辑
 * 管理登录策略的调用
 * 处理登录状态的保存与获取
 * 记录登录审计日志
 * 
 * @author datai
 * @date 2025-12-14
 */
@Service
public class SalesforceLoginServiceImpl implements ISalesforceLoginService {
    
    private static final Logger logger = LoggerFactory.getLogger(SalesforceLoginServiceImpl.class);
    
    @Autowired
    private LoginStrategyFactory loginStrategyFactory;
    
    @Autowired
    private IDataiSfLoginSessionService loginSessionService;
    
    @Autowired
    private IDataiSfTokenService tokenService;
    
    @Autowired
    private IDataiSfLoginAuditService loginAuditService;
    
    @Autowired
    private IDataiSfFailedLoginService failedLoginService;
    
    @Autowired
    private IDataiSfLoginStatisticsService loginStatisticsService;
    
    @Autowired
    private SalesforceAuthCacheManager cacheManager;
    
    /**
     * 根据不同登录类型执行登录
     * 
     * @param request 登录请求
     * @return 登录结果
     */
    public SalesforceLoginResult login(SalesforceLoginRequest request) {
        // 添加跟踪ID到日志上下文
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        
        logger.info("执行Salesforce登录，登录类型: {}, 用户名: {}", request.getLoginType(), request.getUsername());
        
        long startTime = System.currentTimeMillis();
        
        try {
            // 首先检查缓存中是否有有效的登录结果
            // 注意：此处简化处理，实际应用中可能需要更复杂的缓存键
            // SalesforceLoginResult cachedResult = cacheManager.getLoginResult(...);
            // if (cachedResult != null && cachedResult.isSuccess()) {
            //     logger.info("从缓存中获取登录结果，用户: {}", request.getUsername());
            //     return cachedResult;
            // }
            
            // 1. 检查账号是否被锁定
            if (isAccountLocked(request.getUsername())) {
                logger.warn("账号已被锁定，拒绝登录请求，用户名: {}", request.getUsername());
                SalesforceLoginResult result = new SalesforceLoginResult();
                result.setSuccess(false);
                result.setErrorMessage("Account is locked. Please try again later.");
                result.setErrorCode("ACCOUNT_LOCKED");
                
                // 记录登录失败日志
                recordLoginAudit(request, result, "FAILED");
                
                // 更新登录统计（失败）
                updateLoginStatistics(request.getLoginType(), false);
                
                return result;
            }
            
            // 2. 获取登录策略
            LoginStrategy strategy;
            try {
                strategy = loginStrategyFactory.getLoginStrategy(request.getLoginType());
            } catch (Exception e) {
                logger.error("获取登录策略失败，登录类型: {}", request.getLoginType(), e);
                SalesforceLoginResult result = new SalesforceLoginResult();
                result.setSuccess(false);
                result.setErrorMessage("Unsupported login type: " + request.getLoginType());
                result.setErrorCode("UNSUPPORTED_LOGIN_TYPE");
                recordLoginAudit(request, result, "FAILED");
                updateLoginStatistics(request.getLoginType(), false);
                
                return result;
            }
            
            // 3. 执行登录
            SalesforceLoginResult result;
            try {
                result = strategy.login(request);
            } catch (Exception e) {
                logger.error("执行登录策略失败，登录类型: {}, 用户名: {}", request.getLoginType(), request.getUsername(), e);
                result = new SalesforceLoginResult();
                result.setSuccess(false);
                result.setErrorMessage("Authentication failed: " + e.getMessage());
                result.setErrorCode("AUTHENTICATION_FAILED");
                // 记录登录失败尝试
                recordFailedLoginAttempt(request, result);
                // 检查是否需要锁定账号
                checkAndLockAccount(request.getUsername());
                // 记录登录失败日志
                recordLoginAudit(request, result, "FAILED");
                // 更新登录统计（失败）
                updateLoginStatistics(request.getLoginType(), false);
                
                return result;
            }
            
            // 4. 处理登录结果
            if (result.isSuccess()) {
                logger.info("Salesforce登录成功，用户: {}, 访问令牌前缀: {}", 
                    request.getUsername(), 
                    result.getAccessToken() != null ? result.getAccessToken().substring(0, Math.min(10, result.getAccessToken().length())) : "null");
                saveLoginStatus(request, result);
                
                // 缓存登录结果
                if (result.getAccessToken() != null) {
                    cacheManager.cacheLoginResult(result.getAccessToken(), result);
                    cacheManager.cacheAccessTokenInfo(result.getAccessToken(), result);
                }
                
                // 清除登录失败记录
                clearFailedLoginRecords(request.getUsername());
                
                // 记录登录审计日志
                recordLoginAudit(request, result, "SUCCESS");
                
                // 更新登录统计（成功）
                updateLoginStatistics(request.getLoginType(), true);
                
            } else {
                logger.warn("Salesforce登录失败，用户: {}, 错误代码: {}, 错误信息: {}", 
                    request.getUsername(), result.getErrorCode(), result.getErrorMessage());
                // 记录登录失败尝试
                recordFailedLoginAttempt(request, result);
                
                // 检查是否需要锁定账号
                checkAndLockAccount(request.getUsername());
                
                // 记录登录失败日志
                recordLoginAudit(request, result, "FAILED");
                
                // 更新登录统计（失败）
                updateLoginStatistics(request.getLoginType(), false);
            }
            
            return result;
        } finally {
            // 清理MDC上下文
            MDC.remove("traceId");
        }
    }
    
    /**
     * 刷新访问令牌
     * 
     * @param refreshToken 刷新令牌
     * @param loginType 登录类型
     * @return 新的登录结果
     */
    public SalesforceLoginResult refreshToken(String refreshToken, String loginType) {
        // 添加跟踪ID到日志上下文
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        
        logger.info("执行刷新令牌操作，登录类型: {}", loginType);
        
        long startTime = System.currentTimeMillis();
        
        try {
            // 首先检查缓存中是否有相关信息
            String cachedAccessToken = cacheManager.getAccessTokenForRefreshToken(refreshToken);
            if (cachedAccessToken != null) {
                SalesforceLoginResult cachedResult = cacheManager.getLoginResult(cachedAccessToken);
                if (cachedResult != null) {
                    logger.info("从缓存中获取刷新令牌结果，登录类型: {}", loginType);
                    return cachedResult;
                }
            }
            
            // 1. 获取登录策略
            LoginStrategy strategy;
            try {
                strategy = loginStrategyFactory.getLoginStrategy(loginType);
            } catch (Exception e) {
                logger.error("获取登录策略失败，登录类型: {}", loginType, e);
                SalesforceLoginResult result = new SalesforceLoginResult();
                result.setSuccess(false);
                result.setErrorMessage("Unsupported login type: " + loginType);
                result.setErrorCode("UNSUPPORTED_LOGIN_TYPE");
                recordTokenRefreshAudit(result, "FAILED");
                return result;
            }
            
            // 2. 执行刷新令牌操作
            SalesforceLoginResult result;
            try {
                result = strategy.refreshToken(refreshToken, loginType);
            } catch (Exception e) {
                logger.error("执行刷新令牌策略失败，登录类型: {}", loginType, e);
                result = new SalesforceLoginResult();
                result.setSuccess(false);
                result.setErrorMessage("Token refresh failed: " + e.getMessage());
                result.setErrorCode("TOKEN_REFRESH_FAILED");
                recordTokenRefreshAudit(result, "FAILED");
                return result;
            }
            
            // 3. 更新登录状态
            if (result.isSuccess()) {
                logger.info("令牌刷新成功，登录类型: {}, 访问令牌前缀: {}", 
                    loginType, 
                    result.getAccessToken() != null ? result.getAccessToken().substring(0, Math.min(10, result.getAccessToken().length())) : "null");
                updateLoginStatus(result);
                
                // 缓存相关信息
                if (result.getAccessToken() != null) {
                    cacheManager.cacheLoginResult(result.getAccessToken(), result);
                    cacheManager.cacheAccessTokenInfo(result.getAccessToken(), result);
                }
                
                if (refreshToken != null) {
                    cacheManager.cacheRefreshTokenInfo(refreshToken, result);
                }
                
                // 4. 记录审计日志
                recordTokenRefreshAudit(result, "SUCCESS");
                
                // 5. 更新登录统计（令牌刷新）
                updateLoginStatisticsForRefresh(loginType);
                
            } else {
                logger.warn("令牌刷新失败，登录类型: {}, 错误代码: {}, 错误信息: {}", 
                    loginType, result.getErrorCode(), result.getErrorMessage());
                // 记录刷新失败日志
                recordTokenRefreshAudit(result, "FAILED");
            }
            
            return result;
        } finally {
            // 清理MDC上下文
            MDC.remove("traceId");
        }
    }
    
    /**
     * 执行登出操作
     * 
     * @param accessToken 访问令牌
     * @param loginType 登录类型
     * @return 登出是否成功
     */
    public boolean logout(String accessToken, String loginType) {
        // 添加跟踪ID到日志上下文
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        
        logger.info("执行登出操作，登录类型: {}", loginType);
        
        long startTime = System.currentTimeMillis();
        
        try {
            // 清除相关缓存
            cacheManager.evictAccessTokenCache(accessToken);
            
            // 1. 获取登录策略
            LoginStrategy strategy;
            try {
                strategy = loginStrategyFactory.getLoginStrategy(loginType);
            } catch (Exception e) {
                logger.error("获取登录策略失败，登录类型: {}", loginType, e);
                recordLogoutAudit(accessToken, loginType, "FAILED");
                return false;
            }
            
            // 2. 执行登出操作
            boolean success;
            try {
                success = strategy.logout(accessToken, loginType);
            } catch (Exception e) {
                logger.error("执行登出策略失败，登录类型: {}", loginType, e);
                recordLogoutAudit(accessToken, loginType, "FAILED");
                return false;
            }
            
            // 3. 清理登录状态
            if (success) {
                logger.info("登出操作成功，登录类型: {}, 令牌前缀: {}", loginType, accessToken.substring(0, Math.min(10, accessToken.length())));
                cleanupLoginStatus(accessToken);
                
                // 4. 更新登录统计（令牌吊销）
                updateLoginStatisticsForRevoke(loginType);
            } else {
                logger.warn("登出操作失败，登录类型: {}, 令牌前缀: {}", loginType, accessToken.substring(0, Math.min(10, accessToken.length())));
            }
            
            // 5. 记录登出审计日志
            recordLogoutAudit(accessToken, loginType, success ? "SUCCESS" : "FAILED");
            
            return success;
        } finally {
            // 清理MDC上下文
            MDC.remove("traceId");
        }
    }
    
    /**
     * 获取当前登录状态
     * 
     * @return 当前登录状态
     */
    public SalesforceLoginResult getCurrentLoginStatus() {
        // 添加跟踪ID到日志上下文
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        
        logger.info("获取当前登录状态");
        
        // 从缓存中获取当前登录状态
        // 简化实现，实际应从当前线程上下文或Redis缓存中获取
        // 这里从数据库中获取最新的活跃会话
        try {
            // 查询最新的活跃会话
            DataiSfLoginSession sessionQuery = new DataiSfLoginSession();
            sessionQuery.setStatus("ACTIVE");
            // 按登录时间倒序排列，获取最新的会话
            List<DataiSfLoginSession> sessions = loginSessionService.selectDataiSfLoginSessionList(sessionQuery);
            
            if (!sessions.isEmpty()) {
                // 获取最新的会话
                DataiSfLoginSession session = sessions.get(0);
                
                // 查询对应的令牌信息
                DataiSfToken token = tokenService.selectDataiSfTokenByTokenId(session.getTokenId());
                
                if (token != null && "ACTIVE".equals(token.getStatus())) {
                    SalesforceLoginResult result = new SalesforceLoginResult();
                    result.setSuccess(true);
                    result.setAccessToken(token.getAccessToken());
                    result.setRefreshToken(token.getRefreshToken());
                    result.setInstanceUrl(token.getInstanceUrl());
                    result.setOrganizationId(token.getOrganizationId());
                    result.setUserId(token.getUserId());
                    result.setTokenType(token.getTokenType());
                    
                    // 计算剩余过期时间（秒）
                    // 注意：这里假设accessTokenExpire存储的是日期，实际应使用DateTime类型
                    long expiresIn = 7200; // 默认为2小时
                    result.setExpiresIn(expiresIn);
                    
                    logger.info("获取登录状态成功，访问令牌前缀: {}", 
                        result.getAccessToken() != null ? result.getAccessToken().substring(0, Math.min(10, result.getAccessToken().length())) : "null");
                    return result;
                }
            }
            
            // 如果没有找到活跃会话，返回未登录状态
            SalesforceLoginResult result = new SalesforceLoginResult();
            result.setSuccess(false);
            result.setErrorMessage("No active login session");
            result.setErrorCode("NO_ACTIVE_SESSION");
            logger.info("获取登录状态完成，当前无活跃会话");
            return result;
        } catch (Exception e) {
            logger.error("获取当前登录状态失败", e);
            SalesforceLoginResult result = new SalesforceLoginResult();
            result.setSuccess(false);
            result.setErrorMessage("Failed to get login status: " + e.getMessage());
            result.setErrorCode("GET_LOGIN_STATUS_FAILED");
            return result;
        } finally {
            // 清理MDC上下文
            MDC.remove("traceId");
        }
    }
    
    /**
     * 保存登录状态到缓存
     * 
     * @param result 登录结果
     */
    public void saveLoginStatus(SalesforceLoginResult result) {
        String accessToken = result.getAccessToken();
        String tokenPrefix = accessToken != null ? accessToken.substring(0, Math.min(10, accessToken.length())) : "null";
        logger.info("保存登录状态到缓存，访问令牌: {}", tokenPrefix + "...");
        
        try {
            // 实现保存登录状态到Redis缓存的逻辑
            // 这里使用CacheUtils工具类，实际应该使用Spring Cache或RedisTemplate
            long expiresInSeconds = result.getExpiresIn();
            String cacheName = "salesforce_login_cache";
            
            // 使用带有过期时间的put方法设置缓存
            CacheUtils.put(cacheName, "current_access_token", result.getAccessToken(), expiresInSeconds, java.util.concurrent.TimeUnit.SECONDS);
            CacheUtils.put(cacheName, "current_instance_url", result.getInstanceUrl(), expiresInSeconds, java.util.concurrent.TimeUnit.SECONDS);
            CacheUtils.put(cacheName, "current_user_id", result.getUserId(), expiresInSeconds, java.util.concurrent.TimeUnit.SECONDS);
            CacheUtils.put(cacheName, "current_organization_id", result.getOrganizationId(), expiresInSeconds, java.util.concurrent.TimeUnit.SECONDS);
            CacheUtils.put(cacheName, "current_token_type", result.getTokenType(), expiresInSeconds, java.util.concurrent.TimeUnit.SECONDS);
            CacheUtils.put(cacheName, "current_expires_in", result.getExpiresIn(), expiresInSeconds, java.util.concurrent.TimeUnit.SECONDS);
            
            logger.info("登录状态保存到缓存成功");
        } catch (Exception e) {
            logger.error("保存登录状态到缓存失败: {}", e.getMessage(), e);
            // 不抛出异常，因为缓存失败不应该影响主要流程
        }
    }

    /**
     * 获取当前登录会话信息
     * 
     * @return 当前登录会话
     */
    @Override
    public DataiSfLoginSession getCurrentLoginSession() {
        // 添加跟踪ID到日志上下文
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        
        logger.info("获取当前登录会话信息");
        
        try {
            // 查询最新的活跃会话
            DataiSfLoginSession sessionQuery = new DataiSfLoginSession();
            sessionQuery.setStatus("ACTIVE");
            // 按登录时间倒序排列，获取最新的会话
            List<DataiSfLoginSession> sessions = loginSessionService.selectDataiSfLoginSessionList(sessionQuery);
            
            if (!sessions.isEmpty()) {
                // 获取最新的会话
                DataiSfLoginSession session = sessions.get(0);
                logger.info("获取登录会话信息成功，会话ID: {}", session.getSessionId());
                
                // 缓存会话信息
                cacheManager.cacheSessionInfo(String.valueOf(session.getSessionId()), session);
                
                return session;
            }
            
            logger.info("获取登录会话信息完成，当前无活跃会话");
            return null;
        } catch (Exception e) {
            logger.error("获取当前登录会话信息失败", e);
            return null;
        } finally {
            // 清理MDC上下文
            MDC.remove("traceId");
        }
    }
    
    /**
     * 保存登录状态到数据库
     * 
     * @param request 登录请求
     * @param result 登录结果
     */
    private void saveLoginStatus(SalesforceLoginRequest request, SalesforceLoginResult result) {
        logger.info("保存登录状态到数据库，用户: {}", request.getUsername());
        
        try {
            // 1. 保存令牌信息
            DataiSfToken token = new DataiSfToken();
            token.setUsername(request.getUsername());
            token.setAccessToken(result.getAccessToken());
            token.setRefreshToken(result.getRefreshToken());
            // 注意：LocalDate只能保存日期，不能保存时间，实际应使用LocalDateTime类型
            // 这里简化处理，只保存当前日期
            token.setAccessTokenExpire(LocalDateTime.now());
            if (result.getRefreshToken() != null) {
                // 刷新令牌默认有效期30天
                token.setRefreshTokenExpire(LocalDateTime.now().plusDays(30));
            }
            token.setStatus("ACTIVE");
            token.setInstanceUrl(result.getInstanceUrl());
            token.setLoginType(request.getLoginType());
            token.setOrganizationId(result.getOrganizationId());
            token.setUserId(result.getUserId());
            token.setTokenType(result.getTokenType());
            token.setCreateTime(new Date());
            token.setUpdateTime(new Date());
            tokenService.insertDataiSfToken(token);
            
            // 2. 保存登录会话信息
            DataiSfLoginSession session = new DataiSfLoginSession();
            session.setUsername(request.getUsername());
            session.setLoginType(request.getLoginType());
            session.setStatus("ACTIVE");
            // 设置登录时间相关字段
            LocalDateTime now = LocalDateTime.now();
            session.setLoginTime(now);
            session.setLastActivityTime(now);
            // 设置过期时间（默认2小时后过期）
            session.setExpireTime(now.plusHours(2));
            session.setCreateTime(new Date());
            session.setUpdateTime(new Date());
            loginSessionService.insertDataiSfLoginSession(session);
            
            // 3. 保存到Redis缓存
            saveLoginStatus(result);
            
            logger.info("登录状态保存成功，令牌ID: {}, 会话ID: {}", token.getTokenId(), session.getSessionId());
        } catch (Exception e) {
            logger.error("保存登录状态到数据库失败，用户: {}", request.getUsername(), e);
            // 抛出自定义异常，让上层处理
            throw new SalesforceAuthException("SAVE_LOGIN_STATUS_FAILED", "保存登录状态失败", e);
        }
    }
    
    /**
     * 更新登录状态
     * 
     * @param result 新的登录结果
     */
    private void updateLoginStatus(SalesforceLoginResult result) {
        String tokenPrefix = result.getAccessToken() != null ? 
            result.getAccessToken().substring(0, Math.min(10, result.getAccessToken().length())) : "null";
        logger.info("更新登录状态，访问令牌: {}", tokenPrefix + "...");
        
        try {
            // 1. 根据旧的访问令牌查找令牌信息
            // 这里简化处理，实际应该从refreshToken查找
            // 或者在refreshToken方法中传递旧的accessToken
            
            // 2. 创建新的令牌记录
            DataiSfToken newToken = new DataiSfToken();
            newToken.setUsername("unknown"); // 实际应该从用户上下文获取
            newToken.setAccessToken(result.getAccessToken());
            newToken.setRefreshToken(result.getRefreshToken());
            // 注意：LocalDate只能保存日期，不能保存时间，实际应使用LocalDateTime类型
            newToken.setAccessTokenExpire(LocalDateTime.now());
            if (result.getRefreshToken() != null) {
                // 刷新令牌默认有效期30天
                newToken.setRefreshTokenExpire(LocalDateTime.now().plusDays(30));
            }
            newToken.setStatus("ACTIVE");
            newToken.setInstanceUrl(result.getInstanceUrl());
            newToken.setLoginType("oauth2"); // 实际应该从参数获取
            newToken.setOrganizationId(result.getOrganizationId());
            newToken.setUserId(result.getUserId());
            newToken.setTokenType(result.getTokenType());
            newToken.setCreateTime(new Date());
            newToken.setUpdateTime(new Date());
            tokenService.insertDataiSfToken(newToken);
            
            // 3. 更新关联的会话信息
            // 查找最新的活跃会话
            DataiSfLoginSession sessionQuery = new DataiSfLoginSession();
            sessionQuery.setStatus("ACTIVE");
            List<DataiSfLoginSession> sessions = loginSessionService.selectDataiSfLoginSessionList(sessionQuery);
            
            if (!sessions.isEmpty()) {
                DataiSfLoginSession session = sessions.get(0);
                session.setTokenId(newToken.getTokenId());
                // 更新会话的活动时间
                LocalDateTime now = LocalDateTime.now();
                session.setLastActivityTime(now);
                // 如果接近过期，则延长过期时间
                if (session.getExpireTime().isBefore(now.plusHours(1))) {
                    session.setExpireTime(now.plusHours(2));
                }
                session.setUpdateTime(new Date());
                loginSessionService.updateDataiSfLoginSession(session);
            }
            
            logger.info("登录状态更新成功，新令牌ID: {}", newToken.getTokenId());
        } catch (Exception e) {
            logger.error("更新登录状态失败: {}", e.getMessage(), e);
            // 不抛出异常，因为状态更新失败不应该影响主要流程
        }
    }
    
    /**
     * 清理登录状态
     * 
     * @param accessToken 访问令牌
     */
    private void cleanupLoginStatus(String accessToken) {
        logger.info("清理登录状态，访问令牌: {}", accessToken.substring(0, Math.min(10, accessToken.length())) + "...");
        
        try {
            // 1. 查找令牌信息
            // 注意：这里需要根据accessToken查询令牌，但当前接口只支持根据tokenId查询
            // 这里简化处理，查询所有令牌并遍历查找
            DataiSfToken queryToken = new DataiSfToken();
            queryToken.setAccessToken(accessToken);
            List<DataiSfToken> tokens = tokenService.selectDataiSfTokenList(queryToken);
            
            if (!tokens.isEmpty()) {
                DataiSfToken token = tokens.get(0);
                // 2. 更新令牌状态为已吊销
                token.setStatus("REVOKED");
                token.setUpdateTime(new Date());
                tokenService.updateDataiSfToken(token);
                
                // 3. 更新会话状态为已登出
                DataiSfLoginSession sessionQuery = new DataiSfLoginSession();
                sessionQuery.setTokenId(token.getTokenId());
                List<DataiSfLoginSession> sessions = loginSessionService.selectDataiSfLoginSessionList(sessionQuery);
                
                for (DataiSfLoginSession session : sessions) {
                    session.setStatus("EXPIRED");
                    // 更新会话的最后活动时间
                    session.setLastActivityTime(LocalDateTime.now());
                    session.setUpdateTime(new Date());
                    loginSessionService.updateDataiSfLoginSession(session);
                }
                
                // 清除相关缓存
                cacheManager.evictAccessTokenCache(accessToken);
                if (token.getRefreshToken() != null) {
                    cacheManager.evictRefreshTokenCache(token.getRefreshToken());
                }
            }
            
            logger.info("登录状态清理成功，访问令牌: {}", accessToken.substring(0, Math.min(10, accessToken.length())) + "...");
        } catch (Exception e) {
            logger.error("清理登录状态失败，访问令牌: {}", accessToken.substring(0, Math.min(10, accessToken.length())), e);
            // 不抛出异常，因为清理失败不应该影响主要流程
        }
    }
    
    /**
     * 记录登录审计日志
     * 
     * @param request 登录请求
     * @param result 登录结果
     * @param resultStr 操作结果
     */
    private void recordLoginAudit(SalesforceLoginRequest request, SalesforceLoginResult result, String resultStr) {
        try {
            DataiSfLoginAudit audit = new DataiSfLoginAudit();
            audit.setUsername(request.getUsername());
            audit.setOperationType("LOGIN");
            audit.setResult(resultStr);
            audit.setLoginType(request.getLoginType());
            audit.setErrorMessage(result.getErrorMessage());
            audit.setOperationTime(LocalDateTime.now());
            audit.setCreateTime(new Date());
            loginAuditService.insertDataiSfLoginAudit(audit);
            
            logger.info("登录审计日志记录成功，用户名: {}, 结果: {}", request.getUsername(), resultStr);
        } catch (Exception e) {
            logger.error("记录登录审计日志失败，用户名: {}", request.getUsername(), e);
            // 不抛出异常，因为日志记录失败不应该影响主要流程
        }
    }
    
    /**
     * 记录令牌刷新审计日志
     * 
     * @param result 登录结果
     * @param resultStr 操作结果
     */
    private void recordTokenRefreshAudit(SalesforceLoginResult result, String resultStr) {
        try {
            DataiSfLoginAudit audit = new DataiSfLoginAudit();
            audit.setOperationType("REFRESH_TOKEN");
            audit.setResult(resultStr);
            audit.setErrorMessage(result.getErrorMessage());
            audit.setOperationTime(LocalDateTime.now());
            audit.setCreateTime(new Date());
            loginAuditService.insertDataiSfLoginAudit(audit);
            
            logger.info("令牌刷新审计日志记录成功，结果: {}", resultStr);
        } catch (Exception e) {
            logger.error("记录令牌刷新审计日志失败", e);
            // 不抛出异常，因为日志记录失败不应该影响主要流程
        }
    }
    
    /**
     * 记录登出审计日志
     * 
     * @param accessToken 访问令牌
     * @param loginType 登录类型
     * @param resultStr 操作结果
     */
    private void recordLogoutAudit(String accessToken, String loginType, String resultStr) {
        try {
            DataiSfLoginAudit audit = new DataiSfLoginAudit();
            audit.setOperationType("LOGOUT");
            audit.setResult(resultStr);
            audit.setLoginType(loginType);
            audit.setOperationTime(LocalDateTime.now());
            audit.setCreateTime(new Date());
            loginAuditService.insertDataiSfLoginAudit(audit);
            
            logger.info("登出审计日志记录成功，结果: {}", resultStr);
        } catch (Exception e) {
            logger.error("记录登出审计日志失败", e);
            // 不抛出异常，因为日志记录失败不应该影响主要流程
        }
    }
    
    /**
     * 检查账号是否被锁定
     * 
     * @param username 用户名
     * @return 是否被锁定
     */
    private boolean isAccountLocked(String username) {
        logger.debug("检查账号是否被锁定，用户名: {}", username);
        
        try {
            DataiSfFailedLogin query = new DataiSfFailedLogin();
            query.setUsername(username);
            query.setLockStatus("LOCKED");
            
            List<DataiSfFailedLogin> failedLogins = failedLoginService.selectDataiSfFailedLoginList(query);
            
            if (!failedLogins.isEmpty()) {
                DataiSfFailedLogin failedLogin = failedLogins.get(0);
                
                // 检查锁定时间是否已过期
                if (failedLogin.getUnlockTime() != null) {
                    LocalDateTime now = LocalDateTime.now();
                    if (now.isAfter(failedLogin.getUnlockTime())) {
                        // 锁定时间已过期，解锁账号
                        failedLogin.setLockStatus("UNLOCKED");
                        failedLoginService.updateDataiSfFailedLogin(failedLogin);
                        logger.info("账号锁定时间已过期，已解锁，用户名: {}", username);
                        return false;
                    }
                }
                
                logger.info("账号仍处于锁定状态，用户名: {}", username);
                return true;
            }
            
            logger.debug("账号未被锁定，用户名: {}", username);
            return false;
        } catch (Exception e) {
            logger.error("检查账号是否被锁定失败，用户名: {}", username, e);
            // 出现异常时不阻止登录，避免因系统问题导致无法登录
            return false;
        }
    }
    
    /**
     * 记录登录失败尝试
     * 
     * @param request 登录请求
     * @param result 登录结果
     */
    private void recordFailedLoginAttempt(SalesforceLoginRequest request, SalesforceLoginResult result) {
        logger.info("记录登录失败尝试，用户名: {}", request.getUsername());
        
        try {
            DataiSfFailedLogin failedLogin = new DataiSfFailedLogin();
            failedLogin.setUsername(request.getUsername());
            failedLogin.setLoginType(request.getLoginType());
            failedLogin.setFailedTime(LocalDateTime.now());
            failedLogin.setFailedReason(result.getErrorMessage());
            failedLogin.setLockStatus("UNLOCKED");
            failedLogin.setCreateTime(new Date());
            
            failedLoginService.insertDataiSfFailedLogin(failedLogin);
            logger.debug("登录失败尝试记录成功，用户名: {}", request.getUsername());
        } catch (Exception e) {
            logger.error("记录登录失败尝试失败，用户名: {}", request.getUsername(), e);
            // 不抛出异常，因为记录失败不应该影响主要流程
        }
    }
    
    /**
     * 检查是否需要锁定账号
     * 
     * @param username 用户名
     */
    private void checkAndLockAccount(String username) {
        logger.debug("检查是否需要锁定账号，用户名: {}", username);
        
        try {
            // 查询最近5分钟内的失败登录次数
            DataiSfFailedLogin query = new DataiSfFailedLogin();
            query.setUsername(username);
            
            List<DataiSfFailedLogin> failedLogins = failedLoginService.selectDataiSfFailedLoginList(query);
            
            // 获取今天的失败登录次数（由于failedTime是LocalDate类型，只能精确到日期）
            LocalDateTime today = LocalDateTime.now();
            long failedCount = failedLogins.stream()
                    .filter(fl -> fl.getFailedTime() != null && fl.getFailedTime().isEqual(today))
                    .count();
            
            logger.debug("最近5分钟内的失败登录次数: {}，用户名: {}", failedCount, username);
            
            // 如果失败次数超过5次，锁定账号
            if (failedCount >= 5) {
                logger.warn("失败登录次数超过阈值，即将锁定账号，用户名: {}", username);
                lockAccount(username);
            }
        } catch (Exception e) {
            logger.error("检查是否需要锁定账号失败，用户名: {}", username, e);
            // 不抛出异常，因为检查失败不应该影响主要流程
        }
    }
    
    /**
     * 锁定账号
     * 
     * @param username 用户名
     */
    private void lockAccount(String username) {
        logger.info("锁定账号，用户名: {}", username);
        
        try {
            // 创建锁定记录
            DataiSfFailedLogin lockedLogin = new DataiSfFailedLogin();
            lockedLogin.setUsername(username);
            lockedLogin.setLockStatus("LOCKED");
            lockedLogin.setLockTime(LocalDateTime.now());
            // 锁定30分钟 - 由于unlockTime是LocalDate类型，只能表示日期，所以设置为第二天
            lockedLogin.setUnlockTime(LocalDateTime.now().plusDays(1));
            lockedLogin.setLockReason("Too many failed login attempts");
            lockedLogin.setCreateTime(new Date());
            
            failedLoginService.insertDataiSfFailedLogin(lockedLogin);
            logger.info("账号锁定成功，用户名: {}", username);
        } catch (Exception e) {
            logger.error("锁定账号失败，用户名: {}", username, e);
            // 不抛出异常，因为锁定失败不应该影响主要流程
        }
    }
    
    /**
     * 清除登录失败记录
     * 
     * @param username 用户名
     */
    private void clearFailedLoginRecords(String username) {
        logger.debug("清除登录失败记录，用户名: {}", username);
        
        try {
            // 这里简化处理，实际应该根据业务需求决定如何清除
            // 比如只清除成功登录之前的失败记录，或者保留最近的几条
            DataiSfFailedLogin query = new DataiSfFailedLogin();
            query.setUsername(username);
            
            List<DataiSfFailedLogin> failedLogins = failedLoginService.selectDataiSfFailedLoginList(query);
            
            for (DataiSfFailedLogin failedLogin : failedLogins) {
                failedLoginService.deleteDataiSfFailedLoginByFailedId(failedLogin.getFailedId());
            }
            
            logger.debug("登录失败记录清除成功，用户名: {}", username);
        } catch (Exception e) {
            logger.error("清除登录失败记录失败，用户名: {}", username, e);
            // 不抛出异常，因为清除失败不应该影响主要流程
        }
    }
    
    /**
     * 更新登录统计
     * 
     * @param loginType 登录类型
     * @param isSuccess 是否成功
     */
    private void updateLoginStatistics(String loginType, boolean isSuccess) {
        logger.debug("更新登录统计，登录类型: {}, 结果: {}", loginType, isSuccess ? "SUCCESS" : "FAILED");
        
        try {
            // 获取当前日期和小时
            LocalDateTime nowDate = LocalDateTime.now();
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            
            // 查询当前统计记录
            DataiSfLoginStatistics query = new DataiSfLoginStatistics();
            query.setStatDate(nowDate);
            query.setStatHour((long) hour);
            query.setLoginType(loginType);
            
            List<DataiSfLoginStatistics> statisticsList = loginStatisticsService.selectDataiSfLoginStatisticsList(query);
            DataiSfLoginStatistics statistics;
            
            if (statisticsList.isEmpty()) {
                // 创建新的统计记录
                statistics = new DataiSfLoginStatistics();
                statistics.setStatDate(nowDate);
                statistics.setStatHour((long) hour);
                statistics.setLoginType(loginType);
                statistics.setSuccessCount(0L);
                statistics.setFailedCount(0L);
                statistics.setRefreshCount(0L);
                statistics.setRevokeCount(0L);
                statistics.setCreateTime(new Date());
                statistics.setUpdateTime(new Date());
            } else {
                // 更新现有统计记录
                statistics = statisticsList.get(0);
            }
            
            // 更新统计数据
            if (isSuccess) {
                statistics.setSuccessCount(statistics.getSuccessCount() + 1L);
            } else {
                statistics.setFailedCount(statistics.getFailedCount() + 1L);
            }
            
            statistics.setUpdateTime(new Date());
            
            // 保存统计记录
            if (statisticsList.isEmpty()) {
                loginStatisticsService.insertDataiSfLoginStatistics(statistics);
            } else {
                loginStatisticsService.updateDataiSfLoginStatistics(statistics);
            }
            
            logger.debug("登录统计更新成功，登录类型: {}, 结果: {}", loginType, isSuccess ? "SUCCESS" : "FAILED");
        } catch (Exception e) {
            logger.error("更新登录统计失败，登录类型: {}", loginType, e);
            // 不抛出异常，因为统计失败不应该影响主要流程
        }
    }
    
    /**
     * 更新登录统计（令牌刷新）
     * 
     * @param loginType 登录类型
     */
    private void updateLoginStatisticsForRefresh(String loginType) {
        logger.debug("更新登录统计（令牌刷新），登录类型: {}", loginType);
        
        try {
            // 获取当前日期和小时
            LocalDateTime nowDate = LocalDateTime.now();
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            
            // 查询当前统计记录
            DataiSfLoginStatistics query = new DataiSfLoginStatistics();
            query.setStatDate(nowDate);
            query.setStatHour((long) hour);
            query.setLoginType(loginType);
            
            List<DataiSfLoginStatistics> statisticsList = loginStatisticsService.selectDataiSfLoginStatisticsList(query);
            DataiSfLoginStatistics statistics;
            
            if (statisticsList.isEmpty()) {
                // 创建新的统计记录
                statistics = new DataiSfLoginStatistics();
                statistics.setStatDate(nowDate);
                statistics.setStatHour((long) hour);
                statistics.setLoginType(loginType);
                statistics.setSuccessCount(0L);
                statistics.setFailedCount(0L);
                statistics.setRefreshCount(1L);
                statistics.setRevokeCount(0L);
                statistics.setCreateTime(new Date());
                statistics.setUpdateTime(new Date());
                loginStatisticsService.insertDataiSfLoginStatistics(statistics);
            } else {
                // 更新现有统计记录
                statistics = statisticsList.get(0);
                statistics.setRefreshCount(statistics.getRefreshCount() + 1L);
                statistics.setUpdateTime(new Date());
                loginStatisticsService.updateDataiSfLoginStatistics(statistics);
            }
            
            logger.debug("登录统计（令牌刷新）更新成功，登录类型: {}", loginType);
        } catch (Exception e) {
            logger.error("更新登录统计（令牌刷新）失败，登录类型: {}", loginType, e);
            // 不抛出异常，因为统计失败不应该影响主要流程
        }
    }
    
    /**
     * 更新登录统计（令牌吊销）
     * 
     * @param loginType 登录类型
     */
    private void updateLoginStatisticsForRevoke(String loginType) {
        logger.debug("更新登录统计（令牌吊销），登录类型: {}", loginType);
        
        try {
            // 获取当前日期和小时
            LocalDateTime nowDate = LocalDateTime.now();
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            
            // 查询当前统计记录
            DataiSfLoginStatistics query = new DataiSfLoginStatistics();
            query.setStatDate(nowDate);
            query.setStatHour((long) hour);
            query.setLoginType(loginType);
            
            List<DataiSfLoginStatistics> statisticsList = loginStatisticsService.selectDataiSfLoginStatisticsList(query);
            DataiSfLoginStatistics statistics;
            
            if (statisticsList.isEmpty()) {
                // 创建新的统计记录
                statistics = new DataiSfLoginStatistics();
                statistics.setStatDate(nowDate);
                statistics.setStatHour((long) hour);
                statistics.setLoginType(loginType);
                statistics.setSuccessCount(0L);
                statistics.setFailedCount(0L);
                statistics.setRefreshCount(0L);
                statistics.setRevokeCount(1L);
                statistics.setCreateTime(new Date());
                statistics.setUpdateTime(new Date());
                loginStatisticsService.insertDataiSfLoginStatistics(statistics);
            } else {
                // 更新现有统计记录
                statistics = statisticsList.get(0);
                statistics.setRevokeCount(statistics.getRevokeCount() + 1L);
                statistics.setUpdateTime(new Date());
                loginStatisticsService.updateDataiSfLoginStatistics(statistics);
            }
            
            logger.debug("登录统计（令牌吊销）更新成功，登录类型: {}", loginType);
        } catch (Exception e) {
            logger.error("更新登录统计（令牌吊销）失败，登录类型: {}", loginType, e);
            // 不抛出异常，因为统计失败不应该影响主要流程
        }
    }
}