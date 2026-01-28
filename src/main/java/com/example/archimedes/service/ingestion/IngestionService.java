package com.example.archimedes.service.ingestion;

import com.example.archimedes.dto.internal.RawPaper;
import com.example.archimedes.dto.internal.IngestionSummary;
import java.util.List;

/**
 * 摄取服务接口 - 统一版本
 * 
 * 负责数据摄取和处理
 */
public interface IngestionService {
    
    /**
     * 处理原始论文数据
     * 
     * @param rawPapers 原始论文列表
     * @return 处理总结
     */
    IngestionSummary process(List<RawPaper> rawPapers);
    
    /**
     * 从文件摄取数据
     * 
     * @param filePath 文件路径
     * @return 处理总结
     */
    IngestionSummary ingestFromFile(String filePath);
}