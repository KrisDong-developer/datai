# 接口文档

## 接口信息

- **接口名称**: 同步单对象数据到本地数据库
- **接口路径**: /integration/object/{id}/syncData
- **请求方法**: POST
- **模块归属**: 对象同步控制
- **版本号**: v1.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

触发指定对象的单次数据同步操作，将Salesforce对象的数据同步到本地数据库。同步操作包括全量同步和增量同步两种模式，具体模式由对象的配置决定。

## 请求参数

### 路径参数

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| id | Integer | 是 | 对象ID | 1 |

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
    "message": "对象数据同步成功",
    "objectId": 1,
    "objectName": "Account",
    "syncMode": "incremental",
    "totalRecords": 1000,
    "processedRecords": 1000,
    "successRecords": 998,
    "failureRecords": 2,
    "duration": 5000,
    "startTime": "2026-01-09 10:00:00",
    "endTime": "2026-01-09 10:00:05"
  }
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| success | Boolean | 操作结果 | true |
| message | String | 操作消息 | 对象数据同步成功 |
| objectId | Integer | 对象ID | 1 |
| objectName | String | 对象名称 | Account |
| syncMode | String | 同步模式 | incremental/full |
| totalRecords | Integer | 总记录数 | 1000 |
| processedRecords | Integer | 处理记录数 | 1000 |
| successRecords | Integer | 成功记录数 | 998 |
| failureRecords | Integer | 失败记录数 | 2 |
| duration | Long | 同步耗时(毫秒) | 5000 |
| startTime | String | 开始时间 | 2026-01-09 10:00:00 |
| endTime | String | 结束时间 | 2026-01-09 10:00:05 |

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
  "message": "同步对象数据时发生异常: 连接超时",
  "data": null
}
```

## 接口示例

### 请求示例

```bash
curl -X POST "http://localhost:8080/integration/object/1/syncData" \
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
    "message": "对象数据同步成功",
    "objectId": 1,
    "objectName": "Account",
    "syncMode": "incremental",
    "totalRecords": 1000,
    "processedRecords": 1000,
    "successRecords": 998,
    "failureRecords": 2,
    "duration": 5000,
    "startTime": "2026-01-09 10:00:00",
    "endTime": "2026-01-09 10:00:05"
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

- 对象ID为空时，返回400错误
- 对象不存在时，返回400错误
- 同步过程中发生异常时，返回500错误
- 权限不足时，返回403错误

## 注意事项

- 该接口需要对象同步控制的同步权限
- 同步操作可能会耗时较长，特别是对于数据量较大的对象
- 同步过程中会占用一定的服务器资源和网络带宽
- 建议在非高峰期执行同步操作

## 相关接口

- [同步多个对象数据](http://localhost:8080/integration/object/syncMultipleData) - 同步多个对象数据到本地数据库
- [获取对象同步统计信息](http://localhost:8080/integration/object/{id}/statistics) - 获取对象同步统计信息

## 实现细节

- 验证对象ID是否为空
- 调用service层的syncSingleObjectData方法执行同步操作
- 处理同步过程中的异常情况
- 记录同步操作的日志
- 返回同步结果

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 同步存在对象数据 | id=1 | 返回同步成功 | 返回同步成功 | 通过 |
| 同步不存在对象数据 | id=999 | 返回对象不存在错误 | 返回对象不存在错误 | 通过 |
| 同步ID为空 | id=null | 返回对象ID不能为空错误 | 返回对象ID不能为空错误 | 通过 |