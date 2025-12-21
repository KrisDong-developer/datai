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
import org.dromara.salesforce.domain.bo.MigrationObjectBo;
import org.dromara.salesforce.domain.vo.MigrationObjectVo;
import org.dromara.salesforce.domain.MigrationObject;
import org.dromara.salesforce.mapper.MigrationObjectMapper;
import org.dromara.salesforce.service.IMigrationObjectService;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * 迁移对象信息Service业务层处理
 *
 * @author Kris
 * @date 2025-08-26
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MigrationObjectServiceImpl implements IMigrationObjectService {

    private final MigrationObjectMapper baseMapper;

    /**
     * 查询迁移对象信息
     *
     * @param id 主键
     * @return 迁移对象信息
     */
    @Override
    public MigrationObjectVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 根据API名称查询迁移对象信息
     *
     * @param apiName API名称
     * @return 迁移对象信息
     */
    @Override
    public MigrationObjectVo queryByApiName(String apiName) {
        LambdaQueryWrapper<MigrationObject> lqw = Wrappers.lambdaQuery();
        lqw.eq(MigrationObject::getApi, apiName);
        return baseMapper.selectVoOne(lqw);
    }
    
    /**
     * 查询所有迁移对象信息列表
     *
     * @return 迁移对象信息列表
     */
    @Override
    public List<MigrationObjectVo> listAllObjects() {
        LambdaQueryWrapper<MigrationObject> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(MigrationObject::getId);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 分页查询迁移对象信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 迁移对象信息分页列表
     */
    @Override
    public TableDataInfo<MigrationObjectVo> queryPageList(MigrationObjectBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<MigrationObject> lqw = buildQueryWrapper(bo);
        Page<MigrationObjectVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的迁移对象信息列表
     *
     * @param bo 查询条件
     * @return 迁移对象信息列表
     */
    @Override
    public List<MigrationObjectVo> queryList(MigrationObjectBo bo) {
        LambdaQueryWrapper<MigrationObject> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<MigrationObject> buildQueryWrapper(MigrationObjectBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<MigrationObject> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(MigrationObject::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getApi()), MigrationObject::getApi, bo.getApi());
        lqw.eq(StringUtils.isNotBlank(bo.getLabel()), MigrationObject::getLabel, bo.getLabel());
        lqw.eq(StringUtils.isNotBlank(bo.getKeyPrefix()), MigrationObject::getKeyPrefix, bo.getKeyPrefix());
        lqw.eq(StringUtils.isNotBlank(bo.getNamespace()), MigrationObject::getNamespace, bo.getNamespace());
        lqw.eq(bo.getObjectIndex() != null, MigrationObject::getObjectIndex, bo.getObjectIndex());
        lqw.eq(bo.getIsWork() != null, MigrationObject::getIsWork, bo.getIsWork());
        lqw.eq(bo.getIsUpdate() != null, MigrationObject::getIsUpdate, bo.getIsUpdate());
        lqw.eq(bo.getIsCustomsetting() != null, MigrationObject::getIsCustomsetting, bo.getIsCustomsetting());
        lqw.eq(StringUtils.isNotBlank(bo.getBlobField()), MigrationObject::getBlobField, bo.getBlobField());
        lqw.eq(StringUtils.isNotBlank(bo.getBatchField()), MigrationObject::getBatchField, bo.getBatchField());
        lqw.eq(bo.getIsEditable() != null, MigrationObject::getIsEditable, bo.getIsEditable());
        lqw.eq(bo.getIsCustom() != null, MigrationObject::getIsCustom, bo.getIsCustom());
        lqw.eq(bo.getLastSyncDate() != null, MigrationObject::getLastSyncDate, bo.getLastSyncDate());
        return lqw;
    }

    /**
     * 新增迁移对象信息
     *
     * @param bo 迁移对象信息
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(MigrationObjectBo bo) {
        MigrationObject add = MapstructUtils.convert(bo, MigrationObject.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 批量新增迁移对象信息
     *
     * @param boList 迁移对象信息列表
     * @return 是否新增成功
     */
    @Override
    public Boolean insertBatch(Collection<MigrationObjectBo> boList) {
        if (boList == null || boList.isEmpty()) {
            return false;
        }
        
        List<MigrationObject> entityList = MapstructUtils.convert(new ArrayList<>(boList), MigrationObject.class);
        boolean flag = baseMapper.insertBatch(entityList);
        
        // 更新BO列表中的ID
        if (flag) {
            Iterator<MigrationObjectBo> boIterator = boList.iterator();
            Iterator<MigrationObject> entityIterator = entityList.iterator();
            while (boIterator.hasNext() && entityIterator.hasNext()) {
                MigrationObjectBo bo = boIterator.next();
                MigrationObject entity = entityIterator.next();
                bo.setId(entity.getId());
            }
        }
        
        return flag;
    }

    /**
     * 修改迁移对象信息
     *
     * @param bo 迁移对象信息
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(MigrationObjectBo bo) {
        MigrationObject update = MapstructUtils.convert(bo, MigrationObject.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 根据API名称更新迁移对象信息
     *
     * @param bo 迁移对象信息
     * @return 是否更新成功
     */
    @Override
    public Boolean updateByApiName(MigrationObjectBo bo) {
        MigrationObject update = MapstructUtils.convert(bo, MigrationObject.class);
        validEntityBeforeSave(update);
        LambdaQueryWrapper<MigrationObject> lqw = Wrappers.lambdaQuery();
        lqw.eq(MigrationObject::getApi, update.getApi());
        return baseMapper.update(update, lqw) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(MigrationObject entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除迁移对象信息信息
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