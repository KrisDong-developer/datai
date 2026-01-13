# 接口文档 - 查询定时任务列表

## 接口信息

- **接口名称**: 查询定时任务列表
- **接口路径**: /monitor/job/list
- **请求方法**: GET
- **模块归属**: 定时任务管理
- **版本号**: v1.0
- **创建日期**: 2026-01-13
- **最后更新**: 2026-01-13

## 功能描述

查询系统中的定时任务列表，支持分页查询和多条件筛选。该接口用于展示所有已配置的定时任务，包括任务名称、调用目标、Cron表达式、执行状态等信息。

## 请求参数

### 查询参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
| pageNum | Integer | 否 | 页码 | 1 | 1 |
| pageSize | Integer | 否 | 每页数量 | 10 | 10 |
| jobName | String | 否 | 任务名称 | 同步任务 | - |
| jobGroup | String | 否 | 任务组名 | DEFAULT | - |
| status | String | 否 | 任务状态（0正常 1暂停） | 0 | - |

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "total": 100,
  "rows": [
    {
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
      "remark": "定时同步数据"
    }
  ],
  "code": 200,
  "msg": "查询成功"
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| total | Long | 总记录数 | 100 |
| rows | Array | 任务列表 | - |
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
| remark | String | 备注 | 定时同步数据 |
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
curl -X GET "http://localhost:8080/monitor/job/list?pageNum=1&pageSize=10&jobName=同步任务" \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..."
```

### 响应示例

**成功**:

```json
{
  "total": 1,
  "rows": [
    {
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
      "remark": "定时同步数据"
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

## 注意事项

- 需要登录认证
- 需要 `monitor:job:list` 权限
- 支持分页查询，默认每页 10 条记录
- 支持按任务名称、任务组名、状态进行筛选

## 相关接口

- [获取定时任务详细信息](0003-get-info.md) - 根据任务ID获取详细信息
- [新增定时任务](0004-add-job.md) - 创建新的定时任务
- [修改定时任务](0005-edit-job.md) - 更新定时任务信息

## 实现细节

- 使用 Quartz 框架实现定时任务调度
- 通过 `ISysJobService.selectJobList()` 方法查询数据库
- 支持分页查询，使用 `startPage()` 方法设置分页参数
- 调用目标字符串会进行安全校验，防止恶意调用

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 正常查询 | pageNum=1, pageSize=10 | 返回任务列表 | 返回任务列表 | 通过 |
| 按名称筛选 | jobName=同步任务 | 返回匹配的任务 | 返回匹配的任务 | 通过 |
| 按状态筛选 | status=0 | 返回正常状态的任务 | 返回正常状态的任务 | 通过 |
| 无权限访问 | 无权限用户 | 返回403错误 | 返回403错误 | 通过 |
