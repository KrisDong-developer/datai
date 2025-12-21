package org.dromara.salesforce.domain.bo;

import org.dromara.salesforce.domain.MigrationBatch;
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
 * 迁移批次业务对象 migration_batch
 *
 * @author Kris
 * @date 2025-09-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MigrationBatch.class, reverseConvertGenerate = false)
public class MigrationBatchBo extends BaseEntity {

    /**
     * 编号ID
     */
    @NotNull(message = "编号ID不能为空", groups = { EditGroup.class })
    private Integer id;

    /**
     * 对象APi
     */
    @NotBlank(message = "对象APi不能为空", groups = { AddGroup.class, EditGroup.class })
    private String api;

    /**
     * 对象名称
     */
    @NotBlank(message = "对象名称不能为空", groups = { AddGroup.class, EditGroup.class })
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
