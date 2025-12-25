package com.datai.setting.service.impl;

import java.util.List;

import com.datai.common.utils.CacheUtils;
import com.datai.common.core.domain.model.LoginUser;
import com.datai.common.utils.DateUtils;
import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.setting.mapper.DataiConfigurationMapper;
import com.datai.common.utils.SecurityUtils;
import com.datai.salesforce.common.constant.SalesforceConfigConstants;
import com.datai.setting.config.SalesforceConfigCacheManager;
import com.datai.setting.domain.DataiConfiguration;
import com.datai.setting.mapper.DataiConfigurationMapper;
import com.datai.setting.service.IDataiConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 配置Service业务层处理
 *
 * @author datai
 * @date 2025-12-14
 */
@Service
public class DataiConfigurationServiceImpl implements IDataiConfigurationService {

    private static final Logger logger = LoggerFactory.getLogger(DataiConfigurationServiceImpl.class);

    @Autowired
    private DataiConfigurationMapper dataiConfigurationMapper;

    @Autowired
    private ApplicationEventPublisher eventPublisher;

    @Autowired
    private SalesforceConfigCacheManager SalesforceConfigCacheManager;

    /**
     * 查询配置
     *
     * @param configId 配置主键
     * @return 配置
     */
    @Override
    public DataiConfiguration selectDataiConfigurationById(Long configId)
    {
        return dataiConfigurationMapper.selectDataiConfigurationById(configId);
    }

    /**
     * 根据配置键查询配置值
     *
     * @param configKey 配置键
     * @return 配置值
     */
    @Override
    public String selectConfigValueByKey(String configKey) {
        // 获取环境隔离的缓存键 (格式: SALESFORCE_CONFIG_CACHE_KEY:environmentCode)
        String cacheKey = SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY + ":" + SalesforceConfigCacheManager.getCurrentEnvironmentCode();
        
        // 先从缓存获取 - 缓存优先策略
        String configValue = CacheUtils.get(cacheKey, configKey, String.class);
        if (configValue != null) {
            return configValue;
        }

        // 缓存不存在则从数据库查询
        DataiConfiguration query = new DataiConfiguration();
        query.setConfigKey(configKey);
        query.setIsActive(true);
        query.setEnvironmentId(SalesforceConfigCacheManager.getCurrentEnvironmentId()); // 按当前环境过滤
        List<DataiConfiguration> configs = dataiConfigurationMapper.selectDataiConfigurationList(query);

        if (!configs.isEmpty()) {
            configValue = configs.get(0).getConfigValue();
            // 更新缓存 - 写回到缓存
            CacheUtils.getCache(cacheKey).put(configKey, configValue);
            return configValue;
        }

        return null;
    }

    /**
     * 查询配置列表
     *
     * @param dataiConfiguration 配置
     * @return 配置
     */
    @Override
    public List<DataiConfiguration> selectDataiConfigurationList(DataiConfiguration dataiConfiguration)
    {
        return dataiConfigurationMapper.selectDataiConfigurationList(dataiConfiguration);
    }

    /**
     * 新增配置
     *
     * @param dataiConfiguration 配置
     * @return 结果
     */
    @Override
    public int insertDataiConfiguration(DataiConfiguration dataiConfiguration)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

        dataiConfiguration.setCreateTime(DateUtils.getNowDate());
        dataiConfiguration.setUpdateTime(DateUtils.getNowDate());
        dataiConfiguration.setCreateBy(username);
        dataiConfiguration.setUpdateBy(username);

        // 设置当前环境ID
        dataiConfiguration.setEnvironmentId(SalesforceConfigCacheManager.getCurrentEnvironmentId());
        
        // 设置初始版本号为1
        dataiConfiguration.setVersion(1);

        // 1. 先写入数据库
        int result = dataiConfigurationMapper.insertDataiConfiguration(dataiConfiguration);
        if (result > 0) {
            // 2. 再更新缓存 - 缓存优先策略：数据库变更后立即更新缓存
            String cacheKey = SalesforceConfigCacheManager.getCurrentEnvironmentCode() + ":" + SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY;
            CacheUtils.getCache(cacheKey)
                    .put(dataiConfiguration.getConfigKey(), dataiConfiguration.getConfigValue());

            // 3. 发布配置变更事件
            publishConfigChangeEvent(dataiConfiguration, "CREATE", null, dataiConfiguration.getConfigValue());
            
            logger.info("[缓存更新] 新增配置后更新缓存成功: {} = {}, 版本: {}, 环境: {}", 
                dataiConfiguration.getConfigKey(), dataiConfiguration.getConfigValue(), 
                dataiConfiguration.getVersion(), SalesforceConfigCacheManager.getCurrentEnvironmentCode());
        }

        return result;
    }

    /**
     * 修改配置
     *
     * @param dataiConfiguration 配置
     * @return 结果
     */
    @Override
    public int updateDataiConfiguration(DataiConfiguration dataiConfiguration)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

        dataiConfiguration.setUpdateTime(DateUtils.getNowDate());
        dataiConfiguration.setUpdateBy(username);

        // 获取旧配置，用于审计日志和缓存清理
        DataiConfiguration oldConfig = selectDataiConfigurationById(dataiConfiguration.getId());
        String oldValue = oldConfig != null ? oldConfig.getConfigValue() : null;
        String oldConfigKey = oldConfig != null ? oldConfig.getConfigKey() : null;
        
        // 版本自动递增：如果是更新操作，版本号+1
        if (oldConfig != null) {
            Integer currentVersion = oldConfig.getVersion();
            dataiConfiguration.setVersion(currentVersion != null ? currentVersion + 1 : 1);
        } else {
            dataiConfiguration.setVersion(1); // 防止旧记录没有版本号
        }

        // 1. 先更新数据库
        int result = dataiConfigurationMapper.updateDataiConfiguration(dataiConfiguration);
        if (result > 0) {
            String cacheKey = SalesforceConfigCacheManager.getCurrentEnvironmentCode() + ":" + SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY;
            
            // 2. 缓存更新策略：先删除旧键，再添加新键
            if (oldConfig != null && !oldConfigKey.equals(dataiConfiguration.getConfigKey())) {
                // 如果配置键发生变化，清理旧键的缓存
                CacheUtils.getCache(cacheKey).evict(oldConfigKey);
                logger.info("[缓存更新] 配置键变更，已清理旧缓存键: {}, 环境: {}", 
                    oldConfigKey, SalesforceConfigCacheManager.getCurrentEnvironmentCode());
            }

            // 3. 更新缓存 - 缓存优先策略：数据库变更后立即更新缓存
            CacheUtils.getCache(cacheKey)
                    .put(dataiConfiguration.getConfigKey(), dataiConfiguration.getConfigValue());

            // 4. 发布配置变更事件
            publishConfigChangeEvent(dataiConfiguration, "UPDATE", oldValue, dataiConfiguration.getConfigValue());
            
            logger.info("[缓存更新] 修改配置后更新缓存成功: {} -> {} = {}, 版本: {}, 环境: {}", 
                oldValue, dataiConfiguration.getConfigKey(), dataiConfiguration.getConfigValue(), 
                dataiConfiguration.getVersion(), SalesforceConfigCacheManager.getCurrentEnvironmentCode());
        }

        return result;
    }

    /**
     * 批量删除配置
     *
     * @param configIds 需要删除的配置主键
     * @return 结果
     */
    @Override
    public int deleteDataiConfigurationByIds(Long[] configIds)
    {
        int result = 0;
        String cacheKey = SalesforceConfigCacheManager.getCurrentEnvironmentCode() + ":" + SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY;
        
        for (Long configId : configIds) {
            DataiConfiguration config = selectDataiConfigurationById(configId);
            if (config != null) {
                String oldValue = config.getConfigValue();
                String configKeyToDelete = config.getConfigKey();
                
                // 1. 先删除数据库记录
                result += dataiConfigurationMapper.deleteDataiConfigurationById(configId);
                
                // 2. 再删除缓存 - 缓存优先策略：数据库变更后立即清理缓存
                CacheUtils.getCache(cacheKey).evict(configKeyToDelete);
                logger.info("[缓存更新] 批量删除配置后清理缓存: {}, 环境: {}", 
                    configKeyToDelete, SalesforceConfigCacheManager.getCurrentEnvironmentCode());

                // 3. 发布配置变更事件
                publishConfigChangeEvent(config, "DELETE", oldValue, null);
            }
        }
        return result;
    }

    /**
     * 删除配置信息
     *
     * @param configId 配置主键
     * @return 结果
     */
    @Override
    public int deleteDataiConfigurationById(Long configId)
    {
        DataiConfiguration config = selectDataiConfigurationById(configId);
        if (config == null) {
            return 0;
        }

        String oldValue = config.getConfigValue();
        String configKeyToDelete = config.getConfigKey();
        String cacheKey = SalesforceConfigCacheManager.getCurrentEnvironmentCode() + ":" + SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY;
        
        // 1. 先删除数据库记录
        int result = dataiConfigurationMapper.deleteDataiConfigurationById(configId);
        if (result > 0) {
            // 2. 再删除缓存 - 缓存优先策略：数据库变更后立即清理缓存
            CacheUtils.getCache(cacheKey).evict(configKeyToDelete);
            logger.info("[缓存更新] 删除配置后清理缓存: {}, 环境: {}", 
                configKeyToDelete, SalesforceConfigCacheManager.getCurrentEnvironmentCode());

            // 3. 发布配置变更事件
            publishConfigChangeEvent(config, "DELETE", oldValue, null);
        }

        return result;
    }

    /**
     * 加载配置到缓存
     */
    @Override
    public void loadingConfigCache() {
        SalesforceConfigCacheManager.loadConfigToCache();
    }

    /**
     * 清空配置缓存
     */
    @Override
    public void clearConfigCache() {
        SalesforceConfigCacheManager.clearConfigCache();
    }

    /**
     * 重置配置缓存
     */
    @Override
    public void resetConfigCache() {
        SalesforceConfigCacheManager.resetConfigCache();
    }



    /**
     * 发布配置变更事件
     *
     * @param config 配置对象
     * @param operationType 操作类型
     * @param oldValue 旧值
     * @param newValue 新值
     */
    private void publishConfigChangeEvent(DataiConfiguration config, String operationType, String oldValue, String newValue) {
        logger.info("配置变更事件: {} - {} - {} -> {}", operationType, config.getConfigKey(), oldValue, newValue);

        // 发布Spring事件
        eventPublisher.publishEvent(new com.datai.setting.event.ConfigChangeEvent(this, config, operationType, oldValue, newValue));
    }
}

