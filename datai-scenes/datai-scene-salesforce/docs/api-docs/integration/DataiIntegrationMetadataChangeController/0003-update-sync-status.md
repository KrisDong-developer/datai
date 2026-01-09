# 接口文档：批量更新元数据变更同步状态

## 接口信息

- **接口名称**: 批量更新元数据变更同步状态
- **接口路径**: /integration/change/syncStatus
- **请求方法**: PUT
- **模块归属**: integration
- **版本号**: v1.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

批量更新元数据变更的同步状态，包括同步状态、同步错误信息等。

## 请求参数

### 请求体 (JSON)

```json
{
  "ids": [1, 2, 3],
  "syncStatus": 1,
  "syncErrorMessage": "同步成功"
}
```

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| ids | Array<Long> | 是 | 元数据变更ID数组 | [1, 2, 3] |
| syncStatus | Integer | 是 | 同步状态，0-未同步，1-已同步，2-同步失败 | 1 |
| syncErrorMessage | String | 否 | 同步错误信息 | 同步成功 |

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "message": "操作成功",
  "data": 3
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| code | Integer | 响应状态码 | 200 |
| message | String | 响应消息 | 操作成功 |
| data | Integer | 更新成功的记录数 | 3 |

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
| 400 | 参数错误 | 请求参数格式错误 |
| 403 | 无权限执行此操作 | 当用户没有integration:change:updateSync权限时 |
| 500 | 更新失败 | 服务器内部错误导致更新失败 |

## 接口示例

### 请求示例

```bash
curl -X PUT "http://localhost:8080/integration/change/syncStatus" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer [token]" \
  -d '{
    "ids": [1, 2, 3],
    "syncStatus": 1,
    "syncErrorMessage": "同步成功"
  }'
```

### 响应示例

**成功**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": 3
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

- **400 Bad Request**: 请求参数格式错误
- **403 Forbidden**: 用户无权限执行此操作
- **500 Internal Server Error**: 服务器内部错误导致更新失败

## 注意事项

- 此接口用于批量更新元数据变更的同步状态
- syncStatus参数支持以下值：0-未同步，1-已同步，2-同步失败
- 当syncStatus为2时，建议提供syncErrorMessage说明失败原因
- 批量操作的性能取决于ID数组的大小，建议单次操作不超过100个ID

## 相关接口

- [查询对象元数据变更列表](http://localhost:8080/integration/change/list) - 查询所有元数据变更列表
- [同步元数据变更到本地数据库](http://localhost:8080/integration/change/{id}/sync) - 同步指定的元数据变更
- [批量同步元数据变更到本地数据库](http://localhost:8080/integration/change/syncBatch) - 批量同步元数据变更

## 实现细节

- 接口通过调用`dataiIntegrationMetadataChangeService.batchUpdateSyncStatus()`方法实现批量更新
- 服务层会根据传入的ID数组和同步状态进行批量更新
- 支持同时更新同步错误信息

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 批量更新同步状态为已同步 | {"ids": [1, 2, 3], "syncStatus": 1} | 操作成功，返回3 | 操作成功，返回3 | 通过 |
| 批量更新同步状态为同步失败 | {"ids": [1, 2], "syncStatus": 2, "syncErrorMessage": "同步失败：连接超时"} | 操作成功，返回2 | 操作成功，返回2 | 通过 |
| 无权限更新 | 任意参数 | 操作失败，返回403错误 | 操作失败，返回403错误 | 通过 |