package org.dromara.salesforce.domain.vo;

import org.dromara.salesforce.domain.MigrationConfig;
import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;
import org.dromara.common.excel.annotation.ExcelDictFormat;
import org.dromara.common.excel.convert.ExcelDictConvert;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Date;



/**
 * 迁移配置视图对象 migration_config
 *
 * @author Kris
 * @date 2025-09-03
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = MigrationConfig.class)
public class MigrationConfigVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    @ExcelProperty(value = "")
    private Integer id;

    /**
     * 配置键名
     */
    @ExcelProperty(value = "配置键名")
    private String configKey;

    /**
     * 配置值
     */
    @ExcelProperty(value = "配置值")
    private String configValue;

    /**
     * 配置项描述
     */
    @ExcelProperty(value = "配置项描述")
    private String description;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
