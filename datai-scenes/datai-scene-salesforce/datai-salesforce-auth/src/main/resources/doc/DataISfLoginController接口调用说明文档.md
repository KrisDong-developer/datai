# DataISfLoginController 接口调用说明文档

## 概述

`DataISfLoginController` 是 Salesforce 登录控制器，提供登录、登出、获取当前登录信息和自动登录等接口。

**基础路径**: `/salesforce/login`

**作者**: datai  
**日期**: 2025-12-14

---

## 接口列表

### 1. 执行登录操作

**接口地址**: `POST /salesforce/login/doLogin`

**功能描述**: 执行 Salesforce 登录操作，支持多种登录方式

**请求参数**: `SalesforceLoginRequest` (JSON)

**响应格式**: `AjaxResult`

---

#### 登录类型详细说明

系统支持以下 4 种登录类型，每种类型需要不同的参数：

---

### 1.1 OAuth 2.0 登录 (`loginType: "oauth2"`)

OAuth 2.0 登录支持三种授权类型（grant_type），每种类型需要不同的参数：

#### 1.1.1 密码流程 (`grantType: "password"`)

**适用场景**: 使用用户名、密码和安全令牌进行登录

**必填参数**:
- `loginType`: `"oauth2"`
- `grantType`: `"password"`
- `username`: Salesforce 用户名
- `password`: Salesforce 密码

**可选参数**:
- `securityToken`: Salesforce 安全令牌（如果需要）
- `loginUrl`: 登录系统链接地址（如：`https://login.salesforce.com` 或 `https://test.salesforce.com`）

**请求示例**:
```json
{
  "loginType": "oauth2",
  "grantType": "password",
  "username": "user@example.com",
  "password": "your_password",
  "securityToken": "your_security_token",
  "loginUrl": "https://test.salesforce.com"
}
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "登录成功",
  "data": {
    "success": true,
    "sessionId": "00Dxx0000000001!AQoAQH...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "userId": "005xx0000000001",
    "organizationId": "00Dxx0000000001",
    "instanceUrl": "https://your-instance.my.salesforce.com"
  }
}
```

---

#### 1.1.2 客户端凭证流程 (`grantType: "client_credentials"`)

**适用场景**: 使用客户端 ID 和客户端密钥进行登录

**必填参数**:
- `loginType`: `"oauth2"`
- `grantType`: `"client_credentials"`
- `clientId`: OAuth 客户端 ID
- `clientSecret`: OAuth 客户端密钥

**可选参数**:
- `loginUrl`: 登录系统链接地址（如：`https://login.salesforce.com` 或 `https://test.salesforce.com`）

**请求示例**:
```json
{
  "loginType": "oauth2",
  "grantType": "client_credentials",
  "clientId": "your_client_id",
  "clientSecret": "your_client_secret",
  "loginUrl": "https://test.salesforce.com"
}
```

---

#### 1.1.3 授权码流程 (`grantType: "authorization_code"`)

**适用场景**: 使用授权码进行登录（通常用于 Web 应用）

**必填参数**:
- `loginType`: `"oauth2"`
- `grantType`: `"authorization_code"`
- `clientId`: OAuth 客户端 ID
- `clientSecret`: OAuth 客户端密钥
- `code`: OAuth 授权码
- `state`: OAuth state 参数（用于防止 CSRF 攻击）

**可选参数**:
- `loginUrl`: 登录系统链接地址（如：`https://login.salesforce.com` 或 `https://test.salesforce.com`）

**请求示例**:
```json
{
  "loginType": "oauth2",
  "grantType": "authorization_code",
  "clientId": "your_client_id",
  "clientSecret": "your_client_secret",
  "code": "authorization_code_from_salesforce",
  "state": "random_state_string",
  "loginUrl": "https://test.salesforce.com"
}
```

---

### 1.2 传统账密凭证登录 (`loginType: "legacy_credential"`)

**适用场景**: 使用 SOAP API 进行传统账密凭证登录

**必填参数**:
- `loginType`: `"legacy_credential"`
- `username`: Salesforce 用户名
- `password`: Salesforce 密码

**可选参数**:
- `securityToken`: Salesforce 安全令牌（如果需要）
- `loginUrl`: 登录系统链接地址（如：`https://login.salesforce.com` 或 `https://test.salesforce.com`）

**请求示例**:
```json
{
  "loginType": "legacy_credential",
  "username": "user@example.com",
  "password": "your_password",
  "securityToken": "your_security_token",
  "loginUrl": "https://test.salesforce.com"
}
```

**特点**:
- 使用 SOAP API 进行登录
- 密码和安全令牌会自动组合（password + securityToken）
- 不支持刷新 Session，需要重新登录

---

### 1.3 Salesforce CLI 登录 (`loginType: "salesforce_cli"`)

**适用场景**: 使用 Salesforce CLI 工具进行登录

**必填参数**:
- `loginType`: `"salesforce_cli"`
- `orgAlias`: Salesforce CLI 组织别名

**请求示例**:
```json
{
  "loginType": "salesforce_cli",
  "orgAlias": "your_org_alias"
}
```

**特点**:
- 依赖本地安装的 Salesforce CLI 工具
- 使用 CLI 已认证的组织进行登录

---

### 1.4 Session ID 登录 (`loginType: "session_id"`)

**适用场景**: 使用已有的 Salesforce Session ID 进行登录

**必填参数**:
- `loginType`: `"session_id"`
- `sessionId`: Salesforce Session ID

**可选参数**:
- `loginUrl`: 登录系统链接地址（如：`https://login.salesforce.com` 或 `https://test.salesforce.com`）

**请求示例**:
```json
{
  "loginType": "session_id",
  "sessionId": "00Dxx0000000001!AQoAQH...",
  "loginUrl": "https://test.salesforce.com"
}
```

**特点**:
- 使用已存在的 Session ID 进行登录
- 适用于 Session 共享场景

---

## 2. 执行登出操作

**接口地址**: `POST /salesforce/login/logout`

**功能描述**: 执行 Salesforce 登出操作，自动从登录历史中获取最新一条登录成功的记录进行登出

**请求参数**: 无

**响应格式**: `AjaxResult`

**请求示例**:
```bash
curl -X POST http://localhost:8080/salesforce/login/logout
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "登出成功"
}
```

**错误响应示例**:
```json
{
  "code": 500,
  "msg": "未找到登录历史记录"
}
```

**特点**:
- 不需要传递任何参数
- 自动从登录历史信息表中查询最近一条登录成功的数据
- 清理缓存和会话状态

---

## 3. 获取当前登录信息

**接口地址**: `GET /salesforce/login/current`

**功能描述**: 获取当前登录信息，直接从缓存中获取当前缓存的登录结果

**请求参数**: 无

**响应格式**: `AjaxResult`

**请求示例**:
```bash
curl -X GET http://localhost:8080/salesforce/login/current
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "获取登录信息成功",
  "data": {
    "success": true,
    "sessionId": "00Dxx0000000001!AQoAQH...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "userId": "005xx0000000001",
    "organizationId": "00Dxx0000000001",
    "instanceUrl": "https://your-instance.my.salesforce.com",
    "metadataServerUrl": "https://your-instance.my.salesforce.com/services/Soap/m/65.0"
  }
}
```

**错误响应示例**:
```json
{
  "code": 500,
  "msg": "未找到登录信息"
}
```

**特点**:
- 不需要传递任何参数
- 直接从缓存（`salesforce_auth_cache`）中的 `current_result` 获取登录结果
- 响应速度快，无需查询数据库

---

## 4. 自动登录

**接口地址**: `POST /salesforce/login/autoLogin`

**功能描述**: 自动登录，直接查询数据库最新一条登录成功历史信息进行自动登录

**请求参数**: 无

**响应格式**: `AjaxResult`

**请求示例**:
```bash
curl -X POST http://localhost:8080/salesforce/login/autoLogin
```

**响应示例**:
```json
{
  "code": 200,
  "msg": "自动登录成功",
  "data": {
    "success": true,
    "sessionId": "00Dxx0000000001!AQoAQH...",
    "tokenType": "Bearer",
    "expiresIn": 3600,
    "userId": "005xx0000000001",
    "organizationId": "00Dxx0000000001",
    "instanceUrl": "https://your-instance.my.salesforce.com"
  }
}
```

**错误响应示例**:
```json
{
  "code": 500,
  "msg": "未找到登录历史记录"
}
```

**特点**:
- 不需要传递任何参数
- 自动从登录历史信息表中查询最近一条登录成功的历史记录
- 使用历史记录中的参数重新执行登录操作
- 适用于快速重新登录场景

---

## SalesforceLoginRequest 参数说明

| 参数名 | 类型 | 必填 | 说明 |
|--------|------|------|------|
| loginType | String | 是 | 登录类型：`oauth2`、`legacy_credential`、`salesforce_cli`、`session_id` |
| grantType | String | 条件必填 | OAuth 授权类型：`password`、`client_credentials`、`authorization_code`（仅 `oauth2` 登录时必填） |
| username | String | 条件必填 | Salesforce 用户名（`oauth2` + `password`、`legacy_credential` 登录时必填） |
| password | String | 条件必填 | Salesforce 密码（`oauth2` + `password`、`legacy_credential` 登录时必填） |
| securityToken | String | 可选 | Salesforce 安全令牌（`oauth2` + `password`、`legacy_credential` 登录时可选） |
| clientId | String | 条件必填 | OAuth 客户端 ID（`oauth2` + `client_credentials`、`oauth2` + `authorization_code` 登录时必填） |
| clientSecret | String | 条件必填 | OAuth 客户端密钥（`oauth2` + `client_credentials`、`oauth2` + `authorization_code` 登录时必填） |
| code | String | 条件必填 | OAuth 授权码（`oauth2` + `authorization_code` 登录时必填） |
| state | String | 条件必填 | OAuth state 参数（`oauth2` + `authorization_code` 登录时必填） |
| orgAlias | String | 条件必填 | Salesforce CLI 组织别名（`salesforce_cli` 登录时必填） |
| sessionId | String | 条件必填 | Salesforce Session ID（`session_id` 登录时必填） |
| loginUrl | String | 可选 | 登录系统链接地址，用于指定 Salesforce 登录的 API 端点地址（如：`https://login.salesforce.com` 或 `https://test.salesforce.com`） |
| privateKeyPath | String | 可选 | 私有密钥路径（预留字段） |
| privateKeyPassword | String | 可选 | 私有密钥密码（预留字段） |

---

## SalesforceLoginResult 响应字段说明

| 字段名 | 类型 | 说明 |
|--------|------|------|
| success | Boolean | 登录是否成功 |
| sessionId | String | Session ID |
| tokenType | String | Token 类型（如 "Bearer"） |
| expiresIn | Long | 过期时间（秒） |
| userId | String | 用户 ID |
| organizationId | String | 组织 ID |
| instanceUrl | String | 实例 URL |
| metadataServerUrl | String | 元数据服务器 URL |
| passwordExpired | Boolean | 密码是否已过期 |
| sandbox | Boolean | 是否为沙盒环境 |
| errorCode | String | 错误代码（失败时） |
| errorMessage | String | 错误信息（失败时） |

---

## 错误代码说明

| 错误代码 | 说明 |
|----------|------|
| LOGIN_TYPE_EMPTY | 登录类型不能为空 |
| MISSING_USERNAME | 用户名不能为空 |
| MISSING_PASSWORD | 密码不能为空 |
| INVALID_LOGIN_URL | 登录地址格式错误 |
| OAUTH2_UNSUPPORTED_GRANT_TYPE | 不支持的授权类型 |
| OAUTH2_CONFIG_ERROR | OAuth 配置错误 |
| OAUTH2_MISSING_CLIENT_ID | OAuth 客户端 ID 未配置 |
| LEGACY_LOGIN_FAILED | 传统登录失败 |
| CONFIG_NOT_FOUND | 配置未找到 |
| HISTORY_NOT_FOUND | 未找到登录历史记录 |
| HISTORY_STATUS_INVALID | 历史记录登录状态无效 |
| SYSTEM_ERROR | 系统异常 |

---

## 注意事项

1. **登录类型选择**: 根据实际场景选择合适的登录类型
2. **参数验证**: 确保必填参数都已正确填写
3. **缓存机制**: 登录成功后，结果会缓存到 `salesforce_auth_cache` 的 `current_result` 中
4. **历史记录**: 所有登录操作都会记录到登录历史表中
5. **会话管理**: 登出时会清理缓存和更新会话状态
6. **安全令牌**: 在生产环境中，建议使用安全令牌增强安全性
7. **环境配置**: 确保 Salesforce 环境配置正确（生产环境、沙盒环境或自定义环境）
8. **自定义登录地址**: `loginUrl` 参数为可选参数，用于指定 Salesforce 登录的 API 端点地址
   - 支持的登录地址示例：`https://login.salesforce.com`（生产环境）、`https://test.salesforce.com`（沙盒环境）
   - 如果不提供 `loginUrl`，系统将使用默认配置的登录地址
   - `loginUrl` 参数支持 `oauth2`、`legacy_credential` 和 `session_id` 登录类型
   - `salesforce_cli` 登录类型不支持 `loginUrl` 参数，因为 CLI 工具内部管理登录状态
   - 系统会自动验证 `loginUrl` 的格式，如果格式错误将返回错误信息

---

## 完整示例

### OAuth 2.0 密码流程登录示例

```bash
curl -X POST http://localhost:8080/salesforce/login/doLogin \
  -H "Content-Type: application/json" \
  -d '{
    "loginType": "oauth2",
    "grantType": "password",
    "username": "user@example.com",
    "password": "your_password",
    "securityToken": "your_security_token"
  }'
```

### 传统账密凭证登录示例

```bash
curl -X POST http://localhost:8080/salesforce/login/doLogin \
  -H "Content-Type: application/json" \
  -d '{
    "loginType": "legacy_credential",
    "username": "user@example.com",
    "password": "your_password",
    "securityToken": "your_security_token"
  }'
```

### Session ID 登录示例

```bash
curl -X POST http://localhost:8080/salesforce/login/doLogin \
  -H "Content-Type: application/json" \
  -d '{
    "loginType": "session_id",
    "sessionId": "00Dxx0000000001!AQoAQH..."
  }'
```

### 登出示例

```bash
curl -X POST http://localhost:8080/salesforce/login/logout
```

### 获取当前登录信息示例

```bash
curl -X GET http://localhost:8080/salesforce/login/current
```

### 自动登录示例

```bash
curl -X POST http://localhost:8080/salesforce/login/autoLogin
```

---

## 版本历史

| 版本 | 日期 | 说明 |
|------|------|------|
| 1.0 | 2025-12-14 | 初始版本 |
| 1.1 | 2025-12-25 | 优化 logout、getCurrentLoginInfo、autoLogin 方法，无需传参 |
| 1.2 | 2025-12-26 | 新增 loginUrl 参数，支持自定义 Salesforce 登录地址 |

---

## 相关文档

- [Salesforce OAuth 2.0 文档](https://help.salesforce.com/s/articleView?id=sf.remoteaccess_oauth_flow.htm)
- [Salesforce SOAP API 文档](https://developer.salesforce.com/docs/atlas.en-us.api_rest.meta/api_rest/)
- [Salesforce CLI 文档](https://developer.salesforce.com/docs/atlas.en-us.sfdx_cli_reference.meta/sfdx_cli_reference/)
