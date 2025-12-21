package org.dromara.salesforce.core;

import com.sforce.soap.partner.GetUserInfoResult;
import org.dromara.salesforce.exception.NotLoggedInException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;


public class TargetSessionClient {

    private static final Logger logger = LoggerFactory.getLogger(TargetSessionClient.class);

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