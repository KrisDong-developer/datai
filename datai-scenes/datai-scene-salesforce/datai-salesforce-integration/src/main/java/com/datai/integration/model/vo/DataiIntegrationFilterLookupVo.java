package com.datai.integration.model.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;
import com.datai.common.annotation.Excel;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.datai.integration.model.domain.DataiIntegrationFilterLookup;
/**
 * 字段过滤查找信息Vo对象 datai_integration_filter_lookup
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiIntegrationFilterLookupVo implements Serializable {
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


    /**
     * 对象转封装类
     *
     * @param dataiIntegrationFilterLookup DataiIntegrationFilterLookup实体对象
     * @return DataiIntegrationFilterLookupVo
     */
    public static DataiIntegrationFilterLookupVo objToVo(DataiIntegrationFilterLookup dataiIntegrationFilterLookup) {
        if (dataiIntegrationFilterLookup == null) {
            return null;
        }
            DataiIntegrationFilterLookupVo dataiIntegrationFilterLookupVo = new DataiIntegrationFilterLookupVo();
        BeanUtils.copyProperties(dataiIntegrationFilterLookup, dataiIntegrationFilterLookupVo);
        return dataiIntegrationFilterLookupVo;
    }
}
