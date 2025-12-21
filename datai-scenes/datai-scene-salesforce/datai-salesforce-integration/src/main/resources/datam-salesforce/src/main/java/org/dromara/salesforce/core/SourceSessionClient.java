package org.dromara.salesforce.core;

import com.sforce.soap.partner.GetUserInfoResult;
import org.dromara.salesforce.exception.NotLoggedInException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;

/**
 * 源会话客户端类，用于处理与源Salesforce组织的连接和会话管理
 *
 * SourceSessionClient是数据加载器中用于管理与源Salesforce服务器连接的客户端类。
 * 它负责建立连接、管理会话、验证会话有效性以及提供会话相关信息的访问接口。
 *
 * 主要功能：
 * 1. 建立与源Salesforce组织的连接
 * 2. 管理会话信息（会话ID、服务器URL、用户信息等）
 * 3. 验证会话有效性
 * 4. 提供会话连接的访问接口
 *
 * 设计特点：
 * - 与目标会话客户端分离，支持双向数据迁移
 * - 集成Spring框架管理
 * - 提供详细的日志记录功能
 * - 异常处理机制完善
 */
public class SourceSessionClient {

    private static final Logger logger = LoggerFactory.getLogger(SourceSessionClient.class);

    /**
     * 会话ID
     */
    private String sessionId;

    /**
     * 服务器URL
     */
    private String serverUrl;

    /**
     * 用户信息结果
     */
    private GetUserInfoResult userInfo;

    /**
     * 上次活动时间（毫秒）
     */
    private long lastActivityTimeInMsec = 0;

    public boolean isSessionValid() {
        long currentTimeInMsec = Calendar.getInstance().getTimeInMillis();
        long inSessionElapsedTimeInSec = (currentTimeInMsec - this.lastActivityTimeInMsec)/1000;
        return (this.sessionId != null
            && userInfo != null
            && inSessionElapsedTimeInSec < userInfo.getSessionSecondsValid());
    }

    public void validate() throws NotLoggedInException {
        if (!isSessionValid()) throw new NotLoggedInException("");
    }

    public String getSessionId() {
        return this.sessionId;
    }

    public String getServer() {
        return this.serverUrl;
    }

    public GetUserInfoResult getUserInfoResult() {
        return this.userInfo;
    }

    public void performedSessionActivity() {
        this.lastActivityTimeInMsec = Calendar.getInstance().getTimeInMillis();
    }
    
    // Setter methods for session management
    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }
    
    public void setServerUrl(String serverUrl) {
        this.serverUrl = serverUrl;
    }
    
    public void setUserInfo(GetUserInfoResult userInfo) {
        this.userInfo = userInfo;
    }
    
    public void setLastActivityTime(long lastActivityTimeInMsec) {
        this.lastActivityTimeInMsec = lastActivityTimeInMsec;
    }
    
    public long getLastActivityTime() {
        return this.lastActivityTimeInMsec;
    }
}