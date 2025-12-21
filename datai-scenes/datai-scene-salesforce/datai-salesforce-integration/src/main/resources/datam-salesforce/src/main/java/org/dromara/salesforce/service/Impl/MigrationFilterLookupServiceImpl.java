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
import org.dromara.salesforce.domain.bo.MigrationFilterLookupBo;
import org.dromara.salesforce.domain.vo.MigrationFilterLookupVo;
import org.dromara.salesforce.domain.MigrationFilterLookup;
import org.dromara.salesforce.mapper.MigrationFilterLookupMapper;
import org.dromara.salesforce.service.IMigrationFilterLookupService;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * 字段过滤查找信息Service业务层处理
 *
 * @author Kris
 * @date 2025-08-26
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MigrationFilterLookupServiceImpl implements IMigrationFilterLookupService {

    private final MigrationFilterLookupMapper baseMapper;

    /**
     * 查询字段过滤查找信息
     *
     * @param id 主键
     * @return 字段过滤查找信息
     */
    @Override
    public MigrationFilterLookupVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询字段过滤查找信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 字段过滤查找信息分页列表
     */
    @Override
    public TableDataInfo<MigrationFilterLookupVo> queryPageList(MigrationFilterLookupBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<MigrationFilterLookup> lqw = buildQueryWrapper(bo);
        Page<MigrationFilterLookupVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的字段过滤查找信息列表
     *
     * @param bo 查询条件
     * @return 字段过滤查找信息列表
     */
    @Override
    public List<MigrationFilterLookupVo> queryList(MigrationFilterLookupBo bo) {
        LambdaQueryWrapper<MigrationFilterLookup> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<MigrationFilterLookup> buildQueryWrapper(MigrationFilterLookupBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<MigrationFilterLookup> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(MigrationFilterLookup::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getApi()), MigrationFilterLookup::getApi, bo.getApi());
        lqw.eq(StringUtils.isNotBlank(bo.getField()), MigrationFilterLookup::getField, bo.getField());
        lqw.eq(StringUtils.isNotBlank(bo.getControllingField()), MigrationFilterLookup::getControllingField, bo.getControllingField());
        lqw.eq(bo.getDependent() != null, MigrationFilterLookup::getDependent, bo.getDependent());
        lqw.eq(StringUtils.isNotBlank(bo.getLookupFilter()), MigrationFilterLookup::getLookupFilter, bo.getLookupFilter());
        return lqw;
    }

    /**
     * 新增字段过滤查找信息
     *
     * @param bo 字段过滤查找信息
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(MigrationFilterLookupBo bo) {
        MigrationFilterLookup add = MapstructUtils.convert(bo, MigrationFilterLookup.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 批量新增字段过滤查找信息
     *
     * @param boList 字段过滤查找信息列表
     * @return 是否新增成功
     */
    @Override
    public Boolean insertBatch(Collection<MigrationFilterLookupBo> boList) {
        if (boList == null || boList.isEmpty()) {
            return false;
        }
        
        List<MigrationFilterLookup> entityList = MapstructUtils.convert(new ArrayList<>(boList), MigrationFilterLookup.class);
        boolean flag = baseMapper.insertBatch(entityList);
        
        // 更新BO列表中的ID
        if (flag) {
            Iterator<MigrationFilterLookupBo> boIterator = boList.iterator();
            Iterator<MigrationFilterLookup> entityIterator = entityList.iterator();
            while (boIterator.hasNext() && entityIterator.hasNext()) {
                MigrationFilterLookupBo bo = boIterator.next();
                MigrationFilterLookup entity = entityIterator.next();
                bo.setId(entity.getId());
            }
        }
        
        return flag;
    }

    /**
     * 修改字段过滤查找信息
     *
     * @param bo 字段过滤查找信息
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(MigrationFilterLookupBo bo) {
        MigrationFilterLookup update = MapstructUtils.convert(bo, MigrationFilterLookup.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(MigrationFilterLookup entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除字段过滤查找信息信息
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