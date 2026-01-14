package com.datai.setting.model.domain;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 配置审计日志对象 datai_config_audit_log
 * 
 * @author datai
 * @date 2026-01-01
 */
@Schema(description = "配置审计日志对象")
public class DataiConfigAuditLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 日志ID */
    @Schema(title = "日志ID")
    private Long id;

    /** 部门ID */
    @Schema(title = "部门ID")
    private Long deptId;

    /** 操作类型 */
    @Schema(title = "操作类型")
    @Excel(name = "操作类型")
    private String operationType;

    /** 对象类型 */
    @Schema(title = "对象类型")
    @Excel(name = "对象类型")
    private String objectType;

    /** 对象ID */
    @Schema(title = "对象ID")
    @Excel(name = "对象ID")
    private Long objectId;

    /** 旧值 */
    @Schema(title = "旧值")
    @Excel(name = "旧值")
    private String oldValue;

    /** 新值 */
    @Schema(title = "新值")
    @Excel(name = "新值")
    private String newValue;

    /** 操作描述 */
    @Schema(title = "操作描述")
    @Excel(name = "操作描述")
    private String operationDesc;

    /** 操作人 */
    @Schema(title = "操作人")
    @Excel(name = "操作人")
    private String operator;

    /** 操作时间 */
    @Schema(title = "操作时间")
    @Excel(name = "操作时间")
    private LocalDateTime operationTime;

    /** IP地址 */
    @Schema(title = "IP地址")
    @Excel(name = "IP地址")
    private String ipAddress;

    /** 用户代理 */
    @Schema(title = "用户代理")
    @Excel(name = "用户代理")
    private String userAgent;

    /** 请求ID */
    @Schema(title = "请求ID")
    @Excel(name = "请求ID")
    private String requestId;

    /** 操作结果 */
    @Schema(title = "操作结果")
    @Excel(name = "操作结果")
    private String result;

    /** 错误信息 */
    @Schema(title = "错误信息")
    @Excel(name = "错误信息")
    private String errorMessage;

    /** ORG类型 */
    @Schema(title = "ORG类型")
    @Excel(name = "ORG类型")
    private String orgType;
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


    public void setOperationType(String operationType) 
    {
        this.operationType = operationType;
    }

    public String getOperationType() 
    {
        return operationType;
    }


    public void setObjectType(String objectType) 
    {
        this.objectType = objectType;
    }

    public String getObjectType() 
    {
        return objectType;
    }


    public void setObjectId(Long objectId) 
    {
        this.objectId = objectId;
    }

    public Long getObjectId() 
    {
        return objectId;
    }


    public void setOldValue(String oldValue) 
    {
        this.oldValue = oldValue;
    }

    public String getOldValue() 
    {
        return oldValue;
    }


    public void setNewValue(String newValue) 
    {
        this.newValue = newValue;
    }

    public String getNewValue() 
    {
        return newValue;
    }


    public void setOperationDesc(String operationDesc) 
    {
        this.operationDesc = operationDesc;
    }

    public String getOperationDesc() 
    {
        return operationDesc;
    }


    public void setOperator(String operator) 
    {
        this.operator = operator;
    }

    public String getOperator() 
    {
        return operator;
    }


    public void setOperationTime(LocalDateTime operationTime) 
    {
        this.operationTime = operationTime;
    }

    public LocalDateTime getOperationTime() 
    {
        return operationTime;
    }


    public void setIpAddress(String ipAddress) 
    {
        this.ipAddress = ipAddress;
    }

    public String getIpAddress() 
    {
        return ipAddress;
    }


    public void setUserAgent(String userAgent) 
    {
        this.userAgent = userAgent;
    }

    public String getUserAgent() 
    {
        return userAgent;
    }


    public void setRequestId(String requestId) 
    {
        this.requestId = requestId;
    }

    public String getRequestId() 
    {
        return requestId;
    }


    public void setResult(String result) 
    {
        this.result = result;
    }

    public String getResult() 
    {
        return result;
    }


    public void setErrorMessage(String errorMessage) 
    {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() 
    {
        return errorMessage;
    }


    public void setOrgType(String orgType) 
    {
        this.orgType = orgType;
    }

    public String getOrgType() 
    {
        return orgType;
    }


    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deptId", getDeptId())
            .append("operationType", getOperationType())
            .append("objectType", getObjectType())
            .append("objectId", getObjectId())
            .append("oldValue", getOldValue())
            .append("newValue", getNewValue())
            .append("operationDesc", getOperationDesc())
            .append("operator", getOperator())
            .append("operationTime", getOperationTime())
            .append("ipAddress", getIpAddress())
            .append("userAgent", getUserAgent())
            .append("requestId", getRequestId())
            .append("result", getResult())
            .append("errorMessage", getErrorMessage())
            .append("orgType", getOrgType())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
