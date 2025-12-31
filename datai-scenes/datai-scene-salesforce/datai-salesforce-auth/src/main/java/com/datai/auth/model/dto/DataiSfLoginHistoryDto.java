package com.datai.auth.model.dto;

import java.io.Serializable;
import java.util.Map;
import java.util.Date;
import java.util.List;
    import java.time.LocalDateTime;
    import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import org.springframework.beans.BeanUtils;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.datai.auth.model.domain.DataiSfLoginHistory;

/**
 * 登录历史信息通用业务传输对象 (Dto)
 * 整合了查询、新增、修改的所有字段
 *
 * @author datai
 * @date 2026-01-01
 */
@Data
public class DataiSfLoginHistoryDto implements Serializable
{
    private static final long serialVersionUID = 1L;

        /** 主键ID */
    private Long id;

        /** 登录类型 */
    private String loginType;

        /** 用户名 */
    private String username;

        /** 加密密码 */
    private String passwordEncrypted;

        /** 加密安全令牌 */
    private String securityTokenEncrypted;

        /** OAuth客户端ID */
    private String clientId;

        /** 加密客户端密钥 */
    private String clientSecretEncrypted;

        /** OAuth授权类型 */
    private String grantType;

        /** 组织别名 */
    private String orgAlias;

        /** 私有密钥路径 */
    private String privateKeyPath;

        /** OAuth授权码 */
    private String code;

        /** OAuth state参数 */
    private String state;

        /** 会话ID */
    private String sessionId;

        /** 环境编码 */
    private String environmentCode;

        /** 环境ID */
    private Long environmentId;

        /** 实例URL */
    private String instanceUrl;

        /** 组织ID */
    private String organizationId;

        /** 请求IP */
    private String requestIp;

        /** 请求端口 */
    private Integer requestPort;

        /** 用户代理 */
    private String userAgent;

        /** 设备类型 */
    private String deviceType;

        /** 浏览器类型 */
    private String browserType;

        /** 操作系统 */
    private String osType;

        /** 登录状态 */
    private String loginStatus;

        /** 错误代码 */
    private String errorCode;

        /** 错误信息 */
    private String errorMessage;

        /** 结果会话ID */
    private String sessionIdResult;

        /** 加密刷新令牌 */
    private String refreshTokenEncrypted;

        /** 令牌类型 */
    private String tokenType;

        /** 过期时间(秒) */
    private Integer expiresIn;

        /** 请求时间 */
    private LocalDateTime requestTime;

        /** 响应时间 */
    private LocalDateTime responseTime;

        /** 处理耗时(毫秒) */
    private Long durationMs;

        /** 操作人 */
    private String operator;

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

    /** 请求参数（用于存放查询范围等临时数据） */
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private Map<String, Object> params;

    /**
     * Dto 转 业务对象 (DataiSfLoginHistory)
     */
    public static DataiSfLoginHistory toObj(DataiSfLoginHistoryDto Dto) {
        if (Dto == null) {
            return null;
        }
        DataiSfLoginHistory obj = new DataiSfLoginHistory();
        BeanUtils.copyProperties(Dto, obj);
        return obj;
    }

    /**
     * 业务对象 (DataiSfLoginHistory) 转 Dto
     */
    public static DataiSfLoginHistoryDto fromObj(DataiSfLoginHistory obj) {
        if (obj == null) {
            return null;
        }
            DataiSfLoginHistoryDto Dto = new DataiSfLoginHistoryDto();
        BeanUtils.copyProperties(obj, Dto);
        return Dto;
    }
}