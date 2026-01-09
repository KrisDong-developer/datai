# 接口文档

## 接口信息

- **接口名称**: 创建对象表结构
- **接口路径**: /integration/object/{id}/createStructure
- **请求方法**: POST
- **模块归属**: 对象同步控制
- **版本号**: v1.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

根据对象配置在本地数据库中创建对应的表结构，包括表名、字段、索引等信息。

## 请求参数

### 路径参数

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| id | Integer | 是 | 对象ID | 1 |

### 查询参数

无

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
    "message": "表结构创建成功",
    "tableName": "salesforce_account",
    "fieldCount": 20,
    "indexCount": 5
  }
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| success | Boolean | 操作结果 | true |
| message | String | 操作消息 | 表结构创建成功 |
| tableName | String | 创建的表名 | salesforce_account |
| fieldCount | Integer | 创建的字段数 | 20 |
| indexCount | Integer | 创建的索引数 | 5 |

### 失败响应

**HTTP 状态码**: 400 Bad Request

```json
{
  "code": 400,
  "message": "表结构创建失败: 表已存在",
  "data": null
}
```

## 接口示例

### 请求示例

```bash
curl -X POST "http://localhost:8080/integration/object/1/createStructure" \
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
    "message": "表结构创建成功",
    "tableName": "salesforce_account",
    "fieldCount": 20,
    "indexCount": 5
  }
}
```

**失败**:

```json
{
  "code": 400,
  "message": "表结构创建失败: 表已存在",
  "data": null
}
```

## 错误处理

- 对象不存在时，返回400错误
- 表已存在时，返回400错误
- 数据库连接失败时，返回500错误
- 权限不足时，返回403错误

## 注意事项

- 该接口需要对象同步控制的创建权限
- 表结构创建操作可能会耗时较长，特别是对于字段较多的对象
- 创建表结构前，请确保数据库连接正常且有足够的权限

## 相关接口

- [获取对象依赖关系](http://localhost:8080/integration/object/{id}/dependencies) - 获取对象依赖关系
- [同步单对象数据](http://localhost:8080/integration/object/{id}/syncData) - 同步单对象数据到本地数据库

## 实现细节

- 根据对象配置生成SQL建表语句
- 执行SQL语句创建表结构
- 处理创建过程中的异常情况
- 返回创建结果

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 创建不存在对象的表结构 | id=1 | 返回表结构创建成功 | 返回表结构创建成功 | 通过 |
| 创建已存在对象的表结构 | id=1 | 返回表已存在错误 | 返回表已存在错误 | 通过 |
| 创建不存在对象的表结构 | id=999 | 返回对象不存在错误 | 返回对象不存在错误 | 通过 |