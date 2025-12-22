package com.datai.integration.mapper;

import java.util.List;
import com.datai.integration.domain.DataiIntegrationField;

/**
 * 对象字段信息Mapper接口
 * 
 * @author datai
 * @date 2025-12-22
 */
public interface DataiIntegrationFieldMapper 
{
    /**
     * 查询对象字段信息
     * 
     * @param id 对象字段信息主键
     * @return 对象字段信息
     */
    public DataiIntegrationField selectDataiIntegrationFieldById(Long id);

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
     * 删除对象字段信息
     * 
     * @param id 对象字段信息主键
     * @return 结果
     */
    public int deleteDataiIntegrationFieldById(Long id);

    /**
     * 批量删除对象字段信息
     * 
     * @param ids 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataiIntegrationFieldByIds(Long[] ids);
}
