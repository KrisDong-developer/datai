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
import org.dromara.salesforce.domain.vo.MigrationObjectVo;
import org.dromara.salesforce.domain.bo.MigrationObjectBo;
import org.dromara.salesforce.service.IMigrationObjectService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 迁移对象信息
 *
 * @author Kris
 * @date 2025-08-26
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/salesforce/object")
public class MigrationObjectController extends BaseController {

    private final IMigrationObjectService migrationObjectService;

    /**
     * 查询迁移对象信息列表
     */
    @SaCheckPermission("salesforce:object:list")
    @GetMapping("/list")
    public TableDataInfo<MigrationObjectVo> list(MigrationObjectBo bo, PageQuery pageQuery) {
        return migrationObjectService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出迁移对象信息列表
     */
    @SaCheckPermission("salesforce:object:export")
    @Log(title = "迁移对象信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MigrationObjectBo bo, HttpServletResponse response) {
        List<MigrationObjectVo> list = migrationObjectService.queryList(bo);
        ExcelUtil.exportExcel(list, "迁移对象信息", MigrationObjectVo.class, response);
    }

    /**
     * 获取迁移对象信息详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("salesforce:object:query")
    @GetMapping("/{id}")
    public R<MigrationObjectVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(migrationObjectService.queryById(id));
    }

    /**
     * 新增迁移对象信息
     */
    @SaCheckPermission("salesforce:object:add")
    @Log(title = "迁移对象信息", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MigrationObjectBo bo) {
        return toAjax(migrationObjectService.insertByBo(bo));
    }

    /**
     * 修改迁移对象信息
     */
    @SaCheckPermission("salesforce:object:edit")
    @Log(title = "迁移对象信息", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MigrationObjectBo bo) {
        return toAjax(migrationObjectService.updateByBo(bo));
    }

    /**
     * 删除迁移对象信息
     *
     * @param ids 主键串
     */
    @SaCheckPermission("salesforce:object:remove")
    @Log(title = "迁移对象信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(migrationObjectService.deleteWithValidByIds(List.of(ids), true));
    }
}
