package com.datai.integration.service.impl;

import java.util.List;
        import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.integration.mapper.DataiIntegrationSyncLogMapper;
import com.datai.integration.domain.DataiIntegrationSyncLog;
import com.datai.integration.service.IDataiIntegrationSyncLogService;
import com.datai.common.core.domain.model.LoginUser;

/**
 * 数据同步日志Service业务层处理
 *
 * @author datai
 * @date 2025-12-26
 */
@Service
public class DataiIntegrationSyncLogServiceImpl implements IDataiIntegrationSyncLogService {
    @Autowired
    private DataiIntegrationSyncLogMapper dataiIntegrationSyncLogMapper;

    /**
     * 查询数据同步日志
     *
     * @param id 数据同步日志主键
     * @return 数据同步日志
     */
    @Override
    public DataiIntegrationSyncLog selectDataiIntegrationSyncLogById(Long id)
    {
        return dataiIntegrationSyncLogMapper.selectDataiIntegrationSyncLogById(id);
    }

    /**
     * 查询数据同步日志列表
     *
     * @param dataiIntegrationSyncLog 数据同步日志
     * @return 数据同步日志
     */
    @Override
    public List<DataiIntegrationSyncLog> selectDataiIntegrationSyncLogList(DataiIntegrationSyncLog dataiIntegrationSyncLog)
    {
        return dataiIntegrationSyncLogMapper.selectDataiIntegrationSyncLogList(dataiIntegrationSyncLog);
    }

    /**
     * 新增数据同步日志
     *
     * @param dataiIntegrationSyncLog 数据同步日志
     * @return 结果
     */
    @Override
    public int insertDataiIntegrationSyncLog(DataiIntegrationSyncLog dataiIntegrationSyncLog)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationSyncLog.setCreateTime(DateUtils.getNowDate());
                dataiIntegrationSyncLog.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationSyncLog.setCreateBy(username);
                dataiIntegrationSyncLog.setUpdateBy(username);
            return dataiIntegrationSyncLogMapper.insertDataiIntegrationSyncLog(dataiIntegrationSyncLog);
    }

    /**
     * 修改数据同步日志
     *
     * @param dataiIntegrationSyncLog 数据同步日志
     * @return 结果
     */
    @Override
    public int updateDataiIntegrationSyncLog(DataiIntegrationSyncLog dataiIntegrationSyncLog)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationSyncLog.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationSyncLog.setUpdateBy(username);
        return dataiIntegrationSyncLogMapper.updateDataiIntegrationSyncLog(dataiIntegrationSyncLog);
    }

    /**
     * 批量删除数据同步日志
     *
     * @param ids 需要删除的数据同步日志主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationSyncLogByIds(Long[] ids)
    {
        return dataiIntegrationSyncLogMapper.deleteDataiIntegrationSyncLogByIds(ids);
    }

    /**
     * 删除数据同步日志信息
     *
     * @param id 数据同步日志主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationSyncLogById(Long id)
    {
        return dataiIntegrationSyncLogMapper.deleteDataiIntegrationSyncLogById(id);
    }
}
