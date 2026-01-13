# 接口文档 - 根据调度编号获取详细信息

## 接口信息

- **接口名称**: 根据调度编号获取详细信息
- **接口路径**: /monitor/jobLog/{jobLogId}
- **请求方法**: GET
- **模块归属**: 定时任务日志管理
- **版本号**: v1.0
- **创建日期**: 2026-01-13
- **最后更新**: 2026-01-13

## 功能描述

根据日志ID获取定时任务调度日志的详细信息，包括任务信息、执行时间、执行状态、执行结果、异常信息等完整数据。该接口用于查看单条日志的详细执行情况。

## 请求参数

### 路径参数

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| jobLogId | Long | 是 | 日志ID | 1 |

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "jobLogId": 1,
    "jobName": "同步任务",
    "jobGroup": "DEFAULT",
    "invokeTarget": "syncTask.execute()",
    "jobMessage": "任务执行成功",
    "status": "0",
    "exceptionInfo": "",
    "startTime": "2026-01-13 10:00:00",
    "stopTime": "2026-01-13 10:00:05",
    "createTime": "2026-01-13 10:00:05"
  }
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| jobLogId | Long | 日志ID | 1 |
| jobName | String | 任务名称 | 同步任务 |
| jobGroup | String | 任务组名 | DEFAULT |
| invokeTarget | String | 调用目标字符串 | syncTask.execute() |
| jobMessage | String | 任务执行消息 | 任务执行成功 |
| status | String | 执行状态（0正常 1失败） | 0 |
| exceptionInfo | String | 异常信息（执行失败时） | - |
| startTime | String | 开始时间 | 2026-01-13 10:00:00 |
| stopTime | String | 结束时间 | 2026-01-13 10:00:05 |
| createTime | String | 创建时间 | 2026-01-13 10:00:05 |
| code | Integer | 响应码 | 200 |
| msg | String | 响应消息 | 查询成功 |

### 失败响应

**HTTP 状态码**: 404 Not Found

```json
{
  "code": 404,
  "msg": "日志不存在",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 404 | 日志不存在 | 指定的日志ID不存在 |
| 403 | 没有权限访问 | 当前用户没有 monitor:job:query 权限 |

## 接口示例

### 请求示例

```bash
curl -X GET "http://localhost:8080/monitor/jobLog/1" \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

### 响应示例

**成功**:

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "jobLogId": 1,
    "jobName": "同步任务",
    "jobGroup": "DEFAULT",
    "invokeTarget": "syncTask.execute()",
    "jobMessage": "任务执行成功",
    "status": "0",
    "exceptionInfo": "",
    "startTime": "2026-01-13 10:00:00",
    "stopTime": "2026-01-13 10:00:05",
    "createTime": "2026-01-13 10:00:05"
  }
}
```

**失败**:

```json
{
  "code": 404,
  "msg": "日志不存在",
  "data": null
}
```

## 错误处理

- 当日志ID不存在时，返回 404 错误
- 当用户没有 `monitor:job:query` 权限时，返回 403 错误
- jobLogId 参数必须为有效的数字

## 注意事项

- 需要登录认证
- 需要 `monitor:job:query` 权限
- jobLogId 必须是有效的日志ID
- 返回的数据包含日志的完整信息
- 执行失败时，exceptionInfo 字段会包含详细的异常堆栈信息

## 相关接口

- [查询定时任务调度日志列表](0001-job-log-list.md) - 查询日志列表
- [导出定时任务调度日志列表](0002-export-job-log.md) - 导出日志为Excel

## 实现细节

- 通过 `ISysJobLogService.selectJobLogById()` 方法查询数据库
- 返回完整的日志对象信息
- 包含执行开始和结束时间
- 执行失败时返回完整的异常堆栈信息
- 支持查询所有状态的日志

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 正常查询 | jobLogId=1 | 返回日志详细信息 | 返回日志详细信息 | 通过 |
| 查询失败的日志 | jobLogId=失败日志ID | 返回包含异常信息的日志 | 返回包含异常信息的日志 | 通过 |
| 查询不存在的日志 | jobLogId=9999 | 返回404错误 | 返回404错误 | 通过 |
| 无权限访问 | 无权限用户 | 返回403错误 | 返回403错误 | 通过 |
