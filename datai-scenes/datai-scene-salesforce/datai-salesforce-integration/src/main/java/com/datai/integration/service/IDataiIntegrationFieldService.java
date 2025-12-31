package com.datai.integration.service;

import java.util.List;
import com.datai.integration.model.domain.DataiIntegrationField;

/**
 * 对象字段信息Service接口
 * 
 * @author datai
 * @date 2025-12-24
 */
public interface IDataiIntegrationFieldService 
{
    /**
     * 查询对象字段信息
     * 
     * @param id 对象字段信息主键
     * @return 对象字段信息
     */
    public DataiIntegrationField selectDataiIntegrationFieldById(Integer id);

    /**
     * 查询对象字段信息列表
     * 
     * @param dataiIntegrationField 对象字段信息
     * @return 对象字段信息集合
     */
    public List<DataiIntegrationField> selectDataiIntegrationFieldList(DataiIntegrationField dataiIntegrationField);

    /**
     * 新增对象字段信息
     * 
     * @param dataiIntegrationField 对象字段信息
     * @return 结果
     */
    public int insertDataiIntegrationField(DataiIntegrationField dataiIntegrationField);

    /**
     * 修改对象字段信息
     * 
     * @param dataiIntegrationField 对象字段信息
     * @return 结果
     */
    public int updateDataiIntegrationField(DataiIntegrationField dataiIntegrationField);

    /**
     * 批量删除对象字段信息
     * 
     * @param ids 需要删除的对象字段信息主键集合
     * @return 结果
     */
    public int deleteDataiIntegrationFieldByIds(Integer[] ids);

    /**
     * 删除对象字段信息信息
     * 
     * @param id 对象字段信息主键
     * @return 结果
     */
    public int deleteDataiIntegrationFieldById(Integer id);

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
    String getBlobField(String api);

    /**
     * 判断指定API的对象是否存在IsDeleted字段
     *
     * @param api API名称
     * @return 是否存在IsDeleted字段
     */
    boolean isDeletedFieldExists(String api);
}
