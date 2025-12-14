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
 * @date 2025-12-14
 */
@Schema(description = "配置快照对象")
public class DataiConfigSnapshot extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 快照ID */
    @Schema(title = "快照ID")
    private String snapshotId;

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
    private Long configCount;

    /** 租户编号 */
    @Schema(title = "租户编号")
    @Excel(name = "租户编号")
    private String tenantId;
    public void setSnapshotId(String snapshotId) 
    {
        this.snapshotId = snapshotId;
    }

    public String getSnapshotId() 
    {
        return snapshotId;
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


    public void setConfigCount(Long configCount) 
    {
        this.configCount = configCount;
    }

    public Long getConfigCount() 
    {
        return configCount;
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
            .append("snapshotId", getSnapshotId())
            .append("versionId", getVersionId())
            .append("snapshotTime", getSnapshotTime())
            .append("snapshotContent", getSnapshotContent())
            .append("configCount", getConfigCount())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("remark", getRemark())
            .append("tenantId", getTenantId())
            .toString();
    }
}
