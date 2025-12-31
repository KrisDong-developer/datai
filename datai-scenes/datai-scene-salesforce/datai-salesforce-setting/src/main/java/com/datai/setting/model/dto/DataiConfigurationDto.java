package com.datai.setting.model.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Date;
import java.util.List;

import com.datai.common.annotation.Excel;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.datai.setting.model.domain.DataiConfiguration;

/**
 * 配置通用业务传输对象 (Dto)
 * 整合了查询、新增、修改的所有字段
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiConfigurationDto implements Serializable
{
    private static final long serialVersionUID = 1L;

        /** 配置ID */
    private Long id;

        /** 部门ID */
    private Long deptId;

        /** 配置键 */
    private String configKey;

        /** 配置值 */
    private String configValue;

        /** 环境ID */
    private Long environmentId;

        /** 是否敏感配置 */
    private Boolean isSensitive;

        /** 是否加密存储 */
    private Boolean isEncrypted;

        /** 配置描述 */
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

    private Integer version;
    /** 请求参数（用于存放查询范围等临时数据） */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    /**
     * Dto 转 业务对象 (DataiConfiguration)
     */
    public static DataiConfiguration toObj(DataiConfigurationDto Dto) {
        if (Dto == null) {
            return null;
        }
        DataiConfiguration obj = new DataiConfiguration();
        BeanUtils.copyProperties(Dto, obj);
        return obj;
    }

    /**
     * 业务对象 (DataiConfiguration) 转 Dto
     */
    public static DataiConfigurationDto fromObj(DataiConfiguration obj) {
        if (obj == null) {
            return null;
        }
            DataiConfigurationDto Dto = new DataiConfigurationDto();
        BeanUtils.copyProperties(obj, Dto);
        return Dto;
    }
}