package com.datai.integration.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "API使用统计DTO")
public class UsageStatisticsDTO {

    @Schema(description = "API类型")
    private String apiType;

    @Schema(description = "统计开始时间")
    private LocalDateTime startTime;

    @Schema(description = "统计结束时间")
    private LocalDateTime endTime;

    @Schema(description = "总请求数")
    private Long totalRequests;

    @Schema(description = "成功请求数")
    private Long successRequests;

    @Schema(description = "失败请求数")
    private Long failedRequests;

    @Schema(description = "平均响应时间(ms)")
    private Double avgResponseTime;

    @Schema(description = "最大响应时间(ms)")
    private Long maxResponseTime;

    @Schema(description = "最小响应时间(ms)")
    private Long minResponseTime;

    @Schema(description = "成功率百分比")
    private Double successRate;

    @Schema(description = "每秒平均请求数")
    private Double avgRequestsPerSecond;

    @Schema(description = "峰值每秒请求数")
    private Double peakRequestsPerSecond;

    @Schema(description = "限流次数")
    private Long rateLimitHits;

    @Schema(description = "重试次数")
    private Long retryCount;
}
