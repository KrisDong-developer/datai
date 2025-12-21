package org.dromara.salesforce.service.Impl;

import cn.hutool.json.JSONArray;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.client.model.ExecuteResult;
import com.aizuda.snailjob.common.log.SnailJobLog;
import com.alibaba.fastjson.JSON;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.sforce.soap.partner.DescribeSObjectResult;
import com.sforce.soap.partner.Field;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.soap.partner.QueryResult;
import com.sforce.soap.partner.sobject.SObject;
import com.sforce.ws.ConnectionException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.dromara.salesforce.config.SalesforceExecutor;
import org.dromara.salesforce.constant.SalesforceConstant;
import org.dromara.salesforce.domain.bo.MigrationBatchBo;
import org.dromara.salesforce.domain.param.SalesforceParam;
import org.dromara.salesforce.domain.vo.MigrationBatchVo;
import org.dromara.salesforce.factory.SOAPConnectionFactory;
import org.dromara.salesforce.mapper.CustomMapper;
import org.dromara.salesforce.service.IMigrationBatchService;
import org.dromara.salesforce.service.IMigrationFieldService;
import org.dromara.salesforce.service.SalesforceDataSoapService;
import org.dromara.salesforce.util.DataUtil;
import org.dromara.salesforce.util.SqlUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Salesforce数据同步服务实现类
 * <p>
 * 该服务用于根据migrationBatch配置，使用SOAP API查询Salesforce的数据并保存到本地数据库
 * </p>
 *
 * @author lingma
 * @since 1.0.0
 */
@Slf4j
@Service
public class SalesforceDataSoapServiceImpl implements SalesforceDataSoapService {

    @Autowired
    private IMigrationBatchService migrationBatchService;

    @Autowired
    private CustomMapper customMapper;

    @Autowired
    private IMigrationFieldService migrationFieldService;

    /**
     * Salesforce专用线程池执行器
     */
    @Autowired
    private SalesforceExecutor salesforceExecutor;

    // 添加缓存用于存储表的分区状态，避免重复查询数据库
    private final Map<String, Boolean> partitionedCache = new HashMap<>();

    /**
     * 同步Salesforce数据
     * <p>
     * 根据migrationBatch配置，使用SOAP API查询Salesforce的数据并保存到本地数据库。
     * 使用多线程并发处理多个批次，提高数据同步效率。
     * </p>
     *
     * @param jobArgs 任务参数
     * @return ExecuteResult 执行结果
     */
    @Override
    public ExecuteResult syncData(JobArgs jobArgs) {
        SnailJobLog.LOCAL.info("开始执行Salesforce数据同步任务");
        // 使用多线程处理每个批次
        List<Future<?>> futures = new ArrayList<>();
        try {
            // 获取源ORG连接
            PartnerConnection connection = SOAPConnectionFactory.sourceInstance();
            SnailJobLog.LOCAL.info("成功获取源ORG连接");

            // 获取需要处理的migrationBatch
            List<MigrationBatchVo> batchList = getMigrationBatchList(jobArgs);
            SnailJobLog.LOCAL.info("获取到{}个批次需要处理", batchList.size());

            for (int i = 0; i < batchList.size(); i++) {
                MigrationBatchVo batch = batchList.get(i);
                Future<?> future = salesforceExecutor.execute(() -> {
                    processBatch(connection, batch);
                }, i, batch.getId()); // 批次号为索引，优先级为0

                futures.add(future);
            }

            // 等待所有任务完成
            salesforceExecutor.waitForFutures(futures.toArray(new Future[0]));

            SnailJobLog.LOCAL.info("Salesforce数据同步任务执行完成");
            return ExecuteResult.success("Salesforce数据同步成功");
        } catch (Exception e) {
            salesforceExecutor.remove(futures.toArray(new Future<?>[]{}));
            SnailJobLog.LOCAL.error("Salesforce数据同步任务执行失败", e);
            return ExecuteResult.failure("Salesforce数据同步失败: " + e.getMessage());
        }
    }

    /**
     * 获取需要处理的migrationBatch列表
     *
     * @param jobArgs 任务参数
     * @return migrationBatch列表
     */
    private List<MigrationBatchVo> getMigrationBatchList(JobArgs jobArgs) {
        String value = String.valueOf(jobArgs.getJobParams());
        SalesforceParam param = JSON.parseObject(value, SalesforceParam.class);
        List<MigrationBatchVo> migrationBatchVos = new ArrayList<>();

        if (param != null && org.apache.commons.lang3.StringUtils.isNotBlank(param.getApi())) {
            // 按逗号拆分API列表
            Set<String> apiList = DataUtil.toApiSet(param.getApi());
            for (String api : apiList) {
                MigrationBatchBo batchBo = new MigrationBatchBo();
                batchBo.setApi(api);
                if (param.getBeginDate() != null) {
                    batchBo.setSyncStartDate(param.getBeginDate());
                }
                if (param.getEndDate() != null) {
                    batchBo.setSyncEndDate(param.getEndDate());
                }
                migrationBatchVos.addAll(migrationBatchService.queryList(batchBo));
            }
        } else {
            MigrationBatchBo batchBo = new MigrationBatchBo();
            if (param != null) {
                if (param.getBeginDate() != null) {
                    batchBo.setSyncStartDate(param.getBeginDate());
                }
                if (param.getEndDate() != null) {
                    batchBo.setSyncEndDate(param.getEndDate());
                }
            }
            migrationBatchVos.addAll(migrationBatchService.queryList(batchBo));
        }

        return migrationBatchVos;
    }

    /**
     * 处理单个批次的数据同步
     *
     * @param connection SOAP连接
     * @param batch      批次信息
     */
    private void processBatch(PartnerConnection connection, MigrationBatchVo batch) {
        SnailJobLog.LOCAL.info("开始处理批次: {} - API: {}, 同步时间范围: {} ~ {}",
                batch.getId(), batch.getApi(), batch.getSyncStartDate(), batch.getSyncEndDate());

        try {
            // 查询所有数据
            HashMap<String, Integer> integerHashMap = getAllSfData(connection, batch);

            // 更新批次状态
            updateBatchStatus(batch, integerHashMap);

            SnailJobLog.LOCAL.info("批次 ID: {} 处理完成", batch.getId());
        } catch (Exception e) {
            SnailJobLog.LOCAL.error("处理批次 {} 时发生未知异常", batch.getId(), e);
        }
    }

    /**
     * 获取Salesforce所有数据
     * <p>
     * 根据批次配置，查询Salesforce中的数据并处理
     * </p>
     *
     * @param connection SOAP连接
     * @param batch      批次信息
     * @return 处理的数据条数
     */
    private HashMap<String, Integer> getAllSfData(PartnerConnection connection, MigrationBatchVo batch) {
        HashMap<String, Integer> integerHashMap = null;
        try {
            // 构建查询参数
            SalesforceParam param = new SalesforceParam();
            param.setApi(batch.getApi());

            param.setDateField(batch.getBatchField());
            // 设置时间范围
            param.setBeginDate(batch.getSyncStartDate());
            param.setEndDate(batch.getSyncEndDate());
            if (migrationFieldService.isDeletedFieldExists(batch.getApi())){
                param.setIsDeleted(true);
            }

            // 获取对象字段信息
            List<String> fieldList = getSalesforceObjectFields(connection, batch.getApi());
            param.setSelect(String.join(",", fieldList));

            // 分批查询数据
            integerHashMap = queryAndProcessData(connection, param, batch, fieldList);

        } catch (Exception e) {
            SnailJobLog.LOCAL.error("获取Salesforce数据时发生异常，批次: {}", batch.getId(), e);
        }

        return integerHashMap;
    }

    /**
     * 获取Salesforce对象字段列表
     * <p>
     * 查询指定对象的所有字段，并过滤掉不支持的字段类型（如base64）
     * </p>
     *
     * @param connection SOAP连接
     * @param objectApi  对象API名称
     * @return 字段列表
     * @throws ConnectionException 连接异常
     */
    private List<String> getSalesforceObjectFields(PartnerConnection connection, String objectApi) throws ConnectionException {
        List<String> fields = new ArrayList<>();

        // 获取对象描述信息
        DescribeSObjectResult describeResult = connection.describeSObject(objectApi);
        Field[] objectFields = describeResult.getFields();

        // 过滤字段
        for (Field field : objectFields) {
            // 不查询文件类型字段
            if ("base64".equalsIgnoreCase(field.getType().toString())) {
                continue;
            }
            fields.add(field.getName());
        }

        // 添加常用系统字段
        if (!fields.contains("Id")) {
            fields.add("Id");
        }
        if (!fields.contains("Name")) {
            fields.add("Name");
        }

        return fields;
    }

    /**
     * 查询并处理数据
     * <p>
     * 根据参数分批查询Salesforce数据并处理
     * </p>
     *
     * @param connection SOAP连接
     * @param param      查询参数
     * @param batch      批次信息
     * @return 处理的数据条数
     */
    private HashMap<String, Integer> queryAndProcessData(PartnerConnection connection, SalesforceParam param, MigrationBatchVo batch, List<String> fieldList) {
        int totalCount = 0;
        int dbCount = 0;
        String lastId = null;
        boolean hasMoreData = true;
        HashMap<String, Integer> integerHashMap = new HashMap<>();
        while (hasMoreData) {
            try {
                // 设置查询参数
                if (lastId != null) {
                    param.setMaxId(lastId);
                }
                // 构建查询语句
                String query = buildDynamicQuery(param);
                SnailJobLog.LOCAL.info("执行查询SQL: {}", query);

                // 执行查询
                QueryResult result = connection.queryAll(query);

                // 处理查询结果
                if (result.getRecords() != null && result.getRecords().length > 0) {
                    dbCount += processQueryResult(batch.getApi(), result, fieldList);
                    totalCount += result.getRecords().length;

                    // 更新最后处理的ID，用于下一批查询
                    SObject[] records = result.getRecords();
                    lastId = records[records.length - 1].getId();

                    SnailJobLog.LOCAL.info("批次 {} 已处理 {} 条记录", batch.getId(), totalCount);
                }

                // 检查是否还有更多数据
                hasMoreData = !result.isDone();

                // 添加短暂延迟，避免API限制
                TimeUnit.MILLISECONDS.sleep(100);

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                SnailJobLog.LOCAL.error("查询数据时线程被中断，批次: {}", batch.getId(), e);
                break;
            } catch (Exception e) {
                SnailJobLog.LOCAL.error("查询处理数据时发生异常，批次: {}", batch.getId(), e);
                // 出现异常时停止处理
                hasMoreData = false;
            }
        }
        integerHashMap.put("totalCount", totalCount);
        integerHashMap.put("dbCount", dbCount);

        return integerHashMap;
    }

    /**
     * 构建动态查询语句
     *
     * @param param 查询参数
     * @return SOQL查询语句
     */
    private String buildDynamicQuery(SalesforceParam param) {
        Map<String, Object> map = new HashMap<>();
        map.put("param", param);
        return SqlUtil.showSql("org.dromara.salesforce.mapper.SalesforceMapper.list", map);
    }

    /**
     * 处理查询结果
     *
     * @param api    API名称
     * @param result 查询结果
     * @param fieldList 字段列表
     */
    private int processQueryResult(String api, QueryResult result, List<String> fieldList) {

        int count = 0;
        SObject[] records = result.getRecords();

        SnailJobLog.LOCAL.info("处理API {} 的查询结果，共 {} 条记录", api, records.length);

        // 检查表是否分区
        boolean isPartitioned = checkIfTablePartitioned(api);

        // 批量处理记录
        for (SObject record : records) {
            try {
                // 将SObject记录转换为Map
                Map<String, Object> recordMap = convertSObjectToMap(record, fieldList);

                // 根据表是否分区选择不同的upsert方法
                if (isPartitioned) {
                    // 如果表已分区，使用upsertToPartition方法
                    // 注意：这里需要确定合适的分区名，暂时使用默认值
                    customMapper.upsertToPartition(api.toLowerCase(), "p_default", recordMap);
                } else {
                    // 如果表未分区，使用普通upsert方法
                    customMapper.upsert(api.toLowerCase(), recordMap);
                }

                count++;

            } catch (Exception e) {
                SnailJobLog.LOCAL.error("处理记录时发生异常，记录ID: {}", record.getId(), e);
            }
        }
        return count;
    }

    /**
     * 检查表是否已分区
     *
     * @param tableName 表名
     * @return 是否已分区
     */
    private boolean checkIfTablePartitioned(String tableName) {
        // 先从缓存中获取
        if (partitionedCache.containsKey(tableName)) {
            return partitionedCache.get(tableName);
        }

        // 查询数据库确认表是否已分区
        Boolean isPartitioned = customMapper.isPartitioned(tableName);
        boolean result = (isPartitioned != null) ? isPartitioned : false;

        // 将结果存入缓存
        partitionedCache.put(tableName, result);

        return result;
    }

    /**
     * 将SObject转换为Map
     *
     * @param record SObject记录
     * @param fieldList 字段列表
     * @return 转换后的Map
     */
    private Map<String, Object> convertSObjectToMap(SObject record, List<String> fieldList) {
        Map<String, Object> recordMap = new HashMap<>();

        // 添加记录ID
        if (record.getId() != null) {
            recordMap.put("Id", record.getId());
        }

        // 添加其他字段
        for (String field : fieldList) {
            // 跳过ID字段，因为已经处理过了
            if ("Id".equalsIgnoreCase(field)) {
                continue;
            }

            try {
                Object value = record.getField(field);
                // 处理不同类型的字段值
                if (value != null) {
                    // 处理日期类型
                    if (value instanceof java.util.Calendar) {
                        recordMap.put(field.toLowerCase(), ((Calendar) value).getTime());
                    }
                    // 处理其他类型
                    else {
                        recordMap.put(field.toLowerCase(), value);
                    }
                } else {
                    // 对于null值，可以选择不放入map或者放入null
                    recordMap.put(field.toLowerCase(), null);
                }
            } catch (Exception e) {
                SnailJobLog.LOCAL.warn("获取字段 {} 的值时发生异常", field, e);
            }
        }

        return recordMap;
    }

    /**
     * 更新批次状态
     * @param batch       批次信息
     */
    private void updateBatchStatus(MigrationBatchVo batch, HashMap<String, Integer> integerHashMap) {

        MigrationBatchBo updateBo = new MigrationBatchBo();
        updateBo.setId(batch.getId());
        updateBo.setDbNum(integerHashMap.get("dbCount"));
        updateBo.setPullNum(integerHashMap.get("totalCount"));
        updateBo.setLastSyncTime(new Date());
        if (batch.getFirstSyncTime() == null) {
            updateBo.setFirstSyncTime(new Date());
            updateBo.setFirstPullNum(integerHashMap.get("totalCount"));
        }
        migrationBatchService.updateByBo(updateBo);
    }
}