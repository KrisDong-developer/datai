package com.datai.integration.model.dto;

import java.io.Serializable;
import java.util.Map;
import java.util.Date;
import java.util.List;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.datai.setting.model.domain.DataiConfigSnapshot;
import java.time.LocalDateTime;

/**
 * 配置快照通用业务传输对象 (Dto)
 * 整合了查询、新增、修改的所有字段
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiConfigSnapshotDto implements Serializable
{
    private static final long serialVersionUID = 1L;

        /** 快照ID */
    private Long id;

        /** 快照号 */
    private String snapshotNumber;

        /** 环境ID */
    private Long environmentId;

        /** 快照描述 */
    private String snapshotDesc;

        /** 快照内容 */
    private String snapshotContent;

        /** 配置数量 */
    private Integer configCount;

        /** 快照状态 */
    private String status;

        /** ORG类型 */
    private String orgType;

        /** 备注 */
    private String remark;

        /** 部门ID */
    private Long deptId;

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
     * Dto 转 业务对象 (DataiConfigSnapshot)
     */
    public static DataiConfigSnapshot toObj(DataiConfigSnapshotDto Dto) {
        if (Dto == null) {
            return null;
        }
        DataiConfigSnapshot obj = new DataiConfigSnapshot();
        BeanUtils.copyProperties(Dto, obj);
        return obj;
    }

    /**
     * 业务对象 (DataiConfigSnapshot) 转 Dto
     */
    public static DataiConfigSnapshotDto fromObj(DataiConfigSnapshot obj) {
        if (obj == null) {
            return null;
        }
            DataiConfigSnapshotDto Dto = new DataiConfigSnapshotDto();
        BeanUtils.copyProperties(obj, Dto);
        return Dto;
    }
}