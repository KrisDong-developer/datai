package org.dromara.salesforce.domain.param;

import cn.idev.excel.annotation.ExcelIgnoreUnannotated;
import cn.idev.excel.annotation.ExcelProperty;

import lombok.Data;

import java.util.Date;


/**
 * Salesforce参数类（增量版本）
 * <p>
 * 该类是SalesforceParam的增量版本，剥离了beginCreateDate和endCreateDate字段，
 * 用于特定场景下的参数传递，如增量数据同步等。
 * </p>
 *
 * @author lingma
 * @version 1.0
 */
@Data
@ExcelIgnoreUnannotated
public class SalesforceIncrementParam {

    /**
     * 查询对象
     */
    @ExcelProperty(value = "查询对象")
    private String api;
    /**
     * 查询参数
     */
    @ExcelProperty(value = "查询参数")
    private String select;
    /**
     * 开始创建时间
     */
    @ExcelProperty(value = "开始时间")
    private Date beginDate;
    /**
     * 结束创建时间
     */
    @ExcelProperty(value = "结束时间")
    private Date endDate;
    /**
     * 开始修改时间
     */
    @ExcelProperty(value = "开始修改时间")
    private Date beginModifyDate;
    /**
     * 结束修改时间
     */
    @ExcelProperty(value = "结束修改时间")
    private Date endModifyDate;
    /**
     * 最大Id
     */
    @ExcelProperty(value = "最大Id")
    private String maxId;
    /**
     * id字段
     */
    @ExcelProperty(value = "id字段")
    private String idField="Id";
    /**
     * sql
     */
    @ExcelProperty(value = "sql")
    private String sql;
    /**
     * LastModifiedDate
     */
    @ExcelProperty(value = "LastModifiedDate")
    private Date timestamp;
    /**
     * 时间字段
     */
    @ExcelProperty(value = "时间字段")
    private String dateField = "LastModifiedDate";
    /**
     * 批次
     */
    @ExcelProperty(value = "批次")
    private Integer batch = 1;
    /**
     * 逻辑删除
     */
    @ExcelProperty(value = "逻辑删除")
    private Boolean isDeleted;

    /**
     * 查询限制数量
     */
    @ExcelProperty(value = "查询限制数量")
    private Integer limit;

    /**
     * 是否单线程
     */
    @ExcelProperty(value = "是否单线程")
    private Boolean isSingleThread = false;

}
