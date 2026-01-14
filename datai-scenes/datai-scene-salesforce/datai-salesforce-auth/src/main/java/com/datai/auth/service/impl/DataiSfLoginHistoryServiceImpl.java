package com.datai.auth.service.impl;

import java.util.List;
import com.datai.common.utils.DateUtils;
import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.auth.mapper.DataiSfLoginHistoryMapper;
import com.datai.auth.model.domain.DataiSfLoginHistory;
import com.datai.auth.service.IDataiSfLoginHistoryService;
import com.datai.common.core.domain.model.LoginUser;

/**
 * 登录历史信息Service业务层处理
 *
 * @author datai
 * @date 2025-12-25
 */
@Service
public class DataiSfLoginHistoryServiceImpl implements IDataiSfLoginHistoryService {
    @Autowired
    private DataiSfLoginHistoryMapper dataiSfLoginHistoryMapper;

    /**
     * 查询登录历史信息
     *
     * @param id 登录历史信息主键
     * @return 登录历史信息
     */
    @Override
    public DataiSfLoginHistory selectDataiSfLoginHistoryById(Long id)
    {
        return dataiSfLoginHistoryMapper.selectDataiSfLoginHistoryById(id);
    }

    /**
     * 查询登录历史信息列表
     *
     * @param dataiSfLoginHistory 登录历史信息
     * @return 登录历史信息
     */
    @Override
    public List<DataiSfLoginHistory> selectDataiSfLoginHistoryList(DataiSfLoginHistory dataiSfLoginHistory)
    {
        return dataiSfLoginHistoryMapper.selectDataiSfLoginHistoryList(dataiSfLoginHistory);
    }

    /**
     * 新增登录历史信息
     *
     * @param dataiSfLoginHistory 登录历史信息
     * @return 结果
     */
    @Override
    public int insertDataiSfLoginHistory(DataiSfLoginHistory dataiSfLoginHistory)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiSfLoginHistory.setCreateTime(DateUtils.getNowDate());
                dataiSfLoginHistory.setUpdateTime(DateUtils.getNowDate());
                dataiSfLoginHistory.setCreateBy(username);
                dataiSfLoginHistory.setUpdateBy(username);
            return dataiSfLoginHistoryMapper.insertDataiSfLoginHistory(dataiSfLoginHistory);
    }

    /**
     * 修改登录历史信息
     *
     * @param dataiSfLoginHistory 登录历史信息
     * @return 结果
     */
    @Override
    public int updateDataiSfLoginHistory(DataiSfLoginHistory dataiSfLoginHistory)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiSfLoginHistory.setUpdateTime(DateUtils.getNowDate());
                dataiSfLoginHistory.setUpdateBy(username);
        return dataiSfLoginHistoryMapper.updateDataiSfLoginHistory(dataiSfLoginHistory);
    }

    /**
     * 批量删除登录历史信息
     *
     * @param ids 需要删除的登录历史信息主键
     * @return 结果
     */
    @Override
    public int deleteDataiSfLoginHistoryByIds(Long[] ids)
    {
        return dataiSfLoginHistoryMapper.deleteDataiSfLoginHistoryByIds(ids);
    }

    /**
     * 删除登录历史信息信息
     *
     * @param id 登录历史信息主键
     * @return 结果
     */
    @Override
    public int deleteDataiSfLoginHistoryById(Long id)
    {
        return dataiSfLoginHistoryMapper.deleteDataiSfLoginHistoryById(id);
    }

    /**
     * 查询最近一条登录成功的历史记录
     *
     * @return 登录历史信息
     */
    @Override
    public DataiSfLoginHistory selectLatestSuccessLoginHistory()
    {
        return dataiSfLoginHistoryMapper.selectLatestSuccessLoginHistory();
    }

    /**
     * 根据ORG类型查询最近一条登录成功的历史记录
     *
     * @param orgType ORG类型
     * @return 登录历史信息
     */
    @Override
    public DataiSfLoginHistory selectLatestSuccessLoginHistoryByOrgType(String orgType)
    {
        return dataiSfLoginHistoryMapper.selectLatestSuccessLoginHistoryByOrgType(orgType);
    }
}
