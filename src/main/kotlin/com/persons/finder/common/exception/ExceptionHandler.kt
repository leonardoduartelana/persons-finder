package com.persons.finder.common.exception

import com.persons.finder.common.dto.ErrorBody
import com.persons.finder.common.dto.ErrorResponse
import com.persons.finder.common.enum.ErrorCode
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.context.request.WebRequest
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException

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

    @ExceptionHandler(PersonNotFoundException::class)
    fun handleNotFound(ex: PersonNotFoundException, request: WebRequest): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            error = ErrorBody(
                code = ErrorCode.ENTITY_NOT_FOUND.code,
                message = ex.message ?: "No persons found",
            ),
        )
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(errorResponse)
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException::class)
    fun handleTypeMismatchException(
        ex: MethodArgumentTypeMismatchException,
        request: WebRequest,
    ): ResponseEntity<ErrorResponse> {
        val message = "Invalid value for parameter '${ex.name}'"
        val errorResponse = ErrorResponse(
            error = ErrorBody(
                code = ErrorCode.INVALID_INPUT.code,
                message = message,
            ),
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    fun handleHttpMessageNotReadable(ex: HttpMessageNotReadableException): ResponseEntity<ErrorResponse> {
        val errorResponse = ErrorResponse(
            error = ErrorBody(
                code = ErrorCode.INVALID_INPUT.code,
                message = "Invalid Request Body",
            ),
        )
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse)
    }
}
