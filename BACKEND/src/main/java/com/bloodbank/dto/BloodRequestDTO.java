package com.bloodbank.dto;

import com.bloodbank.entity.BloodRequest;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Objects for BloodRequest operations
 */
public class BloodRequestDTO {
    
    /**
     * DTO for creating blood requests
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BloodRequestCreateRequest {
        
        @NotBlank(message = "Requester name is required")
        @Size(min = 2, max = 100, message = "Requester name must be between 2 and 100 characters")
        private String requesterName;
        
        @NotBlank(message = "Contact email is required")
        @Email(message = "Invalid email format")
        private String contactEmail;
        
        @NotBlank(message = "Contact phone is required")
        @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
        private String contactPhone;
        
        @NotBlank(message = "Blood group is required")
        @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Invalid blood group format")
        private String bloodGroup;
        
        @NotNull(message = "Units requested is required")
        @Min(value = 1, message = "At least 1 unit must be requested")
        @Max(value = 10, message = "Maximum 10 units can be requested at once")
        private Integer unitsRequested;
        
        @NotNull(message = "Urgency level is required")
        private BloodRequest.UrgencyLevel urgencyLevel;
        
        @NotBlank(message = "Hospital name is required")
        @Size(max = 150, message = "Hospital name must not exceed 150 characters")
        private String hospitalName;
        
        @NotBlank(message = "Patient name is required")
        @Size(max = 100, message = "Patient name must not exceed 100 characters")
        private String patientName;
        
        @Size(max = 500, message = "Medical reason must not exceed 500 characters")
        private String medicalReason;
    }
    
    /**
     * DTO for updating blood request status
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BloodRequestStatusUpdate {
        
        @NotNull(message = "Status is required")
        private BloodRequest.RequestStatus status;
        
        @Size(max = 500, message = "Admin notes must not exceed 500 characters")
        private String adminNotes;
        
        @NotBlank(message = "Processed by is required")
        private String processedBy;
    }
    
    /**
     * DTO for blood request response
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BloodRequestResponse {
        private Long id;
        private String requesterName;
        private String contactEmail;
        private String contactPhone;
        private String bloodGroup;
        private Integer unitsRequested;
        private BloodRequest.UrgencyLevel urgencyLevel;
        private String urgencyLevelDisplay;
        private String hospitalName;
        private String patientName;
        private String medicalReason;
        private BloodRequest.RequestStatus status;
        private String statusDisplay;
        private String adminNotes;
        private String processedBy;
        private LocalDateTime processedAt;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
    
    /**
     * DTO for blood request summary (for lists)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BloodRequestSummary {
        private Long id;
        private String requesterName;
        private String bloodGroup;
        private Integer unitsRequested;
        private BloodRequest.UrgencyLevel urgencyLevel;
        private String urgencyLevelDisplay;
        private String hospitalName;
        private String patientName;
        private BloodRequest.RequestStatus status;
        private String statusDisplay;
        private LocalDateTime createdAt;
    }
    
    /**
     * DTO for request statistics
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RequestStats {
        private Long totalRequests;
        private Long pendingRequests;
        private Long approvedRequests;
        private Long rejectedRequests;
        private Long emergencyRequests;
        private Long urgentRequests;
    }
    
    /**
     * DTO for blood group request statistics
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BloodGroupRequestStats {
        private String bloodGroup;
        private Long totalRequests;
        private Long totalUnitsRequested;
        private Long pendingRequests;
        private Long pendingUnits;
    }
}