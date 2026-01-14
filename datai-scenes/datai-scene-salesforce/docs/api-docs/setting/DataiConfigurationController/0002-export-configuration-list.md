# 接口文档

## 接口信息

- **接口名称**: 导出配置列表
- **接口路径**: /setting/configuration/export
- **请求方法**: POST
- **模块归属**: datai-salesforce-setting
- **版本号**: v1.0
- **创建日期**: 2026-01-14
- **最后更新**: 2026-01-14

## 功能描述

导出配置列表到 Excel 文件，支持多条件过滤。导出的文件包含配置ID、配置键、配置值、环境ID、是否敏感配置、是否加密存储、配置描述、是否激活、备注、创建人、创建时间、更新人、更新时间、版本号等信息，便于用户离线查看和备份配置数据。

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

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

**Content-Type**: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet

**响应头**:
```
Content-Disposition: attachment; filename=配置数据.xlsx
```

**响应体**: Excel 文件二进制流

### 失败响应

**HTTP 状态码**: 400/401/403/500

```json
{
  "code": 500,
  "msg": "导出失败",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 400 | 参数错误 | 参数验证失败 |
| 401 | 您没有权限执行此操作 | 权限不足 |
| 403 | 访问被拒绝 | 访问被拒绝 |
| 500 | 导出失败 | 系统错误 |

## 接口示例

### 请求示例

```bash
curl -X POST "http://localhost:8080/setting/configuration/export" \
  -H "Authorization: Bearer [token]" \
  -H "Content-Type: application/json" \
  -d '{
    "environmentId": 1,
    "isActive": true
  }' \
  --output configuration.xlsx
```

### 响应示例

**成功**: 返回 Excel 文件

**失败**:

```json
{
  "code": 400,
  "msg": "参数错误",
  "data": null
}
```

## 错误处理

1. **权限验证失败**: 检查用户是否具有 setting:configuration:export 权限
2. **参数验证失败**: 检查参数格式是否正确
3. **导出失败**: 记录错误日志并返回通用错误信息
4. **文件生成失败**: 记录错误日志并返回通用错误信息

## 注意事项

1. 接口需要登录认证，需要携带有效的 token
2. 接口需要 setting:configuration:export 权限
3. 导出操作会记录到操作日志中
4. 导出的文件格式为 Excel (.xlsx)
5. 支持多条件过滤，所有参数都是可选的
6. 敏感配置的值在导出时可能被脱敏处理
7. 加密存储的配置值在导出时可能被解密或脱敏
8. 导出的文件名默认为"配置数据.xlsx"
9. 建议使用环境ID进行过滤，以减少导出数据量
10. 建议使用是否激活进行过滤，以只导出有效配置
11. 导出操作可能耗时较长，建议在低峰期执行
12. 导出的数据包含配置值，请妥善保管

## 相关接口

- [查询配置列表](0001-configuration-list.md) - 查询配置列表
- [获取配置详细信息](0003-get-info.md) - 获取单个配置的详细信息
- [新增配置](0004-add-configuration.md) - 新增配置
- [修改配置](0005-edit-configuration.md) - 修改配置
- [删除配置](0006-remove-configuration.md) - 删除配置

## 实现细节

1. 接收查询参数
2. 调用 IDataiConfigurationService 查询配置列表
3. 使用 ExcelUtil 导出为 Excel 文件
4. 返回文件流

## 测试信息

### 测试环境

- **环境**: 本地开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 导出所有配置 | 无参数 | 导出所有配置 | 导出所有配置 | 通过 |
| 按环境ID导出 | environmentId=1 | 导出指定环境的配置 | 导出指定环境的配置 | 通过 |
| 按配置键导出 | configKey=api.endpoint | 导出指定配置键的配置 | 导出指定配置键的配置 | 通过 |
| 按是否激活导出 | isActive=true | 导出激活的配置 | 导出激活的配置 | 通过 |
| 无权限访问 | 无权限token | 返回401错误 | 返回401错误 | 通过 |
