package com.datai.setting.model.vo;

import java.io.Serializable;
import java.util.Date;
    import java.time.LocalDateTime;
    import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.datai.common.annotation.Excel;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.datai.setting.model.domain.DataiConfigAuditLog;
/**
 * 配置审计日志Vo对象 datai_config_audit_log
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiConfigAuditLogVo implements Serializable {
    private static final long serialVersionUID = 1L;

        /** 日志ID */
    private Long id;

        /** 部门ID */
    private Long deptId;

        /** 操作类型 */
    private String operationType;

        /** 对象类型 */
    private String objectType;

        /** 对象ID */
    private Long objectId;

        /** 旧值 */
    private String oldValue;

        /** 新值 */
    private String newValue;

        /** 操作描述 */
    private String operationDesc;

        /** 操作人 */
    private String operator;

        /** 操作时间 */
    private LocalDateTime operationTime;

        /** IP地址 */
    private String ipAddress;

        /** 用户代理 */
    private String userAgent;

        /** 请求ID */
    private String requestId;

        /** 操作结果 */
    private String result;

        /** 错误信息 */
    private String errorMessage;

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
     * @param dataiConfigAuditLog DataiConfigAuditLog实体对象
     * @return DataiConfigAuditLogVo
     */
    public static DataiConfigAuditLogVo objToVo(DataiConfigAuditLog dataiConfigAuditLog) {
        if (dataiConfigAuditLog == null) {
            return null;
        }
            DataiConfigAuditLogVo dataiConfigAuditLogVo = new DataiConfigAuditLogVo();
        BeanUtils.copyProperties(dataiConfigAuditLog, dataiConfigAuditLogVo);
        return dataiConfigAuditLogVo;
    }
}
