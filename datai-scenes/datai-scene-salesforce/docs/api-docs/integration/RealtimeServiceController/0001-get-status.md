# 获取实时服务状态

## 接口概述

获取 Salesforce 实时同步服务的当前运行状态，包括对象注册表、事件订阅和实时同步服务的状态信息。

## 接口详情

- **接口名称**: 获取实时服务状态
- **接口路径**: `/integration/realtime/service/status`
- **请求方法**: `GET`
- **权限标识**: `integration:realtime:status`
- **接口标签**: 【实时服务管理】统一管理

## 请求参数

### Headers

| 参数名 | 类型 | 必填 | 描述 |
|--------|------|------|------|
| Authorization | String | 是 | 认证令牌，格式：Bearer {token} |

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
  "status": {
    "objectRegistryInitialized": boolean,
    "registeredObjectCount": number,
    "eventSubscriberStarted": boolean,
    "realtimeSyncServiceStarted": boolean
  }
}
```

### 字段说明

| 字段名 | 类型 | 描述 |
|--------|------|------|
| success | Boolean | 请求是否成功，true-成功，false-失败 |
| message | String | 响应消息，描述操作结果 |
| status | Object | 实时服务状态对象 |
| status.objectRegistryInitialized | Boolean | 对象注册表是否已初始化 |
| status.registeredObjectCount | Number | 已注册的对象数量 |
| status.eventSubscriberStarted | Boolean | 事件订阅是否已启动 |
| status.realtimeSyncServiceStarted | Boolean | 实时同步服务是否已启动 |

## 响应示例

### 成功响应

```json
{
  "success": true,
  "message": "获取实时服务状态成功",
  "status": {
    "objectRegistryInitialized": true,
    "registeredObjectCount": 15,
    "eventSubscriberStarted": true,
    "realtimeSyncServiceStarted": true
  }
}
```

### 失败响应

```json
{
  "success": false,
  "message": "获取实时服务状态失败: 系统异常"
}
```

## 错误码说明

| 错误码 | 描述 | 解决方案 |
|--------|------|----------|
| 401 | 未授权，token 无效或已过期 | 重新登录获取有效 token |
| 403 | 无权限访问该接口 | 联系管理员分配相应权限 |
| 500 | 服务器内部错误 | 检查服务器日志，联系技术支持 |

## 注意事项

1. 该接口仅用于查询状态，不会对服务进行任何修改
2. 建议在启动或停止服务后调用此接口确认状态
3. 如果所有服务都未启动，需要先调用初始化和启动接口
4. 对象注册表初始化后，registeredObjectCount 应大于 0
5. 事件订阅和实时同步服务的启动顺序建议：先初始化对象注册表，再启动事件订阅，最后启动实时同步服务

## 相关接口

- [初始化对象注册表](./0008-init-registry.md)
- [启动事件订阅](./0009-start-subscriber.md)
- [启动实时同步服务](./0011-start-service.md)
- [一键启动所有实时服务](./0013-start-all.md)
