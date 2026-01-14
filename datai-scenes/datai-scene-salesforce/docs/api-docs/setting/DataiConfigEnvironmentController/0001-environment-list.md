# 接口文档

## 接口信息

- **接口名称**: 查询配置环境列表
- **接口路径**: /setting/environment/list
- **请求方法**: GET
- **模块归属**: datai-salesforce-setting
- **版本号**: v1.0
- **创建日期**: 2026-01-14
- **最后更新**: 2026-01-14

## 功能描述

查询配置环境列表，支持分页查询和多条件过滤。返回的环境数据包含环境ID、环境名称、环境编码、ORG类型、环境描述、是否激活等信息，便于用户管理和切换不同环境。

## 请求参数

### 查询参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
| pageNum | Integer | 否 | 页码 | 1 | 1 |
| pageSize | Integer | 否 | 每页数量 | 10 | 10 |
| deptId | Long | 否 | 部门ID | 1 | - |
| environmentName | String | 否 | 环境名称 | 生产环境 | - |
| environmentCode | String | 否 | 环境编码 | PROD | - |
| orgType | String | 否 | ORG类型 | source | - |
| description | String | 否 | 环境描述 | 生产环境配置 | - |
| isActive | Boolean | 否 | 是否激活 | true | - |
| remark | String | 否 | 备注 | - | - |
| createBy | String | 否 | 创建人 | admin | - |
| createTime | Date | 否 | 创建时间 | 2026-01-01 00:00:00 | - |
| updateBy | String | 否 | 更新人 | admin | - |
| updateTime | Date | 否 | 更新时间 | 2026-01-14 10:30:00 | - |

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
    },
    {
      "id": 2,
      "deptId": 1,
      "environmentName": "测试环境",
      "environmentCode": "TEST",
      "orgType": "source",
      "description": "测试环境配置",
      "isActive": true,
      "remark": "源ORG测试环境",
      "createBy": "admin",
      "createTime": "2026-01-01 10:00:00",
      "updateBy": null,
      "updateTime": null
    }
  ],
  "code": 200,
  "msg": "查询成功"
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| total | Integer | 总记录数 | 100 |
| rows | Array | 数据列表 | - |
| code | Integer | 响应码 | 200 |
| msg | String | 响应消息 | 查询成功 |

**rows 数组元素字段说明**:

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

**HTTP 状态码**: 401/403/500

```json
{
  "code": 401,
  "msg": "您没有权限执行此操作",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 401 | 您没有权限执行此操作 | 权限不足 |
| 403 | 访问被拒绝 | 访问被拒绝 |
| 500 | 系统错误 | 系统内部错误 |

## 接口示例

### 请求示例

```bash
curl -X GET "http://localhost:8080/setting/environment/list?pageNum=1&pageSize=10&orgType=source&isActive=true" \
  -H "Authorization: Bearer [token]"
```

### 响应示例

**成功**:

```json
{
  "total": 100,
  "rows": [
    {
      "id": 1,
      "environmentName": "生产环境",
      "environmentCode": "PROD",
      "orgType": "source",
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
  "code": 401,
  "msg": "您没有权限执行此操作",
  "data": null
}
```

## 错误处理

1. **权限验证失败**: 检查用户是否具有 setting:environment:list 权限
2. **参数验证失败**: 检查查询参数是否有效
3. **系统错误**: 记录错误日志并返回通用错误信息

## 注意事项

1. 接口需要登录认证，需要携带有效的 token
2. 接口需要 setting:environment:list 权限
3. orgType 字段用于区分源ORG和目标ORG的环境配置
4. isActive 字段标识环境是否激活，只有激活的环境才能被切换使用
5. 支持按环境名称、环境编码、ORG类型等多条件组合查询
6. 环境编码（environmentCode）是唯一标识，用于环境切换

## 相关接口

- [导出配置环境列表](0002-export-environment-list.md) - 导出配置环境列表到 Excel
- [获取配置环境详细信息](0003-get-info.md) - 获取单个环境的详细信息
- [切换当前环境](0007-switch-environment.md) - 切换当前激活的环境
- [获取当前激活的环境](0008-get-current-environment.md) - 获取当前激活的环境信息

## 实现细节

1. 接收查询参数，转换为 DataiConfigEnvironment 对象
2. 调用 IDataiConfigEnvironmentService 查询环境列表
3. 转换为 VO 对象并返回分页数据

## 测试信息

### 测试环境

- **环境**: 本地开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 查询所有环境 | 无参数 | 返回所有环境分页数据 | 返回分页数据 | 通过 |
| 按ORG类型查询 | orgType=source | 返回source类型的环境 | 返回source环境 | 通过 |
| 按激活状态查询 | isActive=true | 返回已激活的环境 | 返回已激活环境 | 通过 |
| 按环境编码查询 | environmentCode=PROD | 返回指定编码的环境 | 返回指定环境 | 通过 |
| 无权限访问 | 无权限token | 返回401错误 | 返回401错误 | 通过 |
