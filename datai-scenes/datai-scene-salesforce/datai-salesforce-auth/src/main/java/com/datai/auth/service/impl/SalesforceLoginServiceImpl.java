package com.datai.auth.service.impl;

import com.datai.auth.constant.SalesforceConfigConstants;
import com.datai.auth.domain.SalesforceLoginResult;
import com.datai.auth.domain.SalesforceLoginRequest;
import com.datai.auth.service.IDataiSfLoginHistoryService;
import com.datai.auth.strategy.LoginStrategyFactory;
import com.datai.auth.strategy.LoginStrategy;
import com.datai.auth.domain.DataiSfLoginSession;
import com.datai.auth.domain.DataiSfLoginHistory;
import com.datai.auth.service.IDataiSfLoginSessionService;
import com.datai.auth.service.ISalesforceLoginService;
import com.datai.common.utils.CacheUtils;
import com.datai.common.utils.StringUtils;
import com.datai.common.utils.ip.IpUtils;
import com.datai.salesforce.common.exception.SalesforceAuthException;
import org.springframework.beans.factory.annotation.Autowired;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

import java.util.List;
import java.util.UUID;
/**
 * Salesforce登录服务实现
 * 实现登录认证的核心逻辑
 * 管理登录策略的调用
 * 处理登录状态的保存与获取
 * 记录登录登录历史
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
    private IDataiSfLoginHistoryService loginHistoryService;

    @Override
    public SalesforceLoginResult login(SalesforceLoginRequest request) {
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        
        logger.info("开始执行Salesforce登录操作，登录类型: {}, 用户名: {}", request.getLoginType(), request.getUsername());
        
        try {
            String loginType = request.getLoginType();
            if (StringUtils.isEmpty(loginType)) {
                throw new SalesforceAuthException("LOGIN_TYPE_EMPTY", "登录类型不能为空");
            }
            
            LoginStrategy strategy = loginStrategyFactory.getLoginStrategy(loginType);
            
            SalesforceLoginResult result = strategy.login(request);
            
            if (result.isSuccess()) {
                
                saveLoginSession(result, request);
                saveLoginHistory(result, request, "success");

                CacheUtils.put(SalesforceConfigConstants.CACHE_NAME, SalesforceConfigConstants.CURRENT_RESULT, result);
                
                logger.info("Salesforce登录成功，Session ID: {}, 用户ID: {}", result.getSessionId(), result.getUserId());
            } else {
                saveLoginHistory(result, request, "failed");
                logger.error("Salesforce登录失败，错误代码: {}, 错误信息: {}", result.getErrorCode(), result.getErrorMessage());
            }
            
            return result;
        } catch (SalesforceAuthException e) {
            logger.error("Salesforce登录异常: {}", e.getMessage(), e);
            SalesforceLoginResult errorResult = new SalesforceLoginResult();
            errorResult.setSuccess(false);
            errorResult.setErrorCode(e.getErrorCode());
            errorResult.setErrorMessage(e.getMessage());
            saveLoginHistory(errorResult, request, "failed");
            return errorResult;
        } catch (Exception e) {
            logger.error("Salesforce登录系统异常: {}", e.getMessage(), e);
            SalesforceLoginResult errorResult = new SalesforceLoginResult();
            errorResult.setSuccess(false);
            errorResult.setErrorCode("SYSTEM_ERROR");
            errorResult.setErrorMessage("系统异常: " + e.getMessage());
            saveLoginHistory(errorResult, request, "failed");
            return errorResult;
        } finally {
            MDC.remove("traceId");
        }
    }
    
    private void saveLoginSession(SalesforceLoginResult result, SalesforceLoginRequest request) {
        try {
            DataiSfLoginSession session = new DataiSfLoginSession();
            session.setUsername(request.getUsername());
            session.setLoginType(request.getLoginType());
            session.setStatus("active");
            session.setLoginTime(LocalDateTime.now());
            
            long expiresIn = result.getExpiresIn();
            if (expiresIn > 0) {
                session.setExpireTime(LocalDateTime.now().plusSeconds(expiresIn));
            }
            
            session.setLastActivityTime(LocalDateTime.now());
            session.setSessionId(result.getSessionId());
            session.setUserId(result.getUserId());
            session.setOrganizationId(result.getOrganizationId());
            session.setInstanceUrl(result.getInstanceUrl());
            
            HttpServletRequest httpRequest = getCurrentRequest();
            if (httpRequest != null) {
                session.setLoginIp(IpUtils.getIpAddr(httpRequest));
                session.setDeviceInfo(httpRequest.getHeader("User-Agent"));
            }
            
            loginSessionService.insertDataiSfLoginSession(session);
            logger.debug("保存登录会话信息成功，Session ID: {}", result.getSessionId());
        } catch (Exception e) {
            logger.error("保存登录会话信息失败: {}", e.getMessage(), e);
        }
    }
    
    private void saveLoginHistory(SalesforceLoginResult result, SalesforceLoginRequest request, String status) {
        try {
            DataiSfLoginHistory history = new DataiSfLoginHistory();
            history.setLoginType(request.getLoginType());
            history.setUsername(request.getUsername());
            history.setClientId(request.getClientId());
            history.setGrantType(request.getGrantType());
            history.setOrgAlias(request.getOrgAlias());
            history.setSessionId(request.getSessionId());
            history.setInstanceUrl(result.getInstanceUrl());
            history.setOrganizationId(result.getOrganizationId());
            history.setLoginStatus(status);
            
            if (!result.isSuccess()) {
                history.setErrorCode(result.getErrorCode());
                history.setErrorMessage(result.getErrorMessage());
            } else {
                history.setSessionIdResult(result.getSessionId());
                history.setTokenType(result.getTokenType());
                history.setExpiresIn((int) result.getExpiresIn());
            }
            
            HttpServletRequest httpRequest = getCurrentRequest();
            if (httpRequest != null) {
                history.setRequestIp(IpUtils.getIpAddr(httpRequest));
                history.setRequestPort(httpRequest.getRemotePort());
                history.setUserAgent(httpRequest.getHeader("User-Agent"));
            }
            
            history.setRequestTime(LocalDateTime.now());
            history.setResponseTime(LocalDateTime.now());
            
            loginHistoryService.insertDataiSfLoginHistory(history);
            logger.debug("保存登录历史记录成功，状态: {}", status);
        } catch (Exception e) {
            logger.error("保存登录历史记录失败: {}", e.getMessage(), e);
        }
    }
    
    private HttpServletRequest getCurrentRequest() {
        try {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            return attributes != null ? attributes.getRequest() : null;
        } catch (Exception e) {
            logger.warn("获取当前HTTP请求失败: {}", e.getMessage());
            return null;
        }
    }

    @Override
    public boolean logout(String sessionId, String loginType) {
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        
        logger.info("开始执行Salesforce登出操作，Session ID: {}, 登录类型: {}", sessionId, loginType);
        
        try {
            if (StringUtils.isEmpty(sessionId)) {
                logger.warn("登出失败，Session ID为空");
                return false;
            }

            CacheUtils.clear(SalesforceConfigConstants.CACHE_NAME);
            
            if (StringUtils.isNotEmpty(loginType)) {
                try {
                    LoginStrategy strategy = loginStrategyFactory.getLoginStrategy(loginType);
                    boolean strategyLogoutSuccess = strategy.logout(sessionId, loginType);
                    logger.debug("策略模式登出结果: {}", strategyLogoutSuccess);
                } catch (Exception e) {
                    logger.error("策略模式登出失败: {}", e.getMessage(), e);
                }
            }
            
            updateSessionStatus(sessionId, "inactive");
            
            CacheUtils.remove(SalesforceConfigConstants.CACHE_NAME, SalesforceConfigConstants.CURRENT_RESULT);
            
            logger.info("Salesforce登出成功，Session ID: {}", sessionId);
            return true;
        } catch (Exception e) {
            logger.error("Salesforce登出系统异常: {}", e.getMessage(), e);
            return false;
        } finally {
            MDC.remove("traceId");
        }
    }
    
    private void updateSessionStatus(String sessionId, String status) {
        try {
            DataiSfLoginSession query = new DataiSfLoginSession();
            query.setSessionId(sessionId);
            List<DataiSfLoginSession> sessions = loginSessionService.selectDataiSfLoginSessionList(query);
            
            if (sessions != null && !sessions.isEmpty()) {
                DataiSfLoginSession session = sessions.get(0);
                session.setStatus(status);
                session.setLastActivityTime(LocalDateTime.now());
                loginSessionService.updateDataiSfLoginSession(session);
                logger.debug("更新会话状态成功，Session ID: {}, 状态: {}", sessionId, status);
            } else {
                logger.warn("未找到会话记录，Session ID: {}", sessionId);
            }
        } catch (Exception e) {
            logger.error("更新会话状态失败: {}", e.getMessage(), e);
        }
    }

    @Override
    public DataiSfLoginSession getCurrentLoginInfo(String sessionId) {
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        
        logger.info("获取当前登录信息，Session ID: {}", sessionId);
        
        try {
            if (StringUtils.isEmpty(sessionId)) {
                logger.warn("获取登录信息失败，Session ID为空");
                return null;
            }
            
            DataiSfLoginSession query = new DataiSfLoginSession();
            query.setSessionId(sessionId);
            List<DataiSfLoginSession> sessions = loginSessionService.selectDataiSfLoginSessionList(query);
            
            if (sessions != null && !sessions.isEmpty()) {
                DataiSfLoginSession session = sessions.get(0);
                
                if (!"active".equals(session.getStatus())) {
                    logger.warn("会话状态非活跃，Session ID: {}, 状态: {}", sessionId, session.getStatus());
                    return null;
                }
                
                if (session.getExpireTime() != null && session.getExpireTime().isBefore(LocalDateTime.now())) {
                    logger.warn("会话已过期，Session ID: {}, 过期时间: {}", sessionId, session.getExpireTime());
                    updateSessionStatus(sessionId, "expired");
                    return null;
                }
                
                session.setLastActivityTime(LocalDateTime.now());
                loginSessionService.updateDataiSfLoginSession(session);
                
                logger.info("获取登录信息成功，Session ID: {}, 用户名: {}", sessionId, session.getUsername());
                return session;
            } else {
                logger.warn("未找到登录会话记录，Session ID: {}", sessionId);
                return null;
            }
        } catch (Exception e) {
            logger.error("获取登录信息系统异常: {}", e.getMessage(), e);
            return null;
        } finally {
            MDC.remove("traceId");
        }
    }

    @Override
    public SalesforceLoginResult autoLogin(Long historyId) {
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        
        logger.info("开始执行自动登录，历史记录ID: {}", historyId);
        
        try {
            if (historyId == null) {
                logger.warn("自动登录失败，历史记录ID为空");
                SalesforceLoginResult errorResult = new SalesforceLoginResult();
                errorResult.setSuccess(false);
                errorResult.setErrorCode("HISTORY_ID_EMPTY");
                errorResult.setErrorMessage("历史记录ID不能为空");
                return errorResult;
            }
            
            DataiSfLoginHistory history = loginHistoryService.selectDataiSfLoginHistoryById(historyId);
            if (history == null) {
                logger.warn("自动登录失败，未找到历史记录，ID: {}", historyId);
                SalesforceLoginResult errorResult = new SalesforceLoginResult();
                errorResult.setSuccess(false);
                errorResult.setErrorCode("HISTORY_NOT_FOUND");
                errorResult.setErrorMessage("未找到登录历史记录");
                return errorResult;
            }
            
            if (!"success".equals(history.getLoginStatus())) {
                logger.warn("自动登录失败，历史记录登录状态非成功，ID: {}, 状态: {}", historyId, history.getLoginStatus());
                SalesforceLoginResult errorResult = new SalesforceLoginResult();
                errorResult.setSuccess(false);
                errorResult.setErrorCode("HISTORY_STATUS_INVALID");
                errorResult.setErrorMessage("历史记录登录状态无效");
                return errorResult;
            }
            
            SalesforceLoginRequest request = new SalesforceLoginRequest();
            request.setLoginType(history.getLoginType());
            request.setUsername(history.getUsername());
            request.setClientId(history.getClientId());
            request.setGrantType(history.getGrantType());
            request.setOrgAlias(history.getOrgAlias());
            request.setSessionId(history.getSessionId());
            
            logger.info("从历史记录提取登录参数，登录类型: {}, 用户名: {}", request.getLoginType(), request.getUsername());
            
            SalesforceLoginResult result = login(request);
            
            if (result.isSuccess()) {
                logger.info("自动登录成功，历史记录ID: {}, 新Session ID: {}", historyId, result.getSessionId());
            } else {
                logger.error("自动登录失败，历史记录ID: {}, 错误: {}", historyId, result.getErrorMessage());
            }
            
            return result;
        } catch (Exception e) {
            logger.error("自动登录系统异常: {}", e.getMessage(), e);
            SalesforceLoginResult errorResult = new SalesforceLoginResult();
            errorResult.setSuccess(false);
            errorResult.setErrorCode("SYSTEM_ERROR");
            errorResult.setErrorMessage("系统异常: " + e.getMessage());
            return errorResult;
        } finally {
            MDC.remove("traceId");
        }
    }

}