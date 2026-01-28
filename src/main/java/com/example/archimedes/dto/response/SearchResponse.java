package com.example.archimedes.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;
import java.util.Collections;

/**
 * 搜索响应DTO - 统一版本
 * 
 * 标准化的搜索结果响应
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SearchResponse {
    
    /**
     * 搜索结果列表
     */
    @Builder.Default
    private List<PaperVO> items = Collections.emptyList();
    
    /**
     * 总命中数
     */
    private long totalHits;
    
    /**
     * 当前页码
     */
    private int currentPage;
    
    /**
     * 每页大小
     */
    private int pageSize;
    
    /**
     * 总页数
     */
    private int totalPages;
    
    /**
     * 查询耗时（毫秒）
     */
    private long tookMillis;
    
    /**
     * 是否使用了语义搜索
     */
    private boolean usedSemanticSearch;
    
    /**
     * 是否还有下一页
     */
    public boolean hasNextPage() {
        return currentPage < totalPages;
    }
    
    /**
     * 是否有上一页
     */
    public boolean hasPreviousPage() {
        return currentPage > 1;
    }
    
    /**
     * 是否为空结果
     */
    public boolean isEmpty() {
        return items == null || items.isEmpty();
    }
    
    /**
     * 获取结果数量
     */
    public int getResultCount() {
        return items != null ? items.size() : 0;
    }
    
    /**
     * 创建空结果响应
     */
    public static SearchResponse empty(int page, int size, long tookMillis) {
        return SearchResponse.builder()
                .items(Collections.emptyList())
                .totalHits(0L)
                .currentPage(page)
                .pageSize(size)
                .totalPages(0)
                .tookMillis(tookMillis)
                .usedSemanticSearch(false)
                .build();
    }
    
    /**
     * 创建成功响应
     */
    public static SearchResponse success(
            List<PaperVO> items, 
            long totalHits,
            int currentPage, 
            int pageSize, 
            long tookMillis,
            boolean usedSemanticSearch) {
        
        int totalPages = pageSize > 0 ? (int) Math.ceil((double) totalHits / pageSize) : 0;
        
        return SearchResponse.builder()
                .items(items != null ? items : Collections.emptyList())
                .totalHits(totalHits)
                .currentPage(currentPage)
                .pageSize(pageSize)
                .totalPages(totalPages)
                .tookMillis(tookMillis)
                .usedSemanticSearch(usedSemanticSearch)
                .build();
    }
}