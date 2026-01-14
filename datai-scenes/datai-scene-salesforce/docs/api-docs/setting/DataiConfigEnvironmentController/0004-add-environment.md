# 接口文档

## 接口信息

- **接口名称**: 新增配置环境
- **接口路径**: /setting/environment
- **请求方法**: POST
- **模块归属**: datai-salesforce-setting
- **版本号**: v1.0
- **创建日期**: 2026-01-14
- **最后更新**: 2026-01-14

## 功能描述

新增配置环境，支持创建源ORG或目标ORG的环境配置。新增的环境可以用于环境切换和管理，支持设置环境名称、环境编码、ORG类型、环境描述、是否激活等信息。

## 请求参数

### 请求体参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
| deptId | Long | 否 | 部门ID | 1 | - |
| environmentName | String | 是 | 环境名称 | 生产环境 | - |
| environmentCode | String | 是 | 环境编码 | PROD | - |
| orgType | String | 是 | ORG类型 | source | - |
| description | String | 否 | 环境描述 | 生产环境配置 | - |
| isActive | Boolean | 否 | 是否激活 | true | false |
| remark | String | 否 | 备注 | 源ORG生产环境 | - |

**参数说明**:
- `environmentCode`: 环境编码必须唯一，用于环境切换
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
| data | Integer | 影响的行数（新增成功的记录数） | 1 |

### 失败响应

**HTTP 状态码**: 400/401/403/500

```json
{
  "code": 400,
  "msg": "环境编码已存在",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 400 | 参数错误 | 参数验证失败或环境编码已存在 |
| 401 | 您没有权限执行此操作 | 权限不足 |
| 403 | 访问被拒绝 | 访问被拒绝 |
| 500 | 系统错误 | 系统内部错误 |

## 接口示例

### 请求示例

```bash
curl -X POST "http://localhost:8080/setting/environment" \
  -H "Authorization: Bearer [token]" \
  -H "Content-Type: application/json" \
  -d '{
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
  "code": 400,
  "msg": "环境编码已存在",
  "data": null
}
```

## 错误处理

1. **权限验证失败**: 检查用户是否具有 setting:environment:add 权限
2. **参数验证失败**: 检查必填参数是否完整，参数格式是否正确
3. **环境编码已存在**: 检查环境编码是否唯一，如已存在则返回错误
4. **系统错误**: 记录错误日志并返回通用错误信息

## 注意事项

1. 接口需要登录认证，需要携带有效的 token
2. 接口需要 setting:environment:add 权限
3. 环境编码（environmentCode）必须唯一，建议使用大写字母和数字的组合
4. orgType 字段用于区分源ORG和目标ORG的环境配置
5. isActive 字段标识环境是否激活，只有激活的环境才能被切换使用
6. 新增操作会记录到操作日志中
7. 环境名称和环境编码为必填项
8. 建议在新增前先查询确认环境编码是否已存在

## 相关接口

- [查询配置环境列表](0001-environment-list.md) - 查询配置环境列表
- [获取配置环境详细信息](0003-get-info.md) - 获取单个环境的详细信息
- [修改配置环境](0005-edit-environment.md) - 修改配置环境
- [删除配置环境](0006-remove-environment.md) - 删除配置环境

## 实现细节

1. 接收请求参数，转换为 DataiConfigEnvironment 对象
2. 调用 IDataiConfigEnvironmentService 新增环境
3. 返回操作结果

## 测试信息

### 测试环境

- **环境**: 本地开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 新增源ORG环境 | orgType=source | 新增成功 | 新增成功 | 通过 |
| 新增目标ORG环境 | orgType=target | 新增成功 | 新增成功 | 通过 |
| 新增激活环境 | isActive=true | 新增成功，环境可切换 | 新增成功 | 通过 |
| 新增未激活环境 | isActive=false | 新增成功，环境不可切换 | 新增成功 | 通过 |
| 环境编码重复 | environmentCode=PROD（已存在） | 返回参数错误 | 返回参数错误 | 通过 |
| 缺少必填参数 | 缺少environmentName | 返回参数错误 | 返回参数错误 | 通过 |
| 无权限访问 | 无权限token | 返回401错误 | 返回401错误 | 通过 |
