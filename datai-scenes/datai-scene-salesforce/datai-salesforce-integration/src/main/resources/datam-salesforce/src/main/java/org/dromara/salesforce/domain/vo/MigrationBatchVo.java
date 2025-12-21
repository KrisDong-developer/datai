package org.dromara.salesforce.domain.vo;

import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;
import org.dromara.salesforce.domain.MigrationBatch;
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
 * 迁移批次视图对象 migration_batch
 *
 * @author Kris
 * @date 2025-09-03
 */
@Data
@ExcelIgnoreUnannotated
@AutoMapper(target = MigrationBatch.class)
public class MigrationBatchVo implements Serializable {

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
     * 开始同步时间
     */
    @ExcelProperty(value = "开始同步时间")
    private Date syncStartDate;

    /**
     * 结束同步时间
     */
    @ExcelProperty(value = "结束同步时间")
    private Date syncEndDate;

    /**
     * SF数据量
     */
    @ExcelProperty(value = "SF数据量")
    private Integer sfNum;

    /**
     * 本地数据量
     */
    @ExcelProperty(value = "本地数据量")
    private Integer dbNum;

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
     * 同步状态 1:正常 0:误差
     */
    @ExcelProperty(value = "同步状态 1:正常 0:误差")
    private Boolean syncStatus;

    /**
     * 首次同步时间
     */
    @ExcelProperty(value = "首次同步时间")
    private Date firstSyncTime;

    /**
     * 最后同步时间
     */
    @ExcelProperty(value = "最后同步时间")
    private Date lastSyncTime;

    /**
     * 备注
     */
    @ExcelProperty(value = "备注")
    private String remark;


}
