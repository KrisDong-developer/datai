package org.dromara.salesforce.domain.vo;

import org.dromara.salesforce.domain.MigrationMetaObject;
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
 * 元对象信息视图对象 migration_meta_object
 *
 * @author Kris
 * @date 2025-09-03
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = MigrationMetaObject.class)
public class MigrationMetaObjectVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @ExcelProperty(value = "主键ID")
    private Integer id;

    /**
     * 对象API
     */
    @ExcelProperty(value = "对象API")
    private String api;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
