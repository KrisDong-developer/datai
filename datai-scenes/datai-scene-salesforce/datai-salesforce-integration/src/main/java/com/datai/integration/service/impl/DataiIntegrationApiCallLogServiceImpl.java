package com.datai.integration.service.impl;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Objects;
import com.datai.common.utils.DateUtils;
import com.datai.common.utils.SecurityUtils;
import com.datai.common.utils.CacheUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.integration.mapper.DataiIntegrationApiCallLogMapper;
import com.datai.integration.model.domain.DataiIntegrationApiCallLog;
import com.datai.integration.service.IDataiIntegrationApiCallLogService;
import com.datai.common.core.domain.model.LoginUser;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

/**
 * API调用日志Service业务层处理
 *
 * @author datai
 * @date 2025-12-28
 */
@Service
@Slf4j
public class DataiIntegrationApiCallLogServiceImpl implements IDataiIntegrationApiCallLogService {
    @Autowired
    private DataiIntegrationApiCallLogMapper dataiIntegrationApiCallLogMapper;

    /**
     * 查询API调用日志
     *
     * @param id API调用日志主键
     * @return API调用日志
     */
    @Override
    public DataiIntegrationApiCallLog selectDataiIntegrationApiCallLogById(Long id)
    {
        return dataiIntegrationApiCallLogMapper.selectDataiIntegrationApiCallLogById(id);
    }

    /**
     * 查询API调用日志列表
     *
     * @param dataiIntegrationApiCallLog API调用日志
     * @return API调用日志
     */
    @Override
    public List<DataiIntegrationApiCallLog> selectDataiIntegrationApiCallLogList(DataiIntegrationApiCallLog dataiIntegrationApiCallLog)
    {
        return dataiIntegrationApiCallLogMapper.selectDataiIntegrationApiCallLogList(dataiIntegrationApiCallLog);
    }

    /**
     * 新增API调用日志
     *
     * @param dataiIntegrationApiCallLog API调用日志
     * @return 结果
     */
    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public int insertDataiIntegrationApiCallLog(DataiIntegrationApiCallLog dataiIntegrationApiCallLog)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationApiCallLog.setCreateTime(DateUtils.getNowDate());
                dataiIntegrationApiCallLog.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationApiCallLog.setCreateBy(username);
                dataiIntegrationApiCallLog.setUpdateBy(username);
            return dataiIntegrationApiCallLogMapper.insertDataiIntegrationApiCallLog(dataiIntegrationApiCallLog);
    }

    /**
     * 修改API调用日志
     *
     * @param dataiIntegrationApiCallLog API调用日志
     * @return 结果
     */
    @Override
    public int updateDataiIntegrationApiCallLog(DataiIntegrationApiCallLog dataiIntegrationApiCallLog)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationApiCallLog.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationApiCallLog.setUpdateBy(username);
        return dataiIntegrationApiCallLogMapper.updateDataiIntegrationApiCallLog(dataiIntegrationApiCallLog);
    }

    /**
     * 批量删除API调用日志
     *
     * @param ids 需要删除的API调用日志主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationApiCallLogByIds(Long[] ids)
    {
        return dataiIntegrationApiCallLogMapper.deleteDataiIntegrationApiCallLogByIds(ids);
    }

    /**
     * 删除API调用日志信息
     *
     * @param id API调用日志主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationApiCallLogById(Long id)
    {
        return dataiIntegrationApiCallLogMapper.deleteDataiIntegrationApiCallLogById(id);
    }

    /**
     * 获取API调用日志统计信息
     *
     * @param params 查询参数
     * @return 统计信息
     */
    @Override
    public Map<String, Object> getApiCallLogStatistics(Map<String, Object> params) {
        Map<String, Object> result = new HashMap<>();
        
        try {
            log.info("开始获取API调用日志统计信息，查询参数: {}", params);
            
            // 生成缓存键
            String cacheKey = generateCacheKey(params);
            
            // 尝试从缓存获取
            Object cachedResult = CacheUtils.get(cacheKey);
            if (cachedResult != null) {
                log.info("从缓存获取API调用日志统计信息成功");
                return (Map<String, Object>) cachedResult;
            }
            
            // 检查是否需要分组统计
            String groupBy = (String) params.get("groupBy");
            if (groupBy != null && !groupBy.isEmpty()) {
                // 分组统计
                List<Map<String, Object>> groupedStatistics = dataiIntegrationApiCallLogMapper.getApiCallLogGroupedStatistics(params);
                
                if (groupedStatistics != null && !groupedStatistics.isEmpty()) {
                    result.put("success", true);
                    result.put("message", "获取分组统计信息成功");
                    result.put("data", groupedStatistics);
                    log.info("获取API调用日志分组统计信息成功，分组维度: {}", groupBy);
                } else {
                    result.put("success", false);
                    result.put("message", "未找到分组统计数据");
                    log.warn("未找到API调用日志分组统计数据");
                }
            } else {
                // 整体统计
                Map<String, Object> statistics = dataiIntegrationApiCallLogMapper.getApiCallLogStatistics(params);
                
                if (statistics != null) {
                    // 计算额外的统计指标
                    calculateAdditionalMetrics(statistics);
                    
                    // 获取趋势数据
                    List<Map<String, Object>> trendData = getTrendData(params);
                    
                    Map<String, Object> completeData = new HashMap<>();
                    completeData.put("summary", statistics);
                    completeData.put("trend", trendData);
                    
                    result.put("success", true);
                    result.put("message", "获取统计信息成功");
                    result.put("data", completeData);
                    log.info("获取API调用日志统计信息成功");
                } else {
                    result.put("success", false);
                    result.put("message", "未找到统计数据");
                    log.warn("未找到API调用日志统计数据");
                }
            }
            
            // 缓存结果，有效期5分钟
            if (result.containsKey("success") && (Boolean) result.get("success")) {
                CacheUtils.put(cacheKey, result, 5 * 60);
                log.info("API调用日志统计信息已缓存，缓存键: {}", cacheKey);
            }
            
        } catch (Exception e) {
            log.error("获取API调用日志统计信息时发生异常", e);
            result.put("success", false);
            result.put("message", "获取统计信息失败: " + e.getMessage());
        }
        
        return result;
    }
    
    /**
     * 生成缓存键
     *
     * @param params 查询参数
     * @return 缓存键
     */
    private String generateCacheKey(Map<String, Object> params) {
        StringBuilder keyBuilder = new StringBuilder("api_call_log_statistics_");
        
        if (params != null) {
            // 按顺序添加参数，确保相同参数生成相同的键
            String[] keys = {"apiType", "connectionClass", "methodName", "status", "startTime", "endTime", "groupBy"};
            for (String key : keys) {
                Object value = params.get(key);
                if (value != null) {
                    keyBuilder.append(key).append("_").append(value).append("_");
                }
            }
        }
        
        return keyBuilder.toString();
    }
    
    /**
     * 计算额外的统计指标
     *
     * @param statistics 统计数据
     */
    private void calculateAdditionalMetrics(Map<String, Object> statistics) {
        // 可以在这里添加更多自定义统计指标
        // 例如：按时间段的分布、按方法的分布等
    }
    
    /**
     * 获取趋势数据
     *
     * @param params 查询参数
     * @return 趋势数据列表
     */
    private List<Map<String, Object>> getTrendData(Map<String, Object> params) {
        Map<String, Object> trendParams = new HashMap<>(params);
        
        // 根据时间范围自动选择合适的时间格式
        Date startTime = (Date) params.get("startTime");
        Date endTime = (Date) params.get("endTime");
        
        String timeFormat = "%Y-%m-%d %H:00";
        if (startTime != null && endTime != null) {
            long timeDiff = endTime.getTime() - startTime.getTime();
            long daysDiff = timeDiff / (24 * 60 * 60 * 1000);
            
            if (daysDiff > 7) {
                timeFormat = "%Y-%m-%d";
            } else if (daysDiff < 1) {
                timeFormat = "%Y-%m-%d %H:%i";
            }
        }
        
        trendParams.put("timeFormat", timeFormat);
        return dataiIntegrationApiCallLogMapper.getApiCallLogTrend(trendParams);
    }
}
