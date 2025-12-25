package com.datai.auth.service;

import java.util.List;
import com.datai.auth.domain.DataiSfLoginAudit;

/**
 * 登录审计日志Service接口
 * 
 * @author datai
 * @date 2025-12-24
 */
public interface IDataiSfLoginAuditService 
{
    /**
     * 查询登录审计日志
     * 
     * @param id 登录审计日志主键
     * @return 登录审计日志
     */
    public DataiSfLoginAudit selectDataiSfLoginAuditById(Long id);

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
     * 批量删除登录审计日志
     * 
     * @param ids 需要删除的登录审计日志主键集合
     * @return 结果
     */
    public int deleteDataiSfLoginAuditByIds(Long[] ids);

    /**
     * 删除登录审计日志信息
     * 
     * @param id 登录审计日志主键
     * @return 结果
     */
    public int deleteDataiSfLoginAuditById(Long id);
}
