package com.datai.integration.domain;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * API限流管理对象 datai_integration_rate_limit
 * 
 * @author datai
 * @date 2025-12-22
 */
@Schema(description = "API限流管理对象")
public class DataiIntegrationRateLimit extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 限流记录ID */
    @Schema(title = "限流记录ID")
    private Long id;

    /** API类型: SOAP/BULK_V1/BULK_V2 */
    @Schema(title = "API类型: SOAP/BULK_V1/BULK_V2")
    @Excel(name = "API类型: SOAP/BULK_V1/BULK_V2")
    private String apiType;

    /** 时间窗口开始 */
    @Schema(title = "时间窗口开始")
    @Excel(name = "时间窗口开始")
    private LocalDateTime windowStart;

    /** 时间窗口结束 */
    @Schema(title = "时间窗口结束")
    @Excel(name = "时间窗口结束")
    private LocalDateTime windowEnd;

    /** 请求计数 */
    @Schema(title = "请求计数")
    @Excel(name = "请求计数")
    private Long requestCount;

    /** 限制阈值 */
    @Schema(title = "限制阈值")
    @Excel(name = "限制阈值")
    private Long limitThreshold;

    /** 剩余请求次数 */
    @Schema(title = "剩余请求次数")
    @Excel(name = "剩余请求次数")
    private Long remainingRequests;

    /** 重置时间 */
    @Schema(title = "重置时间")
    @Excel(name = "重置时间")
    private LocalDateTime resetTime;

    /** 是否被限流 */
    @Schema(title = "是否被限流")
    @Excel(name = "是否被限流")
    private Integer isThrottled;

    /** 租户编号 */
    @Schema(title = "租户编号")
    @Excel(name = "租户编号")
    private String tenantId;
    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }


    public void setApiType(String apiType) 
    {
        this.apiType = apiType;
    }

    public String getApiType() 
    {
        return apiType;
    }


    public void setWindowStart(LocalDateTime windowStart) 
    {
        this.windowStart = windowStart;
    }

    public LocalDateTime getWindowStart() 
    {
        return windowStart;
    }


    public void setWindowEnd(LocalDateTime windowEnd) 
    {
        this.windowEnd = windowEnd;
    }

    public LocalDateTime getWindowEnd() 
    {
        return windowEnd;
    }


    public void setRequestCount(Long requestCount) 
    {
        this.requestCount = requestCount;
    }

    public Long getRequestCount() 
    {
        return requestCount;
    }


    public void setLimitThreshold(Long limitThreshold) 
    {
        this.limitThreshold = limitThreshold;
    }

    public Long getLimitThreshold() 
    {
        return limitThreshold;
    }


    public void setRemainingRequests(Long remainingRequests) 
    {
        this.remainingRequests = remainingRequests;
    }

    public Long getRemainingRequests() 
    {
        return remainingRequests;
    }


    public void setResetTime(LocalDateTime resetTime) 
    {
        this.resetTime = resetTime;
    }

    public LocalDateTime getResetTime() 
    {
        return resetTime;
    }


    public void setIsThrottled(Integer isThrottled) 
    {
        this.isThrottled = isThrottled;
    }

    public Integer getIsThrottled() 
    {
        return isThrottled;
    }


    public void setTenantId(String tenantId) 
    {
        this.tenantId = tenantId;
    }

    public String getTenantId() 
    {
        return tenantId;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("apiType", getApiType())
            .append("windowStart", getWindowStart())
            .append("windowEnd", getWindowEnd())
            .append("requestCount", getRequestCount())
            .append("limitThreshold", getLimitThreshold())
            .append("remainingRequests", getRemainingRequests())
            .append("resetTime", getResetTime())
            .append("isThrottled", getIsThrottled())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .append("tenantId", getTenantId())
            .toString();
    }
}
