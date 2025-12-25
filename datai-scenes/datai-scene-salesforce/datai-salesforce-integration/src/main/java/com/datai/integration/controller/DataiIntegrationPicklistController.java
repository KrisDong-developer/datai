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
import com.datai.integration.domain.DataiIntegrationPicklist;
import com.datai.integration.service.IDataiIntegrationPicklistService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 字段选择列表信息Controller
 * 
 * @author datai
 * @date 2025-12-24
 */
@RestController
@RequestMapping("/integration/picklist")
@Tag(name = "【字段选择列表信息】管理")
public class DataiIntegrationPicklistController extends BaseController
{
    @Autowired
    private IDataiIntegrationPicklistService dataiIntegrationPicklistService;

    /**
     * 查询字段选择列表信息列表
     */
    @Operation(summary = "查询字段选择列表信息列表")
    @PreAuthorize("@ss.hasPermi('integration:picklist:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiIntegrationPicklist dataiIntegrationPicklist)
    {
        startPage();
        List<DataiIntegrationPicklist> list = dataiIntegrationPicklistService.selectDataiIntegrationPicklistList(dataiIntegrationPicklist);
        return getDataTable(list);
    }

    /**
     * 导出字段选择列表信息列表
     */
    @Operation(summary = "导出字段选择列表信息列表")
    @PreAuthorize("@ss.hasPermi('integration:picklist:export')")
    @Log(title = "字段选择列表信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiIntegrationPicklist dataiIntegrationPicklist)
    {
        List<DataiIntegrationPicklist> list = dataiIntegrationPicklistService.selectDataiIntegrationPicklistList(dataiIntegrationPicklist);
        ExcelUtil<DataiIntegrationPicklist> util = new ExcelUtil<DataiIntegrationPicklist>(DataiIntegrationPicklist.class);
        util.exportExcel(response, list, "字段选择列表信息数据");
    }

    /**
     * 获取字段选择列表信息详细信息
     */
    @Operation(summary = "获取字段选择列表信息详细信息")
    @PreAuthorize("@ss.hasPermi('integration:picklist:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id)
    {
        return success(dataiIntegrationPicklistService.selectDataiIntegrationPicklistById(id));
    }

    /**
     * 新增字段选择列表信息
     */
    @Operation(summary = "新增字段选择列表信息")
    @PreAuthorize("@ss.hasPermi('integration:picklist:add')")
    @Log(title = "字段选择列表信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiIntegrationPicklist dataiIntegrationPicklist)
    {
        return toAjax(dataiIntegrationPicklistService.insertDataiIntegrationPicklist(dataiIntegrationPicklist));
    }

    /**
     * 修改字段选择列表信息
     */
    @Operation(summary = "修改字段选择列表信息")
    @PreAuthorize("@ss.hasPermi('integration:picklist:edit')")
    @Log(title = "字段选择列表信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiIntegrationPicklist dataiIntegrationPicklist)
    {
        return toAjax(dataiIntegrationPicklistService.updateDataiIntegrationPicklist(dataiIntegrationPicklist));
    }

    /**
     * 删除字段选择列表信息
     */
    @Operation(summary = "删除字段选择列表信息")
    @PreAuthorize("@ss.hasPermi('integration:picklist:remove')")
    @Log(title = "字段选择列表信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) Integer[] ids) 
    {
        return toAjax(dataiIntegrationPicklistService.deleteDataiIntegrationPicklistByIds(ids));
    }
}
