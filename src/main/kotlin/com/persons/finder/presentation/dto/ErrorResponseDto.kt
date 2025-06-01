package com.persons.finder.presentation.dto

import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

/**
 * DTO for returning error details
 */
data class ErrorResponseDto(
    @Schema(description = "HTTP status code", example = "400")
    val status: Int,
    
    @Schema(description = "Error message", example = "Bad Request")
    val message: String,
    
    @Schema(description = "Detailed error description", example = "Name is required")
    val details: String,
    
    @Schema(description = "Timestamp when the error occurred", example = "2023-07-21T15:30:45.123")
    val timestamp: LocalDateTime = LocalDateTime.now()
)