# 接口文档 - 查询定时任务调度日志列表

## 接口信息

- **接口名称**: 查询定时任务调度日志列表
- **接口路径**: /monitor/jobLog/list
- **请求方法**: GET
- **模块归属**: 定时任务日志管理
- **版本号**: v1.0
- **创建日期**: 2026-01-13
- **最后更新**: 2026-01-13

## 功能描述

查询定时任务的调度日志列表，支持分页查询和多条件筛选。该接口用于展示定时任务的执行历史记录，包括执行时间、执行状态、执行结果等信息。

## 请求参数

### 查询参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
| pageNum | Integer | 否 | 页码 | 1 | 1 |
| pageSize | Integer | 否 | 每页数量 | 10 | 10 |
| jobName | String | 否 | 任务名称 | 同步任务 | - |
| jobGroup | String | 否 | 任务组名 | DEFAULT | - |
| status | String | 否 | 执行状态（0正常 1失败） | 0 | - |
| startTime | String | 否 | 开始时间 | 2026-01-01 00:00:00 | - |
| endTime | String | 否 | 结束时间 | 2026-01-31 23:59:59 | - |

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "total": 100,
  "rows": [
    {
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
  ],
  "code": 200,
  "msg": "查询成功"
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| total | Long | 总记录数 | 100 |
| rows | Array | 日志列表 | - |
| jobLogId | Long | 日志ID | 1 |
| jobName | String | 任务名称 | 同步任务 |
| jobGroup | String | 任务组名 | DEFAULT |
| invokeTarget | String | 调用目标字符串 | syncTask.execute() |
| jobMessage | String | 任务执行消息 | 任务执行成功 |
| status | String | 执行状态（0正常 1失败） | 0 |
| exceptionInfo | String | 异常信息 | - |
| startTime | String | 开始时间 | 2026-01-13 10:00:00 |
| stopTime | String | 结束时间 | 2026-01-13 10:00:05 |
| createTime | String | 创建时间 | 2026-01-13 10:00:05 |
| code | Integer | 响应码 | 200 |
| msg | String | 响应消息 | 查询成功 |

### 失败响应

**HTTP 状态码**: 403 Forbidden

```json
{
  "code": 403,
  "msg": "没有权限访问",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 403 | 没有权限访问 | 当前用户没有 monitor:job:list 权限 |

## 接口示例

### 请求示例

```bash
curl -X GET "http://localhost:8080/monitor/jobLog/list?pageNum=1&pageSize=10&jobName=同步任务&status=0" \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

### 响应示例

**成功**:

```json
{
  "total": 1,
  "rows": [
    {
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
  ],
  "code": 200,
  "msg": "查询成功"
}
```

**失败**:

```json
{
  "code": 403,
  "msg": "没有权限访问",
  "data": null
}
```

## 错误处理

- 当用户没有 `monitor:job:list` 权限时，返回 403 错误
- 查询结果为空时，返回空数组但 code 为 200
- 时间范围查询时，开始时间必须小于结束时间

## 注意事项

- 需要登录认证
- 需要 `monitor:job:list` 权限
- 支持分页查询，默认每页 10 条记录
- 支持按任务名称、任务组名、执行状态进行筛选
- 支持按时间范围查询
- 日志记录包含完整的执行信息

## 相关接口

- [根据调度编号获取详细信息](0003-get-info.md) - 获取单条日志详情
- [导出定时任务调度日志列表](0002-export-job-log.md) - 导出日志为Excel
- [删除定时任务调度日志](0004-remove-job-log.md) - 删除日志

## 实现细节

- 使用 `ISysJobLogService.selectJobLogList()` 方法查询数据库
- 支持分页查询，使用 `startPage()` 方法设置分页参数
- 支持多条件组合查询
- 日志按创建时间倒序排列
- 包含任务的执行状态和异常信息

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 正常查询 | pageNum=1, pageSize=10 | 返回日志列表 | 返回日志列表 | 通过 |
| 按名称筛选 | jobName=同步任务 | 返回匹配的日志 | 返回匹配的日志 | 通过 |
| 按状态筛选 | status=0 | 返回正常状态的日志 | 返回正常状态的日志 | 通过 |
| 按时间范围筛选 | startTime=..., endTime=... | 返回时间范围内的日志 | 返回时间范围内的日志 | 通过 |
| 无权限访问 | 无权限用户 | 返回403错误 | 返回403错误 | 通过 |
