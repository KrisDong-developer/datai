# 接口文档

## 接口信息

- **接口名称**: 获取对象整体统计信息
- **接口路径**: /integration/object/statistics
- **请求方法**: GET
- **模块归属**: 对象同步控制
- **版本号**: v1.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

获取所有对象的整体统计信息，包括对象数量、同步状态分布、数据量统计等。

## 请求参数

### 路径参数

无

### 查询参数

无

### 请求体

无

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
    "totalObjectCount": 20,
    "enabledObjectCount": 15,
    "disabledObjectCount": 5,
    "incrementalEnabledCount": 10,
    "totalSyncCount": 1000,
    "successSyncCount": 950,
    "failureSyncCount": 50,
    "overallSuccessRate": "95%",
    "objectStatistics": [
      {
        "objectId": 1,
        "objectName": "Account",
        "syncCount": 100,
        "successCount": 98,
        "failureCount": 2,
        "successRate": "98%"
      }
    ]
  }
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| success | Boolean | 操作结果 | true |
| message | String | 操作消息 | 获取统计信息成功 |
| totalObjectCount | Integer | 总对象数 | 20 |
| enabledObjectCount | Integer | 启用同步的对象数 | 15 |
| disabledObjectCount | Integer | 禁用同步的对象数 | 5 |
| incrementalEnabledCount | Integer | 启用增量更新的对象数 | 10 |
| totalSyncCount | Integer | 总同步次数 | 1000 |
| successSyncCount | Integer | 成功同步次数 | 950 |
| failureSyncCount | Integer | 失败同步次数 | 50 |
| overallSuccessRate | String | 整体成功率 | 95% |
| objectStatistics | Array | 各对象详细统计 | 数组对象 |

### 失败响应

**HTTP 状态码**: 500 Internal Server Error

```json
{
  "code": 500,
  "message": "获取统计信息失败: 数据库连接异常",
  "data": null
}
```

## 接口示例

### 请求示例

```bash
curl -X GET "http://localhost:8080/integration/object/statistics" \
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
    "totalObjectCount": 20,
    "enabledObjectCount": 15,
    "disabledObjectCount": 5,
    "incrementalEnabledCount": 10,
    "totalSyncCount": 1000,
    "successSyncCount": 950,
    "failureSyncCount": 50,
    "overallSuccessRate": "95%",
    "objectStatistics": [
      {
        "objectId": 1,
        "objectName": "Account",
        "syncCount": 100,
        "successCount": 98,
        "failureCount": 2,
        "successRate": "98%"
      }
    ]
  }
}
```

**失败**:

```json
{
  "code": 500,
  "message": "获取统计信息失败: 数据库连接异常",
  "data": null
}
```

## 错误处理

- 数据库连接异常时，返回500错误
- 权限不足时，返回403错误
- 服务器内部错误时，返回500错误

## 注意事项

- 该接口需要对象同步控制的查看权限
- 统计信息可能会有一定的延迟，取决于数据同步的频率
- 当对象数量较多时，该接口可能会耗时较长

## 相关接口

- [获取对象同步统计信息](http://localhost:8080/integration/object/{id}/statistics) - 获取指定对象的同步统计信息
- [查询对象同步控制列表](http://localhost:8080/integration/object/list) - 查询对象同步控制列表

## 实现细节

- 从数据库中查询所有对象的配置和同步历史
- 计算整体统计指标，包括对象数量、同步成功率等
- 构建并返回统计结果

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 获取整体统计信息 | 无 | 返回整体统计信息 | 返回整体统计信息 | 通过 |
| 无权限访问 | 无 | 返回权限不足错误 | 返回权限不足错误 | 通过 |