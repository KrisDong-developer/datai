package com.datai.integration.service;

import java.util.List;
import java.util.Map;
import com.datai.integration.model.domain.DataiIntegrationObject;

/**
 * 对象同步控制Service接口
 * 
 * @author datai
 * @date 2025-12-24
 */
public interface IDataiIntegrationObjectService 
{
    /**
     * 查询对象同步控制
     * 
     * @param id 对象同步控制主键
     * @return 对象同步控制
     */
    public DataiIntegrationObject selectDataiIntegrationObjectById(Integer id);

    /**
     * 查询对象同步控制列表
     * 
     * @param dataiIntegrationObject 对象同步控制
     * @return 对象同步控制集合
     */
    public List<DataiIntegrationObject> selectDataiIntegrationObjectList(DataiIntegrationObject dataiIntegrationObject);

    /**
     * 新增对象同步控制
     * 
     * @param dataiIntegrationObject 对象同步控制
     * @return 结果
     */
    public int insertDataiIntegrationObject(DataiIntegrationObject dataiIntegrationObject);

    /**
     * 修改对象同步控制
     * 
     * @param dataiIntegrationObject 对象同步控制
     * @return 结果
     */
    public int updateDataiIntegrationObject(DataiIntegrationObject dataiIntegrationObject);

    /**
     * 批量删除对象同步控制
     * 
     * @param ids 需要删除的对象同步控制主键集合
     * @return 结果
     */
    public int deleteDataiIntegrationObjectByIds(Integer[] ids);

    /**
     * 删除对象同步控制信息
     * 
     * @param id 对象同步控制主键
     * @return 结果
     */
    public int deleteDataiIntegrationObjectById(Integer id);

    /**
     * 获取对象同步统计信息
     * 
     * @param id 对象ID
     * @return 统计信息
     */
    public Map<String, Object> getSyncStatistics(Integer id);

    /**
     * 获取对象依赖关系
     * 
     * @param id 对象ID
     * @return 依赖关系列表
     */
    public List<Map<String, Object>> getObjectDependencies(Integer id);

    /**
     * 创建对象表结构
     * 
     * @param id 对象ID
     * @return 创建结果
     */
    public Map<String, Object> createObjectStructure(Integer id);

    /**
     * 变更对象启用同步状态
     * 
     * @param id 对象ID
     * @param isWork 启用同步状态
     * @return 变更结果
     */
    public Map<String, Object> updateWorkStatus(Integer id, Boolean isWork);

    /**
     * 变更对象增量更新状态
     * 
     * @param id 对象ID
     * @param isIncremental 增量更新状态
     * @return 变更结果
     */
    public Map<String, Object> updateIncrementalStatus(Integer id, Boolean isIncremental);

    /**
     * 获取对象整体统计信息
     * 
     * @return 统计信息
     */
    public Map<String, Object> getObjectStatistics();

    /**
     * 同步单对象数据到本地数据库
     * 
     * @param id 对象ID
     * @return 同步结果
     */
    public Map<String, Object> syncSingleObjectData(Integer id);
}
