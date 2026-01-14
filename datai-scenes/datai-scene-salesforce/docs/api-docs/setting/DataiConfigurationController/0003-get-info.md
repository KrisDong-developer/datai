# 接口文档

## 接口信息

- **接口名称**: 获取配置详细信息
- **接口路径**: /setting/configuration/{id}
- **请求方法**: GET
- **模块归属**: datai-salesforce-setting
- **版本号**: v1.0
- **创建日期**: 2026-01-14
- **最后更新**: 2026-01-14

## 功能描述

根据配置ID获取配置的详细信息。返回的配置数据包含配置ID、配置键、配置值、环境ID、环境名称、是否敏感配置、是否加密存储、配置描述、是否激活、备注、创建人、创建时间、更新人、更新时间、版本号等信息。如果配置不存在，则返回错误信息。

## 请求参数

### 路径参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
| id | Long | 是 | 配置ID | 1 | - |

**参数说明**:
- `id`: 配置ID，用于查询指定的配置

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "id": 1,
    "deptId": 1,
    "configKey": "api.endpoint",
    "configValue": "https://api.example.com",
    "environmentId": 1,
    "environmentName": "生产环境",
    "isSensitive": false,
    "isEncrypted": true,
    "description": "API端点配置",
    "isActive": true,
    "remark": "API配置",
    "createBy": "admin",
    "createTime": "2026-01-01 10:00:00",
    "updateBy": "admin",
    "updateTime": "2026-01-14 10:30:00",
    "version": 1
  }
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| code | Integer | 响应码 | 200 |
| msg | String | 响应消息 | 查询成功 |
| data | Object | 配置详细信息 | - |

**data 对象字段说明**:

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| id | Long | 配置ID | 1 |
| deptId | Long | 部门ID | 1 |
| configKey | String | 配置键 | api.endpoint |
| configValue | String | 配置值 | https://api.example.com |
| environmentId | Long | 环境ID | 1 |
| environmentName | String | 环境名称 | 生产环境 |
| isSensitive | Boolean | 是否敏感配置 | false |
| isEncrypted | Boolean | 是否加密存储 | true |
| description | String | 配置描述 | API端点配置 |
| isActive | Boolean | 是否激活 | true |
| remark | String | 备注 | API配置 |
| createBy | String | 创建人 | admin |
| createTime | String | 创建时间 | 2026-01-01 10:00:00 |
| updateBy | String | 更新人 | admin |
| updateTime | String | 更新时间 | 2026-01-14 10:30:00 |
| version | Integer | 配置版本号 | 1 |

### 失败响应

**HTTP 状态码**: 400/401/403/404/500

```json
{
  "code": 404,
  "msg": "配置不存在",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 400 | 参数错误 | 参数验证失败 |
| 401 | 您没有权限执行此操作 | 权限不足 |
| 403 | 访问被拒绝 | 访问被拒绝 |
| 404 | 配置不存在 | 配置不存在 |
| 500 | 查询失败 | 系统错误 |

## 接口示例

### 请求示例

```bash
curl -X GET "http://localhost:8080/setting/configuration/1" \
  -H "Authorization: Bearer [token]"
```

### 响应示例

**成功**:

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "id": 1,
    "configKey": "api.endpoint",
    "configValue": "https://api.example.com",
    "environmentId": 1,
    "environmentName": "生产环境",
    "isActive": true
  }
}
```

**失败**:

```json
{
  "code": 404,
  "msg": "配置不存在",
  "data": null
}
```

## 错误处理

1. **权限验证失败**: 检查用户是否具有 setting:configuration:query 权限
2. **参数验证失败**: 检查参数格式是否正确
3. **配置不存在**: 检查配置是否存在，如不存在则返回错误
4. **系统错误**: 记录错误日志并返回通用错误信息

## 注意事项

1. 接口需要登录认证，需要携带有效的 token
2. 接口需要 setting:configuration:query 权限
3. 配置ID为必填项，通过路径参数传递
4. 返回的数据包含环境名称，便于用户查看
5. 敏感配置的值可能被脱敏处理
6. 加密存储的配置值在返回时可能被解密或脱敏
7. 如果配置不存在，则返回404错误
8. 配置值可能包含敏感信息，请妥善保管
9. 版本号用于标识配置的版本，可用于并发控制
10. 建议在修改前先查询配置详细信息

## 相关接口

- [查询配置列表](0001-configuration-list.md) - 查询配置列表
- [导出配置列表](0002-export-configuration-list.md) - 导出配置列表
- [新增配置](0004-add-configuration.md) - 新增配置
- [修改配置](0005-edit-configuration.md) - 修改配置
- [删除配置](0006-remove-configuration.md) - 删除配置

## 实现细节

1. 接收配置ID参数
2. 调用 IDataiConfigurationService 查询配置详细信息
3. 查询环境信息，设置环境名称
4. 转换为 VO 对象并返回

## 测试信息

### 测试环境

- **环境**: 本地开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 查询存在的配置 | id=1 | 返回配置详细信息 | 返回配置详细信息 | 通过 |
| 查询不存在的配置 | id=999 | 返回404错误 | 返回404错误 | 通过 |
| 查询敏感配置 | id=2（敏感配置） | 返回配置信息（值脱敏） | 返回配置信息（值脱敏） | 通过 |
| 查询加密配置 | id=3（加密配置） | 返回配置信息（值解密） | 返回配置信息（值解密） | 通过 |
| 缺少必填参数 | 缺少id | 返回参数错误 | 返回参数错误 | 通过 |
| 无权限访问 | 无权限token | 返回401错误 | 返回401错误 | 通过 |
