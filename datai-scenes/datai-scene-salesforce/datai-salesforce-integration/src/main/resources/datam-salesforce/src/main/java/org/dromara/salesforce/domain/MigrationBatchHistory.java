package org.dromara.salesforce.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 迁移批次历史对象 migration_batch_history
 *
 * @author Kris
 * @date 2025-09-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("migration_batch_history")
public class MigrationBatchHistory extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 编号ID
     */
    @TableId(value = "id")
    private Integer id;

    /**
     * 对象APi
     */
    private String api;

    /**
     * 对象名称
     */
    private String label;

    /**
     * 批次ID
     */
    private Integer batchId;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 耗费时间
     */
    private Integer cost;

    /**
     * 开始同步时间
     */
    private Date syncStartTime;

    /**
     * 结束同步时间
     */
    private Date syncEndTime;

    /**
     * 拉取数据量
     */
    private Integer pullNum;

    /**
     * 首次拉取数据量
     */
    private Integer firstPullNum;

    /**
     * 插入数据量
     */
    private Integer insertNum;

    /**
     * 更新数据量
     */
    private Integer updateNum;

    /**
     * 同步类型
     */
    private String syncType;

    /**
     * 批次字段
     */
    private String batchField;

    /**
     * 操作类型
     */
    private String operationType;

    /**
     * 操作状态
     */
    private Boolean operationStatus;

    /**
     * 备注
     */
    private String remark;


}
