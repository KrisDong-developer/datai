package org.dromara.salesforce.domain.vo;

import org.dromara.salesforce.domain.MigrationFilterLookup;
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
 * 字段过滤查找信息视图对象 migration_filter_lookup
 *
 * @author Kris
 * @date 2025-09-03
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = MigrationFilterLookup.class)
public class MigrationFilterLookupVo implements Serializable {

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
     * 控制字段API
     */
    @ExcelProperty(value = "控制字段API")
    private String controllingField;

    /**
     * 是否依赖字段
     */
    @ExcelProperty(value = "是否依赖字段")
    private Boolean dependent;

    /**
     * 过滤条件
     */
    @ExcelProperty(value = "过滤条件")
    private String lookupFilter;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
