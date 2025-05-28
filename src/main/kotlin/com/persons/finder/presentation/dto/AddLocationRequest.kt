package com.persons.finder.presentation.dto

import javax.validation.constraints.DecimalMax
import javax.validation.constraints.DecimalMin
import javax.validation.constraints.NotNull

data class AddLocationRequest(
    @field:NotNull(message = "Latitude must not be null")
    @field:DecimalMin(value = "-90.0", inclusive = true, message = "Latitude must be between -90 and 90")
    @field:DecimalMax(value = "90.0", inclusive = true, message = "Latitude must be between -90 and 90")
    val latitude: Double?,

    @field:NotNull(message = "Longitude must not be null")
    @field:DecimalMin(value = "-180.0", inclusive = true, message = "Longitude must be between -180 and 180")
    @field:DecimalMax(value = "180.0", inclusive = true, message = "Longitude must be between -180 and 180")
    val longitude: Double?,
)
