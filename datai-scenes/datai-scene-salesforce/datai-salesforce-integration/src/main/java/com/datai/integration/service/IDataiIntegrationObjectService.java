package com.datai.integration.service;

import java.util.List;
import com.datai.integration.domain.DataiIntegrationObject;

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
}
