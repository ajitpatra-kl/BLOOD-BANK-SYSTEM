package com.bloodbank.service;

import com.bloodbank.dto.BloodRequestDTO;
import com.bloodbank.entity.BloodRequest;
import com.bloodbank.repository.BloodRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for BloodRequest operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class BloodRequestService {
    
    private final BloodRequestRepository bloodRequestRepository;
    private final BloodInventoryService bloodInventoryService;
    
    /**
     * Create a new blood request
     */
    public BloodRequestDTO.BloodRequestResponse createBloodRequest(BloodRequestDTO.BloodRequestCreateRequest request) {
        log.info("Creating new blood request for blood group: {} by {}", request.getBloodGroup(), request.getRequesterName());
        
        BloodRequest bloodRequest = new BloodRequest();
        BeanUtils.copyProperties(request, bloodRequest);
        bloodRequest.setStatus(BloodRequest.RequestStatus.PENDING);
        
        BloodRequest savedRequest = bloodRequestRepository.save(bloodRequest);
        log.info("Successfully created blood request with ID: {}", savedRequest.getId());
        
        return convertToResponse(savedRequest);
    }
    
    /**
     * Get blood request by ID
     */
    @Transactional(readOnly = true)
    public Optional<BloodRequestDTO.BloodRequestResponse> getBloodRequestById(Long id) {
        log.info("Fetching blood request with ID: {}", id);
        return bloodRequestRepository.findById(id)
                .map(this::convertToResponse);
    }
    
    /**
     * Get all blood requests
     */
    @Transactional(readOnly = true)
    public List<BloodRequestDTO.BloodRequestSummary> getAllBloodRequests() {
        log.info("Fetching all blood requests");
        return bloodRequestRepository.findAll().stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());
    }
    
    /**
     * Get blood requests by status
     */
    @Transactional(readOnly = true)
    public List<BloodRequestDTO.BloodRequestSummary> getBloodRequestsByStatus(BloodRequest.RequestStatus status) {
        log.info("Fetching blood requests with status: {}", status);
        return bloodRequestRepository.findByStatus(status).stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());
    }
    
    /**
     * Get pending blood requests (ordered by creation date)
     */
    @Transactional(readOnly = true)
    public List<BloodRequestDTO.BloodRequestSummary> getPendingBloodRequests() {
        log.info("Fetching pending blood requests");
        return bloodRequestRepository.findByStatusOrderByCreatedAtAsc(BloodRequest.RequestStatus.PENDING).stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());
    }
    
    /**
     * Get emergency blood requests
     */
    @Transactional(readOnly = true)
    public List<BloodRequestDTO.BloodRequestSummary> getEmergencyBloodRequests() {
        log.info("Fetching emergency blood requests");
        return bloodRequestRepository.findByUrgencyLevelAndStatusOrderByCreatedAtAsc(
                BloodRequest.UrgencyLevel.EMERGENCY, 
                BloodRequest.RequestStatus.PENDING
        ).stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());
    }
    
    /**
     * Get blood requests by blood group
     */
    @Transactional(readOnly = true)
    public List<BloodRequestDTO.BloodRequestSummary> getBloodRequestsByBloodGroup(String bloodGroup) {
        log.info("Fetching blood requests for blood group: {}", bloodGroup);
        return bloodRequestRepository.findByBloodGroup(bloodGroup).stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());
    }
    
    /**
     * Get blood requests by requester email
     */
    @Transactional(readOnly = true)
    public List<BloodRequestDTO.BloodRequestSummary> getBloodRequestsByEmail(String email) {
        log.info("Fetching blood requests for email: {}", email);
        return bloodRequestRepository.findByContactEmail(email).stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());
    }
    
    /**
     * Update blood request status (approve/reject)
     */
    public BloodRequestDTO.BloodRequestResponse updateRequestStatus(Long id, BloodRequestDTO.BloodRequestStatusUpdate request) {
        log.info("Updating blood request status for ID: {} to {}", id, request.getStatus());
        
        BloodRequest bloodRequest = bloodRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blood request not found with ID: " + id));
        
        if (!bloodRequest.isPending()) {
            throw new RuntimeException("Blood request has already been processed");
        }
        
        // If approving, check if sufficient units are available
        if (request.getStatus() == BloodRequest.RequestStatus.APPROVED) {
            boolean hasSufficientUnits = bloodInventoryService.hasSufficientUnits(
                bloodRequest.getBloodGroup(), 
                bloodRequest.getUnitsRequested()
            );
            
            if (!hasSufficientUnits) {
                throw new RuntimeException("Insufficient blood units available for blood group: " + bloodRequest.getBloodGroup());
            }
        }
        
        bloodRequest.markAsProcessed(request.getProcessedBy(), request.getStatus(), request.getAdminNotes());
        
        BloodRequest updatedRequest = bloodRequestRepository.save(bloodRequest);
        log.info("Successfully updated blood request status for ID: {}", id);
        
        return convertToResponse(updatedRequest);
    }
    
    /**
     * Approve blood request and deduct units from inventory
     */
    public BloodRequestDTO.BloodRequestResponse approveAndFulfillRequest(Long id, BloodRequestDTO.BloodRequestStatusUpdate request) {
        log.info("Approving and fulfilling blood request with ID: {}", id);
        
        BloodRequest bloodRequest = bloodRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blood request not found with ID: " + id));
        
        if (!bloodRequest.isPending()) {
            throw new RuntimeException("Blood request has already been processed");
        }
        
        // Check if sufficient units are available
        boolean hasSufficientUnits = bloodInventoryService.hasSufficientUnits(
            bloodRequest.getBloodGroup(), 
            bloodRequest.getUnitsRequested()
        );
        
        if (!hasSufficientUnits) {
            throw new RuntimeException("Insufficient blood units available for blood group: " + bloodRequest.getBloodGroup());
        }
        
        // Deduct units from inventory
        try {
            bloodInventoryService.removeUnits(
                bloodRequest.getBloodGroup(),
                new com.bloodbank.dto.BloodInventoryDTO.UnitsUpdateRequest(
                    bloodRequest.getUnitsRequested(),
                    "Units deducted for approved request ID: " + id
                )
            );
        } catch (Exception e) {
            log.error("Failed to deduct units from inventory: {}", e.getMessage());
            throw new RuntimeException("Failed to fulfill request: " + e.getMessage());
        }
        
        // Mark request as fulfilled
        bloodRequest.markAsProcessed(request.getProcessedBy(), BloodRequest.RequestStatus.FULFILLED, request.getAdminNotes());
        
        BloodRequest updatedRequest = bloodRequestRepository.save(bloodRequest);
        log.info("Successfully approved and fulfilled blood request with ID: {}", id);
        
        return convertToResponse(updatedRequest);
    }
    
    /**
     * Cancel blood request
     */
    public BloodRequestDTO.BloodRequestResponse cancelBloodRequest(Long id, String reason) {
        log.info("Cancelling blood request with ID: {}", id);
        
        BloodRequest bloodRequest = bloodRequestRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Blood request not found with ID: " + id));
        
        if (!bloodRequest.isPending()) {
            throw new RuntimeException("Blood request has already been processed");
        }
        
        bloodRequest.markAsProcessed("System", BloodRequest.RequestStatus.CANCELLED, reason);
        
        BloodRequest updatedRequest = bloodRequestRepository.save(bloodRequest);
        log.info("Successfully cancelled blood request with ID: {}", id);
        
        return convertToResponse(updatedRequest);
    }
    
    /**
     * Delete blood request
     */
    public void deleteBloodRequest(Long id) {
        log.info("Deleting blood request with ID: {}", id);
        
        if (!bloodRequestRepository.existsById(id)) {
            throw new RuntimeException("Blood request not found with ID: " + id);
        }
        
        bloodRequestRepository.deleteById(id);
        log.info("Successfully deleted blood request with ID: {}", id);
    }
    
    /**
     * Get recent blood requests (last 7 days)
     */
    @Transactional(readOnly = true)
    public List<BloodRequestDTO.BloodRequestSummary> getRecentBloodRequests() {
        log.info("Fetching recent blood requests");
        LocalDateTime sevenDaysAgo = LocalDateTime.now().minusDays(7);
        return bloodRequestRepository.findRecentRequests(sevenDaysAgo).stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());
    }
    
    /**
     * Get overdue pending requests (pending for more than 24 hours)
     */
    @Transactional(readOnly = true)
    public List<BloodRequestDTO.BloodRequestSummary> getOverduePendingRequests() {
        log.info("Fetching overdue pending requests");
        LocalDateTime twentyFourHoursAgo = LocalDateTime.now().minusHours(24);
        return bloodRequestRepository.findOverduePendingRequests(twentyFourHoursAgo).stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());
    }
    
    /**
     * Search requests by hospital name
     */
    @Transactional(readOnly = true)
    public List<BloodRequestDTO.BloodRequestSummary> searchByHospitalName(String hospitalName) {
        log.info("Searching blood requests by hospital name: {}", hospitalName);
        return bloodRequestRepository.findByHospitalNameContainingIgnoreCase(hospitalName).stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());
    }
    
    /**
     * Search requests by patient name
     */
    @Transactional(readOnly = true)
    public List<BloodRequestDTO.BloodRequestSummary> searchByPatientName(String patientName) {
        log.info("Searching blood requests by patient name: {}", patientName);
        return bloodRequestRepository.findByPatientNameContainingIgnoreCase(patientName).stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());
    }
    
    /**
     * Get request statistics
     */
    @Transactional(readOnly = true)
    public BloodRequestDTO.RequestStats getRequestStatistics() {
        log.info("Fetching request statistics");
        
        long totalRequests = bloodRequestRepository.count();
        long pendingRequests = bloodRequestRepository.countByStatus(BloodRequest.RequestStatus.PENDING);
        long approvedRequests = bloodRequestRepository.countByStatus(BloodRequest.RequestStatus.APPROVED);
        long rejectedRequests = bloodRequestRepository.countByStatus(BloodRequest.RequestStatus.REJECTED);
        long emergencyRequests = bloodRequestRepository.countEmergencyRequests();
        
        List<BloodRequest> urgentRequests = bloodRequestRepository.findByUrgencyLevelAndStatusOrderByCreatedAtAsc(
            BloodRequest.UrgencyLevel.URGENT, BloodRequest.RequestStatus.PENDING
        );
        
        return new BloodRequestDTO.RequestStats(
            totalRequests,
            pendingRequests,
            approvedRequests,
            rejectedRequests,
            emergencyRequests,
            (long) urgentRequests.size()
        );
    }
    
    /**
     * Get blood group request statistics
     */
    @Transactional(readOnly = true)
    public List<BloodRequestDTO.BloodGroupRequestStats> getBloodGroupRequestStatistics() {
        log.info("Fetching blood group request statistics");
        
        List<Object[]> stats = bloodRequestRepository.getRequestStatsByBloodGroup();
        
        return stats.stream().map(stat -> {
            String bloodGroup = (String) stat[0];
            Long totalRequests = (Long) stat[1];
            Long totalUnitsRequested = (Long) stat[2];
            
            long pendingRequests = bloodRequestRepository.findByBloodGroupAndStatus(
                bloodGroup, BloodRequest.RequestStatus.PENDING
            ).size();
            
            Integer pendingUnits = bloodRequestRepository.getTotalPendingUnitsForBloodGroup(bloodGroup);
            
            return new BloodRequestDTO.BloodGroupRequestStats(
                bloodGroup,
                totalRequests,
                totalUnitsRequested,
                (long) pendingRequests,
                pendingUnits != null ? pendingUnits.longValue() : 0L
            );
        }).collect(Collectors.toList());
    }
    
    /**
     * Convert BloodRequest entity to BloodRequestResponse DTO
     */
    private BloodRequestDTO.BloodRequestResponse convertToResponse(BloodRequest request) {
        BloodRequestDTO.BloodRequestResponse response = new BloodRequestDTO.BloodRequestResponse();
        BeanUtils.copyProperties(request, response);
        response.setUrgencyLevelDisplay(request.getUrgencyLevel().getDisplayName());
        response.setStatusDisplay(request.getStatus().getDisplayName());
        return response;
    }
    
    /**
     * Convert BloodRequest entity to BloodRequestSummary DTO
     */
    private BloodRequestDTO.BloodRequestSummary convertToSummary(BloodRequest request) {
        BloodRequestDTO.BloodRequestSummary summary = new BloodRequestDTO.BloodRequestSummary();
        summary.setId(request.getId());
        summary.setRequesterName(request.getRequesterName());
        summary.setBloodGroup(request.getBloodGroup());
        summary.setUnitsRequested(request.getUnitsRequested());
        summary.setUrgencyLevel(request.getUrgencyLevel());
        summary.setUrgencyLevelDisplay(request.getUrgencyLevel().getDisplayName());
        summary.setHospitalName(request.getHospitalName());
        summary.setPatientName(request.getPatientName());
        summary.setStatus(request.getStatus());
        summary.setStatusDisplay(request.getStatus().getDisplayName());
        summary.setCreatedAt(request.getCreatedAt());
        return summary;
    }
}