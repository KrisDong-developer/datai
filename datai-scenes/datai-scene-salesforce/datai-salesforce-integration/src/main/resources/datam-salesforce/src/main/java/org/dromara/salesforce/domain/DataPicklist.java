package org.dromara.salesforce.domain;

import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 字段选项列信息对象 data_picklist
 *
 * @author datai
 * @date 2025-11-30
 */
public class DataPicklist extends BaseEntity
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

    /** 值 */
    @Excel(name = "值")
    private String value;

    /** 标签 */
    @Excel(name = "标签")
    private String label;

    /** 是否激活 */
    @Excel(name = "是否激活")
    private Boolean active;

    /** 是否默认值 */
    @Excel(name = "是否默认值")
    private Boolean defaultValue;

    /** 有效条件（二进制数据） */
    @Excel(name = "有效条件", readConverterExp = "二=进制数据")
    private String validFor;

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

    public void setValue(String value)
    {
        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    public void setLabel(String label)
    {
        this.label = label;
    }

    public String getLabel()
    {
        return label;
    }

    public void setActive(Boolean active)
    {
        this.active = active;
    }

    public Boolean getActive()
    {
        return active;
    }

    public void setDefaultValue(Boolean defaultValue)
    {
        this.defaultValue = defaultValue;
    }

    public Boolean getDefaultValue()
    {
        return defaultValue;
    }

    public void setValidFor(String validFor)
    {
        this.validFor = validFor;
    }

    public String getValidFor()
    {
        return validFor;
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
            .append("value", getValue())
            .append("label", getLabel())
            .append("active", getActive())
            .append("defaultValue", getDefaultValue())
            .append("validFor", getValidFor())
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
