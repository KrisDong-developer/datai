package com.datai.integration.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 字段过滤查找信息对象 datai_integration_filter_lookup
 * 
 * @author datai
 * @date 2025-12-24
 */
@Schema(description = "字段过滤查找信息对象")
public class DataiIntegrationFilterLookup extends BaseEntity
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

    /** 字段API */
    @Schema(title = "字段API")
    @Excel(name = "字段API")
    private String field;

    /** 控制字段API */
    @Schema(title = "控制字段API")
    @Excel(name = "控制字段API")
    private String controllingField;

    /** 是否依赖字段 */
    @Schema(title = "是否依赖字段")
    @Excel(name = "是否依赖字段")
    private Boolean dependent;

    /** 过滤条件 */
    @Schema(title = "过滤条件")
    @Excel(name = "过滤条件")
    private String lookupFilter;
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



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deptId", getDeptId())
            .append("api", getApi())
            .append("field", getField())
            .append("controllingField", getControllingField())
            .append("dependent", getDependent())
            .append("lookupFilter", getLookupFilter())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
