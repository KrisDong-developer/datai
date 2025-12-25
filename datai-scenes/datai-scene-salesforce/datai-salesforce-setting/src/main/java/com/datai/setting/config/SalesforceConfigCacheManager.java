package com.datai.setting.config;

import com.datai.common.utils.CacheUtils;
import com.datai.salesforce.common.constant.SalesforceConfigConstants;
import com.datai.setting.domain.DataiConfiguration;
import com.datai.setting.domain.DataiConfigEnvironment;
import com.datai.setting.mapper.DataiConfigurationMapper;
import com.datai.setting.mapper.DataiConfigEnvironmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
    
    @Autowired
    private DataiConfigEnvironmentMapper environmentMapper;
    
    /**
     * 当前环境编码，从配置文件或系统属性中获取
     */
    @Value("${spring.profiles.active:dev}")
    private String currentEnvironmentCode;
    
    /**
     * 当前环境ID，初始化时从数据库中获取
     */
    private Long currentEnvironmentId;

    /**
     * 系统启动时初始化配置缓存
     */
    @PostConstruct
    public void init() {
        long startTime = System.currentTimeMillis();
        logger.info("开始初始化Salesforce配置...");
        try {
            // 1. 初始化当前环境信息
            initCurrentEnvironment();
            // 2. 加载配置到缓存
            loadConfigToCache();
            long endTime = System.currentTimeMillis();
            logger.info("Salesforce配置初始化完成，耗时: {}ms，当前环境: {}(ID: {})",
                (endTime - startTime), currentEnvironmentCode, currentEnvironmentId);
        } catch (Exception e) {
            logger.error("Salesforce配置初始化失败: {}", e.getMessage(), e);
            throw new RuntimeException("Salesforce配置初始化失败", e);
        }
    }

    /**
     * 初始化当前环境信息
     */
    private void initCurrentEnvironment() {
        logger.info("开始初始化当前环境信息，环境编码: {}", currentEnvironmentCode);
        
        // 查询当前环境信息
        DataiConfigEnvironment environment = new DataiConfigEnvironment();
        environment.setEnvironmentCode(currentEnvironmentCode);
        environment.setIsActive(true);
        
        List<DataiConfigEnvironment> environments = environmentMapper.selectDataiConfigEnvironmentList(environment);
        
        if (environments == null || environments.isEmpty()) {
            logger.warn("未找到环境编码为 {} 的激活环境，使用默认环境ID: 1", currentEnvironmentCode);
            // 默认环境ID为1，确保系统能够正常启动
            currentEnvironmentId = 1L;
        } else {
            DataiConfigEnvironment currentEnv = environments.get(0);
            currentEnvironmentId = currentEnv.getId();
            logger.info("成功获取当前环境信息: ID={}, 名称={}, 编码={}", 
                currentEnv.getId(), currentEnv.getEnvironmentName(), currentEnv.getEnvironmentCode());
        }
    }

    /**
     * 将配置加载到缓存
     */
    public void loadConfigToCache() {
        // 清空当前环境的缓存
        CacheUtils.getCache(getEnvironmentCacheKey()).clear();
        
        logger.info("开始加载配置，当前环境: {}(ID: {})", currentEnvironmentCode, currentEnvironmentId);

        // 查询当前环境的所有激活配置
        DataiConfiguration query = new DataiConfiguration();
        query.setIsActive(true); // 只加载激活状态的配置
        query.setEnvironmentId(currentEnvironmentId); // 按当前环境过滤
        List<DataiConfiguration> configs = configurationMapper.selectDataiConfigurationList(query);

        logger.info("加载配置数量: {}", configs.size());

        // 将配置存入缓存
        for (DataiConfiguration config : configs) {
            CacheUtils.getCache(getEnvironmentCacheKey()).put(config.getConfigKey(), config.getConfigValue());
            logger.debug("配置加载到缓存: {} = {}", config.getConfigKey(), config.getConfigValue());
        }
        
        // 如果当前环境没有配置，加载默认环境(environmentId为null)的配置作为 fallback
        if (configs.isEmpty()) {
            logger.warn("当前环境 {} 没有配置，尝试加载默认环境配置", currentEnvironmentCode);
            DataiConfiguration defaultQuery = new DataiConfiguration();
            defaultQuery.setIsActive(true);
            defaultQuery.setEnvironmentId(null); // 默认环境配置
            List<DataiConfiguration> defaultConfigs = configurationMapper.selectDataiConfigurationList(defaultQuery);
            
            logger.info("加载默认环境配置数量: {}", defaultConfigs.size());
            
            for (DataiConfiguration config : defaultConfigs) {
                CacheUtils.getCache(getEnvironmentCacheKey()).put(config.getConfigKey(), config.getConfigValue());
                logger.debug("默认配置加载到缓存: {} = {}", config.getConfigKey(), config.getConfigValue());
            }
        }
    }
    
    /**
     * 获取带环境标识的缓存键
     * @return 带环境标识的缓存键
     */
    private String getEnvironmentCacheKey() {
        return SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY + ":" + currentEnvironmentCode;
    }

    /**
     * 重置配置缓存
     */
    public void resetConfigCache() {
        logger.info("重置Salesforce配置缓存，当前环境: {}", currentEnvironmentCode);
        loadConfigToCache();
    }

    /**
     * 清空配置缓存
     */
    public void clearConfigCache() {
        logger.info("清空Salesforce配置缓存，当前环境: {}", currentEnvironmentCode);
        CacheUtils.getCache(getEnvironmentCacheKey()).clear();
    }
    
    /**
     * 获取配置值
     * 
     * @param configKey 配置键
     * @return 配置值，如果不存在则返回null
     */
    public String getConfigValue(String configKey) {
        Object value = CacheUtils.getCache(getEnvironmentCacheKey()).get(configKey);
        return value != null ? value.toString() : null;
    }
    
    /**
     * 获取当前环境编码
     * @return 当前环境编码
     */
    public String getCurrentEnvironmentCode() {
        return currentEnvironmentCode;
    }
    
    /**
     * 获取当前环境ID
     * @return 当前环境ID
     */
    public Long getCurrentEnvironmentId() {
        return currentEnvironmentId;
    }
}