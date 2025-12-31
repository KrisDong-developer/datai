package com.datai.integration.mapper;

import java.util.List;
import com.datai.integration.model.domain.DataiIntegrationPicklist;

/**
 * 字段选择列表信息Mapper接口
 * 
 * @author datai
 * @date 2025-12-24
 */
public interface DataiIntegrationPicklistMapper 
{
    /**
     * 查询字段选择列表信息
     * 
     * @param id 字段选择列表信息主键
     * @return 字段选择列表信息
     */
    public DataiIntegrationPicklist selectDataiIntegrationPicklistById(Integer id);

    /**
     * 查询字段选择列表信息列表
     * 
     * @param dataiIntegrationPicklist 字段选择列表信息
     * @return 字段选择列表信息集合
     */
    public List<DataiIntegrationPicklist> selectDataiIntegrationPicklistList(DataiIntegrationPicklist dataiIntegrationPicklist);

    /**
     * 新增字段选择列表信息
     * 
     * @param dataiIntegrationPicklist 字段选择列表信息
     * @return 结果
     */
    public int insertDataiIntegrationPicklist(DataiIntegrationPicklist dataiIntegrationPicklist);

    /**
     * 修改字段选择列表信息
     * 
     * @param dataiIntegrationPicklist 字段选择列表信息
     * @return 结果
     */
    public int updateDataiIntegrationPicklist(DataiIntegrationPicklist dataiIntegrationPicklist);

    /**
     * 删除字段选择列表信息
     * 
     * @param id 字段选择列表信息主键
     * @return 结果
     */
    public int deleteDataiIntegrationPicklistById(Integer id);

    /**
     * 批量删除字段选择列表信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataiIntegrationPicklistByIds(Integer[] ids);
}
