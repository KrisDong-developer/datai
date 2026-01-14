package com.datai.auth.model.vo;

import java.io.Serializable;
import java.util.Date;
    import java.time.LocalDateTime;
    import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import com.datai.common.annotation.Excel;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.datai.auth.model.domain.DataiSfLoginSession;
/**
 * 登录会话信息Vo对象 datai_sf_login_session
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiSfLoginSessionVo implements Serializable {
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
    /**
     * 对象转封装类
     *
     * @param dataiSfLoginSession DataiSfLoginSession实体对象
     * @return DataiSfLoginSessionVo
     */
    public static DataiSfLoginSessionVo objToVo(DataiSfLoginSession dataiSfLoginSession) {
        if (dataiSfLoginSession == null) {
            return null;
        }
            DataiSfLoginSessionVo dataiSfLoginSessionVo = new DataiSfLoginSessionVo();
        BeanUtils.copyProperties(dataiSfLoginSession, dataiSfLoginSessionVo);
        return dataiSfLoginSessionVo;
    }
}
