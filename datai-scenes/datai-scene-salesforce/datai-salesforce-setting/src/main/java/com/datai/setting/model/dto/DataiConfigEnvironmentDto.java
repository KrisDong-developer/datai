package com.datai.setting.model.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Date;
import java.util.List;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.datai.setting.model.domain.DataiConfigEnvironment;

/**
 * 配置环境通用业务传输对象 (Dto)
 * 整合了查询、新增、修改的所有字段
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiConfigEnvironmentDto implements Serializable
{
    private static final long serialVersionUID = 1L;

        /** 环境ID */
    private Long id;

        /** 部门ID */
    private Long deptId;

        /** 环境名称 */
    private String environmentName;

        /** 环境编码 */
    private String environmentCode;

        /** ORG类型 */
    private String orgType;

        /** 环境描述 */
    private String description;

        /** 是否激活 */
    private Boolean isActive;

        /** 备注 */
    private String remark;

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
     * Dto 转 业务对象 (DataiConfigEnvironment)
     */
    public static DataiConfigEnvironment toObj(DataiConfigEnvironmentDto Dto) {
        if (Dto == null) {
            return null;
        }
        DataiConfigEnvironment obj = new DataiConfigEnvironment();
        BeanUtils.copyProperties(Dto, obj);
        return obj;
    }

    /**
     * 业务对象 (DataiConfigEnvironment) 转 Dto
     */
    public static DataiConfigEnvironmentDto fromObj(DataiConfigEnvironment obj) {
        if (obj == null) {
            return null;
        }
            DataiConfigEnvironmentDto Dto = new DataiConfigEnvironmentDto();
        BeanUtils.copyProperties(obj, Dto);
        return Dto;
    }
}