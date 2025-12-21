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
import org.dromara.salesforce.domain.bo.MigrationBatchBo;
import org.dromara.salesforce.domain.vo.MigrationBatchVo;
import org.dromara.salesforce.domain.MigrationBatch;
import org.dromara.salesforce.mapper.MigrationBatchMapper;
import org.dromara.salesforce.service.IMigrationBatchService;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * 迁移批次Service业务层处理
 *
 * @author Kris
 * @date 2025-08-26
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MigrationBatchServiceImpl implements IMigrationBatchService {

    private final MigrationBatchMapper baseMapper;

    /**
     * 查询迁移批次
     *
     * @param id 主键
     * @return 迁移批次
     */
    @Override
    public MigrationBatchVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询迁移批次列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 迁移批次分页列表
     */
    @Override
    public TableDataInfo<MigrationBatchVo> queryPageList(MigrationBatchBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<MigrationBatch> lqw = buildQueryWrapper(bo);
        Page<MigrationBatchVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的迁移批次列表
     *
     * @param bo 查询条件
     * @return 迁移批次列表
     */
    @Override
    public List<MigrationBatchVo> queryList(MigrationBatchBo bo) {
        LambdaQueryWrapper<MigrationBatch> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<MigrationBatch> buildQueryWrapper(MigrationBatchBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<MigrationBatch> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(MigrationBatch::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getApi()), MigrationBatch::getApi, bo.getApi());
        lqw.eq(StringUtils.isNotBlank(bo.getLabel()), MigrationBatch::getLabel, bo.getLabel());
        lqw.eq(bo.getSyncStartDate() != null, MigrationBatch::getSyncStartDate, bo.getSyncStartDate());
        lqw.eq(bo.getSyncEndDate() != null, MigrationBatch::getSyncEndDate, bo.getSyncEndDate());
        lqw.eq(bo.getDbNum() != null, MigrationBatch::getDbNum, bo.getDbNum());
        lqw.eq(bo.getSfNum() != null, MigrationBatch::getSfNum, bo.getSfNum());
        lqw.eq(bo.getPullNum() != null, MigrationBatch::getPullNum, bo.getPullNum());
        lqw.eq(bo.getFirstPullNum() != null, MigrationBatch::getFirstPullNum, bo.getFirstPullNum());
        lqw.eq(bo.getInsertNum() != null, MigrationBatch::getInsertNum, bo.getInsertNum());
        lqw.eq(bo.getUpdateNum() != null, MigrationBatch::getUpdateNum, bo.getUpdateNum());
        lqw.eq(bo.getSyncType() != null, MigrationBatch::getSyncType, bo.getSyncType());
        lqw.eq(bo.getSyncStatus() != null, MigrationBatch::getSyncStatus, bo.getSyncStatus());
        lqw.eq(bo.getFirstSyncTime() != null, MigrationBatch::getFirstSyncTime, bo.getFirstSyncTime());
        lqw.eq(bo.getLastSyncTime() != null, MigrationBatch::getLastSyncTime, bo.getLastSyncTime());
        return lqw;
    }

    /**
     * 新增迁移批次
     *
     * @param bo 迁移批次
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(MigrationBatchBo bo) {
        MigrationBatch add = MapstructUtils.convert(bo, MigrationBatch.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 批量新增迁移批次
     *
     * @param boList 迁移批次列表
     * @return 是否新增成功
     */
    @Override
    public Boolean insertBatch(Collection<MigrationBatchBo> boList) {
        if (boList == null || boList.isEmpty()) {
            return false;
        }
        
        List<MigrationBatch> entityList = MapstructUtils.convert(new ArrayList<>(boList), MigrationBatch.class);
        boolean flag = baseMapper.insertBatch(entityList);
        
        // 更新BO列表中的ID
        if (flag) {
            Iterator<MigrationBatchBo> boIterator = boList.iterator();
            Iterator<MigrationBatch> entityIterator = entityList.iterator();
            while (boIterator.hasNext() && entityIterator.hasNext()) {
                MigrationBatchBo bo = boIterator.next();
                MigrationBatch entity = entityIterator.next();
                bo.setId(entity.getId());
            }
        }
        
        return flag;
    }

    /**
     * 修改迁移批次
     *
     * @param bo 迁移批次
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(MigrationBatchBo bo) {
        MigrationBatch update = MapstructUtils.convert(bo, MigrationBatch.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(MigrationBatch entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除迁移批次信息
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