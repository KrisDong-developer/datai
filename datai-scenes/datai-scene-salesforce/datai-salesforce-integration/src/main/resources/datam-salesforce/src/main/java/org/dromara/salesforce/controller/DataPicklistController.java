package org.dromara.salesforce.controller;

import java.util.List;
import javax.servlet.http.HttpServletResponse;
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
import com.datai.salesforce.domain.DataPicklist;
import com.datai.salesforce.service.IDataPicklistService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;

/**
 * 字段选项列信息Controller
 *
 * @author datai
 * @date 2025-11-30
 */
@RestController
@RequestMapping("/salesforce/picklist")
public class DataPicklistController extends BaseController
{
    @Autowired
    private IDataPicklistService dataPicklistService;

    /**
     * 查询字段选项列信息列表
     */
    @PreAuthorize("@ss.hasPermi('salesforce:picklist:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataPicklist dataPicklist)
    {
        startPage();
        List<DataPicklist> list = dataPicklistService.selectDataPicklistList(dataPicklist);
        return getDataTable(list);
    }

    /**
     * 导出字段选项列信息列表
     */
    @PreAuthorize("@ss.hasPermi('salesforce:picklist:export')")
    @Log(title = "字段选项列信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataPicklist dataPicklist)
    {
        List<DataPicklist> list = dataPicklistService.selectDataPicklistList(dataPicklist);
        ExcelUtil<DataPicklist> util = new ExcelUtil<DataPicklist>(DataPicklist.class);
        util.exportExcel(response, list, "字段选项列信息数据");
    }

    /**
     * 获取字段选项列信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('salesforce:picklist:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id)
    {
        return success(dataPicklistService.selectDataPicklistById(id));
    }

    /**
     * 新增字段选项列信息
     */
    @PreAuthorize("@ss.hasPermi('salesforce:picklist:add')")
    @Log(title = "字段选项列信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataPicklist dataPicklist)
    {
        return toAjax(dataPicklistService.insertDataPicklist(dataPicklist));
    }

    /**
     * 修改字段选项列信息
     */
    @PreAuthorize("@ss.hasPermi('salesforce:picklist:edit')")
    @Log(title = "字段选项列信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataPicklist dataPicklist)
    {
        return toAjax(dataPicklistService.updateDataPicklist(dataPicklist));
    }

    /**
     * 删除字段选项列信息
     */
    @PreAuthorize("@ss.hasPermi('salesforce:picklist:remove')")
    @Log(title = "字段选项列信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        return toAjax(dataPicklistService.deleteDataPicklistByIds(ids));
    }
}
