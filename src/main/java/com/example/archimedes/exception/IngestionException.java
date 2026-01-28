package com.example.archimedes.exception;

/**
 * 数据摄取相关异常
 */
public class IngestionException extends BusinessException {
    
    public IngestionException(String message) {
        super("INGESTION_ERROR", message);
    }
    
    public IngestionException(String message, Throwable cause) {
        super("INGESTION_ERROR", message, cause);
    }
}