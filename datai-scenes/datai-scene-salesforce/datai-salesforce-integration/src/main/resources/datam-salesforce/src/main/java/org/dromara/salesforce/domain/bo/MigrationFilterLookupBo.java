package org.dromara.salesforce.domain.bo;

import org.dromara.salesforce.domain.MigrationFilterLookup;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;

/**
 * 字段过滤查找信息业务对象 migration_filter_lookup
 *
 * @author Kris
 * @date 2025-09-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MigrationFilterLookup.class, reverseConvertGenerate = false)
public class MigrationFilterLookupBo extends BaseEntity {

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
     * 控制字段API
     */
    private String controllingField;

    /**
     * 是否依赖字段
     */
    @NotNull(message = "是否依赖字段不能为空", groups = { AddGroup.class, EditGroup.class })
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
