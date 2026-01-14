# 接口文档模板

## 接口信息

- **接口名称**: 执行Salesforce登录操作（源ORG）
- **接口路径**: /salesforce/login/doLogin
- **请求方法**: POST
- **模块归属**: datai-salesforce-auth
- **版本号**: v1.0
- **创建日期**: 2026-01-14
- **最后更新**: 2026-01-14

## 功能描述

执行Salesforce源ORG的登录操作，支持多种登录方式（OAuth2、Salesforce CLI、Legacy Credential等）。登录成功后返回Session ID、Instance URL等授权信息，并将登录信息保存到登录历史记录中。

## 请求参数

### 请求体 (JSON)

```json
{
  "loginType": "oauth2",
  "username": "user@example.com",
  "password": "password123",
  "securityToken": "securityToken123",
  "clientId": "client_id",
  "clientSecret": "client_secret",
  "grantType": "password",
  "orgAlias": "my-org",
  "privateKeyPath": "/path/to/private.key",
  "privateKeyPassword": "keyPassword",
  "code": "authorization_code",
  "state": "random_state",
  "sessionId": "existing_session_id",
  "loginUrl": "https://login.salesforce.com",
  "orgType": "source"
}
```

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| loginType | String | 是 | 登录类型，如oauth2、salesforce_cli、legacy_credential等 | oauth2 |
| username | String | 否 | 用户名（legacy_credential登录方式必填） | user@example.com |
| password | String | 否 | 密码（legacy_credential登录方式必填） | password123 |
| securityToken | String | 否 | 安全令牌（legacy_credential登录方式必填） | securityToken123 |
| clientId | String | 否 | OAuth客户端ID（oauth2登录方式必填） | client_id |
| clientSecret | String | 否 | OAuth客户端密钥（oauth2登录方式必填） | client_secret |
| grantType | String | 否 | OAuth授权类型（oauth2登录方式必填） | password |
| orgAlias | String | 否 | Salesforce CLI组织别名（salesforce_cli登录方式必填） | my-org |
| privateKeyPath | String | 否 | 私有密钥路径（JWT登录方式必填） | /path/to/private.key |
| privateKeyPassword | String | 否 | 私有密钥密码（JWT登录方式必填） | keyPassword |
| code | String | 否 | OAuth授权码（authorization_code登录方式必填） | authorization_code |
| state | String | 否 | OAuth state参数，用于防止CSRF攻击 | random_state |
| sessionId | String | 否 | Session ID（Session ID登录方式必填） | existing_session_id |
| loginUrl | String | 否 | 登录URL | https://login.salesforce.com |
| orgType | String | 否 | 组织类型，默认为source | source |

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "success": true,
    "sessionId": "00D...!AQc...",
    "refreshToken": "5Aep...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "loginTimestamp": 1736803200000,
    "expirationTimestamp": 1736806800000,
    "instanceUrl": "https://yourinstance.my.salesforce.com",
    "metadataServerUrl": "https://yourinstance.my.salesforce.com/services/Soap/m/59.0",
    "sandbox": false,
    "passwordExpired": false,
    "userId": "005...",
    "organizationId": "00D...",
    "orgType": "source",
    "userFullName": "John Doe",
    "userEmail": "john.doe@example.com",
    "organizationName": "Acme Corp",
    "language": "en_US",
    "timeZone": "America/Los_Angeles"
  }
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| success | Boolean | 登录是否成功 | true |
| sessionId | String | Session ID，用于API调用 | 00D...!AQc... |
| refreshToken | String | 刷新令牌（仅OAuth流程中存在） | 5Aep... |
| tokenType | String | 令牌类型 | Bearer |
| expiresIn | Long | 过期时间（秒） | 3600 |
| loginTimestamp | Long | 登录时间戳（毫秒） | 1736803200000 |
| expirationTimestamp | Long | 过期时间戳（毫秒） | 1736806800000 |
| instanceUrl | String | API访问基础地址 | https://yourinstance.my.salesforce.com |
| metadataServerUrl | String | 元数据API专用地址 | https://yourinstance.my.salesforce.com/services/Soap/m/59.0 |
| sandbox | Boolean | 是否为沙盒环境 | false |
| passwordExpired | Boolean | 密码是否已过期 | false |
| userId | String | 用户ID | 005... |
| organizationId | String | 组织ID | 00D... |
| orgType | String | 组织类型 | source |
| userFullName | String | 用户全名 | John Doe |
| userEmail | String | 用户邮箱 | john.doe@example.com |
| organizationName | String | 组织名称 | Acme Corp |
| language | String | 语言设置 | en_US |
| timeZone | String | 时区设置 | America/Los_Angeles |

### 失败响应

**HTTP 状态码**: 400/500

```json
{
  "code": 400,
  "message": "登录失败: 用户名或密码错误",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 400 | 登录失败: 用户名或密码错误 | 用户名或密码不正确 |
| 400 | 登录失败: 无效的登录类型 | 登录类型不支持或无效 |
| 400 | 登录失败: 缺少必要参数 | 缺少登录所需的必要参数 |
| 500 | 登录失败: 系统错误 | 系统内部错误 |

## 接口示例

### 请求示例

```bash
curl -X POST "http://localhost:8080/salesforce/login/doLogin" \
  -H "Content-Type: application/json" \
  -d '{
    "loginType": "oauth2",
    "username": "user@example.com",
    "password": "password123",
    "securityToken": "securityToken123",
    "clientId": "client_id",
    "clientSecret": "client_secret",
    "grantType": "password",
    "orgType": "source"
  }'
```

### 响应示例

**成功**:

```json
{
  "code": 200,
  "message": "登录成功",
  "data": {
    "success": true,
    "sessionId": "00D...!AQc...",
    "instanceUrl": "https://yourinstance.my.salesforce.com",
    "userId": "005...",
    "organizationId": "00D...",
    "orgType": "source"
  }
}
```

**失败**:

```json
{
  "code": 400,
  "message": "登录失败: 用户名或密码错误",
  "data": null
}
```

## 错误处理

1. **参数验证失败**: 检查请求参数是否完整和有效
2. **登录失败**: 根据错误码和错误信息判断失败原因
3. **系统错误**: 记录错误日志并返回通用错误信息

## 注意事项

1. 登录成功后，Session ID会保存到登录历史记录中
2. Session ID有过期时间，过期后需要重新登录或使用刷新令牌
3. 不同登录方式需要提供不同的参数组合
4. 密码等敏感信息在日志中会被脱敏处理
5. orgType参数默认为source，如需登录目标ORG请使用目标ORG登录接口

## 相关接口

- [登出操作](0002-logout.md) - 执行Salesforce登出操作
- [获取当前登录信息](0003-current.md) - 获取当前登录信息
- [自动登录](0004-auto-login.md) - 自动登录（支持源ORG和目标ORG）

## 实现细节

1. 根据loginType选择对应的登录策略
2. 调用SalesforceLoginService执行登录
3. 登录成功后将结果保存到登录历史记录
4. 返回完整的登录结果信息

## 测试信息

### 测试环境

- **环境**: 本地开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| OAuth2登录成功 | loginType=oauth2, username=valid, password=valid | 返回Session ID | 返回Session ID | 通过 |
| 用户名密码错误 | loginType=oauth2, username=invalid, password=invalid | 返回错误信息 | 返回错误信息 | 通过 |
| 缺少必要参数 | loginType=oauth2, 缺少password | 返回参数错误 | 返回参数错误 | 通过 |
| 无效登录类型 | loginType=invalid | 返回无效登录类型 | 返回无效登录类型 | 通过 |
