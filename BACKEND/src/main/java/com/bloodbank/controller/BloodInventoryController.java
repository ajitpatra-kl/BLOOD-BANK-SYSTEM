package com.bloodbank.controller;

import com.bloodbank.dto.BloodInventoryDTO;
import com.bloodbank.dto.CommonDTO;
import com.bloodbank.service.BloodInventoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for BloodInventory operations
 */
@RestController
@RequestMapping("/api/inventory")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class BloodInventoryController {
    
    private final BloodInventoryService bloodInventoryService;
    
    /**
     * Create a new blood inventory record
     */
    @PostMapping
    public ResponseEntity<CommonDTO.ApiResponse<BloodInventoryDTO.BloodInventoryResponse>> createBloodInventory(
            @Valid @RequestBody BloodInventoryDTO.BloodInventoryCreateRequest request) {
        try {
            log.info("Creating new blood inventory for blood group: {}", request.getBloodGroup());
            BloodInventoryDTO.BloodInventoryResponse response = bloodInventoryService.createBloodInventory(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CommonDTO.ApiResponse.success("Blood inventory created successfully", response));
        } catch (RuntimeException e) {
            log.error("Error creating blood inventory: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get blood inventory by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<BloodInventoryDTO.BloodInventoryResponse>> getBloodInventoryById(
            @PathVariable Long id) {
        try {
            log.info("Fetching blood inventory with ID: {}", id);
            return bloodInventoryService.getBloodInventoryById(id)
                    .map(inventory -> ResponseEntity.ok(CommonDTO.ApiResponse.success(inventory)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(CommonDTO.ApiResponse.error("Blood inventory not found with ID: " + id)));
        } catch (Exception e) {
            log.error("Error fetching blood inventory: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get blood inventory by blood group
     */
    @GetMapping("/blood-group/{bloodGroup}")
    public ResponseEntity<CommonDTO.ApiResponse<BloodInventoryDTO.BloodInventoryResponse>> getBloodInventoryByBloodGroup(
            @PathVariable String bloodGroup) {
        try {
            log.info("Fetching blood inventory for blood group: {}", bloodGroup);
            return bloodInventoryService.getBloodInventoryByBloodGroup(bloodGroup)
                    .map(inventory -> ResponseEntity.ok(CommonDTO.ApiResponse.success(inventory)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(CommonDTO.ApiResponse.error("Blood inventory not found for blood group: " + bloodGroup)));
        } catch (Exception e) {
            log.error("Error fetching blood inventory by blood group: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get all blood inventories
     */
    @GetMapping
    public ResponseEntity<CommonDTO.ApiResponse<List<BloodInventoryDTO.BloodInventorySummary>>> getAllBloodInventories() {
        try {
            log.info("Fetching all blood inventories");
            List<BloodInventoryDTO.BloodInventorySummary> inventories = bloodInventoryService.getAllBloodInventories();
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(inventories));
        } catch (Exception e) {
            log.error("Error fetching all blood inventories: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Update blood inventory
     */
    @PutMapping("/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<BloodInventoryDTO.BloodInventoryResponse>> updateBloodInventory(
            @PathVariable Long id,
            @Valid @RequestBody BloodInventoryDTO.BloodInventoryUpdateRequest request) {
        try {
            log.info("Updating blood inventory with ID: {}", id);
            BloodInventoryDTO.BloodInventoryResponse response = bloodInventoryService.updateBloodInventory(id, request);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success("Blood inventory updated successfully", response));
        } catch (RuntimeException e) {
            log.error("Error updating blood inventory: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Add units to blood inventory
     */
    @PostMapping("/{bloodGroup}/add-units")
    public ResponseEntity<CommonDTO.ApiResponse<BloodInventoryDTO.BloodInventoryResponse>> addUnits(
            @PathVariable String bloodGroup,
            @Valid @RequestBody BloodInventoryDTO.UnitsUpdateRequest request) {
        try {
            log.info("Adding {} units to blood group: {}", request.getUnits(), bloodGroup);
            BloodInventoryDTO.BloodInventoryResponse response = bloodInventoryService.addUnits(bloodGroup, request);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success("Units added successfully", response));
        } catch (RuntimeException e) {
            log.error("Error adding units: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Remove units from blood inventory
     */
    @PostMapping("/{bloodGroup}/remove-units")
    public ResponseEntity<CommonDTO.ApiResponse<BloodInventoryDTO.BloodInventoryResponse>> removeUnits(
            @PathVariable String bloodGroup,
            @Valid @RequestBody BloodInventoryDTO.UnitsUpdateRequest request) {
        try {
            log.info("Removing {} units from blood group: {}", request.getUnits(), bloodGroup);
            BloodInventoryDTO.BloodInventoryResponse response = bloodInventoryService.removeUnits(bloodGroup, request);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success("Units removed successfully", response));
        } catch (RuntimeException e) {
            log.error("Error removing units: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Delete blood inventory
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<Void>> deleteBloodInventory(@PathVariable Long id) {
        try {
            log.info("Deleting blood inventory with ID: {}", id);
            bloodInventoryService.deleteBloodInventory(id);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success("Blood inventory deleted successfully", null));
        } catch (RuntimeException e) {
            log.error("Error deleting blood inventory: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get critical shortages
     */
    @GetMapping("/critical-shortages")
    public ResponseEntity<CommonDTO.ApiResponse<List<BloodInventoryDTO.BloodInventorySummary>>> getCriticalShortages() {
        try {
            log.info("Fetching critical shortages");
            List<BloodInventoryDTO.BloodInventorySummary> shortages = bloodInventoryService.getCriticalShortages();
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(shortages));
        } catch (Exception e) {
            log.error("Error fetching critical shortages: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get low stock inventories
     */
    @GetMapping("/low-stock")
    public ResponseEntity<CommonDTO.ApiResponse<List<BloodInventoryDTO.BloodInventorySummary>>> getLowStock() {
        try {
            log.info("Fetching low stock inventories");
            List<BloodInventoryDTO.BloodInventorySummary> lowStock = bloodInventoryService.getLowStock();
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(lowStock));
        } catch (Exception e) {
            log.error("Error fetching low stock inventories: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get out of stock inventories
     */
    @GetMapping("/out-of-stock")
    public ResponseEntity<CommonDTO.ApiResponse<List<BloodInventoryDTO.BloodInventorySummary>>> getOutOfStock() {
        try {
            log.info("Fetching out of stock inventories");
            List<BloodInventoryDTO.BloodInventorySummary> outOfStock = bloodInventoryService.getOutOfStock();
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(outOfStock));
        } catch (Exception e) {
            log.error("Error fetching out of stock inventories: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Check if sufficient units are available
     */
    @GetMapping("/{bloodGroup}/check-availability")
    public ResponseEntity<CommonDTO.ApiResponse<Boolean>> checkAvailability(
            @PathVariable String bloodGroup,
            @RequestParam Integer requiredUnits) {
        try {
            log.info("Checking availability of {} units for blood group: {}", requiredUnits, bloodGroup);
            boolean available = bloodInventoryService.hasSufficientUnits(bloodGroup, requiredUnits);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(available));
        } catch (Exception e) {
            log.error("Error checking availability: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get blood group availability
     */
    @GetMapping("/availability")
    public ResponseEntity<CommonDTO.ApiResponse<List<BloodInventoryDTO.BloodGroupAvailability>>> getBloodGroupAvailability() {
        try {
            log.info("Fetching blood group availability");
            List<BloodInventoryDTO.BloodGroupAvailability> availability = bloodInventoryService.getBloodGroupAvailability();
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(availability));
        } catch (Exception e) {
            log.error("Error fetching blood group availability: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get inventory statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<CommonDTO.ApiResponse<BloodInventoryDTO.InventoryStats>> getInventoryStatistics() {
        try {
            log.info("Fetching inventory statistics");
            BloodInventoryDTO.InventoryStats stats = bloodInventoryService.getInventoryStatistics();
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(stats));
        } catch (Exception e) {
            log.error("Error fetching inventory statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Initialize blood groups
     */
    @PostMapping("/initialize")
    public ResponseEntity<CommonDTO.ApiResponse<Void>> initializeBloodGroups() {
        try {
            log.info("Initializing blood groups");
            bloodInventoryService.initializeBloodGroups();
            return ResponseEntity.ok(CommonDTO.ApiResponse.success("Blood groups initialized successfully", null));
        } catch (Exception e) {
            log.error("Error initializing blood groups: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
}