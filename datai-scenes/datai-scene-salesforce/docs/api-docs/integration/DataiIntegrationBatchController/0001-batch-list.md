# 接口文档：查询数据批次列表

## 接口信息

- **接口名称**: 查询数据批次列表
- **接口路径**: /integration/batch/list
- **请求方法**: GET
- **模块归属**: 数据批次管理
- **版本号**: v1.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

查询数据批次列表，支持分页和条件筛选，返回符合条件的数据批次信息。

## 请求参数

### 查询参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
| api | String | 否 | 对象API名称 | Account | - |
| label | String | 否 | 对象标签 | 账户 | - |
| syncType | String | 否 | 同步类型 | FULL | - |
| syncStatus | Boolean | 否 | 同步状态 | true | - |
| createBy | String | 否 | 创建人 | admin | - |
| createTime | Date | 否 | 创建时间 | 2025-12-24 | - |
| updateBy | String | 否 | 更新人 | admin | - |
| updateTime | Date | 否 | 更新时间 | 2025-12-24 | - |

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 100,
    "rows": [
      {
        "id": 1,
        "api": "Account",
        "label": "账户",
        "syncType": "FULL",
        "syncStatus": true,
        "sfNum": 1000,
        "dbNum": 1000,
        "firstSyncTime": "2025-12-24T10:00:00",
        "lastSyncTime": "2025-12-24T10:30:00",
        "createBy": "admin",
        "createTime": "2025-12-24T09:00:00",
        "updateBy": "admin",
        "updateTime": "2025-12-24T10:30:00"
      }
    ]
  }
}
```

### 失败响应

**HTTP 状态码**: 400/401/500

```json
{
  "code": 401,
  "message": "未授权",
  "data": null
}
```

## 接口示例

### 请求示例

```bash
curl -X GET "http://localhost:8080/integration/batch/list?api=Account&syncStatus=true" \
  -H "Authorization: Bearer [token]"
```

### 响应示例

**成功**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "total": 5,
    "rows": [
      {
        "id": 1,
        "api": "Account",
        "label": "账户",
        "syncType": "FULL",
        "syncStatus": true,
        "sfNum": 1000,
        "dbNum": 1000,
        "firstSyncTime": "2025-12-24T10:00:00",
        "lastSyncTime": "2025-12-24T10:30:00",
        "createBy": "admin",
        "createTime": "2025-12-24T09:00:00",
        "updateBy": "admin",
        "updateTime": "2025-12-24T10:30:00"
      }
    ]
  }
}
```

**失败**:

```json
{
  "code": 401,
  "message": "未授权",
  "data": null
}
```

## 错误处理

- **401 未授权**: 用户没有访问权限
- **500 服务器错误**: 服务器内部错误，可能是数据库连接失败等

## 注意事项

- 接口需要 `integration:batch:list` 权限
- 支持分页查询，默认使用系统分页参数

## 相关接口

- [获取数据批次详细信息](http://localhost:8080/integration/batch/{id}) - 获取单个数据批次的详细信息
- [新增数据批次](http://localhost:8080/integration/batch) - 新增数据批次

## 实现细节

- 接口通过调用 `dataiIntegrationBatchService.selectDataiIntegrationBatchList()` 方法获取数据
- 支持多条件组合查询
- 返回数据经过分页处理

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 无参数查询 | 无 | 操作成功，返回所有批次列表 | 操作成功，返回所有批次列表 | 通过 |
| 按API名称查询 | api=Account | 操作成功，返回Account对象的批次列表 | 操作成功，返回Account对象的批次列表 | 通过 |
| 按同步状态查询 | syncStatus=true | 操作成功，返回同步成功的批次列表 | 操作成功，返回同步成功的批次列表 | 通过 |