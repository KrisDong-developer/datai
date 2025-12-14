package com.datai.setting.config;

import com.datai.common.utils.CacheUtils;
import com.datai.salesforce.common.constant.SalesforceConfigConstants;
import com.datai.setting.domain.DataiConfiguration;
import com.datai.setting.mapper.DataiConfigurationMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;
import java.util.Map;

/**
 * Salesforce配置缓存管理器
 * 负责配置的加载、缓存和更新
 * 
 * @author datai
 * @date 2025-12-11
 */
@Component
public class SalesforceConfigCacheManager {

    private static final Logger logger = LoggerFactory.getLogger(SalesforceConfigCacheManager.class);

    @Autowired
    private DataiConfigurationMapper configurationMapper;

    /**
     * 系统启动时初始化配置缓存
     */
    @PostConstruct
    public void init() {
        long startTime = System.currentTimeMillis();
        logger.info("开始初始化Salesforce配置...");
        try {
            loadConfigToCache();
            long endTime = System.currentTimeMillis();
            logger.info("Salesforce配置初始化完成，耗时: {}ms", (endTime - startTime));
        } catch (Exception e) {
            logger.error("Salesforce配置初始化失败: {}", e.getMessage(), e);
            throw new RuntimeException("Salesforce配置初始化失败", e);
        }
    }

    /**
     * 将配置加载到缓存
     */
    public void loadConfigToCache() {
        // 清空现有缓存
        CacheUtils.getCache(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY).clear();

        // 查询所有激活的配置
        DataiConfiguration query = new DataiConfiguration();
        query.setIsActive(1L); // 只加载激活状态的配置
        List<DataiConfiguration> configs = configurationMapper.selectDataiConfigurationList(query);

        logger.info("加载配置数量: {}", configs.size());

        // 将配置存入缓存
        for (DataiConfiguration config : configs) {
            CacheUtils.getCache(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY).put(config.getConfigKey(), config.getConfigValue());
            logger.debug("配置加载到缓存: {} = {}", config.getConfigKey(), config.getConfigValue());
        }
    }

    /**
     * 重置配置缓存
     */
    public void resetConfigCache() {
        logger.info("重置Salesforce配置缓存...");
        loadConfigToCache();
    }

    /**
     * 清空配置缓存
     */
    public void clearConfigCache() {
        logger.info("清空Salesforce配置缓存");
        CacheUtils.getCache(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY).clear();
    }
    
    /**
     * 获取配置值
     * 
     * @param configKey 配置键
     * @return 配置值，如果不存在则返回null
     */
    public String getConfigValue(String configKey) {
        Object value = CacheUtils.getCache(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY).get(configKey);
        return value != null ? value.toString() : null;
    }
}