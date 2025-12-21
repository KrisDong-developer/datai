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
import org.dromara.salesforce.domain.vo.MigrationPicklistVo;
import org.dromara.salesforce.domain.bo.MigrationPicklistBo;
import org.dromara.salesforce.service.IMigrationPicklistService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 字段选项列信息
 *
 * @author Kris
 * @date 2025-08-26
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/salesforce/picklist")
public class MigrationPicklistController extends BaseController {

    private final IMigrationPicklistService migrationPicklistService;

    /**
     * 查询字段选项列信息列表
     */
    @SaCheckPermission("salesforce:picklist:list")
    @GetMapping("/list")
    public TableDataInfo<MigrationPicklistVo> list(MigrationPicklistBo bo, PageQuery pageQuery) {
        return migrationPicklistService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出字段选项列信息列表
     */
    @SaCheckPermission("salesforce:picklist:export")
    @Log(title = "字段选项列信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MigrationPicklistBo bo, HttpServletResponse response) {
        List<MigrationPicklistVo> list = migrationPicklistService.queryList(bo);
        ExcelUtil.exportExcel(list, "字段选项列信息", MigrationPicklistVo.class, response);
    }

    /**
     * 获取字段选项列信息详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("salesforce:picklist:query")
    @GetMapping("/{id}")
    public R<MigrationPicklistVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(migrationPicklistService.queryById(id));
    }

    /**
     * 新增字段选项列信息
     */
    @SaCheckPermission("salesforce:picklist:add")
    @Log(title = "字段选项列信息", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MigrationPicklistBo bo) {
        return toAjax(migrationPicklistService.insertByBo(bo));
    }

    /**
     * 修改字段选项列信息
     */
    @SaCheckPermission("salesforce:picklist:edit")
    @Log(title = "字段选项列信息", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MigrationPicklistBo bo) {
        return toAjax(migrationPicklistService.updateByBo(bo));
    }

    /**
     * 删除字段选项列信息
     *
     * @param ids 主键串
     */
    @SaCheckPermission("salesforce:picklist:remove")
    @Log(title = "字段选项列信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(migrationPicklistService.deleteWithValidByIds(List.of(ids), true));
    }
}
