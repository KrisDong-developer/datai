# 接口文档模板

## 接口信息

- **接口名称**: 自动登录（支持源ORG和目标ORG）
- **接口路径**: /salesforce/login/autoLogin
- **请求方法**: POST
- **模块归属**: datai-salesforce-auth
- **版本号**: v1.0
- **创建日期**: 2026-01-14
- **最后更新**: 2026-01-14

## 功能描述

使用登录历史记录中的信息自动登录Salesforce。系统会从登录历史记录中获取最新的成功登录信息，使用相同的登录参数重新登录，支持源ORG和目标ORG的自动登录。

## 请求参数

无需请求参数。

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "message": "自动登录成功",
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
  "message": "未找到登录历史记录",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 400 | 未找到登录历史记录 | 系统中没有找到登录历史记录 |
| 400 | 自动登录失败: 用户名或密码错误 | 登录凭证无效 |
| 400 | 自动登录失败: Session已过期 | Session已过期 |
| 500 | 自动登录失败: 系统错误 | 系统内部错误 |

## 接口示例

### 请求示例

```bash
curl -X POST "http://localhost:8080/salesforce/login/autoLogin" \
  -H "Content-Type: application/json"
```

### 响应示例

**成功**:

```json
{
  "code": 200,
  "message": "自动登录成功",
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
  "message": "未找到登录历史记录",
  "data": null
}
```

## 错误处理

1. **未找到登录历史记录**: 检查是否已经登录过
2. **登录凭证无效**: 检查用户名、密码等凭证是否正确
3. **Session已过期**: 检查Session是否有效
4. **系统错误**: 记录错误日志并返回通用错误信息

## 注意事项

1. 自动登录使用登录历史记录中的登录参数
2. 如果登录凭证已变更（如密码已修改），自动登录可能失败
3. 自动登录会创建新的登录历史记录
4. 支持源ORG和目标ORG的自动登录
5. 如果有多个ORG的登录历史记录，会使用最新的记录

## 相关接口

- [登录操作](0001-do-login.md) - 执行Salesforce登录操作
- [登出操作](0002-logout.md) - 执行Salesforce登出操作
- [获取当前登录信息](0003-current.md) - 获取当前登录信息

## 实现细节

1. 从登录历史记录中获取最新的成功登录信息
2. 提取历史记录ID和ORG类型
3. 调用SalesforceLoginService的autoLogin方法
4. 返回新的登录结果信息

## 测试信息

### 测试环境

- **环境**: 本地开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 自动登录成功 | 无 | 返回新的Session ID | 返回新的Session ID | 通过 |
| 未登录时自动登录 | 无 | 返回未找到登录历史记录 | 返回未找到登录历史记录 | 通过 |
| 密码已修改后自动登录 | 无 | 返回登录失败 | 返回登录失败 | 通过 |
| Session已过期后自动登录 | 无 | 返回新的Session ID | 返回新的Session ID | 通过 |
