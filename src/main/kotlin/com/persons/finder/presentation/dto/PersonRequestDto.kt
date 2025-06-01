package com.persons.finder.presentation.dto

import io.swagger.v3.oas.annotations.media.Schema
import javax.validation.constraints.NotBlank

/**
 * DTO for creating a new person
 */
data class PersonRequestDto(
    @field:NotBlank(message = "Name is required")
    @Schema(description = "Person's name", example = "John Doe", required = true)
    val name: String
)