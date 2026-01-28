package com.example.archimedes.ingestion;

import com.example.archimedes.dto.internal.IngestionSummary;

public interface IngestionPipeline {
    /**
     * 对应 PDF Page 3: 触发摄入任务的入口
     */
    IngestionSummary triggerIngestion(IngestionSource source);
}