package com.datai.integration.service.impl;

import java.util.List;
        import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.integration.mapper.DataiIntegrationApiCallLogMapper;
import com.datai.integration.domain.DataiIntegrationApiCallLog;
import com.datai.integration.service.IDataiIntegrationApiCallLogService;
import com.datai.common.core.domain.model.LoginUser;

/**
 * API调用日志Service业务层处理
 *
 * @author datai
 * @date 2025-12-28
 */
@Service
public class DataiIntegrationApiCallLogServiceImpl implements IDataiIntegrationApiCallLogService {
    @Autowired
    private DataiIntegrationApiCallLogMapper dataiIntegrationApiCallLogMapper;

    /**
     * 查询API调用日志
     *
     * @param id API调用日志主键
     * @return API调用日志
     */
    @Override
    public DataiIntegrationApiCallLog selectDataiIntegrationApiCallLogById(Long id)
    {
        return dataiIntegrationApiCallLogMapper.selectDataiIntegrationApiCallLogById(id);
    }

    /**
     * 查询API调用日志列表
     *
     * @param dataiIntegrationApiCallLog API调用日志
     * @return API调用日志
     */
    @Override
    public List<DataiIntegrationApiCallLog> selectDataiIntegrationApiCallLogList(DataiIntegrationApiCallLog dataiIntegrationApiCallLog)
    {
        return dataiIntegrationApiCallLogMapper.selectDataiIntegrationApiCallLogList(dataiIntegrationApiCallLog);
    }

    /**
     * 新增API调用日志
     *
     * @param dataiIntegrationApiCallLog API调用日志
     * @return 结果
     */
    @Override
    public int insertDataiIntegrationApiCallLog(DataiIntegrationApiCallLog dataiIntegrationApiCallLog)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationApiCallLog.setCreateTime(DateUtils.getNowDate());
                dataiIntegrationApiCallLog.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationApiCallLog.setCreateBy(username);
                dataiIntegrationApiCallLog.setUpdateBy(username);
            return dataiIntegrationApiCallLogMapper.insertDataiIntegrationApiCallLog(dataiIntegrationApiCallLog);
    }

    /**
     * 修改API调用日志
     *
     * @param dataiIntegrationApiCallLog API调用日志
     * @return 结果
     */
    @Override
    public int updateDataiIntegrationApiCallLog(DataiIntegrationApiCallLog dataiIntegrationApiCallLog)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationApiCallLog.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationApiCallLog.setUpdateBy(username);
        return dataiIntegrationApiCallLogMapper.updateDataiIntegrationApiCallLog(dataiIntegrationApiCallLog);
    }

    /**
     * 批量删除API调用日志
     *
     * @param ids 需要删除的API调用日志主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationApiCallLogByIds(Long[] ids)
    {
        return dataiIntegrationApiCallLogMapper.deleteDataiIntegrationApiCallLogByIds(ids);
    }

    /**
     * 删除API调用日志信息
     *
     * @param id API调用日志主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationApiCallLogById(Long id)
    {
        return dataiIntegrationApiCallLogMapper.deleteDataiIntegrationApiCallLogById(id);
    }
}
