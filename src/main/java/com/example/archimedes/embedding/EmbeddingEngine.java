package com.example.archimedes.embedding;

import java.util.List;

/**
 * 无状态、确定性的文本向量化引擎
 * MVP文档要求: Stateless & Deterministic
 */
public interface EmbeddingEngine {
    
    /**
     * 单文本向量化 - 主要供 Search Module 使用
     * @param text 待向量化文本，不能为null且长度>0
     * @return 语义向量数组
     * @throws InvalidInputException 输入为空或纯空白字符时
     */
    float[] embedSingle(String text);
    
    /**
     * 批量向量化 - 主要供 Ingestion Module 使用
     * @param texts 文本列表
     * @return 向量列表，与输入严格一一对应，处理失败时对应位置返回null
     */
    List<float[]> embedBatch(List<String> texts);
    
    /**
     * 获取向量维度
     * @return 向量维度数
     */
    int getDimension();
}