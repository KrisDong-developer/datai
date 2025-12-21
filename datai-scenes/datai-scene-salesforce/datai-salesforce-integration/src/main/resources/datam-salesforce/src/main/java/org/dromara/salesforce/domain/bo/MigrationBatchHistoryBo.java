package org.dromara.salesforce.domain.bo;

import org.dromara.salesforce.domain.MigrationBatchHistory;
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
 * 迁移批次历史业务对象 migration_batch_history
 *
 * @author Kris
 * @date 2025-09-03
 */
@Data
@EqualsAndHashCode(callSuper = true)
@AutoMapper(target = MigrationBatchHistory.class, reverseConvertGenerate = false)
public class MigrationBatchHistoryBo extends BaseEntity {

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
     * 批次ID
     */
    private Integer batchId;

    /**
     * 开始时间
     */
    private Date startTime;

    /**
     * 结束时间
     */
    private Date endTime;

    /**
     * 耗费时间
     */
    private Integer cost;

    /**
     * 开始同步时间
     */
    private Date syncStartTime;

    /**
     * 结束同步时间
     */
    private Date syncEndTime;

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
     * 操作类型
     */
    private String operationType;

    /**
     * 操作状态
     */
    private Boolean operationStatus;

    /**
     * 备注
     */
    private String remark;


}
