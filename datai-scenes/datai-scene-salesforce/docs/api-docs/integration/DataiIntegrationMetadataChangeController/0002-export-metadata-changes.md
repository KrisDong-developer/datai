# 接口文档：导出对象元数据变更列表

## 接口信息

- **接口名称**: 导出对象元数据变更列表
- **接口路径**: /integration/change/export
- **请求方法**: POST
- **模块归属**: integration
- **版本号**: v1.0
- **创建日期**: 2026-01-09
- **最后更新**: 2026-01-09

## 功能描述

导出对象元数据变更列表到Excel文件，支持根据筛选条件导出指定的元数据变更记录。

## 请求参数

### 请求体 (Form Data)

| 参数名 | 类型 | 必填 | 描述 | 示例 |
|--------|------|------|------|------|
| changeType | String | 否 | 变更类型 | OBJECT |
| operationType | String | 否 | 操作类型 | CREATE |
| objectApi | String | 否 | 对象API名称 | Account |
| isCustom | Boolean | 否 | 是否自定义 | true |
| syncStatus | Boolean | 否 | 同步状态 | false |
| startTime | String | 否 | 开始时间，格式：yyyy-MM-dd HH:mm:ss | 2026-01-01 00:00:00 |
| endTime | String | 否 | 结束时间，格式：yyyy-MM-dd HH:mm:ss | 2026-01-07 23:59:59 |

## 响应数据

### 成功响应

**HTTP 状态码**: 200 OK

**响应类型**: application/vnd.openxmlformats-officedocument.spreadsheetml.sheet

**响应内容**: Excel文件二进制数据

### 失败响应

**HTTP 状态码**: 403 Forbidden

```json
{
  "code": 403,
  "message": "无权限执行此操作",
  "data": null
}
```

| 错误码 | 错误信息 | 描述 |
|--------|----------|------|
| 403 | 无权限执行此操作 | 当用户没有integration:change:export权限时 |
| 500 | 导出失败 | 服务器内部错误导致导出失败 |

## 接口示例

### 请求示例

```bash
curl -X POST "http://localhost:8080/integration/change/export" \
  -H "Content-Type: application/x-www-form-urlencoded" \
  -H "Authorization: Bearer [token]" \
  -d "changeType=OBJECT&syncStatus=false&startTime=2026-01-01%2000:00:00&endTime=2026-01-07%2023:59:59"
```

### 响应示例

**成功**:

返回Excel文件下载

**失败**:

```json
{
  "code": 403,
  "message": "无权限执行此操作",
  "data": null
}
```

## 错误处理

- **403 Forbidden**: 用户无权限执行此操作
- **500 Internal Server Error**: 服务器内部错误导致导出失败

## 注意事项

- 导出操作可能耗时较长，取决于数据量大小
- 导出文件包含元数据变更的详细信息，包括变更类型、操作类型、对象API名称、同步状态等
- 建议在非高峰期执行大量数据的导出操作
- 导出文件的文件名格式为：对象元数据变更数据_yyyyMMddHHmmss.xlsx

## 相关接口

- [查询对象元数据变更列表](http://localhost:8080/integration/change/list) - 查询所有元数据变更列表
- [查询未同步的元数据变更列表](http://localhost:8080/integration/change/unsynced) - 查询未同步的元数据变更列表

## 实现细节

- 接口通过调用`dataiIntegrationMetadataChangeService.selectDataiIntegrationMetadataChangeList()`方法获取数据
- 使用`ExcelUtil`工具类将数据转换为Excel文件
- 通过`HttpServletResponse`直接输出Excel文件到客户端
- 导出操作会被记录到系统日志中

## 测试信息

### 测试环境

- **环境**: 开发环境
- **版本**: v1.0

### 测试用例

| 测试场景 | 输入参数 | 预期结果 | 实际结果 | 状态 |
|----------|----------|----------|----------|------|
| 导出全部元数据变更 | 无参数 | 操作成功，下载Excel文件 | 操作成功，下载Excel文件 | 通过 |
| 导出筛选元数据变更 | changeType=OBJECT&syncStatus=false | 操作成功，下载包含筛选结果的Excel文件 | 操作成功，下载包含筛选结果的Excel文件 | 通过 |
| 无权限导出 | 任意参数 | 操作失败，返回403错误 | 操作失败，返回403错误 | 通过 |