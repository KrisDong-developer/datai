# 接口文档

## 接口信息

- **接口名称**: 从当前配置生成快照
- **接口路径**: /setting/snapshot/create
- **请求方法**: POST
- **模块归属**: datai-salesforce-setting
- **版本号**: v1.0
- **创建日期**: 2026-01-14
- **最后更新**: 2026-01-14

## 功能描述

从当前配置生成快照，自动收集当前环境的配置信息并保存为快照。生成的快照包含快照号、环境ID、快照描述、快照内容、配置数量、快照状态、ORG类型等信息。生成成功后，系统会自动记录创建人和创建时间。

## 请求参数

### 请求参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
| snapshotNumber | String | 是 | 快照号 | SNAPSHOT001 | - |
| environmentId | Long | 是 | 环境ID | 1 | - |
| snapshotDesc | String | 否 | 快照描述 | 生产环境快照 | - |

**参数说明**:
- `snapshotNumber`: 快照号，唯一标识一个快照
- `environmentId`: 环境ID，关联到配置环境表
- `snapshotDesc`: 快照描述，用于说明快照的用途

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "snapshotNumber": "SNAPSHOT001",
    "environmentId": 1,
    "environmentName": "生产环境",
    "snapshotDesc": "生产环境快照",
    "snapshotContent": "{\"config\":{...}}",
    "configCount": 10,
    "status": "active",
    "orgType": "source",
    "remark": null,
    "deptId": null,
    "createBy": "admin",
    "createTime": "2026-01-14 10:00:00",
    "updateBy": null,
    "updateTime": null
  }
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| code | Integer | 响应码 | 200 |
| msg | String | 响应消息 | 操作成功 |
| data | Object | 快照详细信息 | - |

**data 对象字段说明**:

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
| remark | String | 备注 | null |
| deptId | Long | 部门ID | null |
| createBy | String | 创建人 | admin |
| createTime | String | 创建时间 | 2026-01-14 10:00:00 |
| updateBy | String | 更新人 | null |
| updateTime | String | 更新时间 | null |

### 失败响应

**HTTP 状态码**: 400/401/403/500

```json
{
  "code": 500,
  "msg": "生成快照失败",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 400 | 参数错误 | 参数验证失败 |
| 401 | 您没有权限执行此操作 | 权限不足 |
| 403 | 访问被拒绝 | 访问被拒绝 |
| 500 | 生成快照失败 | 系统错误 |

## 接口示例

### 请求示例

```bash
curl -X POST "http://localhost:8080/setting/snapshot/create" \
  -H "Authorization: Bearer [token]" \
  -H "Content-Type: application/json" \
  -d '{
    "snapshotNumber": "SNAPSHOT001",
    "environmentId": 1,
    "snapshotDesc": "生产环境快照"
  }'
```

### 响应示例

**成功**:

```json
{
  "code": 200,
  "msg": "操作成功",
  "data": {
    "id": 1,
    "snapshotNumber": "SNAPSHOT001",
    "environmentId": 1,
    "environmentName": "生产环境",
    "snapshotDesc": "生产环境快照",
    "configCount": 10,
    "status": "active",
    "orgType": "source"
  }
}
```

**失败**:

```json
{
  "code": 400,
  "msg": "参数错误",
  "data": null
}
```

## 错误处理

1. **权限验证失败**: 检查用户是否具有 setting:snapshot:create 权限
2. **参数验证失败**: 检查必填参数是否完整，参数格式是否正确
3. **快照号重复**: 检查快照号是否已存在，如已存在则返回错误
4. **环境不存在**: 检查环境ID是否存在，如不存在则返回错误
5. **配置不存在**: 检查环境配置是否存在，如不存在则返回错误
6. **系统错误**: 记录错误日志并返回通用错误信息

## 注意事项

1. 接口需要登录认证，需要携带有效的 token
2. 接口需要 setting:snapshot:create 权限
3. 快照号和环境ID为必填项
4. 快照号必须唯一，不能重复
5. 环境ID必须存在于配置环境表中
6. 系统会自动收集当前环境的配置信息并保存为快照
7. 生成操作会记录到操作日志中
8. orgType 字段会根据环境自动设置
9. 系统会自动记录创建人和创建时间
10. 建议在生成前先查询确认环境信息
11. 返回的数据包含环境名称，便于用户查看
12. 快照内容会自动填充为当前环境的配置数据

## 相关接口

- [查询配置快照列表](0001-snapshot-list.md) - 查询配置快照列表
- [获取配置快照详细信息](0003-get-info.md) - 获取单个快照的详细信息
- [新增配置快照](0004-add-snapshot.md) - 新增配置快照
- [恢复快照](0009-restore-snapshot.md) - 恢复快照
- [获取快照详细信息（包含配置内容）](0010-get-snapshot-detail.md) - 获取快照详细信息（包含配置内容）

## 实现细节

1. 接收快照参数
2. 调用 IDataiConfigSnapshotService 从当前配置生成快照
3. 查询环境信息，设置环境名称
4. 转换为 VO 对象并返回

## 测试信息

### 测试环境

- **环境**: 本地开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 生成源ORG快照 | environmentId=1（source） | 生成成功 | 生成成功 | 通过 |
| 生成目标ORG快照 | environmentId=2（target） | 生成成功 | 生成成功 | 通过 |
| 生成完整快照 | 所有必填参数 | 生成成功 | 生成成功 | 通过 |
| 快照号重复 | snapshotNumber=SNAPSHOT001（已存在） | 返回生成失败 | 返回生成失败 | 通过 |
| 环境不存在 | environmentId=999 | 返回生成失败 | 返回生成失败 | 通过 |
| 缺少必填参数 | 缺少snapshotNumber | 返回参数错误 | 返回参数错误 | 通过 |
| 无权限访问 | 无权限token | 返回401错误 | 返回401错误 | 通过 |
