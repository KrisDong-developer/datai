# 接口文档

## 接口信息

- **接口名称**: 同步多个对象数据到本地数据库
- **接口路径**: /integration/object/syncMultipleData
- **请求方法**: POST
- **模块归属**: 对象同步控制
- **版本号**: v1.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

触发多个对象的批量数据同步操作，将多个Salesforce对象的数据同步到本地数据库。同步操作包括全量同步和增量同步两种模式，具体模式由每个对象的配置决定。该方法会依次同步每个对象，即使某个对象同步失败，也会继续同步其他对象。

## 请求参数

### 路径参数

无

### 查询参数

无

### 请求体 (JSON)

```json
{
  "ids": [1, 2, 3]
}
```

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| ids | Integer[] | 是 | 对象ID数组 | [1, 2, 3] |

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "success": true,
    "message": "多对象数据同步完成",
    "totalCount": 3,
    "successCount": 2,
    "failureCount": 1,
    "duration": 15000,
    "startTime": "2026-01-09 10:00:00",
    "endTime": "2026-01-09 10:00:15",
    "syncResults": [
      {
        "objectId": 1,
        "objectName": "Account",
        "success": true,
        "message": "同步成功",
        "processedRecords": 1000
      },
      {
        "objectId": 2,
        "objectName": "Contact",
        "success": true,
        "message": "同步成功",
        "processedRecords": 2000
      },
      {
        "objectId": 3,
        "objectName": "Opportunity",
        "success": false,
        "message": "同步失败: 连接超时",
        "processedRecords": 0
      }
    ]
  }
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| success | Boolean | 操作结果 | true |
| message | String | 操作消息 | 多对象数据同步完成 |
| totalCount | Integer | 总对象数 | 3 |
| successCount | Integer | 成功对象数 | 2 |
| failureCount | Integer | 失败对象数 | 1 |
| duration | Long | 同步总耗时(毫秒) | 15000 |
| startTime | String | 开始时间 | 2026-01-09 10:00:00 |
| endTime | String | 结束时间 | 2026-01-09 10:00:15 |
| syncResults | Array | 每个对象的同步结果 | 数组对象 |

### 失败响应

**HTTP 状态码**: 400 Bad Request

```json
{
  "code": 400,
  "message": "对象ID不能为空",
  "data": null
}
```

**HTTP 状态码**: 500 Internal Server Error

```json
{
  "code": 500,
  "message": "同步多个对象数据时发生异常: 系统错误",
  "data": null
}
```

## 接口示例

### 请求示例

```bash
curl -X POST "http://localhost:8080/integration/object/syncMultipleData" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer [token]" \
  -d '{
    "ids": [1, 2, 3]
  }'
```

### 响应示例

**成功**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "success": true,
    "message": "多对象数据同步完成",
    "totalCount": 3,
    "successCount": 2,
    "failureCount": 1,
    "duration": 15000,
    "startTime": "2026-01-09 10:00:00",
    "endTime": "2026-01-09 10:00:15",
    "syncResults": [
      {
        "objectId": 1,
        "objectName": "Account",
        "success": true,
        "message": "同步成功",
        "processedRecords": 1000
      },
      {
        "objectId": 2,
        "objectName": "Contact",
        "success": true,
        "message": "同步成功",
        "processedRecords": 2000
      },
      {
        "objectId": 3,
        "objectName": "Opportunity",
        "success": false,
        "message": "同步失败: 连接超时",
        "processedRecords": 0
      }
    ]
  }
}
```

**失败**:

```json
{
  "code": 400,
  "message": "对象ID不能为空",
  "data": null
}
```

## 错误处理

- 对象ID数组为空时，返回400错误
- 同步过程中发生异常时，返回500错误
- 权限不足时，返回403错误

## 注意事项

- 该接口需要对象同步控制的同步权限
- 批量同步操作可能会耗时较长，特别是当对象数量较多或数据量较大时
- 同步过程中会占用较多的服务器资源和网络带宽
- 即使某个对象同步失败，也会继续同步其他对象
- 建议在非高峰期执行批量同步操作
- 批量同步的对象数量不宜过多，建议每次不超过10个对象

## 相关接口

- [同步单对象数据](http://localhost:8080/integration/object/{id}/syncData) - 同步单对象数据到本地数据库
- [获取对象整体统计信息](http://localhost:8080/integration/object/statistics) - 获取所有对象的整体统计信息

## 实现细节

- 验证对象ID数组是否为空
- 调用service层的syncMultipleObjectData方法执行批量同步操作
- 处理同步过程中的异常情况
- 记录同步操作的日志
- 返回批量同步结果

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 同步多个存在对象 | {"ids": [1, 2]} | 返回批量同步结果 | 返回批量同步结果 | 通过 |
| 同步包含不存在对象 | {"ids": [1, 999]} | 部分成功，部分失败 | 部分成功，部分失败 | 通过 |
| 同步ID数组为空 | {"ids": []} | 返回对象ID不能为空错误 | 返回对象ID不能为空错误 | 通过 |
| 同步ID为null | {"ids": null} | 返回对象ID不能为空错误 | 返回对象ID不能为空错误 | 通过 |