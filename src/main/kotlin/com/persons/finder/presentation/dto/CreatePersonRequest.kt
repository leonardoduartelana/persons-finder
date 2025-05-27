package com.persons.finder.presentation.dto

import javax.validation.constraints.NotBlank
import javax.validation.constraints.Pattern
import javax.validation.constraints.Size

data class CreatePersonRequest(
    @field:NotBlank(message = "Name must not be blank")
    @field:Size(max = 100, message = "Name must be at most 100 characters")
    @field:Pattern(
        regexp = "^[a-zA-Z\\s\\-']*$",
        message = "Name contains invalid characters",
    )
    val name: String,
)
