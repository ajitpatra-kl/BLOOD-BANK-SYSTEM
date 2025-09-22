package com.bloodbank.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Data Transfer Objects for BloodInventory operations
 */
public class BloodInventoryDTO {
    
    /**
     * DTO for creating blood inventory records
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BloodInventoryCreateRequest {
        
        @NotBlank(message = "Blood group is required")
        @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Invalid blood group format")
        private String bloodGroup;
        
        @NotNull(message = "Initial units are required")
        @Min(value = 0, message = "Units available cannot be negative")
        private Integer unitsAvailable;
        
        @Min(value = 0, message = "Minimum stock level cannot be negative")
        private Integer minimumStock = 5;
        
        @Min(value = 0, message = "Maximum capacity cannot be negative")
        private Integer maximumCapacity = 100;
        
        private LocalDateTime expiryDate;
        
        @Size(max = 255, message = "Notes must not exceed 255 characters")
        private String notes;
    }
    
    /**
     * DTO for updating blood inventory
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BloodInventoryUpdateRequest {
        
        @Min(value = 0, message = "Units available cannot be negative")
        private Integer unitsAvailable;
        
        @Min(value = 0, message = "Minimum stock level cannot be negative")
        private Integer minimumStock;
        
        @Min(value = 0, message = "Maximum capacity cannot be negative")
        private Integer maximumCapacity;
        
        private LocalDateTime expiryDate;
        
        @Size(max = 255, message = "Notes must not exceed 255 characters")
        private String notes;
    }
    
    /**
     * DTO for adding/removing units
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class UnitsUpdateRequest {
        
        @NotNull(message = "Units count is required")
        @Min(value = 1, message = "Units must be at least 1")
        @Max(value = 50, message = "Cannot add/remove more than 50 units at once")
        private Integer units;
        
        @Size(max = 255, message = "Notes must not exceed 255 characters")
        private String notes;
    }
    
    /**
     * DTO for blood inventory response
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BloodInventoryResponse {
        private Long id;
        private String bloodGroup;
        private Integer unitsAvailable;
        private Integer minimumStock;
        private Integer maximumCapacity;
        private LocalDateTime expiryDate;
        private String notes;
        private String stockStatus;
        private Boolean isCriticalShortage;
        private Boolean isAtMaxCapacity;
        private LocalDateTime createdAt;
        private LocalDateTime updatedAt;
    }
    
    /**
     * DTO for blood inventory summary
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BloodInventorySummary {
        private Long id;
        private String bloodGroup;
        private Integer unitsAvailable;
        private String stockStatus;
        private Boolean isCriticalShortage;
    }
    
    /**
     * DTO for inventory statistics
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class InventoryStats {
        private Long totalBloodGroups;
        private Long totalUnitsAvailable;
        private Long criticalShortageCount;
        private Long outOfStockCount;
        private Long adequateStockCount;
    }
    
    /**
     * DTO for blood group availability
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BloodGroupAvailability {
        private String bloodGroup;
        private Integer unitsAvailable;
        private String status;
        private Boolean available;
    }
}