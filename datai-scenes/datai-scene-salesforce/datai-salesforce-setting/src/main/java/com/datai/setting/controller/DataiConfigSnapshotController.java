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
import com.datai.setting.domain.DataiConfigSnapshot;
import com.datai.setting.service.IDataiConfigSnapshotService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 配置快照Controller
 * 
 * @author datai
 * @date 2025-12-24
 */
@RestController
@RequestMapping("/setting/snapshot")
@Tag(name = "【配置快照】管理")
public class DataiConfigSnapshotController extends BaseController
{
    @Autowired
    private IDataiConfigSnapshotService dataiConfigSnapshotService;

    /**
     * 查询配置快照列表
     */
    @Operation(summary = "查询配置快照列表")
    @PreAuthorize("@ss.hasPermi('setting:snapshot:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiConfigSnapshot dataiConfigSnapshot)
    {
        startPage();
        List<DataiConfigSnapshot> list = dataiConfigSnapshotService.selectDataiConfigSnapshotList(dataiConfigSnapshot);
        return getDataTable(list);
    }

    /**
     * 导出配置快照列表
     */
    @Operation(summary = "导出配置快照列表")
    @PreAuthorize("@ss.hasPermi('setting:snapshot:export')")
    @Log(title = "配置快照", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiConfigSnapshot dataiConfigSnapshot)
    {
        List<DataiConfigSnapshot> list = dataiConfigSnapshotService.selectDataiConfigSnapshotList(dataiConfigSnapshot);
        ExcelUtil<DataiConfigSnapshot> util = new ExcelUtil<DataiConfigSnapshot>(DataiConfigSnapshot.class);
        util.exportExcel(response, list, "配置快照数据");
    }

    /**
     * 获取配置快照详细信息
     */
    @Operation(summary = "获取配置快照详细信息")
    @PreAuthorize("@ss.hasPermi('setting:snapshot:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") String id)
    {
        return success(dataiConfigSnapshotService.selectDataiConfigSnapshotById(id));
    }

    /**
     * 新增配置快照
     */
    @Operation(summary = "新增配置快照")
    @PreAuthorize("@ss.hasPermi('setting:snapshot:add')")
    @Log(title = "配置快照", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiConfigSnapshot dataiConfigSnapshot)
    {
        return toAjax(dataiConfigSnapshotService.insertDataiConfigSnapshot(dataiConfigSnapshot));
    }

    /**
     * 修改配置快照
     */
    @Operation(summary = "修改配置快照")
    @PreAuthorize("@ss.hasPermi('setting:snapshot:edit')")
    @Log(title = "配置快照", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiConfigSnapshot dataiConfigSnapshot)
    {
        return toAjax(dataiConfigSnapshotService.updateDataiConfigSnapshot(dataiConfigSnapshot));
    }

    /**
     * 删除配置快照
     */
    @Operation(summary = "删除配置快照")
    @PreAuthorize("@ss.hasPermi('setting:snapshot:remove')")
    @Log(title = "配置快照", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) String[] ids)
    {
        return toAjax(dataiConfigSnapshotService.deleteDataiConfigSnapshotByIds(ids));
    }
    /**
     * 从当前配置生成快照
     */
    @Operation(summary = "从当前配置生成快照")
    @PreAuthorize("@ss.hasPermi('setting:snapshot:create')")
    @Log(title = "配置快照", businessType = BusinessType.INSERT)
    @PostMapping("/create")
    public AjaxResult createSnapshot(@RequestBody DataiConfigSnapshot dataiConfigSnapshot)
    {
        DataiConfigSnapshot snapshot = dataiConfigSnapshotService.createSnapshot(
            dataiConfigSnapshot.getSnapshotNumber(),
            dataiConfigSnapshot.getEnvironmentId(),
            dataiConfigSnapshot.getRemark()
        );
        return success(snapshot);
    }

    /**
     * 恢复快照
     */
    @Operation(summary = "恢复快照")
    @PreAuthorize("@ss.hasPermi('setting:snapshot:restore')")
    @Log(title = "配置快照", businessType = BusinessType.UPDATE)
    @PostMapping("/{snapshotId}/restore")
    public AjaxResult restoreSnapshot(@PathVariable("snapshotId") String snapshotId, @RequestBody DataiConfigSnapshot dataiConfigSnapshot)
    {
        return toAjax(dataiConfigSnapshotService.restoreSnapshot(snapshotId, dataiConfigSnapshot.getRemark()));
    }

    /**
     * 获取快照详细信息（包含配置内容）
     */
    @Operation(summary = "获取快照详细信息（包含配置内容）")
    @PreAuthorize("@ss.hasPermi('setting:snapshot:query')")
    @GetMapping("/{snapshotId}/detail")
    public AjaxResult getSnapshotDetail(@PathVariable("snapshotId") String snapshotId)
    {
        DataiConfigSnapshot snapshot = dataiConfigSnapshotService.getSnapshotDetail(snapshotId);
        return success(snapshot);
    }

    /**
     * 比较两个快照的差异
     */
    @Operation(summary = "比较两个快照的差异")
    @PreAuthorize("@ss.hasPermi('setting:snapshot:query')")
    @GetMapping("/{snapshotId1}/compare/{snapshotId2}")
    public AjaxResult compareSnapshots(@PathVariable("snapshotId1") String snapshotId1, @PathVariable("snapshotId2") String snapshotId2)
    {
        String differences = dataiConfigSnapshotService.compareSnapshots(snapshotId1, snapshotId2);
        return success(differences);
    }
}
