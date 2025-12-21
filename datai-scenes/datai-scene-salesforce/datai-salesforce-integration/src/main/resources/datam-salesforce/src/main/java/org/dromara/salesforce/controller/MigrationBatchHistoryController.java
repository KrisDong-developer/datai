package org.dromara.salesforce.controller;

import java.util.List;

import lombok.RequiredArgsConstructor;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.constraints.*;
import cn.dev33.satoken.annotation.SaCheckPermission;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.annotation.Validated;
import org.dromara.common.idempotent.annotation.RepeatSubmit;
import org.dromara.common.log.annotation.Log;
import org.dromara.common.web.core.BaseController;
import org.dromara.common.mybatis.core.page.PageQuery;
import org.dromara.common.core.domain.R;
import org.dromara.common.core.validate.AddGroup;
import org.dromara.common.core.validate.EditGroup;
import org.dromara.common.log.enums.BusinessType;
import org.dromara.common.excel.utils.ExcelUtil;
import org.dromara.salesforce.domain.vo.MigrationBatchHistoryVo;
import org.dromara.salesforce.domain.bo.MigrationBatchHistoryBo;
import org.dromara.salesforce.service.IMigrationBatchHistoryService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 迁移批次历史
 *
 * @author Kris
 * @date 2025-08-26
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/salesforce/batchHistory")
public class MigrationBatchHistoryController extends BaseController {

    private final IMigrationBatchHistoryService migrationBatchHistoryService;

    /**
     * 查询迁移批次历史列表
     */
    @SaCheckPermission("salesforce:batchHistory:list")
    @GetMapping("/list")
    public TableDataInfo<MigrationBatchHistoryVo> list(MigrationBatchHistoryBo bo, PageQuery pageQuery) {
        return migrationBatchHistoryService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出迁移批次历史列表
     */
    @SaCheckPermission("salesforce:batchHistory:export")
    @Log(title = "迁移批次历史", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MigrationBatchHistoryBo bo, HttpServletResponse response) {
        List<MigrationBatchHistoryVo> list = migrationBatchHistoryService.queryList(bo);
        ExcelUtil.exportExcel(list, "迁移批次历史", MigrationBatchHistoryVo.class, response);
    }

    /**
     * 获取迁移批次历史详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("salesforce:batchHistory:query")
    @GetMapping("/{id}")
    public R<MigrationBatchHistoryVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(migrationBatchHistoryService.queryById(id));
    }

    /**
     * 新增迁移批次历史
     */
    @SaCheckPermission("salesforce:batchHistory:add")
    @Log(title = "迁移批次历史", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MigrationBatchHistoryBo bo) {
        return toAjax(migrationBatchHistoryService.insertByBo(bo));
    }

    /**
     * 修改迁移批次历史
     */
    @SaCheckPermission("salesforce:batchHistory:edit")
    @Log(title = "迁移批次历史", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MigrationBatchHistoryBo bo) {
        return toAjax(migrationBatchHistoryService.updateByBo(bo));
    }

    /**
     * 删除迁移批次历史
     *
     * @param ids 主键串
     */
    @SaCheckPermission("salesforce:batchHistory:remove")
    @Log(title = "迁移批次历史", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(migrationBatchHistoryService.deleteWithValidByIds(List.of(ids), true));
    }
}
