package com.example.archimedes.utils;

import com.example.archimedes.dto.internal.ProcessedPaper;
import com.example.archimedes.dto.response.PaperVO;
import com.example.archimedes.pojo.entity.Paper;
import lombok.experimental.UtilityClass;
import java.util.List;
import java.util.Arrays;

/**
 * DTO转换工具类
 * 负责各模块间的数据对象转换，保持模块边界清晰
 */
@UtilityClass
public class DTOConverter {
    
    /**
     * ProcessedPaper → Paper Entity 转换
     * Ingestion模块的输出转换为持久层实体
     */
    public static Paper toEntity(ProcessedPaper processed) {
        return new Paper()
            .setId(processed.getId())
            .setTitle(processed.getTitle())
            .setAbstractText(processed.getAbstractText())
            .setPublishedDate(processed.getPublishDate())
            .setCategories(String.join(",", processed.getCategories() != null ? processed.getCategories() : List.of()))
            .setUrl(processed.getUrl())
            .setEmbedding(processed.getVector());
    }
    
    /**
     * Paper Entity → PaperVO 转换
     * 数据库实体转换为前端视图对象，包含摘要截断逻辑
     */
    public static PaperVO toPaperVO(Paper paper, Double score, int maxAbstractLength) {
        String abstractText = paper.getAbstractText();
        boolean truncated = false;
        
        // 摘要截断处理
        if (abstractText != null && abstractText.length() > maxAbstractLength) {
            abstractText = abstractText.substring(0, maxAbstractLength) + "...";
            truncated = true;
        }
        
        // 解析分类和作者
        List<String> categories = paper.getCategories() != null 
            ? Arrays.asList(paper.getCategories().split(",")) 
            : List.of();
        
        return PaperVO.builder()
            .id(paper.getId())
            .title(paper.getTitle())
            .abstractText(abstractText)
            .publishDate(paper.getPublishedDate())
            .categories(categories)
            .url(paper.getUrl())
            .relevanceScore(score)
            .abstractTruncated(truncated)
            .build();
    }
    
    /**
     * Paper Entity → PaperVO 转换（使用默认截断长度）
     */
    public static PaperVO toPaperVO(Paper paper, Double score) {
        return toPaperVO(paper, score, 300);
    }
    
    /**
     * Paper Entity → PaperVO 转换（无评分）
     */
    public static PaperVO toPaperVO(Paper paper) {
        return toPaperVO(paper, null, 300);
    }
}