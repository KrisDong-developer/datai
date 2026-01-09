# 接口文档：同步元数据变更到本地数据库

## 接口信息

- **接口名称**: 同步元数据变更到本地数据库
- **接口路径**: /integration/change/{id}/sync
- **请求方法**: POST
- **模块归属**: integration
- **版本号**: v1.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

同步元数据变更到本地数据库，根据元数据变更ID将指定的元数据变更同步到本地数据库。

该方法会：
1. 根据ID查询元数据变更记录
2. 根据变更类型（OBJECT或FIELD）执行相应的同步操作
3. 对于对象变更：执行对象的创建、修改或删除操作
4. 对于字段变更：执行字段的创建、修改或删除操作
5. 更新元数据变更记录的同步状态

## 请求参数

### 路径参数

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| id | Long | 是 | 元数据变更ID | 1 |

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "success": true,
    "message": "同步成功",
    "changeId": 1,
    "changeType": "OBJECT",
    "operationType": "CREATE",
    "objectApi": "TestObject__c"
  }
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| success | Boolean | 同步是否成功 | true |
| message | String | 同步结果消息 | 同步成功 |
| changeId | Long | 元数据变更ID | 1 |
| changeType | String | 变更类型 | OBJECT |
| operationType | String | 操作类型 | CREATE |
| objectApi | String | 对象API名称 | TestObject__c |

### 失败响应

**HTTP 状态码**: 400 Bad Request

```json
{
  "code": 400,
  "message": "同步失败: 元数据变更记录不存在",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 400 | 同步失败: [具体错误信息] | 同步过程中出现的具体错误 |
| 403 | 无权限执行此操作 | 当用户没有integration:change:sync权限时 |
| 500 | 同步失败 | 服务器内部错误导致同步失败 |

## 接口示例

### 请求示例

```bash
curl -X POST "http://localhost:8080/integration/change/1/sync" \
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
    "message": "同步成功",
    "changeId": 1,
    "changeType": "OBJECT",
    "operationType": "CREATE",
    "objectApi": "TestObject__c"
  }
}
```

**失败**:

```json
{
  "code": 400,
  "message": "同步失败: 元数据变更记录不存在",
  "data": null
}
```

## 错误处理

- **400 Bad Request**: 同步过程中出现的具体错误，如元数据变更记录不存在、同步操作失败等
- **403 Forbidden**: 用户无权限执行此操作
- **500 Internal Server Error**: 服务器内部错误导致同步失败

## 注意事项

- 同步操作可能耗时较长，取决于变更的复杂度
- 同步过程中会执行数据库操作，可能会锁定相关表
- 对于对象变更，会执行对象的创建、修改或删除操作
- 对于字段变更，会执行字段的创建、修改或删除操作
- 同步完成后，元数据变更记录的同步状态会被更新

## 相关接口

- [查询对象元数据变更列表](http://localhost:8080/integration/change/list) - 查询所有元数据变更列表
- [查询未同步的元数据变更列表](http://localhost:8080/integration/change/unsynced) - 查询未同步的元数据变更列表
- [批量同步元数据变更到本地数据库](http://localhost:8080/integration/change/syncBatch) - 批量同步元数据变更

## 实现细节

- 接口通过调用`dataiIntegrationMetadataChangeService.syncToLocalDatabase()`方法实现同步
- 服务层会根据变更类型执行相应的同步操作
- 支持对象变更和字段变更的同步
- 同步过程中会处理异常并返回详细的错误信息
- 同步完成后会更新元数据变更记录的同步状态

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 同步对象创建变更 | id=1（对象创建变更） | 操作成功，返回同步成功信息 | 操作成功，返回同步成功信息 | 通过 |
| 同步字段修改变更 | id=2（字段修改变更） | 操作成功，返回同步成功信息 | 操作成功，返回同步成功信息 | 通过 |
| 同步不存在的变更 | id=999（不存在） | 操作失败，返回变更记录不存在错误 | 操作失败，返回变更记录不存在错误 | 通过 |
| 无权限同步 | 任意参数 | 操作失败，返回403错误 | 操作失败，返回403错误 | 通过 |