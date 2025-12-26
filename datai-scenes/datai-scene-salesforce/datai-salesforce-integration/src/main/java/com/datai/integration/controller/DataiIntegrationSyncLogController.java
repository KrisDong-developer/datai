package com.datai.integration.controller;

import java.util.List;
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
import com.datai.integration.domain.DataiIntegrationSyncLog;
import com.datai.integration.service.IDataiIntegrationSyncLogService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 数据同步日志Controller
 * 
 * @author datai
 * @date 2025-12-26
 */
@RestController
@RequestMapping("/integration/synclog")
@Tag(name = "【数据同步日志】管理")
public class DataiIntegrationSyncLogController extends BaseController
{
    @Autowired
    private IDataiIntegrationSyncLogService dataiIntegrationSyncLogService;

    /**
     * 查询数据同步日志列表
     */
    @Operation(summary = "查询数据同步日志列表")
    @PreAuthorize("@ss.hasPermi('integration:synclog:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiIntegrationSyncLog dataiIntegrationSyncLog)
    {
        startPage();
        List<DataiIntegrationSyncLog> list = dataiIntegrationSyncLogService.selectDataiIntegrationSyncLogList(dataiIntegrationSyncLog);
        return getDataTable(list);
    }

    /**
     * 导出数据同步日志列表
     */
    @Operation(summary = "导出数据同步日志列表")
    @PreAuthorize("@ss.hasPermi('integration:synclog:export')")
    @Log(title = "数据同步日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiIntegrationSyncLog dataiIntegrationSyncLog)
    {
        List<DataiIntegrationSyncLog> list = dataiIntegrationSyncLogService.selectDataiIntegrationSyncLogList(dataiIntegrationSyncLog);
        ExcelUtil<DataiIntegrationSyncLog> util = new ExcelUtil<DataiIntegrationSyncLog>(DataiIntegrationSyncLog.class);
        util.exportExcel(response, list, "数据同步日志数据");
    }

    /**
     * 获取数据同步日志详细信息
     */
    @Operation(summary = "获取数据同步日志详细信息")
    @PreAuthorize("@ss.hasPermi('integration:synclog:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dataiIntegrationSyncLogService.selectDataiIntegrationSyncLogById(id));
    }

    /**
     * 新增数据同步日志
     */
    @Operation(summary = "新增数据同步日志")
    @PreAuthorize("@ss.hasPermi('integration:synclog:add')")
    @Log(title = "数据同步日志", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiIntegrationSyncLog dataiIntegrationSyncLog)
    {
        return toAjax(dataiIntegrationSyncLogService.insertDataiIntegrationSyncLog(dataiIntegrationSyncLog));
    }

    /**
     * 修改数据同步日志
     */
    @Operation(summary = "修改数据同步日志")
    @PreAuthorize("@ss.hasPermi('integration:synclog:edit')")
    @Log(title = "数据同步日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiIntegrationSyncLog dataiIntegrationSyncLog)
    {
        return toAjax(dataiIntegrationSyncLogService.updateDataiIntegrationSyncLog(dataiIntegrationSyncLog));
    }

    /**
     * 删除数据同步日志
     */
    @Operation(summary = "删除数据同步日志")
    @PreAuthorize("@ss.hasPermi('integration:synclog:remove')")
    @Log(title = "数据同步日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) Long[] ids) 
    {
        return toAjax(dataiIntegrationSyncLogService.deleteDataiIntegrationSyncLogByIds(ids));
    }
}
