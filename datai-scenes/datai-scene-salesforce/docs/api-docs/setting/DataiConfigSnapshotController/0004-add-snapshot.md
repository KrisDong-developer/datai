# 接口文档

## 接口信息

- **接口名称**: 新增配置快照
- **接口路径**: /setting/snapshot
- **请求方法**: POST
- **模块归属**: datai-salesforce-setting
- **版本号**: v1.0
- **创建日期**: 2026-01-14
- **最后更新**: 2026-01-14

## 功能描述

新增配置快照，保存配置快照的基本信息，包括快照号、环境ID、快照描述、快照内容、配置数量、快照状态、ORG类型等。新增成功后，系统会自动记录创建人和创建时间。

## 请求参数

### 请求参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
| snapshotNumber | String | 是 | 快照号 | SNAPSHOT001 | - |
| environmentId | Long | 是 | 环境ID | 1 | - |
| snapshotDesc | String | 否 | 快照描述 | 生产环境快照 | - |
| snapshotContent | String | 否 | 快照内容（JSON格式） | {"config":{...}} | - |
| configCount | Integer | 否 | 配置数量 | 10 | - |
| status | String | 否 | 快照状态 | active | - |
| orgType | String | 否 | ORG类型 | source | - |
| remark | String | 否 | 备注 | 备份快照 | - |
| deptId | Long | 否 | 部门ID | 1 | - |

**参数说明**:
- `snapshotNumber`: 快照号，唯一标识一个快照
- `environmentId`: 环境ID，关联到配置环境表
- `snapshotDesc`: 快照描述，用于说明快照的用途
- `snapshotContent`: 快照内容，JSON格式的配置数据
- `configCount`: 配置数量，快照中包含的配置项数量
- `status`: 快照状态，可选值为 active（激活）、inactive（未激活）等
- `orgType`: ORG类型，可选值为 source（源ORG）或 target（目标ORG）
- `remark`: 备注，用于记录其他信息

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "msg": "新增成功",
  "data": 1
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| code | Integer | 响应码 | 200 |
| msg | String | 响应消息 | 新增成功 |
| data | Integer | 影响的行数 | 1 |

### 失败响应

**HTTP 状态码**: 400/401/403/500

```json
{
  "code": 500,
  "msg": "新增失败",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 400 | 参数错误 | 参数验证失败 |
| 401 | 您没有权限执行此操作 | 权限不足 |
| 403 | 访问被拒绝 | 访问被拒绝 |
| 500 | 新增失败 | 系统错误 |

## 接口示例

### 请求示例

```bash
curl -X POST "http://localhost:8080/setting/snapshot" \
  -H "Authorization: Bearer [token]" \
  -H "Content-Type: application/json" \
  -d '{
    "snapshotNumber": "SNAPSHOT001",
    "environmentId": 1,
    "snapshotDesc": "生产环境快照",
    "snapshotContent": "{\"config\":{...}}",
    "configCount": 10,
    "status": "active",
    "orgType": "source",
    "remark": "备份快照"
  }'
```

### 响应示例

**成功**:

```json
{
  "code": 200,
  "msg": "新增成功",
  "data": 1
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

1. **权限验证失败**: 检查用户是否具有 setting:snapshot:add 权限
2. **参数验证失败**: 检查必填参数是否完整，参数格式是否正确
3. **快照号重复**: 检查快照号是否已存在，如已存在则返回错误
4. **环境不存在**: 检查环境ID是否存在，如不存在则返回错误
5. **系统错误**: 记录错误日志并返回通用错误信息

## 注意事项

1. 接口需要登录认证，需要携带有效的 token
2. 接口需要 setting:snapshot:add 权限
3. 快照号和环境ID为必填项
4. 快照号必须唯一，不能重复
5. 环境ID必须存在于配置环境表中
6. 快照内容建议使用JSON格式
7. 新增操作会记录到操作日志中
8. orgType 字段用于区分源ORG和目标ORG的快照
9. 系统会自动记录创建人和创建时间
10. 建议在新增前先查询确认快照号和环境ID

## 相关接口

- [查询配置快照列表](0001-snapshot-list.md) - 查询配置快照列表
- [获取配置快照详细信息](0003-get-info.md) - 获取单个快照的详细信息
- [从当前配置生成快照](0008-create-snapshot.md) - 从当前配置生成快照
- [修改配置快照](0005-edit-snapshot.md) - 修改配置快照

## 实现细节

1. 接收快照参数
2. 转换为业务对象
3. 调用 IDataiConfigSnapshotService 新增快照
4. 返回新增结果

## 测试信息

### 测试环境

- **环境**: 本地开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 新增源ORG快照 | orgType=source | 新增成功 | 新增成功 | 通过 |
| 新增目标ORG快照 | orgType=target | 新增成功 | 新增成功 | 通过 |
| 新增完整快照 | 所有必填参数 | 新增成功 | 新增成功 | 通过 |
| 快照号重复 | snapshotNumber=SNAPSHOT001（已存在） | 返回新增失败 | 返回新增失败 | 通过 |
| 环境不存在 | environmentId=999 | 返回新增失败 | 返回新增失败 | 通过 |
| 缺少必填参数 | 缺少snapshotNumber | 返回参数错误 | 返回参数错误 | 通过 |
| 无权限访问 | 无权限token | 返回401错误 | 返回401错误 | 通过 |
