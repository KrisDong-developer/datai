package com.datai.auth.service.impl;

import java.util.List;
        import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.auth.mapper.DataiSfTokenBindingMapper;
import com.datai.auth.domain.DataiSfTokenBinding;
import com.datai.auth.service.IDataiSfTokenBindingService;
import com.datai.common.core.domain.model.LoginUser;


/**
 * 令牌绑定Service业务层处理
 *
 * @author datai
 * @date 2025-12-24
 */
@Service
public class DataiSfTokenBindingServiceImpl implements IDataiSfTokenBindingService {
    @Autowired
    private DataiSfTokenBindingMapper dataiSfTokenBindingMapper;

    /**
     * 查询令牌绑定
     *
     * @param id 令牌绑定主键
     * @return 令牌绑定
     */
    @Override
    public DataiSfTokenBinding selectDataiSfTokenBindingById(Long id)
    {
        return dataiSfTokenBindingMapper.selectDataiSfTokenBindingById(id);
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
     * @param ids 需要删除的令牌绑定主键
     * @return 结果
     */
    @Override
    public int deleteDataiSfTokenBindingByIds(Long[] ids)
    {
        return dataiSfTokenBindingMapper.deleteDataiSfTokenBindingByIds(ids);
    }

    /**
     * 删除令牌绑定信息
     *
     * @param id 令牌绑定主键
     * @return 结果
     */
    @Override
    public int deleteDataiSfTokenBindingById(Long id)
    {
        return dataiSfTokenBindingMapper.deleteDataiSfTokenBindingById(id);
    }
}
