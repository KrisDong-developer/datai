package com.datai.integration.service.impl;

import java.util.List;
        import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.integration.mapper.DataiIntegrationObjectMapper;
import com.datai.integration.domain.DataiIntegrationObject;
import com.datai.integration.service.IDataiIntegrationObjectService;
import com.datai.common.core.domain.model.LoginUser;


/**
 * 对象同步控制Service业务层处理
 *
 * @author datai
 * @date 2025-12-24
 */
@Service
public class DataiIntegrationObjectServiceImpl implements IDataiIntegrationObjectService {
    @Autowired
    private DataiIntegrationObjectMapper dataiIntegrationObjectMapper;

    /**
     * 查询对象同步控制
     *
     * @param id 对象同步控制主键
     * @return 对象同步控制
     */
    @Override
    public DataiIntegrationObject selectDataiIntegrationObjectById(Integer id)
    {
        return dataiIntegrationObjectMapper.selectDataiIntegrationObjectById(id);
    }

    /**
     * 查询对象同步控制列表
     *
     * @param dataiIntegrationObject 对象同步控制
     * @return 对象同步控制
     */
    @Override
    public List<DataiIntegrationObject> selectDataiIntegrationObjectList(DataiIntegrationObject dataiIntegrationObject)
    {
        return dataiIntegrationObjectMapper.selectDataiIntegrationObjectList(dataiIntegrationObject);
    }

    /**
     * 新增对象同步控制
     *
     * @param dataiIntegrationObject 对象同步控制
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
     * 修改对象同步控制
     *
     * @param dataiIntegrationObject 对象同步控制
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
     * 批量删除对象同步控制
     *
     * @param ids 需要删除的对象同步控制主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationObjectByIds(Integer[] ids)
    {
        return dataiIntegrationObjectMapper.deleteDataiIntegrationObjectByIds(ids);
    }

    /**
     * 删除对象同步控制信息
     *
     * @param id 对象同步控制主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationObjectById(Integer id)
    {
        return dataiIntegrationObjectMapper.deleteDataiIntegrationObjectById(id);
    }
}
