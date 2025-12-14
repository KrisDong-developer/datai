package com.datai.auth.service.impl;

import java.util.List;

import com.datai.common.core.domain.model.LoginUser;
import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.auth.mapper.DataiSfTokenMapper;
import com.datai.auth.domain.DataiSfToken;
import com.datai.auth.service.IDataiSfTokenService;


/**
 * 令牌Service业务层处理
 *
 * @author datai
 * @date 2025-12-14
 */
@Service
public class DataiSfTokenServiceImpl implements IDataiSfTokenService {
    @Autowired
    private DataiSfTokenMapper dataiSfTokenMapper;

    /**
     * 查询令牌
     *
     * @param tokenId 令牌主键
     * @return 令牌
     */
    @Override
    public DataiSfToken selectDataiSfTokenByTokenId(Long tokenId)
    {
        return dataiSfTokenMapper.selectDataiSfTokenByTokenId(tokenId);
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
     * @param tokenIds 需要删除的令牌主键
     * @return 结果
     */
    @Override
    public int deleteDataiSfTokenByTokenIds(Long[] tokenIds)
    {
        return dataiSfTokenMapper.deleteDataiSfTokenByTokenIds(tokenIds);
    }

    /**
     * 删除令牌信息
     *
     * @param tokenId 令牌主键
     * @return 结果
     */
    @Override
    public int deleteDataiSfTokenByTokenId(Long tokenId)
    {
        return dataiSfTokenMapper.deleteDataiSfTokenByTokenId(tokenId);
    }
}
