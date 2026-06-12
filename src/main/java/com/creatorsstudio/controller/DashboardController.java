package com.creatorsstudio.controller;

import com.creatorsstudio.exception.ApiResponse;
import com.creatorsstudio.dto.response.DashboardResponse;
import com.creatorsstudio.service.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    public ResponseEntity<ApiResponse<DashboardResponse>> getDashboardSummary() {
        DashboardResponse summary = dashboardService.getDashboardSummary();
        return ResponseEntity.ok(new ApiResponse<>(true, "Dashboard summary retrieved successfully", summary));
    }
}
