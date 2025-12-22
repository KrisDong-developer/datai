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
import com.datai.integration.domain.DataiIntegrationBatch;
import com.datai.integration.service.IDataiIntegrationBatchService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 数据批次Controller
 * 
 * @author datai
 * @date 2025-12-22
 */
@RestController
@RequestMapping("/integration/batch")
@Tag(name = "【数据批次】管理")
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
    public TableDataInfo list(DataiIntegrationBatch dataiIntegrationBatch)
    {
        startPage();
        List<DataiIntegrationBatch> list = dataiIntegrationBatchService.selectDataiIntegrationBatchList(dataiIntegrationBatch);
        return getDataTable(list);
    }

    /**
     * 导出数据批次列表
     */
    @Operation(summary = "导出数据批次列表")
    @PreAuthorize("@ss.hasPermi('integration:batch:export')")
    @Log(title = "数据批次", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiIntegrationBatch dataiIntegrationBatch)
    {
        List<DataiIntegrationBatch> list = dataiIntegrationBatchService.selectDataiIntegrationBatchList(dataiIntegrationBatch);
        ExcelUtil<DataiIntegrationBatch> util = new ExcelUtil<DataiIntegrationBatch>(DataiIntegrationBatch.class);
        util.exportExcel(response, list, "数据批次数据");
    }

    /**
     * 获取数据批次详细信息
     */
    @Operation(summary = "获取数据批次详细信息")
    @PreAuthorize("@ss.hasPermi('integration:batch:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        return success(dataiIntegrationBatchService.selectDataiIntegrationBatchById(id));
    }

    /**
     * 新增数据批次
     */
    @Operation(summary = "新增数据批次")
    @PreAuthorize("@ss.hasPermi('integration:batch:add')")
    @Log(title = "数据批次", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiIntegrationBatch dataiIntegrationBatch)
    {
        return toAjax(dataiIntegrationBatchService.insertDataiIntegrationBatch(dataiIntegrationBatch));
    }

    /**
     * 修改数据批次
     */
    @Operation(summary = "修改数据批次")
    @PreAuthorize("@ss.hasPermi('integration:batch:edit')")
    @Log(title = "数据批次", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiIntegrationBatch dataiIntegrationBatch)
    {
        return toAjax(dataiIntegrationBatchService.updateDataiIntegrationBatch(dataiIntegrationBatch));
    }

    /**
     * 删除数据批次
     */
    @Operation(summary = "删除数据批次")
    @PreAuthorize("@ss.hasPermi('integration:batch:remove')")
    @Log(title = "数据批次", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) Long[] ids) 
    {
        return toAjax(dataiIntegrationBatchService.deleteDataiIntegrationBatchByIds(ids));
    }
}
