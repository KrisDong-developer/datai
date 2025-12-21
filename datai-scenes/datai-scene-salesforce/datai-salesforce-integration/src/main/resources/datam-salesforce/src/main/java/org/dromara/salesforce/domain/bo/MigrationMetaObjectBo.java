package org.dromara.salesforce.domain.bo;

import org.dromara.salesforce.domain.MigrationMetaObject;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 元对象信息业务对象 migration_meta_object
 *
 * @author Kris
 * @date 2025-09-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MigrationMetaObject.class, reverseConvertGenerate = false)
public class MigrationMetaObjectBo extends BaseEntity {

    /**
     * 主键ID
     */
    @NotNull(message = "主键ID不能为空", groups = { EditGroup.class })
    private Integer id;

    /**
     * 对象API
     */
    @NotBlank(message = "对象API不能为空", groups = { AddGroup.class, EditGroup.class })
    private String api;

    /**
     * 备注
     */
    private String remark;


}
