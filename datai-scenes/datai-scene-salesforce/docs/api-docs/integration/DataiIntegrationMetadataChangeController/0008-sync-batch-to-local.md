# 接口文档：批量同步元数据变更到本地数据库

## 接口信息

- **接口名称**: 批量同步元数据变更到本地数据库
- **接口路径**: /integration/change/syncBatch/{ids}
- **请求方法**: POST
- **模块归属**: integration
- **版本号**: v1.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

批量同步元数据变更到本地数据库，根据元数据变更ID数组将指定的元数据变更批量同步到本地数据库。

该方法会：
1. 使用线程池并发执行多个元数据变更的同步操作，提高同步效率
2. 根据ID数组查询元数据变更记录
3. 根据变更类型（OBJECT或FIELD）执行相应的同步操作
4. 对于对象变更：执行对象的创建、修改或删除操作
5. 对于字段变更：执行字段的创建、修改或删除操作
6. 更新元数据变更记录的同步状态
7. 提供实时的同步进度反馈（每10条记录输出一次进度）
8. 汇总同步结果，包含成功和失败的详细信息

优化特性：
- 并发处理：使用SalesforceExecutor线程池并发执行同步任务，大幅提升处理速度
- 进度反馈：实时输出同步进度，方便监控大批量同步的执行状态
- 异常隔离：单个同步任务失败不会影响其他任务的执行
- 结果汇总：提供详细的同步结果统计，包括总数、成功数、失败数和详细信息
- 线程安全：使用AtomicInteger和synchronizedList保证并发环境下的数据一致性

## 请求参数

### 路径参数

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| ids | Long[] | 是 | 元数据变更ID数组 | 1,2,3 |

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "success": true,
    "message": "批量同步完成，成功3条，失败0条",
    "totalCount": 3,
    "successCount": 3,
    "failCount": 0,
    "details": [
      {
        "id": 1,
        "success": true,
        "message": "同步成功"
      },
      {
        "id": 2,
        "success": true,
        "message": "同步成功"
      },
      {
        "id": 3,
        "success": true,
        "message": "同步成功"
      }
    ]
  }
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| success | Boolean | 是否全部成功 | true |
| message | String | 同步结果摘要信息 | 批量同步完成，成功3条，失败0条 |
| totalCount | Integer | 总记录数 | 3 |
| successCount | Integer | 成功数量 | 3 |
| failCount | Integer | 失败数量 | 0 |
| details | Array | 每条记录的同步详情 | 包含每条记录的同步结果 |
| details[].id | Long | 元数据变更ID | 1 |
| details[].success | Boolean | 该记录同步是否成功 | true |
| details[].message | String | 该记录的同步结果消息 | 同步成功 |

### 失败响应

**HTTP 状态码**: 400 Bad Request

```json
{
  "code": 400,
  "message": "批量同步失败: ID数组不能为空",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 400 | 批量同步失败: [具体错误信息] | 批量同步过程中出现的具体错误 |
| 403 | 无权限执行此操作 | 当用户没有integration:change:syncBatch权限时 |
| 500 | 批量同步失败 | 服务器内部错误导致批量同步失败 |

## 接口示例

### 请求示例

```bash
curl -X POST "http://localhost:8080/integration/change/syncBatch/1,2,3" \
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
    "message": "批量同步完成，成功3条，失败0条",
    "totalCount": 3,
    "successCount": 3,
    "failCount": 0,
    "details": [
      {
        "id": 1,
        "success": true,
        "message": "同步成功"
      },
      {
        "id": 2,
        "success": true,
        "message": "同步成功"
      },
      {
        "id": 3,
        "success": true,
        "message": "同步成功"
      }
    ]
  }
}
```

**部分失败**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "success": false,
    "message": "批量同步完成，成功2条，失败1条",
    "totalCount": 3,
    "successCount": 2,
    "failCount": 1,
    "details": [
      {
        "id": 1,
        "success": true,
        "message": "同步成功"
      },
      {
        "id": 2,
        "success": false,
        "message": "同步失败: 元数据变更记录不存在"
      },
      {
        "id": 3,
        "success": true,
        "message": "同步成功"
      }
    ]
  }
}
```

**失败**:

```json
{
  "code": 400,
  "message": "批量同步失败: ID数组不能为空",
  "data": null
}
```

## 错误处理

- **400 Bad Request**: 批量同步过程中出现的具体错误，如ID数组为空、同步操作失败等
- **403 Forbidden**: 用户无权限执行此操作
- **500 Internal Server Error**: 服务器内部错误导致批量同步失败

## 注意事项

- 批量同步操作可能耗时较长，取决于数据量大小和变更复杂度
- 同步过程中会执行数据库操作，可能会锁定相关表
- 支持并发处理，单个同步任务失败不会影响其他任务的执行
- 批量操作的性能取决于ID数组的大小，建议单次操作不超过100个ID
- 同步过程中会输出实时进度日志，方便监控执行状态
- 同步完成后，会返回详细的同步结果统计

## 相关接口

- [同步元数据变更到本地数据库](http://localhost:8080/integration/change/{id}/sync) - 同步指定的元数据变更
- [查询对象元数据变更列表](http://localhost:8080/integration/change/list) - 查询所有元数据变更列表
- [查询未同步的元数据变更列表](http://localhost:8080/integration/change/unsynced) - 查询未同步的元数据变更列表

## 实现细节

- 接口通过调用`dataiIntegrationMetadataChangeService.syncBatchToLocalDatabase()`方法实现批量同步
- 服务层会使用线程池并发执行同步任务
- 支持实时进度反馈和异常隔离
- 同步完成后会汇总详细的同步结果
- 使用AtomicInteger和synchronizedList保证并发环境下的数据一致性

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 批量同步多个变更 | ids=1,2,3 | 操作成功，返回批量同步结果 | 操作成功，返回批量同步结果 | 通过 |
| 批量同步包含不存在的变更 | ids=1,999,3 | 操作成功，部分失败，返回详细结果 | 操作成功，部分失败，返回详细结果 | 通过 |
| 批量同步空数组 | ids= | 操作失败，返回ID数组不能为空错误 | 操作失败，返回ID数组不能为空错误 | 通过 |
| 无权限批量同步 | 任意参数 | 操作失败，返回403错误 | 操作失败，返回403错误 | 通过 |