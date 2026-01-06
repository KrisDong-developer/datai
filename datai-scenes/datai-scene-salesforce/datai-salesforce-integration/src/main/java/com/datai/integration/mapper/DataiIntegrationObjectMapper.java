package com.datai.integration.mapper;

import java.util.List;
import java.util.Map;
import com.datai.integration.model.domain.DataiIntegrationObject;

/**
 * 对象同步控制Mapper接口
 * 
 * @author datai
 * @date 2025-12-24
 */
public interface DataiIntegrationObjectMapper 
{
    /**
     * 查询对象同步控制
     * 
     * @param id 对象同步控制主键
     * @return 对象同步控制
     */
    public DataiIntegrationObject selectDataiIntegrationObjectById(Integer id);

    /**
     * 根据API查询对象同步控制
     * 
     * @param api 对象API
     * @return 对象同步控制
     */
    public DataiIntegrationObject selectDataiIntegrationObjectByApi(String api);

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
     * 删除对象同步控制
     * 
     * @param id 对象同步控制主键
     * @return 结果
     */
    public int deleteDataiIntegrationObjectById(Integer id);

    /**
     * 批量删除对象同步控制
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataiIntegrationObjectByIds(Integer[] ids);

    /**
     * 查询对象同步统计信息
     * 
     * @param api 对象API
     * @return 统计信息
     */
    public Map<String, Object> selectObjectSyncStatistics(String api);

    /**
     * 查询对象依赖关系
     * 
     * @param api 对象API
     * @return 依赖关系列表
     */
    public List<Map<String, Object>> selectObjectDependencies(String api);
}
