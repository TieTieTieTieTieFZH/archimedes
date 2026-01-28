package com.example.archimedes.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import java.util.List;
import java.util.Collections;

/**
 * 向量化响应DTO - 统一版本
 * 
 * 支持单条和批量向量化结果
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VectorizeResponse {
    
    /**
     * 向量结果列表
     * 失败的位置为null
     */
    private List<float[]> vectors;
    
    /**
     * 失败的索引列表
     */
    @Builder.Default
    private List<Integer> failedIndices = Collections.emptyList();
    
    /**
     * 处理耗时（毫秒）
     */
    private long processingTimeMs;
    
    /**
     * 是否全部成功
     */
    public boolean isAllSuccess() {
        return failedIndices == null || failedIndices.isEmpty();
    }
    
    /**
     * 获取成功数量
     */
    public int getSuccessCount() {
        if (vectors == null) return 0;
        return (int) vectors.stream().filter(v -> v != null).count();
    }
    
    /**
     * 获取失败数量
     */
    public int getFailureCount() {
        return failedIndices != null ? failedIndices.size() : 0;
    }
    
    /**
     * 获取单条向量结果（用于单条请求）
     */
    public float[] getSingleVector() {
        if (vectors != null && vectors.size() == 1) {
            return vectors.get(0);
        }
        return null;
    }
    
    /**
     * 创建成功响应
     */
    public static VectorizeResponse success(List<float[]> vectors, long processingTime) {
        return VectorizeResponse.builder()
                .vectors(vectors)
                .failedIndices(Collections.emptyList())
                .processingTimeMs(processingTime)
                .build();
    }
    
    /**
     * 创建单条成功响应
     */
    public static VectorizeResponse singleSuccess(float[] vector, long processingTime) {
        return VectorizeResponse.builder()
                .vectors(List.of(vector))
                .failedIndices(Collections.emptyList())
                .processingTimeMs(processingTime)
                .build();
    }
    
    /**
     * 创建部分失败响应
     */
    public static VectorizeResponse withFailures(
            List<float[]> vectors, 
            List<Integer> failedIndices, 
            long processingTime) {
        
        return VectorizeResponse.builder()
                .vectors(vectors)
                .failedIndices(failedIndices)
                .processingTimeMs(processingTime)
                .build();
    }
}