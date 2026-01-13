# 启动事件订阅

## 接口概述

启动 Salesforce Pub/Sub 事件订阅服务，开始接收来自 Salesforce 的实时变更事件。

## 接口详情

- **接口名称**: 启动事件订阅
- **接口路径**: `/integration/realtime/service/start/subscriber`
- **请求方法**: `POST`
- **权限标识**: `integration:realtime:start:subscriber`
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
  "subscribed": boolean
}
```

### 字段说明

| 字段名 | 类型 | 描述 |
|--------|------|------|
| success | Boolean | 请求是否成功，true-成功，false-失败 |
| message | String | 响应消息，描述操作结果 |
| subscribed | Boolean | 订阅状态，true-已订阅，false-未订阅 |

## 响应示例

### 成功响应

```json
{
  "success": true,
  "message": "事件订阅启动成功",
  "subscribed": true
}
```

### 失败响应

```json
{
  "success": false,
  "message": "事件订阅启动失败: 认证失败",
  "subscribed": false
}
```

## 错误码说明

| 错误码 | 描述 | 解决方案 |
|--------|------|----------|
| 401 | 未授权，token 无效或已过期 | 重新登录获取有效 token |
| 403 | 无权限访问该接口 | 联系管理员分配相应权限 |
| 409 | 订阅服务已在运行 | 无需重复启动，或先停止再启动 |
| 500 | 服务器内部错误 | 检查服务器日志，联系技术支持 |

## 注意事项

1. 启动前请确保对象注册表已初始化，否则可能无法订阅到任何事件
2. 启动前请确保 Salesforce 配置正确，包括：
   - Consumer Key 和 Consumer Secret 已配置
   - Access Token 有效
   - Pub/Sub API 连接配置正确
3. 如果订阅服务已在运行，再次调用此接口不会产生副作用
4. 启动成功后，系统会开始接收和处理来自 Salesforce 的实时变更事件
5. 订阅服务启动后会自动处理以下事件类型：
   - Create（创建）
   - Update（更新）
   - Delete（删除）
   - Undelete（恢复删除）
6. 建议在以下场景启动订阅服务：
   - 系统初始化完成后
   - 对象注册表更新后
   - 订阅服务停止后需要重新启动时
7. 启动操作是幂等的，可以安全地重复执行

## 相关接口

- [获取服务状态](./0001-get-status.md)
- [停止事件订阅](./0010-stop-subscriber.md)
- [初始化对象注册表](./0008-init-registry.md)
