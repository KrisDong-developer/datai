# 接口文档：获取变更统计信息

## 接口信息

- **接口名称**: 获取变更统计信息
- **接口路径**: /integration/change/statistics
- **请求方法**: GET
- **模块归属**: integration
- **版本号**: v1.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

获取元数据变更的统计信息，支持根据变更类型、操作类型、对象API名称、同步状态、是否自定义和时间范围等条件进行筛选。

## 请求参数

### 查询参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
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
    "objectChangeCount": 30,
    "fieldChangeCount": 70,
    "syncedCount": 80,
    "unsyncedCount": 20,
    "customChangeCount": 60,
    "standardChangeCount": 40,
    "createCount": 40,
    "updateCount": 50,
    "deleteCount": 10,
    "successRate": 0.8
  }
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| totalCount | Integer | 总变更数 | 100 |
| objectChangeCount | Integer | 对象变更数 | 30 |
| fieldChangeCount | Integer | 字段变更数 | 70 |
| syncedCount | Integer | 已同步变更数 | 80 |
| unsyncedCount | Integer | 未同步变更数 | 20 |
| customChangeCount | Integer | 自定义变更数 | 60 |
| standardChangeCount | Integer | 标准变更数 | 40 |
| createCount | Integer | 创建操作数 | 40 |
| updateCount | Integer | 更新操作数 | 50 |
| deleteCount | Integer | 删除操作数 | 10 |
| successRate | Double | 同步成功率 | 0.8 |

### 失败响应

**HTTP 状态码**: 400 Bad Request

```json
{
  "code": 400,
  "message": "开始时间格式不正确，请使用 yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd 格式",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 400 | 开始时间格式不正确 | 开始时间格式错误 |
| 400 | 结束时间格式不正确 | 结束时间格式错误 |
| 403 | 无权限执行此操作 | 当用户没有integration:change:statistics权限时 |
| 500 | 统计失败 | 服务器内部错误导致统计失败 |

## 接口示例

### 请求示例

```bash
curl -X GET "http://localhost:8080/integration/change/statistics?changeType=OBJECT&syncStatus=false&startTime=2026-01-01&endTime=2026-01-07" \
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
    "totalCount": 50,
    "objectChangeCount": 50,
    "fieldChangeCount": 0,
    "syncedCount": 30,
    "unsyncedCount": 20,
    "customChangeCount": 35,
    "standardChangeCount": 15,
    "createCount": 20,
    "updateCount": 25,
    "deleteCount": 5,
    "successRate": 0.6
  }
}
```

**失败**:

```json
{
  "code": 400,
  "message": "开始时间格式不正确，请使用 yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd 格式",
  "data": null
}
```

## 错误处理

- **400 Bad Request**: 时间格式错误或其他参数错误
- **403 Forbidden**: 用户无权限执行此操作
- **500 Internal Server Error**: 服务器内部错误导致统计失败

## 注意事项

- 统计操作可能耗时较长，取决于数据量大小
- 支持多种筛选条件组合使用
- 时间参数支持两种格式：yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd
- 当未提供筛选条件时，会统计所有元数据变更记录

## 相关接口

- [获取分组变更统计信息](http://localhost:8080/integration/change/statistics/group) - 按指定维度分组统计变更信息
- [获取趋势变更统计信息](http://localhost:8080/integration/change/statistics/trend) - 获取变更趋势统计信息

## 实现细节

- 接口通过构建参数Map调用`dataiIntegrationMetadataChangeService.getChangeStatistics()`方法获取统计信息
- 服务层会根据传入的参数进行条件筛选和统计
- 支持时间格式验证
- 统计结果包括总变更数、对象变更数、字段变更数、同步状态分布等

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 获取所有变更统计 | 无参数 | 操作成功，返回所有变更的统计信息 | 操作成功，返回所有变更的统计信息 | 通过 |
| 获取对象变更统计 | changeType=OBJECT | 操作成功，返回对象变更的统计信息 | 操作成功，返回对象变更的统计信息 | 通过 |
| 获取未同步变更统计 | syncStatus=false | 操作成功，返回未同步变更的统计信息 | 操作成功，返回未同步变更的统计信息 | 通过 |
| 时间格式错误 | startTime=2026/01/01 | 操作失败，返回时间格式错误信息 | 操作失败，返回时间格式错误信息 | 通过 |
| 无权限获取统计 | 任意参数 | 操作失败，返回403错误 | 操作失败，返回403错误 | 通过 |