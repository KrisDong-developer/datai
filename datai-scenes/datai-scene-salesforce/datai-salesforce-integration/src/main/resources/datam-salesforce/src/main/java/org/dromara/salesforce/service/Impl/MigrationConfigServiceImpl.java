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
import org.dromara.salesforce.domain.bo.MigrationConfigBo;
import org.dromara.salesforce.domain.vo.MigrationConfigVo;
import org.dromara.salesforce.domain.MigrationConfig;
import org.dromara.salesforce.mapper.MigrationConfigMapper;
import org.dromara.salesforce.service.IMigrationConfigService;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * 迁移配置Service业务层处理
 *
 * @author Kris
 * @date 2025-08-26
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MigrationConfigServiceImpl implements IMigrationConfigService {

    private final MigrationConfigMapper baseMapper;

    /**
     * 查询迁移配置
     *
     * @param id 主键
     * @return 迁移配置
     */
    @Override
    public MigrationConfigVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询迁移配置列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 迁移配置分页列表
     */
    @Override
    public TableDataInfo<MigrationConfigVo> queryPageList(MigrationConfigBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<MigrationConfig> lqw = buildQueryWrapper(bo);
        Page<MigrationConfigVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的迁移配置列表
     *
     * @param bo 查询条件
     * @return 迁移配置列表
     */
    @Override
    public List<MigrationConfigVo> queryList(MigrationConfigBo bo) {
        LambdaQueryWrapper<MigrationConfig> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<MigrationConfig> buildQueryWrapper(MigrationConfigBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<MigrationConfig> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(MigrationConfig::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getConfigKey()), MigrationConfig::getConfigKey, bo.getConfigKey());
        lqw.eq(StringUtils.isNotBlank(bo.getConfigValue()), MigrationConfig::getConfigValue, bo.getConfigValue());
        lqw.eq(StringUtils.isNotBlank(bo.getDescription()), MigrationConfig::getDescription, bo.getDescription());
        return lqw;
    }

    /**
     * 新增迁移配置
     *
     * @param bo 迁移配置
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(MigrationConfigBo bo) {
        MigrationConfig add = MapstructUtils.convert(bo, MigrationConfig.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 批量新增迁移配置
     *
     * @param boList 迁移配置列表
     * @return 是否新增成功
     */
    @Override
    public Boolean insertBatch(Collection<MigrationConfigBo> boList) {
        if (boList == null || boList.isEmpty()) {
            return false;
        }
        
        List<MigrationConfig> entityList = MapstructUtils.convert(new ArrayList<>(boList), MigrationConfig.class);
        boolean flag = baseMapper.insertBatch(entityList);
        
        // 更新BO列表中的ID
        if (flag) {
            Iterator<MigrationConfigBo> boIterator = boList.iterator();
            Iterator<MigrationConfig> entityIterator = entityList.iterator();
            while (boIterator.hasNext() && entityIterator.hasNext()) {
                MigrationConfigBo bo = boIterator.next();
                MigrationConfig entity = entityIterator.next();
                bo.setId(entity.getId());
            }
        }
        
        return flag;
    }

    /**
     * 修改迁移配置
     *
     * @param bo 迁移配置
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(MigrationConfigBo bo) {
        MigrationConfig update = MapstructUtils.convert(bo, MigrationConfig.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(MigrationConfig entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除迁移配置信息
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

    /**
     * 根据配置键名更新配置值
     *
     * @param configKey   配置键名
     * @param configValue 配置值
     * @return 是否更新成功
     */
    @Override
    public Boolean updateConfigValueByKey(String configKey, String configValue) {
        LambdaQueryWrapper<MigrationConfig> lqw = Wrappers.lambdaQuery();
        lqw.eq(MigrationConfig::getConfigKey, configKey);
        MigrationConfig config = new MigrationConfig();
        config.setConfigValue(configValue);
        return baseMapper.update(config, lqw) > 0;
    }
}