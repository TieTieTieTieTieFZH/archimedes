package com.example.archimedes.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "archimedes.search")
public class SearchConfig {
    
    /** MVP 文档要求的最大查询长度 */
    private int maxQueryLength = 200;
    
    /** MVP 文档要求的最大页面大小 */
    private int maxPageSize = 100;
    
    /** 摘要字段最大显示长度 */
    private int maxAbstractLength = 300;
    
    /** 向量搜索的相似度阈值 */
    private double vectorSearchThreshold = 0.7;
    
    /** ES 查询超时时间（秒） */
    private int queryTimeoutSeconds = 10;
    
    // getters and setters...
}