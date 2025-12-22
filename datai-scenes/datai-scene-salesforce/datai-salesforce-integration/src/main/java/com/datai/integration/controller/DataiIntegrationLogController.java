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
import com.datai.integration.domain.DataiIntegrationLog;
import com.datai.integration.service.IDataiIntegrationLogService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 数据同步日志Controller
 * 
 * @author datai
 * @date 2025-12-22
 */
@RestController
@RequestMapping("/integration/syncLog")
@Tag(name = "【数据同步日志】管理")
public class DataiIntegrationLogController extends BaseController
{
    @Autowired
    private IDataiIntegrationLogService dataiIntegrationLogService;

    /**
     * 查询数据同步日志列表
     */
    @Operation(summary = "查询数据同步日志列表")
    @PreAuthorize("@ss.hasPermi('integration:syncLog:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiIntegrationLog dataiIntegrationLog)
    {
        startPage();
        List<DataiIntegrationLog> list = dataiIntegrationLogService.selectDataiIntegrationLogList(dataiIntegrationLog);
        return getDataTable(list);
    }

    /**
     * 导出数据同步日志列表
     */
    @Operation(summary = "导出数据同步日志列表")
    @PreAuthorize("@ss.hasPermi('integration:syncLog:export')")
    @Log(title = "数据同步日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiIntegrationLog dataiIntegrationLog)
    {
        List<DataiIntegrationLog> list = dataiIntegrationLogService.selectDataiIntegrationLogList(dataiIntegrationLog);
        ExcelUtil<DataiIntegrationLog> util = new ExcelUtil<DataiIntegrationLog>(DataiIntegrationLog.class);
        util.exportExcel(response, list, "数据同步日志数据");
    }

    /**
     * 获取数据同步日志详细信息
     */
    @Operation(summary = "获取数据同步日志详细信息")
    @PreAuthorize("@ss.hasPermi('integration:syncLog:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dataiIntegrationLogService.selectDataiIntegrationLogById(id));
    }

    /**
     * 新增数据同步日志
     */
    @Operation(summary = "新增数据同步日志")
    @PreAuthorize("@ss.hasPermi('integration:syncLog:add')")
    @Log(title = "数据同步日志", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiIntegrationLog dataiIntegrationLog)
    {
        return toAjax(dataiIntegrationLogService.insertDataiIntegrationLog(dataiIntegrationLog));
    }

    /**
     * 修改数据同步日志
     */
    @Operation(summary = "修改数据同步日志")
    @PreAuthorize("@ss.hasPermi('integration:syncLog:edit')")
    @Log(title = "数据同步日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiIntegrationLog dataiIntegrationLog)
    {
        return toAjax(dataiIntegrationLogService.updateDataiIntegrationLog(dataiIntegrationLog));
    }

    /**
     * 删除数据同步日志
     */
    @Operation(summary = "删除数据同步日志")
    @PreAuthorize("@ss.hasPermi('integration:syncLog:remove')")
    @Log(title = "数据同步日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) Long[] ids) 
    {
        return toAjax(dataiIntegrationLogService.deleteDataiIntegrationLogByIds(ids));
    }
}
