package com.datai.setting.config;

import com.datai.common.utils.CacheUtils;
import com.datai.salesforce.common.constant.SalesforceConfigConstants;
import com.datai.setting.model.domain.DataiConfiguration;
import com.datai.setting.model.domain.DataiConfigEnvironment;
import com.datai.setting.mapper.DataiConfigurationMapper;
import com.datai.setting.mapper.DataiConfigEnvironmentMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.List;

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
     * 当前环境编码，初始化时从数据库中获取
     */
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
        logger.info("[配置加载] 开始初始化Salesforce配置，开始时间: {}", startTime);
        try {
            // 1. 初始化当前环境信息
            long envInitStartTime = System.currentTimeMillis();
            initCurrentEnvironment();
            long envInitEndTime = System.currentTimeMillis();
            logger.info("[配置加载] 环境信息初始化完成，耗时: {}ms", (envInitEndTime - envInitStartTime));
            
            // 2. 加载配置到缓存
            long configLoadStartTime = System.currentTimeMillis();
            loadConfigToCache();
            long configLoadEndTime = System.currentTimeMillis();
            logger.info("[配置加载] 配置加载到缓存完成，耗时: {}ms", (configLoadEndTime - configLoadStartTime));
            
            long endTime = System.currentTimeMillis();
            logger.info("[配置加载] Salesforce配置初始化完成，总耗时: {}ms，当前环境: {}(ID: {})",
                (endTime - startTime), currentEnvironmentCode, currentEnvironmentId);
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            logger.error("[配置加载] Salesforce配置初始化失败，开始时间: {}, 耗时: {}ms, 错误信息: {}", 
                startTime, (endTime - startTime), e.getMessage(), e);
            throw new RuntimeException("Salesforce配置初始化失败", e);
        }
    }

    /**
     * 初始化当前环境信息
     */
    private void initCurrentEnvironment() {
        logger.info("[配置加载] 开始初始化当前环境信息，环境编码: {}", currentEnvironmentCode);
        
        // 查询当前环境信息
        DataiConfigEnvironment environment = new DataiConfigEnvironment();
        environment.setEnvironmentCode(currentEnvironmentCode);
        environment.setIsActive(true);
        
        List<DataiConfigEnvironment> environments = environmentMapper.selectDataiConfigEnvironmentList(environment);
        
        if (environments == null || environments.isEmpty()) {
            logger.warn("[配置加载] 未找到环境编码为 {} 的激活环境，使用默认环境ID: 1", currentEnvironmentCode);
            // 默认环境ID为1，确保系统能够正常启动
            currentEnvironmentId = 1L;
            currentEnvironmentCode = "dev";
        } else {
            DataiConfigEnvironment currentEnv = environments.get(0);
            currentEnvironmentId = currentEnv.getId();
            currentEnvironmentCode = currentEnv.getEnvironmentCode();
            logger.info("[配置加载] 成功获取当前环境信息: ID={}, 名称={}, 编码={}", 
                currentEnv.getId(), currentEnv.getEnvironmentName(), currentEnv.getEnvironmentCode());
        }
    }

    /**
     * 将配置加载到缓存
     */
    public void loadConfigToCache() {
        long loadStartTime = System.currentTimeMillis();
        String cacheKey = getEnvironmentCacheKey();
        logger.info("[配置加载] 开始加载配置到缓存，当前环境: {}(ID: {}), 缓存键: {}", 
            currentEnvironmentCode, currentEnvironmentId, cacheKey);

        // 清空当前环境的缓存
        CacheUtils.getCache(cacheKey).clear();
        logger.info("[配置加载] 已清空现有缓存: {}", cacheKey);

        // 查询当前环境的所有激活配置
        long dbQueryStartTime = System.currentTimeMillis();
        DataiConfiguration query = new DataiConfiguration();
        query.setIsActive(true); // 只加载激活状态的配置
        query.setEnvironmentId(currentEnvironmentId); // 按当前环境过滤
        List<DataiConfiguration> configs = configurationMapper.selectDataiConfigurationList(query);
        long dbQueryEndTime = System.currentTimeMillis();
        logger.info("[配置加载] 数据库查询完成，当前环境配置数量: {}, 查询耗时: {}ms", 
            configs.size(), (dbQueryEndTime - dbQueryStartTime));

        int successCount = 0;
        int failCount = 0;
        long cachePutStartTime = System.currentTimeMillis();
        
        // 将配置存入缓存
        for (DataiConfiguration config : configs) {
            try {
                CacheUtils.getCache(cacheKey).put(config.getConfigKey(), config.getConfigValue());
                logger.debug("[配置加载] 配置加载到缓存成功: {} = {}", config.getConfigKey(), config.getConfigValue());
                successCount++;
            } catch (Exception e) {
                logger.error("[配置加载] 配置处理异常，跳过加载: {} = {}, 错误: {}", 
                    config.getConfigKey(), config.getConfigValue(), e.getMessage(), e);
                failCount++;
            }
        }
        
        long cachePutEndTime = System.currentTimeMillis();
        logger.info("[配置加载] 当前环境配置处理完成，成功: {}个, 失败: {}个, 缓存写入耗时: {}ms", 
            successCount, failCount, (cachePutEndTime - cachePutStartTime));
        
        // 如果当前环境没有配置，加载默认环境(environmentId为null)的配置作为 fallback
        if (configs.isEmpty()) {
            logger.warn("[配置加载] 当前环境 {} 没有配置，尝试加载默认环境配置作为fallback", currentEnvironmentCode);
            long defaultDbQueryStartTime = System.currentTimeMillis();
            DataiConfiguration defaultQuery = new DataiConfiguration();
            defaultQuery.setIsActive(true);
            defaultQuery.setEnvironmentId(null); // 默认环境配置
            List<DataiConfiguration> defaultConfigs = configurationMapper.selectDataiConfigurationList(defaultQuery);
            long defaultDbQueryEndTime = System.currentTimeMillis();
            logger.info("[配置加载] 默认环境配置查询完成，数量: {}, 查询耗时: {}ms", 
                defaultConfigs.size(), (defaultDbQueryEndTime - defaultDbQueryStartTime));
            
            int defaultSuccessCount = 0;
            int defaultFailCount = 0;
            long defaultCachePutStartTime = System.currentTimeMillis();
            
            for (DataiConfiguration config : defaultConfigs) {
                try {
                    CacheUtils.getCache(cacheKey).put(config.getConfigKey(), config.getConfigValue());
                    logger.debug("[配置加载] 默认配置加载到缓存成功: {} = {}", config.getConfigKey(), config.getConfigValue());
                    defaultSuccessCount++;
                } catch (Exception e) {
                    logger.error("[配置加载] 默认配置处理异常，跳过加载: {} = {}, 错误: {}", 
                        config.getConfigKey(), config.getConfigValue(), e.getMessage(), e);
                    defaultFailCount++;
                }
            }
            
            long defaultCachePutEndTime = System.currentTimeMillis();
            logger.info("[配置加载] 默认环境配置处理完成，成功: {}个, 失败: {}个, 缓存写入耗时: {}ms", 
                defaultSuccessCount, defaultFailCount, (defaultCachePutEndTime - defaultCachePutStartTime));
            
            successCount += defaultSuccessCount;
            failCount += defaultFailCount;
        }
        
        // 统计缓存最终状态
        long cacheSize = CacheUtils.getkeys(cacheKey).size();
        long loadEndTime = System.currentTimeMillis();
        logger.info("[配置加载] 配置加载到缓存完成，总耗时: {}ms, 总处理配置: {}个, 成功加载: {}个, 缓存最终大小: {}个, 缓存键: {}", 
            (loadEndTime - loadStartTime), (successCount + failCount), successCount, cacheSize, cacheKey);
    }
    
    /**
     * 获取带环境标识的缓存键
     * @return 带环境标识的缓存键
     */
    public String getEnvironmentCacheKey() {
        return SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY + ":" + currentEnvironmentCode;
    }

    /**
     * 重置配置缓存
     */
    public void resetConfigCache() {
        long resetStartTime = System.currentTimeMillis();
        logger.info("[配置加载] 开始重置Salesforce配置缓存，当前环境: {}, 开始时间: {}", 
            currentEnvironmentCode, resetStartTime);
        
        loadConfigToCache();
        
        long resetEndTime = System.currentTimeMillis();
        logger.info("[配置加载] 配置缓存重置完成，耗时: {}ms, 当前环境: {}", 
            (resetEndTime - resetStartTime), currentEnvironmentCode);
    }

    /**
     * 清空配置缓存
     */
    public void clearConfigCache() {
        long clearStartTime = System.currentTimeMillis();
        String cacheKey = getEnvironmentCacheKey();
        logger.info("[配置加载] 开始清空Salesforce配置缓存，当前环境: {}, 缓存键: {}", 
            currentEnvironmentCode, cacheKey);
        
        CacheUtils.getCache(cacheKey).clear();
        
        long clearEndTime = System.currentTimeMillis();
        long cacheSizeAfter = CacheUtils.getkeys(cacheKey).size();
        logger.info("[配置加载] 配置缓存清空完成，耗时: {}ms, 当前环境: {}, 缓存键: {}, 清空后缓存大小: {}个", 
            (clearEndTime - clearStartTime), currentEnvironmentCode, cacheKey, cacheSizeAfter);
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

    /**
     * 动态切换环境
     * 
     * @param environmentCode 环境编码
     * @return 切换结果
     */
    public boolean switchEnvironment(String environmentCode) {
        long startTime = System.currentTimeMillis();
        String oldEnvironmentCode = this.currentEnvironmentCode;
        Long oldEnvironmentId = this.currentEnvironmentId;
        
        logger.info("[环境切换] 开始切换环境: {} -> {}, 开始时间: {}", 
            oldEnvironmentCode, environmentCode, startTime);
        
        try {
            DataiConfigEnvironment query = new DataiConfigEnvironment();
            query.setEnvironmentCode(environmentCode);
            query.setIsActive(true);
            
            List<DataiConfigEnvironment> environments = environmentMapper.selectDataiConfigEnvironmentList(query);
            
            if (environments == null || environments.isEmpty()) {
                logger.error("[环境切换] 环境切换失败: 环境编码 {} 不存在或未激活", environmentCode);
                return false;
            }
            
            DataiConfigEnvironment newEnvironment = environments.get(0);
            
            if (environmentCode.equals(this.currentEnvironmentCode)) {
                logger.info("[环境切换] 环境未发生变化，无需切换: {}", environmentCode);
                return true;
            }
            
            String oldCacheKey = SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY + ":" + oldEnvironmentCode;
            CacheUtils.getCache(oldCacheKey).clear();
            logger.info("[环境切换] 已清理旧环境缓存: {}", oldCacheKey);
            
            this.currentEnvironmentCode = environmentCode;
            this.currentEnvironmentId = newEnvironment.getId();
            logger.info("[环境切换] 已更新当前环境信息: 编码={}, ID={}", 
                this.currentEnvironmentCode, this.currentEnvironmentId);
            
            loadConfigToCache();
            
            long endTime = System.currentTimeMillis();
            logger.info("[环境切换] 环境切换成功: {} -> {}, 耗时: {}ms", 
                oldEnvironmentCode, environmentCode, (endTime - startTime));
            
            return true;
            
        } catch (Exception e) {
            long endTime = System.currentTimeMillis();
            logger.error("[环境切换] 环境切换失败: {} -> {}, 耗时: {}ms, 错误信息: {}", 
                oldEnvironmentCode, environmentCode, (endTime - startTime), e.getMessage(), e);
            
            this.currentEnvironmentCode = oldEnvironmentCode;
            this.currentEnvironmentId = oldEnvironmentId;
            
            return false;
        }
    }
}