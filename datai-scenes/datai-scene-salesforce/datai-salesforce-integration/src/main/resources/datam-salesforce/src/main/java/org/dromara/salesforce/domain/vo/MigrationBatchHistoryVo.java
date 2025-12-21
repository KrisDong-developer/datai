package org.dromara.salesforce.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.salesforce.domain.MigrationBatchHistory;
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
 * 迁移批次历史视图对象 migration_batch_history
 *
 * @author Kris
 * @date 2025-09-03
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = MigrationBatchHistory.class)
public class MigrationBatchHistoryVo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 编号ID
     */
    @ExcelProperty(value = "编号ID")
    private Integer id;

    /**
     * 对象APi
     */
    @ExcelProperty(value = "对象APi")
    private String api;

    /**
     * 对象名称
     */
    @ExcelProperty(value = "对象名称")
    private String label;

    /**
     * 批次ID
     */
    @ExcelProperty(value = "批次ID")
    private Integer batchId;

    /**
     * 开始时间
     */
    @ExcelProperty(value = "开始时间")
    private Date startTime;

    /**
     * 结束时间
     */
    @ExcelProperty(value = "结束时间")
    private Date endTime;

    /**
     * 耗费时间
     */
    @ExcelProperty(value = "耗费时间")
    private Integer cost;

    /**
     * 开始同步时间
     */
    @ExcelProperty(value = "开始同步时间")
    private Date syncStartTime;

    /**
     * 结束同步时间
     */
    @ExcelProperty(value = "结束同步时间")
    private Date syncEndTime;

    /**
     * 拉取数据量
     */
    @ExcelProperty(value = "拉取数据量")
    private Integer pullNum;

    /**
     * 首次拉取数据量
     */
    @ExcelProperty(value = "首次拉取数据量")
    private Integer firstPullNum;

    /**
     * 插入数据量
     */
    @ExcelProperty(value = "插入数据量")
    private Integer insertNum;

    /**
     * 更新数据量
     */
    @ExcelProperty(value = "更新数据量")
    private Integer updateNum;

    /**
     * 同步类型
     */
    @ExcelProperty(value = "同步类型")
    private String syncType;

    /**
     * 批次字段
     */
    @ExcelProperty(value = "批次字段")
    private String batchField;

    /**
     * 操作类型
     */
    @ExcelProperty(value = "操作类型")
    private String operationType;

    /**
     * 操作状态
     */
    @ExcelProperty(value = "操作状态")
    private Boolean operationStatus;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
