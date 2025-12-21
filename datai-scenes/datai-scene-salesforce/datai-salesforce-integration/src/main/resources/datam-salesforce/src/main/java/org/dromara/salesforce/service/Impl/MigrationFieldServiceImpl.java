package org.dromara.salesforce.service.Impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
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
import org.dromara.salesforce.domain.bo.MigrationFieldBo;
import org.dromara.salesforce.domain.vo.MigrationFieldVo;
import org.dromara.salesforce.domain.MigrationField;
import org.dromara.salesforce.mapper.MigrationFieldMapper;
import org.dromara.salesforce.service.IMigrationFieldService;

import java.util.*;

/**
 * 对象字段信息Service业务层处理
 *
 * @author Kris
 * @date 2025-08-26
 */
@Slf4j
@RequiredArgsConstructor
@Service
public class MigrationFieldServiceImpl implements IMigrationFieldService {

    private final MigrationFieldMapper baseMapper;

    /**
     * 查询对象字段信息
     *
     * @param id 主键
     * @return 对象字段信息
     */
    @Override
    public MigrationFieldVo queryById(Long id){
        return baseMapper.selectVoById(id);
    }

    /**
     * 分页查询对象字段信息列表
     *
     * @param bo        查询条件
     * @param pageQuery 分页参数
     * @return 对象字段信息分页列表
     */
    @Override
    public TableDataInfo<MigrationFieldVo> queryPageList(MigrationFieldBo bo, PageQuery pageQuery) {
        LambdaQueryWrapper<MigrationField> lqw = buildQueryWrapper(bo);
        Page<MigrationFieldVo> result = baseMapper.selectVoPage(pageQuery.build(), lqw);
        return TableDataInfo.build(result);
    }

    /**
     * 查询符合条件的对象字段信息列表
     *
     * @param bo 查询条件
     * @return 对象字段信息列表
     */
    @Override
    public List<MigrationFieldVo> queryList(MigrationFieldBo bo) {
        LambdaQueryWrapper<MigrationField> lqw = buildQueryWrapper(bo);
        return baseMapper.selectVoList(lqw);
    }

    /**
     * 根据API名称查询所有字段信息
     *
     * @param api API名称
     * @return 字段信息列表
     */
    @Override
    public List<MigrationFieldVo> queryListByApi(String api) {
        LambdaQueryWrapper<MigrationField> lqw = Wrappers.lambdaQuery();
        lqw.eq(MigrationField::getApi, api);
        lqw.orderByAsc(MigrationField::getId);
        return baseMapper.selectVoList(lqw);
    }

    private LambdaQueryWrapper<MigrationField> buildQueryWrapper(MigrationFieldBo bo) {
        Map<String, Object> params = bo.getParams();
        LambdaQueryWrapper<MigrationField> lqw = Wrappers.lambdaQuery();
        lqw.orderByAsc(MigrationField::getId);
        lqw.eq(StringUtils.isNotBlank(bo.getApi()), MigrationField::getApi, bo.getApi());
        lqw.eq(StringUtils.isNotBlank(bo.getField()), MigrationField::getField, bo.getField());
        lqw.eq(StringUtils.isNotBlank(bo.getLabel()), MigrationField::getLabel, bo.getLabel());
        lqw.eq(bo.getIsCreateable() != null, MigrationField::getIsCreateable, bo.getIsCreateable());
        lqw.eq(bo.getIsNillable() != null, MigrationField::getIsNillable, bo.getIsNillable());
        lqw.eq(bo.getIsUpdateable() != null, MigrationField::getIsUpdateable, bo.getIsUpdateable());
        lqw.eq(bo.getIsDefaultedOnCreate() != null, MigrationField::getIsDefaultedOnCreate, bo.getIsDefaultedOnCreate());
        lqw.eq(bo.getIsUnique() != null, MigrationField::getIsUnique, bo.getIsUnique());
        lqw.eq(bo.getIsFilterable() != null, MigrationField::getIsFilterable, bo.getIsFilterable());
        lqw.eq(bo.getIsSortable() != null, MigrationField::getIsSortable, bo.getIsSortable());
        lqw.eq(bo.getIsAggregatable() != null, MigrationField::getIsAggregatable, bo.getIsAggregatable());
        lqw.eq(bo.getIsGroupable() != null, MigrationField::getIsGroupable, bo.getIsGroupable());
        lqw.eq(bo.getIsPolymorphicForeignKey() != null, MigrationField::getIsPolymorphicForeignKey, bo.getIsPolymorphicForeignKey());
        lqw.eq(StringUtils.isNotBlank(bo.getPolymorphicForeignField()), MigrationField::getPolymorphicForeignField, bo.getPolymorphicForeignField());
        lqw.eq(bo.getIsExternalId() != null, MigrationField::getIsExternalId, bo.getIsExternalId());
        lqw.eq(bo.getIsCustom() != null, MigrationField::getIsCustom, bo.getIsCustom());
        lqw.eq(bo.getIsCalculated() != null, MigrationField::getIsCalculated, bo.getIsCalculated());
        lqw.eq(bo.getIsAutoNumber() != null, MigrationField::getIsAutoNumber, bo.getIsAutoNumber());
        lqw.eq(bo.getIsCaseSensitive() != null, MigrationField::getIsCaseSensitive, bo.getIsCaseSensitive());
        lqw.eq(bo.getIsEncrypted() != null, MigrationField::getIsEncrypted, bo.getIsEncrypted());
        lqw.eq(bo.getIsHtmlFormatted() != null, MigrationField::getIsHtmlFormatted, bo.getIsHtmlFormatted());
        lqw.eq(bo.getIsIdLookup() != null, MigrationField::getIsIdLookup, bo.getIsIdLookup());
        lqw.eq(bo.getIsPermissionable() != null, MigrationField::getIsPermissionable, bo.getIsPermissionable());
        lqw.eq(bo.getIsRestrictedPicklist() != null, MigrationField::getIsRestrictedPicklist, bo.getIsRestrictedPicklist());
        lqw.eq(bo.getIsRestrictedDelete() != null, MigrationField::getIsRestrictedDelete, bo.getIsRestrictedDelete());
        lqw.eq(bo.getIsWriteRequiresMasterRead() != null, MigrationField::getIsWriteRequiresMasterRead, bo.getIsWriteRequiresMasterRead());
        lqw.eq(StringUtils.isNotBlank(bo.getFieldDataType()), MigrationField::getFieldDataType, bo.getFieldDataType());
        lqw.eq(bo.getFieldLength() != null, MigrationField::getFieldLength, bo.getFieldLength());
        lqw.eq(bo.getFieldPrecision() != null, MigrationField::getFieldPrecision, bo.getFieldPrecision());
        lqw.eq(bo.getFieldScale() != null, MigrationField::getFieldScale, bo.getFieldScale());
        lqw.eq(bo.getFieldByteLength() != null, MigrationField::getFieldByteLength, bo.getFieldByteLength());
        lqw.eq(StringUtils.isNotBlank(bo.getDefaultValue()), MigrationField::getDefaultValue, bo.getDefaultValue());
        lqw.eq(StringUtils.isNotBlank(bo.getCalculatedFormula()), MigrationField::getCalculatedFormula, bo.getCalculatedFormula());
        lqw.eq(StringUtils.isNotBlank(bo.getInlineHelpText()), MigrationField::getInlineHelpText, bo.getInlineHelpText());
        lqw.eq(StringUtils.isNotBlank(bo.getMask()), MigrationField::getMask, bo.getMask());
        lqw.eq(StringUtils.isNotBlank(bo.getMaskType()), MigrationField::getMaskType, bo.getMaskType());
        lqw.eq(StringUtils.isNotBlank(bo.getPicklistValues()), MigrationField::getPicklistValues, bo.getPicklistValues());
        lqw.like(StringUtils.isNotBlank(bo.getRelationshipName()), MigrationField::getRelationshipName, bo.getRelationshipName());
        lqw.eq(bo.getRelationshipOrder() != null, MigrationField::getRelationshipOrder, bo.getRelationshipOrder());
        lqw.eq(StringUtils.isNotBlank(bo.getReferenceTargetField()), MigrationField::getReferenceTargetField, bo.getReferenceTargetField());
        lqw.eq(bo.getQueryByDistance() != null, MigrationField::getQueryByDistance, bo.getQueryByDistance());
        lqw.eq(bo.getSearchPrefilterable() != null, MigrationField::getSearchPrefilterable, bo.getSearchPrefilterable());
        lqw.eq(StringUtils.isNotBlank(bo.getSoapType()), MigrationField::getSoapType, bo.getSoapType());
        lqw.eq(bo.getUpdateable() != null, MigrationField::getUpdateable, bo.getUpdateable());
        lqw.eq(bo.getWriteRequiresMasterRead() != null, MigrationField::getWriteRequiresMasterRead, bo.getWriteRequiresMasterRead());
        return lqw;
    }

    /**
     * 新增对象字段信息
     *
     * @param bo 对象字段信息
     * @return 是否新增成功
     */
    @Override
    public Boolean insertByBo(MigrationFieldBo bo) {
        MigrationField add = MapstructUtils.convert(bo, MigrationField.class);
        validEntityBeforeSave(add);
        boolean flag = baseMapper.insert(add) > 0;
        if (flag) {
            bo.setId(add.getId());
        }
        return flag;
    }

    /**
     * 批量新增对象字段信息
     *
     * @param boList 对象字段信息列表
     * @return 是否新增成功
     */
    @Override
    public Boolean insertBatch(Collection<MigrationFieldBo> boList) {
        if (boList == null || boList.isEmpty()) {
            return false;
        }

        List<MigrationField> fieldList = MapstructUtils.convert(new ArrayList<>(boList), MigrationField.class);
        boolean flag = baseMapper.insertBatch(fieldList);

        // 更新BO列表中的ID
        if (flag) {
            Iterator<MigrationFieldBo> boIterator = boList.iterator();
            Iterator<MigrationField> fieldIterator = fieldList.iterator();
            while (boIterator.hasNext() && fieldIterator.hasNext()) {
                MigrationFieldBo bo = boIterator.next();
                MigrationField field = fieldIterator.next();
                bo.setId(field.getId());
            }
        }

        return flag;
    }

    /**
     * 修改对象字段信息
     *
     * @param bo 对象字段信息
     * @return 是否修改成功
     */
    @Override
    public Boolean updateByBo(MigrationFieldBo bo) {
        MigrationField update = MapstructUtils.convert(bo, MigrationField.class);
        validEntityBeforeSave(update);
        return baseMapper.updateById(update) > 0;
    }

    /**
     * 保存前的数据校验
     */
    private void validEntityBeforeSave(MigrationField entity){
        //TODO 做一些数据校验,如唯一约束
    }

    /**
     * 校验并批量删除对象字段信息信息
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

    @Override
    public String getUpdateField(String api) {
        // 构建查询条件，根据api字段查询对应的MigrationField记录
        LambdaQueryWrapper<MigrationField> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(MigrationField::getApi, api);
        // 查询符合条件的所有字段记录
        List<MigrationField> list = baseMapper.selectList(queryWrapper);

        // 使用Set存储所有字段名，便于快速查找
        Set<String> fieldNames = new HashSet<>();
        for (MigrationField dataField : list) {
            fieldNames.add(dataField.getField());
        }

        // 按优先级顺序检查时间相关字段是否存在，并返回第一个找到的字段名
        if (fieldNames.contains("SystemModstamp")) {
            return "SystemModstamp";
        } else if (fieldNames.contains("LastModifiedDate")) {
            return "LastModifiedDate";
        } else {
            // 如果没有找到任何时间相关字段，返回空字符串
            return null;
        }
    }

    @Override
    public String getDateField(String api) {
        // 构建查询条件，根据api字段查询对应的MigrationField记录
        LambdaQueryWrapper<MigrationField> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(MigrationField::getApi, api);
        // 查询符合条件的所有字段记录
        List<MigrationField> list = baseMapper.selectList(queryWrapper);

        // 使用Set存储所有字段名，便于快速查找
        Set<String> fieldNames = new HashSet<>();
        for (MigrationField dataField : list) {
            fieldNames.add(dataField.getField());
        }

        // 按优先级顺序检查时间相关字段是否存在，并返回第一个找到的字段名
        if (fieldNames.contains("CreatedDate")) {
            return "CreatedDate";
        } else if (fieldNames.contains("SystemModstamp")) {
            return "SystemModstamp";
        } else if (fieldNames.contains("LastModifiedDate")) {
            return "LastModifiedDate";
        } else {
            // 如果没有找到任何时间相关字段，返回空字符串
            return null;
        }
    }

    @Override
    public String getBlodField(String api) {
        // 构建查询条件，根据api字段查询对应的MigrationField记录
        LambdaQueryWrapper<MigrationField> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(MigrationField::getApi, api);
        // 查询符合条件的所有字段记录
        List<MigrationField> list = baseMapper.selectList(queryWrapper);
        for (MigrationField dataField : list) {
            if ("base64".equals(dataField.getFieldDataType())) {
                return dataField.getField();
            }
        }
        return null;
    }
    
    @Override
    public boolean isDeletedFieldExists(String api) {
        // 构建查询条件，根据api和field查询对应的MigrationField记录
        LambdaQueryWrapper<MigrationField> queryWrapper = Wrappers.lambdaQuery();
        queryWrapper.eq(MigrationField::getApi, api);
        queryWrapper.eq(MigrationField::getField, "IsDeleted");
        // 查询符合条件的记录数量
        long count = baseMapper.selectCount(queryWrapper);
        // 如果记录数量大于0，则表示存在IsDeleted字段
        return count > 0;
    }

}