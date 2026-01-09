# 接口文档：全对象元数据变更拉取

## 接口信息

- **接口名称**: 全对象元数据变更拉取
- **接口路径**: /integration/change/pullAll
- **请求方法**: POST
- **模块归属**: integration
- **版本号**: v1.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

从Salesforce拉取所有对象的元数据变更信息并记录到元数据变更表中。

该方法会：
1. 连接到Salesforce获取所有对象的元数据
2. 比较现有数据库中的对象元数据
3. 记录对象级别的变更（新增、修改、删除）
4. 记录字段级别的变更（新增、修改、删除）
5. 检测并记录已从Salesforce中删除的对象

表的变更新增需要满足以下任一条件：
- isQueryable (可查询)
- isCreateable (可创建)
- isUpdateable (可更新)
- isDeletable (可删除)
字段的变更新增无限制

## 请求参数

无

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "success": true,
    "message": "全对象元数据变更拉取完成",
    "objectChangeCount": 10,
    "fieldChangeCount": 50,
    "totalChangeCount": 60,
    "createCount": 30,
    "updateCount": 25,
    "deleteCount": 5
  }
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| success | Boolean | 拉取是否成功 | true |
| message | String | 拉取结果消息 | 全对象元数据变更拉取完成 |
| objectChangeCount | Integer | 对象变更数 | 10 |
| fieldChangeCount | Integer | 字段变更数 | 50 |
| totalChangeCount | Integer | 总变更数 | 60 |
| createCount | Integer | 创建操作数 | 30 |
| updateCount | Integer | 更新操作数 | 25 |
| deleteCount | Integer | 删除操作数 | 5 |

### 失败响应

**HTTP 状态码**: 400 Bad Request

```json
{
  "code": 400,
  "message": "全对象元数据变更拉取失败: 连接Salesforce失败",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 400 | 全对象元数据变更拉取失败: [具体错误信息] | 拉取过程中出现的具体错误 |
| 403 | 无权限执行此操作 | 当用户没有integration:change:pullAll权限时 |
| 500 | 全对象元数据变更拉取失败 | 服务器内部错误导致拉取失败 |

## 接口示例

### 请求示例

```bash
curl -X POST "http://localhost:8080/integration/change/pullAll" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer [token]"
```

### 响应示例

**成功**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": {
    "success": true,
    "message": "全对象元数据变更拉取完成",
    "objectChangeCount": 10,
    "fieldChangeCount": 50,
    "totalChangeCount": 60,
    "createCount": 30,
    "updateCount": 25,
    "deleteCount": 5
  }
}
```

**失败**:

```json
{
  "code": 400,
  "message": "全对象元数据变更拉取失败: 连接Salesforce失败",
  "data": null
}
```

## 错误处理

- **400 Bad Request**: 拉取过程中出现的具体错误，如连接Salesforce失败、元数据获取失败等
- **403 Forbidden**: 用户无权限执行此操作
- **500 Internal Server Error**: 服务器内部错误导致拉取失败

## 注意事项

- 拉取操作可能耗时较长，取决于Salesforce对象数量和网络速度
- 拉取过程中会连接Salesforce并获取大量元数据，可能会触发Salesforce API限制
- 表的变更新增需要满足至少一个可操作条件（可查询、可创建、可更新、可删除）
- 字段的变更新增无限制
- 拉取完成后会记录详细的变更统计信息

## 相关接口

- [查询对象元数据变更列表](http://localhost:8080/integration/change/list) - 查询所有元数据变更列表
- [查询未同步的元数据变更列表](http://localhost:8080/integration/change/unsynced) - 查询未同步的元数据变更列表
- [同步元数据变更到本地数据库](http://localhost:8080/integration/change/{id}/sync) - 同步指定的元数据变更

## 实现细节

- 接口通过调用`dataiIntegrationMetadataChangeService.pullAllMetadataChanges()`方法实现拉取
- 服务层会连接Salesforce获取所有对象的元数据
- 会与现有数据库中的元数据进行比较，检测变更
- 支持对象级别和字段级别的变更检测
- 会记录详细的变更统计信息

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 执行全对象元数据变更拉取 | 无参数 | 操作成功，返回拉取结果统计 | 操作成功，返回拉取结果统计 | 通过 |
| 无权限拉取 | 无参数 | 操作失败，返回403错误 | 操作失败，返回403错误 | 通过 |