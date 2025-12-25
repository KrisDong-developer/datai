package com.datai.auth.service.impl;

import java.util.List;
        import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.auth.mapper.DataiSfTokenMapper;
import com.datai.auth.domain.DataiSfToken;
import com.datai.auth.service.IDataiSfTokenService;
import com.datai.common.core.domain.model.LoginUser;


/**
 * 令牌Service业务层处理
 *
 * @author datai
 * @date 2025-12-24
 */
@Service
public class DataiSfTokenServiceImpl implements IDataiSfTokenService {
    @Autowired
    private DataiSfTokenMapper dataiSfTokenMapper;

    /**
     * 查询令牌
     *
     * @param id 令牌主键
     * @return 令牌
     */
    @Override
    public DataiSfToken selectDataiSfTokenById(Long id)
    {
        return dataiSfTokenMapper.selectDataiSfTokenById(id);
    }

    /**
     * 查询令牌列表
     *
     * @param dataiSfToken 令牌
     * @return 令牌
     */
    @Override
    public List<DataiSfToken> selectDataiSfTokenList(DataiSfToken dataiSfToken)
    {
        return dataiSfTokenMapper.selectDataiSfTokenList(dataiSfToken);
    }

    /**
     * 新增令牌
     *
     * @param dataiSfToken 令牌
     * @return 结果
     */
    @Override
    public int insertDataiSfToken(DataiSfToken dataiSfToken)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiSfToken.setCreateTime(DateUtils.getNowDate());
                dataiSfToken.setUpdateTime(DateUtils.getNowDate());
                dataiSfToken.setCreateBy(username);
                dataiSfToken.setUpdateBy(username);
            return dataiSfTokenMapper.insertDataiSfToken(dataiSfToken);
    }

    /**
     * 修改令牌
     *
     * @param dataiSfToken 令牌
     * @return 结果
     */
    @Override
    public int updateDataiSfToken(DataiSfToken dataiSfToken)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiSfToken.setUpdateTime(DateUtils.getNowDate());
                dataiSfToken.setUpdateBy(username);
        return dataiSfTokenMapper.updateDataiSfToken(dataiSfToken);
    }

    /**
     * 批量删除令牌
     *
     * @param ids 需要删除的令牌主键
     * @return 结果
     */
    @Override
    public int deleteDataiSfTokenByIds(Long[] ids)
    {
        return dataiSfTokenMapper.deleteDataiSfTokenByIds(ids);
    }

    /**
     * 删除令牌信息
     *
     * @param id 令牌主键
     * @return 结果
     */
    @Override
    public int deleteDataiSfTokenById(Long id)
    {
        return dataiSfTokenMapper.deleteDataiSfTokenById(id);
    }
}
