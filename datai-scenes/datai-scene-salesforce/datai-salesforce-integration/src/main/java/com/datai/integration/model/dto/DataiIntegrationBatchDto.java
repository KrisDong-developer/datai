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
import com.datai.integration.model.domain.DataiIntegrationBatch;

/**
 * 数据批次通用业务传输对象 (Dto)
 * 整合了查询、新增、修改的所有字段
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiIntegrationBatchDto implements Serializable
{
    private static final long serialVersionUID = 1L;

        /** 批次ID */
    private Integer id;

        /** 部门ID */
    private Long deptId;

        /** 对象API */
    private String api;

        /** 对象名称 */
    private String label;

        /** SF数据量 */
    private Integer sfNum;

        /** 本地数据量 */
    private Integer dbNum;

        /** 同步类型 */
    private String syncType;

        /** 批次字段 */
    private String batchField;

        /** 同步状态 */
    private Boolean syncStatus;

        /** 开始同步时间 */
    private LocalDateTime syncStartDate;

        /** 结束同步时间 */
    private LocalDateTime syncEndDate;

        /** 首次同步时间 */
    private LocalDateTime firstSyncTime;

        /** 最后同步时间 */
    private LocalDateTime lastSyncTime;

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
     * Dto 转 业务对象 (DataiIntegrationBatch)
     */
    public static DataiIntegrationBatch toObj(DataiIntegrationBatchDto Dto) {
        if (Dto == null) {
            return null;
        }
        DataiIntegrationBatch obj = new DataiIntegrationBatch();
        BeanUtils.copyProperties(Dto, obj);
        return obj;
    }

    /**
     * 业务对象 (DataiIntegrationBatch) 转 Dto
     */
    public static DataiIntegrationBatchDto fromObj(DataiIntegrationBatch obj) {
        if (obj == null) {
            return null;
        }
            DataiIntegrationBatchDto Dto = new DataiIntegrationBatchDto();
        BeanUtils.copyProperties(obj, Dto);
        return Dto;
    }
}