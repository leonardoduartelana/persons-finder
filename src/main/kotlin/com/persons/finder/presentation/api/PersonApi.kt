package com.persons.finder.presentation.api

import com.persons.finder.common.dto.ErrorResponse
import com.persons.finder.presentation.dto.AddLocationRequest
import com.persons.finder.presentation.dto.AddLocationResponse
import com.persons.finder.presentation.dto.CreatePersonRequest
import com.persons.finder.presentation.dto.CreatePersonResponse
import com.persons.finder.presentation.dto.GetPersonResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.media.Content
import io.swagger.v3.oas.annotations.media.Schema
import io.swagger.v3.oas.annotations.responses.ApiResponse
import io.swagger.v3.oas.annotations.responses.ApiResponses
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
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

    @Operation(
        summary = "Get person(s) by ID(s)",
        description = "Retrieve one or more persons using their ID(s) as query parameters.",
        parameters = [
            Parameter(
                name = "id",
                description = "One or more person IDs",
                required = true,
                `in` = ParameterIn.QUERY,
                example = "1,2,3",
            ),
        ],
        responses = [
            ApiResponse(
                responseCode = "200",
                description = "Persons found",
                content = [Content(schema = Schema(implementation = GetPersonResponse::class))],
            ),
            ApiResponse(
                responseCode = "404",
                description = "No persons found for given IDs",
            ),
        ],
    )
    @GetMapping
    fun getPersonsById(
        @RequestParam("id") ids: List<Long>,
    ): ResponseEntity<GetPersonResponse>

    @Operation(summary = "Update or create person location")
    @PutMapping("/{id}/location")
    fun addLocation(
        @PathVariable id: Long,
        @Valid
        @RequestBody
        request: AddLocationRequest,
    ): ResponseEntity<AddLocationResponse>
}
