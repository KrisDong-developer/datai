package com.datai.integration.controller;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import com.datai.salesforce.common.utils.ValidationUtils;

import com.datai.common.utils.PageUtils;
import com.datai.integration.model.domain.DataiIntegrationMetadataChange;
import com.datai.integration.model.vo.DataiIntegrationMetadataChangeVo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.datai.common.annotation.Log;
import com.datai.common.core.controller.BaseController;
import com.datai.common.core.domain.AjaxResult;
import com.datai.common.enums.BusinessType;
import com.datai.integration.model.dto.DataiIntegrationMetadataChangeDto;
import com.datai.integration.service.IDataiIntegrationMetadataChangeService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;

/**
 * 对象元数据变更Controller
 * 
 * @author datai
 * @date 2025-12-27 */@Tag(name = "对象元数据变更")
@RestController
@RequestMapping("/integration/change")
@Slf4j
public class DataiIntegrationMetadataChangeController extends BaseController {
    
    @Autowired
    private IDataiIntegrationMetadataChangeService dataiIntegrationMetadataChangeService;

    /**
     * 查询对象元数据变更列表
     */
    @Operation(summary = "查询对象元数据变更列表")
    @PreAuthorize("@ss.hasPermi('integration:change:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataiIntegrationMetadataChangeDto dataiIntegrationMetadataChangeDto)
    {
        startPage();
        DataiIntegrationMetadataChange dataiIntegrationMetadataChange = DataiIntegrationMetadataChangeDto.toObj(dataiIntegrationMetadataChangeDto);
        List<DataiIntegrationMetadataChange> list = dataiIntegrationMetadataChangeService.selectDataiIntegrationMetadataChangeList(dataiIntegrationMetadataChange);
        List<DataiIntegrationMetadataChangeVo> voList = list.stream().map(DataiIntegrationMetadataChangeVo::objToVo).collect(Collectors.toList());
        return getDataTableByPage(voList,PageUtils.getTotal(list));
    }

    /**
     * 查询未同步的元数据变更列表
     */
    @Operation(summary = "查询未同步的元数据变更列表")
    @PreAuthorize("@ss.hasPermi('integration:change:unsynced')")
    @GetMapping("/unsynced")
    public TableDataInfo getUnsyncedChanges(DataiIntegrationMetadataChangeDto dataiIntegrationMetadataChangeDto)
    {
        startPage();
        DataiIntegrationMetadataChange dataiIntegrationMetadataChange = DataiIntegrationMetadataChangeDto.toObj(dataiIntegrationMetadataChangeDto);
        List<DataiIntegrationMetadataChange> list = dataiIntegrationMetadataChangeService.selectUnsyncedMetadataChangeList(dataiIntegrationMetadataChange);
        List<DataiIntegrationMetadataChangeVo> voList = list.stream().map(DataiIntegrationMetadataChangeVo::objToVo).collect(Collectors.toList());
        return getDataTableByPage(voList,PageUtils.getTotal(list));
    }

    /**
     * 导出对象元数据变更列表
     */
    @Operation(summary = "导出对象元数据变更列表")
    @PreAuthorize("@ss.hasPermi('integration:change:export')")
    @Log(title = "对象元数据变更", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataiIntegrationMetadataChangeDto dataiIntegrationMetadataChangeDto)
    {
        DataiIntegrationMetadataChange dataiIntegrationMetadataChange = DataiIntegrationMetadataChangeDto.toObj(dataiIntegrationMetadataChangeDto);
        List<DataiIntegrationMetadataChange> list = dataiIntegrationMetadataChangeService.selectDataiIntegrationMetadataChangeList(dataiIntegrationMetadataChange);
        ExcelUtil<DataiIntegrationMetadataChange> util = new ExcelUtil<DataiIntegrationMetadataChange>(DataiIntegrationMetadataChange.class);
        util.exportExcel(response, list, "对象元数据变更数据");
    }

    /**
     * 获取对象元数据变更详细信息
     */
    @Operation(summary = "获取对象元数据变更详细信息")
    @PreAuthorize("@ss.hasPermi('integration:change:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Long id)
    {
        DataiIntegrationMetadataChange dataiIntegrationMetadataChange = dataiIntegrationMetadataChangeService.selectDataiIntegrationMetadataChangeById(id);
        DataiIntegrationMetadataChangeVo vo = DataiIntegrationMetadataChangeVo.objToVo(dataiIntegrationMetadataChange);
        return success(vo);
    }

    /**
     * 新增对象元数据变更
     */
    @Operation(summary = "新增对象元数据变更")
    @PreAuthorize("@ss.hasPermi('integration:change:add')")
    @Log(title = "对象元数据变更", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataiIntegrationMetadataChangeDto dataiIntegrationMetadataChangeDto)
    {
        DataiIntegrationMetadataChange dataiIntegrationMetadataChange = DataiIntegrationMetadataChangeDto.toObj(dataiIntegrationMetadataChangeDto);
        return toAjax(dataiIntegrationMetadataChangeService.insertDataiIntegrationMetadataChange(dataiIntegrationMetadataChange));
    }

    /**
     * 修改对象元数据变更
     */
    @Operation(summary = "修改对象元数据变更")
    @PreAuthorize("@ss.hasPermi('integration:change:edit')")
    @Log(title = "对象元数据变更", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataiIntegrationMetadataChangeDto dataiIntegrationMetadataChangeDto)
    {
        DataiIntegrationMetadataChange dataiIntegrationMetadataChange = DataiIntegrationMetadataChangeDto.toObj(dataiIntegrationMetadataChangeDto);
        return toAjax(dataiIntegrationMetadataChangeService.updateDataiIntegrationMetadataChange(dataiIntegrationMetadataChange));
    }

    /**
     * 删除对象元数据变更
     */
    @Operation(summary = "删除对象元数据变更")
    @PreAuthorize("@ss.hasPermi('integration:change:remove')")
    @Log(title = "对象元数据变更", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable( name = "ids" ) Long[] ids) 
    {
        return toAjax(dataiIntegrationMetadataChangeService.deleteDataiIntegrationMetadataChangeByIds(ids));
    }

    /**
     * 批量更新元数据变更同步状态
     */
    @Operation(summary = "批量更新元数据变更同步状态")
    @PreAuthorize("@ss.hasPermi('integration:change:updateSync')")
    @Log(title = "更新元数据变更同步状态", businessType = BusinessType.UPDATE)
    @PutMapping("/syncStatus")
    public AjaxResult updateSyncStatus(@RequestBody java.util.Map<String, Object> params)
    {
        Long[] ids = ((java.util.List<Long>) params.get("ids")).toArray(new Long[0]);
        Integer syncStatus = ((Number) params.get("syncStatus")).intValue();
        String syncErrorMessage = (String) params.get("syncErrorMessage");
        return toAjax(dataiIntegrationMetadataChangeService.batchUpdateSyncStatus(ids, syncStatus, syncErrorMessage));
    }

    /**
     * 获取变更统计信息
     */
    @Operation(summary = "获取变更统计信息")
    @PreAuthorize("@ss.hasPermi('integration:change:statistics')")
    @GetMapping("/statistics")
    public AjaxResult getChangeStatistics(@RequestParam(required = false) String changeType,
                                          @RequestParam(required = false) String operationType,
                                          @RequestParam(required = false) String objectApi,
                                          @RequestParam(required = false) Boolean syncStatus,
                                          @RequestParam(required = false) Boolean isCustom,
                                          @RequestParam(required = false) String startTime,
                                          @RequestParam(required = false) String endTime)
    {
        // 1. 验证时间格式
        if (!ValidationUtils.validateTimeFormat(startTime)) {
            return error("开始时间格式不正确，请使用 yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd 格式");
        }
        if (!ValidationUtils.validateTimeFormat(endTime)) {
            return error("结束时间格式不正确，请使用 yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd 格式");
        }
        
        // 2. 验证日期范围
        if (startTime != null && !startTime.isEmpty() && endTime != null && !endTime.isEmpty()) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                LocalDateTime startDateTime = null;
                LocalDateTime endDateTime = null;
                
                // 尝试解析完整格式
                try {
                    startDateTime = LocalDateTime.parse(startTime, formatter);
                } catch (DateTimeParseException e) {
                    // 尝试解析日期格式
                    try {
                        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        startDateTime = LocalDate.parse(startTime, formatter).atStartOfDay();
                    } catch (DateTimeParseException ex) {
                        // 已经在上面的validateTimeFormat中验证过，这里不会执行到
                    }
                }
                
                try {
                    endDateTime = LocalDateTime.parse(endTime, formatter);
                } catch (DateTimeParseException e) {
                    // 尝试解析日期格式
                    try {
                        formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                        endDateTime = LocalDate.parse(endTime, formatter).atTime(23, 59, 59);
                    } catch (DateTimeParseException ex) {
                        // 已经在上面的validateTimeFormat中验证过，这里不会执行到
                    }
                }
                
                if (startDateTime != null && endDateTime != null && startDateTime.isAfter(endDateTime)) {
                    return error("开始时间不能晚于结束时间");
                }
            } catch (Exception e) {
                return error("日期范围验证失败: " + e.getMessage());
            }
        }
        
        // 3. 验证其他参数的有效性
        // 验证changeType
        if (changeType != null && !changeType.isEmpty()) {
            List<String> validChangeTypes = java.util.Arrays.asList("OBJECT", "FIELD");
            if (!validChangeTypes.contains(changeType)) {
                return error("变更类型不正确，支持的类型：OBJECT, FIELD");
            }
        }
        
        // 验证operationType
        if (operationType != null && !operationType.isEmpty()) {
            List<String> validOperationTypes = java.util.Arrays.asList("CREATE", "UPDATE", "DELETE");
            if (!validOperationTypes.contains(operationType)) {
                return error("操作类型不正确，支持的类型：CREATE, UPDATE, DELETE");
            }
        }
        
        // 4. 构建参数
        Map<String, Object> params = new java.util.HashMap<>();
        if (changeType != null && !changeType.isEmpty()) {
            params.put("changeType", changeType);
        }
        if (operationType != null && !operationType.isEmpty()) {
            params.put("operationType", operationType);
        }
        if (objectApi != null && !objectApi.isEmpty()) {
            params.put("objectApi", objectApi);
        }
        if (syncStatus != null) {
            params.put("syncStatus", syncStatus);
        }
        if (isCustom != null) {
            params.put("isCustom", isCustom);
        }
        if (startTime != null && !startTime.isEmpty()) {
            params.put("startTime", startTime);
        }
        if (endTime != null && !endTime.isEmpty()) {
            params.put("endTime", endTime);
        }
        
        // 5. 调用服务层方法
        Map<String, Object> statistics = dataiIntegrationMetadataChangeService.getChangeStatistics(params);
        return success(statistics);
    }

    /**
     * 获取分组变更统计信息
     */
    @Operation(summary = "获取分组变更统计信息")
    @PreAuthorize("@ss.hasPermi('integration:change:statistics')")
    @GetMapping("/statistics/group")
    public AjaxResult getChangeStatisticsByGroup(@RequestParam(required = true) String groupBy,
                                                @RequestParam(required = false) String changeType,
                                                @RequestParam(required = false) String operationType,
                                                @RequestParam(required = false) String objectApi,
                                                @RequestParam(required = false) Boolean syncStatus,
                                                @RequestParam(required = false) Boolean isCustom,
                                                @RequestParam(required = false) String startTime,
                                                @RequestParam(required = false) String endTime)
    {
        // 验证分组维度
        if (!ValidationUtils.validateGroupBy(groupBy)) {
            return error("分组维度不正确，支持的维度：changeType, operationType, objectApi, syncStatus, isCustom");
        }
        
        // 验证时间格式
        if (!ValidationUtils.validateTimeFormat(startTime)) {
            return error("开始时间格式不正确，请使用 yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd 格式");
        }
        if (!ValidationUtils.validateTimeFormat(endTime)) {
            return error("结束时间格式不正确，请使用 yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd 格式");
        }
        
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("groupBy", groupBy);
        if (changeType != null && !changeType.isEmpty()) {
            params.put("changeType", changeType);
        }
        if (operationType != null && !operationType.isEmpty()) {
            params.put("operationType", operationType);
        }
        if (objectApi != null && !objectApi.isEmpty()) {
            params.put("objectApi", objectApi);
        }
        if (syncStatus != null) {
            params.put("syncStatus", syncStatus);
        }
        if (isCustom != null) {
            params.put("isCustom", isCustom);
        }
        if (startTime != null && !startTime.isEmpty()) {
            params.put("startTime", startTime);
        }
        if (endTime != null && !endTime.isEmpty()) {
            params.put("endTime", endTime);
        }
        
        Map<String, Object> statistics = dataiIntegrationMetadataChangeService.getChangeStatisticsByGroup(params);
        return success(statistics);
    }

    /**
     * 获取趋势变更统计信息
     */
    @Operation(summary = "获取趋势变更统计信息")
    @PreAuthorize("@ss.hasPermi('integration:change:statistics')")
    @GetMapping("/statistics/trend")
    public AjaxResult getChangeStatisticsByTrend(@RequestParam(required = true) String timeUnit,
                                                @RequestParam(required = false) String changeType,
                                                @RequestParam(required = false) String operationType,
                                                @RequestParam(required = false) String objectApi,
                                                @RequestParam(required = false) Boolean syncStatus,
                                                @RequestParam(required = false) Boolean isCustom,
                                                @RequestParam(required = false) String startTime,
                                                @RequestParam(required = false) String endTime)
    {
        // 验证时间维度
        if (!ValidationUtils.validateTimeUnit(timeUnit)) {
            return error("时间维度不正确，支持的维度：day, week, month, quarter");
        }
        
        // 验证时间格式
        if (!ValidationUtils.validateTimeFormat(startTime)) {
            return error("开始时间格式不正确，请使用 yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd 格式");
        }
        if (!ValidationUtils.validateTimeFormat(endTime)) {
            return error("结束时间格式不正确，请使用 yyyy-MM-dd HH:mm:ss 或 yyyy-MM-dd 格式");
        }
        
        Map<String, Object> params = new java.util.HashMap<>();
        params.put("timeUnit", timeUnit);
        if (changeType != null && !changeType.isEmpty()) {
            params.put("changeType", changeType);
        }
        if (operationType != null && !operationType.isEmpty()) {
            params.put("operationType", operationType);
        }
        if (objectApi != null && !objectApi.isEmpty()) {
            params.put("objectApi", objectApi);
        }
        if (syncStatus != null) {
            params.put("syncStatus", syncStatus);
        }
        if (isCustom != null) {
            params.put("isCustom", isCustom);
        }
        if (startTime != null && !startTime.isEmpty()) {
            params.put("startTime", startTime);
        }
        if (endTime != null && !endTime.isEmpty()) {
            params.put("endTime", endTime);
        }
        
        Map<String, Object> statistics = dataiIntegrationMetadataChangeService.getChangeStatisticsByTrend(params);
        return success(statistics);
    }

    /**
     * 同步元数据变更到本地数据库
     * 根据元数据变更ID将指定的元数据变更同步到本地数据库
     * 
     * 该方法会：
     * 1. 根据ID查询元数据变更记录
     * 2. 根据变更类型（OBJECT或FIELD）执行相应的同步操作
     * 3. 对于对象变更：执行对象的创建、修改或删除操作
     * 4. 对于字段变更：执行字段的创建、修改或删除操作
     * 5. 更新元数据变更记录的同步状态
     * 
     * @param id 元数据变更ID
     * @return 同步结果，包含success（是否成功）和message（消息）字段
     */
    @Operation(summary = "同步元数据变更到本地数据库")
    @PreAuthorize("@ss.hasPermi('integration:change:sync')")
    @Log(title = "同步元数据变更", businessType = BusinessType.UPDATE)
    @PostMapping("/{id}/sync")
    public AjaxResult syncToLocalDatabase(@PathVariable("id") Long id)
    {
        try {
            Map<String, Object> result = dataiIntegrationMetadataChangeService.syncToLocalDatabase(id);
            return success(result);
        } catch (Exception e) {
            return error("同步失败: " + e.getMessage());
        }
    }

    /**
     * 批量同步元数据变更到本地数据库
     * 根据元数据变更ID数组将指定的元数据变更批量同步到本地数据库
     * 
     * 该方法会：
     * 1. 使用线程池并发执行多个元数据变更的同步操作，提高同步效率
     * 2. 根据ID数组查询元数据变更记录
     * 3. 根据变更类型（OBJECT或FIELD）执行相应的同步操作
     * 4. 对于对象变更：执行对象的创建、修改或删除操作
     * 5. 对于字段变更：执行字段的创建、修改或删除操作
     * 6. 更新元数据变更记录的同步状态
     * 7. 提供实时的同步进度反馈（每10条记录输出一次进度）
     * 8. 汇总同步结果，包含成功和失败的详细信息
     * 
     * 优化特性：
     * - 并发处理：使用SalesforceExecutor线程池并发执行同步任务，大幅提升处理速度
     * - 进度反馈：实时输出同步进度，方便监控大批量同步的执行状态
     * - 异常隔离：单个同步任务失败不会影响其他任务的执行
     * - 结果汇总：提供详细的同步结果统计，包括总数、成功数、失败数和详细信息
     * - 线程安全：使用AtomicInteger和synchronizedList保证并发环境下的数据一致性
     * 
     * @return 同步结果，包含：
     *         - success: 是否全部成功
     *         - message: 同步结果摘要信息
     *         - data: 详细数据
     *           - totalCount: 总记录数
     *           - successCount: 成功数量
     *           - failCount: 失败数量
     *           - details: 每条记录的同步详情列表
     *             - id: 元数据变更ID
     *             - success: 是否成功
     *             - message: 结果消息
     */
    @Operation(summary = "批量同步元数据变更到本地数据库")
    @PreAuthorize("@ss.hasPermi('integration:change:syncBatch')")
    @Log(title = "批量同步元数据变更", businessType = BusinessType.UPDATE)
    @PostMapping("/syncBatch")
    public AjaxResult syncBatchToLocalDatabase(@PathVariable( name = "ids" ) Long[] ids)
    {
        try {
            Map<String, Object> result = dataiIntegrationMetadataChangeService.syncBatchToLocalDatabase(ids);
            return success(result);
        } catch (Exception e) {
            return error("批量同步失败: " + e.getMessage());
        }
    }

    /**
     * 全对象元数据变更拉取
     * 从Salesforce拉取所有对象的元数据变更信息并记录到元数据变更表中
     * 该方法会：
     * 1. 连接到Salesforce获取所有对象的元数据
     * 2. 比较现有数据库中的对象元数据
     * 3. 记录对象级别的变更（新增、修改、删除）
     * 4. 记录字段级别的变更（新增、修改、删除）
     * 5. 检测并记录已从Salesforce中删除的对象
     * 
     * 表的变更新增需要满足以下任一条件：
     * - isQueryable (可查询)
     * - isCreateable (可创建)
     * - isUpdateable (可更新)
     * - isDeletable (可删除)
     * 字段的变更新增无限制
     * 
     * @return 包含拉取结果的AjaxResult，包含对象变更数量和字段变更数量
     */
    @Operation(summary = "全对象元数据变更拉取")
    @PreAuthorize("@ss.hasPermi('integration:change:pullAll')")
    @Log(title = "全对象元数据变更拉取", businessType = BusinessType.OTHER)
    @PostMapping("/pullAll")
    public AjaxResult pullAllMetadataChanges()
    {
        try {
            // 调用服务层方法执行全对象元数据变更拉取
            Map<String, Object> result = dataiIntegrationMetadataChangeService.pullAllMetadataChanges();
            return success(result);
        } catch (Exception e) {
            // 记录拉取过程中发生的异常
            log.error("全对象元数据变更拉取时发生异常", e);
            return error("全对象元数据变更拉取失败: " + e.getMessage());
        }
    }
}
