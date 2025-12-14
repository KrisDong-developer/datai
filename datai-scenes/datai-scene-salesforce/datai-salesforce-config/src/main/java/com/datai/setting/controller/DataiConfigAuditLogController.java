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
import com.datai.setting.domain.DataiConfigAuditLog;
import com.datai.setting.service.IDataiConfigAuditLogService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 配置审计日志Controller
 * 
 * @author datai
 * @date 2025-12-14
 */
@RestController
@RequestMapping("/setting/log")
@Tag(name = "【配置审计日志】管理")
public class DataiConfigAuditLogController extends BaseController
{
    @Autowired
    private IDataiConfigAuditLogService dataiConfigAuditLogService;

    /**
     * 查询配置审计日志列表
     */
    @Operation(summary = "查询配置审计日志列表")
    @PreAuthorize("@ss.hasPermi('setting:log:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiConfigAuditLog dataiConfigAuditLog)
    {
        startPage();
        List<DataiConfigAuditLog> list = dataiConfigAuditLogService.selectDataiConfigAuditLogList(dataiConfigAuditLog);
        return getDataTable(list);
    }

    /**
     * 导出配置审计日志列表
     */
    @Operation(summary = "导出配置审计日志列表")
    @PreAuthorize("@ss.hasPermi('setting:log:export')")
    @Log(title = "配置审计日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiConfigAuditLog dataiConfigAuditLog)
    {
        List<DataiConfigAuditLog> list = dataiConfigAuditLogService.selectDataiConfigAuditLogList(dataiConfigAuditLog);
        ExcelUtil<DataiConfigAuditLog> util = new ExcelUtil<DataiConfigAuditLog>(DataiConfigAuditLog.class);
        util.exportExcel(response, list, "配置审计日志数据");
    }

    /**
     * 获取配置审计日志详细信息
     */
    @Operation(summary = "获取配置审计日志详细信息")
    @PreAuthorize("@ss.hasPermi('setting:log:query')")
    @GetMapping(value = "/{logId}")
    public AjaxResult getInfo(@PathVariable("logId") Long logId)
    {
        return success(dataiConfigAuditLogService.selectDataiConfigAuditLogByLogId(logId));
    }

    /**
     * 新增配置审计日志
     */
    @Operation(summary = "新增配置审计日志")
    @PreAuthorize("@ss.hasPermi('setting:log:add')")
    @Log(title = "配置审计日志", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiConfigAuditLog dataiConfigAuditLog)
    {
        return toAjax(dataiConfigAuditLogService.insertDataiConfigAuditLog(dataiConfigAuditLog));
    }

    /**
     * 修改配置审计日志
     */
    @Operation(summary = "修改配置审计日志")
    @PreAuthorize("@ss.hasPermi('setting:log:edit')")
    @Log(title = "配置审计日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiConfigAuditLog dataiConfigAuditLog)
    {
        return toAjax(dataiConfigAuditLogService.updateDataiConfigAuditLog(dataiConfigAuditLog));
    }

    /**
     * 删除配置审计日志
     */
    @Operation(summary = "删除配置审计日志")
    @PreAuthorize("@ss.hasPermi('setting:log:remove')")
    @Log(title = "配置审计日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{logIds}")
    public AjaxResult remove(@PathVariable( name = "logIds" ) Long[] logIds) 
    {
        return toAjax(dataiConfigAuditLogService.deleteDataiConfigAuditLogByLogIds(logIds));
    }
}
