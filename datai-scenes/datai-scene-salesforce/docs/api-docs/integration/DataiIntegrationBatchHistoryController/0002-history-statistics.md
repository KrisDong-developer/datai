# 接口文档：获取历史统计信息

## 接口信息

- **接口名称**: 获取历史统计信息
- **接口路径**: /integration/batchhistory/statistics
- **请求方法**: GET
- **模块归属**: integration
- **版本号**: v1.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

获取数据批次历史的统计信息，支持根据多个条件进行筛选统计，包括API名称、批次ID、同步类型、同步状态和时间范围等。

## 请求参数

### 查询参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
| api | String | 否 | API名称 | /api/v1/accounts | 无 |
| batchId | Integer | 否 | 批次ID | 123 | 无 |
| syncType | String | 否 | 同步类型 | FULL | 无 |
| syncStatus | Integer | 否 | 同步状态，0-待执行，1-执行中，2-成功，3-失败 | 2 | 无 |
| startTime | String | 否 | 开始时间，格式：yyyy-MM-dd HH:mm:ss | 2026-01-01 00:00:00 | 无 |
| endTime | String | 否 | 结束时间，格式：yyyy-MM-dd HH:mm:ss | 2026-01-07 23:59:59 | 无 |

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
    "failedCount": 50,
    "successRate": 0.95,
    "avgDuration": 120,
    "statusDistribution": {
      "0": 0,
      "1": 0,
      "2": 950,
      "3": 50
    },
    "typeDistribution": {
      "FULL": 800,
      "INCREMENTAL": 200
    }
  }
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| totalCount | Integer | 总记录数 | 1000 |
| successCount | Integer | 成功记录数 | 950 |
| failedCount | Integer | 失败记录数 | 50 |
| successRate | Double | 成功率 | 0.95 |
| avgDuration | Double | 平均执行时长（秒） | 120 |
| statusDistribution | Map<Integer, Integer> | 状态分布 | {"2": 950, "3": 50} |
| typeDistribution | Map<String, Integer> | 类型分布 | {"FULL": 800, "INCREMENTAL": 200} |

### 失败响应

**HTTP 状态码**: 403 Forbidden

```json
{
  "code": 403,
  "message": "无权限执行此操作",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 403 | 无权限执行此操作 | 当用户没有integration:batchhistory:statistics权限时 |
| 500 | 统计失败 | 服务器内部错误导致统计失败 |

## 接口示例

### 请求示例

```bash
curl -X GET "http://localhost:8080/integration/batchhistory/statistics?batchId=123&syncStatus=2&startTime=2026-01-01%2000:00:00&endTime=2026-01-07%2023:59:59" \
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
    "totalCount": 1000,
    "successCount": 950,
    "failedCount": 50,
    "successRate": 0.95,
    "avgDuration": 120,
    "statusDistribution": {
      "2": 950,
      "3": 50
    },
    "typeDistribution": {
      "FULL": 800,
      "INCREMENTAL": 200
    }
  }
}
```

**失败**:

```json
{
  "code": 403,
  "message": "无权限执行此操作",
  "data": null
}
```

## 错误处理

- **403 Forbidden**: 用户无权限执行此操作
- **500 Internal Server Error**: 服务器内部错误导致统计失败

## 注意事项

- 统计操作可能耗时较长，取决于数据量大小
- 当未提供筛选条件时，会统计所有批次历史记录
- 对于大型数据集，建议使用时间范围进行限制
- 统计结果不包含详细的历史记录，仅提供汇总信息

## 相关接口

- [查询数据批次历史列表](http://localhost:8080/integration/batchhistory/list) - 查询数据批次历史列表
- [获取数据批次历史详细信息](http://localhost:8080/integration/batchhistory/{id}) - 获取批次历史的详细信息
- [导出数据批次历史列表](http://localhost:8080/integration/batchhistory/export) - 导出数据批次历史列表

## 实现细节

- 接口通过构建参数Map调用`dataiIntegrationBatchHistoryService.getHistoryStatistics(params)`方法获取统计信息
- 服务层会根据传入的参数进行条件筛选
- 统计结果包括总数、成功数、失败数、成功率、平均执行时长等
- 还会返回状态分布和类型分布的详细信息

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 获取全部历史统计 | 无参数 | 操作成功，返回所有批次历史的统计数据 | 操作成功，返回所有批次历史的统计数据 | 通过 |
| 获取指定批次统计 | batchId=123 | 操作成功，返回指定批次的历史统计数据 | 操作成功，返回指定批次的历史统计数据 | 通过 |
| 获取指定状态统计 | syncStatus=2 | 操作成功，返回成功状态的历史统计数据 | 操作成功，返回成功状态的历史统计数据 | 通过 |
| 获取时间范围统计 | startTime=2026-01-01%2000:00:00&endTime=2026-01-07%2023:59:59 | 操作成功，返回指定时间范围的历史统计数据 | 操作成功，返回指定时间范围的历史统计数据 | 通过 |
| 无权限获取统计 | 任意参数 | 操作失败，返回403错误 | 操作失败，返回403错误 | 通过 |