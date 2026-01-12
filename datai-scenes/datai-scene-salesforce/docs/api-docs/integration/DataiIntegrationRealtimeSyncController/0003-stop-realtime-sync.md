# 停止实时同步服务接口

## 接口信息

- **接口路径**: `/integration/realtime/stop`
- **请求方法**: POST
- **功能描述**: 停止实时同步服务，停止订阅Salesforce Change Events
- **权限要求**: `integration:realtime:stop`

## 请求参数

| 参数名 | 类型 | 位置 | 必选 | 描述 |
|--------|------|------|------|------|
| 无 | - | - | - | 无请求参数 |

## 响应参数

| 参数名 | 类型 | 描述 |
|--------|------|------|
| success | boolean | 请求是否成功 |
| message | string | 响应消息 |

## 响应示例

```json
{
  "success": true,
  "message": "实时同步服务停止成功"
}
```

## 错误示例

```json
{
  "success": false,
  "message": "实时同步服务停止失败: 服务未启动"
}
```

## 接口说明

1. 该接口用于停止实时同步服务，停止订阅Salesforce Change Events
2. 停止过程中会取消所有事件订阅，并关闭Salesforce连接
3. 接口需要`integration:realtime:stop`权限才能访问
4. 如果没有权限或发生其他错误，接口会返回失败信息