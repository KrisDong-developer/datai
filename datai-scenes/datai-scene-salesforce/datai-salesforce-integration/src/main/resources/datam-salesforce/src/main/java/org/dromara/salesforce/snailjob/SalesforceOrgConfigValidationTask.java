package org.dromara.salesforce.snailjob;

import com.aizuda.snailjob.client.job.core.annotation.JobExecutor;
import com.aizuda.snailjob.client.job.core.dto.JobArgs;
import com.aizuda.snailjob.client.model.ExecuteResult;
import com.aizuda.snailjob.common.log.SnailJobLog;
import lombok.extern.slf4j.Slf4j;
import org.dromara.salesforce.factory.BulkV1ConnectionFactory;
import org.dromara.salesforce.factory.BulkV2ConnectionFactory;
import org.dromara.salesforce.factory.RESTConnectionFactory;
import org.dromara.salesforce.factory.SOAPConnectionFactory;
import org.dromara.salesforce.factory.SourceSessionFactory;
import org.dromara.salesforce.factory.TargetSessionFactory;
import org.springframework.stereotype.Component;

/**
 * Salesforce ORG配置验证任务
 * <p>
 * 该任务验证源ORG和目标ORG的配置是否有效，包括各种连接类型（Bulk V1、Bulk V2、REST、SOAP）的可用性。
 * 通过执行此任务，可以确保Salesforce连接配置的正确性和可用性。
 * </p>
 *
 * @author lingma
 * @since 1.0.0
 */
@Slf4j
@Component
@JobExecutor(name = "salesforceOrgConfigValidationTask")
public class SalesforceOrgConfigValidationTask {

    /**
     * 验证Salesforce ORG配置
     * <p>
     * 检查源ORG和目标ORG的各种连接是否正常工作。
     * 验证包括：
     * 1. 源ORG和目标ORG的会话连接
     * 2. Bulk V1 API连接
     * 3. Bulk V2 API连接
     * 4. REST API连接
     * 5. SOAP API连接
     * </p>
     *
     * @param jobArgs 任务参数
     * @return ExecuteResult 执行结果
     */
    public ExecuteResult jobExecute(JobArgs jobArgs) {
        SnailJobLog.LOCAL.info("开始执行Salesforce ORG配置验证任务");

        try {
            // 验证源ORG配置
            validateSourceOrgConfig();

            // 验证目标ORG配置
            validateTargetOrgConfig();

            SnailJobLog.LOCAL.info("Salesforce ORG配置验证任务执行完成");
            return ExecuteResult.success("Salesforce ORG配置验证成功");
        } catch (Exception e) {
            SnailJobLog.LOCAL.error("Salesforce ORG配置验证任务执行失败", e);
            return ExecuteResult.failure("Salesforce ORG配置验证失败: " + e.getMessage());
        }
    }

    /**
     * 验证源ORG配置
     * <p>
     * 通过尝试获取各种类型的连接来验证源ORG配置是否有效。
     * </p>
     */
    private void validateSourceOrgConfig() {
        SnailJobLog.LOCAL.info("开始验证源ORG配置");

        try {
            // 验证源会话
            SourceSessionFactory.instance();
            SnailJobLog.LOCAL.info("源ORG会话连接验证通过");

            // 验证Bulk V1连接
            BulkV1ConnectionFactory.sourceInstance();
            SnailJobLog.LOCAL.info("源ORG Bulk V1连接验证通过");

            // 验证Bulk V2连接
            BulkV2ConnectionFactory.sourceInstance();
            SnailJobLog.LOCAL.info("源ORG Bulk V2连接验证通过");

            // 验证REST连接
            RESTConnectionFactory.sourceInstance();
            SnailJobLog.LOCAL.info("源ORG REST连接验证通过");

            // 验证SOAP连接
            SOAPConnectionFactory.sourceInstance();
            SnailJobLog.LOCAL.info("源ORG SOAP连接验证通过");

            SnailJobLog.LOCAL.info("源ORG配置验证完成");
        } catch (Exception e) {
            SnailJobLog.LOCAL.error("源ORG配置验证失败", e);
            throw new RuntimeException("源ORG配置验证失败", e);
        }
    }

    /**
     * 验证目标ORG配置
     * <p>
     * 通过尝试获取各种类型的连接来验证目标ORG配置是否有效。
     * </p>
     */
    private void validateTargetOrgConfig() {
        SnailJobLog.LOCAL.info("开始验证目标ORG配置");

        try {
            // 验证目标会话
            TargetSessionFactory.instance();
            SnailJobLog.LOCAL.info("目标ORG会话连接验证通过");

            // 验证Bulk V1连接
            BulkV1ConnectionFactory.targetInstance();
            SnailJobLog.LOCAL.info("目标ORG Bulk V1连接验证通过");

            // 验证Bulk V2连接
            BulkV2ConnectionFactory.targetInstance();
            SnailJobLog.LOCAL.info("目标ORG Bulk V2连接验证通过");

            // 验证REST连接
            RESTConnectionFactory.targetInstance();
            SnailJobLog.LOCAL.info("目标ORG REST连接验证通过");

            // 验证SOAP连接
            SOAPConnectionFactory.targetInstance();
            SnailJobLog.LOCAL.info("目标ORG SOAP连接验证通过");

            SnailJobLog.LOCAL.info("目标ORG配置验证完成");
        } catch (Exception e) {
            SnailJobLog.LOCAL.error("目标ORG配置验证失败", e);
            throw new RuntimeException("目标ORG配置验证失败", e);
        }
    }
}
