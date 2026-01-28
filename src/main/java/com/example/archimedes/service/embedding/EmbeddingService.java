package com.example.archimedes.service.embedding;

import com.example.archimedes.dto.request.VectorizeRequest;
import com.example.archimedes.dto.response.VectorizeResponse;

/**
 * 向量化服务接口 - 统一版本
 * 
 * 负责文本向量化，支持单条和批量处理
 */
public interface EmbeddingService {
    
    /**
     * 向量化文本
     * 
     * 支持单条和批量处理，自动根据请求类型选择优化策略
     * 
     * @param request 向量化请求
     * @return 向量化结果
     */
    VectorizeResponse vectorize(VectorizeRequest request);
    
    /**
     * 获取向量维度
     */
    int getVectorDimension();
    
    /**
     * 检查服务是否可用
     */
    boolean isAvailable();
    
    /**
     * 预热服务
     */
    void warmUp();
}