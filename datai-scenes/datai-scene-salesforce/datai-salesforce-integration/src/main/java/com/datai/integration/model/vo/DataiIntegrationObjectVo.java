package com.datai.integration.model.vo;

import java.io.Serializable;
import java.util.Date;
    import java.time.LocalDateTime;
    import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.datai.common.annotation.Excel;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.datai.integration.model.domain.DataiIntegrationObject;
/**
 * 对象同步控制Vo对象 datai_integration_object
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiIntegrationObjectVo implements Serializable {
    private static final long serialVersionUID = 1L;

        /** 主键ID */
    private Integer id;

        /** 部门ID */
    private Long deptId;

        /** 对象API */
    private String api;

        /** 显示名称 */
    private String label;

        /** 复数名称 */
    private String labelPlural;

        /** ID前缀 */
    private String keyPrefix;

        /** 命名空间 */
    private String namespace;

        /** 可查询 */
    private Boolean isQueryable;

        /** 可创建 */
    private Boolean isCreateable;

        /** 可更新 */
    private Boolean isUpdateable;

        /** 可删除 */
    private Boolean isDeletable;

        /** 可同步复制 */
    private Boolean isReplicateable;

        /** 可检索 */
    private Boolean isRetrieveable;

        /** 可搜索 */
    private Boolean isSearchable;

        /** 是否自定义对象 */
    private Boolean isCustom;

        /** 是否自定义设置 */
    private Boolean isCustomSetting;

        /** 启用同步 */
    private Boolean isWork;

        /** 增量更新 */
    private Boolean isIncremental;

        /** 排序权重 */
    private Integer objectIndex;

        /** 批次字段 */
    private String batchField;

        /** 是否分区 */
    private Boolean isPartitioned;

        /** 本地记录数 */
    private Integer totalRows;

        /** 最近同步时间 */
    private LocalDateTime lastSyncDate;

        /** 最后批次时间 */
    private LocalDateTime lastBatchDate;

        /** 状态 */
    private Boolean syncStatus;

        /** 失败原因 */
    private String errorMessage;

        /** 实时同步 */
    private Boolean isRealtimeSync;

        /** 备注 */
    private String remark;

        /** 创建者 */
    private String createBy;

        /** 创建时间 */
    private Date createTime;

        /** 更新者 */
    private String updateBy;

        /** 更新时间 */
    private Date updateTime;


    /**
     * 对象转封装类
     *
     * @param dataiIntegrationObject DataiIntegrationObject实体对象
     * @return DataiIntegrationObjectVo
     */
    public static DataiIntegrationObjectVo objToVo(DataiIntegrationObject dataiIntegrationObject) {
        if (dataiIntegrationObject == null) {
            return null;
        }
            DataiIntegrationObjectVo dataiIntegrationObjectVo = new DataiIntegrationObjectVo();
        BeanUtils.copyProperties(dataiIntegrationObject, dataiIntegrationObjectVo);
        return dataiIntegrationObjectVo;
    }
}
