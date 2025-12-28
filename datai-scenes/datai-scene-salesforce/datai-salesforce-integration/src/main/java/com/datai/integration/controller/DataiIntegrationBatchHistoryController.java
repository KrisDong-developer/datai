package com.datai.integration.controller;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
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
import com.datai.integration.domain.DataiIntegrationBatchHistory;
import com.datai.integration.service.IDataiIntegrationBatchHistoryService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 数据批次历史Controller
 * 
 * @author datai
 * @date 2025-12-26
 */
@RestController
@RequestMapping("/integration/batchhistory")
@Tag(name = "【数据批次历史】管理")
public class DataiIntegrationBatchHistoryController extends BaseController
{
    @Autowired
    private IDataiIntegrationBatchHistoryService dataiIntegrationBatchHistoryService;

    /**
     * 查询数据批次历史列表
     */
    @Operation(summary = "查询数据批次历史列表")
    @PreAuthorize("@ss.hasPermi('integration:batchhistory:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiIntegrationBatchHistory dataiIntegrationBatchHistory)
    {
        startPage();
        List<DataiIntegrationBatchHistory> list = dataiIntegrationBatchHistoryService.selectDataiIntegrationBatchHistoryList(dataiIntegrationBatchHistory);
        return getDataTable(list);
    }

    /**
     * 导出数据批次历史列表
     */
    @Operation(summary = "导出数据批次历史列表")
    @PreAuthorize("@ss.hasPermi('integration:batchhistory:export')")
    @Log(title = "数据批次历史", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiIntegrationBatchHistory dataiIntegrationBatchHistory)
    {
        List<DataiIntegrationBatchHistory> list = dataiIntegrationBatchHistoryService.selectDataiIntegrationBatchHistoryList(dataiIntegrationBatchHistory);
        ExcelUtil<DataiIntegrationBatchHistory> util = new ExcelUtil<DataiIntegrationBatchHistory>(DataiIntegrationBatchHistory.class);
        util.exportExcel(response, list, "数据批次历史数据");
    }

    /**
     * 获取数据批次历史详细信息
     */
    @Operation(summary = "获取数据批次历史详细信息")
    @PreAuthorize("@ss.hasPermi('integration:batchhistory:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id)
    {
        return success(dataiIntegrationBatchHistoryService.selectDataiIntegrationBatchHistoryById(id));
    }

    /**
     * 新增数据批次历史
     */
    @Operation(summary = "新增数据批次历史")
    @PreAuthorize("@ss.hasPermi('integration:batchhistory:add')")
    @Log(title = "数据批次历史", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiIntegrationBatchHistory dataiIntegrationBatchHistory)
    {
        return toAjax(dataiIntegrationBatchHistoryService.insertDataiIntegrationBatchHistory(dataiIntegrationBatchHistory));
    }

    /**
     * 修改数据批次历史
     */
    @Operation(summary = "修改数据批次历史")
    @PreAuthorize("@ss.hasPermi('integration:batchhistory:edit')")
    @Log(title = "数据批次历史", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiIntegrationBatchHistory dataiIntegrationBatchHistory)
    {
        return toAjax(dataiIntegrationBatchHistoryService.updateDataiIntegrationBatchHistory(dataiIntegrationBatchHistory));
    }

    /**
     * 删除数据批次历史
     */
    @Operation(summary = "删除数据批次历史")
    @PreAuthorize("@ss.hasPermi('integration:batchhistory:remove')")
    @Log(title = "数据批次历史", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) Integer[] ids) 
    {
        return toAjax(dataiIntegrationBatchHistoryService.deleteDataiIntegrationBatchHistoryByIds(ids));
    }

    /**
     * 获取历史统计信息
     */
    @Operation(summary = "获取历史统计信息")
    @PreAuthorize("@ss.hasPermi('integration:batchhistory:statistics')")
    @GetMapping("/statistics")
    public AjaxResult getHistoryStatistics(
            @RequestParam(required = false) String api,
            @RequestParam(required = false) Integer batchId,
            @RequestParam(required = false) String syncType,
            @RequestParam(required = false) Boolean syncStatus,
            @RequestParam(required = false) String startTime,
            @RequestParam(required = false) String endTime)
    {
        Map<String, Object> params = new HashMap<>();
        if (api != null && !api.isEmpty()) {
            params.put("api", api);
        }
        if (batchId != null) {
            params.put("batchId", batchId);
        }
        if (syncType != null && !syncType.isEmpty()) {
            params.put("syncType", syncType);
        }
        if (syncStatus != null) {
            params.put("syncStatus", syncStatus);
        }
        if (startTime != null && !startTime.isEmpty()) {
            params.put("startTime", startTime);
        }
        if (endTime != null && !endTime.isEmpty()) {
            params.put("endTime", endTime);
        }

        return success(dataiIntegrationBatchHistoryService.getHistoryStatistics(params));
    }
}
