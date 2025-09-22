package com.bloodbank.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Common DTOs for API responses
 */
public class CommonDTO {
    
    /**
     * Generic API response wrapper
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ApiResponse<T> {
        private boolean success;
        private String message;
        private T data;
        private String timestamp;
        
        public static <T> ApiResponse<T> success(T data) {
            return new ApiResponse<>(true, "Operation successful", data, 
                java.time.LocalDateTime.now().toString());
        }
        
        public static <T> ApiResponse<T> success(String message, T data) {
            return new ApiResponse<>(true, message, data, 
                java.time.LocalDateTime.now().toString());
        }
        
        public static <T> ApiResponse<T> error(String message) {
            return new ApiResponse<>(false, message, null, 
                java.time.LocalDateTime.now().toString());
        }
    }
    
    /**
     * DTO for dashboard statistics
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DashboardStats {
        private Long totalDonors;
        private Long eligibleDonors;
        private Long totalBloodUnits;
        private Long criticalShortages;
        private Long pendingRequests;
        private Long emergencyRequests;
        private Long todayRequests;
        private Long todayDonations;
    }
    
    /**
     * DTO for error responses
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ErrorResponse {
        private String error;
        private String message;
        private String path;
        private String timestamp;
        private int status;
    }
    
    /**
     * DTO for validation error responses
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ValidationErrorResponse {
        private String error;
        private String message;
        private java.util.Map<String, String> fieldErrors;
        private String timestamp;
        private int status;
    }
}