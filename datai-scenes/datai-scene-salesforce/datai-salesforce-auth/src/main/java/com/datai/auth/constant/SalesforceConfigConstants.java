package com.datai.auth.constant;

/**
 * Salesforce配置常量类
 * 定义配置相关的常量
 */
public final class SalesforceConfigConstants {

    /**
     * 私有构造函数，防止实例化
     */
    private SalesforceConfigConstants() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }
    /**
     * Salesforce配置缓存键
     */
    public static final String SALESFORCE_CONFIG_CACHE_KEY = "salesforce_config";

    public static final String CACHE_NAME = "salesforce_auth_cache";

    public static final String CURRENT_RESULT = "current_result";

}