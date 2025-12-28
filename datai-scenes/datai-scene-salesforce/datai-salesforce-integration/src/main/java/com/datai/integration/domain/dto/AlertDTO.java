package com.datai.integration.domain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Schema(description = "限流告警DTO")
public class AlertDTO {

    @Schema(description = "告警ID")
    private Long id;

    @Schema(description = "API类型")
    private String apiType;

    @Schema(description = "告警级别")
    private String level;

    @Schema(description = "告警类型")
    private String alertType;

    @Schema(description = "告警消息")
    private String message;

    @Schema(description = "当前使用量")
    private Integer currentUsage;

    @Schema(description = "限制值")
    private Integer limitValue;

    @Schema(description = "是否已解决")
    private Boolean resolved;

    @Schema(description = "告警时间")
    private LocalDateTime alertTime;

    @Schema(description = "解决时间")
    private LocalDateTime resolveTime;

    @Schema(description = "告警级别枚举")
    public enum AlertLevel {
        INFO("info"),
        WARNING("warning"),
        ERROR("error"),
        CRITICAL("critical");

        private final String value;

        AlertLevel(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }

    @Schema(description = "告警类型枚举")
    public enum AlertType {
        USAGE_HIGH("usage_high"),
        USAGE_CRITICAL("usage_critical"),
        RATE_LIMIT_EXCEEDED("rate_limit_exceeded"),
        DAILY_LIMIT_EXCEEDED("daily_limit_exceeded");

        private final String value;

        AlertType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }
}
