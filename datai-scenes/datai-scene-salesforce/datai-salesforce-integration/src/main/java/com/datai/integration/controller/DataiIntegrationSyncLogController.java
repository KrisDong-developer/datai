package com.datai.integration.controller;

import java.util.List;
import java.util.Map;

import com.datai.integration.model.dto.DataiIntegrationSyncLogDto;
import com.datai.integration.model.dto.LogStatisticsDTO;
import com.datai.salesforce.common.utils.ValidationUtils;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
import com.datai.integration.model.domain.DataiIntegrationSyncLog;
import com.datai.integration.service.IDataiIntegrationSyncLogService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 数据同步日志Controller
 * 
 * @author datai
 * @date 2025-12-26
 */
@RestController
@RequestMapping("/integration/synclog")
@Tag(name = "【数据同步日志】管理")
public class DataiIntegrationSyncLogController extends BaseController
{
    private static final Logger log = LoggerFactory.getLogger(DataiIntegrationSyncLogController.class);
    
    @Autowired
    private IDataiIntegrationSyncLogService dataiIntegrationSyncLogService;

    /**
     * 查询数据同步日志列表
     */
    @Operation(summary = "查询数据同步日志列表")
    @PreAuthorize("@ss.hasPermi('integration:synclog:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiIntegrationSyncLog dataiIntegrationSyncLog)
    {
        startPage();
        List<DataiIntegrationSyncLog> list = dataiIntegrationSyncLogService.selectDataiIntegrationSyncLogList(dataiIntegrationSyncLog);
        return getDataTable(list);
    }

    /**
     * 导出数据同步日志列表
     */
    @Operation(summary = "导出数据同步日志列表")
    @PreAuthorize("@ss.hasPermi('integration:synclog:export')")
    @Log(title = "数据同步日志", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiIntegrationSyncLogDto dataiIntegrationSyncLogDto)
    {
        try {
            DataiIntegrationSyncLog dataiIntegrationSyncLog = DataiIntegrationSyncLogDto.toObj(dataiIntegrationSyncLogDto);
            List<DataiIntegrationSyncLog> list = dataiIntegrationSyncLogService.selectDataiIntegrationSyncLogList(dataiIntegrationSyncLog);
            ExcelUtil<DataiIntegrationSyncLog> util = new ExcelUtil<DataiIntegrationSyncLog>(DataiIntegrationSyncLog.class);
            util.exportExcel(response, list, "数据同步日志数据");
        } catch (Exception e) {
            log.error("导出数据同步日志列表失败", e);
            // 导出方法无法返回错误信息，只能记录日志
        }
    }

    /**
     * 获取数据同步日志详细信息
     */
    @Operation(summary = "获取数据同步日志详细信息")
    @PreAuthorize("@ss.hasPermi('integration:synclog:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        try {
            return success(dataiIntegrationSyncLogService.selectDataiIntegrationSyncLogById(id));
        } catch (Exception e) {
            log.error("获取数据同步日志详细信息失败，日志ID: {}", id, e);
            return error("获取数据同步日志详细信息失败");
        }
    }

    /**
     * 新增数据同步日志
     */
    @Operation(summary = "新增数据同步日志")
    @PreAuthorize("@ss.hasPermi('integration:synclog:add')")
    @Log(title = "数据同步日志", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiIntegrationSyncLog dataiIntegrationSyncLog)
    {
        try {
            return toAjax(dataiIntegrationSyncLogService.insertDataiIntegrationSyncLog(dataiIntegrationSyncLog));
        } catch (Exception e) {
            log.error("新增数据同步日志失败", e);
            return error("新增数据同步日志失败");
        }
    }

    /**
     * 修改数据同步日志
     */
    @Operation(summary = "修改数据同步日志")
    @PreAuthorize("@ss.hasPermi('integration:synclog:edit')")
    @Log(title = "数据同步日志", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiIntegrationSyncLogDto dataiIntegrationSyncLogDto)
    {
        try {
            DataiIntegrationSyncLog dataiIntegrationSyncLog = DataiIntegrationSyncLogDto.toObj(dataiIntegrationSyncLogDto);
            return toAjax(dataiIntegrationSyncLogService.updateDataiIntegrationSyncLog(dataiIntegrationSyncLog));
        } catch (Exception e) {
            log.error("修改数据同步日志失败", e);
            return error("修改数据同步日志失败");
        }
    }

    /**
     * 删除数据同步日志
     */
    @Operation(summary = "删除数据同步日志")
    @PreAuthorize("@ss.hasPermi('integration:synclog:remove')")
    @Log(title = "数据同步日志", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) Long[] ids) 
    {
        try {
            return toAjax(dataiIntegrationSyncLogService.deleteDataiIntegrationSyncLogByIds(ids));
        } catch (Exception e) {
            log.error("删除数据同步日志失败", e);
            return error("删除数据同步日志失败");
        }
    }

    /**
     * 获取日志统计信息
     */
    @Operation(summary = "获取日志统计信息")
    @PreAuthorize("@ss.hasPermi('integration:synclog:query')")
    @GetMapping("/statistics")
    public AjaxResult getLogStatistics(DataiIntegrationSyncLogDto dataiIntegrationSyncLogDto)
    {
        try {
            // 参数验证
            validateStatisticsParams(dataiIntegrationSyncLogDto);
            
            Map<String, Object> params = new java.util.HashMap<>();
            if (dataiIntegrationSyncLogDto.getBatchId() != null) {
                params.put("batchId", dataiIntegrationSyncLogDto.getBatchId());
            }
            if (dataiIntegrationSyncLogDto.getObjectApi() != null && !dataiIntegrationSyncLogDto.getObjectApi().isEmpty()) {
                params.put("objectApi", dataiIntegrationSyncLogDto.getObjectApi());
            }
            if (dataiIntegrationSyncLogDto.getOperationType() != null && !dataiIntegrationSyncLogDto.getOperationType().isEmpty()) {
                params.put("operationType", dataiIntegrationSyncLogDto.getOperationType());
            }
            if (dataiIntegrationSyncLogDto.getOperationStatus() != null && !dataiIntegrationSyncLogDto.getOperationStatus().isEmpty()) {
                params.put("operationStatus", dataiIntegrationSyncLogDto.getOperationStatus());
            }
            if (dataiIntegrationSyncLogDto.getDeptId() != null) {
                params.put("deptId", dataiIntegrationSyncLogDto.getDeptId());
            }
            if (dataiIntegrationSyncLogDto.getParams() != null) {
                if (dataiIntegrationSyncLogDto.getParams().get("beginTime") != null) {
                    params.put("startTime", dataiIntegrationSyncLogDto.getParams().get("beginTime"));
                }
                if (dataiIntegrationSyncLogDto.getParams().get("endTime") != null) {
                    params.put("endTime", dataiIntegrationSyncLogDto.getParams().get("endTime"));
                }
            }
            
            LogStatisticsDTO statistics = dataiIntegrationSyncLogService.getLogStatistics(params);
            return success(statistics);
        } catch (IllegalArgumentException e) {
            log.warn("参数验证失败: {}", e.getMessage());
            return error(e.getMessage());
        } catch (Exception e) {
            log.error("获取日志统计信息失败", e);
            return error("获取日志统计信息失败: " + e.getMessage());
        }
    }
    
    /**
     * 验证统计参数
     * 
     * @param dto 数据同步日志DTO
     */
    private void validateStatisticsParams(DataiIntegrationSyncLogDto dto) {
        if (dto.getParams() != null) {
            Object beginTimeObj = dto.getParams().get("beginTime");
            Object endTimeObj = dto.getParams().get("endTime");
            
            if (beginTimeObj != null && endTimeObj != null) {
                String beginTime = beginTimeObj.toString();
                String endTime = endTimeObj.toString();
                
                // 验证时间格式
                if (!ValidationUtils.validateTimeFormat(beginTime)) {
                    throw new IllegalArgumentException("开始时间格式不正确，请使用 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss 格式");
                }
                if (!ValidationUtils.validateTimeFormat(endTime)) {
                    throw new IllegalArgumentException("结束时间格式不正确，请使用 yyyy-MM-dd 或 yyyy-MM-dd HH:mm:ss 格式");
                }
                
                // 验证日期范围
                if (!ValidationUtils.validateDateRange(beginTime, endTime)) {
                    throw new IllegalArgumentException("开始时间必须小于或等于结束时间");
                }
            }
        }
        
        // 验证操作类型
        if (dto.getOperationType() != null && !dto.getOperationType().isEmpty()) {
            // 这里可以添加具体的操作类型验证逻辑
        }
        
        // 验证操作状态
        if (dto.getOperationStatus() != null && !dto.getOperationStatus().isEmpty()) {
            // 这里可以添加具体的操作状态验证逻辑
        }
    }
}
