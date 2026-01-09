# 接口文档：获取分组变更统计信息

## 接口信息

- **接口名称**: 获取分组变更统计信息
- **接口路径**: /integration/change/statistics/group
- **请求方法**: GET
- **模块归属**: integration
- **版本号**: v1.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

按指定维度分组获取元数据变更的统计信息，支持根据变更类型、操作类型、对象API名称、同步状态、是否自定义和时间范围等条件进行筛选。

## 请求参数

### 查询参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
| groupBy | String | 是 | 分组维度，支持：changeType, operationType, objectApi, syncStatus, isCustom | changeType | 无 |
| changeType | String | 否 | 变更类型 | OBJECT | 无 |
| operationType | String | 否 | 操作类型 | CREATE | 无 |
| objectApi | String | 否 | 对象API名称 | Account | 无 |
| syncStatus | Boolean | 否 | 同步状态 | false | 无 |
| isCustom | Boolean | 否 | 是否自定义 | true | 无 |
| startTime | String | 否 | 开始时间，格式：yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd | 2026-01-01 00:00:00 | 无 |
| endTime | String | 否 | 结束时间，格式：yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd | 2026-01-07 23:59:59 | 无 |

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "totalCount": 100,
    "groups": {
      "OBJECT": {
        "count": 30,
        "percentage": 0.3
      },
      "FIELD": {
        "count": 70,
        "percentage": 0.7
      }
    }
  }
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| totalCount | Integer | 总变更数 | 100 |
| groups | Object | 分组统计信息 | 按指定维度分组的统计数据 |
| groups[group].count | Integer | 该分组的变更数 | 30 |
| groups[group].percentage | Double | 该分组占总数的百分比 | 0.3 |

### 失败响应

**HTTP 状态码**: 400 Bad Request

```json
{
  "code": 400,
  "message": "分组维度不正确，支持的维度：changeType, operationType, objectApi, syncStatus, isCustom",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 400 | 分组维度不正确 | 分组维度参数错误 |
| 400 | 开始时间格式不正确 | 开始时间格式错误 |
| 400 | 结束时间格式不正确 | 结束时间格式错误 |
| 403 | 无权限执行此操作 | 当用户没有integration:change:statistics权限时 |
| 500 | 统计失败 | 服务器内部错误导致统计失败 |

## 接口示例

### 请求示例

```bash
curl -X GET "http://localhost:8080/integration/change/statistics/group?groupBy=changeType&startTime=2026-01-01&endTime=2026-01-07" \
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
    "totalCount": 100,
    "groups": {
      "OBJECT": {
        "count": 30,
        "percentage": 0.3
      },
      "FIELD": {
        "count": 70,
        "percentage": 0.7
      }
    }
  }
}
```

**失败**:

```json
{
  "code": 400,
  "message": "分组维度不正确，支持的维度：changeType, operationType, objectApi, syncStatus, isCustom",
  "data": null
}
```

## 错误处理

- **400 Bad Request**: 分组维度错误、时间格式错误或其他参数错误
- **403 Forbidden**: 用户无权限执行此操作
- **500 Internal Server Error**: 服务器内部错误导致统计失败

## 注意事项

- 统计操作可能耗时较长，取决于数据量大小
- groupBy参数为必填，支持的维度：changeType, operationType, objectApi, syncStatus, isCustom
- 支持多种筛选条件与分组维度组合使用
- 时间参数支持两种格式：yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd

## 相关接口

- [获取变更统计信息](http://localhost:8080/integration/change/statistics) - 获取变更统计信息
- [获取趋势变更统计信息](http://localhost:8080/integration/change/statistics/trend) - 获取变更趋势统计信息

## 实现细节

- 接口通过构建参数Map调用`dataiIntegrationMetadataChangeService.getChangeStatisticsByGroup()`方法获取统计信息
- 服务层会根据传入的分组维度和筛选条件进行分组统计
- 支持分组维度验证和时间格式验证
- 统计结果包括总变更数和按指定维度分组的详细统计信息

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 按变更类型分组统计 | groupBy=changeType | 操作成功，返回按变更类型分组的统计信息 | 操作成功，返回按变更类型分组的统计信息 | 通过 |
| 按操作类型分组统计 | groupBy=operationType | 操作成功，返回按操作类型分组的统计信息 | 操作成功，返回按操作类型分组的统计信息 | 通过 |
| 按同步状态分组统计 | groupBy=syncStatus | 操作成功，返回按同步状态分组的统计信息 | 操作成功，返回按同步状态分组的统计信息 | 通过 |
| 无效分组维度 | groupBy=invalid | 操作失败，返回分组维度错误信息 | 操作失败，返回分组维度错误信息 | 通过 |
| 无权限获取统计 | 任意参数 | 操作失败，返回403错误 | 操作失败，返回403错误 | 通过 |