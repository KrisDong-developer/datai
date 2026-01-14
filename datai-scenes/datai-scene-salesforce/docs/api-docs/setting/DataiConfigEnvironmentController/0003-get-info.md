# 接口文档

## 接口信息

- **接口名称**: 获取配置环境详细信息
- **接口路径**: /setting/environment/{id}
- **请求方法**: GET
- **模块归属**: datai-salesforce-setting
- **版本号**: v1.0
- **创建日期**: 2026-01-14
- **最后更新**: 2026-01-14

## 功能描述

根据环境ID获取配置环境的详细信息，包括环境ID、环境名称、环境编码、ORG类型、环境描述、是否激活等完整信息，用于查看或编辑环境配置。

## 请求参数

### 路径参数

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| id | Long | 是 | 环境ID | 1 |

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
    "environmentName": "生产环境",
    "environmentCode": "PROD",
    "orgType": "source",
    "description": "生产环境配置",
    "isActive": true,
    "remark": "源ORG生产环境",
    "createBy": "admin",
    "createTime": "2026-01-01 10:00:00",
    "updateBy": "admin",
    "updateTime": "2026-01-14 10:30:00"
  }
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| code | Integer | 响应码 | 200 |
| msg | String | 响应消息 | 查询成功 |
| data | Object | 环境详细信息 | - |

**data 对象字段说明**:

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| id | Long | 环境ID | 1 |
| deptId | Long | 部门ID | 1 |
| environmentName | String | 环境名称 | 生产环境 |
| environmentCode | String | 环境编码 | PROD |
| orgType | String | ORG类型（source/target） | source |
| description | String | 环境描述 | 生产环境配置 |
| isActive | Boolean | 是否激活 | true |
| remark | String | 备注 | 源ORG生产环境 |
| createBy | String | 创建人 | admin |
| createTime | String | 创建时间 | 2026-01-01 10:00:00 |
| updateBy | String | 更新人 | admin |
| updateTime | String | 更新时间 | 2026-01-14 10:30:00 |

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
| 400 | 参数错误 | 参数验证失败 |
| 401 | 您没有权限执行此操作 | 权限不足 |
| 403 | 访问被拒绝 | 访问被拒绝 |
| 404 | 环境不存在 | 指定的环境ID不存在 |
| 500 | 系统错误 | 系统内部错误 |

## 接口示例

### 请求示例

```bash
curl -X GET "http://localhost:8080/setting/environment/1" \
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
    "environmentName": "生产环境",
    "environmentCode": "PROD",
    "orgType": "source",
    "isActive": true
  }
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

1. **权限验证失败**: 检查用户是否具有 setting:environment:query 权限
2. **参数验证失败**: 检查环境ID是否有效
3. **环境不存在**: 返回404错误，提示环境不存在
4. **系统错误**: 记录错误日志并返回通用错误信息

## 注意事项

1. 接口需要登录认证，需要携带有效的 token
2. 接口需要 setting:environment:query 权限
3. 环境ID必须存在，否则返回404错误
4. 返回的数据包含环境的完整信息，可用于编辑操作
5. orgType 字段用于区分源ORG和目标ORG的环境配置
6. isActive 字段标识环境是否激活，只有激活的环境才能被切换使用
7. 环境编码（environmentCode）是唯一标识，用于环境切换

## 相关接口

- [查询配置环境列表](0001-environment-list.md) - 查询配置环境列表
- [新增配置环境](0004-add-environment.md) - 新增配置环境
- [修改配置环境](0005-edit-environment.md) - 修改配置环境
- [删除配置环境](0006-remove-environment.md) - 删除配置环境

## 实现细节

1. 接收环境ID路径参数
2. 调用 IDataiConfigEnvironmentService 根据ID查询环境
3. 转换为 VO 对象并返回

## 测试信息

### 测试环境

- **环境**: 本地开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 查询存在的环境 | id=1 | 返回环境详细信息 | 返回详细信息 | 通过 |
| 查询不存在的环境 | id=9999 | 返回404错误 | 返回404错误 | 通过 |
| 无效ID格式 | id=abc | 返回参数错误 | 返回参数错误 | 通过 |
| 无权限访问 | 无权限token | 返回401错误 | 返回401错误 | 通过 |
