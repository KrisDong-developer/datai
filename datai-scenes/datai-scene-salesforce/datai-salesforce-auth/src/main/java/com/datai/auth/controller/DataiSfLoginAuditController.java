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
import com.datai.auth.domain.DataiSfLoginAudit;
import com.datai.auth.service.IDataiSfLoginAuditService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 登录审计日志Controller
 * 
 * @author datai
 * @date 2025-12-24
 */
@RestController
@RequestMapping("/auth/audit")
@Tag(name = "【登录审计日志】管理")
public class DataiSfLoginAuditController extends BaseController
{
    @Autowired
    private IDataiSfLoginAuditService dataiSfLoginAuditService;

    /**
     * 查询登录审计日志列表
     */
    @Operation(summary = "查询登录审计日志列表")
    @PreAuthorize("@ss.hasPermi('auth:audit:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiSfLoginAudit dataiSfLoginAudit)
    {
        startPage();
        List<DataiSfLoginAudit> list = dataiSfLoginAuditService.selectDataiSfLoginAuditList(dataiSfLoginAudit);
        return getDataTable(list);
    }

    /**
     * 导出登录审计日志列表
     */
    @Operation(summary = "导出登录审计日志列表")
    @PreAuthorize("@ss.hasPermi('auth:audit:export')")
    @Log(title = "登录审计日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiSfLoginAudit dataiSfLoginAudit)
    {
        List<DataiSfLoginAudit> list = dataiSfLoginAuditService.selectDataiSfLoginAuditList(dataiSfLoginAudit);
        ExcelUtil<DataiSfLoginAudit> util = new ExcelUtil<DataiSfLoginAudit>(DataiSfLoginAudit.class);
        util.exportExcel(response, list, "登录审计日志数据");
    }

    /**
     * 获取登录审计日志详细信息
     */
    @Operation(summary = "获取登录审计日志详细信息")
    @PreAuthorize("@ss.hasPermi('auth:audit:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dataiSfLoginAuditService.selectDataiSfLoginAuditById(id));
    }

    /**
     * 新增登录审计日志
     */
    @Operation(summary = "新增登录审计日志")
    @PreAuthorize("@ss.hasPermi('auth:audit:add')")
    @Log(title = "登录审计日志", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiSfLoginAudit dataiSfLoginAudit)
    {
        return toAjax(dataiSfLoginAuditService.insertDataiSfLoginAudit(dataiSfLoginAudit));
    }

    /**
     * 修改登录审计日志
     */
    @Operation(summary = "修改登录审计日志")
    @PreAuthorize("@ss.hasPermi('auth:audit:edit')")
    @Log(title = "登录审计日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiSfLoginAudit dataiSfLoginAudit)
    {
        return toAjax(dataiSfLoginAuditService.updateDataiSfLoginAudit(dataiSfLoginAudit));
    }

    /**
     * 删除登录审计日志
     */
    @Operation(summary = "删除登录审计日志")
    @PreAuthorize("@ss.hasPermi('auth:audit:remove')")
    @Log(title = "登录审计日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) Long[] ids) 
    {
        return toAjax(dataiSfLoginAuditService.deleteDataiSfLoginAuditByIds(ids));
    }
}
