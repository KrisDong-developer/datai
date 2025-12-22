package com.datai.integration.domain;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 同步性能监控对象 datai_integration_performance
 * 
 * @author datai
 * @date 2025-12-22
 */
@Schema(description = "同步性能监控对象")
public class DataiIntegrationPerformance extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 性能记录ID */
    @Schema(title = "性能记录ID")
    private Long id;

    /** 关联批次ID */
    @Schema(title = "关联批次ID")
    @Excel(name = "关联批次ID")
    private Long batchId;

    /** 对象API */
    @Schema(title = "对象API")
    @Excel(name = "对象API")
    private String objectApi;

    /** 同步类型: FULL/INCREMENTAL/INITIAL */
    @Schema(title = "同步类型: FULL/INCREMENTAL/INITIAL")
    @Excel(name = "同步类型: FULL/INCREMENTAL/INITIAL")
    private String syncType;

    /** API类型: SOAP/BULK_V1/BULK_V2 */
    @Schema(title = "API类型: SOAP/BULK_V1/BULK_V2")
    @Excel(name = "API类型: SOAP/BULK_V1/BULK_V2")
    private String apiType;

    /** 处理记录数 */
    @Schema(title = "处理记录数")
    @Excel(name = "处理记录数")
    private Long recordCount;

    /** 成功记录数 */
    @Schema(title = "成功记录数")
    @Excel(name = "成功记录数")
    private Long successCount;

    /** 失败记录数 */
    @Schema(title = "失败记录数")
    @Excel(name = "失败记录数")
    private Long failedCount;

    /** 总耗时(秒) */
    @Schema(title = "总耗时(秒)")
    @Excel(name = "总耗时(秒)")
    private BigDecimal totalTime;

    /** 平均每条记录耗时(秒) */
    @Schema(title = "平均每条记录耗时(秒)")
    @Excel(name = "平均每条记录耗时(秒)")
    private BigDecimal avgTimePerRecord;

    /** 吞吐量(记录/秒) */
    @Schema(title = "吞吐量(记录/秒)")
    @Excel(name = "吞吐量(记录/秒)")
    private BigDecimal throughput;

    /** API调用次数 */
    @Schema(title = "API调用次数")
    @Excel(name = "API调用次数")
    private Long apiCalls;

    /** 数据量(字节) */
    @Schema(title = "数据量(字节)")
    @Excel(name = "数据量(字节)")
    private Long dataVolume;

    /** 内存使用量(字节) */
    @Schema(title = "内存使用量(字节)")
    @Excel(name = "内存使用量(字节)")
    private Long memoryUsage;

    /** CPU使用率(%) */
    @Schema(title = "CPU使用率(%)")
    @Excel(name = "CPU使用率(%)")
    private BigDecimal cpuUsage;

    /** 同步日期 */
    @Schema(title = "同步日期")
    @Excel(name = "同步日期")
    private LocalDate syncDate;

    /** 同步时间 */
    @Schema(title = "同步时间")
    @Excel(name = "同步时间")
    private LocalDateTime syncTime;

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


    public void setBatchId(Long batchId) 
    {
        this.batchId = batchId;
    }

    public Long getBatchId() 
    {
        return batchId;
    }


    public void setObjectApi(String objectApi) 
    {
        this.objectApi = objectApi;
    }

    public String getObjectApi() 
    {
        return objectApi;
    }


    public void setSyncType(String syncType) 
    {
        this.syncType = syncType;
    }

    public String getSyncType() 
    {
        return syncType;
    }


    public void setApiType(String apiType) 
    {
        this.apiType = apiType;
    }

    public String getApiType() 
    {
        return apiType;
    }


    public void setRecordCount(Long recordCount) 
    {
        this.recordCount = recordCount;
    }

    public Long getRecordCount() 
    {
        return recordCount;
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


    public void setTotalTime(BigDecimal totalTime) 
    {
        this.totalTime = totalTime;
    }

    public BigDecimal getTotalTime() 
    {
        return totalTime;
    }


    public void setAvgTimePerRecord(BigDecimal avgTimePerRecord) 
    {
        this.avgTimePerRecord = avgTimePerRecord;
    }

    public BigDecimal getAvgTimePerRecord() 
    {
        return avgTimePerRecord;
    }


    public void setThroughput(BigDecimal throughput) 
    {
        this.throughput = throughput;
    }

    public BigDecimal getThroughput() 
    {
        return throughput;
    }


    public void setApiCalls(Long apiCalls) 
    {
        this.apiCalls = apiCalls;
    }

    public Long getApiCalls() 
    {
        return apiCalls;
    }


    public void setDataVolume(Long dataVolume) 
    {
        this.dataVolume = dataVolume;
    }

    public Long getDataVolume() 
    {
        return dataVolume;
    }


    public void setMemoryUsage(Long memoryUsage) 
    {
        this.memoryUsage = memoryUsage;
    }

    public Long getMemoryUsage() 
    {
        return memoryUsage;
    }


    public void setCpuUsage(BigDecimal cpuUsage) 
    {
        this.cpuUsage = cpuUsage;
    }

    public BigDecimal getCpuUsage() 
    {
        return cpuUsage;
    }


    public void setSyncDate(LocalDate syncDate) 
    {
        this.syncDate = syncDate;
    }

    public LocalDate getSyncDate() 
    {
        return syncDate;
    }


    public void setSyncTime(LocalDateTime syncTime) 
    {
        this.syncTime = syncTime;
    }

    public LocalDateTime getSyncTime() 
    {
        return syncTime;
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
            .append("batchId", getBatchId())
            .append("objectApi", getObjectApi())
            .append("syncType", getSyncType())
            .append("apiType", getApiType())
            .append("recordCount", getRecordCount())
            .append("successCount", getSuccessCount())
            .append("failedCount", getFailedCount())
            .append("totalTime", getTotalTime())
            .append("avgTimePerRecord", getAvgTimePerRecord())
            .append("throughput", getThroughput())
            .append("apiCalls", getApiCalls())
            .append("dataVolume", getDataVolume())
            .append("memoryUsage", getMemoryUsage())
            .append("cpuUsage", getCpuUsage())
            .append("syncDate", getSyncDate())
            .append("syncTime", getSyncTime())
            .append("tenantId", getTenantId())
            .toString();
    }
}
