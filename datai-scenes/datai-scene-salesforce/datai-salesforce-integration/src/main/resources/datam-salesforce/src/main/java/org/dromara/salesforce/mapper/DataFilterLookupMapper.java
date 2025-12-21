package org.dromara.salesforce.mapper;

import java.util.List;
import com.datai.salesforce.domain.DataFilterLookup;

/**
 * 字段过滤查找信息Mapper接口
 *
 * @author datai
 * @date 2025-11-30
 */
public interface DataFilterLookupMapper
{
    /**
     * 查询字段过滤查找信息
     *
     * @param id 字段过滤查找信息主键
     * @return 字段过滤查找信息
     */
    public DataFilterLookup selectDataFilterLookupById(Integer id);

    /**
     * 查询字段过滤查找信息列表
     *
     * @param dataFilterLookup 字段过滤查找信息
     * @return 字段过滤查找信息集合
     */
    public List<DataFilterLookup> selectDataFilterLookupList(DataFilterLookup dataFilterLookup);

    /**
     * 新增字段过滤查找信息
     *
     * @param dataFilterLookup 字段过滤查找信息
     * @return 结果
     */
    public int insertDataFilterLookup(DataFilterLookup dataFilterLookup);

    /**
     * 修改字段过滤查找信息
     *
     * @param dataFilterLookup 字段过滤查找信息
     * @return 结果
     */
    public int updateDataFilterLookup(DataFilterLookup dataFilterLookup);

    /**
     * 删除字段过滤查找信息
     *
     * @param id 字段过滤查找信息主键
     * @return 结果
     */
    public int deleteDataFilterLookupById(Integer id);

    /**
     * 批量删除字段过滤查找信息
     *
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataFilterLookupByIds(Integer[] ids);
}
