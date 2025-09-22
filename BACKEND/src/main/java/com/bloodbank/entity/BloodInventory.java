package com.bloodbank.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * BloodInventory entity representing available blood units in the bank
 */
@Slf4j
@Entity
@Table(name = "blood_inventory")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BloodInventory {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Blood group is required")
    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Invalid blood group format")
    @Column(name = "blood_group", nullable = false, unique = true, length = 3)
    private String bloodGroup;
    
    @Min(value = 0, message = "Units available cannot be negative")
    @Column(name = "units_available", nullable = false)
    private Integer unitsAvailable = 0;
    
    @Min(value = 0, message = "Minimum stock level cannot be negative")
    @Column(name = "minimum_stock", nullable = false)
    private Integer minimumStock = 5;
    
    @Min(value = 0, message = "Maximum capacity cannot be negative")
    @Column(name = "maximum_capacity", nullable = false)
    private Integer maximumCapacity = 100;
    
    @Column(name = "expiry_date")
    private LocalDateTime expiryDate;
    
    @Size(max = 255, message = "Notes must not exceed 255 characters")
    @Column(length = 255)
    private String notes;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Check if blood group is in critical shortage
     */
    public boolean isCriticalShortage() {
        return unitsAvailable <= minimumStock;
    }
    
    /**
     * Check if inventory is at maximum capacity
     */
    public boolean isAtMaxCapacity() {
        return unitsAvailable >= maximumCapacity;
    }
    
    /**
     * Add units to inventory
     */
    public void addUnits(int units) {
        if (units > 0 && (unitsAvailable + units) <= maximumCapacity) {
            this.unitsAvailable += units;
        }
    }
    
    /**
     * Remove units from inventory if available
     */
    public boolean removeUnits(int units) {
        if (units > 0 && unitsAvailable >= units) {
            this.unitsAvailable -= units;
            return true;
        }
        return false;
    }
    
    /**
     * Get stock status
     */
    public String getStockStatus() {
        if (unitsAvailable == 0) {
            return "OUT_OF_STOCK";
        } else if (isCriticalShortage()) {
            return "CRITICAL";
        } else if (unitsAvailable < (minimumStock * 2)) {
            return "LOW";
        } else {
            return "ADEQUATE";
        }
    }
}