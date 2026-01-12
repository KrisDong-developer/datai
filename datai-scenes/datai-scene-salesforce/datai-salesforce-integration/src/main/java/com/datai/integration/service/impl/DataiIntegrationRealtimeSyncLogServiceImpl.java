package com.datai.integration.service.impl;

import java.util.List;
import com.datai.common.utils.DateUtils;
import com.datai.common.utils.SecurityUtils;
import com.datai.integration.model.domain.DataiIntegrationRealtimeSyncLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.integration.mapper.DataiIntegrationRealtimeSyncLogMapper;
import com.datai.integration.service.IDataiIntegrationRealtimeSyncLogService;
import com.datai.common.core.domain.model.LoginUser;

/**
 * 实时同步日志Service业务层处理
 *
 * @author datai
 * @date 2026-01-09
 */
@Service
public class DataiIntegrationRealtimeSyncLogServiceImpl implements IDataiIntegrationRealtimeSyncLogService {
    @Autowired
    private DataiIntegrationRealtimeSyncLogMapper dataiIntegrationRealtimeSyncLogMapper;

    /**
     * 查询实时同步日志
     *
     * @param id 实时同步日志主键
     * @return 实时同步日志
     */
    @Override
    public DataiIntegrationRealtimeSyncLog selectDataiIntegrationRealtimeSyncLogById(Long id)
    {
        return dataiIntegrationRealtimeSyncLogMapper.selectDataiIntegrationRealtimeSyncLogById(id);
    }

    /**
     * 查询实时同步日志列表
     *
     * @param dataiIntegrationRealtimeSyncLog 实时同步日志
     * @return 实时同步日志
     */
    @Override
    public List<DataiIntegrationRealtimeSyncLog> selectDataiIntegrationRealtimeSyncLogList(DataiIntegrationRealtimeSyncLog dataiIntegrationRealtimeSyncLog)
    {
        return dataiIntegrationRealtimeSyncLogMapper.selectDataiIntegrationRealtimeSyncLogList(dataiIntegrationRealtimeSyncLog);
    }

    /**
     * 新增实时同步日志
     *
     * @param dataiIntegrationRealtimeSyncLog 实时同步日志
     * @return 结果
     */
    @Override
    public int insertDataiIntegrationRealtimeSyncLog(DataiIntegrationRealtimeSyncLog dataiIntegrationRealtimeSyncLog)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationRealtimeSyncLog.setCreateTime(DateUtils.getNowDate());
                dataiIntegrationRealtimeSyncLog.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationRealtimeSyncLog.setCreateBy(username);
                dataiIntegrationRealtimeSyncLog.setUpdateBy(username);
            return dataiIntegrationRealtimeSyncLogMapper.insertDataiIntegrationRealtimeSyncLog(dataiIntegrationRealtimeSyncLog);
    }

    /**
     * 修改实时同步日志
     *
     * @param dataiIntegrationRealtimeSyncLog 实时同步日志
     * @return 结果
     */
    @Override
    public int updateDataiIntegrationRealtimeSyncLog(DataiIntegrationRealtimeSyncLog dataiIntegrationRealtimeSyncLog)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationRealtimeSyncLog.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationRealtimeSyncLog.setUpdateBy(username);
        return dataiIntegrationRealtimeSyncLogMapper.updateDataiIntegrationRealtimeSyncLog(dataiIntegrationRealtimeSyncLog);
    }

    /**
     * 批量删除实时同步日志
     *
     * @param ids 需要删除的实时同步日志主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationRealtimeSyncLogByIds(Long[] ids)
    {
        return dataiIntegrationRealtimeSyncLogMapper.deleteDataiIntegrationRealtimeSyncLogByIds(ids);
    }

    /**
     * 删除实时同步日志信息
     *
     * @param id 实时同步日志主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationRealtimeSyncLogById(Long id)
    {
        return dataiIntegrationRealtimeSyncLogMapper.deleteDataiIntegrationRealtimeSyncLogById(id);
    }
}
