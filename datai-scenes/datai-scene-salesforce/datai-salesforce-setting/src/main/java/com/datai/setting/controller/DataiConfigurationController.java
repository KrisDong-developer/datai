package com.datai.setting.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.datai.setting.model.vo.DataiConfigurationVo;
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
import com.datai.setting.model.domain.DataiConfiguration;
import com.datai.setting.model.dto.DataiConfigurationDto;
import com.datai.setting.service.IDataiConfigurationService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 配置Controller
 * 
 * @author datai
 * @date 2025-12-24
 */
@RestController
@RequestMapping("/setting/configuration")
@Tag(name = "【配置】管理")
public class DataiConfigurationController extends BaseController
{
    @Autowired
    private IDataiConfigurationService dataiConfigurationService;

    @Autowired
    private IDataiConfigEnvironmentService dataiConfigEnvironmentService;

    /**
     * 查询配置列表
     */
    @Operation(summary = "查询配置列表")
    @PreAuthorize("@ss.hasPermi('setting:configuration:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiConfigurationDto dataiConfigurationDto)
    {
        startPage();
        DataiConfiguration dataiConfiguration = DataiConfigurationDto.toObj(dataiConfigurationDto);
        List<DataiConfiguration> list = dataiConfigurationService.selectDataiConfigurationList(dataiConfiguration);
        
        List<Long> environmentIds = list.stream()
            .map(DataiConfiguration::getEnvironmentId)
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
        
        List<DataiConfigurationVo> voList = list.stream()
            .map(config -> {
                DataiConfigurationVo vo = DataiConfigurationVo.objToVo(config);
                vo.setEnvironmentName(environmentNameMap.get(config.getEnvironmentId()));
                return vo;
            })
            .collect(Collectors.toList());
        
        return getDataTable(voList);
    }

    /**
     * 导出配置列表
     */
    @Operation(summary = "导出配置列表")
    @PreAuthorize("@ss.hasPermi('setting:configuration:export')")
    @Log(title = "配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiConfigurationDto dataiConfigurationDto)
    {
        DataiConfiguration dataiConfiguration = DataiConfigurationDto.toObj(dataiConfigurationDto);
        List<DataiConfiguration> list = dataiConfigurationService.selectDataiConfigurationList(dataiConfiguration);
        ExcelUtil<DataiConfiguration> util = new ExcelUtil<DataiConfiguration>(DataiConfiguration.class);
        util.exportExcel(response, list, "配置数据");
    }

    /**
     * 获取配置详细信息
     */
    @Operation(summary = "获取配置详细信息")
    @PreAuthorize("@ss.hasPermi('setting:configuration:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        DataiConfiguration dataiConfiguration = dataiConfigurationService.selectDataiConfigurationById(id);
        DataiConfigurationVo dataiConfigurationVo = DataiConfigurationVo.objToVo(dataiConfiguration);
        if (dataiConfigurationVo != null && dataiConfigurationVo.getEnvironmentId() != null) {
            var env = dataiConfigEnvironmentService.selectDataiConfigEnvironmentById(dataiConfigurationVo.getEnvironmentId());
            if (env != null) {
                dataiConfigurationVo.setEnvironmentName(env.getEnvironmentName());
            }
        }
        return success(dataiConfigurationVo);
    }

    /**
     * 新增配置
     */
    @Operation(summary = "新增配置")
    @PreAuthorize("@ss.hasPermi('setting:configuration:add')")
    @Log(title = "配置", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiConfigurationDto dataiConfigurationDto)
    {
        DataiConfiguration dataiConfiguration = DataiConfigurationDto.toObj(dataiConfigurationDto);
        return toAjax(dataiConfigurationService.insertDataiConfiguration(dataiConfiguration));
    }

    /**
     * 修改配置
     */
    @Operation(summary = "修改配置")
    @PreAuthorize("@ss.hasPermi('setting:configuration:edit')")
    @Log(title = "配置", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiConfigurationDto dataiConfigurationDto)
    {
        DataiConfiguration dataiConfiguration = DataiConfigurationDto.toObj(dataiConfigurationDto);
        return toAjax(dataiConfigurationService.updateDataiConfiguration(dataiConfiguration));
    }

    /**
     * 删除配置
     */
    @Operation(summary = "删除配置")
    @PreAuthorize("@ss.hasPermi('setting:configuration:remove')")
    @Log(title = "配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) Long[] ids) 
    {
        return toAjax(dataiConfigurationService.deleteDataiConfigurationByIds(ids));
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


}
