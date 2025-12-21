package org.dromara.salesforce.snailjob;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.client.model.ExecuteResult;
import com.aizuda.snailjob.common.log.SnailJobLog;
import lombok.extern.slf4j.Slf4j;
import org.dromara.salesforce.service.SalesforceDataSoapService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Salesforce数据同步任务
 * <p>
 * 该任务用于根据migrationBatch配置，使用SOAP API查询Salesforce的数据并保存到本地数据库
 * </p>
 *
 * @author lingma
 * @since 1.0.0
 */
@Slf4j
@Component
@JobExecutor(name = "SalesforceDataSoapSyncTask")
public class SalesforceDataSoapSyncTask {

    @Autowired
    private SalesforceDataSoapService salesforceDataSoapService;

    /**
     * SOAP同步Salesforce数据
     * <p>
     * 根据migrationBatch配置，使用SOAP API查询Salesforce的数据并保存到本地数据库
     * </p>
     *
     * @param jobArgs 任务参数
     * @return ExecuteResult 执行结果
     */
    public ExecuteResult jobExecute(JobArgs jobArgs) {
        SnailJobLog.LOCAL.info("开始执行Salesforces数据同步任务");
        ExecuteResult executeResult = salesforceDataSoapService.syncData(jobArgs);
        SnailJobLog.LOCAL.info("Salesforce数据同步任务执行完成");
        return executeResult;
    }
}
