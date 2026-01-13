# 初始化对象注册表

## 接口概述

初始化或刷新对象注册表，从数据库加载所有需要实时同步的 Salesforce 对象配置，并注册到内存中供后续使用。

## 接口详情

- **接口名称**: 初始化对象注册表
- **接口路径**: `/integration/realtime/service/init/registry`
- **请求方法**: `POST`
- **权限标识**: `integration:realtime:init:registry`
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
  "registeredObjectCount": Integer
}
```

### 字段说明

| 字段名 | 类型 | 描述 |
|--------|------|------|
| success | Boolean | 请求是否成功，true-成功，false-失败 |
| message | String | 响应消息，描述操作结果 |
| registeredObjectCount | Integer | 已注册的对象数量 |

## 响应示例

### 成功响应

```json
{
  "success": true,
  "message": "对象注册表初始化成功",
  "registeredObjectCount": 15
}
```

### 失败响应

```json
{
  "success": false,
  "message": "对象注册表初始化失败: 数据库连接异常"
}
```

## 错误码说明

| 错误码 | 描述 | 解决方案 |
|--------|------|----------|
| 401 | 未授权，token 无效或已过期 | 重新登录获取有效 token |
| 403 | 无权限访问该接口 | 联系管理员分配相应权限 |
| 500 | 服务器内部错误 | 检查服务器日志，联系技术支持 |

## 注意事项

1. 初始化操作会清空当前注册表，然后从数据库重新加载所有对象配置
2. 初始化操作不会影响正在运行的事件订阅服务
3. 如果数据库中配置的对象发生变化（新增、修改、删除），建议执行此操作以同步最新配置
4. 初始化成功后，会返回已注册的对象数量，可用于验证配置是否正确加载
5. 建议在以下场景执行初始化操作：
   - 系统首次启动
   - 修改了 Salesforce 对象同步配置
   - 新增或删除了需要实时同步的对象
   - 怀疑对象注册表数据不一致时
6. 初始化操作是幂等的，可以安全地重复执行

## 相关接口

- [获取服务状态](./0001-get-status.md)
- [启动事件订阅](./0009-start-subscriber.md)
- [停止事件订阅](./0010-stop-subscriber.md)
