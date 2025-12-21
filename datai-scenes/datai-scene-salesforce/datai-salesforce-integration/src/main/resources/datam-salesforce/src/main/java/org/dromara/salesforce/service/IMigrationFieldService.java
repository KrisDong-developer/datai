package org.dromara.salesforce.service;

import org.dromara.salesforce.domain.vo.MigrationFieldVo;
import org.dromara.salesforce.domain.bo.MigrationFieldBo;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;

import java.util.Collection;
import java.util.List;

/**
 * 对象字段信息Service接口
 *
 * @author Kris
 * @date 2025-08-26
 */
public interface IMigrationFieldService {

    /**
     * 查询对象字段信息
     *
     * @param id 主键
     * @return 对象字段信息
     */
    MigrationFieldVo queryById(Long id);

    /**
     * 分页查询对象字段信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 对象字段信息分页列表
     */
    TableDataInfo<MigrationFieldVo> queryPageList(MigrationFieldBo bo, PageQuery pageQuery);

    /**
     * 查询符合条件的对象字段信息列表
     *
     * @param bo 查询条件
     * @return 对象字段信息列表
     */
    List<MigrationFieldVo> queryList(MigrationFieldBo bo);

    /**
     * 根据API名称查询所有字段信息
     *
     * @param api API名称
     * @return 字段信息列表
     */
    List<MigrationFieldVo> queryListByApi(String api);

    /**
     * 新增对象字段信息
     *
     * @param bo 对象字段信息
     * @return 是否新增成功
     */
    Boolean insertByBo(MigrationFieldBo bo);

    /**
     * 批量新增对象字段信息
     *
     * @param boList 对象字段信息列表
     * @return 是否新增成功
     */
    Boolean insertBatch(Collection<MigrationFieldBo> boList);

    /**
     * 修改对象字段信息
     *
     * @param bo 对象字段信息
     * @return 是否修改成功
     */
    Boolean updateByBo(MigrationFieldBo bo);

    /**
     * 校验并批量删除对象字段信息信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid);

    /**
     * 根据API名称获取日期字段信息
     *
     * @param api API名称
     * @return 日期字段信息
     */
    String getDateField(String api);

    /**
     * 根据API名称获取更新字段信息
     *
     * @param api API名称
     * @return 日期字段信息
     */
    String getUpdateField(String api);

    /**
     * 根据API名称获取二进制字段信息
     *
     * @param api API名称
     * @return 加粗字段信息
     */
    String getBlodField(String api);

    /**
     * 判断指定API的对象是否存在IsDeleted字段
     *
     * @param api API名称
     * @return 是否存在IsDeleted字段
     */
    boolean isDeletedFieldExists(String api);
}
