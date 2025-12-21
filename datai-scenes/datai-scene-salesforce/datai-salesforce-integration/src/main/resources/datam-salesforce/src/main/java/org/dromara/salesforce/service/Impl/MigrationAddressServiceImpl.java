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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.dromara.salesforce.domain.bo.MigrationAddressBo;
import org.dromara.salesforce.domain.vo.MigrationAddressVo;
import org.dromara.salesforce.domain.MigrationAddress;
import org.dromara.salesforce.mapper.MigrationAddressMapper;
import org.dromara.salesforce.service.IMigrationAddressService;

import java.util.List;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;
import java.util.ArrayList;

/**
 * 地址信息Service业务层处理
 *
 * @author Kris
 * @date 2025-08-26
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MigrationAddressServiceImpl implements IMigrationAddressService {

    private final MigrationAddressMapper baseMapper;

    /**
     * 查询地址信息
     *
     * @param id 主键
     * @return 地址信息
     */
    @Override
    public MigrationAddressVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询地址信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 地址信息分页列表
     */
    @Override
    public TableDataInfo<MigrationAddressVo> queryPageList(MigrationAddressBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<MigrationAddress> lqw = buildQueryWrapper(bo);
        Page<MigrationAddressVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的地址信息列表
     *
     * @param bo 查询条件
     * @return 地址信息列表
     */
    @Override
    public List<MigrationAddressVo> queryList(MigrationAddressBo bo) {
        LambdaQueryWrapper<MigrationAddress> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<MigrationAddress> buildQueryWrapper(MigrationAddressBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<MigrationAddress> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(MigrationAddress::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getApi()), MigrationAddress::getApi, bo.getApi());
        lqw.eq(StringUtils.isNotBlank(bo.getField()), MigrationAddress::getField, bo.getField());
        lqw.eq(StringUtils.isNotBlank(bo.getStreet()), MigrationAddress::getStreet, bo.getStreet());
        lqw.eq(StringUtils.isNotBlank(bo.getCity()), MigrationAddress::getCity, bo.getCity());
        lqw.eq(StringUtils.isNotBlank(bo.getState()), MigrationAddress::getState, bo.getState());
        lqw.eq(StringUtils.isNotBlank(bo.getStateCode()), MigrationAddress::getStateCode, bo.getStateCode());
        lqw.eq(StringUtils.isNotBlank(bo.getPostalCode()), MigrationAddress::getPostalCode, bo.getPostalCode());
        lqw.eq(StringUtils.isNotBlank(bo.getCountry()), MigrationAddress::getCountry, bo.getCountry());
        lqw.eq(StringUtils.isNotBlank(bo.getCountryCode()), MigrationAddress::getCountryCode, bo.getCountryCode());
        lqw.eq(StringUtils.isNotBlank(bo.getGeocodeAccuracy()), MigrationAddress::getGeocodeAccuracy, bo.getGeocodeAccuracy());
        lqw.eq(bo.getLatitude() != null, MigrationAddress::getLatitude, bo.getLatitude());
        lqw.eq(bo.getLongitude() != null, MigrationAddress::getLongitude, bo.getLongitude());
        return lqw;
    }

    /**
     * 新增地址信息
     *
     * @param bo 地址信息
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(MigrationAddressBo bo) {
        MigrationAddress add = MapstructUtils.convert(bo, MigrationAddress.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 批量新增地址信息
     *
     * @param boList 地址信息列表
     * @return 是否新增成功
     */
    @Override
    public Boolean insertBatch(Collection<MigrationAddressBo> boList) {
        if (boList == null || boList.isEmpty()) {
            return false;
        }
        
        List<MigrationAddress> entityList = MapstructUtils.convert(new ArrayList<>(boList), MigrationAddress.class);
        boolean flag = baseMapper.insertBatch(entityList);
        
        // 更新BO列表中的ID
        if (flag) {
            Iterator<MigrationAddressBo> boIterator = boList.iterator();
            Iterator<MigrationAddress> entityIterator = entityList.iterator();
            while (boIterator.hasNext() && entityIterator.hasNext()) {
                MigrationAddressBo bo = boIterator.next();
                MigrationAddress entity = entityIterator.next();
                bo.setId(entity.getId());
            }
        }
        
        return flag;
    }

    /**
     * 修改地址信息
     *
     * @param bo 地址信息
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(MigrationAddressBo bo) {
        MigrationAddress update = MapstructUtils.convert(bo, MigrationAddress.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(MigrationAddress entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除地址信息信息
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