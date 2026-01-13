# 重启实时同步服务

## 接口概述

重启 Salesforce 实时同步服务，先停止当前运行的实时同步服务，然后重新启动，用于应用配置更新或恢复服务正常运行。

## 接口详情

- **接口名称**: 重启实时同步服务
- **接口路径**: `/integration/realtime/service/restart/service`
- **请求方法**: `POST`
- **权限标识**: `integration:realtime:restart:service`
- **接口标签**: 【实时服务管理】统一管理

## 请求参数

### Headers

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| Authorization | String | 是 | 认证令牌，格式：Bearer {token} |
| Content-Type | String | 是 | 请求内容类型，固定值：application/json |

### Query Parameters

无

### Body Parameters

无

## 响应参数

### 响应结构

```json
{
  "success": boolean,
  "message": String,
  "started": boolean
}
```

### 字段说明

| 字段名 | 类型 | 描述 |
|--------|------|------|
| success | Boolean | 请求是否成功，true-成功，false-失败 |
| message | String | 响应消息，描述操作结果 |
| started | Boolean | 服务状态，true-已启动，false-未启动 |

## 响应示例

### 成功响应

```json
{
  "success": true,
  "message": "实时同步服务重启成功",
  "started": true
}
```

### 失败响应

```json
{
  "success": false,
  "message": "实时同步服务重启失败: 启动失败",
  "started": false
}
```

## 错误码说明

| 错误码 | 描述 | 解决方案 |
|--------|------|----------|
| 401 | 未授权，token 无效或已过期 | 重新登录获取有效 token |
| 403 | 无权限访问该接口 | 联系管理员分配相应权限 |
| 500 | 服务器内部错误 | 检查服务器日志，联系技术支持 |

## 注意事项

1. 重启操作会先停止当前运行的实时同步服务，然后重新启动
2. 重启过程中，已接收但尚未处理的事件将继续被处理
3. 重启操作不会影响事件订阅服务的运行状态
4. 如果实时同步服务未在运行，重启操作将直接启动服务
5. 重启成功后，服务将使用最新的配置重新初始化
6. 重启操作是幂等的，可以安全地重复执行
7. 建议在以下场景重启实时同步服务：
   - 修改了实时同步配置后
   - 服务出现异常需要恢复时
   - 更新了事件处理器或数据同步器的实现后
   - 需要重新加载配置或缓存时
   - 性能调优或故障排查后
8. 重启操作会保留统计信息，不会重置计数器
9. 如需重置统计信息，请使用重置统计信息接口

## 相关接口

- [获取服务状态](./0001-get-status.md)
- [启动实时同步服务](./0011-start-service.md)
- [停止实时同步服务](./0012-stop-service.md)
- [一键启动所有实时服务](./0014-start-all.md)
