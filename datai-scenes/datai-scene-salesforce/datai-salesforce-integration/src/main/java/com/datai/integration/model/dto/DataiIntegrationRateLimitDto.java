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
import com.datai.integration.model.domain.DataiIntegrationRateLimit;

/**
 * API限流监控通用业务传输对象 (Dto)
 * 整合了查询、新增、修改的所有字段
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiIntegrationRateLimitDto implements Serializable
{
    private static final long serialVersionUID = 1L;

        /** 主键ID */
    private Long id;

        /** 部门ID */
    private Long deptId;

        /** 接口类型 */
    private String apiType;

        /** 限制维度 */
    private String limitType;

        /** 已用额度 */
    private Integer currentUsage;

        /** 总额度 */
    private Integer maxLimit;

        /** 剩余额度 */
    private Integer remainingVal;

        /** 重置时间 */
    private LocalDateTime resetTime;

        /** 是否限流 */
    private Boolean isBlocked;

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
     * Dto 转 业务对象 (DataiIntegrationRateLimit)
     */
    public static DataiIntegrationRateLimit toObj(DataiIntegrationRateLimitDto Dto) {
        if (Dto == null) {
            return null;
        }
        DataiIntegrationRateLimit obj = new DataiIntegrationRateLimit();
        BeanUtils.copyProperties(Dto, obj);
        return obj;
    }

    /**
     * 业务对象 (DataiIntegrationRateLimit) 转 Dto
     */
    public static DataiIntegrationRateLimitDto fromObj(DataiIntegrationRateLimit obj) {
        if (obj == null) {
            return null;
        }
            DataiIntegrationRateLimitDto Dto = new DataiIntegrationRateLimitDto();
        BeanUtils.copyProperties(obj, Dto);
        return Dto;
    }
}