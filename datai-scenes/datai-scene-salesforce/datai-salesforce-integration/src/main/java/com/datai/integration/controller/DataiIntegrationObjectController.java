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
import com.datai.integration.domain.DataiIntegrationObject;
import com.datai.integration.service.IDataiIntegrationObjectService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 对象同步控制Controller
 * 
 * @author datai
 * @date 2025-12-24
 */
@RestController
@RequestMapping("/integration/object")
@Tag(name = "【对象同步控制】管理")
public class DataiIntegrationObjectController extends BaseController
{
    @Autowired
    private IDataiIntegrationObjectService dataiIntegrationObjectService;

    /**
     * 查询对象同步控制列表
     */
    @Operation(summary = "查询对象同步控制列表")
    @PreAuthorize("@ss.hasPermi('integration:object:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiIntegrationObject dataiIntegrationObject)
    {
        startPage();
        List<DataiIntegrationObject> list = dataiIntegrationObjectService.selectDataiIntegrationObjectList(dataiIntegrationObject);
        return getDataTable(list);
    }

    /**
     * 导出对象同步控制列表
     */
    @Operation(summary = "导出对象同步控制列表")
    @PreAuthorize("@ss.hasPermi('integration:object:export')")
    @Log(title = "对象同步控制", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiIntegrationObject dataiIntegrationObject)
    {
        List<DataiIntegrationObject> list = dataiIntegrationObjectService.selectDataiIntegrationObjectList(dataiIntegrationObject);
        ExcelUtil<DataiIntegrationObject> util = new ExcelUtil<DataiIntegrationObject>(DataiIntegrationObject.class);
        util.exportExcel(response, list, "对象同步控制数据");
    }

    /**
     * 获取对象同步控制详细信息
     */
    @Operation(summary = "获取对象同步控制详细信息")
    @PreAuthorize("@ss.hasPermi('integration:object:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id)
    {
        return success(dataiIntegrationObjectService.selectDataiIntegrationObjectById(id));
    }

    /**
     * 新增对象同步控制
     */
    @Operation(summary = "新增对象同步控制")
    @PreAuthorize("@ss.hasPermi('integration:object:add')")
    @Log(title = "对象同步控制", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiIntegrationObject dataiIntegrationObject)
    {
        return toAjax(dataiIntegrationObjectService.insertDataiIntegrationObject(dataiIntegrationObject));
    }

    /**
     * 修改对象同步控制
     */
    @Operation(summary = "修改对象同步控制")
    @PreAuthorize("@ss.hasPermi('integration:object:edit')")
    @Log(title = "对象同步控制", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiIntegrationObject dataiIntegrationObject)
    {
        return toAjax(dataiIntegrationObjectService.updateDataiIntegrationObject(dataiIntegrationObject));
    }

    /**
     * 删除对象同步控制
     */
    @Operation(summary = "删除对象同步控制")
    @PreAuthorize("@ss.hasPermi('integration:object:remove')")
    @Log(title = "对象同步控制", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) Integer[] ids) 
    {
        return toAjax(dataiIntegrationObjectService.deleteDataiIntegrationObjectByIds(ids));
    }
}
