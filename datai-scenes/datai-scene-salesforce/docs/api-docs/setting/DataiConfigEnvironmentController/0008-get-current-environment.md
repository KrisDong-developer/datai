# 接口文档

## 接口信息

- **接口名称**: 获取当前激活的环境
- **接口路径**: /setting/environment/current
- **请求方法**: GET
- **模块归属**: datai-salesforce-setting
- **版本号**: v1.0
- **创建日期**: 2026-01-14
- **最后更新**: 2026-01-14

## 功能描述

根据ORG类型获取当前激活的环境信息，返回环境的完整详细信息，包括环境ID、环境名称、环境编码、ORG类型、环境描述、是否激活等。如果没有激活的环境，则返回错误信息。

## 请求参数

### 请求参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
| orgType | String | 是 | ORG类型 | source | - |

**参数说明**:
- `orgType`: ORG类型，可选值为 source（源ORG）或 target（目标ORG）

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
| data | Object | 当前激活的环境详细信息 | - |

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

**HTTP 状态码**: 400/401/403/500

```json
{
  "code": 500,
  "msg": "未找到当前激活的环境",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 400 | 参数错误 | 参数验证失败 |
| 401 | 您没有权限执行此操作 | 权限不足 |
| 403 | 访问被拒绝 | 访问被拒绝 |
| 500 | 未找到当前激活的环境 | 没有激活的环境 |

## 接口示例

### 请求示例

```bash
curl -X GET "http://localhost:8080/setting/environment/current?orgType=source" \
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
  "code": 500,
  "msg": "未找到当前激活的环境",
  "data": null
}
```

## 错误处理

1. **权限验证失败**: 检查用户是否具有 setting:environment:query 权限
2. **参数验证失败**: 检查必填参数是否完整，参数格式是否正确
3. **环境不存在**: 检查是否有激活的环境，如没有则返回错误
4. **系统错误**: 记录错误日志并返回通用错误信息

## 注意事项

1. 接口需要登录认证，需要携带有效的 token
2. 接口需要 setting:environment:query 权限
3. orgType 为必填项，用于区分源ORG和目标ORG的环境
4. 如果没有激活的环境，则返回错误信息
5. 返回的环境信息是完整的详细信息
6. 可以用于确认当前使用的环境
7. orgType 字段用于区分源ORG和目标ORG的环境
8. 建议在进行环境切换前先查询当前激活的环境

## 相关接口

- [查询配置环境列表](0001-environment-list.md) - 查询配置环境列表
- [获取配置环境详细信息](0003-get-info.md) - 获取单个环境的详细信息
- [切换当前环境](0007-switch-environment.md) - 切换当前环境

## 实现细节

1. 接收 orgType 参数
2. 调用 IDataiConfigEnvironmentService 获取当前激活的环境
3. 转换为 VO 对象并返回

## 测试信息

### 测试环境

- **环境**: 本地开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 查询源ORG环境 | orgType=source | 返回环境信息 | 返回环境信息 | 通过 |
| 查询目标ORG环境 | orgType=target | 返回环境信息 | 返回环境信息 | 通过 |
| 查询无激活环境 | orgType=source（无激活） | 返回未找到环境 | 返回未找到环境 | 通过 |
| 缺少必填参数 | 缺少orgType | 返回参数错误 | 返回参数错误 | 通过 |
| 无权限访问 | 无权限token | 返回401错误 | 返回401错误 | 通过 |
