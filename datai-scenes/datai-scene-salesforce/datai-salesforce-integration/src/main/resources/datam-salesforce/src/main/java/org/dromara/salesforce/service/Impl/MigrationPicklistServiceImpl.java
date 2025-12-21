package org.dromara.salesforce.service.Impl;

import org.dromara.common.core.utils.MapstructUtils;
import org.dromara.common.core.utils.StringUtils;
import org.dromara.common.mybatis.core.page.TableDataInfo;
import org.dromara.common.mybatis.core.page.PageQuery;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.dromara.salesforce.domain.bo.MigrationPicklistBo;
import org.dromara.salesforce.domain.vo.MigrationPicklistVo;
import org.dromara.salesforce.domain.MigrationPicklist;
import org.dromara.salesforce.mapper.MigrationPicklistMapper;
import org.dromara.salesforce.service.IMigrationPicklistService;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * 字段选项列信息Service业务层处理
 *
 * @author Kris
 * @date 2025-08-26
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MigrationPicklistServiceImpl implements IMigrationPicklistService {

    private final MigrationPicklistMapper baseMapper;

    /**
     * 查询字段选项列信息
     *
     * @param id 主键
     * @return 字段选项列信息
     */
    @Override
    public MigrationPicklistVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询字段选项列信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 字段选项列信息分页列表
     */
    @Override
    public TableDataInfo<MigrationPicklistVo> queryPageList(MigrationPicklistBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<MigrationPicklist> lqw = buildQueryWrapper(bo);
        Page<MigrationPicklistVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的字段选项列信息列表
     *
     * @param bo 查询条件
     * @return 字段选项列信息列表
     */
    @Override
    public List<MigrationPicklistVo> queryList(MigrationPicklistBo bo) {
        LambdaQueryWrapper<MigrationPicklist> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<MigrationPicklist> buildQueryWrapper(MigrationPicklistBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<MigrationPicklist> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(MigrationPicklist::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getApi()), MigrationPicklist::getApi, bo.getApi());
        lqw.eq(StringUtils.isNotBlank(bo.getField()), MigrationPicklist::getField, bo.getField());
        lqw.eq(StringUtils.isNotBlank(bo.getValue()), MigrationPicklist::getValue, bo.getValue());
        lqw.eq(StringUtils.isNotBlank(bo.getLabel()), MigrationPicklist::getLabel, bo.getLabel());
        lqw.eq(bo.getActive() != null, MigrationPicklist::getActive, bo.getActive());
        lqw.eq(bo.getDefaultValue() != null, MigrationPicklist::getDefaultValue, bo.getDefaultValue());
        lqw.eq(StringUtils.isNotBlank(bo.getValidFor()), MigrationPicklist::getValidFor, bo.getValidFor());
        return lqw;
    }

    /**
     * 新增字段选项列信息
     *
     * @param bo 字段选项列信息
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(MigrationPicklistBo bo) {
        MigrationPicklist add = MapstructUtils.convert(bo, MigrationPicklist.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 批量新增字段选项列信息
     *
     * @param boList 字段选项列信息列表
     * @return 是否新增成功
     */
    @Override
    public Boolean insertBatch(Collection<MigrationPicklistBo> boList) {
        if (boList == null || boList.isEmpty()) {
            return false;
        }
        
        List<MigrationPicklist> entityList = MapstructUtils.convert(new ArrayList<>(boList), MigrationPicklist.class);
        boolean flag = baseMapper.insertBatch(entityList);
        
        // 更新BO列表中的ID
        if (flag) {
            Iterator<MigrationPicklistBo> boIterator = boList.iterator();
            Iterator<MigrationPicklist> entityIterator = entityList.iterator();
            while (boIterator.hasNext() && entityIterator.hasNext()) {
                MigrationPicklistBo bo = boIterator.next();
                MigrationPicklist entity = entityIterator.next();
                bo.setId(entity.getId());
            }
        }
        
        return flag;
    }

    /**
     * 修改字段选项列信息
     *
     * @param bo 字段选项列信息
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(MigrationPicklistBo bo) {
        MigrationPicklist update = MapstructUtils.convert(bo, MigrationPicklist.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(MigrationPicklist entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除字段选项列信息信息
     *
     * @param ids     待删除的主键集合
     * @param isValid 是否进行有效性校验
     * @return 是否删除成功
     */
    @Override
    public Boolean deleteWithValidByIds(Collection<Long> ids, Boolean isValid) {
        if(isValid){
            //TODO 做一些业务上的校验,判断是否需要校验
        }
        return baseMapper.deleteByIds(ids) > 0;
    }
}