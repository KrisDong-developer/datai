# Changelog - 实时服务手动启动管理

## 变更信息

- **版本号**: v0.1.9
- **发布日期**: 2026-01-12
- **变更类型**: 特性更新

## 变更摘要

本次变更主要针对 Salesforce 实时同步服务的启动方式进行了优化。移除了自动启动机制，改为通过页面手动控制启动，提高了系统的可控性和灵活性。新增了 RealtimeServiceController 统一管理所有实时服务的初始化和启动。

## 详细变更

### 特性更新

- **移除自动启动机制** - 从以下实现类中移除了 @PostConstruct 注解：
  - RealtimeSyncServiceImpl - 移除 @PostConstruct，改为通过 API 调用启动
  - PubSubEventSubscriberImpl - 移除 @PostConstruct，改为通过 API 调用启动
  - ObjectRegistryImpl - 移除 @PostConstruct，改为通过 API 调用初始化

- **新增 RealtimeServiceController** - 创建了统一管理实时服务的控制器：
  - 提供了获取实时服务状态的接口
  - 提供了初始化对象注册表的接口
  - 提供了启动/停止事件订阅的接口
  - 提供了启动/停止/重启实时同步服务的接口
  - 提供了一键启动所有实时服务的接口
  - 提供了一键停止所有实时服务的接口

## 影响范围

### 受影响的模块

- **datai-salesforce-integration** - 以下类被修改：
  - RealtimeSyncServiceImpl - 移除 @PostConstruct 注解
  - PubSubEventSubscriberImpl - 移除 @PostConstruct 注解
  - ObjectRegistryImpl - 移除 @PostConstruct 注解
  - RealtimeServiceController - 新增控制器类
  - EventProcessor - 新增统计方法接口
  - DataSynchronizer - 新增统计方法接口
  - EventProcessorImpl - 实现统计方法
  - DataSynchronizerImpl - 实现统计方法

### 兼容性说明

- 本次变更不向后兼容，应用启动后不会自动启动实时同步服务
- 需要通过 API 接口手动启动实时服务
- 现有的 DataiIntegrationRealtimeSyncController 仍然可用，但推荐使用新的 RealtimeServiceController

## 升级指南

### 升级步骤

1. 部署新的代码版本

2. 应用启动后，通过以下接口启动实时服务：
   - 方式一：一键启动所有服务
     ```
     POST /integration/realtime/service/start/all
     ```
   - 方式二：分步启动服务
     ```
     POST /integration/realtime/service/init/registry
     POST /integration/realtime/service/start/subscriber
     POST /integration/realtime/service/start/service
     ```

3. 如需停止实时服务，使用以下接口：
   - 方式一：一键停止所有服务
     ```
     POST /integration/realtime/service/stop/all
     ```
   - 方式二：分步停止服务
     ```
     POST /integration/realtime/service/stop/service
     POST /integration/realtime/service/stop/subscriber
     ```

### API 接口说明

#### 获取实时服务状态
- **接口**: GET /integration/realtime/service/status
- **权限**: integration:realtime:status
- **返回**: 实时服务状态信息

#### 初始化对象注册表
- **接口**: POST /integration/realtime/service/init/registry
- **权限**: integration:realtime:init:registry
- **返回**: 初始化结果和注册对象数量

#### 启动事件订阅
- **接口**: POST /integration/realtime/service/start/subscriber
- **权限**: integration:realtime:start:subscriber
- **返回**: 启动结果和订阅状态

#### 停止事件订阅
- **接口**: POST /integration/realtime/service/stop/subscriber
- **权限**: integration:realtime:stop:subscriber
- **返回**: 停止结果和订阅状态

#### 启动实时同步服务
- **接口**: POST /integration/realtime/service/start/service
- **权限**: integration:realtime:start:service
- **返回**: 启动结果和服务状态

#### 停止实时同步服务
- **接口**: POST /integration/realtime/service/stop/service
- **权限**: integration:realtime:stop:service
- **返回**: 停止结果和服务状态

#### 重启实时同步服务
- **接口**: POST /integration/realtime/service/restart/service
- **权限**: integration:realtime:restart:service
- **返回**: 重启结果和服务状态

#### 一键启动所有实时服务
- **接口**: POST /integration/realtime/service/start/all
- **权限**: integration:realtime:start:all
- **返回**: 启动结果和详细信息

#### 一键停止所有实时服务
- **接口**: POST /integration/realtime/service/stop/all
- **权限**: integration:realtime:stop:all
- **返回**: 停止结果和详细信息

### 注意事项

- 应用启动后需要手动启动实时服务，否则实时同步功能将不可用
- 建议在应用启动完成后立即调用启动接口
- 确保配置了正确的权限，否则无法调用相关接口
- 启动顺序建议：先初始化对象注册表，再启动事件订阅，最后启动实时同步服务

## 测试信息

### 测试环境

- 开发环境 - 本地开发环境

### 测试结果

- 移除 @PostConstruct 注解后，应用启动时不会自动启动实时服务
- 通过 API 接口可以成功启动和停止实时服务
- 一键启动/停止接口正常工作
- 服务状态查询接口返回正确的状态信息

## 相关链接

- [设计文档](../design/0001-salesforce-cdc-realtime-sync.md) - Salesforce CDC实时同步设计
- [架构决策](../decisions/adr/0001-salesforce-cdc-sync.md) - Salesforce CDC同步方案
- [变更记录 0008](./0008-pubsub-config-optimization.md) - Pub/Sub API 配置项添加与连接工厂优化

## 发布人员

- 开发人员 - 开发工程师

## 审核信息

- **审核人员**: 待审核
- **审核日期**: 待审核
- **审核状态**: 待审核
- **审核意见**: 待审核
