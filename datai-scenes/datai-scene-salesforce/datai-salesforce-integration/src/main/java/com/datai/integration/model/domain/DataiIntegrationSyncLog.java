package com.datai.integration.model.domain;

import java.math.BigDecimal;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 数据同步日志对象 datai_integration_sync_log
 * 
 * @author datai
 * @date 2026-01-01
 */
@Schema(description = "数据同步日志对象")
public class DataiIntegrationSyncLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 日志ID */
    @Schema(title = "日志ID")
    private Long id;

    /** 关联批次ID */
    @Schema(title = "关联批次ID")
    @Excel(name = "关联批次ID")
    private Integer batchId;

    /** 对象API */
    @Schema(title = "对象API")
    @Excel(name = "对象API")
    private String objectApi;

    /** SF记录ID */
    @Schema(title = "SF记录ID")
    @Excel(name = "SF记录ID")
    private String recordId;

    /** 操作类型 */
    @Schema(title = "操作类型")
    @Excel(name = "操作类型")
    private String operationType;

    /** 操作状态 */
    @Schema(title = "操作状态")
    @Excel(name = "操作状态")
    private String operationStatus;

    /** 错误信息 */
    @Schema(title = "错误信息")
    @Excel(name = "错误信息")
    private String errorMessage;

    /** 执行时间(秒) */
    @Schema(title = "执行时间(秒)")
    @Excel(name = "执行时间(秒)")
    private BigDecimal executionTime;

    /** 部门ID */
    @Schema(title = "部门ID")
    private Long deptId;
    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }


    public void setBatchId(Integer batchId) 
    {
        this.batchId = batchId;
    }

    public Integer getBatchId() 
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


    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
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
            .append("errorMessage", getErrorMessage())
            .append("executionTime", getExecutionTime())
            .append("deptId", getDeptId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
