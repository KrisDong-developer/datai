# 接口文档模板

## 接口信息

- **接口名称**: 获取当前登录信息
- **接口路径**: /salesforce/login/current
- **请求方法**: GET
- **模块归属**: datai-salesforce-auth
- **版本号**: v1.0
- **创建日期**: 2026-01-14
- **最后更新**: 2026-01-14

## 功能描述

获取指定ORG类型的当前登录信息，包括Session ID、Instance URL、用户信息、组织信息等。支持查询源ORG和目标ORG的登录状态。

## 请求参数

### 查询参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
| orgType | String | 是 | ORG类型（source或target） | source | - |

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "message": "获取登录信息成功",
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
  "message": "未找到登录信息",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 400 | 未找到登录信息 | 指定ORG类型没有登录信息 |
| 400 | 参数错误 | orgType参数无效 |
| 500 | 获取登录信息失败: 系统错误 | 系统内部错误 |

## 接口示例

### 请求示例

```bash
curl -X GET "http://localhost:8080/salesforce/login/current?orgType=source" \
  -H "Content-Type: application/json"
```

### 响应示例

**成功**:

```json
{
  "code": 200,
  "message": "获取登录信息成功",
  "data": {
    "success": true,
    "sessionId": "00D...!AQc...",
    "instanceUrl": "https://yourinstance.my.salesforce.com",
    "userId": "005...",
    "organizationId": "00D...",
    "orgType": "source",
    "userFullName": "John Doe",
    "userEmail": "john.doe@example.com"
  }
}
```

**失败**:

```json
{
  "code": 400,
  "message": "未找到登录信息",
  "data": null
}
```

## 错误处理

1. **未找到登录信息**: 检查指定ORG类型是否已登录
2. **参数错误**: 检查orgType参数是否为有效的source或target
3. **系统错误**: 记录错误日志并返回通用错误信息

## 注意事项

1. orgType参数必须为source或target
2. 如果指定ORG类型未登录，将返回未找到登录信息
3. 返回的登录信息包含完整的用户和组织信息
4. 可以通过expirationTimestamp判断Session是否即将过期

## 相关接口

- [登录操作](0001-do-login.md) - 执行Salesforce登录操作
- [登出操作](0002-logout.md) - 执行Salesforce登出操作
- [自动登录](0004-auto-login.md) - 自动登录（支持源ORG和目标ORG）

## 实现细节

1. 根据orgType参数查询对应的登录信息
2. 调用SalesforceLoginService的getCurrentLoginResultByOrgType方法
3. 返回完整的登录结果信息

## 测试信息

### 测试环境

- **环境**: 本地开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 获取源ORG登录信息 | orgType=source | 返回源ORG登录信息 | 返回源ORG登录信息 | 通过 |
| 获取目标ORG登录信息 | orgType=target | 返回目标ORG登录信息 | 返回目标ORG登录信息 | 通过 |
| 未登录时获取信息 | orgType=source | 返回未找到登录信息 | 返回未找到登录信息 | 通过 |
| 无效orgType参数 | orgType=invalid | 返回参数错误 | 返回参数错误 | 通过 |
