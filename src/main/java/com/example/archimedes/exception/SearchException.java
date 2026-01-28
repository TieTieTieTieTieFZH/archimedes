package com.example.archimedes.exception;

/**
 * 搜索相关异常
 */
public class SearchException extends BusinessException {
    
    public SearchException(String message) {
        super("SEARCH_ERROR", message);
    }
    
    public SearchException(String message, Throwable cause) {
        super("SEARCH_ERROR", message, cause);
    }
}