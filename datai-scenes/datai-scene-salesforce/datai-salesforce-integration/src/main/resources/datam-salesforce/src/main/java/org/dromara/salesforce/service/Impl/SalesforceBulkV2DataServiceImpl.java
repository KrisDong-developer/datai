package org.dromara.salesforce.service.Impl;

import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.client.model.ExecuteResult;
import com.aizuda.snailjob.common.log.SnailJobLog;
import com.alibaba.fastjson.JSON;
import com.sforce.async.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.dromara.common.core.utils.DateUtils;
import org.dromara.salesforce.config.SalesforceExecutor;
import org.dromara.salesforce.domain.bo.MigrationBatchBo;
import org.dromara.salesforce.domain.param.SalesforceParam;
import org.dromara.salesforce.domain.vo.MigrationBatchVo;
import org.dromara.salesforce.factory.BulkV2ConnectionFactory;
import org.dromara.salesforce.mapper.CustomMapper;
import org.dromara.salesforce.service.IMigrationBatchService;
import org.dromara.salesforce.service.SalesforceBulkV2DataService;
import org.dromara.salesforce.util.DataUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

/**
 * Salesforce BulkV2数据同步服务实现类
 * <p>
 * 该服务用于根据migrationBatch配置，使用BulkV2 API查询Salesforce的数据并保存到本地数据库
 * </p>
 *
 * @author lingma
 * @since 1.0.0
 */
@Slf4j
@Service
public class SalesforceBulkV2DataServiceImpl implements SalesforceBulkV2DataService {

    @Autowired
    private IMigrationBatchService migrationBatchService;

    @Autowired
    private CustomMapper customMapper;

    /**
     * Salesforce专用线程池执行器
     */
    @Autowired
    private SalesforceExecutor salesforceExecutor;

    // 添加缓存用于存储表的分区状态，避免重复查询数据库
    private final Map<String, Boolean> partitionedCache = new HashMap<>();

    /**
     * 同步Salesforce数据(Bulk V2 API)
     * <p>
     * 根据migrationBatch配置，使用Bulk V2 API查询Salesforce的数据并保存到本地数据库。
     * 使用多线程并发处理多个批次，提高数据同步效率。
     * </p>
     *
     * @param jobArgs 任务参数
     * @return ExecuteResult 执行结果
     */
    @Override
    public ExecuteResult syncStockData(JobArgs jobArgs) {
        SnailJobLog.LOCAL.info("开始执行Salesforce Bulk V2数据同步任务");
        // 使用多线程处理每个批次
        List<Future<?>> futures = new ArrayList<>();
        try {
            // 获取源ORG连接
            org.dromara.salesforce.core.BulkV2Connection connection = BulkV2ConnectionFactory.sourceInstance();
            SnailJobLog.LOCAL.info("成功获取源ORG Bulk V2连接");

            // 获取需要处理的migrationBatch
            List<MigrationBatchVo> batchList = getMigrationBatchList(jobArgs);
            SnailJobLog.LOCAL.info("获取到{}个批次需要处理", batchList.size());

            // 处理每个批次
            for (int i = 0; i < batchList.size(); i++) {
                MigrationBatchVo batch = batchList.get(i);
                Future<?> future = salesforceExecutor.execute(() -> {
                    processBatch(connection, batch);
                }, i, batch.getId()); // 批次号为索引，优先级为0

                futures.add(future);
            }

            // 等待所有任务完成
            salesforceExecutor.waitForFutures(futures.toArray(new Future[0]));

            SnailJobLog.LOCAL.info("Salesforce Bulk V2数据同步任务执行完成");
            return ExecuteResult.success("Salesforce Bulk V2数据同步成功");
        } catch (Exception e) {
            salesforceExecutor.remove(futures.toArray(new Future<?>[]{}));
            SnailJobLog.LOCAL.error("Salesforce Bulk V2数据同步任务执行失败", e);
            return ExecuteResult.failure("Salesforce Bulk V2数据同步失败: " + e.getMessage());
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

        if (param != null && StringUtils.isNotBlank(param.getApi())) {
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
     * @param connection BulkV2连接
     * @param batch      批次信息
     */
    private void processBatch(org.dromara.salesforce.core.BulkV2Connection connection, MigrationBatchVo batch) {
        SnailJobLog.LOCAL.info("开始处理批次: {} - API: {}, 同步时间范围: {} ~ {}",
                batch.getId(), batch.getApi(), batch.getSyncStartDate(), batch.getSyncEndDate());

        try {
            // 创建查询作业
            JobInfo job = createQueryJob(connection, batch);

            // 等待作业完成
            job = waitForJobCompletion(connection, job);

            // 处理查询结果
            processQueryResults(connection, job, batch);

            SnailJobLog.LOCAL.info("批次 ID: {} 处理完成", batch.getId());
        } catch (Exception e) {
            SnailJobLog.LOCAL.error("处理批次 {} 时发生未知异常", batch.getId(), e);
        }
    }

    /**
     * 创建查询作业
     *
     * @param connection BulkV2连接
     * @param batch      批次信息
     * @return JobInfo 作业信息
     * @throws AsyncApiException 异步API异常
     */
    private JobInfo createQueryJob(org.dromara.salesforce.core.BulkV2Connection connection, MigrationBatchVo batch) throws AsyncApiException {
        // 构建SOQL查询语句
        String soql = buildQuery(batch);
        SnailJobLog.LOCAL.info("构建SOQL查询语句: {}", soql);

        // 创建作业信息
        JobInfo job = new JobInfo();
        job.setOperation(OperationEnum.query);
        job.setObject(batch.getApi());
        job.setContentType(ContentType.CSV);

        // 创建作业
        job = connection.createJob(job);
        String jobId = job.getId();
        SnailJobLog.LOCAL.info("创建Bulk作业，作业ID: {}", jobId);

        // 上传查询语句作为数据
        connection.startIngest(jobId, new java.io.ByteArrayInputStream(soql.getBytes(StandardCharsets.UTF_8)));

        return job;
    }

    /**
     * 构建查询语句
     *
     * @param batch 批次信息
     * @return SOQL查询语句
     */
    private String buildQuery(MigrationBatchVo batch) {
        StringBuilder query = new StringBuilder();
        query.append("SELECT ");

        // 添加所有字段（这里简化处理，实际应获取对象的所有字段）
        query.append("Id, Name, CreatedDate, LastModifiedDate ");

        query.append("FROM ").append(batch.getApi());

        // 根据批次类型设置查询字段
        // 假设syncType为"1"时表示按创建时间查询(存量)，其他情况按修改时间查询(增量)
        String dateField = "1".equals(batch.getSyncType()) ? "CreatedDate" : "LastModifiedDate";

        // 添加时间范围条件
        if (batch.getSyncStartDate() != null || batch.getSyncEndDate() != null) {
            query.append(" WHERE ");
            if (batch.getSyncStartDate() != null) {
                query.append(dateField)
                        .append(" >= ")
                        .append(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(batch.getSyncStartDate()));
            }
            if (batch.getSyncEndDate() != null) {
                if (batch.getSyncStartDate() != null) {
                    query.append(" AND ");
                }
                query.append(dateField)
                        .append(" <= ")
                        .append(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'").format(batch.getSyncEndDate()));
            }
        }

        query.append(" ORDER BY Id");

        SnailJobLog.LOCAL.debug("构建的查询语句: {}", query.toString());
        return query.toString();
    }

    /**
     * 等待作业完成
     *
     * @param connection BulkV2连接
     * @param job        作业信息
     * @return JobInfo 完成后的作业信息
     * @throws AsyncApiException 异步API异常
     */
    private JobInfo waitForJobCompletion(org.dromara.salesforce.core.BulkV2Connection connection, JobInfo job) throws AsyncApiException {
        long sleepTime = 1000L; // 初始等待时间1秒
        while (job.getState() != JobStateEnum.JobComplete
                && job.getState() != JobStateEnum.Aborted
                && job.getState() != JobStateEnum.Failed) {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new AsyncApiException("等待作业完成时线程被中断", AsyncExceptionCode.Unknown);
            }
            job = connection.getExtractJobStatus(job.getId());
            SnailJobLog.LOCAL.debug("作业 {} 状态: {}", job.getId(), job.getState());

            // 指数退避，最大等待时间100秒
            sleepTime = Math.min(sleepTime * 2, 100000L);
        }

        if (job.getState() == JobStateEnum.Failed || job.getState() == JobStateEnum.Aborted) {
            throw new AsyncApiException("Bulk作业执行失败，状态: " + job.getState(), AsyncExceptionCode.Unknown);
        }

        return job;
    }

    /**
     * 处理查询结果
     *
     * @param connection BulkV2连接
     * @param job        作业信息
     * @param batch      批次信息
     * @throws AsyncApiException 异步API异常
     */
    private void processQueryResults(org.dromara.salesforce.core.BulkV2Connection connection, JobInfo job, MigrationBatchVo batch) throws AsyncApiException {
        try {
            int totalRecords = 0;
            int dbRecords = 0;

            // 获取查询结果流
            String locator = connection.getQueryLocator();
            while (!"null".equalsIgnoreCase(locator)) {
                try (InputStream resultStream = connection.getQueryResultStream(job.getId(), locator);
                     BufferedReader reader = new BufferedReader(new InputStreamReader(resultStream, StandardCharsets.UTF_8))) {

                    // 读取CSV数据
                    String line;
                    List<String> headers = null;
                    int recordCount = 0;

                    while ((line = reader.readLine()) != null) {
                        String[] values = line.split(",");

                        // 第一行是标题
                        if (headers == null) {
                            headers = Arrays.asList(values);
                            continue;
                        }

                        // 处理数据行
                        Map<String, Object> recordMap = new HashMap<>();
                        for (int i = 0; i < headers.size() && i < values.length; i++) {
                            String header = headers.get(i);
                            String value = values[i];

                            // 处理空值和引号
                            if ("\"\"".equals(value) || "".equals(value)) {
                                value = null;
                            } else if (value != null && value.startsWith("\"") && value.endsWith("\"")) {
                                value = value.substring(1, value.length() - 1);
                            }

                            recordMap.put(header.toLowerCase(), value);
                        }

                        // 检查表是否分区
                        boolean isPartitioned = checkIfTablePartitioned(batch.getApi().toLowerCase());

                        // 保存到数据库
                        if (!recordMap.isEmpty()) {
                            if (isPartitioned) {
                                // 如果表已分区，使用upsertToPartition方法
                                // 注意：这里需要确定合适的分区名，暂时使用默认值
                                customMapper.upsertToPartition(batch.getApi().toLowerCase(), "p_default", recordMap);
                            } else {
                                // 如果表未分区，使用普通upsert方法
                                customMapper.upsert(batch.getApi().toLowerCase(), recordMap);
                            }
                            dbRecords++;
                        }

                        recordCount++;
                        totalRecords++;
                    }

                    SnailJobLog.LOCAL.info("处理查询结果，共 {} 条记录", recordCount);
                } catch (Exception e) {
                    SnailJobLog.LOCAL.error("处理查询结果时发生异常", e);
                }

                // 获取下一个结果页的locator
                locator = connection.getQueryLocator();
            }

            // 更新批次状态
            updateBatchStatus(batch, totalRecords, dbRecords);

        } catch (Exception e) {
            SnailJobLog.LOCAL.error("处理查询结果时发生异常，作业ID: {}", job.getId(), e);
            throw new AsyncApiException("处理查询结果失败: " + e.getMessage(), AsyncExceptionCode.Unknown);
        }
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
     * 更新批次状态
     *
     * @param batch       批次信息
     * @param totalRecords 总记录数
     * @param dbRecords   数据库记录数
     */
    private void updateBatchStatus(MigrationBatchVo batch, int totalRecords, int dbRecords) {
        try {
            MigrationBatchBo updateBo = new MigrationBatchBo();
            updateBo.setId(batch.getId());
            updateBo.setDbNum(dbRecords);
            updateBo.setPullNum(totalRecords);
            updateBo.setLastSyncTime(new Date());

            if (batch.getFirstSyncTime() == null) {
                updateBo.setFirstSyncTime(new Date());
                updateBo.setFirstPullNum(totalRecords);
            }

            migrationBatchService.updateByBo(updateBo);
            SnailJobLog.LOCAL.info("更新批次 {} 状态成功，拉取记录数: {}，保存记录数: {}",
                    batch.getId(), totalRecords, dbRecords);
        } catch (Exception e) {
            SnailJobLog.LOCAL.error("更新批次 {} 状态失败", batch.getId(), e);
        }
    }
}