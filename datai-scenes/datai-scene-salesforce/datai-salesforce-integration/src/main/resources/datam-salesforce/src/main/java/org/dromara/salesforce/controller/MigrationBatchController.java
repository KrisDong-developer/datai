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
import org.dromara.salesforce.domain.vo.MigrationBatchVo;
import org.dromara.salesforce.domain.bo.MigrationBatchBo;
import org.dromara.salesforce.service.IMigrationBatchService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 迁移批次
 *
 * @author Kris
 * @date 2025-08-26
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/salesforce/batch")
public class MigrationBatchController extends BaseController {

    private final IMigrationBatchService migrationBatchService;

    /**
     * 查询迁移批次列表
     */
    @SaCheckPermission("salesforce:batch:list")
    @GetMapping("/list")
    public TableDataInfo<MigrationBatchVo> list(MigrationBatchBo bo, PageQuery pageQuery) {
        return migrationBatchService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出迁移批次列表
     */
    @SaCheckPermission("salesforce:batch:export")
    @Log(title = "迁移批次", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MigrationBatchBo bo, HttpServletResponse response) {
        List<MigrationBatchVo> list = migrationBatchService.queryList(bo);
        ExcelUtil.exportExcel(list, "迁移批次", MigrationBatchVo.class, response);
    }

    /**
     * 获取迁移批次详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("salesforce:batch:query")
    @GetMapping("/{id}")
    public R<MigrationBatchVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(migrationBatchService.queryById(id));
    }

    /**
     * 新增迁移批次
     */
    @SaCheckPermission("salesforce:batch:add")
    @Log(title = "迁移批次", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MigrationBatchBo bo) {
        return toAjax(migrationBatchService.insertByBo(bo));
    }

    /**
     * 修改迁移批次
     */
    @SaCheckPermission("salesforce:batch:edit")
    @Log(title = "迁移批次", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MigrationBatchBo bo) {
        return toAjax(migrationBatchService.updateByBo(bo));
    }

    /**
     * 删除迁移批次
     *
     * @param ids 主键串
     */
    @SaCheckPermission("salesforce:batch:remove")
    @Log(title = "迁移批次", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(migrationBatchService.deleteWithValidByIds(List.of(ids), true));
    }
}
