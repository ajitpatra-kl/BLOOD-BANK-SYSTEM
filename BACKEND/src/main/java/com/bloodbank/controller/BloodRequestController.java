package com.bloodbank.controller;

import com.bloodbank.dto.BloodRequestDTO;
import com.bloodbank.dto.CommonDTO;
import com.bloodbank.entity.BloodRequest;
import com.bloodbank.service.BloodRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * REST Controller for BloodRequest operations
 */
@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class BloodRequestController {
    
    private final BloodRequestService bloodRequestService;
    
    /**
     * Create a new blood request
     */
    @PostMapping
    public ResponseEntity<CommonDTO.ApiResponse<BloodRequestDTO.BloodRequestResponse>> createBloodRequest(
            @Valid @RequestBody BloodRequestDTO.BloodRequestCreateRequest request) {
        try {
            log.info("Creating new blood request for blood group: {} by {}", request.getBloodGroup(), request.getRequesterName());
            BloodRequestDTO.BloodRequestResponse response = bloodRequestService.createBloodRequest(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CommonDTO.ApiResponse.success("Blood request created successfully", response));
        } catch (RuntimeException e) {
            log.error("Error creating blood request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get blood request by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<BloodRequestDTO.BloodRequestResponse>> getBloodRequestById(
            @PathVariable Long id) {
        try {
            log.info("Fetching blood request with ID: {}", id);
            return bloodRequestService.getBloodRequestById(id)
                    .map(request -> ResponseEntity.ok(CommonDTO.ApiResponse.success(request)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(CommonDTO.ApiResponse.error("Blood request not found with ID: " + id)));
        } catch (Exception e) {
            log.error("Error fetching blood request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get all blood requests
     */
    @GetMapping
    public ResponseEntity<CommonDTO.ApiResponse<List<BloodRequestDTO.BloodRequestSummary>>> getAllBloodRequests() {
        try {
            log.info("Fetching all blood requests");
            List<BloodRequestDTO.BloodRequestSummary> requests = bloodRequestService.getAllBloodRequests();
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(requests));
        } catch (Exception e) {
            log.error("Error fetching all blood requests: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get blood requests by status
     */
    @GetMapping("/status/{status}")
    public ResponseEntity<CommonDTO.ApiResponse<List<BloodRequestDTO.BloodRequestSummary>>> getBloodRequestsByStatus(
            @PathVariable BloodRequest.RequestStatus status) {
        try {
            log.info("Fetching blood requests with status: {}", status);
            List<BloodRequestDTO.BloodRequestSummary> requests = bloodRequestService.getBloodRequestsByStatus(status);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(requests));
        } catch (Exception e) {
            log.error("Error fetching blood requests by status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get pending blood requests
     */
    @GetMapping("/pending")
    public ResponseEntity<CommonDTO.ApiResponse<List<BloodRequestDTO.BloodRequestSummary>>> getPendingBloodRequests() {
        try {
            log.info("Fetching pending blood requests");
            List<BloodRequestDTO.BloodRequestSummary> requests = bloodRequestService.getPendingBloodRequests();
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(requests));
        } catch (Exception e) {
            log.error("Error fetching pending blood requests: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get emergency blood requests
     */
    @GetMapping("/emergency")
    public ResponseEntity<CommonDTO.ApiResponse<List<BloodRequestDTO.BloodRequestSummary>>> getEmergencyBloodRequests() {
        try {
            log.info("Fetching emergency blood requests");
            List<BloodRequestDTO.BloodRequestSummary> requests = bloodRequestService.getEmergencyBloodRequests();
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(requests));
        } catch (Exception e) {
            log.error("Error fetching emergency blood requests: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get blood requests by blood group
     */
    @GetMapping("/blood-group/{bloodGroup}")
    public ResponseEntity<CommonDTO.ApiResponse<List<BloodRequestDTO.BloodRequestSummary>>> getBloodRequestsByBloodGroup(
            @PathVariable String bloodGroup) {
        try {
            log.info("Fetching blood requests for blood group: {}", bloodGroup);
            List<BloodRequestDTO.BloodRequestSummary> requests = bloodRequestService.getBloodRequestsByBloodGroup(bloodGroup);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(requests));
        } catch (Exception e) {
            log.error("Error fetching blood requests by blood group: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get blood requests by email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<CommonDTO.ApiResponse<List<BloodRequestDTO.BloodRequestSummary>>> getBloodRequestsByEmail(
            @PathVariable String email) {
        try {
            log.info("Fetching blood requests for email: {}", email);
            List<BloodRequestDTO.BloodRequestSummary> requests = bloodRequestService.getBloodRequestsByEmail(email);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(requests));
        } catch (Exception e) {
            log.error("Error fetching blood requests by email: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Update blood request status
     */
    @PutMapping("/{id}/status")
    public ResponseEntity<CommonDTO.ApiResponse<BloodRequestDTO.BloodRequestResponse>> updateRequestStatus(
            @PathVariable Long id,
            @Valid @RequestBody BloodRequestDTO.BloodRequestStatusUpdate request) {
        try {
            log.info("Updating blood request status for ID: {} to {}", id, request.getStatus());
            BloodRequestDTO.BloodRequestResponse response = bloodRequestService.updateRequestStatus(id, request);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success("Request status updated successfully", response));
        } catch (RuntimeException e) {
            log.error("Error updating request status: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Approve and fulfill blood request
     */
    @PutMapping("/{id}/approve-fulfill")
    public ResponseEntity<CommonDTO.ApiResponse<BloodRequestDTO.BloodRequestResponse>> approveAndFulfillRequest(
            @PathVariable Long id,
            @Valid @RequestBody BloodRequestDTO.BloodRequestStatusUpdate request) {
        try {
            log.info("Approving and fulfilling blood request with ID: {}", id);
            BloodRequestDTO.BloodRequestResponse response = bloodRequestService.approveAndFulfillRequest(id, request);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success("Request approved and fulfilled successfully", response));
        } catch (RuntimeException e) {
            log.error("Error approving and fulfilling request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Cancel blood request
     */
    @PutMapping("/{id}/cancel")
    public ResponseEntity<CommonDTO.ApiResponse<BloodRequestDTO.BloodRequestResponse>> cancelBloodRequest(
            @PathVariable Long id,
            @RequestParam String reason) {
        try {
            log.info("Cancelling blood request with ID: {}", id);
            BloodRequestDTO.BloodRequestResponse response = bloodRequestService.cancelBloodRequest(id, reason);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success("Request cancelled successfully", response));
        } catch (RuntimeException e) {
            log.error("Error cancelling request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Delete blood request
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<Void>> deleteBloodRequest(@PathVariable Long id) {
        try {
            log.info("Deleting blood request with ID: {}", id);
            bloodRequestService.deleteBloodRequest(id);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success("Blood request deleted successfully", null));
        } catch (RuntimeException e) {
            log.error("Error deleting blood request: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get recent blood requests (last 7 days)
     */
    @GetMapping("/recent")
    public ResponseEntity<CommonDTO.ApiResponse<List<BloodRequestDTO.BloodRequestSummary>>> getRecentBloodRequests() {
        try {
            log.info("Fetching recent blood requests");
            List<BloodRequestDTO.BloodRequestSummary> requests = bloodRequestService.getRecentBloodRequests();
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(requests));
        } catch (Exception e) {
            log.error("Error fetching recent blood requests: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get overdue pending requests
     */
    @GetMapping("/overdue")
    public ResponseEntity<CommonDTO.ApiResponse<List<BloodRequestDTO.BloodRequestSummary>>> getOverduePendingRequests() {
        try {
            log.info("Fetching overdue pending requests");
            List<BloodRequestDTO.BloodRequestSummary> requests = bloodRequestService.getOverduePendingRequests();
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(requests));
        } catch (Exception e) {
            log.error("Error fetching overdue pending requests: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Search requests by hospital name
     */
    @GetMapping("/search/hospital")
    public ResponseEntity<CommonDTO.ApiResponse<List<BloodRequestDTO.BloodRequestSummary>>> searchByHospitalName(
            @RequestParam String hospitalName) {
        try {
            log.info("Searching blood requests by hospital name: {}", hospitalName);
            List<BloodRequestDTO.BloodRequestSummary> requests = bloodRequestService.searchByHospitalName(hospitalName);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(requests));
        } catch (Exception e) {
            log.error("Error searching requests by hospital: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Search requests by patient name
     */
    @GetMapping("/search/patient")
    public ResponseEntity<CommonDTO.ApiResponse<List<BloodRequestDTO.BloodRequestSummary>>> searchByPatientName(
            @RequestParam String patientName) {
        try {
            log.info("Searching blood requests by patient name: {}", patientName);
            List<BloodRequestDTO.BloodRequestSummary> requests = bloodRequestService.searchByPatientName(patientName);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(requests));
        } catch (Exception e) {
            log.error("Error searching requests by patient: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get request statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<CommonDTO.ApiResponse<BloodRequestDTO.RequestStats>> getRequestStatistics() {
        try {
            log.info("Fetching request statistics");
            BloodRequestDTO.RequestStats stats = bloodRequestService.getRequestStatistics();
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(stats));
        } catch (Exception e) {
            log.error("Error fetching request statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get blood group request statistics
     */
    @GetMapping("/statistics/blood-groups")
    public ResponseEntity<CommonDTO.ApiResponse<List<BloodRequestDTO.BloodGroupRequestStats>>> getBloodGroupRequestStatistics() {
        try {
            log.info("Fetching blood group request statistics");
            List<BloodRequestDTO.BloodGroupRequestStats> stats = bloodRequestService.getBloodGroupRequestStatistics();
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(stats));
        } catch (Exception e) {
            log.error("Error fetching blood group request statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
}