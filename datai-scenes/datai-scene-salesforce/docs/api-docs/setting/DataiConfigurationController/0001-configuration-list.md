# 接口文档

## 接口信息

- **接口名称**: 查询配置列表
- **接口路径**: /setting/configuration/list
- **请求方法**: GET
- **模块归属**: datai-salesforce-setting
- **版本号**: v1.0
- **创建日期**: 2026-01-14
- **最后更新**: 2026-01-14

## 功能描述

查询配置列表，支持分页查询和多条件过滤。返回的配置数据包含配置ID、配置键、配置值、环境ID、环境名称、是否敏感配置、是否加密存储、配置描述、是否激活、备注、创建人、创建时间、更新人、更新时间、版本号等信息，便于用户管理和查看配置。

## 请求参数

### 查询参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
| id | Long | 否 | 配置ID | 1 | - |
| deptId | Long | 否 | 部门ID | 1 | - |
| configKey | String | 否 | 配置键 | api.endpoint | - |
| configValue | String | 否 | 配置值 | https://api.example.com | - |
| environmentId | Long | 否 | 环境ID | 1 | - |
| isSensitive | Boolean | 否 | 是否敏感配置 | false | - |
| isEncrypted | Boolean | 否 | 是否加密存储 | true | - |
| description | String | 否 | 配置描述 | API端点配置 | - |
| isActive | Boolean | 否 | 是否激活 | true | - |
| pageNum | Integer | 否 | 页码 | 1 | 1 |
| pageSize | Integer | 否 | 每页数量 | 10 | 10 |

**参数说明**:
- `id`: 配置ID，精确查询
- `deptId`: 部门ID，精确查询
- `configKey`: 配置键，模糊查询
- `configValue`: 配置值，模糊查询
- `environmentId`: 环境ID，精确查询
- `isSensitive`: 是否敏感配置，精确查询
- `isEncrypted`: 是否加密存储，精确查询
- `description`: 配置描述，模糊查询
- `isActive`: 是否激活，精确查询
- `pageNum`: 页码，从1开始
- `pageSize`: 每页数量

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "total": 100,
  "rows": [
    {
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
  ],
  "code": 200,
  "msg": "查询成功"
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| total | Integer | 总记录数 | 100 |
| rows | Array | 配置列表 | - |
| code | Integer | 响应码 | 200 |
| msg | String | 响应消息 | 查询成功 |

**rows 数组对象字段说明**:

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

**HTTP 状态码**: 400/401/403/500

```json
{
  "code": 500,
  "msg": "查询失败",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 400 | 参数错误 | 参数验证失败 |
| 401 | 您没有权限执行此操作 | 权限不足 |
| 403 | 访问被拒绝 | 访问被拒绝 |
| 500 | 查询失败 | 系统错误 |

## 接口示例

### 请求示例

```bash
curl -X GET "http://localhost:8080/setting/configuration/list?environmentId=1&isActive=true&pageNum=1&pageSize=10" \
  -H "Authorization: Bearer [token]"
```

### 响应示例

**成功**:

```json
{
  "total": 10,
  "rows": [
    {
      "id": 1,
      "configKey": "api.endpoint",
      "configValue": "https://api.example.com",
      "environmentId": 1,
      "environmentName": "生产环境",
      "isActive": true
    }
  ],
  "code": 200,
  "msg": "查询成功"
}
```

**失败**:

```json
{
  "code": 400,
  "msg": "参数错误",
  "data": null
}
```

## 错误处理

1. **权限验证失败**: 检查用户是否具有 setting:configuration:list 权限
2. **参数验证失败**: 检查参数格式是否正确
3. **系统错误**: 记录错误日志并返回通用错误信息

## 注意事项

1. 接口需要登录认证，需要携带有效的 token
2. 接口需要 setting:configuration:list 权限
3. 支持分页查询，默认每页10条记录
4. 支持多条件过滤，所有参数都是可选的
5. 返回的数据包含环境名称，便于用户查看
6. 敏感配置的值可能被脱敏处理
7. 加密存储的配置值在返回时可能被解密或脱敏
8. 建议使用环境ID进行过滤，以减少查询结果
9. 建议使用是否激活进行过滤，以只查询有效配置
10. 配置值可能包含敏感信息，请妥善保管

## 相关接口

- [导出配置列表](0002-export-configuration-list.md) - 导出配置列表
- [获取配置详细信息](0003-get-info.md) - 获取单个配置的详细信息
- [新增配置](0004-add-configuration.md) - 新增配置
- [修改配置](0005-edit-configuration.md) - 修改配置
- [删除配置](0006-remove-configuration.md) - 删除配置

## 实现细节

1. 接收查询参数
2. 调用 IDataiConfigurationService 查询配置列表
3. 查询环境信息，设置环境名称
4. 转换为 VO 对象并返回

## 测试信息

### 测试环境

- **环境**: 本地开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 查询所有配置 | 无参数 | 返回所有配置 | 返回所有配置 | 通过 |
| 按环境ID查询 | environmentId=1 | 返回指定环境的配置 | 返回指定环境的配置 | 通过 |
| 按配置键查询 | configKey=api.endpoint | 返回指定配置键的配置 | 返回指定配置键的配置 | 通过 |
| 按是否激活查询 | isActive=true | 返回激活的配置 | 返回激活的配置 | 通过 |
| 分页查询 | pageNum=1, pageSize=10 | 返回第一页数据 | 返回第一页数据 | 通过 |
| 无权限访问 | 无权限token | 返回401错误 | 返回401错误 | 通过 |
