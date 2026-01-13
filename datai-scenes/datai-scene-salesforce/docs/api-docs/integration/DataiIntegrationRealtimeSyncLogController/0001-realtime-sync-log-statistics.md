# 接口文档模板

## 接口信息

- **接口名称**: 获取实时同步日志统计信息
- **接口路径**: /integration/realtimelog/statistics
- **请求方法**: GET
- **模块归属**: Salesforce 集成
- **版本号**: v1.0
- **创建日期**: 2026-01-13
- **最后更新**: 2026-01-13

## 功能描述

获取实时同步日志的详细统计信息，支持多种分组维度，包括综合统计、按对象统计、按操作类型统计、按状态统计和按时间趋势统计。该接口提供了对实时同步日志数据的全面分析能力，帮助用户了解同步服务的运行状况和性能指标。

## 请求参数

### 查询参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
| groupBy | String | 否 | 分组维度，可选值：overall（综合）、object（对象）、operationType（操作类型）、status（状态）、time（时间趋势） | overall | overall |
| timeUnit | String | 否 | 时间单位，当 groupBy=time 时有效，可选值：day（天）、week（周）、month（月）、quarter（季度） | day | day |
| objectName | String | 否 | 对象名称，用于过滤特定对象的统计信息 | Account | - |
| operationType | String | 否 | 操作类型，用于过滤特定操作类型的统计信息，可选值：CREATE、UPDATE、DELETE、UNDELETE | CREATE | - |
| syncStatus | String | 否 | 同步状态，用于过滤特定状态的统计信息，可选值：SUCCESS、FAILED、PENDING | SUCCESS | - |
| startTime | String | 否 | 开始时间，格式：yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss | 2026-01-01 | 30天前 |
| endTime | String | 否 | 结束时间，格式：yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss | 2026-01-13 | 当前时间 |

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

**综合统计响应 (groupBy=overall)**:

```json
{
  "success": true,
  "message": "获取综合统计信息成功",
  "data": {
    "totalCount": 1000,
    "successCount": 950,
    "failureCount": 40,
    "pendingCount": 10,
    "successRate": 95.0,
    "avgProcessingTime": 2.5
  }
}
```

**按对象统计响应 (groupBy=object)**:

```json
{
  "success": true,
  "message": "获取对象统计信息成功",
  "data": [
    {
      "objectName": "Account",
      "totalCount": 500,
      "successCount": 480,
      "failureCount": 15,
      "pendingCount": 5,
      "successRate": 96.0,
      "avgProcessingTime": 2.3
    },
    {
      "objectName": "Contact",
      "totalCount": 300,
      "successCount": 285,
      "failureCount": 12,
      "pendingCount": 3,
      "successRate": 95.0,
      "avgProcessingTime": 2.7
    }
  ]
}
```

**按操作类型统计响应 (groupBy=operationType)**:

```json
{
  "success": true,
  "message": "获取操作类型统计信息成功",
  "data": [
    {
      "operationType": "CREATE",
      "totalCount": 400,
      "successCount": 390,
      "failureCount": 8,
      "pendingCount": 2,
      "successRate": 97.5,
      "avgProcessingTime": 2.1
    },
    {
      "operationType": "UPDATE",
      "totalCount": 500,
      "successCount": 475,
      "failureCount": 20,
      "pendingCount": 5,
      "successRate": 95.0,
      "avgProcessingTime": 2.8
    }
  ]
}
```

**按状态统计响应 (groupBy=status)**:

```json
{
  "success": true,
  "message": "获取状态统计信息成功",
  "data": [
    {
      "syncStatus": "SUCCESS",
      "totalCount": 950,
      "successRate": 100.0,
      "avgProcessingTime": 2.3
    },
    {
      "syncStatus": "FAILED",
      "totalCount": 40,
      "successRate": 0.0,
      "avgProcessingTime": 3.5
    }
  ]
}
```

**按时间趋势统计响应 (groupBy=time)**:

```json
{
  "success": true,
  "message": "获取时间趋势统计信息成功",
  "data": [
    {
      "timeKey": "2026-01-01",
      "totalCount": 100,
      "successCount": 95,
      "failureCount": 4,
      "pendingCount": 1,
      "successRate": 95.0,
      "totalRetryCount": 5,
      "maxRetryCount": 2,
      "avgProcessingTime": 2.4
    },
    {
      "timeKey": "2026-01-02",
      "totalCount": 120,
      "successCount": 115,
      "failureCount": 3,
      "pendingCount": 2,
      "successRate": 95.83,
      "totalRetryCount": 8,
      "maxRetryCount": 3,
      "avgProcessingTime": 2.6
    }
  ]
}
```

### 字段说明

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| success | Boolean | 操作是否成功 | true |
| message | String | 操作结果消息 | "获取综合统计信息成功" |
| data | Object/Array | 统计数据，根据 groupBy 参数返回不同结构 | - |
| totalCount | Integer | 总记录数 | 1000 |
| successCount | Integer | 成功记录数 | 950 |
| failureCount | Integer | 失败记录数 | 40 |
| pendingCount | Integer | 待处理记录数 | 10 |
| successRate | Double | 成功率（百分比） | 95.0 |
| avgProcessingTime | Double | 平均处理时间（秒） | 2.5 |
| objectName | String | 对象名称 | "Account" |
| operationType | String | 操作类型 | "CREATE" |
| syncStatus | String | 同步状态 | "SUCCESS" |
| timeKey | String | 时间键，格式根据 timeUnit 参数变化 | "2026-01-01" |
| totalRetryCount | Integer | 总重试次数 | 5 |
| maxRetryCount | Integer | 最大重试次数 | 2 |

### 失败响应

**HTTP 状态码**: 500

```json
{
  "success": false,
  "message": "获取统计信息失败: [错误详情]",
  "data": null
}
```

## 接口示例

### 请求示例

**获取综合统计**:

```bash
curl -X GET "http://localhost/dev-api/integration/realtimelog/statistics?groupBy=overall" \
  -H "Authorization: Bearer [token]"
```

**按对象统计**:

```bash
curl -X GET "http://localhost/dev-api/integration/realtimelog/statistics?groupBy=object&startTime=2026-01-01&endTime=2026-01-13" \
  -H "Authorization: Bearer [token]"
```

**按时间趋势统计（按天）**:

```bash
curl -X GET "http://localhost/dev-api/integration/realtimelog/statistics?groupBy=time&timeUnit=day&startTime=2026-01-01&endTime=2026-01-13" \
  -H "Authorization: Bearer [token]"
```

**按时间趋势统计（按月）**:

```bash
curl -X GET "http://localhost/dev-api/integration/realtimelog/statistics?groupBy=time&timeUnit=month&startTime=2026-01-01&endTime=2026-01-13" \
  -H "Authorization: Bearer [token]"
```

**按对象和时间趋势统计**:

```bash
curl -X GET "http://localhost/dev-api/integration/realtimelog/statistics?groupBy=time&timeUnit=day&objectName=Account&startTime=2026-01-01&endTime=2026-01-13" \
  -H "Authorization: Bearer [token]"
```

### 响应示例

**成功 - 综合统计**:

```json
{
  "success": true,
  "message": "获取综合统计信息成功",
  "data": {
    "totalCount": 1000,
    "successCount": 950,
    "failureCount": 40,
    "pendingCount": 10,
    "successRate": 95.0,
    "avgProcessingTime": 2.5
  }
}
```

**成功 - 时间趋势统计**:

```json
{
  "success": true,
  "message": "获取时间趋势统计信息成功",
  "data": [
    {
      "timeKey": "2026-01-01",
      "totalCount": 100,
      "successCount": 95,
      "failureCount": 4,
      "pendingCount": 1,
      "successRate": 95.0,
      "totalRetryCount": 5,
      "maxRetryCount": 2,
      "avgProcessingTime": 2.4
    },
    {
      "timeKey": "2026-01-02",
      "totalCount": 120,
      "successCount": 115,
      "failureCount": 3,
      "pendingCount": 2,
      "successRate": 95.83,
      "totalRetryCount": 8,
      "maxRetryCount": 3,
      "avgProcessingTime": 2.6
    }
  ]
}
```

**失败**:

```json
{
  "success": false,
  "message": "获取统计信息失败: 时间格式解析失败",
  "data": null
}
```

## 错误处理

接口在以下情况下会返回错误响应：

1. **时间格式错误**: 当 startTime 或 endTime 参数格式不正确时，返回错误信息
2. **数据库查询异常**: 当数据库查询失败时，返回错误信息
3. **参数验证失败**: 当 groupBy 参数值不在允许的范围内时，默认使用 overall

## 注意事项

1. **时间格式**: startTime 和 endTime 参数支持两种格式：
   - 日期格式：yyyy-MM-dd（如 2026-01-13）
   - 日期时间格式：yyyy-MM-dd HH:mm:ss（如 2026-01-13 14:30:00）

2. **默认时间范围**: 如果未提供 startTime 和 endTime 参数，默认查询最近 30 天的数据

3. **时间单位**: timeUnit 参数仅在 groupBy=time 时生效，支持以下值：
   - day: 按天统计，时间键格式为 yyyy-MM-dd
   - week: 按周统计，时间键格式为 yyyy-w（如 2026-02 表示 2026 年第 2 周）
   - month: 按月统计，时间键格式为 yyyy-MM
   - quarter: 按季度统计，时间键格式为 yyyy-Qn（如 2026-Q1 表示 2026 年第 1 季度）

4. **数据完整性**: 时间趋势统计会自动填充缺失的时间点，确保返回的时间序列数据完整

5. **权限要求**: 调用该接口需要拥有 `integration:realtimelog:statistics` 权限

6. **性能考虑**: 对于大数据量的统计查询，建议合理设置时间范围以避免查询超时

## 相关接口

- [实时同步状态](DataiIntegrationRealtimeSyncController/0001-realtime-sync-status.md) - 获取实时同步服务的运行状态
- [实时同步统计](DataiIntegrationRealtimeSyncController/0006-realtime-sync-statistics.md) - 获取实时同步服务的统计信息
- [同步日志统计](DataiIntegrationSyncLogController/0002-log-statistics.md) - 获取同步日志的统计信息

## 实现细节

### 技术架构

该接口采用分层架构实现：

1. **Controller 层**: `DataiIntegrationRealtimeSyncLogController` 处理 HTTP 请求，参数验证和响应封装
2. **Service 层**: `IDataiIntegrationRealtimeSyncLogService` 和 `DataiIntegrationRealtimeSyncLogServiceImpl` 实现业务逻辑和数据处理
3. **Mapper 层**: `DataiIntegrationRealtimeSyncLogMapper` 和对应的 XML 文件实现数据库查询

### 核心逻辑

1. **参数处理**: 接收并验证查询参数，设置默认值
2. **分组策略**: 根据 groupBy 参数选择不同的统计策略
3. **时间处理**: 自动处理时间范围，生成完整的时间键序列
4. **数据填充**: 填充缺失的时间点数据，确保统计结果完整
5. **指标计算**: 计算成功率、平均处理时间等关键指标

### 数据库查询

使用 MyBatis 实现 SQL 查询，支持动态条件过滤和分组聚合：

- 综合统计：使用 COUNT、SUM、AVG 聚合函数
- 分组统计：使用 GROUP BY 子句按指定维度分组
- 时间趋势：使用 DATE_FORMAT 函数按时间单位分组

### 性能优化

1. 使用数据库索引加速查询
2. 合理设置时间范围避免全表扫描
3. 使用聚合函数减少数据传输量

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0
- **数据库**: MySQL 8.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 获取综合统计 | groupBy=overall | 返回综合统计数据 | 综合统计数据 | 通过 |
| 按对象统计 | groupBy=object | 返回对象统计列表 | 对象统计列表 | 通过 |
| 按操作类型统计 | groupBy=operationType | 返回操作类型统计列表 | 操作类型统计列表 | 通过 |
| 按状态统计 | groupBy=status | 返回状态统计列表 | 状态统计列表 | 通过 |
| 按天趋势统计 | groupBy=time&timeUnit=day | 返回按天统计的趋势数据 | 按天趋势数据 | 通过 |
| 按月趋势统计 | groupBy=time&timeUnit=month | 返回按月统计的趋势数据 | 按月趋势数据 | 通过 |
| 按对象过滤 | groupBy=object&objectName=Account | 返回 Account 对象的统计数据 | Account 统计数据 | 通过 |
| 时间范围过滤 | startTime=2026-01-01&endTime=2026-01-13 | 返回指定时间范围的统计数据 | 时间范围统计数据 | 通过 |
| 默认时间范围 | 不提供时间参数 | 返回最近 30 天的统计数据 | 最近 30 天数据 | 通过 |
| 无效时间格式 | startTime=invalid | 返回错误信息 | 错误信息 | 通过 |
