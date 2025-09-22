package com.bloodbank.service;

import com.bloodbank.dto.BloodInventoryDTO;
import com.bloodbank.entity.BloodInventory;
import com.bloodbank.repository.BloodInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for BloodInventory operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BloodInventoryService {
    
    private final BloodInventoryRepository bloodInventoryRepository;
    
    /**
     * Create a new blood inventory record
     */
    public BloodInventoryDTO.BloodInventoryResponse createBloodInventory(BloodInventoryDTO.BloodInventoryCreateRequest request) {
        log.info("Creating new blood inventory for blood group: {}", request.getBloodGroup());
        
        // Check if blood group already exists
        if (bloodInventoryRepository.existsByBloodGroup(request.getBloodGroup())) {
            throw new RuntimeException("Blood inventory for blood group " + request.getBloodGroup() + " already exists");
        }
        
        BloodInventory inventory = new BloodInventory();
        BeanUtils.copyProperties(request, inventory);
        
        BloodInventory savedInventory = bloodInventoryRepository.save(inventory);
        log.info("Successfully created blood inventory with ID: {}", savedInventory.getId());
        
        return convertToResponse(savedInventory);
    }
    
    /**
     * Get blood inventory by ID
     */
    @Transactional(readOnly = true)
    public Optional<BloodInventoryDTO.BloodInventoryResponse> getBloodInventoryById(Long id) {
        log.info("Fetching blood inventory with ID: {}", id);
        return bloodInventoryRepository.findById(id)
                .map(this::convertToResponse);
    }
    
    /**
     * Get blood inventory by blood group
     */
    @Transactional(readOnly = true)
    public Optional<BloodInventoryDTO.BloodInventoryResponse> getBloodInventoryByBloodGroup(String bloodGroup) {
        log.info("Fetching blood inventory for blood group: {}", bloodGroup);
        return bloodInventoryRepository.findByBloodGroup(bloodGroup)
                .map(this::convertToResponse);
    }
    
    /**
     * Get all blood inventories
     */
    @Transactional(readOnly = true)
    public List<BloodInventoryDTO.BloodInventorySummary> getAllBloodInventories() {
        log.info("Fetching all blood inventories");
        return bloodInventoryRepository.findAll().stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());
    }
    
    /**
     * Update blood inventory
     */
    public BloodInventoryDTO.BloodInventoryResponse updateBloodInventory(Long id, BloodInventoryDTO.BloodInventoryUpdateRequest request) {
        log.info("Updating blood inventory with ID: {}", id);
        
        BloodInventory inventory = bloodInventoryRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blood inventory not found with ID: " + id));
        
        // Update only non-null fields
        if (request.getUnitsAvailable() != null) {
            inventory.setUnitsAvailable(request.getUnitsAvailable());
        }
        if (request.getMinimumStock() != null) {
            inventory.setMinimumStock(request.getMinimumStock());
        }
        if (request.getMaximumCapacity() != null) {
            inventory.setMaximumCapacity(request.getMaximumCapacity());
        }
        if (request.getExpiryDate() != null) {
            inventory.setExpiryDate(request.getExpiryDate());
        }
        if (request.getNotes() != null) {
            inventory.setNotes(request.getNotes());
        }
        
        BloodInventory updatedInventory = bloodInventoryRepository.save(inventory);
        log.info("Successfully updated blood inventory with ID: {}", id);
        
        return convertToResponse(updatedInventory);
    }
    
    /**
     * Add units to blood inventory
     */
    public BloodInventoryDTO.BloodInventoryResponse addUnits(String bloodGroup, BloodInventoryDTO.UnitsUpdateRequest request) {
        log.info("Adding {} units to blood group: {}", request.getUnits(), bloodGroup);
        
        BloodInventory inventory = bloodInventoryRepository.findByBloodGroup(bloodGroup)
                .orElseThrow(() -> new RuntimeException("Blood inventory not found for blood group: " + bloodGroup));
        
        int newUnits = inventory.getUnitsAvailable() + request.getUnits();
        if (newUnits > inventory.getMaximumCapacity()) {
            throw new RuntimeException("Adding units would exceed maximum capacity of " + inventory.getMaximumCapacity());
        }
        
        inventory.setUnitsAvailable(newUnits);
        if (request.getNotes() != null) {
            inventory.setNotes(request.getNotes());
        }
        
        BloodInventory updatedInventory = bloodInventoryRepository.save(inventory);
        log.info("Successfully added {} units to blood group: {}", request.getUnits(), bloodGroup);
        
        return convertToResponse(updatedInventory);
    }
    
    /**
     * Remove units from blood inventory
     */
    public BloodInventoryDTO.BloodInventoryResponse removeUnits(String bloodGroup, BloodInventoryDTO.UnitsUpdateRequest request) {
        log.info("Removing {} units from blood group: {}", request.getUnits(), bloodGroup);
        
        BloodInventory inventory = bloodInventoryRepository.findByBloodGroup(bloodGroup)
                .orElseThrow(() -> new RuntimeException("Blood inventory not found for blood group: " + bloodGroup));
        
        if (inventory.getUnitsAvailable() < request.getUnits()) {
            throw new RuntimeException("Insufficient units available. Current: " + inventory.getUnitsAvailable() + 
                                     ", Requested: " + request.getUnits());
        }
        
        inventory.setUnitsAvailable(inventory.getUnitsAvailable() - request.getUnits());
        if (request.getNotes() != null) {
            inventory.setNotes(request.getNotes());
        }
        
        BloodInventory updatedInventory = bloodInventoryRepository.save(inventory);
        log.info("Successfully removed {} units from blood group: {}", request.getUnits(), bloodGroup);
        
        return convertToResponse(updatedInventory);
    }
    
    /**
     * Delete blood inventory
     */
    public void deleteBloodInventory(Long id) {
        log.info("Deleting blood inventory with ID: {}", id);
        
        if (!bloodInventoryRepository.existsById(id)) {
            throw new RuntimeException("Blood inventory not found with ID: " + id);
        }
        
        bloodInventoryRepository.deleteById(id);
        log.info("Successfully deleted blood inventory with ID: {}", id);
    }
    
    /**
     * Get critical shortages
     */
    @Transactional(readOnly = true)
    public List<BloodInventoryDTO.BloodInventorySummary> getCriticalShortages() {
        log.info("Fetching critical shortages");
        return bloodInventoryRepository.findCriticalShortages().stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());
    }
    
    /**
     * Get low stock inventories
     */
    @Transactional(readOnly = true)
    public List<BloodInventoryDTO.BloodInventorySummary> getLowStock() {
        log.info("Fetching low stock inventories");
        return bloodInventoryRepository.findLowStock().stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());
    }
    
    /**
     * Get out of stock inventories
     */
    @Transactional(readOnly = true)
    public List<BloodInventoryDTO.BloodInventorySummary> getOutOfStock() {
        log.info("Fetching out of stock inventories");
        return bloodInventoryRepository.findOutOfStock().stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());
    }
    
    /**
     * Check if sufficient units are available
     */
    @Transactional(readOnly = true)
    public boolean hasSufficientUnits(String bloodGroup, Integer requiredUnits) {
        log.info("Checking if {} units are available for blood group: {}", requiredUnits, bloodGroup);
        Boolean result = bloodInventoryRepository.hasSufficientUnits(bloodGroup, requiredUnits);
        return result != null ? result : false;
    }
    
    /**
     * Get blood group availability
     */
    @Transactional(readOnly = true)
    public List<BloodInventoryDTO.BloodGroupAvailability> getBloodGroupAvailability() {
        log.info("Fetching blood group availability");
        return bloodInventoryRepository.findAll().stream()
                .map(this::convertToAvailability)
                .collect(Collectors.toList());
    }
    
    /**
     * Get inventory statistics
     */
    @Transactional(readOnly = true)
    public BloodInventoryDTO.InventoryStats getInventoryStatistics() {
        log.info("Fetching inventory statistics");
        
        List<BloodInventory> allInventories = bloodInventoryRepository.findAll();
        Long totalBloodGroups = (long) allInventories.size();
        Long totalUnitsAvailable = bloodInventoryRepository.getTotalUnitsAvailable();
        Long criticalShortageCount = (long) bloodInventoryRepository.findCriticalShortages().size();
        Long outOfStockCount = (long) bloodInventoryRepository.findOutOfStock().size();
        Long adequateStockCount = (long) bloodInventoryRepository.findAdequateStock().size();
        
        return new BloodInventoryDTO.InventoryStats(
            totalBloodGroups,
            totalUnitsAvailable != null ? totalUnitsAvailable : 0L,
            criticalShortageCount,
            outOfStockCount,
            adequateStockCount
        );
    }
    
    /**
     * Initialize blood inventory for all blood groups
     */
    public void initializeBloodGroups() {
        log.info("Initializing blood inventory for all blood groups");
        
        String[] bloodGroups = {"A+", "A-", "B+", "B-", "AB+", "AB-", "O+", "O-"};
        
        for (String bloodGroup : bloodGroups) {
            if (!bloodInventoryRepository.existsByBloodGroup(bloodGroup)) {
                BloodInventory inventory = new BloodInventory();
                inventory.setBloodGroup(bloodGroup);
                inventory.setUnitsAvailable(0);
                inventory.setMinimumStock(5);
                inventory.setMaximumCapacity(100);
                inventory.setNotes("Initialized automatically");
                
                bloodInventoryRepository.save(inventory);
                log.info("Initialized blood inventory for blood group: {}", bloodGroup);
            }
        }
    }
    
    /**
     * Convert BloodInventory entity to BloodInventoryResponse DTO
     */
    private BloodInventoryDTO.BloodInventoryResponse convertToResponse(BloodInventory inventory) {
        BloodInventoryDTO.BloodInventoryResponse response = new BloodInventoryDTO.BloodInventoryResponse();
        BeanUtils.copyProperties(inventory, response);
        response.setStockStatus(inventory.getStockStatus());
        response.setIsCriticalShortage(inventory.isCriticalShortage());
        response.setIsAtMaxCapacity(inventory.isAtMaxCapacity());
        return response;
    }
    
    /**
     * Convert BloodInventory entity to BloodInventorySummary DTO
     */
    private BloodInventoryDTO.BloodInventorySummary convertToSummary(BloodInventory inventory) {
        BloodInventoryDTO.BloodInventorySummary summary = new BloodInventoryDTO.BloodInventorySummary();
        summary.setId(inventory.getId());
        summary.setBloodGroup(inventory.getBloodGroup());
        summary.setUnitsAvailable(inventory.getUnitsAvailable());
        summary.setStockStatus(inventory.getStockStatus());
        summary.setIsCriticalShortage(inventory.isCriticalShortage());
        return summary;
    }
    
    /**
     * Convert BloodInventory entity to BloodGroupAvailability DTO
     */
    private BloodInventoryDTO.BloodGroupAvailability convertToAvailability(BloodInventory inventory) {
        BloodInventoryDTO.BloodGroupAvailability availability = new BloodInventoryDTO.BloodGroupAvailability();
        availability.setBloodGroup(inventory.getBloodGroup());
        availability.setUnitsAvailable(inventory.getUnitsAvailable());
        availability.setStatus(inventory.getStockStatus());
        availability.setAvailable(inventory.getUnitsAvailable() > 0);
        return availability;
    }
}