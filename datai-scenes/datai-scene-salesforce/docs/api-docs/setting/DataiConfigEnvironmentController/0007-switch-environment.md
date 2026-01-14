# 接口文档

## 接口信息

- **接口名称**: 切换当前环境
- **接口路径**: /setting/environment/switch
- **请求方法**: POST
- **模块归属**: datai-salesforce-setting
- **版本号**: v1.0
- **创建日期**: 2026-01-14
- **最后更新**: 2026-01-14

## 功能描述

根据环境编码和ORG类型切换当前激活的环境。切换成功后，系统会记录切换原因，并返回切换后的环境信息。只有已激活的环境才能被切换使用。

## 请求参数

### 请求参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
| environmentCode | String | 是 | 环境编码 | PROD | - |
| orgType | String | 是 | ORG类型 | source | - |
| switchReason | String | 否 | 切换原因 | 手动切换 | 手动切换 |

**参数说明**:
- `environmentCode`: 要切换到的环境编码，必须是已存在的环境
- `orgType`: ORG类型，可选值为 source（源ORG）或 target（目标ORG）
- `switchReason`: 切换原因，用于记录环境切换的原因，默认为"手动切换"

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "msg": "环境切换成功：生产环境",
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
| msg | String | 响应消息 | 环境切换成功：生产环境 |
| data | Object | 切换后的环境详细信息 | - |

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
  "msg": "环境切换失败，请检查环境编码是否正确且已激活",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 400 | 参数错误 | 参数验证失败 |
| 401 | 您没有权限执行此操作 | 权限不足 |
| 403 | 访问被拒绝 | 访问被拒绝 |
| 500 | 环境切换失败，请检查环境编码是否正确且已激活 | 环境不存在或未激活 |

## 接口示例

### 请求示例

```bash
curl -X POST "http://localhost:8080/setting/environment/switch" \
  -H "Authorization: Bearer [token]" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -d "environmentCode=PROD&orgType=source&switchReason=手动切换"
```

### 响应示例

**成功**:

```json
{
  "code": 200,
  "msg": "环境切换成功：生产环境",
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
  "msg": "环境切换失败，请检查环境编码是否正确且已激活",
  "data": null
}
```

## 错误处理

1. **权限验证失败**: 检查用户是否具有 setting:environment:switch 权限
2. **参数验证失败**: 检查必填参数是否完整，参数格式是否正确
3. **环境不存在**: 检查环境编码是否存在，如不存在则返回错误
4. **环境未激活**: 检查环境是否已激活，如未激活则返回错误
5. **系统错误**: 记录错误日志并返回通用错误信息

## 注意事项

1. 接口需要登录认证，需要携带有效的 token
2. 接口需要 setting:environment:switch 权限
3. 环境编码和ORG类型为必填项
4. 只有已激活的环境才能被切换使用
5. 切换原因用于记录环境切换的原因，建议填写
6. 切换操作会记录到操作日志中
7. 切换成功后会返回切换后的环境信息
8. orgType 字段用于区分源ORG和目标ORG的环境切换
9. 切换环境会影响后续的 Salesforce 连接操作
10. 建议在切换前先查询确认环境信息

## 相关接口

- [查询配置环境列表](0001-environment-list.md) - 查询配置环境列表
- [获取配置环境详细信息](0003-get-info.md) - 获取单个环境的详细信息
- [获取当前激活的环境](0008-get-current-environment.md) - 获取当前激活的环境

## 实现细节

1. 接收环境编码、ORG类型和切换原因参数
2. 调用 IDataiConfigEnvironmentService 切换环境
3. 切换成功后获取当前激活的环境信息
4. 转换为 VO 对象并返回

## 测试信息

### 测试环境

- **环境**: 本地开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 切换源ORG环境 | orgType=source | 切换成功 | 切换成功 | 通过 |
| 切换目标ORG环境 | orgType=target | 切换成功 | 切换成功 | 通过 |
| 切换已激活环境 | environmentCode=PROD（已激活） | 切换成功 | 切换成功 | 通过 |
| 切换未激活环境 | environmentCode=TEST（未激活） | 返回切换失败 | 返回切换失败 | 通过 |
| 切换不存在的环境 | environmentCode=NOT_EXIST | 返回切换失败 | 返回切换失败 | 通过 |
| 缺少必填参数 | 缺少environmentCode | 返回参数错误 | 返回参数错误 | 通过 |
| 无权限访问 | 无权限token | 返回401错误 | 返回401错误 | 通过 |
