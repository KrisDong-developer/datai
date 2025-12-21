package org.dromara.salesforce.service;

import org.dromara.salesforce.domain.vo.MigrationConfigVo;
import org.dromara.salesforce.domain.bo.MigrationConfigBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 迁移配置Service接口
 *
 * @author Kris
 * @date 2025-08-26
 */
public interface IMigrationConfigService {

    /**
     * 查询迁移配置
     *
     * @param id 主键
     * @return 迁移配置
     */
    MigrationConfigVo queryById(Long id);

    /**
     * 分页查询迁移配置列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 迁移配置分页列表
     */
    TableDataInfo<MigrationConfigVo> queryPageList(MigrationConfigBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的迁移配置列表
     *
     * @param bo 查询条件
     * @return 迁移配置列表
     */
    List<MigrationConfigVo> queryList(MigrationConfigBo bo);

    /**
     * 新增迁移配置
     *
     * @param bo 迁移配置
     * @return 是否新增成功
     */
    Boolean insertByBo(MigrationConfigBo bo);

    /**
     * 批量新增迁移配置
     *
     * @param boList 迁移配置列表
     * @return 是否新增成功
     */
    Boolean insertBatch(Collection<MigrationConfigBo> boList);

    /**
     * 修改迁移配置
     *
     * @param bo 迁移配置
     * @return 是否修改成功
     */
    Boolean updateByBo(MigrationConfigBo bo);

    /**
     * 校验并批量删除迁移配置信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 根据配置键名更新配置值
     *
     * @param configKey   配置键名
     * @param configValue 配置值
     * @return 是否更新成功
     */
    Boolean updateConfigValueByKey(String configKey, String configValue);
}