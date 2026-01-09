# 接口文档：获取批次同步统计信息

## 接口信息

- **接口名称**: 获取批次同步统计信息
- **接口路径**: /integration/batch/{id}/statistics
- **请求方法**: GET
- **模块归属**: integration
- **版本号**: v1.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

获取指定数据同步批次的统计信息，包括同步成功、失败、总记录数等数据。

## 请求参数

### 路径参数

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| id | Integer | 是 | 批次ID | 123 |

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "batchId": 123,
    "totalRecords": 1000,
    "successCount": 950,
    "failedCount": 50,
    "successRate": 0.95,
    "syncStartTime": "2026-01-09T10:00:00",
    "syncEndTime": "2026-01-09T10:05:00",
    "duration": 300
  }
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| batchId | Integer | 批次ID | 123 |
| totalRecords | Integer | 总记录数 | 1000 |
| successCount | Integer | 成功记录数 | 950 |
| failedCount | Integer | 失败记录数 | 50 |
| successRate | Double | 成功率 | 0.95 |
| syncStartTime | String | 同步开始时间 | 2026-01-09T10:00:00 |
| syncEndTime | String | 同步结束时间 | 2026-01-09T10:05:00 |
| duration | Integer | 同步时长（秒） | 300 |

### 失败响应

**HTTP 状态码**: 400 Bad Request

```json
{
  "code": 400,
  "message": "批次不存在",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 400 | 批次不存在 | 当批次ID不存在时 |
| 403 | 无权限执行此操作 | 当用户没有integration:batch:statistics权限时 |

## 接口示例

### 请求示例

```bash
curl -X GET "http://localhost:8080/integration/batch/123/statistics" \
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
    "batchId": 123,
    "totalRecords": 1000,
    "successCount": 950,
    "failedCount": 50,
    "successRate": 0.95,
    "syncStartTime": "2026-01-09T10:00:00",
    "syncEndTime": "2026-01-09T10:05:00",
    "duration": 300
  }
}
```

**失败**:

```json
{
  "code": 400,
  "message": "批次不存在",
  "data": null
}
```

## 错误处理

- **400 Bad Request**: 批次不存在
- **403 Forbidden**: 用户无权限执行此操作
- **500 Internal Server Error**: 服务器内部错误

## 注意事项

- 此接口返回的是批次的统计信息，不包含详细的同步记录
- 对于正在执行中的批次，返回的是实时统计数据
- 对于未执行的批次，返回的统计数据可能为空

## 相关接口

- [获取批次详细信息](http://localhost:8080/integration/batch/{id}) - 获取批次的详细信息
- [同步批次数据](http://localhost:8080/integration/batch/{id}/sync) - 同步批次数据

## 实现细节

- 接口通过调用`dataiIntegrationBatchService.getSyncStatistics(id)`方法获取统计信息
- 服务层会从批次历史记录中计算统计数据
- 对于正在执行的批次，会实时计算当前的统计状态

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 获取存在批次的统计信息 | id=123（存在） | 操作成功，返回统计数据 | 操作成功，返回统计数据 | 通过 |
| 获取不存在批次的统计信息 | id=999（不存在） | 操作失败，返回错误信息 | 操作失败，返回错误信息 | 通过 |
| 获取正在执行批次的统计信息 | id=124（执行中） | 操作成功，返回实时统计数据 | 操作成功，返回实时统计数据 | 通过 |