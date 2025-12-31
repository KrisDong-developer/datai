package com.datai.auth.strategy.impl;

import com.datai.auth.model.domain.SalesforceLoginResult;
import com.datai.auth.model.domain.SalesforceLoginRequest;
import com.datai.auth.strategy.LoginStrategy;
import com.datai.salesforce.common.exception.SalesforceCliLoginException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Salesforce CLI登录策略实现
 * 支持通过Salesforce CLI获取和使用Session
 * 
 * @author datai
 * @date 2025-12-14
 */
@Component
public class SalesforceCliLoginStrategy implements LoginStrategy {
    
    private static final Logger logger = LoggerFactory.getLogger(SalesforceCliLoginStrategy.class);
    
    @Override
    public SalesforceLoginResult login(SalesforceLoginRequest request) {
        logger.info("执行Salesforce CLI登录，登录类型：{}", request.getLoginType());
        
        // 这里实现具体的Salesforce CLI登录逻辑
        SalesforceLoginResult result = new SalesforceLoginResult();
        
        try {
            // 1. 检查Salesforce CLI是否安装
            if (!isSalesforceCliInstalled()) {
                throw new SalesforceCliLoginException("CLI_NOT_INSTALLED", "Salesforce CLI not installed");
            }
            
            // 2. 获取Session ID
            String[] command;
            if (request.getOrgAlias() != null && !request.getOrgAlias().trim().isEmpty()) {
                // 使用指定的组织别名
                command = new String[] { "sf", "org", "display", "--alias", request.getOrgAlias(), "--json" };
            } else {
                // 使用默认组织
                command = new String[] { "sf", "org", "display", "--json" };
            }
            
            String output = executeCommand(command);
            logger.debug("Salesforce CLI输出: {}", output);
            
            // 3. 解析CLI输出，获取Session信息
            result = parseCliOutput(output);
            result.setSuccess(true);
        } catch (Exception e) {
            logger.error("Salesforce CLI登录失败: {}", e.getMessage(), e);
            result.setSuccess(false);
            if (e instanceof SalesforceCliLoginException) {
                result.setErrorMessage(e.getMessage());
                result.setErrorCode(((SalesforceCliLoginException) e).getErrorCode());
            } else {
                result.setErrorMessage(e.getMessage());
                result.setErrorCode("CLI_LOGIN_FAILED");
            }
        }
        
        return result;
    }
    
    @Override
    public SalesforceLoginResult refreshToken(String refreshToken, String loginType) {
        logger.info("执行Salesforce CLI刷新Session操作");
        
        // Salesforce CLI会自动处理Session刷新，所以这里只需要重新获取Session即可
        SalesforceLoginResult result = new SalesforceLoginResult();
        
        try {
            // 1. 执行CLI命令获取最新Session
            String[] command = new String[] { "sf", "org", "display", "--json" };
            String output = executeCommand(command);
            
            // 2. 解析CLI输出，获取新的Session信息
            result = parseCliOutput(output);
            result.setSuccess(true);
        } catch (Exception e) {
            logger.error("Salesforce CLI刷新Session失败: {}", e.getMessage(), e);
            result.setSuccess(false);
            if (e instanceof SalesforceCliLoginException) {
                result.setErrorMessage(e.getMessage());
                result.setErrorCode(((SalesforceCliLoginException) e).getErrorCode());
            } else {
                result.setErrorMessage(e.getMessage());
                result.setErrorCode("CLI_REFRESH_FAILED");
            }
        }
        
        return result;
    }
    
    @Override
    public boolean logout(String sessionId, String loginType) {
        logger.info("执行Salesforce CLI登出操作");
        
        try {
            // 1. 执行CLI登出命令
            String[] command = new String[] { "sf", "org", "logout", "--json" };
            String output = executeCommand(command);
            logger.debug("Salesforce CLI登出输出: {}", output);
            
            // 2. 解析登出结果
            return output.contains("\"status\": 0");
        } catch (Exception e) {
            logger.error("Salesforce CLI登出失败: {}", e.getMessage(), e);
            return false;
        }
    }
    
    @Override
    public String getLoginType() {
        return "salesforce_cli";
    }
    
    /**
     * 检查Salesforce CLI是否安装
     * 
     * @return CLI是否安装
     */
    private boolean isSalesforceCliInstalled() {
        try {
            String[] command = new String[] { "sf", "--version" };
            executeCommand(command);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
    
    /**
     * 执行命令行命令
     * 
     * @param command 命令数组
     * @return 命令输出
     * @throws Exception 执行异常
     */
    private String executeCommand(String[] command) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder(command);
        processBuilder.redirectErrorStream(true); // 将错误流合并到输出流
        Process process = processBuilder.start();
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
            StringBuilder output = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                output.append(line).append(System.lineSeparator());
            }
            
            int exitCode = process.waitFor();
            if (exitCode != 0) {
                throw new SalesforceCliLoginException("CLI_COMMAND_FAILED", "Command failed with exit code " + exitCode + ": " + output.toString().trim());
            }
            
            return output.toString().trim();
        }
    }
    
    /**
     * 解析Salesforce CLI输出，提取Session信息
     * 
     * @param output CLI输出
     * @return 登录结果
     * @throws SalesforceCliLoginException 解析失败时抛出
     */
    private SalesforceLoginResult parseCliOutput(String output) throws SalesforceCliLoginException {
        SalesforceLoginResult result = new SalesforceLoginResult();
        
        // 简化实现，使用正则表达式提取关键信息
        // 实际实现中应该使用JSON解析库解析输出
        
        // 提取实例URL
        Pattern instanceUrlPattern = Pattern.compile("instanceUrl.*?:.*?\"(.*?)\"");
        Matcher instanceUrlMatcher = instanceUrlPattern.matcher(output);
        if (instanceUrlMatcher.find()) {
            result.setInstanceUrl(instanceUrlMatcher.group(1));
        }
        
        // 提取Session
        Pattern sessionIdPattern = Pattern.compile("accessToken.*?:.*?\"(.*?)\"");
        Matcher sessionIdMatcher = sessionIdPattern.matcher(output);
        if (sessionIdMatcher.find()) {
            result.setSessionId(sessionIdMatcher.group(1));
        } else {
            // 尝试其他可能的Session字段名
            Pattern tokenPattern = Pattern.compile("token.*?:.*?\"(.*?)\"");
            Matcher tokenMatcher = tokenPattern.matcher(output);
            if (tokenMatcher.find()) {
                result.setSessionId(tokenMatcher.group(1));
            } else {
                throw new SalesforceCliLoginException("CLI_PARSE_ERROR", "Failed to extract session from CLI output");
            }
        }
        
        // 提取组织ID
        Pattern orgIdPattern = Pattern.compile("orgId.*?:.*?\"(.*?)\"");
        Matcher orgIdMatcher = orgIdPattern.matcher(output);
        if (orgIdMatcher.find()) {
            result.setOrganizationId(orgIdMatcher.group(1));
        }
        
        // 提取用户ID
        Pattern userIdPattern = Pattern.compile("userId.*?:.*?\"(.*?)\"");
        Matcher userIdMatcher = userIdPattern.matcher(output);
        if (userIdMatcher.find()) {
            result.setUserId(userIdMatcher.group(1));
        }
        
        // 设置默认值
        if (result.getInstanceUrl() == null) {
            result.setInstanceUrl("https://instance.salesforce.com");
        }
        if (result.getOrganizationId() == null) {
            result.setOrganizationId("00Dxxxxxxxxxxxx");
        }
        
        result.setTokenType("Bearer");
        result.setExpiresIn(7200);
        
        return result;
    }
}