# API Controller 方法清单文档

## 文档说明
本文档记录了Salesforce集成模块中所有Controller的现有方法清单，以及建议需要增加的方法清单。

---

## 一、现有Controller方法清单

### 1. DataiIntegrationBatchController - 数据批次管理
**路径：** `/integration/batch`

| 序号 | 方法名 | HTTP方法 | 路径 | 说明 | 权限 |
|------|--------|----------|------|------|------|
| 1 | list | GET | /list | 查询数据批次列表 | integration:batch:list |
| 2 | export | POST | /export | 导出数据批次列表 | integration:batch:export |
| 3 | getInfo | GET | /{id} | 获取数据批次详细信息 | integration:batch:query |
| 4 | add | POST | / | 新增数据批次 | integration:batch:add |
| 5 | edit | PUT | / | 修改数据批次 | integration:batch:edit |
| 6 | remove | DELETE | /{ids} | 删除数据批次 | integration:batch:remove |

---

### 2. DataiIntegrationBatchHistoryController - 数据批次历史管理
**路径：** `/integration/batchhistory`

| 序号 | 方法名 | HTTP方法 | 路径 | 说明 | 权限 |
|------|--------|----------|------|------|------|
| 1 | list | GET | /list | 查询数据批次历史列表 | integration:batchhistory:list |
| 2 | export | POST | /export | 导出数据批次历史列表 | integration:batchhistory:export |
| 3 | getInfo | GET | /{id} | 获取数据批次历史详细信息 | integration:batchhistory:query |
| 4 | add | POST | / | 新增数据批次历史 | integration:batchhistory:add |
| 5 | edit | PUT | / | 修改数据批次历史 | integration:batchhistory:edit |
| 6 | remove | DELETE | /{ids} | 删除数据批次历史 | integration:batchhistory:remove |

---

### 3. DataiIntegrationFieldController - 对象字段信息管理
**路径：** `/integration/field`

| 序号 | 方法名 | HTTP方法 | 路径 | 说明 | 权限 |
|------|--------|----------|------|------|------|
| 1 | list | GET | /list | 查询对象字段信息列表 | integration:field:list |
| 2 | export | POST | /export | 导出对象字段信息列表 | integration:field:export |
| 3 | getInfo | GET | /{id} | 获取对象字段信息详细信息 | integration:field:query |
| 4 | add | POST | / | 新增对象字段信息 | integration:field:add |
| 5 | edit | PUT | / | 修改对象字段信息 | integration:field:edit |
| 6 | remove | DELETE | /{ids} | 删除对象字段信息 | integration:field:remove |

---

### 4. DataiIntegrationFilterLookupController - 字段过滤查找信息管理
**路径：** `/integration/lookup`

| 序号 | 方法名 | HTTP方法 | 路径 | 说明 | 权限 |
|------|--------|----------|------|------|------|
| 1 | list | GET | /list | 查询字段过滤查找信息列表 | integration:lookup:list |
| 2 | export | POST | /export | 导出字段过滤查找信息列表 | integration:lookup:export |
| 3 | getInfo | GET | /{id} | 获取字段过滤查找信息详细信息 | integration:lookup:query |
| 4 | add | POST | / | 新增字段过滤查找信息 | integration:lookup:add |
| 5 | edit | PUT | / | 修改字段过滤查找信息 | integration:lookup:edit |
| 6 | remove | DELETE | /{ids} | 删除字段过滤查找信息 | integration:lookup:remove |

---

### 5. DataiIntegrationMetadataChangeController - 对象元数据变更管理
**路径：** `/integration/change`

| 序号 | 方法名 | HTTP方法 | 路径 | 说明 | 权限 |
|------|--------|----------|------|------|------|
| 1 | list | GET | /list | 查询对象元数据变更列表 | integration:change:list |
| 2 | getUnsyncedChanges | GET | /unsynced | 查询未同步的元数据变更列表 | integration:change:unsynced |
| 3 | export | POST | /export | 导出对象元数据变更列表 | integration:change:export |
| 4 | getInfo | GET | /{id} | 获取对象元数据变更详细信息 | integration:change:query |
| 5 | add | POST | / | 新增对象元数据变更 | integration:change:add |
| 6 | edit | PUT | / | 修改对象元数据变更 | integration:change:edit |
| 7 | remove | DELETE | /{ids} | 删除对象元数据变更 | integration:change:remove |
| 8 | updateSyncStatus | PUT | /syncStatus | 批量更新元数据变更同步状态 | integration:change:updateSync |

---

### 6. DataiIntegrationObjectController - 对象同步控制管理
**路径：** `/integration/object`

| 序号 | 方法名 | HTTP方法 | 路径 | 说明 | 权限 |
|------|--------|----------|------|------|------|
| 1 | list | GET | /list | 查询对象同步控制列表 | integration:object:list |
| 2 | export | POST | /export | 导出对象同步控制列表 | integration:object:export |
| 3 | getInfo | GET | /{id} | 获取对象同步控制详细信息 | integration:object:query |
| 4 | add | POST | / | 新增对象同步控制 | integration:object:add |
| 5 | edit | PUT | / | 修改对象同步控制 | integration:object:edit |
| 6 | remove | DELETE | /{ids} | 删除对象同步控制 | integration:object:remove |

---

### 7. DataiIntegrationPicklistController - 字段选择列表信息管理
**路径：** `/integration/picklist`

| 序号 | 方法名 | HTTP方法 | 路径 | 说明 | 权限 |
|------|--------|----------|------|------|------|
| 1 | list | GET | /list | 查询字段选择列表信息列表 | integration:picklist:list |
| 2 | export | POST | /export | 导出字段选择列表信息列表 | integration:picklist:export |
| 3 | getInfo | GET | /{id} | 获取字段选择列表信息详细信息 | integration:picklist:query |
| 4 | add | POST | / | 新增字段选择列表信息 | integration:picklist:add |
| 5 | edit | PUT | / | 修改字段选择列表信息 | integration:picklist:edit |
| 6 | remove | DELETE | /{ids} | 删除字段选择列表信息 | integration:picklist:remove |

---

### 8. DataiIntegrationRateLimitController - API限流监控管理
**路径：** `/integration/limit`

| 序号 | 方法名 | HTTP方法 | 路径 | 说明 | 权限 |
|------|--------|----------|------|------|------|
| 1 | list | GET | /list | 查询API限流监控列表 | integration:limit:list |
| 2 | export | POST | /export | 导出API限流监控列表 | integration:limit:export |
| 3 | getInfo | GET | /{id} | 获取API限流监控详细信息 | integration:limit:query |
| 4 | add | POST | / | 新增API限流监控 | integration:limit:add |
| 5 | edit | PUT | / | 修改API限流监控 | integration:limit:edit |
| 6 | remove | DELETE | /{ids} | 删除API限流监控 | integration:limit:remove |

---

### 9. DataiIntegrationSyncLogController - 数据同步日志管理
**路径：** `/integration/synclog`

| 序号 | 方法名 | HTTP方法 | 路径 | 说明 | 权限 |
|------|--------|----------|------|------|------|
| 1 | list | GET | /list | 查询数据同步日志列表 | integration:synclog:list |
| 2 | export | POST | /export | 导出数据同步日志列表 | integration:synclog:export |
| 3 | getInfo | GET | /{id} | 获取数据同步日志详细信息 | integration:synclog:query |
| 4 | add | POST | / | 新增数据同步日志 | integration:synclog:add |
| 5 | edit | PUT | / | 修改数据同步日志 | integration:synclog:edit |
| 6 | remove | DELETE | /{ids} | 删除数据同步日志 | integration:synclog:remove |

---

### 10. SalesforceRateLimitController - Salesforce API限流管理
**路径：** `/api/rate-limit`

| 序号 | 方法名 | HTTP方法 | 路径 | 说明 |
|------|--------|----------|------|------|
| 1 | getStatus | GET | /status | 获取限流状态信息 |
| 2 | getStatusByType | GET | /status/{apiType} | 获取指定API类型的限流状态 |
| 3 | getAvailableTokens | GET | /tokens/{apiType} | 获取可用令牌数 |
| 4 | getDailyUsedRequests | GET | /used/{apiType} | 获取今日已使用的API请求数 |
| 5 | getDailyRemainingRequests | GET | /remaining/{apiType} | 获取今日剩余的API请求数 |
| 6 | reset | POST | /reset | 重置限流计数器 |

---

## 二、建议需要增加的方法清单

### 1. DataiIntegrationBatchController - 数据批次管理

| 序号 | 方法名 | HTTP方法 | 路径 | 说明 | 优先级 |
|------|--------|----------|------|------|--------|
| 1 | startSync | POST | /{id}/start | 启动批次同步 | 高 |
| 2 | stopSync | POST | /{id}/stop | 停止批次同步 | 高 |
| 3 | pauseSync | POST | /{id}/pause | 暂停批次同步 | 中 |
| 4 | resumeSync | POST | /{id}/resume | 恢复批次同步 | 中 |
| 5 | getSyncProgress | GET | /{id}/progress | 获取批次同步进度 | 高 |
| 6 | retryFailed | POST | /{id}/retry | 重试失败的批次 | 中 |
| 7 | getSyncStatistics | GET | /{id}/statistics | 获取批次同步统计信息 | 中 |
| 8 | batchStart | POST | /batch/start | 批量启动多个批次 | 低 |
| 9 | batchStop | POST | /batch/stop | 批量停止多个批次 | 低 |
| 10 | getRecentBatches | GET | /recent | 获取最近执行的批次 | 中 |

---

### 2. DataiIntegrationBatchHistoryController - 数据批次历史管理

| 序号 | 方法名 | HTTP方法 | 路径 | 说明 | 优先级 |
|------|--------|----------|------|------|--------|
| 1 | getHistoryByBatchId | GET | /batch/{batchId} | 根据批次ID获取历史记录 | 高 |
| 2 | getRecentHistory | GET | /recent | 获取最近的历史记录 | 中 |
| 3 | getFailedHistory | GET | /failed | 获取失败的历史记录 | 高 |
| 4 | getSuccessHistory | GET | /success | 获取成功的历史记录 | 中 |
| 5 | getHistoryStatistics | GET | /statistics | 获取历史统计信息 | 中 |
| 6 | exportByDateRange | POST | /export/daterange | 按日期范围导出历史记录 | 中 |
| 7 | deleteOldHistory | DELETE | /old/{days} | 删除指定天数之前的历史记录 | 低 |
| 8 | compareHistory | POST | /compare | 比较两次历史记录的差异 | 低 |

---

### 3. DataiIntegrationFieldController - 对象字段信息管理

| 序号 | 方法名 | HTTP方法 | 路径 | 说明 | 优先级 |
|------|--------|----------|------|------|--------|
| 1 | getFieldsByObject | GET | /object/{objectApiName} | 根据对象API名称获取字段列表 | 高 |
| 2 | syncFieldsFromSalesforce | POST | /sync/{objectApiName} | 从Salesforce同步字段信息 | 高 |
| 3 | getFieldTypes | GET | /types | 获取所有字段类型 | 中 |
| 4 | getRequiredFields | GET | /required/{objectApiName} | 获取必填字段 | 中 |
| 5 | getCustomFields | GET | /custom/{objectApiName} | 获取自定义字段 | 中 |
| 6 | getStandardFields | GET | /standard/{objectApiName} | 获取标准字段 | 中 |
| 7 | validateFieldMapping | POST | /validate/mapping | 验证字段映射 | 中 |
| 8 | batchUpdateFields | PUT | /batch/update | 批量更新字段信息 | 低 |
| 9 | getFieldDependencies | GET | /dependencies/{fieldId} | 获取字段依赖关系 | 低 |
| 10 | exportFieldMapping | POST | /export/mapping | 导出字段映射关系 | 中 |

---

### 4. DataiIntegrationFilterLookupController - 字段过滤查找信息管理

| 序号 | 方法名 | HTTP方法 | 路径 | 说明 | 优先级 |
|------|--------|----------|------|------|--------|
| 1 | testLookup | POST | /test | 测试查找过滤条件 | 高 |
| 2 | getLookupByField | GET | /field/{fieldId} | 根据字段ID获取查找过滤 | 高 |
| 3 | syncLookupFromSalesforce | POST | /sync/{fieldId} | 从Salesforce同步查找过滤 | 高 |
| 4 | validateLookup | POST | /validate | 验证查找过滤配置 | 中 |
| 5 | getLookupStatistics | GET | /statistics | 获取查找过滤统计信息 | 中 |
| 6 | batchUpdateLookup | PUT | /batch/update | 批量更新查找过滤 | 低 |
| 7 | exportLookupConfig | POST | /export | 导出查找过滤配置 | 中 |

---

### 5. DataiIntegrationMetadataChangeController - 对象元数据变更管理

| 序号 | 方法名 | HTTP方法 | 路径 | 说明 | 优先级 |
|------|--------|----------|------|------|--------|
| 1 | syncChanges | POST | /sync | 同步元数据变更 | 高 |
| 2 | getChangeDiff | GET | /diff/{id} | 获取变更差异详情 | 高 |
| 3 | rollbackChange | POST | /rollback/{id} | 回滚元数据变更 | 高 |
| 4 | getChangeHistory | GET | /history/{objectApiName} | 获取对象变更历史 | 中 |
| 5 | getPendingChanges | GET | /pending | 获取待处理的变更 | 中 |
| 6 | approveChange | POST | /approve/{id} | 审批元数据变更 | 中 |
| 7 | rejectChange | POST | /reject/{id} | 拒绝元数据变更 | 中 |
| 8 | getChangeStatistics | GET | /statistics | 获取变更统计信息 | 中 |
| 9 | exportChangeReport | POST | /export/report | 导出变更报告 | 中 |
| 10 | batchSyncChanges | POST | /batch/sync | 批量同步变更 | 低 |

---

### 6. DataiIntegrationObjectController - 对象同步控制管理

| 序号 | 方法名 | HTTP方法 | 路径 | 说明 | 优先级 |
|------|--------|----------|------|------|--------|
| 1 | startObjectSync | POST | /{id}/start | 启动对象同步 | 高 |
| 2 | stopObjectSync | POST | /{id}/stop | 停止对象同步 | 高 |
| 3 | getSyncStatus | GET | /{id}/status | 获取对象同步状态 | 高 |
| 4 | getSyncProgress | GET | /{id}/progress | 获取对象同步进度 | 高 |
| 5 | retryObjectSync | POST | /{id}/retry | 重试对象同步 | 中 |
| 6 | getSyncStatistics | GET | /{id}/statistics | 获取对象同步统计信息 | 中 |
| 7 | batchStartSync | POST | /batch/start | 批量启动对象同步 | 中 |
| 8 | batchStopSync | POST | /batch/stop | 批量停止对象同步 | 中 |
| 9 | getObjectDependencies | GET | /{id}/dependencies | 获取对象依赖关系 | 低 |
| 10 | exportSyncConfig | POST | /{id}/export/config | 导出同步配置 | 中 |

---

### 7. DataiIntegrationPicklistController - 字段选择列表信息管理

| 序号 | 方法名 | HTTP方法 | 路径 | 说明 | 优先级 |
|------|--------|----------|------|------|--------|
| 1 | getPicklistByField | GET | /field/{fieldId} | 根据字段ID获取选择列表 | 高 |
| 2 | syncPicklistFromSalesforce | POST | /sync/{fieldId} | 从Salesforce同步选择列表 | 高 |
| 3 | validatePicklist | POST | /validate | 验证选择列表配置 | 中 |
| 4 | getActivePicklistValues | GET | /active/{fieldId} | 获取激活的选择列表值 | 中 |
| 5 | getInactivePicklistValues | GET | /inactive/{fieldId} | 获取未激活的选择列表值 | 中 |
| 6 | batchUpdatePicklist | PUT | /batch/update | 批量更新选择列表 | 低 |
| 7 | exportPicklistConfig | POST | /export | 导出选择列表配置 | 中 |
| 8 | importPicklistConfig | POST | /import | 导入选择列表配置 | 中 |

---

### 8. DataiIntegrationRateLimitController - API限流监控管理

| 序号 | 方法名 | HTTP方法 | 路径 | 说明 | 优先级 |
|------|--------|----------|------|------|--------|
| 1 | getRealTimeStatus | GET | /realtime | 获取实时限流状态 | 高 |
| 2 | getAlerts | GET | /alerts | 获取限流告警信息 | 高 |
| 3 | configureLimit | PUT | /configure | 配置限流参数 | 高 |
| 4 | getUsageStatistics | GET | /statistics | 获取API使用统计 | 中 |
| 5 | getTrend | GET | /trend | 获取API使用趋势 | 中 |
| 4 | exportStatistics | POST | /export/statistics | 导出统计信息 | 中 |
| 5 | resetAlert | POST | /alert/reset | 重置告警 | 低 |

---

### 9. DataiIntegrationSyncLogController - 数据同步日志管理

| 序号 | 方法名 | HTTP方法 | 路径 | 说明 | 优先级 |
|------|--------|----------|------|------|--------|
| 1 | getLogsByBatchId | GET | /batch/{batchId} | 根据批次ID获取日志 | 高 |
| 2 | getLogsByObjectId | GET | /object/{objectId} | 根据对象ID获取日志 | 高 |
| 3 | getErrorLogs | GET | /error | 获取错误日志 | 高 |
| 4 | getSuccessLogs | GET | /success | 获取成功日志 | 中 |
| 5 | getRecentLogs | GET | /recent | 获取最近的日志 | 中 |
| 6 | getLogStatistics | GET | /statistics | 获取日志统计信息 | 中 |
| 5 | exportLogsByDateRange | POST | /export/daterange | 按日期范围导出日志 | 中 |
| 6 | deleteOldLogs | DELETE | /old/{days} | 删除指定天数之前的日志 | 低 |
| 7 | getLogDetails | GET | /{id}/details | 获取日志详细信息 | 中 |
| 8 | retryFailedSync | POST | /retry/{id} | 重试失败的同步 | 中 |

---

### 10. SalesforceRateLimitController - Salesforce API限流管理

| 序号 | 方法名 | HTTP方法 | 路径 | 说明 | 优先级 |
|------|--------|----------|------|------|--------|
| 1 | getRealTimeStatus | GET | /realtime | 获取实时限流状态 | 高 |
| 2 | getAlerts | GET | /alerts | 获取限流告警信息 | 高 |
| 3 | configureLimit | PUT | /configure | 配置限流参数 | 高 |
| 4 | getUsageStatistics | GET | /statistics | 获取API使用统计 | 中 |
| 5 | getTrend | GET | /trend | 获取API使用趋势 | 中 |
| 6 | exportStatistics | POST | /export/statistics | 导出统计信息 | 中 |
| 7 | resetAlert | POST | /alert/reset | 重置告警 | 低 |

---

## 三、优先级说明

- **高优先级**：核心业务功能，建议优先实现
- **中优先级**：增强功能，提升用户体验
- **低优先级**：辅助功能，可根据实际需求选择性实现

---

## 四、实施建议

1. **分阶段实施**：按照优先级分阶段实现，优先实现高优先级功能
2. **统一规范**：遵循现有的代码规范和命名约定
3. **权限控制**：为新增的方法添加适当的权限控制注解
4. **日志记录**：为关键操作添加日志记录
5. **异常处理**：完善异常处理机制
6. **API文档**：使用Swagger注解完善API文档
7. **单元测试**：为新增方法编写单元测试

---

## 五、版本历史

| 版本 | 日期 | 修改内容 | 修改人 |
|------|------|----------|--------|
| 1.0 | 2025-12-27 | 初始版本，记录现有方法和建议新增方法 | datai |
