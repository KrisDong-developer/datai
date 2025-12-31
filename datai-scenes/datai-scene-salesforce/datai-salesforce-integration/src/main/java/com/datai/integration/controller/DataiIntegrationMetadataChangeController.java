package com.datai.integration.controller;

import java.util.List;
import java.util.Map;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.datai.common.annotation.Log;
import com.datai.common.core.controller.BaseController;
import com.datai.common.core.domain.AjaxResult;
import com.datai.common.enums.BusinessType;
import com.datai.integration.domain.DataiIntegrationMetadataChange;
import com.datai.integration.service.IDataiIntegrationMetadataChangeService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 对象元数据变更Controller
 * 
 * @author datai
 * @date 2025-12-27
 */
@RestController
@RequestMapping("/integration/change")
@Tag(name = "【对象元数据变更】管理")
public class DataiIntegrationMetadataChangeController extends BaseController
{
    @Autowired
    private IDataiIntegrationMetadataChangeService dataiIntegrationMetadataChangeService;

    /**
     * 查询对象元数据变更列表
     */
    @Operation(summary = "查询对象元数据变更列表")
    @PreAuthorize("@ss.hasPermi('integration:change:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiIntegrationMetadataChange dataiIntegrationMetadataChange)
    {
        startPage();
        List<DataiIntegrationMetadataChange> list = dataiIntegrationMetadataChangeService.selectDataiIntegrationMetadataChangeList(dataiIntegrationMetadataChange);
        return getDataTable(list);
    }

    /**
     * 查询未同步的元数据变更列表
     */
    @Operation(summary = "查询未同步的元数据变更列表")
    @PreAuthorize("@ss.hasPermi('integration:change:unsynced')")
    @GetMapping("/unsynced")
    public TableDataInfo getUnsyncedChanges(DataiIntegrationMetadataChange dataiIntegrationMetadataChange)
    {
        startPage();
        List<DataiIntegrationMetadataChange> list = dataiIntegrationMetadataChangeService.selectUnsyncedMetadataChangeList(dataiIntegrationMetadataChange);
        return getDataTable(list);
    }

    /**
     * 导出对象元数据变更列表
     */
    @Operation(summary = "导出对象元数据变更列表")
    @PreAuthorize("@ss.hasPermi('integration:change:export')")
    @Log(title = "对象元数据变更", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiIntegrationMetadataChange dataiIntegrationMetadataChange)
    {
        List<DataiIntegrationMetadataChange> list = dataiIntegrationMetadataChangeService.selectDataiIntegrationMetadataChangeList(dataiIntegrationMetadataChange);
        ExcelUtil<DataiIntegrationMetadataChange> util = new ExcelUtil<DataiIntegrationMetadataChange>(DataiIntegrationMetadataChange.class);
        util.exportExcel(response, list, "对象元数据变更数据");
    }

    /**
     * 获取对象元数据变更详细信息
     */
    @Operation(summary = "获取对象元数据变更详细信息")
    @PreAuthorize("@ss.hasPermi('integration:change:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dataiIntegrationMetadataChangeService.selectDataiIntegrationMetadataChangeById(id));
    }

    /**
     * 新增对象元数据变更
     */
    @Operation(summary = "新增对象元数据变更")
    @PreAuthorize("@ss.hasPermi('integration:change:add')")
    @Log(title = "对象元数据变更", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiIntegrationMetadataChange dataiIntegrationMetadataChange)
    {
        return toAjax(dataiIntegrationMetadataChangeService.insertDataiIntegrationMetadataChange(dataiIntegrationMetadataChange));
    }

    /**
     * 修改对象元数据变更
     */
    @Operation(summary = "修改对象元数据变更")
    @PreAuthorize("@ss.hasPermi('integration:change:edit')")
    @Log(title = "对象元数据变更", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiIntegrationMetadataChange dataiIntegrationMetadataChange)
    {
        return toAjax(dataiIntegrationMetadataChangeService.updateDataiIntegrationMetadataChange(dataiIntegrationMetadataChange));
    }

    /**
     * 删除对象元数据变更
     */
    @Operation(summary = "删除对象元数据变更")
    @PreAuthorize("@ss.hasPermi('integration:change:remove')")
    @Log(title = "对象元数据变更", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) Long[] ids) 
    {
        return toAjax(dataiIntegrationMetadataChangeService.deleteDataiIntegrationMetadataChangeByIds(ids));
    }

    /**
     * 批量更新元数据变更同步状态
     */
    @Operation(summary = "批量更新元数据变更同步状态")
    @PreAuthorize("@ss.hasPermi('integration:change:updateSync')")
    @Log(title = "更新元数据变更同步状态", businessType = BusinessType.UPDATE)
    @PutMapping("/syncStatus")
    public AjaxResult updateSyncStatus(@RequestBody java.util.Map<String, Object> params)
    {
        Long[] ids = ((java.util.List<Long>) params.get("ids")).toArray(new Long[0]);
        Integer syncStatus = ((Number) params.get("syncStatus")).intValue();
        String syncErrorMessage = (String) params.get("syncErrorMessage");
        return toAjax(dataiIntegrationMetadataChangeService.batchUpdateSyncStatus(ids, syncStatus, syncErrorMessage));
    }

    /**
     * 获取变更统计信息
     */
    @Operation(summary = "获取变更统计信息")
    @PreAuthorize("@ss.hasPermi('integration:change:statistics')")
    @GetMapping("/statistics")
    public AjaxResult getChangeStatistics(@RequestParam(required = false) String changeType,
                                          @RequestParam(required = false) String operationType,
                                          @RequestParam(required = false) String objectApi,
                                          @RequestParam(required = false) Boolean syncStatus,
                                          @RequestParam(required = false) Boolean isCustom,
                                          @RequestParam(required = false) String startTime,
                                          @RequestParam(required = false) String endTime)
    {
        Map<String, Object> params = new java.util.HashMap<>();
        if (changeType != null && !changeType.isEmpty()) {
            params.put("changeType", changeType);
        }
        if (operationType != null && !operationType.isEmpty()) {
            params.put("operationType", operationType);
        }
        if (objectApi != null && !objectApi.isEmpty()) {
            params.put("objectApi", objectApi);
        }
        if (syncStatus != null) {
            params.put("syncStatus", syncStatus);
        }
        if (isCustom != null) {
            params.put("isCustom", isCustom);
        }
        if (startTime != null && !startTime.isEmpty()) {
            params.put("startTime", startTime);
        }
        if (endTime != null && !endTime.isEmpty()) {
            params.put("endTime", endTime);
        }
        
        Map<String, Object> statistics = dataiIntegrationMetadataChangeService.getChangeStatistics(params);
        return success(statistics);
    }

    /**
     * 同步元数据变更到本地数据库
     */
    @Operation(summary = "同步元数据变更到本地数据库")
    @PreAuthorize("@ss.hasPermi('integration:change:sync')")
    @Log(title = "同步元数据变更", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/sync")
    public AjaxResult syncToLocalDatabase(@PathVariable("id") Long id)
    {
        try {
            Map<String, Object> result = dataiIntegrationMetadataChangeService.syncToLocalDatabase(id);
            return success(result);
        } catch (Exception e) {
            return error("同步失败: " + e.getMessage());
        }
    }

    /**
     * 批量同步元数据变更到本地数据库
     */
    @Operation(summary = "批量同步元数据变更到本地数据库")
    @PreAuthorize("@ss.hasPermi('integration:change:syncBatch')")
    @Log(title = "批量同步元数据变更", businessType = BusinessType.UPDATE)
    @PostMapping("/syncBatch")
    public AjaxResult syncBatchToLocalDatabase(@RequestBody java.util.Map<String, Object> params)
    {
        try {
            Long[] ids = ((java.util.List<Long>) params.get("ids")).toArray(new Long[0]);
            Map<String, Object> result = dataiIntegrationMetadataChangeService.syncBatchToLocalDatabase(ids);
            return success(result);
        } catch (Exception e) {
            return error("批量同步失败: " + e.getMessage());
        }
    }
}
