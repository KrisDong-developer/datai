package com.datai.integration.service.impl;

import java.util.List;

import com.datai.common.core.domain.model.LoginUser;
import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.integration.mapper.DataiIntegrationFieldMapper;
import com.datai.integration.domain.DataiIntegrationField;
import com.datai.integration.service.IDataiIntegrationFieldService;


/**
 * 对象字段信息Service业务层处理
 *
 * @author datai
 * @date 2025-12-22
 */
@Service
public class DataiIntegrationFieldServiceImpl implements IDataiIntegrationFieldService {
    @Autowired
    private DataiIntegrationFieldMapper dataiIntegrationFieldMapper;

    /**
     * 查询对象字段信息
     *
     * @param id 对象字段信息主键
     * @return 对象字段信息
     */
    @Override
    public DataiIntegrationField selectDataiIntegrationFieldById(Long id)
    {
        return dataiIntegrationFieldMapper.selectDataiIntegrationFieldById(id);
    }

    /**
     * 查询对象字段信息列表
     *
     * @param dataiIntegrationField 对象字段信息
     * @return 对象字段信息
     */
    @Override
    public List<DataiIntegrationField> selectDataiIntegrationFieldList(DataiIntegrationField dataiIntegrationField)
    {
        return dataiIntegrationFieldMapper.selectDataiIntegrationFieldList(dataiIntegrationField);
    }

    /**
     * 新增对象字段信息
     *
     * @param dataiIntegrationField 对象字段信息
     * @return 结果
     */
    @Override
    public int insertDataiIntegrationField(DataiIntegrationField dataiIntegrationField)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationField.setCreateTime(DateUtils.getNowDate());
                dataiIntegrationField.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationField.setCreateBy(username);
                dataiIntegrationField.setUpdateBy(username);
            return dataiIntegrationFieldMapper.insertDataiIntegrationField(dataiIntegrationField);
    }

    /**
     * 修改对象字段信息
     *
     * @param dataiIntegrationField 对象字段信息
     * @return 结果
     */
    @Override
    public int updateDataiIntegrationField(DataiIntegrationField dataiIntegrationField)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationField.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationField.setUpdateBy(username);
        return dataiIntegrationFieldMapper.updateDataiIntegrationField(dataiIntegrationField);
    }

    /**
     * 批量删除对象字段信息
     *
     * @param ids 需要删除的对象字段信息主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationFieldByIds(Long[] ids)
    {
        return dataiIntegrationFieldMapper.deleteDataiIntegrationFieldByIds(ids);
    }

    /**
     * 删除对象字段信息信息
     *
     * @param id 对象字段信息主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationFieldById(Long id)
    {
        return dataiIntegrationFieldMapper.deleteDataiIntegrationFieldById(id);
    }
}
