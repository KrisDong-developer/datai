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
import com.datai.setting.domain.DataiConfigVersion;
import com.datai.setting.service.IDataiConfigVersionService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 配置版本Controller
 * 
 * @author datai
 * @date 2025-12-24
 */
@RestController
@RequestMapping("/setting/version")
@Tag(name = "【配置版本】管理")
public class DataiConfigVersionController extends BaseController
{
    @Autowired
    private IDataiConfigVersionService dataiConfigVersionService;

    /**
     * 查询配置版本列表
     */
    @Operation(summary = "查询配置版本列表")
    @PreAuthorize("@ss.hasPermi('setting:version:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiConfigVersion dataiConfigVersion)
    {
        startPage();
        List<DataiConfigVersion> list = dataiConfigVersionService.selectDataiConfigVersionList(dataiConfigVersion);
        return getDataTable(list);
    }

    /**
     * 导出配置版本列表
     */
    @Operation(summary = "导出配置版本列表")
    @PreAuthorize("@ss.hasPermi('setting:version:export')")
    @Log(title = "配置版本", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiConfigVersion dataiConfigVersion)
    {
        List<DataiConfigVersion> list = dataiConfigVersionService.selectDataiConfigVersionList(dataiConfigVersion);
        ExcelUtil<DataiConfigVersion> util = new ExcelUtil<DataiConfigVersion>(DataiConfigVersion.class);
        util.exportExcel(response, list, "配置版本数据");
    }

    /**
     * 获取配置版本详细信息
     */
    @Operation(summary = "获取配置版本详细信息")
    @PreAuthorize("@ss.hasPermi('setting:version:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dataiConfigVersionService.selectDataiConfigVersionById(id));
    }

    /**
     * 新增配置版本
     */
    @Operation(summary = "新增配置版本")
    @PreAuthorize("@ss.hasPermi('setting:version:add')")
    @Log(title = "配置版本", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiConfigVersion dataiConfigVersion)
    {
        return toAjax(dataiConfigVersionService.insertDataiConfigVersion(dataiConfigVersion));
    }

    /**
     * 修改配置版本
     */
    @Operation(summary = "修改配置版本")
    @PreAuthorize("@ss.hasPermi('setting:version:edit')")
    @Log(title = "配置版本", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiConfigVersion dataiConfigVersion)
    {
        return toAjax(dataiConfigVersionService.updateDataiConfigVersion(dataiConfigVersion));
    }

    /**
     * 删除配置版本
     */
    @Operation(summary = "删除配置版本")
    @PreAuthorize("@ss.hasPermi('setting:version:remove')")
    @Log(title = "配置版本", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) Long[] ids) 
    {
        return toAjax(dataiConfigVersionService.deleteDataiConfigVersionByIds(ids));
    }
}
