# 接口文档：同步批次数据

## 接口信息

- **接口名称**: 同步批次数据
- **接口路径**: /integration/batch/{id}/sync
- **请求方法**: POST
- **模块归属**: integration
- **版本号**: v1.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

手动触发指定数据批次的同步操作，执行数据从源系统到目标系统的同步过程。

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
    "success": true,
    "batchId": 123,
    "message": "同步成功",
    "syncTime": "2026-01-09T10:00:00",
    "recordsSynced": 1000
  }
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| success | Boolean | 同步是否成功 | true |
| batchId | Integer | 批次ID | 123 |
| message | String | 同步结果消息 | 同步成功 |
| syncTime | String | 同步执行时间 | 2026-01-09T10:00:00 |
| recordsSynced | Integer | 同步的记录数 | 1000 |

### 失败响应

**HTTP 状态码**: 400 Bad Request

```json
{
  "code": 400,
  "message": "同步失败：连接超时",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 400 | 同步失败：[具体错误信息] | 同步过程中出现的具体错误 |
| 403 | 无权限执行此操作 | 当用户没有integration:batch:sync权限时 |

## 接口示例

### 请求示例

```bash
curl -X POST "http://localhost:8080/integration/batch/123/sync" \
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
    "success": true,
    "batchId": 123,
    "message": "同步成功",
    "syncTime": "2026-01-09T10:00:00",
    "recordsSynced": 1000
  }
}
```

**失败**:

```json
{
  "code": 400,
  "message": "同步失败：连接超时",
  "data": null
}
```

## 错误处理

- **400 Bad Request**: 同步过程中出现的具体错误，如连接超时、认证失败等
- **403 Forbidden**: 用户无权限执行此操作
- **500 Internal Server Error**: 服务器内部错误

## 注意事项

- 同步操作可能耗时较长，建议在后台执行
- 同步过程中系统会占用较多资源，请注意系统负载
- 同步结果会更新到批次的状态和历史记录中
- 对于大型批次，可能会触发系统的批处理机制

## 相关接口

- [获取批次详细信息](http://localhost:8080/integration/batch/{id}) - 获取批次的详细信息
- [获取批次同步统计信息](http://localhost:8080/integration/batch/{id}/statistics) - 获取批次的同步统计信息
- [重试失败的批次](http://localhost:8080/integration/batch/{id}/retry) - 重试失败的批次

## 实现细节

- 接口通过调用`dataiIntegrationBatchService.syncBatchData(id)`方法实现同步逻辑
- 服务层会执行完整的数据同步流程，包括数据提取、转换和加载
- 同步结果会以Map形式返回，包含成功状态、消息等信息
- 控制器会根据返回的success字段判断同步是否成功

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 同步存在批次 | id=123（存在） | 操作成功，返回同步结果 | 操作成功，返回同步结果 | 通过 |
| 同步不存在批次 | id=999（不存在） | 操作失败，返回错误信息 | 操作失败，返回错误信息 | 通过 |
| 同步过程中出现错误 | id=124（存在但源系统不可达） | 操作失败，返回错误信息 | 操作失败，返回错误信息 | 通过 |