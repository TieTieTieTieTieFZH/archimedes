package com.example.archimedes.search.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
@Tag(name = "测试接口", description = "用于测试的API接口")
public class ControllerTest {

    @GetMapping("/hello")
    @Operation(summary = "问候接口", description = "返回问候信息")
    public String hello() {
        return "Hello from Archimedes!";
    }
}