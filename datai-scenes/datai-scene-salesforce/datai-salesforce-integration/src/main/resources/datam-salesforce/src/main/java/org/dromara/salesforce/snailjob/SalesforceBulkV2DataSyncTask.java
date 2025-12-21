package org.dromara.salesforce.snailjob;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.client.model.ExecuteResult;
import lombok.extern.slf4j.Slf4j;
import org.dromara.salesforce.service.SalesforceBulkV2DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Salesforce BulkV2数据同步任务
 * <p>
 * 该任务用于根据migrationBatch配置，使用BulkV2 API查询Salesforce的数据并保存到本地数据库
 * </p>
 *
 * @author lingma
 * @since 1.0.0
 */
@Slf4j
@Component
@JobExecutor(name = "salesforceBulkV2DataSyncTask")
public class SalesforceBulkV2DataSyncTask {

    @Autowired
    private SalesforceBulkV2DataService salesforceBulkV2DataService;

    /**
     * 同步Salesforce数据(BulkV2 API)
     * <p>
     * 根据migrationBatch配置，使用BulkV2 API查询Salesforce的数据并保存到本地数据库。
     * </p>
     *
     * @param jobArgs 任务参数
     * @return ExecuteResult 执行结果
     */
    public ExecuteResult jobExecute(JobArgs jobArgs) {
        return salesforceBulkV2DataService.syncStockData(jobArgs);
    }
}