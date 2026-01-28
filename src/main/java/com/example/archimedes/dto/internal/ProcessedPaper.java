package com.example.archimedes.dto.internal;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 处理后论文数据 - 统一版本
 * 
 * 经过清洗、验证、富集的论文数据，准备持久化
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProcessedPaper {
    
    /**
     * 清理后的ArXiv ID
     */
    private String id;
    
    /**
     * 清洗后的标题
     */
    private String title;
    
    /**
     * 清洗后的摘要
     */
    private String abstractText;
    
    /**
     * 解析后的发布日期
     */
    private LocalDate publishDate;
    
    /**
     * 清洗后的作者列表
     */
    private List<String> authors;
    
    /**
     * 清洗后的分类列表
     */
    private List<String> categories;
    
    /**
     * 标准化的URL
     */
    private String url;
    
    /**
     * 生成的向量
     */
    private float[] vector;
    
    /**
     * 处理时间戳
     */
    private LocalDateTime processedAt;
    
    /**
     * 数据质量评分（0-100）
     */
    private int qualityScore;
    
    /**
     * 检查是否完整
     */
    public boolean isComplete() {
        return id != null && !id.trim().isEmpty()
               && title != null && !title.trim().isEmpty()
               && abstractText != null && !abstractText.trim().isEmpty()
               && publishDate != null
               && vector != null && vector.length > 0;
    }
    
    /**
     * 获取用于向量化的组合文本
     */
    public String getCombinedText() {
        StringBuilder sb = new StringBuilder();
        if (title != null) {
            sb.append(title);
        }
        if (abstractText != null) {
            if (sb.length() > 0) sb.append(" ");
            sb.append(abstractText);
        }
        return sb.toString();
    }
    
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
        return authors != null && !authors.isEmpty() ? authors.get(0) : null;
    }
}