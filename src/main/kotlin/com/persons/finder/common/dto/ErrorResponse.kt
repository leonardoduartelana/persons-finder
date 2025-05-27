package com.persons.finder.common.dto
import java.util.UUID

data class ErrorResponse(
    val transactionId: String = UUID.randomUUID().toString(),
    val error: ErrorBody,
)

data class ErrorBody(
    val code: String,
    val message: String,
)
