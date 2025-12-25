package com.datai.auth.service;

import java.util.List;
import com.datai.auth.domain.DataiSfTokenBinding;

/**
 * 令牌绑定Service接口
 * 
 * @author datai
 * @date 2025-12-24
 */
public interface IDataiSfTokenBindingService 
{
    /**
     * 查询令牌绑定
     * 
     * @param id 令牌绑定主键
     * @return 令牌绑定
     */
    public DataiSfTokenBinding selectDataiSfTokenBindingById(Long id);

    /**
     * 查询令牌绑定列表
     * 
     * @param dataiSfTokenBinding 令牌绑定
     * @return 令牌绑定集合
     */
    public List<DataiSfTokenBinding> selectDataiSfTokenBindingList(DataiSfTokenBinding dataiSfTokenBinding);

    /**
     * 新增令牌绑定
     * 
     * @param dataiSfTokenBinding 令牌绑定
     * @return 结果
     */
    public int insertDataiSfTokenBinding(DataiSfTokenBinding dataiSfTokenBinding);

    /**
     * 修改令牌绑定
     * 
     * @param dataiSfTokenBinding 令牌绑定
     * @return 结果
     */
    public int updateDataiSfTokenBinding(DataiSfTokenBinding dataiSfTokenBinding);

    /**
     * 批量删除令牌绑定
     * 
     * @param ids 需要删除的令牌绑定主键集合
     * @return 结果
     */
    public int deleteDataiSfTokenBindingByIds(Long[] ids);

    /**
     * 删除令牌绑定信息
     * 
     * @param id 令牌绑定主键
     * @return 结果
     */
    public int deleteDataiSfTokenBindingById(Long id);
}
