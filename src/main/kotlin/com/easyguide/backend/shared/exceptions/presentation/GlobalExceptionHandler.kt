package com.easyguide.backend.shared.exceptions.presentation

import com.easyguide.backend.shared.exceptions.exception.BasicException
import com.easyguide.backend.shared.exceptions.exception.ErrorKind
import com.easyguide.backend.shared.exceptions.exception.FieldsAware
import com.easyguide.backend.shared.exceptions.presentation.dto.ErrorDto
import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.validation.ConstraintViolationException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.http.converter.HttpMessageNotReadableException
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.multipart.MaxUploadSizeExceededException
import org.springframework.web.servlet.resource.NoResourceFoundException

@ControllerAdvice
class GlobalExceptionHandler {

    private val logger = KotlinLogging.logger {}

    @ExceptionHandler(BasicException::class)
    fun handleBusinessException(e: BasicException): ResponseEntity<ErrorDto> {
        val status = when (e.code) {
            ErrorKind.CONFLICT -> HttpStatus.CONFLICT
            ErrorKind.VALIDATION -> HttpStatus.BAD_REQUEST
            ErrorKind.NOT_FOUND -> HttpStatus.NOT_FOUND
            ErrorKind.FORBIDDEN -> HttpStatus.FORBIDDEN
            ErrorKind.UNAUTHORIZED -> HttpStatus.UNAUTHORIZED
            ErrorKind.INTERNAL -> HttpStatus.INTERNAL_SERVER_ERROR
        }
        return ResponseEntity
            .status(status)
            .body(
                ErrorDto(
                    e.message,
                    e.code,
                    (e as? FieldsAware)?.fields,
                )
            )
    }

    @ExceptionHandler(MethodArgumentNotValidException::class)
    fun handleValidationException(): ResponseEntity<ErrorDto> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorDto(
                    "Запрос невалиден",
                    ErrorKind.VALIDATION,
                )
            )
    }


    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolation(): ResponseEntity<ErrorDto> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorDto(
                    "Запрос невалиден",
                    ErrorKind.VALIDATION,
                )
            )
    }

    @ExceptionHandler(HttpMessageNotReadableException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleNoBody(): ResponseEntity<ErrorDto> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorDto(
                    "Тело запроса отсутствует",
                    ErrorKind.VALIDATION
                )
            )
    }

    @ExceptionHandler(MaxUploadSizeExceededException::class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    fun handleMaxUploadSizeExceeded(): ResponseEntity<ErrorDto> {
        return ResponseEntity
            .status(HttpStatus.BAD_REQUEST)
            .body(
                ErrorDto(
                    "Файл превышает максимально допустимый размер",
                    ErrorKind.VALIDATION,
                )
            )
    }

    @ExceptionHandler(NoResourceFoundException::class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    fun handleNotFound(): ResponseEntity<ErrorDto> {
        return ResponseEntity
            .status(HttpStatus.NOT_FOUND)
            .body(
                ErrorDto(
                    "Путь не найден",
                    ErrorKind.NOT_FOUND
                )
            )
    }

    @ExceptionHandler(Exception::class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    fun handleInternalError(e: Exception): ResponseEntity<ErrorDto> {
        logger.error(e) { "Internal server error\n${e.message}" }
        return ResponseEntity
            .internalServerError()
            .body(
                ErrorDto(
                    "Внутренняя ошибка сервера",
                    ErrorKind.INTERNAL
                )
            )
    }

}
