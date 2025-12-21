package org.dromara.salesforce.service;

import com.sforce.ws.ConnectionException;
import org.dromara.salesforce.exception.NotLoggedInException;

/**
 * 会话管理服务接口 - 提供源ORG和目标ORG的登录与登出功能
 * 
 * ISessionManagementService是Data Loader中负责处理Salesforce会话管理的服务接口。
 * 它定义了登录和登出操作所需的所有方法，支持源ORG和目标ORG的独立会话管理。
 * 
 * 主要功能：
 * 1. 提供源ORG和目标ORG的登录功能
 * 2. 提供源ORG和目标ORG的登出功能
 * 3. 管理会话信息的缓存存储和清除
 * 
 * 设计特点：
 * - 遵循Spring Boot服务接口设计规范
 * - 提供源ORG和目标ORG的独立会话管理方法
 * - 统一异常处理，抛出ConnectionException和NotLoggedInException
 * - 集成Redis缓存进行会话信息存储
 * 
 * 使用场景：
 * - 需要管理Salesforce源ORG和目标ORG连接会话的场景
 * - 需要在Redis中缓存会话信息以支持分布式部署的场景
 * - 需要同时管理多个Salesforce组织连接的场景
 * 
 * @author Salesforce
 * @since 64.0.0
 */
public interface ISessionManagementService {

    /**
     * 源ORG登录（从配置中获取账号密码）
     * 
     * @throws ConnectionException 连接异常
     */
    void loginSource() throws ConnectionException;
    
    /**
     * 目标ORG登录（从配置中获取账号密码）
     * 
     * @throws ConnectionException 连接异常
     */
    void loginTarget() throws ConnectionException;
    
    /**
     * 源ORG登出
     * 
     * @throws NotLoggedInException 未登录异常
     */
    void logoutSource() throws NotLoggedInException;
    
    /**
     * 目标ORG登出
     * 
     * @throws NotLoggedInException 未登录异常
     */
    void logoutTarget() throws NotLoggedInException;
    
    /**
     * 检查源ORG是否已登录
     * 
     * @return boolean 是否已登录
     */
    boolean isSourceLoggedIn();
    
    /**
     * 检查目标ORG是否已登录
     * 
     * @return boolean 是否已登录
     */
    boolean isTargetLoggedIn();
}