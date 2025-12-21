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
import org.dromara.salesforce.domain.vo.MigrationFilterLookupVo;
import org.dromara.salesforce.domain.bo.MigrationFilterLookupBo;
import org.dromara.salesforce.service.IMigrationFilterLookupService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 字段过滤查找信息
 *
 * @author Kris
 * @date 2025-08-26
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/salesforce/filterLookup")
public class MigrationFilterLookupController extends BaseController {

    private final IMigrationFilterLookupService migrationFilterLookupService;

    /**
     * 查询字段过滤查找信息列表
     */
    @SaCheckPermission("salesforce:filterLookup:list")
    @GetMapping("/list")
    public TableDataInfo<MigrationFilterLookupVo> list(MigrationFilterLookupBo bo, PageQuery pageQuery) {
        return migrationFilterLookupService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出字段过滤查找信息列表
     */
    @SaCheckPermission("salesforce:filterLookup:export")
    @Log(title = "字段过滤查找信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MigrationFilterLookupBo bo, HttpServletResponse response) {
        List<MigrationFilterLookupVo> list = migrationFilterLookupService.queryList(bo);
        ExcelUtil.exportExcel(list, "字段过滤查找信息", MigrationFilterLookupVo.class, response);
    }

    /**
     * 获取字段过滤查找信息详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("salesforce:filterLookup:query")
    @GetMapping("/{id}")
    public R<MigrationFilterLookupVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(migrationFilterLookupService.queryById(id));
    }

    /**
     * 新增字段过滤查找信息
     */
    @SaCheckPermission("salesforce:filterLookup:add")
    @Log(title = "字段过滤查找信息", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MigrationFilterLookupBo bo) {
        return toAjax(migrationFilterLookupService.insertByBo(bo));
    }

    /**
     * 修改字段过滤查找信息
     */
    @SaCheckPermission("salesforce:filterLookup:edit")
    @Log(title = "字段过滤查找信息", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MigrationFilterLookupBo bo) {
        return toAjax(migrationFilterLookupService.updateByBo(bo));
    }

    /**
     * 删除字段过滤查找信息
     *
     * @param ids 主键串
     */
    @SaCheckPermission("salesforce:filterLookup:remove")
    @Log(title = "字段过滤查找信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(migrationFilterLookupService.deleteWithValidByIds(List.of(ids), true));
    }
}
