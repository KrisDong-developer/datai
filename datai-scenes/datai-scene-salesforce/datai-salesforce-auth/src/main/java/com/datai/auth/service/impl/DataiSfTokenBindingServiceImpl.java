package com.datai.auth.service.impl;

import java.util.List;

import com.datai.common.core.domain.model.LoginUser;
import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.auth.mapper.DataiSfTokenBindingMapper;
import com.datai.auth.domain.DataiSfTokenBinding;
import com.datai.auth.service.IDataiSfTokenBindingService;


/**
 * 令牌绑定Service业务层处理
 *
 * @author datai
 * @date 2025-12-14
 */
@Service
public class DataiSfTokenBindingServiceImpl implements IDataiSfTokenBindingService {
    @Autowired
    private DataiSfTokenBindingMapper dataiSfTokenBindingMapper;

    /**
     * 查询令牌绑定
     *
     * @param bindingId 令牌绑定主键
     * @return 令牌绑定
     */
    @Override
    public DataiSfTokenBinding selectDataiSfTokenBindingByBindingId(Long bindingId)
    {
        return dataiSfTokenBindingMapper.selectDataiSfTokenBindingByBindingId(bindingId);
    }

    /**
     * 查询令牌绑定列表
     *
     * @param dataiSfTokenBinding 令牌绑定
     * @return 令牌绑定
     */
    @Override
    public List<DataiSfTokenBinding> selectDataiSfTokenBindingList(DataiSfTokenBinding dataiSfTokenBinding)
    {
        return dataiSfTokenBindingMapper.selectDataiSfTokenBindingList(dataiSfTokenBinding);
    }

    /**
     * 新增令牌绑定
     *
     * @param dataiSfTokenBinding 令牌绑定
     * @return 结果
     */
    @Override
    public int insertDataiSfTokenBinding(DataiSfTokenBinding dataiSfTokenBinding)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiSfTokenBinding.setCreateTime(DateUtils.getNowDate());
                dataiSfTokenBinding.setUpdateTime(DateUtils.getNowDate());
                dataiSfTokenBinding.setCreateBy(username);
                dataiSfTokenBinding.setUpdateBy(username);
            return dataiSfTokenBindingMapper.insertDataiSfTokenBinding(dataiSfTokenBinding);
    }

    /**
     * 修改令牌绑定
     *
     * @param dataiSfTokenBinding 令牌绑定
     * @return 结果
     */
    @Override
    public int updateDataiSfTokenBinding(DataiSfTokenBinding dataiSfTokenBinding)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiSfTokenBinding.setUpdateTime(DateUtils.getNowDate());
                dataiSfTokenBinding.setUpdateBy(username);
        return dataiSfTokenBindingMapper.updateDataiSfTokenBinding(dataiSfTokenBinding);
    }

    /**
     * 批量删除令牌绑定
     *
     * @param bindingIds 需要删除的令牌绑定主键
     * @return 结果
     */
    @Override
    public int deleteDataiSfTokenBindingByBindingIds(Long[] bindingIds)
    {
        return dataiSfTokenBindingMapper.deleteDataiSfTokenBindingByBindingIds(bindingIds);
    }

    /**
     * 删除令牌绑定信息
     *
     * @param bindingId 令牌绑定主键
     * @return 结果
     */
    @Override
    public int deleteDataiSfTokenBindingByBindingId(Long bindingId)
    {
        return dataiSfTokenBindingMapper.deleteDataiSfTokenBindingByBindingId(bindingId);
    }
}
