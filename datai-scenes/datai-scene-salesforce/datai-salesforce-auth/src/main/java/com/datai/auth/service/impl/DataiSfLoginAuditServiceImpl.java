package com.datai.auth.service.impl;

import java.util.List;
        import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.auth.mapper.DataiSfLoginAuditMapper;
import com.datai.auth.domain.DataiSfLoginAudit;
import com.datai.auth.service.IDataiSfLoginAuditService;
import com.datai.common.core.domain.model.LoginUser;


/**
 * 登录审计日志Service业务层处理
 *
 * @author datai
 * @date 2025-12-24
 */
@Service
public class DataiSfLoginAuditServiceImpl implements IDataiSfLoginAuditService {
    @Autowired
    private DataiSfLoginAuditMapper dataiSfLoginAuditMapper;

    /**
     * 查询登录审计日志
     *
     * @param id 登录审计日志主键
     * @return 登录审计日志
     */
    @Override
    public DataiSfLoginAudit selectDataiSfLoginAuditById(Long id)
    {
        return dataiSfLoginAuditMapper.selectDataiSfLoginAuditById(id);
    }

    /**
     * 查询登录审计日志列表
     *
     * @param dataiSfLoginAudit 登录审计日志
     * @return 登录审计日志
     */
    @Override
    public List<DataiSfLoginAudit> selectDataiSfLoginAuditList(DataiSfLoginAudit dataiSfLoginAudit)
    {
        return dataiSfLoginAuditMapper.selectDataiSfLoginAuditList(dataiSfLoginAudit);
    }

    /**
     * 新增登录审计日志
     *
     * @param dataiSfLoginAudit 登录审计日志
     * @return 结果
     */
    @Override
    public int insertDataiSfLoginAudit(DataiSfLoginAudit dataiSfLoginAudit)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiSfLoginAudit.setCreateTime(DateUtils.getNowDate());
                dataiSfLoginAudit.setUpdateTime(DateUtils.getNowDate());
                dataiSfLoginAudit.setCreateBy(username);
                dataiSfLoginAudit.setUpdateBy(username);
            return dataiSfLoginAuditMapper.insertDataiSfLoginAudit(dataiSfLoginAudit);
    }

    /**
     * 修改登录审计日志
     *
     * @param dataiSfLoginAudit 登录审计日志
     * @return 结果
     */
    @Override
    public int updateDataiSfLoginAudit(DataiSfLoginAudit dataiSfLoginAudit)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiSfLoginAudit.setUpdateTime(DateUtils.getNowDate());
                dataiSfLoginAudit.setUpdateBy(username);
        return dataiSfLoginAuditMapper.updateDataiSfLoginAudit(dataiSfLoginAudit);
    }

    /**
     * 批量删除登录审计日志
     *
     * @param ids 需要删除的登录审计日志主键
     * @return 结果
     */
    @Override
    public int deleteDataiSfLoginAuditByIds(Long[] ids)
    {
        return dataiSfLoginAuditMapper.deleteDataiSfLoginAuditByIds(ids);
    }

    /**
     * 删除登录审计日志信息
     *
     * @param id 登录审计日志主键
     * @return 结果
     */
    @Override
    public int deleteDataiSfLoginAuditById(Long id)
    {
        return dataiSfLoginAuditMapper.deleteDataiSfLoginAuditById(id);
    }
}
