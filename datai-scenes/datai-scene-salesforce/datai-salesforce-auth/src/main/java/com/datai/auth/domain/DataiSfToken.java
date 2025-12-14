package com.datai.auth.domain;

import java.time.LocalDate;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import org.apache.commons.lang3.builder.ToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;
import com.datai.common.annotation.Excel;
import com.datai.common.core.domain.BaseEntity;

/**
 * 令牌对象 datai_sf_token
 * 
 * @author datai
 * @date 2025-12-14
 */
@Schema(description = "令牌对象")
public class DataiSfToken extends BaseEntity
{
    private static final long serialVersionUID = 1L;


    /** 令牌ID */
    @Schema(title = "令牌ID")
    private Long tokenId;

    /** 租户编号 */
    @Schema(title = "租户编号")
    @Excel(name = "租户编号")
    private String tenantId;

    /** 用户名 */
    @Schema(title = "用户名")
    @Excel(name = "用户名")
    private String username;

    /** 访问令牌 */
    @Schema(title = "访问令牌")
    @Excel(name = "访问令牌")
    private String accessToken;

    /** 刷新令牌 */
    @Schema(title = "刷新令牌")
    @Excel(name = "刷新令牌")
    private String refreshToken;

    /** 访问令牌过期时间 */
    @Schema(title = "访问令牌过期时间")
    @Excel(name = "访问令牌过期时间")
    private LocalDate accessTokenExpire;

    /** 刷新令牌过期时间 */
    @Schema(title = "刷新令牌过期时间")
    @Excel(name = "刷新令牌过期时间")
    private LocalDate refreshTokenExpire;

    /** 令牌状态 */
    @Schema(title = "令牌状态")
    @Excel(name = "令牌状态")
    private String status;

    /** 实例URL */
    @Schema(title = "实例URL")
    @Excel(name = "实例URL")
    private String instanceUrl;

    /** 登录类型 */
    @Schema(title = "登录类型")
    @Excel(name = "登录类型")
    private String loginType;

    /** 客户端ID */
    @Schema(title = "客户端ID")
    @Excel(name = "客户端ID")
    private String clientId;

    /** 作用域 */
    @Schema(title = "作用域")
    @Excel(name = "作用域")
    private String scope;

    /** 令牌类型 */
    @Schema(title = "令牌类型")
    @Excel(name = "令牌类型")
    private String tokenType;

    /** 组织ID */
    @Schema(title = "组织ID")
    @Excel(name = "组织ID")
    private String organizationId;

    /** 用户ID */
    @Schema(title = "用户ID")
    @Excel(name = "用户ID")
    private String userId;
    public void setTokenId(Long tokenId) 
    {
        this.tokenId = tokenId;
    }

    public Long getTokenId() 
    {
        return tokenId;
    }


    public void setTenantId(String tenantId) 
    {
        this.tenantId = tenantId;
    }

    public String getTenantId() 
    {
        return tenantId;
    }


    public void setUsername(String username) 
    {
        this.username = username;
    }

    public String getUsername() 
    {
        return username;
    }


    public void setAccessToken(String accessToken) 
    {
        this.accessToken = accessToken;
    }

    public String getAccessToken() 
    {
        return accessToken;
    }


    public void setRefreshToken(String refreshToken) 
    {
        this.refreshToken = refreshToken;
    }

    public String getRefreshToken() 
    {
        return refreshToken;
    }


    public void setAccessTokenExpire(LocalDate accessTokenExpire) 
    {
        this.accessTokenExpire = accessTokenExpire;
    }

    public LocalDate getAccessTokenExpire() 
    {
        return accessTokenExpire;
    }


    public void setRefreshTokenExpire(LocalDate refreshTokenExpire) 
    {
        this.refreshTokenExpire = refreshTokenExpire;
    }

    public LocalDate getRefreshTokenExpire() 
    {
        return refreshTokenExpire;
    }


    public void setStatus(String status) 
    {
        this.status = status;
    }

    public String getStatus() 
    {
        return status;
    }


    public void setInstanceUrl(String instanceUrl) 
    {
        this.instanceUrl = instanceUrl;
    }

    public String getInstanceUrl() 
    {
        return instanceUrl;
    }


    public void setLoginType(String loginType) 
    {
        this.loginType = loginType;
    }

    public String getLoginType() 
    {
        return loginType;
    }


    public void setClientId(String clientId) 
    {
        this.clientId = clientId;
    }

    public String getClientId() 
    {
        return clientId;
    }


    public void setScope(String scope) 
    {
        this.scope = scope;
    }

    public String getScope() 
    {
        return scope;
    }


    public void setTokenType(String tokenType) 
    {
        this.tokenType = tokenType;
    }

    public String getTokenType() 
    {
        return tokenType;
    }


    public void setOrganizationId(String organizationId) 
    {
        this.organizationId = organizationId;
    }

    public String getOrganizationId() 
    {
        return organizationId;
    }


    public void setUserId(String userId) 
    {
        this.userId = userId;
    }

    public String getUserId() 
    {
        return userId;
    }



    @Override
    public String toString() {
        return new ToStringBuilder(this,ToStringStyle.MULTI_LINE_STYLE)
            .append("tokenId", getTokenId())
            .append("tenantId", getTenantId())
            .append("username", getUsername())
            .append("accessToken", getAccessToken())
            .append("refreshToken", getRefreshToken())
            .append("accessTokenExpire", getAccessTokenExpire())
            .append("refreshTokenExpire", getRefreshTokenExpire())
            .append("status", getStatus())
            .append("instanceUrl", getInstanceUrl())
            .append("loginType", getLoginType())
            .append("clientId", getClientId())
            .append("scope", getScope())
            .append("tokenType", getTokenType())
            .append("organizationId", getOrganizationId())
            .append("userId", getUserId())
            .append("createBy", getCreateBy())
            .append("createTime", getCreateTime())
            .append("updateBy", getUpdateBy())
            .append("updateTime", getUpdateTime())
            .toString();
    }
}
