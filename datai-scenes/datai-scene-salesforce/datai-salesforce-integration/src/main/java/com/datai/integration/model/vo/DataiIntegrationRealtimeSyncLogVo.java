package com.datai.integration.model.vo;

import java.io.Serializable;
import java.util.Date;
    import java.time.LocalDateTime;
    import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.datai.common.annotation.Excel;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.datai.integration.model.domain.DataiIntegrationRealtimeSyncLog;
/**
 * 实时同步日志Vo对象 datai_integration_realtime_sync_log
 *
 * @author datai
 * @date 2026-01-09
 */
@Data
public class DataiIntegrationRealtimeSyncLogVo implements Serializable {
    private static final long serialVersionUID = 1L;

        /** 主键ID */
    private Long id;

        /** 对象名称 */
    private String objectName;

        /** 记录ID */
    private String recordId;

        /** 操作类型  */
    private String operationType;

        /** 变更数据 */
    private String changeData;

        /** 同步状态 */
    private String syncStatus;

        /** 错误信息 */
    private String errorMessage;

        /** 重试次数 */
    private Integer retryCount;

        /** Salesforce时间戳 */
    private LocalDateTime salesforceTimestamp;

        /** 同步时间戳 */
    private LocalDateTime syncTimestamp;

        /** 创建时间 */
    private LocalDateTime createTime;

        /** 更新时间 */
    private LocalDateTime updateTime;


    /**
     * 对象转封装类
     *
     * @param dataiIntegrationRealtimeSyncLog DataiIntegrationRealtimeSyncLog实体对象
     * @return DataiIntegrationRealtimeSyncLogVo
     */
    public static DataiIntegrationRealtimeSyncLogVo objToVo(DataiIntegrationRealtimeSyncLog dataiIntegrationRealtimeSyncLog) {
        if (dataiIntegrationRealtimeSyncLog == null) {
            return null;
        }
            DataiIntegrationRealtimeSyncLogVo dataiIntegrationRealtimeSyncLogVo = new DataiIntegrationRealtimeSyncLogVo();
        BeanUtils.copyProperties(dataiIntegrationRealtimeSyncLog, dataiIntegrationRealtimeSyncLogVo);
        return dataiIntegrationRealtimeSyncLogVo;
    }
}
