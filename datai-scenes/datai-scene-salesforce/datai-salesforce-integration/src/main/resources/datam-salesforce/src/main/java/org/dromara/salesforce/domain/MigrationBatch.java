package org.dromara.salesforce.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 迁移批次对象 migration_batch
 *
 * @author Kris
 * @date 2025-09-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("migration_batch")
public class MigrationBatch extends TenantEntity {

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
     * 开始同步时间
     */
    private Date syncStartDate;

    /**
     * 结束同步时间
     */
    private Date syncEndDate;

    /**
     * SF数据量
     */
    private Integer sfNum;

    /**
     * 本地数据量
     */
    private Integer dbNum;

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
     * 同步状态 1:正常 0:误差
     */
    private Boolean syncStatus;

    /**
     * 首次同步时间
     */
    private Date firstSyncTime;

    /**
     * 最后同步时间
     */
    private Date lastSyncTime;

    /**
     * 备注
     */
    private String remark;


}
