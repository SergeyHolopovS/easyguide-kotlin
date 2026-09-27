package com.easyguide.backend.shared.infrastructure.security

import com.easyguide.backend.shared.exceptions.exception.ErrorKind
import com.easyguide.backend.shared.exceptions.presentation.dto.ErrorDto
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.AuthenticationEntryPoint
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

/**
 * Без явного entry point Spring Security на отсутствие/невалидный токен по умолчанию отвечает
 * 403, а не 401 — это ломает семантику "не аутентифицирован". Тело ответа приводим к тому же
 * ErrorDto, что и GlobalExceptionHandler: Security-фильтры отрабатывают до DispatcherServlet,
 * поэтому @ControllerAdvice здесь не сработает и формировать JSON приходится вручную.
 */
@Component
class JwtAuthenticationEntryPoint(
    private val objectMapper: ObjectMapper,
) : AuthenticationEntryPoint {

    override fun commence(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authException: AuthenticationException,
    ) {
        response.status = HttpStatus.UNAUTHORIZED.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"
        response.writer.write(
            objectMapper.writeValueAsString(
                ErrorDto(message = "Требуется аутентификация", status = ErrorKind.UNAUTHORIZED),
            ),
        )
    }
}
