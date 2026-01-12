# 接口文档：修改数据批次

## 接口信息

- **接口名称**: 修改数据批次
- **接口路径**: /integration/batch
- **请求方法**: PUT
- **模块归属**: 数据批次管理
- **版本号**: v1.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

修改数据批次信息，用于更新批次配置和状态。

## 请求参数

### 请求体 (JSON)

```json
{
  "id": 1,
  "api": "Account",
  "label": "账户",
  "syncType": "FULL",
  "batchField": "CreatedDate"
}
```

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| id | Integer | 是 | 批次ID | 1 |
| api | String | 是 | 对象API名称 | Account |
| label | String | 是 | 对象标签 | 账户 |
| syncType | String | 是 | 同步类型 | FULL |
| batchField | String | 是 | 批次字段 | CreatedDate |
| syncStartDate | Date | 否 | 同步开始日期 | 2025-12-01 |
| syncEndDate | Date | 否 | 同步结束日期 | 2025-12-31 |

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "message": "操作成功",
  "data": 1
}
```

### 失败响应

**HTTP 状态码**: 400/401/404/500

```json
{
  "code": 404,
  "message": "批次不存在",
  "data": 0
}
```

## 接口示例

### 请求示例

```bash
curl -X PUT "http://localhost:8080/integration/batch" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer [token]" \
  -d '{
    "id": 1,
    "api": "Account",
    "label": "账户",
    "syncType": "FULL",
    "batchField": "CreatedDate"
  }'
```

### 响应示例

**成功**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": 1
}
```

**失败**:

```json
{
  "code": 404,
  "message": "批次不存在",
  "data": 0
}
```

## 错误处理

- **401 未授权**: 用户没有访问权限
- **404 未找到**: 批次不存在
- **400 参数错误**: 请求参数不完整或格式错误
- **500 服务器错误**: 服务器内部错误，可能是数据库连接失败等

## 注意事项

- 接口需要 `integration:batch:edit` 权限
- `id`、`api`、`label`、`syncType` 和 `batchField` 为必填参数
- 只能修改存在的批次

## 相关接口

- [查询数据批次列表](http://localhost:8080/integration/batch/list) - 查询数据批次列表
- [获取数据批次详细信息](http://localhost:8080/integration/batch/{id}) - 获取单个数据批次的详细信息

## 实现细节

- 接口通过调用 `dataiIntegrationBatchService.updateDataiIntegrationBatch()` 方法更新批次信息
- 自动更新更新人和更新时间字段

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 修改存在批次 | id=1（存在） | 操作成功，返回1 | 操作成功，返回1 | 通过 |
| 修改不存在批次 | id=999（不存在） | 操作失败，返回0 | 操作失败，返回0 | 通过 |
| 缺少必填参数 | 缺少id参数 | 操作失败，返回0 | 操作失败，返回0 | 通过 |