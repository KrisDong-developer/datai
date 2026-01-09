# 接口文档

## 接口信息

- **接口名称**: 获取对象依赖关系
- **接口路径**: /integration/object/{id}/dependencies
- **请求方法**: GET
- **模块归属**: 对象同步控制
- **版本号**: v1.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

获取指定对象的依赖关系，包括该对象依赖的其他对象以及依赖该对象的其他对象。

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
  "data": [
    {
      "objectId": 2,
      "objectName": "Contact",
      "dependencyType": "dependsOn",
      "relationshipName": "Account",
      "fieldName": "AccountId"
    },
    {
      "objectId": 3,
      "objectName": "Opportunity",
      "dependencyType": "dependedBy",
      "relationshipName": "Account",
      "fieldName": "AccountId"
    }
  ]
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| objectId | Integer | 依赖对象ID | 2 |
| objectName | String | 依赖对象名称 | Contact |
| dependencyType | String | 依赖类型 | dependsOn/dependedBy |
| relationshipName | String | 关系名称 | Account |
| fieldName | String | 字段名称 | AccountId |

### 失败响应

**HTTP 状态码**: 400 Bad Request

```json
{
  "code": 400,
  "message": "对象不存在",
  "data": null
}
```

## 接口示例

### 请求示例

```bash
curl -X GET "http://localhost:8080/integration/object/1/dependencies" \
  -H "Authorization: Bearer [token]"
```

### 响应示例

**成功**:

```json
{
  "code": 200,
  "message": "操作成功",
  "data": [
    {
      "objectId": 2,
      "objectName": "Contact",
      "dependencyType": "dependsOn",
      "relationshipName": "Account",
      "fieldName": "AccountId"
    }
  ]
}
```

**失败**:

```json
{
  "code": 400,
  "message": "对象不存在",
  "data": null
}
```

## 错误处理

- 对象不存在时，返回400错误
- 权限不足时，返回403错误
- 服务器内部错误时，返回500错误

## 注意事项

- 该接口需要对象同步控制的查看权限
- 依赖关系分析可能会耗时较长，特别是对于复杂的对象关系网络

## 相关接口

- [获取对象同步统计信息](http://localhost:8080/integration/object/{id}/statistics) - 获取对象同步统计信息
- [创建对象表结构](http://localhost:8080/integration/object/{id}/createStructure) - 创建对象表结构

## 实现细节

- 从数据库中查询对象的字段信息
- 分析字段中的外键关系，确定对象依赖
- 构建并返回依赖关系列表

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 获取存在对象的依赖关系 | id=1 | 返回依赖关系列表 | 返回依赖关系列表 | 通过 |
| 获取不存在对象的依赖关系 | id=999 | 返回对象不存在错误 | 返回对象不存在错误 | 通过 |
| 获取无依赖对象的依赖关系 | id=2 | 返回空列表 | 返回空列表 | 通过 |