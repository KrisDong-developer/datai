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
import com.datai.integration.model.domain.DataiIntegrationMetadataChange;

/**
 * 对象元数据变更通用业务传输对象 (Dto)
 * 整合了查询、新增、修改的所有字段
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiIntegrationMetadataChangeDto implements Serializable
{
    private static final long serialVersionUID = 1L;

        /** 主键ID */
    private Long id;

        /** 变更类型 */
    private String changeType;

        /** 操作类型 */
    private String operationType;

        /** 对象API */
    private String objectApi;

        /** 字段API */
    private String fieldApi;

        /** 对象名 */
    private String objectLabel;

        /** 字段名 */
    private String fieldLabel;

        /** 变更时间 */
    private LocalDateTime changeTime;

        /** 变更人 */
    private String changeUser;

        /** 变更原因 */
    private String changeReason;

        /** 同步状态 */
    private Boolean syncStatus;

        /** 同步时间 */
    private LocalDateTime syncTime;

        /** 异常信息 */
    private String syncErrorMessage;

        /** 重试次数 */
    private Integer retryCount;

        /** 重试时间 */
    private LocalDateTime lastRetryTime;

        /** 是否自定义 */
    private Boolean isCustom;

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

        /** 备注 */
    private String remark;

    /** 请求参数（用于存放查询范围等临时数据） */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    /**
     * Dto 转 业务对象 (DataiIntegrationMetadataChange)
     */
    public static DataiIntegrationMetadataChange toObj(DataiIntegrationMetadataChangeDto Dto) {
        if (Dto == null) {
            return null;
        }
        DataiIntegrationMetadataChange obj = new DataiIntegrationMetadataChange();
        BeanUtils.copyProperties(Dto, obj);
        return obj;
    }

    /**
     * 业务对象 (DataiIntegrationMetadataChange) 转 Dto
     */
    public static DataiIntegrationMetadataChangeDto fromObj(DataiIntegrationMetadataChange obj) {
        if (obj == null) {
            return null;
        }
            DataiIntegrationMetadataChangeDto Dto = new DataiIntegrationMetadataChangeDto();
        BeanUtils.copyProperties(obj, Dto);
        return Dto;
    }
}