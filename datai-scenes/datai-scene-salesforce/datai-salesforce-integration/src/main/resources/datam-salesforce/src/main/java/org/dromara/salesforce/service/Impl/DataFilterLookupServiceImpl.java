package org.dromara.salesforce.service.Impl;

import java.util.List;
import com.datai.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.salesforce.mapper.DataFilterLookupMapper;
import com.datai.salesforce.domain.DataFilterLookup;
import com.datai.salesforce.service.IDataFilterLookupService;

/**
 * 字段过滤查找信息Service业务层处理
 *
 * @author datai
 * @date 2025-11-30
 */
@Service
public class DataFilterLookupServiceImpl implements IDataFilterLookupService
{
    @Autowired
    private DataFilterLookupMapper dataFilterLookupMapper;

    /**
     * 查询字段过滤查找信息
     *
     * @param id 字段过滤查找信息主键
     * @return 字段过滤查找信息
     */
    @Override
    public DataFilterLookup selectDataFilterLookupById(Integer id)
    {
        return dataFilterLookupMapper.selectDataFilterLookupById(id);
    }

    /**
     * 查询字段过滤查找信息列表
     *
     * @param dataFilterLookup 字段过滤查找信息
     * @return 字段过滤查找信息
     */
    @Override
    public List<DataFilterLookup> selectDataFilterLookupList(DataFilterLookup dataFilterLookup)
    {
        return dataFilterLookupMapper.selectDataFilterLookupList(dataFilterLookup);
    }

    /**
     * 新增字段过滤查找信息
     *
     * @param dataFilterLookup 字段过滤查找信息
     * @return 结果
     */
    @Override
    public int insertDataFilterLookup(DataFilterLookup dataFilterLookup)
    {
        dataFilterLookup.setCreateTime(DateUtils.getNowDate());
        return dataFilterLookupMapper.insertDataFilterLookup(dataFilterLookup);
    }

    /**
     * 修改字段过滤查找信息
     *
     * @param dataFilterLookup 字段过滤查找信息
     * @return 结果
     */
    @Override
    public int updateDataFilterLookup(DataFilterLookup dataFilterLookup)
    {
        dataFilterLookup.setUpdateTime(DateUtils.getNowDate());
        return dataFilterLookupMapper.updateDataFilterLookup(dataFilterLookup);
    }

    /**
     * 批量删除字段过滤查找信息
     *
     * @param ids 需要删除的字段过滤查找信息主键
     * @return 结果
     */
    @Override
    public int deleteDataFilterLookupByIds(Integer[] ids)
    {
        return dataFilterLookupMapper.deleteDataFilterLookupByIds(ids);
    }

    /**
     * 删除字段过滤查找信息信息
     *
     * @param id 字段过滤查找信息主键
     * @return 结果
     */
    @Override
    public int deleteDataFilterLookupById(Integer id)
    {
        return dataFilterLookupMapper.deleteDataFilterLookupById(id);
    }
}
