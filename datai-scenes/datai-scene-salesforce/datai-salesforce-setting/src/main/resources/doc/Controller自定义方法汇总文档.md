# Controller 自定义方法汇总文档

## 概述

本文档汇总了 `d:\idea_demo\datai\datai-scenes\datai-scene-salesforce\datai-salesforce-setting\src\main\java\com\datai\setting\controller` 目录下所有 Controller 中非模板生成的自定义方法。

## 模板生成方法说明

根据 `controller.java.vm` 模板，以下方法为模板自动生成的标准方法：
- `list()` - 查询列表
- `export()` - 导出列表
- `getInfo()` - 获取详细信息
- `add()` - 新增
- `edit()` - 修改
- `remove()` - 删除

## 自定义方法清单

### 1. DataiConfigEnvironmentController

#### 1.1 切换当前环境
- **方法名**: `switchEnvironment`
- **请求路径**: `POST /setting/environment/switch`
- **权限**: `setting:environment:switch`
- **参数**:
  - `environmentCode` (String, 必填) - 环境编码
  - `switchReason` (String, 可选) - 切换原因，默认为"手动切换"
- **功能说明**: 切换当前激活的环境，支持记录切换原因
- **返回值**: 成功返回切换后的环境名称，失败返回错误信息

#### 1.2 获取当前激活的环境
- **方法名**: `getCurrentEnvironment`
- **请求路径**: `GET /setting/environment/current`
- **权限**: `setting:environment:query`
- **参数**: 无
- **功能说明**: 获取当前激活的环境信息
- **返回值**: 当前激活的环境对象

### 2. DataiConfigSnapshotController

#### 2.1 从当前配置生成快照
- **方法名**: `createSnapshot`
- **请求路径**: `POST /setting/snapshot/create`
- **权限**: `setting:snapshot:create`
- **参数**:
  - `snapshotNumber` (String) - 快照编号
  - `environmentId` (Long) - 环境ID
  - `remark` (String) - 备注
- **功能说明**: 从当前环境的配置生成快照，自动记录快照时间和配置数量
- **返回值**: 创建的快照对象

#### 2.2 恢复快照
- **方法名**: `restoreSnapshot`
- **请求路径**: `POST /setting/snapshot/{snapshotId}/restore`
- **权限**: `setting:snapshot:restore`
- **参数**:
  - `snapshotId` (String, 路径参数) - 快照ID
  - `remark` (String) - 恢复原因
- **功能说明**: 将指定快照的配置恢复到当前环境，会先删除当前环境的所有配置
- **返回值**: 恢复的配置数量

#### 2.3 获取快照详细信息（包含配置内容）
- **方法名**: `getSnapshotDetail`
- **请求路径**: `GET /setting/snapshot/{snapshotId}/detail`
- **权限**: `setting:snapshot:query`
- **参数**:
  - `snapshotId` (String, 路径参数) - 快照ID
- **功能说明**: 获取快照的详细信息，包括快照内容（JSON格式的配置列表）
- **返回值**: 快照对象（包含配置内容）

#### 2.4 比较两个快照的差异
- **方法名**: `compareSnapshots`
- **请求路径**: `GET /setting/snapshot/{snapshotId1}/compare/{snapshotId2}`
- **权限**: `setting:snapshot:query`
- **参数**:
  - `snapshotId1` (String, 路径参数) - 第一个快照ID
  - `snapshotId2` (String, 路径参数) - 第二个快照ID
- **功能说明**: 比较两个快照之间的配置差异，返回差异信息
- **返回值**: 差异信息字符串

### 3. DataiConfigurationController

#### 3.1 刷新配置缓存
- **方法名**: `refreshConfigCache`
- **请求路径**: `POST /setting/configuration/refresh`
- **权限**: `setting:configuration:refresh`
- **参数**: 无
- **功能说明**: 刷新配置缓存，清除并重新加载配置
- **返回值**: 成功提示信息

#### 3.2 查询配置缓存状态
- **方法名**: `getConfigCacheStatus`
- **请求路径**: `GET /setting/configuration/cache`
- **权限**: `setting:configuration:cache`
- **参数**: 无
- **功能说明**: 查询配置缓存的状态
- **返回值**: 缓存状态信息

### 4. DataiConfigAuditLogController

**说明**: 该 Controller 所有方法均为模板生成的标准方法，无自定义方法。

## 统计汇总

| Controller 类 | 自定义方法数量 | 方法列表 |
|--------------|--------------|---------|
| DataiConfigEnvironmentController | 2 | switchEnvironment, getCurrentEnvironment |
| DataiConfigSnapshotController | 4 | createSnapshot, restoreSnapshot, getSnapshotDetail, compareSnapshots |
| DataiConfigurationController | 2 | refreshConfigCache, getConfigCacheStatus |
| DataiConfigAuditLogController | 0 | 无 |
| **总计** | **8** | - |

## 功能模块说明

### 环境管理模块
- 支持多环境切换
- 提供当前环境查询功能
- 记录环境切换历史

### 快照管理模块
- 支持从当前配置创建快照
- 支持快照恢复功能
- 支持快照对比功能
- 支持快照详情查询（包含配置内容）

### 配置管理模块
- 支持配置缓存刷新
- 支持配置缓存状态查询

## 注意事项

1. 所有自定义方法都使用了 `@PreAuthorize` 注解进行权限控制
2. 所有自定义方法都使用了 `@Operation` 注解进行 API 文档说明
3. 部分方法使用了 `@Log` 注解记录操作日志
4. 快照相关的操作涉及配置的序列化和反序列化，需要注意数据格式
5. 环境切换和快照恢复都是重要操作，建议添加额外的确认机制

## 生成日期

2025-12-26
