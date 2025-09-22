package com.bloodbank.repository;

import com.bloodbank.entity.BloodRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Repository interface for BloodRequest entity
 */
@Repository
public interface BloodRequestRepository extends JpaRepository<BloodRequest, Long> {
    
    /**
     * Find requests by status
     */
    List<BloodRequest> findByStatus(BloodRequest.RequestStatus status);
    
    /**
     * Find requests by blood group
     */
    List<BloodRequest> findByBloodGroup(String bloodGroup);
    
    /**
     * Find requests by requester email
     */
    List<BloodRequest> findByContactEmail(String contactEmail);
    
    /**
     * Find pending requests
     */
    List<BloodRequest> findByStatusOrderByCreatedAtAsc(BloodRequest.RequestStatus status);
    
    /**
     * Find emergency requests
     */
    List<BloodRequest> findByUrgencyLevelAndStatusOrderByCreatedAtAsc(
        BloodRequest.UrgencyLevel urgencyLevel, BloodRequest.RequestStatus status);
    
    /**
     * Find requests by blood group and status
     */
    List<BloodRequest> findByBloodGroupAndStatus(String bloodGroup, BloodRequest.RequestStatus status);
    
    /**
     * Count requests by status
     */
    long countByStatus(BloodRequest.RequestStatus status);
    
    /**
     * Count pending requests
     */
    @Query("SELECT COUNT(br) FROM BloodRequest br WHERE br.status = 'PENDING'")
    long countPendingRequests();
    
    /**
     * Count emergency requests
     */
    @Query("SELECT COUNT(br) FROM BloodRequest br WHERE br.urgencyLevel = 'EMERGENCY' AND br.status = 'PENDING'")
    long countEmergencyRequests();
    
    /**
     * Find requests created between dates
     */
    List<BloodRequest> findByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
    
    /**
     * Find recent requests (last 7 days)
     */
    @Query("SELECT br FROM BloodRequest br WHERE br.createdAt >= :date ORDER BY br.createdAt DESC")
    List<BloodRequest> findRecentRequests(@Param("date") LocalDateTime date);
    
    /**
     * Find requests processed by admin
     */
    List<BloodRequest> findByProcessedBy(String processedBy);
    
    /**
     * Get request statistics by blood group
     */
    @Query("SELECT br.bloodGroup, COUNT(br), SUM(br.unitsRequested) " +
           "FROM BloodRequest br GROUP BY br.bloodGroup")
    List<Object[]> getRequestStatsByBloodGroup();
    
    /**
     * Get request statistics by status
     */
    @Query("SELECT br.status, COUNT(br) FROM BloodRequest br GROUP BY br.status")
    List<Object[]> getRequestStatsByStatus();
    
    /**
     * Find overdue pending requests (pending for more than specified hours)
     */
    @Query("SELECT br FROM BloodRequest br WHERE br.status = 'PENDING' " +
           "AND br.createdAt < :cutoffTime ORDER BY br.urgencyLevel DESC, br.createdAt ASC")
    List<BloodRequest> findOverduePendingRequests(@Param("cutoffTime") LocalDateTime cutoffTime);
    
    /**
     * Get total units requested for a blood group in pending status
     */
    @Query("SELECT SUM(br.unitsRequested) FROM BloodRequest br " +
           "WHERE br.bloodGroup = :bloodGroup AND br.status = 'PENDING'")
    Integer getTotalPendingUnitsForBloodGroup(@Param("bloodGroup") String bloodGroup);
    
    /**
     * Find requests by hospital
     */
    List<BloodRequest> findByHospitalNameContainingIgnoreCase(String hospitalName);
    
    /**
     * Find requests by patient name
     */
    List<BloodRequest> findByPatientNameContainingIgnoreCase(String patientName);
}