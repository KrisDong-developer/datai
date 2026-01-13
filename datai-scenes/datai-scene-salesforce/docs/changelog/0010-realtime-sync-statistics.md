# Changelog - 实时同步统计接口

## 变更信息

- **版本号**: v0.1.10
- **发布日期**: 2026-01-13
- **变更类型**: 特性更新

## 变更摘要

本次变更新增了实时同步统计接口，用于获取实时同步服务的详细统计信息。统计信息包括服务状态、对象统计、订阅统计等多个维度，为系统监控和运维提供了全面的数据支持。

## 详细变更

### 特性更新

- **新增实时同步统计接口** - 在 RealtimeSyncService 中新增 getStatistics 方法：
  - 服务状态统计：包括运行状态、启动时间、运行时长
  - 对象统计：包括启用实时同步的对象总数、标准对象数量、自定义对象数量、对象详细信息列表
  - 订阅统计：包括订阅状态

- **新增 Controller 接口** - 在 DataiIntegrationRealtimeSyncController 中新增统计接口：
  - 接口路径：GET /integration/realtime/statistics
  - 权限要求：integration:realtime:statistics
  - 返回数据：包含服务状态、对象统计、订阅统计的综合信息

- **服务启动时间记录** - 在 RealtimeSyncServiceImpl 中新增启动时间记录：
  - 服务启动时记录启动时间
  - 服务停止时清空启动时间
  - 用于计算服务运行时长

## 影响范围

### 受影响的模块

- **datai-salesforce-integration** - 以下类被修改：
  - RealtimeSyncService - 新增 getStatistics 方法接口
  - RealtimeSyncServiceImpl - 实现 getStatistics 方法，新增 startTime 字段
  - DataiIntegrationRealtimeSyncController - 新增 getStatistics 接口方法

### 兼容性说明

- 本次变更向后兼容，不影响现有功能
- 新增接口为可选功能，不影响现有接口的使用

## 升级指南

### 升级步骤

1. 部署新的代码版本

2. 使用新的统计接口获取实时同步服务信息：
   ```
   GET /integration/realtime/statistics
   ```

### API 接口说明

#### 获取实时同步统计信息
- **接口**: GET /integration/realtime/statistics
- **权限**: integration:realtime:statistics
- **返回**: 实时同步统计信息

#### 返回数据结构
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

### 注意事项

- 确保配置了正确的权限 integration:realtime:statistics
- 服务未启动时，startTime 和 runningDuration 字段为 null
- 对象列表包含所有启用实时同步的对象信息

## 测试信息

### 测试环境

- 开发环境 - 本地开发环境

### 测试结果

- 服务启动后，统计接口返回正确的服务状态信息
- 对象统计信息准确反映启用实时同步的对象数量和类型
- 订阅统计信息正确反映当前订阅状态
- 服务未启动时，相关字段正确返回 null

## 相关链接

- [设计文档](../design/0001-salesforce-cdc-realtime-sync.md) - Salesforce CDC实时同步设计
- [架构决策](../decisions/adr/0001-salesforce-cdc-sync.md) - Salesforce CDC同步方案
- [变更记录 0009](./0009-realtime-service-manual-start.md) - 实时服务手动启动管理

## 发布人员

- 开发人员 - 开发工程师

## 审核信息

- **审核人员**: 待审核
- **审核日期**: 待审核
- **审核状态**: 待审核
- **审核意见**: 待审核
