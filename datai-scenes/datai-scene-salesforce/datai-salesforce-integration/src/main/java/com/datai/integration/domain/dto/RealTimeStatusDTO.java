package com.datai.integration.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "实时限流状态DTO")
public class RealTimeStatusDTO {

    @Schema(description = "API类型")
    private String apiType;

    @Schema(description = "可用令牌数")
    private Integer availableTokens;

    @Schema(description = "今日已用请求数")
    private Integer dailyUsed;

    @Schema(description = "每日限额")
    private Integer dailyLimit;

    @Schema(description = "剩余请求数")
    private Integer dailyRemaining;

    @Schema(description = "每秒请求数限制")
    private Integer requestsPerSecond;

    @Schema(description = "使用率百分比")
    private Double usagePercentage;

    @Schema(description = "状态")
    private String status;

    @Schema(description = "最后更新时间")
    private LocalDateTime lastUpdateTime;

    @Schema(description = "告警列表")
    private List<AlertDTO> alerts;
}
