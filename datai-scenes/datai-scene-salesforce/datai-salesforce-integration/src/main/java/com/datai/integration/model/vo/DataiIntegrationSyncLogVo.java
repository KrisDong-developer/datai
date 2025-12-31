package com.datai.integration.model.vo;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;
    import java.math.BigDecimal;
import lombok.Data;
import com.datai.common.annotation.Excel;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.datai.integration.model.domain.DataiIntegrationSyncLog;
/**
 * 数据同步日志Vo对象 datai_integration_sync_log
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiIntegrationSyncLogVo implements Serializable {
    private static final long serialVersionUID = 1L;

        /** 日志ID */
    private Long id;

        /** 关联批次ID */
    private Integer batchId;

        /** 对象API */
    private String objectApi;

        /** SF记录ID */
    private String recordId;

        /** 操作类型 */
    private String operationType;

        /** 操作状态 */
    private String operationStatus;

        /** 错误信息 */
    private String errorMessage;

        /** 执行时间(秒) */
    private BigDecimal executionTime;

        /** 部门ID */
    private Long deptId;

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
     * @param dataiIntegrationSyncLog DataiIntegrationSyncLog实体对象
     * @return DataiIntegrationSyncLogVo
     */
    public static DataiIntegrationSyncLogVo objToVo(DataiIntegrationSyncLog dataiIntegrationSyncLog) {
        if (dataiIntegrationSyncLog == null) {
            return null;
        }
            DataiIntegrationSyncLogVo dataiIntegrationSyncLogVo = new DataiIntegrationSyncLogVo();
        BeanUtils.copyProperties(dataiIntegrationSyncLog, dataiIntegrationSyncLogVo);
        return dataiIntegrationSyncLogVo;
    }
}
