package com.example.archimedes.search.controller;


import com.example.archimedes.dto.request.SearchRequest;
import com.example.archimedes.dto.response.SearchResponse;
import com.example.archimedes.service.search.SearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.beans.factory.annotation.Autowired;

@RestController
@RequestMapping("/search")
@Tag(name = "搜索接口", description = "论文搜索API")
public class SearchController {
    
    @Autowired
    private SearchService searchService;
    
    @PostMapping
    @Operation(summary = "搜索论文")
    public SearchResponse search(@Valid @RequestBody SearchRequest request) {
        return searchService.search(request);
    }
}
