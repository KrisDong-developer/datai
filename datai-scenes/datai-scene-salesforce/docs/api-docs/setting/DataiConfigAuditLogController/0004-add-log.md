# 接口文档

## 接口信息

- **接口名称**: 新增配置审计日志
- **接口路径**: /setting/log
- **请求方法**: POST
- **模块归属**: datai-salesforce-setting
- **版本号**: v1.0
- **创建日期**: 2026-01-14
- **最后更新**: 2026-01-14

## 功能描述

新增配置审计日志记录。通常此接口由系统内部调用，用于记录配置变更操作，包括操作类型、对象信息、新旧值等详细信息。

## 请求参数

### 请求体 (JSON)

```json
{
  "deptId": 1,
  "operationType": "UPDATE",
  "objectType": "CONFIGURATION",
  "objectId": 100,
  "oldValue": "{\"apiVersion\":\"59.0\"}",
  "newValue": "{\"apiVersion\":\"60.0\"}",
  "operationDesc": "更新API版本",
  "operator": "admin",
  "operationTime": "2026-01-14 10:30:00",
  "ipAddress": "192.168.1.100",
  "userAgent": "Mozilla/5.0...",
  "requestId": "req_123456789",
  "result": "SUCCESS",
  "errorMessage": null,
  "orgType": "source"
}
```

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| deptId | Long | 否 | 部门ID | 1 |
| operationType | String | 否 | 操作类型（CREATE/UPDATE/DELETE） | UPDATE |
| objectType | String | 否 | 对象类型（CONFIGURATION/ENVIRONMENT/SNAPSHOT等） | CONFIGURATION |
| objectId | Long | 否 | 对象ID | 100 |
| oldValue | String | 否 | 旧值（JSON格式） | {"apiVersion":"59.0"} |
| newValue | String | 否 | 新值（JSON格式） | {"apiVersion":"60.0"} |
| operationDesc | String | 否 | 操作描述 | 更新API版本 |
| operator | String | 否 | 操作人 | admin |
| operationTime | String | 否 | 操作时间 | 2026-01-14 10:30:00 |
| ipAddress | String | 否 | IP地址 | 192.168.1.100 |
| userAgent | String | 否 | 用户代理 | Mozilla/5.0... |
| requestId | String | 否 | 请求ID | req_123456789 |
| result | String | 否 | 操作结果（SUCCESS/FAILED） | SUCCESS |
| errorMessage | String | 否 | 错误信息 | null |
| orgType | String | 否 | ORG类型（source/target） | source |

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": 1
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| code | Integer | 响应码 | 200 |
| msg | String | 响应消息 | 操作成功 |
| data | Integer | 影响的行数（新增成功的记录数） | 1 |

### 失败响应

**HTTP 状态码**: 401/403/500

```json
{
  "code": 401,
  "msg": "您没有权限执行此操作",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 401 | 您没有权限执行此操作 | 权限不足 |
| 403 | 访问被拒绝 | 访问被拒绝 |
| 500 | 系统错误 | 系统内部错误 |

## 接口示例

### 请求示例

```bash
curl -X POST "http://localhost:8080/setting/log" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer [token]" \
  -d '{
    "deptId": 1,
    "operationType": "UPDATE",
    "objectType": "CONFIGURATION",
    "objectId": 100,
    "oldValue": "{\"apiVersion\":\"59.0\"}",
    "newValue": "{\"apiVersion\":\"60.0\"}",
    "operationDesc": "更新API版本",
    "operator": "admin",
    "operationTime": "2026-01-14 10:30:00",
    "ipAddress": "192.168.1.100",
    "userAgent": "Mozilla/5.0...",
    "requestId": "req_123456789",
    "result": "SUCCESS",
    "orgType": "source"
  }'
```

### 响应示例

**成功**:

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": 1
}
```

**失败**:

```json
{
  "code": 401,
  "msg": "您没有权限执行此操作",
  "data": null
}
```

## 错误处理

1. **权限验证失败**: 检查用户是否具有 setting:log:add 权限
2. **参数验证失败**: 检查请求参数是否有效
3. **系统错误**: 记录错误日志并返回通用错误信息

## 注意事项

1. 接口需要登录认证，需要携带有效的 token
2. 接口需要 setting:log:add 权限
3. 通常此接口由系统内部调用，用于自动记录配置变更
4. oldValue 和 newValue 字段应使用 JSON 格式存储
5. operationType 应为 CREATE、UPDATE 或 DELETE 之一
6. result 应为 SUCCESS 或 FAILED 之一
7. 新增操作会记录到操作日志中

## 相关接口

- [查询配置审计日志列表](0001-log-list.md) - 查询配置审计日志列表
- [获取配置审计日志详细信息](0003-get-info.md) - 获取单条审计日志的详细信息
- [修改配置审计日志](0005-edit-log.md) - 修改配置审计日志

## 实现细节

1. 接收请求参数，转换为 DataiConfigAuditLog 对象
2. 调用 IDataiConfigAuditLogService 插入日志记录
3. 返回操作结果

## 测试信息

### 测试环境

- **环境**: 本地开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 新增成功日志 | 完整参数 | 返回成功，data=1 | 返回成功，data=1 | 通过 |
| 新增失败日志 | result=FAILED | 返回成功，data=1 | 返回成功，data=1 | 通过 |
| 缺少必要参数 | 缺少operationType | 返回参数错误 | 返回参数错误 | 通过 |
| 无权限访问 | 无权限token | 返回401错误 | 返回401错误 | 通过 |
