package com.easyguide.backend.user.presentation

import com.easyguide.backend.shared.presentation.docs.ApiError
import com.easyguide.backend.shared.presentation.docs.ApiTags
import com.easyguide.backend.shared.presentation.security.CurrentUserId
import com.easyguide.backend.user.application.usecase.auth.GetCurrentUserUseCase
import com.easyguide.backend.user.application.usecase.auth.LoginUseCase
import com.easyguide.backend.user.application.usecase.auth.RegisterUserUseCase
import com.easyguide.backend.user.presentation.dto.AuthResponse
import com.easyguide.backend.user.presentation.dto.LoginRequest
import com.easyguide.backend.user.presentation.dto.RegisterRequest
import com.easyguide.backend.user.presentation.dto.UserResponse
import com.easyguide.backend.user.presentation.dto.toCommand
import com.easyguide.backend.user.presentation.dto.toResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@Tag(name = ApiTags.AUTH)
@RestController
@RequestMapping("/api/auth")
class AuthController(
    private val registerUserUseCase: RegisterUserUseCase,
    private val loginUseCase: LoginUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
) {

    @Operation(
        summary = "Регистрация",
        description = "Создаёт аккаунт туриста и сразу возвращает JWT. Стать гидом можно позже через `POST /api/users/me/become-guide`.",
    )
    @ApiError(409, "Email уже используется")
    @PostMapping("/register")
    fun register(@Valid @RequestBody request: RegisterRequest): AuthResponse =
        registerUserUseCase.execute(request.toCommand()).toResponse()

    @Operation(summary = "Вход", description = "Проверяет email и пароль, возвращает JWT для заголовка `Authorization: Bearer <token>`.")
    @ApiError(401, "Неверный email или пароль")
    @PostMapping("/login")
    fun login(@Valid @RequestBody request: LoginRequest): AuthResponse =
        loginUseCase.execute(request.toCommand()).toResponse()

    @Operation(summary = "Текущий пользователь", description = "Возвращает профиль владельца токена.")
    @GetMapping("/me")
    fun me(@CurrentUserId userId: UUID): UserResponse =
        getCurrentUserUseCase.execute(userId).toResponse()
}
