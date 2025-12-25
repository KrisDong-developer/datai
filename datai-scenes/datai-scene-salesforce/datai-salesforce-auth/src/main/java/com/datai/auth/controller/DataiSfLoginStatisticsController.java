package com.datai.auth.controller;

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
import com.datai.auth.domain.DataiSfLoginStatistics;
import com.datai.auth.service.IDataiSfLoginStatisticsService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 登录统计Controller
 * 
 * @author datai
 * @date 2025-12-24
 */
@RestController
@RequestMapping("/auth/statistics")
@Tag(name = "【登录统计】管理")
public class DataiSfLoginStatisticsController extends BaseController
{
    @Autowired
    private IDataiSfLoginStatisticsService dataiSfLoginStatisticsService;

    /**
     * 查询登录统计列表
     */
    @Operation(summary = "查询登录统计列表")
    @PreAuthorize("@ss.hasPermi('auth:statistics:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiSfLoginStatistics dataiSfLoginStatistics)
    {
        startPage();
        List<DataiSfLoginStatistics> list = dataiSfLoginStatisticsService.selectDataiSfLoginStatisticsList(dataiSfLoginStatistics);
        return getDataTable(list);
    }

    /**
     * 导出登录统计列表
     */
    @Operation(summary = "导出登录统计列表")
    @PreAuthorize("@ss.hasPermi('auth:statistics:export')")
    @Log(title = "登录统计", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiSfLoginStatistics dataiSfLoginStatistics)
    {
        List<DataiSfLoginStatistics> list = dataiSfLoginStatisticsService.selectDataiSfLoginStatisticsList(dataiSfLoginStatistics);
        ExcelUtil<DataiSfLoginStatistics> util = new ExcelUtil<DataiSfLoginStatistics>(DataiSfLoginStatistics.class);
        util.exportExcel(response, list, "登录统计数据");
    }

    /**
     * 获取登录统计详细信息
     */
    @Operation(summary = "获取登录统计详细信息")
    @PreAuthorize("@ss.hasPermi('auth:statistics:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dataiSfLoginStatisticsService.selectDataiSfLoginStatisticsById(id));
    }

    /**
     * 新增登录统计
     */
    @Operation(summary = "新增登录统计")
    @PreAuthorize("@ss.hasPermi('auth:statistics:add')")
    @Log(title = "登录统计", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiSfLoginStatistics dataiSfLoginStatistics)
    {
        return toAjax(dataiSfLoginStatisticsService.insertDataiSfLoginStatistics(dataiSfLoginStatistics));
    }

    /**
     * 修改登录统计
     */
    @Operation(summary = "修改登录统计")
    @PreAuthorize("@ss.hasPermi('auth:statistics:edit')")
    @Log(title = "登录统计", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiSfLoginStatistics dataiSfLoginStatistics)
    {
        return toAjax(dataiSfLoginStatisticsService.updateDataiSfLoginStatistics(dataiSfLoginStatistics));
    }

    /**
     * 删除登录统计
     */
    @Operation(summary = "删除登录统计")
    @PreAuthorize("@ss.hasPermi('auth:statistics:remove')")
    @Log(title = "登录统计", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) Long[] ids) 
    {
        return toAjax(dataiSfLoginStatisticsService.deleteDataiSfLoginStatisticsByIds(ids));
    }
}
