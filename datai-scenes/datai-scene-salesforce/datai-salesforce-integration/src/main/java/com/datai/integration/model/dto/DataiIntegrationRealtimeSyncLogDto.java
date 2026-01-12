package com.datai.integration.model.dto;

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
import com.datai.integration.model.domain.DataiIntegrationRealtimeSyncLog;

/**
 * 实时同步日志通用业务传输对象 (Dto)
 * 整合了查询、新增、修改的所有字段
 *
 * @author datai
 * @date 2026-01-09
 */
@Data
public class DataiIntegrationRealtimeSyncLogDto implements Serializable
{
    private static final long serialVersionUID = 1L;

        /** 主键ID */
    private Long id;

        /** 对象名称 */
    private String objectName;

        /** 记录ID */
    private String recordId;

        /** 操作类型  */
    private String operationType;

        /** 变更数据 */
    private String changeData;

        /** 同步状态 */
    private String syncStatus;

        /** 错误信息 */
    private String errorMessage;

        /** 重试次数 */
    private Integer retryCount;

        /** Salesforce时间戳 */
    private LocalDateTime salesforceTimestamp;

        /** 同步时间戳 */
    private LocalDateTime syncTimestamp;

        /** 创建时间 */
    private LocalDateTime createTime;

        /** 更新时间 */
    private LocalDateTime updateTime;

    /** 请求参数（用于存放查询范围等临时数据） */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    /**
     * Dto 转 业务对象 (DataiIntegrationRealtimeSyncLog)
     */
    public static DataiIntegrationRealtimeSyncLog toObj(DataiIntegrationRealtimeSyncLogDto Dto) {
        if (Dto == null) {
            return null;
        }
        DataiIntegrationRealtimeSyncLog obj = new DataiIntegrationRealtimeSyncLog();
        BeanUtils.copyProperties(Dto, obj);
        return obj;
    }

    /**
     * 业务对象 (DataiIntegrationRealtimeSyncLog) 转 Dto
     */
    public static DataiIntegrationRealtimeSyncLogDto fromObj(DataiIntegrationRealtimeSyncLog obj) {
        if (obj == null) {
            return null;
        }
            DataiIntegrationRealtimeSyncLogDto Dto = new DataiIntegrationRealtimeSyncLogDto();
        BeanUtils.copyProperties(obj, Dto);
        return Dto;
    }
}