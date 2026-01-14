# 接口文档

## 接口信息

- **接口名称**: 导出配置审计日志列表
- **接口路径**: /setting/log/export
- **请求方法**: POST
- **模块归属**: datai-salesforce-setting
- **版本号**: v1.0
- **创建日期**: 2026-01-14
- **最后更新**: 2026-01-14

## 功能描述

导出配置审计日志列表到 Excel 文件，支持多条件过滤。导出的数据包含对象名称（通过对象类型和ID解析），便于用户离线查看和分析配置变更情况。

## 请求参数

### 请求体 (JSON)

```json
{
  "deptId": 1,
  "operationType": "UPDATE",
  "objectType": "CONFIGURATION",
  "objectId": 100,
  "operator": "admin",
  "result": "SUCCESS",
  "orgType": "source",
  "beginTime": "2026-01-01 00:00:00",
  "endTime": "2026-01-31 23:59:59"
}
```

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| deptId | Long | 否 | 部门ID | 1 |
| operationType | String | 否 | 操作类型 | UPDATE |
| objectType | String | 否 | 对象类型 | CONFIGURATION |
| objectId | Long | 否 | 对象ID | 100 |
| operator | String | 否 | 操作人 | admin |
| result | String | 否 | 操作结果 | SUCCESS |
| orgType | String | 否 | ORG类型 | source |
| beginTime | String | 否 | 开始时间 | 2026-01-01 00:00:00 |
| endTime | String | 否 | 结束时间 | 2026-01-31 23:59:59 |

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

**Content-Type**: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet

**Content-Disposition**: attachment; filename=配置审计日志数据.xlsx

**响应体**: Excel 文件二进制数据

Excel 文件包含以下列：

| 列名 | 描述 |
|------|------|
| 日志ID | 日志的唯一标识 |
| 部门ID | 所属部门ID |
| 操作类型 | 操作类型（CREATE/UPDATE/DELETE） |
| 对象类型 | 对象类型（CONFIGURATION/ENVIRONMENT/SNAPSHOT等） |
| 对象ID | 对象ID |
| 对象名称 | 对象名称（通过解析获得） |
| 旧值 | 旧值（JSON格式） |
| 新值 | 新值（JSON格式） |
| 操作描述 | 操作描述 |
| 操作人 | 操作人用户名 |
| 操作时间 | 操作时间 |
| IP地址 | 操作IP地址 |
| 用户代理 | 用户代理信息 |
| 请求ID | 请求ID |
| 操作结果 | 操作结果（SUCCESS/FAILED） |
| 错误信息 | 错误信息（如果有） |
| ORG类型 | ORG类型（source/target） |
| 创建人 | 创建人用户名 |
| 创建时间 | 创建时间 |
| 更新人 | 更新人用户名 |
| 更新时间 | 更新时间 |

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
curl -X POST "http://localhost:8080/setting/log/export" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer [token]" \
  -d '{
    "operationType": "UPDATE",
    "orgType": "source",
    "beginTime": "2026-01-01 00:00:00",
    "endTime": "2026-01-31 23:59:59"
  }' \
  --output audit-log.xlsx
```

### 响应示例

**成功**: 下载 Excel 文件 audit-log.xlsx

**失败**:

```json
{
  "code": 401,
  "msg": "您没有权限执行此操作",
  "data": null
}
```

## 错误处理

1. **权限验证失败**: 检查用户是否具有 setting:log:export 权限
2. **参数验证失败**: 检查请求参数是否有效
3. **系统错误**: 记录错误日志并返回通用错误信息

## 注意事项

1. 接口需要登录认证，需要携带有效的 token
2. 接口需要 setting:log:export 权限
3. objectName 字段是通过 objectType 和 objectId 动态解析得到的
4. 支持按时间范围导出，使用 beginTime 和 endTime 参数
5. 旧值和新值字段可能包含大量 JSON 数据，Excel 中会完整展示
6. 导出的文件名为"配置审计日志数据.xlsx"
7. 导出操作会记录到操作日志中

## 相关接口

- [查询配置审计日志列表](0001-log-list.md) - 查询配置审计日志列表
- [获取配置审计日志详细信息](0003-get-info.md) - 获取单条审计日志的详细信息

## 实现细节

1. 接收请求参数，转换为 DataiConfigAuditLog 对象
2. 调用 IDataiConfigAuditLogService 查询日志列表
3. 使用 AuditLogObjectNameResolver 解析对象名称
4. 转换为 VO 对象
5. 使用 ExcelUtil 导出为 Excel 文件

## 测试信息

### 测试环境

- **环境**: 本地开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 导出所有日志 | 无参数 | 导出所有日志到Excel | 成功导出 | 通过 |
| 按操作类型导出 | operationType=UPDATE | 导出UPDATE类型的日志 | 成功导出 | 通过 |
| 按ORG类型导出 | orgType=source | 导出source类型的日志 | 成功导出 | 通过 |
| 按时间范围导出 | beginTime=2026-01-01, endTime=2026-01-31 | 导出指定时间范围的日志 | 成功导出 | 通过 |
| 无权限访问 | 无权限token | 返回401错误 | 返回401错误 | 通过 |
