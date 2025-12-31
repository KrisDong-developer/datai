package com.datai.setting.mapper;

import java.util.List;
import com.datai.setting.model.domain.DataiConfiguration;

/**
 * 配置Mapper接口
 * 
 * @author datai
 * @date 2025-12-24
 */
public interface DataiConfigurationMapper 
{
    /**
     * 查询配置
     * 
     * @param id 配置主键
     * @return 配置
     */
    public DataiConfiguration selectDataiConfigurationById(Long id);

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
     * @param id 配置主键
     * @return 结果
     */
    public int deleteDataiConfigurationById(Long id);

    /**
     * 批量删除配置
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataiConfigurationByIds(Long[] ids);
}
