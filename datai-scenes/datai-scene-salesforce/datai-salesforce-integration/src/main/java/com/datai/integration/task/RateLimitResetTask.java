package com.datai.integration.task;

import com.datai.integration.domain.DataiIntegrationRateLimit;
import com.datai.integration.service.IDataiIntegrationRateLimitService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Component("rateLimitResetTask")
public class RateLimitResetTask {

    @Autowired
    private IDataiIntegrationRateLimitService rateLimitService;

    public void resetDailyRateLimits() {
        try {
            log.info("开始执行每日限流重置任务");

            DataiIntegrationRateLimit query = new DataiIntegrationRateLimit();
            query.setLimitType("Daily");
            List<DataiIntegrationRateLimit> rateLimits = rateLimitService.selectDataiIntegrationRateLimitList(query);

            int resetCount = 0;
            for (DataiIntegrationRateLimit rateLimit : rateLimits) {
                if (rateLimit.getIsBlocked() != null && rateLimit.getIsBlocked()) {
                    rateLimit.setIsBlocked(false);
                }

                rateLimit.setCurrentUsage(0);
                Integer maxLimit = rateLimit.getMaxLimit() != null ? rateLimit.getMaxLimit() : 0;
                rateLimit.setRemainingVal(maxLimit);
                rateLimit.setResetTime(LocalDateTime.now().plusDays(1).withHour(0).withMinute(0).withSecond(0));

                rateLimitService.updateDataiIntegrationRateLimit(rateLimit);
                resetCount++;
            }

            log.info("每日限流重置任务完成，共重置 {} 条记录", resetCount);
        } catch (Exception e) {
            log.error("执行每日限流重置任务失败", e);
        }
    }
}
