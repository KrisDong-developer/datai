package com.datai.integration.service.impl;

import java.util.List;

import com.datai.common.core.domain.model.LoginUser;
import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.integration.mapper.DataiIntegrationObjectMapper;
import com.datai.integration.domain.DataiIntegrationObject;
import com.datai.integration.service.IDataiIntegrationObjectService;


/**
 * 对象信息Service业务层处理
 *
 * @author datai
 * @date 2025-12-22
 */
@Service
public class DataiIntegrationObjectServiceImpl implements IDataiIntegrationObjectService {
    @Autowired
    private DataiIntegrationObjectMapper dataiIntegrationObjectMapper;

    /**
     * 查询对象信息
     *
     * @param id 对象信息主键
     * @return 对象信息
     */
    @Override
    public DataiIntegrationObject selectDataiIntegrationObjectById(Long id)
    {
        return dataiIntegrationObjectMapper.selectDataiIntegrationObjectById(id);
    }

    /**
     * 查询对象信息列表
     *
     * @param dataiIntegrationObject 对象信息
     * @return 对象信息
     */
    @Override
    public List<DataiIntegrationObject> selectDataiIntegrationObjectList(DataiIntegrationObject dataiIntegrationObject)
    {
        return dataiIntegrationObjectMapper.selectDataiIntegrationObjectList(dataiIntegrationObject);
    }

    /**
     * 新增对象信息
     *
     * @param dataiIntegrationObject 对象信息
     * @return 结果
     */
    @Override
    public int insertDataiIntegrationObject(DataiIntegrationObject dataiIntegrationObject)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationObject.setCreateTime(DateUtils.getNowDate());
                dataiIntegrationObject.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationObject.setCreateBy(username);
                dataiIntegrationObject.setUpdateBy(username);
            return dataiIntegrationObjectMapper.insertDataiIntegrationObject(dataiIntegrationObject);
    }

    /**
     * 修改对象信息
     *
     * @param dataiIntegrationObject 对象信息
     * @return 结果
     */
    @Override
    public int updateDataiIntegrationObject(DataiIntegrationObject dataiIntegrationObject)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationObject.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationObject.setUpdateBy(username);
        return dataiIntegrationObjectMapper.updateDataiIntegrationObject(dataiIntegrationObject);
    }

    /**
     * 批量删除对象信息
     *
     * @param ids 需要删除的对象信息主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationObjectByIds(Long[] ids)
    {
        return dataiIntegrationObjectMapper.deleteDataiIntegrationObjectByIds(ids);
    }

    /**
     * 删除对象信息信息
     *
     * @param id 对象信息主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationObjectById(Long id)
    {
        return dataiIntegrationObjectMapper.deleteDataiIntegrationObjectById(id);
    }
}
