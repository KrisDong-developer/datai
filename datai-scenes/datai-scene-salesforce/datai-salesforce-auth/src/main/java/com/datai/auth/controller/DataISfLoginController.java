package com.datai.auth.controller;

import com.datai.auth.domain.SalesforceLoginResult;
import com.datai.auth.domain.SalesforceLoginRequest;
import com.datai.auth.domain.DataiSfLoginSession;
import com.datai.auth.service.ISalesforceLoginService;
import com.datai.auth.service.ITokenManager;
import com.datai.common.core.domain.AjaxResult;
import com.datai.salesforce.common.exception.SalesforceAuthException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * Salesforce登录控制器
 * 提供登录、刷新令牌、登出等接口
 * 
 * @author datai
 * @date 2025-12-14
 */
@Tag(name = "Salesforce登录接口", description = "登录相关操作")
@RestController
@RequestMapping("/salesforce/login")
@CrossOrigin
public class DataISfLoginController {

    private static final Logger logger = LoggerFactory.getLogger(DataISfLoginController.class);

    @Autowired
    private ISalesforceLoginService salesforceLoginService;

    @Autowired
    private ITokenManager tokenManager;

    /**
     * 执行Salesforce登录
     * 
     * @param request 登录请求，包含登录类型、用户名、密码等信息
     * @return 登录结果
     */
    @Operation(summary = "执行登录", description = "根据不同登录类型执行登录操作")
    @PostMapping("/execute")
    public AjaxResult login(@RequestBody SalesforceLoginRequest request) {
        // 添加跟踪ID到日志上下文
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        
        logger.info("执行Salesforce登录，登录类型: {}, 用户名: {}", request.getLoginType(), request.getUsername());
        try {
            SalesforceLoginResult result = salesforceLoginService.login(request);
            if (result.isSuccess()) {
                logger.info("Salesforce登录成功，用户: {}, 访问令牌前缀: {}", 
                    request.getUsername(), 
                    result.getAccessToken() != null ? result.getAccessToken().substring(0, Math.min(10, result.getAccessToken().length())) : "null");
                return AjaxResult.success("登录成功", result);
            } else {
                logger.warn("Salesforce登录失败，用户: {}, 错误代码: {}, 错误信息: {}", 
                    request.getUsername(), result.getErrorCode(), result.getErrorMessage());
                return AjaxResult.error(result.getErrorMessage());
            }
        } catch (SalesforceAuthException e) {
            logger.error("Salesforce认证异常，用户: {}, 错误代码: {}, 错误信息: {}", 
                request.getUsername(), e.getErrorCode(), e.getMessage(), e);
            return AjaxResult.error("认证失败: " + e.getMessage());
        } catch (Exception e) {
            logger.error("登录失败，用户: {}", request.getUsername(), e);
            return AjaxResult.error("登录失败: " + e.getMessage());
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
    @Operation(summary = "刷新令牌", description = "使用刷新令牌获取新的访问令牌")
    @PostMapping("/refresh-token")
    public AjaxResult refreshToken(@RequestParam String refreshToken, @RequestParam String loginType) {
        // 添加跟踪ID到日志上下文
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        
        logger.info("执行刷新令牌操作，登录类型: {}", loginType);
        try {
            SalesforceLoginResult result = salesforceLoginService.refreshToken(refreshToken, loginType);
            if (result.isSuccess()) {
                logger.info("令牌刷新成功，登录类型: {}, 访问令牌前缀: {}", 
                    loginType, 
                    result.getAccessToken() != null ? result.getAccessToken().substring(0, Math.min(10, result.getAccessToken().length())) : "null");
                // 刷新成功，更新缓存
                String accessToken = result.getAccessToken();

                return AjaxResult.success("令牌刷新成功", result);
            } else {
                logger.warn("令牌刷新失败，登录类型: {}, 错误代码: {}, 错误信息: {}", 
                    loginType, result.getErrorCode(), result.getErrorMessage());
                return AjaxResult.error(result.getErrorMessage());
            }
        } catch (SalesforceAuthException e) {
            logger.error("Salesforce认证异常，登录类型: {}, 错误代码: {}, 错误信息: {}", 
                loginType, e.getErrorCode(), e.getMessage(), e);
            return AjaxResult.error("认证失败: " + e.getMessage());
        } catch (Exception e) {
            logger.error("刷新令牌失败，登录类型: {}", loginType, e);
            return AjaxResult.error("刷新令牌失败: " + e.getMessage());
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
     * @return 登出结果
     */
    @Operation(summary = "执行登出", description = "退出登录，清理登录状态")
    @PostMapping("/logout")
    public AjaxResult logout(@RequestParam String accessToken, @RequestParam String loginType) {
        // 添加跟踪ID到日志上下文
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        
        logger.info("执行登出操作，登录类型: {}", loginType);
        try {
            boolean success = salesforceLoginService.logout(accessToken, loginType);
            if (success) {
                logger.info("登出成功，登录类型: {}", loginType);
                return AjaxResult.success("登出成功");
            } else {
                logger.warn("登出失败，登录类型: {}", loginType);
                return AjaxResult.error("登出失败");
            }
        } catch (SalesforceAuthException e) {
            logger.error("Salesforce认证异常，登录类型: {}, 错误代码: {}, 错误信息: {}", 
                loginType, e.getErrorCode(), e.getMessage(), e);
            return AjaxResult.error("认证失败: " + e.getMessage());
        } catch (Exception e) {
            logger.error("登出失败，登录类型: {}", loginType, e);
            return AjaxResult.error("登出失败: " + e.getMessage());
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
    @Operation(summary = "获取当前登录状态", description = "获取当前系统的登录状态")
    @GetMapping("/status")
    public AjaxResult getCurrentLoginStatus() {
        // 添加跟踪ID到日志上下文
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        
        logger.info("获取当前登录状态");
        try {
            SalesforceLoginResult result = salesforceLoginService.getCurrentLoginStatus();
            if (result.isSuccess()) {
                logger.info("获取登录状态成功，访问令牌前缀: {}", 
                    result.getAccessToken() != null ? result.getAccessToken().substring(0, Math.min(10, result.getAccessToken().length())) : "null");
                return AjaxResult.success("获取成功", result);
            } else {
                logger.warn("获取登录状态失败，错误代码: {}, 错误信息: {}", 
                    result.getErrorCode(), result.getErrorMessage());
                return AjaxResult.error(result.getErrorMessage());
            }
        } catch (SalesforceAuthException e) {
            logger.error("Salesforce认证异常，错误代码: {}, 错误信息: {}", 
                e.getErrorCode(), e.getMessage(), e);
            return AjaxResult.error("认证失败: " + e.getMessage());
        } catch (Exception e) {
            logger.error("获取登录状态失败", e);
            return AjaxResult.error("获取登录状态失败: " + e.getMessage());
        } finally {
            // 清理MDC上下文
            MDC.remove("traceId");
        }
    }

    /**
     * 验证令牌有效性
     * 
     * @param accessToken 访问令牌
     * @return 令牌是否有效
     */
    @Operation(summary = "验证令牌", description = "验证访问令牌的有效性")
    @GetMapping("/validate-token")
    public AjaxResult validateToken(@RequestParam String accessToken) {
        // 添加跟踪ID到日志上下文
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        
        logger.info("验证令牌有效性，令牌前缀: {}", accessToken.substring(0, Math.min(10, accessToken.length())));
        try {
            boolean isValid = tokenManager.validateToken(accessToken);
            if (isValid) {
                logger.info("令牌验证成功，令牌有效");
                return AjaxResult.success("令牌有效");
            } else {
                logger.warn("令牌验证失败，令牌无效");
                return AjaxResult.error("令牌无效");
            }
        } catch (SalesforceAuthException e) {
            logger.error("Salesforce认证异常，错误代码: {}, 错误信息: {}", 
                e.getErrorCode(), e.getMessage(), e);
            return AjaxResult.error("认证失败: " + e.getMessage());
        } catch (Exception e) {
            logger.error("验证令牌失败，令牌前缀: {}", accessToken.substring(0, Math.min(10, accessToken.length())), e);
            return AjaxResult.error("验证令牌失败: " + e.getMessage());
        } finally {
            // 清理MDC上下文
            MDC.remove("traceId");
        }
    }

    /**
     * 绑定令牌到设备/IP
     * 
     * @param accessToken 访问令牌
     * @param deviceId 设备ID
     * @param ip IP地址
     * @return 绑定结果
     */
    @Operation(summary = "绑定令牌", description = "将令牌绑定到指定设备和IP")
    @PostMapping("/bind-token")
    public AjaxResult bindToken(@RequestParam String accessToken, 
                              @RequestParam(required = false) String deviceId, 
                              @RequestParam(required = false) String ip) {
        // 添加跟踪ID到日志上下文
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        
        logger.info("绑定令牌到设备/IP，令牌前缀: {}, 设备ID: {}, IP: {}", 
            accessToken.substring(0, Math.min(10, accessToken.length())), deviceId, ip);
        try {
            tokenManager.bindToken(accessToken, deviceId, ip);
            logger.info("令牌绑定成功，令牌前缀: {}", accessToken.substring(0, Math.min(10, accessToken.length())));
            return AjaxResult.success("令牌绑定成功");
        } catch (SalesforceAuthException e) {
            logger.error("Salesforce认证异常，错误代码: {}, 错误信息: {}", 
                e.getErrorCode(), e.getMessage(), e);
            return AjaxResult.error("认证失败: " + e.getMessage());
        } catch (Exception e) {
            logger.error("绑定令牌失败，令牌前缀: {}", accessToken.substring(0, Math.min(10, accessToken.length())), e);
            return AjaxResult.error("绑定令牌失败: " + e.getMessage());
        } finally {
            // 清理MDC上下文
            MDC.remove("traceId");
        }
    }

    /**
     * 检查令牌绑定
     * 
     * @param accessToken 访问令牌
     * @param deviceId 设备ID
     * @param ip IP地址
     * @return 绑定是否匹配
     */
    @Operation(summary = "检查令牌绑定", description = "检查令牌是否与指定设备和IP匹配")
    @GetMapping("/check-binding")
    public AjaxResult checkTokenBinding(@RequestParam String accessToken, 
                                      @RequestParam(required = false) String deviceId, 
                                      @RequestParam(required = false) String ip) {
        // 添加跟踪ID到日志上下文
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        
        logger.info("检查令牌绑定，令牌前缀: {}, 设备ID: {}, IP: {}", 
            accessToken.substring(0, Math.min(10, accessToken.length())), deviceId, ip);
        try {
            boolean bound = tokenManager.checkTokenBinding(accessToken, deviceId, ip);
            if (bound) {
                logger.info("令牌绑定匹配，令牌前缀: {}", accessToken.substring(0, Math.min(10, accessToken.length())));
                return AjaxResult.success("令牌绑定匹配");
            } else {
                logger.warn("令牌绑定不匹配，令牌前缀: {}", accessToken.substring(0, Math.min(10, accessToken.length())));
                return AjaxResult.error("令牌绑定不匹配");
            }
        } catch (SalesforceAuthException e) {
            logger.error("Salesforce认证异常，错误代码: {}, 错误信息: {}", 
                e.getErrorCode(), e.getMessage(), e);
            return AjaxResult.error("认证失败: " + e.getMessage());
        } catch (Exception e) {
            logger.error("检查令牌绑定失败，令牌前缀: {}", accessToken.substring(0, Math.min(10, accessToken.length())), e);
            return AjaxResult.error("检查令牌绑定失败: " + e.getMessage());
        } finally {
            // 清理MDC上下文
            MDC.remove("traceId");
        }
    }

    /**
     * 获取当前登录会话信息
     * 
     * @return 当前登录会话
     */
    @Operation(summary = "获取当前会话信息", description = "获取当前登录成功的会话详细信息")
    @GetMapping("/session")
    public AjaxResult getCurrentLoginSession() {
        // 添加跟踪ID到日志上下文
        String traceId = UUID.randomUUID().toString();
        MDC.put("traceId", traceId);
        
        logger.info("获取当前登录会话信息");
        try {
            DataiSfLoginSession session = salesforceLoginService.getCurrentLoginSession();
            if (session != null) {
                logger.info("获取登录会话信息成功，会话ID: {}", session.getSessionId());
                return AjaxResult.success("获取成功", session);
            } else {
                logger.warn("获取登录会话信息失败，当前没有活跃的登录会话");
                return AjaxResult.error("当前没有活跃的登录会话");
            }
        } catch (SalesforceAuthException e) {
            logger.error("Salesforce认证异常，错误代码: {}, 错误信息: {}", 
                e.getErrorCode(), e.getMessage(), e);
            return AjaxResult.error("认证失败: " + e.getMessage());
        } catch (Exception e) {
            logger.error("获取当前登录会话信息失败", e);
            return AjaxResult.error("获取当前登录会话信息失败: " + e.getMessage());
        } finally {
            // 清理MDC上下文
            MDC.remove("traceId");
        }
    }
}