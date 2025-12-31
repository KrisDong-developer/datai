package com.datai.integration.model.domain;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 对象同步控制对象 datai_integration_object
 * 
 * @author datai
 * @date 2026-01-01
 */
@Schema(description = "对象同步控制对象")
public class DataiIntegrationObject extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 主键ID */
    @Schema(title = "主键ID")
    private Integer id;

    /** 部门ID */
    @Schema(title = "部门ID")
    private Long deptId;

    /** 对象API */
    @Schema(title = "对象API")
    @Excel(name = "对象API")
    private String api;

    /** 显示名称 */
    @Schema(title = "显示名称")
    @Excel(name = "显示名称")
    private String label;

    /** 复数名称 */
    @Schema(title = "复数名称")
    @Excel(name = "复数名称")
    private String labelPlural;

    /** ID前缀 */
    @Schema(title = "ID前缀")
    @Excel(name = "ID前缀")
    private String keyPrefix;

    /** 命名空间 */
    @Schema(title = "命名空间")
    @Excel(name = "命名空间")
    private String namespace;

    /** 可查询 */
    @Schema(title = "可查询")
    @Excel(name = "可查询")
    private Boolean isQueryable;

    /** 可创建 */
    @Schema(title = "可创建")
    @Excel(name = "可创建")
    private Boolean isCreateable;

    /** 可更新 */
    @Schema(title = "可更新")
    @Excel(name = "可更新")
    private Boolean isUpdateable;

    /** 可删除 */
    @Schema(title = "可删除")
    @Excel(name = "可删除")
    private Boolean isDeletable;

    /** 可同步复制 */
    @Schema(title = "可同步复制")
    @Excel(name = "可同步复制")
    private Boolean isReplicateable;

    /** 可检索 */
    @Schema(title = "可检索")
    @Excel(name = "可检索")
    private Boolean isRetrieveable;

    /** 可搜索 */
    @Schema(title = "可搜索")
    @Excel(name = "可搜索")
    private Boolean isSearchable;

    /** 是否自定义对象 */
    @Schema(title = "是否自定义对象")
    @Excel(name = "是否自定义对象")
    private Boolean isCustom;

    /** 是否自定义设置 */
    @Schema(title = "是否自定义设置")
    @Excel(name = "是否自定义设置")
    private Boolean isCustomSetting;

    /** 启用同步 */
    @Schema(title = "启用同步")
    @Excel(name = "启用同步")
    private Boolean isWork;

    /** 增量更新 */
    @Schema(title = "增量更新")
    @Excel(name = "增量更新")
    private Boolean isIncremental;

    /** 排序权重 */
    @Schema(title = "排序权重")
    @Excel(name = "排序权重")
    private Integer objectIndex;

    /** 批次字段 */
    @Schema(title = "批次字段")
    @Excel(name = "批次字段")
    private String batchField;

    /** 本地记录数 */
    @Schema(title = "本地记录数")
    @Excel(name = "本地记录数")
    private Integer totalRows;

    /** 最近同步时间 */
    @Schema(title = "最近同步时间")
    @Excel(name = "最近同步时间")
    private LocalDateTime lastSyncDate;

    /** 全量同步时间 */
    @Schema(title = "全量同步时间")
    @Excel(name = "全量同步时间")
    private LocalDateTime lastFullSyncDate;

    /** 状态 */
    @Schema(title = "状态")
    @Excel(name = "状态")
    private Boolean syncStatus;

    /** 失败原因 */
    @Schema(title = "失败原因")
    @Excel(name = "失败原因")
    private String errorMessage;
    public void setId(Integer id) 
    {
        this.id = id;
    }

    public Integer getId() 
    {
        return id;
    }


    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }


    public void setApi(String api) 
    {
        this.api = api;
    }

    public String getApi() 
    {
        return api;
    }


    public void setLabel(String label) 
    {
        this.label = label;
    }

    public String getLabel() 
    {
        return label;
    }


    public void setLabelPlural(String labelPlural) 
    {
        this.labelPlural = labelPlural;
    }

    public String getLabelPlural() 
    {
        return labelPlural;
    }


    public void setKeyPrefix(String keyPrefix) 
    {
        this.keyPrefix = keyPrefix;
    }

    public String getKeyPrefix() 
    {
        return keyPrefix;
    }


    public void setNamespace(String namespace) 
    {
        this.namespace = namespace;
    }

    public String getNamespace() 
    {
        return namespace;
    }


    public void setIsQueryable(Boolean isQueryable) 
    {
        this.isQueryable = isQueryable;
    }

    public Boolean getIsQueryable() 
    {
        return isQueryable;
    }


    public void setIsCreateable(Boolean isCreateable) 
    {
        this.isCreateable = isCreateable;
    }

    public Boolean getIsCreateable() 
    {
        return isCreateable;
    }


    public void setIsUpdateable(Boolean isUpdateable) 
    {
        this.isUpdateable = isUpdateable;
    }

    public Boolean getIsUpdateable() 
    {
        return isUpdateable;
    }


    public void setIsDeletable(Boolean isDeletable) 
    {
        this.isDeletable = isDeletable;
    }

    public Boolean getIsDeletable() 
    {
        return isDeletable;
    }


    public void setIsReplicateable(Boolean isReplicateable) 
    {
        this.isReplicateable = isReplicateable;
    }

    public Boolean getIsReplicateable() 
    {
        return isReplicateable;
    }


    public void setIsRetrieveable(Boolean isRetrieveable) 
    {
        this.isRetrieveable = isRetrieveable;
    }

    public Boolean getIsRetrieveable() 
    {
        return isRetrieveable;
    }


    public void setIsSearchable(Boolean isSearchable) 
    {
        this.isSearchable = isSearchable;
    }

    public Boolean getIsSearchable() 
    {
        return isSearchable;
    }


    public void setIsCustom(Boolean isCustom) 
    {
        this.isCustom = isCustom;
    }

    public Boolean getIsCustom() 
    {
        return isCustom;
    }


    public void setIsCustomSetting(Boolean isCustomSetting) 
    {
        this.isCustomSetting = isCustomSetting;
    }

    public Boolean getIsCustomSetting() 
    {
        return isCustomSetting;
    }


    public void setIsWork(Boolean isWork) 
    {
        this.isWork = isWork;
    }

    public Boolean getIsWork() 
    {
        return isWork;
    }


    public void setIsIncremental(Boolean isIncremental) 
    {
        this.isIncremental = isIncremental;
    }

    public Boolean getIsIncremental() 
    {
        return isIncremental;
    }


    public void setObjectIndex(Integer objectIndex) 
    {
        this.objectIndex = objectIndex;
    }

    public Integer getObjectIndex() 
    {
        return objectIndex;
    }


    public void setBatchField(String batchField) 
    {
        this.batchField = batchField;
    }

    public String getBatchField() 
    {
        return batchField;
    }


    public void setTotalRows(Integer totalRows) 
    {
        this.totalRows = totalRows;
    }

    public Integer getTotalRows() 
    {
        return totalRows;
    }


    public void setLastSyncDate(LocalDateTime lastSyncDate) 
    {
        this.lastSyncDate = lastSyncDate;
    }

    public LocalDateTime getLastSyncDate() 
    {
        return lastSyncDate;
    }


    public void setLastFullSyncDate(LocalDateTime lastFullSyncDate) 
    {
        this.lastFullSyncDate = lastFullSyncDate;
    }

    public LocalDateTime getLastFullSyncDate() 
    {
        return lastFullSyncDate;
    }


    public void setSyncStatus(Boolean syncStatus) 
    {
        this.syncStatus = syncStatus;
    }

    public Boolean getSyncStatus() 
    {
        return syncStatus;
    }


    public void setErrorMessage(String errorMessage) 
    {
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() 
    {
        return errorMessage;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deptId", getDeptId())
            .append("api", getApi())
            .append("label", getLabel())
            .append("labelPlural", getLabelPlural())
            .append("keyPrefix", getKeyPrefix())
            .append("namespace", getNamespace())
            .append("isQueryable", getIsQueryable())
            .append("isCreateable", getIsCreateable())
            .append("isUpdateable", getIsUpdateable())
            .append("isDeletable", getIsDeletable())
            .append("isReplicateable", getIsReplicateable())
            .append("isRetrieveable", getIsRetrieveable())
            .append("isSearchable", getIsSearchable())
            .append("isCustom", getIsCustom())
            .append("isCustomSetting", getIsCustomSetting())
            .append("isWork", getIsWork())
            .append("isIncremental", getIsIncremental())
            .append("objectIndex", getObjectIndex())
            .append("batchField", getBatchField())
            .append("totalRows", getTotalRows())
            .append("lastSyncDate", getLastSyncDate())
            .append("lastFullSyncDate", getLastFullSyncDate())
            .append("syncStatus", getSyncStatus())
            .append("errorMessage", getErrorMessage())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
