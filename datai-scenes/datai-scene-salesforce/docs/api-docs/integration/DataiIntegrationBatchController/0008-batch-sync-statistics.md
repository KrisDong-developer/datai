# 接口文档：获取批次同步统计信息

## 接口信息

- **接口名称**: 获取批次同步统计信息
- **接口路径**: /integration/batch/{id}/statistics
- **请求方法**: GET
- **模块归属**: 数据批次管理
- **版本号**: v1.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

根据批次ID获取批次同步统计信息，包括同步次数、成功率、数据量、执行时间等详细统计数据。

## 请求参数

### 路径参数

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| id | Integer | 是 | 批次ID | 1 |

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "success": true,
    "message": "获取统计信息成功",
    "data": {
      "batchId": 1,
      "api": "Account",
      "label": "账户",
      "syncType": "FULL",
      "sfNum": 1000,
      "dbNum": 1000,
      "totalCount": 5,
      "successCount": 4,
      "failedCount": 1,
      "successRate": 80,
      "totalSyncNum": 5000,
      "totalCost": 15000,
      "avgCost": 3000,
      "minCost": 2000,
      "maxCost": 5000,
      "firstSyncTime": "2025-12-24T10:00:00",
      "lastSyncTime": "2025-12-24T10:30:00",
      "syncStatus": true
    }
  }
}
```

### 失败响应

**HTTP 状态码**: 400/401/404/500

```json
{
  "code": 404,
  "message": "批次不存在",
  "data": null
}
```

## 接口示例

### 请求示例

```bash
curl -X GET "http://localhost:8080/integration/batch/1/statistics" \
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
    "message": "获取统计信息成功",
    "data": {
      "batchId": 1,
      "api": "Account",
      "label": "账户",
      "syncType": "FULL",
      "sfNum": 1000,
      "dbNum": 1000,
      "totalCount": 5,
      "successCount": 4,
      "failedCount": 1,
      "successRate": 80,
      "totalSyncNum": 5000,
      "totalCost": 15000,
      "avgCost": 3000,
      "minCost": 2000,
      "maxCost": 5000,
      "firstSyncTime": "2025-12-24T10:00:00",
      "lastSyncTime": "2025-12-24T10:30:00",
      "syncStatus": true
    }
  }
}
```

**失败**:

```json
{
  "code": 404,
  "message": "批次不存在",
  "data": null
}
```

## 错误处理

- **401 未授权**: 用户没有访问权限
- **404 未找到**: 批次不存在
- **500 服务器错误**: 服务器内部错误，可能是数据库连接失败等

## 注意事项

- 接口需要 `integration:batch:statistics` 权限
- 批次ID必须为整数类型
- 对于正在执行的批次，会实时计算当前的统计状态

## 相关接口

- [获取所有批次统计信息](http://localhost:8080/integration/batch/statistics) - 获取所有批次统计信息
- [同步批次数据](http://localhost:8080/integration/batch/{id}/sync) - 同步批次数据

## 实现细节

- 接口通过调用 `dataiIntegrationBatchService.getSyncStatistics(id)` 方法获取统计信息
- 服务层会从批次历史记录中计算统计数据
- 对于正在执行的批次，会实时计算当前的统计状态

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 获取存在批次的统计信息 | id=1（存在） | 操作成功，返回统计数据 | 操作成功，返回统计数据 | 通过 |
| 获取不存在批次的统计信息 | id=999（不存在） | 操作失败，返回错误信息 | 操作失败，返回错误信息 | 通过 |
| 获取正在执行批次的统计信息 | id=2（执行中） | 操作成功，返回实时统计数据 | 操作成功，返回实时统计数据 | 通过 |