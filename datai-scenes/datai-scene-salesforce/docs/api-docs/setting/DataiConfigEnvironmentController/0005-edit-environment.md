# 接口文档

## 接口信息

- **接口名称**: 修改配置环境
- **接口路径**: /setting/environment
- **请求方法**: PUT
- **模块归属**: datai-salesforce-setting
- **版本号**: v1.0
- **创建日期**: 2026-01-14
- **最后更新**: 2026-01-14

## 功能描述

修改配置环境信息，支持更新环境名称、环境编码、ORG类型、环境描述、是否激活等信息。修改后的环境信息会立即生效，但需要注意环境编码的变更可能会影响环境切换功能。

## 请求参数

### 请求体参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
| id | Long | 是 | 环境ID | 1 | - |
| deptId | Long | 否 | 部门ID | 1 | - |
| environmentName | String | 否 | 环境名称 | 生产环境 | - |
| environmentCode | String | 否 | 环境编码 | PROD | - |
| orgType | String | 否 | ORG类型 | source | - |
| description | String | 否 | 环境描述 | 生产环境配置 | - |
| isActive | Boolean | 否 | 是否激活 | true | - |
| remark | String | 否 | 备注 | 源ORG生产环境 | - |

**参数说明**:
- `id`: 环境ID为必填项，用于标识要修改的环境
- `environmentCode`: 环境编码必须唯一，修改时需确保不与其他环境重复
- `orgType`: ORG类型，可选值为 source（源ORG）或 target（目标ORG）
- `isActive`: 是否激活，只有激活的环境才能被切换使用

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
| data | Integer | 影响的行数（修改成功的记录数） | 1 |

### 失败响应

**HTTP 状态码**: 400/401/403/404/500

```json
{
  "code": 404,
  "msg": "环境不存在",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 400 | 参数错误 | 参数验证失败或环境编码已存在 |
| 401 | 您没有权限执行此操作 | 权限不足 |
| 403 | 访问被拒绝 | 访问被拒绝 |
| 404 | 环境不存在 | 指定的环境ID不存在 |
| 500 | 系统错误 | 系统内部错误 |

## 接口示例

### 请求示例

```bash
curl -X PUT "http://localhost:8080/setting/environment" \
  -H "Authorization: Bearer [token]" \
  -H "Content-Type: application/json" \
  -d '{
    "id": 1,
    "environmentName": "生产环境",
    "environmentCode": "PROD",
    "orgType": "source",
    "description": "生产环境配置",
    "isActive": true,
    "remark": "源ORG生产环境"
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
  "msg": "环境不存在",
  "data": null
}
```

## 错误处理

1. **权限验证失败**: 检查用户是否具有 setting:environment:edit 权限
2. **参数验证失败**: 检查必填参数是否完整，参数格式是否正确
3. **环境不存在**: 检查环境ID是否存在，如不存在则返回404错误
4. **环境编码已存在**: 检查环境编码是否唯一，如已存在则返回错误
5. **系统错误**: 记录错误日志并返回通用错误信息

## 注意事项

1. 接口需要登录认证，需要携带有效的 token
2. 接口需要 setting:environment:edit 权限
3. 环境ID为必填项，用于标识要修改的环境
4. 环境编码（environmentCode）必须唯一，修改时需确保不与其他环境重复
5. orgType 字段用于区分源ORG和目标ORG的环境配置
6. isActive 字段标识环境是否激活，只有激活的环境才能被切换使用
7. 修改操作会记录到操作日志中
8. 修改环境编码可能会影响环境切换功能，请谨慎操作
9. 如果修改的环境是当前激活的环境，修改后仍然保持激活状态
10. 建议在修改前先查询确认环境信息

## 相关接口

- [查询配置环境列表](0001-environment-list.md) - 查询配置环境列表
- [获取配置环境详细信息](0003-get-info.md) - 获取单个环境的详细信息
- [新增配置环境](0004-add-environment.md) - 新增配置环境
- [删除配置环境](0006-remove-environment.md) - 删除配置环境

## 实现细节

1. 接收请求参数，转换为 DataiConfigEnvironment 对象
2. 调用 IDataiConfigEnvironmentService 修改环境
3. 返回操作结果

## 测试信息

### 测试环境

- **环境**: 本地开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 修改环境名称 | environmentName=新环境 | 修改成功 | 修改成功 | 通过 |
| 修改环境编码 | environmentCode=NEW_CODE | 修改成功 | 修改成功 | 通过 |
| 修改ORG类型 | orgType=target | 修改成功 | 修改成功 | 通过 |
| 修改激活状态 | isActive=false | 修改成功，环境不可切换 | 修改成功 | 通过 |
| 修改不存在的环境 | id=9999 | 返回404错误 | 返回404错误 | 通过 |
| 环境编码重复 | environmentCode=PROD（已存在） | 返回参数错误 | 返回参数错误 | 通过 |
| 缺少必填参数 | 缺少id | 返回参数错误 | 返回参数错误 | 通过 |
| 无权限访问 | 无权限token | 返回401错误 | 返回401错误 | 通过 |
