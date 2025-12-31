package com.datai.integration.model.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;
import com.datai.common.annotation.Excel;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.datai.integration.model.domain.DataiIntegrationPicklist;
/**
 * 字段选择列表信息Vo对象 datai_integration_picklist
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiIntegrationPicklistVo implements Serializable {
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


    /**
     * 对象转封装类
     *
     * @param dataiIntegrationPicklist DataiIntegrationPicklist实体对象
     * @return DataiIntegrationPicklistVo
     */
    public static DataiIntegrationPicklistVo objToVo(DataiIntegrationPicklist dataiIntegrationPicklist) {
        if (dataiIntegrationPicklist == null) {
            return null;
        }
            DataiIntegrationPicklistVo dataiIntegrationPicklistVo = new DataiIntegrationPicklistVo();
        BeanUtils.copyProperties(dataiIntegrationPicklist, dataiIntegrationPicklistVo);
        return dataiIntegrationPicklistVo;
    }
}
