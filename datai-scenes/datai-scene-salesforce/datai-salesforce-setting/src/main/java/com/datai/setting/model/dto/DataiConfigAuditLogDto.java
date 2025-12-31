package com.datai.setting.model.dto;

import java.io.Serializable;
import java.util.Map;
import java.util.Date;
import java.util.List;
    import java.time.LocalDateTime;
    import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.datai.setting.model.domain.DataiConfigAuditLog;

/**
 * 配置审计日志通用业务传输对象 (Dto)
 * 整合了查询、新增、修改的所有字段
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiConfigAuditLogDto implements Serializable
{
    private static final long serialVersionUID = 1L;

        /** 日志ID */
    private Long id;

        /** 部门ID */
    private Long deptId;

        /** 操作类型 */
    private String operationType;

        /** 对象类型 */
    private String objectType;

        /** 对象ID */
    private Long objectId;

        /** 旧值 */
    private String oldValue;

        /** 新值 */
    private String newValue;

        /** 操作描述 */
    private String operationDesc;

        /** 操作人 */
    private String operator;

        /** 操作时间 */
    private LocalDateTime operationTime;

        /** IP地址 */
    private String ipAddress;

        /** 用户代理 */
    private String userAgent;

        /** 请求ID */
    private String requestId;

        /** 操作结果 */
    private String result;

        /** 错误信息 */
    private String errorMessage;

        /** 创建人 */
    private String createBy;

        /** 创建时间 */
    private Date createTime;

        /** 更新人 */
    private String updateBy;

        /** 更新时间 */
    private Date updateTime;

    /** 请求参数（用于存放查询范围等临时数据） */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    /**
     * Dto 转 业务对象 (DataiConfigAuditLog)
     */
    public static DataiConfigAuditLog toObj(DataiConfigAuditLogDto Dto) {
        if (Dto == null) {
            return null;
        }
        DataiConfigAuditLog obj = new DataiConfigAuditLog();
        BeanUtils.copyProperties(Dto, obj);
        return obj;
    }

    /**
     * 业务对象 (DataiConfigAuditLog) 转 Dto
     */
    public static DataiConfigAuditLogDto fromObj(DataiConfigAuditLog obj) {
        if (obj == null) {
            return null;
        }
            DataiConfigAuditLogDto Dto = new DataiConfigAuditLogDto();
        BeanUtils.copyProperties(obj, Dto);
        return Dto;
    }
}