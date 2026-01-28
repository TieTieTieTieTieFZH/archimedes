package com.example.archimedes.exception;

/**
 * 向量化相关异常
 */
public class EmbeddingException extends BusinessException {
    
    public EmbeddingException(String message) {
        super("EMBEDDING_ERROR", message);
    }
    
    public EmbeddingException(String message, Throwable cause) {
        super("EMBEDDING_ERROR", message, cause);
    }
}