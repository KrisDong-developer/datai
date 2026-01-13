# 实时同步统计接口

## 接口信息

- **接口路径**: `/integration/realtime/statistics`
- **请求方法**: GET
- **功能描述**: 获取实时同步服务的详细统计信息，包括服务状态、对象统计、订阅统计等
- **权限要求**: `integration:realtime:statistics`

## 请求参数

| 参数名 | 类型 | 位置 | 必选 | 描述 |
|--------|------|------|------|------|
| 无 | - | - | - | 无请求参数 |

## 响应参数

| 参数名 | 类型 | 描述 |
|--------|------|------|
| success | boolean | 请求是否成功 |
| message | string | 响应消息 |
| data | object | 统计数据对象 |

### data 对象结构

#### serviceStatus - 服务状态

| 参数名 | 类型 | 描述 |
|--------|------|------|
| isRunning | boolean | 服务是否正在运行 |
| startTime | string/null | 服务启动时间，格式：yyyy-MM-dd HH:mm:ss，未启动时为null |
| runningDuration | string/null | 服务运行时长，如"2小时30分钟"、"30秒"，未启动时为null |

#### objectStatistics - 对象统计

| 参数名 | 类型 | 描述 |
|--------|------|------|
| totalCount | number | 启用实时同步的对象总数 |
| standardCount | number | 标准对象数量 |
| customCount | number | 自定义对象数量 |
| objects | array | 对象详细信息列表 |

#### objects 数组元素

| 参数名 | 类型 | 描述 |
|--------|------|------|
| objectApi | string | 对象API名称 |
| objectName | string | 对象显示名称 |
| isCustom | boolean | 是否为自定义对象 |

#### subscriptionStatistics - 订阅统计

| 参数名 | 类型 | 描述 |
|--------|------|------|
| isSubscribed | boolean | 是否正在订阅事件 |

## 响应示例

### 成功响应示例（服务运行中）

```json
{
  "success": true,
  "message": "获取实时同步统计信息成功",
  "data": {
    "serviceStatus": {
      "isRunning": true,
      "startTime": "2026-01-13 10:00:00",
      "runningDuration": "2小时30分钟"
    },
    "objectStatistics": {
      "totalCount": 15,
      "standardCount": 10,
      "customCount": 5,
      "objects": [
        {
          "objectApi": "Account",
          "objectName": "账户",
          "isCustom": false
        },
        {
          "objectApi": "Contact",
          "objectName": "联系人",
          "isCustom": false
        },
        {
          "objectApi": "CustomObject__c",
          "objectName": "自定义对象",
          "isCustom": true
        }
      ]
    },
    "subscriptionStatistics": {
      "isSubscribed": true
    }
  }
}
```

### 成功响应示例（服务未启动）

```json
{
  "success": true,
  "message": "获取实时同步统计信息成功",
  "data": {
    "serviceStatus": {
      "isRunning": false,
      "startTime": null,
      "runningDuration": null
    },
    "objectStatistics": {
      "totalCount": 15,
      "standardCount": 10,
      "customCount": 5,
      "objects": [
        {
          "objectApi": "Account",
          "objectName": "账户",
          "isCustom": false
        }
      ]
    },
    "subscriptionStatistics": {
      "isSubscribed": false
    }
  }
}
```

## 错误示例

### 权限不足

```json
{
  "success": false,
  "message": "获取实时同步统计信息失败: 权限不足"
}
```

### 系统异常

```json
{
  "success": false,
  "message": "获取实时同步统计信息失败: 系统异常"
}
```

## 接口说明

1. 该接口用于获取实时同步服务的综合统计信息，帮助监控和管理实时同步服务
2. 服务状态信息包括运行状态、启动时间和运行时长，便于了解服务运行情况
3. 对象统计信息包括启用实时同步的对象总数、标准对象和自定义对象的分类统计，以及详细的对象列表
4. 订阅统计信息反映当前事件订阅的状态
5. 服务未启动时，startTime 和 runningDuration 字段为 null
6. 接口需要 `integration:realtime:statistics` 权限才能访问
7. 该接口为只读接口，不会对系统状态产生任何影响

## 使用场景

1. **监控仪表板**：在系统监控仪表板上展示实时同步服务的运行状态和统计信息
2. **运维管理**：运维人员通过该接口了解服务运行情况，及时发现和解决问题
3. **容量规划**：根据对象统计信息进行容量规划和资源分配
4. **故障排查**：当服务出现问题时，通过统计信息快速定位问题

## 注意事项

1. 确保用户具有 `integration:realtime:statistics` 权限
2. 服务启动时间格式为 yyyy-MM-dd HH:mm:ss
3. 运行时长会根据实际运行时间动态计算，格式为"X小时Y分钟"或"X分钟Y秒"或"X秒"
4. 对象列表仅包含启用实时同步的对象，不包含所有已配置的对象
5. 该接口不会返回实时同步的详细日志，如需查看日志请使用日志查询接口

## 相关接口

- [实时同步服务状态接口](./0001-realtime-sync-status.md) - 获取实时同步服务状态
- [启动实时同步服务](./0002-start-realtime-sync.md) - 启动实时同步服务
- [停止实时同步服务](./0003-stop-realtime-sync.md) - 停止实时同步服务
- [重启实时同步服务](./0004-restart-realtime-sync.md) - 重启实时同步服务
