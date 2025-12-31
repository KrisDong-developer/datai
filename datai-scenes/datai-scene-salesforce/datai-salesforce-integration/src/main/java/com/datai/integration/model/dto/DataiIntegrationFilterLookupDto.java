package com.datai.integration.model.dto;

import java.io.Serializable;
import java.util.Map;
import java.util.Date;
import java.util.List;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.datai.integration.model.domain.DataiIntegrationFilterLookup;
import java.time.LocalDateTime;

/**
 * 字段过滤查找信息通用业务传输对象 (Dto)
 * 整合了查询、新增、修改的所有字段
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiIntegrationFilterLookupDto implements Serializable
{
    private static final long serialVersionUID = 1L;

        /** 主键ID */
    private Integer id;

        /** 部门ID */
    private Long deptId;

        /** 对象API */
    private String api;

        /** 字段API */
    private String field;

        /** 控制字段API */
    private String controllingField;

        /** 是否依赖字段 */
    private Boolean dependent;

        /** 过滤条件 */
    private String lookupFilter;

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
     * Dto 转 业务对象 (DataiIntegrationFilterLookup)
     */
    public static DataiIntegrationFilterLookup toObj(DataiIntegrationFilterLookupDto Dto) {
        if (Dto == null) {
            return null;
        }
        DataiIntegrationFilterLookup obj = new DataiIntegrationFilterLookup();
        BeanUtils.copyProperties(Dto, obj);
        return obj;
    }

    /**
     * 业务对象 (DataiIntegrationFilterLookup) 转 Dto
     */
    public static DataiIntegrationFilterLookupDto fromObj(DataiIntegrationFilterLookup obj) {
        if (obj == null) {
            return null;
        }
            DataiIntegrationFilterLookupDto Dto = new DataiIntegrationFilterLookupDto();
        BeanUtils.copyProperties(obj, Dto);
        return Dto;
    }
}