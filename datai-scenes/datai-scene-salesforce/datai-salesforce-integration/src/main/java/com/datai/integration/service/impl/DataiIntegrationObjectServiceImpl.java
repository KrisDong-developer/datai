package com.datai.integration.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
        import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.integration.mapper.DataiIntegrationObjectMapper;
import com.datai.integration.domain.DataiIntegrationObject;
import com.datai.integration.service.IDataiIntegrationObjectService;
import com.datai.common.core.domain.model.LoginUser;


/**
 * 对象同步控制Service业务层处理
 *
 * @author datai
 * @date 2025-12-24
 */
@Service
public class DataiIntegrationObjectServiceImpl implements IDataiIntegrationObjectService {
    @Autowired
    private DataiIntegrationObjectMapper dataiIntegrationObjectMapper;

    /**
     * 查询对象同步控制
     *
     * @param id 对象同步控制主键
     * @return 对象同步控制
     */
    @Override
    public DataiIntegrationObject selectDataiIntegrationObjectById(Integer id)
    {
        return dataiIntegrationObjectMapper.selectDataiIntegrationObjectById(id);
    }

    /**
     * 查询对象同步控制列表
     *
     * @param dataiIntegrationObject 对象同步控制
     * @return 对象同步控制
     */
    @Override
    public List<DataiIntegrationObject> selectDataiIntegrationObjectList(DataiIntegrationObject dataiIntegrationObject)
    {
        return dataiIntegrationObjectMapper.selectDataiIntegrationObjectList(dataiIntegrationObject);
    }

    /**
     * 新增对象同步控制
     *
     * @param dataiIntegrationObject 对象同步控制
     * @return 结果
     */
    @Override
    public int insertDataiIntegrationObject(DataiIntegrationObject dataiIntegrationObject)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationObject.setCreateTime(DateUtils.getNowDate());
                dataiIntegrationObject.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationObject.setCreateBy(username);
                dataiIntegrationObject.setUpdateBy(username);
            return dataiIntegrationObjectMapper.insertDataiIntegrationObject(dataiIntegrationObject);
    }

    /**
     * 修改对象同步控制
     *
     * @param dataiIntegrationObject 对象同步控制
     * @return 结果
     */
    @Override
    public int updateDataiIntegrationObject(DataiIntegrationObject dataiIntegrationObject)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationObject.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationObject.setUpdateBy(username);
        return dataiIntegrationObjectMapper.updateDataiIntegrationObject(dataiIntegrationObject);
    }

    /**
     * 批量删除对象同步控制
     *
     * @param ids 需要删除的对象同步控制主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationObjectByIds(Integer[] ids)
    {
        return dataiIntegrationObjectMapper.deleteDataiIntegrationObjectByIds(ids);
    }

    /**
     * 删除对象同步控制信息
     *
     * @param id 对象同步控制主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationObjectById(Integer id)
    {
        return dataiIntegrationObjectMapper.deleteDataiIntegrationObjectById(id);
    }

    @Override
    public Map<String, Object> getSyncStatistics(Integer id)
    {
        Map<String, Object> statistics = new HashMap<>();

        try {
            DataiIntegrationObject object = dataiIntegrationObjectMapper.selectDataiIntegrationObjectById(id);
            if (object == null) {
                statistics.put("success", false);
                statistics.put("message", "对象不存在");
                return statistics;
            }

            Map<String, Object> syncStats = dataiIntegrationObjectMapper.selectObjectSyncStatistics(object.getApi());
            
            int totalSyncCount = syncStats.get("totalSyncCount") != null ? ((Number) syncStats.get("totalSyncCount")).intValue() : 0;
            int successSyncCount = syncStats.get("successSyncCount") != null ? ((Number) syncStats.get("successSyncCount")).intValue() : 0;
            int failedSyncCount = syncStats.get("failedSyncCount") != null ? ((Number) syncStats.get("failedSyncCount")).intValue() : 0;
            
            double successRate = totalSyncCount > 0 ? (double) successSyncCount / totalSyncCount * 100 : 0;
            
            statistics.put("success", true);
            statistics.put("message", "获取对象同步统计信息成功");
            statistics.put("data", new HashMap<String, Object>() {{
                put("objectId", object.getId());
                put("objectApi", object.getApi());
                put("objectLabel", object.getLabel());
                put("totalSyncCount", totalSyncCount);
                put("successSyncCount", successSyncCount);
                put("failedSyncCount", failedSyncCount);
                put("successRate", successRate);
                put("totalSyncRecords", syncStats.get("totalSyncRecords"));
                put("avgSyncTime", syncStats.get("avgSyncTime"));
                put("firstSyncTime", syncStats.get("firstSyncTime"));
                put("lastSyncTime", syncStats.get("lastSyncTime"));
                put("firstSyncStartTime", syncStats.get("firstSyncStartTime"));
                put("lastSyncEndTime", syncStats.get("lastSyncEndTime"));
                put("fullSyncCount", syncStats.get("fullSyncCount"));
                put("incrementalSyncCount", syncStats.get("incrementalSyncCount"));
                put("lastFullSyncDate", object.getLastFullSyncDate());
                put("lastSyncDate", object.getLastSyncDate());
                put("syncStatus", object.getSyncStatus());
                put("errorMessage", object.getErrorMessage());
                put("totalRows", object.getTotalRows());
            }});
            
        } catch (Exception e) {
            statistics.put("success", false);
            statistics.put("message", "获取对象同步统计信息失败: " + e.getMessage());
        }
        
        return statistics;
    }

    @Override
    public List<Map<String, Object>> getObjectDependencies(Integer id)
    {
        try {
            DataiIntegrationObject object = dataiIntegrationObjectMapper.selectDataiIntegrationObjectById(id);
            if (object == null) {
                throw new RuntimeException("对象不存在");
            }

            return dataiIntegrationObjectMapper.selectObjectDependencies(object.getApi());
            
        } catch (Exception e) {
            throw new RuntimeException("获取对象依赖关系失败: " + e.getMessage());
        }
    }
}
