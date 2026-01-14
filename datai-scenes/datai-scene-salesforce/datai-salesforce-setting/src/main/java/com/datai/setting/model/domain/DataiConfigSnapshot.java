package com.datai.setting.model.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 配置快照对象 datai_config_snapshot
 * 
 * @author datai
 * @date 2026-01-01
 */
@Schema(description = "配置快照对象")
public class DataiConfigSnapshot extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 快照ID */
    @Schema(title = "快照ID")
    private Long id;

    /** 快照号 */
    @Schema(title = "快照号")
    @Excel(name = "快照号")
    private String snapshotNumber;

    /** 环境ID */
    @Schema(title = "环境ID")
    @Excel(name = "环境ID")
    private Long environmentId;

    /** 快照描述 */
    @Schema(title = "快照描述")
    @Excel(name = "快照描述")
    private String snapshotDesc;

    /** 快照内容 */
    @Schema(title = "快照内容")
    @Excel(name = "快照内容")
    private String snapshotContent;

    /** 配置数量 */
    @Schema(title = "配置数量")
    @Excel(name = "配置数量")
    private Integer configCount;

    /** 快照状态 */
    @Schema(title = "快照状态")
    @Excel(name = "快照状态")
    private String status;

    /** ORG类型 */
    @Schema(title = "ORG类型")
    @Excel(name = "ORG类型")
    private String orgType;

    /** 部门ID */
    @Schema(title = "部门ID")
    private Long deptId;
    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
    {
        return id;
    }


    public void setSnapshotNumber(String snapshotNumber) 
    {
        this.snapshotNumber = snapshotNumber;
    }

    public String getSnapshotNumber() 
    {
        return snapshotNumber;
    }


    public void setEnvironmentId(Long environmentId) 
    {
        this.environmentId = environmentId;
    }

    public Long getEnvironmentId() 
    {
        return environmentId;
    }


    public void setSnapshotDesc(String snapshotDesc) 
    {
        this.snapshotDesc = snapshotDesc;
    }

    public String getSnapshotDesc() 
    {
        return snapshotDesc;
    }


    public void setSnapshotContent(String snapshotContent) 
    {
        this.snapshotContent = snapshotContent;
    }

    public String getSnapshotContent() 
    {
        return snapshotContent;
    }


    public void setConfigCount(Integer configCount) 
    {
        this.configCount = configCount;
    }

    public Integer getConfigCount() 
    {
        return configCount;
    }


    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }


    public void setOrgType(String orgType) 
    {
        this.orgType = orgType;
    }

    public String getOrgType() 
    {
        return orgType;
    }


    public void setDeptId(Long deptId) 
    {
        this.deptId = deptId;
    }

    public Long getDeptId() 
    {
        return deptId;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("snapshotNumber", getSnapshotNumber())
            .append("environmentId", getEnvironmentId())
            .append("snapshotDesc", getSnapshotDesc())
            .append("snapshotContent", getSnapshotContent())
            .append("configCount", getConfigCount())
            .append("status", getStatus())
            .append("orgType", getOrgType())
            .append("remark", getRemark())
            .append("deptId", getDeptId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
