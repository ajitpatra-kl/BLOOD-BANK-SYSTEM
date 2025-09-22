package com.bloodbank.controller;

import com.bloodbank.dto.CommonDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Global exception handler for the application
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    
    /**
     * Handle validation errors
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<CommonDTO.ValidationErrorResponse> handleValidationExceptions(
            MethodArgumentNotValidException ex, WebRequest request) {
        
        Map<String, String> fieldErrors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            fieldErrors.put(fieldName, errorMessage);
        });
        
        CommonDTO.ValidationErrorResponse errorResponse = new CommonDTO.ValidationErrorResponse(
            "Validation Failed",
            "Invalid input data provided",
            fieldErrors,
            LocalDateTime.now().toString(),
            HttpStatus.BAD_REQUEST.value()
        );
        
        log.error("Validation error: {}", fieldErrors);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle runtime exceptions
     */
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<CommonDTO.ErrorResponse> handleRuntimeException(
            RuntimeException ex, WebRequest request) {
        
        CommonDTO.ErrorResponse errorResponse = new CommonDTO.ErrorResponse(
            "Business Logic Error",
            ex.getMessage(),
            request.getDescription(false),
            LocalDateTime.now().toString(),
            HttpStatus.BAD_REQUEST.value()
        );
        
        log.error("Runtime error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle illegal argument exceptions
     */
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<CommonDTO.ErrorResponse> handleIllegalArgumentException(
            IllegalArgumentException ex, WebRequest request) {
        
        CommonDTO.ErrorResponse errorResponse = new CommonDTO.ErrorResponse(
            "Invalid Argument",
            ex.getMessage(),
            request.getDescription(false),
            LocalDateTime.now().toString(),
            HttpStatus.BAD_REQUEST.value()
        );
        
        log.error("Illegal argument error: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
    }
    
    /**
     * Handle general exceptions
     */
    @ExceptionHandler(Exception.class)
    public ResponseEntity<CommonDTO.ErrorResponse> handleGlobalException(
            Exception ex, WebRequest request) {
        
        CommonDTO.ErrorResponse errorResponse = new CommonDTO.ErrorResponse(
            "Internal Server Error",
            "An unexpected error occurred: " + ex.getMessage(),
            request.getDescription(false),
            LocalDateTime.now().toString(),
            HttpStatus.INTERNAL_SERVER_ERROR.value()
        );
        
        log.error("Unexpected error: ", ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }
}