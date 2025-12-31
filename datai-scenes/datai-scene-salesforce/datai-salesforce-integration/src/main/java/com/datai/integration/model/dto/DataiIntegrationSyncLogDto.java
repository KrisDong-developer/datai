package com.datai.integration.model.dto;

import java.io.Serializable;
import java.util.Map;
import java.util.Date;
import java.util.List;
    import java.math.BigDecimal;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.datai.integration.model.domain.DataiIntegrationSyncLog;
import java.time.LocalDateTime;

/**
 * 数据同步日志通用业务传输对象 (Dto)
 * 整合了查询、新增、修改的所有字段
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiIntegrationSyncLogDto implements Serializable
{
    private static final long serialVersionUID = 1L;

        /** 日志ID */
    private Long id;

        /** 关联批次ID */
    private Integer batchId;

        /** 对象API */
    private String objectApi;

        /** SF记录ID */
    private String recordId;

        /** 操作类型 */
    private String operationType;

        /** 操作状态 */
    private String operationStatus;

        /** 错误信息 */
    private String errorMessage;

        /** 执行时间(秒) */
    private BigDecimal executionTime;

        /** 部门ID */
    private Long deptId;

        /** 创建者 */
    private String createBy;

        /** 创建时间 */
    private Date createTime;

        /** 更新者 */
    private String updateBy;

        /** 更新时间 */
    private Date updateTime;

    /** 请求参数（用于存放查询范围等临时数据） */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    /**
     * Dto 转 业务对象 (DataiIntegrationSyncLog)
     */
    public static DataiIntegrationSyncLog toObj(DataiIntegrationSyncLogDto Dto) {
        if (Dto == null) {
            return null;
        }
        DataiIntegrationSyncLog obj = new DataiIntegrationSyncLog();
        BeanUtils.copyProperties(Dto, obj);
        return obj;
    }

    /**
     * 业务对象 (DataiIntegrationSyncLog) 转 Dto
     */
    public static DataiIntegrationSyncLogDto fromObj(DataiIntegrationSyncLog obj) {
        if (obj == null) {
            return null;
        }
            DataiIntegrationSyncLogDto Dto = new DataiIntegrationSyncLogDto();
        BeanUtils.copyProperties(obj, Dto);
        return Dto;
    }
}