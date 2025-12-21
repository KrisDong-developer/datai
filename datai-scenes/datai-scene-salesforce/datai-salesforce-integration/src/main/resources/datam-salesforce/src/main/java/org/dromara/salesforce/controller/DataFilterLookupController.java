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
import com.datai.salesforce.domain.DataFilterLookup;
import com.datai.salesforce.service.IDataFilterLookupService;
import com.datai.common.utils.poi.ExcelUtil;
import com.datai.common.core.page.TableDataInfo;

/**
 * 字段过滤查找信息Controller
 *
 * @author datai
 * @date 2025-11-30
 */
@RestController
@RequestMapping("/salesforce/lookup")
public class DataFilterLookupController extends BaseController
{
    @Autowired
    private IDataFilterLookupService dataFilterLookupService;

    /**
     * 查询字段过滤查找信息列表
     */
    @PreAuthorize("@ss.hasPermi('salesforce:lookup:list')")
    @GetMapping("/list")
    public TableDataInfo list(DataFilterLookup dataFilterLookup)
    {
        startPage();
        List<DataFilterLookup> list = dataFilterLookupService.selectDataFilterLookupList(dataFilterLookup);
        return getDataTable(list);
    }

    /**
     * 导出字段过滤查找信息列表
     */
    @PreAuthorize("@ss.hasPermi('salesforce:lookup:export')")
    @Log(title = "字段过滤查找信息", businessType = BusinessType.EXPORT)
    @PostMapping("/export")
    public void export(HttpServletResponse response, DataFilterLookup dataFilterLookup)
    {
        List<DataFilterLookup> list = dataFilterLookupService.selectDataFilterLookupList(dataFilterLookup);
        ExcelUtil<DataFilterLookup> util = new ExcelUtil<DataFilterLookup>(DataFilterLookup.class);
        util.exportExcel(response, list, "字段过滤查找信息数据");
    }

    /**
     * 获取字段过滤查找信息详细信息
     */
    @PreAuthorize("@ss.hasPermi('salesforce:lookup:query')")
    @GetMapping(value = "/{id}")
    public AjaxResult getInfo(@PathVariable("id") Integer id)
    {
        return success(dataFilterLookupService.selectDataFilterLookupById(id));
    }

    /**
     * 新增字段过滤查找信息
     */
    @PreAuthorize("@ss.hasPermi('salesforce:lookup:add')")
    @Log(title = "字段过滤查找信息", businessType = BusinessType.INSERT)
    @PostMapping
    public AjaxResult add(@RequestBody DataFilterLookup dataFilterLookup)
    {
        return toAjax(dataFilterLookupService.insertDataFilterLookup(dataFilterLookup));
    }

    /**
     * 修改字段过滤查找信息
     */
    @PreAuthorize("@ss.hasPermi('salesforce:lookup:edit')")
    @Log(title = "字段过滤查找信息", businessType = BusinessType.UPDATE)
    @PutMapping
    public AjaxResult edit(@RequestBody DataFilterLookup dataFilterLookup)
    {
        return toAjax(dataFilterLookupService.updateDataFilterLookup(dataFilterLookup));
    }

    /**
     * 删除字段过滤查找信息
     */
    @PreAuthorize("@ss.hasPermi('salesforce:lookup:remove')")
    @Log(title = "字段过滤查找信息", businessType = BusinessType.DELETE)
	@DeleteMapping("/{ids}")
    public AjaxResult remove(@PathVariable Integer[] ids)
    {
        return toAjax(dataFilterLookupService.deleteDataFilterLookupByIds(ids));
    }
}
