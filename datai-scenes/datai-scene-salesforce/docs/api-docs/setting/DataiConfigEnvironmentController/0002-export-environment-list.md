# 接口文档

## 接口信息

- **接口名称**: 导出配置环境列表
- **接口路径**: /setting/environment/export
- **请求方法**: POST
- **模块归属**: datai-salesforce-setting
- **版本号**: v1.0
- **创建日期**: 2026-01-14
- **最后更新**: 2026-01-14

## 功能描述

导出配置环境列表到 Excel 文件，支持多条件过滤。导出的文件包含环境ID、环境名称、环境编码、ORG类型、环境描述、是否激活等信息，便于用户离线查看和备份环境配置数据。

## 请求参数

### 请求体参数

| 参数名 | 类型 | 必填 | 描述 | 示例 | 默认值 |
|--------|------|------|------|------|--------|
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

**响应类型**: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet

**响应头**:
```
Content-Disposition: attachment; filename=配置环境数据.xlsx
```

**响应体**: Excel 文件二进制数据

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
curl -X POST "http://localhost:8080/setting/environment/export" \
  -H "Authorization: Bearer [token]" \
  -H "Content-Type: application/json" \
  -d '{
    "orgType": "source",
    "isActive": true
  }' \
  --output environment-list.xlsx
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

## Excel 文件格式

导出的 Excel 文件包含以下列：

| 列名 | 说明 | 示例 |
|------|------|------|
| 环境ID | 环境唯一标识 | 1 |
| 部门ID | 所属部门ID | 1 |
| 环境名称 | 环境显示名称 | 生产环境 |
| 环境编码 | 环境唯一编码 | PROD |
| ORG类型 | ORG类型（source/target） | source |
| 环境描述 | 环境详细描述 | 生产环境配置 |
| 是否激活 | 环境激活状态 | 是 |
| 备注 | 备注信息 | 源ORG生产环境 |
| 创建人 | 创建人用户名 | admin |
| 创建时间 | 创建时间 | 2026-01-01 10:00:00 |
| 更新人 | 更新人用户名 | admin |
| 更新时间 | 更新时间 | 2026-01-14 10:30:00 |

## 错误处理

1. **权限验证失败**: 检查用户是否具有 setting:environment:export 权限
2. **参数验证失败**: 检查查询参数是否有效
3. **系统错误**: 记录错误日志并返回通用错误信息
4. **导出失败**: 检查 Excel 生成过程是否正常

## 注意事项

1. 接口需要登录认证，需要携带有效的 token
2. 接口需要 setting:environment:export 权限
3. 接口使用 POST 方法，参数通过请求体传递
4. 导出操作会记录到操作日志中
5. 导出的 Excel 文件名为"配置环境数据.xlsx"
6. 支持按多个条件过滤导出数据
7. 导出大量数据时可能需要较长时间，请耐心等待
8. 建议在导出前先查询确认要导出的数据范围

## 相关接口

- [查询配置环境列表](0001-environment-list.md) - 查询配置环境列表
- [获取配置环境详细信息](0003-get-info.md) - 获取单个环境的详细信息

## 实现细节

1. 接收查询参数，转换为 DataiConfigEnvironment 对象
2. 调用 IDataiConfigEnvironmentService 查询环境列表
3. 使用 ExcelUtil 工具类导出数据到 Excel 文件
4. 设置响应头，触发浏览器下载

## 测试信息

### 测试环境

- **环境**: 本地开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 导出所有环境 | 无参数 | 导出所有环境数据 | 导出成功 | 通过 |
| 按ORG类型导出 | orgType=source | 导出source类型的环境 | 导出成功 | 通过 |
| 按激活状态导出 | isActive=true | 导出已激活的环境 | 导出成功 | 通过 |
| 按环境编码导出 | environmentCode=PROD | 导出指定编码的环境 | 导出成功 | 通过 |
| 无权限访问 | 无权限token | 返回401错误 | 返回401错误 | 通过 |
| 导出空数据 | 不存在的条件 | 导出空Excel文件 | 导出成功 | 通过 |
