package com.datai.integration.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

@Data
@Schema(description = "API使用趋势DTO")
public class TrendDTO {

    @Schema(description = "API类型")
    private String apiType;

    @Schema(description = "趋势类型")
    private String trendType;

    @Schema(description = "时间间隔")
    private String interval;

    @Schema(description = "开始时间")
    private LocalDateTime startTime;

    @Schema(description = "结束时间")
    private LocalDateTime endTime;

    @Schema(description = "数据点列表")
    private List<TrendDataPoint> dataPoints;

    @Schema(description = "平均增长率")
    private Double averageGrowthRate;

    @Schema(description = "峰值使用量")
    private Long peakUsage;

    @Schema(description = "峰值时间")
    private LocalDateTime peakTime;

    @Schema(description = "趋势类型枚举")
    public enum TrendType {
        REQUEST_COUNT("request_count"),
        SUCCESS_RATE("success_rate"),
        RESPONSE_TIME("response_time"),
        RATE_LIMIT_HITS("rate_limit_hits");

        private final String value;

        TrendType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Schema(description = "时间间隔枚举")
    public enum Interval {
        MINUTE("minute"),
        HOUR("hour"),
        DAY("day"),
        WEEK("week"),
        MONTH("month");

        private final String value;

        Interval(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Data
    @Schema(description = "趋势数据点")
    public static class TrendDataPoint {
        @Schema(description = "时间戳")
        private LocalDateTime timestamp;

        @Schema(description = "数值")
        private Double value;

        @Schema(description = "请求数")
        private Long requestCount;

        @Schema(description = "成功率")
        private Double successRate;

        @Schema(description = "平均响应时间")
        private Double avgResponseTime;
    }
}
