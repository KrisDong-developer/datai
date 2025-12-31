package com.datai.integration.model.vo;

import java.io.Serializable;
import java.util.Date;
    import java.time.LocalDateTime;
    import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.datai.common.annotation.Excel;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.datai.integration.model.domain.DataiIntegrationBatch;
/**
 * 数据批次Vo对象 datai_integration_batch
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiIntegrationBatchVo implements Serializable {
    private static final long serialVersionUID = 1L;

        /** 批次ID */
    private Integer id;

        /** 部门ID */
    private Long deptId;

        /** 对象API */
    private String api;

        /** 对象名称 */
    private String label;

        /** SF数据量 */
    private Integer sfNum;

        /** 本地数据量 */
    private Integer dbNum;

        /** 同步类型 */
    private String syncType;

        /** 批次字段 */
    private String batchField;

        /** 同步状态 */
    private Boolean syncStatus;

        /** 开始同步时间 */
    private LocalDateTime syncStartDate;

        /** 结束同步时间 */
    private LocalDateTime syncEndDate;

        /** 首次同步时间 */
    private LocalDateTime firstSyncTime;

        /** 最后同步时间 */
    private LocalDateTime lastSyncTime;

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
     * @param dataiIntegrationBatch DataiIntegrationBatch实体对象
     * @return DataiIntegrationBatchVo
     */
    public static DataiIntegrationBatchVo objToVo(DataiIntegrationBatch dataiIntegrationBatch) {
        if (dataiIntegrationBatch == null) {
            return null;
        }
            DataiIntegrationBatchVo dataiIntegrationBatchVo = new DataiIntegrationBatchVo();
        BeanUtils.copyProperties(dataiIntegrationBatch, dataiIntegrationBatchVo);
        return dataiIntegrationBatchVo;
    }
}
