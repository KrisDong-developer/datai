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
import com.datai.integration.domain.DataiIntegrationFilterLookup;
import com.datai.integration.service.IDataiIntegrationFilterLookupService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 字段过滤查找信息Controller
 * 
 * @author datai
 * @date 2025-12-24
 */
@RestController
@RequestMapping("/integration/lookup")
@Tag(name = "【字段过滤查找信息】管理")
public class DataiIntegrationFilterLookupController extends BaseController
{
    @Autowired
    private IDataiIntegrationFilterLookupService dataiIntegrationFilterLookupService;

    /**
     * 查询字段过滤查找信息列表
     */
    @Operation(summary = "查询字段过滤查找信息列表")
    @PreAuthorize("@ss.hasPermi('integration:lookup:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiIntegrationFilterLookup dataiIntegrationFilterLookup)
    {
        startPage();
        List<DataiIntegrationFilterLookup> list = dataiIntegrationFilterLookupService.selectDataiIntegrationFilterLookupList(dataiIntegrationFilterLookup);
        return getDataTable(list);
    }

    /**
     * 导出字段过滤查找信息列表
     */
    @Operation(summary = "导出字段过滤查找信息列表")
    @PreAuthorize("@ss.hasPermi('integration:lookup:export')")
    @Log(title = "字段过滤查找信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiIntegrationFilterLookup dataiIntegrationFilterLookup)
    {
        List<DataiIntegrationFilterLookup> list = dataiIntegrationFilterLookupService.selectDataiIntegrationFilterLookupList(dataiIntegrationFilterLookup);
        ExcelUtil<DataiIntegrationFilterLookup> util = new ExcelUtil<DataiIntegrationFilterLookup>(DataiIntegrationFilterLookup.class);
        util.exportExcel(response, list, "字段过滤查找信息数据");
    }

    /**
     * 获取字段过滤查找信息详细信息
     */
    @Operation(summary = "获取字段过滤查找信息详细信息")
    @PreAuthorize("@ss.hasPermi('integration:lookup:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id)
    {
        return success(dataiIntegrationFilterLookupService.selectDataiIntegrationFilterLookupById(id));
    }

    /**
     * 新增字段过滤查找信息
     */
    @Operation(summary = "新增字段过滤查找信息")
    @PreAuthorize("@ss.hasPermi('integration:lookup:add')")
    @Log(title = "字段过滤查找信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiIntegrationFilterLookup dataiIntegrationFilterLookup)
    {
        return toAjax(dataiIntegrationFilterLookupService.insertDataiIntegrationFilterLookup(dataiIntegrationFilterLookup));
    }

    /**
     * 修改字段过滤查找信息
     */
    @Operation(summary = "修改字段过滤查找信息")
    @PreAuthorize("@ss.hasPermi('integration:lookup:edit')")
    @Log(title = "字段过滤查找信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiIntegrationFilterLookup dataiIntegrationFilterLookup)
    {
        return toAjax(dataiIntegrationFilterLookupService.updateDataiIntegrationFilterLookup(dataiIntegrationFilterLookup));
    }

    /**
     * 删除字段过滤查找信息
     */
    @Operation(summary = "删除字段过滤查找信息")
    @PreAuthorize("@ss.hasPermi('integration:lookup:remove')")
    @Log(title = "字段过滤查找信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) Integer[] ids) 
    {
        return toAjax(dataiIntegrationFilterLookupService.deleteDataiIntegrationFilterLookupByIds(ids));
    }
}
