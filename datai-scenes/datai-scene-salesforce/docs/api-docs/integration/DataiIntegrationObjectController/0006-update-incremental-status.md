# 接口文档

## 接口信息

- **接口名称**: 变更对象增量更新状态
- **接口路径**: /integration/object/{id}/incrementalStatus
- **请求方法**: PUT
- **模块归属**: 对象同步控制
- **版本号**: v1.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

变更指定对象的增量更新状态，用于控制对象是否采用增量更新模式进行数据同步。

## 请求参数

### 路径参数

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| id | Integer | 是 | 对象ID | 1 |

### 查询参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
| isIncremental | Boolean | 是 | 是否启用增量更新 | true | 无 |

### 请求体

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
    "message": "增量更新状态更新成功",
    "objectId": 1,
    "isIncremental": true
  }
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| success | Boolean | 操作结果 | true |
| message | String | 操作消息 | 增量更新状态更新成功 |
| objectId | Integer | 对象ID | 1 |
| isIncremental | Boolean | 增量更新状态 | true |

### 失败响应

**HTTP 状态码**: 400 Bad Request

```json
{
  "code": 400,
  "message": "增量更新状态更新失败: 对象不存在",
  "data": null
}
```

## 接口示例

### 请求示例

```bash
curl -X PUT "http://localhost:8080/integration/object/1/incrementalStatus?isIncremental=true" \
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
    "message": "增量更新状态更新成功",
    "objectId": 1,
    "isIncremental": true
  }
}
```

**失败**:

```json
{
  "code": 400,
  "message": "增量更新状态更新失败: 对象不存在",
  "data": null
}
```

## 错误处理

- 对象不存在时，返回400错误
- 参数错误时，返回400错误
- 权限不足时，返回403错误
- 服务器内部错误时，返回500错误

## 注意事项

- 该接口需要对象同步控制的修改权限
- 启用增量更新后，对象会采用增量方式同步数据，只同步变更的数据
- 禁用增量更新后，对象会采用全量方式同步数据，每次同步所有数据
- 增量更新需要依赖 Salesforce 的变更数据捕获机制

## 相关接口

- [变更对象启用同步状态](http://localhost:8080/integration/object/{id}/workStatus) - 变更对象启用同步状态
- [同步单对象数据](http://localhost:8080/integration/object/{id}/syncData) - 同步单对象数据到本地数据库

## 实现细节

- 验证对象是否存在
- 更新对象的增量更新状态
- 处理更新过程中的异常情况
- 返回更新结果

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 启用增量更新 | id=1&isIncremental=true | 返回更新成功 | 返回更新成功 | 通过 |
| 禁用增量更新 | id=1&isIncremental=false | 返回更新成功 | 返回更新成功 | 通过 |
| 更新不存在对象 | id=999&isIncremental=true | 返回对象不存在错误 | 返回对象不存在错误 | 通过 |
| 缺少isIncremental参数 | id=1 | 返回参数错误 | 返回参数错误 | 通过 |