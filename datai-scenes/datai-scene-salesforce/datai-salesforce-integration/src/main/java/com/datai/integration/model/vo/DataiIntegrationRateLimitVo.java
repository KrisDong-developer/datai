package com.datai.integration.model.vo;

import java.io.Serializable;
import java.util.Date;
    import java.time.LocalDateTime;
    import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.datai.common.annotation.Excel;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.datai.integration.model.domain.DataiIntegrationRateLimit;
/**
 * API限流监控Vo对象 datai_integration_rate_limit
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiIntegrationRateLimitVo implements Serializable {
    private static final long serialVersionUID = 1L;

        /** 主键ID */
    private Long id;

        /** 部门ID */
    private Long deptId;

        /** 接口类型 */
    private String apiType;

        /** 限制维度 */
    private String limitType;

        /** 已用额度 */
    private Integer currentUsage;

        /** 总额度 */
    private Integer maxLimit;

        /** 剩余额度 */
    private Integer remainingVal;

        /** 重置时间 */
    private LocalDateTime resetTime;

        /** 是否限流 */
    private Boolean isBlocked;

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
     * @param dataiIntegrationRateLimit DataiIntegrationRateLimit实体对象
     * @return DataiIntegrationRateLimitVo
     */
    public static DataiIntegrationRateLimitVo objToVo(DataiIntegrationRateLimit dataiIntegrationRateLimit) {
        if (dataiIntegrationRateLimit == null) {
            return null;
        }
            DataiIntegrationRateLimitVo dataiIntegrationRateLimitVo = new DataiIntegrationRateLimitVo();
        BeanUtils.copyProperties(dataiIntegrationRateLimit, dataiIntegrationRateLimitVo);
        return dataiIntegrationRateLimitVo;
    }
}
