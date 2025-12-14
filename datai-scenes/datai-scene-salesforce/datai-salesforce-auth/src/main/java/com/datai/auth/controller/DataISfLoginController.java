package com.datai.auth.controller;

import com.datai.auth.domain.SalesforceLoginResult;
import com.datai.auth.domain.SalesforceLoginRequest;
import com.datai.auth.domain.DataiSfLoginSession;
import com.datai.auth.service.ISalesforceLoginService;
import com.datai.auth.service.ITokenManager;
import com.datai.common.core.domain.AjaxResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
        logger.info("执行Salesforce登录，登录类型: {}", request.getLoginType());
        try {
            SalesforceLoginResult result = salesforceLoginService.login(request);
            if (result.isSuccess()) {
                // 登录成功，将登录结果缓存到Redis，便于后续访问
                String accessToken = result.getAccessToken();
                return AjaxResult.success("登录成功", result);
            } else {
                return AjaxResult.error(result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("登录失败: {}", e.getMessage(), e);
            return AjaxResult.error("登录失败: " + e.getMessage());
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
        logger.info("执行刷新令牌操作，登录类型: {}", loginType);
        try {
            SalesforceLoginResult result = salesforceLoginService.refreshToken(refreshToken, loginType);
            if (result.isSuccess()) {
                // 刷新成功，更新缓存
                String accessToken = result.getAccessToken();

                return AjaxResult.success("令牌刷新成功", result);
            } else {
                return AjaxResult.error(result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("刷新令牌失败: {}", e.getMessage(), e);
            return AjaxResult.error("刷新令牌失败: " + e.getMessage());
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
        logger.info("执行登出操作，登录类型: {}", loginType);
        try {
            boolean success = salesforceLoginService.logout(accessToken, loginType);
            if (success) {
                return AjaxResult.success("登出成功");
            } else {
                return AjaxResult.error("登出失败");
            }
        } catch (Exception e) {
            logger.error("登出失败: {}", e.getMessage(), e);
            return AjaxResult.error("登出失败: " + e.getMessage());
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
        logger.info("获取当前登录状态");
        try {
            SalesforceLoginResult result = salesforceLoginService.getCurrentLoginStatus();
            return AjaxResult.success("获取成功", result);
        } catch (Exception e) {
            logger.error("获取登录状态失败: {}", e.getMessage(), e);
            return AjaxResult.error("获取登录状态失败: " + e.getMessage());
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
        logger.info("验证令牌有效性");
        try {
            boolean isValid = tokenManager.validateToken(accessToken);
            if (isValid) {
                return AjaxResult.success("令牌有效");
            } else {
                return AjaxResult.error("令牌无效");
            }
        } catch (Exception e) {
            logger.error("验证令牌失败: {}", e.getMessage(), e);
            return AjaxResult.error("验证令牌失败: " + e.getMessage());
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
        logger.info("绑定令牌到设备/IP");
        try {
            tokenManager.bindToken(accessToken, deviceId, ip);
            return AjaxResult.success("令牌绑定成功");
        } catch (Exception e) {
            logger.error("绑定令牌失败: {}", e.getMessage(), e);
            return AjaxResult.error("绑定令牌失败: " + e.getMessage());
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
        logger.info("检查令牌绑定");
        try {
            boolean bound = tokenManager.checkTokenBinding(accessToken, deviceId, ip);
            if (bound) {
                return AjaxResult.success("令牌绑定匹配");
            } else {
                return AjaxResult.error("令牌绑定不匹配");
            }
        } catch (Exception e) {
            logger.error("检查令牌绑定失败: {}", e.getMessage(), e);
            return AjaxResult.error("检查令牌绑定失败: " + e.getMessage());
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
        logger.info("获取当前登录会话信息");
        try {
            DataiSfLoginSession session = salesforceLoginService.getCurrentLoginSession();
            if (session != null) {
                return AjaxResult.success("获取成功", session);
            } else {
                return AjaxResult.error("当前没有活跃的登录会话");
            }
        } catch (Exception e) {
            logger.error("获取当前登录会话信息失败: {}", e.getMessage(), e);
            return AjaxResult.error("获取当前登录会话信息失败: " + e.getMessage());
        }
    }
}