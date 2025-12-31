package com.datai.integration.service.impl;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
        import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.integration.mapper.DataiIntegrationBatchMapper;
import com.datai.integration.model.domain.DataiIntegrationBatch;
import com.datai.integration.service.IDataiIntegrationBatchService;
import com.datai.common.core.domain.model.LoginUser;
import com.datai.integration.model.domain.DataiIntegrationBatchHistory;
import com.datai.integration.service.IDataiIntegrationBatchHistoryService;
import com.datai.integration.service.ISalesforceDataPullService;
import lombok.extern.slf4j.Slf4j;


/**
 * 数据批次Service业务层处理
 *
 * @author datai
 * @date 2025-12-24
 */
@Service
@Slf4j
public class DataiIntegrationBatchServiceImpl implements IDataiIntegrationBatchService {
    @Autowired
    private DataiIntegrationBatchMapper dataiIntegrationBatchMapper;

    @Autowired
    private IDataiIntegrationBatchHistoryService batchHistoryService;

    @Autowired
    private ISalesforceDataPullService salesforceDataPullService;

    /**
     * 查询数据批次
     *
     * @param id 数据批次主键
     * @return 数据批次
     */
    @Override
    public DataiIntegrationBatch selectDataiIntegrationBatchById(Integer id)
    {
        return dataiIntegrationBatchMapper.selectDataiIntegrationBatchById(id);
    }

    /**
     * 查询数据批次列表
     *
     * @param dataiIntegrationBatch 数据批次
     * @return 数据批次
     */
    @Override
    public List<DataiIntegrationBatch> selectDataiIntegrationBatchList(DataiIntegrationBatch dataiIntegrationBatch)
    {
        return dataiIntegrationBatchMapper.selectDataiIntegrationBatchList(dataiIntegrationBatch);
    }

    /**
     * 新增数据批次
     *
     * @param dataiIntegrationBatch 数据批次
     * @return 结果
     */
    @Override
    public int insertDataiIntegrationBatch(DataiIntegrationBatch dataiIntegrationBatch)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationBatch.setCreateTime(DateUtils.getNowDate());
                dataiIntegrationBatch.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationBatch.setCreateBy(username);
                dataiIntegrationBatch.setUpdateBy(username);
            return dataiIntegrationBatchMapper.insertDataiIntegrationBatch(dataiIntegrationBatch);
    }

    /**
     * 修改数据批次
     *
     * @param dataiIntegrationBatch 数据批次
     * @return 结果
     */
    @Override
    public int updateDataiIntegrationBatch(DataiIntegrationBatch dataiIntegrationBatch)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationBatch.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationBatch.setUpdateBy(username);
        return dataiIntegrationBatchMapper.updateDataiIntegrationBatch(dataiIntegrationBatch);
    }

    /**
     * 批量删除数据批次
     *
     * @param ids 需要删除的数据批次主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationBatchByIds(Integer[] ids)
    {
        return dataiIntegrationBatchMapper.deleteDataiIntegrationBatchByIds(ids);
    }

    /**
     * 删除数据批次信息
     *
     * @param id 数据批次主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationBatchById(Integer id)
    {
        return dataiIntegrationBatchMapper.deleteDataiIntegrationBatchById(id);
    }

    /**
     * 重试失败的批次
     *
     * @param id 批次ID
     * @return 结果
     */
    @Override
    public boolean retryFailed(Integer id) {
        try {
            log.info("开始重试失败的批次，批次ID: {}", id);

            DataiIntegrationBatch batch = selectDataiIntegrationBatchById(id);
            if (batch == null) {
                log.error("批次不存在，批次ID: {}", id);
                return false;
            }

            DataiIntegrationBatchHistory queryHistory = new DataiIntegrationBatchHistory();
            queryHistory.setBatchId(id);
            queryHistory.setSyncStatus(false);
            List<DataiIntegrationBatchHistory> failedHistories = batchHistoryService.selectDataiIntegrationBatchHistoryList(queryHistory);

            if (failedHistories.isEmpty()) {
                log.warn("批次没有失败的同步记录，批次ID: {}", id);
                return true;
            }

            log.info("批次 {} 共有 {} 条失败的同步记录，准备重试", id, failedHistories.size());

            boolean retryResult = salesforceDataPullService.syncObjectDataByBatch(batch.getApi(), id.toString());

            if (retryResult) {
                log.info("批次 {} 重试成功", id);
            } else {
                log.error("批次 {} 重试失败", id);
            }

            return retryResult;
        } catch (Exception e) {
            log.error("重试批次 {} 时发生异常", id, e);
            return false;
        }
    }

    /**
     * 获取批次同步统计信息
     *
     * @param id 批次ID
     * @return 统计信息
     */
    @Override
    public Map<String, Object> getSyncStatistics(Integer id) {
        Map<String, Object> statistics = new HashMap<>();

        try {
            DataiIntegrationBatch batch = selectDataiIntegrationBatchById(id);
            if (batch == null) {
                log.error("批次不存在，批次ID: {}", id);
                statistics.put("success", false);
                statistics.put("message", "批次不存在");
                return statistics;
            }

            DataiIntegrationBatchHistory queryHistory = new DataiIntegrationBatchHistory();
            queryHistory.setBatchId(id);
            List<DataiIntegrationBatchHistory> histories = batchHistoryService.selectDataiIntegrationBatchHistoryList(queryHistory);

            int totalCount = histories.size();
            int successCount = 0;
            int failedCount = 0;
            int totalSyncNum = 0;
            long totalCost = 0L;
            long minCost = Long.MAX_VALUE;
            long maxCost = 0L;

            for (DataiIntegrationBatchHistory history : histories) {
                if (history.getSyncStatus()) {
                    successCount++;
                } else {
                    failedCount++;
                }

                if (history.getSyncNum() != null) {
                    totalSyncNum += history.getSyncNum();
                }

                if (history.getCost() != null) {
                    totalCost += history.getCost();
                    minCost = Math.min(minCost, history.getCost());
                    maxCost = Math.max(maxCost, history.getCost());
                }
            }

            long avgCost = totalCount > 0 ? totalCost / totalCount : 0;
            if (minCost == Long.MAX_VALUE) {
                minCost = 0L;
            }

            statistics.put("success", true);
            statistics.put("message", "获取统计信息成功");
            int finalSuccessCount = successCount;
            int finalFailedCount = failedCount;
            int finalTotalSyncNum = totalSyncNum;
            long finalTotalCost = totalCost;
            long finalMinCost = minCost;
            long finalMaxCost = maxCost;
            statistics.put("data", new HashMap<String, Object>() {{
                put("batchId", id);
                put("api", batch.getApi());
                put("label", batch.getLabel());
                put("syncType", batch.getSyncType());
                put("sfNum", batch.getSfNum());
                put("dbNum", batch.getDbNum());
                put("totalCount", totalCount);
                put("successCount", finalSuccessCount);
                put("failedCount", finalFailedCount);
                put("successRate", totalCount > 0 ? (double) finalSuccessCount / totalCount * 100 : 0);
                put("totalSyncNum", finalTotalSyncNum);
                put("totalCost", finalTotalCost);
                put("avgCost", avgCost);
                put("minCost", finalMinCost);
                put("maxCost", finalMaxCost);
                put("firstSyncTime", batch.getFirstSyncTime());
                put("lastSyncTime", batch.getLastSyncTime());
                put("syncStatus", batch.getSyncStatus());
            }});

            log.info("获取批次 {} 统计信息成功", id);
        } catch (Exception e) {
            log.error("获取批次 {} 统计信息时发生异常", id, e);
            statistics.put("success", false);
            statistics.put("message", "获取统计信息失败: " + e.getMessage());
        }

        return statistics;
    }

    @Override
    public Map<String, Object> syncBatchData(Integer id) {
        Map<String, Object> result = new HashMap<>();

        try {
            log.info("开始同步批次数据，批次ID: {}", id);

            DataiIntegrationBatch batch = selectDataiIntegrationBatchById(id);
            if (batch == null) {
                log.error("批次不存在，批次ID: {}", id);
                result.put("success", false);
                result.put("message", "批次不存在");
                return result;
            }

            String objectApi = batch.getApi();
            String batchId = id.toString();

            log.info("准备同步对象 {} 的批次 {} 数据", objectApi, batchId);

            boolean syncResult = salesforceDataPullService.syncObjectDataByBatch(objectApi, batchId);

            if (syncResult) {
                log.info("批次 {} 数据同步成功", id);
                result.put("success", true);
                result.put("message", "批次数据同步成功");
                result.put("batchId", id);
                result.put("api", objectApi);
                result.put("label", batch.getLabel());
            } else {
                log.error("批次 {} 数据同步失败", id);
                result.put("success", false);
                result.put("message", "批次数据同步失败");
                result.put("batchId", id);
                result.put("api", objectApi);
            }

        } catch (Exception e) {
            log.error("同步批次 {} 数据时发生异常", id, e);
            result.put("success", false);
            result.put("message", "同步批次数据失败: " + e.getMessage());
        }

        return result;
    }
}
