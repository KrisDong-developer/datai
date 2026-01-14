# 接口文档

## 接口信息

- **接口名称**: 获取快照详细信息（包含配置内容）
- **接口路径**: /setting/snapshot/{snapshotId}/detail
- **请求方法**: GET
- **模块归属**: datai-salesforce-setting
- **版本号**: v1.0
- **创建日期**: 2026-01-14
- **最后更新**: 2026-01-14

## 功能描述

根据快照ID获取配置快照的详细信息，包括完整的配置内容。返回的快照数据包含快照ID、快照号、环境ID、环境名称、快照描述、快照内容、配置数量、快照状态、ORG类型等信息。如果快照不存在，则返回错误信息。

## 请求参数

### 路径参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
| snapshotId | String | 是 | 快照ID | 1 | - |

**参数说明**:
- `snapshotId`: 快照ID，用于查询指定的快照

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "id": 1,
    "snapshotNumber": "SNAPSHOT001",
    "environmentId": 1,
    "environmentName": "生产环境",
    "snapshotDesc": "生产环境快照",
    "snapshotContent": "{\"config\":{\"key1\":\"value1\",\"key2\":\"value2\"}}",
    "configCount": 2,
    "status": "active",
    "orgType": "source",
    "remark": "备份快照",
    "deptId": 1,
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
| data | Object | 快照详细信息 | - |

**data 对象字段说明**:

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| id | Long | 快照ID | 1 |
| snapshotNumber | String | 快照号 | SNAPSHOT001 |
| environmentId | Long | 环境ID | 1 |
| environmentName | String | 环境名称 | 生产环境 |
| snapshotDesc | String | 快照描述 | 生产环境快照 |
| snapshotContent | String | 快照内容（JSON格式） | {"config":{"key1":"value1","key2":"value2"}} |
| configCount | Integer | 配置数量 | 2 |
| status | String | 快照状态 | active |
| orgType | String | ORG类型（source/target） | source |
| remark | String | 备注 | 备份快照 |
| deptId | Long | 部门ID | 1 |
| createBy | String | 创建人 | admin |
| createTime | String | 创建时间 | 2026-01-01 10:00:00 |
| updateBy | String | 更新人 | admin |
| updateTime | String | 更新时间 | 2026-01-14 10:30:00 |

### 失败响应

**HTTP 状态码**: 400/401/403/404/500

```json
{
  "code": 404,
  "msg": "快照不存在",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 400 | 参数错误 | 参数验证失败 |
| 401 | 您没有权限执行此操作 | 权限不足 |
| 403 | 访问被拒绝 | 访问被拒绝 |
| 404 | 快照不存在 | 快照不存在 |
| 500 | 查询失败 | 系统错误 |

## 接口示例

### 请求示例

```bash
curl -X GET "http://localhost:8080/setting/snapshot/1/detail" \
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
    "snapshotNumber": "SNAPSHOT001",
    "environmentId": 1,
    "environmentName": "生产环境",
    "snapshotDesc": "生产环境快照",
    "snapshotContent": "{\"config\":{\"key1\":\"value1\",\"key2\":\"value2\"}}",
    "configCount": 2,
    "status": "active",
    "orgType": "source"
  }
}
```

**失败**:

```json
{
  "code": 404,
  "msg": "快照不存在",
  "data": null
}
```

## 错误处理

1. **权限验证失败**: 检查用户是否具有 setting:snapshot:query 权限
2. **参数验证失败**: 检查参数格式是否正确
3. **快照不存在**: 检查快照是否存在，如不存在则返回错误
4. **系统错误**: 记录错误日志并返回通用错误信息

## 注意事项

1. 接口需要登录认证，需要携带有效的 token
2. 接口需要 setting:snapshot:query 权限
3. 快照ID为必填项，通过路径参数传递
4. 返回的数据包含环境名称，便于用户查看
5. 返回的数据包含完整的快照内容（JSON格式）
6. orgType 字段用于区分源ORG和目标ORG的快照
7. 快照状态用于标识快照的可用性
8. 如果快照不存在，则返回404错误
9. 快照内容可能较大，建议在客户端进行分页或懒加载
10. 此接口与 getInfo 接口的区别在于返回的快照内容更详细

## 相关接口

- [查询配置快照列表](0001-snapshot-list.md) - 查询配置快照列表
- [获取配置快照详细信息](0003-get-info.md) - 获取单个快照的详细信息
- [从当前配置生成快照](0007-create-snapshot.md) - 从当前配置生成快照
- [恢复快照](0008-restore-snapshot.md) - 恢复快照
- [比较两个快照的差异](0011-compare-snapshots.md) - 比较两个快照的差异

## 实现细节

1. 接收快照ID参数
2. 调用 IDataiConfigSnapshotService 查询快照详细信息
3. 查询环境信息，设置环境名称
4. 转换为 VO 对象并返回

## 测试信息

### 测试环境

- **环境**: 本地开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 查询存在的快照 | snapshotId=1 | 返回快照详细信息 | 返回快照详细信息 | 通过 |
| 查询不存在的快照 | snapshotId=999 | 返回404错误 | 返回404错误 | 通过 |
| 查询源ORG快照 | snapshotId=1（source） | 返回源ORG快照 | 返回源ORG快照 | 通过 |
| 查询目标ORG快照 | snapshotId=2（target） | 返回目标ORG快照 | 返回目标ORG快照 | 通过 |
| 缺少必填参数 | 缺少snapshotId | 返回参数错误 | 返回参数错误 | 通过 |
| 无权限访问 | 无权限token | 返回401错误 | 返回401错误 | 通过 |
