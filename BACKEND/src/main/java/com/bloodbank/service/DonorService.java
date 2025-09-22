package com.bloodbank.service;

import com.bloodbank.dto.DonorDTO;
import com.bloodbank.entity.Donor;
import com.bloodbank.repository.DonorRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service class for Donor operations
 */
@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DonorService {
    
    private final DonorRepository donorRepository;
    
    /**
     * Create a new donor
     */
    public DonorDTO.DonorResponse createDonor(DonorDTO.DonorCreateRequest request) {
        log.info("Creating new donor with email: {}", request.getEmail());
        
        // Check if email already exists
        if (donorRepository.existsByEmail(request.getEmail())) {
            throw new RuntimeException("Donor with email " + request.getEmail() + " already exists");
        }
        
        // Check if phone already exists
        if (donorRepository.existsByPhone(request.getPhone())) {
            throw new RuntimeException("Donor with phone " + request.getPhone() + " already exists");
        }
        
        Donor donor = new Donor();
        BeanUtils.copyProperties(request, donor);
        donor.setIsEligible(true);
        
        Donor savedDonor = donorRepository.save(donor);
        log.info("Successfully created donor with ID: {}", savedDonor.getId());
        
        return convertToResponse(savedDonor);
    }
    
    /**
     * Get donor by ID
     */
    @Transactional(readOnly = true)
    public Optional<DonorDTO.DonorResponse> getDonorById(Long id) {
        log.info("Fetching donor with ID: {}", id);
        return donorRepository.findById(id)
                .map(this::convertToResponse);
    }
    
    /**
     * Get donor by email
     */
    @Transactional(readOnly = true)
    public Optional<DonorDTO.DonorResponse> getDonorByEmail(String email) {
        log.info("Fetching donor with email: {}", email);
        return donorRepository.findByEmail(email)
                .map(this::convertToResponse);
    }
    
    /**
     * Get all donors
     */
    @Transactional(readOnly = true)
    public List<DonorDTO.DonorSummary> getAllDonors() {
        log.info("Fetching all donors");
        return donorRepository.findAll().stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());
    }
    
    /**
     * Get donors by blood group
     */
    @Transactional(readOnly = true)
    public List<DonorDTO.DonorSummary> getDonorsByBloodGroup(String bloodGroup) {
        log.info("Fetching donors with blood group: {}", bloodGroup);
        return donorRepository.findByBloodGroup(bloodGroup).stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());
    }
    
    /**
     * Get eligible donors who can donate
     */
    @Transactional(readOnly = true)
    public List<DonorDTO.DonorSummary> getEligibleDonors() {
        log.info("Fetching eligible donors");
        LocalDate cutoffDate = LocalDate.now().minusDays(56);
        return donorRepository.findEligibleDonors(cutoffDate).stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());
    }
    
    /**
     * Get eligible donors by blood group
     */
    @Transactional(readOnly = true)
    public List<DonorDTO.DonorSummary> getEligibleDonorsByBloodGroup(String bloodGroup) {
        log.info("Fetching eligible donors with blood group: {}", bloodGroup);
        LocalDate cutoffDate = LocalDate.now().minusDays(56);
        return donorRepository.findEligibleDonorsByBloodGroup(bloodGroup, cutoffDate).stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());
    }
    
    /**
     * Update donor
     */
    public DonorDTO.DonorResponse updateDonor(Long id, DonorDTO.DonorUpdateRequest request) {
        log.info("Updating donor with ID: {}", id);
        
        Donor donor = donorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donor not found with ID: " + id));
        
        // Update only non-null fields
        if (request.getName() != null) {
            donor.setName(request.getName());
        }
        if (request.getPhone() != null) {
            // Check if phone is already used by another donor
            Optional<Donor> existingDonor = donorRepository.findByPhone(request.getPhone());
            if (existingDonor.isPresent() && !existingDonor.get().getId().equals(id)) {
                throw new RuntimeException("Phone number is already in use");
            }
            donor.setPhone(request.getPhone());
        }
        if (request.getLastDonationDate() != null) {
            donor.setLastDonationDate(request.getLastDonationDate());
        }
        if (request.getAge() != null) {
            donor.setAge(request.getAge());
        }
        if (request.getWeight() != null) {
            donor.setWeight(request.getWeight());
        }
        if (request.getAddress() != null) {
            donor.setAddress(request.getAddress());
        }
        if (request.getIsEligible() != null) {
            donor.setIsEligible(request.getIsEligible());
        }
        
        Donor updatedDonor = donorRepository.save(donor);
        log.info("Successfully updated donor with ID: {}", id);
        
        return convertToResponse(updatedDonor);
    }
    
    /**
     * Update last donation date
     */
    public DonorDTO.DonorResponse updateLastDonationDate(Long id, LocalDate donationDate) {
        log.info("Updating last donation date for donor ID: {}", id);
        
        Donor donor = donorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Donor not found with ID: " + id));
        
        donor.setLastDonationDate(donationDate);
        Donor updatedDonor = donorRepository.save(donor);
        
        log.info("Successfully updated last donation date for donor ID: {}", id);
        return convertToResponse(updatedDonor);
    }
    
    /**
     * Delete donor
     */
    public void deleteDonor(Long id) {
        log.info("Deleting donor with ID: {}", id);
        
        if (!donorRepository.existsById(id)) {
            throw new RuntimeException("Donor not found with ID: " + id);
        }
        
        donorRepository.deleteById(id);
        log.info("Successfully deleted donor with ID: {}", id);
    }
    
    /**
     * Search donors by name
     */
    @Transactional(readOnly = true)
    public List<DonorDTO.DonorSummary> searchDonorsByName(String name) {
        log.info("Searching donors by name: {}", name);
        return donorRepository.findByNameContainingIgnoreCase(name).stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());
    }
    
    /**
     * Get donor statistics
     */
    @Transactional(readOnly = true)
    public List<DonorDTO.DonorStats> getDonorStatistics() {
        log.info("Fetching donor statistics");
        List<Object[]> stats = donorRepository.getDonorStatsByBloodGroup();
        LocalDate cutoffDate = LocalDate.now().minusDays(56);
        
        return stats.stream().map(stat -> {
            String bloodGroup = (String) stat[0];
            Long totalDonors = (Long) stat[1];
            Long eligibleDonors = (long) donorRepository.findByBloodGroupAndIsEligibleTrue(bloodGroup).size();
            Long availableDonors = (long) donorRepository.findEligibleDonorsByBloodGroup(bloodGroup, cutoffDate).size();
            
            return new DonorDTO.DonorStats(bloodGroup, totalDonors, eligibleDonors, availableDonors);
        }).collect(Collectors.toList());
    }
    
    /**
     * Get recent donors (donated in last 30 days)
     */
    @Transactional(readOnly = true)
    public List<DonorDTO.DonorSummary> getRecentDonors() {
        log.info("Fetching recent donors");
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);
        return donorRepository.findRecentDonors(thirtyDaysAgo).stream()
                .map(this::convertToSummary)
                .collect(Collectors.toList());
    }
    
    /**
     * Convert Donor entity to DonorResponse DTO
     */
    private DonorDTO.DonorResponse convertToResponse(Donor donor) {
        DonorDTO.DonorResponse response = new DonorDTO.DonorResponse();
        BeanUtils.copyProperties(donor, response);
        response.setCanDonate(donor.canDonate());
        return response;
    }
    
    /**
     * Convert Donor entity to DonorSummary DTO
     */
    private DonorDTO.DonorSummary convertToSummary(Donor donor) {
        DonorDTO.DonorSummary summary = new DonorDTO.DonorSummary();
        summary.setId(donor.getId());
        summary.setName(donor.getName());
        summary.setEmail(donor.getEmail());
        summary.setPhone(donor.getPhone());
        summary.setBloodGroup(donor.getBloodGroup());
        summary.setLastDonationDate(donor.getLastDonationDate());
        summary.setIsEligible(donor.getIsEligible());
        summary.setCanDonate(donor.canDonate());
        return summary;
    }
}