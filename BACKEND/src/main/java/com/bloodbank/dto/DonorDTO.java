package com.bloodbank.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Data Transfer Objects for Donor operations
 */
public class DonorDTO {
    
    /**
     * DTO for donor registration/creation requests
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DonorCreateRequest {
        
        @NotBlank(message = "Name is required")
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        private String name;
        
        @NotBlank(message = "Email is required")
        @Email(message = "Invalid email format")
        private String email;
        
        @NotBlank(message = "Phone number is required")
        @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
        private String phone;
        
        @NotBlank(message = "Blood group is required")
        @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Invalid blood group format")
        private String bloodGroup;
        
        @Past(message = "Last donation date cannot be in the future")
        private LocalDate lastDonationDate;
        
        @NotNull(message = "Age is required")
        @Min(value = 18, message = "Donor must be at least 18 years old")
        @Max(value = 65, message = "Donor must be at most 65 years old")
        private Integer age;
        
        @NotNull(message = "Weight is required")
        @DecimalMin(value = "50.0", message = "Weight must be at least 50 kg")
        private Double weight;
        
        @NotBlank(message = "Address is required")
        @Size(max = 255, message = "Address must not exceed 255 characters")
        private String address;
    }
    
    /**
     * DTO for donor update requests
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DonorUpdateRequest {
        
        @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
        private String name;
        
        @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
        private String phone;
        
        @Past(message = "Last donation date cannot be in the future")
        private LocalDate lastDonationDate;
        
        @Min(value = 18, message = "Donor must be at least 18 years old")
        @Max(value = 65, message = "Donor must be at most 65 years old")
        private Integer age;
        
        @DecimalMin(value = "50.0", message = "Weight must be at least 50 kg")
        private Double weight;
        
        @Size(max = 255, message = "Address must not exceed 255 characters")
        private String address;
        
        private Boolean isEligible;
    }
    
    /**
     * DTO for donor response
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DonorResponse {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String bloodGroup;
        private LocalDate lastDonationDate;
        private Integer age;
        private Double weight;
        private String address;
        private Boolean isEligible;
        private Boolean canDonate;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
    
    /**
     * DTO for donor summary (for lists)
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DonorSummary {
        private Long id;
        private String name;
        private String email;
        private String phone;
        private String bloodGroup;
        private LocalDate lastDonationDate;
        private Boolean isEligible;
        private Boolean canDonate;
    }
    
    /**
     * DTO for donor statistics
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DonorStats {
        private String bloodGroup;
        private Long totalDonors;
        private Long eligibleDonors;
        private Long availableDonors;
    }
}