# 接口文档

## 接口信息

- **接口名称**: 导出配置快照列表
- **接口路径**: /setting/snapshot/export
- **请求方法**: POST
- **模块归属**: datai-salesforce-setting
- **版本号**: v1.0
- **创建日期**: 2026-01-14
- **最后更新**: 2026-01-14

## 功能描述

导出配置快照列表到 Excel 文件，支持多条件过滤。导出的文件包含快照ID、快照号、环境ID、快照描述、快照内容、配置数量、快照状态、ORG类型等信息，便于用户离线查看和备份配置快照数据。

## 请求参数

### 请求参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
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
- `snapshotNumber`: 快照号，支持模糊查询
- `environmentId`: 环境ID，精确匹配
- `status`: 快照状态，可选值为 active（激活）、inactive（未激活）等
- `orgType`: ORG类型，可选值为 source（源ORG）或 target（目标ORG）
- `params`: 用于存放查询范围等临时数据

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

**响应类型**: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet

**响应头**:
- Content-Type: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet
- Content-Disposition: attachment; filename=配置快照数据.xlsx

**响应体**: Excel 文件二进制数据

### 失败响应

**HTTP 状态码**: 400/401/403/500

```json
{
  "code": 500,
  "msg": "导出失败",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 400 | 参数错误 | 参数验证失败 |
| 401 | 您没有权限执行此操作 | 权限不足 |
| 403 | 访问被拒绝 | 访问被拒绝 |
| 500 | 导出失败 | 系统错误 |

## 接口示例

### 请求示例

```bash
curl -X POST "http://localhost:8080/setting/snapshot/export" \
  -H "Authorization: Bearer [token]" \
  -H "Content-Type: application/json" \
  -d '{
    "orgType": "source",
    "status": "active"
  }' \
  --output snapshot_export.xlsx
```

### 响应示例

**成功**: 返回 Excel 文件下载

**失败**:

```json
{
  "code": 401,
  "msg": "您没有权限执行此操作",
  "data": null
}
```

## 错误处理

1. **权限验证失败**: 检查用户是否具有 setting:snapshot:export 权限
2. **参数验证失败**: 检查参数格式是否正确
3. **导出失败**: 记录错误日志并返回通用错误信息

## 注意事项

1. 接口需要登录认证，需要携带有效的 token
2. 接口需要 setting:snapshot:export 权限
3. 请求方法为 POST，参数通过请求体传递
4. 支持多条件过滤，可组合使用
5. 导出的文件格式为 Excel (.xlsx)
6. 文件名默认为"配置快照数据.xlsx"
7. 快照内容可能较大，导出时包含完整内容
8. orgType 字段用于区分源ORG和目标ORG的快照
9. 导出操作会记录到操作日志中
10. 建议在导出前先查询确认数据量

## 相关接口

- [查询配置快照列表](0001-snapshot-list.md) - 查询配置快照列表
- [获取配置快照详细信息](0003-get-info.md) - 获取单个快照的详细信息
- [从当前配置生成快照](0008-create-snapshot.md) - 从当前配置生成快照

## 实现细节

1. 接收查询参数，支持多条件过滤
2. 调用 IDataiConfigSnapshotService 查询快照列表
3. 使用 ExcelUtil 将数据导出为 Excel 文件
4. 设置响应头，触发文件下载

## 测试信息

### 测试环境

- **环境**: 本地开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 导出所有快照 | 无参数 | 导出所有快照 | 导出所有快照 | 通过 |
| 按ORG类型导出 | orgType=source | 导出源ORG快照 | 导出源ORG快照 | 通过 |
| 按状态导出 | status=active | 导出激活快照 | 导出激活快照 | 通过 |
| 组合导出 | orgType=source,status=active | 导出符合条件的快照 | 导出符合条件的快照 | 通过 |
| 无权限访问 | 无权限token | 返回401错误 | 返回401错误 | 通过 |
