package org.dromara.salesforce.snailjob;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.client.model.ExecuteResult;
import com.aizuda.snailjob.common.log.SnailJobLog;
import lombok.extern.slf4j.Slf4j;
import org.dromara.salesforce.service.SobjectSyncService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Salesforce对象同步任务
 * <p>
 * 该任务用于同步Salesforce所有对象的元数据到本地数据库
 * 1. 获取源ORG的connection
 * 2. 获取EntityDefinition的所有字段
 * 3. 拉取EntityDefinition的所有数据
 * </p>
 *
 * @author lingma
 * @since 1.0.0
 */
@Slf4j
@Component
@JobExecutor(name = "sobjectSyncTask")
public class SobjectSyncTask {

    @Autowired
    private SobjectSyncService sobjectSyncService;

    /**
     * 同步Salesforce所有对象结构
     *
     * @param jobArgs 任务参数
     * @return ExecuteResult 执行结果
     */
    public ExecuteResult jobExecute(JobArgs jobArgs) {
        SnailJobLog.LOCAL.info("开始执行Salesforce对象结构同步任务");
        ExecuteResult result = sobjectSyncService.syncObjects(jobArgs);
        SnailJobLog.LOCAL.info("Salesforce对象结构同步任务执行完成");
        return result;
    }


}
