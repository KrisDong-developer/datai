package org.dromara.salesforce.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.salesforce.domain.MigrationObject;
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
 * 迁移对象信息视图对象 migration_object
 *
 * @author Kris
 * @date 2025-09-03
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = MigrationObject.class)
public class MigrationObjectVo implements Serializable {

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
     * 对象名称
     */
    @ExcelProperty(value = "对象名称")
    private String label;

    /**
     * 对象ID前缀
     */
    @ExcelProperty(value = "对象ID前缀")
    private String keyPrefix;

    /**
     * 域名空间
     */
    @ExcelProperty(value = "域名空间")
    private String namespace;

    /**
     * 排序
     */
    @ExcelProperty(value = "排序")
    private Integer objectIndex;

    /**
     * 是否启用
     */
    @ExcelProperty(value = "是否启用")
    private Boolean isWork;

    /**
     * 是否更新
     */
    @ExcelProperty(value = "是否更新")
    private Boolean isUpdate;

    /**
     * 是否自定义设置
     */
    @ExcelProperty(value = "是否自定义设置")
    private Boolean isCustomsetting;

    /**
     * 二进制字段
     */
    @ExcelProperty(value = "二进制字段")
    private String blobField;

    /**
     * 批次字段
     */
    @ExcelProperty(value = "批次字段")
    private String batchField;

    /**
     * 是否可创建字段
     */
    @ExcelProperty(value = "是否可创建字段")
    private Boolean isEditable;

    /**
     * 是否标准对象
     */
    @ExcelProperty(value = "是否标准对象")
    private Boolean isCustom;

    /**
     * 总数据量
     */
    @ExcelProperty(value = "总数据量")
    private Integer totalRows;

    /**
     * 最后同步时间（存量、增量）
     */
    @ExcelProperty(value = "最后同步时间", converter = ExcelDictConvert.class)
    @ExcelDictFormat(readConverterExp = "存=量、增量")
    private Date lastSyncDate;

    /**
     * 是否分区
     */
    @ExcelProperty(value = "是否分区")
    private Boolean isPartitioned;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
