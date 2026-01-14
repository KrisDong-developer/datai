# 接口文档

## 接口信息

- **接口名称**: 修改配置
- **接口路径**: /setting/configuration
- **请求方法**: PUT
- **模块归属**: datai-salesforce-setting
- **版本号**: v1.0
- **创建日期**: 2026-01-14
- **最后更新**: 2026-01-14

## 功能描述

修改配置，更新配置的基本信息，包括配置键、配置值、环境ID、是否敏感配置、是否加密存储、配置描述、是否激活、备注等。修改成功后，系统会自动记录更新人和更新时间。

## 请求参数

### 请求体参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
| id | Long | 是 | 配置ID | 1 | - |
| deptId | Long | 否 | 部门ID | 1 | - |
| configKey | String | 否 | 配置键 | api.endpoint | - |
| configValue | String | 否 | 配置值 | https://api.example.com | - |
| environmentId | Long | 否 | 环境ID | 1 | - |
| isSensitive | Boolean | 否 | 是否敏感配置 | false | - |
| isEncrypted | Boolean | 否 | 是否加密存储 | true | - |
| description | String | 否 | 配置描述 | API端点配置 | - |
| isActive | Boolean | 否 | 是否激活 | true | - |
| remark | String | 否 | 备注 | API配置 | - |

**参数说明**:
- `id`: 配置ID，用于标识要修改的配置，必填
- `deptId`: 部门ID，用于标识配置所属的部门
- `configKey`: 配置键，唯一标识配置
- `configValue`: 配置值，配置的具体值
- `environmentId`: 环境ID，用于标识配置所属的环境
- `isSensitive`: 是否敏感配置
- `isEncrypted`: 是否加密存储
- `description`: 配置描述，用于描述配置的用途
- `isActive`: 是否激活
- `remark`: 备注，用于记录其他信息

**请求示例**:

```json
{
  "id": 1,
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
| 500 | 修改失败 | 系统错误 |

## 接口示例

### 请求示例

```bash
curl -X PUT "http://localhost:8080/setting/configuration" \
  -H "Authorization: Bearer [token]" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
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
  "code": 404,
  "msg": "配置不存在",
  "data": null
}
```

## 错误处理

1. **权限验证失败**: 检查用户是否具有 setting:configuration:edit 权限
2. **参数验证失败**: 检查参数格式是否正确，必填项是否填写
3. **配置不存在**: 检查配置是否存在，如不存在则返回错误
4. **配置键重复**: 检查配置键是否与其他配置冲突，如冲突则返回错误
5. **环境不存在**: 检查环境是否存在，如不存在则返回错误
6. **修改失败**: 记录错误日志并返回通用错误信息

## 注意事项

1. 接口需要登录认证，需要携带有效的 token
2. 接口需要 setting:configuration:edit 权限
3. 配置ID为必填项，用于标识要修改的配置
4. 配置键在同一环境下必须唯一
5. 环境ID必须对应已存在的环境
6. 修改操作会记录到操作日志中
7. 修改成功后，系统会自动记录更新人和更新时间
8. 版本号会自动递增，用于标识配置的版本
9. 修改配置值可能会影响系统运行，请谨慎操作
10. 敏感配置建议设置 isSensitive 为 true
11. 敏感配置的值建议加密存储，设置 isEncrypted 为 true
12. 建议在修改前先查询配置详细信息，确认修改内容
13. 建议在修改重要配置前先创建备份

## 相关接口

- [查询配置列表](0001-configuration-list.md) - 查询配置列表
- [导出配置列表](0002-export-configuration-list.md) - 导出配置列表
- [获取配置详细信息](0003-get-info.md) - 获取单个配置的详细信息
- [新增配置](0004-add-configuration.md) - 新增配置
- [删除配置](0006-remove-configuration.md) - 删除配置

## 实现细节

1. 接收请求参数
2. 验证参数的完整性和有效性
3. 调用 IDataiConfigurationService 修改配置
4. 记录操作日志
5. 返回操作结果

## 测试信息

### 测试环境

- **环境**: 本地开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 修改普通配置 | 正常参数 | 修改成功 | 修改成功 | 通过 |
| 修改敏感配置 | isSensitive=true | 修改成功 | 修改成功 | 通过 |
| 修改加密配置 | isEncrypted=true | 修改成功 | 修改成功 | 通过 |
| 修改配置值 | 修改configValue | 修改成功 | 修改成功 | 通过 |
| 缺少必填参数 | 缺少id | 返回参数错误 | 返回参数错误 | 通过 |
| 配置不存在 | 不存在的id | 返回404错误 | 返回404错误 | 通过 |
| 配置键重复 | 已存在的configKey | 返回错误 | 返回错误 | 通过 |
| 无权限访问 | 无权限token | 返回401错误 | 返回401错误 | 通过 |
