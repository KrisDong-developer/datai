package org.dromara.salesforce.service;

import org.dromara.salesforce.domain.vo.MigrationBatchVo;
import org.dromara.salesforce.domain.bo.MigrationBatchBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 迁移批次Service接口
 *
 * @author Kris
 * @date 2025-08-26
 */
public interface IMigrationBatchService {

    /**
     * 查询迁移批次
     *
     * @param id 主键
     * @return 迁移批次
     */
    MigrationBatchVo queryById(Long id);

    /**
     * 分页查询迁移批次列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 迁移批次分页列表
     */
    TableDataInfo<MigrationBatchVo> queryPageList(MigrationBatchBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的迁移批次列表
     *
     * @param bo 查询条件
     * @return 迁移批次列表
     */
    List<MigrationBatchVo> queryList(MigrationBatchBo bo);

    /**
     * 新增迁移批次
     *
     * @param bo 迁移批次
     * @return 是否新增成功
     */
    Boolean insertByBo(MigrationBatchBo bo);

    /**
     * 批量新增迁移批次
     *
     * @param boList 迁移批次列表
     * @return 是否新增成功
     */
    Boolean insertBatch(Collection<MigrationBatchBo> boList);

    /**
     * 修改迁移批次
     *
     * @param bo 迁移批次
     * @return 是否修改成功
     */
    Boolean updateByBo(MigrationBatchBo bo);

    /**
     * 校验并批量删除迁移批次信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}