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
import com.datai.integration.domain.DataiIntegrationPerformance;
import com.datai.integration.service.IDataiIntegrationPerformanceService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 同步性能监控Controller
 * 
 * @author datai
 * @date 2025-12-22
 */
@RestController
@RequestMapping("/integration/performance")
@Tag(name = "【同步性能监控】管理")
public class DataiIntegrationPerformanceController extends BaseController
{
    @Autowired
    private IDataiIntegrationPerformanceService dataiIntegrationPerformanceService;

    /**
     * 查询同步性能监控列表
     */
    @Operation(summary = "查询同步性能监控列表")
    @PreAuthorize("@ss.hasPermi('integration:performance:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiIntegrationPerformance dataiIntegrationPerformance)
    {
        startPage();
        List<DataiIntegrationPerformance> list = dataiIntegrationPerformanceService.selectDataiIntegrationPerformanceList(dataiIntegrationPerformance);
        return getDataTable(list);
    }

    /**
     * 导出同步性能监控列表
     */
    @Operation(summary = "导出同步性能监控列表")
    @PreAuthorize("@ss.hasPermi('integration:performance:export')")
    @Log(title = "同步性能监控", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiIntegrationPerformance dataiIntegrationPerformance)
    {
        List<DataiIntegrationPerformance> list = dataiIntegrationPerformanceService.selectDataiIntegrationPerformanceList(dataiIntegrationPerformance);
        ExcelUtil<DataiIntegrationPerformance> util = new ExcelUtil<DataiIntegrationPerformance>(DataiIntegrationPerformance.class);
        util.exportExcel(response, list, "同步性能监控数据");
    }

    /**
     * 获取同步性能监控详细信息
     */
    @Operation(summary = "获取同步性能监控详细信息")
    @PreAuthorize("@ss.hasPermi('integration:performance:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dataiIntegrationPerformanceService.selectDataiIntegrationPerformanceById(id));
    }

    /**
     * 新增同步性能监控
     */
    @Operation(summary = "新增同步性能监控")
    @PreAuthorize("@ss.hasPermi('integration:performance:add')")
    @Log(title = "同步性能监控", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiIntegrationPerformance dataiIntegrationPerformance)
    {
        return toAjax(dataiIntegrationPerformanceService.insertDataiIntegrationPerformance(dataiIntegrationPerformance));
    }

    /**
     * 修改同步性能监控
     */
    @Operation(summary = "修改同步性能监控")
    @PreAuthorize("@ss.hasPermi('integration:performance:edit')")
    @Log(title = "同步性能监控", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiIntegrationPerformance dataiIntegrationPerformance)
    {
        return toAjax(dataiIntegrationPerformanceService.updateDataiIntegrationPerformance(dataiIntegrationPerformance));
    }

    /**
     * 删除同步性能监控
     */
    @Operation(summary = "删除同步性能监控")
    @PreAuthorize("@ss.hasPermi('integration:performance:remove')")
    @Log(title = "同步性能监控", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) Long[] ids) 
    {
        return toAjax(dataiIntegrationPerformanceService.deleteDataiIntegrationPerformanceByIds(ids));
    }
}
