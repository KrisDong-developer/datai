package com.datai.integration.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 对象信息对象 datai_integration_object
 * 
 * @author datai
 * @date 2025-12-22
 */
@Schema(description = "对象信息对象")
public class DataiIntegrationObject extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 主键ID */
    @Schema(title = "主键ID")
    private Long id;

    /** 对象API */
    @Schema(title = "对象API")
    @Excel(name = "对象API")
    private String api;

    /** 对象标签 */
    @Schema(title = "对象标签")
    @Excel(name = "对象标签")
    private String label;

    /** 对象复数标签 */
    @Schema(title = "对象复数标签")
    @Excel(name = "对象复数标签")
    private String labelPlural;

    /** 是否自定义对象 */
    @Schema(title = "是否自定义对象")
    @Excel(name = "是否自定义对象")
    private Integer isCustom;

    /** 是否可查询 */
    @Schema(title = "是否可查询")
    @Excel(name = "是否可查询")
    private Integer isQueryable;

    /** 是否可复制 */
    @Schema(title = "是否可复制")
    @Excel(name = "是否可复制")
    private Integer isReplicateable;

    /** 是否可检索 */
    @Schema(title = "是否可检索")
    @Excel(name = "是否可检索")
    private Integer isRetrieveable;

    /** 是否可搜索 */
    @Schema(title = "是否可搜索")
    @Excel(name = "是否可搜索")
    private Integer isSearchable;

    /** 是否可触发 */
    @Schema(title = "是否可触发")
    @Excel(name = "是否可触发")
    private Integer isTriggerable;

    /** 是否可恢复删除 */
    @Schema(title = "是否可恢复删除")
    @Excel(name = "是否可恢复删除")
    private Integer isUndeletable;

    /** 是否可更新 */
    @Schema(title = "是否可更新")
    @Excel(name = "是否可更新")
    private Integer isUpdateable;

    /** 是否可创建 */
    @Schema(title = "是否可创建")
    @Excel(name = "是否可创建")
    private Integer isCreateable;

    /** 是否可删除 */
    @Schema(title = "是否可删除")
    @Excel(name = "是否可删除")
    private Integer isDeletable;

    /** 是否可合并 */
    @Schema(title = "是否可合并")
    @Excel(name = "是否可合并")
    private Integer isMergeable;

    /** 是否最近使用 */
    @Schema(title = "是否最近使用")
    @Excel(name = "是否最近使用")
    private Integer isMru;

    /** 关键前缀 */
    @Schema(title = "关键前缀")
    @Excel(name = "关键前缀")
    private String keyPrefix;

    /** 记录类型信息(JSON) */
    @Schema(title = "记录类型信息(JSON)")
    @Excel(name = "记录类型信息(JSON)")
    private String recordTypeInfos;

    /** 租户编号 */
    @Schema(title = "租户编号")
    @Excel(name = "租户编号")
    private String tenantId;
    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
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


    public void setIsCustom(Integer isCustom) 
    {
        this.isCustom = isCustom;
    }

    public Integer getIsCustom() 
    {
        return isCustom;
    }


    public void setIsQueryable(Integer isQueryable) 
    {
        this.isQueryable = isQueryable;
    }

    public Integer getIsQueryable() 
    {
        return isQueryable;
    }


    public void setIsReplicateable(Integer isReplicateable) 
    {
        this.isReplicateable = isReplicateable;
    }

    public Integer getIsReplicateable() 
    {
        return isReplicateable;
    }


    public void setIsRetrieveable(Integer isRetrieveable) 
    {
        this.isRetrieveable = isRetrieveable;
    }

    public Integer getIsRetrieveable() 
    {
        return isRetrieveable;
    }


    public void setIsSearchable(Integer isSearchable) 
    {
        this.isSearchable = isSearchable;
    }

    public Integer getIsSearchable() 
    {
        return isSearchable;
    }


    public void setIsTriggerable(Integer isTriggerable) 
    {
        this.isTriggerable = isTriggerable;
    }

    public Integer getIsTriggerable() 
    {
        return isTriggerable;
    }


    public void setIsUndeletable(Integer isUndeletable) 
    {
        this.isUndeletable = isUndeletable;
    }

    public Integer getIsUndeletable() 
    {
        return isUndeletable;
    }


    public void setIsUpdateable(Integer isUpdateable) 
    {
        this.isUpdateable = isUpdateable;
    }

    public Integer getIsUpdateable() 
    {
        return isUpdateable;
    }


    public void setIsCreateable(Integer isCreateable) 
    {
        this.isCreateable = isCreateable;
    }

    public Integer getIsCreateable() 
    {
        return isCreateable;
    }


    public void setIsDeletable(Integer isDeletable) 
    {
        this.isDeletable = isDeletable;
    }

    public Integer getIsDeletable() 
    {
        return isDeletable;
    }


    public void setIsMergeable(Integer isMergeable) 
    {
        this.isMergeable = isMergeable;
    }

    public Integer getIsMergeable() 
    {
        return isMergeable;
    }


    public void setIsMru(Integer isMru) 
    {
        this.isMru = isMru;
    }

    public Integer getIsMru() 
    {
        return isMru;
    }


    public void setKeyPrefix(String keyPrefix) 
    {
        this.keyPrefix = keyPrefix;
    }

    public String getKeyPrefix() 
    {
        return keyPrefix;
    }


    public void setRecordTypeInfos(String recordTypeInfos) 
    {
        this.recordTypeInfos = recordTypeInfos;
    }

    public String getRecordTypeInfos() 
    {
        return recordTypeInfos;
    }


    public void setTenantId(String tenantId) 
    {
        this.tenantId = tenantId;
    }

    public String getTenantId() 
    {
        return tenantId;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("api", getApi())
            .append("label", getLabel())
            .append("labelPlural", getLabelPlural())
            .append("isCustom", getIsCustom())
            .append("isQueryable", getIsQueryable())
            .append("isReplicateable", getIsReplicateable())
            .append("isRetrieveable", getIsRetrieveable())
            .append("isSearchable", getIsSearchable())
            .append("isTriggerable", getIsTriggerable())
            .append("isUndeletable", getIsUndeletable())
            .append("isUpdateable", getIsUpdateable())
            .append("isCreateable", getIsCreateable())
            .append("isDeletable", getIsDeletable())
            .append("isMergeable", getIsMergeable())
            .append("isMru", getIsMru())
            .append("keyPrefix", getKeyPrefix())
            .append("recordTypeInfos", getRecordTypeInfos())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("tenantId", getTenantId())
            .toString();
    }
}
