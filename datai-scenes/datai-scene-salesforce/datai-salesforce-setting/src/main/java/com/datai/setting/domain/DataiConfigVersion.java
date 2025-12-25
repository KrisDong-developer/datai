package com.datai.setting.domain;

import java.time.LocalDateTime;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 配置版本对象 datai_config_version
 * 
 * @author datai
 * @date 2025-12-24
 */
@Schema(description = "配置版本对象")
public class DataiConfigVersion extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 版本ID */
    @Schema(title = "版本ID")
    private Long id;

    /** 部门ID */
    @Schema(title = "部门ID")
    private Long deptId;

    /** 版本号 */
    @Schema(title = "版本号")
    @Excel(name = "版本号")
    private String versionNumber;

    /** 版本描述 */
    @Schema(title = "版本描述")
    @Excel(name = "版本描述")
    private String versionDesc;

    /** 快照ID */
    @Schema(title = "快照ID")
    @Excel(name = "快照ID")
    private String snapshotId;

    /** 快照内容 */
    @Schema(title = "快照内容")
    @Excel(name = "快照内容")
    private String snapshotContent;

    /** 版本状态 */
    @Schema(title = "版本状态")
    @Excel(name = "版本状态")
    private String status;

    /** 发布时间 */
    @Schema(title = "发布时间")
    @Excel(name = "发布时间")
    private LocalDateTime publishTime;
    public void setId(Long id) 
    {
        this.id = id;
    }

    public Long getId() 
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


    public void setVersionNumber(String versionNumber) 
    {
        this.versionNumber = versionNumber;
    }

    public String getVersionNumber() 
    {
        return versionNumber;
    }


    public void setVersionDesc(String versionDesc) 
    {
        this.versionDesc = versionDesc;
    }

    public String getVersionDesc() 
    {
        return versionDesc;
    }


    public void setSnapshotId(String snapshotId) 
    {
        this.snapshotId = snapshotId;
    }

    public String getSnapshotId() 
    {
        return snapshotId;
    }


    public void setSnapshotContent(String snapshotContent) 
    {
        this.snapshotContent = snapshotContent;
    }

    public String getSnapshotContent() 
    {
        return snapshotContent;
    }


    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }


    public void setPublishTime(LocalDateTime publishTime) 
    {
        this.publishTime = publishTime;
    }

    public LocalDateTime getPublishTime() 
    {
        return publishTime;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("id", getId())
            .append("deptId", getDeptId())
            .append("versionNumber", getVersionNumber())
            .append("versionDesc", getVersionDesc())
            .append("snapshotId", getSnapshotId())
            .append("snapshotContent", getSnapshotContent())
            .append("status", getStatus())
            .append("publishTime", getPublishTime())
            .append("remark", getRemark())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
