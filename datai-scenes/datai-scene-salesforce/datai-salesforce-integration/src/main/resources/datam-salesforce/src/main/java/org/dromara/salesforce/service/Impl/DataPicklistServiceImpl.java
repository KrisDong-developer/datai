package org.dromara.salesforce.service.Impl;

import java.util.List;
import com.datai.common.utils.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.salesforce.mapper.DataPicklistMapper;
import com.datai.salesforce.domain.DataPicklist;
import com.datai.salesforce.service.IDataPicklistService;

/**
 * 字段选项列信息Service业务层处理
 *
 * @author datai
 * @date 2025-11-30
 */
@Service
public class DataPicklistServiceImpl implements IDataPicklistService
{
    @Autowired
    private DataPicklistMapper dataPicklistMapper;

    /**
     * 查询字段选项列信息
     *
     * @param id 字段选项列信息主键
     * @return 字段选项列信息
     */
    @Override
    public DataPicklist selectDataPicklistById(Integer id)
    {
        return dataPicklistMapper.selectDataPicklistById(id);
    }

    /**
     * 查询字段选项列信息列表
     *
     * @param dataPicklist 字段选项列信息
     * @return 字段选项列信息
     */
    @Override
    public List<DataPicklist> selectDataPicklistList(DataPicklist dataPicklist)
    {
        return dataPicklistMapper.selectDataPicklistList(dataPicklist);
    }

    /**
     * 新增字段选项列信息
     *
     * @param dataPicklist 字段选项列信息
     * @return 结果
     */
    @Override
    public int insertDataPicklist(DataPicklist dataPicklist)
    {
        dataPicklist.setCreateTime(DateUtils.getNowDate());
        return dataPicklistMapper.insertDataPicklist(dataPicklist);
    }

    /**
     * 修改字段选项列信息
     *
     * @param dataPicklist 字段选项列信息
     * @return 结果
     */
    @Override
    public int updateDataPicklist(DataPicklist dataPicklist)
    {
        dataPicklist.setUpdateTime(DateUtils.getNowDate());
        return dataPicklistMapper.updateDataPicklist(dataPicklist);
    }

    /**
     * 批量删除字段选项列信息
     *
     * @param ids 需要删除的字段选项列信息主键
     * @return 结果
     */
    @Override
    public int deleteDataPicklistByIds(Integer[] ids)
    {
        return dataPicklistMapper.deleteDataPicklistByIds(ids);
    }

    /**
     * 删除字段选项列信息信息
     *
     * @param id 字段选项列信息主键
     * @return 结果
     */
    @Override
    public int deleteDataPicklistById(Integer id)
    {
        return dataPicklistMapper.deleteDataPicklistById(id);
    }
}
