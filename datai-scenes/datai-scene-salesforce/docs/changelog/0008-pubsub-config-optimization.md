# Changelog - Pub/Sub API 配置项添加与连接工厂优化

## 变更信息

- **版本号**: v0.1.8
- **发布日期**: 2026-01-12
- **变更类型**: 特性更新

## 变更摘要

本次变更主要针对 Salesforce Pub/Sub API 的配置管理和连接工厂进行了优化。新增了 Consumer Key 和 Consumer Secret 配置项，并优化了 PubSubConnectionFactory 中 StsCredentials 的创建逻辑，使其从缓存中读取配置值，提高了配置的灵活性和可维护性。

## 详细变更

### 特性更新

- **新增 Pub/Sub API 配置项** - 在 datai_configuration 表中添加了两条新的配置项：
  - `salesforce.pubsub.consumer.key` (ID: 171): Salesforce Pub/Sub API Consumer Key，从 Connected App 获取
  - `salesforce.pubsub.consumer.secret` (ID: 172): Salesforce Pub/Sub API Consumer Secret，从 Connected App 获取
  - 两个配置项都设置为敏感数据（is_sensitive=1）并启用加密（is_encrypted=1）

- **优化 PubSubConnectionFactory** - 重构了 createStsCredentials 方法：
  - 添加了 CacheUtils 和 SalesforceConfigCacheManager 的导入
  - 注入了 SalesforceConfigCacheManager 依赖
  - 将硬编码的空字符串替换为从缓存中读取的配置值
  - 使用 `CacheUtils.get(cacheKey, "salesforce.pubsub.consumer.key", String.class)` 获取 Consumer Key
  - 使用 `CacheUtils.get(cacheKey, "salesforce.pubsub.consumer.secret", String.class)` 获取 Consumer Secret

## 影响范围

### 受影响的模块

- **datai-salesforce-setting** - 新增配置项，需要通过配置管理界面录入 Consumer Key 和 Consumer Secret
- **datai-salesforce-integration** - PubSubConnectionFactory 类被修改，需要确保 SalesforceConfigCacheManager 正确配置并缓存了相关配置值

### 兼容性说明

- 本次变更向后兼容，不会影响现有功能
- 如果缓存中没有配置值，StsCredentials 的前两个参数将为 null，需要确保在使用前已正确配置

## 升级指南

### 升级步骤

1. 执行 SQL 脚本添加新的配置项：
   ```sql
   INSERT INTO `datai_configuration` (`id`, `config_key`, `config_value`, `environment_id`, `is_sensitive`, `is_encrypted`, `description`, `is_active`, `version`, `remark`, `dept_id`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (171, 'salesforce.pubsub.consumer.key', '', 1, 1, 1, NULL, 1, NULL, 'Salesforce Pub/Sub API Consumer Key，从Connected App获取', NULL, 'admin', '2026-01-12 00:00:00', 'admin', '2026-01-12 00:00:00');
   INSERT INTO `datai_configuration` (`id`, `config_key`, `config_value`, `environment_id`, `is_sensitive`, `is_encrypted`, `description`, `is_active`, `version`, `remark`, `dept_id`, `create_by`, `create_time`, `update_by`, `update_time`) VALUES (172, 'salesforce.pubsub.consumer.secret', '', 1, 1, 1, NULL, 1, NULL, 'Salesforce Pub/Sub API Consumer Secret，从Connected App获取', NULL, 'admin', '2026-01-12 00:00:00', 'admin', '2026-01-12 00:00:00');
   ```

2. 在 Salesforce 中创建 Connected App 并获取 Consumer Key 和 Consumer Secret

3. 通过配置管理界面录入 Consumer Key 和 Consumer Secret 到新增的配置项中

4. 重启应用以加载新的配置

### 注意事项

- 确保 Consumer Key 和 Consumer Secret 正确配置，否则 Pub/Sub API 连接可能失败
- 配置项为敏感数据，已启用加密，请妥善保管
- 需要确保 SalesforceConfigCacheManager 正确配置并能从数据库加载配置到缓存

## 测试信息

### 测试环境

- 开发环境 - 本地开发环境

### 测试结果

- 配置项添加成功，数据库中新增了两条记录
- PubSubConnectionFactory 编译通过，依赖注入正常
- 代码逻辑符合预期，能够从缓存中读取配置值

## 相关链接

- [设计文档](../design/0001-salesforce-cdc-realtime-sync.md) - Salesforce CDC实时同步设计
- [架构决策](../decisions/adr/0001-salesforce-cdc-sync.md) - Salesforce CDC同步方案
- [变更记录 0007](./0007-pubsub-api-implementation.md) - Salesforce Pub/Sub API实时同步功能实现

## 发布人员

- 开发人员 - 开发工程师

## 审核信息

- **审核人员**: 待审核
- **审核日期**: 待审核
- **审核状态**: 待审核
- **审核意见**: 待审核
