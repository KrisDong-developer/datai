package com.datai.setting.service;

import java.util.List;
import com.datai.setting.domain.DataiConfigSnapshot;

/**
 * 配置快照Service接口
 * 
 * @author datai
 * @date 2025-12-24
 */
public interface IDataiConfigSnapshotService 
{
    /**
     * 查询配置快照
     * 
     * @param id 配置快照主键
     * @return 配置快照
     */
    public DataiConfigSnapshot selectDataiConfigSnapshotById(String id);

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
     * @param ids 需要删除的配置快照主键集合
     * @return 结果
     */
    public int deleteDataiConfigSnapshotByIds(String[] ids);

    /**
     * 删除配置快照信息
     * 
     * @param id 配置快照主键
     * @return 结果
     */
    public int deleteDataiConfigSnapshotById(String id);

    /**
     * 从当前配置生成快照
     * 
     * @param snapshotName 快照名称
     * @param environmentId 环境ID
     * @param description 描述
     * @return 配置快照
     */
    public DataiConfigSnapshot createSnapshot(String snapshotName, Long environmentId, String description);

    /**
     * 恢复快照
     * 
     * @param snapshotId 快照ID
     * @param restoreReason 恢复原因
     * @return 结果
     */
    public int restoreSnapshot(String snapshotId, String restoreReason);

    /**
     * 获取快照详细信息（包含配置内容）
     * 
     * @param snapshotId 快照ID
     * @return 配置快照
     */
    public DataiConfigSnapshot getSnapshotDetail(String snapshotId);

    /**
     * 比较两个快照的差异
     * 
     * @param snapshotId1 快照ID1
     * @param snapshotId2 快照ID2
     * @return 差异信息
     */
    public String compareSnapshots(String snapshotId1, String snapshotId2);
}
