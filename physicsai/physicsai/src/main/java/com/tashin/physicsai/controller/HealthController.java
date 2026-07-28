package com.tashin.physicsai.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.tashin.physicsai.dto.response.ApiResponse;

@RestController
public class HealthController {

    @GetMapping("/")
    public ApiResponse<String> health() {

        return ApiResponse.<String>builder()
                .success(true)
                .message("Physics AI Backend Running")
                .data("Version 1.0")
                .build();
    }
}
