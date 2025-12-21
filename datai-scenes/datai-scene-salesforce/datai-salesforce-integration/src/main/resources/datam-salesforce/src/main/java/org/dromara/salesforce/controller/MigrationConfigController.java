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
import org.dromara.salesforce.domain.vo.MigrationConfigVo;
import org.dromara.salesforce.domain.bo.MigrationConfigBo;
import org.dromara.salesforce.service.IMigrationConfigService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 迁移配置
 *
 * @author Kris
 * @date 2025-08-26
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/salesforce/config")
public class MigrationConfigController extends BaseController {

    private final IMigrationConfigService migrationConfigService;

    /**
     * 查询迁移配置列表
     */
    @SaCheckPermission("salesforce:config:list")
    @GetMapping("/list")
    public TableDataInfo<MigrationConfigVo> list(MigrationConfigBo bo, PageQuery pageQuery) {
        return migrationConfigService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出迁移配置列表
     */
    @SaCheckPermission("salesforce:config:export")
    @Log(title = "迁移配置", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MigrationConfigBo bo, HttpServletResponse response) {
        List<MigrationConfigVo> list = migrationConfigService.queryList(bo);
        ExcelUtil.exportExcel(list, "迁移配置", MigrationConfigVo.class, response);
    }

    /**
     * 获取迁移配置详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("salesforce:config:query")
    @GetMapping("/{id}")
    public R<MigrationConfigVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(migrationConfigService.queryById(id));
    }

    /**
     * 新增迁移配置
     */
    @SaCheckPermission("salesforce:config:add")
    @Log(title = "迁移配置", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MigrationConfigBo bo) {
        return toAjax(migrationConfigService.insertByBo(bo));
    }

    /**
     * 修改迁移配置
     */
    @SaCheckPermission("salesforce:config:edit")
    @Log(title = "迁移配置", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MigrationConfigBo bo) {
        return toAjax(migrationConfigService.updateByBo(bo));
    }

    /**
     * 删除迁移配置
     *
     * @param ids 主键串
     */
    @SaCheckPermission("salesforce:config:remove")
    @Log(title = "迁移配置", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(migrationConfigService.deleteWithValidByIds(List.of(ids), true));
    }
}
