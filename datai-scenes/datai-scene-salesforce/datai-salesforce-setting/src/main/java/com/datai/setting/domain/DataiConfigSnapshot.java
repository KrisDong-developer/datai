package com.datai.setting.domain;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 配置快照对象 datai_config_snapshot
 * 
 * @author datai
 * @date 2025-12-24
 */
@Schema(description = "配置快照对象")
public class DataiConfigSnapshot extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 快照ID */
    @Schema(title = "快照ID")
    private String id;

    /** 部门ID */
    @Schema(title = "部门ID")
    private Long deptId;

    /** 版本ID */
    @Schema(title = "版本ID")
    @Excel(name = "版本ID")
    private Long versionId;

    /** 快照时间 */
    @Schema(title = "快照时间")
    @Excel(name = "快照时间")
    private LocalDateTime snapshotTime;

    /** 快照内容 */
    @Schema(title = "快照内容")
    @Excel(name = "快照内容")
    private String snapshotContent;

    /** 配置数量 */
    @Schema(title = "配置数量")
    @Excel(name = "配置数量")
    private Integer configCount;
    public void setId(String id) 
    {
        this.id = id;
    }

    public String getId() 
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


    public void setVersionId(Long versionId) 
    {
        this.versionId = versionId;
    }

    public Long getVersionId() 
    {
        return versionId;
    }


    public void setSnapshotTime(LocalDateTime snapshotTime) 
    {
        this.snapshotTime = snapshotTime;
    }

    public LocalDateTime getSnapshotTime() 
    {
        return snapshotTime;
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



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deptId", getDeptId())
            .append("versionId", getVersionId())
            .append("snapshotTime", getSnapshotTime())
            .append("snapshotContent", getSnapshotContent())
            .append("configCount", getConfigCount())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
