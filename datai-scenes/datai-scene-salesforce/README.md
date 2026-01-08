# Salesforce 集成项目

## 项目目标

提供与 Salesforce 平台的全面集成解决方案，支持多种 API 交互方式，包括 Bulk API v1/v2、Partner API 和 REST API，实现数据同步、认证管理和元数据处理等核心功能。

## 单一真源入口

本项目采用基于单一真源 (Single Source of Truth) 的 Workflow，所有项目相关文档和决策均集中在 `docs/` 目录中。

### 真源文档中心

- **唯一入口**: [docs/index.md](docs/index.md) - 包含所有文档的链接清单
- **需求文档**: [docs/requirements/](docs/requirements/)
- **设计文档**: [docs/design/](docs/design/)
- **架构决策**: [docs/decisions/adr/](docs/decisions/adr/)
- **提示词库**: [docs/prompts/](docs/prompts/)
- **会话记录**: [docs/sessions/](docs/sessions/)
- **迭代复盘**: [docs/retros/](docs/retros/)
- **变更日志**: [docs/changelog/](docs/changelog/)

### 项目结构

- `datai-salesforce-auth/` - 认证相关模块
- `datai-salesforce-common/` - 通用工具和常量
- `datai-salesforce-integration/` - 核心集成模块

### 协作基线文件

- [CONTRIBUTING.md](CONTRIBUTING.md) - 协作规则
- [CHANGELOG.md](CHANGELOG.md) - 变更摘要

## AI Context Root

- **第一访问点**: [docs/index.md](docs/index.md) - 作为 Trae 的第一访问点，包含所有文档的链接清单
- **视觉看板**: [docs/Authentication.canvas](docs/Authentication.canvas) - 项目架构视觉化展示
