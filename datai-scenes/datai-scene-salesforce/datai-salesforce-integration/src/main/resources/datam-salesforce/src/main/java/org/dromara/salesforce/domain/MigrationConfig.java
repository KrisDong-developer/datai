package org.dromara.salesforce.domain;

import org.dromara.common.tenant.core.TenantEntity;
import com.baomidou.mybatisplus.annotation.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;

/**
 * 迁移配置对象 migration_config
 *
 * @author Kris
 * @date 2025-09-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("migration_config")
public class MigrationConfig extends TenantEntity {

    @Serial
    private static final long serialVersionUID = 1L;

    /**
     * 
     */
    @TableId(value = "id")
    private Integer id;

    /**
     * 配置键名
     */
    private String configKey;

    /**
     * 配置值
     */
    private String configValue;

    /**
     * 配置项描述
     */
    private String description;

    /**
     * 备注
     */
    private String remark;


}
