# 接口文档

## 接口信息

- **接口名称**: 获取配置审计日志详细信息
- **接口路径**: /setting/log/{id}
- **请求方法**: GET
- **模块归属**: datai-salesforce-setting
- **版本号**: v1.0
- **创建日期**: 2026-01-14
- **最后更新**: 2026-01-14

## 功能描述

根据日志ID获取配置审计日志的详细信息，包括对象名称（通过对象类型和ID解析）。返回的数据包含完整的审计信息，便于用户查看具体的配置变更详情。

## 请求参数

### 路径参数

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| id | Long | 是 | 日志ID | 1 |

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "deptId": 1,
    "operationType": "UPDATE",
    "objectType": "CONFIGURATION",
    "objectId": 100,
    "objectName": "API配置",
    "oldValue": "{\"apiVersion\":\"59.0\",\"apiEndpoint\":\"https://api.salesforce.com\"}",
    "newValue": "{\"apiVersion\":\"60.0\",\"apiEndpoint\":\"https://api.salesforce.com\"}",
    "operationDesc": "更新API版本从59.0到60.0",
    "operator": "admin",
    "operationTime": "2026-01-14 10:30:00",
    "ipAddress": "192.168.1.100",
    "userAgent": "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/120.0.0.0 Safari/537.36",
    "requestId": "req_123456789",
    "result": "SUCCESS",
    "errorMessage": null,
    "orgType": "source",
    "createBy": "admin",
    "createTime": "2026-01-14 10:30:00",
    "updateBy": null,
    "updateTime": null
  }
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| id | Long | 日志ID | 1 |
| deptId | Long | 部门ID | 1 |
| operationType | String | 操作类型（CREATE/UPDATE/DELETE） | UPDATE |
| objectType | String | 对象类型（CONFIGURATION/ENVIRONMENT/SNAPSHOT等） | CONFIGURATION |
| objectId | Long | 对象ID | 100 |
| objectName | String | 对象名称（通过解析获得） | API配置 |
| oldValue | String | 旧值（JSON格式） | {"apiVersion":"59.0"...} |
| newValue | String | 新值（JSON格式） | {"apiVersion":"60.0"...} |
| operationDesc | String | 操作描述 | 更新API版本从59.0到60.0 |
| operator | String | 操作人 | admin |
| operationTime | String | 操作时间 | 2026-01-14 10:30:00 |
| ipAddress | String | IP地址 | 192.168.1.100 |
| userAgent | String | 用户代理 | Mozilla/5.0... |
| requestId | String | 请求ID | req_123456789 |
| result | String | 操作结果（SUCCESS/FAILED） | SUCCESS |
| errorMessage | String | 错误信息 | null |
| orgType | String | ORG类型（source/target） | source |
| createBy | String | 创建人 | admin |
| createTime | String | 创建时间 | 2026-01-14 10:30:00 |
| updateBy | String | 更新人 | null |
| updateTime | String | 更新时间 | null |

### 失败响应

**HTTP 状态码**: 400/401/403/404/500

```json
{
  "code": 404,
  "msg": "审计日志不存在",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 400 | 参数错误 | 参数验证失败 |
| 401 | 您没有权限执行此操作 | 权限不足 |
| 403 | 访问被拒绝 | 访问被拒绝 |
| 404 | 审计日志不存在 | 指定的日志ID不存在 |
| 500 | 系统错误 | 系统内部错误 |

## 接口示例

### 请求示例

```bash
curl -X GET "http://localhost:8080/setting/log/1" \
  -H "Authorization: Bearer [token]"
```

### 响应示例

**成功**:

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "operationType": "UPDATE",
    "objectType": "CONFIGURATION",
    "objectId": 100,
    "objectName": "API配置",
    "oldValue": "{\"apiVersion\":\"59.0\"}",
    "newValue": "{\"apiVersion\":\"60.0\"}",
    "operationDesc": "更新API版本",
    "operator": "admin",
    "operationTime": "2026-01-14 10:30:00",
    "result": "SUCCESS",
    "orgType": "source"
  }
}
```

**失败**:

```json
{
  "code": 404,
  "msg": "审计日志不存在",
  "data": null
}
```

## 错误处理

1. **权限验证失败**: 检查用户是否具有 setting:log:query 权限
2. **参数验证失败**: 检查日志ID是否有效
3. **数据不存在**: 检查指定的日志ID是否存在
4. **系统错误**: 记录错误日志并返回通用错误信息

## 注意事项

1. 接口需要登录认证，需要携带有效的 token
2. 接口需要 setting:log:query 权限
3. objectName 字段是通过 objectType 和 objectId 动态解析得到的
4. 旧值和新值字段可能包含大量 JSON 数据，前端需要合理展示
5. 如果操作失败，errorMessage 字段会包含详细的错误信息

## 相关接口

- [查询配置审计日志列表](0001-log-list.md) - 查询配置审计日志列表
- [导出配置审计日志列表](0002-export-log-list.md) - 导出配置审计日志列表到 Excel

## 实现细节

1. 接收日志ID路径参数
2. 调用 IDataiConfigAuditLogService 根据ID查询日志
3. 使用 AuditLogObjectNameResolver 解析对象名称
4. 转换为 VO 对象并返回

## 测试信息

### 测试环境

- **环境**: 本地开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 查询存在的日志 | id=1 | 返回日志详细信息 | 返回详细信息 | 通过 |
| 查询不存在的日志 | id=9999 | 返回404错误 | 返回404错误 | 通过 |
| 查询无效ID | id=abc | 返回400错误 | 返回400错误 | 通过 |
| 无权限访问 | 无权限token | 返回401错误 | 返回401错误 | 通过 |
