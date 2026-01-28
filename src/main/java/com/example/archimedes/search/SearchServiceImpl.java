package com.example.archimedes.search;

import com.example.archimedes.dto.request.SearchRequest;
import com.example.archimedes.dto.response.SearchResponse;
import com.example.archimedes.service.search.SearchService;
import org.springframework.stereotype.Service;

@Service
public class SearchServiceImpl implements SearchService {
    
    @Override
    public SearchResponse search(SearchRequest request) {
        // TODO: 实现搜索逻辑
        return SearchResponse.empty(request.getPage(), request.getSize(), 0L);
    }
}
