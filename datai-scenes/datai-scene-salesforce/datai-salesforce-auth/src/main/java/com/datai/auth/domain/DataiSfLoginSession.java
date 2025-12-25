package com.datai.auth.domain;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 登录会话对象 datai_sf_login_session
 * 
 * @author datai
 * @date 2025-12-24
 */
@Schema(description = "登录会话对象")
public class DataiSfLoginSession extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 会话ID */
    @Schema(title = "会话ID")
    private Long id;

    /** 部门ID */
    @Schema(title = "部门ID")
    private Long deptId;

    /** 用户名 */
    @Schema(title = "用户名")
    @Excel(name = "用户名")
    private String username;

    /** 登录类型 */
    @Schema(title = "登录类型")
    @Excel(name = "登录类型")
    private String loginType;

    /** 会话状态 */
    @Schema(title = "会话状态")
    @Excel(name = "会话状态")
    private String status;

    /** 登录时间 */
    @Schema(title = "登录时间")
    @Excel(name = "登录时间")
    private LocalDateTime loginTime;

    /** 过期时间 */
    @Schema(title = "过期时间")
    @Excel(name = "过期时间")
    private LocalDateTime expireTime;

    /** 最后活动时间 */
    @Schema(title = "最后活动时间")
    @Excel(name = "最后活动时间")
    private LocalDateTime lastActivityTime;

    /** 登录IP */
    @Schema(title = "登录IP")
    @Excel(name = "登录IP")
    private String loginIp;

    /** 设备信息 */
    @Schema(title = "设备信息")
    @Excel(name = "设备信息")
    private String deviceInfo;

    /** 浏览器信息 */
    @Schema(title = "浏览器信息")
    @Excel(name = "浏览器信息")
    private String browserInfo;

    /** 会话标识 */
    @Schema(title = "会话标识")
    @Excel(name = "会话标识")
    private String sessionId;

    /** 令牌ID */
    @Schema(title = "令牌ID")
    @Excel(name = "令牌ID")
    private Long tokenId;
    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }


    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }


    public void setUsername(String username) 
    {
        this.username = username;
    }

    public String getUsername() 
    {
        return username;
    }


    public void setLoginType(String loginType) 
    {
        this.loginType = loginType;
    }

    public String getLoginType() 
    {
        return loginType;
    }


    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }


    public void setLoginTime(LocalDateTime loginTime) 
    {
        this.loginTime = loginTime;
    }

    public LocalDateTime getLoginTime() 
    {
        return loginTime;
    }


    public void setExpireTime(LocalDateTime expireTime) 
    {
        this.expireTime = expireTime;
    }

    public LocalDateTime getExpireTime() 
    {
        return expireTime;
    }


    public void setLastActivityTime(LocalDateTime lastActivityTime) 
    {
        this.lastActivityTime = lastActivityTime;
    }

    public LocalDateTime getLastActivityTime() 
    {
        return lastActivityTime;
    }


    public void setLoginIp(String loginIp) 
    {
        this.loginIp = loginIp;
    }

    public String getLoginIp() 
    {
        return loginIp;
    }


    public void setDeviceInfo(String deviceInfo) 
    {
        this.deviceInfo = deviceInfo;
    }

    public String getDeviceInfo() 
    {
        return deviceInfo;
    }


    public void setBrowserInfo(String browserInfo) 
    {
        this.browserInfo = browserInfo;
    }

    public String getBrowserInfo() 
    {
        return browserInfo;
    }


    public void setSessionId(String sessionId) 
    {
        this.sessionId = sessionId;
    }

    public String getSessionId() 
    {
        return sessionId;
    }


    public void setTokenId(Long tokenId) 
    {
        this.tokenId = tokenId;
    }

    public Long getTokenId() 
    {
        return tokenId;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deptId", getDeptId())
            .append("username", getUsername())
            .append("loginType", getLoginType())
            .append("status", getStatus())
            .append("loginTime", getLoginTime())
            .append("expireTime", getExpireTime())
            .append("lastActivityTime", getLastActivityTime())
            .append("loginIp", getLoginIp())
            .append("deviceInfo", getDeviceInfo())
            .append("browserInfo", getBrowserInfo())
            .append("sessionId", getSessionId())
            .append("tokenId", getTokenId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
