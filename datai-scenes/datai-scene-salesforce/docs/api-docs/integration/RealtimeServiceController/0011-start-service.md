# 启动实时同步服务

## 接口概述

启动 Salesforce 实时同步服务，开始处理从 Salesforce 接收到的实时变更事件，并将数据同步到本地系统。

## 接口详情

- **接口名称**: 启动实时同步服务
- **接口路径**: `/integration/realtime/service/start/service`
- **请求方法**: `POST`
- **权限标识**: `integration:realtime:start:service`
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
  "message": "实时同步服务启动成功",
  "started": true
}
```

### 失败响应

```json
{
  "success": false,
  "message": "实时同步服务启动失败: 初始化失败",
  "started": false
}
```

## 错误码说明

| 错误码 | 描述 | 解决方案 |
|--------|------|----------|
| 401 | 未授权，token 无效或已过期 | 重新登录获取有效 token |
| 403 | 无权限访问该接口 | 联系管理员分配相应权限 |
| 409 | 实时同步服务已在运行 | 无需重复启动，或先停止再启动 |
| 500 | 服务器内部错误 | 检查服务器日志，联系技术支持 |

## 注意事项

1. 启动前请确保以下组件已就绪：
   - 对象注册表已初始化
   - 事件订阅服务已启动
   - 数据同步器已正确配置
2. 启动前请确保数据库连接正常，有足够的存储空间
3. 如果实时同步服务已在运行，再次调用此接口不会产生副作用
4. 启动成功后，服务将开始处理以下任务：
   - 接收来自事件订阅器的变更事件
   - 解析事件数据
   - 调用数据同步器执行数据同步
   - 记录处理统计信息
5. 建议在以下场景启动实时同步服务：
   - 系统初始化完成后
   - 事件订阅服务启动后
   - 实时同步服务停止后需要重新启动时
   - 配置更新后需要重新加载时
6. 启动操作是幂等的，可以安全地重复执行
7. 启动过程中会自动初始化事件处理器和数据同步器

## 相关接口

- [获取服务状态](./0001-get-status.md)
- [停止实时同步服务](./0012-stop-service.md)
- [重启实时同步服务](./0013-restart-service.md)
- [启动事件订阅](./0009-start-subscriber.md)
