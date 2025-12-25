package com.datai.auth.domain;

import java.time.LocalDate;
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
 * @date 2025-12-24
 */
@Schema(description = "登录统计对象")
public class DataiSfLoginStatistics extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 统计ID */
    @Schema(title = "统计ID")
    private Long id;

    /** 部门ID */
    @Schema(title = "部门ID")
    private Long deptId;

    /** 统计日期 */
    @Schema(title = "统计日期")
    @Excel(name = "统计日期")
    private LocalDate statDate;

    /** 统计小时 */
    @Schema(title = "统计小时")
    @Excel(name = "统计小时")
    private Integer statHour;

    /** 登录类型 */
    @Schema(title = "登录类型")
    @Excel(name = "登录类型")
    private String loginType;

    /** 成功次数 */
    @Schema(title = "成功次数")
    @Excel(name = "成功次数")
    private Integer successCount;

    /** 失败次数 */
    @Schema(title = "失败次数")
    @Excel(name = "失败次数")
    private Integer failedCount;

    /** 刷新次数 */
    @Schema(title = "刷新次数")
    @Excel(name = "刷新次数")
    private Integer refreshCount;

    /** 吊销次数 */
    @Schema(title = "吊销次数")
    @Excel(name = "吊销次数")
    private Integer revokeCount;
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


    public void setStatDate(LocalDate statDate) 
    {
        this.statDate = statDate;
    }

    public LocalDate getStatDate() 
    {
        return statDate;
    }


    public void setStatHour(Integer statHour) 
    {
        this.statHour = statHour;
    }

    public Integer getStatHour() 
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


    public void setSuccessCount(Integer successCount) 
    {
        this.successCount = successCount;
    }

    public Integer getSuccessCount() 
    {
        return successCount;
    }


    public void setFailedCount(Integer failedCount) 
    {
        this.failedCount = failedCount;
    }

    public Integer getFailedCount() 
    {
        return failedCount;
    }


    public void setRefreshCount(Integer refreshCount) 
    {
        this.refreshCount = refreshCount;
    }

    public Integer getRefreshCount() 
    {
        return refreshCount;
    }


    public void setRevokeCount(Integer revokeCount) 
    {
        this.revokeCount = revokeCount;
    }

    public Integer getRevokeCount() 
    {
        return revokeCount;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deptId", getDeptId())
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
