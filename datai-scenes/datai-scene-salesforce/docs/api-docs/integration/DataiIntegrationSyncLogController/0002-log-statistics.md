# 接口文档

## 接口信息

- **接口名称**: 获取日志统计信息
- **接口路径**: /integration/synclog/statistics
- **请求方法**: GET
- **模块归属**: 数据同步日志
- **版本号**: v1.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

获取数据同步日志的统计信息，支持根据批处理ID、对象API名称、操作类型、操作状态等条件进行筛选统计。

## 请求参数

### 路径参数

无

### 查询参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
| batchId | Long | 否 | 批处理ID | 1 | 无 |
| objectApi | String | 否 | 对象API名称 | Account | 无 |
| operationType | String | 否 | 操作类型 | insert | 无 |
| operationStatus | String | 否 | 操作状态 | success | 无 |
| deptId | Long | 否 | 部门ID | 100 | 无 |
| beginTime | String | 否 | 开始时间 | 2026-01-01 | 无 |
| endTime | String | 否 | 结束时间 | 2026-01-31 | 无 |

### 请求体

无

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalCount": 1000,
    "successCount": 950,
    "failureCount": 50,
    "successRate": "95%",
    "operationTypeStats": {
      "insert": 500,
      "update": 300,
      "delete": 200
    },
    "objectApiStats": {
      "Account": 400,
      "Contact": 300,
      "Opportunity": 300
    },
    "timeRangeStats": {
      "2026-01-01": 100,
      "2026-01-02": 150,
      "2026-01-03": 200
    }
  }
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| totalCount | Long | 总操作次数 | 1000 |
| successCount | Long | 成功操作次数 | 950 |
| failureCount | Long | 失败操作次数 | 50 |
| successRate | String | 成功率 | 95% |
| operationTypeStats | Object | 操作类型统计 | {"insert": 500, "update": 300, "delete": 200} |
| objectApiStats | Object | 对象API统计 | {"Account": 400, "Contact": 300, "Opportunity": 300} |
| timeRangeStats | Object | 时间范围统计 | {"2026-01-01": 100, "2026-01-02": 150, "2026-01-03": 200} |

### 失败响应

**HTTP 状态码**: 500 Internal Server Error

```json
{
  "code": 500,
  "message": "获取统计信息失败: 系统异常",
  "data": null
}
```

## 接口示例

### 请求示例

```bash
curl -X GET "http://localhost:8080/integration/synclog/statistics?objectApi=Account&beginTime=2026-01-01&endTime=2026-01-31" \
  -H "Authorization: Bearer [token]"
```

### 响应示例

**成功**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalCount": 500,
    "successCount": 480,
    "failureCount": 20,
    "successRate": "96%",
    "operationTypeStats": {
      "insert": 200,
      "update": 250,
      "delete": 50
    },
    "objectApiStats": {
      "Account": 500
    },
    "timeRangeStats": {
      "2026-01-01": 50,
      "2026-01-15": 200,
      "2026-01-31": 250
    }
  }
}
```

**失败**:

```json
{
  "code": 500,
  "message": "获取统计信息失败: 系统异常",
  "data": null
}
```

## 错误处理

- 统计过程中发生异常时，返回500错误
- 参数错误时，返回400错误
- 权限不足时，返回403错误

## 注意事项

- 该接口需要数据同步日志的查看权限
- 统计信息可能会有一定的延迟，取决于数据量的大小
- 当查询时间范围较大时，可能会耗时较长

## 相关接口

- [查询数据同步日志列表](http://localhost:8080/integration/synclog/list) - 查询数据同步日志列表
- [导出数据同步日志列表](http://localhost:8080/integration/synclog/export) - 导出数据同步日志列表

## 实现细节

- 构建查询参数，支持多条件筛选
- 调用service层的getLogStatistics方法获取统计信息
- 处理统计过程中的异常情况
- 返回统计结果

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 获取全部统计信息 | 无 | 返回统计信息 | 返回统计信息 | 通过 |
| 按对象筛选统计 | objectApi=Account | 返回Account对象统计 | 返回Account对象统计 | 通过 |
| 按时间范围筛选 | beginTime=2026-01-01&endTime=2026-01-31 | 返回时间范围内统计 | 返回时间范围内统计 | 通过 |
| 按操作状态筛选 | operationStatus=success | 返回成功操作统计 | 返回成功操作统计 | 通过 |