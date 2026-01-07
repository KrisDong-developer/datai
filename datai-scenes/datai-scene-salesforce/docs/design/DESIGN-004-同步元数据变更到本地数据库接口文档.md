# 同步元数据变更到本地数据库接口文档

## 文档信息
- **版本**: 1.0
- **更新时间**: 2026-01-07
- **来源**: datai-salesforce-integration/src/main/resources/doc/同步元数据变更到本地数据库接口文档.md
- **状态**: 已迁移

## 1. 接口概述

### 1.1 接口描述
将指定的元数据变更同步到本地数据库。该接口根据元数据变更记录中的变更类型（OBJECT或FIELD）和操作类型（INSERT、UPDATE、DELETE）执行相应的同步操作，包括对象的创建/修改/删除、字段的创建/修改/删除，以及相应的数据库表结构和批次的创建。

### 1.2 接口地址
```
POST /integration/change/{id}/sync
```

### 1.3 权限要求
```
@PreAuthorize("@ss.hasPermi('integration:change:sync')")
```

### 1.4 日志记录
```
@Log(title = "同步元数据变更", businessType = BusinessType.UPDATE)
```

## 2. 请求参数

### 2.1 路径参数

| 参数名 | 类型 | 必填 | 描述 | 示例值 |
|--------|------|------|------|--------|
| id | Long | 是 | 元数据变更记录的ID | 123 |

### 2.2 请求示例
```http
POST /integration/change/123/sync
Content-Type: application/json
Authorization: Bearer {token}
```

## 3. 响应格式

### 3.1 成功响应
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "success": true,
    "message": "对象创建成功"
  }
}
```

### 3.2 失败响应
```json
{
  "code": 500,
  "msg": "同步失败: 元数据变更记录不存在"
}
```

### 3.3 响应字段说明

| 字段名 | 类型 | 描述 |
|--------|------|------|
| code | Integer | 响应状态码，200表示成功，其他表示失败 |
| msg | String | 响应消息 |
| data.success | Boolean | 同步是否成功 |
| data.message | String | 同步结果消息 |

## 4. 实现逻辑详解

### 4.1 主流程

```java
@Transactional(rollbackFor = Exception.class)
public Map<String, Object> syncToLocalDatabase(Long id)
```

**执行步骤：**

1. **查询元数据变更记录**
   - 根据ID查询元数据变更记录
   - 如果记录不存在，返回失败结果

2. **获取变更信息**
   - 获取变更类型（changeType）：OBJECT 或 FIELD
   - 获取操作类型（operationType）：INSERT、UPDATE 或 DELETE

3. **根据变更类型执行同步**
   - 如果是 OBJECT 类型，调用 `syncObjectChange()` 方法
   - 如果是 FIELD 类型，调用 `syncFieldChange()` 方法
   - 如果是其他类型，返回失败结果

4. **更新同步状态**
   - 同步成功：更新 syncStatus 为 true，记录同步时间
   - 同步失败：更新 syncStatus 为 false，记录错误信息，增加重试次数

### 4.2 对象变更同步（OBJECT）

#### 4.2.1 INSERT 操作 - 创建对象

**执行流程：**

1. **获取Salesforce连接**
   ```java
   PartnerConnection connection = retryOperation(() -> soapConnectionFactory.getConnection(), 3, 1000);
   ```
   - 使用重试机制获取连接，最多重试3次，每次间隔1秒

2. **获取对象元数据**
   ```java
   DescribeSObjectResult objDetail = connection.describeSObject(objectApi.trim());
   ```

3. **构建对象元数据**
   ```java
   DataiIntegrationObject object = buildObjectMetadata(objDetail);
   ```
   - 从Salesforce对象描述中提取对象信息
   - 包括API名称、标签、是否自定义、是否可查询/创建/更新/删除等属性

4. **插入对象记录**
   ```java
   int insertResult = dataiIntegrationObjectService.insertDataiIntegrationObject(object);
   ```

5. **保存对象字段信息**
   ```java
   saveObjectFieldsToDataiIntegrationField(objDetail);
   ```
   - 遍历对象的所有字段
   - 保存字段基本信息到 `datai_integration_field` 表
   - 保存Picklist选项到 `datai_integration_picklist` 表
   - 保存过滤查找信息到 `datai_integration_filter_lookup` 表

6. **查询对象数据量**
   ```java
   int objectNum = isLargeObject(connection, objectApi.trim());
   ```
   - 查询Salesforce中该对象的数据量
   - 用于判断是否需要创建分区表

7. **更新对象字段信息**
   ```java
   updateDataiIntegrationObjectFields(objectApi.trim(), objectNum);
   ```

8. **创建数据库表**
   ```java
   if (objectNum > LARGE_OBJECT_THRESHOLD) {
       // 数据量大于500万，创建分区表
       createDatabaseTable(objectApi, objectLabel, objDetail, true, result);
   } else {
       // 数据量小于等于500万，创建普通表
       createDatabaseTable(objectApi, objectLabel, objDetail, false, result);
   }
   ```

   **表创建逻辑：**
   - 检查表是否已存在，存在则跳过
   - 遍历对象的所有字段，构建字段定义
   - 转换Salesforce字段类型到MySQL字段类型
   - 特殊字段处理：
     - **base64类型字段**：自动添加 `file_path`、`is_download`、`is_upload` 三个字段
     - **多态外键字段**：自动添加 `{field_name}_type` 字段
   - 添加系统字段：
     - `new_id`：新SFID
     - `is_update`：是否更新
     - `fail_reason`：失败原因
   - 为所有字段创建索引
   - 如果是分区表，按年份创建分区（从系统配置的开始年份开始）

9. **创建同步批次**
   ```java
   createBatchesForNewObject(objectApi, objectLabel, connection);
   ```

   **批次创建逻辑：**
   - 查询对象的批次字段（batchField）
   - 从缓存获取系统数据开始时间配置（system.data.start.time）
   - 默认从当前日期往前5年开始
   - 根据数据量动态调整批次粒度：
     - 初始按年创建批次
     - 如果年批次数据量超过50万，自动细化为月批次
     - 如果月批次数据量超过50万，自动细化为周批次
     - 周批次即使超过50万也直接保存（最小粒度）
   - 批次递归最大深度：10层
   - 数据量查询失败时最多重试3次，使用指数退避策略

#### 4.2.2 UPDATE 操作 - 更新对象

**执行流程：**

1. **查询现有对象**
   ```java
   DataiIntegrationObject queryObject = new DataiIntegrationObject();
   queryObject.setApi(objectApi);
   List<DataiIntegrationObject> existingObjects = dataiIntegrationObjectService.selectDataiIntegrationObjectList(queryObject);
   ```

2. **更新对象信息**
   ```java
   DataiIntegrationObject object = existingObjects.get(0);
   object.setLabel(metadataChange.getObjectLabel());
   object.setIsWork(false);
   object.setIsIncremental(false);
   object.setUpdateTime(DateUtils.getNowDate());
   int updateResult = dataiIntegrationObjectService.updateDataiIntegrationObject(object);
   ```

**注意：**
- 对象的UPDATE操作只更新对象的标签信息
- 不修改数据库表结构
- 不重新创建批次

#### 4.2.3 DELETE 操作 - 删除对象

**执行流程：**

1. **查询现有对象**
   ```java
   DataiIntegrationObject queryObject = new DataiIntegrationObject();
   queryObject.setApi(objectApi);
   List<DataiIntegrationObject> existingObjects = dataiIntegrationObjectService.selectDataiIntegrationObjectList(queryObject);
   ```

2. **更新对象状态**
   ```java
   DataiIntegrationObject object = existingObjects.get(0);
   object.setIsWork(false);
   object.setIsIncremental(false);
   object.setUpdateTime(DateUtils.getNowDate());
   int updateResult = dataiIntegrationObjectService.updateDataiIntegrationObject(object);
   ```

**注意：**
- 对象的DELETE操作是软删除，只是将 `is_work` 和 `is_incremental` 设置为false
- 不删除数据库表
- 不删除批次记录

### 4.3 字段变更同步（FIELD）

#### 4.3.1 INSERT 操作 - 创建字段

**执行流程：**

1. **构建字段对象**
   ```java
   DataiIntegrationField field = new DataiIntegrationField();
   field.setApi(objectApi);
   field.setField(fieldApi);
   field.setLabel(metadataChange.getFieldLabel());
   field.setIsCustom(metadataChange.getIsCustom());
   field.setCreateTime(DateUtils.getNowDate());
   field.setUpdateTime(DateUtils.getNowDate());
   ```

2. **插入字段记录**
   ```java
   int insertResult = dataiIntegrationFieldService.insertDataiIntegrationField(field);
   ```

3. **添加数据库列**
   ```java
   if (insertResult > 0) {
       addDatabaseColumn(objectApi, fieldApi, result);
   }
   ```

   **添加列逻辑：**
   - 获取Salesforce连接
   - 查询对象元数据
   - 找到对应的字段
   - 转换字段类型到MySQL类型
   - 执行 `ALTER TABLE ADD COLUMN` 语句
   - 处理字段的可空属性

#### 4.3.2 UPDATE 操作 - 更新字段

**执行流程：**

1. **查询现有字段**
   ```java
   DataiIntegrationField queryField = new DataiIntegrationField();
   queryField.setApi(objectApi);
   queryField.setField(fieldApi);
   List<DataiIntegrationField> existingFields = dataiIntegrationFieldService.selectDataiIntegrationFieldList(queryField);
   ```

2. **更新字段信息**
   ```java
   DataiIntegrationField field = existingFields.get(0);
   field.setLabel(metadataChange.getFieldLabel());
   field.setUpdateTime(DateUtils.getNowDate());
   int updateResult = dataiIntegrationFieldService.updateDataiIntegrationField(field);
   ```

3. **修改数据库列**
   ```java
   if (updateResult > 0) {
       modifyDatabaseColumn(objectApi, fieldApi, result);
   }
   ```

   **修改列逻辑：**
   - 获取Salesforce连接
   - 查询对象元数据
   - 找到对应的字段
   - 转换字段类型到MySQL类型
   - 执行 `ALTER TABLE MODIFY COLUMN` 语句
   - 处理字段的可空属性

**注意：**
- 字段的UPDATE操作只更新字段的标签信息
- 如果字段类型发生变化，会修改数据库列的类型

#### 4.3.3 DELETE 操作 - 删除字段

**执行流程：**

1. **查询现有字段**
   ```java
   DataiIntegrationField queryField = new DataiIntegrationField();
   queryField.setApi(objectApi);
   queryField.setField(fieldApi);
   List<DataiIntegrationField> existingFields = dataiIntegrationFieldService.selectDataiIntegrationFieldList(queryField);
   ```

2. **删除字段记录**
   ```java
   Integer[] ids = existingFields.stream()
       .map(DataiIntegrationField::getId)
       .toArray(Integer[]::new);
   int deleteResult = dataiIntegrationFieldService.deleteDataiIntegrationFieldByIds(ids);
   ```

3. **删除数据库列**
   ```java
   if (deleteResult > 0) {
       dropDatabaseColumn(objectApi, fieldApi, result);
   }
   ```

   **删除列逻辑：**
   - 执行 `ALTER TABLE DROP COLUMN` 语句
   - 删除对应的索引

**注意：**
- 字段的DELETE操作是硬删除，会同时删除数据库列
- 会删除字段相关的索引

## 5. 同步状态更新

### 5.1 成功状态更新

```java
updateSyncStatus(metadataChange.getId(), true, null);
```

更新字段：
- `sync_status`：设置为 true
- `sync_time`：设置为当前时间
- `sync_error_message`：设置为 null
- `retry_count`：不更新
- `last_retry_time`：不更新

### 5.2 失败状态更新

```java
updateSyncStatus(metadataChange.getId(), false, errorMessage);
```

更新字段：
- `sync_status`：设置为 false
- `sync_time`：设置为当前时间
- `sync_error_message`：设置为错误消息
- `retry_count`：在原有基础上 +1
- `last_retry_time`：设置为当前时间

## 6. 错误处理

### 6.1 常见错误

| 错误类型 | 错误消息 | 处理方式 |
|---------|---------|---------|
| 记录不存在 | 元数据变更记录不存在 | 直接返回失败 |
| 不支持的变更类型 | 不支持的变更类型: {type} | 直接返回失败 |
| 不支持的操作类型 | 不支持的操作类型: {type} | 直接返回失败 |
| 对象不存在 | 对象不存在，无法更新 | 直接返回失败 |
| 字段不存在 | 字段不存在，无法更新/删除 | 直接返回失败 |
| 连接失败 | 获取Salesforce连接失败 | 记录错误日志，返回失败 |
| 表已存在 | 表已存在，跳过创建表 | 记录日志，继续执行 |
| 批次字段未设置 | 对象没有设置批次字段，跳过批次创建 | 记录警告日志，继续执行 |

### 6.2 异常处理

所有异常都会被捕获并记录到日志中：
```java
try {
    // 业务逻辑
} catch (Exception e) {
    result.put("success", false);
    result.put("message", "同步失败: " + e.getMessage());
    updateSyncStatus(id, false, e.getMessage());
}
```

### 6.3 重试机制

**Salesforce连接重试：**
```java
PartnerConnection connection = retryOperation(() -> soapConnectionFactory.getConnection(), 3, 1000);
```
- 最多重试3次
- 每次间隔1秒

**数据量查询重试：**
```java
while (retryCount < maxRetries) {
    try {
        totalNum = countSfNum(connection, apiName, batchField, currentStartDate, lastDay);
        break;
    } catch (Exception e) {
        retryCount++;
        if (retryCount >= maxRetries) {
            totalNum = 0;
        } else {
            Thread.sleep(1000 * retryCount); // 指数退避
        }
    }
}
```
- 最多重试3次
- 使用指数退避策略（1秒、2秒、3秒）

## 7. 数据类型映射

### 7.1 Salesforce到MySQL类型映射

| Salesforce类型 | MySQL类型 | 说明 |
|---------------|-----------|------|
| string | varchar(255) | 字符串 |
| textarea | text | 长文本 |
| phone | varchar(40) | 电话号码 |
| url | varchar(255) | URL地址 |
| email | varchar(255) | 邮箱地址 |
| encryptedstring | varchar(255) | 加密字符串 |
| id | varchar(255) | ID |
| reference | varchar(255) | 外键引用 |
| picklist | varchar(255) | 选择列表 |
| multipicklist | text | 多选列表 |
| combobox | varchar(255) | 组合框 |
| anyType | varchar(255) | 任意类型 |
| boolean | tinyint(1) | 布尔值 |
| currency | decimal(18,4) | 货币 |
| double | decimal(18,4) | 双精度浮点 |
| int | int | 整数 |
| percent | decimal(5,2) | 百分比 |
| date | date | 日期 |
| datetime | datetime | 日期时间 |
| time | time | 时间 |
| base64 | longblob | 二进制数据 |
| address | text | 地址 |

### 7.2 特殊字段处理

**base64类型字段：**
- 自动添加 `file_path`（text）：文件路径
- 自动添加 `is_download`（tinyint(1)）：是否下载
- 自动添加 `is_upload`（tinyint(1)）：是否上传

**多态外键字段：**
- 自动添加 `{field_name}_type`（varchar(255)）：关联对象类型

## 8. 系统配置

### 8.1 系统数据开始时间配置

**配置键：** `system.data.start.time`

**配置值格式：** `yyyy-MM-dd`（例如：2020-01-01）

**用途：**
- 用于确定分区表的起始年份
- 用于确定批次创建的起始日期

**默认值：** 当前日期往前5年

**获取方式：**
```java
String systemDataStartTimeStr = CacheUtils.get(
    salesforceConfigCacheManager.getEnvironmentCacheKey(), 
    "system.data.start.time", 
    String.class
);
```

### 8.2 常量配置

| 常量名 | 值 | 说明 |
|--------|-----|------|
| LARGE_OBJECT_THRESHOLD | 5000000 | 大数据量对象阈值（500万） |
| BATCH_DATA_THRESHOLD | 500000 | 批次数据量阈值（50万） |
| BATCH_MAX_DEPTH | 10 | 批次递归最大深度 |
| BATCH_TYPE_YEAR | "YEAR" | 批次类型-年 |
| BATCH_TYPE_MONTH | "MONTH" | 批次类型-月 |
| BATCH_TYPE_WEEK | "WEEK" | 批次类型-周 |

## 9. 使用示例

### 9.1 同步对象创建变更

**请求：**
```http
POST /integration/change/1/sync
```

**变更记录：**
```json
{
  "id": 1,
  "changeType": "OBJECT",
  "operationType": "INSERT",
  "objectApi": "Account",
  "objectLabel": "账户",
  "isCustom": false
}
```

**响应：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "success": true,
    "message": "对象创建成功"
  }
}
```

**执行结果：**
1. 在 `datai_integration_object` 表中插入Account对象记录
2. 在 `datai_integration_field` 表中插入Account的所有字段
3. 创建 `account` 数据库表
4. 如果Account数据量大于500万，创建分区表
5. 创建Account的同步批次

### 9.2 同步字段创建变更

**请求：**
```http
POST /integration/change/2/sync
```

**变更记录：**
```json
{
  "id": 2,
  "changeType": "FIELD",
  "operationType": "INSERT",
  "objectApi": "Account",
  "fieldApi": "CustomField__c",
  "fieldLabel": "自定义字段",
  "isCustom": true
}
```

**响应：**
```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "success": true,
    "message": "字段创建成功"
  }
}
```

**执行结果：**
1. 在 `datai_integration_field` 表中插入字段记录
2. 在 `account` 数据库表中添加 `custom_field__c` 列

### 9.3 同步失败场景

**请求：**
```http
POST /integration/change/999/sync
```

**响应：**
```json
{
  "code": 500,
  "msg": "同步失败: 元数据变更记录不存在"
}
```

**执行结果：**
- 更新元数据变更记录的同步状态为失败
- 记录错误消息到 `sync_error_message` 字段
- 增加重试次数 `retry_count`

## 10. 注意事项

### 10.1 事务管理
- 整个同步过程使用 `@Transactional(rollbackFor = Exception.class)` 注解
- 任何异常都会触发事务回滚
- 确保数据一致性

### 10.2 性能考虑
- 大数据量对象（>500万）会创建分区表
- 批次创建会根据数据量动态调整粒度
- 使用缓存获取配置信息，减少数据库查询

### 10.3 数据安全
- base64类型字段不会直接存储二进制数据
- 加密字符串字段使用varchar存储
- 敏感字段需要额外加密处理

### 10.4 并发控制
- 同步操作是串行执行的
- 批量同步使用 `syncBatchToLocalDatabase` 方法
- 每个变更记录独立同步，互不影响

### 10.5 日志记录
- 所有关键操作都有日志记录
- 错误日志包含完整的异常堆栈
- 便于问题排查和审计

## 11. 相关接口

### 11.1 批量同步接口
```
POST /integration/change/syncBatch
```

用于批量同步多个元数据变更记录。

### 11.2 更新同步状态接口
```
PUT /integration/change/syncStatus
```

用于批量更新元数据变更记录的同步状态。

### 11.3 查询未同步变更接口
```
GET /integration/change/unsynced
```

用于查询未同步的元数据变更列表。

## 12. 附录

### 12.1 数据库表结构

**datai_integration_metadata_change（元数据变更表）：**
- `id`：主键
- `change_type`：变更类型（OBJECT/FIELD）
- `operation_type`：操作类型（INSERT/UPDATE/DELETE）
- `object_api`：对象API名称
- `object_label`：对象标签
- `field_api`：字段API名称
- `field_label`：字段标签
- `is_custom`：是否自定义
- `sync_status`：同步状态
- `sync_time`：同步时间
- `sync_error_message`：同步错误消息
- `retry_count`：重试次数
- `last_retry_time`：最后重试时间

**datai_integration_object（对象表）：**
- `id`：主键
- `api`：对象API名称
- `label`：对象标签
- `is_custom`：是否自定义
- `is_work`：是否启用
- `is_incremental`：是否增量同步
- `batch_field`：批次字段
- `sf_num`：Salesforce数据量

**datai_integration_field（字段表）：**
- `id`：主键
- `api`：对象API名称
- `field`：字段名称
- `label`：字段标签
- `is_custom`：是否自定义
- 字段属性（可创建、可更新、可查询等）

**datai_integration_batch（批次表）：**
- `id`：主键
- `api`：对象API名称
- `label`：对象标签
- `batch_field`：批次字段
- `sync_type`：同步类型（FULL/INCREMENTAL）
- `sync_status`：同步状态
- `sync_start_date`：同步开始日期
- `sync_end_date`：同步结束日期
- `sf_num`：Salesforce数据量

### 12.2 版本历史

| 版本 | 日期 | 修改内容 |
|------|------|---------|
| 1.0.0 | 2026-01-04 | 初始版本 |

### 12.3 联系方式

如有问题，请联系开发团队。