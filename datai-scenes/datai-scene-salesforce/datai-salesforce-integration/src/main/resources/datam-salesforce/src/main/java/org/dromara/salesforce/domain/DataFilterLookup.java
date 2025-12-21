package org.dromara.salesforce.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 字段过滤查找信息对象 data_filter_lookup
 *
 * @author datai
 * @date 2025-11-30
 */
public class DataFilterLookup extends BaseEntity
{
    private static final long serialVersionUID = 1L;

    /** 主键ID */
    private Integer id;

    /** 对象API */
    @Excel(name = "对象API")
    private String api;

    /** 字段API */
    @Excel(name = "字段API")
    private String field;

    /** 控制字段API */
    @Excel(name = "控制字段API")
    private String controllingField;

    /** 是否依赖字段 */
    @Excel(name = "是否依赖字段")
    private Boolean dependent;

    /** 过滤条件 */
    @Excel(name = "过滤条件")
    private String lookupFilter;

    /** 创建部门 */
    @Excel(name = "创建部门")
    private Integer createDept;

    /** 租户编号 */
    @Excel(name = "租户编号")
    private String tenantId;

    public void setId(Integer id)
    {
        this.id = id;
    }

    public Integer getId()
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

    public void setField(String field)
    {
        this.field = field;
    }

    public String getField()
    {
        return field;
    }

    public void setControllingField(String controllingField)
    {
        this.controllingField = controllingField;
    }

    public String getControllingField()
    {
        return controllingField;
    }

    public void setDependent(Boolean dependent)
    {
        this.dependent = dependent;
    }

    public Boolean getDependent()
    {
        return dependent;
    }

    public void setLookupFilter(String lookupFilter)
    {
        this.lookupFilter = lookupFilter;
    }

    public String getLookupFilter()
    {
        return lookupFilter;
    }

    public void setCreateDept(Integer createDept)
    {
        this.createDept = createDept;
    }

    public Integer getCreateDept()
    {
        return createDept;
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
            .append("field", getField())
            .append("controllingField", getControllingField())
            .append("dependent", getDependent())
            .append("lookupFilter", getLookupFilter())
            .append("createDept", getCreateDept())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("tenantId", getTenantId())
            .toString();
    }
}
