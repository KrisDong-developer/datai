# 停止实时同步服务

## 接口概述

停止 Salesforce 实时同步服务，不再处理从 Salesforce 接收到的实时变更事件。

## 接口详情

- **接口名称**: 停止实时同步服务
- **接口路径**: `/integration/realtime/service/stop/service`
- **请求方法**: `POST`
- **权限标识**: `integration:realtime:stop:service`
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
  "message": "实时同步服务停止成功",
  "started": false
}
```

### 失败响应

```json
{
  "success": false,
  "message": "实时同步服务停止失败: 服务异常",
  "started": true
}
```

## 错误码说明

| 错误码 | 描述 | 解决方案 |
|--------|------|----------|
| 401 | 未授权，token 无效或已过期 | 重新登录获取有效 token |
| 403 | 无权限访问该接口 | 联系管理员分配相应权限 |
| 500 | 服务器内部错误 | 检查服务器日志，联系技术支持 |

## 注意事项

1. 停止操作不会影响已接收但尚未处理的事件，这些事件将继续被处理
2. 停止操作不会影响事件订阅服务的运行状态
3. 如果实时同步服务未在运行，再次调用此接口不会产生副作用
4. 停止成功后，系统将不再处理来自 Salesforce 的实时变更事件
5. 停止操作是幂等的，可以安全地重复执行
6. 建议在以下场景停止实时同步服务：
   - 系统维护期间
   - 需要暂停实时同步功能时
   - 配置更新前需要先停止服务
   - 故障排查或性能调优时
   - 数据库维护或迁移时
7. 停止实时同步服务后，如需重新启动，请使用启动实时同步服务接口
8. 停止服务后，统计信息仍可查询，但不会继续更新

## 相关接口

- [获取服务状态](./0001-get-status.md)
- [启动实时同步服务](./0011-start-service.md)
- [重启实时同步服务](./0013-restart-service.md)
- [停止事件订阅](./0010-stop-subscriber.md)
