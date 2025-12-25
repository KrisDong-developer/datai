package com.datai.setting.controller;

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
import com.datai.setting.domain.DataiConfigEnvironment;
import com.datai.setting.service.IDataiConfigEnvironmentService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 配置环境Controller
 * 
 * @author datai
 * @date 2025-12-24
 */
@RestController
@RequestMapping("/setting/environment")
@Tag(name = "【配置环境】管理")
public class DataiConfigEnvironmentController extends BaseController
{
    @Autowired
    private IDataiConfigEnvironmentService dataiConfigEnvironmentService;

    /**
     * 查询配置环境列表
     */
    @Operation(summary = "查询配置环境列表")
    @PreAuthorize("@ss.hasPermi('setting:environment:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiConfigEnvironment dataiConfigEnvironment)
    {
        startPage();
        List<DataiConfigEnvironment> list = dataiConfigEnvironmentService.selectDataiConfigEnvironmentList(dataiConfigEnvironment);
        return getDataTable(list);
    }

    /**
     * 导出配置环境列表
     */
    @Operation(summary = "导出配置环境列表")
    @PreAuthorize("@ss.hasPermi('setting:environment:export')")
    @Log(title = "配置环境", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiConfigEnvironment dataiConfigEnvironment)
    {
        List<DataiConfigEnvironment> list = dataiConfigEnvironmentService.selectDataiConfigEnvironmentList(dataiConfigEnvironment);
        ExcelUtil<DataiConfigEnvironment> util = new ExcelUtil<DataiConfigEnvironment>(DataiConfigEnvironment.class);
        util.exportExcel(response, list, "配置环境数据");
    }

    /**
     * 获取配置环境详细信息
     */
    @Operation(summary = "获取配置环境详细信息")
    @PreAuthorize("@ss.hasPermi('setting:environment:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dataiConfigEnvironmentService.selectDataiConfigEnvironmentById(id));
    }

    /**
     * 新增配置环境
     */
    @Operation(summary = "新增配置环境")
    @PreAuthorize("@ss.hasPermi('setting:environment:add')")
    @Log(title = "配置环境", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiConfigEnvironment dataiConfigEnvironment)
    {
        return toAjax(dataiConfigEnvironmentService.insertDataiConfigEnvironment(dataiConfigEnvironment));
    }

    /**
     * 修改配置环境
     */
    @Operation(summary = "修改配置环境")
    @PreAuthorize("@ss.hasPermi('setting:environment:edit')")
    @Log(title = "配置环境", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiConfigEnvironment dataiConfigEnvironment)
    {
        return toAjax(dataiConfigEnvironmentService.updateDataiConfigEnvironment(dataiConfigEnvironment));
    }

    /**
     * 删除配置环境
     */
    @Operation(summary = "删除配置环境")
    @PreAuthorize("@ss.hasPermi('setting:environment:remove')")
    @Log(title = "配置环境", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) Long[] ids) 
    {
        return toAjax(dataiConfigEnvironmentService.deleteDataiConfigEnvironmentByIds(ids));
    }
}
