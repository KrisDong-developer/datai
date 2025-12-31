package com.datai.integration.model.domain;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 数据批次历史对象 datai_integration_batch_history
 * 
 * @author datai
 * @date 2026-01-01
 */
@Schema(description = "数据批次历史对象")
public class DataiIntegrationBatchHistory extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 编号ID */
    @Schema(title = "编号ID")
    private Integer id;

    /** 部门ID */
    @Schema(title = "部门ID")
    private Long deptId;

    /** 对象API */
    @Schema(title = "对象API")
    @Excel(name = "对象API")
    private String api;

    /** 对象名称 */
    @Schema(title = "对象名称")
    @Excel(name = "对象名称")
    private String label;

    /** 批次ID */
    @Schema(title = "批次ID")
    @Excel(name = "批次ID")
    private Integer batchId;

    /** 批次字段 */
    @Schema(title = "批次字段")
    @Excel(name = "批次字段")
    private String batchField;

    /** 同步数据量 */
    @Schema(title = "同步数据量")
    @Excel(name = "同步数据量")
    private Integer syncNum;

    /** 同步类型 */
    @Schema(title = "同步类型")
    @Excel(name = "同步类型")
    private String syncType;

    /** 同步状态 */
    @Schema(title = "同步状态")
    @Excel(name = "同步状态")
    private Boolean syncStatus;

    /** 开始时间 */
    @Schema(title = "开始时间")
    @Excel(name = "开始时间")
    private LocalDateTime startTime;

    /** 结束时间 */
    @Schema(title = "结束时间")
    @Excel(name = "结束时间")
    private LocalDateTime endTime;

    /** 耗费时间 */
    @Schema(title = "耗费时间")
    @Excel(name = "耗费时间")
    private Long cost;

    /** 开始同步时间 */
    @Schema(title = "开始同步时间")
    @Excel(name = "开始同步时间")
    private LocalDateTime syncStartTime;

    /** 结束同步时间 */
    @Schema(title = "结束同步时间")
    @Excel(name = "结束同步时间")
    private LocalDateTime syncEndTime;
    public void setId(Integer id) 
    {
        this.id = id;
    }

    public Integer getId() 
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


    public void setBatchId(Integer batchId) 
    {
        this.batchId = batchId;
    }

    public Integer getBatchId() 
    {
        return batchId;
    }


    public void setBatchField(String batchField) 
    {
        this.batchField = batchField;
    }

    public String getBatchField() 
    {
        return batchField;
    }


    public void setSyncNum(Integer syncNum) 
    {
        this.syncNum = syncNum;
    }

    public Integer getSyncNum() 
    {
        return syncNum;
    }


    public void setSyncType(String syncType) 
    {
        this.syncType = syncType;
    }

    public String getSyncType() 
    {
        return syncType;
    }


    public void setSyncStatus(Boolean syncStatus) 
    {
        this.syncStatus = syncStatus;
    }

    public Boolean getSyncStatus() 
    {
        return syncStatus;
    }


    public void setStartTime(LocalDateTime startTime) 
    {
        this.startTime = startTime;
    }

    public LocalDateTime getStartTime() 
    {
        return startTime;
    }


    public void setEndTime(LocalDateTime endTime) 
    {
        this.endTime = endTime;
    }

    public LocalDateTime getEndTime() 
    {
        return endTime;
    }


    public void setCost(Long cost) 
    {
        this.cost = cost;
    }

    public Long getCost() 
    {
        return cost;
    }


    public void setSyncStartTime(LocalDateTime syncStartTime) 
    {
        this.syncStartTime = syncStartTime;
    }

    public LocalDateTime getSyncStartTime() 
    {
        return syncStartTime;
    }


    public void setSyncEndTime(LocalDateTime syncEndTime) 
    {
        this.syncEndTime = syncEndTime;
    }

    public LocalDateTime getSyncEndTime() 
    {
        return syncEndTime;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deptId", getDeptId())
            .append("api", getApi())
            .append("label", getLabel())
            .append("batchId", getBatchId())
            .append("batchField", getBatchField())
            .append("syncNum", getSyncNum())
            .append("syncType", getSyncType())
            .append("syncStatus", getSyncStatus())
            .append("startTime", getStartTime())
            .append("endTime", getEndTime())
            .append("cost", getCost())
            .append("syncStartTime", getSyncStartTime())
            .append("syncEndTime", getSyncEndTime())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
