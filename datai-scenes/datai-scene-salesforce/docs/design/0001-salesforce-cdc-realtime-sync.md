# Design 模板

## 设计信息

- **设计名称**: Salesforce Pub/Sub API实时同步设计
- **设计类型**: 架构设计 + 数据结构设计
- **设计日期**: 2026-01-10
- **设计版本**: v1.1.0
- **设计作者**: 系统管理员

## 输入引用

引用相关的 docs 文档链接：

- [需求文档](../requirements/0001-salesforce-realtime-sync.md) - Salesforce数据及时同步至本地数据库
- [架构决策](../decisions/adr/0001-salesforce-cdc-sync.md) - Salesforce Pub/Sub API同步方案
- [设计文档模板](../design/0000-template.md) - 设计文档模板

## Context Maps

强制列出本次设计依赖的 Canvas 文件：

- [Authentication.canvas](../Authentication.canvas) - 项目架构视觉化展示
- **相关节点**: [集成任务](node_integration_task) - 处理Salesforce的定时同步任务
- **相关节点**: [SessionManager](node_session_manager_detail) - 会话管理，提供登录服务
- **相关节点**: [Salesforce连接类型](node_integration_connections) - 提供与Salesforce的各种连接方式

## 设计目标

明确描述此设计的目标和预期效果，包括要解决的问题、实现的功能或达成的结果。

本设计的目标是实现Salesforce Pub/Sub API 实时同步功能，通过利用Salesforce Pub/Sub API订阅Salesforce事件总线，实时捕获数据变更并同步至本地数据库。设计一套可复用的实时同步代码，支持多个对象同时使用，采用事件驱动架构，确保数据的及时同步和可靠性。

## 设计内容

### 概述

简要描述设计的整体思路和架构。

本设计采用事件驱动架构，通过Salesforce Pub/Sub API订阅Salesforce事件总线实现数据的实时同步。核心组件包括：配置管理、事件订阅、事件处理、数据同步和日志记录。设计一套可复用的实时同步代码，支持多个对象同时使用，通过配置管理控制对象的同步状态。

### 详细设计

#### 1. 同步配置管理

- **设计说明**: 基于现有的DataiConfiguration表存储同步配置，通过is_realtime_sync字段（tinyint(1)类型）控制对象是否开启实时同步
- **设计图**: 配置管理流程图
- **关键元素**:
  - DataiConfiguration表：存储同步配置信息
  - is_realtime_sync字段：tinyint(1)类型，控制对象是否开启实时同步
  - objectController：提供变更实时同步状态的方法
  - batch表：存储全量拉取记录
- **实现细节**:
  - 在objectController中增加updateRealtimeSyncStatus方法
  - 方法中调用Salesforce API查询PlatformEventChannelMember表，检查是否启用实时同步
  - 检查本地数据库中的batch表是否已经全量拉取存量数据，若没有则提示但仍可启用Pub/Sub API
  - 根据查询结果更新对象的实时同步状态
  - 同步状态变更时，更新缓存中的配置信息

#### 2. 实时数据同步

- **设计说明**: 实现Salesforce Pub/Sub API事件订阅和处理，采用事件驱动架构，支持多个对象复用同一套代码。事件处理采用异步执行方式，upsert操作参考DataiIntegrationBatchServiceImpl中的processQueryResult方法实现。利用Pub/Sub API的自动重连和消息重试机制，提高系统可靠性。
- **设计图**: 实时数据同步流程图
- **关键元素**:
  - PubSubClient：Pub/Sub API客户端，订阅Salesforce事件总线
  - EventProcessor：事件处理器，解析和处理捕获的变更事件
  - DataSynchronizer：数据同步器，将变更数据同步至本地数据库
  - ObjectRegistry：对象注册表，管理所有启用实时同步的对象
- **实现细节**:
  - 使用Salesforce Pub/Sub API客户端库订阅事件总线
  - 事件处理采用异步执行方式，提高处理效率
  - 数据同步采用upsert操作，参考DataiIntegrationBatchServiceImpl中的processQueryResult方法实现
  - 利用Pub/Sub API的自动重连和消息重试机制，提高系统可靠性
  - 通过配置管理控制对象的同步状态，实现多对象复用

#### 3. 实时同步日志表

- **设计说明**: 设计实时同步日志表，记录同步操作的详细信息
- **设计图**: 实时同步日志表结构
- **关键元素**:
  - datai_integration_realtime_sync_log表：记录同步操作的详细信息
  - 字段包括：id、objectName、recordId、operationType、changeData、syncStatus、errorMessage、retryCount、salesforceTimestamp、syncTimestamp
- **实现细节**:
  - 已实现DataiIntegrationRealtimeSyncLog实体类
  - 已实现DataiIntegrationRealtimeSyncLogController控制器
  - changeData字段存储变更数据的字符串格式
  - 提供日志查询、导出和详情查看功能
  - 对应SQL文件：create-realtime-sync-log-table.sql

#### 4. 异常处理与重试

- **设计说明**: 实现事件处理和同步操作的异常处理机制。暂不考虑失败重试。
- **设计图**: 异常处理流程图
- **关键元素**:
  - ExceptionHandler：异常处理器，处理各类异常
- **实现细节**:
  - 异常分类处理，不同类型的异常采用不同的处理策略
  - 记录异常信息，便于后续分析和处理

## 约束

列出设计时的约束条件，例如：
- 技术栈限制：基于现有的Spring Boot3+Vue3技术栈
- 性能要求：同步操作不能影响系统的正常运行
- 安全性要求：确保同步过程中的数据安全
- 兼容性要求：支持不同版本的Salesforce API
- 其他约束：需要遵守Salesforce的API使用限制

## Rule Set

"请严格参考 @Authentication.canvas 中的状态机转移逻辑，不要自行发挥。"

**具体规则**：
- 必须使用 Canvas 中定义的类名和方法名
- 必须遵循 Canvas 中定义的调用关系
- 必须参考 Canvas 中的流程图逻辑

## 验收标准

定义验证设计质量的具体标准，例如：
- 功能完整性：所有同步功能能够正常工作
- 设计合理性：设计符合架构设计原则
- 实现可行性：设计方案能够在现有技术栈中实现
- 性能指标：同步操作的执行时间在可接受范围内
- 用户体验：同步操作的操作界面友好易用

## 风险

识别设计可能带来的风险，例如：
- 技术实现风险：Salesforce API的变更可能影响同步功能
- 性能风险：事件处理可能成为性能瓶颈
- 兼容性风险：不同版本的Salesforce API可能存在差异
- 维护风险：系统复杂度增加可能影响维护
- 其他潜在风险：网络连接不稳定可能影响同步可靠性

## 设计变更记录

| 日期 | 变更内容 | 变更原因 | 变更人 | 审核人 |
|------|---------|---------|--------|--------|
| 2026-01-09 | 创建设计文档 | 初始设计 | 系统管理员 | - |

## 使用指南

### 适用场景

描述设计适用的场景和条件。

本设计适用于需要实时同步Salesforce数据至本地数据库的场景，特别是对数据实时性要求较高的业务系统。

### 实施步骤

提供设计实施的具体步骤。

1. 创建实时同步日志表
2. 修改对象表，添加is_realtime_sync字段
3. 在objectController中增加updateRealtimeSyncStatus方法
4. 实现事件订阅和处理逻辑
5. 配置对象的实时同步状态
6. 启动事件订阅服务

### 维护建议

提供设计维护的建议和注意事项。

- 定期检查同步日志，监控同步状态
- 注意Salesforce API限流，合理控制事件处理频率
- 确保系统有足够的资源处理高并发的事件流
- 定期备份同步配置和日志数据
- 关注Salesforce API的变更，及时更新事件处理逻辑