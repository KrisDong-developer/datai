package com.datai.auth.mapper;

import java.util.List;
import com.datai.auth.domain.DataiSfToken;

/**
 * 令牌Mapper接口
 * 
 * @author datai
 * @date 2025-12-14
 */
public interface DataiSfTokenMapper 
{
    /**
     * 查询令牌
     * 
     * @param tokenId 令牌主键
     * @return 令牌
     */
    public DataiSfToken selectDataiSfTokenByTokenId(Long tokenId);

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
     * 删除令牌
     * 
     * @param tokenId 令牌主键
     * @return 结果
     */
    public int deleteDataiSfTokenByTokenId(Long tokenId);

    /**
     * 批量删除令牌
     * 
     * @param tokenIds 需要删除的数据主键集合
     * @return 结果
     */
    public int deleteDataiSfTokenByTokenIds(Long[] tokenIds);
}
