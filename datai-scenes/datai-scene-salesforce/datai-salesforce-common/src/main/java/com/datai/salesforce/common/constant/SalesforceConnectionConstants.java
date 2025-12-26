package com.datai.salesforce.common.constant;

import java.util.Arrays;
import java.util.List;

/**
 * Salesforce连接相关常量类 - 管理连接管理类相关的常量
 *
 * 该类专门用于管理Salesforce连接管理类相关的常量，包括连接状态、连接类型、
 * 连接管理相关的配置和错误信息等。
 *
 * 设计特点：
 * 1. 专注于连接管理相关的常量
 * 2. 按功能模块分类，结构清晰
 * 3. 使用final修饰，确保常量不可修改
 * 4. 使用static修饰，便于直接访问
 * 5. 提供完整的注释，便于理解和使用
 *
 * @author datai
 * @since 1.0.0
 */
public final class SalesforceConnectionConstants {

    /**
     * 私有构造函数，防止实例化
     */
    private SalesforceConnectionConstants() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    // ================================= 连接状态常量 =================================
    
    /**
     * 连接状态 - 已创建
     */
    public static final String CONNECTION_STATUS_CREATED = "CREATED";
    
    /**
     * 连接状态 - 活跃
     */
    public static final String CONNECTION_STATUS_ACTIVE = "ACTIVE";
    
    /**
     * 连接状态 - 已关闭
     */
    public static final String CONNECTION_STATUS_CLOSED = "CLOSED";
    
    /**
     * 连接状态 - 错误
     */
    public static final String CONNECTION_STATUS_ERROR = "ERROR";

    // ================================= 连接管理相关配置 =================================
    
    /**
     * 默认的连接超时时间（毫秒）
     */
    public static final int DEFAULT_CONNECTION_TIMEOUT = 60000;
    
    /**
     * 默认的读取超时时间（毫秒）
     */
    public static final int DEFAULT_READ_TIMEOUT = 60000;
    
    /**
     * 默认的连接池大小
     */
    public static final int DEFAULT_CONNECTION_POOL_SIZE = 10;
    
    /**
     * 连接最大空闲时间（毫秒）
     */
    public static final long DEFAULT_MAX_IDLE_TIME = 3600000L; // 1小时
    
    /**
     * 连接最大生命周期（毫秒）
     */
    public static final long DEFAULT_MAX_LIFETIME = 7200000L; // 2小时

    // ================================= 连接管理相关错误信息 =================================
    
    /**
     * 连接不存在的错误信息
     */
    public static final String ERROR_CONNECTION_NOT_EXIST = "连接不存在，连接ID: {}";
    
    /**
     * 连接已关闭的错误信息
     */
    public static final String ERROR_CONNECTION_CLOSED = "连接已关闭，连接ID: {}";
    
    /**
     * 连接出错的错误信息
     */
    public static final String ERROR_CONNECTION_ERROR = "连接出错，连接ID: {}, 错误信息: {}";
    
    /**
     * 关闭连接失败的错误信息
     */
    public static final String ERROR_CLOSE_CONNECTION_FAILED = "关闭连接失败，连接ID: {}";
    
    /**
     * 连接器配置不能为空的错误信息
     */
    public static final String ERROR_CONNECTOR_CONFIG_NULL = "ConnectorConfig不能为空";

    // ================================= 连接管理相关日志信息 =================================
    
    /**
     * 连接管理初始化成功的日志信息
     */
    public static final String LOG_CONNECTION_MANAGER_INITIALIZED = "SalesforceConnectionManager初始化成功";
    
    /**
     * 开始创建连接的日志信息
     */
    public static final String LOG_START_CREATE_CONNECTION = "开始创建{}连接，连接ID: {}";
    
    /**
     * 连接创建成功的日志信息
     */
    public static final String LOG_CONNECTION_CREATED = "{}连接创建成功，连接ID: {}";
    
    /**
     * 连接创建失败的日志信息
     */
    public static final String LOG_CONNECTION_CREATE_FAILED = "创建{}连接失败，连接ID: {}";
    
    /**
     * 关闭连接的日志信息
     */
    public static final String LOG_CONNECTION_CLOSED = "连接已关闭，连接ID: {}";
    
    /**
     * 开始关闭所有连接的日志信息
     */
    public static final String LOG_START_CLOSE_ALL_CONNECTIONS = "开始关闭所有连接，当前连接数: {}";
    
    /**
     * 所有连接已关闭的日志信息
     */
    public static final String LOG_ALL_CONNECTIONS_CLOSED = "所有连接已关闭";
    
    /**
     * 连接类型不支持自动关闭的日志信息
     */
    public static final String LOG_CONNECTION_TYPE_NOT_SUPPORT_AUTO_CLOSE = "连接类型{}不支持自动关闭";

    // ================================= 连接ID生成相关常量 =================================
    
    /**
     * 连接ID分隔符
     */
    public static final String CONNECTION_ID_SEPARATOR = "_";
    
    /**
     * 连接ID前缀 - SOAP
     */
    public static final String CONNECTION_ID_PREFIX_SOAP = "SOAP";
    
    /**
     * 连接ID前缀 - REST
     */
    public static final String CONNECTION_ID_PREFIX_REST = "REST";
    
    /**
     * 连接ID前缀 - Bulk V1
     */
    public static final String CONNECTION_ID_PREFIX_BULK_V1 = "BULK_V1";
    
    /**
     * 连接ID前缀 - Bulk V2
     */
    public static final String CONNECTION_ID_PREFIX_BULK_V2 = "BULK_V2";

    // ================================= 连接管理统计相关常量 =================================
    
    /**
     * 连接统计信息的键 - SOAP连接数
     */
    public static final String STATS_KEY_SOAP_CONNECTIONS = "SOAP_CONNECTIONS";
    
    /**
     * 连接统计信息的键 - REST连接数
     */
    public static final String STATS_KEY_REST_CONNECTIONS = "REST_CONNECTIONS";
    
    /**
     * 连接统计信息的键 - Bulk V1连接数
     */
    public static final String STATS_KEY_BULK_V1_CONNECTIONS = "BULK_V1_CONNECTIONS";
    
    /**
     * 连接统计信息的键 - Bulk V2连接数
     */
    public static final String STATS_KEY_BULK_V2_CONNECTIONS = "BULK_V2_CONNECTIONS";
    
    /**
     * 连接统计信息的键 - 活跃连接数
     */
    public static final String STATS_KEY_ACTIVE_CONNECTIONS = "ACTIVE_CONNECTIONS";
    
    /**
     * 连接统计信息的键 - 已关闭连接数
     */
    public static final String STATS_KEY_CLOSED_CONNECTIONS = "CLOSED_CONNECTIONS";
    
    /**
     * 连接统计信息的键 - 错误连接数
     */
    public static final String STATS_KEY_ERROR_CONNECTIONS = "ERROR_CONNECTIONS";

    // ================================= 连接管理默认值 =================================
    
    /**
     * 默认的API版本
     */
    public static final String DEFAULT_API_VERSION = "64.0";
    
    /**
     * 默认的连接超时时间（毫秒）
     */
    public static final int DEFAULT_TIMEOUT = 60000;
    
    /**
     * 默认的读取超时时间（毫秒）
     */
    public static final int DEFAULT_READ_TIMEOUT_MS = 60000;
    
    /**
     * 默认的连接池大小
     */
    public static final int DEFAULT_POOL_SIZE = 10;

    /**
     * 表索引常量
     */
    public static final List<String> TABLE_INDEX = Arrays.asList("Id", "CreatedDate", "LastModifiedDate", "SystemModstamp");

}
