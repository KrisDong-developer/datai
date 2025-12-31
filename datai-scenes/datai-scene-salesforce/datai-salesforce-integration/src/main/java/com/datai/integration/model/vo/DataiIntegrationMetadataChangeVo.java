package com.datai.integration.model.vo;

import java.io.Serializable;
import java.util.Date;
    import java.time.LocalDateTime;
    import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.datai.common.annotation.Excel;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.datai.integration.model.domain.DataiIntegrationMetadataChange;
/**
 * 对象元数据变更Vo对象 datai_integration_metadata_change
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiIntegrationMetadataChangeVo implements Serializable {
    private static final long serialVersionUID = 1L;

        /** 主键ID */
    private Long id;

        /** 变更类型 */
    private String changeType;

        /** 操作类型 */
    private String operationType;

        /** 对象API */
    private String objectApi;

        /** 字段API */
    private String fieldApi;

        /** 对象名 */
    private String objectLabel;

        /** 字段名 */
    private String fieldLabel;

        /** 变更时间 */
    private LocalDateTime changeTime;

        /** 变更人 */
    private String changeUser;

        /** 变更原因 */
    private String changeReason;

        /** 同步状态 */
    private Boolean syncStatus;

        /** 同步时间 */
    private LocalDateTime syncTime;

        /** 异常信息 */
    private String syncErrorMessage;

        /** 重试次数 */
    private Integer retryCount;

        /** 重试时间 */
    private LocalDateTime lastRetryTime;

        /** 是否自定义 */
    private Boolean isCustom;

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

        /** 备注 */
    private String remark;


    /**
     * 对象转封装类
     *
     * @param dataiIntegrationMetadataChange DataiIntegrationMetadataChange实体对象
     * @return DataiIntegrationMetadataChangeVo
     */
    public static DataiIntegrationMetadataChangeVo objToVo(DataiIntegrationMetadataChange dataiIntegrationMetadataChange) {
        if (dataiIntegrationMetadataChange == null) {
            return null;
        }
            DataiIntegrationMetadataChangeVo dataiIntegrationMetadataChangeVo = new DataiIntegrationMetadataChangeVo();
        BeanUtils.copyProperties(dataiIntegrationMetadataChange, dataiIntegrationMetadataChangeVo);
        return dataiIntegrationMetadataChangeVo;
    }
}
