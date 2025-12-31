package com.datai.auth.controller;

import java.util.List;
import java.util.stream.Collectors;

import com.datai.auth.model.vo.DataiSfLoginSessionVo;
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
import com.datai.auth.model.domain.DataiSfLoginSession;
import com.datai.auth.model.dto.DataiSfLoginSessionDto;
import com.datai.auth.service.IDataiSfLoginSessionService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 登录会话信息Controller
 * 
 * @author datai
 * @date 2025-12-25
 */
@RestController
@RequestMapping("/auth/session")
@Tag(name = "【登录会话信息】管理")
public class DataiSfLoginSessionController extends BaseController
{
    @Autowired
    private IDataiSfLoginSessionService dataiSfLoginSessionService;

    /**
     * 查询登录会话信息列表
     */
    @Operation(summary = "查询登录会话信息列表")
    @PreAuthorize("@ss.hasPermi('auth:session:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiSfLoginSessionDto dataiSfLoginSessionDto)
    {
        startPage();
        List<DataiSfLoginSession> list = dataiSfLoginSessionService.selectDataiSfLoginSessionList(DataiSfLoginSessionDto.toObj(dataiSfLoginSessionDto));
        List<DataiSfLoginSessionVo> voList = list.stream().map(DataiSfLoginSessionVo::objToVo).collect(Collectors.toList());
        return getDataTable(voList);
    }

    /**
     * 导出登录会话信息列表
     */
    @Operation(summary = "导出登录会话信息列表")
    @PreAuthorize("@ss.hasPermi('auth:session:export')")
    @Log(title = "登录会话信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiSfLoginSessionDto dataiSfLoginSessionDto)
    {
        List<DataiSfLoginSession> list = dataiSfLoginSessionService.selectDataiSfLoginSessionList(DataiSfLoginSessionDto.toObj(dataiSfLoginSessionDto));
        ExcelUtil<DataiSfLoginSession> util = new ExcelUtil<DataiSfLoginSession>(DataiSfLoginSession.class);
        util.exportExcel(response, list, "登录会话信息数据");
    }

    /**
     * 获取登录会话信息详细信息
     */
    @Operation(summary = "获取登录会话信息详细信息")
    @PreAuthorize("@ss.hasPermi('auth:session:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        DataiSfLoginSession dataiSfLoginSession = dataiSfLoginSessionService.selectDataiSfLoginSessionById(id);
        return success(DataiSfLoginSessionVo.objToVo(dataiSfLoginSession));
    }

    /**
     * 新增登录会话信息
     */
    @Operation(summary = "新增登录会话信息")
    @PreAuthorize("@ss.hasPermi('auth:session:add')")
    @Log(title = "登录会话信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiSfLoginSessionDto dataiSfLoginSessionDto)
    {
        return toAjax(dataiSfLoginSessionService.insertDataiSfLoginSession(DataiSfLoginSessionDto.toObj(dataiSfLoginSessionDto)));
    }

    /**
     * 修改登录会话信息
     */
    @Operation(summary = "修改登录会话信息")
    @PreAuthorize("@ss.hasPermi('auth:session:edit')")
    @Log(title = "登录会话信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiSfLoginSessionDto dataiSfLoginSessionDto)
    {
        return toAjax(dataiSfLoginSessionService.updateDataiSfLoginSession(DataiSfLoginSessionDto.toObj(dataiSfLoginSessionDto)));
    }

    /**
     * 删除登录会话信息
     */
    @Operation(summary = "删除登录会话信息")
    @PreAuthorize("@ss.hasPermi('auth:session:remove')")
    @Log(title = "登录会话信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) Long[] ids) 
    {
        return toAjax(dataiSfLoginSessionService.deleteDataiSfLoginSessionByIds(ids));
    }
}
