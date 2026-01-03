package com.datai.integration.mapper;

import cn.hutool.json.JSONObject;
import com.datai.salesforce.common.param.SalesforceParam;
import org.apache.ibatis.annotations.MapKey;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 通用数据库操作Mapper接口
 * 提供表结构管理、数据操作、查询统计等通用功能
 *
 * @author Red
 * @author Lingma
 * @description 通用数据库操作Mapper，支持动态表名和字段操作
 * @date 2022/09/27
 */
@Mapper
public interface CustomMapper {

    /**
     * 获取表所有字段
     *
     * @param tableName 表名
     * @return 字段名列表
     */
    List<String> getFields(@Param("tableName") String tableName);

    /**
     * 获取表中存在的ID列表
     *
     * @param tableName 表名
     * @param ids       ID列表
     * @return 存在的ID列表
     */
    List<String> getIds(@Param("tableName") String tableName, @Param("ids") List<String> ids);

    /**
     * 统计表数据条数
     *
     * @param param 查询参数
     * @return 数据条数
     */
    Integer count(@Param("param") SalesforceParam param);

    /**
     * 检测表是否存在
     *
     * @param tableName 表名
     * @return 表名（如果存在）或null（如果不存在）
     */
    String checkTable(@Param("tableName") String tableName);

    /**
     * 创建表
     *
     * @param tableName     表名
     * @param tableComment  表注释
     * @param maps          字段定义列表
     * @param index         索引定义列表
     */
    void createTable(@Param("tableName") String tableName, 
                     @Param("tableComment") String tableComment, 
                     @Param("maps") List<Map<String, Object>> maps, 
                     @Param("index") List<Map<String, Object>> index);

    /**
     * 创建字段
     *
     * @param tableName  表名
     * @param fieldName  字段名
     * @return 影响行数
     */
    int createField(@Param("tableName") String tableName, @Param("fieldName") String fieldName);

    /**
     * 添加字段（通用方法，支持字段类型和约束）
     *
     * @param tableName  表名
     * @param fieldName  字段名
     * @param fieldType  字段类型
     * @param isNullable 是否可为空
     * @return 影响行数
     */
    int addField(@Param("tableName") String tableName, 
                 @Param("fieldName") String fieldName, 
                 @Param("fieldType") String fieldType, 
                 @Param("isNullable") Boolean isNullable);

    /**
     * 修改字段（通用方法，支持字段类型和约束）
     *
     * @param tableName  表名
     * @param fieldName  字段名
     * @param fieldType  字段类型
     * @param isNullable 是否可为空
     * @return 影响行数
     */
    int modifyField(@Param("tableName") String tableName, 
                    @Param("fieldName") String fieldName, 
                    @Param("fieldType") String fieldType, 
                    @Param("isNullable") Boolean isNullable);

    /**
     * 删除字段（通用方法）
     *
     * @param tableName 表名
     * @param fieldName 字段名
     * @return 影响行数
     */
    int dropField(@Param("tableName") String tableName, 
                  @Param("fieldName") String fieldName);

    /**
     * 根据ID更新记录
     *
     * @param tableName 表名
     * @param maps      更新字段映射
     * @param id        记录ID
     */
    void updateById(@Param("tableName") String tableName, 
                    @Param("maps") List<Map<String, Object>> maps, 
                    @Param("id") String id);

    /**
     * 根据条件更新记录
     *
     * @param maps      更新字段映射
     * @param tableName 表名
     * @param sql       更新条件SQL
     */
    void update(@Param("maps") List<Map<String, Object>> maps, 
                @Param("tableName") String tableName, 
                @Param("sql") String sql);

    /**
     * 根据字段条件更新记录
     *
     * @param maps      查询条件字段映射
     * @param tableName 表名
     * @param newId     新ID值
     */
    void updateByField(@Param("maps") List<Map<String, Object>> maps, 
                       @Param("tableName") String tableName, 
                       @Param("newId") String newId);

    /**
     * 插入单条记录
     *
     * @param tableName 表名
     * @param maps      插入字段映射
     */
    void save(@Param("tableName") String tableName, 
              @Param("maps") List<Map<String, Object>> maps);

    /**
     * 批量插入记录
     *
     * @param tableName 表名
     * @param keys      字段名列表
     * @param values    值列表
     */
    void saveBatch(@Param("tableName") String tableName, 
                   @Param("keys") Collection<String> keys, 
                   @Param("values") Collection<Collection<Object>> values);

    /**
     * 根据ID更新或插入记录（upsert）
     * 如果map参数中包含Id且记录存在则更新，否则插入新记录
     *
     * @param tableName 表名
     * @param map       数据字段映射
     */
    void upsert(@Param("tableName") String tableName, 
                @Param("map") Map<String, Object> map);

    /**
     * 通用表数据查询
     *
     * @param select 查询字段
     * @param api    表名
     * @param sql    查询条件SQL
     * @return 查询结果列表
     */
    @MapKey("id")
    List<Map<String, Object>> list(@Param("select") String select, 
                                   @Param("api") String api, 
                                   @Param("sql") String sql);

    /**
     * 通用表数据查询，返回JSONObject
     *
     * @param select 查询字段
     * @param api    表名
     * @param sql    查询条件SQL
     * @return 查询结果列表
     */
    @MapKey("id")
    List<JSONObject> listJsonObject(@Param("select") String select, 
                                    @Param("api") String api, 
                                    @Param("sql") String sql);

    /**
     * 通用表ID数据查询
     *
     * @param select 查询字段
     * @param api    表名
     * @param sql    查询条件SQL
     * @return ID列表
     */
    List<String> listById(@Param("select") String select, 
                          @Param("api") String api, 
                          @Param("sql") String sql);

    /**
     * 根据ID获取记录
     *
     * @param select 查询字段
     * @param api    表名
     * @param id     记录ID
     * @return 记录映射
     */
    @MapKey("id")
    Map<String, Object> getById(@Param("select") String select, 
                                @Param("api") String api, 
                                @Param("id") String id);

    /**
     * 根据NewId获取记录
     *
     * @param select 查询字段
     * @param api    表名
     * @param id     NewId值
     * @return 记录映射
     */
    @MapKey("id")
    Map<String, Object> getByNewId(@Param("select") String select, 
                                   @Param("api") String api, 
                                   @Param("id") String id);

    /**
     * 根据SQL统计记录数
     *
     * @param api 表名
     * @param sql 查询条件SQL
     * @return 记录数
     */
    Integer countBySQL(@Param("api") String api, @Param("sql") String sql);

    /**
     * 根据条件删除记录
     *
     * @param tableName 表名
     * @param ids       ID列表
     */
    void delete(@Param("tableName") String tableName, @Param("ids") List<String> ids);

    /**
     * 根据ID删除单条记录
     *
     * @param tableName 表名
     * @param id        记录ID
     */
    void deleteOne(@Param("tableName") String tableName, @Param("id") String id);

    /**
     * 根据SQL条件删除记录
     *
     * @param tableName 表名
     * @param sql       删除条件SQL
     */
    void deleteBySQL(@Param("tableName") String tableName, @Param("sql") String sql);

    /**
     * 分页查询数据
     *
     * @param select 查询字段
     * @param api    表名
     * @param sql    查询条件SQL
     * @param offset 偏移量
     * @param limit  限制条数
     * @return 查询结果列表
     */
    @MapKey("id")
    List<Map<String, Object>> listPage(@Param("select") String select,
                                       @Param("api") String api,
                                       @Param("sql") String sql,
                                       @Param("offset") int offset,
                                       @Param("limit") int limit);

    /**
     * 执行原生SQL查询
     *
     * @param sql 原生SQL
     * @return 查询结果列表
     */
    @MapKey("id")
    List<Map<String, Object>> executeQuery(@Param("sql") String sql);

    /**
     * 执行原生SQL更新
     *
     * @param sql 原生SQL
     * @return 影响行数
     */
    int executeUpdate(@Param("sql") String sql);

    /**
     * 获取表记录总数
     *
     * @param tableName 表名
     * @return 记录总数
     */
    Integer countAll(@Param("tableName") String tableName);

    /**
     * 根据多个字段条件查询记录
     *
     * @param tableName 表名
     * @param conditions 查询条件字段映射
     * @return 查询结果列表
     */
    @MapKey("id")
    List<Map<String, Object>> listByConditions(@Param("tableName") String tableName,
                                               @Param("conditions") Map<String, Object> conditions);
                                               
    /**
     * 根据多个字段条件查询记录数
     *
     * @param tableName 表名
     * @param conditions 查询条件字段映射
     * @return 记录数
     */
    Integer countByConditions(@Param("tableName") String tableName,
                              @Param("conditions") Map<String, Object> conditions);
                              
    /**
     * 根据多个字段条件更新记录
     *
     * @param tableName 表名
     * @param updateFields 更新字段映射
     * @param conditions 查询条件字段映射
     */
    void updateByConditions(@Param("tableName") String tableName,
                            @Param("updateFields") Map<String, Object> updateFields,
                            @Param("conditions") Map<String, Object> conditions);
                            
    // ==================== MySQL分区相关方法 ====================
    
    /**
     * 检查表是否已分区
     *
     * @param tableName 表名
     * @return 是否已分区
     */
    Boolean isPartitioned(@Param("tableName") String tableName);
    
    /**
     * 创建RANGE分区表
     *
     * @param tableName    表名
     * @param tableComment 表注释
     * @param maps         字段定义列表
     * @param index        索引定义列表
     * @param partitionKey 分区键
     * @param partitions   分区定义列表
     */
    void createRangePartitionTable(@Param("tableName") String tableName,
                                   @Param("tableComment") String tableComment,
                                   @Param("maps") List<Map<String, Object>> maps,
                                   @Param("index") List<Map<String, Object>> index,
                                   @Param("partitionKey") String partitionKey,
                                   @Param("partitions") List<Map<String, Object>> partitions);
                                   
    /**
     * 创建HASH分区表
     *
     * @param tableName    表名
     * @param partitionKey 分区键
     * @param partitionNum 分区数量
     */
    void createHashPartitionTable(@Param("tableName") String tableName,
                                  @Param("partitionKey") String partitionKey,
                                  @Param("partitionNum") Integer partitionNum);
                                  
    /**
     * 添加RANGE分区
     *
     * @param tableName  表名
     * @param partitionName 分区名
     * @param partitionValue 分区值
     */
    void addRangePartition(@Param("tableName") String tableName,
                           @Param("partitionName") String partitionName,
                           @Param("partitionValue") String partitionValue);
                           
    /**
     * 删除分区
     *
     * @param tableName  表名
     * @param partitionName 分区名
     */
    void dropPartition(@Param("tableName") String tableName,
                       @Param("partitionName") String partitionName);
                       
    /**
     * 重建分区
     *
     * @param tableName  表名
     * @param partitionName 分区名
     */
    void rebuildPartition(@Param("tableName") String tableName,
                          @Param("partitionName") String partitionName);
                          
    /**
     * 获取表分区信息
     *
     * @param tableName 表名
     * @return 分区信息列表
     */
    @MapKey("partition_name")
    List<Map<String, Object>> getPartitionInfo(@Param("tableName") String tableName);
    
    /**
     * 获取分区表记录数
     *
     * @param tableName 表名
     * @param partitionName 分区名
     * @return 记录数
     */
    Integer countByPartition(@Param("tableName") String tableName,
                             @Param("partitionName") String partitionName);
                             
    /**
     * 在指定分区插入单条记录
     *
     * @param tableName 表名
     * @param partitionName 分区名
     * @param maps      插入字段映射
     */
    void saveToPartition(@Param("tableName") String tableName,
                         @Param("partitionName") String partitionName,
                         @Param("maps") List<Map<String, Object>> maps);
                         
    /**
     * 在指定分区批量插入记录
     *
     * @param tableName 表名
     * @param partitionName 分区名
     * @param keys      字段名列表
     * @param values    值列表
     */
    void saveBatchToPartition(@Param("tableName") String tableName,
                              @Param("partitionName") String partitionName,
                              @Param("keys") Collection<String> keys,
                              @Param("values") Collection<Collection<Object>> values);
                              
    /**
     * 在指定分区根据ID更新或插入记录（upsert）
     *
     * @param tableName 表名
     * @param partitionName 分区名
     * @param map       数据字段映射
     */
    void upsertToPartition(@Param("tableName") String tableName,
                           @Param("partitionName") String partitionName,
                           @Param("map") Map<String, Object> map);
                           
    /**
     * 在指定分区查询数据
     *
     * @param select 查询字段
     * @param tableName 表名
     * @param partitionName 分区名
     * @param sql    查询条件SQL
     * @return 查询结果列表
     */
    @MapKey("id")
    List<Map<String, Object>> listFromPartition(@Param("select") String select,
                                                @Param("tableName") String tableName,
                                                @Param("partitionName") String partitionName,
                                                @Param("sql") String sql);
                                                
    /**
     * 在指定分区根据ID获取记录
     *
     * @param select 查询字段
     * @param tableName 表名
     * @param partitionName 分区名
     * @param id     记录ID
     * @return 记录映射
     */
    @MapKey("id")
    Map<String, Object> getByIdFromPartition(@Param("select") String select,
                                             @Param("tableName") String tableName,
                                             @Param("partitionName") String partitionName,
                                             @Param("id") String id);
                                             
    /**
     * 在指定分区根据条件删除记录
     *
     * @param tableName 表名
     * @param partitionName 分区名
     * @param sql       删除条件SQL
     */
    void deleteFromPartition(@Param("tableName") String tableName,
                             @Param("partitionName") String partitionName,
                             @Param("sql") String sql);
                             
    /**
     * 根据分区键值确定数据应该存储在哪个分区
     *
     * @param tableName    表名
     * @param partitionKey 分区键
     * @param keyValue     分区键值
     * @return 分区名
     */
    String determinePartition(@Param("tableName") String tableName, 
                              @Param("partitionKey") String partitionKey, 
                              @Param("keyValue") Object keyValue);
    
    /**
     * 在指定分区根据ID更新记录
     *
     * @param tableName 表名
     * @param partitionName 分区名
     * @param maps      更新字段映射
     * @param id        记录ID
     */
    void updateByIdFromPartition(@Param("tableName") String tableName,
                                 @Param("partitionName") String partitionName,
                                 @Param("maps") List<Map<String, Object>> maps,
                                 @Param("id") String id);

}