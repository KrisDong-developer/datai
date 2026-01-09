# 接口文档：查询未同步的元数据变更列表

## 接口信息

- **接口名称**: 查询未同步的元数据变更列表
- **接口路径**: /integration/change/unsynced
- **请求方法**: GET
- **模块归属**: integration
- **版本号**: v1.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

查询未同步的元数据变更列表，支持分页和筛选条件。

## 请求参数

### 查询参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
| changeType | String | 否 | 变更类型 | OBJECT | 无 |
| operationType | String | 否 | 操作类型 | CREATE | 无 |
| objectApi | String | 否 | 对象API名称 | Account | 无 |
| isCustom | Boolean | 否 | 是否自定义 | true | 无 |
| startTime | String | 否 | 开始时间，格式：yyyy-MM-dd HH:mm:ss | 2026-01-01 00:00:00 | 无 |
| endTime | String | 否 | 结束时间，格式：yyyy-MM-dd HH:mm:ss | 2026-01-07 23:59:59 | 无 |
| pageNum | Integer | 否 | 页码 | 1 | 1 |
| pageSize | Integer | 否 | 每页条数 | 10 | 10 |

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 100,
    "rows": [
      {
        "id": 1,
        "changeType": "OBJECT",
        "operationType": "CREATE",
        "objectApi": "TestObject__c",
        "objectLabel": "测试对象",
        "isCustom": true,
        "syncStatus": false,
        "syncErrorMessage": null,
        "createdBy": "system",
        "createdTime": "2026-01-09T10:00:00"
      }
    ],
    "pageNum": 1,
    "pageSize": 10,
    "size": 10,
    "startRow": 1,
    "endRow": 10,
    "pages": 10,
    "prePage": 0,
    "nextPage": 2,
    "isFirstPage": true,
    "isLastPage": false,
    "hasPreviousPage": false,
    "hasNextPage": true,
    "navigatePages": 8,
    "navigatepageNums": [1, 2, 3, 4, 5, 6, 7, 8],
    "navigateFirstPage": 1,
    "navigateLastPage": 8
  }
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| total | Integer | 总记录数 | 100 |
| rows | Array | 数据列表 | 包含未同步的元数据变更记录 |
| pageNum | Integer | 当前页码 | 1 |
| pageSize | Integer | 每页条数 | 10 |
| size | Integer | 当前页实际条数 | 10 |

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
| 403 | 无权限执行此操作 | 当用户没有integration:change:unsynced权限时 |
| 500 | 查询失败 | 服务器内部错误导致查询失败 |

## 接口示例

### 请求示例

```bash
curl -X GET "http://localhost:8080/integration/change/unsynced?changeType=OBJECT&pageNum=1&pageSize=10" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer [token]"
```

### 响应示例

**成功**:

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "total": 5,
    "rows": [
      {
        "id": 1,
        "changeType": "OBJECT",
        "operationType": "CREATE",
        "objectApi": "TestObject__c",
        "objectLabel": "测试对象",
        "isCustom": true,
        "syncStatus": false,
        "syncErrorMessage": null,
        "createdBy": "system",
        "createdTime": "2026-01-09T10:00:00"
      }
    ],
    "pageNum": 1,
    "pageSize": 10,
    "size": 1,
    "startRow": 1,
    "endRow": 1,
    "pages": 1,
    "prePage": 0,
    "nextPage": 0,
    "isFirstPage": true,
    "isLastPage": true,
    "hasPreviousPage": false,
    "hasNextPage": false,
    "navigatePages": 8,
    "navigatepageNums": [1],
    "navigateFirstPage": 1,
    "navigateLastPage": 1
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
- **500 Internal Server Error**: 服务器内部错误导致查询失败

## 注意事项

- 此接口返回的是未同步的元数据变更列表
- 支持分页查询，默认每页10条记录
- 可以通过多个筛选条件组合查询特定的未同步变更

## 相关接口

- [查询对象元数据变更列表](http://localhost:8080/integration/change/list) - 查询所有元数据变更列表
- [同步元数据变更到本地数据库](http://localhost:8080/integration/change/{id}/sync) - 同步指定的元数据变更
- [批量同步元数据变更到本地数据库](http://localhost:8080/integration/change/syncBatch) - 批量同步元数据变更

## 实现细节

- 接口通过调用`dataiIntegrationMetadataChangeService.selectUnsyncedMetadataChangeList()`方法获取数据
- 服务层会筛选出同步状态为未同步的元数据变更记录
- 支持分页查询和多条件筛选

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 查询未同步的元数据变更 | 无参数 | 操作成功，返回未同步的变更列表 | 操作成功，返回未同步的变更列表 | 通过 |
| 查询指定类型的未同步变更 | changeType=OBJECT | 操作成功，返回对象类型的未同步变更列表 | 操作成功，返回对象类型的未同步变更列表 | 通过 |
| 无权限查询 | 任意参数 | 操作失败，返回403错误 | 操作失败，返回403错误 | 通过 |