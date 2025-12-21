package org.dromara.salesforce.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 字段选项列信息对象 migration_picklist
 *
 * @author Kris
 * @date 2025-09-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("migration_picklist")
public class MigrationPicklist extends TenantEntity {

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
     * 值
     */
    private String value;

    /**
     * 标签
     */
    private String label;

    /**
     * 是否激活
     */
    private Boolean active;

    /**
     * 是否默认值
     */
    private Boolean defaultValue;

    /**
     * 有效条件（二进制数据）
     */
    private String validFor;

    /**
     * 备注
     */
    private String remark;


}
