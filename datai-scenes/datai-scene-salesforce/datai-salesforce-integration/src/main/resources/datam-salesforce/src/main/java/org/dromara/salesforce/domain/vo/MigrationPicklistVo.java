package org.dromara.salesforce.domain.vo;

import org.dromara.salesforce.domain.MigrationPicklist;
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
 * 字段选项列信息视图对象 migration_picklist
 *
 * @author Kris
 * @date 2025-09-03
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = MigrationPicklist.class)
public class MigrationPicklistVo implements Serializable {

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
     * 字段API
     */
    @ExcelProperty(value = "字段API")
    private String field;

    /**
     * 值
     */
    @ExcelProperty(value = "值")
    private String value;

    /**
     * 标签
     */
    @ExcelProperty(value = "标签")
    private String label;

    /**
     * 是否激活
     */
    @ExcelProperty(value = "是否激活")
    private Boolean active;

    /**
     * 是否默认值
     */
    @ExcelProperty(value = "是否默认值")
    private Boolean defaultValue;

    /**
     * 有效条件（二进制数据）
     */
    @ExcelProperty(value = "有效条件", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "二=进制数据")
    private String validFor;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
