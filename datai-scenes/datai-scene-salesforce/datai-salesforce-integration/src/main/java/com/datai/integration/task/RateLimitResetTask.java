package com.datai.integration.task;

import com.datai.integration.model.domain.DataiIntegrationRateLimit;
import com.datai.integration.service.IDataiIntegrationRateLimitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 每日限流重置任务
 * 负责重置所有每日限流配置的使用量、剩余值和重置时间
 */
@Slf4j
@Component("rateLimitResetTask")
public class RateLimitResetTask {

    @Autowired
    private IDataiIntegrationRateLimitService rateLimitService;

    /**
     * 重置每日限流
     * 将所有每日限流配置的使用量重置为0，剩余值重置为最大限制值，并更新下一次重置时间
     */
    public void resetDailyRateLimits() {
        try {
            log.info("开始执行每日限流重置任务");

            // 查询所有每日限流配置
            DataiIntegrationRateLimit query = new DataiIntegrationRateLimit();
            query.setLimitType("Daily");
            List<DataiIntegrationRateLimit> rateLimits = rateLimitService.selectDataiIntegrationRateLimitList(query);

            int resetCount = 0;
            for (DataiIntegrationRateLimit rateLimit : rateLimits) {
                // 如果当前被阻止，则解除阻止状态
                if (rateLimit.getIsBlocked() != null && rateLimit.getIsBlocked()) {
                    rateLimit.setIsBlocked(false);
                }

                // 重置使用量为0
                rateLimit.setCurrentUsage(0);
                // 重置剩余值为最大限制值
                Integer maxLimit = rateLimit.getMaxLimit() != null ? rateLimit.getMaxLimit() : 0;
                rateLimit.setRemainingVal(maxLimit);
                // 更新重置时间为明天0点
                rateLimit.setResetTime(LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0));

                // 更新数据库
                rateLimitService.updateDataiIntegrationRateLimit(rateLimit);
                resetCount++;
            }

            log.info("每日限流重置任务完成，共重置 {} 条记录", resetCount);
        } catch (Exception e) {
            log.error("执行每日限流重置任务失败", e);
        }
    }
}
