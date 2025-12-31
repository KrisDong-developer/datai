package com.datai.integration.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.datai.integration.model.vo.DataiIntegrationApiCallLogVo;
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
import com.datai.integration.model.domain.DataiIntegrationApiCallLog;
import com.datai.integration.model.dto.DataiIntegrationApiCallLogDto;
import com.datai.integration.service.IDataiIntegrationApiCallLogService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * API调用日志Controller
 * 
 * @author datai
 * @date 2025-12-28
 */
@RestController
@RequestMapping("/integration/apilog")
@Tag(name = "【API调用日志】管理")
public class DataiIntegrationApiCallLogController extends BaseController
{
    @Autowired
    private IDataiIntegrationApiCallLogService dataiIntegrationApiCallLogService;

    /**
     * 查询API调用日志列表
     */
    @Operation(summary = "查询API调用日志列表")
    @PreAuthorize("@ss.hasPermi('integration:apilog:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiIntegrationApiCallLogDto dataiIntegrationApiCallLogDto)
    {
        startPage();
        List<DataiIntegrationApiCallLog> list = dataiIntegrationApiCallLogService.selectDataiIntegrationApiCallLogList(
            DataiIntegrationApiCallLogDto.toObj(dataiIntegrationApiCallLogDto));
        List<DataiIntegrationApiCallLogVo> voList = list.stream()
            .map(DataiIntegrationApiCallLogVo::objToVo)
            .collect(Collectors.toList());
        return getDataTable(voList);
    }

    /**
     * 导出API调用日志列表
     */
    @Operation(summary = "导出API调用日志列表")
    @PreAuthorize("@ss.hasPermi('integration:apilog:export')")
    @Log(title = "API调用日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiIntegrationApiCallLogDto dataiIntegrationApiCallLogDto)
    {
        List<DataiIntegrationApiCallLog> list = dataiIntegrationApiCallLogService.selectDataiIntegrationApiCallLogList(
            DataiIntegrationApiCallLogDto.toObj(dataiIntegrationApiCallLogDto));
        ExcelUtil<DataiIntegrationApiCallLog> util = new ExcelUtil<DataiIntegrationApiCallLog>(DataiIntegrationApiCallLog.class);
        util.exportExcel(response, list, "API调用日志数据");
    }

    /**
     * 获取API调用日志详细信息
     */
    @Operation(summary = "获取API调用日志详细信息")
    @PreAuthorize("@ss.hasPermi('integration:apilog:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        DataiIntegrationApiCallLog dataiIntegrationApiCallLog = dataiIntegrationApiCallLogService.selectDataiIntegrationApiCallLogById(id);
        return success(DataiIntegrationApiCallLogVo.objToVo(dataiIntegrationApiCallLog));
    }

    /**
     * 新增API调用日志
     */
    @Operation(summary = "新增API调用日志")
    @PreAuthorize("@ss.hasPermi('integration:apilog:add')")
    @Log(title = "API调用日志", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiIntegrationApiCallLogDto dataiIntegrationApiCallLogDto)
    {
        return toAjax(dataiIntegrationApiCallLogService.insertDataiIntegrationApiCallLog(
            DataiIntegrationApiCallLogDto.toObj(dataiIntegrationApiCallLogDto)));
    }

    /**
     * 修改API调用日志
     */
    @Operation(summary = "修改API调用日志")
    @PreAuthorize("@ss.hasPermi('integration:apilog:edit')")
    @Log(title = "API调用日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiIntegrationApiCallLogDto dataiIntegrationApiCallLogDto)
    {
        return toAjax(dataiIntegrationApiCallLogService.updateDataiIntegrationApiCallLog(
            DataiIntegrationApiCallLogDto.toObj(dataiIntegrationApiCallLogDto)));
    }

    /**
     * 删除API调用日志
     */
    @Operation(summary = "删除API调用日志")
    @PreAuthorize("@ss.hasPermi('integration:apilog:remove')")
    @Log(title = "API调用日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) Long[] ids) 
    {
        return toAjax(dataiIntegrationApiCallLogService.deleteDataiIntegrationApiCallLogByIds(ids));
    }

    /**
     * 获取API调用日志统计信息
     */
    @Operation(summary = "获取API调用日志统计信息")
    @PreAuthorize("@ss.hasPermi('integration:apilog:statistics')")
    @GetMapping("/statistics")
    public AjaxResult getStatistics(DataiIntegrationApiCallLogDto dataiIntegrationApiCallLogDto)
    {
        Map<String, Object> params = new java.util.HashMap<>();
        if (dataiIntegrationApiCallLogDto.getApiType() != null) {
            params.put("apiType", dataiIntegrationApiCallLogDto.getApiType());
        }
        if (dataiIntegrationApiCallLogDto.getConnectionClass() != null) {
            params.put("connectionClass", dataiIntegrationApiCallLogDto.getConnectionClass());
        }
        if (dataiIntegrationApiCallLogDto.getMethodName() != null) {
            params.put("methodName", dataiIntegrationApiCallLogDto.getMethodName());
        }
        if (dataiIntegrationApiCallLogDto.getStatus() != null) {
            params.put("status", dataiIntegrationApiCallLogDto.getStatus());
        }
        
        Map<String, Object> result = dataiIntegrationApiCallLogService.getApiCallLogStatistics(params);
        if ((Boolean) result.get("success")) {
            return success(result);
        } else {
            return error((String) result.get("message"));
        }
    }
}
