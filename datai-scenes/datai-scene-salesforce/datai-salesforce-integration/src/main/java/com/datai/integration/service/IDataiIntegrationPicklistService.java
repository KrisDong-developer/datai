package com.datai.integration.service;

import java.util.List;
import com.datai.integration.domain.DataiIntegrationPicklist;

/**
 * 字段选择列信息Service接口
 * 
 * @author datai
 * @date 2025-12-22
 */
public interface IDataiIntegrationPicklistService 
{
    /**
     * 查询字段选择列信息
     * 
     * @param id 字段选择列信息主键
     * @return 字段选择列信息
     */
    public DataiIntegrationPicklist selectDataiIntegrationPicklistById(Long id);

    /**
     * 查询字段选择列信息列表
     * 
     * @param dataiIntegrationPicklist 字段选择列信息
     * @return 字段选择列信息集合
     */
    public List<DataiIntegrationPicklist> selectDataiIntegrationPicklistList(DataiIntegrationPicklist dataiIntegrationPicklist);

    /**
     * 新增字段选择列信息
     * 
     * @param dataiIntegrationPicklist 字段选择列信息
     * @return 结果
     */
    public int insertDataiIntegrationPicklist(DataiIntegrationPicklist dataiIntegrationPicklist);

    /**
     * 修改字段选择列信息
     * 
     * @param dataiIntegrationPicklist 字段选择列信息
     * @return 结果
     */
    public int updateDataiIntegrationPicklist(DataiIntegrationPicklist dataiIntegrationPicklist);

    /**
     * 批量删除字段选择列信息
     * 
     * @param ids 需要删除的字段选择列信息主键集合
     * @return 结果
     */
    public int deleteDataiIntegrationPicklistByIds(Long[] ids);

    /**
     * 删除字段选择列信息信息
     * 
     * @param id 字段选择列信息主键
     * @return 结果
     */
    public int deleteDataiIntegrationPicklistById(Long id);
}
