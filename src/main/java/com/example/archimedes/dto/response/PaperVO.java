package com.example.archimedes.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDate;
import java.util.List;

/**
 * 论文VO - 统一版本
 * 
 * 用于搜索结果返回，包含前端需要的所有信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PaperVO {
    
    /**
     * ArXiv ID
     */
    private String id;
    
    /**
     * 论文标题
     */
    private String title;
    
    /**
     * 摘要（已截断）
     */
    private String abstractText;
    
    /**
     * 作者列表
     */
    private List<String> authors;
    
    /**
     * 发布日期
     */
    private LocalDate publishDate;
    
    /**
     * 分类列表
     */
    private List<String> categories;
    
    /**
     * ArXiv URL
     */
    private String url;
    
    /**
     * 相关性评分（语义搜索时）
     */
    private Double relevanceScore;
    
    /**
     * 摘要是否被截断
     */
    private boolean abstractTruncated;
    
    /**
     * 高亮片段
     */
    private List<String> highlights;
    
    /**
     * 获取主要分类
     */
    public String getPrimaryCategory() {
        return categories != null && !categories.isEmpty() ? categories.get(0) : null;
    }
    
    /**
     * 获取第一作者
     */
    public String getFirstAuthor() {
        return authors != null && !authors.isEmpty() ? authors.get(0) : "未知作者";
    }
    
    /**
     * 获取发布年份
     */
    public Integer getPublishYear() {
        return publishDate != null ? publishDate.getYear() : null;
    }
}