package com.example.archimedes.search.model;

import jakarta.validation.constraints.AssertTrue;
import java.util.List;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 结构化筛选条件
 * MVP文档要求：年份范围必须符合逻辑（Start <= End）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SearchFilter {
    
    /** 年份范围 - 开始年份 */
    private Integer startYear;
    
    /** 年份范围 - 结束年份 */
    private Integer endYear;
    
    /** 分类枚举列表 (例如: ["cs.AI", "cs.LG"]) */
    private List<String> categories;
    
    /** 作者筛选 */
    private List<String> authors;
    
    /**
     * 校验年份范围逻辑 - MVP 文档要求: Start <= End
     */
    @AssertTrue(message = "开始年份不能大于结束年份")
    public boolean isValidYearRange() {
        if (startYear == null || endYear == null) {
            return true; // 允许部分为空
        }
        return startYear <= endYear;
    }
    
    /**
     * 判断是否有任何筛选条件
     */
    public boolean hasAnyFilter() {
        return startYear != null || endYear != null || 
               (categories != null && !categories.isEmpty()) ||
               (authors != null && !authors.isEmpty());
    }
}