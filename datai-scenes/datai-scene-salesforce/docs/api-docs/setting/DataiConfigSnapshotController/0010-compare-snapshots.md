# 接口文档

## 接口信息

- **接口名称**: 比较两个快照的差异
- **接口路径**: /setting/snapshot/{snapshotId1}/compare/{snapshotId2}
- **请求方法**: GET
- **模块归属**: datai-salesforce-setting
- **版本号**: v1.0
- **创建日期**: 2026-01-14
- **最后更新**: 2026-01-14

## 功能描述

比较两个快照的配置差异，返回差异信息。差异信息包括新增、修改、删除的配置项，以及具体的差异内容。如果快照不存在，则返回错误信息。

## 请求参数

### 路径参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
| snapshotId1 | String | 是 | 第一个快照ID | 1 | - |
| snapshotId2 | String | 是 | 第二个快照ID | 2 | - |

**参数说明**:
- `snapshotId1`: 第一个快照ID，用于比较
- `snapshotId2`: 第二个快照ID，用于比较

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "snapshotId1": 1,
    "snapshotNumber1": "SNAPSHOT001",
    "snapshotId2": 2,
    "snapshotNumber2": "SNAPSHOT002",
    "differences": {
      "added": [
        {
          "key": "config3",
          "value": "value3"
        }
      ],
      "modified": [
        {
          "key": "config1",
          "oldValue": "value1",
          "newValue": "value1_updated"
        }
      ],
      "deleted": [
        {
          "key": "config2",
          "value": "value2"
        }
      ]
    },
    "summary": {
      "totalChanges": 3,
      "addedCount": 1,
      "modifiedCount": 1,
      "deletedCount": 1
    }
  }
}
```

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| code | Integer | 响应码 | 200 |
| msg | String | 响应消息 | 查询成功 |
| data | Object | 差异信息 | - |

**data 对象字段说明**:

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| snapshotId1 | String | 第一个快照ID | 1 |
| snapshotNumber1 | String | 第一个快照号 | SNAPSHOT001 |
| snapshotId2 | String | 第二个快照ID | 2 |
| snapshotNumber2 | String | 第二个快照号 | SNAPSHOT002 |
| differences | Object | 差异详情 | - |
| summary | Object | 差异汇总 | - |

**differences 对象字段说明**:

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| added | Array | 新增的配置项 | [{"key":"config3","value":"value3"}] |
| modified | Array | 修改的配置项 | [{"key":"config1","oldValue":"value1","newValue":"value1_updated"}] |
| deleted | Array | 删除的配置项 | [{"key":"config2","value":"value2"}] |

**summary 对象字段说明**:

| 字段名 | 类型 | 描述 | 示例 |
|--------|------|------|------|
| totalChanges | Integer | 总变更数 | 3 |
| addedCount | Integer | 新增数量 | 1 |
| modifiedCount | Integer | 修改数量 | 1 |
| deletedCount | Integer | 删除数量 | 1 |

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
curl -X GET "http://localhost:8080/setting/snapshot/1/compare/2" \
  -H "Authorization: Bearer [token]"
```

### 响应示例

**成功**:

```json
{
  "code": 200,
  "msg": "查询成功",
  "data": {
    "snapshotId1": 1,
    "snapshotNumber1": "SNAPSHOT001",
    "snapshotId2": 2,
    "snapshotNumber2": "SNAPSHOT002",
    "differences": {
      "added": [
        {
          "key": "config3",
          "value": "value3"
        }
      ],
      "modified": [
        {
          "key": "config1",
          "oldValue": "value1",
          "newValue": "value1_updated"
        }
      ],
      "deleted": [
        {
          "key": "config2",
          "value": "value2"
        }
      ]
    },
    "summary": {
      "totalChanges": 3,
      "addedCount": 1,
      "modifiedCount": 1,
      "deletedCount": 1
    }
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
4. **快照内容无效**: 检查快照内容是否有效，如无效则返回错误
5. **系统错误**: 记录错误日志并返回通用错误信息

## 注意事项

1. 接口需要登录认证，需要携带有效的 token
2. 接口需要 setting:snapshot:query 权限
3. 快照ID1和快照ID2为必填项，通过路径参数传递
4. 比较结果包含详细的差异信息，包括新增、修改、删除的配置项
5. 比较结果包含汇总信息，便于快速了解变更情况
6. 如果快照不存在，则返回404错误
7. 如果快照内容无效，则返回错误
8. 比较是基于快照内容进行的，不是基于快照基本信息
9. 建议在比较前先查看快照的详细信息
10. 比较结果可能较大，建议在客户端进行分页或懒加载
11. 可以比较不同ORG类型的快照，但需要注意差异可能较大
12. 比较结果可以用于配置变更审计和回滚决策

## 相关接口

- [查询配置快照列表](0001-snapshot-list.md) - 查询配置快照列表
- [获取配置快照详细信息](0003-get-info.md) - 获取单个快照的详细信息
- [获取快照详细信息（包含配置内容）](0009-get-snapshot-detail.md) - 获取快照详细信息（包含配置内容）
- [从当前配置生成快照](0007-create-snapshot.md) - 从当前配置生成快照
- [恢复快照](0008-restore-snapshot.md) - 恢复快照

## 实现细节

1. 接收两个快照ID参数
2. 调用 IDataiConfigSnapshotService 比较快照差异
3. 返回差异信息

## 测试信息

### 测试环境

- **环境**: 本地开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 比较两个存在的快照 | snapshotId1=1, snapshotId2=2 | 返回差异信息 | 返回差异信息 | 通过 |
| 比较不存在的快照 | snapshotId1=999, snapshotId2=2 | 返回404错误 | 返回404错误 | 通过 |
| 比较相同快照 | snapshotId1=1, snapshotId2=1 | 返回无差异 | 返回无差异 | 通过 |
| 比较源ORG快照 | snapshotId1=1（source）, snapshotId2=3（source） | 返回差异信息 | 返回差异信息 | 通过 |
| 比较不同ORG快照 | snapshotId1=1（source）, snapshotId2=2（target） | 返回差异信息 | 返回差异信息 | 通过 |
| 缺少必填参数 | 缺少snapshotId1 | 返回参数错误 | 返回参数错误 | 通过 |
| 无权限访问 | 无权限token | 返回401错误 | 返回401错误 | 通过 |
