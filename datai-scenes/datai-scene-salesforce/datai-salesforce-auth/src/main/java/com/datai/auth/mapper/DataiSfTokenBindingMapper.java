package com.datai.auth.mapper;

import java.util.List;
import com.datai.auth.domain.DataiSfTokenBinding;

/**
 * 令牌绑定Mapper接口
 * 
 * @author datai
 * @date 2025-12-14
 */
public interface DataiSfTokenBindingMapper 
{
    /**
     * 查询令牌绑定
     * 
     * @param bindingId 令牌绑定主键
     * @return 令牌绑定
     */
    public DataiSfTokenBinding selectDataiSfTokenBindingByBindingId(Long bindingId);

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
     * 删除令牌绑定
     * 
     * @param bindingId 令牌绑定主键
     * @return 结果
     */
    public int deleteDataiSfTokenBindingByBindingId(Long bindingId);

    /**
     * 批量删除令牌绑定
     * 
     * @param bindingIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataiSfTokenBindingByBindingIds(Long[] bindingIds);
}
