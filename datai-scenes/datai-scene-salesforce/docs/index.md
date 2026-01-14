# 单一真源文档中心

## 项目概述

本文件是 Salesforce 集成项目的单一真源入口，包含所有项目相关文档的链接清单。

## 文档目录

### 【视觉看板】

- **架构图**: [Authentication.canvas](../Authentication.canvas) - 项目架构视觉化展示，包含认证、集成、通用和配置模块的详细结构

### 1. 需求文档

- **目录**: [requirements/](requirements/)
- **描述**: 包含项目的功能需求、非功能需求和验收标准
- **需求文档**:
  - [0001-salesforce-realtime-sync.md](requirements/0001-salesforce-realtime-sync.md) - Salesforce数据及时同步至本地数据库
  - [0000-template.md](requirements/0000-template.md) - 需求文档模板

### 2. 设计文档

- **目录**: [design/](design/)
- **描述**: 包含系统架构设计、模块设计和接口设计
- **设计文档**:
  - [0001-salesforce-cdc-realtime-sync.md](design/0001-salesforce-cdc-realtime-sync.md) - Salesforce CDC实时同步设计
  - [0000-template.md](design/0000-template.md) - 设计文档模板

### 3. 架构决策

- **目录**: [decisions/adr/](decisions/adr/)
- **描述**: 包含架构决策记录 (ADR)，记录重要的架构决策过程
- **ADR文档**:
  - [0001-salesforce-cdc-sync.md](decisions/adr/0001-salesforce-cdc-sync.md) - Salesforce CDC同步方案
  - [0000-template.md](decisions/adr/0000-template.md) - ADR文档模板

### 4. 提示词库

- **目录**: [prompts/](prompts/)
- **描述**: 包含 AI 提示词模板和最佳实践
- **模板**: [0000-template.md](prompts/0000-template.md)
- **提示词文件**:
  - [01创建白板提示词.md](prompts/01创建白板提示词.md)
  - [02白板优化提示词.md](prompts/02白板优化提示词.md)
  - [03创建工作流提示词.md](prompts/03创建工作流提示词.md)
  - [04工作流整合白板提示词.md](prompts/04工作流整合白板提示词.md)
  - [05创建项目规则提示词.md](prompts/05创建项目规则提示词.md)
  - [06更新当前项目README说明文档.md](prompts/06更新当前项目README说明文档.md) - 更新项目README文档的提示词

### 6. 会话记录

- **目录**: [sessions/](sessions/)
- **描述**: 包含每次重要操作的会话记录
- **会话记录**:
  - [20260109-salesforce-cdc-sync.md](sessions/20260109-salesforce-cdc-sync.md) - Salesforce CDC实时同步实现
  - [YYYYMMDD-template.md](sessions/YYYYMMDD-template.md) - 会话记录模板

### 6. 迭代复盘

- **目录**: [retros/](retros/)
- **描述**: 包含每个迭代的复盘报告
- **模板**: [YYYYMMDD-template.md](retros/YYYYMMDD-template.md)

### 7. 变更日志

- **目录**: [changelog/](changelog/)
- **描述**: 包含详细的变更记录
- **变更记录文档**:
  - [0001-core-components-implementation.md](changelog/0001-core-components-implementation.md) - 核心组件实现
  - [0002-database-structure-adjustment.md](changelog/0002-database-structure-adjustment.md) - 数据库结构调整
  - [0003-sync-configuration-management.md](changelog/0003-sync-configuration-management.md) - 同步配置管理实现
  - [0004-realtime-sync-log-enhancement.md](changelog/0004-realtime-sync-log-enhancement.md) - 实时同步日志功能完善
  - [0005-testing-and-verification.md](changelog/0005-testing-and-verification.md) - 测试和验证
  - [0006-documentation-improvement.md](changelog/0006-documentation-improvement.md) - 文档完善
  - [0007-pubsub-api-implementation.md](changelog/0007-pubsub-api-implementation.md) - Salesforce Pub/Sub API 实时同步功能实现
  - [0008-pubsub-config-optimization.md](changelog/0008-pubsub-config-optimization.md) - Pub/Sub API 配置项添加与连接工厂优化
  - [0009-realtime-service-manual-start.md](changelog/0009-realtime-service-manual-start.md) - 实时服务手动启动管理
  - [0010-realtime-sync-statistics.md](changelog/0010-realtime-sync-statistics.md) - 实时同步统计接口
  - [0011-target-org-login-functionality.md](changelog/0011-target-org-login-functionality.md) - 目标ORG登录功能实现
  - [0000-template.md](changelog/0000-template.md) - 变更记录模板

### 8. 接口文档

- **目录**: [api-docs/](api-docs/)
- **描述**: 包含提供给前端调用的接口文档
- **模块索引**:
  - [Setting 模块接口索引](api-docs/setting/index.md) - 配置管理相关接口索引
- **接口文档**: 
  - [DataISfLoginController](api-docs/auth/DataISfLoginController/) - Salesforce登录认证接口
  - [DataiIntegrationRealtimeSyncController](api-docs/integration/DataiIntegrationRealtimeSyncController/) - 实时同步服务管理接口
  - [DataiIntegrationObjectController](api-docs/integration/DataiIntegrationObjectController/) - 对象同步控制接口
  - [DataiIntegrationBatchController](api-docs/integration/DataiIntegrationBatchController/) - 批次同步控制接口
  - [DataiIntegrationMetadataChangeController](api-docs/integration/DataiIntegrationMetadataChangeController/) - 元数据变更控制接口
  - [RealtimeServiceController](api-docs/integration/RealtimeServiceController/) - 实时服务管理接口
  - [DataiConfigAuditLogController](api-docs/setting/DataiConfigAuditLogController/) - 配置审计日志管理接口
  - [DataiConfigEnvironmentController](api-docs/setting/DataiConfigEnvironmentController/) - 配置环境管理接口
  - [DataiConfigSnapshotController](api-docs/setting/DataiConfigSnapshotController/) - 配置快照管理接口
  - [DataiConfigurationController](api-docs/setting/DataiConfigurationController/) - 配置管理接口
  - [SysJobController](api-docs/monitor/SysJobController/) - 定时任务管理接口
  - [SysJobLogController](api-docs/monitor/SysJobLogController/) - 定时任务日志管理接口

### 9. SQL文件

- **目录**: [sql/](sql/)
- **描述**: 包含数据库结构和初始化SQL文件
- **SQL文件**:
  - [create-realtime-sync-log-table.sql](sql/create-realtime-sync-log-table.sql) - 实时同步日志表创建SQL
  - [add_is_realtime_sync_column.sql](sql/add_is_realtime_sync_column.sql) - 添加实时同步列SQL
  - [add_is_partitioned_column.sql](sql/add_is_partitioned_column.sql) - 添加分区列SQL
  - [datai_table.sql](sql/datai_table.sql) - 数据表初始化SQL

### 10. 参考代码

- **目录**: [reference-code/](reference-code/)
- **描述**: 包含项目参考代码和示例实现
- **组件详情**:
  - **multicloudj组件**: [salesforce-pubsub-realtime-sync/multicloudj](reference-code/salesforce-pubsub-realtime-sync/multicloudj/)
    - **来源**: 从 `com.salesforce.multicloudj:pubsub-client:0.2.10` 依赖编译而来
    - **包含模块**: 
      - multicloudj-common-0.2.10: 通用异常处理和工具类
      - pubsub-client-0.2.10: Pub/Sub API 客户端实现
      - sts-client-0.2.10: STS 客户端实现
    - **使用说明**: 往后直接使用 `com.salesforce.multicloudj:pubsub-client:0.2.10` 依赖，可参考此组件中的代码实现
    - **核心功能**: 提供了订阅 Salesforce Event Bus、处理事件、管理连接等功能的封装

## 协作基线文件

- **README.md**: [../README.md](../README.md) - 项目目标和真源入口
- **CONTRIBUTING.md**: [../CONTRIBUTING.md](../CONTRIBUTING.md) - 协作规则
- **CHANGELOG.md**: [../CHANGELOG.md](../CHANGELOG.md) - 变更摘要

## 导航说明

- 本文件是项目文档的唯一入口，所有文档变更必须在此添加链接
- 文档间引用必须使用相对路径链接
- 新文档必须使用相应目录中的模板文件

## 最后更新

- 更新时间: 2026-01-14
- 更新内容: 添加 06更新当前项目README说明文档.md 提示词文档，用于指导 AI 根据项目实际情况更新 README 文档
