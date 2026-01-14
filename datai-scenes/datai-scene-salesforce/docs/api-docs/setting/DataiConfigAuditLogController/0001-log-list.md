# 接口文档

## 接口信息

- **接口名称**: 查询配置审计日志列表
- **接口路径**: /setting/log/list
- **请求方法**: GET
- **模块归属**: datai-salesforce-setting
- **版本号**: v1.0
- **创建日期**: 2026-01-14
- **最后更新**: 2026-01-14

## 功能描述

查询配置审计日志列表，支持分页查询和多条件过滤。返回的日志数据包含对象名称（通过对象类型和ID解析），便于用户快速了解配置变更情况。

## 请求参数

### 查询参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
| pageNum | Integer | 否 | 页码 | 1 | 1 |
| pageSize | Integer | 否 | 每页数量 | 10 | 10 |
| deptId | Long | 否 | 部门ID | 1 | - |
| operationType | String | 否 | 操作类型 | UPDATE | - |
| objectType | String | 否 | 对象类型 | CONFIGURATION | - |
| objectId | Long | 否 | 对象ID | 100 | - |
| operator | String | 否 | 操作人 | admin | - |
| result | String | 否 | 操作结果 | SUCCESS | - |
| orgType | String | 否 | ORG类型 | source | - |
| beginTime | String | 否 | 开始时间 | 2026-01-01 00:00:00 | - |
| endTime | String | 否 | 结束时间 | 2026-01-31 23:59:59 | - |

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "total": 100,
  "rows": [
    {
      "id": 1,
      "deptId": 1,
      "operationType": "UPDATE",
      "objectType": "CONFIGURATION",
      "objectId": 100,
      "objectName": "API配置",
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
      "orgType": "source",
      "createBy": "admin",
      "createTime": "2026-01-14 10:30:00",
      "updateBy": null,
      "updateTime": null
    }
  ],
  "code": 200,
  "msg": "查询成功"
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| total | Integer | 总记录数 | 100 |
| rows | Array | 数据列表 | - |
| code | Integer | 响应码 | 200 |
| msg | String | 响应消息 | 查询成功 |

**rows 数组元素字段说明**:

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| id | Long | 日志ID | 1 |
| deptId | Long | 部门ID | 1 |
| operationType | String | 操作类型（CREATE/UPDATE/DELETE） | UPDATE |
| objectType | String | 对象类型（CONFIGURATION/ENVIRONMENT/SNAPSHOT等） | CONFIGURATION |
| objectId | Long | 对象ID | 100 |
| objectName | String | 对象名称（通过解析获得） | API配置 |
| oldValue | String | 旧值（JSON格式） | {"apiVersion":"59.0"} |
| newValue | String | 新值（JSON格式） | {"apiVersion":"60.0"} |
| operationDesc | String | 操作描述 | 更新API版本 |
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
curl -X GET "http://localhost:8080/setting/log/list?pageNum=1&pageSize=10&operationType=UPDATE&orgType=source" \
  -H "Authorization: Bearer [token]"
```

### 响应示例

**成功**:

```json
{
  "total": 100,
  "rows": [
    {
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
  ],
  "code": 200,
  "msg": "查询成功"
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

1. **权限验证失败**: 检查用户是否具有 setting:log:list 权限
2. **参数验证失败**: 检查查询参数是否有效
3. **系统错误**: 记录错误日志并返回通用错误信息

## 注意事项

1. 接口需要登录认证，需要携带有效的 token
2. 接口需要 setting:log:list 权限
3. objectName 字段是通过 objectType 和 objectId 动态解析得到的
4. 支持按时间范围查询，使用 beginTime 和 endTime 参数
5. 旧值和新值字段可能包含大量 JSON 数据，前端需要合理展示

## 相关接口

- [导出配置审计日志列表](0002-export-log-list.md) - 导出配置审计日志列表到 Excel
- [获取配置审计日志详细信息](0003-get-info.md) - 获取单条审计日志的详细信息

## 实现细节

1. 接收查询参数，转换为 DataiConfigAuditLog 对象
2. 调用 IDataiConfigAuditLogService 查询日志列表
3. 使用 AuditLogObjectNameResolver 解析对象名称
4. 转换为 VO 对象并返回分页数据

## 测试信息

### 测试环境

- **环境**: 本地开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 查询所有日志 | 无参数 | 返回所有日志分页数据 | 返回分页数据 | 通过 |
| 按操作类型查询 | operationType=UPDATE | 返回UPDATE类型的日志 | 返回UPDATE日志 | 通过 |
| 按ORG类型查询 | orgType=source | 返回source类型的日志 | 返回source日志 | 通过 |
| 按时间范围查询 | beginTime=2026-01-01, endTime=2026-01-31 | 返回指定时间范围的日志 | 返回时间范围日志 | 通过 |
| 无权限访问 | 无权限token | 返回401错误 | 返回401错误 | 通过 |
