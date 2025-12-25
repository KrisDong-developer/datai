package com.datai.auth.controller;

import com.datai.auth.domain.DataiSfLoginSession;
import com.datai.auth.domain.SalesforceLoginRequest;
import com.datai.auth.domain.SalesforceLoginResult;
import com.datai.auth.service.ISalesforceLoginService;
import com.datai.common.core.controller.BaseController;
import com.datai.common.core.domain.AjaxResult;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * Salesforce登录控制器
 * 提供登录、刷新Session、登出等接口
 * 
 * @author datai
 * @date 2025-12-14
 */
@Tag(name = "Salesforce登录接口", description = "登录相关操作")
@RestController
@RequestMapping("/salesforce/login")
public class DataISfLoginController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(DataISfLoginController.class);

    @Autowired
    private ISalesforceLoginService salesforceLoginService;

    /**
     * 执行登录操作
     */
    @Operation(summary = "执行Salesforce登录操作")
    @PostMapping("/doLogin")
    public AjaxResult login(@RequestBody SalesforceLoginRequest request) {
        logger.info("接收到登录请求，登录类型: {}, 用户名: {}", request.getLoginType(), request.getUsername());
        
        try {
            SalesforceLoginResult result = salesforceLoginService.login(request);
            
            if (result.isSuccess()) {
                return AjaxResult.success("登录成功", result);
            } else {
                return AjaxResult.error(result.getErrorCode(), result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("登录请求处理失败: {}", e.getMessage(), e);
            return AjaxResult.error("登录失败: " + e.getMessage());
        }
    }

    /**
     * 执行登出操作
     */
    @Operation(summary = "执行Salesforce登出操作")
    @PostMapping("/logout")
    public AjaxResult logout(@RequestParam String sessionId, @RequestParam String loginType) {
        logger.info("接收到登出请求，Session ID: {}, 登录类型: {}", sessionId, loginType);
        
        try {
            boolean success = salesforceLoginService.logout(sessionId, loginType);
            
            if (success) {
                return AjaxResult.success("登出成功");
            } else {
                return AjaxResult.error("登出失败");
            }
        } catch (Exception e) {
            logger.error("登出请求处理失败: {}", e.getMessage(), e);
            return AjaxResult.error("登出失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前登录信息
     */
    @Operation(summary = "获取当前登录信息")
    @GetMapping("/current")
    public AjaxResult getCurrentLoginInfo(@RequestParam String sessionId) {
        logger.info("接收到获取登录信息请求，Session ID: {}", sessionId);
        
        try {
            DataiSfLoginSession session = salesforceLoginService.getCurrentLoginInfo(sessionId);
            
            if (session != null) {
                return AjaxResult.success("获取登录信息成功", session);
            } else {
                return AjaxResult.error("未找到登录信息");
            }
        } catch (Exception e) {
            logger.error("获取登录信息请求处理失败: {}", e.getMessage(), e);
            return AjaxResult.error("获取登录信息失败: " + e.getMessage());
        }
    }

    /**
     * 自动登录
     */
    @Operation(summary = "自动登录")
    @PostMapping("/autoLogin")
    public AjaxResult autoLogin(@RequestParam Long historyId) {
        logger.info("接收到自动登录请求，历史ID: {}", historyId);
        
        try {
            SalesforceLoginResult result = salesforceLoginService.autoLogin(historyId);
            
            if (result.isSuccess()) {
                return AjaxResult.success("自动登录成功", result);
            } else {
                return AjaxResult.error(result.getErrorCode(), result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("自动登录请求处理失败: {}", e.getMessage(), e);
            return AjaxResult.error("自动登录失败: " + e.getMessage());
        }
    }
}