package com.datai.auth.mapper;

import java.util.List;
import com.datai.auth.domain.DataiSfLoginHistory;

/**
 * 登录历史信息Mapper接口
 * 
 * @author datai
 * @date 2025-12-25
 */
public interface DataiSfLoginHistoryMapper 
{
    /**
     * 查询登录历史信息
     * 
     * @param id 登录历史信息主键
     * @return 登录历史信息
     */
    public DataiSfLoginHistory selectDataiSfLoginHistoryById(Long id);

    /**
     * 查询登录历史信息列表
     * 
     * @param dataiSfLoginHistory 登录历史信息
     * @return 登录历史信息集合
     */
    public List<DataiSfLoginHistory> selectDataiSfLoginHistoryList(DataiSfLoginHistory dataiSfLoginHistory);

    /**
     * 新增登录历史信息
     * 
     * @param dataiSfLoginHistory 登录历史信息
     * @return 结果
     */
    public int insertDataiSfLoginHistory(DataiSfLoginHistory dataiSfLoginHistory);

    /**
     * 修改登录历史信息
     * 
     * @param dataiSfLoginHistory 登录历史信息
     * @return 结果
     */
    public int updateDataiSfLoginHistory(DataiSfLoginHistory dataiSfLoginHistory);

    /**
     * 删除登录历史信息
     * 
     * @param id 登录历史信息主键
     * @return 结果
     */
    public int deleteDataiSfLoginHistoryById(Long id);

    /**
     * 批量删除登录历史信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataiSfLoginHistoryByIds(Long[] ids);

    /**
     * 查询最近一条登录成功的历史记录
     * 
     * @return 登录历史信息
     */
    public DataiSfLoginHistory selectLatestSuccessLoginHistory();
}
