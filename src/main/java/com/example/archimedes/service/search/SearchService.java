package com.example.archimedes.service.search;

import com.example.archimedes.dto.request.SearchRequest;
import com.example.archimedes.dto.response.SearchResponse;

/**
 * 搜索服务接口 - 统一版本
 * 
 * 负责论文搜索，支持关键词和语义搜索
 */
public interface SearchService {
    
    /**
     * 执行搜索
     * 
     * @param request 搜索请求
     * @return 搜索结果
     */
    SearchResponse search(SearchRequest request);
}