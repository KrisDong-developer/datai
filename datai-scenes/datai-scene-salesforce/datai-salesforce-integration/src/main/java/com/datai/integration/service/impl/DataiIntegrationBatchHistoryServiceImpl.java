package com.datai.integration.service.impl;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import com.datai.common.utils.DateUtils;
import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.integration.mapper.DataiIntegrationBatchHistoryMapper;
import com.datai.integration.domain.DataiIntegrationBatchHistory;
import com.datai.integration.service.IDataiIntegrationBatchHistoryService;
import com.datai.common.core.domain.model.LoginUser;

/**
 * 数据批次历史Service业务层处理
 *
 * @author datai
 * @date 2025-12-26
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
    public DataiIntegrationBatchHistory selectDataiIntegrationBatchHistoryById(Integer id)
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
    public int deleteDataiIntegrationBatchHistoryByIds(Integer[] ids)
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
    public int deleteDataiIntegrationBatchHistoryById(Integer id)
    {
        return dataiIntegrationBatchHistoryMapper.deleteDataiIntegrationBatchHistoryById(id);
    }

    /**
     * 获取历史统计信息
     *
     * @param params 查询参数
     * @return 统计信息
     */
    @Override
    public Map<String, Object> getHistoryStatistics(Map<String, Object> params)
    {
        Map<String, Object> statistics = new HashMap<>();

        try {
            Map<String, Object> result = dataiIntegrationBatchHistoryMapper.selectHistoryStatistics(params);

            int totalCount = result.get("totalCount") != null ? ((Number) result.get("totalCount")).intValue() : 0;
            int successCount = result.get("successCount") != null ? ((Number) result.get("successCount")).intValue() : 0;
            int failedCount = result.get("failedCount") != null ? ((Number) result.get("failedCount")).intValue() : 0;
            int totalSyncNum = result.get("totalSyncNum") != null ? ((Number) result.get("totalSyncNum")).intValue() : 0;
            long totalCost = result.get("totalCost") != null ? ((Number) result.get("totalCost")).longValue() : 0L;
            double avgCost = result.get("avgCost") != null ? ((Number) result.get("avgCost")).doubleValue() : 0.0;
            long minCost = result.get("minCost") != null ? ((Number) result.get("minCost")).longValue() : 0L;
            long maxCost = result.get("maxCost") != null ? ((Number) result.get("maxCost")).longValue() : 0L;
            int batchCount = result.get("batchCount") != null ? ((Number) result.get("batchCount")).intValue() : 0;
            int apiCount = result.get("apiCount") != null ? ((Number) result.get("apiCount")).intValue() : 0;

            statistics.put("success", true);
            statistics.put("message", "获取统计信息成功");
            statistics.put("data", new HashMap<String, Object>() {{
                put("totalCount", totalCount);
                put("successCount", successCount);
                put("failedCount", failedCount);
                put("successRate", totalCount > 0 ? (double) successCount / totalCount * 100 : 0);
                put("totalSyncNum", totalSyncNum);
                put("totalCost", totalCost);
                put("avgCost", avgCost);
                put("minCost", minCost);
                put("maxCost", maxCost);
                put("batchCount", batchCount);
                put("apiCount", apiCount);
                put("firstSyncTime", result.get("firstSyncTime"));
                put("lastSyncTime", result.get("lastSyncTime"));
            }});

        } catch (Exception e) {
            statistics.put("success", false);
            statistics.put("message", "获取统计信息失败: " + e.getMessage());
        }

        return statistics;
    }
}
