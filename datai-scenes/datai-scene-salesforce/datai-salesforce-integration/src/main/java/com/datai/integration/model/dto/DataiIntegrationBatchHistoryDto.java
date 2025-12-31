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
import com.datai.integration.model.domain.DataiIntegrationBatchHistory;

/**
 * 数据批次历史通用业务传输对象 (Dto)
 * 整合了查询、新增、修改的所有字段
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiIntegrationBatchHistoryDto implements Serializable
{
    private static final long serialVersionUID = 1L;

        /** 编号ID */
    private Integer id;

        /** 部门ID */
    private Long deptId;

        /** 对象API */
    private String api;

        /** 对象名称 */
    private String label;

        /** 批次ID */
    private Integer batchId;

        /** 批次字段 */
    private String batchField;

        /** 同步数据量 */
    private Integer syncNum;

        /** 同步类型 */
    private String syncType;

        /** 同步状态 */
    private Boolean syncStatus;

        /** 开始时间 */
    private LocalDateTime startTime;

        /** 结束时间 */
    private LocalDateTime endTime;

        /** 耗费时间 */
    private Long cost;

        /** 开始同步时间 */
    private LocalDateTime syncStartTime;

        /** 结束同步时间 */
    private LocalDateTime syncEndTime;

        /** 备注 */
    private String remark;

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
     * Dto 转 业务对象 (DataiIntegrationBatchHistory)
     */
    public static DataiIntegrationBatchHistory toObj(DataiIntegrationBatchHistoryDto Dto) {
        if (Dto == null) {
            return null;
        }
        DataiIntegrationBatchHistory obj = new DataiIntegrationBatchHistory();
        BeanUtils.copyProperties(Dto, obj);
        return obj;
    }

    /**
     * 业务对象 (DataiIntegrationBatchHistory) 转 Dto
     */
    public static DataiIntegrationBatchHistoryDto fromObj(DataiIntegrationBatchHistory obj) {
        if (obj == null) {
            return null;
        }
            DataiIntegrationBatchHistoryDto Dto = new DataiIntegrationBatchHistoryDto();
        BeanUtils.copyProperties(obj, Dto);
        return Dto;
    }
}