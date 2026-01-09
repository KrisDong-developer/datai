# API调用日志统计接口文档

## 接口信息

- **接口名称**: API调用日志统计
- **接口路径**: `/integration/apilog/statistics`
- **请求方法**: GET
- **模块归属**: 集成模块
- **版本号**: v1.0.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

获取API调用日志的统计信息，支持按不同维度进行筛选和分组统计。该接口可以帮助监控API调用情况，分析调用趋势，识别异常调用，为系统优化提供数据支持。

## 请求参数

### 路径参数

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| 无 | - | - | - | - |

### 查询参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
| apiType | String | 否 | API类型 | REST | - |
| connectionClass | String | 否 | 连接类名 | RESTConnection | - |
| methodName | String | 否 | 方法名 | query | - |
| status | String | 否 | 状态 | SUCCESS | - |
| startTime | Date | 否 | 开始时间 | 2026-01-01 00:00:00 | - |
| endTime | Date | 否 | 结束时间 | 2026-01-31 23:59:59 | - |
| groupBy | String | 否 | 分组维度 | apiType | - |

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "success": true,
    "statistics": {
      "totalCount": 1000,
      "successCount": 950,
      "failCount": 50,
      "avgResponseTime": 150,
      "groupedData": {
        "REST": 600,
        "SOAP": 400
      }
    }
  }
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| success | Boolean | 是否成功 | true |
| statistics | Object | 统计数据 | - |
| totalCount | Integer | 总调用次数 | 1000 |
| successCount | Integer | 成功调用次数 | 950 |
| failCount | Integer | 失败调用次数 | 50 |
| avgResponseTime | Integer | 平均响应时间(ms) | 150 |
| groupedData | Object | 分组统计数据 | - |

### 失败响应

**HTTP 状态码**: 400 Bad Request

```json
{
  "code": 400,
  "message": "参数错误",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 400 | 参数错误 | 请求参数格式不正确 |
| 401 | 未授权 | 未提供有效的认证信息 |
| 403 | 禁止访问 | 没有权限访问该接口 |
| 500 | 服务器错误 | 服务器内部错误 |

## 接口示例

### 请求示例

```bash
curl -X GET "http://localhost:8080/integration/apilog/statistics?apiType=REST&status=SUCCESS&startTime=2026-01-01%2000:00:00&endTime=2026-01-31%2023:59:59&groupBy=apiType" \
  -H "Authorization: Bearer your-token"
```

### 响应示例

**成功**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "success": true,
    "statistics": {
      "totalCount": 600,
      "successCount": 580,
      "failCount": 20,
      "avgResponseTime": 120,
      "groupedData": {
        "REST": 600
      }
    }
  }
}
```

**失败**:

```json
{
  "code": 403,
  "message": "没有权限访问该接口",
  "data": null
}
```

## 错误处理

- **参数错误**: 当请求参数格式不正确时，返回400错误码和相应的错误信息
- **未授权**: 当未提供有效的认证信息时，返回401错误码
- **禁止访问**: 当用户没有权限访问该接口时，返回403错误码
- **服务器错误**: 当服务器内部发生错误时，返回500错误码和相应的错误信息

## 注意事项

- **权限要求**: 访问该接口需要具有 `integration:apilog:statistics` 权限
- **时间格式**: startTime 和 endTime 参数需要使用 `yyyy-MM-dd HH:mm:ss` 格式
- **分组维度**: groupBy 参数支持按 apiType、connectionClass、methodName、status 等维度进行分组
- **性能考虑**: 当查询时间范围较大时，可能会影响接口响应速度，建议合理设置时间范围

## 相关接口

- [查询API调用日志列表](/integration/apilog/list) - 查询API调用日志的详细列表
- [导出API调用日志列表](/integration/apilog/export) - 导出API调用日志列表

## 实现细节

- **实现类**: `DataiIntegrationApiCallLogController.java`
- **方法**: `getStatistics()`
- **服务调用**: 调用 `IDataiIntegrationApiCallLogService.getApiCallLogStatistics()` 方法获取统计数据
- **参数处理**: 使用 `@DateTimeFormat` 注解对日期参数进行格式化
- **权限控制**: 使用 `@PreAuthorize` 注解进行权限控制

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v3.8.9-G

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 基本统计 | 无参数 | 返回所有API调用的统计数据 | 返回所有API调用的统计数据 | 通过 |
| 按API类型统计 | apiType=REST | 返回REST类型API的统计数据 | 返回REST类型API的统计数据 | 通过 |
| 按状态统计 | status=SUCCESS | 返回成功状态的API调用统计数据 | 返回成功状态的API调用统计数据 | 通过 |
| 按时间范围统计 | startTime=2026-01-01 00:00:00&endTime=2026-01-31 23:59:59 | 返回指定时间范围内的API调用统计数据 | 返回指定时间范围内的API调用统计数据 | 通过 |
| 分组统计 | groupBy=apiType | 返回按API类型分组的统计数据 | 返回按API类型分组的统计数据 | 通过 |
| 权限测试 | 无权限 | 返回403错误 | 返回403错误 | 通过 |