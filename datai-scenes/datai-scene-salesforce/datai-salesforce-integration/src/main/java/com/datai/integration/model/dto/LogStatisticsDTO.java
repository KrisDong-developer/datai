package com.datai.integration.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@Data
@Schema(description = "日志统计DTO")
public class LogStatisticsDTO {

    @Schema(description = "总记录数")
    private Long totalCount;

    @Schema(description = "成功记录数")
    private Long successCount;

    @Schema(description = "失败记录数")
    private Long failureCount;

    @Schema(description = "成功率(%)")
    private BigDecimal successRate;

    @Schema(description = "平均执行时间(秒)")
    private BigDecimal avgExecutionTime;

    @Schema(description = "最大执行时间(秒)")
    private BigDecimal maxExecutionTime;

    @Schema(description = "最小执行时间(秒)")
    private BigDecimal minExecutionTime;

    @Schema(description = "按操作类型统计")
    private List<OperationTypeStatistics> operationTypeStats;

    @Schema(description = "按对象API统计")
    private List<ObjectApiStatistics> objectApiStats;

    @Schema(description = "按状态统计")
    private Map<String, Long> statusStats;

    @Schema(description = "按批次统计")
    private List<BatchStatistics> batchStats;

    @Data
    @Schema(description = "操作类型统计")
    public static class OperationTypeStatistics {
        @Schema(description = "操作类型")
        private String operationType;

        @Schema(description = "记录数")
        private Long count;

        @Schema(description = "成功数")
        private Long successCount;

        @Schema(description = "失败数")
        private Long failureCount;

        @Schema(description = "平均执行时间(秒)")
        private BigDecimal avgExecutionTime;
    }

    @Data
    @Schema(description = "对象API统计")
    public static class ObjectApiStatistics {
        @Schema(description = "对象API")
        private String objectApi;

        @Schema(description = "记录数")
        private Long count;

        @Schema(description = "成功数")
        private Long successCount;

        @Schema(description = "失败数")
        private Long failureCount;

        @Schema(description = "平均执行时间(秒)")
        private BigDecimal avgExecutionTime;
    }

    @Data
    @Schema(description = "批次统计")
    public static class BatchStatistics {
        @Schema(description = "批次ID")
        private Integer batchId;

        @Schema(description = "记录数")
        private Long count;

        @Schema(description = "成功数")
        private Long successCount;

        @Schema(description = "失败数")
        private Long failureCount;

        @Schema(description = "平均执行时间(秒)")
        private BigDecimal avgExecutionTime;
    }
}
