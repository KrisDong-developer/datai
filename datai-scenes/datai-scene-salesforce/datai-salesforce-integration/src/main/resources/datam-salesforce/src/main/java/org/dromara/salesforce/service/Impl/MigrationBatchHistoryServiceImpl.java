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
import org.dromara.salesforce.domain.bo.MigrationBatchHistoryBo;
import org.dromara.salesforce.domain.vo.MigrationBatchHistoryVo;
import org.dromara.salesforce.domain.MigrationBatchHistory;
import org.dromara.salesforce.mapper.MigrationBatchHistoryMapper;
import org.dromara.salesforce.service.IMigrationBatchHistoryService;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * 迁移批次历史Service业务层处理
 *
 * @author Kris
 * @date 2025-08-26
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MigrationBatchHistoryServiceImpl implements IMigrationBatchHistoryService {

    private final MigrationBatchHistoryMapper baseMapper;

    /**
     * 查询迁移批次历史
     *
     * @param id 主键
     * @return 迁移批次历史
     */
    @Override
    public MigrationBatchHistoryVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询迁移批次历史列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 迁移批次历史分页列表
     */
    @Override
    public TableDataInfo<MigrationBatchHistoryVo> queryPageList(MigrationBatchHistoryBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<MigrationBatchHistory> lqw = buildQueryWrapper(bo);
        Page<MigrationBatchHistoryVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的迁移批次历史列表
     *
     * @param bo 查询条件
     * @return 迁移批次历史列表
     */
    @Override
    public List<MigrationBatchHistoryVo> queryList(MigrationBatchHistoryBo bo) {
        LambdaQueryWrapper<MigrationBatchHistory> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<MigrationBatchHistory> buildQueryWrapper(MigrationBatchHistoryBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<MigrationBatchHistory> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(MigrationBatchHistory::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getApi()), MigrationBatchHistory::getApi, bo.getApi());
        lqw.eq(StringUtils.isNotBlank(bo.getLabel()), MigrationBatchHistory::getLabel, bo.getLabel());
        lqw.eq(bo.getBatchId() != null, MigrationBatchHistory::getBatchId, bo.getBatchId());
        lqw.eq(bo.getStartTime() != null, MigrationBatchHistory::getStartTime, bo.getStartTime());
        lqw.eq(bo.getEndTime() != null, MigrationBatchHistory::getEndTime, bo.getEndTime());
        lqw.eq(bo.getCost() != null, MigrationBatchHistory::getCost, bo.getCost());
        lqw.eq(bo.getSyncStartTime() != null, MigrationBatchHistory::getSyncStartTime, bo.getSyncStartTime());
        lqw.eq(bo.getSyncEndTime() != null, MigrationBatchHistory::getSyncEndTime, bo.getSyncEndTime());
        lqw.eq(bo.getPullNum() != null, MigrationBatchHistory::getPullNum, bo.getPullNum());
        lqw.eq(bo.getFirstPullNum() != null, MigrationBatchHistory::getFirstPullNum, bo.getFirstPullNum());
        lqw.eq(bo.getInsertNum() != null, MigrationBatchHistory::getInsertNum, bo.getInsertNum());
        lqw.eq(bo.getUpdateNum() != null, MigrationBatchHistory::getUpdateNum, bo.getUpdateNum());
        lqw.eq(bo.getSyncType() != null, MigrationBatchHistory::getSyncType, bo.getSyncType());
        lqw.eq(StringUtils.isNotBlank(bo.getOperationType()), MigrationBatchHistory::getOperationType, bo.getOperationType());
        lqw.eq(bo.getOperationStatus() != null, MigrationBatchHistory::getOperationStatus, bo.getOperationStatus());
        return lqw;
    }

    /**
     * 新增迁移批次历史
     *
     * @param bo 迁移批次历史
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(MigrationBatchHistoryBo bo) {
        MigrationBatchHistory add = MapstructUtils.convert(bo, MigrationBatchHistory.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 批量新增迁移批次历史
     *
     * @param boList 迁移批次历史列表
     * @return 是否新增成功
     */
    @Override
    public Boolean insertBatch(Collection<MigrationBatchHistoryBo> boList) {
        if (boList == null || boList.isEmpty()) {
            return false;
        }
        
        List<MigrationBatchHistory> entityList = MapstructUtils.convert(new ArrayList<>(boList), MigrationBatchHistory.class);
        boolean flag = baseMapper.insertBatch(entityList);
        
        // 更新BO列表中的ID
        if (flag) {
            Iterator<MigrationBatchHistoryBo> boIterator = boList.iterator();
            Iterator<MigrationBatchHistory> entityIterator = entityList.iterator();
            while (boIterator.hasNext() && entityIterator.hasNext()) {
                MigrationBatchHistoryBo bo = boIterator.next();
                MigrationBatchHistory entity = entityIterator.next();
                bo.setId(entity.getId());
            }
        }
        
        return flag;
    }

    /**
     * 修改迁移批次历史
     *
     * @param bo 迁移批次历史
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(MigrationBatchHistoryBo bo) {
        MigrationBatchHistory update = MapstructUtils.convert(bo, MigrationBatchHistory.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(MigrationBatchHistory entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除迁移批次历史信息
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