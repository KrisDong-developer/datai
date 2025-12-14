package com.datai.setting.service.impl;

import java.util.List;

import com.datai.common.core.domain.model.LoginUser;
import com.datai.common.utils.DateUtils;
        import com.datai.common.utils.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.datai.setting.mapper.DataiConfigSnapshotMapper;
import com.datai.setting.domain.DataiConfigSnapshot;
import com.datai.setting.service.IDataiConfigSnapshotService;


/**
 * 配置快照Service业务层处理
 *
 * @author datai
 * @date 2025-12-14
 */
@Service
public class DataiConfigSnapshotServiceImpl implements IDataiConfigSnapshotService {
    @Autowired
    private DataiConfigSnapshotMapper dataiConfigSnapshotMapper;

    /**
     * 查询配置快照
     *
     * @param snapshotId 配置快照主键
     * @return 配置快照
     */
    @Override
    public DataiConfigSnapshot selectDataiConfigSnapshotBySnapshotId(String snapshotId)
    {
        return dataiConfigSnapshotMapper.selectDataiConfigSnapshotBySnapshotId(snapshotId);
    }

    /**
     * 查询配置快照列表
     *
     * @param dataiConfigSnapshot 配置快照
     * @return 配置快照
     */
    @Override
    public List<DataiConfigSnapshot> selectDataiConfigSnapshotList(DataiConfigSnapshot dataiConfigSnapshot)
    {
        return dataiConfigSnapshotMapper.selectDataiConfigSnapshotList(dataiConfigSnapshot);
    }

    /**
     * 新增配置快照
     *
     * @param dataiConfigSnapshot 配置快照
     * @return 结果
     */
    @Override
    public int insertDataiConfigSnapshot(DataiConfigSnapshot dataiConfigSnapshot)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

                dataiConfigSnapshot.setCreateTime(DateUtils.getNowDate());
                dataiConfigSnapshot.setUpdateTime(DateUtils.getNowDate());
                dataiConfigSnapshot.setCreateBy(username);
                dataiConfigSnapshot.setUpdateBy(username);
            return dataiConfigSnapshotMapper.insertDataiConfigSnapshot(dataiConfigSnapshot);
    }

    /**
     * 修改配置快照
     *
     * @param dataiConfigSnapshot 配置快照
     * @return 结果
     */
    @Override
    public int updateDataiConfigSnapshot(DataiConfigSnapshot dataiConfigSnapshot)
    {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        String username = loginUser.getUsername();

        return dataiConfigSnapshotMapper.updateDataiConfigSnapshot(dataiConfigSnapshot);
    }

    /**
     * 批量删除配置快照
     *
     * @param snapshotIds 需要删除的配置快照主键
     * @return 结果
     */
    @Override
    public int deleteDataiConfigSnapshotBySnapshotIds(String[] snapshotIds)
    {
        return dataiConfigSnapshotMapper.deleteDataiConfigSnapshotBySnapshotIds(snapshotIds);
    }

    /**
     * 删除配置快照信息
     *
     * @param snapshotId 配置快照主键
     * @return 结果
     */
    @Override
    public int deleteDataiConfigSnapshotBySnapshotId(String snapshotId)
    {
        return dataiConfigSnapshotMapper.deleteDataiConfigSnapshotBySnapshotId(snapshotId);
    }
}
