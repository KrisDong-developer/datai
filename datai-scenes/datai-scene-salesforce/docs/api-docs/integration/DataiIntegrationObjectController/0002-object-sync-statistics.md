# 接口文档

## 接口信息

- **接口名称**: 获取对象同步统计信息
- **接口路径**: /integration/object/{id}/statistics
- **请求方法**: GET
- **模块归属**: 对象同步控制
- **版本号**: v1.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

获取指定对象的同步统计信息，包括同步次数、成功率、最近同步时间等数据。

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
    "totalSyncCount": 100,
    "successCount": 95,
    "failureCount": 5,
    "successRate": "95%",
    "lastSyncTime": "2026-01-08 12:00:00",
    "lastSyncStatus": "success",
    "syncDuration": 12000
  }
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| totalSyncCount | Integer | 总同步次数 | 100 |
| successCount | Integer | 成功次数 | 95 |
| failureCount | Integer | 失败次数 | 5 |
| successRate | String | 成功率 | 95% |
| lastSyncTime | String | 最近同步时间 | 2026-01-08 12:00:00 |
| lastSyncStatus | String | 最近同步状态 | success |
| syncDuration | Long | 同步耗时(毫秒) | 12000 |

### 失败响应

**HTTP 状态码**: 400 Bad Request

```json
{
  "code": 400,
  "message": "对象不存在",
  "data": null
}
```

## 接口示例

### 请求示例

```bash
curl -X GET "http://localhost:8080/integration/object/1/statistics" \
  -H "Authorization: Bearer [token]"
```

### 响应示例

**成功**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalSyncCount": 100,
    "successCount": 95,
    "failureCount": 5,
    "successRate": "95%",
    "lastSyncTime": "2026-01-08 12:00:00",
    "lastSyncStatus": "success",
    "syncDuration": 12000
  }
}
```

**失败**:

```json
{
  "code": 400,
  "message": "对象不存在",
  "data": null
}
```

## 错误处理

- 对象不存在时，返回400错误
- 权限不足时，返回403错误
- 服务器内部错误时，返回500错误

## 注意事项

- 该接口需要对象同步控制的查看权限
- 统计数据可能会有一定的延迟，取决于数据同步的频率

## 相关接口

- [获取对象依赖关系](http://localhost:8080/integration/object/{id}/dependencies) - 获取对象依赖关系
- [同步单对象数据](http://localhost:8080/integration/object/{id}/syncData) - 同步单对象数据到本地数据库

## 实现细节

- 从数据库中查询对象的同步历史记录
- 计算同步统计指标，包括总次数、成功率等
- 构建并返回统计结果

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 获取存在对象的统计信息 | id=1 | 返回统计信息 | 返回统计信息 | 通过 |
| 获取不存在对象的统计信息 | id=999 | 返回对象不存在错误 | 返回对象不存在错误 | 通过 |
| 无权限访问 | id=1 | 返回权限不足错误 | 返回权限不足错误 | 通过 |