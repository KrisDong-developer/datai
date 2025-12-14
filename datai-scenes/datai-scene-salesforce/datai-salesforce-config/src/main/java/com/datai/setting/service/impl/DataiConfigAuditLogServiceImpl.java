package com.datai.setting.service.impl;

import java.util.List;

import com.datai.common.core.domain.model.LoginUser;
import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.setting.mapper.DataiConfigAuditLogMapper;
import com.datai.setting.domain.DataiConfigAuditLog;
import com.datai.setting.service.IDataiConfigAuditLogService;


/**
 * 配置审计日志Service业务层处理
 *
 * @author datai
 * @date 2025-12-14
 */
@Service
public class DataiConfigAuditLogServiceImpl implements IDataiConfigAuditLogService {
    @Autowired
    private DataiConfigAuditLogMapper dataiConfigAuditLogMapper;

    /**
     * 查询配置审计日志
     *
     * @param logId 配置审计日志主键
     * @return 配置审计日志
     */
    @Override
    public DataiConfigAuditLog selectDataiConfigAuditLogByLogId(Long logId)
    {
        return dataiConfigAuditLogMapper.selectDataiConfigAuditLogByLogId(logId);
    }

    /**
     * 查询配置审计日志列表
     *
     * @param dataiConfigAuditLog 配置审计日志
     * @return 配置审计日志
     */
    @Override
    public List<DataiConfigAuditLog> selectDataiConfigAuditLogList(DataiConfigAuditLog dataiConfigAuditLog)
    {
        return dataiConfigAuditLogMapper.selectDataiConfigAuditLogList(dataiConfigAuditLog);
    }

    /**
     * 新增配置审计日志
     *
     * @param dataiConfigAuditLog 配置审计日志
     * @return 结果
     */
    @Override
    public int insertDataiConfigAuditLog(DataiConfigAuditLog dataiConfigAuditLog)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

            return dataiConfigAuditLogMapper.insertDataiConfigAuditLog(dataiConfigAuditLog);
    }

    /**
     * 修改配置审计日志
     *
     * @param dataiConfigAuditLog 配置审计日志
     * @return 结果
     */
    @Override
    public int updateDataiConfigAuditLog(DataiConfigAuditLog dataiConfigAuditLog)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

        return dataiConfigAuditLogMapper.updateDataiConfigAuditLog(dataiConfigAuditLog);
    }

    /**
     * 批量删除配置审计日志
     *
     * @param logIds 需要删除的配置审计日志主键
     * @return 结果
     */
    @Override
    public int deleteDataiConfigAuditLogByLogIds(Long[] logIds)
    {
        return dataiConfigAuditLogMapper.deleteDataiConfigAuditLogByLogIds(logIds);
    }

    /**
     * 删除配置审计日志信息
     *
     * @param logId 配置审计日志主键
     * @return 结果
     */
    @Override
    public int deleteDataiConfigAuditLogByLogId(Long logId)
    {
        return dataiConfigAuditLogMapper.deleteDataiConfigAuditLogByLogId(logId);
    }
}
