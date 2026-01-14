package com.datai.integration.model.domain;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 数据批次对象 datai_integr    ation_batch
 * 
 * 字段更新规则：
 * 1. 创建批次时设置的字段（后续不允许修改）：
 *    - id: 批次ID
 *    - deptId: 部门ID
 *    - api: 对象API
 *    - label: 对象名称
 *    - syncType: 同步类型（FULL/INCREMENTAL）
 *    - batchField: 批次字段
 *    - syncStartDate: 开始同步时间（批次的时间范围起点）
 *    - syncEndDate: 结束同步时间（批次的时间范围终点）
 * 
 * 2. 同步时更新的字段：
 *    - sfNum: SF数据量（每次同步时更新）
 *    - dbNum: 本地数据量（每次同步时更新）
 *    - syncStatus: 同步状态（true=成功，false=失败）
 *    - firstSyncTime: 首次同步时间（仅在首次同步时设置）
 *    - lastSyncTime: 最后同步时间（每次同步时更新）
 *    - updateTime: 更新时间（每次更新时自动设置）
 * 
 * 3. 推送时更新的字段：
 *    - sfInsertNum: SF插入数据量（每次推送时更新）
 *    - sfUpdateNum: SF更新数据量（每次推送时更新）
 *    - firstPushTime: 首次推送时间（仅在首次推送时设置）
 *    - lastPushTime: 最后推送时间（每次推送时更新）
 * 
 * @author datai
 * @date 2026-01-01
 */
@Schema(description = "数据批次对象")
public class DataiIntegrationBatch extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 批次ID */
    @Schema(title = "批次ID")
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

    /** SF数据量 */
    @Schema(title = "SF数据量")
    @Excel(name = "SF数据量")
    private Integer sfNum;

    /** 本地数据量 */
    @Schema(title = "本地数据量")
    @Excel(name = "本地数据量")
    private Integer dbNum;

    /** SF插入数据量 */
    @Schema(title = "SF插入数据量")
    @Excel(name = "SF插入数据量")
    private Integer sfInsertNum;

    /** SF更新数据量 */
    @Schema(title = "SF更新数据量")
    @Excel(name = "SF更新数据量")
    private Integer sfUpdateNum;

    /** 同步类型 */
    @Schema(title = "同步类型")
    @Excel(name = "同步类型")
    private String syncType;

    /** 批次字段 */
    @Schema(title = "批次字段")
    @Excel(name = "批次字段")
    private String batchField;

    /** 同步状态 */
    @Schema(title = "同步状态")
    @Excel(name = "同步状态")
    private Boolean syncStatus;

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

    /** 首次推送时间 */
    @Schema(title = "首次推送时间")
    @Excel(name = "首次推送时间")
    private LocalDateTime firstPushTime;

    /** 最后推送时间 */
    @Schema(title = "最后推送时间")
    @Excel(name = "最后推送时间")
    private LocalDateTime lastPushTime;
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


    public void setSfNum(Integer sfNum) 
    {
        this.sfNum = sfNum;
    }

    public Integer getSfNum() 
    {
        return sfNum;
    }


    public void setDbNum(Integer dbNum) 
    {
        this.dbNum = dbNum;
    }

    public Integer getDbNum() 
    {
        return dbNum;
    }


    public void setSfInsertNum(Integer sfInsertNum) 
    {
        this.sfInsertNum = sfInsertNum;
    }

    public Integer getSfInsertNum() 
    {
        return sfInsertNum;
    }


    public void setSfUpdateNum(Integer sfUpdateNum) 
    {
        this.sfUpdateNum = sfUpdateNum;
    }

    public Integer getSfUpdateNum() 
    {
        return sfUpdateNum;
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


    public void setSyncStatus(Boolean syncStatus) 
    {
        this.syncStatus = syncStatus;
    }

    public Boolean getSyncStatus() 
    {
        return syncStatus;
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


    public void setFirstPushTime(LocalDateTime firstPushTime) 
    {
        this.firstPushTime = firstPushTime;
    }

    public LocalDateTime getFirstPushTime() 
    {
        return firstPushTime;
    }


    public void setLastPushTime(LocalDateTime lastPushTime) 
    {
        this.lastPushTime = lastPushTime;
    }

    public LocalDateTime getLastPushTime() 
    {
        return lastPushTime;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deptId", getDeptId())
            .append("api", getApi())
            .append("label", getLabel())
            .append("sfNum", getSfNum())
            .append("dbNum", getDbNum())
            .append("sfInsertNum", getSfInsertNum())
            .append("sfUpdateNum", getSfUpdateNum())
            .append("syncType", getSyncType())
            .append("batchField", getBatchField())
            .append("syncStatus", getSyncStatus())
            .append("syncStartDate", getSyncStartDate())
            .append("syncEndDate", getSyncEndDate())
            .append("firstSyncTime", getFirstSyncTime())
            .append("lastSyncTime", getLastSyncTime())
            .append("firstPushTime", getFirstPushTime())
            .append("lastPushTime", getLastPushTime())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
