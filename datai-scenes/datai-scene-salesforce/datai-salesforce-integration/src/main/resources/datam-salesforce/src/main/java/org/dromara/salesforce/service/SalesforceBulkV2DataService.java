package org.dromara.salesforce.service;

import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.client.model.ExecuteResult;

/**
 * Salesforce BulkV2数据同步服务接口
 * <p>
 * 该服务用于根据migrationBatch配置，使用BulkV2 API查询Salesforce的数据并保存到本地数据库
 * </p>
 *
 * @author lingma
 * @since 1.0.0
 */
public interface SalesforceBulkV2DataService {

    /**
     * 同步Salesforce数据(BulkV2 API)
     * <p>
     * 根据migrationBatch配置，使用BulkV2 API查询Salesforce的数据并保存到本地数据库。
     * </p>
     *
     * @param jobArgs 任务参数
     * @return ExecuteResult 执行结果
     */
    ExecuteResult syncStockData(JobArgs jobArgs);
}