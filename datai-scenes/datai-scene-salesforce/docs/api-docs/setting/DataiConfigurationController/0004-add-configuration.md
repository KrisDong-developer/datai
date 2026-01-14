# 接口文档

## 接口信息

- **接口名称**: 新增配置
- **接口路径**: /setting/configuration
- **请求方法**: POST
- **模块归属**: datai-salesforce-setting
- **版本号**: v1.0
- **创建日期**: 2026-01-14
- **最后更新**: 2026-01-14

## 功能描述

新增配置，保存配置的基本信息，包括配置键、配置值、环境ID、是否敏感配置、是否加密存储、配置描述、是否激活、备注等。新增成功后，系统会自动记录创建人和创建时间。

## 请求参数

### 请求体参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
| deptId | Long | 否 | 部门ID | 1 | - |
| configKey | String | 是 | 配置键 | api.endpoint | - |
| configValue | String | 是 | 配置值 | https://api.example.com | - |
| environmentId | Long | 是 | 环境ID | 1 | - |
| isSensitive | Boolean | 否 | 是否敏感配置 | false | false |
| isEncrypted | Boolean | 否 | 是否加密存储 | true | false |
| description | String | 否 | 配置描述 | API端点配置 | - |
| isActive | Boolean | 否 | 是否激活 | true | true |
| remark | String | 否 | 备注 | API配置 | - |

**参数说明**:
- `deptId`: 部门ID，用于标识配置所属的部门
- `configKey`: 配置键，唯一标识配置，必填
- `configValue`: 配置值，配置的具体值，必填
- `environmentId`: 环境ID，用于标识配置所属的环境，必填
- `isSensitive`: 是否敏感配置，默认为 false
- `isEncrypted`: 是否加密存储，默认为 false
- `description`: 配置描述，用于描述配置的用途
- `isActive`: 是否激活，默认为 true
- `remark`: 备注，用于记录其他信息

**请求示例**:

```json
{
  "deptId": 1,
  "configKey": "api.endpoint",
  "configValue": "https://api.example.com",
  "environmentId": 1,
  "isSensitive": false,
  "isEncrypted": true,
  "description": "API端点配置",
  "isActive": true,
  "remark": "API配置"
}
```

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": 1
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| code | Integer | 响应码 | 200 |
| msg | String | 响应消息 | 操作成功 |
| data | Integer | 影响的行数 | 1 |

### 失败响应

**HTTP 状态码**: 400/401/403/500

```json
{
  "code": 400,
  "msg": "参数错误",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 400 | 参数错误 | 参数验证失败 |
| 401 | 您没有权限执行此操作 | 权限不足 |
| 403 | 访问被拒绝 | 访问被拒绝 |
| 500 | 新增失败 | 系统错误 |

## 接口示例

### 请求示例

```bash
curl -X POST "http://localhost:8080/setting/configuration" \
  -H "Authorization: Bearer [token]" \
  -H "Content-Type: application/json" \
  -d '{
    "deptId": 1,
    "configKey": "api.endpoint",
    "configValue": "https://api.example.com",
    "environmentId": 1,
    "isSensitive": false,
    "isEncrypted": true,
    "description": "API端点配置",
    "isActive": true,
    "remark": "API配置"
  }'
```

### 响应示例

**成功**:

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": 1
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

1. **权限验证失败**: 检查用户是否具有 setting:configuration:add 权限
2. **参数验证失败**: 检查参数格式是否正确，必填项是否填写
3. **配置键重复**: 检查配置键是否已存在，如存在则返回错误
4. **环境不存在**: 检查环境是否存在，如不存在则返回错误
5. **新增失败**: 记录错误日志并返回通用错误信息

## 注意事项

1. 接口需要登录认证，需要携带有效的 token
2. 接口需要 setting:configuration:add 权限
3. 配置键为必填项，且在同一环境下必须唯一
4. 配置值为必填项，不能为空
5. 环境ID为必填项，必须对应已存在的环境
6. 新增操作会记录到操作日志中
7. 新增成功后，系统会自动记录创建人和创建时间
8. 敏感配置建议设置 isSensitive 为 true
9. 敏感配置的值建议加密存储，设置 isEncrypted 为 true
10. 新增的配置默认为激活状态
11. 配置值可能包含敏感信息，请妥善保管
12. 建议在新增前先查询配置键是否已存在

## 相关接口

- [查询配置列表](0001-configuration-list.md) - 查询配置列表
- [导出配置列表](0002-export-configuration-list.md) - 导出配置列表
- [获取配置详细信息](0003-get-info.md) - 获取单个配置的详细信息
- [修改配置](0005-edit-configuration.md) - 修改配置
- [删除配置](0006-remove-configuration.md) - 删除配置

## 实现细节

1. 接收请求参数
2. 验证参数的完整性和有效性
3. 调用 IDataiConfigurationService 新增配置
4. 记录操作日志
5. 返回操作结果

## 测试信息

### 测试环境

- **环境**: 本地开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 新增普通配置 | 正常参数 | 新增成功 | 新增成功 | 通过 |
| 新增敏感配置 | isSensitive=true | 新增成功 | 新增成功 | 通过 |
| 新增加密配置 | isEncrypted=true | 新增成功 | 新增成功 | 通过 |
| 缺少必填参数 | 缺少configKey | 返回参数错误 | 返回参数错误 | 通过 |
| 配置键重复 | 已存在的configKey | 返回错误 | 返回错误 | 通过 |
| 环境不存在 | 不存在的environmentId | 返回错误 | 返回错误 | 通过 |
| 无权限访问 | 无权限token | 返回401错误 | 返回401错误 | 通过 |
