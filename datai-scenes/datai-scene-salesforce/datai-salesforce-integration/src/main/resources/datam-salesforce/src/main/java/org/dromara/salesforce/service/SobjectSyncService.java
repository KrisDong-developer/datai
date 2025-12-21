package org.dromara.salesforce.service;

import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.client.model.ExecuteResult;
import com.sforce.soap.partner.PartnerConnection;
import com.sforce.ws.ConnectionException;
import org.dromara.salesforce.domain.param.SalesforceIncrementParam;
import org.dromara.salesforce.domain.param.SalesforceParam;
import org.dromara.salesforce.domain.param.SalesforceStockParam;

/**
 * Salesforce对象同步服务接口
 * <p>
 * 该服务用于同步Salesforce所有对象的元数据到本地数据库
 * 1. 获取源ORG的connection
 * 2. 获取EntityDefinition的所有字段
 * 3. 拉取EntityDefinition的所有数据
 * </p>
 *
 * @author lingma
 * @since 1.0.0
 */
public interface SobjectSyncService {

    /**
     * 同步Salesforce对象结构
     *
     * @return ExecuteResult 执行结果
     */
    ExecuteResult syncObjects(JobArgs jobArgs);

    int countSfNumByDateField(PartnerConnection connect, SalesforceParam param);

    int countSfStockNum(PartnerConnection connect, SalesforceStockParam param);

    int countSfIncrementNum(PartnerConnection connect, SalesforceIncrementParam param);

}
