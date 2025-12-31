package com.datai.integration.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "限流配置DTO")
public class LimitConfigDTO {

    @Schema(description = "API类型")
    private String apiType;

    @Schema(description = "每日请求限制")
    private Integer maxRequestsPerDay;

    @Schema(description = "每秒请求限制")
    private Integer requestsPerSecond;

    @Schema(description = "令牌桶容量")
    private Integer bucketCapacity;

    @Schema(description = "告警阈值(百分比)")
    private Double alertThreshold;

    @Schema(description = "严重告警阈值(百分比)")
    private Double criticalThreshold;

    @Schema(description = "是否启用")
    private Boolean enabled;

    @Schema(description = "最大重试次数")
    private Integer maxRetries;

    @Schema(description = "初始退避延迟(ms)")
    private Long initialBackoffMs;

    @Schema(description = "最大退避延迟(ms)")
    private Long maxBackoffMs;
}
