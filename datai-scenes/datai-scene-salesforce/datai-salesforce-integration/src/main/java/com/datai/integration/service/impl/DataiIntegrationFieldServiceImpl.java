package com.datai.integration.service.impl;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.integration.mapper.DataiIntegrationFieldMapper;
import com.datai.integration.model.domain.DataiIntegrationField;
import com.datai.integration.service.IDataiIntegrationFieldService;
import com.datai.common.core.domain.model.LoginUser;


/**
 * 对象字段信息Service业务层处理
 *
 * @author datai
 * @date 2025-12-24
 */
@Service
public class DataiIntegrationFieldServiceImpl implements IDataiIntegrationFieldService {
    @Autowired
    private DataiIntegrationFieldMapper dataiIntegrationFieldMapper;

    /**
     * 查询对象字段信息
     *
     * @param id 对象字段信息主键
     * @return 对象字段信息
     */
    @Override
    public DataiIntegrationField selectDataiIntegrationFieldById(Integer id)
    {
        return dataiIntegrationFieldMapper.selectDataiIntegrationFieldById(id);
    }

    /**
     * 查询对象字段信息列表
     *
     * @param dataiIntegrationField 对象字段信息
     * @return 对象字段信息
     */
    @Override
    public List<DataiIntegrationField> selectDataiIntegrationFieldList(DataiIntegrationField dataiIntegrationField)
    {
        return dataiIntegrationFieldMapper.selectDataiIntegrationFieldList(dataiIntegrationField);
    }

    /**
     * 新增对象字段信息
     *
     * @param dataiIntegrationField 对象字段信息
     * @return 结果
     */
    @Override
    public int insertDataiIntegrationField(DataiIntegrationField dataiIntegrationField)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationField.setCreateTime(DateUtils.getNowDate());
                dataiIntegrationField.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationField.setCreateBy(username);
                dataiIntegrationField.setUpdateBy(username);
            return dataiIntegrationFieldMapper.insertDataiIntegrationField(dataiIntegrationField);
    }

    /**
     * 修改对象字段信息
     *
     * @param dataiIntegrationField 对象字段信息
     * @return 结果
     */
    @Override
    public int updateDataiIntegrationField(DataiIntegrationField dataiIntegrationField)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiIntegrationField.setUpdateTime(DateUtils.getNowDate());
                dataiIntegrationField.setUpdateBy(username);
        return dataiIntegrationFieldMapper.updateDataiIntegrationField(dataiIntegrationField);
    }

    /**
     * 批量删除对象字段信息
     *
     * @param ids 需要删除的对象字段信息主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationFieldByIds(Integer[] ids)
    {
        return dataiIntegrationFieldMapper.deleteDataiIntegrationFieldByIds(ids);
    }

    /**
     * 删除对象字段信息信息
     *
     * @param id 对象字段信息主键
     * @return 结果
     */
    @Override
    public int deleteDataiIntegrationFieldById(Integer id)
    {
        return dataiIntegrationFieldMapper.deleteDataiIntegrationFieldById(id);
    }


    @Override
    public String getUpdateField(String api) {
        // 构建查询条件，根据api字段查询对应的 DataiIntegrationField记录
        DataiIntegrationField query = new DataiIntegrationField();
        query.setApi(api);
        // 查询符合条件的所有字段记录
        List<DataiIntegrationField> list = dataiIntegrationFieldMapper.selectDataiIntegrationFieldList(query);

        // 使用Set存储所有字段名，便于快速查找
        Set<String> fieldNames = new HashSet<>();
        for (DataiIntegrationField dataField : list) {
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
        // 构建查询条件，根据api字段查询对应的 DataiIntegrationField记录
        DataiIntegrationField query = new DataiIntegrationField();
        query.setApi(api);
        // 查询符合条件的所有字段记录
        List<DataiIntegrationField> list = dataiIntegrationFieldMapper.selectDataiIntegrationFieldList(query);

        // 使用Set存储所有字段名，便于快速查找
        Set<String> fieldNames = new HashSet<>();
        for (DataiIntegrationField dataField : list) {
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
    public String getBlobField(String api) {
        // 构建查询条件，根据api字段查询对应的 DataiIntegrationField记录
        DataiIntegrationField query = new DataiIntegrationField();
        query.setApi(api);
        // 查询符合条件的所有字段记录
        List<DataiIntegrationField> list = dataiIntegrationFieldMapper.selectDataiIntegrationFieldList(query);
        for (DataiIntegrationField dataField : list) {
            if ("base64".equals(dataField.getFieldDataType())) {
                return dataField.getField();
            }
        }
        return null;
    }

    @Override
    public boolean isDeletedFieldExists(String api) {
        // 构建查询条件，根据api和field查询对应的 DataiIntegrationField记录
        DataiIntegrationField query = new DataiIntegrationField();
        query.setApi(api);
        query.setField("IsDeleted");
        // 查询符合条件的所有字段记录
        List<DataiIntegrationField> list = dataiIntegrationFieldMapper.selectDataiIntegrationFieldList(query);
        // 如果记录数量大于0，则表示存在IsDeleted字段
        return !list.isEmpty();
    }
}
