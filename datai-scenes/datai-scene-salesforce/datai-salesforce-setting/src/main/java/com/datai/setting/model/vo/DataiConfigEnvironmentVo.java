package com.datai.setting.model.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;
import com.datai.common.annotation.Excel;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.datai.setting.model.domain.DataiConfigEnvironment;
/**
 * 配置环境Vo对象 datai_config_environment
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiConfigEnvironmentVo implements Serializable {
    private static final long serialVersionUID = 1L;

        /** 环境ID */
    private Long id;

        /** 部门ID */
    private Long deptId;

        /** 环境名称 */
    private String environmentName;

        /** 环境编码 */
    private String environmentCode;

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


    /**
     * 对象转封装类
     *
     * @param dataiConfigEnvironment DataiConfigEnvironment实体对象
     * @return DataiConfigEnvironmentVo
     */
    public static DataiConfigEnvironmentVo objToVo(DataiConfigEnvironment dataiConfigEnvironment) {
        if (dataiConfigEnvironment == null) {
            return null;
        }
            DataiConfigEnvironmentVo dataiConfigEnvironmentVo = new DataiConfigEnvironmentVo();
        BeanUtils.copyProperties(dataiConfigEnvironment, dataiConfigEnvironmentVo);
        return dataiConfigEnvironmentVo;
    }
}
