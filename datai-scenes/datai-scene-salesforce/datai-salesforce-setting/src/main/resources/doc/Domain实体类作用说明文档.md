# Salesforce配置管理模块 - Domain实体类作用说明文档

## 1. 文档概述

本文档详细说明 `datai-salesforce-setting` 模块中 `domain` 目录下所有实体类的作用、字段含义、业务用途以及它们之间的关联关系。这些实体类构成了配置管理系统的核心数据模型，支撑着配置的存储、版本控制、审计和多环境管理等功能。

## 2. 实体类总览

| 实体类名称 | 对应数据表 | 核心作用 | 关键特性 |
|-----------|-----------|---------|---------|
| DataiConfiguration | datai_configuration | 配置存储实体 | 支持多环境、敏感配置、加密存储 |
| DataiConfigEnvironment | datai_config_environment | 环境管理实体 | 环境隔离、激活状态管理 |
| DataiConfigVersion | datai_config_version | 版本管理实体 | 版本控制、快照关联、状态管理 |
| DataiConfigSnapshot | datai_config_snapshot | 快照存储实体 | 时间点快照、配置备份 |
| DataiConfigAuditLog | datai_config_audit_log | 审计日志实体 | 操作追踪、变更记录、安全审计 |

## 3. 实体类详细说明

### 3.1 DataiConfiguration - 配置实体类

#### 3.1.1 类概述
`DataiConfiguration` 是配置管理的核心实体类，对应数据库表 `datai_configuration`。它负责存储所有 Salesforce 相关的配置信息，是整个配置管理系统的基础数据模型。

**文件位置**: [DataiConfiguration.java](file:///d:/idea_demo/datai/datai-scenes/datai-scene-salesforce/datai-salesforce-setting/src/main/java/com/datai/setting/domain/DataiConfiguration.java)

#### 3.1.2 核心字段说明

| 字段名 | 类型 | 说明 | 业务用途 |
|-------|------|------|---------|
| id | Long | 配置ID（主键） | 唯一标识配置项 |
| deptId | Long | 部门ID | 支持多租户隔离，不同部门配置相互独立 |
| configKey | String | 配置键 | 唯一标识配置项的键名，如 "sf.api.endpoint" |
| configValue | String | 配置值 | 配置的实际值，如 "https://api.salesforce.com" |
| environmentId | Long | 环境ID | 关联到具体的环境（开发/测试/生产） |
| isSensitive | Boolean | 是否敏感配置 | 标记敏感配置，如密码、密钥等 |
| isEncrypted | Boolean | 是否加密存储 | 标记配置值是否需要加密存储 |
| description | String | 配置描述 | 配置项的详细说明，方便理解和维护 |
| isActive | Boolean | 是否激活 | 控制配置是否生效，支持配置的启用/禁用 |
| version | Integer | 配置版本号 | 记录配置的版本，用于乐观锁和变更追踪 |

#### 3.1.3 业务作用

**1. 配置集中存储**
- 将所有 Salesforce 相关配置集中存储在数据库中
- 支持配置的增删改查操作
- 提供配置的统一管理入口

**2. 多环境配置隔离**
- 通过 `environmentId` 字段关联到具体环境
- 不同环境的配置相互隔离，互不影响
- 支持开发、测试、生产环境的差异化配置

**3. 敏感配置保护**
- `isSensitive` 字段标记敏感配置项
- `isEncrypted` 字段控制是否加密存储
- 敏感配置在传输和存储过程中进行加密处理

**4. 配置生命周期管理**
- `isActive` 字段控制配置的启用/禁用状态
- `version` 字段记录配置版本，支持乐观锁
- 支持配置的完整生命周期管理

**5. 多租户支持**
- `deptId` 字段实现部门级别的配置隔离
- 不同部门的配置相互独立，互不干扰
- 支持企业级多租户场景

#### 3.1.4 使用场景

**场景1：API端点配置**
```java
// 配置Salesforce API端点
DataiConfiguration config = new DataiConfiguration();
config.setConfigKey("sf.api.endpoint");
config.setConfigValue("https://api.salesforce.com");
config.setEnvironmentId(1L); // 生产环境
config.setIsSensitive(false);
config.setIsEncrypted(false);
config.setIsActive(true);
config.setDescription("Salesforce API访问端点");
```

**场景2：敏感配置存储**
```java
// 配置OAuth客户端密钥
DataiConfiguration config = new DataiConfiguration();
config.setConfigKey("sf.oauth.client_secret");
config.setConfigValue("secret_value");
config.setEnvironmentId(1L);
config.setIsSensitive(true);  // 标记为敏感配置
config.setIsEncrypted(true);  // 加密存储
config.setIsActive(true);
config.setDescription("OAuth客户端密钥");
```

**场景3：环境差异化配置**
```java
// 开发环境配置
DataiConfiguration devConfig = new DataiConfiguration();
devConfig.setConfigKey("sf.api.timeout");
devConfig.setConfigValue("30000");
devConfig.setEnvironmentId(1L); // 开发环境ID

// 生产环境配置
DataiConfiguration prodConfig = new DataiConfiguration();
prodConfig.setConfigKey("sf.api.timeout");
prodConfig.setConfigValue("10000");
prodConfig.setEnvironmentId(3L); // 生产环境ID
```

---

### 3.2 DataiConfigEnvironment - 配置环境实体类

#### 3.2.1 类概述
`DataiConfigEnvironment` 是配置环境管理的实体类，对应数据库表 `datai_config_environment`。它负责定义和管理不同的配置环境，如开发环境、测试环境、生产环境等，实现配置的多环境隔离。

**文件位置**: [DataiConfigEnvironment.java](file:///d:/idea_demo/datai/datai-scenes/datai-scene-salesforce/datai-salesforce-setting/src/main/java/com/datai/setting/domain/DataiConfigEnvironment.java)

#### 3.2.2 核心字段说明

| 字段名 | 类型 | 说明 | 业务用途 |
|-------|------|------|---------|
| id | Long | 环境ID（主键） | 唯一标识环境 |
| deptId | Long | 部门ID | 支持多租户隔离，不同部门的环境相互独立 |
| environmentName | String | 环境名称 | 环境的显示名称，如 "生产环境" |
| environmentCode | String | 环境编码 | 环境的唯一编码，如 "PROD" |
| description | String | 环境描述 | 环境的详细说明 |
| isActive | Boolean | 是否激活 | 控制环境是否可用 |

#### 3.2.3 业务作用

**1. 环境定义与管理**
- 定义不同的配置环境（开发、测试、生产等）
- 提供环境的增删改查功能
- 支持环境的启用/禁用管理

**2. 配置隔离**
- 不同环境的配置相互隔离
- 同一个配置键在不同环境中可以有不同的值
- 避免环境间配置相互干扰

**3. 环境切换**
- 支持环境的动态切换
- 切换环境时自动加载对应环境的配置
- 提供环境切换事件通知

**4. 多租户支持**
- `deptId` 字段实现部门级别的环境隔离
- 不同部门可以定义自己的环境体系
- 支持企业级多租户场景

#### 3.2.4 使用场景

**场景1：环境初始化**
```java
// 创建开发环境
DataiConfigEnvironment devEnv = new DataiConfigEnvironment();
devEnv.setEnvironmentName("开发环境");
devEnv.setEnvironmentCode("DEV");
devEnv.setDescription("用于开发测试的环境");
devEnv.setIsActive(true);

// 创建测试环境
DataiConfigEnvironment testEnv = new DataiConfigEnvironment();
testEnv.setEnvironmentName("测试环境");
testEnv.setEnvironmentCode("TEST");
testEnv.setDescription("用于集成测试的环境");
testEnv.setIsActive(true);

// 创建生产环境
DataiConfigEnvironment prodEnv = new DataiConfigEnvironment();
prodEnv.setEnvironmentName("生产环境");
prodEnv.setEnvironmentCode("PROD");
prodEnv.setDescription("正式运行环境");
prodEnv.setIsActive(true);
```

**场景2：环境切换**
```java
// 查询当前激活的环境
DataiConfigEnvironment activeEnv = environmentService.selectActiveEnvironment();

// 切换到生产环境
DataiConfigEnvironment prodEnv = environmentService.selectByCode("PROD");
if (prodEnv != null && prodEnv.getIsActive()) {
    // 发布环境切换事件
    applicationEventPublisher.publishEvent(new EnvironmentSwitchEvent(prodEnv));
}
```

---

### 3.3 DataiConfigVersion - 配置版本实体类

#### 3.3.1 类概述
`DataiConfigVersion` 是配置版本管理的实体类，对应数据库表 `datai_config_version`。它负责管理配置的版本信息，支持配置的版本控制、发布和回滚功能。

**文件位置**: [DataiConfigVersion.java](file:///d:/idea_demo/datai/datai-scenes/datai-scene-salesforce/datai-salesforce-setting/src/main/java/com/datai/setting/domain/DataiConfigVersion.java)

#### 3.3.2 核心字段说明

| 字段名 | 类型 | 说明 | 业务用途 |
|-------|------|------|---------|
| id | Long | 版本ID（主键） | 唯一标识版本 |
| deptId | Long | 部门ID | 支持多租户隔离 |
| versionNumber | String | 版本号 | 版本的唯一标识，如 "v1.0.0" |
| versionDesc | String | 版本描述 | 版本的详细说明 |
| snapshotId | String | 快照ID | 关联的配置快照ID |
| snapshotContent | String | 快照内容 | 配置快照的完整内容（JSON格式） |
| status | String | 版本状态 | 版本状态：已创建、已发布 |
| publishTime | LocalDateTime | 发布时间 | 版本发布的时间 |

#### 3.3.3 业务作用

**1. 版本创建与管理**
- 创建配置版本快照
- 管理版本的生命周期
- 支持版本的增删改查操作

**2. 版本发布**
- 将配置版本发布到生产环境
- 记录版本发布时间
- 发布时自动刷新配置缓存

**3. 版本回滚**
- 支持回滚到历史版本
- 从快照恢复配置数据
- 回滚后自动刷新缓存

**4. 版本状态管理**
- 管理版本的状态流转
- 支持版本状态查询
- 确保版本操作的合法性

#### 3.3.4 使用场景

**场景1：创建版本**
```java
// 创建配置版本
DataiConfigVersion version = new DataiConfigVersion();
version.setVersionNumber("v1.0.0");
version.setVersionDesc("首次发布版本");
version.setSnapshotId(UUID.randomUUID().toString());
version.setSnapshotContent(snapshotJson); // 配置快照JSON
version.setStatus("已创建");
```

**场景2：发布版本**
```java
// 发布版本
DataiConfigVersion version = versionService.selectByVersionNumber("v1.0.0");
if (version != null && "已创建".equals(version.getStatus())) {
    version.setStatus("已发布");
    version.setPublishTime(LocalDateTime.now());
    versionService.updateDataiConfigVersion(version);
    
    // 刷新配置缓存
    cacheManager.resetConfigCache();
}
```

**场景3：回滚版本**
```java
// 回滚到指定版本
DataiConfigVersion version = versionService.selectByVersionNumber("v1.0.0");
if (version != null && "已发布".equals(version.getStatus())) {
    // 从快照恢复配置
    String snapshotContent = version.getSnapshotContent();
    List<DataiConfiguration> configs = parseSnapshotContent(snapshotContent);
    
    // 更新数据库配置
    configurationService.batchUpdateConfiguration(configs);
    
    // 刷新配置缓存
    cacheManager.resetConfigCache();
}
```

---

### 3.4 DataiConfigSnapshot - 配置快照实体类

#### 3.4.1 类概述
`DataiConfigSnapshot` 是配置快照存储的实体类，对应数据库表 `datai_config_snapshot`。它负责记录配置在特定时间点的完整状态，用于版本管理和配置回滚。

**文件位置**: [DataiConfigSnapshot.java](file:///d:/idea_demo/datai/datai-scenes/datai-scene-salesforce/datai-salesforce-setting/src/main/java/com/datai/setting/domain/DataiConfigSnapshot.java)

#### 3.4.2 核心字段说明

| 字段名 | 类型 | 说明 | 业务用途 |
|-------|------|------|---------|
| id | String | 快照ID（主键） | 唯一标识快照（UUID） |
| deptId | Long | 部门ID | 支持多租户隔离 |
| versionId | Long | 版本ID | 关联的配置版本ID |
| snapshotTime | LocalDateTime | 快照时间 | 快照创建的时间 |
| snapshotContent | String | 快照内容 | 配置快照的完整内容（JSON格式） |
| configCount | Integer | 配置数量 | 快照中包含的配置项数量 |

#### 3.4.3 业务作用

**1. 配置快照创建**
- 在特定时间点创建配置快照
- 记录所有配置项的完整状态
- 支持快照的查询和管理

**2. 配置备份**
- 作为配置的备份机制
- 支持配置的历史版本查询
- 提供配置恢复的数据源

**3. 版本关联**
- 关联到具体的配置版本
- 支持版本与快照的关联查询
- 为版本回滚提供数据支持

**4. 配置恢复**
- 从快照恢复配置数据
- 支持批量恢复配置项
- 确保恢复后配置的一致性

#### 3.4.4 使用场景

**场景1：创建快照**
```java
// 创建配置快照
List<DataiConfiguration> configs = configurationService.selectAllActiveConfigs();

DataiConfigSnapshot snapshot = new DataiConfigSnapshot();
snapshot.setId(UUID.randomUUID().toString());
snapshot.setSnapshotTime(LocalDateTime.now());
snapshot.setConfigCount(configs.size());
snapshot.setSnapshotContent(JSON.toJSONString(configs));

snapshotService.insertDataiConfigSnapshot(snapshot);
```

**场景2：从快照恢复**
```java
// 从快照恢复配置
DataiConfigSnapshot snapshot = snapshotService.selectById(snapshotId);
if (snapshot != null) {
    String snapshotContent = snapshot.getSnapshotContent();
    List<DataiConfiguration> configs = JSON.parseArray(snapshotContent, DataiConfiguration.class);
    
    // 更新数据库配置
    configurationService.batchUpdateConfiguration(configs);
    
    // 刷新配置缓存
    cacheManager.resetConfigCache();
}
```

---

### 3.5 DataiConfigAuditLog - 配置审计日志实体类

#### 3.5.1 类概述
`DataiConfigAuditLog` 是配置审计日志的实体类，对应数据库表 `datai_config_audit_log`。它负责记录所有配置操作的审计日志，实现配置变更的可追溯性和安全审计。

**文件位置**: [DataiConfigAuditLog.java](file:///d:/idea_demo/datai/datai-scenes/datai-scene-salesforce/datai-salesforce-setting/src/main/java/com/datai/setting/domain/DataiConfigAuditLog.java)

#### 3.5.2 核心字段说明

| 字段名 | 类型 | 说明 | 业务用途 |
|-------|------|------|---------|
| id | Long | 日志ID（主键） | 唯一标识审计日志 |
| deptId | Long | 部门ID | 支持多租户隔离 |
| operationType | String | 操作类型 | 操作类型：新增、修改、删除、发布、回滚等 |
| objectType | String | 对象类型 | 操作的对象类型：配置、版本、环境等 |
| objectId | Long | 对象ID | 操作对象的ID |
| oldValue | String | 旧值 | 操作前的值（修改操作） |
| newValue | String | 新值 | 操作后的值（修改操作） |
| operationDesc | String | 操作描述 | 操作的详细说明 |
| operator | String | 操作人 | 执行操作的用户 |
| operationTime | LocalDateTime | 操作时间 | 操作执行的时间 |
| ipAddress | String | IP地址 | 操作来源的IP地址 |
| userAgent | String | 用户代理 | 操作的浏览器/客户端信息 |
| requestId | String | 请求ID | 关联的请求ID，用于追踪 |
| result | String | 操作结果 | 操作结果：成功、失败 |
| errorMessage | String | 错误信息 | 操作失败时的错误信息 |

#### 3.5.3 业务作用

**1. 操作审计**
- 记录所有配置操作
- 追踪配置变更历史
- 支持操作审计和合规性检查

**2. 安全监控**
- 记录操作人、IP地址、时间等信息
- 支持异常操作检测
- 提供安全审计依据

**3. 问题排查**
- 记录操作的详细信息和错误信息
- 支持问题定位和排查
- 提供操作回溯能力

**4. 合规性要求**
- 满足审计合规性要求
- 支持审计日志导出
- 提供完整的操作记录

#### 3.5.4 使用场景

**场景1：记录配置新增**
```java
// 记录配置新增操作
DataiConfigAuditLog auditLog = new DataiConfigAuditLog();
auditLog.setOperationType("新增");
auditLog.setObjectType("配置");
auditLog.setObjectId(config.getId());
auditLog.setNewValue(JSON.toJSONString(config));
auditLog.setOperationDesc("新增配置项：" + config.getConfigKey());
auditLog.setOperator(SecurityUtils.getUsername());
auditLog.setOperationTime(LocalDateTime.now());
auditLog.setIpAddress(ServletUtils.getClientIP());
auditLog.setUserAgent(ServletUtils.getRequest().getHeader("User-Agent"));
auditLog.setRequestId(MDC.get("traceId"));
auditLog.setResult("成功");

auditLogService.insertDataiConfigAuditLog(auditLog);
```

**场景2：记录配置修改**
```java
// 记录配置修改操作
DataiConfigAuditLog auditLog = new DataiConfigAuditLog();
auditLog.setOperationType("修改");
auditLog.setObjectType("配置");
auditLog.setObjectId(config.getId());
auditLog.setOldValue(JSON.toJSONString(oldConfig));
auditLog.setNewValue(JSON.toJSONString(newConfig));
auditLog.setOperationDesc("修改配置项：" + config.getConfigKey());
auditLog.setOperator(SecurityUtils.getUsername());
auditLog.setOperationTime(LocalDateTime.now());
auditLog.setIpAddress(ServletUtils.getClientIP());
auditLog.setUserAgent(ServletUtils.getRequest().getHeader("User-Agent"));
auditLog.setRequestId(MDC.get("traceId"));
auditLog.setResult("成功");

auditLogService.insertDataiConfigAuditLog(auditLog);
```

**场景3：记录版本回滚**
```java
// 记录版本回滚操作
DataiConfigAuditLog auditLog = new DataiConfigAuditLog();
auditLog.setOperationType("回滚");
auditLog.setObjectType("版本");
auditLog.setObjectId(version.getId());
auditLog.setOperationDesc("回滚到版本：" + version.getVersionNumber());
auditLog.setOperator(SecurityUtils.getUsername());
auditLog.setOperationTime(LocalDateTime.now());
auditLog.setIpAddress(ServletUtils.getClientIP());
auditLog.setUserAgent(ServletUtils.getRequest().getHeader("User-Agent"));
auditLog.setRequestId(MDC.get("traceId"));
auditLog.setResult("成功");

auditLogService.insertDataiConfigAuditLog(auditLog);
```

---

## 4. 实体类关联关系

### 4.1 ER关系图

```
DataiConfiguration (配置)
    |
    | environmentId (N:1)
    |
DataiConfigEnvironment (环境)

DataiConfigVersion (版本)
    |
    | snapshotId (1:1)
    |
DataiConfigSnapshot (快照)

DataiConfigAuditLog (审计日志)
    |
    | objectId (N:1)
    |
DataiConfiguration / DataiConfigVersion (配置/版本)
```

### 4.2 关联关系说明

**1. DataiConfiguration 与 DataiConfigEnvironment**
- 关系类型：多对一（N:1）
- 关联字段：`DataiConfiguration.environmentId` → `DataiConfigEnvironment.id`
- 业务含义：一个配置项属于一个环境，一个环境包含多个配置项
- 级联操作：删除环境时需要检查是否有关联的配置项

**2. DataiConfigVersion 与 DataiConfigSnapshot**
- 关系类型：一对一（1:1）
- 关联字段：`DataiConfigVersion.snapshotId` → `DataiConfigSnapshot.id`
- 业务含义：一个版本对应一个快照，一个快照属于一个版本
- 级联操作：删除版本时级联删除对应的快照

**3. DataiConfigAuditLog 与 DataiConfiguration/DataiConfigVersion**
- 关系类型：多对一（N:1）
- 关联字段：`DataiConfigAuditLog.objectId` → `DataiConfiguration.id` 或 `DataiConfigVersion.id`
- 业务含义：一条审计日志记录对一个配置或版本的操作
- 级联操作：删除配置或版本时保留审计日志，用于历史追溯

---

## 5. 实体类在业务流程中的作用

### 5.1 配置加载流程

```
系统启动
    ↓
查询 DataiConfigEnvironment (获取激活环境)
    ↓
查询 DataiConfiguration (加载对应环境的配置)
    ↓
存入 Redis 缓存
    ↓
记录 DataiConfigAuditLog (配置加载日志)
```

**涉及的实体类**：
- `DataiConfigEnvironment`：确定当前激活的环境
- `DataiConfiguration`：加载配置数据
- `DataiConfigAuditLog`：记录配置加载操作

### 5.2 配置更新流程

```
客户端请求更新配置
    ↓
验证配置合法性
    ↓
更新 DataiConfiguration (数据库)
    ↓
更新 Redis 缓存
    ↓
记录 DataiConfigAuditLog (配置更新日志)
    ↓
发布 ConfigChangeEvent (配置变更事件)
```

**涉及的实体类**：
- `DataiConfiguration`：更新配置数据
- `DataiConfigAuditLog`：记录配置更新操作

### 5.3 版本发布流程

```
创建配置快照
    ↓
生成 DataiConfigSnapshot (快照数据)
    ↓
创建 DataiConfigVersion (版本信息)
    ↓
关联快照ID到版本
    ↓
记录 DataiConfigAuditLog (版本创建日志)
    ↓
发布版本
    ↓
更新版本状态为"已发布"
    ↓
刷新配置缓存
```

**涉及的实体类**：
- `DataiConfigSnapshot`：存储配置快照
- `DataiConfigVersion`：管理版本信息
- `DataiConfigAuditLog`：记录版本操作日志

### 5.4 版本回滚流程

```
查询目标版本
    ↓
验证版本合法性
    ↓
获取 DataiConfigVersion (版本信息)
    ↓
获取 DataiConfigSnapshot (快照数据)
    ↓
解析快照内容
    ↓
更新 DataiConfiguration (恢复配置)
    ↓
刷新配置缓存
    ↓
记录 DataiConfigAuditLog (回滚日志)
```

**涉及的实体类**：
- `DataiConfigVersion`：获取版本信息
- `DataiConfigSnapshot`：获取快照数据
- `DataiConfiguration`：恢复配置数据
- `DataiConfigAuditLog`：记录回滚操作日志

---

## 6. 实体类设计原则

### 6.1 继承 BaseEntity
所有实体类都继承自 `BaseEntity`，获得以下基础字段：
- `createBy`：创建人
- `createTime`：创建时间
- `updateBy`：更新人
- `updateTime`：更新时间
- `remark`：备注

这些字段提供了通用的审计信息，支持数据变更的追踪。

### 6.2 使用注解增强功能

**1. Swagger 注解**
- `@Schema`：用于生成 API 文档
- 提供字段的描述信息，方便前端开发人员理解

**2. Excel 注解**
- `@Excel`：用于 Excel 导出功能
- 支持配置数据的批量导入导出

**3. JSON 注解**
- `@JsonFormat`：用于日期格式化
- 确保日期字段在 JSON 序列化时格式正确

### 6.3 多租户支持
所有实体类都包含 `deptId` 字段，实现部门级别的数据隔离：
- 不同部门的数据相互独立
- 支持企业级多租户场景
- 确保数据安全和隔离性

### 6.4 审计日志
所有关键操作都记录到 `DataiConfigAuditLog`：
- 记录操作类型、操作人、操作时间
- 记录操作前后的值（修改操作）
- 记录操作结果和错误信息
- 支持操作审计和问题排查

---

## 7. 实体类使用最佳实践

### 7.1 配置管理

**1. 配置键命名规范**
- 使用点分隔的层级结构：`模块.子模块.配置项`
- 示例：`sf.api.endpoint`、`sf.oauth.client_id`

**2. 敏感配置处理**
- 标记 `isSensitive = true`
- 标记 `isEncrypted = true`
- 在日志中脱敏处理

**3. 配置版本管理**
- 每次修改配置时递增版本号
- 使用乐观锁防止并发修改
- 定期创建版本快照

### 7.2 环境管理

**1. 环境编码规范**
- 使用大写字母：`DEV`、`TEST`、`PROD`
- 保持编码简洁明了
- 避免使用特殊字符

**2. 环境切换**
- 切换环境时验证环境状态
- 确保环境配置完整
- 发布环境切换事件

### 7.3 版本管理

**1. 版本号规范**
- 使用语义化版本号：`v1.0.0`
- 主版本号.次版本号.修订号
- 记录版本变更说明

**2. 快照管理**
- 定期创建配置快照
- 快照内容使用 JSON 格式
- 记录快照中的配置数量

### 7.4 审计日志

**1. 日志记录时机**
- 配置增删改操作
- 版本发布和回滚操作
- 环境切换操作

**2. 日志记录内容**
- 操作类型和对象类型
- 操作前后的值（修改操作）
- 操作人、时间、IP地址
- 操作结果和错误信息

**3. 日志查询**
- 支持按时间范围查询
- 支持按操作人查询
- 支持按操作类型查询
- 支持日志导出

---

## 8. 总结

`datai-salesforce-setting` 模块的 domain 实体类构成了配置管理系统的核心数据模型，支撑着配置的存储、版本控制、审计和多环境管理等功能。

**核心实体类**：
1. **DataiConfiguration**：配置存储实体，支持多环境、敏感配置、加密存储
2. **DataiConfigEnvironment**：环境管理实体，实现配置的多环境隔离
3. **DataiConfigVersion**：版本管理实体，支持版本控制、发布和回滚
4. **DataiConfigSnapshot**：快照存储实体，记录配置的时间点状态
5. **DataiConfigAuditLog**：审计日志实体，记录所有配置操作

**设计特点**：
- 支持多租户隔离
- 支持多环境配置
- 支持版本控制和回滚
- 支持操作审计和追踪
- 支持敏感配置保护

**业务价值**：
- 提供集中式配置管理
- 确保配置的安全性和可靠性
- 支持配置的版本控制和回滚
- 提供完整的操作审计记录
- 支持企业级多租户场景

通过这些实体类，`datai-salesforce-setting` 模块为 Salesforce 集成系统提供了可靠的配置管理解决方案，确保配置的安全性、可靠性和高性能。

---

**文档更新时间**：2025-12-26
**文档版本**：1.0
**文档作者**：TraeAI
**所属模块**：datai-salesforce-setting
