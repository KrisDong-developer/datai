package org.dromara.salesforce.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serial;

/**
 * 迁移对象信息对象 migration_object
 *
 * @author Kris
 * @date 2025-09-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("migration_object")
public class MigrationObject extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 主键ID
     */
    @TableId(value = "id")
    private Integer id;

    /**
     * 对象API
     */
    private String api;

    /**
     * 对象名称
     */
    private String label;

    /**
     * 对象ID前缀
     */
    private String keyPrefix;

    /**
     * 域名空间
     */
    private String namespace;

    /**
     * 排序
     */
    private Integer objectIndex;

    /**
     * 是否启用
     */
    private Boolean isWork;

    /**
     * 是否更新
     */
    private Boolean isUpdate;

    /**
     * 是否自定义设置
     */
    private Boolean isCustomsetting;

    /**
     * 二进制字段
     */
    private String blobField;

    /**
     * 批次字段
     */
    private String batchField;

    /**
     * 是否可创建字段
     */
    private Boolean isEditable;

    /**
     * 是否标准对象
     */
    private Boolean isCustom;

    /**
     * 总数据量
     */
    private Integer totalRows;

    /**
     * 最后同步时间（存量、增量）
     */
    private Date lastSyncDate;

    /**
     * 是否分区
     */
    private Boolean isPartitioned;

    /**
     * 备注
     */
    private String remark;


}
