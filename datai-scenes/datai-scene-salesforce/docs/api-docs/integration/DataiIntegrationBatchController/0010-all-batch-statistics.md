# 接口文档：获取所有批次统计信息

## 接口信息

- **接口名称**: 获取所有批次统计信息
- **接口路径**: /integration/batch/statistics
- **请求方法**: GET
- **模块归属**: 数据批次管理
- **版本号**: v1.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

获取所有批次的统计信息，包括总批次数、成功批次数、失败批次数、成功率、数据量、执行时间等详细统计数据。

## 请求参数

### 查询参数

无

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "success": true,
    "message": "获取所有批次统计信息成功",
    "data": {
      "totalCount": 10,
      "successCount": 8,
      "failedCount": 2,
      "successRate": 80,
      "totalSfNum": 10000,
      "totalDbNum": 10000,
      "avgSfNum": 1000,
      "avgDbNum": 1000,
      "totalHistoryCount": 50,
      "totalCost": 150000,
      "avgCost": 3000,
      "syncTypeStats": {
        "typeCount": {
          "FULL": 6,
          "INCREMENTAL": 4
        }
      }
    }
  }
}
```

### 失败响应

**HTTP 状态码**: 400/401/500

```json
{
  "code": 500,
  "message": "获取所有批次统计信息失败",
  "data": null
}
```

## 接口示例

### 请求示例

```bash
curl -X GET "http://localhost:8080/integration/batch/statistics" \
  -H "Authorization: Bearer [token]"
```

### 响应示例

**成功**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "success": true,
    "message": "获取所有批次统计信息成功",
    "data": {
      "totalCount": 10,
      "successCount": 8,
      "failedCount": 2,
      "successRate": 80,
      "totalSfNum": 10000,
      "totalDbNum": 10000,
      "avgSfNum": 1000,
      "avgDbNum": 1000,
      "totalHistoryCount": 50,
      "totalCost": 150000,
      "avgCost": 3000,
      "syncTypeStats": {
        "typeCount": {
          "FULL": 6,
          "INCREMENTAL": 4
        }
      }
    }
  }
}
```

**失败**:

```json
{
  "code": 500,
  "message": "获取所有批次统计信息失败",
  "data": null
}
```

## 错误处理

- **401 未授权**: 用户没有访问权限
- **500 服务器错误**: 服务器内部错误，可能是数据库连接失败等

## 注意事项

- 接口需要 `integration:batch:statistics` 权限
- 对于没有批次数据的情况，会返回错误信息
- 统计信息包括批次基本信息、数据量信息、执行时间信息和按同步类型统计

## 相关接口

- [获取批次同步统计信息](http://localhost:8080/integration/batch/{id}/statistics) - 获取批次同步统计信息
- [查询数据批次列表](http://localhost:8080/integration/batch/list) - 查询数据批次列表

## 实现细节

- 接口通过调用 `dataiIntegrationBatchService.getAllBatchStatistics()` 方法获取所有批次的统计信息
- 服务层会查询所有批次数据和历史记录，计算详细的统计数据
- 支持按同步类型统计批次数量

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 有批次数据的情况 | 无 | 操作成功，返回所有批次统计信息 | 操作成功，返回所有批次统计信息 | 通过 |
| 无批次数据的情况 | 无 | 操作失败，返回错误信息 | 操作失败，返回错误信息 | 通过 |