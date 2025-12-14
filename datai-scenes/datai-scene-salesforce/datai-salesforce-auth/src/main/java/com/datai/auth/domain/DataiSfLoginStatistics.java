package com.datai.auth.domain;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 登录统计对象 datai_sf_login_statistics
 * 
 * @author datai
 * @date 2025-12-14
 */
@Schema(description = "登录统计对象")
public class DataiSfLoginStatistics extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 统计ID */
    @Schema(title = "统计ID")
    private Long statId;

    /** 租户编号 */
    @Schema(title = "租户编号")
    @Excel(name = "租户编号")
    private String tenantId;

    /** 统计日期 */
    @Schema(title = "统计日期")
    @Excel(name = "统计日期")
    private LocalDateTime statDate;

    /** 统计小时 */
    @Schema(title = "统计小时")
    @Excel(name = "统计小时")
    private Long statHour;

    /** 登录类型 */
    @Schema(title = "登录类型")
    @Excel(name = "登录类型")
    private String loginType;

    /** 成功次数 */
    @Schema(title = "成功次数")
    @Excel(name = "成功次数")
    private Long successCount;

    /** 失败次数 */
    @Schema(title = "失败次数")
    @Excel(name = "失败次数")
    private Long failedCount;

    /** 刷新次数 */
    @Schema(title = "刷新次数")
    @Excel(name = "刷新次数")
    private Long refreshCount;

    /** 吊销次数 */
    @Schema(title = "吊销次数")
    @Excel(name = "吊销次数")
    private Long revokeCount;
    public void setStatId(Long statId) 
    {
        this.statId = statId;
    }

    public Long getStatId() 
    {
        return statId;
    }


    public void setTenantId(String tenantId) 
    {
        this.tenantId = tenantId;
    }

    public String getTenantId() 
    {
        return tenantId;
    }


    public void setStatDate(LocalDateTime statDate)
    {
        this.statDate = statDate;
    }

    public LocalDateTime getStatDate()
    {
        return statDate;
    }


    public void setStatHour(Long statHour) 
    {
        this.statHour = statHour;
    }

    public Long getStatHour() 
    {
        return statHour;
    }


    public void setLoginType(String loginType) 
    {
        this.loginType = loginType;
    }

    public String getLoginType() 
    {
        return loginType;
    }


    public void setSuccessCount(Long successCount) 
    {
        this.successCount = successCount;
    }

    public Long getSuccessCount() 
    {
        return successCount;
    }


    public void setFailedCount(Long failedCount) 
    {
        this.failedCount = failedCount;
    }

    public Long getFailedCount() 
    {
        return failedCount;
    }


    public void setRefreshCount(Long refreshCount) 
    {
        this.refreshCount = refreshCount;
    }

    public Long getRefreshCount() 
    {
        return refreshCount;
    }


    public void setRevokeCount(Long revokeCount) 
    {
        this.revokeCount = revokeCount;
    }

    public Long getRevokeCount() 
    {
        return revokeCount;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("statId", getStatId())
            .append("tenantId", getTenantId())
            .append("statDate", getStatDate())
            .append("statHour", getStatHour())
            .append("loginType", getLoginType())
            .append("successCount", getSuccessCount())
            .append("failedCount", getFailedCount())
            .append("refreshCount", getRefreshCount())
            .append("revokeCount", getRevokeCount())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
