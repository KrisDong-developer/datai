package com.datai.salesforce.common.constant;

/**
 * Salesforce常量类 - 统一管理Salesforce相关常量
 *
 * 该类按照不同类别组织Salesforce相关常量，包括API版本、连接端点、
 * 连接类型、日志消息模板等，便于维护和使用。
 *
 * 设计特点：
 * 1. 按功能模块分类，结构清晰
 * 2. 使用final修饰，确保常量不可修改
 * 3. 使用static修饰，便于直接访问
 * 4. 提供完整的注释，便于理解和使用
 *
 * @author datai
 * @since 1.0.0
 */
public final class SalesforceConstants {

    /**
     * 私有构造函数，防止实例化
     */
    private SalesforceConstants() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    // ================================= API版本常量 =================================
    
    /**
     * REST API版本
     */
    public static final String REST_API_VERSION = "v59.0";
    
    /**
     * Bulk API v1版本
     */
    public static final String BULK_V1_API_VERSION = "47.0";
    
    /**
     * Bulk API v2版本
     */
    public static final String BULK_V2_API_VERSION = "v59.0";

    // ================================= 连接端点常量 =================================
    
    /**
     * REST API端点后缀
     */
    public static final String REST_ENDPOINT_SUFFIX = "/services/data/";
    
    /**
     * Bulk API v1端点后缀
     */
    public static final String BULK_V1_ENDPOINT_SUFFIX = "/services/async/";
    
    /**
     * Bulk API v2端点后缀
     */
    public static final String BULK_V2_ENDPOINT_SUFFIX = "/services/data/" + BULK_V2_API_VERSION + "/jobs/";
    
    /**
     * SOAP API端点后缀
     */
    public static final String SOAP_ENDPOINT_SUFFIX = "/services/Soap/u/" + REST_API_VERSION.replace("v", "") + "/";

    // ================================= 连接类型常量 =================================
    
    /**
     * 连接类型 - SOAP
     */
    public static final String CONNECTION_TYPE_SOAP = "SOAP";
    
    /**
     * 连接类型 - REST
     */
    public static final String CONNECTION_TYPE_REST = "REST";
    
    /**
     * 连接类型 - Bulk API v1
     */
    public static final String CONNECTION_TYPE_BULK_V1 = "BULK_V1";
    
    /**
     * 连接类型 - Bulk API v2
     */
    public static final String CONNECTION_TYPE_BULK_V2 = "BULK_V2";

    // ================================= 连接缓存常量 =================================

    
    /**
     * 配置缓存键
     */
    public static final String SESSION_CONFIG_SOURCE = "SOURCE_SESSION_CONFIG";

    // ================================= 日志消息模板 =================================
    
    /**
     * 创建并缓存源ORG连接的日志模板
     */
    public static final String LOG_CREATE_CACHE_SOURCE_CONNECTION = "创建并缓存{}连接实例";
    
    /**
     * 创建并缓存目标ORG连接的日志模板
     */
    public static final String LOG_CREATE_CACHE_TARGET_CONNECTION = "创建并缓存目标ORG的{}连接实例";
    
    /**
     * 清除源ORG连接的日志模板
     */
    public static final String LOG_CLEAR_SOURCE_CONNECTION = "{}连接已清除";
    
    /**
     * 清除目标ORG连接的日志模板
     */
    public static final String LOG_CLEAR_TARGET_CONNECTION = "目标ORG的{}连接已清除";
    
    /**
     * 未找到源ORG连接的日志模板
     */
    public static final String LOG_NOT_FOUND_SOURCE_CONNECTION = "没有找到{}连接";
    
    /**
     * 未找到目标ORG连接的日志模板
     */
    public static final String LOG_NOT_FOUND_TARGET_CONNECTION = "没有找到目标ORG的{}连接";
    
    /**
     * 创建连接失败的日志模板
     */
    public static final String LOG_CREATE_CONNECTION_FAILED = "创建{}ORG的{}连接失败: {}";

    // ================================= 异常消息模板 =================================
    
    /**
     * 创建连接失败的异常消息模板
     */
    public static final String EXCEPTION_CREATE_CONNECTION_FAILED = "创建{}ORG的{}连接失败: {}";

    // ================================= 连接工厂相关常量 =================================
    
    /**
     * 连接配置已清除的消息
     */
    public static final String CONNECTION_CONFIG_CLEARED = "连接配置已清除";
    
    /**
     * 未找到连接配置的消息
     */
    public static final String CONNECTION_CONFIG_NOT_FOUND = "未找到连接配置";

    // ================================= 其他常量 =================================
    
    /**
     * 序列化版本号
     */
    public static final long SERIAL_VERSION_UID = 1L;
    
    /**
     * 默认的批处理大小
     */
    public static final int DEFAULT_BATCH_SIZE = 10000;
    
    /**
     * 默认的查询超时时间（毫秒）
     */
    public static final long DEFAULT_QUERY_TIMEOUT = 300000L;
    
    /**
     * 默认的连接超时时间（毫秒）
     */
    public static final int DEFAULT_CONNECTION_TIMEOUT = 60000;
    
    /**
     * 默认的读取超时时间（毫秒）
     */
    public static final int DEFAULT_READ_TIMEOUT = 60000;

    // ================================= HTTP相关常量 =================================
    
    /**
     * HTTP GET方法
     */
    public static final String HTTP_METHOD_GET = "GET";
    
    /**
     * HTTP POST方法
     */
    public static final String HTTP_METHOD_POST = "POST";
    
    /**
     * HTTP PUT方法
     */
    public static final String HTTP_METHOD_PUT = "PUT";
    
    /**
     * HTTP PATCH方法
     */
    public static final String HTTP_METHOD_PATCH = "PATCH";
    
    /**
     * HTTP DELETE方法
     */
    public static final String HTTP_METHOD_DELETE = "DELETE";
    
    /**
     * 内容类型 - JSON
     */
    public static final String CONTENT_TYPE_JSON = "application/json";
    
    /**
     * 内容类型 - CSV
     */
    public static final String CONTENT_TYPE_CSV = "text/csv";
    
    /**
     * 内容类型 - XML
     */
    public static final String CONTENT_TYPE_XML = "application/xml";
    
    /**
     * 授权头前缀
     */
    public static final String AUTH_HEADER_PREFIX = "Bearer ";
    
    /**
     * 授权头名称
     */
    public static final String AUTH_HEADER_NAME = "Authorization";
    
    /**
     * 内容类型头名称
     */
    public static final String CONTENT_TYPE_HEADER = "Content-Type";
    
    /**
     * 接受内容类型头名称
     */
    public static final String ACCEPT_HEADER = "Accept";
    
    // ================================= Bulk API 相关常量 =================================
    
    /**
     * Salesforce调用选项头
     */
    public static final String SFORCE_CALL_OPTIONS_HEADER = "Sforce-Call-Options";
    
    /**
     * UTF-8编码
     */
    public static final String UTF_8 = "UTF-8";
    
    /**
     * 授权头值前缀
     */
    public static final String AUTH_HEADER_VALUE_PREFIX = "Bearer ";
    
    /**
     * 请求内容类型头
     */
    public static final String REQUEST_CONTENT_TYPE_HEADER = "Content-Type";
    
    /**
     * 接受内容类型头
     */
    public static final String ACCEPT_CONTENT_TYPES_HEADER = "Accept";
    
    /**
     * 授权头
     */
    public static final String AUTH_HEADER = "Authorization";

    /**
     * Bulk API V2 导入作业的路径前缀
     * 完整路径示例: /services/data/v60.0/jobs/ingest/
     */
    public static final String URI_STEM_INGEST = "/jobs/ingest/";

    /**
     * Bulk API V2 查询作业的路径前缀
     * 完整路径示例: /services/data/v60.0/jobs/query/
     */
    public static final String URI_STEM_QUERY = "/jobs/query/";

    /**
     * 摄取结果 - 成功
     */
    public static final String INGEST_RESULTS_SUCCESSFUL = "successfulResults/";
    
    /**
     * 摄取结果 - 失败
     */
    public static final String INGEST_RESULTS_UNSUCCESSFUL = "failedResults/";
    
    /**
     * 摄取记录 - 未处理
     */
    public static final String INGEST_RECORDS_UNPROCESSED = "unprocessedrecords/";
    
    /**
     * 方法 - GET
     */
    public static final String METHOD_GET = "GET";
    
    /**
     * 方法 - POST
     */
    public static final String METHOD_POST = "POST";
    
    /**
     * 方法 - PATCH
     */
    public static final String METHOD_PATCH = "PATCH";
    
    /**
     * 方法 - PUT
     */
    public static final String METHOD_PUT = "PUT";

    // ================================= 日期格式常量 =================================

    /**
     * Salesforce 日期时间格式
     * 格式示例: 2024-01-01T00:00:00.000Z
     */
    public static final String SF_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";

    /**
     * Salesforce 日期格式
     * 格式示例: 2024-01-01
     */
    public static final String SF_DATE_ONLY_FORMAT = "yyyy-MM-dd";
}
