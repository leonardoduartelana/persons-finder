package com.persons.finder.presentation.api

import com.persons.finder.common.dto.ErrorResponse
import com.persons.finder.presentation.dto.CreatePersonRequest
import com.persons.finder.presentation.dto.CreatePersonResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import javax.validation.Valid

@RequestMapping("api/v1/persons")
interface PersonApi {

    @Operation(
        summary = "Create a new person",
        description = "This endpoint allows you to create a new person by providing a valid name.",
    )
    @ApiResponses(
        value = [
            ApiResponse(
                responseCode = "201",
                description = "Person successfully created",
                content = [Content(schema = Schema(implementation = CreatePersonResponse::class))],
            ),
            ApiResponse(
                responseCode = "400",
                description = "Validation error",
                content = [Content(schema = Schema(implementation = ErrorResponse::class))],
            ),
        ],
    )
    @PostMapping
    fun createPerson(
        @Valid
        @RequestBody
        request: CreatePersonRequest,
    ): ResponseEntity<CreatePersonResponse>
}
