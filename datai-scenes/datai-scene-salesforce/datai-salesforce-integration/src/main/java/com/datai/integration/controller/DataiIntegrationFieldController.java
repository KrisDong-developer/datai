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
import com.datai.integration.domain.DataiIntegrationField;
import com.datai.integration.service.IDataiIntegrationFieldService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 对象字段信息Controller
 * 
 * @author datai
 * @date 2025-12-22
 */
@RestController
@RequestMapping("/integration/field")
@Tag(name = "【对象字段信息】管理")
public class DataiIntegrationFieldController extends BaseController
{
    @Autowired
    private IDataiIntegrationFieldService dataiIntegrationFieldService;

    /**
     * 查询对象字段信息列表
     */
    @Operation(summary = "查询对象字段信息列表")
    @PreAuthorize("@ss.hasPermi('integration:field:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiIntegrationField dataiIntegrationField)
    {
        startPage();
        List<DataiIntegrationField> list = dataiIntegrationFieldService.selectDataiIntegrationFieldList(dataiIntegrationField);
        return getDataTable(list);
    }

    /**
     * 导出对象字段信息列表
     */
    @Operation(summary = "导出对象字段信息列表")
    @PreAuthorize("@ss.hasPermi('integration:field:export')")
    @Log(title = "对象字段信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiIntegrationField dataiIntegrationField)
    {
        List<DataiIntegrationField> list = dataiIntegrationFieldService.selectDataiIntegrationFieldList(dataiIntegrationField);
        ExcelUtil<DataiIntegrationField> util = new ExcelUtil<DataiIntegrationField>(DataiIntegrationField.class);
        util.exportExcel(response, list, "对象字段信息数据");
    }

    /**
     * 获取对象字段信息详细信息
     */
    @Operation(summary = "获取对象字段信息详细信息")
    @PreAuthorize("@ss.hasPermi('integration:field:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dataiIntegrationFieldService.selectDataiIntegrationFieldById(id));
    }

    /**
     * 新增对象字段信息
     */
    @Operation(summary = "新增对象字段信息")
    @PreAuthorize("@ss.hasPermi('integration:field:add')")
    @Log(title = "对象字段信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiIntegrationField dataiIntegrationField)
    {
        return toAjax(dataiIntegrationFieldService.insertDataiIntegrationField(dataiIntegrationField));
    }

    /**
     * 修改对象字段信息
     */
    @Operation(summary = "修改对象字段信息")
    @PreAuthorize("@ss.hasPermi('integration:field:edit')")
    @Log(title = "对象字段信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiIntegrationField dataiIntegrationField)
    {
        return toAjax(dataiIntegrationFieldService.updateDataiIntegrationField(dataiIntegrationField));
    }

    /**
     * 删除对象字段信息
     */
    @Operation(summary = "删除对象字段信息")
    @PreAuthorize("@ss.hasPermi('integration:field:remove')")
    @Log(title = "对象字段信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) Long[] ids) 
    {
        return toAjax(dataiIntegrationFieldService.deleteDataiIntegrationFieldByIds(ids));
    }
}
