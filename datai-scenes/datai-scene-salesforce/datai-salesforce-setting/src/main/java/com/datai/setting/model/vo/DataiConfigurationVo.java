package com.datai.setting.model.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import com.datai.common.annotation.Excel;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.datai.setting.model.domain.DataiConfiguration;
/**
 * 配置Vo对象 datai_configuration
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiConfigurationVo implements Serializable {
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

    /** 配置版本号 */
    private Integer version;


    /**
     * 对象转封装类
     *
     * @param dataiConfiguration DataiConfiguration实体对象
     * @return DataiConfigurationVo
     */
    public static DataiConfigurationVo objToVo(DataiConfiguration dataiConfiguration) {
        if (dataiConfiguration == null) {
            return null;
        }
            DataiConfigurationVo dataiConfigurationVo = new DataiConfigurationVo();
        BeanUtils.copyProperties(dataiConfiguration, dataiConfigurationVo);
        return dataiConfigurationVo;
    }
}
