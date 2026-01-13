# 接口文档 - 修改定时任务状态

## 接口信息

- **接口名称**: 修改定时任务状态
- **接口路径**: /monitor/job/changeStatus
- **请求方法**: PUT
- **模块归属**: 定时任务管理
- **版本号**: v1.0
- **创建日期**: 2026-01-13
- **最后更新**: 2026-01-13

## 功能描述

修改定时任务的执行状态，可以暂停或恢复任务的执行。状态修改后会立即在 Quartz 调度器中生效。

## 请求参数

### 请求体 (JSON)

```json
{
  "jobId": 1,
  "status": "0"
}
```

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| jobId | Long | 是 | 任务ID | 1 |
| status | String | 是 | 任务状态（0正常 1暂停） | 0 |

**status 参数说明**:
- `0`: 正常状态，任务会按照Cron表达式正常执行
- `1`: 暂停状态，任务不会执行

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": 1
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| code | Integer | 响应码 | 200 |
| msg | String | 响应消息 | 操作成功 |
| data | Integer | 影响行数 | 1 |

### 失败响应

**HTTP 状态码**: 500 Internal Server Error

```json
{
  "code": 500,
  "msg": "任务不存在",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 500 | 任务不存在 | 指定的任务ID不存在 |
| 403 | 没有权限访问 | 当前用户没有 monitor:job:changeStatus 权限 |

## 接口示例

### 请求示例

```bash
# 恢复任务执行
curl -X PUT "http://localhost:8080/monitor/job/changeStatus" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -d '{
    "jobId": 1,
    "status": "0"
  }'

# 暂停任务执行
curl -X PUT "http://localhost:8080/monitor/job/changeStatus" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -d '{
    "jobId": 1,
    "status": "1"
  }'
```

### 响应示例

**成功**:

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": 1
}
```

**失败**:

```json
{
  "code": 500,
  "msg": "任务不存在",
  "data": null
}
```

## 错误处理

- 当任务ID不存在时，返回 500 错误
- 当用户没有 `monitor:job:changeStatus` 权限时，返回 403 错误
- 状态值必须是 "0" 或 "1"
- 状态修改操作会被记录在系统日志中

## 注意事项

- 需要登录认证
- 需要 `monitor:job:changeStatus` 权限
- jobId 必须是有效的任务ID
- status 只能是 "0"（正常）或 "1"（暂停）
- 状态修改会立即生效
- 暂停任务不会删除任务，只是停止执行
- 恢复任务会立即按照Cron表达式开始执行
- 修改操作会被记录在系统日志中

## 相关接口

- [查询定时任务列表](0001-job-list.md) - 查询任务列表
- [获取定时任务详细信息](0003-get-info.md) - 获取任务详情
- [立即执行一次](0007-run-job.md) - 立即执行任务

## 实现细节

- 通过 `ISysJobService.selectJobById()` 方法查询任务信息
- 通过 `ISysJobService.changeStatus()` 方法修改任务状态
- 状态修改后会立即同步到Quartz调度器
- 暂停任务时会停止Quartz中的Job
- 恢复任务时会恢复Quartz中的Job
- 使用 `@Log` 注解记录状态修改操作

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 恢复任务 | status=0 | 任务恢复执行 | 任务恢复执行 | 通过 |
| 暂停任务 | status=1 | 任务暂停执行 | 任务暂停执行 | 通过 |
| 任务不存在 | jobId=9999 | 返回500错误 | 返回500错误 | 通过 |
| 无效状态 | status=2 | 返回500错误 | 返回500错误 | 通过 |
| 无权限访问 | 无权限用户 | 返回403错误 | 返回403错误 | 通过 |
