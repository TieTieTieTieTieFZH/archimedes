package com.example.archimedes.dto.internal;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

/**
 * 原始论文数据 - 统一版本
 * 
 * 从数据源解析出来的原始数据，容错性优先
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RawPaper {
    
    /**
     * ArXiv ID（原始字符串）
     */
    private String id;
    
    /**
     * 原始标题
     */
    private String title;
    
    /**
     * 原始摘要
     */
    private String abstractText;
    
    /**
     * 原始发布日期
     */
    private String publishDate;
    
    /**
     * 原始分类数组
     */
    private String[] categories;
    
    /**
     * 原始作者数组
     */
    private String[] authors;
    
    /**
     * ArXiv URL
     */
    private String url;
    
    /**
     * 其他原始字段
     */
    private String journal;
    private String doi;
    private String comments;
    
    /**
     * 数据来源
     */
    private String sourceType;
    
    /**
     * 检查是否有效
     */
    public boolean isValid() {
        return id != null && !id.trim().isEmpty() 
               && title != null && !title.trim().isEmpty();
    }
    
    /**
     * 获取清理后的ID
     */
    public String getCleanId() {
        if (id == null) return null;
        return id.replaceAll("v\\d+$", "");
    }
}