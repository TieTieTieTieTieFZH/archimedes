package com.example.archimedes.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import java.util.List;

/**
 * 搜索请求DTO - 统一版本
 * 
 * 合并了原有的复杂设计，保留核心功能
 */
@Data
@Builder
@NoArgsConstructor 
@AllArgsConstructor
public class SearchRequest {
    
    /**
     * 搜索查询词
     */
    @Size(max = 200, message = "查询文本长度不能超过200字符")
    private String query;
    
    /**
     * 分类筛选
     */
    private List<String> categories;
    
    /**
     * 日期筛选
     */
    private DateRange dateRange;
    
    /**
     * 页码（从1开始）
     */
    @Min(value = 1, message = "页码必须大于等于1")
    @Max(value = 1000, message = "页码不能超过1000") 
    @Builder.Default
    private int page = 1;
    
    /**
     * 每页大小
     */
    @Min(value = 1, message = "每页大小必须大于等于1")
    @Max(value = 100, message = "每页大小不能超过100")
    @Builder.Default
    private int size = 20;
    
    /**
     * 是否启用语义搜索
     */
    @Builder.Default
    private boolean enableSemanticSearch = true;
    
    /**
     * 日期范围内部类
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DateRange {
        private String startDate; // yyyy-MM-dd
        private String endDate;   // yyyy-MM-dd
    }
    
    /**
     * 判断是否为纯筛选模式
     */
    public boolean isPureFilterMode() {
        return query == null || query.trim().isEmpty();
    }
    
    /**
     * 获取有效查询文本
     */
    public String getEffectiveQuery() {
        return query != null ? query.trim() : "";
    }
    
    /**
     * 计算偏移量
     */
    public int getOffset() {
        return (page - 1) * size;
    }
}