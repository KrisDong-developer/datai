package com.datai.integration.model.vo;

import java.io.Serializable;
import java.util.Date;
    import java.time.LocalDateTime;
    import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.datai.common.annotation.Excel;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.datai.integration.model.domain.DataiIntegrationBatchHistory;
/**
 * 数据批次历史Vo对象 datai_integration_batch_history
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiIntegrationBatchHistoryVo implements Serializable {
    private static final long serialVersionUID = 1L;

        /** 编号ID */
    private Integer id;

        /** 部门ID */
    private Long deptId;

        /** 对象API */
    private String api;

        /** 对象名称 */
    private String label;

        /** 批次ID */
    private Integer batchId;

        /** 批次字段 */
    private String batchField;

        /** 同步数据量 */
    private Integer syncNum;

        /** 同步类型 */
    private String syncType;

        /** 同步状态 */
    private Boolean syncStatus;

        /** 开始时间 */
    private LocalDateTime startTime;

        /** 结束时间 */
    private LocalDateTime endTime;

        /** 耗费时间 */
    private Long cost;

        /** 开始同步时间 */
    private LocalDateTime syncStartTime;

        /** 结束同步时间 */
    private LocalDateTime syncEndTime;

        /** 备注 */
    private String remark;

        /** 创建者 */
    private String createBy;

        /** 创建时间 */
    private Date createTime;

        /** 更新者 */
    private String updateBy;

        /** 更新时间 */
    private Date updateTime;


    /**
     * 对象转封装类
     *
     * @param dataiIntegrationBatchHistory DataiIntegrationBatchHistory实体对象
     * @return DataiIntegrationBatchHistoryVo
     */
    public static DataiIntegrationBatchHistoryVo objToVo(DataiIntegrationBatchHistory dataiIntegrationBatchHistory) {
        if (dataiIntegrationBatchHistory == null) {
            return null;
        }
            DataiIntegrationBatchHistoryVo dataiIntegrationBatchHistoryVo = new DataiIntegrationBatchHistoryVo();
        BeanUtils.copyProperties(dataiIntegrationBatchHistory, dataiIntegrationBatchHistoryVo);
        return dataiIntegrationBatchHistoryVo;
    }
}
