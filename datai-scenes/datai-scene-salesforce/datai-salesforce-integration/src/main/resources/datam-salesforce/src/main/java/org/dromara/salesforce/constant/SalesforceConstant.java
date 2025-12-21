package org.dromara.salesforce.constant;

import com.google.common.collect.Lists;
import org.dromara.common.core.constant.GlobalConstants;

import java.util.List;

public interface SalesforceConstant {

    /**
     * 源ORG session配置KEY
     */
    String SOURCE_ORG_SESSION_KEY = GlobalConstants.GLOBAL_REDIS_KEY + "salesforce:source_org_session";

    /**
     * 目标ORG session配置KEY
     */
    String TARGET_ORG_SESSION_KEY = GlobalConstants.GLOBAL_REDIS_KEY + "salesforce:target_org_session";

    /**
     * 基础客户端名称
     */
    String BASE_CLIENT_NAME = "DataMigration";

    /**
     * Bulk API 客户端类型
     */
    String BULK_API_CLIENT_TYPE = "Bulk";

    /**
     * Bulk V2 API 客户端类型
     */
    String BULK_V2_API_CLIENT_TYPE = "Bulkv2";

    /**
     * Partner API 客户端类型
     */
    String PARTNER_API_CLIENT_TYPE = "Partner";

    /**
     * 批处理客户端标识字符串
     */
    String BATCH_CLIENT_STRING = "Batch";

    /**
     * Salesforce 调用选项请求头
     */
    String SFORCE_CALL_OPTIONS_HEADER = "Sforce-Call-Options";

    /**
     * sf 日期格式
     */
    String SF_DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
    
    /**
     * sf 时间格式
     */
    String SF_TIME_FORMAT = "HH:mm:ss.SSS'Z'";
    
    /**
     * 表索引字段
     */
    List<String> TABLE_INDEX = Lists.newArrayList(
        "CreatedDate",
        "SystemModstamp",
        "lastModifiedDate",
        "IsDeleted"
    );
    
    /**
     * 默认分区4年
     */
    int DEFAULT_PARTITION_YEARS = 4;

    /**
     * 存量类型
     */
    int STOCK_LOAD_TYPE = 1;

    /**
     * 增量类型
     */
    int INCREMENTAL_LOAD_TYPE = 0;

    /**
     * 批次数据量阈值，超过此值需要进一步拆分
     */
    int BATCH_SIZE_THRESHOLD = 500000;

    /**
     * 批次类型常量
     */
    String BATCH_TYPE_YEAR = "YEAR";
    String BATCH_TYPE_MONTH = "MONTH";
    String BATCH_TYPE_WEEK = "WEEK";
    String BATCH_TYPE_DAY = "DAY";

    /**
     * 增量批次类型
     */
    String BATCH_TYPE_INCREMENTAL = "INCREMENTAL";

    /**
     * 存量批次类型
     */
    String BATCH_TYPE_STOCK = "STOCK";

    /**
     * 初始等待时间1秒
     */
    long INITIAL_SLEEP_TIME = 1000L;

    /**
     * 最大等待时间100秒
     */
    long MAX_SLEEP_TIME = 100000L;
}