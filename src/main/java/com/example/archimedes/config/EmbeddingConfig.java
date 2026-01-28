package com.example.archimedes.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.time.Duration;

@Configuration
@ConfigurationProperties(prefix = "archimedes.embedding")
public class EmbeddingConfig {
    
    /** 批处理大小，与 Ingestion 模块的 batch.size 保持一致 */
    private int batchSize = 32;
    
    /** 向量维度 */
    private int dimension = 768;
    
    /** 请求超时时间 */
    private Duration timeout = Duration.ofSeconds(30);
    
    /** 输入为空时的处理策略: EXCEPTION | ZERO_VECTOR */
    private EmptyInputStrategy emptyInputStrategy = EmptyInputStrategy.EXCEPTION;
    
    /** 最大文本长度 */
    private int maxTextLength = 512;
    
    // getters and setters...
    
    public enum EmptyInputStrategy {
        EXCEPTION,    // 抛出异常
        ZERO_VECTOR   // 返回零向量
    }
}