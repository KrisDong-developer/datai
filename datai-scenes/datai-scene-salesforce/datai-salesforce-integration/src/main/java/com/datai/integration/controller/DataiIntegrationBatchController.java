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
import org.springframework.web.bind.annotation.RestController;
import com.datai.common.annotation.Log;
import com.datai.common.core.controller.BaseController;
import com.datai.common.core.domain.AjaxResult;
import com.datai.common.enums.BusinessType;
import com.datai.integration.domain.DataiIntegrationBatch;
import com.datai.integration.service.IDataiIntegrationBatchService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 数据批次Controller
 * 
 * @author datai
 * @date 2025-12-24
 */
@RestController
@RequestMapping("/integration/batch")
@Tag(name = "【数据批次】管理")
public class DataiIntegrationBatchController extends BaseController
{
    @Autowired
    private IDataiIntegrationBatchService dataiIntegrationBatchService;

    /**
     * 查询数据批次列表
     */
    @Operation(summary = "查询数据批次列表")
    @PreAuthorize("@ss.hasPermi('integration:batch:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiIntegrationBatch dataiIntegrationBatch)
    {
        startPage();
        List<DataiIntegrationBatch> list = dataiIntegrationBatchService.selectDataiIntegrationBatchList(dataiIntegrationBatch);
        return getDataTable(list);
    }

    /**
     * 导出数据批次列表
     */
    @Operation(summary = "导出数据批次列表")
    @PreAuthorize("@ss.hasPermi('integration:batch:export')")
    @Log(title = "数据批次", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiIntegrationBatch dataiIntegrationBatch)
    {
        List<DataiIntegrationBatch> list = dataiIntegrationBatchService.selectDataiIntegrationBatchList(dataiIntegrationBatch);
        ExcelUtil<DataiIntegrationBatch> util = new ExcelUtil<DataiIntegrationBatch>(DataiIntegrationBatch.class);
        util.exportExcel(response, list, "数据批次数据");
    }

    /**
     * 获取数据批次详细信息
     */
    @Operation(summary = "获取数据批次详细信息")
    @PreAuthorize("@ss.hasPermi('integration:batch:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id)
    {
        return success(dataiIntegrationBatchService.selectDataiIntegrationBatchById(id));
    }

    /**
     * 新增数据批次
     */
    @Operation(summary = "新增数据批次")
    @PreAuthorize("@ss.hasPermi('integration:batch:add')")
    @Log(title = "数据批次", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiIntegrationBatch dataiIntegrationBatch)
    {
        return toAjax(dataiIntegrationBatchService.insertDataiIntegrationBatch(dataiIntegrationBatch));
    }

    /**
     * 修改数据批次
     */
    @Operation(summary = "修改数据批次")
    @PreAuthorize("@ss.hasPermi('integration:batch:edit')")
    @Log(title = "数据批次", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiIntegrationBatch dataiIntegrationBatch)
    {
        return toAjax(dataiIntegrationBatchService.updateDataiIntegrationBatch(dataiIntegrationBatch));
    }

    /**
     * 删除数据批次
     */
    @Operation(summary = "删除数据批次")
    @PreAuthorize("@ss.hasPermi('integration:batch:remove')")
    @Log(title = "数据批次", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) Integer[] ids) 
    {
        return toAjax(dataiIntegrationBatchService.deleteDataiIntegrationBatchByIds(ids));
    }

    /**
     * 重试失败的批次
     */
    @Operation(summary = "重试失败的批次")
    @PreAuthorize("@ss.hasPermi('integration:batch:retry')")
    @Log(title = "数据批次", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/retry")
    public AjaxResult retryFailed(@PathVariable("id") Integer id)
    {
        return toAjax(dataiIntegrationBatchService.retryFailed(id) ? 1 : 0);
    }

    /**
     * 获取批次同步统计信息
     */
    @Operation(summary = "获取批次同步统计信息")
    @PreAuthorize("@ss.hasPermi('integration:batch:statistics')")
    @GetMapping("/{id}/statistics")
    public AjaxResult getSyncStatistics(@PathVariable("id") Integer id)
    {
        return success(dataiIntegrationBatchService.getSyncStatistics(id));
    }

    /**
     * 同步批次数据
     */
    @Operation(summary = "同步批次数据")
    @PreAuthorize("@ss.hasPermi('integration:batch:sync')")
    @Log(title = "数据批次", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/sync")
    public AjaxResult syncBatchData(@PathVariable("id") Integer id)
    {
        Map<String, Object> result = dataiIntegrationBatchService.syncBatchData(id);
        if ((Boolean) result.get("success")) {
            return success(result);
        } else {
            return error((String) result.get("message"));
        }
    }
}
