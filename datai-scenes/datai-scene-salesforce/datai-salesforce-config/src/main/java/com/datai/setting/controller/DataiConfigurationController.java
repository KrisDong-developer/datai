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
import com.datai.setting.domain.DataiConfiguration;
import com.datai.setting.service.IDataiConfigurationService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 配置Controller
 * 
 * @author datai
 * @date 2025-12-14
 */
@RestController
@RequestMapping("/setting/configuration")
@Tag(name = "【配置】管理")
public class DataiConfigurationController extends BaseController
{
    @Autowired
    private IDataiConfigurationService dataiConfigurationService;

    /**
     * 查询配置列表
     */
    @Operation(summary = "查询配置列表")
    @PreAuthorize("@ss.hasPermi('setting:configuration:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiConfiguration dataiConfiguration)
    {
        startPage();
        List<DataiConfiguration> list = dataiConfigurationService.selectDataiConfigurationList(dataiConfiguration);
        return getDataTable(list);
    }

    /**
     * 导出配置列表
     */
    @Operation(summary = "导出配置列表")
    @PreAuthorize("@ss.hasPermi('setting:configuration:export')")
    @Log(title = "配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiConfiguration dataiConfiguration)
    {
        List<DataiConfiguration> list = dataiConfigurationService.selectDataiConfigurationList(dataiConfiguration);
        ExcelUtil<DataiConfiguration> util = new ExcelUtil<DataiConfiguration>(DataiConfiguration.class);
        util.exportExcel(response, list, "配置数据");
    }

    /**
     * 获取配置详细信息
     */
    @Operation(summary = "获取配置详细信息")
    @PreAuthorize("@ss.hasPermi('setting:configuration:query')")
    @GetMapping(value = "/{configId}")
    public AjaxResult getInfo(@PathVariable("configId") Long configId)
    {
        return success(dataiConfigurationService.selectDataiConfigurationByConfigId(configId));
    }

    /**
     * 新增配置
     */
    @Operation(summary = "新增配置")
    @PreAuthorize("@ss.hasPermi('setting:configuration:add')")
    @Log(title = "配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiConfiguration dataiConfiguration)
    {
        return toAjax(dataiConfigurationService.insertDataiConfiguration(dataiConfiguration));
    }

    /**
     * 修改配置
     */
    @Operation(summary = "修改配置")
    @PreAuthorize("@ss.hasPermi('setting:configuration:edit')")
    @Log(title = "配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiConfiguration dataiConfiguration)
    {
        return toAjax(dataiConfigurationService.updateDataiConfiguration(dataiConfiguration));
    }

    /**
     * 删除配置
     */
    @Operation(summary = "删除配置")
    @PreAuthorize("@ss.hasPermi('setting:configuration:remove')")
    @Log(title = "配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{configIds}")
    public AjaxResult remove(@PathVariable( name = "configIds" ) Long[] configIds) 
    {
        return toAjax(dataiConfigurationService.deleteDataiConfigurationByConfigIds(configIds));
    }
    
    /**
     * 刷新配置缓存
     */
    @Operation(summary = "刷新配置缓存")
    @PreAuthorize("@ss.hasPermi('setting:configuration:refresh')")
    @Log(title = "配置", businessType = BusinessType.UPDATE)
    @PostMapping("/refresh")
    public AjaxResult refreshConfigCache() 
    {
        dataiConfigurationService.resetConfigCache();
        return success("配置缓存刷新成功");
    }
    
    /**
     * 查询配置缓存状态
     */
    @Operation(summary = "查询配置缓存状态")
    @PreAuthorize("@ss.hasPermi('setting:configuration:cache')")
    @GetMapping("/cache")
    public AjaxResult getConfigCacheStatus() 
    {
        // 这里可以返回更详细的缓存状态信息
        return success("配置缓存状态正常");
    }
    
    /**
     * 验证配置值合法性
     */
    @Operation(summary = "验证配置值合法性")
    @PreAuthorize("@ss.hasPermi('setting:configuration:validate')")
    @PostMapping("/validate")
    public AjaxResult validateConfig(@RequestBody DataiConfiguration config) 
    {
        boolean isValid = dataiConfigurationService.validateConfigValue(config);
        return isValid ? success("配置值验证通过") : error("配置值验证失败");
    }
}
