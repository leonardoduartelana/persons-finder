package com.persons.finder.presentation.dto

import io.swagger.v3.oas.annotations.media.Schema

/**
 * DTO for returning nearby persons with their locations
 */
data class NearbyPersonsResponseDto(
    val persons: List<NearbyPersonDto>
)

/**
 * DTO for a person with their location and distance from query point
 */
data class NearbyPersonDto(
    val id: Long,
    val name: String,
    val latitude: Double,
    val longitude: Double,
    @Schema(description = "Distance from query point in kilometers", example = "1.5")
    val distanceKm: Double
)