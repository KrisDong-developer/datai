package com.datai.salesforce.common.constant;

import java.util.Arrays;
import java.util.List;

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

    public static final String CURRENT_SOURCE_ORG_RESULT = "current_source_org_result";

    public static final String CURRENT_TARGET_ORG_RESULT = "current_target_org_result";

    /**
     * 系统数据开始时间的默认年份
     */
    public static final int DEFAULT_SYSTEM_DATA_START_YEAR = 2020;

    /**
     * 批次类型 - 年
     */
    public static final String BATCH_TYPE_YEAR = "YEAR";

    /**
     * 批次类型 - 月
     */
    public static final String BATCH_TYPE_MONTH = "MONTH";

    /**
     * 批次类型 - 周
     */
    public static final String BATCH_TYPE_WEEK = "WEEK";

    /**
     * 批次数据量阈值（50万）
     * 当批次数据量超过此阈值时，需要细化批次粒度
     */
    public static final int BATCH_DATA_THRESHOLD = 500000;

    /**
     * 批次递归最大深度
     */
    public static final int BATCH_MAX_DEPTH = 10;

    /**
     * 表索引字段
     * 只有在列表中的字段才需要创建索引
     */
    public static final List<String> TABLE_INDEX = Arrays.asList(
        "CreatedDate",
        "LastModifiedDate",
        "IsDeleted",
        "new_id"
    );

}