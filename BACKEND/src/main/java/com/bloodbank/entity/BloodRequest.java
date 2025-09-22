package com.bloodbank.entity;

import java.time.LocalDateTime;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * BloodRequest entity representing blood requests from patients/hospitals
 */
@Slf4j
@Entity
@Table(name = "blood_requests")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class BloodRequest {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "Requester name is required")
    @Size(min = 2, max = 100, message = "Requester name must be between 2 and 100 characters")
    @Column(name = "requester_name", nullable = false, length = 100)
    private String requesterName;
    
    @NotBlank(message = "Contact email is required")
    @Email(message = "Invalid email format")
    @Column(name = "contact_email", nullable = false, length = 150)
    private String contactEmail;
    
    @NotBlank(message = "Contact phone is required")
    @Pattern(regexp = "^[+]?[0-9]{10,15}$", message = "Invalid phone number format")
    @Column(name = "contact_phone", nullable = false, length = 15)
    private String contactPhone;
    
    @NotBlank(message = "Blood group is required")
    @Pattern(regexp = "^(A|B|AB|O)[+-]$", message = "Invalid blood group format")
    @Column(name = "blood_group", nullable = false, length = 3)
    private String bloodGroup;
    
    @Min(value = 1, message = "At least 1 unit must be requested")
    @Max(value = 10, message = "Maximum 10 units can be requested at once")
    @Column(name = "units_requested", nullable = false)
    private Integer unitsRequested;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "urgency_level", nullable = false)
    private UrgencyLevel urgencyLevel = UrgencyLevel.NORMAL;
    
    @NotBlank(message = "Hospital name is required")
    @Size(max = 150, message = "Hospital name must not exceed 150 characters")
    @Column(name = "hospital_name", nullable = false, length = 150)
    private String hospitalName;
    
    @NotBlank(message = "Patient name is required")
    @Size(max = 100, message = "Patient name must not exceed 100 characters")
    @Column(name = "patient_name", nullable = false, length = 100)
    private String patientName;
    
    @Size(max = 500, message = "Medical reason must not exceed 500 characters")
    @Column(name = "medical_reason", length = 500)
    private String medicalReason;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private RequestStatus status = RequestStatus.PENDING;
    
    @Size(max = 500, message = "Admin notes must not exceed 500 characters")
    @Column(name = "admin_notes", length = 500)
    private String adminNotes;
    
    @Column(name = "processed_by")
    private String processedBy;
    
    @Column(name = "processed_at")
    private LocalDateTime processedAt;
    
    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    /**
     * Enum for request status
     */
    public enum RequestStatus {
        PENDING("Pending Review"),
        APPROVED("Approved"),
        REJECTED("Rejected"),
        FULFILLED("Fulfilled"),
        CANCELLED("Cancelled");
        
        private final String displayName;
        
        RequestStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Enum for urgency level
     */
    public enum UrgencyLevel {
        EMERGENCY("Emergency"),
        URGENT("Urgent"),
        NORMAL("Normal");
        
        private final String displayName;
        
        UrgencyLevel(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * Check if request is still pending
     */
    public boolean isPending() {
        return status == RequestStatus.PENDING;
    }
    
    /**
     * Check if request is approved
     */
    public boolean isApproved() {
        return status == RequestStatus.APPROVED;
    }
    
    /**
     * Mark request as processed
     */
    public void markAsProcessed(String processedBy, RequestStatus newStatus, String notes) {
        this.processedBy = processedBy;
        this.processedAt = LocalDateTime.now();
        this.status = newStatus;
        this.adminNotes = notes;
    }
}