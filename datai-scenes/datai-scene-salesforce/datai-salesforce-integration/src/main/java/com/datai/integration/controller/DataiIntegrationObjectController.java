package com.datai.integration.controller;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.datai.common.utils.PageUtils;
import com.datai.integration.model.vo.DataiIntegrationObjectVo;
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
import com.datai.integration.model.domain.DataiIntegrationObject;
import com.datai.integration.model.dto.DataiIntegrationObjectDto;
import com.datai.integration.service.IDataiIntegrationObjectService;
import com.datai.integration.service.IDataiIntegrationFieldService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

/**
 * 对象同步控制Controller
 * 
 * @author datai
 * @date 2025-12-24
 */
@RestController
@RequestMapping("/integration/object")
@Tag(name = "【对象同步控制】管理")
@Slf4j
public class DataiIntegrationObjectController extends BaseController
{
    @Autowired
    private IDataiIntegrationObjectService dataiIntegrationObjectService;

    /**
     * 查询对象同步控制列表
     */
    @Operation(summary = "查询对象同步控制列表")
    @PreAuthorize("@ss.hasPermi('integration:object:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiIntegrationObjectDto dataiIntegrationObjectDto)
    {
        startPage();
        DataiIntegrationObject dataiIntegrationObject = DataiIntegrationObjectDto.toObj(dataiIntegrationObjectDto);
        List<DataiIntegrationObject> list = dataiIntegrationObjectService.selectDataiIntegrationObjectList(dataiIntegrationObject);
        List<DataiIntegrationObjectVo> voList = list.stream().map(DataiIntegrationObjectVo::objToVo).collect(Collectors.toList());
        return getDataTableByPage(voList,PageUtils.getTotal(list));
    }

    /**
     * 导出对象同步控制列表
     */
    @Operation(summary = "导出对象同步控制列表")
    @PreAuthorize("@ss.hasPermi('integration:object:export')")
    @Log(title = "对象同步控制", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiIntegrationObjectDto dataiIntegrationObjectDto)
    {
        DataiIntegrationObject dataiIntegrationObject = DataiIntegrationObjectDto.toObj(dataiIntegrationObjectDto);
        List<DataiIntegrationObject> list = dataiIntegrationObjectService.selectDataiIntegrationObjectList(dataiIntegrationObject);
        ExcelUtil<DataiIntegrationObject> util = new ExcelUtil<DataiIntegrationObject>(DataiIntegrationObject.class);
        util.exportExcel(response, list, "对象同步控制数据");
    }

    /**
     * 获取对象同步控制详细信息
     */
    @Operation(summary = "获取对象同步控制详细信息")
    @PreAuthorize("@ss.hasPermi('integration:object:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id)
    {
        DataiIntegrationObject dataiIntegrationObject = dataiIntegrationObjectService.selectDataiIntegrationObjectById(id);
        DataiIntegrationObjectVo vo = DataiIntegrationObjectVo.objToVo(dataiIntegrationObject);
        return success(vo);
    }

    /**
     * 新增对象同步控制
     */
    @Operation(summary = "新增对象同步控制")
    @PreAuthorize("@ss.hasPermi('integration:object:add')")
    @Log(title = "对象同步控制", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiIntegrationObjectDto dataiIntegrationObjectDto)
    {
        DataiIntegrationObject dataiIntegrationObject = DataiIntegrationObjectDto.toObj(dataiIntegrationObjectDto);
        return toAjax(dataiIntegrationObjectService.insertDataiIntegrationObject(dataiIntegrationObject));
    }

    /**
     * 修改对象同步控制
     */
    @Operation(summary = "修改对象同步控制")
    @PreAuthorize("@ss.hasPermi('integration:object:edit')")
    @Log(title = "对象同步控制", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiIntegrationObjectDto dataiIntegrationObjectDto)
    {
        DataiIntegrationObject dataiIntegrationObject = DataiIntegrationObjectDto.toObj(dataiIntegrationObjectDto);
        return toAjax(dataiIntegrationObjectService.updateDataiIntegrationObject(dataiIntegrationObject));
    }

    /**
     * 删除对象同步控制
     */
    @Operation(summary = "删除对象同步控制")
    @PreAuthorize("@ss.hasPermi('integration:object:remove')")
    @Log(title = "对象同步控制", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) Integer[] ids) 
    {
        return toAjax(dataiIntegrationObjectService.deleteDataiIntegrationObjectByIds(ids));
    }

    /**
     * 获取对象同步统计信息
     */
    @Operation(summary = "获取对象同步统计信息")
    @PreAuthorize("@ss.hasPermi('integration:object:statistics')")
    @GetMapping("/{id}/statistics")
    public AjaxResult getSyncStatistics(@PathVariable("id") Integer id)
    {
        Map<String, Object> statistics = dataiIntegrationObjectService.getSyncStatistics(id);
        return success(statistics);
    }

    /**
     * 获取对象依赖关系
     */
    @Operation(summary = "获取对象依赖关系")
    @PreAuthorize("@ss.hasPermi('integration:object:dependencies')")
    @GetMapping("/{id}/dependencies")
    public AjaxResult getObjectDependencies(@PathVariable("id") Integer id)
    {
        List<Map<String, Object>> dependencies = dataiIntegrationObjectService.getObjectDependencies(id);
        return success(dependencies);
    }

    /**
     * 创建对象表结构
     */
    @Operation(summary = "创建对象表结构")
    @PreAuthorize("@ss.hasPermi('integration:object:createStructure')")
    @Log(title = "对象同步控制", businessType = BusinessType.INSERT)
    @PostMapping("/{id}/createStructure")
    public AjaxResult createObjectStructure(@PathVariable("id") Integer id)
    {
        Map<String, Object> result = dataiIntegrationObjectService.createObjectStructure(id);
        if ((Boolean) result.get("success")) {
            return success(result);
        } else {
            return error((String) result.get("message"));
        }
    }

    /**
     * 变更对象启用同步状态
     */
    @Operation(summary = "变更对象启用同步状态")
    @PreAuthorize("@ss.hasPermi('integration:object:updateWorkStatus')")
    @Log(title = "对象同步控制", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/workStatus")
    public AjaxResult updateWorkStatus(@PathVariable("id") Integer id, @org.springframework.web.bind.annotation.RequestParam("isWork") Boolean isWork)
    {
        Map<String, Object> result = dataiIntegrationObjectService.updateWorkStatus(id, isWork);
        if ((Boolean) result.get("success")) {
            return success(result);
        } else {
            return error((String) result.get("message"));
        }
    }

    /**
     * 变更对象增量更新状态
     */
    @Operation(summary = "变更对象增量更新状态")
    @PreAuthorize("@ss.hasPermi('integration:object:updateIncrementalStatus')")
    @Log(title = "对象同步控制", businessType = BusinessType.UPDATE)
    @PutMapping("/{id}/incrementalStatus")
    public AjaxResult updateIncrementalStatus(@PathVariable("id") Integer id, @org.springframework.web.bind.annotation.RequestParam("isIncremental") Boolean isIncremental)
    {
        Map<String, Object> result = dataiIntegrationObjectService.updateIncrementalStatus(id, isIncremental);
        if ((Boolean) result.get("success")) {
            return success(result);
        } else {
            return error((String) result.get("message"));
        }
    }

    /**
     * 获取对象整体统计信息
     */
    @Operation(summary = "获取对象整体统计信息")
    @PreAuthorize("@ss.hasPermi('integration:object:statistics')")
    @GetMapping("/statistics")
    public AjaxResult getObjectStatistics()
    {
        Map<String, Object> statistics = dataiIntegrationObjectService.getObjectStatistics();
        if ((Boolean) statistics.get("success")) {
            return success(statistics);
        } else {
            return error((String) statistics.get("message"));
        }
    }

    /**
     * 同步单对象数据到本地数据库
     * 
     * 该方法用于触发指定对象的单次数据同步操作，将Salesforce对象的数据同步到本地数据库
     * 同步操作包括全量同步和增量同步两种模式，具体模式由对象的配置决定
     * 
     * @param id 对象ID，用于标识需要同步的Salesforce对象
     * @return AjaxResult 同步结果，包含成功/失败状态、同步数据量、耗时等信息
     *         - 成功时返回：success=true, message="对象数据同步成功", 以及详细的同步信息
     *         - 失败时返回：success=false, message=错误信息
     */
    @Operation(summary = "同步单对象数据到本地数据库")
    @PreAuthorize("@ss.hasPermi('integration:object:syncData')")
    @Log(title = "对象同步控制", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/syncData")
    public AjaxResult syncObjectData(@PathVariable("id") Integer id)
    {
        if (id == null) {
            log.error("对象ID为空，无法同步数据");
            return error("对象ID不能为空");
        }

        try {
            log.info("开始同步对象数据，对象ID: {}", id);

            Map<String, Object> result = dataiIntegrationObjectService.syncSingleObjectData(id);

            if ((Boolean) result.get("success")) {
                log.info("对象数据同步成功，对象ID: {}", id);
                return success(result);
            } else {
                log.error("对象数据同步失败，对象ID: {}, 错误信息: {}", id, result.get("message"));
                return error((String) result.get("message"));
            }

        } catch (Exception e) {
            log.error("同步对象数据时发生异常，对象ID: {}", id, e);
            return error("同步对象数据时发生异常: " + e.getMessage());
        }
    }

    /**
     * 同步多个对象数据到本地数据库
     * 
     * 该方法用于触发多个对象的批量数据同步操作，将多个Salesforce对象的数据同步到本地数据库
     * 同步操作包括全量同步和增量同步两种模式，具体模式由每个对象的配置决定
     * 该方法会依次同步每个对象，即使某个对象同步失败，也会继续同步其他对象
     * 
     * @param ids 对象ID数组，用于标识需要同步的多个Salesforce对象
     * @return AjaxResult 同步结果，包含成功/失败状态、每个对象的同步结果、总耗时等信息
     *         - 成功时返回：success=true, message="多对象数据同步完成", 以及详细的同步信息
     *         - 失败时返回：success=false, message=错误信息
     */
    @Operation(summary = "同步多个对象数据到本地数据库")
    @PreAuthorize("@ss.hasPermi('integration:object:syncData')")
    @Log(title = "对象同步控制", businessType = BusinessType.UPDATE)
    @PostMapping("/syncMultipleData")
    public AjaxResult syncMultipleObjectData(@RequestBody Integer[] ids)
    {
        if (ids == null || ids.length == 0) {
            log.error("对象ID数组为空，无法同步数据");
            return error("对象ID不能为空");
        }

        try {
            log.info("开始同步多个对象数据，对象ID数量: {}", ids.length);

            Map<String, Object> result = dataiIntegrationObjectService.syncMultipleObjectData(ids);

            if ((Boolean) result.get("success")) {
                log.info("多对象数据同步完成，成功数量: {}, 失败数量: {}", 
                    result.get("successCount"), result.get("failureCount"));
                return success(result);
            } else {
                log.error("多对象数据同步失败，错误信息: {}", result.get("message"));
                return error((String) result.get("message"));
            }

        } catch (Exception e) {
            log.error("同步多个对象数据时发生异常", e);
            return error("同步多个对象数据时发生异常: " + e.getMessage());
        }
    }
}
