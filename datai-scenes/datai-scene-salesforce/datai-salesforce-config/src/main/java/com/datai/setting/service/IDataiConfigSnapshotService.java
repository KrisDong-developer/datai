package com.datai.setting.service;

import java.util.List;
import com.datai.setting.domain.DataiConfigSnapshot;

/**
 * 配置快照Service接口
 * 
 * @author datai
 * @date 2025-12-14
 */
public interface IDataiConfigSnapshotService 
{
    /**
     * 查询配置快照
     * 
     * @param snapshotId 配置快照主键
     * @return 配置快照
     */
    public DataiConfigSnapshot selectDataiConfigSnapshotBySnapshotId(String snapshotId);

    /**
     * 查询配置快照列表
     * 
     * @param dataiConfigSnapshot 配置快照
     * @return 配置快照集合
     */
    public List<DataiConfigSnapshot> selectDataiConfigSnapshotList(DataiConfigSnapshot dataiConfigSnapshot);

    /**
     * 新增配置快照
     * 
     * @param dataiConfigSnapshot 配置快照
     * @return 结果
     */
    public int insertDataiConfigSnapshot(DataiConfigSnapshot dataiConfigSnapshot);

    /**
     * 修改配置快照
     * 
     * @param dataiConfigSnapshot 配置快照
     * @return 结果
     */
    public int updateDataiConfigSnapshot(DataiConfigSnapshot dataiConfigSnapshot);

    /**
     * 批量删除配置快照
     * 
     * @param snapshotIds 需要删除的配置快照主键集合
     * @return 结果
     */
    public int deleteDataiConfigSnapshotBySnapshotIds(String[] snapshotIds);

    /**
     * 删除配置快照信息
     * 
     * @param snapshotId 配置快照主键
     * @return 结果
     */
    public int deleteDataiConfigSnapshotBySnapshotId(String snapshotId);
}
