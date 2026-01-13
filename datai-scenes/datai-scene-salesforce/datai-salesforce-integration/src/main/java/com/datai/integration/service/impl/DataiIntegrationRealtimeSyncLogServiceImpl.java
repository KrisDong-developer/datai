package com.datai.integration.service.impl;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import com.datai.common.utils.DateUtils;
import com.datai.common.utils.SecurityUtils;
import com.datai.integration.model.domain.DataiIntegrationRealtimeSyncLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.integration.mapper.DataiIntegrationRealtimeSyncLogMapper;
import com.datai.integration.service.IDataiIntegrationRealtimeSyncLogService;
import com.datai.common.core.domain.model.LoginUser;

/**
 * 实时同步日志Service业务层处理
 *
 * @author datai
 * @date 2026-01-09
 */
@Service
public class DataiIntegrationRealtimeSyncLogServiceImpl implements IDataiIntegrationRealtimeSyncLogService {
    @Autowired
    private DataiIntegrationRealtimeSyncLogMapper dataiIntegrationRealtimeSyncLogMapper;

    /**
     * 查询实时同步日志
     *
     * @param id 实时同步日志主键
     * @return 实时同步日志
     */
    @Override
    public DataiIntegrationRealtimeSyncLog selectDataiIntegrationRealtimeSyncLogById(Long id)
    {
        return dataiIntegrationRealtimeSyncLogMapper.selectDataiIntegrationRealtimeSyncLogById(id);
    }

    /**
     * 查询实时同步日志列表
     *
     * @param dataiIntegrationRealtimeSyncLog 实时同步日志
     * @return 实时同步日志
     */
    @Override
    public List<DataiIntegrationRealtimeSyncLog> selectDataiIntegrationRealtimeSyncLogList(DataiIntegrationRealtimeSyncLog dataiIntegrationRealtimeSyncLog)
    {
        return dataiIntegrationRealtimeSyncLogMapper.selectDataiIntegrationRealtimeSyncLogList(dataiIntegrationRealtimeSyncLog);
    }

    /**
     * 新增实时同步日志
     *
     * @param dataiIntegrationRealtimeSyncLog 实时同步日志
     * @return 结果
     */
    @Override
    public int insertDataiIntegrationRealtimeSyncLog(DataiIntegrationRealtimeSyncLog dataiIntegrationRealtimeSyncLog)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationRealtimeSyncLog.setCreateTime(DateUtils.getNowDate());
                dataiIntegrationRealtimeSyncLog.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationRealtimeSyncLog.setCreateBy(username);
                dataiIntegrationRealtimeSyncLog.setUpdateBy(username);
            return dataiIntegrationRealtimeSyncLogMapper.insertDataiIntegrationRealtimeSyncLog(dataiIntegrationRealtimeSyncLog);
    }

    /**
     * 修改实时同步日志
     *
     * @param dataiIntegrationRealtimeSyncLog 实时同步日志
     * @return 结果
     */
    @Override
    public int updateDataiIntegrationRealtimeSyncLog(DataiIntegrationRealtimeSyncLog dataiIntegrationRealtimeSyncLog)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationRealtimeSyncLog.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationRealtimeSyncLog.setUpdateBy(username);
        return dataiIntegrationRealtimeSyncLogMapper.updateDataiIntegrationRealtimeSyncLog(dataiIntegrationRealtimeSyncLog);
    }

    /**
     * 批量删除实时同步日志
     *
     * @param ids 需要删除的实时同步日志主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationRealtimeSyncLogByIds(Long[] ids)
    {
        return dataiIntegrationRealtimeSyncLogMapper.deleteDataiIntegrationRealtimeSyncLogByIds(ids);
    }

    /**
     * 删除实时同步日志信息
     *
     * @param id 实时同步日志主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationRealtimeSyncLogById(Long id)
    {
        return dataiIntegrationRealtimeSyncLogMapper.deleteDataiIntegrationRealtimeSyncLogById(id);
    }

    @Override
    public Map<String, Object> getStatistics(Map<String, Object> params) {
        Map<String, Object> statistics = new HashMap<>();
        
        try {
            String groupBy = (String) params.get("groupBy");
            
            if (groupBy == null || groupBy.isEmpty()) {
                groupBy = "overall";
            }
            
            switch (groupBy) {
                case "object":
                    statistics.put("success", true);
                    statistics.put("message", "获取对象统计信息成功");
                    statistics.put("data", getStatisticsByObject(params));
                    break;
                case "operationType":
                    statistics.put("success", true);
                    statistics.put("message", "获取操作类型统计信息成功");
                    statistics.put("data", getStatisticsByOperationType(params));
                    break;
                case "status":
                    statistics.put("success", true);
                    statistics.put("message", "获取状态统计信息成功");
                    statistics.put("data", getStatisticsByStatus(params));
                    break;
                case "time":
                    statistics.put("success", true);
                    statistics.put("message", "获取时间趋势统计信息成功");
                    statistics.put("data", getStatisticsByTrend(params));
                    break;
                case "overall":
                default:
                    statistics.put("success", true);
                    statistics.put("message", "获取综合统计信息成功");
                    statistics.put("data", getOverallStatistics(params));
                    break;
            }
            
        } catch (Exception e) {
            statistics.put("success", false);
            statistics.put("message", "获取统计信息失败: " + e.getMessage());
        }
        
        return statistics;
    }
    
    private Map<String, Object> getOverallStatistics(Map<String, Object> params) {
        Map<String, Object> overallStats = dataiIntegrationRealtimeSyncLogMapper.selectOverallStatistics(params);
        
        int totalCount = getSafeInt(overallStats.get("totalCount"));
        int successCount = getSafeInt(overallStats.get("successCount"));
        int failureCount = getSafeInt(overallStats.get("failureCount"));
        int pendingCount = getSafeInt(overallStats.get("pendingCount"));
        
        double successRate = totalCount > 0 ? (double) successCount / totalCount * 100 : 0.0;
        overallStats.put("successRate", Math.round(successRate * 100.0) / 100.0);
        
        overallStats.put("totalCount", totalCount);
        overallStats.put("successCount", successCount);
        overallStats.put("failureCount", failureCount);
        overallStats.put("pendingCount", pendingCount);
        
        return overallStats;
    }
    
    private List<Map<String, Object>> getStatisticsByObject(Map<String, Object> params) {
        List<Map<String, Object>> objectStats = dataiIntegrationRealtimeSyncLogMapper.selectStatisticsByObject(params);
        
        for (Map<String, Object> stat : objectStats) {
            int totalCount = getSafeInt(stat.get("totalCount"));
            int successCount = getSafeInt(stat.get("successCount"));
            double successRate = totalCount > 0 ? (double) successCount / totalCount * 100 : 0.0;
            stat.put("successRate", Math.round(successRate * 100.0) / 100.0);
        }
        
        return objectStats;
    }
    
    private List<Map<String, Object>> getStatisticsByOperationType(Map<String, Object> params) {
        List<Map<String, Object>> operationStats = dataiIntegrationRealtimeSyncLogMapper.selectStatisticsByOperationType(params);
        
        for (Map<String, Object> stat : operationStats) {
            int totalCount = getSafeInt(stat.get("totalCount"));
            int successCount = getSafeInt(stat.get("successCount"));
            double successRate = totalCount > 0 ? (double) successCount / totalCount * 100 : 0.0;
            stat.put("successRate", Math.round(successRate * 100.0) / 100.0);
        }
        
        return operationStats;
    }
    
    private List<Map<String, Object>> getStatisticsByStatus(Map<String, Object> params) {
        List<Map<String, Object>> statusStats = dataiIntegrationRealtimeSyncLogMapper.selectStatisticsByStatus(params);
        
        for (Map<String, Object> stat : statusStats) {
            stat.put("totalCount", getSafeInt(stat.get("totalCount")));
            stat.put("totalRetryCount", getSafeInt(stat.get("totalRetryCount")));
            stat.put("avgRetryCount", Math.round(getSafeDouble(stat.get("avgRetryCount")) * 100.0) / 100.0);
        }
        
        return statusStats;
    }
    
    private List<Map<String, Object>> getStatisticsByTrend(Map<String, Object> params) {
        Map<String, Object> statistics = new HashMap<>();
        
        try {
            String timeUnit = (String) params.get("timeUnit");
            String startTime = (String) params.get("startTime");
            String endTime = (String) params.get("endTime");
            
            LocalDateTime startDateTime = null;
            LocalDateTime endDateTime = null;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            
            try {
                if (startTime != null && !startTime.isEmpty()) {
                    startDateTime = LocalDateTime.parse(startTime.length() == 10 ? startTime + " 00:00:00" : startTime, formatter);
                } else {
                    startDateTime = LocalDateTime.now().minusDays(30);
                }
                
                if (endTime != null && !endTime.isEmpty()) {
                    endDateTime = LocalDateTime.parse(endTime.length() == 10 ? endTime + " 23:59:59" : endTime, formatter);
                } else {
                    endDateTime = LocalDateTime.now();
                }
            } catch (Exception e) {
                statistics.put("success", false);
                statistics.put("message", "时间格式解析失败: " + e.getMessage());
                return new ArrayList<>();
            }
            
            params.put("startTime", startDateTime.format(formatter));
            params.put("endTime", endDateTime.format(formatter));
            
            List<Map<String, Object>> trendStatistics = dataiIntegrationRealtimeSyncLogMapper.selectStatisticsByTrend(params);
            
            Map<String, Map<String, Object>> trendMap = new LinkedHashMap<>();
            List<String> timeKeys = generateTimeKeys(startDateTime, endDateTime, timeUnit);
            
            for (String timeKey : timeKeys) {
                Map<String, Object> data = new HashMap<>();
                data.put("timeKey", timeKey);
                data.put("totalCount", 0);
                data.put("successCount", 0);
                data.put("failureCount", 0);
                data.put("pendingCount", 0);
                data.put("successRate", 0.0);
                data.put("totalRetryCount", 0);
                data.put("maxRetryCount", 0);
                data.put("avgProcessingTime", 0.0);
                trendMap.put(timeKey, data);
            }
            
            for (Map<String, Object> row : trendStatistics) {
                String timeKey = (String) row.get("timeKey");
                if (timeKey != null && trendMap.containsKey(timeKey)) {
                    Map<String, Object> data = trendMap.get(timeKey);
                    data.put("totalCount", getSafeInt(row.get("totalCount")));
                    data.put("successCount", getSafeInt(row.get("successCount")));
                    data.put("failureCount", getSafeInt(row.get("failureCount")));
                    data.put("pendingCount", getSafeInt(row.get("pendingCount")));
                    
                    int totalCount = getSafeInt(row.get("totalCount"));
                    int successCount = getSafeInt(row.get("successCount"));
                    double successRate = totalCount > 0 ? (double) successCount / totalCount * 100 : 0.0;
                    data.put("successRate", Math.round(successRate * 100.0) / 100.0);
                    
                    data.put("totalRetryCount", getSafeInt(row.get("totalRetryCount")));
                    data.put("maxRetryCount", getSafeInt(row.get("maxRetryCount")));
                    data.put("avgProcessingTime", Math.round(getSafeDouble(row.get("avgProcessingTime")) * 100.0) / 100.0);
                }
            }
            
            List<Map<String, Object>> result = new ArrayList<>(trendMap.values());
            
            return result;
            
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    
    private int getSafeInt(Object value) {
        if (value == null) {
            return 0;
        }
        if (value instanceof Number) {
            return ((Number) value).intValue();
        }
        try {
            return Integer.parseInt(value.toString());
        } catch (Exception e) {
            return 0;
        }
    }
    
    private double getSafeDouble(Object value) {
        if (value == null) {
            return 0.0;
        }
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        try {
            return Double.parseDouble(value.toString());
        } catch (Exception e) {
            return 0.0;
        }
    }
    
    private List<String> generateTimeKeys(LocalDateTime startDateTime, LocalDateTime endDateTime, String timeUnit) {
        List<String> timeKeys = new ArrayList<>();
        DateTimeFormatter formatter;
        
        switch (timeUnit) {
            case "day":
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDateTime currentDay = startDateTime.toLocalDate().atStartOfDay();
                while (!currentDay.isAfter(endDateTime.toLocalDate().atStartOfDay())) {
                    timeKeys.add(currentDay.format(formatter));
                    currentDay = currentDay.plusDays(1);
                }
                break;
            case "week":
                formatter = DateTimeFormatter.ofPattern("yyyy-w");
                LocalDateTime currentWeek = startDateTime;
                while (!currentWeek.isAfter(endDateTime)) {
                    int weekOfYear = currentWeek.get(java.time.temporal.WeekFields.ISO.weekOfYear());
                    timeKeys.add(currentWeek.getYear() + "-" + String.format("%02d", weekOfYear));
                    currentWeek = currentWeek.plusWeeks(1);
                }
                break;
            case "month":
                formatter = DateTimeFormatter.ofPattern("yyyy-MM");
                LocalDateTime currentMonth = startDateTime.withDayOfMonth(1);
                while (!currentMonth.isAfter(endDateTime.withDayOfMonth(1))) {
                    timeKeys.add(currentMonth.format(formatter));
                    currentMonth = currentMonth.plusMonths(1);
                }
                break;
            case "quarter":
                LocalDateTime currentQuarter = startDateTime;
                while (!currentQuarter.isAfter(endDateTime)) {
                    int quarter = (currentQuarter.getMonthValue() - 1) / 3 + 1;
                    timeKeys.add(currentQuarter.getYear() + "-Q" + quarter);
                    currentQuarter = currentQuarter.plusMonths(3);
                }
                break;
            default:
                formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                LocalDateTime currentDefault = startDateTime.toLocalDate().atStartOfDay();
                while (!currentDefault.isAfter(endDateTime.toLocalDate().atStartOfDay())) {
                    timeKeys.add(currentDefault.format(formatter));
                    currentDefault = currentDefault.plusDays(1);
                }
                break;
        }
        
        return timeKeys;
    }
}
