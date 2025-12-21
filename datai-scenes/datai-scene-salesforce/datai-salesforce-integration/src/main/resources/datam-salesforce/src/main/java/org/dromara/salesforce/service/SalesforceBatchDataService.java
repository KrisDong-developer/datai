package org.dromara.salesforce.service;

import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.client.model.ExecuteResult;
import org.dromara.salesforce.domain.bo.MigrationObjectBo;

/**
 * Salesforce批次数据服务接口
 * <p>
 * 该服务用于根据对象信息创建数据同步批次
 * </p>
 *
 * @author lingma
 * @since 1.0.0
 */
public interface SalesforceBatchDataService {

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
    ExecuteResult createBatchData(JobArgs jobArgs);

}
