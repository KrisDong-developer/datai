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
import com.datai.auth.domain.DataiSfFailedLogin;
import com.datai.auth.service.IDataiSfFailedLoginService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 失败登录Controller
 * 
 * @author datai
 * @date 2025-12-24
 */
@RestController
@RequestMapping("/auth/login")
@Tag(name = "【失败登录】管理")
public class DataiSfFailedLoginController extends BaseController
{
    @Autowired
    private IDataiSfFailedLoginService dataiSfFailedLoginService;

    /**
     * 查询失败登录列表
     */
    @Operation(summary = "查询失败登录列表")
    @PreAuthorize("@ss.hasPermi('auth:login:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiSfFailedLogin dataiSfFailedLogin)
    {
        startPage();
        List<DataiSfFailedLogin> list = dataiSfFailedLoginService.selectDataiSfFailedLoginList(dataiSfFailedLogin);
        return getDataTable(list);
    }

    /**
     * 导出失败登录列表
     */
    @Operation(summary = "导出失败登录列表")
    @PreAuthorize("@ss.hasPermi('auth:login:export')")
    @Log(title = "失败登录", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiSfFailedLogin dataiSfFailedLogin)
    {
        List<DataiSfFailedLogin> list = dataiSfFailedLoginService.selectDataiSfFailedLoginList(dataiSfFailedLogin);
        ExcelUtil<DataiSfFailedLogin> util = new ExcelUtil<DataiSfFailedLogin>(DataiSfFailedLogin.class);
        util.exportExcel(response, list, "失败登录数据");
    }

    /**
     * 获取失败登录详细信息
     */
    @Operation(summary = "获取失败登录详细信息")
    @PreAuthorize("@ss.hasPermi('auth:login:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dataiSfFailedLoginService.selectDataiSfFailedLoginById(id));
    }

    /**
     * 新增失败登录
     */
    @Operation(summary = "新增失败登录")
    @PreAuthorize("@ss.hasPermi('auth:login:add')")
    @Log(title = "失败登录", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiSfFailedLogin dataiSfFailedLogin)
    {
        return toAjax(dataiSfFailedLoginService.insertDataiSfFailedLogin(dataiSfFailedLogin));
    }

    /**
     * 修改失败登录
     */
    @Operation(summary = "修改失败登录")
    @PreAuthorize("@ss.hasPermi('auth:login:edit')")
    @Log(title = "失败登录", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiSfFailedLogin dataiSfFailedLogin)
    {
        return toAjax(dataiSfFailedLoginService.updateDataiSfFailedLogin(dataiSfFailedLogin));
    }

    /**
     * 删除失败登录
     */
    @Operation(summary = "删除失败登录")
    @PreAuthorize("@ss.hasPermi('auth:login:remove')")
    @Log(title = "失败登录", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) Long[] ids) 
    {
        return toAjax(dataiSfFailedLoginService.deleteDataiSfFailedLoginByIds(ids));
    }
}
