package com.datai.integration.model.domain;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 实时同步日志对象 datai_integration_realtime_sync_log
 * 
 * @author datai
 * @date 2026-01-09
 */
@Schema(description = "实时同步日志对象")
public class DataiIntegrationRealtimeSyncLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 主键ID */
    @Schema(title = "主键ID")
    private Long id;

    /** 对象名称 */
    @Schema(title = "对象名称")
    @Excel(name = "对象名称")
    private String objectName;

    /** 记录ID */
    @Schema(title = "记录ID")
    @Excel(name = "记录ID")
    private String recordId;

    /** 操作类型  */
    @Schema(title = "操作类型 ")
    @Excel(name = "操作类型 ")
    private String operationType;

    /** 变更数据 */
    @Schema(title = "变更数据")
    @Excel(name = "变更数据")
    private String changeData;

    /** 同步状态 */
    @Schema(title = "同步状态")
    @Excel(name = "同步状态")
    private String syncStatus;

    /** 错误信息 */
    @Schema(title = "错误信息")
    @Excel(name = "错误信息")
    private String errorMessage;

    /** 重试次数 */
    @Schema(title = "重试次数")
    @Excel(name = "重试次数")
    private Integer retryCount;

    /** Salesforce时间戳 */
    @Schema(title = "Salesforce时间戳")
    @Excel(name = "Salesforce时间戳")
    private LocalDateTime salesforceTimestamp;

    /** 同步时间戳 */
    @Schema(title = "同步时间戳")
    @Excel(name = "同步时间戳")
    private LocalDateTime syncTimestamp;
    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }


    public void setObjectName(String objectName) 
    {
        this.objectName = objectName;
    }

    public String getObjectName() 
    {
        return objectName;
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


    public void setChangeData(String changeData) 
    {
        this.changeData = changeData;
    }

    public String getChangeData() 
    {
        return changeData;
    }


    public void setSyncStatus(String syncStatus) 
    {
        this.syncStatus = syncStatus;
    }

    public String getSyncStatus() 
    {
        return syncStatus;
    }


    public void setErrorMessage(String errorMessage) 
    {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() 
    {
        return errorMessage;
    }


    public void setRetryCount(Integer retryCount) 
    {
        this.retryCount = retryCount;
    }

    public Integer getRetryCount() 
    {
        return retryCount;
    }


    public void setSalesforceTimestamp(LocalDateTime salesforceTimestamp) 
    {
        this.salesforceTimestamp = salesforceTimestamp;
    }

    public LocalDateTime getSalesforceTimestamp() 
    {
        return salesforceTimestamp;
    }


    public void setSyncTimestamp(LocalDateTime syncTimestamp) 
    {
        this.syncTimestamp = syncTimestamp;
    }

    public LocalDateTime getSyncTimestamp() 
    {
        return syncTimestamp;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("objectName", getObjectName())
            .append("recordId", getRecordId())
            .append("operationType", getOperationType())
            .append("changeData", getChangeData())
            .append("syncStatus", getSyncStatus())
            .append("errorMessage", getErrorMessage())
            .append("retryCount", getRetryCount())
            .append("salesforceTimestamp", getSalesforceTimestamp())
            .append("syncTimestamp", getSyncTimestamp())
            .append("createTime", getCreateTime())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
