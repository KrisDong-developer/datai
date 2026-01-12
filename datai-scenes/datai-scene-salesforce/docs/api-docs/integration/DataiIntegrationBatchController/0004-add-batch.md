# 接口文档：新增数据批次

## 接口信息

- **接口名称**: 新增数据批次
- **接口路径**: /integration/batch
- **请求方法**: POST
- **模块归属**: 数据批次管理
- **版本号**: v1.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

新增数据批次，用于配置和管理数据同步批次信息。

## 请求参数

### 请求体 (JSON)

```json
{
  "api": "Account",
  "label": "账户",
  "syncType": "FULL",
  "batchField": "CreatedDate"
}
```

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
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

**HTTP 状态码**: 400/401/500

```json
{
  "code": 400,
  "message": "参数错误",
  "data": 0
}
```

## 接口示例

### 请求示例

```bash
curl -X POST "http://localhost:8080/integration/batch" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer [token]" \
  -d '{
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
  "code": 400,
  "message": "参数错误",
  "data": 0
}
```

## 错误处理

- **401 未授权**: 用户没有访问权限
- **400 参数错误**: 请求参数不完整或格式错误
- **500 服务器错误**: 服务器内部错误，可能是数据库连接失败等

## 注意事项

- 接口需要 `integration:batch:add` 权限
- `api`、`label`、`syncType` 和 `batchField` 为必填参数
- `syncType` 通常为 "FULL"（全量同步）或 "INCREMENTAL"（增量同步）

## 相关接口

- [查询数据批次列表](http://localhost:8080/integration/batch/list) - 查询数据批次列表
- [修改数据批次](http://localhost:8080/integration/batch) - 修改数据批次

## 实现细节

- 接口通过调用 `dataiIntegrationBatchService.insertDataiIntegrationBatch()` 方法新增批次
- 自动设置创建人、创建时间、更新人、更新时间等字段
- 支持设置同步时间范围

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 完整参数新增 | 包含所有必填参数 | 操作成功，返回批次ID | 操作成功，返回批次ID | 通过 |
| 缺少必填参数 | 缺少api参数 | 操作失败，返回参数错误 | 操作失败，返回参数错误 | 通过 |
| 重复API新增 | 已存在的api | 操作成功，返回新批次ID | 操作成功，返回新批次ID | 通过 |