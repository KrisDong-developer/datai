package org.dromara.salesforce.service;

import org.dromara.salesforce.domain.vo.MigrationAddressVo;
import org.dromara.salesforce.domain.bo.MigrationAddressBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 地址信息Service接口
 *
 * @author Kris
 * @date 2025-08-26
 */
public interface IMigrationAddressService {

    /**
     * 查询地址信息
     *
     * @param id 主键
     * @return 地址信息
     */
    MigrationAddressVo queryById(Long id);

    /**
     * 分页查询地址信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 地址信息分页列表
     */
    TableDataInfo<MigrationAddressVo> queryPageList(MigrationAddressBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的地址信息列表
     *
     * @param bo 查询条件
     * @return 地址信息列表
     */
    List<MigrationAddressVo> queryList(MigrationAddressBo bo);

    /**
     * 新增地址信息
     *
     * @param bo 地址信息
     * @return 是否新增成功
     */
    Boolean insertByBo(MigrationAddressBo bo);

    /**
     * 批量新增地址信息
     *
     * @param boList 地址信息列表
     * @return 是否新增成功
     */
    Boolean insertBatch(Collection<MigrationAddressBo> boList);

    /**
     * 修改地址信息
     *
     * @param bo 地址信息
     * @return 是否修改成功
     */
    Boolean updateByBo(MigrationAddressBo bo);

    /**
     * 校验并批量删除地址信息信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);
}