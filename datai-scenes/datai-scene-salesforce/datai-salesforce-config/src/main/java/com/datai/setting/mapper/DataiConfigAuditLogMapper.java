package com.datai.setting.mapper;

import java.util.List;
import com.datai.setting.domain.DataiConfigAuditLog;

/**
 * 配置审计日志Mapper接口
 * 
 * @author datai
 * @date 2025-12-14
 */
public interface DataiConfigAuditLogMapper 
{
    /**
     * 查询配置审计日志
     * 
     * @param logId 配置审计日志主键
     * @return 配置审计日志
     */
    public DataiConfigAuditLog selectDataiConfigAuditLogByLogId(Long logId);

    /**
     * 查询配置审计日志列表
     * 
     * @param dataiConfigAuditLog 配置审计日志
     * @return 配置审计日志集合
     */
    public List<DataiConfigAuditLog> selectDataiConfigAuditLogList(DataiConfigAuditLog dataiConfigAuditLog);

    /**
     * 新增配置审计日志
     * 
     * @param dataiConfigAuditLog 配置审计日志
     * @return 结果
     */
    public int insertDataiConfigAuditLog(DataiConfigAuditLog dataiConfigAuditLog);

    /**
     * 修改配置审计日志
     * 
     * @param dataiConfigAuditLog 配置审计日志
     * @return 结果
     */
    public int updateDataiConfigAuditLog(DataiConfigAuditLog dataiConfigAuditLog);

    /**
     * 删除配置审计日志
     * 
     * @param logId 配置审计日志主键
     * @return 结果
     */
    public int deleteDataiConfigAuditLogByLogId(Long logId);

    /**
     * 批量删除配置审计日志
     * 
     * @param logIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataiConfigAuditLogByLogIds(Long[] logIds);
}
