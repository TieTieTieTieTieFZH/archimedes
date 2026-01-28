package com.example.archimedes.dto.request;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import java.util.List;

/**
 * 向量化请求DTO - 统一版本
 * 
 * 支持单条和批量向量化，简化API设计
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VectorizeRequest {
    
    /**
     * 待向量化的文本列表
     * 单条请求时只包含一个元素
     */
    @NotNull(message = "文本列表不能为空")
    @NotEmpty(message = "文本列表不能为空")
    @Size(max = 500, message = "单次请求不能超过500条文本")
    private List<String> texts;
    
    /**
     * 创建单条文本请求
     */
    public static VectorizeRequest single(String text) {
        return new VectorizeRequest(List.of(text));
    }
    
    /**
     * 创建批量文本请求
     */
    public static VectorizeRequest batch(List<String> texts) {
        return new VectorizeRequest(texts);
    }
    
    /**
     * 是否为单条请求
     */
    public boolean isSingle() {
        return texts != null && texts.size() == 1;
    }
    
    /**
     * 获取文本数量
     */
    public int getCount() {
        return texts != null ? texts.size() : 0;
    }
}