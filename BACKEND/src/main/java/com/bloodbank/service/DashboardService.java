package com.bloodbank.service;

import com.bloodbank.dto.CommonDTO;
import com.bloodbank.entity.BloodRequest;
import com.bloodbank.repository.BloodInventoryRepository;
import com.bloodbank.repository.BloodRequestRepository;
import com.bloodbank.repository.DonorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * Service class for Dashboard operations and statistics
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class DashboardService {
    
    private final DonorRepository donorRepository;
    private final BloodInventoryRepository bloodInventoryRepository;
    private final BloodRequestRepository bloodRequestRepository;
    
    /**
     * Get dashboard statistics
     */
    public CommonDTO.DashboardStats getDashboardStats() {
        log.info("Fetching dashboard statistics");
        
        // Donor statistics
        Long totalDonors = donorRepository.count();
        Long eligibleDonors = donorRepository.countByIsEligibleTrue();
        
        // Blood inventory statistics
        Long totalBloodUnits = bloodInventoryRepository.getTotalUnitsAvailable();
        Long criticalShortages = (long) bloodInventoryRepository.findCriticalShortages().size();
        
        // Blood request statistics
        Long pendingRequests = bloodRequestRepository.countByStatus(BloodRequest.RequestStatus.PENDING);
        Long emergencyRequests = bloodRequestRepository.countEmergencyRequests();
        
        // Today's statistics
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        LocalDateTime todayEnd = LocalDate.now().atTime(23, 59, 59);
        Long todayRequests = (long) bloodRequestRepository.findByCreatedAtBetween(todayStart, todayEnd).size();
        
        // Today's donations (recent donors in last 30 days as proxy)
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        Long todayDonations = (long) donorRepository.findRecentDonors(thirtyDaysAgo).size();
        
        return new CommonDTO.DashboardStats(
            totalDonors,
            eligibleDonors,
            totalBloodUnits != null ? totalBloodUnits : 0L,
            criticalShortages,
            pendingRequests,
            emergencyRequests,
            todayRequests,
            todayDonations
        );
    }
    
    /**
     * Get system health status
     */
    public String getSystemHealthStatus() {
        log.info("Checking system health status");
        
        Long criticalShortages = (long) bloodInventoryRepository.findCriticalShortages().size();
        Long emergencyRequests = bloodRequestRepository.countEmergencyRequests();
        Long overdueRequests = (long) bloodRequestRepository.findOverduePendingRequests(
            LocalDateTime.now().minusHours(24)
        ).size();
        
        if (emergencyRequests > 0 || overdueRequests > 5) {
            return "CRITICAL";
        } else if (criticalShortages > 3) {
            return "WARNING";
        } else {
            return "HEALTHY";
        }
    }
}