package com.datai.integration.model.dto;

import java.io.Serializable;
import java.util.Map;
import java.util.Date;
import java.util.List;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.datai.integration.model.domain.DataiIntegrationPicklist;
import java.time.LocalDateTime;

/**
 * 字段选择列表信息通用业务传输对象 (Dto)
 * 整合了查询、新增、修改的所有字段
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiIntegrationPicklistDto implements Serializable
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

        /** 选择列表值 */
    private String picklistValue;

        /** 选择列表标签 */
    private String picklistLabel;

        /** 排序 */
    private Integer picklistIndex;

        /** 是否激活 */
    private Boolean isActive;

        /** 是否默认值 */
    private Boolean isDefault;

        /** 有效性 */
    private String validFor;

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
     * Dto 转 业务对象 (DataiIntegrationPicklist)
     */
    public static DataiIntegrationPicklist toObj(DataiIntegrationPicklistDto Dto) {
        if (Dto == null) {
            return null;
        }
        DataiIntegrationPicklist obj = new DataiIntegrationPicklist();
        BeanUtils.copyProperties(Dto, obj);
        return obj;
    }

    /**
     * 业务对象 (DataiIntegrationPicklist) 转 Dto
     */
    public static DataiIntegrationPicklistDto fromObj(DataiIntegrationPicklist obj) {
        if (obj == null) {
            return null;
        }
            DataiIntegrationPicklistDto Dto = new DataiIntegrationPicklistDto();
        BeanUtils.copyProperties(obj, Dto);
        return Dto;
    }
}