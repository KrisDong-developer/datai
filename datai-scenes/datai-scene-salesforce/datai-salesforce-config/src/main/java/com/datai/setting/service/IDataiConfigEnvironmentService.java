package com.datai.setting.service;

import java.util.List;
import com.datai.setting.domain.DataiConfigEnvironment;

/**
 * 配置环境Service接口
 * 
 * @author datai
 * @date 2025-12-14
 */
public interface IDataiConfigEnvironmentService 
{
    /**
     * 查询配置环境
     * 
     * @param environmentId 配置环境主键
     * @return 配置环境
     */
    public DataiConfigEnvironment selectDataiConfigEnvironmentByEnvironmentId(Long environmentId);

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
     * 批量删除配置环境
     * 
     * @param environmentIds 需要删除的配置环境主键集合
     * @return 结果
     */
    public int deleteDataiConfigEnvironmentByEnvironmentIds(Long[] environmentIds);

    /**
     * 删除配置环境信息
     * 
     * @param environmentId 配置环境主键
     * @return 结果
     */
    public int deleteDataiConfigEnvironmentByEnvironmentId(Long environmentId);
}
