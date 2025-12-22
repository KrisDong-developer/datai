package com.datai.integration.mapper;

import java.util.List;
import com.datai.integration.domain.DataiIntegrationObject;

/**
 * 对象信息Mapper接口
 * 
 * @author datai
 * @date 2025-12-22
 */
public interface DataiIntegrationObjectMapper 
{
    /**
     * 查询对象信息
     * 
     * @param id 对象信息主键
     * @return 对象信息
     */
    public DataiIntegrationObject selectDataiIntegrationObjectById(Long id);

    /**
     * 查询对象信息列表
     * 
     * @param dataiIntegrationObject 对象信息
     * @return 对象信息集合
     */
    public List<DataiIntegrationObject> selectDataiIntegrationObjectList(DataiIntegrationObject dataiIntegrationObject);

    /**
     * 新增对象信息
     * 
     * @param dataiIntegrationObject 对象信息
     * @return 结果
     */
    public int insertDataiIntegrationObject(DataiIntegrationObject dataiIntegrationObject);

    /**
     * 修改对象信息
     * 
     * @param dataiIntegrationObject 对象信息
     * @return 结果
     */
    public int updateDataiIntegrationObject(DataiIntegrationObject dataiIntegrationObject);

    /**
     * 删除对象信息
     * 
     * @param id 对象信息主键
     * @return 结果
     */
    public int deleteDataiIntegrationObjectById(Long id);

    /**
     * 批量删除对象信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataiIntegrationObjectByIds(Long[] ids);
}
