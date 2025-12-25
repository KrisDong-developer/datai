package com.datai.auth.domain;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 失败登录对象 datai_sf_failed_login
 * 
 * @author datai
 * @date 2025-12-24
 */
@Schema(description = "失败登录对象")
public class DataiSfFailedLogin extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 记录ID */
    @Schema(title = "记录ID")
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

    /** 失败时间 */
    @Schema(title = "失败时间")
    @Excel(name = "失败时间")
    private LocalDateTime failedTime;

    /** 失败原因 */
    @Schema(title = "失败原因")
    @Excel(name = "失败原因")
    private String failedReason;

    /** IP地址 */
    @Schema(title = "IP地址")
    @Excel(name = "IP地址")
    private String ipAddress;

    /** 设备信息 */
    @Schema(title = "设备信息")
    @Excel(name = "设备信息")
    private String deviceInfo;

    /** 锁定状态 */
    @Schema(title = "锁定状态")
    @Excel(name = "锁定状态")
    private String lockStatus;

    /** 锁定时间 */
    @Schema(title = "锁定时间")
    @Excel(name = "锁定时间")
    private LocalDateTime lockTime;

    /** 解锁时间 */
    @Schema(title = "解锁时间")
    @Excel(name = "解锁时间")
    private LocalDateTime unlockTime;

    /** 锁定原因 */
    @Schema(title = "锁定原因")
    @Excel(name = "锁定原因")
    private String lockReason;
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


    public void setFailedTime(LocalDateTime failedTime) 
    {
        this.failedTime = failedTime;
    }

    public LocalDateTime getFailedTime() 
    {
        return failedTime;
    }


    public void setFailedReason(String failedReason) 
    {
        this.failedReason = failedReason;
    }

    public String getFailedReason() 
    {
        return failedReason;
    }


    public void setIpAddress(String ipAddress) 
    {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() 
    {
        return ipAddress;
    }


    public void setDeviceInfo(String deviceInfo) 
    {
        this.deviceInfo = deviceInfo;
    }

    public String getDeviceInfo() 
    {
        return deviceInfo;
    }


    public void setLockStatus(String lockStatus) 
    {
        this.lockStatus = lockStatus;
    }

    public String getLockStatus() 
    {
        return lockStatus;
    }


    public void setLockTime(LocalDateTime lockTime) 
    {
        this.lockTime = lockTime;
    }

    public LocalDateTime getLockTime() 
    {
        return lockTime;
    }


    public void setUnlockTime(LocalDateTime unlockTime) 
    {
        this.unlockTime = unlockTime;
    }

    public LocalDateTime getUnlockTime() 
    {
        return unlockTime;
    }


    public void setLockReason(String lockReason) 
    {
        this.lockReason = lockReason;
    }

    public String getLockReason() 
    {
        return lockReason;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deptId", getDeptId())
            .append("username", getUsername())
            .append("loginType", getLoginType())
            .append("failedTime", getFailedTime())
            .append("failedReason", getFailedReason())
            .append("ipAddress", getIpAddress())
            .append("deviceInfo", getDeviceInfo())
            .append("lockStatus", getLockStatus())
            .append("lockTime", getLockTime())
            .append("unlockTime", getUnlockTime())
            .append("lockReason", getLockReason())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
