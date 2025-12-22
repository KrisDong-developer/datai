package com.datai.integration.service.impl;

import java.util.List;

import com.datai.common.core.domain.model.LoginUser;
import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.integration.mapper.DataiIntegrationFilterLookupMapper;
import com.datai.integration.domain.DataiIntegrationFilterLookup;
import com.datai.integration.service.IDataiIntegrationFilterLookupService;


/**
 * 字段过滤查找信息Service业务层处理
 *
 * @author datai
 * @date 2025-12-22
 */
@Service
public class DataiIntegrationFilterLookupServiceImpl implements IDataiIntegrationFilterLookupService {
    @Autowired
    private DataiIntegrationFilterLookupMapper dataiIntegrationFilterLookupMapper;

    /**
     * 查询字段过滤查找信息
     *
     * @param id 字段过滤查找信息主键
     * @return 字段过滤查找信息
     */
    @Override
    public DataiIntegrationFilterLookup selectDataiIntegrationFilterLookupById(Long id)
    {
        return dataiIntegrationFilterLookupMapper.selectDataiIntegrationFilterLookupById(id);
    }

    /**
     * 查询字段过滤查找信息列表
     *
     * @param dataiIntegrationFilterLookup 字段过滤查找信息
     * @return 字段过滤查找信息
     */
    @Override
    public List<DataiIntegrationFilterLookup> selectDataiIntegrationFilterLookupList(DataiIntegrationFilterLookup dataiIntegrationFilterLookup)
    {
        return dataiIntegrationFilterLookupMapper.selectDataiIntegrationFilterLookupList(dataiIntegrationFilterLookup);
    }

    /**
     * 新增字段过滤查找信息
     *
     * @param dataiIntegrationFilterLookup 字段过滤查找信息
     * @return 结果
     */
    @Override
    public int insertDataiIntegrationFilterLookup(DataiIntegrationFilterLookup dataiIntegrationFilterLookup)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationFilterLookup.setCreateTime(DateUtils.getNowDate());
                dataiIntegrationFilterLookup.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationFilterLookup.setCreateBy(username);
                dataiIntegrationFilterLookup.setUpdateBy(username);
            return dataiIntegrationFilterLookupMapper.insertDataiIntegrationFilterLookup(dataiIntegrationFilterLookup);
    }

    /**
     * 修改字段过滤查找信息
     *
     * @param dataiIntegrationFilterLookup 字段过滤查找信息
     * @return 结果
     */
    @Override
    public int updateDataiIntegrationFilterLookup(DataiIntegrationFilterLookup dataiIntegrationFilterLookup)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationFilterLookup.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationFilterLookup.setUpdateBy(username);
        return dataiIntegrationFilterLookupMapper.updateDataiIntegrationFilterLookup(dataiIntegrationFilterLookup);
    }

    /**
     * 批量删除字段过滤查找信息
     *
     * @param ids 需要删除的字段过滤查找信息主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationFilterLookupByIds(Long[] ids)
    {
        return dataiIntegrationFilterLookupMapper.deleteDataiIntegrationFilterLookupByIds(ids);
    }

    /**
     * 删除字段过滤查找信息信息
     *
     * @param id 字段过滤查找信息主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationFilterLookupById(Long id)
    {
        return dataiIntegrationFilterLookupMapper.deleteDataiIntegrationFilterLookupById(id);
    }
}
