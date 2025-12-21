package org.dromara.salesforce.service;

import org.dromara.salesforce.domain.vo.MigrationObjectVo;
import org.dromara.salesforce.domain.bo.MigrationObjectBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 迁移对象信息Service接口
 *
 * @author Kris
 * @date 2025-08-26
 */
public interface IMigrationObjectService {

    /**
     * 查询迁移对象信息
     *
     * @param id 主键
     * @return 迁移对象信息
     */
    MigrationObjectVo queryById(Long id);

    /**
     * 根据API名称查询迁移对象信息
     *
     * @param apiName API名称
     * @return 迁移对象信息
     */
    MigrationObjectVo queryByApiName(String apiName);
    
    /**
     * 查询所有迁移对象信息列表
     *
     * @return 迁移对象信息列表
     */
    List<MigrationObjectVo> listAllObjects();

    /**
     * 分页查询迁移对象信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 迁移对象信息分页列表
     */
    TableDataInfo<MigrationObjectVo> queryPageList(MigrationObjectBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的迁移对象信息列表
     *
     * @param bo 查询条件
     * @return 迁移对象信息列表
     */
    List<MigrationObjectVo> queryList(MigrationObjectBo bo);

    /**
     * 新增迁移对象信息
     *
     * @param bo 迁移对象信息
     * @return 是否新增成功
     */
    Boolean insertByBo(MigrationObjectBo bo);

    /**
     * 批量新增迁移对象信息
     *
     * @param boList 迁移对象信息列表
     * @return 是否新增成功
     */
    Boolean insertBatch(Collection<MigrationObjectBo> boList);

    /**
     * 修改迁移对象信息
     *
     * @param bo 迁移对象信息
     * @return 是否修改成功
     */
    Boolean updateByBo(MigrationObjectBo bo);

    /**
     * 根据API名称更新迁移对象信息
     *
     * @param bo 迁移对象信息
     * @return 是否更新成功
     */
    Boolean updateByApiName(MigrationObjectBo bo);

    /**
     * 校验并批量删除迁移对象信息信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}