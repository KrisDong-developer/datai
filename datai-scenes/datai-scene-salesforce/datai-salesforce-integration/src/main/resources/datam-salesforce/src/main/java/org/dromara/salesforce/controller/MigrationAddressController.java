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
import org.dromara.salesforce.domain.vo.MigrationAddressVo;
import org.dromara.salesforce.domain.bo.MigrationAddressBo;
import org.dromara.salesforce.service.IMigrationAddressService;
import org.dromara.common.mybatis.core.page.TableDataInfo;

/**
 * 地址信息
 *
 * @author Kris
 * @date 2025-08-26
 */
@Validated
@RequiredArgsConstructor
@RestController
@RequestMapping("/salesforce/address")
public class MigrationAddressController extends BaseController {

    private final IMigrationAddressService migrationAddressService;

    /**
     * 查询地址信息列表
     */
    @SaCheckPermission("salesforce:address:list")
    @GetMapping("/list")
    public TableDataInfo<MigrationAddressVo> list(MigrationAddressBo bo, PageQuery pageQuery) {
        return migrationAddressService.queryPageList(bo, pageQuery);
    }

    /**
     * 导出地址信息列表
     */
    @SaCheckPermission("salesforce:address:export")
    @Log(title = "地址信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(MigrationAddressBo bo, HttpServletResponse response) {
        List<MigrationAddressVo> list = migrationAddressService.queryList(bo);
        ExcelUtil.exportExcel(list, "地址信息", MigrationAddressVo.class, response);
    }

    /**
     * 获取地址信息详细信息
     *
     * @param id 主键
     */
    @SaCheckPermission("salesforce:address:query")
    @GetMapping("/{id}")
    public R<MigrationAddressVo> getInfo(@NotNull(message = "主键不能为空")
                                     @PathVariable Long id) {
        return R.ok(migrationAddressService.queryById(id));
    }

    /**
     * 新增地址信息
     */
    @SaCheckPermission("salesforce:address:add")
    @Log(title = "地址信息", businessType = BusinessType.INSERT)
    @RepeatSubmit()
    @PostMapping()
    public R<Void> add(@Validated(AddGroup.class) @RequestBody MigrationAddressBo bo) {
        return toAjax(migrationAddressService.insertByBo(bo));
    }

    /**
     * 修改地址信息
     */
    @SaCheckPermission("salesforce:address:edit")
    @Log(title = "地址信息", businessType = BusinessType.UPDATE)
    @RepeatSubmit()
    @PutMapping()
    public R<Void> edit(@Validated(EditGroup.class) @RequestBody MigrationAddressBo bo) {
        return toAjax(migrationAddressService.updateByBo(bo));
    }

    /**
     * 删除地址信息
     *
     * @param ids 主键串
     */
    @SaCheckPermission("salesforce:address:remove")
    @Log(title = "地址信息", businessType = BusinessType.DELETE)
    @DeleteMapping("/{ids}")
    public R<Void> remove(@NotEmpty(message = "主键不能为空")
                          @PathVariable Long[] ids) {
        return toAjax(migrationAddressService.deleteWithValidByIds(List.of(ids), true));
    }
}
