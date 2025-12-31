package com.datai.integration.model.domain;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * API调用日志对象 datai_integration_api_call_log
 * 
 * @author datai
 * @date 2026-01-01
 */
@Schema(description = "API调用日志对象")
public class DataiIntegrationApiCallLog extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 日志ID */
    @Schema(title = "日志ID")
    private Long id;

    /** API类型 */
    @Schema(title = "API类型")
    @Excel(name = "API类型")
    private String apiType;

    /** 连接类 */
    @Schema(title = "连接类")
    @Excel(name = "连接类")
    private String connectionClass;

    /** 方法名 */
    @Schema(title = "方法名")
    @Excel(name = "方法名")
    private String methodName;

    /** 耗时(ms) */
    @Schema(title = "耗时(ms)")
    @Excel(name = "耗时(ms)")
    private Long executionTime;

    /** 状态 */
    @Schema(title = "状态")
    @Excel(name = "状态")
    private String status;

    /** 异常信息 */
    @Schema(title = "异常信息")
    @Excel(name = "异常信息")
    private String errorMessage;

    /** 调用时间 */
    @Schema(title = "调用时间")
    @Excel(name = "调用时间")
    private LocalDateTime callTime;

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


    public void setApiType(String apiType) 
    {
        this.apiType = apiType;
    }

    public String getApiType() 
    {
        return apiType;
    }


    public void setConnectionClass(String connectionClass) 
    {
        this.connectionClass = connectionClass;
    }

    public String getConnectionClass() 
    {
        return connectionClass;
    }


    public void setMethodName(String methodName) 
    {
        this.methodName = methodName;
    }

    public String getMethodName() 
    {
        return methodName;
    }


    public void setExecutionTime(Long executionTime) 
    {
        this.executionTime = executionTime;
    }

    public Long getExecutionTime() 
    {
        return executionTime;
    }


    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }


    public void setErrorMessage(String errorMessage) 
    {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() 
    {
        return errorMessage;
    }


    public void setCallTime(LocalDateTime callTime) 
    {
        this.callTime = callTime;
    }

    public LocalDateTime getCallTime() 
    {
        return callTime;
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
            .append("apiType", getApiType())
            .append("connectionClass", getConnectionClass())
            .append("methodName", getMethodName())
            .append("executionTime", getExecutionTime())
            .append("status", getStatus())
            .append("errorMessage", getErrorMessage())
            .append("callTime", getCallTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("deptId", getDeptId())
            .toString();
    }
}
