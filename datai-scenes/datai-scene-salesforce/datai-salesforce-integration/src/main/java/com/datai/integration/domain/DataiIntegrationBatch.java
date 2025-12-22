package com.datai.integration.domain;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 数据批次对象 datai_integration_batch
 * 
 * @author datai
 * @date 2025-12-22
 */
@Schema(description = "数据批次对象")
public class DataiIntegrationBatch extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 批次ID */
    @Schema(title = "批次ID")
    private Long id;

    /** 对象API */
    @Schema(title = "对象API")
    @Excel(name = "对象API")
    private String api;

    /** 对象名称 */
    @Schema(title = "对象名称")
    @Excel(name = "对象名称")
    private String label;

    /** SF数据量 */
    @Schema(title = "SF数据量")
    @Excel(name = "SF数据量")
    private Long sfNum;

    /** 本地数据量 */
    @Schema(title = "本地数据量")
    @Excel(name = "本地数据量")
    private Long dbNum;

    /** 同步类型 */
    @Schema(title = "同步类型")
    @Excel(name = "同步类型")
    private String syncType;

    /** 批次字段 */
    @Schema(title = "批次字段")
    @Excel(name = "批次字段")
    private String batchField;

    /** 同步状态 1:正常 0:误差 */
    @Schema(title = "同步状态 1:正常 0:误差")
    @Excel(name = "同步状态 1:正常 0:误差")
    private Long syncStatus;

    /** 同步优先级 */
    @Schema(title = "同步优先级")
    @Excel(name = "同步优先级")
    private LocalDateTime syncIndex;

    /** 开始同步时间 */
    @Schema(title = "开始同步时间")
    @Excel(name = "开始同步时间")
    private LocalDateTime syncStartDate;

    /** 结束同步时间 */
    @Schema(title = "结束同步时间")
    @Excel(name = "结束同步时间")
    private LocalDateTime syncEndDate;

    /** 首次同步时间 */
    @Schema(title = "首次同步时间")
    @Excel(name = "首次同步时间")
    private LocalDateTime firstSyncTime;

    /** 最后同步时间 */
    @Schema(title = "最后同步时间")
    @Excel(name = "最后同步时间")
    private LocalDateTime lastSyncTime;

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


    public void setApi(String api) 
    {
        this.api = api;
    }

    public String getApi() 
    {
        return api;
    }


    public void setLabel(String label) 
    {
        this.label = label;
    }

    public String getLabel() 
    {
        return label;
    }


    public void setSfNum(Long sfNum) 
    {
        this.sfNum = sfNum;
    }

    public Long getSfNum() 
    {
        return sfNum;
    }


    public void setDbNum(Long dbNum) 
    {
        this.dbNum = dbNum;
    }

    public Long getDbNum() 
    {
        return dbNum;
    }


    public void setSyncType(String syncType) 
    {
        this.syncType = syncType;
    }

    public String getSyncType() 
    {
        return syncType;
    }


    public void setBatchField(String batchField) 
    {
        this.batchField = batchField;
    }

    public String getBatchField() 
    {
        return batchField;
    }


    public void setSyncStatus(Long syncStatus) 
    {
        this.syncStatus = syncStatus;
    }

    public Long getSyncStatus() 
    {
        return syncStatus;
    }


    public void setSyncIndex(LocalDateTime syncIndex) 
    {
        this.syncIndex = syncIndex;
    }

    public LocalDateTime getSyncIndex() 
    {
        return syncIndex;
    }


    public void setSyncStartDate(LocalDateTime syncStartDate) 
    {
        this.syncStartDate = syncStartDate;
    }

    public LocalDateTime getSyncStartDate() 
    {
        return syncStartDate;
    }


    public void setSyncEndDate(LocalDateTime syncEndDate) 
    {
        this.syncEndDate = syncEndDate;
    }

    public LocalDateTime getSyncEndDate() 
    {
        return syncEndDate;
    }


    public void setFirstSyncTime(LocalDateTime firstSyncTime) 
    {
        this.firstSyncTime = firstSyncTime;
    }

    public LocalDateTime getFirstSyncTime() 
    {
        return firstSyncTime;
    }


    public void setLastSyncTime(LocalDateTime lastSyncTime) 
    {
        this.lastSyncTime = lastSyncTime;
    }

    public LocalDateTime getLastSyncTime() 
    {
        return lastSyncTime;
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
            .append("api", getApi())
            .append("label", getLabel())
            .append("sfNum", getSfNum())
            .append("dbNum", getDbNum())
            .append("syncType", getSyncType())
            .append("batchField", getBatchField())
            .append("syncStatus", getSyncStatus())
            .append("syncIndex", getSyncIndex())
            .append("syncStartDate", getSyncStartDate())
            .append("syncEndDate", getSyncEndDate())
            .append("firstSyncTime", getFirstSyncTime())
            .append("lastSyncTime", getLastSyncTime())
            .append("createTime", getCreateTime())
            .append("createBy", getCreateBy())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("tenantId", getTenantId())
            .toString();
    }
}
