package com.datai.setting.service.impl;

import com.datai.common.core.domain.model.LoginUser;
import com.datai.common.utils.DateUtils;
import com.alibaba.fastjson2.JSON;
import com.datai.common.utils.SecurityUtils;
import com.datai.setting.domain.DataiConfigVersion;
import com.datai.setting.domain.DataiConfiguration;
import com.datai.setting.mapper.DataiConfigVersionMapper;
import com.datai.setting.mapper.DataiConfigurationMapper;
import com.datai.setting.service.IDataiConfigVersionService;
import com.datai.setting.service.IDataiConfigurationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

/**
 * 配置版本Service业务层处理
 *
 * @author datai
 * @date 2025-12-14
 */
@Service
public class DataiConfigVersionServiceImpl implements IDataiConfigVersionService {
    private static final Logger logger = LoggerFactory.getLogger(DataiConfigVersionServiceImpl.class);
    
    @Autowired
    private DataiConfigVersionMapper dataiConfigVersionMapper;
    
    @Autowired
    private DataiConfigurationMapper dataiConfigurationMapper;
    
    @Autowired
    private IDataiConfigurationService dataiConfigurationService;

    /**
     * 查询配置版本
     *
     * @param versionId 配置版本主键
     * @return 配置版本
     */
    @Override
    public DataiConfigVersion selectDataiConfigVersionByVersionId(Long versionId)
    {
        return dataiConfigVersionMapper.selectDataiConfigVersionByVersionId(versionId);
    }

    /**
     * 查询配置版本列表
     *
     * @param dataiConfigVersion 配置版本
     * @return 配置版本
     */
    @Override
    public List<DataiConfigVersion> selectDataiConfigVersionList(DataiConfigVersion dataiConfigVersion)
    {
        return dataiConfigVersionMapper.selectDataiConfigVersionList(dataiConfigVersion);
    }

    /**
     * 新增配置版本
     *
     * @param dataiConfigVersion 配置版本
     * @return 结果
     */
    @Override
    public int insertDataiConfigVersion(DataiConfigVersion dataiConfigVersion)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

        dataiConfigVersion.setCreateTime(DateUtils.getNowDate());
        dataiConfigVersion.setUpdateTime(DateUtils.getNowDate());
        dataiConfigVersion.setCreateBy(username);
        dataiConfigVersion.setUpdateBy(username);
        return dataiConfigVersionMapper.insertDataiConfigVersion(dataiConfigVersion);
    }

    /**
     * 修改配置版本
     *
     * @param dataiConfigVersion 配置版本
     * @return 结果
     */
    @Override
    public int updateDataiConfigVersion(DataiConfigVersion dataiConfigVersion)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

        dataiConfigVersion.setUpdateTime(DateUtils.getNowDate());
        dataiConfigVersion.setUpdateBy(username);
        return dataiConfigVersionMapper.updateDataiConfigVersion(dataiConfigVersion);
    }

    /**
     * 批量删除配置版本
     *
     * @param versionIds 需要删除的配置版本主键
     * @return 结果
     */
    @Override
    public int deleteDataiConfigVersionByVersionIds(Long[] versionIds)
    {
        return dataiConfigVersionMapper.deleteDataiConfigVersionByVersionIds(versionIds);
    }

    /**
     * 删除配置版本信息
     *
     * @param versionId 配置版本主键
     * @return 结果
     */
    @Override
    public int deleteDataiConfigVersionByVersionId(Long versionId)
    {
        return dataiConfigVersionMapper.deleteDataiConfigVersionByVersionId(versionId);
    }

    /**
     * 创建配置版本快照
     *
     * @param versionDesc 版本描述
     * @return 配置版本
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public DataiConfigVersion createVersionSnapshot(String versionDesc) {
        logger.info("开始创建配置版本快照，描述: {}", versionDesc);
        
        // 查询当前所有激活的配置
        DataiConfiguration query = new DataiConfiguration();
        query.setIsActive(1L); // 只包含激活状态的配置
        List<DataiConfiguration> allConfigs = dataiConfigurationMapper.selectDataiConfigurationList(query);
        logger.info("当前激活配置数量: {}", allConfigs.size());
        
        // 生成快照内容
        String snapshotContent = JSON.toJSONString(allConfigs);
        
        // 创建配置版本
        DataiConfigVersion version = new DataiConfigVersion();
        version.setVersionNumber(generateVersionNumber());
        version.setVersionDesc(versionDesc);
        version.setSnapshotId(UUID.randomUUID().toString());
        version.setSnapshotContent(snapshotContent);
        version.setStatus("CREATED");
        
        // 设置审计信息
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();
        version.setCreateTime(DateUtils.getNowDate());
        version.setUpdateTime(DateUtils.getNowDate());
        version.setCreateBy(username);
        version.setUpdateBy(username);
        
        // 保存到数据库
        int result = dataiConfigVersionMapper.insertDataiConfigVersion(version);
        if (result > 0) {
            logger.info("配置版本快照创建成功，版本号: {}", version.getVersionNumber());
            return version;
        } else {
            logger.error("配置版本快照创建失败");
            throw new RuntimeException("配置版本快照创建失败");
        }
    }

    /**
     * 发布配置版本
     *
     * @param versionId 版本ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int publishConfigVersion(Long versionId) {
        logger.info("开始发布配置版本，版本ID: {}", versionId);
        
        // 查询版本信息
        DataiConfigVersion version = dataiConfigVersionMapper.selectDataiConfigVersionByVersionId(versionId);
        if (version == null) {
            logger.error("配置版本不存在，版本ID: {}", versionId);
            return 0;
        }
        
        // 更新版本状态为已发布
        version.setStatus("PUBLISHED");
        version.setPublishTime(LocalDate.now());
        
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();
        version.setUpdateTime(DateUtils.getNowDate());
        version.setUpdateBy(username);
        
        int result = dataiConfigVersionMapper.updateDataiConfigVersion(version);
        if (result > 0) {
            logger.info("配置版本发布成功，版本号: {}", version.getVersionNumber());
        } else {
            logger.error("配置版本发布失败，版本ID: {}", versionId);
        }
        
        return result;
    }

    /**
     * 回滚到指定版本
     *
     * @param versionId 版本ID
     * @return 结果
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public int rollbackToVersion(Long versionId) {
        logger.info("开始回滚配置到版本，版本ID: {}", versionId);
        
        // 查询版本信息
        DataiConfigVersion version = dataiConfigVersionMapper.selectDataiConfigVersionByVersionId(versionId);
        if (version == null) {
            logger.error("配置版本不存在，版本ID: {}", versionId);
            return 0;
        }
        
        // 解析快照内容
        List<DataiConfiguration> snapshotConfigs = JSON.parseArray(version.getSnapshotContent(), DataiConfiguration.class);
        if (snapshotConfigs == null || snapshotConfigs.isEmpty()) {
            logger.error("配置版本快照内容为空，版本ID: {}", versionId);
            return 0;
        }
        
        // 清空当前所有配置
        dataiConfigurationMapper.deleteAllConfigurations();
        logger.info("已清空当前所有配置");
        
        // 重新插入快照中的配置
        int insertedCount = 0;
        for (DataiConfiguration config : snapshotConfigs) {
            // 重置ID和时间戳
            config.setConfigId(null);
            
            LoginUser loginUser = SecurityUtils.getLoginUser();
            String username = loginUser.getUsername();
            config.setCreateTime(DateUtils.getNowDate());
            config.setUpdateTime(DateUtils.getNowDate());
            config.setCreateBy(username);
            config.setUpdateBy(username);
            
            int result = dataiConfigurationMapper.insertDataiConfiguration(config);
            if (result > 0) {
                insertedCount++;
            }
        }
        
        logger.info("回滚配置成功，插入配置数量: {}", insertedCount);
        
        // 重置配置缓存
        dataiConfigurationService.resetConfigCache();
        
        return insertedCount;
    }

    /**
     * 获取当前生效版本
     *
     * @return 当前生效版本
     */
    @Override
    public DataiConfigVersion getCurrentActiveVersion() {
        // 查询最新发布的版本
        DataiConfigVersion query = new DataiConfigVersion();
        query.setStatus("PUBLISHED");
        List<DataiConfigVersion> versions = dataiConfigVersionMapper.selectDataiConfigVersionList(query);
        
        if (versions.isEmpty()) {
            return null;
        }
        
        // 返回最新的已发布版本（按创建时间倒序排序）
        return versions.stream()
                .max((v1, v2) -> v1.getCreateTime().compareTo(v2.getCreateTime()))
                .orElse(null);
    }
    
    /**
     * 生成版本号
     * 格式：YYYY.MM.DD.N (如 2025.12.14.1)
     *
     * @return 版本号
     */
    private String generateVersionNumber() {
        // 获取当前日期
        String dateStr = DateUtils.getDate();
        dateStr = dateStr.replace("-", ".");
        
        // 查询当天已生成的版本数量
        int count = dataiConfigVersionMapper.countVersionsByDate(DateUtils.getDate());
        
        // 生成版本号
        return dateStr + "." + (count + 1);
    }
}
