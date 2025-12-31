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
import com.datai.integration.model.domain.DataiIntegrationApiCallLog;

/**
 * API调用日志通用业务传输对象 (Dto)
 * 整合了查询、新增、修改的所有字段
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiIntegrationApiCallLogDto implements Serializable
{
    private static final long serialVersionUID = 1L;

        /** 日志ID */
    private Long id;

        /** API类型 */
    private String apiType;

        /** 连接类 */
    private String connectionClass;

        /** 方法名 */
    private String methodName;

        /** 耗时(ms) */
    private Long executionTime;

        /** 状态 */
    private String status;

        /** 异常信息 */
    private String errorMessage;

        /** 调用时间 */
    private LocalDateTime callTime;

        /** 创建者 */
    private String createBy;

        /** 创建时间 */
    private Date createTime;

        /** 更新者 */
    private String updateBy;

        /** 更新时间 */
    private Date updateTime;

        /** 部门ID */
    private Long deptId;

    /** 请求参数（用于存放查询范围等临时数据） */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    /**
     * Dto 转 业务对象 (DataiIntegrationApiCallLog)
     */
    public static DataiIntegrationApiCallLog toObj(DataiIntegrationApiCallLogDto Dto) {
        if (Dto == null) {
            return null;
        }
        DataiIntegrationApiCallLog obj = new DataiIntegrationApiCallLog();
        BeanUtils.copyProperties(Dto, obj);
        return obj;
    }

    /**
     * 业务对象 (DataiIntegrationApiCallLog) 转 Dto
     */
    public static DataiIntegrationApiCallLogDto fromObj(DataiIntegrationApiCallLog obj) {
        if (obj == null) {
            return null;
        }
            DataiIntegrationApiCallLogDto Dto = new DataiIntegrationApiCallLogDto();
        BeanUtils.copyProperties(obj, Dto);
        return Dto;
    }
}