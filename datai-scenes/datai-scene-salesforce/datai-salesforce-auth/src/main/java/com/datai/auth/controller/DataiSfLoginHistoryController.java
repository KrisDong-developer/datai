package com.datai.auth.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.datai.auth.model.vo.DataiSfLoginHistoryVo;
import com.datai.common.utils.PageUtils;
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
import com.datai.auth.model.domain.DataiSfLoginHistory;
import com.datai.auth.model.dto.DataiSfLoginHistoryDto;
import com.datai.auth.service.IDataiSfLoginHistoryService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 登录历史信息Controller
 * 
 * @author datai
 * @date 2025-12-25
 */
@RestController
@RequestMapping("/auth/history")
@Tag(name = "【登录历史信息】管理")
public class DataiSfLoginHistoryController extends BaseController
{
    @Autowired
    private IDataiSfLoginHistoryService dataiSfLoginHistoryService;

    /**
     * 查询登录历史信息列表
     */
    @Operation(summary = "查询登录历史信息列表")
    @PreAuthorize("@ss.hasPermi('auth:history:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiSfLoginHistoryDto dataiSfLoginHistoryDto)
    {
        startPage();
        List<DataiSfLoginHistory> list = dataiSfLoginHistoryService.selectDataiSfLoginHistoryList(DataiSfLoginHistoryDto.toObj(dataiSfLoginHistoryDto));
        List<DataiSfLoginHistoryVo> voList = list.stream().map(DataiSfLoginHistoryVo::objToVo).collect(Collectors.toList());
        return getDataTableByPage(voList,PageUtils.getTotal(list));
    }

    /**
     * 导出登录历史信息列表
     */
    @Operation(summary = "导出登录历史信息列表")
    @PreAuthorize("@ss.hasPermi('auth:history:export')")
    @Log(title = "登录历史信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiSfLoginHistoryDto dataiSfLoginHistoryDto)
    {
        List<DataiSfLoginHistory> list = dataiSfLoginHistoryService.selectDataiSfLoginHistoryList(DataiSfLoginHistoryDto.toObj(dataiSfLoginHistoryDto));
        ExcelUtil<DataiSfLoginHistory> util = new ExcelUtil<DataiSfLoginHistory>(DataiSfLoginHistory.class);
        util.exportExcel(response, list, "登录历史信息数据");
    }

    /**
     * 获取登录历史信息详细信息
     */
    @Operation(summary = "获取登录历史信息详细信息")
    @PreAuthorize("@ss.hasPermi('auth:history:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        DataiSfLoginHistory dataiSfLoginHistory = dataiSfLoginHistoryService.selectDataiSfLoginHistoryById(id);
        return success(DataiSfLoginHistoryVo.objToVo(dataiSfLoginHistory));
    }

    /**
     * 新增登录历史信息
     */
    @Operation(summary = "新增登录历史信息")
    @PreAuthorize("@ss.hasPermi('auth:history:add')")
    @Log(title = "登录历史信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiSfLoginHistoryDto dataiSfLoginHistoryDto)
    {
        return toAjax(dataiSfLoginHistoryService.insertDataiSfLoginHistory(DataiSfLoginHistoryDto.toObj(dataiSfLoginHistoryDto)));
    }

    /**
     * 修改登录历史信息
     */
    @Operation(summary = "修改登录历史信息")
    @PreAuthorize("@ss.hasPermi('auth:history:edit')")
    @Log(title = "登录历史信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiSfLoginHistoryDto dataiSfLoginHistoryDto)
    {
        return toAjax(dataiSfLoginHistoryService.updateDataiSfLoginHistory(DataiSfLoginHistoryDto.toObj(dataiSfLoginHistoryDto)));
    }

    /**
     * 删除登录历史信息
     */
    @Operation(summary = "删除登录历史信息")
    @PreAuthorize("@ss.hasPermi('auth:history:remove')")
    @Log(title = "登录历史信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) Long[] ids) 
    {
        return toAjax(dataiSfLoginHistoryService.deleteDataiSfLoginHistoryByIds(ids));
    }
}
