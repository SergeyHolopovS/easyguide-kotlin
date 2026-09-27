package com.easyguide.backend.shared.infrastructure.security

import com.easyguide.backend.shared.exceptions.exception.ErrorKind
import com.easyguide.backend.shared.exceptions.presentation.dto.ErrorDto
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.access.AccessDeniedHandler
import org.springframework.stereotype.Component
import tools.jackson.databind.ObjectMapper

/**
 * Срабатывает, когда запрос аутентифицирован, но в доступе отказывает сам Spring Security
 * (например, при появлении в будущем правил на основе ролей/authorities в authorizeHttpRequests).
 * Бизнесовые 403 (AccessDeniedDomainException — "чужой тур" и т.п.) этот хендлер не перехватывает —
 * они бросаются уже внутри контроллера/use-case и уходят через GlobalExceptionHandler.
 */
@Component
class JwtAccessDeniedHandler(
    private val objectMapper: ObjectMapper,
) : AccessDeniedHandler {

    override fun handle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        accessDeniedException: AccessDeniedException,
    ) {
        response.status = HttpStatus.FORBIDDEN.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"
        response.writer.write(
            objectMapper.writeValueAsString(
                ErrorDto(message = "Доступ запрещён", status = ErrorKind.FORBIDDEN),
            ),
        )
    }
}
