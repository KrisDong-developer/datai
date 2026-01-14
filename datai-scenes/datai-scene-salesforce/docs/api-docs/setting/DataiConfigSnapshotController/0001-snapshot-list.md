# 接口文档

## 接口信息

- **接口名称**: 查询配置快照列表
- **接口路径**: /setting/snapshot/list
- **请求方法**: GET
- **模块归属**: datai-salesforce-setting
- **版本号**: v1.0
- **创建日期**: 2026-01-14
- **最后更新**: 2026-01-14

## 功能描述

查询配置快照列表，支持分页查询和多条件过滤。返回的快照数据包含快照ID、快照号、环境ID、环境名称、快照描述、快照内容、配置数量、快照状态、ORG类型等信息，便于用户管理和查看配置快照。

## 请求参数

### 查询参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
| pageNum | Integer | 否 | 页码 | 1 | 1 |
| pageSize | Integer | 否 | 每页数量 | 10 | 10 |
| id | Long | 否 | 快照ID | 1 | - |
| snapshotNumber | String | 否 | 快照号 | SNAPSHOT001 | - |
| environmentId | Long | 否 | 环境ID | 1 | - |
| snapshotDesc | String | 否 | 快照描述 | 生产环境快照 | - |
| configCount | Integer | 否 | 配置数量 | 10 | - |
| status | String | 否 | 快照状态 | active | - |
| orgType | String | 否 | ORG类型 | source | - |
| remark | String | 否 | 备注 | 备份快照 | - |
| deptId | Long | 否 | 部门ID | 1 | - |
| createBy | String | 否 | 创建人 | admin | - |
| params | Map | 否 | 请求参数（用于存放查询范围等临时数据） | - | - |

**参数说明**:
- `pageNum`: 当前页码，从1开始
- `pageSize`: 每页显示的记录数
- `snapshotNumber`: 快照号，支持模糊查询
- `environmentId`: 环境ID，精确匹配
- `status`: 快照状态，可选值为 active（激活）、inactive（未激活）等
- `orgType`: ORG类型，可选值为 source（源ORG）或 target（目标ORG）
- `params`: 用于存放查询范围等临时数据

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "total": 100,
  "rows": [
    {
      "id": 1,
      "snapshotNumber": "SNAPSHOT001",
      "environmentId": 1,
      "environmentName": "生产环境",
      "snapshotDesc": "生产环境快照",
      "snapshotContent": "{\"config\":{...}}",
      "configCount": 10,
      "status": "active",
      "orgType": "source",
      "remark": "备份快照",
      "deptId": 1,
      "createBy": "admin",
      "createTime": "2026-01-01 10:00:00",
      "updateBy": "admin",
      "updateTime": "2026-01-14 10:30:00"
    }
  ],
  "code": 200,
  "msg": "查询成功"
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| total | Long | 总记录数 | 100 |
| rows | Array | 快照列表数据 | - |
| code | Integer | 响应码 | 200 |
| msg | String | 响应消息 | 查询成功 |

**rows 数组中每个对象的字段说明**:

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| id | Long | 快照ID | 1 |
| snapshotNumber | String | 快照号 | SNAPSHOT001 |
| environmentId | Long | 环境ID | 1 |
| environmentName | String | 环境名称 | 生产环境 |
| snapshotDesc | String | 快照描述 | 生产环境快照 |
| snapshotContent | String | 快照内容（JSON格式） | {"config":{...}} |
| configCount | Integer | 配置数量 | 10 |
| status | String | 快照状态 | active |
| orgType | String | ORG类型（source/target） | source |
| remark | String | 备注 | 备份快照 |
| deptId | Long | 部门ID | 1 |
| createBy | String | 创建人 | admin |
| createTime | String | 创建时间 | 2026-01-01 10:00:00 |
| updateBy | String | 更新人 | admin |
| updateTime | String | 更新时间 | 2026-01-14 10:30:00 |

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
curl -X GET "http://localhost:8080/setting/snapshot/list?pageNum=1&pageSize=10&orgType=source&status=active" \
  -H "Authorization: Bearer [token]"
```

### 响应示例

**成功**:

```json
{
  "total": 50,
  "rows": [
    {
      "id": 1,
      "snapshotNumber": "SNAPSHOT001",
      "environmentId": 1,
      "environmentName": "生产环境",
      "snapshotDesc": "生产环境快照",
      "configCount": 10,
      "status": "active",
      "orgType": "source"
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

1. **权限验证失败**: 检查用户是否具有 setting:snapshot:list 权限
2. **参数验证失败**: 检查参数格式是否正确
3. **系统错误**: 记录错误日志并返回通用错误信息

## 注意事项

1. 接口需要登录认证，需要携带有效的 token
2. 接口需要 setting:snapshot:list 权限
3. 支持分页查询，默认每页10条记录
4. 支持多条件过滤，可组合使用
5. 返回的数据包含环境名称，便于用户查看
6. 快照内容可能较大，列表查询时不返回完整内容
7. orgType 字段用于区分源ORG和目标ORG的快照
8. 快照状态用于标识快照的可用性

## 相关接口

- [导出配置快照列表](0002-export-snapshot-list.md) - 导出配置快照列表
- [获取配置快照详细信息](0003-get-info.md) - 获取单个快照的详细信息
- [从当前配置生成快照](0008-create-snapshot.md) - 从当前配置生成快照

## 实现细节

1. 接收查询参数，支持分页和多条件过滤
2. 调用 IDataiConfigSnapshotService 查询快照列表
3. 批量查询环境信息，构建环境名称映射
4. 将快照数据转换为 VO 对象，并设置环境名称
5. 返回分页结果

## 测试信息

### 测试环境

- **环境**: 本地开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 查询所有快照 | 无参数 | 返回所有快照 | 返回所有快照 | 通过 |
| 按ORG类型查询 | orgType=source | 返回源ORG快照 | 返回源ORG快照 | 通过 |
| 按状态查询 | status=active | 返回激活快照 | 返回激活快照 | 通过 |
| 分页查询 | pageNum=1,pageSize=10 | 返回第一页10条 | 返回第一页10条 | 通过 |
| 组合查询 | orgType=source,status=active | 返回符合条件的快照 | 返回符合条件的快照 | 通过 |
| 无权限访问 | 无权限token | 返回401错误 | 返回401错误 | 通过 |
