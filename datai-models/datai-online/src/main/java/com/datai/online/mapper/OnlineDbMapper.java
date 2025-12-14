package com.datai.online.mapper;

import java.util.List;
import java.util.Map;

import com.datai.common.core.domain.BaseEntity;

/**
 * mysql数据库Mapper接口
 * 
 * @author Dftre
 * @date 2024-01-26
 */
public interface OnlineDbMapper {
    public List<Map<String,String>> selectDbTableList(BaseEntity baseEntity);
    public List<Map<String,String>> selectDbColumnsListByTableName(String tableName);
}
