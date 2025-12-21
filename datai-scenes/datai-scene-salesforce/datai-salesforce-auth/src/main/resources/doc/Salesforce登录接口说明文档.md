# Salesforce登录接口说明文档

## 1. 概述

本文档详细描述了系统中提供的四种Salesforce登录策略的使用方法，包括每种策略所需的参数、调用方式以及异常处理机制。系统采用策略模式设计，方便扩展和维护。

## 2. 登录策略概览

系统目前支持以下四种登录策略：

| 策略名称 | 登录类型(loginType) | 描述 |
|---------|-------------------|------|
| OAuth2LoginStrategy | oauth2 | 支持OAuth 2.0的三种授权模式 |
| LegacyCredentialLoginStrategy | legacy_credential | 使用用户名、密码和安全令牌的传统登录方式 |
| SessionIdLoginStrategy | session_id | 使用已有的Salesforce Session ID进行登录 |
| SalesforceCliLoginStrategy | salesforce_cli | 通过Salesforce CLI工具获取访问令牌 |

## 3. 各登录策略详细说明

### 3.1 OAuth2LoginStrategy (oauth2)

#### 3.1.1 支持的授权模式

该策略支持以下三种OAuth 2.0授权模式：

1. **密码模式 (Password Grant)** - 直接使用用户名和密码获取访问令牌
2. **客户端凭证模式 (Client Credentials Grant)** - 使用客户端凭证获取访问令牌
3. **授权码模式 (Authorization Code Grant)** - 通过用户授权获取访问令牌

#### 3.1.2 参数说明

| 参数名 | 类型 | 必填 | 说明 |
|-------|-----|-----|------|
| loginType | String | 是 | 固定值："oauth2" |
| grantType | String | 是 | 授权类型："password"、"client_credentials"或"authorization_code" |
| username | String | 条件 | 密码模式必填 |
| password | String | 条件 | 密码模式必填 |
| securityToken | String | 条件 | 密码模式时的安全令牌 |
| clientId | String | 是 | 应用的客户端ID |
| clientSecret | String | 是 | 应用的客户端密钥 |
| code | String | 条件 | 授权码模式必填 |
| state | String | 条件 | 授权码模式必填，用于防止CSRF攻击 |

#### 3.1.3 调用示例

##### 密码模式示例：

```java
SalesforceLoginRequest request = new SalesforceLoginRequest();
request.setLoginType("oauth2");
request.setGrantType("password");
request.setUsername("user@example.com");
request.setPassword("password");
request.setSecurityToken("securityToken");
request.setClientId("your_client_id");
request.setClientSecret("your_client_secret");

SalesforceLoginResult result = loginService.login(request);
```

##### 客户端凭证模式示例：

```java
SalesforceLoginRequest request = new SalesforceLoginRequest();
request.setLoginType("oauth2");
request.setGrantType("client_credentials");
request.setClientId("your_client_id");
request.setClientSecret("your_client_secret");

SalesforceLoginResult result = loginService.login(request);
```

##### 授权码模式示例：

```java
// 第一步：生成授权URL并引导用户访问
// 第二步：用户授权后回调获取code和state参数
SalesforceLoginRequest request = new SalesforceLoginRequest();
request.setLoginType("oauth2");
request.setGrantType("authorization_code");
request.setCode("authorization_code_from_callback");
request.setState("state_from_callback");
request.setClientId("your_client_id");
request.setClientSecret("your_client_secret");

SalesforceLoginResult result = loginService.login(request);
```

#### 3.1.4 异常处理

| 异常类 | 错误码 | 说明 |
|--------|-------|------|
| SalesforceOAuthException | OAUTH2_UNSUPPORTED_GRANT_TYPE | 不支持的授权类型 |
| SalesforceOAuthException | OAUTH2_MISSING_REFRESH_TOKEN | 缺少刷新令牌 |
| SalesforceOAuthException | OAUTH2_CONFIG_ERROR | 配置错误 |
| SalesforceOAuthException | OAUTH2_MISSING_CLIENT_ID | 缺少客户端ID |
| SalesforceOAuthException | OAUTH2_MISSING_CLIENT_SECRET | 缺少客户端密钥 |
| SalesforceOAuthException | OAUTH2_MISSING_AUTHORIZATION_CODE | 缺少授权码 |
| SalesforceOAuthException | OAUTH2_MISSING_STATE | 缺少state参数 |
| SalesforceOAuthException | OAUTH2_INVALID_STATE | 无效的state参数 |
| SalesforceOAuthException | OAUTH2_EXPIRED_STATE | state参数已过期 |

### 3.2 LegacyCredentialLoginStrategy (legacy_credential)

#### 3.2.1 参数说明

| 参数名 | 类型 | 必填 | 说明 |
|-------|-----|-----|------|
| loginType | String | 是 | 固定值："legacy_credential" |
| username | String | 是 | Salesforce用户名 |
| password | String | 是 | Salesforce密码 |
| securityToken | String | 否 | 安全令牌（根据Salesforce设置可能必需） |

#### 3.2.2 调用示例

```java
SalesforceLoginRequest request = new SalesforceLoginRequest();
request.setLoginType("legacy_credential");
request.setUsername("user@example.com");
request.setPassword("password");
request.setSecurityToken("securityToken");

SalesforceLoginResult result = loginService.login(request);
```

#### 3.2.3 异常处理

| 异常类 | 错误码 | 说明 |
|--------|-------|------|
| SalesforceLegacyCredentialLoginException | MISSING_USERNAME | 缺少用户名 |
| SalesforceLegacyCredentialLoginException | MISSING_PASSWORD | 缺少密码 |
| SalesforceLegacyCredentialLoginException | CONFIG_NOT_FOUND | 配置未找到 |
| SalesforceLegacyCredentialLoginException | SOAP_FAULT | SOAP故障 |

### 3.3 SessionIdLoginStrategy (session_id)

#### 3.3.1 参数说明

| 参数名 | 类型 | 必填 | 说明 |
|-------|-----|-----|------|
| loginType | String | 是 | 固定值："session_id" |
| sessionId | String | 是 | Salesforce Session ID |

#### 3.3.2 调用示例

```java
SalesforceLoginRequest request = new SalesforceLoginRequest();
request.setLoginType("session_id");
request.setSessionId("00Dxx0000000000!ARUAQH7...");

SalesforceLoginResult result = loginService.login(request);
```

#### 3.3.3 异常处理

| 异常类 | 错误码 | 说明 |
|--------|-------|------|
| SalesforceSessionIdLoginException | SESSION_ID_EMPTY | Session ID为空 |
| SalesforceSessionIdLoginException | CONFIG_NOT_FOUND | 配置未找到 |
| SalesforceSessionIdLoginException | INVALID_SESSION_ID | 无效的Session ID |

### 3.4 SalesforceCliLoginStrategy (salesforce_cli)

#### 3.4.1 参数说明

| 参数名 | 类型 | 必填 | 说明 |
|-------|-----|-----|------|
| loginType | String | 是 | 固定值："salesforce_cli" |
| orgAlias | String | 否 | Salesforce组织别名，如果不提供则使用默认组织 |

#### 3.4.2 调用示例

```java
SalesforceLoginRequest request = new SalesforceLoginRequest();
request.setLoginType("salesforce_cli");
request.setOrgAlias("your_org_alias"); // 可选

SalesforceLoginResult result = loginService.login(request);
```

#### 3.4.3 异常处理

| 异常类 | 错误码 | 说明 |
|--------|-------|------|
| SalesforceCliLoginException | CLI_NOT_INSTALLED | Salesforce CLI未安装 |
| SalesforceCliLoginException | CLI_COMMAND_FAILED | CLI命令执行失败 |
| SalesforceCliLoginException | CLI_PARSE_ERROR | CLI输出解析错误 |

## 4. 返回结果说明

所有登录方法都会返回[SalesforceLoginResult](file:///D:/idea_demo/datai/datai-scenes/datai-scene-salesforce/datai-salesforce-auth/src/main/java/com/datai/auth/domain/SalesforceLoginResult.java#L15-L152)对象，包含以下字段：

| 字段名 | 类型 | 说明 |
|-------|-----|------|
| success | boolean | 登录是否成功 |
| accessToken | String | 访问令牌 |
| refreshToken | String | 刷新令牌 |
| instanceUrl | String | Salesforce实例URL |
| organizationId | String | 组织ID |
| userId | String | 用户ID |
| tokenType | String | 令牌类型（通常是"Bearer"） |
| expiresIn | long | 令牌过期时间（秒） |
| errorMessage | String | 错误消息 |
| errorCode | String | 错误码 |
