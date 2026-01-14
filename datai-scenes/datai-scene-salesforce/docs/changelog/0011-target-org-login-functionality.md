# Changelog - 目标ORG登录功能实现

## 变更信息

- **版本号**: v0.1.11
- **发布日期**: 2026-01-13
- **变更类型**: 特性更新

## 变更摘要

本次变更实现了目标ORG登录功能，为后续实现本地数据库到Salesforce的双向同步提供了基础。通过复用现有的登录历史和会话对象，并添加ORG类型字段进行区分，实现了目标ORG的独立认证管理。创建了完整的服务层和控制器，支持目标ORG的登录、登出、获取登录信息和自动登录功能。

## 详细变更

### 特性更新

- **目标ORG登录模型** - 创建了 `TargetOrgLoginRequest` 模型类，用于接收目标ORG登录请求参数
- **ORG类型区分** - 在 `DataiSfLoginHistory` 和 `DataiSfLoginSession` 中添加了 `orgType` 字段，用于区分源ORG和目标ORG
- **目标ORG登录历史服务** - 创建了 `ITargetOrgLoginHistoryService` 接口和 `TargetOrgLoginHistoryServiceImpl` 实现类，管理目标ORG登录历史
- **目标ORG登录会话服务** - 创建了 `ITargetOrgLoginSessionService` 接口和 `TargetOrgLoginSessionServiceImpl` 实现类，管理目标ORG登录会话
- **目标ORG登录服务** - 创建了 `ITargetOrgLoginService` 接口和 `TargetOrgLoginServiceImpl` 实现类，实现目标ORG登录核心逻辑
- **目标ORG登录控制器** - 创建了 `DataISfTargetOrgLoginController` 控制器，提供目标ORG登录相关REST API接口
- **数据访问层扩展** - 在 `DataiSfLoginHistoryMapper` 接口中添加了 `selectLatestSuccessLoginHistoryByOrgType` 方法，支持按ORG类型查询登录历史

### Bug 修复

无

### 性能优化

无

### 安全修复

无

### 其他变更

无

## 影响范围

### 受影响的模块

- **datai-salesforce-auth** - 新增目标ORG登录相关功能，不影响现有源ORG登录功能

### 兼容性说明

- 本次变更完全兼容现有功能，所有新增内容均为独立模块
- 复用了现有的 `DataiSfLoginHistory` 和 `DataiSfLoginSession` 对象，通过 `orgType` 字段进行区分
- 目标ORG登录接口路径为 `/salesforce/target/login/*`，与源ORG登录接口 `/salesforce/login/*` 完全独立

## 升级指南

### 升级步骤

1. 部署新版本的 `datai-salesforce-auth` 模块
2. 更新数据库表结构，为 `datai_sf_login_history` 和 `datai_sf_login_session` 表添加 `org_type` 字段
3. 更新 `DataiSfLoginHistoryMapper.xml`，添加 `selectLatestSuccessLoginHistoryByOrgType` SQL实现

### 注意事项

- 需要确保数据库表结构已更新，添加 `org_type` 字段
- 目标ORG登录功能与源ORG登录功能完全独立，可以同时使用
- 建议在测试环境中先验证目标ORG登录功能后再部署到生产环境

## 测试信息

### 测试环境

- 本地开发环境

### 测试结果

- 待测试

## 相关链接

- [需求文档](../requirements/0002-salesforce-org-config-management.md) - Salesforce ORG配置管理需求
- [设计文档](../design/0002-salesforce-org-config-management.md) - Salesforce ORG配置管理设计
- [架构决策](../decisions/adr/0002-salesforce-bidirectional-sync.md) - Salesforce双向同步架构决策

## 发布人员

- AI Assistant - 开发人员

## 审核信息

- **审核人员**: 待审核
- **审核日期**: 待审核
- **审核状态**: 待审核
- **审核意见**: 待审核
