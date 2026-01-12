# 刷新对象注册表接口

## 接口信息

- **接口路径**: `/integration/realtime/refresh`
- **请求方法**: POST
- **功能描述**: 刷新对象注册表，获取最新的启用实时同步的对象列表
- **权限要求**: `integration:realtime:refresh`

## 请求参数

| 参数名 | 类型 | 位置 | 必选 | 描述 |
|--------|------|------|------|------|
| 无 | - | - | - | 无请求参数 |

## 响应参数

| 参数名 | 类型 | 描述 |
|--------|------|------|
| success | boolean | 请求是否成功 |
| message | string | 响应消息 |
| realtimeSyncObjects | array | 启用实时同步的对象列表 |
| objectCount | number | 启用实时同步的对象数量 |

### realtimeSyncObjects 数组元素

| 参数名 | 类型 | 描述 |
|--------|------|------|
| id | number | 对象ID |
| api | string | 对象API名称 |
| label | string | 对象显示名称 |
| isRealtimeSync | boolean | 是否开启实时同步 |
| lastSyncDate | string | 最后同步时间 |
| totalRows | number | 本地记录数 |

## 响应示例

```json
{
  "success": true,
  "message": "对象注册表刷新成功",
  "realtimeSyncObjects": [
    {
      "id": 1,
      "api": "Account",
      "label": "账户",
      "isRealtimeSync": true,
      "lastSyncDate": "2026-01-10T10:00:00",
      "totalRows": 1000
    },
    {
      "id": 2,
      "api": "Contact",
      "label": "联系人",
      "isRealtimeSync": true,
      "lastSyncDate": "2026-01-10T10:05:00",
      "totalRows": 2000
    }
  ],
  "objectCount": 2
}
```

## 错误示例

```json
{
  "success": false,
  "message": "对象注册表刷新失败: 查询对象列表时发生异常"
}
```

## 接口说明

1. 该接口用于刷新对象注册表，获取最新的启用实时同步的对象列表
2. 刷新过程中会查询所有启用实时同步的对象，并更新注册表
3. 接口需要`integration:realtime:refresh`权限才能访问
4. 如果没有权限或发生其他错误，接口会返回失败信息