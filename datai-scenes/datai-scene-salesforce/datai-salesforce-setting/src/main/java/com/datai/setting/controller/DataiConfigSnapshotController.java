package com.datai.setting.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.datai.common.utils.PageUtils;
import com.datai.setting.model.dto.DataiConfigSnapshotDto;
import com.datai.setting.model.vo.DataiConfigSnapshotVo;
import com.datai.setting.service.IDataiConfigEnvironmentService;
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
import com.datai.setting.model.domain.DataiConfigSnapshot;
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

    @Autowired
    private IDataiConfigEnvironmentService dataiConfigEnvironmentService;

    /**
     * 查询配置快照列表
     */
    @Operation(summary = "查询配置快照列表")
    @PreAuthorize("@ss.hasPermi('setting:snapshot:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiConfigSnapshotDto dataiConfigSnapshotDto)
    {
        startPage();
        DataiConfigSnapshot dataiConfigSnapshot = DataiConfigSnapshotDto.toObj(dataiConfigSnapshotDto);
        List<DataiConfigSnapshot> list = dataiConfigSnapshotService.selectDataiConfigSnapshotList(dataiConfigSnapshot);
        
        if (list == null || list.isEmpty()) {
            return getDataTableByPage(new ArrayList<>(), 0);
        }
        
        List<Long> environmentIds = list.stream()
            .map(DataiConfigSnapshot::getEnvironmentId)
            .filter(id -> id != null)
            .distinct()
            .collect(Collectors.toList());
        
        Map<Long, String> environmentNameMap = environmentIds.stream()
            .collect(Collectors.toMap(
                id -> id,
                id -> {
                    var env = dataiConfigEnvironmentService.selectDataiConfigEnvironmentById(id);
                    return env != null ? env.getEnvironmentName() : null;
                }
            ));
        
        List<DataiConfigSnapshotVo> voList = list.stream()
            .map(snapshot -> {
                DataiConfigSnapshotVo vo = DataiConfigSnapshotVo.objToVo(snapshot);
                if (snapshot.getEnvironmentId() != null) {
                    vo.setEnvironmentName(environmentNameMap.get(snapshot.getEnvironmentId()));
                }
                return vo;
            })
            .collect(Collectors.toList());
        
        return getDataTableByPage(voList,PageUtils.getTotal(list));
    }

    /**
     * 导出配置快照列表
     */
    @Operation(summary = "导出配置快照列表")
    @PreAuthorize("@ss.hasPermi('setting:snapshot:export')")
    @Log(title = "配置快照", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiConfigSnapshotDto dataiConfigSnapshotDto)
    {
        DataiConfigSnapshot dataiConfigSnapshot = DataiConfigSnapshotDto.toObj(dataiConfigSnapshotDto);
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
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        DataiConfigSnapshot dataiConfigSnapshot = dataiConfigSnapshotService.selectDataiConfigSnapshotById(id);
        DataiConfigSnapshotVo dataiConfigSnapshotVo = DataiConfigSnapshotVo.objToVo(dataiConfigSnapshot);
        if (dataiConfigSnapshotVo != null && dataiConfigSnapshotVo.getEnvironmentId() != null) {
            var env = dataiConfigEnvironmentService.selectDataiConfigEnvironmentById(dataiConfigSnapshotVo.getEnvironmentId());
            if (env != null) {
                dataiConfigSnapshotVo.setEnvironmentName(env.getEnvironmentName());
            }
        }
        return success(dataiConfigSnapshotVo);
    }

    /**
     * 新增配置快照
     */
    @Operation(summary = "新增配置快照")
    @PreAuthorize("@ss.hasPermi('setting:snapshot:add')")
    @Log(title = "配置快照", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiConfigSnapshotDto dataiConfigSnapshotDto)
    {
        DataiConfigSnapshot dataiConfigSnapshot = DataiConfigSnapshotDto.toObj(dataiConfigSnapshotDto);
        return toAjax(dataiConfigSnapshotService.insertDataiConfigSnapshot(dataiConfigSnapshot));
    }

    /**
     * 修改配置快照
     */
    @Operation(summary = "修改配置快照")
    @PreAuthorize("@ss.hasPermi('setting:snapshot:edit')")
    @Log(title = "配置快照", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiConfigSnapshotDto dataiConfigSnapshotDto)
    {
        DataiConfigSnapshot dataiConfigSnapshot = DataiConfigSnapshotDto.toObj(dataiConfigSnapshotDto);
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
    public AjaxResult createSnapshot(@RequestBody DataiConfigSnapshotDto dataiConfigSnapshotDto)
    {
        DataiConfigSnapshot snapshot = dataiConfigSnapshotService.createSnapshot(
            dataiConfigSnapshotDto.getSnapshotNumber(),
            dataiConfigSnapshotDto.getEnvironmentId(),
            dataiConfigSnapshotDto.getSnapshotDesc()
        );
        DataiConfigSnapshotVo snapshotVo = DataiConfigSnapshotVo.objToVo(snapshot);
        if (snapshotVo != null && snapshotVo.getEnvironmentId() != null) {
            var env = dataiConfigEnvironmentService.selectDataiConfigEnvironmentById(snapshotVo.getEnvironmentId());
            if (env != null) {
                snapshotVo.setEnvironmentName(env.getEnvironmentName());
            }
        }
        return success(snapshotVo);
    }

    /**
     * 恢复快照
     */
    @Operation(summary = "恢复快照")
    @PreAuthorize("@ss.hasPermi('setting:snapshot:restore')")
    @Log(title = "配置快照", businessType = BusinessType.UPDATE)
    @PostMapping("/{snapshotId}/restore")
    public AjaxResult restoreSnapshot(@PathVariable("snapshotId") String snapshotId, @RequestBody DataiConfigSnapshotDto dataiConfigSnapshotDto)
    {
        return toAjax(dataiConfigSnapshotService.restoreSnapshot(snapshotId, dataiConfigSnapshotDto.getRemark()));
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
        DataiConfigSnapshotVo snapshotVo = DataiConfigSnapshotVo.objToVo(snapshot);
        if (snapshotVo != null && snapshotVo.getEnvironmentId() != null) {
            var env = dataiConfigEnvironmentService.selectDataiConfigEnvironmentById(snapshotVo.getEnvironmentId());
            if (env != null) {
                snapshotVo.setEnvironmentName(env.getEnvironmentName());
            }
        }
        return success(snapshotVo);
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
