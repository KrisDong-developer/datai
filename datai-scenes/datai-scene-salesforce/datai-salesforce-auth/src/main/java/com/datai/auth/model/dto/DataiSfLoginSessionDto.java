package com.datai.auth.model.dto;

import java.io.Serializable;
import java.util.Map;
import java.util.Date;
import java.util.List;
    import java.time.LocalDateTime;
    import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.datai.auth.model.domain.DataiSfLoginSession;

/**
 * 登录会话信息通用业务传输对象 (Dto)
 * 整合了查询、新增、修改的所有字段
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiSfLoginSessionDto implements Serializable
{
    private static final long serialVersionUID = 1L;

        /** 会话ID */
    private Long id;

        /** 用户名 */
    private String username;

        /** 登录类型 */
    private String loginType;

        /** 会话状态 */
    private String status;

        /** 登录时间 */
    private LocalDateTime loginTime;

        /** 过期时间 */
    private LocalDateTime expireTime;

        /** 最后活动时间 */
    private LocalDateTime lastActivityTime;

        /** 登录IP */
    private String loginIp;

        /** 设备信息 */
    private String deviceInfo;

        /** 浏览器信息 */
    private String browserInfo;

        /** 会话ID */
    private String sessionId;

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

    /** Salesforce用户ID */
    private String sfUserId;

    /** Salesforce组织ID */
    private String sfOrganizationId;

    /** ORG类型 */
    private String orgType;

    /** 实例URL */
    private String instanceUrl;

    /** 请求参数（用于存放查询范围等临时数据） */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    /**
     * Dto 转 业务对象 (DataiSfLoginSession)
     */
    public static DataiSfLoginSession toObj(DataiSfLoginSessionDto Dto) {
        if (Dto == null) {
            return null;
        }
        DataiSfLoginSession obj = new DataiSfLoginSession();
        BeanUtils.copyProperties(Dto, obj);
        return obj;
    }

    /**
     * 业务对象 (DataiSfLoginSession) 转 Dto
     */
    public static DataiSfLoginSessionDto fromObj(DataiSfLoginSession obj) {
        if (obj == null) {
            return null;
        }
            DataiSfLoginSessionDto Dto = new DataiSfLoginSessionDto();
        BeanUtils.copyProperties(obj, Dto);
        return Dto;
    }
}