package org.dromara.salesforce.service;

import java.util.List;
import com.datai.salesforce.domain.DataPicklist;

/**
 * 字段选项列信息Service接口
 *
 * @author datai
 * @date 2025-11-30
 */
public interface IDataPicklistService
{
    /**
     * 查询字段选项列信息
     *
     * @param id 字段选项列信息主键
     * @return 字段选项列信息
     */
    public DataPicklist selectDataPicklistById(Integer id);

    /**
     * 查询字段选项列信息列表
     *
     * @param dataPicklist 字段选项列信息
     * @return 字段选项列信息集合
     */
    public List<DataPicklist> selectDataPicklistList(DataPicklist dataPicklist);

    /**
     * 新增字段选项列信息
     *
     * @param dataPicklist 字段选项列信息
     * @return 结果
     */
    public int insertDataPicklist(DataPicklist dataPicklist);

    /**
     * 修改字段选项列信息
     *
     * @param dataPicklist 字段选项列信息
     * @return 结果
     */
    public int updateDataPicklist(DataPicklist dataPicklist);

    /**
     * 批量删除字段选项列信息
     *
     * @param ids 需要删除的字段选项列信息主键集合
     * @return 结果
     */
    public int deleteDataPicklistByIds(Integer[] ids);

    /**
     * 删除字段选项列信息信息
     *
     * @param id 字段选项列信息主键
     * @return 结果
     */
    public int deleteDataPicklistById(Integer id);
}
