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
import com.datai.integration.domain.DataiIntegrationRateLimit;
import com.datai.integration.service.IDataiIntegrationRateLimitService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * API限流管理Controller
 * 
 * @author datai
 * @date 2025-12-22
 */
@RestController
@RequestMapping("/integration/limit")
@Tag(name = "【API限流管理】管理")
public class DataiIntegrationRateLimitController extends BaseController
{
    @Autowired
    private IDataiIntegrationRateLimitService dataiIntegrationRateLimitService;

    /**
     * 查询API限流管理列表
     */
    @Operation(summary = "查询API限流管理列表")
    @PreAuthorize("@ss.hasPermi('integration:limit:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiIntegrationRateLimit dataiIntegrationRateLimit)
    {
        startPage();
        List<DataiIntegrationRateLimit> list = dataiIntegrationRateLimitService.selectDataiIntegrationRateLimitList(dataiIntegrationRateLimit);
        return getDataTable(list);
    }

    /**
     * 导出API限流管理列表
     */
    @Operation(summary = "导出API限流管理列表")
    @PreAuthorize("@ss.hasPermi('integration:limit:export')")
    @Log(title = "API限流管理", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiIntegrationRateLimit dataiIntegrationRateLimit)
    {
        List<DataiIntegrationRateLimit> list = dataiIntegrationRateLimitService.selectDataiIntegrationRateLimitList(dataiIntegrationRateLimit);
        ExcelUtil<DataiIntegrationRateLimit> util = new ExcelUtil<DataiIntegrationRateLimit>(DataiIntegrationRateLimit.class);
        util.exportExcel(response, list, "API限流管理数据");
    }

    /**
     * 获取API限流管理详细信息
     */
    @Operation(summary = "获取API限流管理详细信息")
    @PreAuthorize("@ss.hasPermi('integration:limit:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dataiIntegrationRateLimitService.selectDataiIntegrationRateLimitById(id));
    }

    /**
     * 新增API限流管理
     */
    @Operation(summary = "新增API限流管理")
    @PreAuthorize("@ss.hasPermi('integration:limit:add')")
    @Log(title = "API限流管理", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiIntegrationRateLimit dataiIntegrationRateLimit)
    {
        return toAjax(dataiIntegrationRateLimitService.insertDataiIntegrationRateLimit(dataiIntegrationRateLimit));
    }

    /**
     * 修改API限流管理
     */
    @Operation(summary = "修改API限流管理")
    @PreAuthorize("@ss.hasPermi('integration:limit:edit')")
    @Log(title = "API限流管理", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiIntegrationRateLimit dataiIntegrationRateLimit)
    {
        return toAjax(dataiIntegrationRateLimitService.updateDataiIntegrationRateLimit(dataiIntegrationRateLimit));
    }

    /**
     * 删除API限流管理
     */
    @Operation(summary = "删除API限流管理")
    @PreAuthorize("@ss.hasPermi('integration:limit:remove')")
    @Log(title = "API限流管理", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) Long[] ids) 
    {
        return toAjax(dataiIntegrationRateLimitService.deleteDataiIntegrationRateLimitByIds(ids));
    }
}
