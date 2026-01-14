package com.datai.setting.model.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
import lombok.Data;
import com.datai.common.annotation.Excel;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.datai.setting.model.domain.DataiConfigSnapshot;
/**
 * 配置快照Vo对象 datai_config_snapshot
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiConfigSnapshotVo implements Serializable {
    private static final long serialVersionUID = 1L;

        /** 快照ID */
    private Long id;

        /** 快照号 */
    private String snapshotNumber;

        /** 环境ID */
    private Long environmentId;

        /** 环境名称 */
    private String environmentName;

        /** 快照描述 */
    private String snapshotDesc;

        /** 快照内容 */
    private String snapshotContent;

        /** 配置数量 */
    private Integer configCount;

        /** 快照状态 */
    private String status;

        /** ORG类型 */
    private String orgType;

        /** 备注 */
    private String remark;

        /** 部门ID */
    private Long deptId;

        /** 创建人 */
    private String createBy;

        /** 创建时间 */
    private Date createTime;

        /** 更新人 */
    private String updateBy;

        /** 更新时间 */
    private Date updateTime;


    /**
     * 对象转封装类
     *
     * @param dataiConfigSnapshot DataiConfigSnapshot实体对象
     * @return DataiConfigSnapshotVo
     */
    public static DataiConfigSnapshotVo objToVo(DataiConfigSnapshot dataiConfigSnapshot) {
        if (dataiConfigSnapshot == null) {
            return null;
        }
            DataiConfigSnapshotVo dataiConfigSnapshotVo = new DataiConfigSnapshotVo();
        BeanUtils.copyProperties(dataiConfigSnapshot, dataiConfigSnapshotVo);
        return dataiConfigSnapshotVo;
    }
}
