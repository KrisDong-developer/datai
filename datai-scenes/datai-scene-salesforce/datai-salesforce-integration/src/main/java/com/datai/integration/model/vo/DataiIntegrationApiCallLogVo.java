package com.datai.integration.model.vo;

import java.io.Serializable;
import java.util.Date;
    import java.time.LocalDateTime;
    import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.datai.common.annotation.Excel;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.datai.integration.model.domain.DataiIntegrationApiCallLog;
/**
 * API调用日志Vo对象 datai_integration_api_call_log
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiIntegrationApiCallLogVo implements Serializable {
    private static final long serialVersionUID = 1L;

        /** 日志ID */
    private Long id;

        /** API类型 */
    private String apiType;

        /** 连接类 */
    private String connectionClass;

        /** 方法名 */
    private String methodName;

        /** 耗时(ms) */
    private Long executionTime;

        /** 状态 */
    private String status;

        /** 异常信息 */
    private String errorMessage;

        /** 调用时间 */
    private LocalDateTime callTime;

        /** 创建者 */
    private String createBy;

        /** 创建时间 */
    private Date createTime;

        /** 更新者 */
    private String updateBy;

        /** 更新时间 */
    private Date updateTime;

        /** 部门ID */
    private Long deptId;


    /**
     * 对象转封装类
     *
     * @param dataiIntegrationApiCallLog DataiIntegrationApiCallLog实体对象
     * @return DataiIntegrationApiCallLogVo
     */
    public static DataiIntegrationApiCallLogVo objToVo(DataiIntegrationApiCallLog dataiIntegrationApiCallLog) {
        if (dataiIntegrationApiCallLog == null) {
            return null;
        }
            DataiIntegrationApiCallLogVo dataiIntegrationApiCallLogVo = new DataiIntegrationApiCallLogVo();
        BeanUtils.copyProperties(dataiIntegrationApiCallLog, dataiIntegrationApiCallLogVo);
        return dataiIntegrationApiCallLogVo;
    }
}
