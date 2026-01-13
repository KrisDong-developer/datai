# 接口文档 - 导出定时任务列表

## 接口信息

- **接口名称**: 导出定时任务列表
- **接口路径**: /monitor/job/export
- **请求方法**: POST
- **模块归属**: 定时任务管理
- **版本号**: v1.0
- **创建日期**: 2026-01-13
- **最后更新**: 2026-01-13

## 功能描述

将定时任务列表导出为 Excel 文件，支持按条件筛选导出。导出的文件包含任务的基本信息、执行配置、状态等详细数据。

## 请求参数

### 请求体 (JSON)

```json
{
  "jobName": "同步任务",
  "jobGroup": "DEFAULT",
  "status": "0"
}
```

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| jobName | String | 否 | 任务名称 | 同步任务 |
| jobGroup | String | 否 | 任务组名 | DEFAULT |
| status | String | 否 | 任务状态（0正常 1暂停） | 0 |

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

**Content-Type**: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet

**Content-Disposition**: attachment; filename=定时任务.xlsx

响应体为 Excel 文件的二进制数据。

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
| 403 | 没有权限访问 | 当前用户没有 monitor:job:export 权限 |

## 接口示例

### 请求示例

```bash
curl -X POST "http://localhost:8080/monitor/job/export" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer eyJhbGciOiJIUzUxMiJ9..." \
  -d '{
    "jobName": "同步任务",
    "status": "0"
  }' \
  --output 定时任务.xlsx
```

### 响应示例

**成功**:

响应为 Excel 文件下载，文件名为"定时任务.xlsx"。

**失败**:

```json
{
  "code": 403,
  "msg": "没有权限访问",
  "data": null
}
```

## 错误处理

- 当用户没有 `monitor:job:export` 权限时，返回 403 错误
- 查询结果为空时，导出空表格
- 导出失败时返回错误信息

## 注意事项

- 需要登录认证
- 需要 `monitor:job:export` 权限
- 导出文件格式为 Excel (.xlsx)
- 支持按条件筛选导出
- 导出操作会被记录在系统日志中

## 相关接口

- [查询定时任务列表](0001-job-list.md) - 查询任务列表
- [新增定时任务](0004-add-job.md) - 创建新的定时任务

## 实现细节

- 使用 `ExcelUtil` 工具类生成 Excel 文件
- 通过 `ISysJobService.selectJobList()` 方法查询数据
- 使用 `@Log` 注解记录导出操作
- 文件名固定为"定时任务.xlsx"
- 使用 `SysJob` 类的注解配置 Excel 列名和格式

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 导出全部数据 | 空参数 | 导出所有任务 | 导出所有任务 | 通过 |
| 按名称筛选导出 | jobName=同步任务 | 导出匹配的任务 | 导出匹配的任务 | 通过 |
| 按状态筛选导出 | status=0 | 导出正常状态的任务 | 导出正常状态的任务 | 通过 |
| 无权限访问 | 无权限用户 | 返回403错误 | 返回403错误 | 通过 |
