package com.datai.auth.controller;

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
import com.datai.auth.domain.DataiSfToken;
import com.datai.auth.service.IDataiSfTokenService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 令牌Controller
 * 
 * @author datai
 * @date 2025-12-14
 */
@RestController
@RequestMapping("/auth/token")
@Tag(name = "【令牌】管理")
public class DataiSfTokenController extends BaseController
{
    @Autowired
    private IDataiSfTokenService dataiSfTokenService;

    /**
     * 查询令牌列表
     */
    @Operation(summary = "查询令牌列表")
    @PreAuthorize("@ss.hasPermi('auth:token:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiSfToken dataiSfToken)
    {
        startPage();
        List<DataiSfToken> list = dataiSfTokenService.selectDataiSfTokenList(dataiSfToken);
        return getDataTable(list);
    }

    /**
     * 导出令牌列表
     */
    @Operation(summary = "导出令牌列表")
    @PreAuthorize("@ss.hasPermi('auth:token:export')")
    @Log(title = "令牌", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiSfToken dataiSfToken)
    {
        List<DataiSfToken> list = dataiSfTokenService.selectDataiSfTokenList(dataiSfToken);
        ExcelUtil<DataiSfToken> util = new ExcelUtil<DataiSfToken>(DataiSfToken.class);
        util.exportExcel(response, list, "令牌数据");
    }

    /**
     * 获取令牌详细信息
     */
    @Operation(summary = "获取令牌详细信息")
    @PreAuthorize("@ss.hasPermi('auth:token:query')")
    @GetMapping(value = "/{tokenId}")
    public AjaxResult getInfo(@PathVariable("tokenId") Long tokenId)
    {
        return success(dataiSfTokenService.selectDataiSfTokenByTokenId(tokenId));
    }

    /**
     * 新增令牌
     */
    @Operation(summary = "新增令牌")
    @PreAuthorize("@ss.hasPermi('auth:token:add')")
    @Log(title = "令牌", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiSfToken dataiSfToken)
    {
        return toAjax(dataiSfTokenService.insertDataiSfToken(dataiSfToken));
    }

    /**
     * 修改令牌
     */
    @Operation(summary = "修改令牌")
    @PreAuthorize("@ss.hasPermi('auth:token:edit')")
    @Log(title = "令牌", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiSfToken dataiSfToken)
    {
        return toAjax(dataiSfTokenService.updateDataiSfToken(dataiSfToken));
    }

    /**
     * 删除令牌
     */
    @Operation(summary = "删除令牌")
    @PreAuthorize("@ss.hasPermi('auth:token:remove')")
    @Log(title = "令牌", businessType = BusinessType.DELETE)
    @DeleteMapping("/{tokenIds}")
    public AjaxResult remove(@PathVariable( name = "tokenIds" ) Long[] tokenIds) 
    {
        return toAjax(dataiSfTokenService.deleteDataiSfTokenByTokenIds(tokenIds));
    }
}
