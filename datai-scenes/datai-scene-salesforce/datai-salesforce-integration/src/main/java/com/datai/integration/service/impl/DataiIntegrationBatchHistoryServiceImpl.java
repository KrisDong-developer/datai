package com.datai.integration.service.impl;

import java.util.List;

import com.datai.common.core.domain.model.LoginUser;
import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.integration.mapper.DataiIntegrationBatchHistoryMapper;
import com.datai.integration.domain.DataiIntegrationBatchHistory;
import com.datai.integration.service.IDataiIntegrationBatchHistoryService;


/**
 * 数据批次历史Service业务层处理
 *
 * @author datai
 * @date 2025-12-22
 */
@Service
public class DataiIntegrationBatchHistoryServiceImpl implements IDataiIntegrationBatchHistoryService {
    @Autowired
    private DataiIntegrationBatchHistoryMapper dataiIntegrationBatchHistoryMapper;

    /**
     * 查询数据批次历史
     *
     * @param id 数据批次历史主键
     * @return 数据批次历史
     */
    @Override
    public DataiIntegrationBatchHistory selectDataiIntegrationBatchHistoryById(Long id)
    {
        return dataiIntegrationBatchHistoryMapper.selectDataiIntegrationBatchHistoryById(id);
    }

    /**
     * 查询数据批次历史列表
     *
     * @param dataiIntegrationBatchHistory 数据批次历史
     * @return 数据批次历史
     */
    @Override
    public List<DataiIntegrationBatchHistory> selectDataiIntegrationBatchHistoryList(DataiIntegrationBatchHistory dataiIntegrationBatchHistory)
    {
        return dataiIntegrationBatchHistoryMapper.selectDataiIntegrationBatchHistoryList(dataiIntegrationBatchHistory);
    }

    /**
     * 新增数据批次历史
     *
     * @param dataiIntegrationBatchHistory 数据批次历史
     * @return 结果
     */
    @Override
    public int insertDataiIntegrationBatchHistory(DataiIntegrationBatchHistory dataiIntegrationBatchHistory)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationBatchHistory.setCreateTime(DateUtils.getNowDate());
                dataiIntegrationBatchHistory.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationBatchHistory.setCreateBy(username);
                dataiIntegrationBatchHistory.setUpdateBy(username);
            return dataiIntegrationBatchHistoryMapper.insertDataiIntegrationBatchHistory(dataiIntegrationBatchHistory);
    }

    /**
     * 修改数据批次历史
     *
     * @param dataiIntegrationBatchHistory 数据批次历史
     * @return 结果
     */
    @Override
    public int updateDataiIntegrationBatchHistory(DataiIntegrationBatchHistory dataiIntegrationBatchHistory)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationBatchHistory.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationBatchHistory.setUpdateBy(username);
        return dataiIntegrationBatchHistoryMapper.updateDataiIntegrationBatchHistory(dataiIntegrationBatchHistory);
    }

    /**
     * 批量删除数据批次历史
     *
     * @param ids 需要删除的数据批次历史主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationBatchHistoryByIds(Long[] ids)
    {
        return dataiIntegrationBatchHistoryMapper.deleteDataiIntegrationBatchHistoryByIds(ids);
    }

    /**
     * 删除数据批次历史信息
     *
     * @param id 数据批次历史主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationBatchHistoryById(Long id)
    {
        return dataiIntegrationBatchHistoryMapper.deleteDataiIntegrationBatchHistoryById(id);
    }
}
