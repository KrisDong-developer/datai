package org.dromara.salesforce.domain.bo;

import org.dromara.salesforce.domain.MigrationPicklist;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 字段选项列信息业务对象 migration_picklist
 *
 * @author Kris
 * @date 2025-09-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MigrationPicklist.class, reverseConvertGenerate = false)
public class MigrationPicklistBo extends BaseEntity {

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
     * 字段API
     */
    @NotBlank(message = "字段API不能为空", groups = { AddGroup.class, EditGroup.class })
    private String field;

    /**
     * 值
     */
    @NotBlank(message = "值不能为空", groups = { AddGroup.class, EditGroup.class })
    private String value;

    /**
     * 标签
     */
    private String label;

    /**
     * 是否激活
     */
    @NotNull(message = "是否激活不能为空", groups = { AddGroup.class, EditGroup.class })
    private Boolean active;

    /**
     * 是否默认值
     */
    @NotNull(message = "是否默认值不能为空", groups = { AddGroup.class, EditGroup.class })
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
