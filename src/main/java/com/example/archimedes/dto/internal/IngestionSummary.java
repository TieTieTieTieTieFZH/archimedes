package com.example.archimedes.dto.internal;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDateTime;
import java.time.Duration;
import java.util.Map;
import java.util.List;

/**
 * 摄取任务总结 - 统一版本
 * 
 * 摄取任务执行结果的统计信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngestionSummary {
    
    /**
     * 任务ID
     */
    private String jobId;
    
    /**
     * 数据源描述
     */
    private String sourceDescription;
    
    /**
     * 开始时间
     */
    private LocalDateTime startTime;
    
    /**
     * 结束时间
     */
    private LocalDateTime endTime;
    
    /**
     * 总处理数量
     */
    private long totalProcessed;
    
    /**
     * 成功数量
     */
    private long successCount;
    
    /**
     * 失败数量
     */
    private long failureCount;
    
    /**
     * 跳过数量
     */
    private long skippedCount;
    
    /**
     * 错误分类统计
     */
    private Map<String, Integer> errorCategories;
    
    /**
     * 警告信息
     */
    private List<String> warnings;
    
    /**
     * 计算总耗时
     */
    public Duration getTotalDuration() {
        if (startTime == null || endTime == null) {
            return Duration.ZERO;
        }
        return Duration.between(startTime, endTime);
    }
    
    /**
     * 计算成功率
     */
    public double getSuccessRate() {
        if (totalProcessed == 0) return 0.0;
        return (double) successCount / totalProcessed * 100.0;
    }
    
    /**
     * 计算失败率
     */
    public double getFailureRate() {
        if (totalProcessed == 0) return 0.0;
        return (double) failureCount / totalProcessed * 100.0;
    }
    
    /**
     * 是否执行成功
     */
    public boolean isSuccessful() {
        return getSuccessRate() >= 95.0;
    }
    
    /**
     * 是否需要关注
     */
    public boolean needsAttention() {
        return getFailureRate() > 5.0 || (warnings != null && !warnings.isEmpty());
    }
    
    /**
     * 创建成功总结
     */
    public static IngestionSummary success(
            String jobId,
            String sourceDescription,
            LocalDateTime startTime,
            LocalDateTime endTime,
            long totalProcessed,
            long successCount) {
        
        return IngestionSummary.builder()
                .jobId(jobId)
                .sourceDescription(sourceDescription)
                .startTime(startTime)
                .endTime(endTime)
                .totalProcessed(totalProcessed)
                .successCount(successCount)
                .failureCount(0L)
                .skippedCount(totalProcessed - successCount)
                .build();
    }
}