package com.datai.setting.service.impl;

import java.util.List;
        import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.setting.mapper.DataiConfigAuditLogMapper;
import com.datai.setting.domain.DataiConfigAuditLog;
import com.datai.setting.service.IDataiConfigAuditLogService;
import com.datai.common.core.domain.model.LoginUser;


/**
 * 配置审计日志Service业务层处理
 *
 * @author datai
 * @date 2025-12-24
 */
@Service
public class DataiConfigAuditLogServiceImpl implements IDataiConfigAuditLogService {
    @Autowired
    private DataiConfigAuditLogMapper dataiConfigAuditLogMapper;

    /**
     * 查询配置审计日志
     *
     * @param id 配置审计日志主键
     * @return 配置审计日志
     */
    @Override
    public DataiConfigAuditLog selectDataiConfigAuditLogById(Long id)
    {
        return dataiConfigAuditLogMapper.selectDataiConfigAuditLogById(id);
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

                dataiConfigAuditLog.setCreateTime(DateUtils.getNowDate());
                dataiConfigAuditLog.setUpdateTime(DateUtils.getNowDate());
                dataiConfigAuditLog.setCreateBy(username);
                dataiConfigAuditLog.setUpdateBy(username);
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

                dataiConfigAuditLog.setUpdateTime(DateUtils.getNowDate());
                dataiConfigAuditLog.setUpdateBy(username);
        return dataiConfigAuditLogMapper.updateDataiConfigAuditLog(dataiConfigAuditLog);
    }

    /**
     * 批量删除配置审计日志
     *
     * @param ids 需要删除的配置审计日志主键
     * @return 结果
     */
    @Override
    public int deleteDataiConfigAuditLogByIds(Long[] ids)
    {
        return dataiConfigAuditLogMapper.deleteDataiConfigAuditLogByIds(ids);
    }

    /**
     * 删除配置审计日志信息
     *
     * @param id 配置审计日志主键
     * @return 结果
     */
    @Override
    public int deleteDataiConfigAuditLogById(Long id)
    {
        return dataiConfigAuditLogMapper.deleteDataiConfigAuditLogById(id);
    }
}
