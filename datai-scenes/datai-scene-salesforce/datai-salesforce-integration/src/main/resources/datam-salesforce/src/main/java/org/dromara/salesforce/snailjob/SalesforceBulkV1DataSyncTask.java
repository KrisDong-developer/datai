package org.dromara.salesforce.snailjob;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.client.model.ExecuteResult;
import lombok.extern.slf4j.Slf4j;
import org.dromara.salesforce.service.SalesforceBulkV1DataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Salesforce BulkV1数据同步任务
 * <p>
 * 该任务用于根据migrationBatch配置，使用BulkV1 API查询Salesforce的数据并保存到本地数据库
 * </p>
 *
 * @author lingma
 * @since 1.0.0
 */
@Slf4j
@Component
@JobExecutor(name = "salesforceBulkV1DataSyncTask")
public class SalesforceBulkV1DataSyncTask {

    @Autowired
    private SalesforceBulkV1DataService salesforceBulkV1DataService;

    /**
     * 同步Salesforce数据(BulkV1 API)
     * <p>
     * 根据migrationBatch配置，使用BulkV1 API查询Salesforce的数据并保存到本地数据库。
     * </p>
     *
     * @param jobArgs 任务参数
     * @return ExecuteResult 执行结果
     */
    public ExecuteResult jobExecute(JobArgs jobArgs) {
        return salesforceBulkV1DataService.syncData(jobArgs);
    }
}
