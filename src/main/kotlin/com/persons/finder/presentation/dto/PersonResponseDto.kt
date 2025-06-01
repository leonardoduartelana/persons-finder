package com.persons.finder.presentation.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * DTO for returning person details
 */
data class PersonResponseDto(
    @Schema(description = "Person's unique identifier", example = "1")
    val id: Long,
    
    @Schema(description = "Person's name", example = "John Doe")
    val name: String
)