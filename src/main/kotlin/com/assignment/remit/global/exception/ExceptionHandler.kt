package com.assignment.remit.global.exception

import com.fasterxml.jackson.databind.exc.InvalidFormatException
import com.fasterxml.jackson.databind.exc.MismatchedInputException
import jakarta.persistence.EntityNotFoundException
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.context.request.WebRequest
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler

@RestControllerAdvice
class ExceptionHandler: ResponseEntityExceptionHandler() {
    override fun handleHttpMessageNotReadable(
        ex: HttpMessageNotReadableException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest,
    ): ResponseEntity<Any>? {
        logger.warn(ex.message)
        val message = when (val exception = ex.cause) {
            is InvalidFormatException -> "${exception.path.last().fieldName.orEmpty()}: 올바른 형식이어야 합니다"
            is MismatchedInputException -> "${exception.path.last().fieldName.orEmpty()}: null 일 수 없습니다"
            else -> exception?.message.orEmpty()
        }
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(message))
    }

    override fun handleMethodArgumentNotValid(
        ex: MethodArgumentNotValidException,
        headers: HttpHeaders,
        status: HttpStatusCode,
        request: WebRequest
    ): ResponseEntity<Any>? {
        logger.warn(ex.messages())
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(ex.messages()))
    }

    private fun MethodArgumentNotValidException.messages(): String {
        return bindingResult.fieldErrors.joinToString(", ") { "${it.field}: ${it.defaultMessage.orEmpty()}" }
    }

    @ExceptionHandler(PermissionDeniedException::class)
    fun handlePermissionDeniedException(exception: RuntimeException): ResponseEntity<ErrorResponse> {
        logger.warn(exception.message)
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ErrorResponse(exception.message))
    }

    @ExceptionHandler(EntityNotFoundException::class)
    fun handleEntityNotfoundException(exception: RuntimeException): ResponseEntity<ErrorResponse> {
        logger.warn(exception.message)
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ErrorResponse(exception.message))
    }

    @ExceptionHandler(CustomException::class)
    fun handleGlobalException(exception: RuntimeException): ResponseEntity<ErrorResponse> {
        logger.warn(exception.message)
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ErrorResponse(exception.message))
    }
}