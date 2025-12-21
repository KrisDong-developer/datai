package org.dromara.salesforce.snailjob;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.client.model.ExecuteResult;
import com.aizuda.snailjob.common.log.SnailJobLog;
import lombok.extern.slf4j.Slf4j;
import org.dromara.salesforce.service.SalesforceBatchDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Salesforce批次数据同步任务
 * <p>
 * 该任务用于根据对象信息创建数据同步批次
 * </p>
 *
 * @author lingma
 * @since 1.0.0
 */
@Slf4j
@Component
@JobExecutor(name = "salesforceBatchDataSyncTask")
public class SalesforceBatchDataSyncTask {

    @Autowired
    private SalesforceBatchDataService salesforceBatchDataService;

    /**
     * 创建批次数据
     * <p>
     * 根据对象信息创建数据同步批次，如果对象有最后同步时间，则从最后同步时间开始创建批次，
     * 否则从配置的源组织开始时间创建批次
     * </p>
     *
     * @param jobArgs 任务参数
     * @return ExecuteResult 执行结果
     */
    public ExecuteResult jobExecute(JobArgs jobArgs) {
        SnailJobLog.LOCAL.info("开始执行Salesforce批次数据创建任务");
        ExecuteResult batchData = salesforceBatchDataService.createBatchData(jobArgs);
        SnailJobLog.LOCAL.info("结束执行Salesforce批次数据创建任务");
        return batchData;
    }
}
