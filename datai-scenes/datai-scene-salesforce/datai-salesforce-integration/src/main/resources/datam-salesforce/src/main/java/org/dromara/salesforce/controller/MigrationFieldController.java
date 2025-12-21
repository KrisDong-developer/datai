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
import org.dromara.salesforce.domain.vo.MigrationFieldVo;
import org.dromara.salesforce.domain.bo.MigrationFieldBo;
import org.dromara.salesforce.service.IMigrationFieldService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 对象字段信息
 *
 * @author Kris
 * @date 2025-08-26
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/salesforce/field")
public class MigrationFieldController extends BaseController {

    private final IMigrationFieldService migrationFieldService;

    /**
     * 查询对象字段信息列表
     */
    @SaCheckPermission("salesforce:field:list")
    @GetMapping("/list")
    public TableDataInfo<MigrationFieldVo> list(MigrationFieldBo bo, PageQuery pageQuery) {
        return migrationFieldService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出对象字段信息列表
     */
    @SaCheckPermission("salesforce:field:export")
    @Log(title = "对象字段信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MigrationFieldBo bo, HttpServletResponse response) {
        List<MigrationFieldVo> list = migrationFieldService.queryList(bo);
        ExcelUtil.exportExcel(list, "对象字段信息", MigrationFieldVo.class, response);
    }

    /**
     * 获取对象字段信息详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("salesforce:field:query")
    @GetMapping("/{id}")
    public R<MigrationFieldVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(migrationFieldService.queryById(id));
    }

    /**
     * 新增对象字段信息
     */
    @SaCheckPermission("salesforce:field:add")
    @Log(title = "对象字段信息", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MigrationFieldBo bo) {
        return toAjax(migrationFieldService.insertByBo(bo));
    }

    /**
     * 修改对象字段信息
     */
    @SaCheckPermission("salesforce:field:edit")
    @Log(title = "对象字段信息", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MigrationFieldBo bo) {
        return toAjax(migrationFieldService.updateByBo(bo));
    }

    /**
     * 删除对象字段信息
     *
     * @param ids 主键串
     */
    @SaCheckPermission("salesforce:field:remove")
    @Log(title = "对象字段信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(migrationFieldService.deleteWithValidByIds(List.of(ids), true));
    }
}
