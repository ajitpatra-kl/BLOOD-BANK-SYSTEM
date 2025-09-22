package com.bloodbank.controller;

import com.bloodbank.dto.CommonDTO;
import com.bloodbank.dto.DonorDTO;
import com.bloodbank.service.DonorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

/**
 * REST Controller for Donor operations
 */
@RestController
@RequestMapping("/api/donors")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = "http://localhost:3000")
public class DonorController {
    
    private final DonorService donorService;
    
    /**
     * Create a new donor
     */
    @PostMapping
    public ResponseEntity<CommonDTO.ApiResponse<DonorDTO.DonorResponse>> createDonor(
            @Valid @RequestBody DonorDTO.DonorCreateRequest request) {
        try {
            log.info("Creating new donor with email: {}", request.getEmail());
            DonorDTO.DonorResponse response = donorService.createDonor(request);
            return ResponseEntity.status(HttpStatus.CREATED)
                    .body(CommonDTO.ApiResponse.success("Donor created successfully", response));
        } catch (RuntimeException e) {
            log.error("Error creating donor: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get donor by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<DonorDTO.DonorResponse>> getDonorById(@PathVariable Long id) {
        try {
            log.info("Fetching donor with ID: {}", id);
            return donorService.getDonorById(id)
                    .map(donor -> ResponseEntity.ok(CommonDTO.ApiResponse.success(donor)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(CommonDTO.ApiResponse.error("Donor not found with ID: " + id)));
        } catch (Exception e) {
            log.error("Error fetching donor: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get donor by email
     */
    @GetMapping("/email/{email}")
    public ResponseEntity<CommonDTO.ApiResponse<DonorDTO.DonorResponse>> getDonorByEmail(@PathVariable String email) {
        try {
            log.info("Fetching donor with email: {}", email);
            return donorService.getDonorByEmail(email)
                    .map(donor -> ResponseEntity.ok(CommonDTO.ApiResponse.success(donor)))
                    .orElse(ResponseEntity.status(HttpStatus.NOT_FOUND)
                            .body(CommonDTO.ApiResponse.error("Donor not found with email: " + email)));
        } catch (Exception e) {
            log.error("Error fetching donor by email: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get all donors
     */
    @GetMapping
    public ResponseEntity<CommonDTO.ApiResponse<List<DonorDTO.DonorSummary>>> getAllDonors() {
        try {
            log.info("Fetching all donors");
            List<DonorDTO.DonorSummary> donors = donorService.getAllDonors();
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(donors));
        } catch (Exception e) {
            log.error("Error fetching all donors: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get donors by blood group
     */
    @GetMapping("/blood-group/{bloodGroup}")
    public ResponseEntity<CommonDTO.ApiResponse<List<DonorDTO.DonorSummary>>> getDonorsByBloodGroup(
            @PathVariable String bloodGroup) {
        try {
            log.info("Fetching donors with blood group: {}", bloodGroup);
            List<DonorDTO.DonorSummary> donors = donorService.getDonorsByBloodGroup(bloodGroup);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(donors));
        } catch (Exception e) {
            log.error("Error fetching donors by blood group: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get eligible donors
     */
    @GetMapping("/eligible")
    public ResponseEntity<CommonDTO.ApiResponse<List<DonorDTO.DonorSummary>>> getEligibleDonors() {
        try {
            log.info("Fetching eligible donors");
            List<DonorDTO.DonorSummary> donors = donorService.getEligibleDonors();
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(donors));
        } catch (Exception e) {
            log.error("Error fetching eligible donors: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get eligible donors by blood group
     */
    @GetMapping("/eligible/blood-group/{bloodGroup}")
    public ResponseEntity<CommonDTO.ApiResponse<List<DonorDTO.DonorSummary>>> getEligibleDonorsByBloodGroup(
            @PathVariable String bloodGroup) {
        try {
            log.info("Fetching eligible donors with blood group: {}", bloodGroup);
            List<DonorDTO.DonorSummary> donors = donorService.getEligibleDonorsByBloodGroup(bloodGroup);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(donors));
        } catch (Exception e) {
            log.error("Error fetching eligible donors by blood group: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Update donor
     */
    @PutMapping("/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<DonorDTO.DonorResponse>> updateDonor(
            @PathVariable Long id,
            @Valid @RequestBody DonorDTO.DonorUpdateRequest request) {
        try {
            log.info("Updating donor with ID: {}", id);
            DonorDTO.DonorResponse response = donorService.updateDonor(id, request);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success("Donor updated successfully", response));
        } catch (RuntimeException e) {
            log.error("Error updating donor: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Update last donation date
     */
    @PutMapping("/{id}/donation-date")
    public ResponseEntity<CommonDTO.ApiResponse<DonorDTO.DonorResponse>> updateLastDonationDate(
            @PathVariable Long id,
            @RequestParam LocalDate donationDate) {
        try {
            log.info("Updating last donation date for donor ID: {}", id);
            DonorDTO.DonorResponse response = donorService.updateLastDonationDate(id, donationDate);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success("Donation date updated successfully", response));
        } catch (RuntimeException e) {
            log.error("Error updating donation date: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Delete donor
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<CommonDTO.ApiResponse<Void>> deleteDonor(@PathVariable Long id) {
        try {
            log.info("Deleting donor with ID: {}", id);
            donorService.deleteDonor(id);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success("Donor deleted successfully", null));
        } catch (RuntimeException e) {
            log.error("Error deleting donor: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Search donors by name
     */
    @GetMapping("/search")
    public ResponseEntity<CommonDTO.ApiResponse<List<DonorDTO.DonorSummary>>> searchDonorsByName(
            @RequestParam String name) {
        try {
            log.info("Searching donors by name: {}", name);
            List<DonorDTO.DonorSummary> donors = donorService.searchDonorsByName(name);
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(donors));
        } catch (Exception e) {
            log.error("Error searching donors: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get donor statistics
     */
    @GetMapping("/statistics")
    public ResponseEntity<CommonDTO.ApiResponse<List<DonorDTO.DonorStats>>> getDonorStatistics() {
        try {
            log.info("Fetching donor statistics");
            List<DonorDTO.DonorStats> stats = donorService.getDonorStatistics();
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(stats));
        } catch (Exception e) {
            log.error("Error fetching donor statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
    
    /**
     * Get recent donors (last 30 days)
     */
    @GetMapping("/recent")
    public ResponseEntity<CommonDTO.ApiResponse<List<DonorDTO.DonorSummary>>> getRecentDonors() {
        try {
            log.info("Fetching recent donors");
            List<DonorDTO.DonorSummary> donors = donorService.getRecentDonors();
            return ResponseEntity.ok(CommonDTO.ApiResponse.success(donors));
        } catch (Exception e) {
            log.error("Error fetching recent donors: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(CommonDTO.ApiResponse.error(e.getMessage()));
        }
    }
}