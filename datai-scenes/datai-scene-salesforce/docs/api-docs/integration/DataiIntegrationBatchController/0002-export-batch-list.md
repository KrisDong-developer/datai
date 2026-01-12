# 接口文档：导出数据批次列表

## 接口信息

- **接口名称**: 导出数据批次列表
- **接口路径**: /integration/batch/export
- **请求方法**: POST
- **模块归属**: 数据批次管理
- **版本号**: v1.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

导出数据批次列表为Excel文件，支持条件筛选，方便用户离线查看和分析批次数据。

## 请求参数

### 请求体 (JSON)

```json
{
  "api": "Account",
  "label": "账户",
  "syncType": "FULL",
  "syncStatus": true
}
```

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| api | String | 否 | 对象API名称 | Account |
| label | String | 否 | 对象标签 | 账户 |
| syncType | String | 否 | 同步类型 | FULL |
| syncStatus | Boolean | 否 | 同步状态 | true |
| createBy | String | 否 | 创建人 | admin |
| createTime | Date | 否 | 创建时间 | 2025-12-24 |
| updateBy | String | 否 | 更新人 | admin |
| updateTime | Date | 否 | 更新时间 | 2025-12-24 |

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

**响应类型**: application/vnd.ms-excel

**响应内容**: Excel文件，包含数据批次列表

### 失败响应

**HTTP 状态码**: 400/401/500

```json
{
  "code": 401,
  "message": "未授权",
  "data": null
}
```

## 接口示例

### 请求示例

```bash
curl -X POST "http://localhost:8080/integration/batch/export" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer [token]" \
  -d '{
    "api": "Account",
    "syncStatus": true
  }'
```

### 响应示例

**成功**:

Excel文件下载，文件名为"数据批次数据.xlsx"

**失败**:

```json
{
  "code": 401,
  "message": "未授权",
  "data": null
}
```

## 错误处理

- **401 未授权**: 用户没有访问权限
- **500 服务器错误**: 服务器内部错误，可能是数据库连接失败或Excel生成失败等

## 注意事项

- 接口需要 `integration:batch:export` 权限
- 导出文件可能较大，请确保网络连接稳定
- 支持与查询接口相同的筛选条件

## 相关接口

- [查询数据批次列表](http://localhost:8080/integration/batch/list) - 查询数据批次列表
- [获取数据批次详细信息](http://localhost:8080/integration/batch/{id}) - 获取单个数据批次的详细信息

## 实现细节

- 接口通过调用 `dataiIntegrationBatchService.selectDataiIntegrationBatchList()` 方法获取数据
- 使用 `ExcelUtil` 工具类生成Excel文件
- 支持多条件组合筛选

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 无参数导出 | 无 | 操作成功，导出所有批次列表 | 操作成功，导出所有批次列表 | 通过 |
| 按API名称导出 | api=Account | 操作成功，导出Account对象的批次列表 | 操作成功，导出Account对象的批次列表 | 通过 |
| 按同步状态导出 | syncStatus=true | 操作成功，导出同步成功的批次列表 | 操作成功，导出同步成功的批次列表 | 通过 |