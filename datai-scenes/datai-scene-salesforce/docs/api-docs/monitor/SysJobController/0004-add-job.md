# 接口文档 - 新增定时任务

## 接口信息

- **接口名称**: 新增定时任务
- **接口路径**: /monitor/job
- **请求方法**: POST
- **模块归属**: 定时任务管理
- **版本号**: v1.0
- **创建日期**: 2026-01-13
- **最后更新**: 2026-01-13

## 功能描述

创建新的定时任务，配置任务的执行参数、Cron表达式、调用目标等信息。创建成功后会自动在 Quartz 调度器中注册该任务。

## 请求参数

### 请求体 (JSON)

```json
{
  "jobName": "同步任务",
  "jobGroup": "DEFAULT",
  "invokeTarget": "syncTask.execute()",
  "cronExpression": "0/30 * * * * ?",
  "misfirePolicy": "1",
  "concurrent": "1",
  "status": "0",
  "remark": "定时同步数据"
}
```

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| jobName | String | 是 | 任务名称 | 同步任务 |
| jobGroup | String | 是 | 任务组名 | DEFAULT |
| invokeTarget | String | 是 | 调用目标字符串（Spring Bean名称.方法名） | syncTask.execute() |
| cronExpression | String | 是 | Cron表达式 | 0/30 * * * * ? |
| misfirePolicy | String | 否 | 错过执行策略（1立即执行 2执行一次 3放弃执行） | 1 |
| concurrent | String | 否 | 是否并发执行（0禁止 1允许） | 1 |
| status | String | 否 | 任务状态（0正常 1暂停） | 0 |
| remark | String | 否 | 备注 | 定时同步数据 |

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
  "msg": "新增任务'同步任务'失败，Cron表达式不正确",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 500 | Cron表达式不正确 | 提供的Cron表达式格式错误 |
| 500 | 目标字符串不允许'rmi'调用 | 调用目标包含RMI协议 |
| 500 | 目标字符串不允许'ldap(s)'调用 | 调用目标包含LDAP协议 |
| 500 | 目标字符串不允许'http(s)'调用 | 调用目标包含HTTP协议 |
| 500 | 目标字符串存在违规 | 调用目标包含非法字符 |
| 500 | 目标字符串不在白名单内 | 调用目标未在白名单中 |
| 403 | 没有权限访问 | 当前用户没有 monitor:job:add 权限 |

## 接口示例

### 请求示例

```bash
curl -X POST "http://localhost:8080/monitor/job" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -d '{
    "jobName": "同步任务",
    "jobGroup": "DEFAULT",
    "invokeTarget": "syncTask.execute()",
    "cronExpression": "0/30 * * * * ?",
    "misfirePolicy": "1",
    "concurrent": "1",
    "status": "0",
    "remark": "定时同步数据"
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
  "msg": "新增任务'同步任务'失败，Cron表达式不正确",
  "data": null
}
```

## 错误处理

- Cron表达式格式错误时，返回 500 错误
- 调用目标包含危险协议（RMI、LDAP、HTTP）时，返回 500 错误
- 调用目标不在白名单时，返回 500 错误
- 当用户没有 `monitor:job:add` 权限时，返回 403 错误
- 创建任务失败时，返回详细的错误信息

## 注意事项

- 需要登录认证
- 需要 `monitor:job:add` 权限
- Cron表达式必须符合Quartz规范
- 调用目标必须是Spring Bean的方法调用
- 调用目标会进行严格的安全校验
- 禁止使用RMI、LDAP、HTTP等危险协议
- 调用目标必须在白名单中
- 创建操作会被记录在系统日志中

## 相关接口

- [查询定时任务列表](0001-job-list.md) - 查询任务列表
- [修改定时任务](0005-edit-job.md) - 更新定时任务信息
- [修改定时任务状态](0006-change-status.md) - 修改任务状态

## 实现细节

- 使用 `CronUtils.isValid()` 验证Cron表达式
- 使用 `StringUtils.containsIgnoreCase()` 检测危险协议
- 使用 `ScheduleUtils.whiteList()` 验证调用目标白名单
- 通过 `ISysJobService.insertJob()` 方法保存任务
- 自动记录创建者信息
- 创建成功后在Quartz调度器中注册任务

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 正常创建 | 完整参数 | 创建成功 | 创建成功 | 通过 |
| Cron表达式错误 | cronExpression=invalid | 返回500错误 | 返回500错误 | 通过 |
| 包含RMI协议 | invokeTarget=rmi://... | 返回500错误 | 返回500错误 | 通过 |
| 包含HTTP协议 | invokeTarget=http://... | 返回500错误 | 返回500错误 | 通过 |
| 不在白名单 | invokeTarget=unknown.method() | 返回500错误 | 返回500错误 | 通过 |
| 无权限访问 | 无权限用户 | 返回403错误 | 返回403错误 | 通过 |
