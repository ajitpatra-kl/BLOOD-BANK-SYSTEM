package com.bloodbank.controller;

import com.bloodbank.dto.CommonDTO;
import com.bloodbank.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST Controller for Dashboard operations and statistics
 */
@RestController
@RequestMapping("/api/dashboard")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class DashboardController {
    
    private final DashboardService dashboardService;
    
    /**
     * Get dashboard statistics
     */
    @GetMapping("/stats")
    public ResponseEntity<CommonDTO.ApiResponse<CommonDTO.DashboardStats>> getDashboardStats() {
        try {
            log.info("Fetching dashboard statistics");
            CommonDTO.DashboardStats stats = dashboardService.getDashboardStats();
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(stats));
        } catch (Exception e) {
            log.error("Error fetching dashboard statistics: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get system health status
     */
    @GetMapping("/health")
    public ResponseEntity<CommonDTO.ApiResponse<String>> getSystemHealthStatus() {
        try {
            log.info("Checking system health status");
            String healthStatus = dashboardService.getSystemHealthStatus();
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(healthStatus));
        } catch (Exception e) {
            log.error("Error checking system health: {}", e.getMessage());
            return ResponseEntity.status(500)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
}