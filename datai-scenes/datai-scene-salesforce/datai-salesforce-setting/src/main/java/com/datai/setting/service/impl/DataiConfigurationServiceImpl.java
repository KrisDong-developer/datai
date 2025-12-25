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
        // 先从缓存获取
        String configValue = CacheUtils.get(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY, configKey, String.class);
        if (configValue != null) {
            return configValue;
        }

        // 缓存不存在则从数据库查询
        DataiConfiguration query = new DataiConfiguration();
        query.setConfigKey(configKey);
        query.setIsActive(true);
        List<DataiConfiguration> configs = dataiConfigurationMapper.selectDataiConfigurationList(query);

        if (!configs.isEmpty()) {
            configValue = configs.get(0).getConfigValue();
            // 更新缓存
            CacheUtils.getCache(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY).put(configKey, configValue);
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
        // 验证配置值合法性
        if (!validateConfigValue(dataiConfiguration)) {
            logger.error("配置值验证失败: {}", dataiConfiguration.getConfigKey());
            return 0;
        }

        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

        dataiConfiguration.setCreateTime(DateUtils.getNowDate());
        dataiConfiguration.setUpdateTime(DateUtils.getNowDate());
        dataiConfiguration.setCreateBy(username);
        dataiConfiguration.setUpdateBy(username);

        int result = dataiConfigurationMapper.insertDataiConfiguration(dataiConfiguration);
        if (result > 0) {
            // 更新缓存
            CacheUtils.getCache(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY)
                    .put(dataiConfiguration.getConfigKey(), dataiConfiguration.getConfigValue());

            // 发布配置变更事件
            publishConfigChangeEvent(dataiConfiguration, "CREATE", null, dataiConfiguration.getConfigValue());
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
        // 验证配置值合法性
        if (!validateConfigValue(dataiConfiguration)) {
            logger.error("配置值验证失败: {}", dataiConfiguration.getConfigKey());
            return 0;
        }

        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

        dataiConfiguration.setUpdateTime(DateUtils.getNowDate());
        dataiConfiguration.setUpdateBy(username);

        // 获取旧配置，用于审计日志和缓存清理
        DataiConfiguration oldConfig = selectDataiConfigurationById(dataiConfiguration.getId());
        String oldValue = oldConfig != null ? oldConfig.getConfigValue() : null;

        int result = dataiConfigurationMapper.updateDataiConfiguration(dataiConfiguration);
        if (result > 0) {
            // 如果配置键发生变化，清理旧键的缓存
            if (oldConfig != null && !oldConfig.getConfigKey().equals(dataiConfiguration.getConfigKey())) {
                CacheUtils.getCache(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY)
                        .evict(oldConfig.getConfigKey());
            }

            // 更新缓存
            CacheUtils.getCache(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY)
                    .put(dataiConfiguration.getConfigKey(), dataiConfiguration.getConfigValue());

            // 发布配置变更事件
            publishConfigChangeEvent(dataiConfiguration, "UPDATE", oldValue, dataiConfiguration.getConfigValue());
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
        for (Long configId : configIds) {
            DataiConfiguration config = selectDataiConfigurationById(configId);
            if (config != null) {
                String oldValue = config.getConfigValue();
                result += dataiConfigurationMapper.deleteDataiConfigurationById(configId);
                // 删除缓存
                CacheUtils.getCache(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY)
                        .evict(config.getConfigKey());

                // 发布配置变更事件
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
        int result = dataiConfigurationMapper.deleteDataiConfigurationById(configId);
        if (result > 0) {
            // 删除缓存
            CacheUtils.getCache(SalesforceConfigConstants.SALESFORCE_CONFIG_CACHE_KEY)
                    .evict(config.getConfigKey());

            // 发布配置变更事件
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

    @Autowired
    private IDataiConfigValidationRuleService configValidationRuleService;

    /**
     * 验证配置值合法性
     *
     * @param dataiConfiguration 配置
     * @return 验证结果
     */
    @Override
    public boolean validateConfigValue(DataiConfiguration dataiConfiguration) {
        // 1. 基础验证：配置键不能为空
        String configKey = dataiConfiguration.getConfigKey();
        if (configKey == null || configKey.isEmpty()) {
            logger.error("配置键不能为空");
            return false;
        }

        // 2. 使用新的配置验证规则服务进行验证
        return configValidationRuleService.validateConfigIntegrity(dataiConfiguration);
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

