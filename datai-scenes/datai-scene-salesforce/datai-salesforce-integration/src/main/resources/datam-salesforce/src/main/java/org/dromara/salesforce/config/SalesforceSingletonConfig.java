package org.dromara.salesforce.config;

import lombok.extern.slf4j.Slf4j;
import org.dromara.salesforce.service.IMigrationConfigService;
import org.dromara.salesforce.domain.vo.MigrationConfigVo;
import org.dromara.salesforce.domain.bo.MigrationConfigBo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Salesforce单例配置类
 * <p>
 * 用于存储和管理Salesforce相关配置参数的单例对象，在项目初始化时加载配置，
 * 并提供手动刷新机制
 * </p>
 *
 * @author lingma
 * @since 1.0.0
 */
@Slf4j
@Component
public class SalesforceSingletonConfig {

    // 使用静态变量持有单例实例
    private static volatile SalesforceSingletonConfig instance;

    // 存储配置的线程安全Map
    private final Map<String, String> configMap = new ConcurrentHashMap<>();

    @Autowired
    private IMigrationConfigService migrationConfigService;

    /**
     * 私有构造函数，防止外部实例化
     */
    private SalesforceSingletonConfig() {
    }

    /**
     * 获取单例实例
     *
     * @return SalesforceSingletonConfig实例
     */
    public static SalesforceSingletonConfig getInstance() {
        if (instance == null) {
            synchronized (SalesforceSingletonConfig.class) {
                if (instance == null) {
                    instance = new SalesforceSingletonConfig();
                }
            }
        }
        return instance;
    }

    /**
     * 初始化配置 - 在Spring容器初始化时调用
     */
    @PostConstruct
    public void init() {
        instance = this;
        refreshConfig();
        log.info("Salesforce单例配置初始化完成");
    }

    /**
     * 刷新配置 - 从数据库重新加载所有配置
     */
    public void refreshConfig() {
        try {
            // 清空现有配置
            configMap.clear();

            // 从数据库加载所有配置
            MigrationConfigBo bo = new MigrationConfigBo();
            List<MigrationConfigVo> configList = migrationConfigService.queryList(bo);

            // 将配置加载到内存中
            for (MigrationConfigVo config : configList) {
                configMap.put(config.getConfigKey(), config.getConfigValue());
            }

            log.info("Salesforce配置刷新完成，共加载 {} 个配置项", configMap.size());
        } catch (Exception e) {
            log.error("刷新Salesforce配置时发生错误", e);
        }
    }

    /**
     * 获取配置值
     *
     * @param key 配置键
     * @return 配置值，如果不存在返回null
     */
    public String getConfigValue(String key) {
        return configMap.get(key);
    }

    /**
     * 获取配置值，如果不存在则返回默认值
     *
     * @param key          配置键
     * @param defaultValue 默认值
     * @return 配置值或默认值
     */
    public String getConfigValue(String key, String defaultValue) {
        return configMap.getOrDefault(key, defaultValue);
    }

    /**
     * 设置配置值
     *
     * @param key   配置键
     * @param value 配置值
     */
    public void setConfigValue(String key, String value) {
        configMap.put(key, value);
    }

    /**
     * 获取整型配置值
     *
     * @param key 配置键
     * @return 配置值，如果不存在或转换失败返回0
     */
    public int getIntConfigValue(String key) {
        return getIntConfigValue(key, 0);
    }

    /**
     * 获取整型配置值
     *
     * @param key          配置键
     * @param defaultValue 默认值
     * @return 配置值，如果不存在或转换失败返回默认值
     */
    public int getIntConfigValue(String key, int defaultValue) {
        String value = configMap.get(key);
        if (value == null) {
            return defaultValue;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * 获取布尔型配置值
     *
     * @param key 配置键
     * @return 配置值，如果不存在或转换失败返回false
     */
    public boolean getBooleanConfigValue(String key) {
        return getBooleanConfigValue(key, false);
    }

    /**
     * 获取布尔型配置值
     *
     * @param key          配置键
     * @param defaultValue 默认值
     * @return 配置值，如果不存在或转换失败返回默认值
     */
    public boolean getBooleanConfigValue(String key, boolean defaultValue) {
        String value = configMap.get(key);
        if (value == null) {
            return defaultValue;
        }
        return Boolean.parseBoolean(value);
    }

    /**
     * 获取所有配置项
     *
     * @return 包含所有配置项的Map
     */
    public Map<String, String> getAllConfig() {
        return new ConcurrentHashMap<>(configMap);
    }

    // ==================== CSV相关配置 ====================

    public String getLoaderCsvComma() {
        return getConfigValue("LOADER_CSVCOMMA", "true");
    }

    public String getLoaderCsvTab() {
        return getConfigValue("LOADER_CSVTAB", "false");
    }

    public String getLoaderCsvOther() {
        return getConfigValue("LOADER_CSVOTHER", "false");
    }

    public String getLoaderCsvOtherValue() {
        return getConfigValue("LOADER_CSVOTHERVALUE", "");
    }

    public String getLoaderQueryDelimiter() {
        return getConfigValue("LOADER_QUERY_DELIMITER", ",");
    }

    // ==================== Salesforce认证配置 ====================

    public String getSfdcUsername() {
        return getConfigValue("SFDC_USERNAME");
    }

    public String getSfdcPassword() {
        return getConfigValue("SOURCE_PASSWORD");
    }

    public String getSfdcEndpointProduction() {
        return getConfigValue("SOURCE_ENDPOINT_PRODUCTION", "https://login.salesforce.com/");
    }

    public String getSourceEndpointSandbox() {
        return getConfigValue("SOURCE_ENDPOINT_SANDBOX", "https://test.salesforce.com/");
    }

    public String getSourceProxyHost() {
        return getConfigValue("SOURCE_SFDC_PROXYHOST");
    }

    public String getSourceProxyPort() {
        return getConfigValue("SOURCE_SFDC_PROXYPORT");
    }

    public String getSourceProxyUsername() {
        return getConfigValue("SOURCE_SFDC_PROXYUSERNAME");
    }

    public String getSourceProxyPassword() {
        return getConfigValue("SOURCE_SFDC_PROXYPASSWORD");
    }

    public String getSourceProxyNtlmDomain() {
        return getConfigValue("SOURCE_SFDC_PROXYNTLMDOMAIN");
    }

    public String getSourceOrgStartDate() {
        return getConfigValue("SOURCE_ORG_START_DATE");
    }

    // ==================== Target SFDC配置 ====================

    public String getTargetSfdcUsername() {
        return getConfigValue("TARGET_SFDC_USERNAME");
    }

    public String getTargetSfdcPassword() {
        return getConfigValue("TARGET_SFDC_PASSWORD");
    }

    public String getTargetSfdcEndpointProduction() {
        return getConfigValue("TARGET_SFDC_ENDPOINT_PRODUCTION", "https://login.salesforce.com/");
    }

    public String getTargetSfdcEndpointSandbox() {
        return getConfigValue("TARGET_SFDC_ENDPOINT_SANDBOX", "https://test.salesforce.com/");
    }

    public String getTargetSfdcProxyHost() {
        return getConfigValue("TARGET_SFDC_PROXYHOST");
    }

    public String getTargetSfdcProxyPort() {
        return getConfigValue("TARGET_SFDC_PROXYPORT");
    }

    public String getTargetSfdcProxyUsername() {
        return getConfigValue("TARGET_SFDC_PROXYUSERNAME");
    }

    public String getTargetSfdcProxyPassword() {
        return getConfigValue("TARGET_SFDC_PROXYPASSWORD");
    }

    public String getTargetSfdcProxyNtlmDomain() {
        return getConfigValue("TARGET_SFDC_PROXYNTLMDOMAIN");
    }

    public String getTargetOrgStartDate() {
        return getConfigValue("TARGET_ORG_START_DATE");
    }

    // ==================== Bulk API配置 ====================

    public boolean isSfdcUseBulkApi() {
        return getBooleanConfigValue("SFDC_USEBULKAPI", false);
    }

    public boolean isSfdcUseBulkV2Api() {
        return getBooleanConfigValue("SFDC_USEBULKV2API", false);
    }

    public boolean isSfdcBulkApiSerialMode() {
        return getBooleanConfigValue("SFDC_BULKAPISERIALMODE", false);
    }

    public boolean isSfdcBulkApiZipContent() {
        return getBooleanConfigValue("SFDC_BULKAPIZIPCONTENT", false);
    }

    public boolean isSfdcUpdateWithExternalId() {
        return getBooleanConfigValue("SFDC_UPDATEWITHEXTERNALID", false);
    }

    public boolean isSfdcDeleteWithExternalId() {
        return getBooleanConfigValue("SFDC_DELETEWITHEXTERNALID", false);
    }

    // ==================== 数据操作配置 ====================

    public int getSfdcTimeoutSecs() {
        return getIntConfigValue("SFDC_TIMEOUTSECS", 540);
    }

    public int getSfdcConnectionTimeoutSecs() {
        return getIntConfigValue("SFDC_CONNECTIONTIMEOUTSECS", 60);
    }

    public boolean isSfdcEnableRetries() {
        return getBooleanConfigValue("SFDC_ENABLERETRIES", true);
    }

    public int getSfdcMaxRetries() {
        return getIntConfigValue("SFDC_MAXRETRIES", 3);
    }

    public int getSfdcMinRetrySleepSecs() {
        return getIntConfigValue("SFDC_MINRETRY_SLEEPSECS", 2);
    }

    public boolean isSfdcTruncateFields() {
        return getBooleanConfigValue("SFDC_TRUNCATEFIELDS", true);
    }

    public int getSfdcLoadBatchSize() {
        return getIntConfigValue("SFDC_LOADBATCHSIZE", 200);
    }

    // ==================== 数据访问配置 ====================

    public int getDataAccessReadBatchSize() {
        return getIntConfigValue("DATAACCESS_READBATCHSIZE", 200);
    }

    public int getDataAccessWriteBatchSize() {
        return getIntConfigValue("DATAACCESS_WRITEBATCHSIZE", 500);
    }

    // ==================== 处理配置 ====================

    public String getProcessOperation() {
        return getConfigValue("PROCESS_OPERATION", "insert");
    }

    public boolean isProcessEnableLastRunOutput() {
        return getBooleanConfigValue("PROCESS_ENABLELASTRUNOUTPUT", true);
    }

    public int getProcessLoadRowToStartAt() {
        return getIntConfigValue("PROCESS_LOADROWTOSTARTAT", 0);
    }


    public String getBatchType() {
        return getConfigValue("BATCH_DATE_TYPE", "YEAR");
    }

    public String getProcessMappingFile() {
        return getConfigValue("PROCESS_MAPPINGFILE");
    }

    public String getProcessStatusOutputDirectory() {
        return getConfigValue("PROCESS_STATUSOUTPUTDIRECTORY");
    }

    public boolean isProcessOutputSuccess() {
        return getBooleanConfigValue("PROCESS_OUTPUTSUCCESS", false);
    }

    public boolean isProcessOutputError() {
        return getBooleanConfigValue("PROCESS_OUTPUTERROR", false);
    }

    public String getProcessEncryptionKeyFile() {
        return getConfigValue("PROCESS_ENCRYPTIONKEYFILE");
    }

    public String getProcessLastRunOutputDirectory() {
        return getConfigValue("PROCESS_LASTRUNOUTPUTDIRECTORY");
    }

    public String getSalesforceApiVersion() {
        return getConfigValue("SALESFORCE_API_VERSION", "59.0");
    }

    public String getSfdcTimezone() {
        return getConfigValue("SFDC_TIMEZONE");
    }

    public boolean isSfdcDebugMessages() {
        return getBooleanConfigValue("SFDC_DEBUGMESSAGES", false);
    }

    public String getSfdcDebugMessagesFile() {
        return getConfigValue("SFDC_DEBUGMESSAGESFILE");
    }

    public boolean isProcessUseEuropeAndDates() {
        return getBooleanConfigValue("PROCESS_USEEUROPEANDATES", false);
    }

}
