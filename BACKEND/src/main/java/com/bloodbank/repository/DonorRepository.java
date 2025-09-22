package com.bloodbank.repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.bloodbank.entity.Donor;

/**
 * Repository interface for Donor entity
 */
@Repository
public interface DonorRepository extends JpaRepository<Donor, Long> {
    
    /**
     * Find donor by email
     */
    Optional<Donor> findByEmail(String email);
    
    /**
     * Find donors by blood group
     */
    List<Donor> findByBloodGroup(String bloodGroup);
    
    /**
     * Find eligible donors by blood group
     */
    List<Donor> findByBloodGroupAndIsEligibleTrue(String bloodGroup);
    
    /**
     * Find donors who can donate (eligible and past 56 days since last donation)
     */
    @Query("SELECT d FROM Donor d WHERE d.isEligible = true AND " +
           "(d.lastDonationDate IS NULL OR d.lastDonationDate < :cutoffDate)")
    List<Donor> findEligibleDonors(@Param("cutoffDate") LocalDate cutoffDate);
    
    /**
     * Find eligible donors by blood group who can donate
     */
    @Query("SELECT d FROM Donor d WHERE d.bloodGroup = :bloodGroup AND d.isEligible = true AND " +
           "(d.lastDonationDate IS NULL OR d.lastDonationDate < :cutoffDate)")
    List<Donor> findEligibleDonorsByBloodGroup(@Param("bloodGroup") String bloodGroup, 
                                               @Param("cutoffDate") LocalDate cutoffDate);
    
    /**
     * Count donors by blood group
     */
    long countByBloodGroup(String bloodGroup);
    
    /**
     * Count eligible donors
     */
    long countByIsEligibleTrue();
    
    /**
     * Find donors by name containing (case insensitive)
     */
    List<Donor> findByNameContainingIgnoreCase(String name);
    
    /**
     * Find donors by phone number
     */
    Optional<Donor> findByPhone(String phone);
    
    /**
     * Check if email exists
     */
    boolean existsByEmail(String email);
    
    /**
     * Check if phone exists
     */
    boolean existsByPhone(String phone);
    
    /**
     * Find recent donors (donated within last 30 days)
     */
    @Query("SELECT d FROM Donor d WHERE d.lastDonationDate >= :date ORDER BY d.lastDonationDate DESC")
    List<Donor> findRecentDonors(@Param("date") LocalDate date);
    
    /**
     * Get donor statistics by blood group
     */
    @Query("SELECT d.bloodGroup, COUNT(d) FROM Donor d GROUP BY d.bloodGroup")
    List<Object[]> getDonorStatsByBloodGroup();
}