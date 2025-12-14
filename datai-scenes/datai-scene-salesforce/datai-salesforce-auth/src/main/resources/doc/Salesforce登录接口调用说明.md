# Salesforce登录接口调用说明文档

## 1. 文档说明

### 1.1 文档版本
| 版本 | 日期       | 作者   | 变更说明 |
|------|------------|--------|----------|
| 1.1  | 2025-12-14 | datai  | 新增SESSION_ID登录方式，完善错误码说明 |

### 1.2 术语定义
| 术语         | 解释说明                                         |
|--------------|--------------------------------------------------|
| Access Token | 访问令牌，用于访问Salesforce API的凭证           |
| Refresh Token| 刷新令牌，用于获取新的访问令牌                   |
| Instance URL | Salesforce实例URL，API请求的基础地址             |
| Identity URL | 身份URL，用于获取用户身份信息                    |
| AjaxResult   | 统一响应格式，包含success、message、data字段     |

## 2. 接口概述

### 2.1 基础信息
- 服务名称：Salesforce认证服务
- 基础URL：`/salesforce/login`
- 通信协议：HTTP/HTTPS
- 响应格式：JSON
- 编码格式：UTF-8

### 2.2 统一响应格式
所有接口返回统一的`AjaxResult`格式：

```json
{
  "success": true/false,    // 操作是否成功
  "message": "字符串",      // 操作结果描述
  "data": {}                // 响应数据（可选）
}
```

## 3. 接口详细说明

### 3.1 执行登录

#### 3.1.1 接口信息
- **接口名称**：执行登录
- **功能描述**：根据不同登录类型执行Salesforce登录操作
- **请求URL**：`/salesforce/login/execute`
- **请求方法**：POST
- **请求头**：`Content-Type: application/json`

#### 3.1.2 请求参数
| 参数类型 | 参数名称 | 数据类型 | 是否必填 | 默认值 | 参数说明               |
|----------|----------|----------|----------|--------|------------------------|
| 请求体   | request  | Object   | 是       | 无     | 登录请求对象           |

**request对象结构**：
| 参数名称   | 数据类型 | 是否必填 | 默认值 | 参数说明       |
|------------|----------|----------|--------|----------------|
| loginType  | String   | 是       | 无     | 登录类型，支持SESSION_ID |
| username   | String   | 否       | 无     | 用户名（根据登录类型而定） |
| password   | String   | 否       | 无     | 密码（根据登录类型而定） |
| sessionId  | String   | 否       | 无     | Salesforce Session ID（当loginType为SESSION_ID时必填） |
| 其他字段   | 任意     | 否       | 无     | 其他登录相关参数 |

#### 3.1.3 返回参数
| 参数名称 | 数据类型 | 返回值说明             | 可能的取值范围 |
|----------|----------|------------------------|----------------|
| success  | Boolean  | 登录是否成功           | true/false     |
| message  | String   | 登录结果描述           | 成功/失败信息  |
| data     | Object   | 登录结果对象           | SalesforceLoginResult |

**data对象结构**：
| 参数名称     | 数据类型 | 返回值说明           | 可能的取值范围 |
|--------------|----------|----------------------|----------------|
| success      | Boolean  | 登录是否成功         | true/false     |
| accessToken  | String   | 访问令牌             | 字符串         |
| refreshToken | String   | 刷新令牌             | 字符串         |
| instanceUrl  | String   | Salesforce实例URL    | URL地址        |
| identityUrl  | String   | 身份URL              | URL地址        |
| userId       | String   | 用户ID               | 字符串         |
| orgId        | String   | 组织ID               | 字符串         |
| errorMessage | String   | 错误信息             | 字符串         |

#### 3.1.4 错误码说明
| 错误码 | 错误信息示例                 | 说明                     |
|--------|------------------------------|--------------------------|
| -      | 登录失败: 用户名或密码错误   | 用户名或密码不正确       |
| -      | 登录失败: 连接超时           | 连接Salesforce超时       |
| -      | 登录失败: 无效的登录类型     | 登录类型不支持           |
| SESSION_ID_EMPTY | Session ID不能为空 | 使用SESSION_ID登录时，sessionId参数为空 |
| SESSION_ID_LOGIN_FAILED | Session ID登录失败: Invalid Session ID | Session ID无效或已过期 |
| ACCOUNT_LOCKED | Account is locked. Please try again later. | 账号已被锁定 |

#### 3.1.5 请求示例

**密码登录示例**：
```json
{
  "loginType": "PASSWORD",
  "username": "user@example.com",
  "password": "password123"
}
```

**Session ID登录示例**：
```json
{
  "loginType": "SESSION_ID",
  "sessionId": "00Dxxxxxxxxxxxx!AQEAQ..."
}
```

#### 3.1.6 响应示例

**密码登录成功响应**：
```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "success": true,
    "accessToken": "00Dxx0000000001!AQEAQ...",
    "refreshToken": "5Aep861...",
    "instanceUrl": "https://na152.salesforce.com",
    "identityUrl": "https://login.salesforce.com/id/00Dxx0000000001EAA/005xx0000000001AAA",
    "userId": "005xx0000000001AAA",
    "orgId": "00Dxx0000000001EAA",
    "errorMessage": null
  }
}
```

**Session ID登录成功响应**：
```json
{
  "success": true,
  "message": "登录成功",
  "data": {
    "success": true,
    "accessToken": "00Dxx0000000001!AQEAQ...",
    "refreshToken": null,
    "instanceUrl": "https://na152.salesforce.com",
    "identityUrl": null,
    "userId": "005xx0000000001AAA",
    "orgId": "00Dxx0000000001EAA",
    "errorMessage": null
  }
}
```

**密码登录失败响应**：
```json
{
  "success": false,
  "message": "登录失败: 用户名或密码错误",
  "data": null
}
```

**Session ID登录失败响应**：
```json
{
  "success": false,
  "message": "登录失败: Session ID不能为空",
  "data": null
}
```

**无效Session ID响应**：
```json
{
  "success": false,
  "message": "登录失败: Session ID登录失败: Invalid Session ID",
  "data": null
}
```

#### 3.1.7 代码逻辑流程
1. 记录登录类型日志
2. 调用`ISalesforceLoginService.login()`方法执行登录
3. 判断登录结果是否成功
4. 登录成功：返回包含访问令牌、刷新令牌等信息的完整登录结果
5. 登录失败：返回错误信息
6. 捕获所有异常，记录错误日志并返回错误信息

### 3.2 刷新访问令牌

#### 3.2.1 接口信息
- **接口名称**：刷新访问令牌
- **功能描述**：使用刷新令牌获取新的访问令牌
- **请求URL**：`/salesforce/login/refresh-token`
- **请求方法**：POST
- **请求头**：`Content-Type: application/x-www-form-urlencoded`

#### 3.2.2 请求参数
| 参数类型 | 参数名称     | 数据类型 | 是否必填 | 默认值 | 参数说明         |
|----------|--------------|----------|----------|--------|------------------|
| 查询参数 | refreshToken | String   | 是       | 无     | 刷新令牌         |
| 查询参数 | loginType    | String   | 是       | 无     | 登录类型         |

#### 3.2.3 返回参数
| 参数名称 | 数据类型 | 返回值说明             | 可能的取值范围 |
|----------|----------|------------------------|----------------|
| success  | Boolean  | 刷新是否成功           | true/false     |
| message  | String   | 刷新结果描述           | 成功/失败信息  |
| data     | Object   | 新的登录结果对象       | SalesforceLoginResult |

**data对象结构**：同3.1.3中的data对象结构

#### 3.2.4 错误码说明
| 错误码 | 错误信息示例                 | 说明                     |
|--------|------------------------------|--------------------------|
| -      | 刷新令牌失败: 无效的刷新令牌 | 刷新令牌已过期或无效     |
| -      | 刷新令牌失败: 连接超时       | 连接Salesforce超时       |
| -      | 刷新令牌失败: 无效的登录类型 | 登录类型不支持           |
| REFRESH_TOKEN_NOT_SUPPORTED | Session ID登录不支持刷新令牌 | 使用SESSION_ID登录时不支持刷新令牌操作 |

#### 3.2.5 请求示例
```
POST /salesforce/login/refresh-token
Content-Type: application/x-www-form-urlencoded

refreshToken=5Aep861...&loginType=PASSWORD
```

#### 3.2.6 响应示例

**成功响应**：
```json
{
  "success": true,
  "message": "令牌刷新成功",
  "data": {
    "success": true,
    "accessToken": "00Dxx0000000001!AQEBQ...",
    "refreshToken": "5Aep862...",
    "instanceUrl": "https://na152.salesforce.com",
    "identityUrl": "https://login.salesforce.com/id/00Dxx0000000001EAA/005xx0000000001AAA",
    "userId": "005xx0000000001AAA",
    "orgId": "00Dxx0000000001EAA",
    "errorMessage": null
  }
}
```

**失败响应 - 无效的刷新令牌**：
```json
{
  "success": false,
  "message": "刷新令牌失败: 无效的刷新令牌",
  "data": null
}
```

**失败响应 - Session ID不支持刷新**：
```json
{
  "success": false,
  "message": "刷新令牌失败: Session ID登录不支持刷新令牌",
  "data": null
}
```

#### 3.2.7 代码逻辑流程
1. 记录刷新令牌操作日志
2. 调用`ISalesforceLoginService.refreshToken()`方法刷新令牌
3. 判断刷新结果是否成功
4. 刷新成功：返回包含新访问令牌的登录结果
5. 刷新失败：返回错误信息
6. 捕获所有异常，记录错误日志并返回错误信息

### 3.3 执行登出

#### 3.3.1 接口信息
- **接口名称**：执行登出
- **功能描述**：退出登录，清理登录状态
- **请求URL**：`/salesforce/login/logout`
- **请求方法**：POST
- **请求头**：`Content-Type: application/x-www-form-urlencoded`

#### 3.3.2 请求参数
| 参数类型 | 参数名称     | 数据类型 | 是否必填 | 默认值 | 参数说明         |
|----------|--------------|----------|----------|--------|------------------|
| 查询参数 | accessToken  | String   | 是       | 无     | 访问令牌         |
| 查询参数 | loginType    | String   | 是       | 无     | 登录类型         |

#### 3.3.3 返回参数
| 参数名称 | 数据类型 | 返回值说明             | 可能的取值范围 |
|----------|----------|------------------------|----------------|
| success  | Boolean  | 登出是否成功           | true/false     |
| message  | String   | 登出结果描述           | 成功/失败信息  |
| data     | null     | 无数据返回             | null           |

#### 3.3.4 错误码说明
| 错误码 | 错误信息示例                 | 说明                     |
|--------|------------------------------|--------------------------|
| -      | 登出失败: 无效的访问令牌     | 访问令牌已过期或无效     |
| -      | 登出失败: 连接超时           | 连接Salesforce超时       |
| -      | 登出失败: 无效的登录类型     | 登录类型不支持           |

#### 3.3.5 请求示例
```
POST /salesforce/login/logout
Content-Type: application/x-www-form-urlencoded

accessToken=00Dxx0000000001!AQEAQ...&loginType=PASSWORD
```

#### 3.3.6 响应示例

**成功响应**：
```json
{
  "success": true,
  "message": "登出成功",
  "data": null
}
```

**失败响应**：
```json
{
  "success": false,
  "message": "登出失败: 无效的访问令牌",
  "data": null
}
```

#### 3.3.7 代码逻辑流程
1. 记录登出操作日志
2. 调用`ISalesforceLoginService.logout()`方法执行登出
3. 判断登出结果是否成功
4. 返回登出结果
5. 捕获所有异常，记录错误日志并返回错误信息

### 3.4 获取当前登录状态

#### 3.4.1 接口信息
- **接口名称**：获取当前登录状态
- **功能描述**：获取当前系统的登录状态
- **请求URL**：`/salesforce/login/status`
- **请求方法**：GET
- **请求头**：无特殊请求头

#### 3.4.2 请求参数
无请求参数

#### 3.4.3 返回参数
| 参数名称 | 数据类型 | 返回值说明             | 可能的取值范围 |
|----------|----------|------------------------|----------------|
| success  | Boolean  | 获取是否成功           | true/false     |
| message  | String   | 获取结果描述           | 成功/失败信息  |
| data     | Object   | 当前登录状态对象       | SalesforceLoginResult |

**data对象结构**：同3.1.3中的data对象结构

#### 3.4.4 错误码说明
| 错误码 | 错误信息示例                     | 说明                     |
|--------|----------------------------------|--------------------------|
| -      | 获取登录状态失败: 无活跃会话     | 当前没有活跃的登录会话   |
| -      | 获取登录状态失败: 内部错误       | 系统内部错误             |

#### 3.4.5 请求示例
```
GET /salesforce/login/status
```

#### 3.4.6 响应示例

**成功响应**：
```json
{
  "success": true,
  "message": "获取成功",
  "data": {
    "success": true,
    "accessToken": "00Dxx0000000001!AQEAQ...",
    "refreshToken": "5Aep861...",
    "instanceUrl": "https://na152.salesforce.com",
    "identityUrl": "https://login.salesforce.com/id/00Dxx0000000001EAA/005xx0000000001AAA",
    "userId": "005xx0000000001AAA",
    "orgId": "00Dxx0000000001EAA",
    "errorMessage": null
  }
}
```

**失败响应**：
```json
{
  "success": false,
  "message": "获取登录状态失败: 无活跃会话",
  "data": null
}
```

#### 3.4.7 代码逻辑流程
1. 记录获取登录状态日志
2. 调用`ISalesforceLoginService.getCurrentLoginStatus()`方法获取状态
3. 返回登录状态
4. 捕获所有异常，记录错误日志并返回错误信息

### 3.5 验证令牌有效性

#### 3.5.1 接口信息
- **接口名称**：验证令牌有效性
- **功能描述**：验证访问令牌的有效性
- **请求URL**：`/salesforce/login/validate-token`
- **请求方法**：GET
- **请求头**：无特殊请求头

#### 3.5.2 请求参数
| 参数类型 | 参数名称    | 数据类型 | 是否必填 | 默认值 | 参数说明         |
|----------|-------------|----------|----------|--------|------------------|
| 查询参数 | accessToken | String   | 是       | 无     | 访问令牌         |

#### 3.5.3 返回参数
| 参数名称 | 数据类型 | 返回值说明             | 可能的取值范围 |
|----------|----------|------------------------|----------------|
| success  | Boolean  | 验证是否成功           | true/false     |
| message  | String   | 验证结果描述           | 令牌有效/令牌无效/验证失败 |
| data     | null     | 无数据返回             | null           |

#### 3.5.4 错误码说明
| 错误码 | 错误信息示例                 | 说明                     |
|--------|------------------------------|--------------------------|
| -      | 验证令牌失败: 无效的令牌     | 令牌已过期或无效         |
| -      | 验证令牌失败: 内部错误       | 系统内部错误             |

#### 3.5.5 请求示例
```
GET /salesforce/login/validate-token?accessToken=00Dxx0000000001!AQEAQ...
```

#### 3.5.6 响应示例

**成功响应**：
```json
{
  "success": true,
  "message": "令牌有效",
  "data": null
}
```

**失败响应**：
```json
{
  "success": false,
  "message": "令牌无效",
  "data": null
}
```

#### 3.5.7 代码逻辑流程
1. 记录验证令牌日志
2. 调用`ITokenManager.validateToken()`方法验证令牌
3. 判断令牌是否有效
4. 令牌有效：返回成功信息
5. 令牌无效：返回失败信息
6. 捕获所有异常，记录错误日志并返回错误信息

### 3.6 绑定令牌到设备/IP

#### 3.6.1 接口信息
- **接口名称**：绑定令牌到设备/IP
- **功能描述**：将令牌绑定到指定设备和IP，增强安全性
- **请求URL**：`/salesforce/login/bind-token`
- **请求方法**：POST
- **请求头**：`Content-Type: application/x-www-form-urlencoded`

#### 3.6.2 请求参数
| 参数类型 | 参数名称     | 数据类型 | 是否必填 | 默认值 | 参数说明         |
|----------|--------------|----------|----------|--------|------------------|
| 查询参数 | accessToken  | String   | 是       | 无     | 访问令牌         |
| 查询参数 | deviceId     | String   | 否       | 无     | 设备ID           |
| 查询参数 | ip           | String   | 否       | 无     | IP地址           |

#### 3.6.3 返回参数
| 参数名称 | 数据类型 | 返回值说明             | 可能的取值范围 |
|----------|----------|------------------------|----------------|
| success  | Boolean  | 绑定是否成功           | true/false     |
| message  | String   | 绑定结果描述           | 成功/失败信息  |
| data     | null     | 无数据返回             | null           |

#### 3.6.4 错误码说明
| 错误码 | 错误信息示例                 | 说明                     |
|--------|------------------------------|--------------------------|
| -      | 绑定令牌失败: 无效的令牌     | 令牌已过期或无效         |
| -      | 绑定令牌失败: 内部错误       | 系统内部错误             |

#### 3.6.5 请求示例
```
POST /salesforce/login/bind-token
Content-Type: application/x-www-form-urlencoded

accessToken=00Dxx0000000001!AQEAQ...&deviceId=dev-123&ip=192.168.1.1
```

#### 3.6.6 响应示例

**成功响应**：
```json
{
  "success": true,
  "message": "令牌绑定成功",
  "data": null
}
```

**失败响应**：
```json
{
  "success": false,
  "message": "绑定令牌失败: 无效的令牌",
  "data": null
}
```

#### 3.6.7 代码逻辑流程
1. 记录绑定令牌日志
2. 调用`ITokenManager.bindToken()`方法绑定令牌
3. 绑定成功：返回成功信息
4. 捕获所有异常，记录错误日志并返回错误信息

### 3.7 检查令牌绑定

#### 3.7.1 接口信息
- **接口名称**：检查令牌绑定
- **功能描述**：检查令牌是否与指定设备和IP匹配
- **请求URL**：`/salesforce/login/check-binding`
- **请求方法**：GET
- **请求头**：无特殊请求头

#### 3.7.2 请求参数
| 参数类型 | 参数名称     | 数据类型 | 是否必填 | 默认值 | 参数说明         |
|----------|--------------|----------|----------|--------|------------------|
| 查询参数 | accessToken  | String   | 是       | 无     | 访问令牌         |
| 查询参数 | deviceId     | String   | 否       | 无     | 设备ID           |
| 查询参数 | ip           | String   | 否       | 无     | IP地址           |

#### 3.7.3 返回参数
| 参数名称 | 数据类型 | 返回值说明             | 可能的取值范围 |
|----------|----------|------------------------|----------------|
| success  | Boolean  | 检查是否成功           | true/false     |
| message  | String   | 检查结果描述           | 令牌绑定匹配/令牌绑定不匹配/检查失败 |
| data     | null     | 无数据返回             | null           |

#### 3.7.4 错误码说明
| 错误码 | 错误信息示例                 | 说明                     |
|--------|------------------------------|--------------------------|
| -      | 检查令牌绑定失败: 无效的令牌 | 令牌已过期或无效         |
| -      | 检查令牌绑定失败: 内部错误   | 系统内部错误             |

#### 3.7.5 请求示例
```
GET /salesforce/login/check-binding?accessToken=00Dxx0000000001!AQEAQ...&deviceId=dev-123&ip=192.168.1.1
```

#### 3.7.6 响应示例

**成功响应（绑定匹配）**：
```json
{
  "success": true,
  "message": "令牌绑定匹配",
  "data": null
}
```

**成功响应（绑定不匹配）**：
```json
{
  "success": false,
  "message": "令牌绑定不匹配",
  "data": null
}
```

**失败响应**：
```json
{
  "success": false,
  "message": "检查令牌绑定失败: 无效的令牌",
  "data": null
}
```

#### 3.7.7 代码逻辑流程
1. 记录检查令牌绑定日志
2. 调用`ITokenManager.checkTokenBinding()`方法检查绑定
3. 判断绑定是否匹配
4. 绑定匹配：返回成功信息
5. 绑定不匹配：返回失败信息
6. 捕获所有异常，记录错误日志并返回错误信息

### 3.8 获取当前登录会话信息

#### 3.8.1 接口信息
- **接口名称**：获取当前登录会话信息
- **功能描述**：获取当前登录成功的会话详细信息
- **请求URL**：`/salesforce/login/session`
- **请求方法**：GET
- **请求头**：无特殊请求头

#### 3.8.2 请求参数
无请求参数

#### 3.8.3 返回参数
| 参数名称 | 数据类型 | 返回值说明             | 可能的取值范围 |
|----------|----------|------------------------|----------------|
| success  | Boolean  | 获取是否成功           | true/false     |
| message  | String   | 获取结果描述           | 成功/失败信息  |
| data     | Object   | 当前登录会话对象       | DataiSfLoginSession |

**data对象结构**：
| 参数名称     | 数据类型 | 返回值说明           | 可能的取值范围 |
|--------------|----------|----------------------|----------------|
| accessToken  | String   | 访问令牌             | 字符串         |
| refreshToken | String   | 刷新令牌             | 字符串         |
| loginType    | String   | 登录类型             | 字符串         |
| instanceUrl  | String   | Salesforce实例URL    | URL地址        |
| identityUrl  | String   | 身份URL              | URL地址        |
| userId       | String   | 用户ID               | 字符串         |
| orgId        | String   | 组织ID               | 字符串         |
| loginTime    | Date     | 登录时间             | 日期时间       |
| 其他字段     | 任意     | 其他会话相关信息     | 自定义         |

#### 3.8.4 错误码说明
| 错误码 | 错误信息示例                     | 说明                     |
|--------|----------------------------------|--------------------------|
| -      | 当前没有活跃的登录会话           | 当前没有活跃的登录会话   |
| -      | 获取当前登录会话信息失败: 内部错误 | 系统内部错误             |

#### 3.8.5 请求示例
```
GET /salesforce/login/session
```

#### 3.8.6 响应示例

**成功响应**：
```json
{
  "success": true,
  "message": "获取成功",
  "data": {
    "accessToken": "00Dxx0000000001!AQEAQ...",
    "refreshToken": "5Aep861...",
    "loginType": "PASSWORD",
    "instanceUrl": "https://na152.salesforce.com",
    "identityUrl": "https://login.salesforce.com/id/00Dxx0000000001EAA/005xx0000000001AAA",
    "userId": "005xx0000000001AAA",
    "orgId": "00Dxx0000000001EAA",
    "loginTime": "2025-12-14T10:00:00.000+0000"
  }
}
```

**失败响应**：
```json
{
  "success": false,
  "message": "当前没有活跃的登录会话",
  "data": null
}
```

#### 3.8.7 代码逻辑流程
1. 记录获取会话信息日志
2. 调用`ISalesforceLoginService.getCurrentLoginSession()`方法获取会话
3. 判断会话是否存在
4. 会话存在：返回会话详细信息
5. 会话不存在：返回错误信息
6. 捕获所有异常，记录错误日志并返回错误信息

## 4. 安全说明

1. 所有接口建议通过HTTPS访问，确保数据传输安全
2. 访问令牌和刷新令牌是敏感信息，应妥善保管，避免泄露
3. 建议定期刷新令牌，以提高安全性
4. 绑定令牌到设备和IP可以进一步增强安全性
5. 访问令牌具有一定的有效期，过期后需要使用刷新令牌获取新的访问令牌
6. 登出操作会使访问令牌和刷新令牌失效，建议在用户退出时调用

## 5. 调用建议

1. 登录成功后，建议将返回的访问令牌和刷新令牌存储在安全的地方
2. 每次调用Salesforce API前，建议先验证令牌有效性
3. 当令牌即将过期或已过期时，及时调用刷新令牌接口获取新令牌
4. 用户退出系统时，务必调用登出接口清理登录状态
5. 对于敏感操作，建议结合令牌绑定功能，限制令牌的使用范围
6. 定期获取登录状态，确保系统状态的一致性

## 6. 文档维护

本文档由系统自动生成，基于`DataISfLoginController.java`文件的代码分析。若代码发生变更，请重新生成文档以保持文档的准确性。

---

**文档生成时间**：2025-12-14
**文档版本**：1.1
**生成工具**：AI自动生成
