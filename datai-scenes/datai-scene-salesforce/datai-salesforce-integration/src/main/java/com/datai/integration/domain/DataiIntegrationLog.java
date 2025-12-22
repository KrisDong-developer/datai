package com.datai.integration.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 数据同步日志对象 datai_integration_log
 * 
 * @author datai
 * @date 2025-12-22
 */
@Schema(description = "数据同步日志对象")
public class DataiIntegrationLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 日志ID */
    @Schema(title = "日志ID")
    private Long id;

    /** 关联批次ID */
    @Schema(title = "关联批次ID")
    @Excel(name = "关联批次ID")
    private Long batchId;

    /** 对象API */
    @Schema(title = "对象API")
    @Excel(name = "对象API")
    private String objectApi;

    /** 记录ID */
    @Schema(title = "记录ID")
    @Excel(name = "记录ID")
    private String recordId;

    /** 操作类型: INSERT/UPDATE/DELETE/UPSERT */
    @Schema(title = "操作类型: INSERT/UPDATE/DELETE/UPSERT")
    @Excel(name = "操作类型: INSERT/UPDATE/DELETE/UPSERT")
    private String operationType;

    /** 操作状态: SUCCESS/FAILED/PARTIAL */
    @Schema(title = "操作状态: SUCCESS/FAILED/PARTIAL")
    @Excel(name = "操作状态: SUCCESS/FAILED/PARTIAL")
    private String operationStatus;

    /** 源数据(JSON格式) */
    @Schema(title = "源数据(JSON格式)")
    @Excel(name = "源数据(JSON格式)")
    private String sourceData;

    /** 目标数据(JSON格式) */
    @Schema(title = "目标数据(JSON格式)")
    @Excel(name = "目标数据(JSON格式)")
    private String targetData;

    /** 错误信息 */
    @Schema(title = "错误信息")
    @Excel(name = "错误信息")
    private String errorMessage;

    /** 执行时间(秒) */
    @Schema(title = "执行时间(秒)")
    @Excel(name = "执行时间(秒)")
    private BigDecimal executionTime;

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


    public void setRecordId(String recordId) 
    {
        this.recordId = recordId;
    }

    public String getRecordId() 
    {
        return recordId;
    }


    public void setOperationType(String operationType) 
    {
        this.operationType = operationType;
    }

    public String getOperationType() 
    {
        return operationType;
    }


    public void setOperationStatus(String operationStatus) 
    {
        this.operationStatus = operationStatus;
    }

    public String getOperationStatus() 
    {
        return operationStatus;
    }


    public void setSourceData(String sourceData) 
    {
        this.sourceData = sourceData;
    }

    public String getSourceData() 
    {
        return sourceData;
    }


    public void setTargetData(String targetData) 
    {
        this.targetData = targetData;
    }

    public String getTargetData() 
    {
        return targetData;
    }


    public void setErrorMessage(String errorMessage) 
    {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() 
    {
        return errorMessage;
    }


    public void setExecutionTime(BigDecimal executionTime) 
    {
        this.executionTime = executionTime;
    }

    public BigDecimal getExecutionTime() 
    {
        return executionTime;
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
            .append("recordId", getRecordId())
            .append("operationType", getOperationType())
            .append("operationStatus", getOperationStatus())
            .append("sourceData", getSourceData())
            .append("targetData", getTargetData())
            .append("errorMessage", getErrorMessage())
            .append("executionTime", getExecutionTime())
            .append("syncTime", getSyncTime())
            .append("createTime", getCreateTime())
            .append("tenantId", getTenantId())
            .toString();
    }
}
