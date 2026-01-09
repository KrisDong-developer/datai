# 接口文档：获取趋势变更统计信息

## 接口信息

- **接口名称**: 获取趋势变更统计信息
- **接口路径**: /integration/change/statistics/trend
- **请求方法**: GET
- **模块归属**: integration
- **版本号**: v1.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

获取元数据变更的趋势统计信息，支持按天、周、月、季度等时间维度查看变更趋势，可根据变更类型、操作类型、对象API名称、同步状态、是否自定义和时间范围等条件进行筛选。

## 请求参数

### 查询参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
| timeUnit | String | 是 | 时间维度，支持：day, week, month, quarter | day | 无 |
| changeType | String | 否 | 变更类型 | OBJECT | 无 |
| operationType | String | 否 | 操作类型 | CREATE | 无 |
| objectApi | String | 否 | 对象API名称 | Account | 无 |
| syncStatus | Boolean | 否 | 同步状态 | false | 无 |
| isCustom | Boolean | 否 | 是否自定义 | true | 无 |
| startTime | String | 否 | 开始时间，格式：yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd | 2026-01-01 00:00:00 | 无 |
| endTime | String | 否 | 结束时间，格式：yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd | 2026-01-07 23:59:59 | 无 |

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalCount": 100,
    "timeUnit": "day",
    "trends": [
      {
        "time": "2026-01-01",
        "count": 15
      },
      {
        "time": "2026-01-02",
        "count": 20
      },
      {
        "time": "2026-01-03",
        "count": 10
      },
      {
        "time": "2026-01-04",
        "count": 25
      },
      {
        "time": "2026-01-05",
        "count": 30
      }
    ]
  }
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| totalCount | Integer | 总变更数 | 100 |
| timeUnit | String | 时间维度 | day |
| trends | Array | 趋势数据 | 按时间维度统计的变更数据 |
| trends[].time | String | 时间点 | 2026-01-01 |
| trends[].count | Integer | 该时间点的变更数 | 15 |

### 失败响应

**HTTP 状态码**: 400 Bad Request

```json
{
  "code": 400,
  "message": "时间维度不正确，支持的维度：day, week, month, quarter",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 400 | 时间维度不正确 | 时间维度参数错误 |
| 400 | 开始时间格式不正确 | 开始时间格式错误 |
| 400 | 结束时间格式不正确 | 结束时间格式错误 |
| 403 | 无权限执行此操作 | 当用户没有integration:change:statistics权限时 |
| 500 | 统计失败 | 服务器内部错误导致统计失败 |

## 接口示例

### 请求示例

```bash
curl -X GET "http://localhost:8080/integration/change/statistics/trend?timeUnit=day&startTime=2026-01-01&endTime=2026-01-05" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer [token]"
```

### 响应示例

**成功**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalCount": 100,
    "timeUnit": "day",
    "trends": [
      {
        "time": "2026-01-01",
        "count": 15
      },
      {
        "time": "2026-01-02",
        "count": 20
      },
      {
        "time": "2026-01-03",
        "count": 10
      },
      {
        "time": "2026-01-04",
        "count": 25
      },
      {
        "time": "2026-01-05",
        "count": 30
      }
    ]
  }
}
```

**失败**:

```json
{
  "code": 400,
  "message": "时间维度不正确，支持的维度：day, week, month, quarter",
  "data": null
}
```

## 错误处理

- **400 Bad Request**: 时间维度错误、时间格式错误或其他参数错误
- **403 Forbidden**: 用户无权限执行此操作
- **500 Internal Server Error**: 服务器内部错误导致统计失败

## 注意事项

- 统计操作可能耗时较长，取决于数据量大小
- timeUnit参数为必填，支持的维度：day, week, month, quarter
- 支持多种筛选条件与时间维度组合使用
- 时间参数支持两种格式：yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd
- 当未提供时间范围时，会根据时间维度自动选择合适的时间范围

## 相关接口

- [获取变更统计信息](http://localhost:8080/integration/change/statistics) - 获取变更统计信息
- [获取分组变更统计信息](http://localhost:8080/integration/change/statistics/group) - 按指定维度分组统计变更信息

## 实现细节

- 接口通过构建参数Map调用`dataiIntegrationMetadataChangeService.getChangeStatisticsByTrend()`方法获取统计信息
- 服务层会根据传入的时间维度和筛选条件进行趋势统计
- 支持时间维度验证和时间格式验证
- 统计结果包括总变更数和按时间维度统计的趋势数据

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 按天维度查看趋势 | timeUnit=day&startTime=2026-01-01&endTime=2026-01-05 | 操作成功，返回按天统计的趋势数据 | 操作成功，返回按天统计的趋势数据 | 通过 |
| 按周维度查看趋势 | timeUnit=week&startTime=2025-12-28&endTime=2026-01-10 | 操作成功，返回按周统计的趋势数据 | 操作成功，返回按周统计的趋势数据 | 通过 |
| 按月维度查看趋势 | timeUnit=month&startTime=2025-12-01&endTime=2026-02-28 | 操作成功，返回按月统计的趋势数据 | 操作成功，返回按月统计的趋势数据 | 通过 |
| 无效时间维度 | timeUnit=invalid | 操作失败，返回时间维度错误信息 | 操作失败，返回时间维度错误信息 | 通过 |
| 无权限获取统计 | 任意参数 | 操作失败，返回403错误 | 操作失败，返回403错误 | 通过 |