package com.datai.integration.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.datai.common.utils.PageUtils;
import com.datai.integration.model.vo.DataiIntegrationBatchVo;
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
import com.datai.integration.model.domain.DataiIntegrationBatch;
import com.datai.integration.model.dto.DataiIntegrationBatchDto;
import com.datai.integration.service.IDataiIntegrationBatchService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

/**
 * 数据批次Controller
 * 
 * @author datai
 * @date 2025-12-24
 */
@RestController
@RequestMapping("/integration/batch")
@Tag(name = "【数据批次】管理")
@Slf4j
public class DataiIntegrationBatchController extends BaseController
{
    @Autowired
    private IDataiIntegrationBatchService dataiIntegrationBatchService;

    /**
     * 查询数据批次列表
     */
    @Operation(summary = "查询数据批次列表")
    @PreAuthorize("@ss.hasPermi('integration:batch:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiIntegrationBatchDto dataiIntegrationBatchDto)
    {
        startPage();
        List<DataiIntegrationBatch> list = dataiIntegrationBatchService.selectDataiIntegrationBatchList(
            DataiIntegrationBatchDto.toObj(dataiIntegrationBatchDto));
        List<DataiIntegrationBatchVo> voList = list.stream()
            .map(DataiIntegrationBatchVo::objToVo)
            .collect(Collectors.toList());
        return getDataTableByPage(voList,PageUtils.getTotal(list));
    }

    /**
     * 导出数据批次列表
     */
    @Operation(summary = "导出数据批次列表")
    @PreAuthorize("@ss.hasPermi('integration:batch:export')")
    @Log(title = "数据批次", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiIntegrationBatchDto dataiIntegrationBatchDto)
    {
        List<DataiIntegrationBatch> list = dataiIntegrationBatchService.selectDataiIntegrationBatchList(
            DataiIntegrationBatchDto.toObj(dataiIntegrationBatchDto));
        ExcelUtil<DataiIntegrationBatch> util = new ExcelUtil<DataiIntegrationBatch>(DataiIntegrationBatch.class);
        util.exportExcel(response, list, "数据批次数据");
    }

    /**
     * 获取数据批次详细信息
     */
    @Operation(summary = "获取数据批次详细信息")
    @PreAuthorize("@ss.hasPermi('integration:batch:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id)
    {
        DataiIntegrationBatch dataiIntegrationBatch = dataiIntegrationBatchService.selectDataiIntegrationBatchById(id);
        return success(DataiIntegrationBatchVo.objToVo(dataiIntegrationBatch));
    }

    /**
     * 新增数据批次
     */
    @Operation(summary = "新增数据批次")
    @PreAuthorize("@ss.hasPermi('integration:batch:add')")
    @Log(title = "数据批次", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiIntegrationBatchDto dataiIntegrationBatchDto)
    {
        return toAjax(dataiIntegrationBatchService.insertDataiIntegrationBatch(
            DataiIntegrationBatchDto.toObj(dataiIntegrationBatchDto)));
    }

    /**
     * 修改数据批次
     */
    @Operation(summary = "修改数据批次")
    @PreAuthorize("@ss.hasPermi('integration:batch:edit')")
    @Log(title = "数据批次", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiIntegrationBatchDto dataiIntegrationBatchDto)
    {
        return toAjax(dataiIntegrationBatchService.updateDataiIntegrationBatch(
            DataiIntegrationBatchDto.toObj(dataiIntegrationBatchDto)));
    }

    /**
     * 删除数据批次
     */
    @Operation(summary = "删除数据批次")
    @PreAuthorize("@ss.hasPermi('integration:batch:remove')")
    @Log(title = "数据批次", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) Integer[] ids) 
    {
        return toAjax(dataiIntegrationBatchService.deleteDataiIntegrationBatchByIds(ids));
    }

    /**
     * 重试失败的批次
     */
    @Operation(summary = "重试失败的批次")
    @PreAuthorize("@ss.hasPermi('integration:batch:retry')")
    @Log(title = "数据批次", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/retry")
    public AjaxResult retryFailed(@PathVariable("id") Integer id)
    {
        return toAjax(dataiIntegrationBatchService.retryFailed(id) ? 1 : 0);
    }

    /**
     * 获取批次同步统计信息
     */
    @Operation(summary = "获取批次同步统计信息")
    @PreAuthorize("@ss.hasPermi('integration:batch:statistics')")
    @GetMapping("/{id}/statistics")
    public AjaxResult getSyncStatistics(@PathVariable("id") Integer id)
    {
        return success(dataiIntegrationBatchService.getSyncStatistics(id));
    }

    /**
     * 同步批次数据
     */
    @Operation(summary = "同步批次数据")
    @PreAuthorize("@ss.hasPermi('integration:batch:sync')")
    @Log(title = "数据批次", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/sync")
    public AjaxResult syncBatchData(@PathVariable("id") Integer id)
    {
        Map<String, Object> result = dataiIntegrationBatchService.syncBatchData(id);
        if ((Boolean) result.get("success")) {
            return success(result);
        } else {
            return error((String) result.get("message"));
        }
    }

    /**
     * 获取所有批次统计信息
     */
    @Operation(summary = "获取所有批次统计信息")
    @PreAuthorize("@ss.hasPermi('integration:batch:statistics')")
    @GetMapping("/statistics")
    public AjaxResult getAllBatchStatistics()
    {
        Map<String, Object> result = dataiIntegrationBatchService.getAllBatchStatistics();
        if ((Boolean) result.get("success")) {
            return success(result);
        } else {
            return error((String) result.get("message"));
        }
    }

    /**
     * 批次数据插入目标系统
     * 
     * 该方法用于将本地数据库中的指定批次数据插入到目标Salesforce系统
     * 插入操作会查询本地表中符合批次条件的数据，然后通过SOAP API插入到目标系统
     * 插入成功后，会将返回的ID保存到new_id字段，并更新is_insert字段
     * 
     * @param id 批次ID，用于标识需要插入的批次
     * @param targetOrgType 目标ORG类型，用于标识目标Salesforce系统
     * @return AjaxResult 插入结果，包含成功/失败状态、插入数据量、耗时等信息
     *         - 成功时返回：success=true, message="批次数据插入完成", 以及详细的插入信息
     *         - 失败时返回：success=false, message=错误信息
     */
    @Operation(summary = "批次数据插入目标系统")
    @PreAuthorize("@ss.hasPermi('integration:batch:insertData')")
    @Log(title = "数据批次", businessType = BusinessType.INSERT)
    @PostMapping("/{id}/insertData")
    public AjaxResult insertBatchDataToTarget(@PathVariable("id") Integer id,
                                                @org.springframework.web.bind.annotation.RequestParam("targetOrgType") String targetOrgType)
    {
        if (id == null) {
            log.error("批次ID为空，无法插入数据");
            return error("批次ID不能为空");
        }

        if (targetOrgType == null || targetOrgType.trim().isEmpty()) {
            log.error("目标ORG类型为空，无法插入数据");
            return error("目标ORG类型不能为空");
        }

        try {
            log.info("开始插入批次数据到目标系统，批次ID: {}, 目标ORG类型: {}", id, targetOrgType);

            Map<String, Object> result = dataiIntegrationBatchService.insertBatchDataToTarget(id, targetOrgType);

            if ((Boolean) result.get("success")) {
                log.info("批次数据插入成功，批次ID: {}, 目标ORG类型: {}", id, targetOrgType);
                return success(result);
            } else {
                log.error("批次数据插入失败，批次ID: {}, 目标ORG类型: {}, 错误信息: {}",
                    id, targetOrgType, result.get("message"));
                return error((String) result.get("message"));
            }

        } catch (Exception e) {
            log.error("插入批次数据时发生异常，批次ID: {}, 目标ORG类型: {}", id, targetOrgType, e);
            return error("插入批次数据时发生异常: " + e.getMessage());
        }
    }

    /**
     * 批次数据更新目标系统
     * 
     * 该方法用于将本地数据库中的指定批次数据更新到目标Salesforce系统
     * 更新操作会查询本地表中符合批次条件的数据，然后通过SOAP API更新到目标系统
     * 更新时使用new_id作为目标系统的ID，更新成功后，更新is_update字段
     * 
     * @param id 批次ID，用于标识需要更新的批次
     * @param targetOrgType 目标ORG类型，用于标识目标Salesforce系统
     * @return AjaxResult 更新结果，包含成功/失败状态、更新数据量、耗时等信息
     *         - 成功时返回：success=true, message="批次数据更新完成", 以及详细的更新信息
     *         - 失败时返回：success=false, message=错误信息
     */
    @Operation(summary = "批次数据更新目标系统")
    @PreAuthorize("@ss.hasPermi('integration:batch:updateData')")
    @Log(title = "数据批次", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/updateData")
    public AjaxResult updateBatchDataToTarget(@PathVariable("id") Integer id,
                                                @org.springframework.web.bind.annotation.RequestParam("targetOrgType") String targetOrgType)
    {
        if (id == null) {
            log.error("批次ID为空，无法更新数据");
            return error("批次ID不能为空");
        }

        if (targetOrgType == null || targetOrgType.trim().isEmpty()) {
            log.error("目标ORG类型为空，无法更新数据");
            return error("目标ORG类型不能为空");
        }

        try {
            log.info("开始更新批次数据到目标系统，批次ID: {}, 目标ORG类型: {}", id, targetOrgType);

            Map<String, Object> result = dataiIntegrationBatchService.updateBatchDataToTarget(id, targetOrgType);

            if ((Boolean) result.get("success")) {
                log.info("批次数据更新成功，批次ID: {}, 目标ORG类型: {}", id, targetOrgType);
                return success(result);
            } else {
                log.error("批次数据更新失败，批次ID: {}, 目标ORG类型: {}, 错误信息: {}",
                    id, targetOrgType, result.get("message"));
                return error((String) result.get("message"));
            }

        } catch (Exception e) {
            log.error("更新批次数据时发生异常，批次ID: {}, 目标ORG类型: {}", id, targetOrgType, e);
            return error("更新批次数据时发生异常: " + e.getMessage());
        }
    }
}
