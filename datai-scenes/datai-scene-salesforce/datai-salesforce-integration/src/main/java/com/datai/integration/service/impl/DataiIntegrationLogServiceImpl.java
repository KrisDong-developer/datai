package com.datai.integration.service.impl;

import java.util.List;

import com.datai.common.core.domain.model.LoginUser;
import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.integration.mapper.DataiIntegrationLogMapper;
import com.datai.integration.domain.DataiIntegrationLog;
import com.datai.integration.service.IDataiIntegrationLogService;


/**
 * 数据同步日志Service业务层处理
 *
 * @author datai
 * @date 2025-12-22
 */
@Service
public class DataiIntegrationLogServiceImpl implements IDataiIntegrationLogService {
    @Autowired
    private DataiIntegrationLogMapper dataiIntegrationLogMapper;

    /**
     * 查询数据同步日志
     *
     * @param id 数据同步日志主键
     * @return 数据同步日志
     */
    @Override
    public DataiIntegrationLog selectDataiIntegrationLogById(Long id)
    {
        return dataiIntegrationLogMapper.selectDataiIntegrationLogById(id);
    }

    /**
     * 查询数据同步日志列表
     *
     * @param dataiIntegrationLog 数据同步日志
     * @return 数据同步日志
     */
    @Override
    public List<DataiIntegrationLog> selectDataiIntegrationLogList(DataiIntegrationLog dataiIntegrationLog)
    {
        return dataiIntegrationLogMapper.selectDataiIntegrationLogList(dataiIntegrationLog);
    }

    /**
     * 新增数据同步日志
     *
     * @param dataiIntegrationLog 数据同步日志
     * @return 结果
     */
    @Override
    public int insertDataiIntegrationLog(DataiIntegrationLog dataiIntegrationLog)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationLog.setCreateTime(DateUtils.getNowDate());
                dataiIntegrationLog.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationLog.setCreateBy(username);
                dataiIntegrationLog.setUpdateBy(username);
            return dataiIntegrationLogMapper.insertDataiIntegrationLog(dataiIntegrationLog);
    }

    /**
     * 修改数据同步日志
     *
     * @param dataiIntegrationLog 数据同步日志
     * @return 结果
     */
    @Override
    public int updateDataiIntegrationLog(DataiIntegrationLog dataiIntegrationLog)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

        return dataiIntegrationLogMapper.updateDataiIntegrationLog(dataiIntegrationLog);
    }

    /**
     * 批量删除数据同步日志
     *
     * @param ids 需要删除的数据同步日志主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationLogByIds(Long[] ids)
    {
        return dataiIntegrationLogMapper.deleteDataiIntegrationLogByIds(ids);
    }

    /**
     * 删除数据同步日志信息
     *
     * @param id 数据同步日志主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationLogById(Long id)
    {
        return dataiIntegrationLogMapper.deleteDataiIntegrationLogById(id);
    }
}
