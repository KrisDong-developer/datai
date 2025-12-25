package com.datai.setting.mapper;

import java.util.List;
import com.datai.setting.domain.DataiConfigEnvironment;

/**
 * 配置环境Mapper接口
 * 
 * @author datai
 * @date 2025-12-24
 */
public interface DataiConfigEnvironmentMapper 
{
    /**
     * 查询配置环境
     * 
     * @param id 配置环境主键
     * @return 配置环境
     */
    public DataiConfigEnvironment selectDataiConfigEnvironmentById(Long id);

    /**
     * 查询配置环境列表
     * 
     * @param dataiConfigEnvironment 配置环境
     * @return 配置环境集合
     */
    public List<DataiConfigEnvironment> selectDataiConfigEnvironmentList(DataiConfigEnvironment dataiConfigEnvironment);

    /**
     * 新增配置环境
     * 
     * @param dataiConfigEnvironment 配置环境
     * @return 结果
     */
    public int insertDataiConfigEnvironment(DataiConfigEnvironment dataiConfigEnvironment);

    /**
     * 修改配置环境
     * 
     * @param dataiConfigEnvironment 配置环境
     * @return 结果
     */
    public int updateDataiConfigEnvironment(DataiConfigEnvironment dataiConfigEnvironment);

    /**
     * 删除配置环境
     * 
     * @param id 配置环境主键
     * @return 结果
     */
    public int deleteDataiConfigEnvironmentById(Long id);

    /**
     * 批量删除配置环境
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataiConfigEnvironmentByIds(Long[] ids);
}
