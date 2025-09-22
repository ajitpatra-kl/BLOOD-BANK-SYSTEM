package com.bloodbank.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bloodbank.entity.BloodInventory;

/**
 * Repository interface for BloodInventory entity
 */
@Repository
public interface BloodInventoryRepository extends JpaRepository<BloodInventory, Long> {
    
    /**
     * Find blood inventory by blood group
     */
    Optional<BloodInventory> findByBloodGroup(String bloodGroup);
    
    /**
     * Find all inventories with critical shortage
     */
    @Query("SELECT bi FROM BloodInventory bi WHERE bi.unitsAvailable <= bi.minimumStock")
    List<BloodInventory> findCriticalShortages();
    
    /**
     * Find all inventories with low stock
     */
    @Query("SELECT bi FROM BloodInventory bi WHERE bi.unitsAvailable <= (bi.minimumStock * 2) AND bi.unitsAvailable > bi.minimumStock")
    List<BloodInventory> findLowStock();
    
    /**
     * Find inventories with adequate stock
     */
    @Query("SELECT bi FROM BloodInventory bi WHERE bi.unitsAvailable > (bi.minimumStock * 2)")
    List<BloodInventory> findAdequateStock();
    
    /**
     * Find inventories at maximum capacity
     */
    @Query("SELECT bi FROM BloodInventory bi WHERE bi.unitsAvailable >= bi.maximumCapacity")
    List<BloodInventory> findAtMaxCapacity();
    
    /**
     * Get total units available across all blood groups
     */
    @Query("SELECT SUM(bi.unitsAvailable) FROM BloodInventory bi")
    Long getTotalUnitsAvailable();
    
    /**
     * Get units available for specific blood group
     */
    @Query("SELECT bi.unitsAvailable FROM BloodInventory bi WHERE bi.bloodGroup = :bloodGroup")
    Integer getUnitsAvailableByBloodGroup(String bloodGroup);
    
    /**
     * Check if sufficient units are available for blood group
     */
    @Query("SELECT CASE WHEN bi.unitsAvailable >= :requiredUnits THEN true ELSE false END " +
           "FROM BloodInventory bi WHERE bi.bloodGroup = :bloodGroup")
    Boolean hasSufficientUnits(String bloodGroup, Integer requiredUnits);
    
    /**
     * Find all blood groups with zero units
     */
    @Query("SELECT bi FROM BloodInventory bi WHERE bi.unitsAvailable = 0")
    List<BloodInventory> findOutOfStock();
    
    /**
     * Get inventory summary statistics
     */
    @Query("SELECT bi.bloodGroup, bi.unitsAvailable, " +
           "CASE WHEN bi.unitsAvailable = 0 THEN 'OUT_OF_STOCK' " +
           "     WHEN bi.unitsAvailable <= bi.minimumStock THEN 'CRITICAL' " +
           "     WHEN bi.unitsAvailable <= (bi.minimumStock * 2) THEN 'LOW' " +
           "     ELSE 'ADEQUATE' END as status " +
           "FROM BloodInventory bi ORDER BY bi.bloodGroup")
    List<Object[]> getInventorySummary();
    
    /**
     * Check if blood group exists in inventory
     */
    boolean existsByBloodGroup(String bloodGroup);
}