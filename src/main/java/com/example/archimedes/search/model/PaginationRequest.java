package com.example.archimedes.search.model;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Max;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 分页参数
 * MVP文档要求：Page Size必须强制限制在配置定义的max_page_size以内
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaginationRequest {
    
    @Min(value = 1, message = "页码必须大于0")
    private int page = 1;
    
    @Min(value = 1, message = "每页大小必须大于0")
    @Max(value = 100, message = "每页大小不能超过100") // max_page_size 配置
    private int size = 10;
    
    /**
     * 计算 Elasticsearch 查询的 from 参数
     */
    public int getOffset() {
        return (page - 1) * size;
    }
    
    /**
     * 获取限制数量（for ES size parameter）
     */
    public int getLimit() {
        return size;
    }
}