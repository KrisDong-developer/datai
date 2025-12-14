package com.datai.setting.mapper;

import java.util.List;
import com.datai.setting.domain.DataiConfiguration;

/**
 * 配置Mapper接口
 * 
 * @author datai
 * @date 2025-12-14
 */
public interface DataiConfigurationMapper 
{
    /**
     * 查询配置
     * 
     * @param configId 配置主键
     * @return 配置
     */
    public DataiConfiguration selectDataiConfigurationByConfigId(Long configId);

    /**
     * 查询配置列表
     * 
     * @param dataiConfiguration 配置
     * @return 配置集合
     */
    public List<DataiConfiguration> selectDataiConfigurationList(DataiConfiguration dataiConfiguration);

    /**
     * 新增配置
     * 
     * @param dataiConfiguration 配置
     * @return 结果
     */
    public int insertDataiConfiguration(DataiConfiguration dataiConfiguration);

    /**
     * 修改配置
     * 
     * @param dataiConfiguration 配置
     * @return 结果
     */
    public int updateDataiConfiguration(DataiConfiguration dataiConfiguration);

    /**
     * 删除配置
     * 
     * @param configId 配置主键
     * @return 结果
     */
    public int deleteDataiConfigurationByConfigId(Long configId);

    /**
     * 批量删除配置
     * 
     * @param configIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataiConfigurationByConfigIds(Long[] configIds);
}
