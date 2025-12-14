package com.datai.setting.domain;

import java.time.LocalDate;
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
 * @date 2025-12-14
 */
@Schema(description = "配置版本对象")
public class DataiConfigVersion extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 版本ID */
    @Schema(title = "版本ID")
    private Long versionId;

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
    private LocalDate publishTime;

    /** 租户编号 */
    @Schema(title = "租户编号")
    @Excel(name = "租户编号")
    private String tenantId;
    public void setVersionId(Long versionId) 
    {
        this.versionId = versionId;
    }

    public Long getVersionId() 
    {
        return versionId;
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


    public void setPublishTime(LocalDate publishTime) 
    {
        this.publishTime = publishTime;
    }

    public LocalDate getPublishTime() 
    {
        return publishTime;
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
            .append("versionId", getVersionId())
            .append("versionNumber", getVersionNumber())
            .append("versionDesc", getVersionDesc())
            .append("snapshotId", getSnapshotId())
            .append("snapshotContent", getSnapshotContent())
            .append("status", getStatus())
            .append("publishTime", getPublishTime())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .append("remark", getRemark())
            .append("tenantId", getTenantId())
            .toString();
    }
}
