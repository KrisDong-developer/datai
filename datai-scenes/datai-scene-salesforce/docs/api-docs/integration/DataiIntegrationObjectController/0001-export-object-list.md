# 接口文档

## 接口信息

- **接口名称**: 导出对象同步控制列表
- **接口路径**: /integration/object/export
- **请求方法**: POST
- **模块归属**: 对象同步控制
- **版本号**: v1.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

导出对象同步控制列表数据为Excel文件，支持根据查询条件筛选导出数据。

## 请求参数

### 路径参数

无

### 查询参数

无

### 请求体 (FormData)

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| dataiIntegrationObjectDto | Object | 否 | 对象同步控制查询条件 | {} |

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

**响应类型**: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet

**响应内容**: Excel文件二进制数据

### 失败响应

**HTTP 状态码**: 500 Internal Server Error

```json
{
  "code": 500,
  "message": "导出失败",
  "data": null
}
```

## 接口示例

### 请求示例

```bash
curl -X POST "http://localhost:8080/integration/object/export" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer [token]" \
  -d '{
    "objectName": "Account",
    "status": "1"
  }'
```

### 响应示例

**成功**:

Excel文件下载

**失败**:

```json
{
  "code": 500,
  "message": "导出失败: 系统异常",
  "data": null
}
```

## 错误处理

- 导出过程中发生异常时，返回500错误
- 参数错误时，返回400错误

## 注意事项

- 导出操作可能耗时较长，建议在后台执行
- 导出的数据量较大时，可能会占用较多服务器资源

## 相关接口

- [查询对象同步控制列表](http://localhost:8080/integration/object/list) - 查询对象同步控制列表

## 实现细节

- 使用ExcelUtil工具类生成Excel文件
- 支持根据查询条件筛选导出数据
- 导出文件名为"对象同步控制数据.xlsx"

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 导出全部数据 | {} | 成功导出Excel文件 | 成功导出Excel文件 | 通过 |
| 导出指定对象数据 | {"objectName": "Account"} | 成功导出Account对象数据 | 成功导出Account对象数据 | 通过 |
| 导出条件不存在数据 | {"objectName": "NonExist"} | 导出空Excel文件 | 导出空Excel文件 | 通过 |