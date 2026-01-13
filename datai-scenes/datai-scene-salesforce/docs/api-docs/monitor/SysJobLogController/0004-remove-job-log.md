# 删除定时任务调度日志

## 接口信息

- **接口名称**: 删除定时任务调度日志
- **接口路径**: /monitor/jobLog/{jobLogIds}
- **请求方法**: DELETE
- **模块归属**: 定时任务日志管理
- **版本号**: v1.0
- **创建日期**: 2026-01-13
- **最后更新**: 2026-01-13

## 功能描述

根据调度日志ID批量删除定时任务调度日志。该接口用于清理不再需要的任务执行记录，支持批量删除操作。

## 权限要求

- **权限标识**: monitor:job:remove
- **权限说明**: 需要定时任务删除权限

## 请求参数

### 路径参数

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| jobLogIds | Long[] | 是 | 调度日志ID数组，支持批量删除 | 1,2,3 |

### 请求示例

**删除单条日志:**
```
DELETE /monitor/jobLog/1
```

**批量删除多条日志:**
```
DELETE /monitor/jobLog/1,2,3
```

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": null
}
```

### 失败响应

**HTTP 状态码**: 401 Unauthorized

```json
{
  "code": 401,
  "msg": "您没有权限执行该操作",
  "data": null
}
```

**HTTP 状态码**: 500 Internal Server Error

```json
{
  "code": 500,
  "msg": "删除失败",
  "data": null
}
```

## 响应参数说明

| 参数名 | 类型 | 描述 |
|--------|------|------|
| code | Integer | 响应状态码，200表示成功 |
| msg | String | 响应消息 |
| data | Object | 响应数据，删除操作通常为null |

## 错误码说明

| 错误码 | 说明 | 处理建议 |
|--------|------|----------|
| 200 | 删除成功 | 无需处理 |
| 401 | 未授权 | 检查用户权限 |
| 500 | 服务器内部错误 | 联系管理员或检查日志 |

## 业务规则

1. 支持批量删除，多个ID用逗号分隔
2. 删除操作不可恢复，请谨慎操作
3. 删除操作会记录操作日志
4. 删除前建议先确认日志ID是否正确

## 实现细节

### 核心代码

```java
@PreAuthorize("@ss.hasPermi('monitor:job:remove')")
@Log(title = "定时任务调度日志", businessType = BusinessType.DELETE)
@DeleteMapping("/{jobLogIds}")
public AjaxResult remove(@PathVariable(name = "jobLogIds") Long[] jobLogIds) 
{
    return toAjax(jobLogService.deleteJobLogByIds(jobLogIds));
}
```

### 业务逻辑

1. **权限验证**: 使用 `@PreAuthorize` 注解验证用户是否具有 `monitor:job:remove` 权限
2. **日志记录**: 使用 `@Log` 注解记录删除操作，业务类型为 `BusinessType.DELETE`
3. **参数解析**: 从路径参数中解析日志ID数组
4. **删除操作**: 调用 `ISysJobLogService.deleteJobLogByIds()` 方法执行删除
5. **结果返回**: 使用 `toAjax()` 方法返回操作结果

### 相关服务

- **ISysJobLogService**: 定时任务日志服务接口
  - `deleteJobLogByIds(Long[] jobLogIds)`: 根据日志ID数组批量删除日志

## 使用场景

1. **日志清理**: 定期清理历史日志数据，释放存储空间
2. **错误日志删除**: 删除执行失败的日志记录
3. **测试数据清理**: 清理测试环境中的日志数据
4. **批量管理**: 一次性删除多条不需要的日志记录

## 注意事项

1. 删除操作不可逆，请谨慎操作
2. 建议在删除前先查询确认日志内容
3. 批量删除时注意ID数量，避免一次性删除过多数据
4. 删除操作会记录在系统日志中，便于审计追踪

## 相关接口

- [查询定时任务调度日志列表](./0001-job-log-list.md)
- [导出定时任务调度日志](./0002-export-job-log.md)
- [根据调度编号获取详细信息](./0003-get-info.md)
- [清空定时任务调度日志](./0005-clean-job-log.md)

## 更新记录

| 版本号 | 更新日期 | 更新内容 | 更新人 |
|--------|----------|----------|--------|
| v1.0 | 2026-01-13 | 初始版本创建 | AI Assistant |
