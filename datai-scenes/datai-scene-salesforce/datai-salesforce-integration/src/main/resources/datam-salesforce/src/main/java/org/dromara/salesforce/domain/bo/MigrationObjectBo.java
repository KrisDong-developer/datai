package org.dromara.salesforce.domain.bo;

import org.dromara.salesforce.domain.MigrationObject;
import org.dromara.common.mybatis.core.domain.BaseEntity;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import io.github.linpeilie.annotations.AutoMapper;
import lombok.Data;
import lombok.EqualsAndHashCode;
import jakarta.validation.constraints.*;
import java.util.Date;
import com.fasterxml.jackson.annotation.JsonFormat;

/**
 * 迁移对象信息业务对象 migration_object
 *
 * @author Kris
 * @date 2025-09-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MigrationObject.class, reverseConvertGenerate = false)
public class MigrationObjectBo extends BaseEntity {

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
