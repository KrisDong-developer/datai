package com.datai.auth.service;

import java.util.List;
import com.datai.auth.domain.DataiSfToken;

/**
 * 令牌Service接口
 * 
 * @author datai
 * @date 2025-12-24
 */
public interface IDataiSfTokenService 
{
    /**
     * 查询令牌
     * 
     * @param id 令牌主键
     * @return 令牌
     */
    public DataiSfToken selectDataiSfTokenById(Long id);

    /**
     * 查询令牌列表
     * 
     * @param dataiSfToken 令牌
     * @return 令牌集合
     */
    public List<DataiSfToken> selectDataiSfTokenList(DataiSfToken dataiSfToken);

    /**
     * 新增令牌
     * 
     * @param dataiSfToken 令牌
     * @return 结果
     */
    public int insertDataiSfToken(DataiSfToken dataiSfToken);

    /**
     * 修改令牌
     * 
     * @param dataiSfToken 令牌
     * @return 结果
     */
    public int updateDataiSfToken(DataiSfToken dataiSfToken);

    /**
     * 批量删除令牌
     * 
     * @param ids 需要删除的令牌主键集合
     * @return 结果
     */
    public int deleteDataiSfTokenByIds(Long[] ids);

    /**
     * 删除令牌信息
     * 
     * @param id 令牌主键
     * @return 结果
     */
    public int deleteDataiSfTokenById(Long id);
}
