package org.dromara.salesforce.service;

import org.dromara.salesforce.domain.vo.MigrationPicklistVo;
import org.dromara.salesforce.domain.bo.MigrationPicklistBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 字段选项列信息Service接口
 *
 * @author Kris
 * @date 2025-08-26
 */
public interface IMigrationPicklistService {

    /**
     * 查询字段选项列信息
     *
     * @param id 主键
     * @return 字段选项列信息
     */
    MigrationPicklistVo queryById(Long id);

    /**
     * 分页查询字段选项列信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 字段选项列信息分页列表
     */
    TableDataInfo<MigrationPicklistVo> queryPageList(MigrationPicklistBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的字段选项列信息列表
     *
     * @param bo 查询条件
     * @return 字段选项列信息列表
     */
    List<MigrationPicklistVo> queryList(MigrationPicklistBo bo);

    /**
     * 新增字段选项列信息
     *
     * @param bo 字段选项列信息
     * @return 是否新增成功
     */
    Boolean insertByBo(MigrationPicklistBo bo);

    /**
     * 批量新增字段选项列信息
     *
     * @param boList 字段选项列信息列表
     * @return 是否新增成功
     */
    Boolean insertBatch(Collection<MigrationPicklistBo> boList);

    /**
     * 修改字段选项列信息
     *
     * @param bo 字段选项列信息
     * @return 是否修改成功
     */
    Boolean updateByBo(MigrationPicklistBo bo);

    /**
     * 校验并批量删除字段选项列信息信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}