# Datai - Salesforce 集成系统

## 项目概述

Datai 是一个基于若依Geek框架构建的企业级管理系统，采用模块化架构设计，提供可扩展的插件和场景模块。系统集成了 Salesforce 平台，支持多种 API 交互方式，实现数据同步、认证管理和元数据处理等核心功能。

**当前实现状态**：项目目前专注于从 Salesforce 源系统同步数据到本地数据库，包括元数据管理、批量数据同步、实时数据同步等功能。

## 技术栈

| 技术 | 版本 | 说明 |
|------|------|------|
| Java | 21 / 22 | 根项目使用 Java 21，Salesforce 模块使用 Java 22 |
| Spring Boot | 3.5.7 | 核心框架 |
| MyBatis | 3.5.16 | 持久层框架 |
| MySQL | 8.3.0 | 关系型数据库 |
| PostgreSQL | 42.7.7 | 关系型数据库 |
| Knife4j | 4.5.0 | API 文档工具 |
| Druid | 1.2.27 | 数据库连接池 |
| Maven | - | 构建工具 |
| Salesforce Partner API | 0.0.1 | Salesforce SOAP API 客户端 |
| Salesforce Pub/Sub Client | 0.2.21 | Salesforce CDC 实时同步客户端 |
| Apache Avro | 1.11.3 | 数据序列化格式 |
| Fastjson | 2.0.58 | JSON 解析器 |
| JWT | 0.12.6 | Token 生成与解析 |
| PageHelper | 2.1.1 | 分页插件 |

## 项目结构

```
datai/
├── datai-admin/              # 管理后台启动模块
├── datai-framework/           # 框架核心模块
├── datai-system/              # 系统管理模块
├── datai-common/              # 通用工具模块
├── datai-scenes/              # 场景模块
│   ├── datai-scene-salesforce/ # Salesforce 集成场景
│   │   ├── datai-salesforce-auth/       # 认证相关模块
│   │   ├── datai-salesforce-common/      # 通用工具和常量
│   │   ├── datai-salesforce-integration/ # 核心集成模块
│   │   ├── datai-salesforce-setting/     # 配置管理模块
│   │   └── datai-salesforce-starter/     # 启动器模块
│   ├── datai-scene-auth/       # 认证场景
│   │   ├── datai-auth-common/           # 认证通用模块
│   │   ├── datai-auth-starter/           # 认证启动器
│   │   ├── datai-oauth-justauth/        # JustAuth OAuth
│   │   ├── datai-oauth-wx/               # 微信 OAuth
│   │   ├── datai-tfa-email/              # 邮箱双因子认证
│   │   └── datai-tfa-phone/              # 手机双因子认证
│   ├── datai-scene-pay/        # 支付场景
│   │   ├── datai-pay-common/             # 支付通用模块
│   │   ├── datai-pay-starter/            # 支付启动器
│   │   ├── datai-pay-alipay/             # 支付宝支付
│   │   ├── datai-pay-wx/                 # 微信支付
│   │   └── datai-pay-sqb/                # 收钱吧支付
│   └── datai-scene-file/       # 文件场景
│       ├── datai-file-common/            # 文件通用模块
│       ├── datai-file-starter/            # 文件启动器
│       ├── datai-file-local/              # 本地文件存储
│       ├── datai-file-minio/              # MinIO 文件存储
│       └── datai-file-aliyun-oss/        # 阿里云 OSS 文件存储
├── datai-plugins/             # 插件模块
│   ├── datai-plugins-starter/  # 插件启动器
│   ├── datai-mybatis-plus/     # MyBatis Plus 插件
│   ├── datai-cache-redis/      # Redis 缓存插件
│   ├── datai-cache-ehcache/   # Ehcache 缓存插件
│   ├── datai-rabbitmq/        # RabbitMQ 消息队列插件
│   ├── datai-websocket/       # WebSocket 插件
│   ├── datai-netty/           # Netty 网络插件
│   └── datai-atomikos/        # Atomikos 分布式事务插件
└── datai-models/              # 模型模块
    ├── datai-models-starter/   # 模型启动器
    ├── datai-quartz/          # Quartz 定时任务
    ├── datai-form/            # 表单管理
    ├── datai-message/         # 消息管理
    ├── datai-generator/       # 代码生成器
    ├── datai-online/          # 在线用户
    └── datai-flowable/        # 工作流
```

## 核心功能

### Salesforce 集成模块（当前实现）

提供与 Salesforce 源系统的数据集成解决方案，支持从 Salesforce 同步数据到本地数据库。

#### 认证管理
- **多种登录方式**: OAuth2、SessionId、CLI、Legacy Credential
- **会话管理**: 登录会话的创建、查询和销毁
- **历史记录**: 登录历史记录的查询和统计

#### 数据同步（源系统 → 本地）
- **Bulk API v1/v2**: 批量数据同步，支持大规模数据导入导出
- **Partner API**: SOAP API 数据交互，支持 CRUD 操作
- **REST API**: RESTful API 数据交互
- **CDC 实时同步**: Change Data Capture 实时数据同步

#### 元数据管理
- **对象管理**: Salesforce 对象的查询、同步和结构创建
- **字段管理**: 对象字段的查询和管理
- **Picklist 管理**: Picklist 值的查询和管理
- **Lookup 过滤器**: Lookup 过滤器的查询和管理

#### 批次管理
- **批次创建**: 创建数据同步批次
- **批次数据插入**: 将本地数据插入到 Salesforce，保存返回 ID
- **批次数据更新**: 使用保存的 ID 更新 Salesforce 数据
- **批次重试**: 失败批次的重试机制
- **批次历史**: 批次操作历史记录

#### 实时同步
- **Pub/Sub API**: Salesforce Event Bus 实时数据订阅
- **事件处理**: CDC 事件的接收和处理
- **数据同步**: 实时数据同步到本地数据库
- **服务管理**: 实时同步服务的启动、停止和重启

#### 监控日志
- **API 调用日志**: Salesforce API 调用记录和统计
- **同步日志**: 数据同步操作日志
- **速率限制监控**: Salesforce API 速率限制监控
- **实时同步日志**: CDC 实时同步日志

### 开发计划

#### 1. 数据推送到目标 Salesforce 系统
- **目标系统认证**: 支持目标 Salesforce 系统的多种认证方式
- **数据推送**: 支持将本地数据批量推送到目标 Salesforce 系统
- **双向同步**: 实现源系统 → 本地 → 目标系统的数据流转
- **数据映射**: 支持源系统和目标系统之间的字段映射
- **推送监控**: 数据推送任务的监控和日志记录

#### 2. 源 Salesforce 系统文件下载
- **文件元数据**: 获取 Salesforce 附件和文档的元数据信息
- **文件下载**: 支持批量下载 Salesforce 中的文件（Attachment、Document、ContentVersion）
- **文件存储**: 支持本地存储、MinIO、阿里云 OSS 等多种存储方式
- **文件同步**: 文件与数据的关联同步
- **下载监控**: 文件下载任务的监控和进度追踪

#### 3. User 权限同步集成系统
- **权限元数据**: 同步 Salesforce User、Profile、PermissionSet、Role 等权限相关对象
- **权限映射**: 将 Salesforce 权限映射到系统用户权限
- **数据权限控制**: 基于用户权限控制数据访问范围
- **权限同步**: 实时同步用户权限变更
- **权限审计**: 权限变更的审计日志

#### 4. 集成可视化报表插件
- **报表设计器**: 可视化拖拽式报表设计器，支持自定义报表布局
- **数据源配置**: 支持从本地数据库和 Salesforce 系统配置数据源
- **图表组件**: 提供丰富的图表类型（柱状图、折线图、饼图、表格等）
- **数据筛选**: 支持动态筛选条件和参数化查询
- **报表导出**: 支持导出为 PDF、Excel、图片等多种格式
- **报表分享**: 支持报表分享和权限控制
- **实时刷新**: 支持报表数据的实时刷新和定时更新
- **报表模板**: 提供常用报表模板，快速生成标准报表

### 认证场景模块

- **OAuth 集成**: JustAuth 和微信 OAuth 登录
- **双因子认证**: 邮箱和手机双因子认证
- **用户管理**: OAuth 用户信息管理


### 文件场景模块

- **本地存储**: 本地文件系统存储
- **MinIO 存储**: MinIO 对象存储
- **阿里云 OSS**: 阿里云对象存储服务
- **文件管理**: 文件上传、下载和管理

### 插件模块

- **MyBatis Plus**: MyBatis 增强工具
- **Redis 缓存**: Redis 分布式缓存
- **Ehcache 缓存**: Ehcache 本地缓存
- **RabbitMQ**: 消息队列中间件
- **WebSocket**: WebSocket 实时通信
- **Netty**: Netty 网络框架
- **Atomikos**: 分布式事务管理

### 模型模块

- **Quartz**: 定时任务调度
- **表单管理**: 动态表单生成和管理
- **消息管理**: 系统消息管理
- **代码生成器**: 代码自动生成工具
- **在线用户**: 在线用户管理
- **工作流**: Flowable 工作流引擎

## 快速开始

### 环境要求

- JDK 21+
- Maven 3.6+
- MySQL 8.3.0+ 或 PostgreSQL 42.7.7+
- Redis（可选，用于缓存）

### 配置说明

#### 关键配置项

| 配置项 | 默认值 | 说明 |
|--------|--------|------|
| server.port | 8080 | 服务端口 |
| datai.profile | D:/datai/uploadPath | 文件上传路径 |
| datai.fileServer | local | 文件服务类型（local/minio/oss） |
| datai.fileMaxSize | 50 | 文件上传最大大小（MB） |
| spring.cache.type | redis | 缓存类型（redis/jcache） |
| token.expireTime | 1440 | Token 有效期（分钟） |
| pagehelper.helperDialect | mysql | 数据库方言 |

#### 启用配置文件

- `application-druid.yml`: 数据库连接池配置
- `application-file.yml`: 文件服务配置
- `application-auth.yml`: 认证配置
- `application-pay.yml`: 支付配置
- `application-plugins.yml`: 插件配置
- `application-model.yml`: 模型配置
- `application-apidoc.yml`: API 文档配置

### 构建项目

```bash
mvn clean install
```

### 运行项目

```bash
cd datai-admin
mvn spring-boot:run
```

启动成功后，访问：
- 应用地址: http://localhost:8080
- API 文档: http://localhost:8080/doc.html

## 文档

### 项目规则

项目采用基于单一真源 (Single Source of Truth) 的 Workflow，所有项目相关文档和决策均集中在 `docs/` 目录中。

### Salesforce 模块文档

详细文档请参考 [Salesforce 集成模块文档中心](datai-scenes/datai-scene-salesforce/docs/index.md)

- **唯一入口**: [datai-scene-salesforce/docs/index.md](datai-scenes/datai-scene-salesforce/docs/index.md)
- **架构看板**: [datai-scene-salesforce/docs/Authentication.canvas](datai-scenes/datai-scene-salesforce/docs/Authentication.canvas)
- **接口文档**: [datai-scene-salesforce/docs/api-docs/](datai-scenes/datai-scene-salesforce/docs/api-docs/)

### 文档目录

- **需求文档**: [datai-scenes/datai-scene-salesforce/docs/requirements/](datai-scenes/datai-scene-salesforce/docs/requirements/)
- **设计文档**: [datai-scenes/datai-scene-salesforce/docs/design/](datai-scenes/datai-scene-salesforce/docs/design/)
- **架构决策**: [datai-scenes/datai-scene-salesforce/docs/decisions/adr/](datai-scenes/datai-scene-salesforce/docs/decisions/adr/)
- **提示词库**: [datai-scenes/datai-scene-salesforce/docs/prompts/](datai-scenes/datai-scene-salesforce/docs/prompts/)
- **会话记录**: [datai-scenes/datai-scene-salesforce/docs/sessions/](datai-scenes/datai-scene-salesforce/docs/sessions/)
- **变更日志**: [datai-scenes/datai-scene-salesforce/docs/changelog/](datai-scenes/datai-scene-salesforce/docs/changelog/)

## 贡献指南

请参考 [CONTRIBUTING.md](CONTRIBUTING.md) 了解如何参与项目贡献。

## 变更日志

请参考 [CHANGELOG.md](CHANGELOG.md) 查看项目变更记录。

## 许可证

本项目基于若依Geek框架开发，遵循相应的开源许可证。

## 联系方式

- 项目地址: https://gitee.com/geek-xd/datai-springboot3.git
- 文档中心: [datai-scenes/datai-scene-salesforce/docs/index.md](datai-scenes/datai-scene-salesforce/docs/index.md)
