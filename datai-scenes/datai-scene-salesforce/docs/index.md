# 单一真源文档中心

## 项目概述

本文件是 Salesforce 集成项目的单一真源入口，包含所有项目相关文档的链接清单。

## 文档目录

### 【视觉看板】

- **架构图**: [Authentication.canvas](../Authentication.canvas) - 项目架构视觉化展示，包含认证、集成、通用和配置模块的详细结构

### 1. 需求文档

- **目录**: [requirements/](requirements/)
- **描述**: 包含项目的功能需求、非功能需求和验收标准

### 2. 设计文档

- **目录**: [design/](design/)
- **描述**: 包含系统架构设计、模块设计和接口设计

### 3. 架构决策

- **目录**: [decisions/adr/](decisions/adr/)
- **描述**: 包含架构决策记录 (ADR)，记录重要的架构决策过程
- **模板**: [0000-template.md](decisions/adr/0000-template.md)

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

### 5. 会话记录

- **目录**: [sessions/](sessions/)
- **描述**: 包含每次重要操作的会话记录
- **模板**: [YYYYMMDD-template.md](sessions/YYYYMMDD-template.md)

### 6. 迭代复盘

- **目录**: [retros/](retros/)
- **描述**: 包含每个迭代的复盘报告
- **模板**: [YYYYMMDD-template.md](retros/YYYYMMDD-template.md)

### 7. 变更日志

- **目录**: [changelog/](changelog/)
- **描述**: 包含详细的变更记录

## 协作基线文件

- **README.md**: [../README.md](../README.md) - 项目目标和真源入口
- **CONTRIBUTING.md**: [../CONTRIBUTING.md](../CONTRIBUTING.md) - 协作规则
- **CHANGELOG.md**: [../CHANGELOG.md](../CHANGELOG.md) - 变更摘要

## 导航说明

- 本文件是项目文档的唯一入口，所有文档变更必须在此添加链接
- 文档间引用必须使用相对路径链接
- 新文档必须使用相应目录中的模板文件

## 最后更新

- 更新时间: 2026-01-08
- 更新内容: 初始化文档中心结构
