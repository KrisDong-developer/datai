package com.datai.integration.model.domain;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 对象元数据变更对象 datai_integration_metadata_change
 * 
 * @author datai
 * @date 2026-01-01
 */
@Schema(description = "对象元数据变更对象")
public class DataiIntegrationMetadataChange extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 主键ID */
    @Schema(title = "主键ID")
    private Long id;

    /** 变更类型 */
    @Schema(title = "变更类型")
    @Excel(name = "变更类型")
    private String changeType;

    /** 操作类型 */
    @Schema(title = "操作类型")
    @Excel(name = "操作类型")
    private String operationType;

    /** 对象API */
    @Schema(title = "对象API")
    @Excel(name = "对象API")
    private String objectApi;

    /** 字段API */
    @Schema(title = "字段API")
    @Excel(name = "字段API")
    private String fieldApi;

    /** 对象名 */
    @Schema(title = "对象名")
    @Excel(name = "对象名")
    private String objectLabel;

    /** 字段名 */
    @Schema(title = "字段名")
    @Excel(name = "字段名")
    private String fieldLabel;

    /** 变更时间 */
    @Schema(title = "变更时间")
    @Excel(name = "变更时间")
    private LocalDateTime changeTime;

    /** 变更人 */
    @Schema(title = "变更人")
    @Excel(name = "变更人")
    private String changeUser;

    /** 变更原因 */
    @Schema(title = "变更原因")
    @Excel(name = "变更原因")
    private String changeReason;

    /** 同步状态 */
    @Schema(title = "同步状态")
    @Excel(name = "同步状态")
    private Boolean syncStatus;

    /** 同步时间 */
    @Schema(title = "同步时间")
    @Excel(name = "同步时间")
    private LocalDateTime syncTime;

    /** 异常信息 */
    @Schema(title = "异常信息")
    @Excel(name = "异常信息")
    private String syncErrorMessage;

    /** 重试次数 */
    @Schema(title = "重试次数")
    @Excel(name = "重试次数")
    private Integer retryCount;

    /** 重试时间 */
    @Schema(title = "重试时间")
    @Excel(name = "重试时间")
    private LocalDateTime lastRetryTime;

    /** 是否自定义 */
    @Schema(title = "是否自定义")
    @Excel(name = "是否自定义")
    private Boolean isCustom;

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


    public void setChangeType(String changeType) 
    {
        this.changeType = changeType;
    }

    public String getChangeType() 
    {
        return changeType;
    }


    public void setOperationType(String operationType) 
    {
        this.operationType = operationType;
    }

    public String getOperationType() 
    {
        return operationType;
    }


    public void setObjectApi(String objectApi) 
    {
        this.objectApi = objectApi;
    }

    public String getObjectApi() 
    {
        return objectApi;
    }


    public void setFieldApi(String fieldApi) 
    {
        this.fieldApi = fieldApi;
    }

    public String getFieldApi() 
    {
        return fieldApi;
    }


    public void setObjectLabel(String objectLabel) 
    {
        this.objectLabel = objectLabel;
    }

    public String getObjectLabel() 
    {
        return objectLabel;
    }


    public void setFieldLabel(String fieldLabel) 
    {
        this.fieldLabel = fieldLabel;
    }

    public String getFieldLabel() 
    {
        return fieldLabel;
    }


    public void setChangeTime(LocalDateTime changeTime) 
    {
        this.changeTime = changeTime;
    }

    public LocalDateTime getChangeTime() 
    {
        return changeTime;
    }


    public void setChangeUser(String changeUser) 
    {
        this.changeUser = changeUser;
    }

    public String getChangeUser() 
    {
        return changeUser;
    }


    public void setChangeReason(String changeReason) 
    {
        this.changeReason = changeReason;
    }

    public String getChangeReason() 
    {
        return changeReason;
    }


    public void setSyncStatus(Boolean syncStatus) 
    {
        this.syncStatus = syncStatus;
    }

    public Boolean getSyncStatus() 
    {
        return syncStatus;
    }


    public void setSyncTime(LocalDateTime syncTime) 
    {
        this.syncTime = syncTime;
    }

    public LocalDateTime getSyncTime() 
    {
        return syncTime;
    }


    public void setSyncErrorMessage(String syncErrorMessage) 
    {
        this.syncErrorMessage = syncErrorMessage;
    }

    public String getSyncErrorMessage() 
    {
        return syncErrorMessage;
    }


    public void setRetryCount(Integer retryCount) 
    {
        this.retryCount = retryCount;
    }

    public Integer getRetryCount() 
    {
        return retryCount;
    }


    public void setLastRetryTime(LocalDateTime lastRetryTime) 
    {
        this.lastRetryTime = lastRetryTime;
    }

    public LocalDateTime getLastRetryTime() 
    {
        return lastRetryTime;
    }


    public void setIsCustom(Boolean isCustom) 
    {
        this.isCustom = isCustom;
    }

    public Boolean getIsCustom() 
    {
        return isCustom;
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
            .append("changeType", getChangeType())
            .append("operationType", getOperationType())
            .append("objectApi", getObjectApi())
            .append("fieldApi", getFieldApi())
            .append("objectLabel", getObjectLabel())
            .append("fieldLabel", getFieldLabel())
            .append("changeTime", getChangeTime())
            .append("changeUser", getChangeUser())
            .append("changeReason", getChangeReason())
            .append("syncStatus", getSyncStatus())
            .append("syncTime", getSyncTime())
            .append("syncErrorMessage", getSyncErrorMessage())
            .append("retryCount", getRetryCount())
            .append("lastRetryTime", getLastRetryTime())
            .append("isCustom", getIsCustom())
            .append("deptId", getDeptId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .toString();
    }
}
