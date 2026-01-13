# 接口文档 - 获取定时任务详细信息

## 接口信息

- **接口名称**: 获取定时任务详细信息
- **接口路径**: /monitor/job/{jobId}
- **请求方法**: GET
- **模块归属**: 定时任务管理
- **版本号**: v1.0
- **创建日期**: 2026-01-13
- **最后更新**: 2026-01-13

## 功能描述

根据任务ID获取定时任务的详细信息，包括任务配置、执行参数、状态等完整信息。该接口用于在编辑或查看任务详情时使用。

## 请求参数

### 路径参数

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| jobId | Long | 是 | 任务ID | 1 |

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "jobId": 1,
    "jobName": "同步任务",
    "jobGroup": "DEFAULT",
    "invokeTarget": "syncTask.execute()",
    "cronExpression": "0/30 * * * * ?",
    "misfirePolicy": "1",
    "concurrent": "1",
    "status": "0",
    "createBy": "admin",
    "createTime": "2026-01-01 10:00:00",
    "updateBy": "admin",
    "updateTime": "2026-01-10 15:30:00",
    "remark": "定时同步数据"
  }
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| jobId | Long | 任务ID | 1 |
| jobName | String | 任务名称 | 同步任务 |
| jobGroup | String | 任务组名 | DEFAULT |
| invokeTarget | String | 调用目标字符串 | syncTask.execute() |
| cronExpression | String | Cron表达式 | 0/30 * * * * ? |
| misfirePolicy | String | 错过执行策略（1立即执行 2执行一次 3放弃执行） | 1 |
| concurrent | String | 是否并发执行（0禁止 1允许） | 1 |
| status | String | 任务状态（0正常 1暂停） | 0 |
| createBy | String | 创建者 | admin |
| createTime | String | 创建时间 | 2026-01-01 10:00:00 |
| updateBy | String | 更新者 | admin |
| updateTime | String | 更新时间 | 2026-01-10 15:30:00 |
| remark | String | 备注 | 定时同步数据 |
| code | Integer | 响应码 | 200 |
| msg | String | 响应消息 | 查询成功 |

### 失败响应

**HTTP 状态码**: 404 Not Found

```json
{
  "code": 404,
  "msg": "任务不存在",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 404 | 任务不存在 | 指定的任务ID不存在 |
| 403 | 没有权限访问 | 当前用户没有 monitor:job:query 权限 |

## 接口示例

### 请求示例

```bash
curl -X GET "http://localhost:8080/monitor/job/1" \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

### 响应示例

**成功**:

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "jobId": 1,
    "jobName": "同步任务",
    "jobGroup": "DEFAULT",
    "invokeTarget": "syncTask.execute()",
    "cronExpression": "0/30 * * * * ?",
    "misfirePolicy": "1",
    "concurrent": "1",
    "status": "0",
    "createBy": "admin",
    "createTime": "2026-01-01 10:00:00",
    "updateBy": "admin",
    "updateTime": "2026-01-10 15:30:00",
    "remark": "定时同步数据"
  }
}
```

**失败**:

```json
{
  "code": 404,
  "msg": "任务不存在",
  "data": null
}
```

## 错误处理

- 当任务ID不存在时，返回 404 错误
- 当用户没有 `monitor:job:query` 权限时，返回 403 错误
- jobId 参数必须为有效的数字

## 注意事项

- 需要登录认证
- 需要 `monitor:job:query` 权限
- jobId 必须是有效的任务ID
- 返回的数据包含任务的所有配置信息

## 相关接口

- [查询定时任务列表](0001-job-list.md) - 查询任务列表
- [修改定时任务](0005-edit-job.md) - 更新定时任务信息
- [修改定时任务状态](0006-change-status.md) - 修改任务状态

## 实现细节

- 通过 `ISysJobService.selectJobById()` 方法查询数据库
- 返回完整的任务对象信息
- 包含创建和更新时间戳
- 支持查询所有状态的任务

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 正常查询 | jobId=1 | 返回任务详细信息 | 返回任务详细信息 | 通过 |
| 查询不存在的任务 | jobId=9999 | 返回404错误 | 返回404错误 | 通过 |
| 无权限访问 | 无权限用户 | 返回403错误 | 返回403错误 | 通过 |
