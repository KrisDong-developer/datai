package com.datai.integration.model.domain;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * API限流监控对象 datai_integration_rate_limit
 * 
 * @author datai
 * @date 2026-01-01
 */
@Schema(description = "API限流监控对象")
public class DataiIntegrationRateLimit extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 主键ID */
    @Schema(title = "主键ID")
    private Long id;

    /** 部门ID */
    @Schema(title = "部门ID")
    private Long deptId;

    /** 接口类型 */
    @Schema(title = "接口类型")
    @Excel(name = "接口类型")
    private String apiType;

    /** 限制维度 */
    @Schema(title = "限制维度")
    @Excel(name = "限制维度")
    private String limitType;

    /** 已用额度 */
    @Schema(title = "已用额度")
    @Excel(name = "已用额度")
    private Integer currentUsage;

    /** 总额度 */
    @Schema(title = "总额度")
    @Excel(name = "总额度")
    private Integer maxLimit;

    /** 剩余额度 */
    @Schema(title = "剩余额度")
    @Excel(name = "剩余额度")
    private Integer remainingVal;

    /** 重置时间 */
    @Schema(title = "重置时间")
    @Excel(name = "重置时间")
    private LocalDateTime resetTime;

    /** 是否限流 */
    @Schema(title = "是否限流")
    @Excel(name = "是否限流")
    private Boolean isBlocked;
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


    public void setApiType(String apiType) 
    {
        this.apiType = apiType;
    }

    public String getApiType() 
    {
        return apiType;
    }


    public void setLimitType(String limitType) 
    {
        this.limitType = limitType;
    }

    public String getLimitType() 
    {
        return limitType;
    }


    public void setCurrentUsage(Integer currentUsage) 
    {
        this.currentUsage = currentUsage;
    }

    public Integer getCurrentUsage() 
    {
        return currentUsage;
    }


    public void setMaxLimit(Integer maxLimit) 
    {
        this.maxLimit = maxLimit;
    }

    public Integer getMaxLimit() 
    {
        return maxLimit;
    }


    public void setRemainingVal(Integer remainingVal) 
    {
        this.remainingVal = remainingVal;
    }

    public Integer getRemainingVal() 
    {
        return remainingVal;
    }


    public void setResetTime(LocalDateTime resetTime) 
    {
        this.resetTime = resetTime;
    }

    public LocalDateTime getResetTime() 
    {
        return resetTime;
    }


    public void setIsBlocked(Boolean isBlocked) 
    {
        this.isBlocked = isBlocked;
    }

    public Boolean getIsBlocked() 
    {
        return isBlocked;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deptId", getDeptId())
            .append("apiType", getApiType())
            .append("limitType", getLimitType())
            .append("currentUsage", getCurrentUsage())
            .append("maxLimit", getMaxLimit())
            .append("remainingVal", getRemainingVal())
            .append("resetTime", getResetTime())
            .append("isBlocked", getIsBlocked())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
