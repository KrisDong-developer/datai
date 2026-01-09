package com.datai.integration.service.impl;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.datai.common.utils.DateUtils;
import com.datai.common.utils.SecurityUtils;
import com.datai.integration.model.dto.LogStatisticsDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.integration.mapper.DataiIntegrationSyncLogMapper;
import com.datai.integration.model.domain.DataiIntegrationSyncLog;
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
    private static final Logger log = LoggerFactory.getLogger(DataiIntegrationSyncLogServiceImpl.class);
    
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
        try {
            return dataiIntegrationSyncLogMapper.selectDataiIntegrationSyncLogById(id);
        } catch (Exception e) {
            log.error("查询数据同步日志失败，日志ID: {}", id, e);
            throw e;
        }
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
        try {
            return dataiIntegrationSyncLogMapper.selectDataiIntegrationSyncLogList(dataiIntegrationSyncLog);
        } catch (Exception e) {
            log.error("查询数据同步日志列表失败", e);
            throw e;
        }
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
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            String username = loginUser.getUsername();

                    dataiIntegrationSyncLog.setCreateTime(DateUtils.getNowDate());
                    dataiIntegrationSyncLog.setUpdateTime(DateUtils.getNowDate());
                    dataiIntegrationSyncLog.setCreateBy(username);
                    dataiIntegrationSyncLog.setUpdateBy(username);
                return dataiIntegrationSyncLogMapper.insertDataiIntegrationSyncLog(dataiIntegrationSyncLog);
        } catch (Exception e) {
            log.error("新增数据同步日志失败", e);
            throw e;
        }
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
        try {
            LoginUser loginUser = SecurityUtils.getLoginUser();
            String username = loginUser.getUsername();

                    dataiIntegrationSyncLog.setUpdateTime(DateUtils.getNowDate());
                    dataiIntegrationSyncLog.setUpdateBy(username);
            return dataiIntegrationSyncLogMapper.updateDataiIntegrationSyncLog(dataiIntegrationSyncLog);
        } catch (Exception e) {
            log.error("修改数据同步日志失败，日志ID: {}", dataiIntegrationSyncLog.getId(), e);
            throw e;
        }
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
        try {
            return dataiIntegrationSyncLogMapper.deleteDataiIntegrationSyncLogByIds(ids);
        } catch (Exception e) {
            log.error("批量删除数据同步日志失败", e);
            throw e;
        }
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
        try {
            return dataiIntegrationSyncLogMapper.deleteDataiIntegrationSyncLogById(id);
        } catch (Exception e) {
            log.error("删除数据同步日志失败，日志ID: {}", id, e);
            throw e;
        }
    }

    /**
     * 获取日志统计信息
     *
     * @param params 查询参数
     * @return 日志统计信息
     */
    @Override
    public LogStatisticsDTO getLogStatistics(Map<String, Object> params)
    {
        return dataiIntegrationSyncLogMapper.getLogStatistics(params);
    }
}
