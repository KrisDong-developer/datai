# Setting 模块接口文档索引

## 模块概述

本模块包含配置管理相关的所有接口文档，涵盖配置审计日志、配置环境、配置快照和配置管理等功能。

## 接口文档列表

### 1. 配置审计日志管理 (DataiConfigAuditLogController)

**控制器路径**: `/setting/auditLog`

**接口列表**:
- [查询审计日志列表](DataiConfigAuditLogController/0001-log-list.md) - 支持分页查询和多条件过滤
- [导出审计日志列表](DataiConfigAuditLogController/0002-export-log-list.md) - 导出到 Excel 文件
- [获取审计日志详细信息](DataiConfigAuditLogController/0003-get-info.md) - 根据日志ID获取详细信息
- [新增审计日志](DataiConfigAuditLogController/0004-add-log.md) - 保存审计日志基本信息
- [修改审计日志](DataiConfigAuditLogController/0005-edit-log.md) - 更新审计日志基本信息
- [删除审计日志](DataiConfigAuditLogController/0006-remove-log.md) - 支持批量删除

### 2. 配置环境管理 (DataiConfigEnvironmentController)

**控制器路径**: `/setting/environment`

**接口列表**:
- [查询环境列表](DataiConfigEnvironmentController/0001-environment-list.md) - 支持分页查询和多条件过滤
- [导出环境列表](DataiConfigEnvironmentController/0002-export-environment-list.md) - 导出到 Excel 文件
- [获取环境详细信息](DataiConfigEnvironmentController/0003-get-info.md) - 根据环境ID获取详细信息
- [新增环境](DataiConfigEnvironmentController/0004-add-environment.md) - 保存环境基本信息
- [修改环境](DataiConfigEnvironmentController/0005-edit-environment.md) - 更新环境基本信息
- [删除环境](DataiConfigEnvironmentController/0006-remove-environment.md) - 支持批量删除
- [切换环境](DataiConfigEnvironmentController/0007-switch-environment.md) - 切换当前使用的环境
- [获取当前环境](DataiConfigEnvironmentController/0008-get-current-environment.md) - 获取当前使用的环境信息

### 3. 配置快照管理 (DataiConfigSnapshotController)

**控制器路径**: `/setting/snapshot`

**接口列表**:
- [查询快照列表](DataiConfigSnapshotController/0001-snapshot-list.md) - 支持分页查询和多条件过滤
- [导出快照列表](DataiConfigSnapshotController/0002-export-snapshot-list.md) - 导出到 Excel 文件
- [获取快照详细信息](DataiConfigSnapshotController/0003-get-info.md) - 根据快照ID获取详细信息
- [新增快照](DataiConfigSnapshotController/0004-add-snapshot.md) - 保存快照基本信息
- [修改快照](DataiConfigSnapshotController/0005-edit-snapshot.md) - 更新快照基本信息
- [删除快照](DataiConfigSnapshotController/0006-remove-snapshot.md) - 支持批量删除
- [从当前配置生成快照](DataiConfigSnapshotController/0007-create-snapshot.md) - 从当前配置生成快照
- [恢复快照](DataiConfigSnapshotController/0008-restore-snapshot.md) - 将快照恢复为当前配置
- [获取快照详细信息（包含配置内容）](DataiConfigSnapshotController/0009-get-snapshot-detail.md) - 获取快照的详细信息和配置内容
- [比较两个快照的差异](DataiConfigSnapshotController/0010-compare-snapshots.md) - 比较两个快照之间的配置差异

### 4. 配置管理 (DataiConfigurationController)

**控制器路径**: `/setting/configuration`

**接口列表**:
- [查询配置列表](DataiConfigurationController/0001-configuration-list.md) - 支持分页查询和多条件过滤
- [导出配置列表](DataiConfigurationController/0002-export-configuration-list.md) - 导出到 Excel 文件
- [获取配置详细信息](DataiConfigurationController/0003-get-info.md) - 根据配置ID获取详细信息
- [新增配置](DataiConfigurationController/0004-add-configuration.md) - 保存配置基本信息
- [修改配置](DataiConfigurationController/0005-edit-configuration.md) - 更新配置基本信息
- [删除配置](DataiConfigurationController/0006-remove-configuration.md) - 支持批量删除
- [刷新配置缓存](DataiConfigurationController/0007-refresh-config-cache.md) - 清除缓存并重新加载
- [查询配置缓存状态](DataiConfigurationController/0008-get-config-cache-status.md) - 监控缓存运行情况

## 接口统计

| 控制器 | 接口数量 | 创建日期 |
|--------|----------|----------|
| DataiConfigAuditLogController | 6 | 2026-01-14 |
| DataiConfigEnvironmentController | 8 | 2026-01-14 |
| DataiConfigSnapshotController | 10 | 2026-01-14 |
| DataiConfigurationController | 8 | 2026-01-14 |
| **总计** | **32** | - |

## 功能说明

### 配置审计日志
- 记录所有配置变更操作
- 支持按操作类型、操作人、时间范围等条件查询
- 提供完整的审计追踪功能

### 配置环境
- 管理多个配置环境（开发、测试、生产等）
- 支持环境切换功能
- 提供环境级别的配置隔离

### 配置快照
- 支持配置快照的创建和管理
- 提供快照恢复功能
- 支持快照之间的差异比较
- 便于配置版本管理和回滚

### 配置管理
- 提供配置的增删改查功能
- 支持敏感配置和加密配置
- 提供配置缓存管理功能
- 支持配置导出功能

## 权限说明

所有接口都需要相应的权限才能访问，具体权限要求请参考各接口文档中的权限说明部分。

## 最后更新

- 更新时间: 2026-01-14
- 更新内容: 创建 Setting 模块接口文档索引
