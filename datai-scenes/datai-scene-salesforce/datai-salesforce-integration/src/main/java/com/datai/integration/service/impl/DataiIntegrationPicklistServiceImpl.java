package com.datai.integration.service.impl;

import java.time.LocalDateTime;
import java.util.List;
        import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.integration.mapper.DataiIntegrationPicklistMapper;
import com.datai.integration.domain.DataiIntegrationPicklist;
import com.datai.integration.service.IDataiIntegrationPicklistService;
import com.datai.common.core.domain.model.LoginUser;


/**
 * 字段选择列表信息Service业务层处理
 *
 * @author datai
 * @date 2025-12-24
 */
@Service
public class DataiIntegrationPicklistServiceImpl implements IDataiIntegrationPicklistService {
    @Autowired
    private DataiIntegrationPicklistMapper dataiIntegrationPicklistMapper;

    /**
     * 查询字段选择列表信息
     *
     * @param id 字段选择列表信息主键
     * @return 字段选择列表信息
     */
    @Override
    public DataiIntegrationPicklist selectDataiIntegrationPicklistById(Integer id)
    {
        return dataiIntegrationPicklistMapper.selectDataiIntegrationPicklistById(id);
    }

    /**
     * 查询字段选择列表信息列表
     *
     * @param dataiIntegrationPicklist 字段选择列表信息
     * @return 字段选择列表信息
     */
    @Override
    public List<DataiIntegrationPicklist> selectDataiIntegrationPicklistList(DataiIntegrationPicklist dataiIntegrationPicklist)
    {
        return dataiIntegrationPicklistMapper.selectDataiIntegrationPicklistList(dataiIntegrationPicklist);
    }

    /**
     * 新增字段选择列表信息
     *
     * @param dataiIntegrationPicklist 字段选择列表信息
     * @return 结果
     */
    @Override
    public int insertDataiIntegrationPicklist(DataiIntegrationPicklist dataiIntegrationPicklist)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationPicklist.setCreateTime(DateUtils.getNowDate());
                dataiIntegrationPicklist.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationPicklist.setCreateBy(username);
                dataiIntegrationPicklist.setUpdateBy(username);
            return dataiIntegrationPicklistMapper.insertDataiIntegrationPicklist(dataiIntegrationPicklist);
    }

    /**
     * 修改字段选择列表信息
     *
     * @param dataiIntegrationPicklist 字段选择列表信息
     * @return 结果
     */
    @Override
    public int updateDataiIntegrationPicklist(DataiIntegrationPicklist dataiIntegrationPicklist)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationPicklist.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationPicklist.setUpdateBy(username);
        return dataiIntegrationPicklistMapper.updateDataiIntegrationPicklist(dataiIntegrationPicklist);
    }

    /**
     * 批量删除字段选择列表信息
     *
     * @param ids 需要删除的字段选择列表信息主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationPicklistByIds(Integer[] ids)
    {
        return dataiIntegrationPicklistMapper.deleteDataiIntegrationPicklistByIds(ids);
    }

    /**
     * 删除字段选择列表信息信息
     *
     * @param id 字段选择列表信息主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationPicklistById(Integer id)
    {
        return dataiIntegrationPicklistMapper.deleteDataiIntegrationPicklistById(id);
    }
}
