package com.datai.setting.service.impl;

import java.util.List;

import com.alibaba.fastjson2.JSON;
import com.datai.common.utils.DateUtils;
import com.datai.common.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.setting.mapper.DataiConfigSnapshotMapper;
import com.datai.setting.model.domain.DataiConfigSnapshot;
import com.datai.setting.model.domain.DataiConfiguration;
import com.datai.setting.service.IDataiConfigSnapshotService;
import com.datai.setting.service.IDataiConfigurationService;
import com.datai.common.core.domain.model.LoginUser;


/**
 * 配置快照Service业务层处理
 *
 * @author datai
 * @date 2025-12-24
 */
@Service
public class DataiConfigSnapshotServiceImpl implements IDataiConfigSnapshotService {
    private static final Logger logger = LoggerFactory.getLogger(DataiConfigSnapshotServiceImpl.class);

    @Autowired
    private DataiConfigSnapshotMapper dataiConfigSnapshotMapper;

    @Autowired
    private IDataiConfigurationService dataiConfigurationService;

    /**
     * 查询配置快照
     *
     * @param id 配置快照主键
     * @return 配置快照
     */
    @Override
    public DataiConfigSnapshot selectDataiConfigSnapshotById(Long id)
    {
        return dataiConfigSnapshotMapper.selectDataiConfigSnapshotById(id);
    }

    /**
     * 查询配置快照列表
     *
     * @param dataiConfigSnapshot 配置快照
     * @return 配置快照
     */
    @Override
    public List<DataiConfigSnapshot> selectDataiConfigSnapshotList(DataiConfigSnapshot dataiConfigSnapshot)
    {
        return dataiConfigSnapshotMapper.selectDataiConfigSnapshotList(dataiConfigSnapshot);
    }

    /**
     * 新增配置快照
     *
     * @param dataiConfigSnapshot 配置快照
     * @return 结果
     */
    @Override
    public int insertDataiConfigSnapshot(DataiConfigSnapshot dataiConfigSnapshot)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiConfigSnapshot.setCreateTime(DateUtils.getNowDate());
                dataiConfigSnapshot.setUpdateTime(DateUtils.getNowDate());
                dataiConfigSnapshot.setCreateBy(username);
                dataiConfigSnapshot.setUpdateBy(username);
            return dataiConfigSnapshotMapper.insertDataiConfigSnapshot(dataiConfigSnapshot);
    }

    /**
     * 修改配置快照
     *
     * @param dataiConfigSnapshot 配置快照
     * @return 结果
     */
    @Override
    public int updateDataiConfigSnapshot(DataiConfigSnapshot dataiConfigSnapshot)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiConfigSnapshot.setUpdateTime(DateUtils.getNowDate());
                dataiConfigSnapshot.setUpdateBy(username);
        return dataiConfigSnapshotMapper.updateDataiConfigSnapshot(dataiConfigSnapshot);
    }

    /**
     * 批量删除配置快照
     *
     * @param ids 需要删除的配置快照主键
     * @return 结果
     */
    @Override
    public int deleteDataiConfigSnapshotByIds(String[] ids)
    {
        return dataiConfigSnapshotMapper.deleteDataiConfigSnapshotByIds(ids);
    }

    /**
     * 删除配置快照信息
     *
     * @param id 配置快照主键
     * @return 结果
     */
    @Override
    public int deleteDataiConfigSnapshotById(String id)
    {
        return dataiConfigSnapshotMapper.deleteDataiConfigSnapshotById(id);
    }

    /**
     * 从当前配置生成快照
     *
     * @param snapshotName 快照名称
     * @param environmentId 环境ID
     * @param description 描述
     * @return 配置快照
     */
    @Override
    public DataiConfigSnapshot createSnapshot(String snapshotName, Long environmentId, String description)
    {
        logger.info("创建快照，快照名称: {}, 环境ID: {}", snapshotName, environmentId);
        
        try {
            DataiConfiguration queryConfig = new DataiConfiguration();
            queryConfig.setEnvironmentId(environmentId);
            List<DataiConfiguration> configs = dataiConfigurationService.selectDataiConfigurationList(queryConfig);
            
            String snapshotContent = JSON.toJSONString(configs);
            
            DataiConfigSnapshot snapshot = new DataiConfigSnapshot();
            snapshot.setSnapshotNumber(snapshotName);
            snapshot.setEnvironmentId(environmentId);
            snapshot.setSnapshotDesc(description);
            snapshot.setSnapshotContent(snapshotContent);
            snapshot.setConfigCount(configs.size());
            
            LoginUser loginUser = SecurityUtils.getLoginUser();
            String username = loginUser.getUsername();
            snapshot.setCreateBy(username);
            snapshot.setCreateTime(DateUtils.getNowDate());
            snapshot.setUpdateBy(username);
            snapshot.setUpdateTime(DateUtils.getNowDate());
            
            dataiConfigSnapshotMapper.insertDataiConfigSnapshot(snapshot);
            
            logger.info("快照创建成功，快照ID: {}, 配置数量: {}", snapshot.getId(), configs.size());
            
            return snapshot;
            
        } catch (Exception e) {
            logger.error("创建快照失败，快照名称: {}, 环境ID: {}, 错误信息: {}", snapshotName, environmentId, e.getMessage(), e);
            throw new RuntimeException("创建快照失败: " + e.getMessage(), e);
        }
    }

    /**
     * 恢复快照
     *
     * @param snapshotId 快照ID
     * @param restoreReason 恢复原因
     * @return 结果
     */
    @Override
    public int restoreSnapshot(String snapshotId, String restoreReason)
    {
        logger.info("开始恢复快照，快照ID: {}, 恢复原因: {}", snapshotId, restoreReason);
        
        try {
            DataiConfigSnapshot snapshot = dataiConfigSnapshotMapper.selectDataiConfigSnapshotById(Long.valueOf(snapshotId));
            if (snapshot == null) {
                logger.error("快照不存在，快照ID: {}", snapshotId);
                throw new RuntimeException("快照不存在");
            }
            
            String snapshotContent = snapshot.getSnapshotContent();
            if (snapshotContent == null || snapshotContent.isEmpty()) {
                logger.error("快照内容为空，快照ID: {}", snapshotId);
                throw new RuntimeException("快照内容为空");
            }
            
            List<DataiConfiguration> configs = JSON.parseArray(snapshotContent, DataiConfiguration.class);
            if (configs == null || configs.isEmpty()) {
                logger.warn("快照中没有配置数据，快照ID: {}", snapshotId);
            }
            
            DataiConfiguration queryConfig = new DataiConfiguration();
            queryConfig.setEnvironmentId(snapshot.getEnvironmentId());
            List<DataiConfiguration> existingConfigs = dataiConfigurationService.selectDataiConfigurationList(queryConfig);
            
            if (!existingConfigs.isEmpty()) {
                Long[] existingIds = existingConfigs.stream()
                    .map(DataiConfiguration::getId)
                    .toArray(Long[]::new);
                dataiConfigurationService.deleteDataiConfigurationByIds(existingIds);
                logger.info("已删除当前环境的配置，数量: {}", existingIds.length);
            }
            
            LoginUser loginUser = SecurityUtils.getLoginUser();
            String username = loginUser.getUsername();
            
            int restoredCount = 0;
            for (DataiConfiguration config : configs) {
                config.setId(null);
                config.setCreateBy(username);
                config.setCreateTime(DateUtils.getNowDate());
                config.setUpdateBy(username);
                config.setUpdateTime(DateUtils.getNowDate());
                dataiConfigurationService.insertDataiConfiguration(config);
                restoredCount++;
            }
            
            dataiConfigurationService.clearConfigCache();
            dataiConfigurationService.loadingConfigCache();
            
            logger.info("快照恢复成功，快照ID: {}, 恢复配置数量: {}", snapshotId, restoredCount);
            
            return restoredCount;
            
        } catch (Exception e) {
            logger.error("恢复快照失败，快照ID: {}, 恢复原因: {}, 错误信息: {}", snapshotId, restoreReason, e.getMessage(), e);
            throw new RuntimeException("恢复快照失败: " + e.getMessage(), e);
        }
    }

    /**
     * 获取快照详细信息（包含配置内容）
     *
     * @param snapshotId 快照ID
     * @return 配置快照
     */
    @Override
    public DataiConfigSnapshot getSnapshotDetail(String snapshotId)
    {
        logger.info("获取快照详细信息，快照ID: {}", snapshotId);
        
        try {
            DataiConfigSnapshot snapshot = dataiConfigSnapshotMapper.selectDataiConfigSnapshotById(Long.valueOf(snapshotId));
            if (snapshot == null) {
                logger.error("快照不存在，快照ID: {}", snapshotId);
                throw new RuntimeException("快照不存在");
            }
            
            logger.info("获取快照详细信息成功，快照ID: {}, 配置数量: {}", snapshotId, snapshot.getConfigCount());
            
            return snapshot;
            
        } catch (Exception e) {
            logger.error("获取快照详细信息失败，快照ID: {}, 错误信息: {}", snapshotId, e.getMessage(), e);
            throw new RuntimeException("获取快照详细信息失败: " + e.getMessage(), e);
        }
    }

    /**
     * 比较两个快照的差异
     *
     * @param snapshotId1 快照ID1
     * @param snapshotId2 快照ID2
     * @return 差异信息
     */
    @Override
    public String compareSnapshots(String snapshotId1, String snapshotId2)
    {
        logger.info("开始比较两个快照的差异，快照ID1: {}, 快照ID2: {}", snapshotId1, snapshotId2);
        
        try {
            DataiConfigSnapshot snapshot1 = dataiConfigSnapshotMapper.selectDataiConfigSnapshotById(Long.valueOf(snapshotId1));
            DataiConfigSnapshot snapshot2 = dataiConfigSnapshotMapper.selectDataiConfigSnapshotById(Long.valueOf(snapshotId2));
            
            if (snapshot1 == null) {
                throw new RuntimeException("快照不存在: " + snapshotId1);
            }
            if (snapshot2 == null) {
                throw new RuntimeException("快照不存在: " + snapshotId2);
            }
            
            List<DataiConfiguration> configs1 = JSON.parseArray(snapshot1.getSnapshotContent(), DataiConfiguration.class);
            List<DataiConfiguration> configs2 = JSON.parseArray(snapshot2.getSnapshotContent(), DataiConfiguration.class);
            
            StringBuilder result = new StringBuilder();
            result.append("快照差异比较报告\n");
            result.append("====================\n");
            result.append("快照1: ").append(snapshot1.getSnapshotNumber())
                  .append(" (").append(snapshot1.getCreateTime()).append(")\n");
            result.append("快照2: ").append(snapshot2.getSnapshotNumber())
                  .append(" (").append(snapshot2.getCreateTime()).append(")\n");
            result.append("====================\n\n");
            
            java.util.Map<String, DataiConfiguration> map1 = new java.util.HashMap<>();
            for (DataiConfiguration config : configs1) {
                map1.put(config.getConfigKey(), config);
            }
            
            java.util.Map<String, DataiConfiguration> map2 = new java.util.HashMap<>();
            for (DataiConfiguration config : configs2) {
                map2.put(config.getConfigKey(), config);
            }
            
            java.util.Set<String> allKeys = new java.util.TreeSet<>();
            allKeys.addAll(map1.keySet());
            allKeys.addAll(map2.keySet());
            
            int addedCount = 0;
            int removedCount = 0;
            int modifiedCount = 0;
            int unchangedCount = 0;
            
            result.append("配置项差异详情:\n");
            result.append("----------------\n");
            
            for (String key : allKeys) {
                DataiConfiguration config1 = map1.get(key);
                DataiConfiguration config2 = map2.get(key);
                
                if (config1 == null && config2 != null) {
                    addedCount++;
                    result.append("[新增] ").append(key).append("\n");
                    result.append("  配置值: ").append(config2.getConfigValue()).append("\n");
                    result.append("  描述: ").append(config2.getDescription() != null ? config2.getDescription() : "").append("\n");
                } else if (config1 != null && config2 == null) {
                    removedCount++;
                    result.append("[删除] ").append(key).append("\n");
                    result.append("  原配置值: ").append(config1.getConfigValue()).append("\n");
                } else if (config1 != null && config2 != null) {
                    boolean valueChanged = !java.util.Objects.equals(config1.getConfigValue(), config2.getConfigValue());
                    boolean descriptionChanged = !java.util.Objects.equals(config1.getDescription(), config2.getDescription());
                    boolean activeChanged = !java.util.Objects.equals(config1.getIsActive(), config2.getIsActive());
                    
                    if (valueChanged || descriptionChanged || activeChanged) {
                        modifiedCount++;
                        result.append("[修改] ").append(key).append("\n");
                        if (valueChanged) {
                            result.append("  配置值: ").append(config1.getConfigValue())
                                  .append(" -> ").append(config2.getConfigValue()).append("\n");
                        }
                        if (descriptionChanged) {
                            result.append("  描述: ").append(config1.getDescription() != null ? config1.getDescription() : "")
                                  .append(" -> ").append(config2.getDescription() != null ? config2.getDescription() : "").append("\n");
                        }
                        if (activeChanged) {
                            result.append("  激活状态: ").append(config1.getIsActive())
                                  .append(" -> ").append(config2.getIsActive()).append("\n");
                        }
                    } else {
                        unchangedCount++;
                    }
                }
            }
            
            result.append("\n====================\n");
            result.append("统计摘要:\n");
            result.append("----------------\n");
            result.append("新增配置项: ").append(addedCount).append("\n");
            result.append("删除配置项: ").append(removedCount).append("\n");
            result.append("修改配置项: ").append(modifiedCount).append("\n");
            result.append("未改变配置项: ").append(unchangedCount).append("\n");
            result.append("总配置项变化: ").append(addedCount + removedCount + modifiedCount).append("\n");
            result.append("====================\n");
            
            logger.info("快照差异比较完成，快照ID1: {}, 快照ID2: {}, 新增: {}, 删除: {}, 修改: {}, 未改变: {}", 
                       snapshotId1, snapshotId2, addedCount, removedCount, modifiedCount, unchangedCount);
            
            return result.toString();
            
        } catch (Exception e) {
            logger.error("比较快照差异失败，快照ID1: {}, 快照ID2: {}, 错误信息: {}", snapshotId1, snapshotId2, e.getMessage(), e);
            throw new RuntimeException("比较快照差异失败: " + e.getMessage(), e);
        }
    }
}
