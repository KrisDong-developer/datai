# 一键启动所有实时服务

## 接口概述

一键启动所有 Salesforce 实时服务，包括对象注册表初始化、事件订阅服务和实时同步服务，按顺序依次启动。

## 接口详情

- **接口名称**: 一键启动所有实时服务
- **接口路径**: `/integration/realtime/service/start/all`
- **请求方法**: `POST`
- **权限标识**: `integration:realtime:start:all`
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
  "details": {
    "objectRegistry": String,
    "eventSubscriber": String,
    "realtimeSyncService": String
  }
}
```

### 字段说明

| 字段名 | 类型 | 描述 |
|--------|------|------|
| success | Boolean | 请求是否成功，true-成功，false-失败 |
| message | String | 响应消息，描述操作结果 |
| details | Object | 各服务启动详情 |
| details.objectRegistry | String | 对象注册表初始化详情 |
| details.eventSubscriber | String | 事件订阅服务启动详情 |
| details.realtimeSyncService | String | 实时同步服务启动详情 |

## 响应示例

### 成功响应

```json
{
  "success": true,
  "message": "所有实时服务启动成功",
  "details": {
    "objectRegistry": "初始化成功，注册对象数: 15",
    "eventSubscriber": "启动成功，订阅状态: true",
    "realtimeSyncService": "启动成功，运行状态: true"
  }
}
```

### 失败响应

```json
{
  "success": false,
  "message": "启动所有实时服务失败: 认证失败",
  "details": {
    "objectRegistry": "初始化成功，注册对象数: 15",
    "eventSubscriber": "启动失败",
    "realtimeSyncService": "未启动"
  }
}
```

## 错误码说明

| 错误码 | 描述 | 解决方案 |
|--------|------|----------|
| 401 | 未授权，token 无效或已过期 | 重新登录获取有效 token |
| 403 | 无权限访问该接口 | 联系管理员分配相应权限 |
| 500 | 服务器内部错误 | 检查服务器日志，联系技术支持 |

## 注意事项

1. 启动顺序为：对象注册表 → 事件订阅服务 → 实时同步服务
2. 如果某个服务启动失败，后续服务将不会启动
3. 启动前请确保以下配置正确：
   - Salesforce Consumer Key 和 Consumer Secret 已配置
   - Access Token 有效
   - Pub/Sub API 连接配置正确
   - 数据库连接正常
4. 如果服务已在运行，再次调用此接口会重新初始化并启动所有服务
5. 启动成功后，系统将开始接收和处理来自 Salesforce 的实时变更事件
6. 建议在以下场景使用一键启动：
   - 系统首次启动
   - 系统维护后重新启动
   - 配置更新后需要重新加载
   - 所有服务停止后需要重新启动
7. 一键启动操作是幂等的，可以安全地重复执行
8. 启动过程中会返回每个服务的详细状态，便于排查问题

## 相关接口

- [获取服务状态](./0001-get-status.md)
- [一键停止所有实时服务](./0015-stop-all.md)
- [初始化对象注册表](./0008-init-registry.md)
- [启动事件订阅](./0009-start-subscriber.md)
- [启动实时同步服务](./0011-start-service.md)
