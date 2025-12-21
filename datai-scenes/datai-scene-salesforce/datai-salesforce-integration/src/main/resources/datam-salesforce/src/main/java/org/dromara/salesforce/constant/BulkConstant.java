package org.dromara.salesforce.constant;

import org.dromara.common.core.constant.GlobalConstants;

import java.nio.charset.StandardCharsets;

public interface BulkConstant {

    /**
     * 默认配置KEY
     */
    public static final String DEFAULT_CONFIG_KEY = GlobalConstants.GLOBAL_REDIS_KEY + "sys_oss:default_config";

    public static final String BULK_API_CLIENT_TYPE = "Bulk";
    public static final String BULK_V2_API_CLIENT_TYPE = "Bulkv2";
    public static final String PARTNER_API_CLIENT_TYPE = "Partner";
    public static final String BATCH_CLIENT_STRING = "Batch";
    public static final String SFORCE_CALL_OPTIONS_HEADER = "Sforce-Call-Options";

    public static final String URI_STEM_QUERY = "query/";
    public static final String URI_STEM_INGEST = "ingest/";
    public static final String AUTH_HEADER = "Authorization";
    public static final String REQUEST_CONTENT_TYPE_HEADER = "Content-Type";
    public static final String ACCEPT_CONTENT_TYPES_HEADER = "ACCEPT";
    public static final String AUTH_HEADER_VALUE_PREFIX = "Bearer ";
    public static final String UTF_8 = StandardCharsets.UTF_8.name();
    public static final String INGEST_RESULTS_SUCCESSFUL = "successfulResults";
    public static final String INGEST_RESULTS_UNSUCCESSFUL = "failedResults";
    public static final String INGEST_RECORDS_UNPROCESSED = "unprocessedrecords";
    public static final String JSON_CONTENT_TYPE = "application/json";
    public static final String CSV_CONTENT_TYPE = "text/csv";

    // HTTP方法名称常量
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    public static final String METHOD_PATCH = "PATCH";
    public static final String METHOD_PUT = "PUT";

}
