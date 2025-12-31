package com.datai.integration.mapper;

import java.util.List;
import com.datai.integration.model.domain.DataiIntegrationFilterLookup;

/**
 * 字段过滤查找信息Mapper接口
 * 
 * @author datai
 * @date 2025-12-24
 */
public interface DataiIntegrationFilterLookupMapper 
{
    /**
     * 查询字段过滤查找信息
     * 
     * @param id 字段过滤查找信息主键
     * @return 字段过滤查找信息
     */
    public DataiIntegrationFilterLookup selectDataiIntegrationFilterLookupById(Integer id);

    /**
     * 查询字段过滤查找信息列表
     * 
     * @param dataiIntegrationFilterLookup 字段过滤查找信息
     * @return 字段过滤查找信息集合
     */
    public List<DataiIntegrationFilterLookup> selectDataiIntegrationFilterLookupList(DataiIntegrationFilterLookup dataiIntegrationFilterLookup);

    /**
     * 新增字段过滤查找信息
     * 
     * @param dataiIntegrationFilterLookup 字段过滤查找信息
     * @return 结果
     */
    public int insertDataiIntegrationFilterLookup(DataiIntegrationFilterLookup dataiIntegrationFilterLookup);

    /**
     * 修改字段过滤查找信息
     * 
     * @param dataiIntegrationFilterLookup 字段过滤查找信息
     * @return 结果
     */
    public int updateDataiIntegrationFilterLookup(DataiIntegrationFilterLookup dataiIntegrationFilterLookup);

    /**
     * 删除字段过滤查找信息
     * 
     * @param id 字段过滤查找信息主键
     * @return 结果
     */
    public int deleteDataiIntegrationFilterLookupById(Integer id);

    /**
     * 批量删除字段过滤查找信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataiIntegrationFilterLookupByIds(Integer[] ids);
}
