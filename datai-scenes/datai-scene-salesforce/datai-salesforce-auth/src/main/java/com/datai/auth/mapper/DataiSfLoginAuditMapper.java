package com.datai.auth.mapper;

import java.util.List;
import com.datai.auth.domain.DataiSfLoginAudit;

/**
 * 登录审计日志Mapper接口
 * 
 * @author datai
 * @date 2025-12-14
 */
public interface DataiSfLoginAuditMapper 
{
    /**
     * 查询登录审计日志
     * 
     * @param auditId 登录审计日志主键
     * @return 登录审计日志
     */
    public DataiSfLoginAudit selectDataiSfLoginAuditByAuditId(Long auditId);

    /**
     * 查询登录审计日志列表
     * 
     * @param dataiSfLoginAudit 登录审计日志
     * @return 登录审计日志集合
     */
    public List<DataiSfLoginAudit> selectDataiSfLoginAuditList(DataiSfLoginAudit dataiSfLoginAudit);

    /**
     * 新增登录审计日志
     * 
     * @param dataiSfLoginAudit 登录审计日志
     * @return 结果
     */
    public int insertDataiSfLoginAudit(DataiSfLoginAudit dataiSfLoginAudit);

    /**
     * 修改登录审计日志
     * 
     * @param dataiSfLoginAudit 登录审计日志
     * @return 结果
     */
    public int updateDataiSfLoginAudit(DataiSfLoginAudit dataiSfLoginAudit);

    /**
     * 删除登录审计日志
     * 
     * @param auditId 登录审计日志主键
     * @return 结果
     */
    public int deleteDataiSfLoginAuditByAuditId(Long auditId);

    /**
     * 批量删除登录审计日志
     * 
     * @param auditIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataiSfLoginAuditByAuditIds(Long[] auditIds);
}
