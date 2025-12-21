package org.dromara.salesforce.service;

import org.dromara.salesforce.domain.vo.MigrationBatchHistoryVo;
import org.dromara.salesforce.domain.bo.MigrationBatchHistoryBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 迁移批次历史Service接口
 *
 * @author Kris
 * @date 2025-08-26
 */
public interface IMigrationBatchHistoryService {

    /**
     * 查询迁移批次历史
     *
     * @param id 主键
     * @return 迁移批次历史
     */
    MigrationBatchHistoryVo queryById(Long id);

    /**
     * 分页查询迁移批次历史列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 迁移批次历史分页列表
     */
    TableDataInfo<MigrationBatchHistoryVo> queryPageList(MigrationBatchHistoryBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的迁移批次历史列表
     *
     * @param bo 查询条件
     * @return 迁移批次历史列表
     */
    List<MigrationBatchHistoryVo> queryList(MigrationBatchHistoryBo bo);

    /**
     * 新增迁移批次历史
     *
     * @param bo 迁移批次历史
     * @return 是否新增成功
     */
    Boolean insertByBo(MigrationBatchHistoryBo bo);

    /**
     * 批量新增迁移批次历史
     *
     * @param boList 迁移批次历史列表
     * @return 是否新增成功
     */
    Boolean insertBatch(Collection<MigrationBatchHistoryBo> boList);

    /**
     * 修改迁移批次历史
     *
     * @param bo 迁移批次历史
     * @return 是否修改成功
     */
    Boolean updateByBo(MigrationBatchHistoryBo bo);

    /**
     * 校验并批量删除迁移批次历史信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}