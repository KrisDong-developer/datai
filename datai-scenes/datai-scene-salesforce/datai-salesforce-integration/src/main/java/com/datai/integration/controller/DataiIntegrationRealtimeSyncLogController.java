package com.datai.integration.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.datai.common.utils.PageUtils;
import com.datai.integration.model.domain.DataiIntegrationRealtimeSyncLog;
import com.datai.integration.model.dto.DataiIntegrationRealtimeSyncLogDto;
import com.datai.integration.model.vo.DataiIntegrationRealtimeSyncLogVo;
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

import com.datai.integration.service.IDataiIntegrationRealtimeSyncLogService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 实时同步日志Controller
 * 
 * @author datai
 * @date 2026-01-09
 */
@RestController
@RequestMapping("/integration/realtimelog")
@Tag(name = "【实时同步日志】管理")
public class DataiIntegrationRealtimeSyncLogController extends BaseController
{
    @Autowired
    private IDataiIntegrationRealtimeSyncLogService dataiIntegrationRealtimeSyncLogService;

    /**
     * 查询实时同步日志列表
     */
    @Operation(summary = "查询实时同步日志列表")
    @PreAuthorize("@ss.hasPermi('integration:realtimelog:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiIntegrationRealtimeSyncLogDto dataiIntegrationRealtimeSyncLogDto)
    {
        startPage();
        List<DataiIntegrationRealtimeSyncLog> list = dataiIntegrationRealtimeSyncLogService.selectDataiIntegrationRealtimeSyncLogList(DataiIntegrationRealtimeSyncLogDto.toObj(dataiIntegrationRealtimeSyncLogDto));
        List<DataiIntegrationRealtimeSyncLogVo> voList = list.stream().map(DataiIntegrationRealtimeSyncLogVo::objToVo).collect(Collectors.toList());
        return getDataTableByPage(voList, PageUtils.getTotal(list));
    }

    /**
     * 导出实时同步日志列表
     */
    @Operation(summary = "导出实时同步日志列表")
    @PreAuthorize("@ss.hasPermi('integration:realtimelog:export')")
    @Log(title = "实时同步日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiIntegrationRealtimeSyncLogDto dataiIntegrationRealtimeSyncLogDto)
    {
        List<DataiIntegrationRealtimeSyncLog> list = dataiIntegrationRealtimeSyncLogService.selectDataiIntegrationRealtimeSyncLogList(DataiIntegrationRealtimeSyncLogDto.toObj(dataiIntegrationRealtimeSyncLogDto));
        ExcelUtil<DataiIntegrationRealtimeSyncLog> util = new ExcelUtil<DataiIntegrationRealtimeSyncLog>(DataiIntegrationRealtimeSyncLog.class);
        util.exportExcel(response, list, "实时同步日志数据");
    }

    /**
     * 获取实时同步日志详细信息
     */
    @Operation(summary = "获取实时同步日志详细信息")
    @PreAuthorize("@ss.hasPermi('integration:realtimelog:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        DataiIntegrationRealtimeSyncLog dataiIntegrationRealtimeSyncLog = dataiIntegrationRealtimeSyncLogService.selectDataiIntegrationRealtimeSyncLogById(id);
        return success(DataiIntegrationRealtimeSyncLogVo.objToVo(dataiIntegrationRealtimeSyncLog));
    }

    /**
     * 新增实时同步日志
     */
    @Operation(summary = "新增实时同步日志")
    @PreAuthorize("@ss.hasPermi('integration:realtimelog:add')")
    @Log(title = "实时同步日志", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiIntegrationRealtimeSyncLogDto dataiIntegrationRealtimeSyncLogDto)
    {
        return toAjax(dataiIntegrationRealtimeSyncLogService.insertDataiIntegrationRealtimeSyncLog(DataiIntegrationRealtimeSyncLogDto.toObj(dataiIntegrationRealtimeSyncLogDto)));
    }

    /**
     * 修改实时同步日志
     */
    @Operation(summary = "修改实时同步日志")
    @PreAuthorize("@ss.hasPermi('integration:realtimelog:edit')")
    @Log(title = "实时同步日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiIntegrationRealtimeSyncLogDto dataiIntegrationRealtimeSyncLogDto)
    {
        return toAjax(dataiIntegrationRealtimeSyncLogService.updateDataiIntegrationRealtimeSyncLog(DataiIntegrationRealtimeSyncLogDto.toObj(dataiIntegrationRealtimeSyncLogDto)));
    }

    /**
     * 删除实时同步日志
     */
    @Operation(summary = "删除实时同步日志")
    @PreAuthorize("@ss.hasPermi('integration:realtimelog:remove')")
    @Log(title = "实时同步日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) Long[] ids) 
    {
        return toAjax(dataiIntegrationRealtimeSyncLogService.deleteDataiIntegrationRealtimeSyncLogByIds(ids));
    }
}
