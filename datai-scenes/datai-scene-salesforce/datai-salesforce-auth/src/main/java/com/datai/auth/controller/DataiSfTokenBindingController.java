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
import com.datai.auth.domain.DataiSfTokenBinding;
import com.datai.auth.service.IDataiSfTokenBindingService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 令牌绑定Controller
 * 
 * @author datai
 * @date 2025-12-24
 */
@RestController
@RequestMapping("/auth/binding")
@Tag(name = "【令牌绑定】管理")
public class DataiSfTokenBindingController extends BaseController
{
    @Autowired
    private IDataiSfTokenBindingService dataiSfTokenBindingService;

    /**
     * 查询令牌绑定列表
     */
    @Operation(summary = "查询令牌绑定列表")
    @PreAuthorize("@ss.hasPermi('auth:binding:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiSfTokenBinding dataiSfTokenBinding)
    {
        startPage();
        List<DataiSfTokenBinding> list = dataiSfTokenBindingService.selectDataiSfTokenBindingList(dataiSfTokenBinding);
        return getDataTable(list);
    }

    /**
     * 导出令牌绑定列表
     */
    @Operation(summary = "导出令牌绑定列表")
    @PreAuthorize("@ss.hasPermi('auth:binding:export')")
    @Log(title = "令牌绑定", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiSfTokenBinding dataiSfTokenBinding)
    {
        List<DataiSfTokenBinding> list = dataiSfTokenBindingService.selectDataiSfTokenBindingList(dataiSfTokenBinding);
        ExcelUtil<DataiSfTokenBinding> util = new ExcelUtil<DataiSfTokenBinding>(DataiSfTokenBinding.class);
        util.exportExcel(response, list, "令牌绑定数据");
    }

    /**
     * 获取令牌绑定详细信息
     */
    @Operation(summary = "获取令牌绑定详细信息")
    @PreAuthorize("@ss.hasPermi('auth:binding:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dataiSfTokenBindingService.selectDataiSfTokenBindingById(id));
    }

    /**
     * 新增令牌绑定
     */
    @Operation(summary = "新增令牌绑定")
    @PreAuthorize("@ss.hasPermi('auth:binding:add')")
    @Log(title = "令牌绑定", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiSfTokenBinding dataiSfTokenBinding)
    {
        return toAjax(dataiSfTokenBindingService.insertDataiSfTokenBinding(dataiSfTokenBinding));
    }

    /**
     * 修改令牌绑定
     */
    @Operation(summary = "修改令牌绑定")
    @PreAuthorize("@ss.hasPermi('auth:binding:edit')")
    @Log(title = "令牌绑定", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiSfTokenBinding dataiSfTokenBinding)
    {
        return toAjax(dataiSfTokenBindingService.updateDataiSfTokenBinding(dataiSfTokenBinding));
    }

    /**
     * 删除令牌绑定
     */
    @Operation(summary = "删除令牌绑定")
    @PreAuthorize("@ss.hasPermi('auth:binding:remove')")
    @Log(title = "令牌绑定", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) Long[] ids) 
    {
        return toAjax(dataiSfTokenBindingService.deleteDataiSfTokenBindingByIds(ids));
    }
}
