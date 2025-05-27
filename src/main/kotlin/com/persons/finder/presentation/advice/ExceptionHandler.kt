package com.persons.finder.presentation.advice

import com.persons.finder.common.dto.ErrorBody
import com.persons.finder.common.dto.ErrorResponse
import com.persons.finder.common.enum.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest

@ControllerAdvice
class ExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationExceptions(
        ex: MethodArgumentNotValidException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val message = ex.bindingResult.allErrors
            .firstOrNull()?.defaultMessage ?: "Validation error"

        val errorResponse = ErrorResponse(
            error = ErrorBody(
                code = ErrorCode.VALIDATION_ERROR.code,
                message = message,
            ),
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }
}
