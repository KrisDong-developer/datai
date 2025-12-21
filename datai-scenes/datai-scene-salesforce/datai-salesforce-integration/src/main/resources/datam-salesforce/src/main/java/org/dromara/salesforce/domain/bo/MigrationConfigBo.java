package org.dromara.salesforce.domain.bo;

import org.dromara.salesforce.domain.MigrationConfig;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 迁移配置业务对象 migration_config
 *
 * @author Kris
 * @date 2025-09-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MigrationConfig.class, reverseConvertGenerate = false)
public class MigrationConfigBo extends BaseEntity {

    /**
     * 
     */
    @NotNull(message = "不能为空", groups = { EditGroup.class })
    private Integer id;

    /**
     * 配置键名
     */
    @NotBlank(message = "配置键名不能为空", groups = { AddGroup.class, EditGroup.class })
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
