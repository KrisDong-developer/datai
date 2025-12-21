package org.dromara.salesforce.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 字段过滤查找信息对象 migration_filter_lookup
 *
 * @author Kris
 * @date 2025-09-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("migration_filter_lookup")
public class MigrationFilterLookup extends TenantEntity {

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
     * 字段API
     */
    private String field;

    /**
     * 控制字段API
     */
    private String controllingField;

    /**
     * 是否依赖字段
     */
    private Boolean dependent;

    /**
     * 过滤条件
     */
    private String lookupFilter;

    /**
     * 备注
     */
    private String remark;


}
