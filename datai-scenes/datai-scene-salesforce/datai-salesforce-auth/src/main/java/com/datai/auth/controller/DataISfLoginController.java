package com.datai.auth.controller;

import com.datai.auth.model.domain.DataiSfLoginHistory;
import com.datai.auth.model.domain.SalesforceLoginRequest;
import com.datai.auth.model.domain.SalesforceLoginResult;
import com.datai.auth.service.IDataiSfLoginHistoryService;
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
 * 支持通过orgType区分源ORG和目标ORG
 * 
 * @author datai
 * @date 2025-12-14
 */
@Tag(name = "Salesforce登录接口", description = "登录相关操作（支持源ORG和目标ORG）")
@RestController
@RequestMapping("/salesforce/login")
public class DataISfLoginController extends BaseController {

    private static final Logger logger = LoggerFactory.getLogger(DataISfLoginController.class);

    @Autowired
    private ISalesforceLoginService salesforceLoginService;

    @Autowired
    private IDataiSfLoginHistoryService loginHistoryService;

    /**
     * 执行登录操作（源ORG登录）
     */
    @Operation(summary = "执行Salesforce登录操作（源ORG）")
    @PostMapping("/doLogin")
    public AjaxResult login(@RequestBody SalesforceLoginRequest request) {
        logger.info("接收到源ORG登录请求，登录类型: {}, 用户名: {}, ORG类型: {}", 
            request.getLoginType(), request.getUsername(), request.getOrgType());
        
        try {
            SalesforceLoginResult result = salesforceLoginService.login(request);
            
            if (result.isSuccess()) {
                logger.info("源ORG登录成功，用户ID: {}, 组织ID: {}, ORG类型: {}", 
                    result.getUserId(), result.getOrganizationId(), result.getOrgType());
                return AjaxResult.success("登录成功", result);
            } else {
                logger.warn("源ORG登录失败，错误码: {}, 错误信息: {}", 
                    result.getErrorCode(), result.getErrorMessage());
                return AjaxResult.error(result.getErrorCode(), result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("源ORG登录请求处理失败: {}", e.getMessage(), e);
            return AjaxResult.error("登录失败: " + e.getMessage());
        }
    }

    /**
     * 执行登出操作（源ORG登出）
     */
    @Operation(summary = "执行Salesforce登出操作（源ORG）")
    @PostMapping("/logout")
    public AjaxResult logout() {
        logger.info("接收到源ORG登出请求");
        
        try {
            DataiSfLoginHistory latestLoginHistory = loginHistoryService.selectLatestSuccessLoginHistory();
            
            if (latestLoginHistory == null) {
                logger.warn("未找到源ORG登录历史记录");
                return AjaxResult.error("未找到登录历史记录");
            }
            
            String sessionId = latestLoginHistory.getSessionIdResult();
            String loginType = latestLoginHistory.getLoginType();
            String orgType = latestLoginHistory.getOrgType();
            
            logger.info("从源ORG登录历史中获取到Session ID: {}, 登录类型: {}, ORG类型: {}", 
                sessionId, loginType, orgType);
            
            boolean success = salesforceLoginService.logout(sessionId, loginType, orgType);
            
            if (success) {
                logger.info("源ORG登出成功，Session ID: {}, ORG类型: {}", sessionId, orgType);
                return AjaxResult.success("登出成功");
            } else {
                logger.warn("源ORG登出失败，Session ID: {}, ORG类型: {}", sessionId, orgType);
                return AjaxResult.error("登出失败");
            }
        } catch (Exception e) {
            logger.error("源ORG登出请求处理失败: {}", e.getMessage(), e);
            return AjaxResult.error("登出失败: " + e.getMessage());
        }
    }

    /**
     * 获取当前登录信息
     */
    @Operation(summary = "获取当前登录信息")
    @GetMapping("/current")
    public AjaxResult getCurrentLoginInfo(@RequestParam String orgType) {
        logger.info("接收到获取登录信息请求，ORG类型: {}", orgType);
        
        try {
            SalesforceLoginResult result = salesforceLoginService.getCurrentLoginResultByOrgType(orgType);
            
            if (result != null) {
                return AjaxResult.success("获取登录信息成功", result);
            } else {
                return AjaxResult.error("未找到登录信息");
            }
        } catch (Exception e) {
            logger.error("获取登录信息请求处理失败: {}", e.getMessage(), e);
            return AjaxResult.error("获取登录信息失败: " + e.getMessage());
        }
    }

    /**
     * 自动登录（支持源ORG和目标ORG）
     */
    @Operation(summary = "自动登录（支持源ORG和目标ORG）")
    @PostMapping("/autoLogin")
    public AjaxResult autoLogin(@RequestParam String orgType) {
        logger.info("接收到自动登录请求，ORG类型: {}", orgType);
        
        try {
            DataiSfLoginHistory latestLoginHistory = loginHistoryService.selectLatestSuccessLoginHistoryByOrgType(orgType);
            
            if (latestLoginHistory == null) {
                logger.warn("未找到ORG类型为 {} 的登录历史记录", orgType);
                return AjaxResult.error("未找到ORG类型为 " + orgType + " 的登录历史记录");
            }
            
            Long historyId = latestLoginHistory.getId();
            String historyOrgType = latestLoginHistory.getOrgType();
            logger.info("从登录历史中获取到历史ID: {}, ORG类型: {}", historyId, historyOrgType);
            
            SalesforceLoginResult result = salesforceLoginService.autoLogin(historyId);
            
            if (result.isSuccess()) {
                logger.info("自动登录成功，用户ID: {}, 组织ID: {}, ORG类型: {}", 
                    result.getUserId(), result.getOrganizationId(), result.getOrgType());
                return AjaxResult.success("自动登录成功", result);
            } else {
                logger.warn("自动登录失败，错误码: {}, 错误信息: {}", 
                    result.getErrorCode(), result.getErrorMessage());
                return AjaxResult.error(result.getErrorCode(), result.getErrorMessage());
            }
        } catch (Exception e) {
            logger.error("自动登录请求处理失败: {}", e.getMessage(), e);
            return AjaxResult.error("自动登录失败: " + e.getMessage());
        }
    }
}