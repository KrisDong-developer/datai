package com.datai.setting.service;

import java.util.List;
import com.datai.setting.domain.DataiConfigVersion;

/**
 * 配置版本Service接口
 * 
 * @author datai
 * @date 2025-12-24
 */
public interface IDataiConfigVersionService 
{
    /**
     * 查询配置版本
     * 
     * @param id 配置版本主键
     * @return 配置版本
     */
    public DataiConfigVersion selectDataiConfigVersionById(Long id);

    /**
     * 查询配置版本列表
     * 
     * @param dataiConfigVersion 配置版本
     * @return 配置版本集合
     */
    public List<DataiConfigVersion> selectDataiConfigVersionList(DataiConfigVersion dataiConfigVersion);

    /**
     * 新增配置版本
     * 
     * @param dataiConfigVersion 配置版本
     * @return 结果
     */
    public int insertDataiConfigVersion(DataiConfigVersion dataiConfigVersion);

    /**
     * 修改配置版本
     * 
     * @param dataiConfigVersion 配置版本
     * @return 结果
     */
    public int updateDataiConfigVersion(DataiConfigVersion dataiConfigVersion);

    /**
     * 批量删除配置版本
     * 
     * @param ids 需要删除的配置版本主键集合
     * @return 结果
     */
    public int deleteDataiConfigVersionByIds(Long[] ids);

    /**
     * 删除配置版本信息
     * 
     * @param id 配置版本主键
     * @return 结果
     */
    public int deleteDataiConfigVersionById(Long id);
}
